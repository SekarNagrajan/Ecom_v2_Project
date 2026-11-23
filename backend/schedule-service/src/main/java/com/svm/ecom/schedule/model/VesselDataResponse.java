package com.svm.ecom.schedule.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * POJO representing the complete vessel schedule API response
 */
@Schema(description = "Root response object containing vessel schedule data with status information")
public class VesselDataResponse {

    @Schema(description = "Response status code", example = "200", required = true)
    @JsonProperty("code")
    private String code;

    @Schema(description = "Response status message", example = "Success", required = true)
    @JsonProperty("message")
    private String message;

    @Schema(description = "List of vessel schedule items", required = true)
    @JsonProperty("byVessel")
    private List<VesselScheduleItem> byVessel;

    /**
     * Default constructor
     */
    public VesselDataResponse() {}

    /**
     * Constructor with basic parameters
     */
    public VesselDataResponse(String code, String message, List<VesselScheduleItem> byVessel) {
        this.code = code;
        this.message = message;
        this.byVessel = byVessel;
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

    public List<VesselScheduleItem> getByVessel() {
        return byVessel;
    }

    public void setByVessel(List<VesselScheduleItem> byVessel) {
        this.byVessel = byVessel;
    }

    @Override
    public String toString() {
        return "VesselDataResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", byVessel=" + (byVessel != null ? byVessel.size() + " items" : "null") +
                '}';
    }
}
