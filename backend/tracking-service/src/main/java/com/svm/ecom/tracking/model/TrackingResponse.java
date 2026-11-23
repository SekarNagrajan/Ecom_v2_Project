package com.svm.ecom.tracking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;

/**
 * Response object for tracking API calls
 */
@Schema(description = "Response containing tracking information and status")
public class TrackingResponse {

    @Schema(description = "Tracking data containing container and movement information", required = true)
    @JsonProperty("trackingData")
    private TrackingData trackingData;

    @Schema(description = ModelDocs.ScheduleResponse.MESSAGE_CODE, example = ModelDocs.ScheduleResponse.EX_MESSAGE_CODE, required = true)
    private String messageCode;

    @Schema(description = ModelDocs.ScheduleResponse.MESSAGE, example = ModelDocs.ScheduleResponse.EX_MESSAGE, required = true)
    @JsonProperty("message")
    private String message;

    @Schema(description = ModelDocs.ScheduleResponse.TIMESTAMP, example = ModelDocs.Common.EX_DATETIME_TZ)
    private String timestamp;

    @Schema(description = ModelDocs.ScheduleResponse.PROCESSED_BY, example = ModelDocs.ScheduleResponse.EX_PROCESSED_BY)
    private String processedBy;

    @Schema(description = "Container number that was tracked", example = ModelDocs.TrackingRequest.EX_CONTAINER)
    private String strcontainer;

    /**
     * Default constructor
     */
    public TrackingResponse() {
        this.timestamp = java.time.Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS).toString();
    }

    /**
     * Constructor with basic parameters
     */
    public TrackingResponse(TrackingData trackingData, String messageCode, String message) {
        this();
        this.trackingData = trackingData;
        this.messageCode = messageCode;
        this.message = message;
    }

    /**
     * Constructor with all parameters
     */
    public TrackingResponse(TrackingData trackingData, String messageCode, String message, String processedBy, String strcontainer) {
        this(trackingData, messageCode, message);
        this.processedBy = processedBy;
        this.strcontainer = strcontainer;
    }

    public TrackingData getTrackingData() {
        return trackingData;
    }

    public void setTrackingData(TrackingData trackingData) {
        this.trackingData = trackingData;
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

    public String getStrcontainer() {
        return strcontainer;
    }

    public void setStrcontainer(String strcontainer) {
        this.strcontainer = strcontainer;
    }

    @Override
    public String toString() {
        return "TrackingResponse{" +
                "messageCode='" + messageCode + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", processedBy='" + processedBy + '\'' +
                ", strcontainer='" + strcontainer + '\'' +
                '}';
    }
}