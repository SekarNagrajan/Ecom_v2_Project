package com.svm.ecom.tracking.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Request object for container tracking
 */
@Schema(description = ModelDocs.TrackingRequest.DESC)
public class TrackingRequest {

    @Schema(description = ModelDocs.TrackingRequest.KEY, example = ModelDocs.TrackingRequest.EX_KEY, required = true)
    @NotBlank(message = "Tracking key is required")
    @Size(min = 3, max = 50, message = "Tracking key must be between 3 and 50 characters")
    private String key;

    @Schema(description = ModelDocs.TrackingRequest.CONTAINER, example = ModelDocs.TrackingRequest.EX_CONTAINER, required = false)
    private String strcontainer;

    /**
     * Default constructor
     */
    public TrackingRequest() {
    }

    /**
     * Constructor with key and container
     */
    public TrackingRequest(String key, String strcontainer) {
        this.key = key;
        this.strcontainer = strcontainer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStrcontainer() {
        return strcontainer;
    }

    public void setStrcontainer(String strcontainer) {
        this.strcontainer = strcontainer;
    }

    @Override
    public String toString() {
        return "TrackingRequest{" +
                "key='" + key + '\'' +
                ", strcontainer='" + strcontainer + '\'' +
                '}';
    }
}