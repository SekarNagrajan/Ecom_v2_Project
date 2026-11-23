package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Leg type enumeration for route/schedule legs.
 * P = Pre-carriage, M = Main leg, O = On-carriage
 */
@Schema(description = "Leg Type: P – Pre-carriage, M – Main leg, O – On-carriage", enumAsRef = true)
public enum LegType {

    @Schema(description = "P – Pre-carriage")
    P("P"),

    @Schema(description = "M – Main leg")
    M("M"),

    @Schema(description = "O – On-carriage")
    O("O");

    private final String value;

    LegType(String value) {
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
            case P: return "Pre-carriage";
            case M: return "Main leg";
            case O: return "On-carriage";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static LegType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (LegType type : LegType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid LegType value: " + value + ". Valid values are: P (Pre-carriage), M (Main leg), O (On-carriage)");
    }
}

