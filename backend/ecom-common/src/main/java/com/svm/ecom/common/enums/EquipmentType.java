package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Equipment/Container type enumeration.
 * D = Dry, R = Reefer
 */
@Schema(description = "Equipment Type: D – Dry, R – Reefer", enumAsRef = true)
public enum EquipmentType {

    @Schema(description = "D – Dry")
    D("D"),

    @Schema(description = "R – Reefer")
    R("R");

    private final String value;

    EquipmentType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getDescription() {
        return this == D ? "Dry" : "Reefer";
    }

    /**
     * Value to persist in database. D (Dry) is stored as N in DB; R remains R.
     */
    public String getDbValue() {
        return this == D ? "N" : value;
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     * DB stores "N" for Dry; map to D when reading.
     */
    @JsonCreator
    public static EquipmentType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String v = value.trim();
        // DB stores "N" for Dry; expose as D in API
        if ("N".equalsIgnoreCase(v)) {
            return D;
        }
        for (EquipmentType type : EquipmentType.values()) {
            if (type.value.equalsIgnoreCase(v)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid EquipmentType value: " + value + ". Valid values are: D (Dry), R (Reefer)");
    }
}

