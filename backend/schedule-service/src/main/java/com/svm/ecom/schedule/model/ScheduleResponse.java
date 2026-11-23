package com.svm.ecom.schedule.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;
import java.util.List;

/**
 * Response object for schedule API calls
 */
@Schema(description = ModelDocs.ScheduleResponse.DESC)
public class ScheduleResponse {

    @Schema(description = ModelDocs.ScheduleResponse.SCHEDULE_DATA, required = true)
    private List<ScheduleEntry> scheduleData;

    @Schema(description = ModelDocs.ScheduleResponse.MESSAGE_CODE, example = ModelDocs.ScheduleResponse.EX_MESSAGE_CODE, required = true)
    private String messageCode;

    @Schema(description = ModelDocs.ScheduleResponse.MESSAGE, example = ModelDocs.ScheduleResponse.EX_MESSAGE, required = true)
    private String message;

    @Schema(description = ModelDocs.ScheduleResponse.TIMESTAMP, example = ModelDocs.Common.EX_DATETIME_TZ)
    private String timestamp;

    @Schema(description = ModelDocs.ScheduleResponse.PROCESSED_BY, example = ModelDocs.ScheduleResponse.EX_PROCESSED_BY)
    private String processedBy;

    /**
     * Default constructor
     */
    public ScheduleResponse() {
        this.timestamp = java.time.Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS).toString();
    }

    /**
     * Constructor with basic parameters
     */
    public ScheduleResponse(List<ScheduleEntry> scheduleData, String messageCode, String message) {
        this();
        this.scheduleData = scheduleData;
        this.messageCode = messageCode;
        this.message = message;
    }

    /**
     * Constructor with all parameters
     */
    public ScheduleResponse(List<ScheduleEntry> scheduleData, String messageCode, String message, String processedBy) {
        this(scheduleData, messageCode, message);
        this.processedBy = processedBy;
    }

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
        return "ScheduleResponse{" +
                "messageCode='" + messageCode + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", processedBy='" + processedBy + '\'' +
                '}';
    }
}