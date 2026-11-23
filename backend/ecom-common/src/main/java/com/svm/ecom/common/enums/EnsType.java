package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ENS/Documentation Type enumeration.
 * S = Straight B/L, C = Consolidated
 */
@Schema(description = "ENS/Documentation Type: S – Straight B/L, C – Consolidated", enumAsRef = true)
public enum EnsType {

    @Schema(description = "S – Straight B/L")
    S("S"),

    @Schema(description = "C – Consolidated")
    C("C");

    private final String value;

    EnsType(String value) {
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
        return this == S ? "Straight B/L" : "Consolidated";
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static EnsType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (EnsType type : EnsType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid EnsType value: " + value + ". Valid values are: S (Straight B/L), C (Consolidated)");
    }
}

