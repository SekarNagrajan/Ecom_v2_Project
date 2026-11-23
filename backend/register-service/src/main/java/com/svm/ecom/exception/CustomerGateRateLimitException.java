package com.svm.ecom.exception;


public class CustomerGateRateLimitException extends RuntimeException {

    private final long retryAfterSeconds;

    public CustomerGateRateLimitException(String message, long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
