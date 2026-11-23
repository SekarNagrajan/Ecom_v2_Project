package com.solverminds.booking.exception;

public class BookingCancellationConflictException extends RuntimeException {
    private final String conflictCode;

    public BookingCancellationConflictException(String conflictCode, String message) {
        super(message);
        this.conflictCode = conflictCode;
    }

    public String getConflictCode() {
        return conflictCode;
    }
}
