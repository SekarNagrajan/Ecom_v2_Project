package com.solverminds.booking.util;

import com.solverminds.booking.dto.ReeferSettingDTO;
import com.solverminds.booking.dto.v2.ReeferContainerSettingsV2;
import com.solverminds.booking.entity.EcomBookingReefer;
import com.solverminds.edi.entity.EdiTempBookingCargo;
import com.svm.ecom.common.enums.TemperatureUnit;
import com.svm.ecom.common.enums.YesNoFlag;

/**
 * SI-aligned reefer field mapping shared across create, view, and EDI export.
 * Mirrors SIBLReeferDTO / SiCreateService.createReeferRecord semantics.
 */
public final class ReeferMappingHelper {

    private ReeferMappingHelper() {
    }

    /**
     * Populate {@link EcomBookingReefer} from API DTO and parent container identifiers.
     */
    public static void populateEntity(EcomBookingReefer entity, ReeferSettingDTO dto,
                                      Integer conid, String containerNo) {
        entity.setConid(conid);
        entity.setContainerno(containerNo);
        entity.setItemno(1);

        if (dto == null) {
            return;
        }

        if (dto.getSetTemp() != null) {
            entity.setRefSetTemp(dto.getSetTemp().floatValue());
        }
        if (dto.getMinTemp() != null && !Double.isNaN(dto.getMinTemp())) {
            entity.setRefMinTemp(dto.getMinTemp().floatValue());
        }
        if (dto.getMaxTemp() != null && !Double.isNaN(dto.getMaxTemp())) {
            entity.setRefMaxTemp(dto.getMaxTemp().floatValue());
        }
        entity.setRefTempUnit(dto.getTempUnit() != null ? dto.getTempUnit().getValue() : null);

        if (dto.getHumidity() != null) {
            entity.setHumidity(dto.getHumidity().toString());
        }
        if (dto.getHumidityFlag() != null) {
            entity.setHumidityFlag(dto.getHumidityFlag() == YesNoFlag.Y);
        }
        if (dto.getDrains() != null && !dto.getDrains().trim().isEmpty()) {
            entity.setDrains(dto.getDrains());
        }
        if (dto.getO2Value() != null) {
            entity.setO2Value(dto.getO2Value().toString());
        }
        if (dto.getCo2Value() != null) {
            entity.setCo2Value(dto.getCo2Value().toString());
        }
        if (dto.getVentilation() != null && !dto.getVentilation().trim().isEmpty()) {
            entity.setVentPercentage(dto.getVentilation().trim());
        }
    }

    /**
     * Populate {@link ReeferSettingDTO} from persisted entity (Booking View response mapping).
     * Internal linkage keys (cargoNo, containerNo, itemNo) are omitted from the API payload.
     */
    public static ReeferSettingDTO toViewDto(EcomBookingReefer entity) {
        if (entity == null) {
            return null;
        }
        ReeferSettingDTO dto = new ReeferSettingDTO();

        if (entity.getRefSetTemp() != null) {
            dto.setSetTemp(entity.getRefSetTemp().intValue());
        }
        if (entity.getRefMinTemp() != null) {
            dto.setMinTemp(entity.getRefMinTemp().doubleValue());
        }
        if (entity.getRefMaxTemp() != null) {
            dto.setMaxTemp(entity.getRefMaxTemp().doubleValue());
        }
        if (entity.getRefTempUnit() != null && !entity.getRefTempUnit().trim().isEmpty()) {
            dto.setTempUnit(TemperatureUnit.fromValue(entity.getRefTempUnit()));
        }
        if (entity.getHumidity() != null && !entity.getHumidity().isEmpty()) {
            Double humidity = parseDoubleSafe(entity.getHumidity());
            if (humidity != null) {
                dto.setHumidity(humidity);
            }
        }
        if (entity.getHumidityFlag() != null) {
            dto.setHumidityFlag(Boolean.TRUE.equals(entity.getHumidityFlag()) ? YesNoFlag.Y : YesNoFlag.N);
        }
        if (entity.getDrains() != null && !entity.getDrains().isEmpty()) {
            dto.setDrains(entity.getDrains());
        }
        if (entity.getO2Value() != null && !entity.getO2Value().isEmpty()) {
            Double o2 = parseDoubleSafe(entity.getO2Value());
            if (o2 != null) {
                dto.setO2Value(o2);
            }
        }
        if (entity.getCo2Value() != null && !entity.getCo2Value().isEmpty()) {
            Double co2 = parseDoubleSafe(entity.getCo2Value());
            if (co2 != null) {
                dto.setCo2Value(co2);
            }
        }
        mapVentPercentageToDto(entity.getVentPercentage(), dto);
        return dto;
    }

