package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * BL (Bill of Lading) status enumeration.
 * D = Draft, C = Confirmed, I = Issued, V = Cancelled
 */
@Schema(description = "BL Status: D – Draft, C – Confirmed, I – Issued, V – Cancelled", enumAsRef = true)
public enum BlStatus {

    @Schema(description = "D – Draft")
    D("D"),

    @Schema(description = "C – Confirmed")
    C("C"),

    @Schema(description = "I – Issued")
    I("I"),

    @Schema(description = "V – Cancelled")
    V("V");

    private final String value;

    BlStatus(String value) {
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
            case D: return "Draft";
            case C: return "Confirmed";
            case I: return "Issued";
            case V: return "Cancelled";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static BlStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (BlStatus status : BlStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid BlStatus value: " + value + ". Valid values are: D (Draft), C (Confirmed), I (Issued), V (Cancelled)");
    }
}

