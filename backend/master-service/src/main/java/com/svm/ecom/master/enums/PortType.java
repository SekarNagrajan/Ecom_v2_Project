package com.svm.ecom.master.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Port type enumeration.
 * P – Port , I – Inland
 */
@Schema(description = "Port type: P – Port, I – Inland", enumAsRef = true)
public enum PortType {
    
    @Schema(description = "Port")
    P("P"),

    @Schema(description = "Inland")
    I("I");

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
            case P: return "Port";
            case I: return "Inland";
            default: return value;
        }
    }

    /**
     * Create enum from string value
     */
    public static PortType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (PortType type : PortType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid PortType value: " + value + ". Valid values are: P (Port), I (Inland)");
    }

}
