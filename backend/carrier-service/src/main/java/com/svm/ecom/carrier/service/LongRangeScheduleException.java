package com.svm.ecom.carrier.service;

import org.springframework.http.HttpStatus;

/**
 * Failure while validating or calling the long range schedule live API.
 */
public class LongRangeScheduleException extends RuntimeException {

    private final HttpStatus status;

    public LongRangeScheduleException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
