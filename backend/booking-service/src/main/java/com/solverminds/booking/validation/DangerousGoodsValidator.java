package com.solverminds.booking.validation;

import com.solverminds.booking.config.DangerousGoodsProperties;
import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.dto.CommodityDTO;
import com.solverminds.booking.dto.ContainerDTO;
import com.solverminds.booking.dto.v2.DangerousGoodsInfoV2;
import com.solverminds.booking.dto.v2.DgEmergencyContactV2;
import com.solverminds.booking.dto.v2.DgPackagingReferenceV2;
import com.solverminds.booking.dto.v2.ErrorMessage;
import com.solverminds.booking.dto.v2.NestedErrorMessage;
import com.svm.ecom.common.enums.YesNoFlag;
import com.svm.ecom.common.master.repo.DgPackagingMasterRepository;
import com.svm.ecom.common.master.repo.DgPackagingMasterRepository.DgPackagingRow;
import com.svm.ecom.common.master.repo.ImdgMasterRepository;
import com.svm.ecom.common.master.repo.ImdgMasterRepository.ImdgRow;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * TSHG-3000 Phase 2 DG validation for Booking V2 create/update.
 * Emits V2 {@link NestedErrorMessage} codes (DG_*); never puts PII in error detail values.
 */
@Component
public class DangerousGoodsValidator {

    public static final String DG_UNNO_INVALID = "DG_UNNO_INVALID";
    public static final String DG_UNNO_NOT_FOUND = "DG_UNNO_NOT_FOUND";
    public static final String DG_VARIANT_REQUIRED = "DG_VARIANT_REQUIRED";
    public static final String DG_CLASS_MISMATCH = "DG_CLASS_MISMATCH";
    public static final String DG_PACKAGING_NOT_FOUND = "DG_PACKAGING_NOT_FOUND";
    public static final String DG_PACKAGING_TYPE_INVALID = "DG_PACKAGING_TYPE_INVALID";
    public static final String DG_UNIT_REQUIRED = "DG_UNIT_REQUIRED";
    public static final String DG_WEIGHT_INVALID = "DG_WEIGHT_INVALID";
    public static final String DG_NEW_WEIGHT_NOT_ALLOWED = "DG_NEW_WEIGHT_NOT_ALLOWED";
    public static final String DG_FLAG_MISMATCH = "DG_FLAG_MISMATCH";
    public static final String DG_TEMPERATURE_INVALID = "DG_TEMPERATURE_INVALID";
    public static final String DG_EMERGENCY_CONTACT_INVALID = "DG_EMERGENCY_CONTACT_INVALID";

    private static final Pattern UNNO_PATTERN = Pattern.compile("^[0-9]{4}$");
    private static final Pattern COUNTRY_CODE = Pattern.compile("^[0-9]{1,4}$");
    private static final Pattern AREA_CODE = Pattern.compile("^[0-9]{1,6}$");
    private static final Pattern LOCAL_NUMBER = Pattern.compile("^[0-9]{4,12}$");

    private final ImdgMasterRepository imdgMasterRepository;
    private final DgPackagingMasterRepository dgPackagingMasterRepository;
    private final DangerousGoodsProperties properties;

    public DangerousGoodsValidator(ImdgMasterRepository imdgMasterRepository,
                                   DgPackagingMasterRepository dgPackagingMasterRepository,
                                   DangerousGoodsProperties properties) {
        this.imdgMasterRepository = imdgMasterRepository;
        this.dgPackagingMasterRepository = dgPackagingMasterRepository;
        this.properties = properties;
    }

