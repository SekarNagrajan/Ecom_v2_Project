package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Booking source enumeration.
 * C = ECOM, E = EDI, T = LRP
 */
@Schema(description = "Booking Source: C – ECOM, E – EDI, T – LRP", enumAsRef = true)
public enum BookingSource {

    @Schema(description = "C – ECOM")
    C("C"),

    @Schema(description = "E – EDI")
    E("E"),

    @Schema(description = "T – LRP")
    T("T");

    private final String value;

    BookingSource(String value) {
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
            case C: return "ECOM";
            case E: return "EDI";
            case T: return "LRP";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static BookingSource fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (BookingSource source : BookingSource.values()) {
            if (source.value.equalsIgnoreCase(value)) {
                return source;
            }
        }
        throw new IllegalArgumentException("Invalid BookingSource value: " + value +
            ". Valid values are: C (ECOM), E (EDI), T (LRP)");
    }
}
