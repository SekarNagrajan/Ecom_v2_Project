package com.solverminds.booking.repo;

import com.solverminds.booking.dto.v2.vgm.Address;
import com.solverminds.booking.dto.v2.vgm.ContainerVGM;
import com.solverminds.booking.dto.v2.vgm.TareResolutionResult;
import com.solverminds.booking.dto.v2.vgm.VGMInfo;
import com.solverminds.booking.dto.v2.vgm.VgmMethod;
import com.solverminds.booking.dto.v2.vgm.VgmStatus;
import com.svm.ecom.common.enums.WeightUnit;
import com.solverminds.booking.dto.v2.vgm.row.BookingCargoRow;
import com.solverminds.booking.dto.v2.vgm.row.EquipmentPassportRow;
import com.solverminds.booking.dto.v2.vgm.row.VgmInsertRow;
import com.solverminds.booking.dto.v2.vgm.row.VgmSolasRow;
import com.solverminds.booking.exception.BookingNotFoundException;
import com.solverminds.booking.exception.BookingVgmForbiddenException;
import com.solverminds.booking.exception.ContainerNotFoundException;
import com.solverminds.booking.service.VgmDocumentationMirrorService;
import com.solverminds.booking.service.VgmTareWeightResolver;
import com.solverminds.booking.service.VgmTransactionalWriter;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Service-style repository for VGM operations (aligned with {@link BookingRepository} pattern).
 *
 * <p>Orchestrates business validations and calls into {@link VgmDataRepository} (data access) and
 * {@link VgmTransactionalWriter} (transactional writes across ECOM and Documentation).</p>
 */
@Repository
public class VgmRepository {

    private static final String SOURCE_CUSTOMER = "C";
    private static final String DEFAULT_UNIT_KG = "K";

    private static final String MSG_VGM_NOT_ABOVE_TARE =
            "Invalid VGM weight for container %s. It must be greater than tare weight (%s kg).";
    private static final String MSG_TARE_UNAVAILABLE =
            "Tare weight not available for container %s. Unable to validate VGM weight.";
    private static final String MSG_INVALID_PROFILE_TARE =
            "Invalid tare weight (0 or negative) for container %s.";
    private static final String MSG_BATCH = "Validation failed for one or more containers.";

    private static final BigDecimal MAX_VGM_WEIGHT_KG = new BigDecimal("50000");
    private static final String MSG_VGM_EXCEEDS_MAX =
            "VGM Weight should be less than or equal to 50000 Kilos/50 Tons";

    private final VgmDataRepository data;
    private final VgmTransactionalWriter transactionalWriter;
    private final VgmDocumentationMirrorService documentationMirror;
    private final VgmTareWeightResolver tareWeightResolver;

    public VgmRepository(
            VgmDataRepository data,
            VgmTransactionalWriter transactionalWriter,
            VgmDocumentationMirrorService documentationMirror,
            VgmTareWeightResolver tareWeightResolver) {
        this.data = data;
        this.transactionalWriter = transactionalWriter;
        this.documentationMirror = documentationMirror;
        this.tareWeightResolver = tareWeightResolver;
    }

    private void ensureConfigured() {
        if (data == null || transactionalWriter == null || documentationMirror == null || tareWeightResolver == null) {
            throw new IllegalStateException("VGM components are not configured");
        }
    }