    public Optional<ErrorMessage> validate(BookingRequestDTO request, String instance) {
        if (request == null) {
            return Optional.empty();
        }
        List<NestedErrorMessage> nested = new ArrayList<>();
        List<CommodityDTO> commodities = request.getCommodities();
        List<ContainerDTO> containers = request.getContainers();

        Map<String, Boolean> isDgByContainerNo = buildIsDgByContainerNo(containers);
        Map<Integer, Boolean> isDgByCargoNo = buildIsDgByCargoNo(containers);
        Set<String> containersWithDg = new HashSet<>();
        Set<Integer> cargoNosWithDg = new HashSet<>();

        if (commodities != null) {
            for (int i = 0; i < commodities.size(); i++) {
                CommodityDTO c = commodities.get(i);
                if (c == null) {
                    continue;
                }
                DangerousGoodsInfoV2 dgi = c.getDangerousGoods();
                boolean hasDg = hasDangerousGoodsPayload(dgi);
                if (hasDg) {
                    if (c.getContainerNo() != null && !c.getContainerNo().trim().isEmpty()) {
                        containersWithDg.add(c.getContainerNo().trim());
                    }
                    if (c.getCargoNo() != null) {
                        cargoNosWithDg.add(c.getCargoNo());
                    }
                    validateDgPayload(dgi, commodityDgPointer(i), nested);
                }

                Boolean isDg = resolveIsDg(c, isDgByContainerNo, isDgByCargoNo);
                if (Boolean.FALSE.equals(isDg) && hasDg) {
                    nested.add(new NestedErrorMessage(
                            "Dangerous goods details are present but isDG is false.",
                            "#/" + commodityDgPointer(i),
                            DG_FLAG_MISMATCH));
                }
                if (Boolean.TRUE.equals(isDg) && !hasDg && properties.isRequireDgWhenIsDg()) {
                    nested.add(new NestedErrorMessage(
                            "isDG is true but dangerousGoodsInfo is missing.",
                            "#/" + commodityPointer(i) + "/dangerousGoodsInfo",
                            DG_FLAG_MISMATCH));
                }
            }
        }

        if (containers != null) {
            for (int ci = 0; ci < containers.size(); ci++) {
                ContainerDTO container = containers.get(ci);
                if (container == null || !isYes(container.getDgFlag())) {
                    continue;
                }
                boolean matched = false;
                if (container.getContainerNo() != null
                        && containersWithDg.contains(container.getContainerNo().trim())) {
                    matched = true;
                }
                if (container.getCargoNo() != null && cargoNosWithDg.contains(container.getCargoNo())) {
                    matched = true;
                }
                if (!matched && properties.isRequireDgWhenIsDg()) {
                    nested.add(new NestedErrorMessage(
                            "isDG is true but no matching commodity has dangerousGoodsInfo.",
                            "#/containers[" + ci + "]/isDG",
                            DG_FLAG_MISMATCH));
                }
            }
        }

        if (nested.isEmpty()) {
            return Optional.empty();
        }
        ErrorMessage error = new ErrorMessage(
                "Booking data validation failed. Please review the provided data.",
                "Validation Failed",
                400);
        error.setInstance(instance);
        error.setCode("VALIDATION_FAILED");
        error.setType(ErrorMessage.TYPE_VALIDATION_FAILED);
        for (NestedErrorMessage n : nested) {
            error.addNestedError(n);
        }
        return Optional.of(error);
    }

