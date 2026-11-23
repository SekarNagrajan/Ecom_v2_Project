package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ENS (Entry Summary Declaration) Filing type enumeration.
 * S = Single Filing, P = Partial Filing, N = NA
 */
@Schema(description = "ENS Filing Type: S – Single Filing, P – Partial Filing, N – NA", enumAsRef = true)
public enum EnsFillingType {

    @Schema(description = "S – Single Filing")
    S("S"),

    @Schema(description = "P – Partial Filing")
    P("P"),

    @Schema(description = "N – NA")
    N("N");

    private final String value;

    EnsFillingType(String value) {
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
            case S: return "Single Filing";
            case P: return "Partial Filing";
            case N: return "NA";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static EnsFillingType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (EnsFillingType type : EnsFillingType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid EnsFillingType value: " + value + ". Valid values are: S (Single Filing), P (Partial Filing), N (NA)");
    }
}

