package com.svm.ecom.schedule.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Transport mode enumeration.
 * T = Road, R = Rail, M = Mainline, B = Barge, F = Feeder, C = Combined, I = ITT
 */ 
@Schema(description = "Transport Mode: T – ROAD, R – RAIL, M – MAINLINE, B – BARGE, F – FEEDER, C – COMBINED, I – ITT", enumAsRef = true)
public enum TransportMode {

    @Schema(description = "ROAD")
    T("T"),

    @Schema(description = "RAIL")
    R("R"),

    @Schema(description = "MAINLINE")
    M("M"),

    @Schema(description = "BARGE")
    B("B"),

    @Schema(description = "FEEDER")
    F("F"),

    @Schema(description = "COMBINED")
    C("C"),

    @Schema(description = "ITT")
    I("I");

    private final String value;

    TransportMode(String value) {
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
            case T: return "ROAD";
            case R: return "RAIL";
            case M: return "MAINLINE";
            case B: return "BARGE";
            case F: return "FEEDER";
            case C: return "COMBINED";
            case I: return "ITT";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     * Accepts both code (T, R, M, etc.) and description (Road, Rail, Mainline, etc.)
     */
    @JsonCreator
    public static TransportMode fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String trimmedValue = value.trim();
        for (TransportMode mode : TransportMode.values()) {
            // Check against code (T, R, M, etc.)
            if (mode.value.equalsIgnoreCase(trimmedValue)) {
                return mode;
            }
            // Check against description (ROAD, RAIL, MAINLINE, etc.)
            if (mode.getDescription().equalsIgnoreCase(trimmedValue)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Invalid TransportMode value: " + value + ". Valid values are: T (ROAD), R (RAIL), M (MAINLINE), B (BARGE), F (FEEDER), C (COMBINED), I (ITT)");
    }
}

