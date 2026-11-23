package com.svm.ecom.tracking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.svm.ecom.tracking.util.TrackingOffsetDateTimeDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "Individual tracking result containing BL, booking, port, and movement information")
public class TrackingResult {

    @Schema(description = "Bill of Lading number", example = "TSHGCGP23000031", required = true)
    @JsonProperty("blNo")
    private String blNo;

    @Schema(description = "Booking number", example = "TSHGCGP23000031", required = true)
    @JsonProperty("bookNo")
    private String bookNo;

    @Schema(description = "Origin port description", example = "CHATTOGRAM, BANGLADESH", required = true)
    @JsonProperty("originPortDesc")
    private String originPortDesc;

    @Schema(description = "Final port description", example = "MOERDIJK, NETHERLANDS", required = true)
    @JsonProperty("finalPortDesc")
    private String finalPortDesc;

    @Schema(description = "Port of discharge arrival time in port local time (ISO 8601 with offset)", example = "2024-03-01T07:00:00+01:00", required = true)
    @JsonProperty("podAt")
    @JsonDeserialize(using = TrackingOffsetDateTimeDeserializer.class)
    private OffsetDateTime podAt;

    @Schema(description = "Port of loading departure time in port local time (ISO 8601 with offset)", example = "2024-01-21T05:20:00+06:00", required = true)
    @JsonProperty("polAt")
    @JsonDeserialize(using = TrackingOffsetDateTimeDeserializer.class)
    private OffsetDateTime polAt;

    @Schema(description = "Discharge port description", example = "NLMOE", required = true)
    @JsonProperty("dischargePortDesc")
    private String dischargePortDesc;

    @Schema(description = "Load arrival date in port local time (ISO 8601 with offset)", example = "2024-01-20T07:18:00+06:00", required = true)
    @JsonProperty("loadarrdate")
    @JsonDeserialize(using = TrackingOffsetDateTimeDeserializer.class)
    private OffsetDateTime loadarrdate;

    @Schema(description = "Port of loading terminal", example = "BDCGPCCT00", required = true)
    @JsonProperty("polterminal")
    private String polterminal;

    @Schema(description = "Port of loading terminal name", example = "Chattogram Container Terminal", required = true)
    @JsonProperty("polterminalname")
    private String polterminalname;

    @Schema(description = "Port of discharge terminal", example = "NLMOEPCT00", required = false)
    @JsonProperty("podterminal")
    private String podterminal;

    @Schema(description = "Port of discharge terminal name", example = "Moerdijk Container Terminal", required = false)
    @JsonProperty("podterminalname")
    private String podterminalname;

    @Schema(description = "Load port ID", example = "BDCGP", required = true)
    @JsonProperty("loadportid")
    private String loadportid;

    @Schema(description = "List of container movements", required = true)
    @JsonProperty("movementList")
    private List<Movement> movementList;

    @Schema(description = "List of port calls", required = true)
    @JsonProperty("portacklist")
    private List<PortCall> portacklist;

    @Schema(description = "Origin port code", example = "BDCGP", required = true)
    @JsonProperty("originPortCode")
    private String originPortCode;

    @Schema(description = "Delivery port code", example = "NLMOE", required = true)
    @JsonProperty("deliveryPortCode")
    private String deliveryPortCode;

    // Constructors
    public TrackingResult() {}

    // Getters and Setters
    public String getBlNo() {
        return blNo;
    }

    public void setBlNo(String blNo) {
        this.blNo = blNo;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getOriginPortDesc() {
        return originPortDesc;
    }

    public void setOriginPortDesc(String originPortDesc) {
        this.originPortDesc = originPortDesc;
    }

    public String getFinalPortDesc() {
        return finalPortDesc;
    }

    public void setFinalPortDesc(String finalPortDesc) {
        this.finalPortDesc = finalPortDesc;
    }

    public OffsetDateTime getPodAt() {
        return podAt;
    }

    public void setPodAt(OffsetDateTime podAt) {
        this.podAt = podAt;
    }

    public OffsetDateTime getPolAt() {
        return polAt;
    }

    public void setPolAt(OffsetDateTime polAt) {
        this.polAt = polAt;
    }

    public String getDischargePortDesc() {
        return dischargePortDesc;
    }

    public void setDischargePortDesc(String dischargePortDesc) {
        this.dischargePortDesc = dischargePortDesc;
    }

    public OffsetDateTime getLoadarrdate() {
        return loadarrdate;
    }

    public void setLoadarrdate(OffsetDateTime loadarrdate) {
        this.loadarrdate = loadarrdate;
    }

    public String getPolterminal() {
        return polterminal;
    }

    public void setPolterminal(String polterminal) {
        this.polterminal = polterminal;
    }

    public String getLoadportid() {
        return loadportid;
    }

    public void setLoadportid(String loadportid) {
        this.loadportid = loadportid;
    }

    public List<Movement> getMovementList() {
        return movementList;
    }

    public void setMovementList(List<Movement> movementList) {
        this.movementList = movementList;
    }

    public List<PortCall> getPortacklist() {
        return portacklist;
    }

    public void setPortacklist(List<PortCall> portacklist) {
        this.portacklist = portacklist;
    }

    public String getOriginPortCode() {
        return originPortCode;
    }

    public void setOriginPortCode(String originPortCode) {
        this.originPortCode = originPortCode;
    }

    public String getDeliveryPortCode() {
        return deliveryPortCode;
    }

    public void setDeliveryPortCode(String deliveryPortCode) {
        this.deliveryPortCode = deliveryPortCode;
    }   

    public String getPolterminalname() {
        return polterminalname;
    }

    public void setPolterminalname(String polterminalname) {
        this.polterminalname = polterminalname;
    }

    public String getPodterminal() {
        return podterminal;
    }

    public void setPodterminal(String podterminal) {
        this.podterminal = podterminal;
    }

    public String getPodterminalname() {
        return podterminalname;
    }

    public void setPodterminalname(String podterminalname) {
        this.podterminalname = podterminalname;
    }
}
