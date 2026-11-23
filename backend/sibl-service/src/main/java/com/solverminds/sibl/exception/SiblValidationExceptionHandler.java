package com.solverminds.sibl.exception;

import com.svm.ecom.dto.ErrorResponse;
import com.svm.ecom.error.ErrorReason;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Handles SI validation failures and returns ErrorResponse (same structure as booking-service
 * and GlobalExceptionHandler.handleMethodArgumentNotValid).
 */
@ControllerAdvice
public class SiblValidationExceptionHandler {

    @ExceptionHandler(SIValidationException.class)
    public ResponseEntity<ErrorResponse> handleSIValidationException(SIValidationException ex) {
        ErrorResponse body = new ErrorResponse(
                400,
                "Bad Request",
                LocalDateTime.now()
        );
        ex.getErrors().forEach(err ->
                body.addError(
                        err.getErrorCode(),
                        err.getField(),
                        err.getValue(),
                        ErrorReason.VALIDATION_ERROR,
                        err.getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