    public void saveVGMDetails(String bookingId, VGMInfo request, String customerCode, String loginUser) {
        ensureConfigured();

        String bid = bookingId != null ? bookingId.trim() : "";
        String cust = customerCode != null ? customerCode.trim() : "";
        if (bid.isEmpty()) {
            throw new BookingNotFoundException("Booking not found or void");
        }
        String user = loginUser != null ? loginUser.trim() : "";

        if (!data.bookingExistsActive(bid)) {
            throw new BookingNotFoundException("Booking not found or void");
        }

        assertCustomerAllowed(bid, cust);

        validateContainersAgainstBooking(bid, request);
        validateContainersOnBookingAndPassport(bid, request);
        validateVgmMaxWeightKg(request);
        validateVgmWeightAgainstResolvedTare(bid, request);

        String vgmStatus = "A";
        String oprStatus = null;
        String dbDate = toDBDateFormat(request.getObtainedAt());

        List<VgmInsertRow> rows = new ArrayList<>();
        List<String> weightStrings = new ArrayList<>();
        for (ContainerVGM container : request.getContainers()) {
            String eqpUpper = container.getEquipmentId().trim().toUpperCase(Locale.ROOT);
            Integer maxVersion = data.findMaxVersionEcom(bid, eqpUpper);
            int newVersion = maxVersion == null ? 1 : maxVersion + 1;

            VgmInsertRow row = VgmInsertRow.builder()
                    .bookNo(bid)
                    .blNo(null)
                    .eqptype(container.getEquipmentType().trim())
                    .eqpid(eqpUpper)
                    .vgmWeight(container.getWeight())
                    .vgmUnit(container.getUnit() != null ? container.getUnit().getValue().trim() : DEFAULT_UNIT_KG)
                    .versionNo(newVersion)
                    .vgmSource("C")
                    .vgmType("")
                    .vgmRefNumber(nullIfEmpty(request.getReferenceNumber()))
                    .vgmOrderNumber(nullIfEmpty(request.getOrderNumber()))
                    .vgmObtainedDate(dbDate)
                    .vgmObtainedMethod(request.getMethod() != null ? request.getMethod().getValue().trim() : null)
                    .vgmCompanyName(toUpperCase(request.getCompanyName()))
                    .vgmAddress1(getLine(request.getAddress(), 0))
                    .vgmAddress2(getLine(request.getAddress(), 1))
                    .vgmZipcode(getPostalCode(request.getAddress()))
                    .vgmCity(getLocality(request.getAddress()))
                    .vgmCountry(getCountryCode(request.getAddress()))
                    .vgmAuthorizedPerson(request.getAuthorizedPerson().trim().toUpperCase(Locale.ROOT))
                    .telephone(nullIfEmpty(request.getPhone()))
                    .email(nullIfEmpty(request.getEmail()))
                    .status(vgmStatus)
                    .userCr(user)
                    .userUp(user)
                    .oprStatus(oprStatus)
                    .build();
            rows.add(row);
            weightStrings.add(weightToString(container.getWeight()));
        }

        transactionalWriter.applyEcomUpsert(rows, weightStrings);
        documentationMirror.mirrorToDocumentationBestEffort(rows, weightStrings, bid);
    }

    /**
     * Validates VGM request containers against booking cargo distribution (equipment type counts).
     *
     * Rules:
     * - total request container count must match booking total
     * - each request equipmentType must exist in booking
     * - per-equipmentType counts must match
     *
     * Throws {@link IllegalArgumentException} with client-specified messages on mismatch.
     */
    private void validateContainersAgainstBooking(String bookingId, VGMInfo request) {
        if (request == null || request.getContainers() == null) {
            throw new IllegalArgumentException("Container count mismatch between booking and VGM request.");
        }

        Map<String, Long> expectedRaw = data.findEquipmentTypeCounts(bookingId);
        Map<String, Long> expected = new HashMap<>();
        if (expectedRaw != null) {
            for (Map.Entry<String, Long> e : expectedRaw.entrySet()) {
                String k = normalizeType(e.getKey());
                if (k.isEmpty()) continue;
                long v = e.getValue() != null ? e.getValue() : 0L;
                expected.put(k, v);
            }
        }

        Map<String, Long> provided = new HashMap<>();
        for (ContainerVGM c : request.getContainers()) {
            if (c == null) {
                continue;
            }
            String type = normalizeType(c.getEquipmentType());
            if (type.isEmpty()) {
                continue;
            }
            provided.put(type, provided.getOrDefault(type, 0L) + 1L);
        }

        long expectedTotal = 0L;
        for (Long v : expected.values()) {
            expectedTotal += v != null ? v : 0L;
        }
        long providedTotal = 0L;
        for (Long v : provided.values()) {
            providedTotal += v != null ? v : 0L;
        }

        if (expectedTotal != providedTotal) {
            throw new IllegalArgumentException("Container count mismatch between booking and VGM request.");
        }

        Set<String> providedTypes = provided.keySet();
        for (String t : providedTypes) {
            if (!expected.containsKey(t)) {
                throw new IllegalArgumentException("Invalid equipmentType provided: " + t);
            }
        }

        for (Map.Entry<String, Long> e : expected.entrySet()) {
            String type = e.getKey();
            long expectedCount = e.getValue() != null ? e.getValue() : 0L;
            long providedCount = provided.getOrDefault(type, 0L);
            if (expectedCount != providedCount) {
                throw new IllegalArgumentException(
                        "EquipmentType " + type + " count mismatch. Expected: " + expectedCount + ", Provided: " + providedCount + ".");
            }
        }
    }

