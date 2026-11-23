package com.svm.ecom.tracking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.tracking.util.TrackingOffsetDateTimeDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Individual container activity with movement details")
public class ContainerActivity {

    @Schema(description = "Container number", example = "RCDU7012080", required = true)
    @JsonProperty("containerNo")
    private String containerNo;

    @Schema(description = "Movement description", example = "RECEIVED FROM CONSIGNEE", required = true)
    @JsonProperty("movementDesc")
    private String movementDesc;

    @Schema(description = "Port name", example = "MOERDIJK, NETHERLANDS", required = true)
    @JsonProperty("portName")
    private String portName;

    @Schema(description = "Vessel name", example = "FAI", required = true)
    @JsonProperty("vessel")
    private String vessel;

    @Schema(description = "Activity date in port local time (ISO 8601 with offset)", example = "2024-03-18T15:37:00+01:00", required = true)
    @JsonProperty("activityDate")
    @JsonDeserialize(using = TrackingOffsetDateTimeDeserializer.class)
    private OffsetDateTime activityDate;

    @Schema(description = "Service code", example = "TEX", required = true)
    @JsonProperty("serviceCode")
    private String serviceCode;

    @Schema(description = "Voyage number", example = "305", required = true)
    @JsonProperty("voyage")
    private String voyage;

    @Schema(description = "Bound direction", example = "W", required = true)
    @JsonProperty("bound")
    private Bound bound;

    @Schema(description = "Booking sales agent", example = "", required = true)
    @JsonProperty("bkSalesAgent")
    private String bkSalesAgent;

    @Schema(description = "BL sales agent", example = "", required = true)
    @JsonProperty("blSalesAgent")
    private String blSalesAgent;

    @Schema(description = "Equipment type ID", example = "45HC", required = true)
    @JsonProperty("eqpTypeId")
    private String eqpTypeId;

    @Schema(description = "Activity location code", example = "NLMOE", required = true)
    @JsonProperty("activityloccode")
    private String activityloccode;

    @Schema(description = "Next port of discharge", example = "NLMOE", required = true)
    @JsonProperty("nextpod")
    private String nextpod;

    // Constructors
    public ContainerActivity() {}

    // Getters and Setters
    public String getContainerNo() {
        return containerNo;
    }

    public void setContainerNo(String containerNo) {
        this.containerNo = containerNo;
    }

    public String getMovementDesc() {
        return movementDesc;
    }

    public void setMovementDesc(String movementDesc) {
        this.movementDesc = movementDesc;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getVessel() {
        return vessel;
    }

    public void setVessel(String vessel) {
        this.vessel = vessel;
    }

    public OffsetDateTime getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(OffsetDateTime activityDate) {
        this.activityDate = activityDate;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getVoyage() {
        return voyage;
    }

    public void setVoyage(String voyage) {
        this.voyage = voyage;
    }

    public Bound getBound() {
        return bound;
    }

    public void setBound(Bound bound) {
        this.bound = bound;
    }

    public String getBkSalesAgent() {
        return bkSalesAgent;
    }

    public void setBkSalesAgent(String bkSalesAgent) {
        this.bkSalesAgent = bkSalesAgent;
    }

    public String getBlSalesAgent() {
        return blSalesAgent;
    }

    public void setBlSalesAgent(String blSalesAgent) {
        this.blSalesAgent = blSalesAgent;
    }

    public String getEqpTypeId() {
        return eqpTypeId;
    }

    public void setEqpTypeId(String eqpTypeId) {
        this.eqpTypeId = eqpTypeId;
    }

    public String getActivityloccode() {
        return activityloccode;
    }

    public void setActivityloccode(String activityloccode) {
        this.activityloccode = activityloccode;
    }

    public String getNextpod() {
        return nextpod;
    }

    public void setNextpod(String nextpod) {
        this.nextpod = nextpod;
    }
}
