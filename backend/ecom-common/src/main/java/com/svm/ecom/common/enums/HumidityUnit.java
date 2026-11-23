package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Humidity unit enumeration.
 * PERCENT = %, ABS = Absolute
 */
@Schema(description = "Humidity Unit: % – Percentage, ABS – Absolute", enumAsRef = true)
public enum HumidityUnit {

    @Schema(description = "% – Percentage")
    PERCENT("%"),

    @Schema(description = "ABS – Absolute")
    ABS("ABS");

    private final String value;

    HumidityUnit(String value) {
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
        return this == PERCENT ? "Percentage" : "Absolute";
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static HumidityUnit fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (HumidityUnit unit : HumidityUnit.values()) {
            if (unit.value.equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Invalid HumidityUnit value: " + value + ". Valid values are: % (Percentage), ABS (Absolute)");
    }
}