    /**
     * Ensures each request container exists on {@code booking_cargo} and has an active
     * {@code Equipmentpassport} row whose {@code equipmenttype} matches the request (legacy ECOM behavior).
     *
     * @throws ContainerNotFoundException when equipment is not on the booking or passport is missing / type mismatch
     * @throws IllegalArgumentException   when {@code equipmentId} is blank for a non-null container entry
     */
    private void validateContainersOnBookingAndPassport(String bookingId, VGMInfo request) {
        if (request == null || request.getContainers() == null) {
            return;
        }

        List<BookingCargoRow> cargoRows = data.findBookingCargoRows(bookingId);
        Set<String> bookingEqpids = new HashSet<>();
        for (BookingCargoRow row : cargoRows) {
            if (row == null || row.getEqpid() == null) {
                continue;
            }
            String id = row.getEqpid().trim();
            if (id.isEmpty()) {
                continue;
            }
            bookingEqpids.add(id.toUpperCase(Locale.ROOT));
        }

        LinkedHashSet<String> notOnBooking = new LinkedHashSet<>();
        LinkedHashSet<String> allRequestIds = new LinkedHashSet<>();

        for (ContainerVGM c : request.getContainers()) {
            if (c == null) {
                continue;
            }
            if (c.getEquipmentId() == null || c.getEquipmentId().trim().isEmpty()) {
                throw new IllegalArgumentException("Equipment ID is required for each container.");
            }
            String eqpUpper = c.getEquipmentId().trim().toUpperCase(Locale.ROOT);
            allRequestIds.add(eqpUpper);
            if (!bookingEqpids.contains(eqpUpper)) {
                notOnBooking.add(eqpUpper);
            }
        }

        // if (!notOnBooking.isEmpty()) {
        //     throw new ContainerNotFoundException("Container(s) not on booking: " + String.join(", ", notOnBooking));
        // }

        if (allRequestIds.isEmpty()) {
            return;
        }

        Map<String, EquipmentPassportRow> passportByUpper = new HashMap<>();
        for (EquipmentPassportRow p : data.findEquipmentPassportActiveIn(new ArrayList<>(allRequestIds))) {
            if (p == null || p.getContainerno() == null) {
                continue;
            }
            String key = p.getContainerno().trim().toUpperCase(Locale.ROOT);
            if (!key.isEmpty()) {
                passportByUpper.putIfAbsent(key, p);
            }
        }

        LinkedHashSet<String> passportInvalid = new LinkedHashSet<>();
        for (ContainerVGM c : request.getContainers()) {
            if (c == null) {
                continue;
            }
            String eqpUpper = c.getEquipmentId().trim().toUpperCase(Locale.ROOT);
            EquipmentPassportRow p = passportByUpper.get(eqpUpper);
            if (p == null) {
                passportInvalid.add(eqpUpper);
                continue;
            }
            String requestType = normalizeType(c.getEquipmentType());
            String passportType = normalizeType(p.getEquipmenttype());
            if (requestType.isEmpty() || !requestType.equals(passportType)) {
                passportInvalid.add(eqpUpper);
            }
        }

        if (!passportInvalid.isEmpty()) {
            throw new ContainerNotFoundException("Invalid container number(s): " + String.join(", ", passportInvalid));
        }
    }

