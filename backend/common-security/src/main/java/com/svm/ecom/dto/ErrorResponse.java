package com.svm.ecom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.svm.ecom.constants.DateFormatConstants;
import com.svm.ecom.error.ErrorReason;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Standard error wrapper for API failures (e.g. validation, authentication or server errors).")
public class ErrorResponse {
    @Schema(description = "HTTP status code.")
    private int statusCode;

    @Schema(description = "HTTP status text.")
    private String statusCodeText;

    @Schema(description = "When the error occurred.", type = "string",
    example = "2025-08-08T11:54:11Z", format = "date-time")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime errorDateTime;

    @Schema(description = "List of error details.")
    private List<ErrorDetail> errors = new ArrayList<>();

    public ErrorResponse(int statusCode, String statusCodeText, LocalDateTime errorDateTime) {
        this.statusCode = statusCode;
        this.statusCodeText = statusCodeText;
        this.errorDateTime = errorDateTime;
    }

    public int getStatusCode() { return statusCode; }
    public String getStatusCodeText() { return statusCodeText; }
    public LocalDateTime getErrorDateTime() { return errorDateTime; }
    public List<ErrorDetail> getErrors() { return errors; }

    public void addError(int errorCode, String field, String value, ErrorReason reason, String message) {
        this.errors.add(new ErrorDetail(errorCode, field, value, reason, message));
    }

    @Schema(description = "Single error item (e.g. a request field validation failure).")
    public static class ErrorDetail {
        @Schema(description = "Integer error code.")
        private int errorCode;

        @Schema(description = "Request field, query parameter or JSON path that caused the error (e.g. fromDate, service, vessel).", nullable = true, example = "fromDate")
        private String field;

        @Schema(description = "The rejected value for the field, when available.", nullable = true, example = "2026-13-01")
        private String value;

        @Schema(description = "Type of error.")
        private ErrorReason reason;

        @Schema(description = "Human-readable error message describing the validation failure or error.", example = "fromDate must be in yyyy-MM-dd format")
        private String message;

        public ErrorDetail(int errorCode, String field, String value, ErrorReason reason, String message) {
            this.errorCode = errorCode;
            this.field = field;
            this.value = value;
            this.reason = reason;
            this.message = message;
        }

        public int getErrorCode() { return errorCode; }
        public String getField() { return field; }
        public String getValue() { return value; }
        public ErrorReason getReason() { return reason; }
        public String getMessage() { return message; }
    }
}
