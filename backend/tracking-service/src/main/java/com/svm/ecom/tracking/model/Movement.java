package com.svm.ecom.tracking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Container movement information with container number and activities")
public class Movement {

    @Schema(description = "Container number", example = "RCDU7012080", required = true)
    @JsonProperty("containerNo")
    private String containerNo;

    @Schema(description = "List of container activities", required = true)
    @JsonProperty("containerActivity")
    private List<ContainerActivity> containerActivity;

    // Constructors
    public Movement() {}

    public Movement(String containerNo, List<ContainerActivity> containerActivity) {
        this.containerNo = containerNo;
        this.containerActivity = containerActivity;
    }

    // Getters and Setters
    public String getContainerNo() {
        return containerNo;
    }

    public void setContainerNo(String containerNo) {
        this.containerNo = containerNo;
    }

    public List<ContainerActivity> getContainerActivity() {
        return containerActivity;
    }

    public void setContainerActivity(List<ContainerActivity> containerActivity) {
        this.containerActivity = containerActivity;
    }
}
