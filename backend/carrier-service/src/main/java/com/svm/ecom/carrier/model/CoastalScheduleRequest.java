package com.svm.ecom.carrier.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "One coastal schedule query. The request body is an array of these objects.")
public class CoastalScheduleRequest {

    @Schema(description = "Start of the schedule window.", type = "string", format = "date", example = "2026-08-10", required = true)
    @NotNull(message = "fromDate is required")
    private LocalDate fromDate;

    @Schema(description = "End of the schedule window.", type = "string", format = "date", example = "2026-10-14", required = true)
    @NotNull(message = "toDate is required")
    private LocalDate toDate;

    @Schema(description = "Service code", example = "PAX", required = true)
    @NotBlank(message = "service is required")
    private String service;

    @Schema(description = "Vessel code", example = "PCX", required = true)
    @NotBlank(message = "vessel is required")
    private String vessel;

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

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVessel() {
        return vessel;
    }

    public void setVessel(String vessel) {
        this.vessel = vessel;
    }
}
