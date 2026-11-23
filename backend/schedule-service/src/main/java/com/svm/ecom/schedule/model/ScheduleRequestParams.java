package com.svm.ecom.schedule.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Request parameters for schedule API
 */
@Schema(description = "Request parameters for fetching sailing schedules")
public class ScheduleRequestParams {

    @Schema(description = "Origin port code", required = true, example = "CNNGB")
    @NotBlank(message = "Origin port code is required")
    private String origin;

    @Schema(description = "Destination port code", required = true, example = "NLRTM")
    @NotBlank(message = "Destination port code is required")
    private String delivery;

    @Schema(description = "Start date of the schedule window", required = true, example = "2026-03-02")
    @NotBlank(message = "From date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "From date must be in YYYY-MM-DD format")
    @JsonProperty("fromDate")
    private String fromDate;

    @Schema(description = "End date of the schedule window", required = true, example = "2026-04-02")
    @NotBlank(message = "To date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "To date must be in YYYY-MM-DD format")
    @JsonProperty("toDate")
    private String toDate;

    /**
     * Default constructor
     */
    public ScheduleRequestParams() {
    }

    /**
     * Constructor with all parameters
     */
    public ScheduleRequestParams(String origin, String delivery, String fromDate, String toDate) {
        this.origin = origin;
        this.delivery = delivery;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
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
        return "ScheduleRequestParams{" +
                "origin='" + origin + '\'' +
                ", delivery='" + delivery + '\'' +
                ", fromDate='" + fromDate + '\'' +
                ", toDate='" + toDate + '\'' +
                '}';
    }
}