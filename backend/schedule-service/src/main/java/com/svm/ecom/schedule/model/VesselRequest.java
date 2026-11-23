package com.svm.ecom.schedule.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Request object for vessel schedule API
 */
@Schema(description = "Request object for fetching vessel schedule information")
public class VesselRequest {

    @Schema(description = "Code of the vessel to search for", 
        example = "BUX", 
        required = true
    )
    @NotBlank(message = "Vessel code is required")
    private String vesselCode;

    @Schema(description = "Start date for schedule search (YYYY-MM-DD format)", 
        example = "2025-08-01", 
        required = true
    )
    @NotBlank(message = "From date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "From date must be in YYYY-MM-DD format")
    @JsonProperty("fromDate")
    private String fromDate;

    @Schema(description = "End date for schedule search (YYYY-MM-DD format)", 
        example = "2025-08-10", 
        required = true
    )
    @NotBlank(message = "To date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "To date must be in YYYY-MM-DD format")
    @JsonProperty("toDate")
    private String toDate;

    /**
     * Default constructor
     */
    public VesselRequest() {}

    /**
     * Constructor with all parameters
     */
    public VesselRequest(String vesselCode, String fromDate, String toDate) {
        this.vesselCode = vesselCode;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getVesselCode() {
        return vesselCode;
    }

    public void setVesselCode(String vesselCode) {
        this.vesselCode = vesselCode;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return "VesselRequest{" +
                "vesselCode='" + vesselCode + '\'' +
                ", fromDate='" + fromDate + '\'' +
                ", toDate='" + toDate + '\'' +
                '}';
    }
}
