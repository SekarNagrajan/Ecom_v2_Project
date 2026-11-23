package com.solverminds.sibl.service;

import com.solverminds.bl.common.entity.*;
import com.solverminds.bl.common.repository.*;
import com.solverminds.booking.common.entity.*;
import com.solverminds.booking.common.repository.*;
import com.solverminds.sibl.dto.BookingRequestDTO;
import com.solverminds.sibl.dto.SIBLCommodityDTO;
import com.solverminds.sibl.dto.SIBLContainerDTO;
import com.solverminds.sibl.dto.SIBLCustomerDTO;
import com.solverminds.sibl.dto.SIBLDangerousGoodsDTO;
import com.solverminds.sibl.dto.SIBLOogDTO;
import com.solverminds.sibl.dto.SIBLReeferDTO;
import com.solverminds.sibl.dto.SIBLRequestDTO;
import com.solverminds.sibl.entity.*;
import com.svm.ecom.common.enums.*;
import com.solverminds.sibl.repo.PortNameRepository;
import com.solverminds.sibl.repo.*;
import com.solverminds.sibl.util.VoyageCodeUtil;
import com.svm.ecom.common.master.cache.CommonMasterDataCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.svm.ecom.constants.DateFormatConstants;

/**
 * Service for retrieving SI details with all related data.
 * Supports three data sources for bill-of-lading view (priority: bookingNo → onlineSINo → blNumber):
 * 1. Booking (via bookingNo) - same logic as booking view (in-process), maps to SIBLRequestDTO
 * 2. ECOM database (via onlineSINo/eSINo) - uses EcomSi* entities
 * 3. Documentation database (via blNumber) - uses <b>bl-common</b> (Bl, BlHeader, BlCust, BlCargo, BlCommodity entities and repositories)
 */
@Service
@Transactional(transactionManager = "ecomTransactionManager", readOnly = true)
public class SiViewService {

    private static final Logger logger = LoggerFactory.getLogger(SiViewService.class);

    private final EcomSiRepository siRepo;
    private final EcomSiContainerRepository containerRepo;
    private final EcomSiCommodityRepository commodityRepo;
    private final EcomSiCustomerRepository customerRepo;
    private final EcomSiReeferRepository reeferRepo;
    private final EcomSiDgRepository dgRepo;
    private final EcomSiOogRepository oogRepo;
    /** bl-common: BL / documentation DB (used when SI view fetches by blNumber) */
    private final BlRepository blRepository;
    private final BlHeaderRepository blHeaderRepository;
    private final BlCustRepository blCustRepository;
    private final BlCargoRepository blCargoRepository;
    private final BlCommodityRepository blCommodityRepository;

    private final BookingViewRepository bookingViewRepository;
    private final BookingCustViewRepository bookingCustViewRepository;
    private final BookingLegViewRepository bookingLegViewRepository;
    private final BookingCargoViewRepository bookingCargoViewRepository;
    private final BookingCommodityViewRepository bookingCommodityViewRepository;
    private final BookingReferenceViewRepository bookingReferenceViewRepository;
    private final PortNameRepository portNameRepository;
    private final CommonMasterDataCacheService commonMasterDataCacheService;

    /** Service for enriching SI view from Documentation DB (booking table); same pattern as booking-service DocumentationBookingViewService. */
    private DocumentationBookingEnrichmentService documentationBookingEnrichmentService;

    public SiViewService(
            EcomSiRepository siRepo,
            EcomSiContainerRepository containerRepo,
            EcomSiCommodityRepository commodityRepo,
            EcomSiCustomerRepository customerRepo,
            EcomSiReeferRepository reeferRepo,
            EcomSiDgRepository dgRepo,
            EcomSiOogRepository oogRepo,
            BlRepository blRepository,
            BlHeaderRepository blHeaderRepository,
            BlCustRepository blCustRepository,
            BlCargoRepository blCargoRepository,
            BlCommodityRepository blCommodityRepository,
            BookingViewRepository bookingViewRepository,
            BookingCustViewRepository bookingCustViewRepository,
            BookingLegViewRepository bookingLegViewRepository,
            BookingCargoViewRepository bookingCargoViewRepository,
            BookingCommodityViewRepository bookingCommodityViewRepository,
            BookingReferenceViewRepository bookingReferenceViewRepository,
            PortNameRepository portNameRepository,
            CommonMasterDataCacheService commonMasterDataCacheService) {
        this.siRepo = siRepo;
        this.containerRepo = containerRepo;
        this.commodityRepo = commodityRepo;
        this.customerRepo = customerRepo;
        this.reeferRepo = reeferRepo;
        this.dgRepo = dgRepo;
        this.oogRepo = oogRepo;
        this.blRepository = blRepository;
        this.blHeaderRepository = blHeaderRepository;
        this.blCustRepository = blCustRepository;
        this.blCargoRepository = blCargoRepository;
        this.blCommodityRepository = blCommodityRepository;
        this.bookingViewRepository = bookingViewRepository;
        this.bookingCustViewRepository = bookingCustViewRepository;
        this.bookingLegViewRepository = bookingLegViewRepository;
        this.bookingCargoViewRepository = bookingCargoViewRepository;
        this.bookingCommodityViewRepository = bookingCommodityViewRepository;
        this.bookingReferenceViewRepository = bookingReferenceViewRepository;
        this.portNameRepository = portNameRepository;
        this.commonMasterDataCacheService = commonMasterDataCacheService;
    }

    /**
     * Setter for DocumentationBookingEnrichmentService (optional dependency).
     * Using setter injection to avoid circular dependency, same as booking-service DocumentationBookingViewService.
     */
    @Autowired(required = false)
    public void setDocumentationBookingEnrichmentService(DocumentationBookingEnrichmentService documentationBookingEnrichmentService) {
        this.documentationBookingEnrichmentService = documentationBookingEnrichmentService;
    }

    /**
     * Find bill-of-lading view data by priority: bookingNo → onlineSINo → blNumber (first non-empty wins).
     * Returns SIBLRequestDTO (same shape as SI create payload) for unified UI.
     * No status filtering.
     *
     * @param customerCode from JWT
     * @param bookingNo     optional; when provided, same logic as booking view (in-process), map to SIBLRequestDTO
     * @param onlineSINo   optional; when provided, fetch from ecomSI and build SIBLRequestDTO
     * @param blNumber     optional; when provided, fetch from bl and build SIBLRequestDTO (onlineSINo in response = null)
     * @return Optional with SIBLRequestDTO if found; empty otherwise
     */
    public Optional<SIBLRequestDTO> findBillOfLadingView(String customerCode, String bookingNo,
                                                         String onlineSINo, String blNumber) {
        String b = bookingNo != null ? bookingNo.trim() : "";
        String s = onlineSINo != null ? onlineSINo.trim() : "";
        String bl = blNumber != null ? blNumber.trim() : "";

        if (!b.isEmpty()) {
            logger.debug("Bill-of-lading view by bookingNo: {}", b);
            return findFromBookingAsSIBL(customerCode, b);
        }
        if (!s.isEmpty()) {
            logger.debug("Bill-of-lading view by onlineSINo: {}", s);
            return findFromEcomDatabaseAsSIBL(customerCode, s);
        }
        if (!bl.isEmpty()) {
            logger.debug("Bill-of-lading view by blNumber: {}", bl);
            return findFromBlTableAsSIBL(customerCode, bl);
        }
        logger.debug("No valid key provided for bill-of-lading view");
        return Optional.empty();
    }

    @Transactional(transactionManager = "documentationTransactionManager", readOnly = true)
    private Optional<SIBLRequestDTO> findFromBookingAsSIBL(String customerCode, String bookingNo) {
        Optional<Booking> bookingOpt = bookingViewRepository.findByBookNo(bookingNo);
        if (!bookingOpt.isPresent()) {
            logger.debug("Booking not found for bookingNo: {}", bookingNo);
            return Optional.empty();
        }
        Booking booking = bookingOpt.get();
        String bookNo = booking.getBookNo();
        List<BookingCust> custList = bookingCustViewRepository.findByBookNoOrderByCid(bookNo);
        List<BookingLeg> legs = bookingLegViewRepository.findByBookNoOrderByLegNo(bookNo);
        List<BookingCargo> cargoList = bookingCargoViewRepository.findByBookNoOrderByCargoNo(bookNo);
        List<BookingCommodity> commodityList = bookingCommodityViewRepository.findByBookNoOrderByCargoNoAndItemNo(bookNo);
        List<BookingReference> refList = bookingReferenceViewRepository.findByBookNoOrderById(bookNo);
        SIBLRequestDTO dto = BookingCommonToSIBLMapper.fromBookingCommon(
                booking, legs, custList, cargoList, commodityList, refList);
        if (dto != null) {
            applyViewResponseRules(dto);
            // When view is by bookingNo: set port header names from report DB (ports) and SH/CN/NT from booking_cust
            enrichBookingViewWithPortNamesAndParties(dto, custList);
            // polTerminal and podTerminal remain as terminal codes per spec (not names)
        }
        return Optional.ofNullable(dto);
    }

    /**
     * Set plaHeaderName, polHeaderName, podHeaderName, pldHeaderName from port names (report DB) using
     * originPortId → plaHeaderName, loadPortId → polHeaderName, dischargePortId → podHeaderName, finalPortId → pldHeaderName.
     * Used for SI view by bookingNo, by SI no (ecomSI), and by BL no (bl table) so all paths return header names consistently.
     */
    private void enrichPortHeaderNames(SIBLRequestDTO dto) {
        Map<String, String> portNameMap = portNameRepository.getPortNameMap();
        String originPortId = emptyIfNull(dto.getOriginPortId()).trim();
        String loadPortId = emptyIfNull(dto.getLoadPortId()).trim();
        String dischargePortId = emptyIfNull(dto.getDischargePortId()).trim();
        String finalPortId = emptyIfNull(dto.getFinalPortId()).trim();
        if (!originPortId.isEmpty()) dto.setPlaHeaderName(portNameMap.getOrDefault(originPortId, ""));
        if (!loadPortId.isEmpty()) dto.setPolHeaderName(portNameMap.getOrDefault(loadPortId, ""));
        if (!dischargePortId.isEmpty()) dto.setPodHeaderName(portNameMap.getOrDefault(dischargePortId, ""));
        if (!finalPortId.isEmpty()) dto.setPldHeaderName(portNameMap.getOrDefault(finalPortId, ""));
    }

