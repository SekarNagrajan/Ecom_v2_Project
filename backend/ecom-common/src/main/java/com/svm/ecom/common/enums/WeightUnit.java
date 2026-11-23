package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Weight unit enumeration.
 * K = Kilos, T = Metric Tonnes
 */
@Schema(name = "WeightUnit", description = "Weight Unit: K – Kilos, T – Metric Tonnes", enumAsRef = true, example = "K")
public enum WeightUnit {

    @Schema(description = "K – Kilos")
    K("K"),

    @Schema(description = "T – Metric Tonnes")
    T("T");

    private final String value;

    WeightUnit(String value) {
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
        return this == K ? "Kilos" : "Metric Tonnes";
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static WeightUnit fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (WeightUnit unit : WeightUnit.values()) {
            if (unit.value.equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Invalid WeightUnit value: " + value + ". Valid values are: K (Kilos), T (Metric Tonnes)");
    }
}

