package com.solverminds.summary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.enums.BlStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import com.svm.ecom.constants.DateFormatConstants;
/**
 * DTO for Dashboard Summary data from usp_getdashboardecomboookingsummary stored procedure
 */
@Schema(description = ModelDocs.DashboardSummaryDto.DESC)
public class DashboardSummaryDto {
    
    @Schema(description = ModelDocs.DashboardSummaryDto.ONLINE_BOOK_NO, example = ModelDocs.DashboardSummaryDto.EX_ONLINE_BOOK_NO)
    @JsonProperty("onlineBookNo")
    private String onlineRefNo;
    
    @Schema(description = ModelDocs.DashboardSummaryDto.ORIGIN_PORT_ID, example = ModelDocs.DashboardSummaryDto.EX_ORIGIN_PORT)
    @JsonProperty("originPortId")
    private String originPort;
    
    @Schema(description = ModelDocs.DashboardSummaryDto.FINAL_PORT_ID, example = ModelDocs.DashboardSummaryDto.EX_FINAL_PORT)
    @JsonProperty("finalPortId")
    private String finalPortIc;
    
    @Schema(description = ModelDocs.DashboardSummaryDto.ORIGIN_PORT_DESC, example = ModelDocs.DashboardSummaryDto.EX_ORIGIN_PORT_DESC)
    @JsonProperty("originPortDesc")
    private String originPortSecond;
    
    @Schema(description = ModelDocs.DashboardSummaryDto.FINAL_PORT_DESC, example = ModelDocs.DashboardSummaryDto.EX_FINAL_PORT_DESC)
    @JsonProperty("finalPortDesc")
    private String finalPortD;
    
    @Schema(description = ModelDocs.DashboardSummaryDto.AGENCY_ID, example = ModelDocs.DashboardSummaryDto.EX_AGENCY_ID)
    @JsonProperty("agencyId")
    private String agencyId;
    
    @Schema(description = "Departure date/time at load port in port local time (ISO 8601 with offset)", example = "2025-09-10T14:30:00+08:00")
    @JsonProperty("departureDate")
    private OffsetDateTime polAt;
    
    @Schema(description = ModelDocs.DashboardSummaryDto.LOAD_PORT_ID, example = ModelDocs.DashboardSummaryDto.EX_LOAD_PORT)
    @JsonProperty("loadPortId")
    private String loadPortIc;
    
    @Schema(description = ModelDocs.DashboardSummaryDto.DISCHARGE_PORT_ID, example = ModelDocs.DashboardSummaryDto.EX_DISCHARGE_PORT)
    @JsonProperty("dischargePortId")
    private String dischargeL;
    
    @Schema(type = "string", format = "date-time", description = ModelDocs.DashboardSummaryDto.BOOK_DATE, example = "2025-09-10T00:00:00Z")
    @JsonProperty("bookDate")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime bookDate;
    
    @Schema(description = ModelDocs.DashboardSummaryDto.BOOKING_NUMBER, example = ModelDocs.DashboardSummaryDto.EX_BOOKING_NUMBER)
    @JsonProperty("bookingNumber")
    private String bookNo;
    
    @Schema(description = ModelDocs.DashboardSummaryDto.STATUS, example = ModelDocs.DashboardSummaryDto.EX_STATUS)
    @JsonProperty("status")
    private BlStatus status;
    
    @Schema(description = ModelDocs.DashboardSummaryDto.BL_NUMBER, example = ModelDocs.DashboardSummaryDto.EX_BL_NUMBER)
    @JsonProperty("blNumber")
    private String blNo;
    
    @Schema(description = ModelDocs.DashboardSummaryDto.CONTAINER_NO, example = ModelDocs.DashboardSummaryDto.EX_CONTAINER_NO)
    @JsonProperty("containerno")
    private String containerL;
    
    @Schema(description = ModelDocs.DashboardSummaryDto.LATEST_ACTIVITY_CODE, example = ModelDocs.DashboardSummaryDto.EX_LATEST_ACTIVITY)
    @JsonProperty("latestActivityCode")
    private String latestAct;
    
    @Schema(description = ModelDocs.DashboardSummaryDto.TUES_COUNT, example = ModelDocs.DashboardSummaryDto.EX_TUES_COUNT)
    @JsonProperty("tuesCount")
    private Integer tuesCount;

