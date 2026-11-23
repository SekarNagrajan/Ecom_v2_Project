package com.solverminds.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.enums.BookingSource;
import com.svm.ecom.common.enums.BookingStatus;
import com.svm.ecom.constants.DateFormatConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * DTO for transformed Booking Summary response format
 * Used by /summaryresponse endpoint to return data with renamed fields
 */
@Schema(description = ModelDocs.BookingSummaryResponseDto.DESC)
public class BookingSummaryResponseDto {
    
    @JsonProperty("bookingNumber")
    @Schema(description = ModelDocs.BookingSummaryResponseDto.BOOKING_NUMBER, example = ModelDocs.BookingSummaryResponseDto.EX_BOOKING_NUMBER)
    private String bookingNumber;
    
    @JsonProperty("onlineBookNo")
    @Schema(description = ModelDocs.BookingSummaryResponseDto.ONLINE_BOOK_NO, example = ModelDocs.BookingSummaryResponseDto.EX_ONLINE_BOOK_NO)
    private String onlineBookNo;
    
    @JsonProperty("originPortId")
    @Schema(description = ModelDocs.BookingSummaryResponseDto.ORIGIN_PORT_ID, example = ModelDocs.BookingSummaryResponseDto.EX_ORIGIN_PORT)
    private String originPortId;
    
    @JsonProperty("finalPortId")
    @Schema(description = ModelDocs.BookingSummaryResponseDto.FINAL_PORT_ID, example = ModelDocs.BookingSummaryResponseDto.EX_FINAL_PORT)
    private String finalPortId;
    
    @JsonProperty("createdDate")
    @Schema(
        type = "string",
        format = "date-time",
        description = "created date of booking",
        example = "2025-09-10T14:30:00Z"
    )
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime createdDate;
    
    @JsonProperty("changeDate")
    @Schema(
        type = "string",
        format = "date-time",
        description = "last modified date of booking",
        example = "2025-09-10T14:30:00Z"
    )
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime changeDate;
    
    @JsonProperty("status")
    @Schema(description = ModelDocs.BookingSummaryResponseDto.STATUS, implementation = BookingStatus.class)
    private BookingStatus status;
    
    @JsonIgnore
    @Schema(hidden = true)
    private String ecomLrpStatus;
    
    @JsonProperty("submittedBy")
    @Schema(description = ModelDocs.BookingSummaryResponseDto.SUBMITTED_BY, example = ModelDocs.BookingSummaryResponseDto.EX_USER)
    private String submittedBy;
    
    @JsonProperty("agencyRefNo")
    @Schema(description = ModelDocs.BookingSummaryResponseDto.AGENCY_REF_NO, example = ModelDocs.BookingSummaryResponseDto.EX_AGENCY_REF)
    private String agencyRefNo;
    
    @JsonProperty("agencyId")
    @Schema(description = ModelDocs.BookingSummaryResponseDto.AGENCY_ID, example = ModelDocs.BookingSummaryResponseDto.EX_AGENCY_ID)
    private String agencyId;
    
    @JsonProperty("confirmedDate")
    @Schema(description = ModelDocs.BookingSummaryResponseDto.CONFIRMED_DATE, example = ModelDocs.BookingSummaryResponseDto.EX_CONFIRMED_DATE)
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime confirmedDate;
    
    @JsonProperty("statusDesc")
    @Schema(description = ModelDocs.BookingSummaryResponseDto.STATUS_DESC, example = ModelDocs.BookingSummaryResponseDto.EX_STATUS_DESC)
    private String statusDesc;
    
    @JsonProperty("tuesCount")
    @Schema(description = ModelDocs.BookingSummaryResponseDto.TUES_COUNT, example = ModelDocs.BookingSummaryResponseDto.EX_TUES_COUNT)
    private Integer tuesCount;
    
    @JsonProperty("bookingCreatedUser")
    @Schema(description = ModelDocs.BookingSummaryResponseDto.BOOKING_CREATED_USER, example = ModelDocs.BookingSummaryResponseDto.EX_BOOKING_CREATED_USER)
    private String bookingCreatedUser;
    
    @JsonProperty("departureDate")
    @Schema(description = "Departure date/time at origin port in port local time (ISO 8601 with offset)", example = "2025-09-10T14:30:00+08:00")
    private OffsetDateTime departureDate;

    @JsonProperty("eta")
    @Schema(description = "Estimated time of arrival at destination (from last leg when available)", example = "2025-09-25T10:00:00Z")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime eta;

    @JsonProperty("originName")
    @Schema(description = "Origin port name", example = "NINGBO, CHINA")
    private String originName;

    @JsonProperty("deliveryName")
    @Schema(description = "Delivery port name", example = "ROTTERDAM")
    private String deliveryName;

    @JsonProperty("polName")
    @Schema(description = "Port of loading name", example = "NINGBO, CHINA")
    private String polName;

