package com.svm.ecom.exception;

/**
 * Runtime exception thrown when there are issues accessing the reportdb database.
 * This exception is used to indicate database connectivity issues or query execution failures.
 */
public class ReportDbAccessException extends RuntimeException {

    public ReportDbAccessException(String message) {
        super(message);
    }

    public ReportDbAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
