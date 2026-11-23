package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Booking status enumeration.
 * D = ECOM Draft, S = Submitted, P = In-Progress, C = Confirmed, L = Cancelled, V = Rejected, T = Template, I = Void
 */
@Schema(description = "Booking Status: D – ECOM Draft, S – Submitted, P – In-Progress (ERP Draft), C – Confirmed (ERP Confirmed), L – Cancelled, V – Rejected, T – Template, I – Void", enumAsRef = true)
public enum BookingStatus {

    @Schema(description = "D – ECOM Draft")
    D("D"),

    @Schema(description = "S – Submitted")
    S("S"),

    @Schema(description = "P – In-Progress (ERP Draft Booking)")
    P("P"),

    @Schema(description = "C – Confirmed (ERP Confirmed Booking)")
    C("C"),

    @Schema(description = "L – Cancelled (ERP Cancelled Booking)")
    L("L"),

    @Schema(description = "V – Rejected (Declined in Ecommerce Validator)")
    V("V"),

    @Schema(description = "T – Template")
    T("T"),

    @Schema(description = "I – Void")
    I("I");

    private final String value;

    BookingStatus(String value) {
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
            case D: return "ECOM Draft";
            case S: return "Submitted";
            case P: return "In-Progress (ERP Draft Booking)";
            case C: return "Confirmed (ERP Confirmed Booking)";
            case L: return "Cancelled (ERP Cancelled Booking)";
            case V: return "Rejected (Declined in Ecommerce Validator)";
            case T: return "Template";
            case I: return "Void";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static BookingStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (BookingStatus status : BookingStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid BookingStatus value: " + value + 
            ". Valid values are: D (Draft), S (Submitted), P (In-Progress), C (Confirmed), L (Cancelled), V (Rejected), T (Template), I (Void)");
    }
}