    @JsonProperty("podName")
    @Schema(description = "Port of discharge name", example = "KOPER, SLOVENIA")
    private String podName;

    @JsonProperty("blNumber")
    @Schema(description = "Bill of Lading number (Master BL)", example = "BL20240115001")
    private String blNumber;

    @JsonProperty("blStatus")
    @Schema(description = "BL status: D (Draft), C (Confirmed), I (Issued), V (Cancelled)", example = "C")
    private String blStatus;

    @JsonProperty("loadPortId")
    @Schema(description = "Port of loading code", example = "CNNGB")
    private String loadPortId;

    @JsonProperty("dischargePortId")
    @Schema(description = "Port of discharge code", example = "NLRTM")
    private String dischargePortId;

    @JsonProperty("bookingSource")
    @Schema(
        description = ModelDocs.BookingSummaryResponseDto.BOOKING_SOURCE,
        accessMode = Schema.AccessMode.READ_ONLY,
        example = ModelDocs.BookingSummaryResponseDto.EX_BOOKING_SOURCE,
        implementation = BookingSource.class
    )
    private BookingSource bookingSource;

    // Default constructor
    public BookingSummaryResponseDto() {}

    // Constructor removed - direct mapping from database now handled by BookingSummaryMapper

    // Getters and Setters
    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public String getOnlineBookNo() {
        return onlineBookNo;
    }

    public void setOnlineBookNo(String onlineBookNo) {
        this.onlineBookNo = onlineBookNo;
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getEcomLrpStatus() {
        return ecomLrpStatus;
    }

    public void setEcomLrpStatus(String ecomLrpStatus) {
        this.ecomLrpStatus = ecomLrpStatus;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getAgencyRefNo() {
        return agencyRefNo;
    }

    public void setAgencyRefNo(String agencyRefNo) {
        this.agencyRefNo = agencyRefNo;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public LocalDateTime getConfirmedDate() {
        return confirmedDate;
    }

    public void setConfirmedDate(LocalDateTime confirmedDate) {
        this.confirmedDate = confirmedDate;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public Integer getTuesCount() {
        return tuesCount;
    }

    public void setTuesCount(Integer tuesCount) {
        this.tuesCount = tuesCount;
    }

    public String getBookingCreatedUser() {
        return bookingCreatedUser;
    }

    public void setBookingCreatedUser(String bookingCreatedUser) {
        this.bookingCreatedUser = bookingCreatedUser;
    }

    public OffsetDateTime getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(OffsetDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDateTime getEta() {
        return eta;
    }

    public void setEta(LocalDateTime eta) {
        this.eta = eta;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getPolName() {
        return polName;
    }

    public void setPolName(String polName) {
        this.polName = polName;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public String getBlNumber() {
        return blNumber;
    }

    public void setBlNumber(String blNumber) {
        this.blNumber = blNumber;
    }

    public String getBlStatus() {
        return blStatus;
    }

    public void setBlStatus(String blStatus) {
        this.blStatus = blStatus;
    }

    public String getLoadPortId() {
        return loadPortId;
    }

    public void setLoadPortId(String loadPortId) {
        this.loadPortId = loadPortId;
    }

    public String getDischargePortId() {
        return dischargePortId;
    }

    public void setDischargePortId(String dischargePortId) {
        this.dischargePortId = dischargePortId;
    }

    public BookingSource getBookingSource() {
        return bookingSource;
    }

    public void setBookingSource(BookingSource bookingSource) {
        this.bookingSource = bookingSource;
    }

    @Override
    public String toString() {
        return "BookingSummaryResponseDto{" +
                "bookingNumber='" + bookingNumber + '\'' +
                ", onlineBookNo='" + onlineBookNo + '\'' +
                ", originPortId='" + originPortId + '\'' +
                ", finalPortId='" + finalPortId + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", changeDate='" + changeDate + '\'' +
                ", status='" + status + '\'' +
                ", ecomLrpStatus='" + ecomLrpStatus + '\'' +
                ", submittedBy='" + submittedBy + '\'' +
                ", agencyRefNo='" + agencyRefNo + '\'' +
                ", agencyId='" + agencyId + '\'' +
                ", confirmedDate='" + confirmedDate + '\'' +
                ", statusDesc='" + statusDesc + '\'' +
                ", tuesCount=" + tuesCount +
                ", bookingCreatedUser='" + bookingCreatedUser + '\'' +
                ", departureDate='" + departureDate + '\'' +
                ", eta='" + eta + '\'' +
                ", originName='" + originName + '\'' +
                ", deliveryName='" + deliveryName + '\'' +
                ", polName='" + polName + '\'' +
                ", podName='" + podName + '\'' +
                ", blNumber='" + blNumber + '\'' +
                ", blStatus='" + blStatus + '\'' +
                ", loadPortId='" + loadPortId + '\'' +
                ", dischargePortId='" + dischargePortId + '\'' +
                ", bookingSource=" + bookingSource +
                '}';
    }
}