    // Default constructor
    public DashboardSummaryDto() {}

    // All-args constructor
    public DashboardSummaryDto(String onlineRefNo, String originPort, String finalPortIc,
                                    String originPortSecond, String finalPortD, String agencyId, OffsetDateTime polAt,
                                    String loadPortIc, String dischargeL, LocalDateTime bookDate, String bookNo,
                                    BlStatus status, String blNo, String containerL, String latestAct, Integer tuesCount) {
        this.onlineRefNo = onlineRefNo;
        this.originPort = originPort;
        this.finalPortIc = finalPortIc;
        this.originPortSecond = originPortSecond;
        this.finalPortD = finalPortD;
        this.agencyId = agencyId;
        this.polAt = polAt;
        this.loadPortIc = loadPortIc;
        this.dischargeL = dischargeL;
        this.bookDate = bookDate;
        this.bookNo = bookNo;
        this.status = status;
        this.blNo = blNo;
        this.containerL = containerL;
        this.latestAct = latestAct;
        this.tuesCount = tuesCount;
    }

    // Getters and Setters
    public String getOnlineRefNo() {
        return onlineRefNo;
    }

    public void setOnlineRefNo(String onlineRefNo) {
        this.onlineRefNo = onlineRefNo;
    }

    public String getOriginPort() {
        return originPort;
    }

    public void setOriginPort(String originPort) {
        this.originPort = originPort;
    }

    public String getFinalPortIc() {
        return finalPortIc;
    }

    public void setFinalPortIc(String finalPortIc) {
        this.finalPortIc = finalPortIc;
    }

    public String getOriginPortSecond() {
        return originPortSecond;
    }

    public void setOriginPortSecond(String originPortSecond) {
        this.originPortSecond = originPortSecond;
    }

    public String getFinalPortD() {
        return finalPortD;
    }

    public void setFinalPortD(String finalPortD) {
        this.finalPortD = finalPortD;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public OffsetDateTime getPolAt() {
        return polAt;
    }

    public void setPolAt(OffsetDateTime polAt) {
        this.polAt = polAt;
    }

    public String getLoadPortIc() {
        return loadPortIc;
    }

    public void setLoadPortIc(String loadPortIc) {
        this.loadPortIc = loadPortIc;
    }

    public String getDischargeL() {
        return dischargeL;
    }

    public void setDischargeL(String dischargeL) {
        this.dischargeL = dischargeL;
    }

    public LocalDateTime getBookDate() {
        return bookDate;
    }

    public void setBookDate(LocalDateTime bookDate) {
        this.bookDate = bookDate;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public BlStatus getStatus() {
        return status;
    }

    public void setStatus(BlStatus status) {
        this.status = status;
    }

    public String getBlNo() {
        return blNo;
    }

    public void setBlNo(String blNo) {
        this.blNo = blNo;
    }

    public String getContainerL() {
        return containerL;
    }

    public void setContainerL(String containerL) {
        this.containerL = containerL;
    }

    public String getLatestAct() {
        return latestAct;
    }

    public void setLatestAct(String latestAct) {
        this.latestAct = latestAct;
    }

    public Integer getTuesCount() {
        return tuesCount;
    }

    public void setTuesCount(Integer tuesCount) {
        this.tuesCount = tuesCount;
    }

    @Override
    public String toString() {
        return "DashboardSummaryDto{" +
                "onlineRefNo='" + onlineRefNo + '\'' +
                ", originPort='" + originPort + '\'' +
                ", finalPortIc='" + finalPortIc + '\'' +
                ", originPortSecond='" + originPortSecond + '\'' +
                ", finalPortD='" + finalPortD + '\'' +
                ", agencyId='" + agencyId + '\'' +
                ", polAt='" + polAt + '\'' +
                ", loadPortIc='" + loadPortIc + '\'' +
                ", dischargeL='" + dischargeL + '\'' +
                ", bookDate='" + bookDate + '\'' +
                ", bookNo='" + bookNo + '\'' +
                ", status='" + status + '\'' +
                ", blNo='" + blNo + '\'' +
                ", containerL='" + containerL + '\'' +
                ", latestAct='" + latestAct + '\'' +
                ", tuesCount=" + tuesCount +
                '}';
    }
}
