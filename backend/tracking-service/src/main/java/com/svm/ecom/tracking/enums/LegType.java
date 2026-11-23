package com.svm.ecom.tracking.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Leg type enumeration.
 * R – Rail, F – Feeder, C – Combined, M – Mainline, T – Road, B – Barge
 */ 
@Schema(description = "Leg type: F – Feeder, R – Rail, C – Combined, M – Mainline, T – Road, B – Barge", enumAsRef = true)
public enum LegType {

    @Schema(description = "Feeder")
    F("F"),

    @Schema(description = "Rail")
    R("R"),

    @Schema(description = "Combined")
    C("C"),

    @Schema(description = "Mainline")
    M("M"),

    @Schema(description = "Road")
    T("T"),

    @Schema(description = "Barge")
    B("B");

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
            case F: return "Feeder";
            case R: return "Rail";
            case C: return "Combined";
            case M: return "Mainline";
            case T: return "Road";
            case B: return "Barge";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Used by Jackson for JSON deserialization.
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
        throw new IllegalArgumentException("Invalid LegType value: " + value + ". Valid values are: F (Feeder), R (Rail), C (Combined), M (Mainline), T (Road), B (Barge)");
    }
}

