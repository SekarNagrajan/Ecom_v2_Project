package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Temperature unit enumeration.
 * C = Celsius, F = Fahrenheit
 */
@Schema(description = "Temperature Unit: C – Celsius, F – Fahrenheit", enumAsRef = true)
public enum TemperatureUnit {

    @Schema(description = "C – Celsius")
    C("C"),

    @Schema(description = "F – Fahrenheit")
    F("F");

    private final String value;

    TemperatureUnit(String value) {
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
        return this == C ? "Celsius" : "Fahrenheit";
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static TemperatureUnit fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (TemperatureUnit unit : TemperatureUnit.values()) {
            if (unit.value.equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Invalid TemperatureUnit value: " + value + ". Valid values are: C (Celsius), F (Fahrenheit)");
    }
}