    /**
     * For SI view by bookingNo: set plaHeaderName, polHeaderName, podHeaderName, pldHeaderName from port names (report DB),
     * and shipper/consigee/notify name and address from booking_cust where custTypeId in ('SH','CN','NT').
     */
    private void enrichBookingViewWithPortNamesAndParties(SIBLRequestDTO dto, List<BookingCust> custList) {
        enrichPortHeaderNames(dto);

        if (custList != null) {
            for (BookingCust c : custList) {
                String type = c.getCustTypeId() != null ? c.getCustTypeId().trim().toUpperCase() : "";
                String name = emptyIfNull(c.getName());
                String address1 = emptyIfNull(c.getAddress1());
                if ("SH".equals(type)) {
                    dto.setShipperName(name);
                    dto.setShipperAddress(address1);
                } else if ("CN".equals(type)) {
                    dto.setConsigeeName(name);
                    dto.setConsigeeAddress(address1);
                } else if ("NT".equals(type)) {
                    dto.setNotifyName(name);
                    dto.setNotifyAddress(address1);
                }
            }
        }
    }

    @Transactional(transactionManager = "ecomTransactionManager", readOnly = true)
    private Optional<SIBLRequestDTO> findFromEcomDatabaseAsSIBL(String customerCode, String onlineSINo) {
        Optional<EcomSi> siOpt = siRepo.findByeSINo(onlineSINo);
        if (!siOpt.isPresent()) {
            logger.debug("SI not found in ecomsi with onlineSINo: {}", onlineSINo);
            return Optional.empty();
        }
        EcomSi si = siOpt.get();
        if (!matchesCustomer(si, customerCode)) {
            logger.warn("SI {} customer mismatch; returning anyway for view", onlineSINo);
        }
        String eSINo = si.geteSINo();
        SIBLRequestDTO dto = mapEcomSiToSIBLRequestDTO(si,
                fetchSIBLCustomers(eSINo),
                fetchSIBLContainers(eSINo),
                fetchSIBLCommodities(eSINo));
        dto.setCustomers(excludeApBpCustomers(dto.getCustomers()));
        // When view is by SI number, ecomsi does not have serviceCode, groupCallId, polTerminal, podTerminal; enrich from Documentation DB (booking) when bookno is present
        String bookNo = emptyIfNull(si.getBookno()).trim();
        if (!bookNo.isEmpty() && documentationBookingEnrichmentService != null) {
            documentationBookingEnrichmentService.enrichSIBLFromBooking(dto, bookNo);
        }
        applyViewResponseRules(dto);
        // When ecomSI header names are empty, fill from port lookup (originPortId → pla, loadPortId → pol, etc.) for consistency
        if (emptyIfNull(dto.getPlaHeaderName()).isEmpty() || emptyIfNull(dto.getPolHeaderName()).isEmpty()
                || emptyIfNull(dto.getPodHeaderName()).isEmpty() || emptyIfNull(dto.getPldHeaderName()).isEmpty()) {
            enrichPortHeaderNames(dto);
        }
        // polTerminal and podTerminal remain as terminal codes per spec
        return Optional.of(dto);
    }

    /**
     * Apply SI View response rules (same for bookingNo, onlineSINo, blNumber): do not show AP/BP customers;
     * for each cargo in container list: only show reefer when reefer=Y, only show OOG dimensions when oogFlag=Y; DG is at commodity level only.
     * for each commodity: only show dangerousGoods when hazardous=Y, only show oog when container (cargoNo) has oogFlag=Y.
     */
    private void applyViewResponseRules(SIBLRequestDTO dto) {
        if (dto == null) return;
        dto.setCustomers(excludeApBpCustomers(dto.getCustomers()));
        if (dto.getContainers() != null) {
            for (SIBLContainerDTO c : dto.getContainers()) {
                if (c != null) {
                    if (c.getReefer() != YesNoFlag.Y) c.setReeferSettings(null);
                    if (c.getOogFlag() != YesNoFlag.Y) c.setOogDimensions(null);
                }
            }
        }
        java.util.Set<Integer> cargoNosWithOog = cargoNosWithOogFlagYForRequest(dto.getContainers());
        if (dto.getCommodities() != null) {
            for (SIBLCommodityDTO c : dto.getCommodities()) {
                if (c != null) {
                    if (c.getHazardous() != YesNoFlag.Y) c.setDangerousGoods(null);
                    // OOG: only show when container (cargoNo) has oogFlag=Y (same rule as booking)
                    if (c.getCargoNo() == null || !cargoNosWithOog.contains(c.getCargoNo())) c.setOog(null);
                }
            }
        }
    }

    /** Cargo numbers (conId) where container has oogFlag=Y; used by applyViewResponseRules for commodity OOG visibility. */
    private static java.util.Set<Integer> cargoNosWithOogFlagYForRequest(List<SIBLContainerDTO> containers) {
        java.util.Set<Integer> set = new java.util.HashSet<>();
        if (containers == null) return set;
        for (SIBLContainerDTO c : containers) {
            if (c != null && c.getOogFlag() == YesNoFlag.Y && c.getCargoNo() != null) set.add(c.getCargoNo());
        }
        return set;
    }

