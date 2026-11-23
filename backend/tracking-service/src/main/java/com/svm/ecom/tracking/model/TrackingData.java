package com.svm.ecom.tracking.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Tracking data containing result information")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackingData {

    @Schema(description = "List of tracking results", required = true)
    @JsonProperty("result")
    private List<TrackingResult> result;

    // Constructors
    public TrackingData() {}

    public TrackingData(List<TrackingResult> result) {
        this.result = result;
    }

    // Getters and Setters
    public List<TrackingResult> getResult() {
        return result;
    }

    public void setResult(List<TrackingResult> result) {
        this.result = result;
    }
}
