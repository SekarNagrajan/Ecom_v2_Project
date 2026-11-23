package com.svm.ecom.schedule.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response wrapper for vessel schedule API. Items in byVessel have arrdate and depdate in port local time (ISO 8601 with offset).")
public class VesselScheduleResponse {

    @Schema(description = "Status code", example = "200")
    private String code;

    @Schema(description = "Status message", example = "Success")
    private String message;

    @Schema(description = "List of vessel schedule details; arrdate and depdate are in port local time with timezone offset")
    private List<VesselScheduleItem> byVessel;

    @Schema(description = "Request timestamp in UTC (Z suffix)", example = "2025-08-07T11:47:49Z")
    private String timestamp;

    @Schema(description = "Request processed by user", example = "user@example.com")
    private String processedBy;

    /**
     * Default constructor
     */
    public VesselScheduleResponse() {
        this.timestamp = java.time.Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS).toString();
    }

    /**
     * Constructor with basic parameters
     */
    public VesselScheduleResponse(String code, String message, List<VesselScheduleItem> byVessel) {
        this();
        this.code = code;
        this.message = message;
        this.byVessel = byVessel;
    }

    /**
     * Constructor with all parameters
     */
    public VesselScheduleResponse(String code, String message, List<VesselScheduleItem> byVessel, String processedBy) {
        this(code, message, byVessel);
        this.processedBy = processedBy;
    }

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

    public List<VesselScheduleItem> getByVessel() {
        return byVessel;
    }

    public void setByVessel(List<VesselScheduleItem> byVessel) {
        this.byVessel = byVessel;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
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
        return "VesselScheduleResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", processedBy='" + processedBy + '\'' +
                '}';
    }
}
