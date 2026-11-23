package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Freight payment type enumeration.
 * P = Prepaid, C = Collect
 */
@Schema(description = "Freight Payment Type: P – Prepaid, C – Collect", enumAsRef = true)
public enum FreightPaymentType {

    @Schema(description = "P – Prepaid")
    P("P"),

    @Schema(description = "C – Collect")
    C("C");

    private final String value;

    FreightPaymentType(String value) {
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
        return this == P ? "Prepaid" : "Collect";
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static FreightPaymentType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (FreightPaymentType type : FreightPaymentType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid FreightPaymentType value: " + value + ". Valid values are: P (Prepaid), C (Collect)");
    }
}

