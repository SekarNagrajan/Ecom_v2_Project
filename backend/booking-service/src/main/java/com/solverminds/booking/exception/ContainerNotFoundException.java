package com.solverminds.booking.exception;

/**
 * Requested equipment is not part of the booking (booking_cargo), or fails active
 * {@code Equipmentpassport} validation (missing row or equipment type mismatch).
 */
public class ContainerNotFoundException extends RuntimeException {

    public ContainerNotFoundException(String message) {
        super(message);
    }
}
