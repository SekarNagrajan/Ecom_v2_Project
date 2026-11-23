package com.solverminds.booking.exception;

/**
 * Booking missing, void (status V), or not eligible for the requested operation.
 */
public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(String message) {
        super(message);
    }
}