    private void validateDgPayload(DangerousGoodsInfoV2 dgi, String basePointer, List<NestedErrorMessage> nested) {
        String unNo = trim(dgi.getUnNo());
        if (unNo == null) {
            nested.add(new NestedErrorMessage("UN number is required when dangerous goods are present.",
                    "#/" + basePointer + "/unNo", DG_UNNO_INVALID));
            return;
        }
        if (!UNNO_PATTERN.matcher(unNo).matches()) {
            nested.add(new NestedErrorMessage("UN number must be exactly 4 digits.",
                    "#/" + basePointer + "/unNo", DG_UNNO_INVALID));
            return;
        }

        List<ImdgRow> allVariants = imdgMasterRepository.findByUnNo(unNo, null);
        if (allVariants.isEmpty()) {
            nested.add(new NestedErrorMessage("UN number was not found in the current IMDG master.",
                    "#/" + basePointer + "/unNo", DG_UNNO_NOT_FOUND));
            return;
        }

        String variant = trim(dgi.getVariant());
        Set<String> distinctVariants = new HashSet<>();
        for (ImdgRow row : allVariants) {
            if (row.getVariant() != null) {
                distinctVariants.add(row.getVariant());
            }
        }
        if (distinctVariants.size() > 1 && variant == null) {
            nested.add(new NestedErrorMessage(
                    "Variant is required when the UN number has multiple IMDG variants.",
                    "#/" + basePointer + "/variant", DG_VARIANT_REQUIRED));
        }

        List<ImdgRow> matched = variant != null
                ? imdgMasterRepository.findByUnNo(unNo, variant)
                : allVariants;
        if (variant != null && matched.isEmpty()) {
            nested.add(new NestedErrorMessage("UN number was not found in the current IMDG master.",
                    "#/" + basePointer + "/unNo", DG_UNNO_NOT_FOUND));
            return;
        }

        ImdgRow master = matched.get(0);
        String imoClass = trim(dgi.getImoClass());
        if (imoClass != null && master.getImoClass() != null
                && !imoClass.equalsIgnoreCase(master.getImoClass())) {
            nested.add(new NestedErrorMessage(
                    "imoClass does not match the IMDG master class for this UN number.",
                    "#/" + basePointer + "/imoClass", DG_CLASS_MISMATCH));
        }

        validatePackaging(dgi.getOuterPacking(), basePointer + "/outerPacking", "o", nested);
        validatePackaging(dgi.getInnerPacking(), basePointer + "/innerPacking", "i", nested);

        if (dgi.getFlashPoint() != null && dgi.getFlashPointUnit() == null) {
            nested.add(new NestedErrorMessage("flashPointUnit is required when flashPoint is provided.",
                    "#/" + basePointer + "/flashPointUnit", DG_UNIT_REQUIRED));
        }

        Double net = dgi.getNetWeight();
        Double gross = dgi.getGrossWeight();
        if (net != null && net <= 0) {
            nested.add(new NestedErrorMessage("netWeight must be greater than 0.",
                    "#/" + basePointer + "/netWeight", DG_WEIGHT_INVALID));
        }
        if (gross != null && gross <= 0) {
            nested.add(new NestedErrorMessage("grossWeight must be greater than 0.",
                    "#/" + basePointer + "/grossWeight", DG_WEIGHT_INVALID));
        }
        if (net != null && gross != null && net > gross) {
            nested.add(new NestedErrorMessage("netWeight must not exceed grossWeight.",
                    "#/" + basePointer + "/netWeight", DG_WEIGHT_INVALID));
        }

        String effectiveClass = imoClass != null ? imoClass : master.getImoClass();
        if (dgi.getNetExplosiveWeight() != null && !isClass1(effectiveClass)) {
            nested.add(new NestedErrorMessage(
                    "netExplosiveWeight is only allowed for IMO class 1.x.",
                    "#/" + basePointer + "/netExplosiveWeight", DG_NEW_WEIGHT_NOT_ALLOWED));
        }

        boolean anyTemp = dgi.getSadt() != null || dgi.getControlTemperature() != null
                || dgi.getEmergencyTemperature() != null;
        if (anyTemp && dgi.getTemperatureUnit() == null) {
            nested.add(new NestedErrorMessage(
                    "temperatureUnit is required when SADT, control, or emergency temperature is provided.",
                    "#/" + basePointer + "/temperatureUnit", DG_UNIT_REQUIRED));
        }
        if (dgi.getControlTemperature() != null && dgi.getEmergencyTemperature() != null
                && dgi.getControlTemperature() > dgi.getEmergencyTemperature()) {
            nested.add(new NestedErrorMessage(
                    "controlTemperature must be less than or equal to emergencyTemperature.",
                    "#/" + basePointer + "/controlTemperature", DG_TEMPERATURE_INVALID));
        }
        if (dgi.getEmergencyTemperature() != null && dgi.getSadt() != null
                && dgi.getEmergencyTemperature() > dgi.getSadt()) {
            nested.add(new NestedErrorMessage(
                    "emergencyTemperature must be less than or equal to SADT.",
                    "#/" + basePointer + "/emergencyTemperature", DG_TEMPERATURE_INVALID));
        }

        validateEmergencyContact(dgi.getEmergencyContact(), basePointer + "/emergencyContact", nested);
    }

    private void validatePackaging(DgPackagingReferenceV2 ref, String pointer, String expectedUsage,
                                   List<NestedErrorMessage> nested) {
        if (ref == null || trim(ref.getCode()) == null) {
            return;
        }
        DgPackagingRow row = dgPackagingMasterRepository.findByCode(ref.getCode().trim());
        if (row == null) {
            nested.add(new NestedErrorMessage("Packaging code was not found in the DG packaging master.",
                    "#/" + pointer + "/code", DG_PACKAGING_NOT_FOUND));
            return;
        }
        String type = row.getPackType() != null ? row.getPackType().trim().toLowerCase() : "";
        boolean ok = "b".equals(type) || expectedUsage.equals(type);
        if (!ok) {
            nested.add(new NestedErrorMessage(
                    "Packaging code is not valid for this usage (OUTER/INNER).",
                    "#/" + pointer + "/code", DG_PACKAGING_TYPE_INVALID));
        }
    }

    private void validateEmergencyContact(DgEmergencyContactV2 ec, String pointer,
                                          List<NestedErrorMessage> nested) {
        if (ec == null) {
            return;
        }
        boolean any = trim(ec.getContactName()) != null || trim(ec.getCountryCode()) != null
                || trim(ec.getAreaCode()) != null || trim(ec.getLocalNumber()) != null;
        if (!any) {
            return;
        }
        if (trim(ec.getContactName()) == null) {
            nested.add(new NestedErrorMessage("Emergency contact name is required.",
                    "#/" + pointer + "/contactName", DG_EMERGENCY_CONTACT_INVALID));
        }
        if (trim(ec.getCountryCode()) == null || !COUNTRY_CODE.matcher(ec.getCountryCode().trim()).matches()) {
            nested.add(new NestedErrorMessage("Emergency contact country code must be digits only.",
                    "#/" + pointer + "/countryCode", DG_EMERGENCY_CONTACT_INVALID));
        }
        if (trim(ec.getAreaCode()) == null || !AREA_CODE.matcher(ec.getAreaCode().trim()).matches()) {
            nested.add(new NestedErrorMessage("Emergency contact area code must be digits only.",
                    "#/" + pointer + "/areaCode", DG_EMERGENCY_CONTACT_INVALID));
        }
        if (trim(ec.getLocalNumber()) == null || !LOCAL_NUMBER.matcher(ec.getLocalNumber().trim()).matches()) {
            nested.add(new NestedErrorMessage("Emergency contact local number must be digits only.",
                    "#/" + pointer + "/localNumber", DG_EMERGENCY_CONTACT_INVALID));
        }
    }

