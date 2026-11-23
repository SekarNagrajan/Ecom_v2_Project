package com.svm.ecom.exception;

import com.svm.ecom.error.ErrorReason;
import java.util.ArrayList;
import java.util.List;

public class BusinessException extends RuntimeException {
    private String summaryMessage;
    private int httpStatusOverride = 0; // 0 means use default (422)
    private List<ErrorDetail> details = new ArrayList<>();

    public BusinessException(String summaryMessage) {
        super(summaryMessage);
        this.summaryMessage = summaryMessage;
    }

    public BusinessException(String summaryMessage, int httpStatusOverride) {
        super(summaryMessage);
        this.summaryMessage = summaryMessage;
        this.httpStatusOverride = httpStatusOverride;
    }

    public String getSummaryMessage() { return summaryMessage; }
    public int getHttpStatusOverride() { return httpStatusOverride; }
    public List<ErrorDetail> getDetails() { return details; }

    public BusinessException addDetail(int errorCode, String field, String value, ErrorReason reason, String message) {
        this.details.add(new ErrorDetail(errorCode, field, value, reason, message));
        return this;
    }

    public static class ErrorDetail {
        private int errorCode;
        private String field;
        private String value;
        private ErrorReason reason;
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
