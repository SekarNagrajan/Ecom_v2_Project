package com.svm.ecom.carrier.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Call status for long range schedule rows.
 * S = Sailed/Confirmed Call, N = Normal call, D = Omitted call
 */
@Schema(description = "Call status: S – Sailed/Confirmed Call, N – Normal call, D – Omitted call", enumAsRef = true)
public enum CallStatus {

    @Schema(description = "S – Sailed/Confirmed Call")
    S("S"),

    @Schema(description = "N – Normal call")
    N("N"),

    @Schema(description = "D – Omitted call")
    D("D");

    private final String value;

    CallStatus(String value) {
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

    /**
     * Create enum from live/API string. Empty becomes null.
     * Also accepts longer live labels such as "Sailed/Confirmed Call".
     */
    @JsonCreator
    public static CallStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String trimmed = value.trim();
        for (CallStatus status : CallStatus.values()) {
            if (status.value.equalsIgnoreCase(trimmed)) {
                return status;
            }
        }
        String lower = trimmed.toLowerCase();
        if (lower.contains("omitted")) {
            return D;
        }
        if (lower.contains("normal")) {
            return N;
        }
        if (lower.contains("sailed") || lower.contains("confirmed")) {
            return S;
        }
        throw new IllegalArgumentException(
                "Invalid CallStatus value: " + value + ". Valid values are: S, N, D");
    }
}
