package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Bound/Direction enumeration.
 * W = West, E = East, N = North, S = South
 */
@Schema(description = "Bound direction: W – West, E – East, N – North, S – South", enumAsRef = true)
public enum Bound {

    @Schema(description = "W – West")
    W("W"),

    @Schema(description = "E – East")
    E("E"),

    @Schema(description = "N – North")
    N("N"),

    @Schema(description = "S – South")
    S("S");

    private final String value;

    Bound(String value) {
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
            case W: return "West";
            case E: return "East";
            case N: return "North";
            case S: return "South";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static Bound fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (Bound bound : Bound.values()) {
            if (bound.value.equalsIgnoreCase(value)) {
                return bound;
            }
        }
        throw new IllegalArgumentException("Invalid Bound value: " + value + ". Valid values are: W (West), E (East), N (North), S (South)");
    }
}