    public static boolean hasDangerousGoodsPayload(DangerousGoodsInfoV2 dgi) {
        if (dgi == null) {
            return false;
        }
        if (trim(dgi.getUnNo()) != null || trim(dgi.getImoClass()) != null
                || trim(dgi.getTechnicalName()) != null || trim(dgi.getVariant()) != null
                || trim(dgi.getSegregation()) != null || trim(dgi.getFullPackagingCode()) != null) {
            return true;
        }
        if (dgi.getFlashPoint() != null || dgi.getFlashPointUnit() != null) {
            return true;
        }
        if (dgi.getIsMarinePollutant() != null || dgi.getIsLimitedQuantity() != null
                || dgi.getIsExceptedQuantity() != null || dgi.getIsReportableQuantity() != null
                || dgi.getIsWaste() != null) {
            return true;
        }
        if (dgi.getGrossWeight() != null || dgi.getNetWeight() != null
                || dgi.getNetExplosiveWeight() != null
                || dgi.getOuterPackingQuantity() != null || dgi.getInnerPackingQuantity() != null) {
            return true;
        }
        if (dgi.getSadt() != null || dgi.getControlTemperature() != null
                || dgi.getEmergencyTemperature() != null || dgi.getTemperatureUnit() != null) {
            return true;
        }
        if (dgi.getEmergencyContact() != null) {
            DgEmergencyContactV2 ec = dgi.getEmergencyContact();
            if (trim(ec.getContactName()) != null || trim(ec.getCountryCode()) != null
                    || trim(ec.getAreaCode()) != null || trim(ec.getLocalNumber()) != null) {
                return true;
            }
        }
        if (dgi.getOuterPacking() != null && trim(dgi.getOuterPacking().getCode()) != null) {
            return true;
        }
        return dgi.getInnerPacking() != null && trim(dgi.getInnerPacking().getCode()) != null;
    }

    private static boolean isClass1(String imoClass) {
        if (imoClass == null || imoClass.trim().isEmpty()) {
            return false;
        }
        String c = imoClass.trim();
        return c.startsWith("1");
    }

    private static boolean isYes(YesNoFlag flag) {
        return flag == YesNoFlag.Y;
    }

    private static Boolean resolveIsDg(CommodityDTO c,
                                       Map<String, Boolean> byContainerNo,
                                       Map<Integer, Boolean> byCargoNo) {
        if (c.getContainerNo() != null && byContainerNo.containsKey(c.getContainerNo().trim())) {
            return byContainerNo.get(c.getContainerNo().trim());
        }
        if (c.getCargoNo() != null && byCargoNo.containsKey(c.getCargoNo())) {
            return byCargoNo.get(c.getCargoNo());
        }
        return null;
    }

    private static Map<String, Boolean> buildIsDgByContainerNo(List<ContainerDTO> containers) {
        Map<String, Boolean> map = new HashMap<>();
        if (containers == null) {
            return map;
        }
        for (ContainerDTO c : containers) {
            if (c == null || c.getContainerNo() == null || c.getContainerNo().trim().isEmpty()) {
                continue;
            }
            if (c.getDgFlag() != null) {
                map.put(c.getContainerNo().trim(), isYes(c.getDgFlag()));
            }
        }
        return map;
    }

    private static Map<Integer, Boolean> buildIsDgByCargoNo(List<ContainerDTO> containers) {
        Map<Integer, Boolean> map = new HashMap<>();
        if (containers == null) {
            return map;
        }
        for (ContainerDTO c : containers) {
            if (c == null || c.getCargoNo() == null) {
                continue;
            }
            if (c.getDgFlag() != null) {
                map.put(c.getCargoNo(), isYes(c.getDgFlag()));
            }
        }
        return map;
    }

    private static String commodityPointer(int index) {
        return "containers[*]/commodities[" + index + "]";
    }

    /**
     * Prefer container-indexed path when possible; commodities are flat on V1 DTO so use commodities[i].
     */
    private static String commodityDgPointer(int commodityIndex) {
        return "commodities[" + commodityIndex + "]/dangerousGoodsInfo";
    }

    private static String trim(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        return s.trim();
    }
}
