package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Ecom print status enumeration.
 * 0 = Disable, 1 = Enable
 */
@Schema(description = "Ecom print status: 0 – Disable, 1 – Enable", enumAsRef = true)
public enum EcomPrintStatus {

    @Schema(description = "0 – Disable")
    D("0"),

    @Schema(description = "1 – Enable")
    E("1");

    private final String value;

    EcomPrintStatus(String value) {
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
            case D: return "Disable";
            case E: return "Enable";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static EcomPrintStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (EcomPrintStatus status : EcomPrintStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid EcomPrintStatus value: " + value + ". Valid values are: 0 (Disable), 1 (Enable)");
    }
}

