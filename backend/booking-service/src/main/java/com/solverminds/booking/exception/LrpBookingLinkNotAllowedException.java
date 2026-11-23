package com.solverminds.booking.exception;

/**
 * Thrown when an LRP booking cannot be linked for ECOM update (e.g. status is not Draft).
 */
public class LrpBookingLinkNotAllowedException extends RuntimeException {

    private final String lrpStatus;

    public LrpBookingLinkNotAllowedException(String lrpStatus, String message) {
        super(message);
        this.lrpStatus = lrpStatus;
    }

    public String getLrpStatus() {
        return lrpStatus;
    }
}
