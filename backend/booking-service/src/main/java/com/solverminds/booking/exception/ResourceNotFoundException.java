package com.solverminds.booking.exception;

/**
 * Thrown when a requested resource (e.g. booking) is not found.
 * Mapped to HTTP 404 with ErrorMessage (detail, instance, code NOT_FOUND) by BookingV2ExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
