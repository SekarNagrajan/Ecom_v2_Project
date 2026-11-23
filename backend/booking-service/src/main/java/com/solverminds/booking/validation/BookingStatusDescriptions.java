package com.solverminds.booking.validation;

/**
 * Human-readable ECOM status labels (shared by view errors and V2-aligned messages).
 */
final class BookingStatusDescriptions {

    private BookingStatusDescriptions() {
    }

    static String describe(String status) {
        if (status == null) {
            return "Unknown";
        }
        switch (status.toUpperCase()) {
            case "D":
                return "Draft";
            case "S":
                return "Submitted";
            case "T":
                return "Template";
            case "V":
                return "Rejected";
            case "C":
                return "Confirmed";
            case "P":
                return "In-Progress";
            case "A":
                return "Accepted";
            case "I":
                return "Void";
            default:
                return "Unknown";
        }
    }
}
