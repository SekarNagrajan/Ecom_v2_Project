package com.svm.ecom.schedule.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Port call type enumeration.
 * L = Local, T = Transshipment
 */
@Schema(description = "Port Call Type: L – Local, T – Transshipment", enumAsRef = true)
public enum PortCallType {

    @Schema(description = "Local")
    L("L"),

    @Schema(description = "Transshipment")
    T("T");

    private final String value;

    PortCallType(String value) {
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
            case L: return "Local";
            case T: return "Transshipment";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static PortCallType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (PortCallType type : PortCallType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid PortCallType value: " + value + ". Valid values are: L (Local), T (Transshipment)");
    }
}

