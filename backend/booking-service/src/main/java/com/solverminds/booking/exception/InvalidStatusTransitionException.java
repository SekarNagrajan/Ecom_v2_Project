package com.solverminds.booking.exception;

/**
 * Exception thrown when an invalid booking status transition is attempted.
 * Returns 422 Unprocessable Entity when caught by the controller.
 */
public class InvalidStatusTransitionException extends RuntimeException {

    private final String currentStatus;
    private final String lrpStatus;
    private final String requestedStatus;

    public InvalidStatusTransitionException(String currentStatus, String lrpStatus, String requestedStatus, String message) {
        super(message);
        this.currentStatus = currentStatus;
        this.lrpStatus = lrpStatus;
        this.requestedStatus = requestedStatus;
    }

    public InvalidStatusTransitionException(String currentStatus, String requestedStatus, String message) {
        this(currentStatus, null, requestedStatus, message);
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getLrpStatus() {
        return lrpStatus;
    }

    public String getRequestedStatus() {
        return requestedStatus;
    }
}
