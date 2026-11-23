package com.solverminds.booking.service;

import com.solverminds.booking.dto.v2.vgm.TareResolutionResult;
import com.solverminds.booking.dto.v2.vgm.row.ContainerProfileTareRow;
import com.solverminds.booking.repo.EquipmentRepository;
import com.solverminds.booking.repo.VgmDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Resolves tare for VGM validation (legacy {@code EcomVGMBean#togetTareWtForCntr}).
 * {@code containerprofile} errors (missing table, etc.) are swallowed at the repository layer; this resolver
 * continues with equipment master when the SOC / TNB–TBN gate allows it.
 */
@Service
public class VgmTareWeightResolver {

    private static final Logger logger = LoggerFactory.getLogger(VgmTareWeightResolver.class);

    private final VgmDataRepository vgmDataRepository;
    private final EquipmentRepository equipmentRepository;

    public VgmTareWeightResolver(VgmDataRepository vgmDataRepository, EquipmentRepository equipmentRepository) {
        this.vgmDataRepository = vgmDataRepository;
        this.equipmentRepository = equipmentRepository;
    }

    /**
     * @param containerNo      container number
     * @param equipmentType    e.g. {@code 20DC}
     * @param shipperOwnFlag   {@code booking_cargo.shipperown}
     */
    public TareResolutionResult resolveForValidation(
            String containerNo,
            String equipmentType,
            String shipperOwnFromBooking) {
        if (equipmentType == null || equipmentType.trim().isEmpty()) {
            return TareResolutionResult.unavailable();
        }
        String cn = containerNo != null ? containerNo.trim().toUpperCase(Locale.ROOT) : "";
        String equip = equipmentType.trim().toUpperCase(Locale.ROOT);
        Optional<ContainerProfileTareRow> profileOpt = vgmDataRepository.findContainerProfileTare(cn, equip);
        return resolveWithProfile(cn, equip, equipmentType, shipperOwnFromBooking, profileOpt,
                equipmentRepository::getTareWeightByEquipType);
    }

    /**
     * Same rules as {@link #resolveForValidation(String, String, String)} using preloaded profile
     * and equipment-master tares (V3 batch path). Does not change v2 lookup behavior.
     */
    public TareResolutionResult resolveForValidation(
            String containerNo,
            String equipmentType,
            String shipperOwnFromBooking,
            Optional<ContainerProfileTareRow> profileOpt,
            Map<String, Float> equipmentTareByType) {
        if (equipmentType == null || equipmentType.trim().isEmpty()) {
            return TareResolutionResult.unavailable();
        }
        String cn = containerNo != null ? containerNo.trim().toUpperCase(Locale.ROOT) : "";
        String equip = equipmentType.trim().toUpperCase(Locale.ROOT);
        return resolveWithProfile(cn, equip, equipmentType, shipperOwnFromBooking,
                profileOpt == null ? Optional.empty() : profileOpt,
                type -> lookupTare(equipmentTareByType, type));
    }

    private TareResolutionResult resolveWithProfile(
            String cn,
            String equip,
            String equipmentType,
            String shipperOwnFromBooking,
            Optional<ContainerProfileTareRow> profileOpt,
            Function<String, Float> equipmentTareLookup) {
        boolean socProfileRow = false;
        String rawTareFromProfile = null;
        if (profileOpt != null && profileOpt.isPresent()) {
            ContainerProfileTareRow row = profileOpt.get();
            if ("Y".equalsIgnoreCase(trimToEmpty(row.getShipperownedunit()))) {
                socProfileRow = true;
            } else {
                rawTareFromProfile = row.getTarewt();
            }
        }

        if (!socProfileRow) {
            Optional<BigDecimal> fromProfile = parsePositiveTareKg(rawTareFromProfile);
            if (fromProfile.isPresent()) {
                return TareResolutionResult.numeric(fromProfile.get());
            }
        }

        boolean gate = equipmentFallbackPermitted(shipperOwnFromBooking, cn, equipmentType);
        boolean badProfileTare = !socProfileRow && hasNonEmptyUnusableProfileTare(rawTareFromProfile);

        if (!gate) {
            if (badProfileTare) {
                return TareResolutionResult.invalidProfileTare();
            }
            logger.debug("Tare weight validation is not applicable for SOC container {}.", cn);
            return TareResolutionResult.skippedSoc();
        }

        Float tkg = equipmentTareLookup.apply(equip);
        if (tkg != null && tkg > 0f) {
            return TareResolutionResult.numeric(BigDecimal.valueOf(tkg.doubleValue()));
        }

        return TareResolutionResult.unavailable();
    }

    private static Float lookupTare(Map<String, Float> equipmentTareByType, String equipUpper) {
        if (equipmentTareByType == null || equipUpper == null) {
            return null;
        }
        Float tkg = equipmentTareByType.get(equipUpper);
        if (tkg != null) {
            return tkg;
        }
        return equipmentTareByType.get(equipUpper.trim());
    }

    /**
     * Non-empty profile tare text that is not a positive number (0, negative, or not parseable).
     */
    static boolean hasNonEmptyUnusableProfileTare(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return false;
        }
        return !parsePositiveTareKg(raw).isPresent();
    }

    /**
     * Legacy gate: allow equipment master lookup unless shipper-owned is Y without TNB/TBN exception.
     */
    static boolean equipmentFallbackPermitted(
            String shipperOwnFromBooking,
            String containerNoUpper,
            String equipmentType) {
        String soc = shipperOwnFromBooking != null ? shipperOwnFromBooking.trim() : "";
        if (!"Y".equalsIgnoreCase(soc)) {
            return true;
        }
        String prefix = leadingDigitPrefix(equipmentType);
        return containerNoUpper.contains(prefix + "TNB")
                || containerNoUpper.contains(prefix + "TBN");
    }

    static String leadingDigitPrefix(String equipmentType) {
        if (equipmentType == null) {
            return "";
        }
        String t = equipmentType.trim();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if (Character.isDigit(c)) {
                sb.append(c);
            } else {
                break;
            }
        }
        return sb.toString();
    }

    static Optional<BigDecimal> parsePositiveTareKg(String raw) {
        if (raw == null) {
            return Optional.empty();
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return Optional.empty();
        }
        try {
            BigDecimal d = new BigDecimal(s.replace(",", ""));
            if (d.compareTo(BigDecimal.ZERO) <= 0) {
                return Optional.empty();
            }
            return Optional.of(d);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    static String trimToEmpty(String s) {
        return s != null ? s.trim() : "";
    }
}
