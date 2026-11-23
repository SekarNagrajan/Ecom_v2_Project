package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * SI (Shipping Instruction) status enumeration.
 * D = Draft, S = Submitted, A = Accepted, V = Declined
 */
@Schema(description = "SI Status: D – Draft, S – Submitted, A – Accepted, V – Declined", enumAsRef = true)
public enum SiStatus {

    @Schema(description = "D – Draft")
    D("D"),

    @Schema(description = "S – Submitted")
    S("S"),

    @Schema(description = "A – Accepted")
    A("A"),

    @Schema(description = "V – Declined")
    V("V");

    private final String value;

    SiStatus(String value) {
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
            case S: return "Submitted";
            case A: return "Accepted";
            case V: return "Declined";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     * Accepts both single-letter codes (D, S, A, V) and display names (Draft, Submitted, Accepted, Declined), case-insensitive.
     */
    @JsonCreator
    public static SiStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String v = value.trim();
        // Map display names to enum (same approach as PortPair schedule_type)
        if ("Draft".equalsIgnoreCase(v)) {
            return D;
        }
        if ("Submitted".equalsIgnoreCase(v)) {
            return S;
        }
        if ("Accepted".equalsIgnoreCase(v)) {
            return A;
        }
        if ("Declined".equalsIgnoreCase(v)) {
            return V;
        }
        // For standard values (D, S, A, V), match by enum value
        for (SiStatus status : SiStatus.values()) {
            if (status.value.equalsIgnoreCase(v)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid SiStatus value: " + value + ". Valid values are: D (Draft), S (Submitted), A (Accepted), V (Declined)");
    }
}

