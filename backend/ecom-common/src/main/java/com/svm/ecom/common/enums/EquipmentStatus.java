package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Equipment load status enumeration.
 * F = Full (Laden), M = Empty
 */
@Schema(description = "Equipment Status: F – Full (Laden), M – Empty", enumAsRef = true)
public enum EquipmentStatus {

    @Schema(description = "F – Full (Laden)")
    F("F"),

    @Schema(description = "M – Empty")
    M("M");

    private final String value;

    EquipmentStatus(String value) {
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
        return this == F ? "Full (Laden)" : "Empty";
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static EquipmentStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (EquipmentStatus status : EquipmentStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid EquipmentStatus value: " + value + ". Valid values are: F (Full/Laden), M (Empty)");
    }
}

