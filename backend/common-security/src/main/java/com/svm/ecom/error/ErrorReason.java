package com.svm.ecom.error;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Error reason enumeration for categorizing API errors.
 */
@Schema(description = "Error Reason: Categorizes the type of error that occurred", enumAsRef = true)
public enum ErrorReason {

    @Schema(description = "Bad request - Invalid input or malformed request")
    BAD_REQUEST("BAD_REQUEST"),
    
    @Schema(description = "Unauthorized - Authentication required or failed")
    UNAUTHORIZED("UNAUTHORIZED"),
    
    @Schema(description = "Forbidden - Insufficient permissions to access resource")
    FORBIDDEN("FORBIDDEN"),
    
    @Schema(description = "Not found - Requested resource does not exist")
    NOT_FOUND("NOT_FOUND"),
    
    @Schema(description = "Method not allowed - HTTP method not supported for this endpoint")
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED"),
    
    @Schema(description = "Conflict - Request conflicts with current state of the resource")
    CONFLICT("CONFLICT"),
    
    @Schema(description = "Unsupported media type - Content type not supported")
    UNSUPPORTED_MEDIA_TYPE("UNSUPPORTED_MEDIA_TYPE"),
    
    @Schema(description = "Validation error - Input validation failed")
    VALIDATION_ERROR("VALIDATION_ERROR"),
    
    @Schema(description = "Business rule violation - Business logic constraint not met")
    BUSINESS_RULE("BUSINESS_RULE"),
    
    @Schema(description = "Internal error - Unexpected server error")
    INTERNAL_ERROR("INTERNAL_ERROR"),
    
    @Schema(description = "Service unavailable - Service temporarily unavailable")
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE"),

    @Schema(description = "Rate limited - Too many requests, retry after the given interval")
    RATE_LIMITED("RATE_LIMITED");

    private final String value;

    ErrorReason(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}

