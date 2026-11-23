package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Ventilation unit enumeration.
 * CBM = Cubic Meters, CFT = Cubic Feet
 */
@Schema(description = "Ventilation Unit: CBM – Cubic Meters, CFT – Cubic Feet", enumAsRef = true)
public enum VentilationUnit {

    @Schema(description = "CBM – Cubic Meters")
    CBM("CBM"),

    @Schema(description = "CFT – Cubic Feet")
    CFT("CFT");

    private final String value;

    VentilationUnit(String value) {
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
        return this == CBM ? "Cubic Meters" : "Cubic Feet";
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static VentilationUnit fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (VentilationUnit unit : VentilationUnit.values()) {
            if (unit.value.equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Invalid VentilationUnit value: " + value + ". Valid values are: CBM (Cubic Meters), CFT (Cubic Feet)");
    }
}

