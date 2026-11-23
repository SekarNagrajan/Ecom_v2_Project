package com.svm.ecom.tracking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.svm.ecom.tracking.enums.LegType;
import com.svm.ecom.tracking.util.TrackingOffsetDateTimeDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Port call information with leg type, ports, and schedules")
public class PortCall {

    @Schema(description = "Leg type", example = "M", required = true)
    @JsonProperty("legType")
    private LegType legType;

    @Schema(description = "Port of loading", example = "CHATTOGRAM, BANGLADESH", required = true)
    @JsonProperty("pol")
    private String pol;

    @Schema(description = "Port of discharge", example = "MOERDIJK, NETHERLANDS", required = true)
    @JsonProperty("pod")
    private String pod;

    @Schema(description = "Estimated time of departure in load port local time (ISO 8601 with offset)", example = "2024-01-21T05:20:00+06:00", required = true)
    @JsonProperty("etd")
    @JsonDeserialize(using = TrackingOffsetDateTimeDeserializer.class)
    private OffsetDateTime etd;

    @Schema(description = "Estimated time of arrival in discharge port local time (ISO 8601 with offset)", example = "2024-01-26T15:42:00+05:30", required = true)
    @JsonProperty("eta")
    @JsonDeserialize(using = TrackingOffsetDateTimeDeserializer.class)
    private OffsetDateTime eta;

    @Schema(description = "Load port ID", example = "BDCGP", required = true)
    @JsonProperty("loadportid")
    private String loadportid;

    @Schema(description = "Discharge port ID", example = "NLMOE", required = true)
    @JsonProperty("dischargeportid")
    private String dischargeportid;

    // Constructors
    public PortCall() {}

    public PortCall(LegType legType, String pol, String pod, OffsetDateTime etd, OffsetDateTime eta, String loadportid, String dischargeportid) {
        this.legType = legType;
        this.pol = pol;
        this.pod = pod;
        this.etd = etd;
        this.eta = eta;
        this.loadportid = loadportid;
        this.dischargeportid = dischargeportid;
    }

    // Getters and Setters
    public LegType getLegType() {
        return legType;
    }
  
    
    public void setLegType(LegType legType) {
        this.legType = legType;
    }

    public String getPol() {
        return pol;
    }

    public void setPol(String pol) {
        this.pol = pol;
    }

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }

    public OffsetDateTime getEtd() {
        return etd;
    }

    public void setEtd(OffsetDateTime etd) {
        this.etd = etd;
    }

    public OffsetDateTime getEta() {
        return eta;
    }

    public void setEta(OffsetDateTime eta) {
        this.eta = eta;
    }

    public String getLoadportid() {
        return loadportid;
    }

    public void setLoadportid(String loadportid) {
        this.loadportid = loadportid;
    }

    public String getDischargeportid() {
        return dischargeportid;
    }

    public void setDischargeportid(String dischargeportid) {
        this.dischargeportid = dischargeportid;
    }
}
