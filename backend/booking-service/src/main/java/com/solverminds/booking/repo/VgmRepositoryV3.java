package com.solverminds.booking.repo;

import com.solverminds.booking.dto.v2.vgm.Address;
import com.solverminds.booking.dto.v2.vgm.TareResolutionResult;
import com.solverminds.booking.dto.v2.vgm.VgmMethod;
import com.solverminds.booking.dto.v2.vgm.VgmStatus;
import com.solverminds.booking.dto.v2.vgm.row.BookingCargoRow;
import com.solverminds.booking.dto.v2.vgm.row.BookingCargoSnapshot;
import com.solverminds.booking.dto.v2.vgm.row.ContainerProfileTareRow;
import com.solverminds.booking.dto.v2.vgm.row.EquipmentPassportRow;
import com.solverminds.booking.dto.v2.vgm.row.VgmInsertRow;
import com.solverminds.booking.dto.v2.vgm.row.VgmSolasRow;
import com.solverminds.booking.dto.v3.vgm.ContainerVgmV3;
import com.solverminds.booking.dto.v3.vgm.VgmInfoV3;
import com.solverminds.booking.exception.BookingNotFoundException;
import com.solverminds.booking.exception.BookingVgmForbiddenException;
import com.solverminds.booking.exception.ContainerNotFoundException;
import com.solverminds.booking.service.VgmDocumentationMirrorService;
import com.solverminds.booking.service.VgmTareWeightResolver;
import com.solverminds.booking.service.VgmTransactionalWriter;
import com.svm.ecom.common.enums.WeightUnit;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * V3 VGM: same business rules as {@link VgmRepository}, container-level header fields,
 * LRP-only access, and batched reads (exists+cargo, passport, profile, version).
 */
@Repository
public class VgmRepositoryV3 {

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
    private final EquipmentRepository equipmentRepository;

    public VgmRepositoryV3(
            VgmDataRepository data,
            VgmTransactionalWriter transactionalWriter,
            VgmDocumentationMirrorService documentationMirror,
            VgmTareWeightResolver tareWeightResolver,
            EquipmentRepository equipmentRepository) {
        this.data = data;
        this.transactionalWriter = transactionalWriter;
        this.documentationMirror = documentationMirror;
        this.tareWeightResolver = tareWeightResolver;
        this.equipmentRepository = equipmentRepository;
    }

    private void ensureConfigured() {
        if (data == null || transactionalWriter == null || documentationMirror == null
                || tareWeightResolver == null || equipmentRepository == null) {
            throw new IllegalStateException("VGM components are not configured");
        }
    }

    public void saveVGMDetails(String bookingId, VgmInfoV3 request, String customerCode, String loginUser) {
        ensureConfigured();

        String bid = bookingId != null ? bookingId.trim() : "";
        String cust = customerCode != null ? customerCode.trim() : "";
        if (bid.isEmpty()) {
            throw new BookingNotFoundException("Booking not found or void");
        }
        String user = loginUser != null ? loginUser.trim() : "";

        BookingCargoSnapshot snapshot = data.findBookingCargoSnapshot(bid);
        if (!snapshot.isBookingFound()) {
            throw new BookingNotFoundException("Booking not found or void");
        }

        assertLrpCustomerAllowed(bid, cust);

        List<BookingCargoRow> cargoRows = snapshot.getCargoRows();
        validateContainersAgainstBooking(cargoRows, request);
        validateContainersOnPassport(request);
        validateVgmMaxWeightKg(request);
        validateVgmWeightAgainstResolvedTare(cargoRows, request);

        List<String> eqpids = new ArrayList<>();
        for (ContainerVgmV3 container : request.getContainers()) {
            eqpids.add(container.getEquipmentId().trim().toUpperCase(Locale.ROOT));
        }
        Map<String, Integer> maxVersions = data.findMaxVersionEcomIn(bid, eqpids);

        List<VgmInsertRow> rows = new ArrayList<>();
        List<String> weightStrings = new ArrayList<>();
        for (ContainerVgmV3 container : request.getContainers()) {
            String eqpUpper = container.getEquipmentId().trim().toUpperCase(Locale.ROOT);
            Integer maxVersion = maxVersions.get(eqpUpper);
            int newVersion = maxVersion == null ? 1 : maxVersion + 1;

            VgmInsertRow row = VgmInsertRow.builder()
                    .bookNo(bid)
                    .blNo(null)
                    .eqptype(container.getEquipmentType().trim())
                    .eqpid(eqpUpper)
                    .vgmWeight(container.getWeight())
                    .vgmUnit(container.getUnit() != null ? container.getUnit().getValue().trim() : DEFAULT_UNIT_KG)
                    .versionNo(newVersion)
                    .vgmSource(SOURCE_CUSTOMER)
                    .vgmType("")
                    .vgmRefNumber(nullIfEmpty(container.getReferenceNumber()))
                    .vgmOrderNumber(null)
                    .vgmObtainedDate(toDBDateFormat(container.getObtainedAt()))
                    .vgmObtainedMethod(container.getMethod() != null ? container.getMethod().getValue().trim() : null)
                    .vgmCompanyName(toUpperCase(container.getCompanyName()))
                    .vgmAddress1(getLine(container.getAddress(), 0))
                    .vgmAddress2(getLine(container.getAddress(), 1))
                    .vgmZipcode(getPostalCode(container.getAddress()))
                    .vgmCity(getLocality(container.getAddress()))
                    .vgmCountry(getCountryCode(container.getAddress()))
                    .vgmAuthorizedPerson(container.getAuthorizedPerson().trim().toUpperCase(Locale.ROOT))
                    .telephone(nullIfEmpty(container.getPhone()))
                    .email(nullIfEmpty(container.getEmail()))
                    .status("A")
                    .userCr(user)
                    .userUp(user)
                    .oprStatus(null)
                    .build();
            rows.add(row);
            weightStrings.add(weightToString(container.getWeight()));
        }

        transactionalWriter.applyEcomUpsert(rows, weightStrings);
        documentationMirror.mirrorToDocumentationBestEffort(rows, weightStrings, bid);
    }

