package com.solverminds.booking.exception;

/**
 * Thrown when cancel is requested for an {@code ebookno} that does not exist (maps to HTTP 404).
 */
public class BookingNotFoundForCancelException extends RuntimeException {
    private final String bookingRequestId;

    public BookingNotFoundForCancelException(String bookingRequestId) {
        super("Booking not found: " + bookingRequestId);
        this.bookingRequestId = bookingRequestId;
    }

    public String getBookingRequestId() {
        return bookingRequestId;
    }
}
