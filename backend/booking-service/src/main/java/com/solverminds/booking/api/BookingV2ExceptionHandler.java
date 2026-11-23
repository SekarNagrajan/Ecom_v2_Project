package com.solverminds.booking.api;

import com.solverminds.booking.dto.v2.ErrorMessage;
import com.solverminds.booking.dto.v2.NestedErrorMessage;
import com.solverminds.booking.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps exceptions to client-spec error responses for booking v2 endpoints.
 * - MethodArgumentNotValidException → 400 with errors array (pointer + code per field)
 * - ResourceNotFoundException → 404 with instance
 */
@RestControllerAdvice(assignableTypes = BookingControllerV2.class)
public class BookingV2ExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String instance = request.getRequestURI();
        BindingResult result = ex.getBindingResult();
        String detail = result.getFieldErrors().isEmpty()
            ? "Request validation failed."
            : "Booking data validation failed. Please review the provided data.";
        ErrorMessage error = new ErrorMessage(detail, "Validation Failed", 400);
        error.setInstance(instance);
        error.setCode("VALIDATION_FAILED");
        error.setType(ErrorMessage.TYPE_VALIDATION_FAILED);
        List<NestedErrorMessage> errors = result.getFieldErrors().stream()
            .map(fe -> new NestedErrorMessage(
                fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                "#/" + fe.getField(),
                fe.getCode() != null ? fe.getCode() : "INVALID"
            ))
            .collect(Collectors.toList());
        error.setErrors(errors != null ? errors : new ArrayList<>());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        String instance = request.getRequestURI();
        ErrorMessage error = ErrorMessage.notFound(ex.getMessage(), instance);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
