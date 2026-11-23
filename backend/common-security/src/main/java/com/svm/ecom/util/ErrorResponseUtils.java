package com.svm.ecom.util;

import com.svm.ecom.dto.ErrorResponse;
import com.svm.ecom.error.ErrorCodes;
import com.svm.ecom.error.ErrorReason;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

/**
 * Utility class for creating standardized ErrorResponse objects
 * Provides common error response helper methods that can be used across all controllers
 */
public final class ErrorResponseUtils {
    
    // Private constructor to prevent instantiation
    private ErrorResponseUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Create a proper ErrorResponse for 400 Bad Request errors
     */
    public static ResponseEntity<ErrorResponse> badRequest(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            400,
            "Bad Request", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.BAD_REQUEST,
            null,
            null,
            ErrorReason.BAD_REQUEST,
            message != null ? message : "Bad request parameters"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 401 Unauthorized errors
     */
    public static ResponseEntity<ErrorResponse> unauthorized(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            401,
            "Unauthorized", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.UNAUTHORIZED,
            null,
            null,
            ErrorReason.UNAUTHORIZED,
            message != null ? message : "Authentication required or invalid token"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 403 Forbidden errors
     */
    public static ResponseEntity<ErrorResponse> forbidden(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            403,
            "Forbidden", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.FORBIDDEN,
            null,
            null,
            ErrorReason.FORBIDDEN,
            message != null ? message : "You do not have permission to access this resource"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 404 Not Found errors
     */
    public static ResponseEntity<ErrorResponse> notFound(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            404,
            "Not Found", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.NOT_FOUND,
            null,
            null,
            ErrorReason.NOT_FOUND,
            message != null ? message : "The requested resource was not found"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 422 Unprocessable Entity errors
     */
    public static ResponseEntity<ErrorResponse> unprocessableEntity(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            422,
            "Unprocessable Entity", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.BUSINESS_RULE_VIOLATION,
            null,
            null,
            ErrorReason.BUSINESS_RULE,
            message != null ? message : "Request cannot be processed due to business rule violation"
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 500 Internal Server Error
     */
    public static ResponseEntity<ErrorResponse> internalError(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            500,
            "Internal Server Error", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.INTERNAL_ERROR,
            null,
            null,
            ErrorReason.INTERNAL_ERROR,
            message != null ? message : "An unexpected server error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 503 Service Unavailable errors
     */
    public static ResponseEntity<ErrorResponse> serviceUnavailable(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            503,
            "Service Unavailable", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.SERVICE_UNAVAILABLE,
            null,
            null,
            ErrorReason.SERVICE_UNAVAILABLE,
            message != null ? message : "The service is currently unavailable"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for custom HTTP status codes
     */
    public static ResponseEntity<ErrorResponse> customError(int statusCode, String statusText, 
                                                           int errorCode, ErrorReason reason, String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            statusCode,
            statusText, 
            LocalDateTime.now()
        );
        errorResponse.addError(
            errorCode,
            null,
            null,
            reason,
            message
        );
        return ResponseEntity.status(statusCode).body(errorResponse);
    }
}