    /** Fetch SI view by BL number using bl-common (Bl, BlHeader, BlCust, BlCargo, BlCommodity). */
    @Transactional(transactionManager = "documentationTransactionManager", readOnly = true)
    private Optional<SIBLRequestDTO> findFromBlTableAsSIBL(String customerCode, String blNumber) {
        try {
            Optional<Bl> blOpt = blRepository.findByBlNo(blNumber);
            if (!blOpt.isPresent()) {
                logger.debug("BL not found with blNumber: {}", blNumber);
                return Optional.empty();
            }
            Bl bl = blOpt.get();
            String blNo = bl.getBlNo();
            List<BlCust> blCustList = blCustRepository.findByBlNoOrderByCid(blNo);
            List<BlCargo> blCargoList = blCargoRepository.findByBlNoOrderByCargoNo(blNo);
            Optional<BlHeader> blHeaderOpt = blHeaderRepository.findByBlNo(blNo);
            SIBLRequestDTO dto = mapBlToSIBLRequestDTO(bl, blHeaderOpt.orElse(null),
                    blCustList, blCargoList);
            applyViewResponseRules(dto);
            // Set plaHeaderName, polHeaderName, podHeaderName, pldHeaderName from port lookup (originPortId, loadPortId, dischargePortId, finalPortId)
            enrichPortHeaderNames(dto);
            // polTerminal and podTerminal remain as terminal codes per spec
            return Optional.of(dto);
        } catch (Exception e) {
            logger.error("Error loading BL for view: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    private SIBLRequestDTO mapEcomSiToSIBLRequestDTO(EcomSi si,
                                                     List<SIBLCustomerDTO> customers,
                                                     List<SIBLContainerDTO> containers,
                                                     List<SIBLCommodityDTO> commodities) {
        SIBLRequestDTO dto = new SIBLRequestDTO();
        dto.setOnlineSINo(emptyIfNull(si.geteSINo()));
        dto.setBlNo(emptyIfNull(si.getBlno()));
        dto.setBookingNumber(emptyIfNull(si.getBookno()));
        dto.setVesselCode(emptyIfNull(si.getVessel()));
        dto.setVoyageCode(emptyIfNull(VoyageCodeUtil.normalizeToNumeric(si.getVoyage())));
        dto.setLoadBound(safeBound(si.getBound()));
        dto.setLoadPortId(emptyIfNull(si.getPol()));
        dto.setDischargePortId(emptyIfNull(si.getPod()));
        dto.setOriginPortId(emptyIfNull(si.getPla()));
        dto.setFinalPortId(emptyIfNull(si.getPld()));
        dto.setCustomerCodeFromJWT(emptyIfNull(si.getCustcode()));
        dto.setCreatedUser(emptyIfNull(si.getCrUser()));
        dto.setCreatedDate(si.getCrDate());
        dto.setChangeUser(emptyIfNull(si.getChgUser()));
        dto.setChangeDate(si.getChgDate());
        dto.setEditBl(si.getEditbl() != null ? si.getEditbl() : 0);
        dto.setBlVersion(si.getBlversion() != null ? si.getBlversion() : 0);
        dto.setStatus(safeSiStatus(si.getStatus()));
        dto.setDeclinedRemarks(emptyIfNull(si.getDeclinedRemarks()));
        dto.setAdditionalRemarks(emptyIfNull(si.getCustremarks()));
        dto.setPlaHeaderName(emptyIfNull(si.getPlaHeaderName()));
        dto.setPolHeaderName(emptyIfNull(si.getPolHeaderName()));
        dto.setPodHeaderName(emptyIfNull(si.getPodHeaderName()));
        dto.setPldHeaderName(emptyIfNull(si.getPldHeaderName()));
        dto.setAppVersion(si.getAppversion() != null ? si.getAppversion() : 0);
        dto.setCcMailId(emptyIfNull(si.getCcmailid()));
        dto.setShipperAddress(emptyIfNull(si.getShipper()));
        dto.setShipperName(emptyIfNull(si.getShippername()));
        dto.setConsigeeAddress(emptyIfNull(si.getConsigee()));
        dto.setConsigeeName(emptyIfNull(si.getConsigeename()));
        dto.setNotifyAddress(emptyIfNull(si.getNotify()));
        dto.setNotifyName(emptyIfNull(si.getNotifyname()));
        dto.setAesNumber(emptyIfNull(si.getAesNumber()));
        dto.setAesDisclaimer(emptyIfNull(si.getAesDiscliamer()));
        dto.setReleaseType(emptyIfNull(si.getReleasetype()));
        dto.setDeclaredValue(emptyIfNull(si.getDeclaredvalue()));
        dto.setBlType(emptyIfNull(si.getBlType()));
        dto.setAcid(emptyIfNull(si.getAcid()));
        dto.setFreight(safeFreightPaymentType(si.getFreight()));
        dto.setCargoProtectId(si.getCargoprotectid());
        dto.setT2lFilling(safeYesNoFlag(si.getT2lfiling()));
        dto.setPackingList(emptyIfNull(si.getPackinglst()));
        dto.setInvoiceUpload(emptyIfNull(si.getInvoiceupload()));
        dto.setTelexRelease(emptyIfNull(si.getTelexrls()));
        dto.setEnsType(safeEnsType(si.getEnsType()));
        dto.setCustomers(customers != null ? customers : new ArrayList<>());
        dto.setContainers(containers != null ? containers : new ArrayList<>());
        dto.setCommodities(commodities != null ? commodities : new ArrayList<>());
        return dto;
    }

    private List<SIBLCustomerDTO> fetchSIBLCustomers(String eSINo) {
        List<EcomSiCustomer> entities = customerRepo.findByeSINo(eSINo);
        List<SIBLCustomerDTO> out = new ArrayList<>();
        for (EcomSiCustomer e : entities) {
            SIBLCustomerDTO dto = new SIBLCustomerDTO();
            dto.setOnlineSINo(eSINo);
            dto.setCustomerType(safeCustomerType(e.getCustTypeId()));
            dto.setCustomerCode(emptyIfNull(e.getCustId()));
            dto.setCountryId(emptyIfNull(e.getCountryId()));
            dto.setCustomerName(emptyIfNull(e.getName()));
            dto.setAddress(emptyIfNull(e.getAddress1()));
            dto.setAddress2(emptyIfNull(e.getAddress2()));
            dto.setCity(emptyIfNull(e.getCity()));
            dto.setStateId(emptyIfNull(e.getStateId()));
            dto.setTelephone(emptyIfNull(e.getTelephone()));
            dto.setEmail(emptyIfNull(e.getEmail()));
            out.add(dto);
        }
        return out;
    }

    /**
     * Exclude AP and BP customer types from SI view customer list.
     * When fetching by bookingNo, onlineSINo, or blNumber, AP/BP customer details are not shown.
     */
    private List<SIBLCustomerDTO> excludeApBpCustomers(List<SIBLCustomerDTO> customers) {
        if (customers == null || customers.isEmpty()) {
            return customers;
        }
        return customers.stream()
                .filter(c -> {
                    CustomerType type = c != null ? c.getCustomerType() : null;
                    return type == null || (type != CustomerType.AP && type != CustomerType.BP);
                })
                .collect(Collectors.toList());
    }

    private List<SIBLContainerDTO> fetchSIBLContainers(String eSINo) {
        List<EcomSiContainer> entities = containerRepo.findByeSINo(eSINo);
        List<EcomSiReefer> reefers = reeferRepo.findByeSINo(eSINo);
        List<EcomSiDg> dgList = dgRepo.findByeSINo(eSINo);
        List<EcomSiOog> oogs = oogRepo.findByeSINo(eSINo);
        List<SIBLContainerDTO> out = new ArrayList<>();
        for (EcomSiContainer e : entities) {
            SIBLContainerDTO dto = new SIBLContainerDTO();
            dto.setOnlineSINo(eSINo);
            dto.setCargoNo(e.getConId() != null ? e.getConId() : 1);
            dto.setEquipmentSize(emptyIfNull(e.getEqpSize()));
            dto.setContainerNo(emptyIfNull(e.getContainerno()));
            dto.setSocFlag(safeYesNoFlag(e.getShipperOwn()));
            dto.setCarrierSeal(emptyIfNull(e.getCarrierSeal()));
            dto.setShipperSeal(emptyIfNull(e.getShipperSeal()));
            dto.setTareWeight(e.getTareWeight());
            dto.setCargoWeight(e.getCargoWeight() != null ? e.getCargoWeight() : 0);
            dto.setGrossweight(e.getGrossweight());
            dto.setWeightUnit(safeWeightUnit(e.getWeightUnit()));
            dto.setNor(safeYesNoFlag(e.getNor()));
            dto.setEquipmentStatus(safeEquipmentStatus(e.getEquipmentStatus()));
            dto.setOogFlag(safeYesNoFlag(e.getOog()));
            dto.setRateEquipmentTypeId(emptyIfNull(e.getRateEquipmentSize()));
            dto.setEquipmentType(safeEquipmentType(e.getEqpType()));
            // Reefer: only show SIBLReeferDTO when reeferFlag=Y (reefer entity present and nor!=Y). Temperature: convertEmptyToNull.
            EcomSiReefer reefer = findReeferForContainer(reefers, e.getConId(), e.getContainerno());
            boolean isReefer = reefer != null && !"Y".equalsIgnoreCase(e.getNor());
            dto.setReefer(isReefer ? YesNoFlag.Y : YesNoFlag.N);
            if (isReefer) {
                SIBLReeferDTO r = new SIBLReeferDTO();
                r.setTempSet(convertEmptyToNull(reefer.getRefSetTemp()));
                r.setTempMin(convertEmptyToNull(reefer.getRefMinTemp()));
                r.setTempMax(convertEmptyToNull(reefer.getRefMaxTemp()));
                r.setTempSetUnit(safeTemperatureUnit(reefer.getRefTempUnit()));
                if (reefer.getHumidity() != null && !reefer.getHumidity().isEmpty()) { Double v = parseDoubleSafe(reefer.getHumidity()); if (v != null) r.setHumidity(v); }
                r.setHumidityFlag(reefer.getHumidityFlag() != null && Boolean.TRUE.equals(reefer.getHumidityFlag()) ? YesNoFlag.Y : YesNoFlag.N);
                String ventPct = reefer.getVentPercentage();
                if (ventPct != null && !ventPct.trim().isEmpty()) {
                    r.setVentilation(ventPct.trim());
                    r.setVentSet(1);
                } else {
                    r.setVentSet(0);
                }
                if (reefer.getCo2Value() != null && !reefer.getCo2Value().isEmpty()) { Double v = parseDoubleSafe(reefer.getCo2Value()); if (v != null) r.setCo2Level(v); }
                if (reefer.getO2Value() != null && !reefer.getO2Value().isEmpty()) { Double v = parseDoubleSafe(reefer.getO2Value()); if (v != null) r.setO2Level(v); }
                r.setDrains(emptyIfNull(reefer.getDrains()));
                dto.setReeferSettings(r);
            }
            // DG is at commodity level only; no dgFlag/dgDetails on container
            // OOG: only show SIBLOogDTO when oogFlag=Y (oogFlag already set above)
            if ("Y".equalsIgnoreCase(e.getOog())) {
                EcomSiOog oog = findOogForContainer(oogs, e.getConId(), e.getContainerno());
                if (oog != null) {
                    SIBLOogDTO oogDto = new SIBLOogDTO();
                    oogDto.setLeftFt(oog.getLeftft());
                    oogDto.setRightFt(oog.getRightft());
                    oogDto.setForward(oog.getForward());
                    oogDto.setAft(oog.getAlt());
                    oogDto.setTopFt(oog.getTopft());
                    oogDto.setUnit(safeLengthUnit(oog.getUnit()));
                    dto.setOogDimensions(oogDto);
                }
            }
            out.add(dto);
        }
        return out;
    }

    /** Temperature values: convertEmptyToNull - return null when value is null or empty (e.g. NaN). */
    private static Double convertEmptyToNull(Float f) {
        if (f == null || Float.isNaN(f)) return null;
        return f.doubleValue();
    }

    /** Find first OOG record for container (by conId, containerno; container-level OOG typically comItemNo=1). */
    private EcomSiOog findOogForContainer(List<EcomSiOog> oogs, Integer conId, String containerNo) {
        if (oogs == null || conId == null) return null;
        for (EcomSiOog oog : oogs) {
            if (conId.equals(oog.getConId()) && (containerNo == null || containerNo.equals(oog.getContainerno()))) {
                return oog;
            }
        }
        return null;
    }

    private List<SIBLCommodityDTO> fetchSIBLCommodities(String eSINo) {
        List<EcomSiCommodity> entities = commodityRepo.findByeSINo(eSINo);
        List<EcomSiOog> oogs = oogRepo.findByeSINo(eSINo);
        List<EcomSiDg> dgList = dgRepo.findByeSINo(eSINo);
        List<EcomSiContainer> containers = containerRepo.findByeSINo(eSINo);
        List<SIBLCommodityDTO> out = new ArrayList<>();
        for (EcomSiCommodity e : entities) {
            SIBLCommodityDTO dto = new SIBLCommodityDTO();
            dto.setOnlineSINo(eSINo);
            dto.setCargoNo(e.getConId() != null ? e.getConId() : 1);
            dto.setContainerNo(emptyIfNull(e.getContainerno()));
            dto.setItemNo(e.getItemno() != null ? e.getItemno() : 1);
            dto.setCommodityGroup(emptyIfNull(e.getCommodity()));
            dto.setPackageType(emptyIfNull(e.getPackageType()));
            dto.setPackageQty(e.getPackageQty() != null ? e.getPackageQty() : 0);
            dto.setItemWeight(e.getItemWeight() != null ? e.getItemWeight().intValue() : null);
            dto.setItemWeightUnit(safeWeightUnit(e.getItemWeightUnit()));
            dto.setNettwt(e.getNettwt() != null ? e.getNettwt().intValue() : null);
            dto.setItemVolume(e.getItemVolume());
            dto.setItemVolumeUnit(safeVolumeUnit(e.getItemVolumeUnit()));
            dto.setHsCode(emptyIfNull(e.getHsCommodity()));
            dto.setMarksNo(emptyIfNull(e.getMarksNo()));
            dto.setMarksDesc(emptyIfNull(e.getMarksDesc()));
            dto.setCommodityHBLNo(emptyIfNull(e.getCmdtyHblno()));
            dto.setBuyerCode(emptyIfNull(e.getBycode()));
            dto.setSellerCode(emptyIfNull(e.getSlcode()));
            dto.setBuyerName(emptyIfNull(e.getByname()));
            dto.setSellerName(emptyIfNull(e.getSlname()));
            dto.setHazardous(safeYesNoFlag(e.getHazardous()));
            EcomSiContainer cont = findContainerForCommodity(containers, e.getConId(), e.getContainerno());
            if (cont != null && "Y".equalsIgnoreCase(cont.getOog())) {
                EcomSiOog oog = findOogForCommodity(oogs, e.getConId(), e.getContainerno(), e.getItemno());
                if (oog != null) {
                    SIBLOogDTO oogDto = new SIBLOogDTO();
                    oogDto.setLeftFt(oog.getLeftft());
                    oogDto.setRightFt(oog.getRightft());
                    oogDto.setForward(oog.getForward());
                    oogDto.setAft(oog.getAlt());
                    oogDto.setTopFt(oog.getTopft());
                    oogDto.setUnit(safeLengthUnit(oog.getUnit()));
                    dto.setOog(oogDto);
                }
            }
            // DG at commodity level; also show legacy container-level DG (itemno=0) on first commodity
            List<EcomSiDg> forItem = findDangerousGoodsForCommodity(dgList, e.getConId(), e.getContainerno(), e.getItemno());
            if (forItem == null || forItem.isEmpty()) {
                forItem = findDangerousGoodsForCommodity(dgList, e.getConId(), e.getContainerno(), 0);
                if (forItem != null && !forItem.isEmpty()) {
                    dto.setHazardous(YesNoFlag.Y);
                }
            }
            if (forItem != null && !forItem.isEmpty()) {
                EcomSiDg first = forItem.get(0);
                SIBLDangerousGoodsDTO dgDto = new SIBLDangerousGoodsDTO();
                dgDto.setMarinePollutant(safeYesNoFlag(first.getMarinePollutant()));
                dgDto.setUnNo(emptyIfNull(first.getUnNo()));
                dgDto.setImdgClass(emptyIfNull(first.getImdgClass()));
                dgDto.setFlashPt(first.getFlashPt() != null ? first.getFlashPt().doubleValue() : null);
                dgDto.setFlashPtUnit(safeFlashPtUnit(first.getFlashPtUnit()));
                dgDto.setImoContact(emptyIfNull(first.getImoContact()));
                dgDto.setImoPhone(emptyIfNull(first.getImoPhone()));
                dto.setDangerousGoods(dgDto);
            }
            out.add(dto);
        }
        return out;
    }

    private static int parseIntSafe(String s, int defaultVal) {
        if (s == null || s.trim().isEmpty()) return defaultVal;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private static Double parseDoubleSafe(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return Double.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Maps bl-common entities (Bl, BlHeader, BlCust, BlCargo, BlCommodity) to SIBLRequestDTO for SI view by blNumber. */
    private SIBLRequestDTO mapBlToSIBLRequestDTO(Bl bl, BlHeader blHeader,
                                                  List<BlCust> blCustList,
                                                  List<BlCargo> blCargoList) {
        SIBLRequestDTO dto = new SIBLRequestDTO();
        dto.setOnlineSINo(null); // Bl entity has no eSINo
        dto.setBlNo(emptyIfNull(bl.getBlNo()));
        dto.setBookingNumber(emptyIfNull(bl.getBookNo()));
        dto.setAgencyRefNo(emptyIfNull(bl.getAgencyRefNo()));
        dto.setVesselCode(emptyIfNull(bl.getVesselCode()));
        dto.setVoyageCode(emptyIfNull(VoyageCodeUtil.normalizeToNumeric(bl.getVoyageCode())));
        dto.setLoadBound(safeBound(bl.getBound()));
        dto.setLoadPortId(emptyIfNull(bl.getLoadPortId()));
        dto.setDischargePortId(emptyIfNull(bl.getDischargePortId()));
        dto.setOriginPortId(emptyIfNull(bl.getOriginPortId()));
        dto.setFinalPortId(emptyIfNull(bl.getFinalPortId()));
        dto.setServiceCode(emptyIfNull(bl.getServiceCode()));
        dto.setGroupCallId(emptyIfNull(bl.getGrpCallId()));
        dto.setPolTerminal(emptyIfNull(bl.getPolTerminal()));
        dto.setPodTerminal(emptyIfNull(bl.getPodTerminal()));
        dto.setEnsFilling(safeEnsFillingType(bl.getEnsFiling()));
        dto.setStatus(safeSiStatus(bl.getStatus()));
        dto.setEditBl(0);
        dto.setBlVersion(0);
        dto.setCustomers(blCustListToSIBL(blCustList));
        List<SIBLContainerDTO> containers = new ArrayList<>();
        List<SIBLCommodityDTO> commodities = new ArrayList<>();
        Set<String> containerKeys = new LinkedHashSet<>();
        Set<String> commodityKeys = new LinkedHashSet<>();
        for (BlCargo c : blCargoList) {
            String eqpId = c.getEqpId();
            String eqpSize = c.getEqpTypeId();
            if (eqpId != null && !containerKeys.contains(eqpId)) {
                containerKeys.add(eqpId);
                SIBLContainerDTO con = new SIBLContainerDTO();
                con.setCargoNo(c.getCargoNo() != null ? c.getCargoNo() : 1);
                con.setContainerNo(emptyIfNull(eqpId));
                con.setEquipmentSize(emptyIfNull(eqpSize));
                con.setTareWeight(c.getTareWeight() != null ? c.getTareWeight().intValue() : null);
                con.setCargoWeight(c.getCargoWeight() != null ? c.getCargoWeight().intValue() : 0);
                con.setGrossweight(c.getGrossWeight() != null ? c.getGrossWeight().intValue() : null);
                con.setWeightUnit(safeWeightUnit(c.getWeightUnit()));
                con.setNor(safeYesNoFlag(c.getNor()));
                con.setEquipmentStatus(safeEquipmentStatus(c.getEqpStatus()));
                con.setOogFlag(safeYesNoFlag(c.getOog()));
                con.setSocFlag(safeYesNoFlag(c.getShipperOwn()));
                con.setCarrierSeal(emptyIfNull(c.getSeal1No()));
                con.setShipperSeal(emptyIfNull(c.getSeal2No()));
                con.setRateEquipmentTypeId(emptyIfNull(c.getRateEqpTypeId()));
                con.setEquipmentType(EquipmentType.fromValue(c.getType()));
                // DG is at commodity level only; do not set dgFlag on container
                // Reefer details from bl_cargo
                boolean hasReeferData = (c.getSetTemp() != null || c.getTempMin() != null || c.getTempMax() != null
                        || (c.getHumidity() != null && !c.getHumidity().isEmpty())
                        || (c.getCo2Value() != null && !c.getCo2Value().isEmpty())
                        || (c.getO2Value() != null && !c.getO2Value().isEmpty())
                        || c.getVentSetting() != null
                        || (c.getVentPercentage() != null && !c.getVentPercentage().trim().isEmpty()));
                boolean isReefer = hasReeferData && !"Y".equalsIgnoreCase(c.getNor());
                con.setReefer(isReefer ? YesNoFlag.Y : YesNoFlag.N);
                if (isReefer) {
                    SIBLReeferDTO r = new SIBLReeferDTO();
                    r.setTempSet(c.getSetTemp() != null ? c.getSetTemp().doubleValue() : null);
                    r.setTempMin(c.getTempMin() != null ? c.getTempMin().doubleValue() : null);
                    r.setTempMax(c.getTempMax() != null ? c.getTempMax().doubleValue() : null);
                    r.setTempSetUnit(safeTemperatureUnit(c.getTempUnit()));
                    if (c.getHumidity() != null && !c.getHumidity().isEmpty()) {
                        Double v = parseDoubleSafe(c.getHumidity());
                        if (v != null) r.setHumidity(v);
                    }
                    r.setHumidityFlag(c.getHumidityFlag() != null && Boolean.TRUE.equals(c.getHumidityFlag()) ? YesNoFlag.Y : YesNoFlag.N);
                    if (c.getVentSetting() != null) {
                        r.setVentSet(c.getVentSetting());
                    }
                    String blVentPct = c.getVentPercentage();
                    if (blVentPct != null && !blVentPct.trim().isEmpty()) {
                        r.setVentilation(blVentPct.trim());
                    }
                    if (c.getCo2Value() != null && !c.getCo2Value().isEmpty()) {
                        Double v = parseDoubleSafe(c.getCo2Value());
                        if (v != null) r.setCo2Level(v);
                    }
                    if (c.getO2Value() != null && !c.getO2Value().isEmpty()) {
                        Double v = parseDoubleSafe(c.getO2Value());
                        if (v != null) r.setO2Level(v);
                    }
                    r.setDrains(emptyIfNull(c.getDrains()));
                    con.setReeferSettings(r);
                }
                // DG is at commodity level only; container dgFlag from bl_cargo is not set on container DTO
                containers.add(con);
            }
            if (eqpId == null) continue;
            List<BlCommodity> blCommodityList = blCommodityRepository.findByBlNoAndEqpId(bl.getBlNo(), eqpId);
            for (BlCommodity cm : blCommodityList) {
                String ck = (cm.getCommodityId() != null ? cm.getCommodityId() : "") + "|" + (cm.getMarksDesc() != null ? cm.getMarksDesc() : "");
                if (!ck.equals("|") && !commodityKeys.contains(ck)) {
                    commodityKeys.add(ck);
                    SIBLCommodityDTO com = new SIBLCommodityDTO();
                    com.setCargoNo(c.getCargoNo() != null ? c.getCargoNo() : 1);
                    com.setContainerNo(emptyIfNull(eqpId));
                    com.setItemNo(cm.getItemNo() != null ? cm.getItemNo() : 1);
                    com.setCommodityGroup(emptyIfNull(cm.getCommodityGroup()));
                    com.setPackageType(emptyIfNull(cm.getPackageId()));
                    com.setPackageQty(cm.getItemQty() != null ? cm.getItemQty() : 0);
                    com.setItemWeight(cm.getItemWeight() != null ? cm.getItemWeight().intValue() : null);
                    com.setItemWeightUnit(safeWeightUnit(cm.getItemWeightUnit()));
                    com.setNettwt(cm.getNetWeight() != null ? cm.getNetWeight().intValue() : null);
                    com.setItemVolume(cm.getItemVolume());
                    com.setItemVolumeUnit(safeVolumeUnit(cm.getItemVolumeUnit()));
                    com.setHsCode(emptyIfNull(cm.getCommodityId()));
                    com.setMarksNo(emptyIfNull(cm.getMarksNumbers()));
                    com.setMarksDesc(emptyIfNull(cm.getMarksDesc()));
                    com.setHazardous(safeYesNoFlag(cm.getHazardous()));
                    com.setBuyerCode(emptyIfNull(cm.getByCode()));
                    com.setSellerCode(emptyIfNull(cm.getSlCode()));
                    com.setBuyerName(emptyIfNull(cm.getByName()));
                    com.setSellerName(emptyIfNull(cm.getSlName()));
                    com.setCommodityHBLNo(emptyIfNull(cm.getCmdtyHblNo()));
                    // OOG details from bl_commodity (itemLeft, itemRight, itemForward, itemAft, itemTop, itemLengthUnit)
                    if (cm.getItemLeft() != null || cm.getItemRight() != null || cm.getItemForward() != null
                            || cm.getItemAft() != null || cm.getItemTop() != null) {
                        SIBLOogDTO oogDto = new SIBLOogDTO();
                        oogDto.setLeftFt(cm.getItemLeft() != null ? cm.getItemLeft().doubleValue() : null);
                        oogDto.setRightFt(cm.getItemRight() != null ? cm.getItemRight().doubleValue() : null);
                        oogDto.setForward(cm.getItemForward() != null ? cm.getItemForward().doubleValue() : null);
                        oogDto.setAft(cm.getItemAft() != null ? cm.getItemAft().doubleValue() : null);
                        oogDto.setTopFt(cm.getItemTop() != null ? cm.getItemTop().doubleValue() : null);
                        oogDto.setUnit(safeLengthUnit(cm.getItemLengthUnit()));
                        com.setOog(oogDto);
                    }
                    // DG details from bl_commodity when hazardous=Y (imoContact, imoPhone)
                    if ("Y".equalsIgnoreCase(cm.getHazardous())) {
                        SIBLDangerousGoodsDTO dgDto = new SIBLDangerousGoodsDTO();
                        dgDto.setImoContact(emptyIfNull(cm.getImoContact()));
                        dgDto.setImoPhone(emptyIfNull(cm.getImoPhone()));
                        com.setDangerousGoods(dgDto);
                    }
                    commodities.add(com);
                }
            }
        }
        dto.setContainers(containers);
        dto.setCommodities(commodities);
        // When data from BL: set container oogFlag=Y when any commodity (same cargoNo) has OOG from bl_commodity, so applyViewResponseRules keeps commodity OOG (same rule as booking).
        for (SIBLContainerDTO con : containers) {
            if (con == null || con.getCargoNo() == null) continue;
            boolean anyCommodityOog = false;
            for (SIBLCommodityDTO com : commodities) {
                if (com != null && con.getCargoNo().equals(com.getCargoNo()) && com.getOog() != null) {
                    anyCommodityOog = true;
                    break;
                }
            }
            if (anyCommodityOog) con.setOogFlag(YesNoFlag.Y);
        }
        if (blHeader != null) {
            dto.setLoadPortId(emptyIfNull(blHeader.getPol()) != null ? blHeader.getPol() : dto.getLoadPortId());
            dto.setDischargePortId(emptyIfNull(blHeader.getPod()) != null ? blHeader.getPod() : dto.getDischargePortId());
            dto.setShipperAddress(emptyIfNull(blHeader.getShipper()));
            dto.setShipperName(emptyIfNull(blHeader.getShipperName()));
            dto.setConsigeeAddress(emptyIfNull(blHeader.getConsignee()));
            dto.setConsigeeName(emptyIfNull(blHeader.getConsigneeName()));
            dto.setNotifyAddress(emptyIfNull(blHeader.getNotify()));
            dto.setNotifyName(emptyIfNull(blHeader.getNotifierName()));
        }
        return dto;
    }

    private List<SIBLCustomerDTO> blCustListToSIBL(List<BlCust> list) {
        if (list == null) return new ArrayList<>();
        List<SIBLCustomerDTO> out = new ArrayList<>();
        for (BlCust e : list) {
            SIBLCustomerDTO dto = new SIBLCustomerDTO();
            dto.setCustomerType(safeCustomerType(e.getCustTypeId()));
            dto.setCustomerCode(emptyIfNull(e.getCustId()));
            dto.setCountryId(emptyIfNull(e.getCountryId()));
            dto.setCustomerName(emptyIfNull(e.getName()));
            dto.setAddress(emptyIfNull(e.getAddress1()));
            dto.setAddress2(emptyIfNull(e.getAddress2()));
            dto.setCity(emptyIfNull(e.getCity()));
            dto.setStateId(emptyIfNull(e.getStateId()));
            dto.setTelephone(emptyIfNull(e.getTelephone()));
            dto.setEmail(emptyIfNull(e.getEmail()));
            out.add(dto);
        }
        return out;
    }

    /**
     * Find an SI by customer code and optional search criteria.
     * 
     * Logic (matching booking view pattern):
     * - If onlineSINo is provided -> search in ECOM database (ecomsi tables)
     * - If blNumber is provided (and onlineSINo is empty) -> search in Documentation database (bl table)
     * 
     * @param customerCode The customer code (from JWT)
     * @param onlineSINo Optional online SI number (eSINo from ECOM)
     * @param blNumber Optional BL number (from bl table in Documentation database)
     * @param status Optional SI status
     * @return Optional containing the complete SI DTO if found
     */
    public Optional<BookingRequestDTO> findSI(String customerCode, String onlineSINo,
                                             String blNumber, String status) {
        
        // Case 1: If onlineSINo is provided, search in ECOM database (ecomsi table)
        if (onlineSINo != null && !onlineSINo.trim().isEmpty()) {
            logger.debug("Searching ECOM database (ecomsi table) with onlineSINo: {}", onlineSINo);
            Optional<BookingRequestDTO> result = findFromEcomDatabase(customerCode, onlineSINo, status);
            if (result.isPresent()) {
                return result;
            }
        }
        
        // Case 2: If blNumber is provided (and onlineSINo not found or not provided), 
        // search in Documentation database (bl table)
        if (blNumber != null && !blNumber.trim().isEmpty()) {
            logger.debug("Searching Documentation database (bl table) with blNumber: {}", blNumber);
            return findFromBlTable(customerCode, blNumber, status);
        }
        
        // Case 3: No valid search criteria provided
        logger.debug("No valid search criteria provided for SI lookup");
        return Optional.empty();
    }

    /**
     * Find SI from ECOM database (ecomsi tables).
     */
    @Transactional(transactionManager = "ecomTransactionManager", readOnly = true)
    private Optional<BookingRequestDTO> findFromEcomDatabase(String customerCode, String onlineSINo, String status) {
        // Find the main SI entity
        Optional<EcomSi> siOpt = siRepo.findByeSINo(onlineSINo.trim());
        
        if (!siOpt.isPresent()) {
            logger.debug("SI not found in ecomsi table with onlineSINo: {}", onlineSINo);
            return Optional.empty();
        }

        EcomSi si = siOpt.get();
        
        // Log customer mismatch but still return the SI (view operation - less strict validation)
        if (!matchesCustomer(si, customerCode)) {
            logger.warn("SI {} belongs to customer {} but request is from customer {} - returning anyway for view", 
                    onlineSINo, si.getCustcode(), customerCode);
        }
        
        // Apply status filter if provided (remove any trailing dashes or special chars)
        if (status != null && !status.trim().isEmpty()) {
            String requestedStatus = status.trim().replaceAll("[-_\\s]+$", ""); // Remove trailing dashes, underscores, spaces
            String siStatus = safe(si.getStatus());
            if (!requestedStatus.equalsIgnoreCase(siStatus)) {
                logger.debug("Status mismatch: requested='{}', actual='{}' for SI {}", requestedStatus, siStatus, onlineSINo);
                return Optional.empty();
            }
        }

        String eSINo = si.geteSINo();

        // Build the complete DTO
        BookingRequestDTO dto = new BookingRequestDTO();
        
        // Map master SI fields
        mapMasterFields(si, dto);
        
        // Fetch and map related entities
        List<BookingRequestDTO.CustomerSummary> customers = fetchCustomers(eSINo);
        dto.setCustomers(customers);
        
        // Extract forwarder and warehouse from customers
        extractForwarderAndWarehouse(customers, dto);
        
        dto.setContainers(fetchContainers(eSINo));
        dto.setCommodities(fetchCommodities(eSINo));
        
        return Optional.of(dto);
    }

    /**
     * Find SI from Documentation database (bl table).
     * Uses JPA entities to fetch complete BL data from bl, bl_header, bl_cargo, and bl_commodity tables.
     */
    @Transactional(transactionManager = "documentationTransactionManager", readOnly = true)
    private Optional<BookingRequestDTO> findFromBlTable(String customerCode, String blNumber, String status) {
        try {
            String blNo = blNumber.trim();
            
            // 1. Fetch BL Master data using JPA
            Optional<Bl> blOpt = blRepository.findByBlNo(blNo);
            
            if (!blOpt.isPresent()) {
                logger.debug("BL not found in bl table with blNumber: {}", blNumber);
                return Optional.empty();
            }
            
            Bl bl = blOpt.get();
            
            // Apply status filter if provided (before fetching other data)
            if (status != null && !status.trim().isEmpty()) {
                String requestedStatus = status.trim().replaceAll("[-_\\s]+$", "");
                String blStatus = safe(bl.getStatus());
                if (!requestedStatus.equalsIgnoreCase(blStatus)) {
                    logger.debug("Status mismatch: requested='{}', actual='{}' for BL {}", requestedStatus, blStatus, blNumber);
                    return Optional.empty();
                }
            }
            
            // Build DTO from BL master
            BookingRequestDTO dto = new BookingRequestDTO();
            dto.setBlNumber(emptyIfNull(bl.getBlNo()));
            dto.setBookingNo(emptyIfNull(bl.getBookNo()));
            dto.setLoadPortId(emptyIfNull(bl.getLoadPortId()));
            dto.setDischargePortId(emptyIfNull(bl.getDischargePortId()));
            dto.setStatus(safeSiStatus(bl.getStatus()));
            
            // 2. Fetch BL Customer data (party information) using JPA - similar to booking service
            List<BlCust> blCustList = blCustRepository.findByBlNoOrderByCid(blNo);
            dto.setCustomers(fetchCustomersFromBlCust(blCustList));
            
            // Also fetch BL Header data (party information and ports) using JPA
            // Note: bl_header may have more complete party info, so we use it to supplement/override
            Optional<BlHeader> blHeaderOpt = blHeaderRepository.findByBlNo(blNo);
            if (blHeaderOpt.isPresent()) {
                BlHeader blHeader = blHeaderOpt.get();
                
                // Map party information from bl_header (override if not already set from bl_cust)
                // Shipper: name = shippername, address = shipper
                if (blHeader.getShipper() != null || blHeader.getShipperName() != null) {
                    BookingRequestDTO.Party shipper = new BookingRequestDTO.Party();
                    shipper.setName(emptyIfNull(blHeader.getShipperName()));
                    shipper.setAddress(emptyIfNull(blHeader.getShipper()));
                    dto.setShipper(shipper);
                }
                
                // Consignee: name = consigneename, address = consignee
                if (blHeader.getConsignee() != null || blHeader.getConsigneeName() != null) {
                    BookingRequestDTO.Party consignee = new BookingRequestDTO.Party();
                    consignee.setName(emptyIfNull(blHeader.getConsigneeName()));
                    consignee.setAddress(emptyIfNull(blHeader.getConsignee()));
                    dto.setConsignee(consignee);
                }
                
                // Notify: name = notifiername, address = notify
                if (blHeader.getNotify() != null || blHeader.getNotifierName() != null) {
                    BookingRequestDTO.Party notify = new BookingRequestDTO.Party();
                    notify.setName(emptyIfNull(blHeader.getNotifierName()));
                    notify.setAddress(emptyIfNull(blHeader.getNotify()));
                    dto.setNotifyParty(notify);
                }
                
                // Update ports from bl_header if available
                if (blHeader.getPol() != null) {
                    dto.setLoadPortId(emptyIfNull(blHeader.getPol()));
                }
                if (blHeader.getPod() != null) {
                    dto.setDischargePortId(emptyIfNull(blHeader.getPod()));
                }
            }
            
            // 3. Fetch Container and Commodity data using JPA
            List<BlCargo> blCargoList = blCargoRepository.findByBlNoOrderByCargoNo(blNo);
            List<BookingRequestDTO.ContainerSummary> containers = new ArrayList<>();
            List<BookingRequestDTO.CommoditySummary> commodities = new ArrayList<>();
            
            // Use LinkedHashSet to avoid duplicates while preserving order
            Set<String> containerKeys = new LinkedHashSet<>();
            Set<String> commodityKeys = new LinkedHashSet<>();
            
            for (BlCargo blCargo : blCargoList) {
                // Map container data (deduplicate by eqpId)
                String eqpId = blCargo.getEqpId();
                String eqpSize = blCargo.getEqpTypeId();
                
                if (eqpId != null && !containerKeys.contains(eqpId)) {
                    containerKeys.add(eqpId);
                    BookingRequestDTO.ContainerSummary container = new BookingRequestDTO.ContainerSummary();
                    container.setContainerNo(emptyIfNull(eqpId));
                    container.setEqpSize(emptyIfNull(eqpSize));
                    container.setTareWeight(blCargo.getTareWeight() != null ? String.valueOf(blCargo.getTareWeight()) : "");
                    container.setCargoWeight(blCargo.getCargoWeight() != null ? String.valueOf(blCargo.getCargoWeight()) : "");
                    container.setGrossweight(blCargo.getGrossWeight() != null ? String.valueOf(blCargo.getGrossWeight()) : "");
                    container.setWeightUnit(safeWeightUnit(blCargo.getWeightUnit()));
                    container.setRef_nor(safeYesNoFlag(blCargo.getNor()));
                    container.setEquipmentType(safeEquipmentType(blCargo.getType()));
                    container.setEquipmentTypestatus(safeEquipmentStatus(blCargo.getEqpStatus()));
                    container.setOogFlag(safeYesNoFlag(blCargo.getOog()));
                    container.setDangerousGoodsflag(safeYesNoFlag(blCargo.getDgFlag()));
                    containers.add(container);
                }
                
                // Fetch commodities for this container
                List<BlCommodity> blCommodityList = blCommodityRepository.findByBlNoAndEqpId(blNo, eqpId);
                for (BlCommodity blCommodity : blCommodityList) {
                    // Map commodity data (deduplicate by commodityId + marksDesc)
                    String commodityId = blCommodity.getCommodityId();
                    String marksDesc = blCommodity.getMarksDesc();
                    String commodityKey = (commodityId != null ? commodityId : "") + "|" + (marksDesc != null ? marksDesc : "");
                    
                    if (!commodityKey.equals("|") && !commodityKeys.contains(commodityKey)) {
                        commodityKeys.add(commodityKey);
                        BookingRequestDTO.CommoditySummary commodity = new BookingRequestDTO.CommoditySummary();
                        commodity.setHsCode(emptyIfNull(commodityId));
                        commodity.setDescription(emptyIfNull(marksDesc));
                        commodity.setPackageType(emptyIfNull(blCommodity.getPackageId()));
                        commodity.setPackageQty(blCommodity.getItemQty() != null ? String.valueOf(blCommodity.getItemQty()) : "");
                        commodity.setItemWeight(blCommodity.getItemWeight() != null ? String.valueOf(blCommodity.getItemWeight()) : "");
                        commodity.setItemWeightUnit(safeWeightUnit(blCommodity.getItemWeightUnit()));
                        commodity.setItemVolume(blCommodity.getItemVolume() != null ? String.valueOf(blCommodity.getItemVolume()) : "");
                        commodity.setVolumnUnit(safeVolumeUnit(blCommodity.getItemVolumeUnit()));
                        // Note: BlCommodity doesn't have buyer/seller fields, so these will be empty
                        commodity.setBuyerCode("");
                        commodity.setSellerCode("");
                        commodity.setBuyerName("");
                        commodity.setSellerName("");
                        commodities.add(commodity);
                    }
                }
            }
            
            dto.setContainers(containers);
            dto.setCommodities(commodities);
            
            logger.debug("Successfully retrieved BL {} from bl table with {} containers and {} commodities", 
                    blNumber, containers.size(), commodities.size());
            return Optional.of(dto);
            
        } catch (Exception e) {
            logger.error("Error querying bl table: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Check if SI belongs to the customer
     */
    private boolean matchesCustomer(EcomSi si, String customerCode) {
        return customerCode != null && customerCode.equalsIgnoreCase(safe(si.getCustcode()));
    }

    /**
     * Map master SI fields to DTO
     * Shipper, Consignee, Notify are taken from ecomSI master table
     */
    private void mapMasterFields(EcomSi entity, BookingRequestDTO dto) {
        // Map SIBLMasterVO fields in specified order
        dto.setSiNo(emptyIfNull(entity.geteSINo()));
        dto.setBlNo(emptyIfNull(entity.getBlno()));
        dto.setBookNo(emptyIfNull(entity.getBookno()));
        dto.setVessel(emptyIfNull(entity.getVessel()));
        dto.setVoyage(emptyIfNull(entity.getVoyage()));
        dto.setBound(safeBound(entity.getBound()));
        dto.setPla(emptyIfNull(entity.getPla()));
        dto.setPol(emptyIfNull(entity.getPol()));
        dto.setPod(emptyIfNull(entity.getPod()));
        dto.setPld(emptyIfNull(entity.getPld()));
        // plaDesc and pldDesc - not stored in ecomSI, leave empty or lookup from port master
        dto.setPlaDesc(""); // TODO: Lookup from port master if needed
        dto.setPldDesc(""); // TODO: Lookup from port master if needed
        dto.setPlaHeaderName(emptyIfNull(entity.getPlaHeaderName()));
        dto.setPolHeaderName(emptyIfNull(entity.getPolHeaderName()));
        dto.setPodHeaderName(emptyIfNull(entity.getPodHeaderName()));
        dto.setPldHeaderName(emptyIfNull(entity.getPldHeaderName()));
        dto.setCustCode(emptyIfNull(entity.getCustcode()));
        dto.setCrUser(emptyIfNull(entity.getCrUser()));
        dto.setCrDate(entity.getCrDate() != null ? entity.getCrDate().format(DateTimeFormatter.ofPattern(DateFormatConstants.UTC_DATE_TIME)) : "");
        dto.setChgUser(emptyIfNull(entity.getChgUser()));
        dto.setChgDate(entity.getChgDate() != null ? entity.getChgDate().format(DateTimeFormatter.ofPattern(DateFormatConstants.UTC_DATE_TIME)) : "");
        dto.setEditBL(entity.getEditbl() != null ? String.valueOf(entity.getEditbl()) : "");
        dto.setBlVersion(entity.getBlversion() != null ? String.valueOf(entity.getBlversion()) : "");
        dto.setStatus(safeSiStatus(entity.getStatus()));
        dto.setCustRemarks(emptyIfNull(entity.getCustremarks()));
        
        // Legacy fields for backward compatibility
        dto.setOnlineSINo(emptyIfNull(entity.geteSINo()));
        dto.setBlNumber(emptyIfNull(entity.getBlno()));
        dto.setBookingNo(emptyIfNull(entity.getBookno()));
        dto.setLoadPortId(emptyIfNull(entity.getPol()));
        dto.setDischargePortId(emptyIfNull(entity.getPod()));
        
        // Map party information from ecomSI master table
        // Shipper: name = shippername, address = shipper
        if (entity.getShipper() != null || entity.getShippername() != null) {
            BookingRequestDTO.Party shipper = new BookingRequestDTO.Party();
            shipper.setName(emptyIfNull(entity.getShippername()));
            shipper.setAddress(emptyIfNull(entity.getShipper()));
            dto.setShipper(shipper);
        }
        
        // Consignee: name = consigeename, address = consigee
        if (entity.getConsigee() != null || entity.getConsigeename() != null) {
            BookingRequestDTO.Party consignee = new BookingRequestDTO.Party();
            consignee.setName(emptyIfNull(entity.getConsigeename()));
            consignee.setAddress(emptyIfNull(entity.getConsigee()));
            dto.setConsignee(consignee);
        }
        
        // Notify: name = notifyname, address = notify
        if (entity.getNotify() != null || entity.getNotifyname() != null) {
            BookingRequestDTO.Party notify = new BookingRequestDTO.Party();
            notify.setName(emptyIfNull(entity.getNotifyname()));
            notify.setAddress(emptyIfNull(entity.getNotify()));
            dto.setNotifyParty(notify);
        }
    }
    
    /**
     * Fetch customers for an SI from ECOM database (similar to BookingViewService)
     */
    private List<BookingRequestDTO.CustomerSummary> fetchCustomers(String eSINo) {
        List<EcomSiCustomer> entities = customerRepo.findByeSINo(eSINo);
        List<BookingRequestDTO.CustomerSummary> dtos = new ArrayList<>();
        
        for (EcomSiCustomer entity : entities) {
            BookingRequestDTO.CustomerSummary dto = new BookingRequestDTO.CustomerSummary();
            dto.setCustomerType(safeCustomerType(entity.getCustTypeId()));
            dto.setCustomerCode(emptyIfNull(entity.getCustId()));
            dto.setCountryId(emptyIfNull(entity.getCountryId()));
            dto.setCustomerName(emptyIfNull(entity.getName()));
            dto.setAddress(emptyIfNull(entity.getAddress1()));
            dto.setAddress1(emptyIfNull(entity.getAddress2()));
            dto.setCity(emptyIfNull(entity.getCity()));
            dto.setZipCode(emptyIfNull(entity.getZipCode()));
            dto.setTelephone(emptyIfNull(entity.getTelephone()));
            dto.setEmail(emptyIfNull(entity.getEmail()));
            dtos.add(dto);
        }
        
        return dtos;
    }
    
    /**
     * Extract forwarder and warehouse from customers list
     */
    private void extractForwarderAndWarehouse(List<BookingRequestDTO.CustomerSummary> customers, BookingRequestDTO dto) {
        if (customers == null) return;
        
        for (BookingRequestDTO.CustomerSummary customer : customers) {
            CustomerType custType = customer.getCustomerType();
            if (custType != null) {
                String custTypeValue = custType.getValue();
                // Forwarder typically has custTypeId like "FW" or "FR"
                if (custType == CustomerType.FW || custTypeValue.equalsIgnoreCase("FR") || 
                    custTypeValue.equalsIgnoreCase("FORWARDER")) {
                    dto.setForwarderName(customer.getCustomerName());
                    dto.setForwarderHeader(customer.getCustomerName() + 
                        (customer.getAddress() != null && !customer.getAddress().isEmpty() ? 
                         ", " + customer.getAddress() : ""));
                }
                // Warehouse typically has custTypeId like "WH" or "WAREHOUSE"
                else if (custTypeValue.equalsIgnoreCase("WH") || custTypeValue.equalsIgnoreCase("WAREHOUSE")) {
                    dto.setWarehouseName(customer.getCustomerName());
                    dto.setWarehouseHeader(customer.getCustomerName() + 
                        (customer.getAddress() != null && !customer.getAddress().isEmpty() ? 
                         ", " + customer.getAddress() : ""));
                }
            }
        }
    }

    /**
     * Fetch customers for a BL from Documentation database (similar to BookingViewService.fetchCustomers)
     */
    private List<BookingRequestDTO.CustomerSummary> fetchCustomersFromBlCust(List<BlCust> blCustList) {
        List<BookingRequestDTO.CustomerSummary> dtos = new ArrayList<>();
        
        for (BlCust entity : blCustList) {
            BookingRequestDTO.CustomerSummary dto = new BookingRequestDTO.CustomerSummary();
            dto.setCustomerType(safeCustomerType(entity.getCustTypeId()));
            dto.setCustomerCode(emptyIfNull(entity.getCustId()));
            dto.setCountryId(emptyIfNull(entity.getCountryId()));
            dto.setCustomerName(emptyIfNull(entity.getName()));
            dto.setAddress(emptyIfNull(entity.getAddress1()));
            dto.setAddress1(emptyIfNull(entity.getAddress2()));
            dto.setCity(emptyIfNull(entity.getCity()));
            dto.setZipCode(emptyIfNull(entity.getZipCode()));
            dto.setTelephone(emptyIfNull(entity.getTelephone()));
            dto.setEmail(emptyIfNull(entity.getEmail()));
            dtos.add(dto);
        }
        
        return dtos;
    }

    /**
     * Fetch containers for an SI
     */
    private List<BookingRequestDTO.ContainerSummary> fetchContainers(String eSINo) {
        List<EcomSiContainer> entities = containerRepo.findByeSINo(eSINo);
        List<BookingRequestDTO.ContainerSummary> dtos = new ArrayList<>();
        
        // Fetch all reefer and dangerous goods data for this SI
        List<EcomSiReefer> reefers = reeferRepo.findByeSINo(eSINo);
        List<EcomSiDg> dangerousGoods = dgRepo.findByeSINo(eSINo);
        
        for (EcomSiContainer entity : entities) {
            BookingRequestDTO.ContainerSummary dto = new BookingRequestDTO.ContainerSummary();
            dto.setCargoNo(entity.getConId());
            dto.setContainerNo(emptyIfNull(entity.getContainerno()));
            dto.setEqpSize(emptyIfNull(entity.getEqpSize()));
            dto.setEquipmentQty(1); // Default to 1, can be calculated if needed
            dto.setTareWeight(entity.getTareWeight() != null ? String.valueOf(entity.getTareWeight()) : "");
            dto.setCargoWeight(entity.getCargoWeight() != null ? String.valueOf(entity.getCargoWeight()) : "");
            dto.setGrossweight(entity.getGrossweight() != null ? String.valueOf(entity.getGrossweight()) : "");
            dto.setWeightUnit(safeWeightUnit(entity.getWeightUnit()));
            dto.setRef_nor(safeYesNoFlag(entity.getNor()));
            dto.setNor(safeYesNoFlag(entity.getNor())); // Set nor field separately
            dto.setSocFlag(safeYesNoFlag(entity.getShipperOwn()));
            dto.setCarrierSeal(emptyIfNull(entity.getCarrierSeal()));
            dto.setShipperSeal(emptyIfNull(entity.getShipperSeal()));
            dto.setEquipmentType(safeEquipmentType(entity.getEqpType()));
            dto.setEquipmentTypestatus(safeEquipmentStatus(entity.getEquipmentStatus()));
            dto.setOogFlag(safeYesNoFlag(entity.getOog()));
            dto.setDangerousGoodsflag(safeYesNoFlag(entity.getDgFlag()));
            
            // Add reefer data if condition met: refer = "Y" AND nor != "Y"
            // If there's reefer data in ecomSI_reefer, it means refer = "Y"
            // Check if container's ref_nor is not "Y" (meaning it's not a NOR)
            // The condition: refer = "Y" (reefer data exists) AND nor != "Y" (not a Non-Operating Reefer)
            EcomSiReefer reefer = findReeferForContainer(reefers, entity.getConId(), entity.getContainerno());
            if (reefer != null) {
                // Check if nor != "Y" - the container's ref_nor field
                // If ref_nor = "Y", it might indicate it's a NOR, so we exclude it
                // If ref_nor is null or not "Y", and reefer data exists, include it
                String refNor = entity.getNor(); // This is the ref_nor field
                // Condition: refer = "Y" (reefer exists) AND nor != "Y" (ref_nor is not "Y")
                if (refNor == null || !"Y".equalsIgnoreCase(refNor)) {
                    BookingRequestDTO.ReeferSummary reeferSummary = mapReefer(reefer, entity.getConId());
                    dto.setReefer(reeferSummary);
                }
            }
            
            // Add dangerous goods data if condition met: hazardous = "y"
            // Find dangerous goods for this container
            // Note: The hazardous flag is at commodity level, but DG data is shown at container level
            // So we include DG data if it exists for this container (presence implies hazardous)
            EcomSiDg dg = findDangerousGoodsForContainer(dangerousGoods, entity.getConId(), entity.getContainerno());
            if (dg != null) {
                // Verify that there's at least one commodity with hazardous = "y" for this container
                // This check ensures the condition "hazardous = 'y'" is met
                boolean hasHazardousCommodity = checkIfContainerHasHazardousCommodity(eSINo, entity.getConId(), entity.getContainerno());
                if (hasHazardousCommodity) {
                    BookingRequestDTO.DangerousGoodsSummary dgSummary = mapDangerousGoods(dg, entity.getConId());
                    dto.setDangerousGoods(dgSummary);
                }
            }
            
            dtos.add(dto);
        }
        
        return dtos;
    }
    
    /**
     * Find reefer data for a specific container
     */
    private EcomSiReefer findReeferForContainer(List<EcomSiReefer> reefers, Integer conId, String containerNo) {
        if (reefers == null || conId == null) return null;
        for (EcomSiReefer reefer : reefers) {
            if (conId.equals(reefer.getConId()) && 
                (containerNo == null || containerNo.equals(reefer.getContainerno()))) {
                return reefer;
            }
        }
        return null;
    }
    
    /**
     * Find dangerous goods data for a specific container
     */
    private EcomSiDg findDangerousGoodsForContainer(List<EcomSiDg> dangerousGoods, Integer conId, String containerNo) {
        if (dangerousGoods == null || conId == null) return null;
        for (EcomSiDg dg : dangerousGoods) {
            if (conId.equals(dg.getConId()) && 
                (containerNo == null || containerNo.equals(dg.getContainerno()))) {
                return dg;
            }
        }
        return null;
    }
    
    /**
     * Map reefer entity to DTO
     */
    private BookingRequestDTO.ReeferSummary mapReefer(EcomSiReefer entity, Integer cargoNo) {
        BookingRequestDTO.ReeferSummary dto = new BookingRequestDTO.ReeferSummary();
        dto.setCargoNo(cargoNo);
        dto.setContainerNo(emptyIfNull(entity.getContainerno()));
        dto.setItemNo(1); // Default to 1, as reefer is at container level
        if (entity.getRefSetTemp() != null) {
            dto.setSetTemp(entity.getRefSetTemp().doubleValue());
        }
        if (entity.getRefMinTemp() != null) {
            dto.setMinTemp(entity.getRefMinTemp().doubleValue());
        }
        if (entity.getRefMaxTemp() != null) {
            dto.setMaxTemp(entity.getRefMaxTemp().doubleValue());
        }
        dto.setTempUnit(safeTemperatureUnit(entity.getRefTempUnit()));
        return dto;
    }
    
    /**
     * Check if container has any commodity with hazardous = "y"
     */
    private boolean checkIfContainerHasHazardousCommodity(String eSINo, Integer conId, String containerNo) {
        List<EcomSiCommodity> commodities = commodityRepo.findByeSINo(eSINo);
        for (EcomSiCommodity commodity : commodities) {
            if (conId.equals(commodity.getConId()) && 
                (containerNo == null || containerNo.equals(commodity.getContainerno()))) {
                String hazardous = commodity.getHazardous();
                if ("y".equalsIgnoreCase(hazardous) || "Y".equalsIgnoreCase(hazardous)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Map dangerous goods entity to DTO
     */
    private BookingRequestDTO.DangerousGoodsSummary mapDangerousGoods(EcomSiDg entity, Integer cargoNo) {
        BookingRequestDTO.DangerousGoodsSummary dto = new BookingRequestDTO.DangerousGoodsSummary();
        dto.setCargoNo(cargoNo);
        dto.setContainerNo(emptyIfNull(entity.getContainerno()));
        dto.setItemNo(entity.getItemno());
        dto.setMarinePollutant(safeYesNoFlag(entity.getMarinePollutant()));
        dto.setUnNo(emptyIfNull(entity.getUnNo()));
        dto.setFlashPt(entity.getFlashPt());
        dto.setFlashPtUnit(safeFlashPtUnit(entity.getFlashPtUnit()));
        dto.setClassCode(emptyIfNull(entity.getImdgClass()));
        return dto;
    }

    /**
     * Fetch commodities for an SI
     */
    private List<BookingRequestDTO.CommoditySummary> fetchCommodities(String eSINo) {
        List<EcomSiCommodity> entities = commodityRepo.findByeSINo(eSINo);
        List<BookingRequestDTO.CommoditySummary> dtos = new ArrayList<>();
        
        // Fetch all OOG and dangerous goods data for this SI
        List<EcomSiOog> oogs = oogRepo.findByeSINo(eSINo);
        List<EcomSiDg> dangerousGoods = dgRepo.findByeSINo(eSINo);
        
        // Also need to check container OOG flag to determine if OOG should be shown
        List<EcomSiContainer> containers = containerRepo.findByeSINo(eSINo);
        
        for (EcomSiCommodity entity : entities) {
            BookingRequestDTO.CommoditySummary dto = new BookingRequestDTO.CommoditySummary();
            dto.setCargoNo(entity.getConId());
            dto.setContainerNo(emptyIfNull(entity.getContainerno()));
            dto.setItemNo(entity.getItemno());
            dto.setCommodityGroup(emptyIfNull(entity.getCommodity())); // Using commodity code as group
            dto.setHsCode(emptyIfNull(entity.getCommodity())); // HS Code is the commodity field
            dto.setDescription(emptyIfNull(entity.getHsCommodity()));
            dto.setPackageType(emptyIfNull(entity.getPackageType()));
            dto.setPackageQty(entity.getPackageQty() != null ? String.valueOf(entity.getPackageQty()) : "");
            dto.setItemWeight(entity.getItemWeight() != null ? String.valueOf(entity.getItemWeight()) : "");
            dto.setItemWeightUnit(safeWeightUnit(entity.getItemWeightUnit()));
            dto.setItemVolume(entity.getItemVolume() != null ? String.valueOf(entity.getItemVolume()) : "");
            dto.setVolumnUnit(safeVolumeUnit(entity.getItemVolumeUnit()));
            dto.setNettwt(entity.getNettwt() != null ? String.valueOf(entity.getNettwt()) : "");
            dto.setCommodityHBLNo(emptyIfNull(entity.getCmdtyHblno()));
            dto.setBuyerCode(emptyIfNull(entity.getBycode()));
            dto.setSellerCode(emptyIfNull(entity.getSlcode()));
            dto.setBuyerName(emptyIfNull(entity.getByname()));
            dto.setSellerName(emptyIfNull(entity.getSlname()));
            
            // Add OOG data if condition met: OOG = "Y"
            // Find the container for this commodity to check OOG flag
            EcomSiContainer container = findContainerForCommodity(containers, entity.getConId(), entity.getContainerno());
            if (container != null && "Y".equalsIgnoreCase(container.getOog())) {
                // Find OOG data for this commodity item
                EcomSiOog oog = findOogForCommodity(oogs, entity.getConId(), entity.getContainerno(), entity.getItemno());
                if (oog != null) {
                    BookingRequestDTO.OogSummary oogSummary = mapOog(oog, entity.getConId());
                    dto.setOog(oogSummary);
                }
            }
            
            // Add dangerous goods data if condition met: hazardous = "y"
            if ("y".equalsIgnoreCase(entity.getHazardous()) || "Y".equalsIgnoreCase(entity.getHazardous())) {
                // Find all dangerous goods for this commodity item
                List<EcomSiDg> dgList = findDangerousGoodsForCommodity(dangerousGoods, entity.getConId(), entity.getContainerno(), entity.getItemno());
                if (dgList != null && !dgList.isEmpty()) {
                    List<BookingRequestDTO.DangerousGoodsSummary> dgSummaries = new ArrayList<>();
                    for (EcomSiDg dg : dgList) {
                        BookingRequestDTO.DangerousGoodsSummary dgSummary = mapDangerousGoods(dg, entity.getConId());
                        dgSummaries.add(dgSummary);
                    }
                    dto.setDangerousGoods(dgSummaries);
                }
            }
            
            dtos.add(dto);
        }
        
        return dtos;
    }
    
    /**
     * Find container for a commodity
     */
    private EcomSiContainer findContainerForCommodity(List<EcomSiContainer> containers, Integer conId, String containerNo) {
        if (containers == null || conId == null) return null;
        for (EcomSiContainer container : containers) {
            if (conId.equals(container.getConId()) && 
                (containerNo == null || containerNo.equals(container.getContainerno()))) {
                return container;
            }
        }
        return null;
    }
    
    /**
     * Find OOG data for a specific commodity item
     */
    private EcomSiOog findOogForCommodity(List<EcomSiOog> oogs, Integer conId, String containerNo, Integer itemNo) {
        if (oogs == null || conId == null || itemNo == null) return null;
        for (EcomSiOog oog : oogs) {
            if (conId.equals(oog.getConId()) && 
                itemNo.equals(oog.getComItemNo()) &&
                (containerNo == null || containerNo.equals(oog.getContainerno()))) {
                return oog;
            }
        }
        return null;
    }
    
    /**
     * Find dangerous goods data for a specific commodity item
     */
    private List<EcomSiDg> findDangerousGoodsForCommodity(List<EcomSiDg> dangerousGoods, Integer conId, String containerNo, Integer itemNo) {
        if (dangerousGoods == null || conId == null || itemNo == null) return null;
        List<EcomSiDg> result = new ArrayList<>();
        for (EcomSiDg dg : dangerousGoods) {
            if (conId.equals(dg.getConId()) && 
                itemNo.equals(dg.getItemno()) &&
                (containerNo == null || containerNo.equals(dg.getContainerno()))) {
                result.add(dg);
            }
        }
        return result.isEmpty() ? null : result;
    }
    
    /**
     * Map OOG entity to DTO
     */
    private BookingRequestDTO.OogSummary mapOog(EcomSiOog entity, Integer cargoNo) {
        BookingRequestDTO.OogSummary dto = new BookingRequestDTO.OogSummary();
        dto.setCargoNo(cargoNo);
        dto.setContainerNo(emptyIfNull(entity.getContainerno()));
        dto.setItemNo(entity.getComItemNo());
        dto.setItemLeft(entity.getLeftft());
        dto.setItemRight(entity.getRightft());
        dto.setItemForward(entity.getForward());
        dto.setItemAft(entity.getAlt()); // Alt is the aft dimension
        dto.setItemTop(entity.getTopft());
        dto.setItemLengthUnit(safeLengthUnit(entity.getUnit()));
        return dto;
    }

    /**
     * Safe string helper
     */
    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    /**
     * Convert null to empty string for response fields
     */
    private String emptyIfNull(String s) {
        return s == null ? "" : s;
    }

    private static Bound safeBound(String v) {
        try { return Bound.fromValue(v); } catch (Exception e) { return null; }
    }
    private static SiStatus safeSiStatus(String v) {
        try { return SiStatus.fromValue(v); } catch (Exception e) { return null; }
    }
    private static CustomerType safeCustomerType(String v) {
        try { return CustomerType.fromValue(v); } catch (Exception e) { return null; }
    }
    private static YesNoFlag safeYesNoFlag(String v) {
        try { return YesNoFlag.fromValue(v); } catch (Exception e) { return null; }
    }
    private static WeightUnit safeWeightUnit(String v) {
        try { return WeightUnit.fromValue(v); } catch (Exception e) { return null; }
    }
    private static EquipmentStatus safeEquipmentStatus(String v) {
        try { return EquipmentStatus.fromValue(v); } catch (Exception e) { return null; }
    }
    private static EquipmentType safeEquipmentType(String v) {
        try { return EquipmentType.fromValue(v); } catch (Exception e) { return null; }
    }
    private static VolumeUnit safeVolumeUnit(String v) {
        if (v == null || v.trim().isEmpty()) return null;
        try { return VolumeUnit.fromValue(v); } catch (Exception e) { return null; }
    }
    private static EnsType safeEnsType(String v) {
        if (v == null || v.trim().isEmpty()) return null;
        try { return EnsType.fromValue(v); } catch (Exception e) { return null; }
    }
    private static EnsFillingType safeEnsFillingType(String v) {
        if (v == null || v.trim().isEmpty()) return null;
        try { return EnsFillingType.fromValue(v.trim()); } catch (Exception e) { return null; }
    }
    private static FreightPaymentType safeFreightPaymentType(String v) {
        if (v == null || v.trim().isEmpty()) return null;
        try { return FreightPaymentType.fromValue(v); } catch (Exception e) { return null; }
    }
    private static TemperatureUnit safeFlashPtUnit(String v) {
        if (v == null || v.trim().isEmpty()) return null;
        try { return TemperatureUnit.fromValue(v); } catch (Exception e) { return null; }
    }
    private static TemperatureUnit safeTemperatureUnit(String v) {
        if (v == null || v.trim().isEmpty()) return null;
        try { return TemperatureUnit.fromValue(v); } catch (Exception e) { return null; }
    }
    private static LengthUnit safeLengthUnit(String v) {
        if (v == null || v.trim().isEmpty()) return null;
        try { return LengthUnit.fromValue(v); } catch (Exception e) { return null; }
    }
} 
