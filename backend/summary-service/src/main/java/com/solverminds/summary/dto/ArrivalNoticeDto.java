package com.solverminds.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.enums.EcomPrintStatus;
import com.svm.ecom.common.enums.Bound;
import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;

import java.time.OffsetDateTime;
/**
 * DTO for Arrival Notice data
 */
@Schema(description = ModelDocs.ArrivalNoticeDto.DESC)
public class ArrivalNoticeDto {
    
    @Schema(description = ModelDocs.ArrivalNoticeDto.CUSTOMER_CODE, example = ModelDocs.ArrivalNoticeDto.EX_CUSTOMER_CODE)
    @JsonProperty("customerCode")
    private String custCode;
    
    @Schema(description = ModelDocs.ArrivalNoticeDto.BL_NUMBER, example = ModelDocs.ArrivalNoticeDto.EX_BL_NUMBER)
    @JsonProperty("blNumber")
    private String blNumber;
    
    @Schema(description = ModelDocs.ArrivalNoticeDto.ARRIVAL_NOTICE_NO, example = ModelDocs.ArrivalNoticeDto.EX_ARRIVAL_NOTICE_NO)
    @JsonProperty("arrivalNoticeNo")
    private String arrivalNoticeNo;
    
    @Schema(description = ModelDocs.ArrivalNoticeDto.ORIGIN_PORT_ID, example = ModelDocs.ArrivalNoticeDto.EX_ORIGIN_PORT)
    @JsonProperty("originPortId")
    private String originPortId;
    
    @Schema(description = ModelDocs.ArrivalNoticeDto.FINAL_PORT_ID, example = ModelDocs.ArrivalNoticeDto.EX_FINAL_PORT)
    @JsonProperty("finalPortId")
    private String finalPortId;
    
    @Schema(description = ModelDocs.ArrivalNoticeDto.VESSEL_CODE, example = ModelDocs.ArrivalNoticeDto.EX_VESSEL_CODE)
    @JsonProperty("vesselCode")
    private String vesselCode;
    
    @Schema(description = ModelDocs.ArrivalNoticeDto.VOYAGE, example = ModelDocs.ArrivalNoticeDto.EX_VOYAGE)
    @JsonProperty("voyage")
    private String voyage;
    
    @Schema(description = ModelDocs.ArrivalNoticeDto.BOUND, example = ModelDocs.ArrivalNoticeDto.EX_BOUND)
    @JsonProperty("bound")
    private Bound bound;
    
    @Schema(description = ModelDocs.ArrivalNoticeDto.HBL_NUMBER, example = ModelDocs.ArrivalNoticeDto.EX_HBL_NUMBER)
    @JsonProperty("hblNumber")
    private String hblNumber;
    
    @Schema(description = "Arrival date at final port in port local time (ISO 8601 with offset)", example = "2025-09-26T02:00:00+04:00")
    @JsonProperty("arrDate")
    private OffsetDateTime arrDate;
    
    @Schema(description = ModelDocs.ArrivalNoticeDto.POD_TERMINAL, example = ModelDocs.ArrivalNoticeDto.EX_POD_TERMINAL)
    @JsonProperty("podTerminal")
    private String podTerminal;
    
    @Schema(description = ModelDocs.ArrivalNoticeDto.ECOM_PRINT_STATUS, example = ModelDocs.ArrivalNoticeDto.EX_ECOM_PRINT_STATUS)
    @JsonProperty("ecomPrintStatus")
    private EcomPrintStatus ecomPrintStatus;

    // Default constructor
    public ArrivalNoticeDto() {}

    // All-args constructor
    public ArrivalNoticeDto(String custCode, String blNumber, String arrivalNoticeNo,
                           String originPortId, String finalPortId, String vesselCode,
                           String voyage, Bound bound, String hblNumber, OffsetDateTime arrDate,
                           String podTerminal, EcomPrintStatus ecomPrintStatus) {
        this.custCode = custCode;
        this.blNumber = blNumber;
        this.arrivalNoticeNo = arrivalNoticeNo;
        this.originPortId = originPortId;
        this.finalPortId = finalPortId;
        this.vesselCode = vesselCode;
        this.voyage = voyage;
        this.bound = bound;
        this.hblNumber = hblNumber;
        this.arrDate = arrDate;
        this.podTerminal = podTerminal;
        this.ecomPrintStatus = ecomPrintStatus;
    }

    // Getters and Setters
    public String getCustCode() {
        return custCode;
    }

    public void setCustCode(String custCode) {
        this.custCode = custCode;
    }

    public String getBlNumber() {
        return blNumber;
    }

    public void setBlNumber(String blNumber) {
        this.blNumber = blNumber;
    }

    public String getArrivalNoticeNo() {
        return arrivalNoticeNo;
    }

    public void setArrivalNoticeNo(String arrivalNoticeNo) {
        this.arrivalNoticeNo = arrivalNoticeNo;
    }

    public String getOriginPortId() {
        return originPortId;
    }

    public void setOriginPortId(String originPortId) {
        this.originPortId = originPortId;
    }

    public String getFinalPortId() {
        return finalPortId;
    }

    public void setFinalPortId(String finalPortId) {
        this.finalPortId = finalPortId;
    }

    public String getVesselCode() {
        return vesselCode;
    }

    public void setVesselCode(String vesselCode) {
        this.vesselCode = vesselCode;
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

    public String getHblNumber() {
        return hblNumber;
    }

    public void setHblNumber(String hblNumber) {
        this.hblNumber = hblNumber;
    }

    public OffsetDateTime getArrDate() {
        return arrDate;
    }

    public void setArrDate(OffsetDateTime arrDate) {
        this.arrDate = arrDate;
    }

    public String getPodTerminal() {
        return podTerminal;
    }

    public void setPodTerminal(String podTerminal) {
        this.podTerminal = podTerminal;
    }

    public EcomPrintStatus getEcomPrintStatus() {
        return ecomPrintStatus;
    }

    public void setEcomPrintStatus(EcomPrintStatus ecomPrintStatus) {
        this.ecomPrintStatus = ecomPrintStatus;
    }

    @Override
    public String toString() {
        return "ArrivalNoticeDto{" +
                "custCode='" + custCode + '\'' +
                ", blNumber='" + blNumber + '\'' +
                ", arrivalNoticeNo='" + arrivalNoticeNo + '\'' +
                ", originPortId='" + originPortId + '\'' +
                ", finalPortId='" + finalPortId + '\'' +
                ", vesselCode='" + vesselCode + '\'' +
                ", voyage='" + voyage + '\'' +
                ", bound='" + bound + '\'' +
                ", hblNumber='" + hblNumber + '\'' +
                ", arrDate=" + arrDate +
                ", podTerminal='" + podTerminal + '\'' +
                ", ecomPrintStatus='" + ecomPrintStatus + '\'' +
                '}';
    }
}
