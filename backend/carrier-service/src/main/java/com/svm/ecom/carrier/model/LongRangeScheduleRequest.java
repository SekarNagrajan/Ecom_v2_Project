package com.svm.ecom.carrier.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "One long range schedule query. The request body is an array of these objects.")
public class LongRangeScheduleRequest {

    @Schema(description = "Service code", example = "PAX", required = true)
    @NotBlank(message = "service is required")
    private String service;

    @Schema(description = "Start of the schedule window.", type = "string", format = "date", example = "2026-08-18", required = true)
    @NotNull(message = "fromDate is required")
    private LocalDate fromDate;

    @Schema(description = "End of the schedule window. Must be on or after fromDate.", type = "string", format = "date", example = "2026-09-15", required = true)
    @NotNull(message = "toDate is required")
    private LocalDate toDate;

    @Schema(description = "Week start day. M = Monday, S = Sunday. Defaults to M when omitted.", example = "M")
    private String weekStartDay;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public String getWeekStartDay() {
        return weekStartDay;
    }

    public void setWeekStartDay(String weekStartDay) {
        this.weekStartDay = weekStartDay;
    }
}
