package com.solverminds.sibl.service;

import com.solverminds.booking.common.entity.Booking;
import com.solverminds.booking.common.entity.BookingCargo;
import com.solverminds.booking.common.entity.BookingCommodity;
import com.solverminds.booking.common.repository.BookingCargoViewRepository;
import com.solverminds.booking.common.repository.BookingCommodityViewRepository;
import com.solverminds.booking.common.repository.BookingViewRepository;
import com.solverminds.sibl.dto.SIBLCommodityDTO;
import com.solverminds.sibl.dto.SIBLContainerDTO;
import com.solverminds.sibl.dto.SIBLCustomerDTO;
import com.solverminds.sibl.dto.SIBLRequestDTO;
import com.solverminds.sibl.dto.validation.SIBLCustomerTypeConstants;
import com.solverminds.sibl.repo.EcomSiRepository;
import com.solverminds.sibl.util.VoyageCodeUtil;
import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.common.enums.CustomerType;
import com.svm.ecom.common.enums.EquipmentStatus;
import com.svm.ecom.common.enums.EquipmentType;
import com.svm.ecom.common.enums.SiStatus;
import com.svm.ecom.common.enums.TransportMode;
import com.svm.ecom.common.enums.WeightUnit;
import com.svm.ecom.common.enums.YesNoFlag;
import com.svm.ecom.common.master.cache.CommonMasterDataCacheService;
import com.svm.ecom.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * SI Validation Service - service-level validation only (same pattern as booking-service).
 * <p>
 * <b>DTO-level validations</b> (ports, POL?POD, vessel/service/terminals, container rules including cargo weight capacity,
 * customer/commodity master data, duplicate containers within SI, reefer/DG/OOG, ISO 6346) are performed by Bean
 * Validation ({@code @Valid}) via the {@code com.solverminds.sibl.dto.validation} package when the controller
 * receives the request body - same as booking-service ({@code ValidPortCodes}, {@code ContainerRulesValidator}, etc.).
 * </p>
 * <p>
 * This service runs only validations that require DB or request context:
 * </p>
 * <ul>
 *   <li><b>Mandatory fields</b>: customerCodeFromJWT, required customer types (SH, CN, NT).</li>
 *   <li><b>Voyage</b>: when serviceType is M or F, voyage must exist in ROE master and not be closed (same as Booking F/M legs).</li>
 *   <li><b>Booking</b>: booking must exist and belong to customer (direct DB).</li>
 *   <li><b>Status</b>: must be D (Draft) or S (Submit).</li>
 *   <li><b>Duplicate containers across bookings</b>: containers must not be in use in another booking (same voyage; direct DB).</li>
 *   <li><b>Container–commodity</b>: each container line must have at least one commodity; each commodity must align with booking
 *   cargo line (cargoNo + eqpTypeId via equipmentSize) and with the SI container row (containerNo). Runtime cargo weight
 *   aggregation by cargoNo + containerNo is unchanged.</li>
 * </ul>
 * <p><b>Applies to SI create and SI update:</b> SiCreateService and SiUpdateService call
 * {@link #validateSIRequest(SIBLRequestDTO, String)} after the controller has already run {@code @Valid} on the request body.</p>
 * <p><b>Database reference:</b> Documentation database for {@code booking}, {@code booking_cargo} and
 * {@code booking_commodity} (used for ecomSI validation) is <b>ecomm_jsf</b>. These are read via
 * {@link BookingViewRepository}, {@link BookingCargoViewRepository}, {@link BookingCommodityViewRepository} (documentationTransactionManager).</p>
 */
@Service
public class SIValidationService {

    private static final Logger logger = LoggerFactory.getLogger(SIValidationService.class);

    private static final String CONFIRMED_STATUS = "C";

    private final JdbcTemplate reportJdbcTemplate;
    private final CommonMasterDataCacheService masterDataCacheService;
    /** Documentation DB (ecomm_jsf): booking, booking_cargo, booking_commodity for ecomSI validation. */
    private final BookingViewRepository bookingViewRepository;
    private final BookingCargoViewRepository bookingCargoViewRepository;
    private final BookingCommodityViewRepository bookingCommodityViewRepository;
    private final EcomSiRepository ecomSiRepository;

    public SIValidationService(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate,
                               @Qualifier(CommonMasterDataCacheService.BEAN_NAME) CommonMasterDataCacheService masterDataCacheService,
                               BookingViewRepository bookingViewRepository,
                               BookingCargoViewRepository bookingCargoViewRepository,
                               BookingCommodityViewRepository bookingCommodityViewRepository,
                               EcomSiRepository ecomSiRepository) {
        this.reportJdbcTemplate = reportJdbcTemplate;
        this.masterDataCacheService = masterDataCacheService;
        this.bookingViewRepository = bookingViewRepository;
        this.bookingCargoViewRepository = bookingCargoViewRepository;
        this.bookingCommodityViewRepository = bookingCommodityViewRepository;
        this.ecomSiRepository = ecomSiRepository;
    }

    /**
     * Service-level validation for SI create and SI update. Delegates to {@link #validateSIRequest(SIBLRequestDTO, String, boolean)}
     * with isCreate = false (update path). For SI create, use the overload with isCreate = true.
     */
    public ValidationResult validateSIRequest(SIBLRequestDTO request, String customerCode) {
        return validateSIRequest(request, customerCode, false);
    }

    /**
     * Service-level validation for SI create and SI update. Called by SiCreateService and SiUpdateService after
     * controller has run Bean Validation (@Valid). DTO-level validations (ports, POL!=POD, vessel/service/terminals,
     * container rules including weight capacity, customer/commodity master, duplicate containers within SI) are
     * performed by validators in dto/validation package (same pattern as booking-service). This method only runs
     * validations that require DB or request context: mandatory fields (incl. customerCodeFromJWT), status, voyage,
     * booking existence and confirmed status, no multiple SI per booking (create only), containers/commodities match
     * booking, restrict (values must match booking), duplicate containers across bookings.
     *
     * @param isCreate true for SI create, false for SI update
     */
    public ValidationResult validateSIRequest(SIBLRequestDTO request, String customerCode, boolean isCreate) {
        ValidationResult result = new ValidationResult();

        // Step 1: Mandatory field validations (customerCodeFromJWT, required customer types - need request context)
        validateMandatoryFields(request, result);

        // Step 2: Bound validation
        validateBound(request.getLoadBound(), result);

        // Step 3: Voyage validation when serviceType is M or F (ROE master and closure - requires DB)
        if (isMainlineOrFeeder(request.getServiceType())) {
            validateVoyage(request, result);
        }

        // Step 4: Booking validation (requires DB) - runs even if mandatory fields failed to collect all errors
        validateBooking(request.getBookingNumber(), customerCode, result);

        // Step 5: Booking must be confirmed (status = 'C') - use same DB as validateBooking (Report DB) for consistency
        Optional<Booking> bookingOpt = Optional.empty();
        String bookingNo = request.getBookingNumber();
        if (!isEmpty(bookingNo)) {
            try {
                // Check status from Report DB (same as step 4) so it matches the booking table you query
                String statusFromDb = getBookingStatusFromReportDb(bookingNo.trim());
                if (statusFromDb != null && !CONFIRMED_STATUS.equalsIgnoreCase(statusFromDb.trim())) {
                    result.addError(ErrorCodes.SI_BOOKING_NOT_CONFIRMED, "bookingNumber", bookingNo,
                            "Booking must be confirmed (status C) to create or update SI.");
                }
                // Load booking snapshot from Documentation DB (ecomm_jsf: booking, booking_cargo, booking_commodity) for steps 7a/7b/7c
                if (result.isValid()) {
                    bookingOpt = bookingViewRepository.findByBookNo(bookingNo.trim());
                }
            } catch (Exception e) {
                logger.warn("Could not load booking for confirmed check: {}", e.getMessage());
            }
        }

        // Step 6: No multiple SI per booking (SI create only)
        if (isCreate && !isEmpty(bookingNo)) {
            try {
                if (ecomSiRepository.existsByBookno(bookingNo.trim())) {
                    result.addError(ErrorCodes.SI_ALREADY_EXISTS_FOR_BOOKING, "bookingNumber", bookingNo,
                            "An SI already exists for this booking; multiple SI submissions are not allowed.");
                }
            } catch (Exception e) {
                logger.warn("Could not check existing SI for booking: {}", e.getMessage());
            }
        }

        // Step 7: Load booking snapshot from ecomm_jsf (booking_cargo, booking_commodity) and run container/commodity/restrict validations
        if (result.isValid() && bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            String bookNo = booking.getBookNo();
            List<BookingCargo> cargoList;
            List<BookingCommodity> commodityList;
            try {
                cargoList = bookingCargoViewRepository.findByBookNoOrderByCargoNo(bookNo);
                commodityList = bookingCommodityViewRepository.findByBookNoOrderByCargoNoAndItemNo(bookNo);
            } catch (Exception e) {
                logger.warn("Could not load booking cargo/commodity for validation: {}", e.getMessage());
                cargoList = Collections.emptyList();
                commodityList = Collections.emptyList();
            }
            // Step 7a: Cargo lines (cargoNo + eqpTypeId) must match booking; no add/remove
            validateContainersMatchBooking(request, cargoList, result);
            // Step 7b: Commodities mandatory per container; map to booking (cargoNo + eqpTypeId) and SI container (containerNo)
            validateCommoditiesForExistingContainers(request, cargoList, result);
            // Step 7c: Restrict - request values must match booking (no structural changes)
            validateRequestMatchesBooking(request, booking, cargoList, commodityList, result);
        }

        // Step 8: Status must be D (Draft) or S (Submit)
        validateStatus(request, result);

        // Step 9: Duplicate containers across bookings (requires DB)
        validateDuplicateContainersAcrossBookings(request, result);

        // Step 10: Customer master data (customerCode, countryId) and contact (telephone format)
        validateCustomersMasterDataViaCache(request, result);
        validateCustomerContactFields(request, result);

        return result;
    }

    private static String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    /**
     * Validate status is D (Draft) or S (Submit) only.
     */
    private void validateStatus(SIBLRequestDTO request, ValidationResult result) {
        SiStatus status = request.getStatus();
        if (status == null) {
            return; // default will be applied in service
        }
        if (status != SiStatus.D && status != SiStatus.S) {
            result.addError(ErrorCodes.SI_STATUS_INVALID, "status", status.getValue(), "Status must be D (Draft) or S (Submit)");
        }
    }

    /**
     * Validate containers: when reefer=Y and nor!=Y, SIBLReeferDTO required;
     * when oogFlag=Y, SIBLOogDTO required. DG is at commodity level only.
     */
    private void validateContainers(SIBLRequestDTO request, ValidationResult result) {
        if (request.getContainers() == null || request.getContainers().isEmpty()) {
            return;
        }
        int idx = 0;
        for (SIBLContainerDTO c : request.getContainers()) {
            idx++;
            boolean isReefer = c.getReefer() == YesNoFlag.Y || (c.getEquipmentType() != null && c.getEquipmentType() == EquipmentType.R);
            boolean isNor = c.getNor() == YesNoFlag.Y;
            if (isReefer && !isNor) {
                if (c.getReeferSettings() == null) {
                    result.addError(ErrorCodes.SI_CONTAINER_REEFER_SETTINGS_REQUIRED, "containers[" + (idx - 1) + "].reeferSettings", null,
                            "SIBLReeferDTO is required when reefer=Y (or equipment type R) and nor!=Y");
                }
            }
            // DG is at commodity level only; no container-level DG validation
            if (c.getOogFlag() == YesNoFlag.Y) {
                if (c.getOogDimensions() == null) {
                    result.addError(ErrorCodes.SI_CONTAINER_OOG_DIMENSIONS_REQUIRED, "containers[" + (idx - 1) + "].oogDimensions", null,
                            "SIBLOogDTO is required when OOG=Y");
                }
            }
        }
    }

    /**
     * Validate container number per ISO 6346: 11 characters = 4 letters + 6 digits + 1 check digit.
     * Check digit is computed from first 10 characters (modulo 11, weight 2^i); if remainder 10 then check digit 0.
     * Letter values: A=10, B=12, ... (11, 22, 33 omitted), digits 0-9 = 0-9.
     */
    private static boolean isValidISO6346ContainerNumber(String containerNo) {
        if (containerNo == null) return false;
        String s = containerNo.trim();
        if (s.length() != 11) return false;
        for (int i = 0; i < 4; i++) {
            if (!Character.isLetter(s.charAt(i))) return false;
        }
        for (int i = 4; i < 10; i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        if (!Character.isDigit(s.charAt(10))) return false;
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            int value = iso6346CharValue(s.charAt(i));
            if (value < 0) return false;
            sum += value * (1 << i); // 2^i
        }
        int check = sum % 11;
        if (check == 10) check = 0;
        return (s.charAt(10) - '0') == check;
    }

    /** ISO 6346: digits 0-9 = 0-9; letters A-Z = 10,12,13,... (11, 22, 33 omitted). */
    private static int iso6346CharValue(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'A' && c <= 'Z') {
            int index = c - 'A';
            int value = 10 + index + (index >= 1 ? 1 : 0) + (index >= 11 ? 1 : 0) + (index >= 21 ? 1 : 0);
            return value;
        }
        if (c >= 'a' && c <= 'z') return iso6346CharValue(Character.toUpperCase(c));
        return -1;
    }

    /**
     * Container master data and capacity validations (same as Booking ContainerRulesValidator).
     * - Equipment size must exist in equipment master (isValidEquipmentSize).
     * - cargoWeight + tareWeight must not exceed container gross capacity from cache (ErrorCode 4315).
     * - equipmentStatus must be F (Full) or M (Empty) when provided.
     * - containerNo when provided must follow ISO 6346 (4 letters + 6 digits + check digit, 11 chars).
     */
    private void validateContainersMasterDataAndCapacity(SIBLRequestDTO request, ValidationResult result) {
        if (request.getContainers() == null || request.getContainers().isEmpty()) return;
        if (masterDataCacheService == null || !masterDataCacheService.isInitialized()) {
            logger.warn("CommonMasterDataCacheService not available, skipping container equipment/weight validation");
            return;
        }
        // Bulk fetch tare and gross for all container equipment types in one query each (handles 10+ cargoes efficiently)
        List<String> equipTypes = request.getContainers().stream()
            .map(SIBLContainerDTO::getEquipmentSize)
            .filter(s -> s != null && !s.trim().isEmpty())
            .map(String::trim)
            .distinct()
            .collect(Collectors.toList());
        Map<String, Float> tareByEquipType = masterDataCacheService.getTareWeightsByEquipTypes(equipTypes);
        Map<String, Float> grossByEquipType = masterDataCacheService.getGrossWeightsByEquipTypes(equipTypes);

        int idx = 0;
        for (SIBLContainerDTO c : request.getContainers()) {
            String prefix = "containers[" + idx + "]";

            // Equipment size must exist in equipment master
            if (!isEmpty(c.getEquipmentSize())) {
                if (!masterDataCacheService.isValidEquipmentSize(c.getEquipmentSize())) {
                    result.addError(ErrorCodes.SI_CONTAINER_EQUIPMENT_SIZE_INVALID, prefix + ".equipmentSize", c.getEquipmentSize(),
                            "Invalid equipment size: " + c.getEquipmentSize() + " - Equipment must exist in equipment master");
                } else {
                    // Weight capacity: cargoWeight + tareWeight <= gross capacity. When commodities present use derived cargoWeight = SUM(itemWeight).
                    int cargoWt = request.getCommodities() != null && !request.getCommodities().isEmpty()
                        ? sumCommodityItemWeightKgForContainer(request.getCommodities(), c.getCargoNo(), c.getContainerNo())
                        : (c.getCargoWeight() != null ? c.getCargoWeight() : 0);
                    String eqKey = c.getEquipmentSize().trim().toUpperCase();
                    Float tareFromMap = tareByEquipType.get(eqKey);
                    float tareWt = tareFromMap != null ? tareFromMap : (c.getTareWeight() != null ? c.getTareWeight().floatValue() : 0f);
                    Float maxGross = grossByEquipType.get(eqKey);
                    if (maxGross != null && maxGross > 0) {
                        float grossKg = cargoWt + tareWt;
                        if (grossKg > maxGross) {
                            result.addError(ErrorCodes.SI_CONTAINER_WEIGHT_EXCEEDS_CAPACITY, prefix + ".cargoWeight", String.valueOf(cargoWt),
                                    "Weight " + (int) grossKg + " kg exceeds container capacity (" + maxGross.intValue() + " kg for " + c.getEquipmentSize() + ")");
                        }
                    }
                }
            }

            // equipmentStatus must be F or M when provided (enum restricts; invalid value would be null after parse)
            if (c.getEquipmentStatus() != null && c.getEquipmentStatus() != EquipmentStatus.F && c.getEquipmentStatus() != EquipmentStatus.M) {
                result.addError(ErrorCodes.SI_EQUIPMENT_STATUS_INVALID, prefix + ".equipmentStatus", c.getEquipmentStatus() != null ? c.getEquipmentStatus().getValue() : null,
                        "Equipment status must be F (Full/Laden) or M (Empty)");
            }

            // ISO 6346 container number: 11 chars = 4 letters + 6 digits + check digit (exact rule and check digit)
            if (!isEmpty(c.getContainerNo())) {
                String cn = c.getContainerNo().trim();
                if (!isValidISO6346ContainerNumber(cn)) {
                    result.addError(ErrorCodes.SI_CONTAINER_NUMBER_INVALID, prefix + ".containerNo", c.getContainerNo(),
                            "Invalid ISO container format for container " + c.getContainerNo() + ".");
                }
            }

            idx++;
        }
    }

    /** Sum of commodity itemWeight for the given container in kg (T → * 1000, K → as-is). Used for capacity check when commodities present. */
    private static int sumCommodityItemWeightKgForContainer(List<SIBLCommodityDTO> commodities, Integer cargoNo, String containerNo) {
        if (commodities == null) return 0;
        int sum = 0;
        for (SIBLCommodityDTO c : commodities) {
            if (cargoNo != null && !cargoNo.equals(c.getCargoNo())) continue;
            if (containerNo != null && !containerNo.equals(c.getContainerNo())) continue;
            Integer w = c.getItemWeight();
            if (w == null) continue;
            sum += (c.getItemWeightUnit() == WeightUnit.T) ? w * 1000 : w;
        }
        return sum;
    }

    /**
     * Duplicate container within same SI: container numbers must be unique per SI (trim, uppercase for comparison).
     * Empty/null excluded from duplicate check. One error per duplicate number.
     */
    private void validateDuplicateContainersWithinSI(SIBLRequestDTO request, ValidationResult result) {
        if (request.getContainers() == null || request.getContainers().isEmpty()) return;
        Set<String> seen = new HashSet<>();
        Set<String> duplicates = new LinkedHashSet<>();
        for (SIBLContainerDTO c : request.getContainers()) {
            String no = c.getContainerNo();
            if (no == null || no.trim().isEmpty()) continue;
            String n = no.trim().toUpperCase();
            if (seen.contains(n)) {
                duplicates.add(n);
            }
            seen.add(n);
        }
        for (String dup : duplicates) {
            result.addError(ErrorCodes.SI_CONTAINER_DUPLICATE_IN_SI, "containers", dup,
                    "Duplicate container number " + dup + " within the same Shipping Instruction.");
        }
    }

    /**
     * Duplicate container across bookings: containers must not already be in use in another booking (same voyage).
     * Reuses logic equivalent to EcomShippingInstructionBean.checkContainerValidation(bookNo, containerlisstr).
     */
    private void validateDuplicateContainersAcrossBookings(SIBLRequestDTO request, ValidationResult result) {
        if (request.getContainers() == null || request.getContainers().isEmpty()) return;
        List<String> containerList = request.getContainers().stream()
                .map(c -> c.getContainerNo() != null ? c.getContainerNo().trim().toUpperCase() : null)
                .filter(s -> s != null && !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        if (containerList.isEmpty()) return;
        String bookNo = request.getBookingNumber();
        if (isEmpty(bookNo)) return;
        try {
            List<String> alreadyUsed = findContainersUsedInOtherBookings(bookNo, containerList);
            for (String containerNo : alreadyUsed) {
                result.addError(ErrorCodes.SI_CONTAINER_ALREADY_IN_USE, "containers", containerNo,
                        "Container No " + containerNo + " is already in use in another booking.");
            }
        } catch (Exception e) {
            logger.error("Error checking container usage across bookings: " + e.getMessage(), e);
            result.addError(ErrorCodes.SI_CONTAINER_ALREADY_IN_USE, "containers", null,
                    "Error validating container uniqueness across bookings: " + e.getMessage());
        }
    }

    /**
     * Find container numbers from the given list that are already used in another booking (same voyage).
     * Equivalent to VDS: booking b1 join b2 (same vessel/voyage/service/bound/loadPortId/callId/polTerminal), b2 <> b1, booking_cargo bc on b2, bc.eqpId in list, bc.isDummy='N'.
     */
    private List<String> findContainersUsedInOtherBookings(String bookNo, List<String> containerNumbers) {
        if (containerNumbers == null || containerNumbers.isEmpty()) return Collections.emptyList();
        String inClause = containerNumbers.stream().map(c -> "?").collect(Collectors.joining(","));
        String sql = "SELECT DISTINCT bc.eqpId FROM booking b1 (NOLOCK) " +
                "INNER JOIN booking b2 (NOLOCK) ON b1.vesselCode = b2.vesselCode AND b1.voyageCode = b2.voyageCode " +
                "AND b1.serviceCode = b2.serviceCode AND b1.bound = b2.bound AND b1.loadPortId = b2.loadPortId " +
                "AND (b1.callId = b2.callId OR (b1.callId IS NULL AND b2.callId IS NULL)) " +
                "AND (b1.polTerminal = b2.polTerminal OR (b1.polTerminal IS NULL AND b2.polTerminal IS NULL)) " +
                "INNER JOIN booking_cargo bc (NOLOCK) ON bc.book_no = b2.book_no " +
                "WHERE b1.book_no = ? AND b2.book_no <> b1.book_no AND b2.status <> 'V' AND bc.isDummy = 'N' AND bc.eqpId IN (" + inClause + ")";
        List<Object> args = new ArrayList<>();
        args.add(bookNo.trim());
        for (String c : containerNumbers) {
            args.add(c);
        }
        return reportJdbcTemplate.query(sql, args.toArray(), (rs, rowNum) -> rs.getString("eqpId"));
    }


    /**
     * Validate commodities: when hazardous=Y, SIBLDangerousGoodsDTO required.
     */
    private void validateCommodities(SIBLRequestDTO request, ValidationResult result) {
        if (request.getCommodities() == null || request.getCommodities().isEmpty()) {
            return;
        }
        int idx = 0;
        for (SIBLCommodityDTO c : request.getCommodities()) {
            idx++;
            if (c.getHazardous() == YesNoFlag.Y) {
                if (c.getDangerousGoods() == null) {
                    result.addError(ErrorCodes.SI_COMMODITY_DANGEROUS_GOODS_REQUIRED, "commodities[" + (idx - 1) + "].dangerousGoods", null,
                            "SIBLDangerousGoodsDTO is required when hazardous=Y");
                }
            }
        }
    }

    /**
     * Validate mandatory fields as per SI requirements
     */
    private void validateMandatoryFields(SIBLRequestDTO request, ValidationResult result) {
        if (isEmpty(request.getBookingNumber())) {
            result.addError(ErrorCodes.SI_BOOKING_NUMBER_REQUIRED, "bookingNumber", null, "Booking Number is required");
        }
        if (isEmpty(request.getVesselCode())) {
            result.addError(ErrorCodes.SI_VESSEL_REQUIRED, "vesselCode", null, "Vessel is required");
        }
        if (isEmpty(request.getVoyageCode())) {
            result.addError(ErrorCodes.SI_VOYAGE_REQUIRED, "voyageCode", null, "Voyage is required");
        }
        if (request.getLoadBound() == null) {
            result.addError(ErrorCodes.SI_LOAD_BOUND_REQUIRED, "loadBound", null, "Bound is required");
        }
        if (isEmpty(request.getLoadPortId())) {
            result.addError(ErrorCodes.SI_LOAD_PORT_REQUIRED, "loadPortId", null, "Port of Loading is required");
        }
        if (isEmpty(request.getDischargePortId())) {
            result.addError(ErrorCodes.SI_DISCHARGE_PORT_REQUIRED, "dischargePortId", null, "Port of Discharge is required");
        }
        if (isEmpty(request.getCustomerCodeFromJWT())) {
            result.addError(ErrorCodes.SI_CUSTOMER_CODE_REQUIRED, "customerCode", null, "Customer Code is required");
        }
        // SH (Shipper), CN (Consignee), NT (Notify) are required in customer details
        validateRequiredCustomerTypes(request, result);
    }

    /** Customer types required in request body: SH (Shipper), CN (Consignee), NT (Notify). */
    private static final Set<CustomerType> REQUIRED_CUSTOMER_TYPES = Collections.unmodifiableSet(
        new HashSet<>(Arrays.asList(CustomerType.SH, CustomerType.CN, CustomerType.NT)));
    private void validateRequiredCustomerTypes(SIBLRequestDTO request, ValidationResult result) {
        if (request.getCustomers() == null || request.getCustomers().isEmpty()) {
            result.addError(ErrorCodes.SI_CUSTOMERS_REQUIRED, "customers", null, "Customer details are required (SH=Shipper, CN=Consignee, NT=Notify)");
            return;
        }
        Set<CustomerType> present = new HashSet<>();
        for (SIBLCustomerDTO c : request.getCustomers()) {
            CustomerType type = c.getCustomerType();
            if (type != null) {
                present.add(type);
                if (!SIBLCustomerTypeConstants.ALLOWED_FOR_SI.contains(type)) {
                    result.addError(ErrorCodes.SI_CUSTOMER_TYPE_REQUIRED, "customers", type.getValue(),
                            "Customer type must be one of: " + SIBLCustomerTypeConstants.ALLOWED_VALUES_STRING);
                }
            }
        }
        for (CustomerType required : SIBLCustomerTypeConstants.REQUIRED_FOR_SI) {
            if (!present.contains(required)) {
                String msg = required == CustomerType.SH ? "Shipper (SH)" : required == CustomerType.CN ? "Consignee (CN)" : "Notify (NT)";
                result.addError(ErrorCodes.SI_CUSTOMERS_REQUIRED, "customers", required.getValue(), "Customer details must include " + msg);
            }
        }
    }

    /**
     * Validate bound value (Bound enum guarantees valid when non-null).
     */
    private void validateBound(Bound bound, ValidationResult result) {
        // With enum, any non-null value is valid (E, W, N, S, T, I, O, L). Null is checked in validateMandatoryFields.
    }

    /**
     * POL (loadPortId) and POD (dischargePortId) must be different.
     * Same as Booking DifferentPortsValidator; no master lookup.
     */
    private void validatePolPod(String pol, String pod, ValidationResult result) {
        if (pol != null && pod != null && pol.equalsIgnoreCase(pod)) {
            result.addError(ErrorCodes.SI_POL_POD_SAME, "loadPortId/dischargePortId", pol,
                "POL and POD cannot be the same");
        }
    }

    /**
     * Valid port code check for originPortId, finalPortId, loadPortId, dischargePortId.
     * Same as Booking PortCodesValidator: port codes must exist in Portmaster; uses CommonMasterDataCacheService
     * (cache from reportDB); skips if cache not initialized.
     */
    private void validatePortsViaCache(SIBLRequestDTO request, ValidationResult result) {
        if (masterDataCacheService == null || !masterDataCacheService.isInitialized()) {
            logger.warn("CommonMasterDataCacheService not available, skipping port validation");
            return;
        }
        if (!isEmpty(request.getLoadPortId()) && !masterDataCacheService.isValidPortCode(request.getLoadPortId())) {
            result.addError(ErrorCodes.SI_PORT_INVALID, "loadPortId", request.getLoadPortId(), "Invalid POL Code - Port does not exist");
        }
        if (!isEmpty(request.getDischargePortId()) && !masterDataCacheService.isValidPortCode(request.getDischargePortId())) {
            result.addError(ErrorCodes.SI_PORT_INVALID, "dischargePortId", request.getDischargePortId(), "Invalid POD Code - Port does not exist");
        }
        if (!isEmpty(request.getOriginPortId()) && !masterDataCacheService.isValidPortCode(request.getOriginPortId())) {
            result.addError(ErrorCodes.SI_PORT_INVALID, "originPortId", request.getOriginPortId(), "Invalid PLA Code - Port does not exist");
        }
        if (!isEmpty(request.getFinalPortId()) && !masterDataCacheService.isValidPortCode(request.getFinalPortId())) {
            result.addError(ErrorCodes.SI_PORT_INVALID, "finalPortId", request.getFinalPortId(), "Invalid PLD Code - Port does not exist");
        }
    }

    /**
     * Vessel code existence validation via MasterDataCacheService (same as Booking RoutePortsValidator).
     * When serviceType is M (Mainline) or F (Feeder), vessel is required and must exist in vessel master with status A.
     */
    private void validateVesselViaCache(SIBLRequestDTO request, ValidationResult result) {
        TransportMode serviceType = request.getServiceType();
        String vesselCode = request.getVesselCode();
        if (isMainlineOrFeeder(serviceType)) {
            if (isEmpty(vesselCode)) {
                result.addError(ErrorCodes.SI_VESSEL_REQUIRED, "vesselCode", null, "Vessel is required when serviceType is F (Feeder) or M (Mainline)");
                return;
            }
        }
        if (isEmpty(vesselCode)) return;
        if (masterDataCacheService == null || !masterDataCacheService.isInitialized()) {
            logger.warn("CommonMasterDataCacheService not available, skipping vessel validation");
            return;
        }
        if (!masterDataCacheService.isValidVesselCode(vesselCode)) {
            result.addError(ErrorCodes.SI_VESSEL_INVALID, "vesselCode", vesselCode, "Invalid Vessel Code - Vessel does not exist or is inactive (vessel master status A)");
        }
    }

    /**
     * Terminal validation when serviceType is M or F (same as Booking RoutePortsValidator; Master cache; isValidTerminalCode()).
     * polTerminal = loadTerminal, podTerminal = dischargeTerminal; must exist in terminal master.
     */
    private void validateTerminalsViaCache(SIBLRequestDTO request, ValidationResult result) {
        if (!isMainlineOrFeeder(request.getServiceType())) return;
        if (masterDataCacheService == null || !masterDataCacheService.isInitialized()) {
            logger.warn("CommonMasterDataCacheService not available, skipping terminal validation");
            return;
        }
        if (!isEmpty(request.getPolTerminal()) && !masterDataCacheService.isValidTerminalCode(request.getPolTerminal())) {
            result.addError(ErrorCodes.SI_POL_TERMINAL_INVALID, "polTerminal", request.getPolTerminal(), "Invalid POL terminal code - Terminal does not exist in terminal master");
        }
        if (!isEmpty(request.getPodTerminal()) && !masterDataCacheService.isValidTerminalCode(request.getPodTerminal())) {
            result.addError(ErrorCodes.SI_POD_TERMINAL_INVALID, "podTerminal", request.getPodTerminal(), "Invalid POD terminal code - Terminal does not exist in terminal master");
        }
    }

    private static boolean isMainlineOrFeeder(TransportMode serviceType) {
        return serviceType == TransportMode.M || serviceType == TransportMode.F;
    }

    /**
     * Validate serviceCode when serviceType is M (Mainline) or F (Feeder).
     * Same as Booking RoutePortsValidator: service code must exist in service master (Master cache; isValidServiceCode()).
     */
    private void validateServiceCodeViaCache(SIBLRequestDTO request, ValidationResult result) {
        TransportMode serviceType = request.getServiceType();
        if (serviceType != TransportMode.M && serviceType != TransportMode.F) {
            return; // Only validate serviceCode when serviceType is M or F
        }
        String serviceCode = request.getServiceCode();
        if (serviceCode == null || serviceCode.trim().isEmpty()) {
            return; // No serviceCode to validate
        }
        if (masterDataCacheService == null || !masterDataCacheService.isInitialized()) {
            logger.warn("CommonMasterDataCacheService not available, skipping service code validation");
            return;
        }
        if (!masterDataCacheService.isValidServiceCode(serviceCode)) {
            result.addError(ErrorCodes.SI_SERVICE_CODE_INVALID, "serviceCode", serviceCode, "Invalid service code: " + serviceCode + " - Service code must exist in service master");
        }
    }

    /**
     * Customer Master Validation and Country Code Validation for SIBLCustomerDTO (same as Booking CustomerMasterDataValidator).
     * - customerCode: validate against master cache; customer must exist in customermaster.
     * - countryId: validate against country master (ISO 2-letter); country must exist in country master.
     * Skips if cache not initialized.
     */
    private void validateCustomersMasterDataViaCache(SIBLRequestDTO request, ValidationResult result) {
        if (request.getCustomers() == null || request.getCustomers().isEmpty()) return;
        if (masterDataCacheService == null || !masterDataCacheService.isInitialized()) {
            logger.warn("CommonMasterDataCacheService not available, skipping customer/country master data validation");
            return;
        }
        int idx = 0;
        for (SIBLCustomerDTO c : request.getCustomers()) {
            String custType = c.getCustomerType() != null ? c.getCustomerType().getValue() : "UNKNOWN";
            if (c.getCustomerCode() != null && !c.getCustomerCode().trim().isEmpty()
                    && !masterDataCacheService.isValidCustomerCode(c.getCustomerCode())) {
                result.addError(ErrorCodes.SI_CUSTOMER_CODE_INVALID, "customers[" + idx + "].customerCode", c.getCustomerCode(), 
                    "Invalid customer code: " + c.getCustomerCode() + " for customer type " + custType + " - Customer must exist in master table");
            }
            if (c.getCountryId() != null && !c.getCountryId().trim().isEmpty()
                    && !masterDataCacheService.isValidCountryCode(c.getCountryId())) {
                result.addError(ErrorCodes.SI_COUNTRY_CODE_INVALID, "customers[" + idx + "].countryId", c.getCountryId(), 
                    "Invalid country code: " + c.getCountryId() + " for customer type " + custType + " - Country must exist in master table (ISO 2-letter)");
            }
            idx++;
        }
    }

    /** Telephone format: optional +, digits, spaces, hyphens, parentheses, dots; length 6-30 (ErrorCode 5209). */
    private static final Pattern TELEPHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s\\-.()]{6,30}$");

    /**
     * Customer contact validations: telephone format when provided (ErrorCode 5209).
     * Email format is validated at DTO level via @Email (ErrorCode 5210 in GlobalExceptionHandler).
     */
    private void validateCustomerContactFields(SIBLRequestDTO request, ValidationResult result) {
        if (request.getCustomers() == null || request.getCustomers().isEmpty()) return;
        int idx = 0;
        for (SIBLCustomerDTO c : request.getCustomers()) {
            String custType = c.getCustomerType() != null ? c.getCustomerType().getValue() : "UNKNOWN";
            String tel = c.getTelephone();
            if (tel != null && !tel.trim().isEmpty()) {
                if (!TELEPHONE_PATTERN.matcher(tel.trim()).matches()) {
                    result.addError(ErrorCodes.SI_CUSTOMER_TELEPHONE_INVALID, "customers[" + idx + "].telephone", tel, 
                        "Customer telephone invalid for customer type " + custType);
                }
            }
            idx++;
        }
    }

    /**
     * Commodity master validations for SIBLCommodityDTO (same as Booking CommodityMasterDataValidator).
     * - commodityGroup: must exist in commodity master.
     * - hsCode: must exist in commodity master when provided.
     * - hsCode must be applicable for commodityGroup when both are valid master codes (cache: comdtycode → grpcode).
     * - packageType: must exist in package master when provided.
     */
    private void validateCommoditiesMasterDataViaCache(SIBLRequestDTO request, ValidationResult result) {
        if (request.getCommodities() == null || request.getCommodities().isEmpty()) return;
        if (masterDataCacheService == null || !masterDataCacheService.isInitialized()) {
            logger.warn("CommonMasterDataCacheService not available, skipping commodity master data validation");
            return;
        }
        int idx = 0;
        for (SIBLCommodityDTO c : request.getCommodities()) {
            String cg = c.getCommodityGroup();
            boolean cgOk = cg == null || cg.trim().isEmpty()
                    || masterDataCacheService.isValidCommodityCode(cg.trim());
            if (cg != null && !cg.trim().isEmpty() && !cgOk) {
                result.addError(ErrorCodes.SI_COMMODITY_GROUP_INVALID, "commodities[" + idx + "].commodityGroup", cg,
                        "Invalid commodity group: " + cg + " - Commodity group must exist in master table");
            }
            boolean hsOk = c.getHsCode() == null || c.getHsCode().trim().isEmpty()
                    || masterDataCacheService.isValidCommodityCode(c.getHsCode());
            if (c.getHsCode() != null && !c.getHsCode().trim().isEmpty() && !hsOk) {
                result.addError(ErrorCodes.SI_HS_CODE_INVALID, "commodities[" + idx + "].hsCode", c.getHsCode(),
                        "Invalid HS code: " + c.getHsCode() + " - HS code must exist in commodity master when provided");
            }
            if (cgOk && hsOk && cg != null && !cg.trim().isEmpty()
                    && c.getHsCode() != null && !c.getHsCode().trim().isEmpty()
                    && !masterDataCacheService.isHsCodeApplicableForCommodityGroup(c.getHsCode(), cg)) {
                result.addError(ErrorCodes.SI_HS_CODE_INVALID, "commodities[" + idx + "].hsCode", c.getHsCode(),
                        "hsCode is not applicable for the given commodity group code");
            }
            if (c.getPackageType() != null && !c.getPackageType().trim().isEmpty() && !masterDataCacheService.isValidPackageCode(c.getPackageType())) {
                result.addError(ErrorCodes.SI_PACKAGE_TYPE_INVALID, "commodities[" + idx + "].packageType", c.getPackageType(),
                        "Invalid package type: " + c.getPackageType() + " - Package type must exist in package master when provided");
            }
            idx++;
        }
    }

    /**
     * Validate booking exists and belongs to customer
     */
    private void validateBooking(String bookingNo, String customerCode, ValidationResult result) {
        if (isEmpty(bookingNo)) return;

        try {
            String sql = "select COUNT(1) from booking(nolock) inner join booking_cust(nolock) on booking_cust.book_no = booking.book_no " +
                    "where booking.book_no = ? and booking_cust.custid = ? AND booking.status in ('C','D')";
            Integer count = reportJdbcTemplate.queryForObject(sql, Integer.class, bookingNo.trim(), 
                customerCode != null ? customerCode.trim() : "");
            if (count == null || count == 0) {
                result.addError(ErrorCodes.SI_BOOKING_NOT_FOUND, "bookingNumber", bookingNo,
                    "Booking not found or does not belong to the customer");
            }
        } catch (Exception e) {
            logger.error("Error validating booking: " + e.getMessage());
            result.addError(ErrorCodes.SI_BOOKING_NOT_FOUND, "bookingNumber", bookingNo, "Error validating booking: " + e.getMessage());
        }
    }

    /**
     * Get booking status from Report DB (same source as validateBooking). Used for confirmed (status C) check
     * so the result matches the booking table you query in Report DB.
     */
    private String getBookingStatusFromReportDb(String bookNo) {
        if (isEmpty(bookNo)) return null;
        try {
            String sql = "SELECT status FROM booking (NOLOCK) WHERE book_no = ?";
            List<String> rows = reportJdbcTemplate.queryForList(sql, String.class, bookNo.trim());
            return (rows != null && !rows.isEmpty()) ? rows.get(0) : null;
        } catch (Exception e) {
            logger.debug("Could not get booking status from Report DB: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Containers in request must match booking_cargo lines: same set of (cargoNo, eqpTypeId) as SI (cargoNo, equipmentSize);
     * no add/remove of lines. Container number (eqpId) may differ from booking when equipment was updated or dummy placeholders are used.
     */
    private void validateContainersMatchBooking(SIBLRequestDTO request, List<BookingCargo> cargoList, ValidationResult result) {
        List<String> bookingLineKeys = (cargoList == null ? Collections.<BookingCargo>emptyList() : cargoList).stream()
                .map(bc -> cargoLineKey(bc.getCargoNo(), bc.getEqpTypeId()))
                .sorted()
                .collect(Collectors.toList());
        List<SIBLContainerDTO> containers = request.getContainers();
        if (containers == null || containers.isEmpty()) {
            if (!bookingLineKeys.isEmpty()) {
                result.addError(ErrorCodes.SI_CONTAINERS_MUST_MATCH_BOOKING, "containers", null,
                        "Unable to add or remove containers in SI");
            }
            return;
        }
        List<String> requestLineKeys = containers.stream()
                .map(rc -> cargoLineKey(rc.getCargoNo(), rc.getEquipmentSize()))
                .sorted()
                .collect(Collectors.toList());
        if (!bookingLineKeys.equals(requestLineKeys)) {
            result.addError(ErrorCodes.SI_CONTAINERS_MUST_MATCH_BOOKING, "containers", null,
                    "Unable to add or remove containers in SI");
        }
    }

    /**
     * Each SI container line must have at least one commodity (same {@code cargoNo} and {@code containerNo} as the container row,
     * matching runtime weight aggregation). Each commodity must reference a booking cargo line by stable line identity
     * {@code (cargoNo, eqpTypeId)}: SI {@code equipmentSize} must match booking {@code eqpTypeId} for that {@code cargoNo},
     * and {@code commodity.containerNo} must match the SI container row for that line.
     */
    private void validateCommoditiesForExistingContainers(SIBLRequestDTO request, List<BookingCargo> cargoList, ValidationResult result) {
        List<SIBLContainerDTO> containers = request.getContainers();
        if (containers == null || containers.isEmpty()) {
            return;
        }

        List<BookingCargo> cargos = cargoList != null ? cargoList : Collections.emptyList();
        Set<Integer> bookingCargoNos = cargos.stream()
                .map(BookingCargo::getCargoNo)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Integer, BookingCargo> cargoByCargoNo = cargos.stream()
                .filter(c -> c.getCargoNo() != null)
                .collect(Collectors.toMap(BookingCargo::getCargoNo, c -> c, (a, b) -> a));

        Set<Integer> seenCargoNos = new HashSet<>();
        for (int i = 0; i < containers.size(); i++) {
            SIBLContainerDTO c = containers.get(i);
            if (c.getCargoNo() == null) {
                continue;
            }
            if (!seenCargoNos.add(c.getCargoNo())) {
                result.addError(ErrorCodes.SI_COMMODITY_CONTAINER_MAPPING_INVALID, "containers[" + i + "].cargoNo",
                        String.valueOf(c.getCargoNo()),
                        "Invalid commodity cargoNo and eqpTypeId.");
            }
        }

        Map<Integer, SIBLContainerDTO> containerByCargoNo = containers.stream()
                .filter(c -> c.getCargoNo() != null)
                .collect(Collectors.toMap(SIBLContainerDTO::getCargoNo, c -> c, (a, b) -> a));

        List<SIBLCommodityDTO> commodities = request.getCommodities();
        if (commodities == null) {
            commodities = Collections.emptyList();
        }

        for (int i = 0; i < containers.size(); i++) {
            SIBLContainerDTO c = containers.get(i);
            Integer cargoNo = c.getCargoNo();
            if (cargoNo == null) {
                continue;
            }
            boolean hasCommodityForLine = false;
            for (SIBLCommodityDTO m : commodities) {
                if (cargoNo.equals(m.getCargoNo()) && sameContainerNumber(c.getContainerNo(), m.getContainerNo())) {
                    hasCommodityForLine = true;
                    break;
                }
            }
            if (!hasCommodityForLine) {
                result.addError(ErrorCodes.SI_CONTAINER_COMMODITY_REQUIRED, "containers[" + i + "]", null,
                        "Each container must have at least one commodity.");
            }
        }

        for (int i = 0; i < commodities.size(); i++) {
            SIBLCommodityDTO m = commodities.get(i);
            Integer cargoNo = m.getCargoNo();
            if (cargoNo == null || !bookingCargoNos.contains(cargoNo)) {
                result.addError(ErrorCodes.SI_COMMODITY_CONTAINER_NOT_IN_BOOKING, "commodities[" + i + "].cargoNo",
                        cargoNo != null ? String.valueOf(cargoNo) : null,
                        "Unable to add or remove containers in SI");
                continue;
            }
            SIBLContainerDTO rc = containerByCargoNo.get(cargoNo);
            BookingCargo bc = cargoByCargoNo.get(cargoNo);
            if (rc == null || bc == null) {
                result.addError(ErrorCodes.SI_COMMODITY_CONTAINER_MAPPING_INVALID, "commodities[" + i + "].cargoNo",
                        cargoNo != null ? String.valueOf(cargoNo) : null,
                        "Invalid commodity mapping for container. Please verify cargoNo and eqpTypeId.");
                continue;
            }
            if (!Objects.equals(cargoLineKey(rc.getCargoNo(), rc.getEquipmentSize()), cargoLineKey(bc.getCargoNo(), bc.getEqpTypeId()))) {
                result.addError(ErrorCodes.SI_COMMODITY_CONTAINER_MAPPING_INVALID, "commodities[" + i + "].cargoNo",
                        String.valueOf(cargoNo),
                        "Invalid commodity mapping for container. Please verify cargoNo and eqpTypeId.");
                continue;
            }
            if (!sameContainerNumber(rc.getContainerNo(), m.getContainerNo())) {
                result.addError(ErrorCodes.SI_COMMODITY_CONTAINER_MAPPING_INVALID, "commodities[" + i + "].containerNo",
                        m.getContainerNo(),
                        "Invalid commodity mapping for container. Please verify cargoNo and eqpTypeId.");
            }
        }
    }

    /** Trim/ignore-case equality for container numbers (links commodity line to SI container for weight aggregation). */
    private static boolean sameContainerNumber(String a, String b) {
        String na = emptyToNull(a);
        String nb = emptyToNull(b);
        if (na == null && nb == null) {
            return true;
        }
        if (na == null || nb == null) {
            return false;
        }
        return na.equalsIgnoreCase(nb);
    }

    /** Stable identity for a booking/SI cargo line: sequence + equipment type (eqpTypeId / equipmentSize), not container number. */
    private static String cargoLineKey(Integer cargoNo, String eqpTypeIdOrSize) {
        int n = cargoNo != null ? cargoNo : -1;
        String t = normalizeForCompare(emptyToNull(eqpTypeIdOrSize));
        return n + "|" + (t != null ? t : "");
    }

    /**
     * Restrict validation: request values must match booking (vessel, voyage, ports, cargo and commodity attributes); no structural changes.
     * Collects all mismatches into the result (aligned with Booking V1 error list behaviour) so one response returns the full list.
     */
    private void validateRequestMatchesBooking(SIBLRequestDTO request, Booking booking, List<BookingCargo> cargoList,
                                               List<BookingCommodity> commodityList, ValidationResult result) {
        // Booking-level: vesselCode, voyageCode, loadBound, loadPortId, dischargePortId, originPortId, finalPortId
        String bVessel = emptyToNull(booking.getVesselCode());
        String bVoyage = emptyToNull(VoyageCodeUtil.normalizeToNumeric(booking.getVoyageCode()));
        String bBound = emptyToNull(booking.getBound());
        String bLoadPort = emptyToNull(booking.getLoadPortId());
        String bDischargePort = emptyToNull(booking.getDischargePortId());
        String bOrigin = emptyToNull(booking.getOriginPortId());
        if (bOrigin == null) bOrigin = bLoadPort;
        String bFinal = emptyToNull(booking.getFinalPortId());
        if (bFinal == null) bFinal = bDischargePort;

        String rVessel = emptyToNull(request.getVesselCode());
        String rVoyage = request.getVoyageCode() != null ? emptyToNull(VoyageCodeUtil.normalizeToNumeric(request.getVoyageCode())) : null;
        String rBound = request.getLoadBound() != null ? request.getLoadBound().getValue() : null;
        String rLoadPort = emptyToNull(request.getLoadPortId());
        String rDischargePort = emptyToNull(request.getDischargePortId());
        String rOrigin = emptyToNull(request.getOriginPortId());
        if (rOrigin == null) rOrigin = rLoadPort;
        String rFinal = emptyToNull(request.getFinalPortId());
        if (rFinal == null) rFinal = rDischargePort;

        if (!Objects.equals(normalizeForCompare(rVessel), normalizeForCompare(bVessel))) {
            result.addError(ErrorCodes.SI_VALUE_DIFFERS_FROM_BOOKING, "vesselCode", rVessel, mustMatchBookingMessage("vesselCode"));
        }
        if (!Objects.equals(normalizeForCompare(rVoyage), normalizeForCompare(bVoyage))) {
            result.addError(ErrorCodes.SI_VALUE_DIFFERS_FROM_BOOKING, "voyageCode", rVoyage, mustMatchBookingMessage("voyageCode"));
        }
        if (!Objects.equals(normalizeForCompare(rBound), normalizeForCompare(bBound))) {
            result.addError(ErrorCodes.SI_VALUE_DIFFERS_FROM_BOOKING, "loadBound", rBound, mustMatchBookingMessage("loadBound"));
        }
        if (!Objects.equals(normalizeForCompare(rLoadPort), normalizeForCompare(bLoadPort))) {
            result.addError(ErrorCodes.SI_VALUE_DIFFERS_FROM_BOOKING, "loadPortId", rLoadPort, mustMatchBookingMessage("loadPortId"));
        }
        if (!Objects.equals(normalizeForCompare(rDischargePort), normalizeForCompare(bDischargePort))) {
            result.addError(ErrorCodes.SI_VALUE_DIFFERS_FROM_BOOKING, "dischargePortId", rDischargePort, mustMatchBookingMessage("dischargePortId"));
        }
        if (!Objects.equals(normalizeForCompare(rOrigin), normalizeForCompare(bOrigin))) {
            result.addError(ErrorCodes.SI_VALUE_DIFFERS_FROM_BOOKING, "originPortId", rOrigin, mustMatchBookingMessage("originPortId"));
        }
        if (!Objects.equals(normalizeForCompare(rFinal), normalizeForCompare(bFinal))) {
            result.addError(ErrorCodes.SI_VALUE_DIFFERS_FROM_BOOKING, "finalPortId", rFinal, mustMatchBookingMessage("finalPortId"));
        }

        // Cargo-level: equipmentSize, tareWeight, equipmentStatus, equipmentType (match by cargoNo + eqpTypeId; container number may differ from booking)
        Map<Integer, BookingCargo> cargoByCargoNo = cargoList.stream()
                .filter(c -> c.getCargoNo() != null)
                .collect(Collectors.toMap(BookingCargo::getCargoNo, c -> c, (a, b) -> a));
        List<SIBLContainerDTO> containers = request.getContainers();
        if (containers != null) {
            for (int i = 0; i < containers.size(); i++) {
                SIBLContainerDTO rc = containers.get(i);
                Integer cargoNo = rc.getCargoNo();
                if (cargoNo == null) continue;
                BookingCargo bc = cargoByCargoNo.get(cargoNo);
                if (bc == null) continue;
                String prefix = "containers[" + i + "]";
                if (!Objects.equals(normalizeForCompare(emptyToNull(rc.getEquipmentSize())), normalizeForCompare(emptyToNull(bc.getEqpTypeId())))) {
                    result.addError(ErrorCodes.SI_VALUE_DIFFERS_FROM_BOOKING, prefix + ".equipmentSize", rc.getEquipmentSize(), mustMatchBookingMessage("equipmentSize"));
                }
                Integer rTare = rc.getTareWeight();
                Integer bTare = bc.getTareWeight() != null ? bc.getTareWeight().intValue() : null;
                // if (!Objects.equals(rTare, bTare)) {
                //     result.addError(ErrorCodes.SI_VALUE_DIFFERS_FROM_BOOKING, prefix + ".tareWeight", rTare != null ? String.valueOf(rTare) : null, mustMatchBookingMessage("tareWeight"));
                // }
                String rStatus = rc.getEquipmentStatus() != null ? rc.getEquipmentStatus().getValue() : null;
                String bStatus = emptyToNull(bc.getEqpStatus());
                if (!Objects.equals(normalizeForCompare(rStatus), normalizeForCompare(bStatus))) {
                    result.addError(ErrorCodes.SI_VALUE_DIFFERS_FROM_BOOKING, prefix + ".equipmentStatus", rStatus, mustMatchBookingMessage("equipmentStatus"));
                }
                // Dry is stored as N in booking_cargo.type; API sends D — compare using DB form (same as SiCreateService)
                String rType = rc.getEquipmentType() != null ? rc.getEquipmentType().getDbValue() : null;
                String bType = normalizeBookingEquipmentType(bc.getType());
                if (!Objects.equals(normalizeForCompare(rType), normalizeForCompare(bType))) {
                    result.addError(ErrorCodes.SI_VALUE_DIFFERS_FROM_BOOKING, prefix + ".equipmentType", rType, mustMatchBookingMessage("equipmentType"));
                }
            }
        }

        // Commodity-level: commodityGroup, hazardous (match by cargoNo + itemNo to booking_commodity)
        Map<String, BookingCommodity> commodityByKey = commodityList.stream()
                .filter(c -> c.getCargoNo() != null && c.getItemNo() != null)
                .collect(Collectors.toMap(c -> (c.getCargoNo() + "|" + c.getItemNo()), c -> c, (a, b) -> a));
        List<SIBLCommodityDTO> commodities = request.getCommodities();
        if (commodities != null) {
            for (int i = 0; i < commodities.size(); i++) {
                SIBLCommodityDTO rc = commodities.get(i);
                Integer cargoNo = rc.getCargoNo();
                if (cargoNo == null) continue;
                Integer itemNo = rc.getItemNo() != null ? rc.getItemNo() : 1;
                String key = cargoNo + "|" + itemNo;
                BookingCommodity bc = commodityByKey.get(key);
                if (bc == null) continue;
                String rComGrp = rc.getCommodityGroup() != null && !rc.getCommodityGroup().trim().isEmpty() ? rc.getCommodityGroup().trim() : null;
                String bComGrp = emptyToNull(bc.getCommodityGroup());
                if (!Objects.equals(rComGrp, bComGrp)) {
                    result.addError(ErrorCodes.SI_VALUE_DIFFERS_FROM_BOOKING, "commodities[" + i + "].commodityGroup", rComGrp, mustMatchBookingMessage("commodityGroup"));
                }
                String rHaz = rc.getHazardous() != null ? rc.getHazardous().getValue() : null;
                String bHaz = emptyToNull(bc.getHazardous());
                if (!Objects.equals(normalizeForCompare(rHaz), normalizeForCompare(bHaz))) {
                    result.addError(ErrorCodes.SI_VALUE_DIFFERS_FROM_BOOKING, "commodities[" + i + "].hazardous", rHaz, mustMatchBookingMessage("hazardous"));
                }
            }
        }
    }

    private static String normalizeForCompare(String s) {
        return s == null || s.trim().isEmpty() ? null : s.trim().toUpperCase();
    }

    /** Canonical DB equipment type for restrict validation (N = dry, R = reefer). */
    private static String normalizeBookingEquipmentType(String type) {
        String t = normalizeForCompare(type);
        if ("D".equals(t)) {
            return "N";
        }
        return t;
    }

    /** Message for restrict validation: field must match booking value. */
    private static String mustMatchBookingMessage(String fieldName) {
        return fieldName + " must match booking value.";
    }

    /**
     * Validate voyage - comprehensive check including ROE and closure
     */
    private void validateVoyage(SIBLRequestDTO request, ValidationResult result) {
        String service = request.getServiceCode();
        String vessel = request.getVesselCode();
        String voyage = VoyageCodeUtil.normalizeToNumeric(request.getVoyageCode());
        if (voyage == null) voyage = "";
        String bound = request.getLoadBound() != null ? request.getLoadBound().getValue() : "";
        String callId = request.getGroupCallId();
        String port = request.getLoadPortId();
        String terminal = request.getPolTerminal();

        // Step 1: Check if voyage ROE exists in voyageroemaster
        String roeid = getVoyageRoeId(service, vessel, voyage, bound, callId);
        if (roeid == null || roeid.isEmpty() || "Invalid".equalsIgnoreCase(roeid)) {
            result.addError(ErrorCodes.SI_VOYAGE_INVALID, "voyageCode", voyage,
                "Invalid Voyage - Voyage does not exist for the specified vessel");
            return; // Can't continue without valid ROE ID
        }

        // Step 2: Check if voyage ROE details exist in voyageroedetail
        if (!voyageRoeDetailsExist(roeid)) {
            result.addError(ErrorCodes.SI_VOYAGE_ROE_NOT_FOUND, "voyageCode", voyage,
                "VoyageRoe does not exist - No ROE details found for voyage");
            return;
        }

        // Step 3: Check if voyage is closed
        if (isVoyageClosed(service, vessel, voyage, bound, port, terminal)) {
            result.addError(ErrorCodes.SI_VOYAGE_MANIFEST_CLOSED, "voyageCode", voyage,
                "Manifest closed - Cannot create SI for this Voyage");
        }
    }

    /**
     * Get ROE ID from voyageroemaster
     */
    private String getVoyageRoeId(String service, String vessel, String voyage, String bound, String callId) {
        try {
            String sql = "SELECT TOP 1 roeid FROM voyageroemaster (NOLOCK) " +
                        "WHERE service = ? AND vslcode = ? AND voycode = ? AND bound = ? AND callid = ?";
            List<String> results = reportJdbcTemplate.queryForList(sql, String.class,
                safe(service), safe(vessel), safe(voyage), safe(bound), safe(callId));
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            logger.error("Error getting voyage ROE ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Check if voyage ROE details exist
     */
    private boolean voyageRoeDetailsExist(String roeid) {
        if (isEmpty(roeid)) return false;
        try {
            String sql = "SELECT COUNT(1) FROM voyageroedetail (NOLOCK) WHERE roeid = ?";
            Integer count = reportJdbcTemplate.queryForObject(sql, Integer.class, roeid.trim());
            return count != null && count > 0;
        } catch (Exception e) {
            logger.error("Error checking voyage ROE details: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if voyage is closed
     */
    private boolean isVoyageClosed(String service, String vessel, String voyage, 
                                   String bound, String port, String terminal) {
        try {
            String sql = "SELECT isclose FROM voyageclosure (NOLOCK) " +
                        "WHERE service = ? AND vessel = ? AND voyage = ? " +
                        "AND bound = ? AND port = ? AND terminal = ?";
            List<String> results = reportJdbcTemplate.queryForList(sql, String.class,
                safe(service), safe(vessel), safe(voyage), safe(bound), safe(port), safe(terminal));
            if (results.isEmpty()) return false;
            String isClosed = results.get(0);
            return "Y".equalsIgnoreCase(isClosed != null ? isClosed.trim() : "N");
        } catch (Exception e) {
            logger.error("Error checking voyage closure: " + e.getMessage());
            return false;
        }
    }

    // ==================== HELPER METHODS ====================

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String safe(String value) {
        return value != null ? value.trim() : "";
    }

    // ==================== VALIDATION RESULT CLASS ====================

    /**
     * Validation result containing all validation errors (error codes aligned with booking ErrorCodes 51xx/52xx/53xx).
     */
    public static class ValidationResult {
        private List<ValidationError> errors = new ArrayList<>();

        public boolean isValid() {
            return errors.isEmpty();
        }

        public void addError(int errorCode, String field, String value, String message) {
            errors.add(new ValidationError(errorCode, field, value, message));
        }

        public List<ValidationError> getErrors() {
            return errors;
        }

        public String getFirstErrorMessage() {
            return errors.isEmpty() ? null : errors.get(0).getMessage();
        }

        /** Returns first error code as string for backward compatibility. */
        public String getFirstErrorCode() {
            return errors.isEmpty() ? null : String.valueOf(errors.get(0).getErrorCode());
        }

        public int getFirstErrorCodeInt() {
            return errors.isEmpty() ? 0 : errors.get(0).getErrorCode();
        }

        public String getFirstErrorField() {
            return errors.isEmpty() ? null : errors.get(0).getField();
        }
    }

    /**
     * Single validation error (errorCode from ErrorCodes, same pattern as booking).
     */
    public static class ValidationError {
        private int errorCode;
        private String field;
        private String value;
        private String message;

        public ValidationError(int errorCode, String field, String value, String message) {
            this.errorCode = errorCode;
            this.field = field;
            this.value = value;
            this.message = message;
        }

        public int getErrorCode() { return errorCode; }
        public String getField() { return field; }
        public String getValue() { return value; }
        public String getMessage() { return message; }
    }
}