    public VgmInfoV3 getVGMDetails(String bookingId, String customerCode) {
        ensureConfigured();

        String bid = bookingId != null ? bookingId.trim() : "";
        String cust = customerCode != null ? customerCode.trim() : "";
        if (bid.isEmpty()) {
            throw new BookingNotFoundException("Booking not found or void");
        }

        BookingCargoSnapshot snapshot = data.findBookingCargoSnapshot(bid);
        if (!snapshot.isBookingFound() || snapshot.getCargoRows().isEmpty()) {
            throw new BookingNotFoundException("Booking not found or void");
        }

        assertLrpCustomerAllowed(bid, cust);

        Map<String, VgmSolasRow> latestByEqp = latestActiveByEqpid(data.findActiveVgmRowsEcom(bid));
        Map<String, Deque<VgmSolasRow>> unmatchedVgmByType =
                groupUnmatchedVgmByType(snapshot.getCargoRows(), latestByEqp);

        List<ContainerVgmV3> containers = new ArrayList<>();
        LinkedHashSet<String> emitted = new LinkedHashSet<>();

        for (BookingCargoRow cargo : snapshot.getCargoRows()) {
            if (cargo == null || isNullOrBlank(cargo.getEqpid())) {
                continue;
            }
            String eqpUpper = cargo.getEqpid().trim().toUpperCase(Locale.ROOT);
            VgmSolasRow vgm = latestByEqp.get(eqpUpper);

            // Placeholder/dummy cargo lines (real container not yet reconciled onto booking_cargo)
            // can leave an orphaned VGM row keyed on the real container id. Pair it here by
            // equipment type instead of emitting both a blank placeholder and a duplicate real row.
            if (vgm == null && isTruthyY(cargo.getIsdummy())) {
                Deque<VgmSolasRow> pool = unmatchedVgmByType.get(normalizeType(cargo.getEqptypeid()));
                if (pool != null && !pool.isEmpty()) {
                    vgm = pool.poll();
                }
            }

            if (vgm != null) {
                containers.add(toContainerFromVgmRow(vgm, bid));
                String vgmEqpUpper = !isNullOrBlank(vgm.getEqpid())
                        ? vgm.getEqpid().trim().toUpperCase(Locale.ROOT) : null;
                if (vgmEqpUpper != null) {
                    emitted.add(vgmEqpUpper);
                }
            } else {
                containers.add(emptyContainerFromCargo(cargo));
            }
            emitted.add(eqpUpper);
        }
        for (Map.Entry<String, VgmSolasRow> e : latestByEqp.entrySet()) {
            if (e.getKey() == null || emitted.contains(e.getKey()) || e.getValue() == null) {
                continue;
            }
            containers.add(toContainerFromVgmRow(e.getValue(), bid));
        }

        return VgmInfoV3.builder().containers(containers).build();
    }

