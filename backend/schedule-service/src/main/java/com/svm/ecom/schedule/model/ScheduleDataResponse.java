package com.svm.ecom.schedule.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;
import java.util.List;

/**
 * Complete schedule data response wrapper that matches the API output structure
 */
@Schema(description = "Complete schedule response containing route information with vessel schedules and port pairs")
public class ScheduleDataResponse {

    @Schema(description = "Array of schedule entries with detailed route, vessel, and timing information", required = true)
    private List<ScheduleEntry> scheduleData;

    @Schema(description = ModelDocs.ScheduleResponse.MESSAGE_CODE, example = ModelDocs.ScheduleResponse.EX_MESSAGE_CODE, required = true)
    private String messageCode;

    @Schema(description = ModelDocs.ScheduleResponse.MESSAGE, example = "Success", required = true)
    private String message;

    @Schema(description = ModelDocs.ScheduleResponse.TIMESTAMP, example = "2025-09-08T05:22:25Z")
    private String timestamp;

    @Schema(description = ModelDocs.ScheduleResponse.PROCESSED_BY, example = ModelDocs.ScheduleResponse.EX_PROCESSED_BY)
    private String processedBy;

    /**
     * Default constructor
     */
    public ScheduleDataResponse() {
        this.timestamp = java.time.Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS).toString();
    }

    /**
     * Constructor with basic parameters
     */
    public ScheduleDataResponse(List<ScheduleEntry> scheduleData, String messageCode, String message) {
        this();
        this.scheduleData = scheduleData;
        this.messageCode = messageCode;
        this.message = message;
    }

    /**
     * Constructor with all parameters
     */
    public ScheduleDataResponse(List<ScheduleEntry> scheduleData, String messageCode, String message, String processedBy) {
        this(scheduleData, messageCode, message);
        this.processedBy = processedBy;
    }

    // Getters and Setters
    public List<ScheduleEntry> getScheduleData() {
        return scheduleData;
    }

    public void setScheduleData(List<ScheduleEntry> scheduleData) {
        this.scheduleData = scheduleData;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
        return "ScheduleDataResponse{" +
                "scheduleDataSize=" + (scheduleData != null ? scheduleData.size() : 0) +
                ", messageCode='" + messageCode + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", processedBy='" + processedBy + '\'' +
                '}';
    }
}