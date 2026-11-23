package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Length/Dimension unit enumeration.
 * F = Feet, M = Meters
 */
@Schema(description = "Length Unit: F – Feet, M – Meters", enumAsRef = true)
public enum LengthUnit {

    @Schema(description = "F – Feet")
    F("F"),

    @Schema(description = "M – Meters")
    M("M");

    private final String value;

    LengthUnit(String value) {
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
        return this == F ? "Feet" : "Meters";
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static LengthUnit fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (LengthUnit unit : LengthUnit.values()) {
            if (unit.value.equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Invalid LengthUnit value: " + value + ". Valid values are: F (Feet), M (Meters)");
    }
}

