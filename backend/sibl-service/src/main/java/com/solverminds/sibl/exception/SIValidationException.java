package com.solverminds.sibl.exception;

import com.solverminds.sibl.service.SIValidationService;

import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when SI (Shipping Instruction) validation fails.
 * Contains structured error details for API response (same pattern as booking-service
 * MethodArgumentNotValidException → ErrorResponse with multiple errors).
 */
public class SIValidationException extends RuntimeException {

    private final List<SIValidationService.ValidationError> errors;

    /** Single-error constructor for backward compatibility. */
    public SIValidationException(int errorCode, String field, String value, String message) {
        super(message);
        this.errors = Collections.singletonList(
                new SIValidationService.ValidationError(errorCode, field, value, message));
    }

    /** Multi-error constructor used when validation service returns multiple failures. */
    public SIValidationException(List<SIValidationService.ValidationError> errors) {
        super(errors == null || errors.isEmpty() ? "SI validation failed" : errors.get(0).getMessage());
        this.errors = errors == null ? Collections.emptyList() : errors;
    }

    public List<SIValidationService.ValidationError> getErrors() {
        return errors;
    }
}

