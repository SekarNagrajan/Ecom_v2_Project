package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Carriage type enumeration (for pre-carriage and on-carriage).
 * C = Carrier, M = Merchant, N = Not Applicable
 */
@Schema(description = "Carriage Type: C – Carrier, M – Merchant, N – N/A", enumAsRef = true)
public enum CarriageType {

    @Schema(description = "C – Carrier")
    C("C"),

    @Schema(description = "M – Merchant")
    M("M"),

    @Schema(description = "N – N/A")
    N("N");

    private final String value;

    CarriageType(String value) {
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
            case C: return "Carrier";
            case M: return "Merchant";
            case N: return "Not Applicable";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static CarriageType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (CarriageType type : CarriageType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid CarriageType value: " + value + ". Valid values are: C (Carrier), M (Merchant), N (Not Applicable)");
    }
}