    /**
     * VGM rows whose eqpid has no exact match among the booking's cargo rows, grouped by equipment
     * type. Used only to reconcile placeholder/dummy cargo lines (see {@link #getVGMDetails}); rows
     * that already match a cargo eqpid exactly are excluded and continue to be handled by the normal
     * direct-match path unchanged.
     */
    private static Map<String, Deque<VgmSolasRow>> groupUnmatchedVgmByType(
            List<BookingCargoRow> cargoRows, Map<String, VgmSolasRow> latestByEqp) {
        LinkedHashSet<String> cargoEqpids = new LinkedHashSet<>();
        if (cargoRows != null) {
            for (BookingCargoRow cargo : cargoRows) {
                if (cargo != null && !isNullOrBlank(cargo.getEqpid())) {
                    cargoEqpids.add(cargo.getEqpid().trim().toUpperCase(Locale.ROOT));
                }
            }
        }
        Map<String, Deque<VgmSolasRow>> byType = new HashMap<>();
        for (Map.Entry<String, VgmSolasRow> e : latestByEqp.entrySet()) {
            if (e.getKey() == null || cargoEqpids.contains(e.getKey()) || e.getValue() == null) {
                continue;
            }
            String type = normalizeType(e.getValue().getEqptype());
            byType.computeIfAbsent(type, k -> new ArrayDeque<>()).add(e.getValue());
        }
        return byType;
    }

    private void assertLrpCustomerAllowed(String bookingId, String customerCode) {
        if (customerCode == null || customerCode.trim().isEmpty()) {
            return;
        }
        if (data.customerHasLrpAccess(bookingId, customerCode)) {
            return;
        }
        throw new BookingVgmForbiddenException("Access denied to booking: " + bookingId);
    }