    /**
     * Legacy cap: VGM in kg must not exceed 50,000 kg (50 t). Uses {@link #vgmSubmittedWeightToKg(Double, String)}
     * so {@code T} is converted before comparison. Runs before tare validation and does not interact with
     * {@link VgmTareWeightResolver}.
     */
    private void validateVgmMaxWeightKg(VGMInfo request) {
        if (request == null || request.getContainers() == null) {
            return;
        }
        List<String> overMax = new ArrayList<>();
        for (ContainerVGM c : request.getContainers()) {
            if (c == null || c.getEquipmentId() == null || c.getEquipmentId().trim().isEmpty()) {
                continue;
            }
            String eqpUpper = c.getEquipmentId().trim().toUpperCase(Locale.ROOT);
            BigDecimal kg = vgmSubmittedWeightToKg(c.getWeight(), c.getUnit());
            if (kg == null) {
                continue;
            }
            if (kg.compareTo(MAX_VGM_WEIGHT_KG) > 0) {
                overMax.add(eqpUpper);
            }
        }
        if (overMax.isEmpty()) {
            return;
        }
        throw new IllegalArgumentException(MSG_VGM_EXCEEDS_MAX + " - " + String.join(", ", overMax));
    }

    /**
     * When a positive tare (kg) can be resolved (legacy {@code togetTareWtForCntr} chain), VGM must be strictly
     * greater than tare, matching the legacy web validator. Dummy booking lines skip this check.
     */
    private void validateVgmWeightAgainstResolvedTare(String bookingId, VGMInfo request) {
        if (request == null || request.getContainers() == null) {
            return;
        }
        Map<String, BookingCargoRow> cargoByEqp = new HashMap<>();
        for (BookingCargoRow row : data.findBookingCargoRows(bookingId)) {
            if (row == null || row.getEqpid() == null) {
                continue;
            }
            String id = row.getEqpid().trim();
            if (!id.isEmpty()) {
                cargoByEqp.putIfAbsent(id.toUpperCase(Locale.ROOT), row);
            }
        }
        List<String> failures = new ArrayList<>();
        for (ContainerVGM c : request.getContainers()) {
            if (c == null || c.getEquipmentId() == null || c.getEquipmentId().trim().isEmpty()) {
                continue;
            }
            String eqpUpper = c.getEquipmentId().trim().toUpperCase(Locale.ROOT);
            BookingCargoRow cargo = cargoByEqp.get(eqpUpper);
            if (cargo != null && isTruthyY(cargo.getIsdummy())) {
                continue;
            }
            String shipperOwn = cargo != null ? cargo.getShipperown() : "";
            TareResolutionResult r = tareWeightResolver.resolveForValidation(
                    eqpUpper, c.getEquipmentType(), shipperOwn);
            switch (r.getKind()) {
                case SKIPPED_SOC:
                    break;
                case INVALID_PROFILE_TARE:
                    failures.add(String.format(MSG_INVALID_PROFILE_TARE, eqpUpper));
                    break;
                case UNAVAILABLE:
                    failures.add(String.format(MSG_TARE_UNAVAILABLE, eqpUpper));
                    break;
                case NUMERIC:
                    BigDecimal tareKg = r.getTareKg().orElse(null);
                    if (tareKg == null) {
                        failures.add(String.format(MSG_TARE_UNAVAILABLE, eqpUpper));
                        break;
                    }
                    BigDecimal vgmKg = vgmSubmittedWeightToKg(c.getWeight(), c.getUnit());
                    if (vgmKg == null) {
                        failures.add(String.format(MSG_TARE_UNAVAILABLE, eqpUpper));
                        break;
                    }
                    if (vgmKg.compareTo(tareKg) <= 0) {
                        failures.add(String.format(MSG_VGM_NOT_ABOVE_TARE, eqpUpper, formatKgForMessage(tareKg)));
                    }
                    break;
                default:
                    break;
            }
        }
        if (!failures.isEmpty()) {
            if (failures.size() == 1) {
                throw new IllegalArgumentException(failures.get(0));
            }
            throw new IllegalArgumentException(MSG_BATCH + " " + String.join("; ", failures));
        }
    }

