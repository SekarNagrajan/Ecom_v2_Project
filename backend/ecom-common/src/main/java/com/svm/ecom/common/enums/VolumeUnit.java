package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Volume unit enumeration.
 * M = Cubic Meter, F = Cubic Feet
 */
@Schema(description = "Volume Unit: M – Cubic Meter, F – Cubic Feet", enumAsRef = true)
public enum VolumeUnit {

    @Schema(description = "M – Cubic Meter")
    M("M"),

    @Schema(description = "F – Cubic Feet")
    F("F");

    private final String value;

    VolumeUnit(String value) {
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
        switch (this) {
            case M: return "Cubic Meter";
            case F: return "Cubic Feet";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static VolumeUnit fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (VolumeUnit unit : VolumeUnit.values()) {
            if (unit.value.equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Invalid VolumeUnit value: " + value + ". Valid values are: M (Cubic Meter), F (Cubic Feet)");
    }
}

