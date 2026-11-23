package com.svm.ecom.carrier.service;

import org.springframework.http.HttpStatus;

/**
 * Failure while validating or calling the coastal schedule live API.
 */
public class CoastalScheduleException extends RuntimeException {

    private final HttpStatus status;

    public CoastalScheduleException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