    private void validateContainersAgainstBooking(List<BookingCargoRow> cargoRows, VgmInfoV3 request) {
        if (request == null || request.getContainers() == null) {
            throw new IllegalArgumentException("Container count mismatch between booking and VGM request.");
        }

        Map<String, Long> expected = new HashMap<>();
        if (cargoRows != null) {
            for (BookingCargoRow row : cargoRows) {
                if (row == null) {
                    continue;
                }
                String k = normalizeType(row.getEqptypeid());
                if (k.isEmpty()) {
                    continue;
                }
                expected.put(k, expected.getOrDefault(k, 0L) + 1L);
            }
        }

        Map<String, Long> provided = new HashMap<>();
        for (ContainerVgmV3 c : request.getContainers()) {
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

        for (String t : provided.keySet()) {
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
                        "EquipmentType " + type + " count mismatch. Expected: " + expectedCount
                                + ", Provided: " + providedCount + ".");
            }
        }
    }

    private void validateContainersOnPassport(VgmInfoV3 request) {
        if (request == null || request.getContainers() == null) {
            return;
        }

        LinkedHashSet<String> allRequestIds = new LinkedHashSet<>();
        for (ContainerVgmV3 c : request.getContainers()) {
            if (c == null) {
                continue;
            }
            if (c.getEquipmentId() == null || c.getEquipmentId().trim().isEmpty()) {
                throw new IllegalArgumentException("Equipment ID is required for each container.");
            }
            allRequestIds.add(c.getEquipmentId().trim().toUpperCase(Locale.ROOT));
        }
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
        for (ContainerVgmV3 c : request.getContainers()) {
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

    private void validateVgmMaxWeightKg(VgmInfoV3 request) {
        if (request == null || request.getContainers() == null) {
            return;
        }
        List<String> overMax = new ArrayList<>();
        for (ContainerVgmV3 c : request.getContainers()) {
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

    private void validateVgmWeightAgainstResolvedTare(List<BookingCargoRow> cargoRows, VgmInfoV3 request) {
        if (request == null || request.getContainers() == null) {
            return;
        }
        Map<String, BookingCargoRow> cargoByEqp = new HashMap<>();
        if (cargoRows != null) {
            for (BookingCargoRow row : cargoRows) {
                if (row == null || row.getEqpid() == null) {
                    continue;
                }
                String id = row.getEqpid().trim();
                if (!id.isEmpty()) {
                    cargoByEqp.putIfAbsent(id.toUpperCase(Locale.ROOT), row);
                }
            }
        }

        List<String> profileIds = new ArrayList<>();
        LinkedHashSet<String> distinctTypes = new LinkedHashSet<>();
        for (ContainerVgmV3 c : request.getContainers()) {
            if (c == null || isNullOrBlank(c.getEquipmentId())) {
                continue;
            }
            profileIds.add(c.getEquipmentId().trim().toUpperCase(Locale.ROOT));
            if (!isNullOrBlank(c.getEquipmentType())) {
                distinctTypes.add(c.getEquipmentType().trim().toUpperCase(Locale.ROOT));
            }
        }

        Map<String, ContainerProfileTareRow> profileByKey = new HashMap<>();
        for (ContainerProfileTareRow row : data.findContainerProfileTareIn(profileIds)) {
            if (row == null || isNullOrBlank(row.getContainerno()) || isNullOrBlank(row.getEquipmenttype())) {
                continue;
            }
            String key = row.getContainerno().trim().toUpperCase(Locale.ROOT)
                    + "|" + row.getEquipmenttype().trim().toUpperCase(Locale.ROOT);
            profileByKey.putIfAbsent(key, row);
        }

        Map<String, Float> equipmentTareByType = new HashMap<>();
        if (!distinctTypes.isEmpty()) {
            Map<String, Float> raw = equipmentRepository.getTareWeightsByEquipTypes(distinctTypes);
            if (raw != null) {
                for (Map.Entry<String, Float> e : raw.entrySet()) {
                    if (e.getKey() == null) {
                        continue;
                    }
                    equipmentTareByType.put(e.getKey().trim().toUpperCase(Locale.ROOT), e.getValue());
                }
            }
        }

        List<String> failures = new ArrayList<>();
        for (ContainerVgmV3 c : request.getContainers()) {
            if (c == null || isNullOrBlank(c.getEquipmentId())) {
                continue;
            }
            String eqpUpper = c.getEquipmentId().trim().toUpperCase(Locale.ROOT);
            BookingCargoRow cargo = cargoByEqp.get(eqpUpper);
            if (cargo != null && isTruthyY(cargo.getIsdummy())) {
                continue;
            }
            String shipperOwn = cargo != null ? cargo.getShipperown() : "";
            String typeKey = normalizeType(c.getEquipmentType());
            Optional<ContainerProfileTareRow> profileOpt = Optional.ofNullable(
                    profileByKey.get(eqpUpper + "|" + typeKey));
            TareResolutionResult r = tareWeightResolver.resolveForValidation(
                    eqpUpper, c.getEquipmentType(), shipperOwn, profileOpt, equipmentTareByType);
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

    private static Map<String, VgmSolasRow> latestActiveByEqpid(List<VgmSolasRow> rows) {
        Map<String, VgmSolasRow> latest = new LinkedHashMap<>();
        if (rows == null) {
            return latest;
        }
        for (VgmSolasRow row : rows) {
            if (row == null || isNullOrBlank(row.getEqpid())) {
                continue;
            }
            String key = row.getEqpid().trim().toUpperCase(Locale.ROOT);
            VgmSolasRow existing = latest.get(key);
            if (existing == null) {
                latest.put(key, row);
                continue;
            }
            int existingVer = existing.getVersionNo() != null ? existing.getVersionNo() : 0;
            int rowVer = row.getVersionNo() != null ? row.getVersionNo() : 0;
            if (rowVer >= existingVer) {
                latest.put(key, row);
            }
        }
        return latest;
    }

    private ContainerVgmV3 toContainerFromVgmRow(VgmSolasRow row, String bookingId) {
        String eqpUpper = row.getEqpid().trim().toUpperCase(Locale.ROOT);
        String ref = nullIfEmpty(row.getVgmRefNumber());
        return ContainerVgmV3.builder()
                .equipmentId(eqpUpper)
                .equipmentType(nullIfEmpty(row.getEqptype()) != null ? row.getEqptype().trim() : "")
                .weight(parseWeight(row.getVgmWeight()))
                .unit(toWeightUnit(nullIfEmpty(row.getVgmUnit())))
                .authorizedPerson(emptyIfNull(upperOrNull(nullIfEmpty(row.getVgmAuthorizedPerson()))))
                .obtainedAt(emptyIfNull(toISO8601(row.getVgmObtainedDate())))
                .method(parseVgmMethod(row.getVgmObtainedMethod()))
                .referenceNumber(ref != null ? ref : bookingId)
                .companyName(emptyIfNull(upperOrNull(nullIfEmpty(row.getVgmCompanyName()))))
                .phone(emptyIfNull(nullIfEmpty(row.getTelephone())))
                .email(emptyIfNull(nullIfEmpty(row.getEmail())))
                .address(buildAddress(row))
                .source(SOURCE_CUSTOMER)
                .version(row.getVersionNo() != null ? row.getVersionNo() : 0)
                .status(parseVgmStatus(row.getStatus()))
                .build();
    }

    private static ContainerVgmV3 emptyContainerFromCargo(BookingCargoRow cargo) {
        String eqpUpper = cargo.getEqpid().trim().toUpperCase(Locale.ROOT);
        return ContainerVgmV3.builder()
                .equipmentId(eqpUpper)
                .equipmentType(cargo.getEqptypeid())
                .weight(null)
                .unit(WeightUnit.K)
                .authorizedPerson("")
                .obtainedAt("")
                .method(null)
                .referenceNumber("")
                .companyName("")
                .phone("")
                .email("")
                .address(emptyAddress())
                .source(SOURCE_CUSTOMER)
                .version(0)
                .status(null)
                .build();
    }

    private static boolean isTruthyY(String flag) {
        return flag != null && "Y".equalsIgnoreCase(flag.trim());
    }

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

    /** Persist the submitted weight as-is (no rounding/truncation). v2 still stores whole kilos. */
    private static String weightToString(Double weight) {
        if (weight == null) {
            return null;
        }
        return new BigDecimal(weight.toString()).stripTrailingZeros().toPlainString();
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
