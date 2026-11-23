package com.svm.ecom.rates.dto.estimate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.svm.ecom.common.enums.CarriageType;
import com.svm.ecom.constants.DateFormatConstants;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(name = "RateEstimateRequest", description = "Request payload for Auto Rate surcharge estimate")
public class RateEstimateRequest {

    @NotBlank
    @Schema(description = "Origin port / location code", example = "CRPMN", requiredMode = Schema.RequiredMode.REQUIRED)
    private String originId;

    @NotBlank
    @Schema(description = "Delivery / final destination code", example = "NLVLI", requiredMode = Schema.RequiredMode.REQUIRED)
    private String deliveryId;

    @NotBlank
    @Schema(description = "Port of loading code", example = "CRPMN", requiredMode = Schema.RequiredMode.REQUIRED)
    private String polId;

    @NotBlank
    @Schema(description = "Port of discharge code", example = "NLVLI", requiredMode = Schema.RequiredMode.REQUIRED)
    private String podId;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.ISO_DATE_TIME_ZONE)
    @Schema(description = "Estimated time of arrival", example = "2026-08-15T00:00:00+00:00",
            type = "string", format = "date-time", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime eta;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.ISO_DATE_TIME_ZONE)
    @Schema(description = "Estimated time of departure", example = "2026-08-08T00:00:00+00:00",
            type = "string", format = "date-time", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime etd;

    @NotNull
    @Schema(description = "Pre-carriage type", example = "N", requiredMode = Schema.RequiredMode.REQUIRED)
    private CarriageType preCarriage;

    @NotNull
    @Schema(description = "On-carriage type", example = "N", requiredMode = Schema.RequiredMode.REQUIRED)
    private CarriageType onCarriage;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.ISO_DATE_TIME_ZONE)
    @Schema(description = "Cargo ready date", example = "2026-08-08T00:00:00+00:00",
            type = "string", format = "date-time", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime cargoReadyDate;

    @NotBlank
    @Schema(description = "Agreement party / customer code used for surcharge calculation",
            example = "013041", requiredMode = Schema.RequiredMode.REQUIRED)
    private String agrPartyCode;

    @NotBlank
    @Schema(description = "Transport service (e.g. CY/CY)", example = "CY/CY",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String transportService;

    @NotNull
    @Schema(description = "Route identifier", example = "776", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer routeId;

    @NotEmpty
    @Valid
    @Schema(description = "Container lines for surcharge estimate", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RateEstimateContainerRequest> containers = new ArrayList<>();

    public String getOriginId() { return originId; }
    public void setOriginId(String originId) { this.originId = originId; }
    public String getDeliveryId() { return deliveryId; }
    public void setDeliveryId(String deliveryId) { this.deliveryId = deliveryId; }
    public String getPolId() { return polId; }
    public void setPolId(String polId) { this.polId = polId; }
    public String getPodId() { return podId; }
    public void setPodId(String podId) { this.podId = podId; }
    public OffsetDateTime getEta() { return eta; }
    public void setEta(OffsetDateTime eta) { this.eta = eta; }
    public OffsetDateTime getEtd() { return etd; }
    public void setEtd(OffsetDateTime etd) { this.etd = etd; }
    public CarriageType getPreCarriage() { return preCarriage; }
    public void setPreCarriage(CarriageType preCarriage) { this.preCarriage = preCarriage; }
    public CarriageType getOnCarriage() { return onCarriage; }
    public void setOnCarriage(CarriageType onCarriage) { this.onCarriage = onCarriage; }
    public OffsetDateTime getCargoReadyDate() { return cargoReadyDate; }
    public void setCargoReadyDate(OffsetDateTime cargoReadyDate) { this.cargoReadyDate = cargoReadyDate; }
    public String getAgrPartyCode() { return agrPartyCode; }
    public void setAgrPartyCode(String agrPartyCode) { this.agrPartyCode = agrPartyCode; }
    public String getTransportService() { return transportService; }
    public void setTransportService(String transportService) { this.transportService = transportService; }
    public Integer getRouteId() { return routeId; }
    public void setRouteId(Integer routeId) { this.routeId = routeId; }
    public List<RateEstimateContainerRequest> getContainers() { return containers; }
    public void setContainers(List<RateEstimateContainerRequest> containers) {
        this.containers = containers != null ? containers : new ArrayList<>();
    }
}
