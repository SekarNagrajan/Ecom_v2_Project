package com.svm.ecom.master.api;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenAPI documentation schema for vessel data
 * Used only for API documentation purposes
 */
@Schema(description = "Vessel master data")
public class VesselDoc {

    @Schema(description = "Vessel code", example = "BUX", required = true)
    private String vesselCode;

    @Schema(description = "Vessel name", example = "BUNGA RAYA DUA", required = true)
    private String vesselName;

    @Schema(description = "IMO Number", example = "945765832", required = true)
    private String imoNumber;

    @Schema(description = "Year vessel was built", example = "2015")
    private String builtYear;

    @Schema(description = "Month vessel was built", example = "SEP")
    private String builtMonth;

    @Schema(description = "Vessel operational status", example = "ACTIVE")
    private String status;

    @Schema(description = "Gross tonnage (GRT)", example = "66280")
    private Integer grossTonnage;

    @Schema(description = "Length overall (LOA)", example = "276.2")
    private Double loa;

    @Schema(description = "Vessel width (beam / breadth)", example = "32.2")
    private Double breadth;

    @Schema(description = "Summer draft", example = "14.0")
    private Double draft;

    @Schema(description = "Deadweight tonnage (DWT)", example = "68288")
    private Double deadWeight;

    @Schema(description = "Reefer plug points", example = "500")
    private Integer reeferPlugs;

    @Schema(description = "Vessel TEU capacity", example = "5527")
    private Integer capacityTeu;

    @Schema(description = "Vessel registered country / flag", example = "PORTUGAL")
    private String flag;

    @Schema(description = "Vessel communication call sign", example = "CQDD")
    private String callSign;

    // Constructors
    public VesselDoc() {}

    public VesselDoc(String vesselCode, String vesselName, String imoNumber) {
        this.vesselCode = vesselCode;
        this.vesselName = vesselName;
        this.imoNumber = imoNumber;
    }

    // Getters and setters
    public String getVesselCode() {
        return vesselCode;
    }

    public void setVesselCode(String vesselCode) {
        this.vesselCode = vesselCode;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public String getImoNumber() {
        return imoNumber;
    }

    public void setImoNumber(String imoNumber) {
        this.imoNumber = imoNumber;
    }

    public String getBuiltYear() {
        return builtYear;
    }

    public void setBuiltYear(String builtYear) {
        this.builtYear = builtYear;
    }

    public String getBuiltMonth() {
        return builtMonth;
    }

    public void setBuiltMonth(String builtMonth) {
        this.builtMonth = builtMonth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getGrossTonnage() {
        return grossTonnage;
    }

    public void setGrossTonnage(Integer grossTonnage) {
        this.grossTonnage = grossTonnage;
    }

    public Double getLoa() {
        return loa;
    }

    public void setLoa(Double loa) {
        this.loa = loa;
    }

    public Double getBreadth() {
        return breadth;
    }

    public void setBreadth(Double breadth) {
        this.breadth = breadth;
    }

    public Double getDraft() {
        return draft;
    }

    public void setDraft(Double draft) {
        this.draft = draft;
    }

    public Double getDeadWeight() {
        return deadWeight;
    }

    public void setDeadWeight(Double deadWeight) {
        this.deadWeight = deadWeight;
    }

    public Integer getReeferPlugs() {
        return reeferPlugs;
    }

    public void setReeferPlugs(Integer reeferPlugs) {
        this.reeferPlugs = reeferPlugs;
    }

    public Integer getCapacityTeu() {
        return capacityTeu;
    }

    public void setCapacityTeu(Integer capacityTeu) {
        this.capacityTeu = capacityTeu;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }
}