    /**
     * Apply reefer settings to EDI temp cargo (SI-aligned export).
     */
    public static void populateEdiCargo(EdiTempBookingCargo edi, ReeferSettingDTO reefer) {
        if (edi == null) {
            return;
        }
        if (reefer == null) {
            edi.setVentSetting(0);
            return;
        }
        if (reefer.getSetTemp() != null) {
            edi.setSetTemp(reefer.getSetTemp().floatValue());
        }
        if (reefer.getMinTemp() != null) {
            edi.setTempMin(reefer.getMinTemp().floatValue());
        }
        if (reefer.getMaxTemp() != null) {
            edi.setTempMax(reefer.getMaxTemp().floatValue());
        }
        edi.setTempUnit(reefer.getTempUnit() != null ? reefer.getTempUnit().getValue() : null);
        if (reefer.getHumidity() != null) {
            edi.setHumidity(reefer.getHumidity().toString());
        }
        if (reefer.getHumidityFlag() != null) {
            edi.setHumidityFlag(reefer.getHumidityFlag() == YesNoFlag.Y);
        }
        if (reefer.getDrains() != null && !reefer.getDrains().trim().isEmpty()) {
            edi.setDrains(reefer.getDrains());
        }
        if (reefer.getCo2Value() != null) {
            edi.setCo2Value(reefer.getCo2Value().toString());
        }
        if (reefer.getO2Value() != null) {
            edi.setO2Value(reefer.getO2Value().toString());
        }
        if (reefer.getVentilation() != null && !reefer.getVentilation().trim().isEmpty()) {
            edi.setVentPercentage(reefer.getVentilation().trim());
        }
        edi.setVentSetting(resolveVentSetting(reefer));
    }

    /**
     * Resolve ventSetting for editempbooking_cargo: use request ventSet when provided,
     * otherwise derive from ventilation presence (1 = enabled, 0 = disabled).
     */
    private static Integer resolveVentSetting(ReeferSettingDTO reefer) {
        if (reefer.getVentSet() != null) {
            return reefer.getVentSet().intValue();
        }
        return reefer.getVentilation() != null && !reefer.getVentilation().trim().isEmpty() ? 1 : 0;
    }

    public static ReeferSettingDTO toV1Reefer(ReeferContainerSettingsV2 v2) {
        if (v2 == null) {
            return null;
        }
        ReeferSettingDTO reefer = new ReeferSettingDTO();
        if (v2.getTemperature() != null) {
            reefer.setSetTemp(v2.getTemperature().intValue());
        }
        reefer.setTempUnit(v2.getTemperatureUnit());
        reefer.setMinTemp(v2.getMinTemp());
        reefer.setMaxTemp(v2.getMaxTemp());
        reefer.setVentilation(v2.getVentilation());
        reefer.setVentSet(v2.getVentSet() != null ? v2.getVentSet().intValue() : null);
        reefer.setHumidity(v2.getHumidity());
        reefer.setHumidityFlag(v2.getHumidityFlag());
        reefer.setDrains(v2.getDrains());
        reefer.setO2Value(v2.getO2Value());
        reefer.setCo2Value(v2.getCo2Value());
        return reefer;
    }

    public static ReeferContainerSettingsV2 toV2Reefer(ReeferSettingDTO v1) {
        if (v1 == null) {
            return null;
        }
        ReeferContainerSettingsV2 reefer = new ReeferContainerSettingsV2();
        if (v1.getSetTemp() != null) {
            reefer.setTemperature(v1.getSetTemp().doubleValue());
        }
        reefer.setTemperatureUnit(v1.getTempUnit());
        reefer.setMinTemp(v1.getMinTemp());
        reefer.setMaxTemp(v1.getMaxTemp());
        reefer.setVentilation(v1.getVentilation());
        reefer.setVentSet(v1.getVentSet() != null ? v1.getVentSet().doubleValue() : null);
        reefer.setHumidity(v1.getHumidity());
        reefer.setHumidityFlag(v1.getHumidityFlag());
        reefer.setDrains(v1.getDrains());
        reefer.setO2Value(v1.getO2Value());
        reefer.setCo2Value(v1.getCo2Value());
        return reefer;
    }

    /**
     * View mapping for tables without ventsetting (e.g. ecombooking_refer):
     * ventilation ← VentPercentage; ventSet derived from ventilation presence.
     */
    private static void mapVentPercentageToDto(String ventPercentage, ReeferSettingDTO dto) {
        if (ventPercentage == null || ventPercentage.trim().isEmpty()) {
            dto.setVentSet(0);
            return;
        }
        dto.setVentilation(ventPercentage.trim());
        dto.setVentSet(1);
    }

    public static Double parseDoubleSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
