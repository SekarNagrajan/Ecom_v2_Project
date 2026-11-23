package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Port type enumeration.
 * S = Seaport, I = Inland/ICD, A = Airport
 */
@Schema(description = "Port Type: S – Seaport, I – Inland/ICD, A – Airport", enumAsRef = true)
public enum PortType {

    @Schema(description = "S – Seaport")
    S("S"),

    @Schema(description = "I – Inland/ICD")
    I("I"),

    @Schema(description = "A – Airport")
    A("A");

    private final String value;

    PortType(String value) {
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
            case S: return "Seaport";
            case I: return "Inland/ICD";
            case A: return "Airport";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static PortType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (PortType type : PortType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid PortType value: " + value + ". Valid values are: S (Seaport), I (Inland/ICD), A (Airport)");
    }
}

