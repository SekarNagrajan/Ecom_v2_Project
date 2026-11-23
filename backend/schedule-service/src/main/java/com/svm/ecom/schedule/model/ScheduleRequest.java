package com.svm.ecom.schedule.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for fetching schedule between origin and delivery")
public class ScheduleRequest {

    @Schema(description = "Origin port code", example = "SGSIN", required = true)
    private String origin;

    @Schema(description = "Delivery port code", example = "USLAX", required = true)
    private String delivery;

    @Schema(description = "Schedule start date (YYYY-MM-DD)", example = "2025-08-01", required = true)
    private String fromdate;

    @Schema(description = "Schedule end date (YYYY-MM-DD)", example = "2025-08-10", required = true)
    private String todate;

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

    public String getFromdate() {
        return fromdate;
    }

    public void setFromdate(String fromdate) {
        this.fromdate = fromdate;
    }

    public String getTodate() {
        return todate;
    }

    public void setTodate(String todate) {
        this.todate = todate;
    }
} 
