package com.svm.ecom.schedule.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.svm.ecom.constants.DateFormatConstants;
import java.time.LocalDateTime;

import java.util.List;

/**
 * POJO representing the complete port schedule API response
 */
@Schema(description = "Root response object containing port schedule data with status and metadata information")
public class PortDataResponse {

    @Schema(description = "Response status code", example = "200", required = true)
    @JsonProperty("code")
    private String code;

    @Schema(description = "Response status message", example = "Success", required = true)
    @JsonProperty("message")
    private String message;

    @Schema(description = "List of port schedule items", required = true)
    @JsonProperty("byPort")
    private List<PortScheduleItem> byPort;

    @Schema(description = "Request timestamp", 
    example = "2025-08-08T09:04:48Z")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "Request processed by user", example = "alice")
    @JsonProperty("processedBy")
    private String processedBy;

    /**
     * Default constructor
     */
    public PortDataResponse() {}

    /**
     * Constructor with basic parameters
     */
    public PortDataResponse(String code, String message, List<PortScheduleItem> byPort) {
        this.code = code;
        this.message = message;
        this.byPort = byPort;
    }

    /**
     * Constructor with all parameters
     */
    public PortDataResponse(String code, String message, List<PortScheduleItem> byPort, 
                           LocalDateTime timestamp, String processedBy) {
        this.code = code;
        this.message = message;
        this.byPort = byPort;
        this.timestamp = timestamp;
        this.processedBy = processedBy;
    }

    // Getters and Setters

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<PortScheduleItem> getByPort() {
        return byPort;
    }

    public void setByPort(List<PortScheduleItem> byPort) {
        this.byPort = byPort;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }

    @Override
    public String toString() {
        return "PortDataResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", byPort=" + (byPort != null ? byPort.size() + " items" : "null") +
                ", timestamp='" + timestamp + '\'' +
                ", processedBy='" + processedBy + '\'' +
                '}';
    }
}
