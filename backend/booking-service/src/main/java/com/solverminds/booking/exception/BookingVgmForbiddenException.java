package com.solverminds.booking.exception;

/**
 * Authenticated user is not permitted to access VGM for this booking.
 */
public class BookingVgmForbiddenException extends RuntimeException {

    public BookingVgmForbiddenException(String message) {
        super(message);
    }
}
