package com.solverminds.booking.dto.v2;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.svm.ecom.common.enums.BlStatus;
import com.svm.ecom.common.enums.BookingStatus;
import com.svm.ecom.constants.DateFormatConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Shortened information about a single booking for list view in V2 API.
 * Matches OpenAPI schema PagedBookingListItem.
 */
@Schema(
    name = "PagedBookingListItem",
    description = "Shortened information about a single booking.",
    example = "{\"id\":\"TSHG123\",\"bookingRequestId\":\"BKON123\",\"originId\":\"CNNGB\",\"originName\":\"NINGBO, CHINA\",\"deliveryId\":\"HUBUD\",\"deliveryName\":\"BUDAPEST\",\"polId\":\"CNNGB\",\"polName\":\"NINGBO, CHINA\",\"podId\":\"SIKOP\",\"podName\":\"KOPER, SLOVENIA\",\"status\":\"D\",\"agencyId\":\"INDMUM\",\"agencyRefNo\":\"AEG1232\",\"teus\":20,\"etd\":\"2026-03-19T17:03:00+01:00\",\"eta\":\"2026-03-26T17:03:00+01:00\",\"createdBy\":\"john.doe@example.com\",\"createdAt\":\"2026-01-02T08:00:00Z\",\"updatedAt\":\"2026-01-02T08:00:00Z\",\"confirmedAt\":\"2026-01-02T08:00:00Z\"}"
)
public class PagedBookingListItemV2 {

    @Schema(description = "Confirmed booking ID (TSHG number). Null for draft/unconfirmed bookings.", example = "TSHG123")
    private String id;

    @Schema(description = "Booking request ID (BKON number).", example = "BKON123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bookingRequestId;

    @Schema(description = "Origin location code.", example = "CNNGB", requiredMode = Schema.RequiredMode.REQUIRED)
    private String originId;

    @Schema(description = "Name of the origin location.", example = "NINGBO, CHINA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String originName;

    @Schema(description = "Delivery location code.", example = "HUBUD", requiredMode = Schema.RequiredMode.REQUIRED)
    private String deliveryId;

    @Schema(description = "Name of the delivery location.", example = "BUDAPEST", requiredMode = Schema.RequiredMode.REQUIRED)
    private String deliveryName;

    @Schema(description = "Port of loading code.", example = "CNNGB", requiredMode = Schema.RequiredMode.REQUIRED)
    private String polId;

    @Schema(description = "Name of the port of loading.", example = "NINGBO, CHINA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String polName;

    @Schema(description = "Port of discharge code.", example = "SIKOP", requiredMode = Schema.RequiredMode.REQUIRED)
    private String podId;

    @Schema(description = "Name of the port of discharge.", example = "KOPER, SLOVENIA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String podName;

    @Schema(description = "Booking status code (BookingStatus): D – ECOM Draft, S – Submitted, P – In-Progress, C – Confirmed, L – Cancelled, V – Rejected, T – Template, I – Void. Returns enum code only.", example = "D", implementation = BookingStatus.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private BookingStatus status;

    @Schema(description = "Agency code.", example = "CNMNGB")
    private String agencyId;

    @Schema(description = "Agency reference number.", example = "AEG1232")
    private String agencyRefNo;

    @Schema(description = "Total number of TEUs for the booking.", example = "20", minimum = "0")
    private Integer teus;

    @Schema(description = "Estimated time of departure (ISO 8601 with timezone offset).", example = "2025-09-19T03:00:00+05:30", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.ISO_DATE_TIME_ZONE)
    private OffsetDateTime etd;

    @Schema(description = "Estimated time of arrival (ISO 8601 with timezone offset).", example = "2025-09-26T03:00:00+05:30", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.ISO_DATE_TIME_ZONE)
    private OffsetDateTime eta;

    @Schema(description = "Bill of Lading number.", example = "BL12345678")
    private String blNumber;

    @Schema(description = "Bill of Lading status (D, C, I, V).", example = "D")
    private BlStatus blStatus;

    @Schema(description = "The user who created the booking.", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String createdBy;

    @Schema(description = "The date and time when the booking was created.", example = "2026-01-02T08:00:00Z", type = "string", format = "date-time", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime createdAt;

    @Schema(description = "The date and time when the booking was last updated.", example = "2026-01-02T08:00:00Z", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime updatedAt;

    @Schema(description = "The date and time when the booking was confirmed.", example = "2026-01-02T08:00:00Z", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime confirmedAt;

    public PagedBookingListItemV2() {
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingRequestId() {
        return bookingRequestId;
    }

    public void setBookingRequestId(String bookingRequestId) {
        this.bookingRequestId = bookingRequestId;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getPolId() {
        return polId;
    }

    public void setPolId(String polId) {
        this.polId = polId;
    }

    public String getPolName() {
        return polName;
    }

    public void setPolName(String polName) {
        this.polName = polName;
    }

    public String getPodId() {
        return podId;
    }

    public void setPodId(String podId) {
        this.podId = podId;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getAgencyRefNo() {
        return agencyRefNo;
    }

    public void setAgencyRefNo(String agencyRefNo) {
        this.agencyRefNo = agencyRefNo;
    }

    public Integer getTeus() {
        return teus;
    }

    public void setTeus(Integer teus) {
        this.teus = teus;
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

    public String getBlNumber() {
        return blNumber;
    }

    public void setBlNumber(String blNumber) {
        this.blNumber = blNumber;
    }

    public BlStatus getBlStatus() {
        return blStatus;
    }

    public void setBlStatus(BlStatus blStatus) {
        this.blStatus = blStatus;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
}