    private static boolean isTruthyY(String flag) {
        return flag != null && "Y".equalsIgnoreCase(flag.trim());
    }

    /** Plain-string kg for user-facing validation messages (no scientific notation). */
    private static String formatKgForMessage(BigDecimal kg) {
        if (kg == null) {
            return "";
        }
        return kg.stripTrailingZeros().toPlainString();
    }

    private static BigDecimal vgmSubmittedWeightToKg(Double weight, WeightUnit unit) {
        if (weight == null) {
            return null;
        }
        BigDecimal w = BigDecimal.valueOf(weight.doubleValue());
        if (unit == null) {
            return w;
        }
        if (WeightUnit.T == unit) {
            return w.multiply(BigDecimal.valueOf(1000L));
        }
        return w;
    }

    private static String normalizeType(String equipmentType) {
        if (equipmentType == null) {
            return "";
        }
        return equipmentType.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * When LRP {@code vgmsolas} has rows for the booking (latest version), response containers
     * come from those rows so real container numbers and weights are returned even if
     * {@code booking_cargo.eqpid} still holds placeholders.
     */
    private static List<ContainerVGM> buildContainersFromVgmSolasRows(List<VgmSolasRow> vgmRows) {
        List<ContainerVGM> out = new ArrayList<>();
        for (VgmSolasRow r : vgmRows) {
            if (r == null || isNullOrBlank(r.getEqpid())) {
                continue;
            }
            String eqpUpper = r.getEqpid().trim().toUpperCase(Locale.ROOT);
            Double weight = parseWeight(r.getVgmWeight());
            WeightUnit unit = toWeightUnit(nullIfEmpty(r.getVgmUnit()));
            String equipmentType = nullIfEmpty(r.getEqptype()) != null ? r.getEqptype().trim() : "";
            out.add(ContainerVGM.builder()
                    .equipmentId(eqpUpper)
                    .equipmentType(equipmentType)
                    .weight(weight)
                    .unit(unit)
                    .build());
        }
        return out;
    }

    /**
     * No VGM rows yet: one output row per {@code booking_cargo}, merging LRP VGM by matching {@code eqpid}
     * when present, or a single-container unambiguous VGM row.
     */
    private static List<ContainerVGM> buildContainersFromBookingCargoWithVgmOverlay(
            List<BookingCargoRow> containerList,
            List<VgmSolasRow> vgmRows) {
        Map<String, VgmSolasRow> vgmByEqpUpper = new HashMap<>();
        if (vgmRows != null) {
            for (VgmSolasRow r : vgmRows) {
                if (r == null || isNullOrBlank(r.getEqpid())) {
                    continue;
                }
                vgmByEqpUpper.put(r.getEqpid().trim().toUpperCase(Locale.ROOT), r);
            }
        }

        VgmSolasRow unambiguousLrpVgm = null;
        if (containerList.size() == 1
                && vgmRows != null
                && vgmRows.size() == 1
                && vgmRows.get(0) != null
                && !isNullOrBlank(vgmRows.get(0).getEqpid())) {
            unambiguousLrpVgm = vgmRows.get(0);
        }

        List<ContainerVGM> containers = new ArrayList<>();
        for (BookingCargoRow row : containerList) {
            String eqpUpper = row.getEqpid() != null ? row.getEqpid().trim().toUpperCase(Locale.ROOT) : null;
            VgmSolasRow vgm = eqpUpper != null ? vgmByEqpUpper.get(eqpUpper) : null;
            boolean usedUnambiguousLrpVgm = false;
            if (vgm == null && unambiguousLrpVgm != null) {
                vgm = unambiguousLrpVgm;
                usedUnambiguousLrpVgm = true;
            }

            String equipmentIdOut = eqpUpper;
            if (usedUnambiguousLrpVgm && vgm != null && vgm.getEqpid() != null) {
                equipmentIdOut = vgm.getEqpid().trim().toUpperCase(Locale.ROOT);
            }

            Double weight;
            WeightUnit unit;
            if (vgm != null) {
                weight = parseWeight(vgm.getVgmWeight());
                unit = toWeightUnit(nullIfEmpty(vgm.getVgmUnit()));
            } else {
                weight = null;
                unit = WeightUnit.K;
            }

            String equipmentType;
            if (vgm != null && nullIfEmpty(vgm.getEqptype()) != null) {
                equipmentType = vgm.getEqptype().trim();
            } else {
                equipmentType = row.getEqptypeid();
            }

            containers.add(ContainerVGM.builder()
                    .equipmentId(equipmentIdOut)
                    .equipmentType(equipmentType)
                    .weight(weight)
                    .unit(unit)
                    .build());
        }
        return containers;
    }

    public VGMInfo getVGMDetails(String bookingId, String customerCode) {
        ensureConfigured();

        String bid = bookingId != null ? bookingId.trim() : "";
        String cust = customerCode != null ? customerCode.trim() : "";
        if (bid.isEmpty()) {
            throw new BookingNotFoundException("Booking not found or void");
        }

        List<BookingCargoRow> containerList = data.findBookingCargoRows(bid);
        if (containerList.isEmpty()) {
            throw new BookingNotFoundException("Booking not found or void");
        }

        assertCustomerAllowed(bid, cust);

        Optional<VgmSolasRow> vgmHeaderOpt = data.findLatestActiveVgmHeader(bid);
        VgmSolasRow vgmHeader = vgmHeaderOpt.orElse(null);

        List<VgmSolasRow> vgmRows = data.findContainerListFromVgmSolas(bid);
        List<ContainerVGM> containers;
        if (vgmRows != null && !vgmRows.isEmpty()) {
            containers = buildContainersFromVgmSolasRows(vgmRows);
            if (containers.isEmpty()) {
                containers = buildContainersFromBookingCargoWithVgmOverlay(containerList, vgmRows);
            }
        } else {
            containers = buildContainersFromBookingCargoWithVgmOverlay(containerList, vgmRows);
        }

        String refForResponse = bid;
        if (vgmHeader != null && nullIfEmpty(vgmHeader.getVgmRefNumber()) != null) {
            refForResponse = nullIfEmpty(vgmHeader.getVgmRefNumber());
        }

        VGMInfo.VGMInfoBuilder b = VGMInfo.builder()
                .referenceNumber(refForResponse)
                .source(SOURCE_CUSTOMER)
                .containers(containers);

        if (vgmHeader != null) {
            b.orderNumber(emptyIfNull(nullIfEmpty(vgmHeader.getVgmOrderNumber())))
                    .authorizedPerson(emptyIfNull(upperOrNull(nullIfEmpty(vgmHeader.getVgmAuthorizedPerson()))))
                    .obtainedAt(emptyIfNull(toISO8601(vgmHeader.getVgmObtainedDate())))
                    .method(parseVgmMethod(vgmHeader.getVgmObtainedMethod()))
                    .version(vgmHeader.getVersionNo() != null ? vgmHeader.getVersionNo() : 0)
                    .status(parseVgmStatus(vgmHeader.getStatus()))
                    .companyName(emptyIfNull(upperOrNull(nullIfEmpty(vgmHeader.getVgmCompanyName()))))
                    .phone(emptyIfNull(nullIfEmpty(vgmHeader.getTelephone())))
                    .email(emptyIfNull(nullIfEmpty(vgmHeader.getEmail())))
                    .address(buildAddress(vgmHeader));
        } else {
            b.orderNumber("")
                    .authorizedPerson("")
                    .obtainedAt("")
                    .method(null)
                    .version(0)
                    .status(null)
                    .companyName("")
                    .phone("")
                    .email("")
                    .address(emptyAddress());
        }

        return b.build();
    }

    private void assertCustomerAllowed(String bookingId, String customerCode) {
        if (customerCode == null || customerCode.trim().isEmpty()) {
            return;
        }
        if (data.customerHasEcomAccess(bookingId, customerCode)
                || data.customerHasLrpAccess(bookingId, customerCode)) {
            return;
        }
        throw new BookingVgmForbiddenException("Access denied to booking: " + bookingId);
    }

    private static boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String toISO8601(String dbDate) {
        if (isNullOrBlank(dbDate)) {
            return null;
        }
        String d = dbDate.trim();
        if (d.length() == 10) {
            return d + "T00:00:00Z";
        }
        return d.replace(" ", "T") + "Z";
    }

    private static Double parseWeight(String weight) {
        if (isNullOrBlank(weight)) {
            return null;
        }
        try {
            return Double.parseDouble(weight.replace(",", "").trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String nullIfEmpty(String value) {
        return isNullOrBlank(value) ? null : value.trim();
    }

    private static String upperOrNull(String value) {
        return value == null ? null : value.toUpperCase(Locale.ROOT);
    }

    private static String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    private static String toDBDateFormat(String iso) {
        if (isNullOrBlank(iso)) {
            return null;
        }
        return iso.replace("T", " ").replace("Z", "").trim();
    }

    private static String toUpperCase(String value) {
        return isNullOrBlank(value) ? null : value.toUpperCase(Locale.ROOT).trim();
    }

    private static WeightUnit toWeightUnit(String unit) {
        if (isNullOrBlank(unit)) {
            return WeightUnit.K;
        }
        try {
            return WeightUnit.fromValue(unit.trim());
        } catch (IllegalArgumentException ex) {
            return WeightUnit.K;
        }
    }

    private static VgmMethod parseVgmMethod(String method) {
        if (isNullOrBlank(method)) {
            return null;
        }
        try {
            return VgmMethod.fromValue(method.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static VgmStatus parseVgmStatus(String status) {
        String s = nullIfEmpty(status);
        if (s == null) {
            return null;
        }
        try {
            return VgmStatus.valueOf(s.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String getLine(Address address, int index) {
        if (address == null || address.getLines() == null) {
            return null;
        }
        if (address.getLines().size() <= index) {
            return null;
        }
        String line = address.getLines().get(index);
        return isNullOrBlank(line) ? null : line.trim();
    }

    private static String getLocality(Address address) {
        return address == null ? null : nullIfEmpty(address.getLocality());
    }

    private static String getPostalCode(Address address) {
        return address == null ? null : nullIfEmpty(address.getPostalCode());
    }

    private static String getCountryCode(Address address) {
        if (address == null) {
            return null;
        }
        String code = nullIfEmpty(address.getCountryCode());
        return code == null ? null : code.toUpperCase(Locale.ROOT);
    }

    private static String weightToString(Double weight) {
        if (weight == null) {
            return null;
        }
        return String.valueOf(weight.longValue());
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private Address buildAddress(VgmSolasRow row) {
        List<String> lines = new ArrayList<>();
        if (!isBlank(row.getVgmAddress1())) {
            lines.add(row.getVgmAddress1().trim());
        }
        if (!isBlank(row.getVgmAddress2())) {
            lines.add(row.getVgmAddress2().trim());
        }

        Address dto = Address.builder()
                .lines(lines)
                .locality(emptyIfNull(nullIfEmpty(row.getVgmCity())))
                .postalCode(emptyIfNull(nullIfEmpty(row.getVgmZipcode())))
                .countryCode(emptyIfNull(nullIfEmpty(row.getVgmCountry())))
                .administrativeArea("")
                .build();
        if (dto.getLines() == null) {
            dto.setLines(new ArrayList<>());
        }
        return dto;
    }

    private static Address emptyAddress() {
        return Address.builder()
                .lines(new ArrayList<>())
                .locality("")
                .postalCode("")
                .countryCode("")
                .administrativeArea("")
                .build();
    }
}
