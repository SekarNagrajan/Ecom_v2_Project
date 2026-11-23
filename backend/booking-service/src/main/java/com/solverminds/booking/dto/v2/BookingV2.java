package com.solverminds.booking.dto.v2;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.svm.ecom.common.enums.BookingSource;
import com.svm.ecom.common.enums.BookingStatus;
import com.svm.ecom.common.enums.CarriageType;
import com.svm.ecom.common.enums.EnsFillingType;
import com.svm.ecom.common.enums.EnsType;
import com.svm.ecom.common.enums.FreightPaymentType;
import com.svm.ecom.common.enums.YesNoFlag;
import com.svm.ecom.constants.DateFormatConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Full booking response DTO for V2 API.
 * Used for GET by ID and POST create responses.
 */
@Schema(name = "Booking", description = "Details about a booking")
public class BookingV2 {

    @Schema(description = "Confirmed booking ID (TSHG number). Null for draft/unconfirmed bookings.", example = "TSHG123")
    private String id;

    @Schema(description = "Booking request ID (BKON number). Always present.", example = "BKON123")
    private String bookingRequestId;

    @Schema(description = "Origin location code", example = "CNNGB")
    private String originId;

    @Schema(description = "Name of the origin location", example = "NINGBO, CHINA")
    private String originName;

    @Schema(description = "Delivery location code", example = "ESBCN")
    private String deliveryId;

    @Schema(description = "Name of the delivery location", example = "BARCELONA, SPAIN")
    private String deliveryName;

    @Schema(description = "Port of loading code", example = "CNNGB")
    private String polId;

    @Schema(description = "Name of the port of loading", example = "NINGBO, CHINA")
    private String polName;

    @Schema(description = "Port of discharge code", example = "ESBCN")
    private String podId;

    @Schema(description = "Name of the port of discharge", example = "BARCELONA, SPAIN")
    private String podName;

    @Schema(description = "Estimated time of departure in POL local time (ISO 8601 with offset).", example = "2025-09-19T03:00:00+05:30", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime etd;

    @Schema(description = "Estimated time of arrival in POD local time (ISO 8601 with offset).", example = "2025-09-26T03:00:00+05:30", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime eta;

    @Schema(description = "Booking status code (BookingStatus): D – ECOM Draft, S – Submitted, P – In-Progress, C – Confirmed, L – Cancelled, V – Rejected, T – Template, I – Void. Returns enum code only.", example = "D", implementation = BookingStatus.class)
    private BookingStatus status;

    @Schema(description = "Agency code", example = "CNMNGB")
    private String agencyId;

    @Schema(description = "Agency reference number", example = "AEG1232")
    private String agencyRefNo;

    @Schema(description = "Total number of TEUs for the booking", example = "20")
    private Integer teus;

    @Schema(description = "Pre-carriage type", example = "C")
    private CarriageType preCarriage;

    @Schema(description = "On-carriage type", example = "C")
    private CarriageType onCarriage;

    @Schema(description = "Any additional remarks or instructions", example = "Handle with care")
    private String remarks;

    @Schema(description = "Customer code for the booking", example = "20121345938")
    private String customerCode;

    @Schema(description = "Ocean freight payment type", example = "P")
    private FreightPaymentType oceanFreight;

    @Schema(description = "Name of the booking template", example = "MUM_AEJ_20DC")
    private String templateName;

    @Schema(description = "Date when cargo is ready", example = "2026-01-02T08:00:00Z", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime cargoReadyDate;

    @Schema(description = "Indicates whether this booking is an amendment", example = "false")
    private Boolean isAmendment;

    @Schema(description = "Contract reference number (also known as rateRefNo)", example = "CNMNGB78271")
    private String contractReference;

    @Schema(description = "Export reference number", example = "E98211")
    private String exportRefNo;

    @Schema(
        description = "Customer reference number",
        example = "PO829281",
        maxLength = 200)
    private String customerReference;

    @Schema(description = "ENS filing type", example = "S")
    private EnsFillingType ensFilingType;

    @Schema(description = "ENS type", example = "S")
    private EnsType ensType;

    @Schema(description = "T2L filing flag (YesNoFlag: Y - Yes, N - No)", example = "N")
    private YesNoFlag t2lFiling;

    @Schema(description = "List of customer details")
    private List<BookingCustomerV2> customers;

    @Schema(description = "List of container details")
    private List<BookingContainerV2> containers;

    @Schema(description = "Route details")
    private BookingRouteV2 route;

    @Schema(description = "List of file attachments")
    private List<BookingAttachmentV2> files;

    @Schema(description = "The user who created the booking", example = "john.doe@example.com")
    private String createdBy;

    @Schema(description = "The date and time when the booking was created", example = "2026-01-02T08:00:00Z", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime createdAt;

    @Schema(description = "The date and time when the booking was last updated", example = "2026-01-02T08:00:00Z", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime updatedAt;

    @Schema(description = "The date and time when the booking was confirmed", example = "2026-01-02T08:00:00Z", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime confirmedAt;

    @Schema(
        description = "Booking source (read-only)",
        accessMode = Schema.AccessMode.READ_ONLY,
        example = "C",
        implementation = BookingSource.class
    )
    private BookingSource bookingSource;

    public BookingV2() {
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

    public CarriageType getPreCarriage() {
        return preCarriage;
    }

    public void setPreCarriage(CarriageType preCarriage) {
        this.preCarriage = preCarriage;
    }

    public CarriageType getOnCarriage() {
        return onCarriage;
    }

    public void setOnCarriage(CarriageType onCarriage) {
        this.onCarriage = onCarriage;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public FreightPaymentType getOceanFreight() {
        return oceanFreight;
    }

    public void setOceanFreight(FreightPaymentType oceanFreight) {
        this.oceanFreight = oceanFreight;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public LocalDateTime getCargoReadyDate() {
        return cargoReadyDate;
    }

    public void setCargoReadyDate(LocalDateTime cargoReadyDate) {
        this.cargoReadyDate = cargoReadyDate;
    }

    public Boolean getIsAmendment() {
        return isAmendment;
    }

    public void setIsAmendment(Boolean isAmendment) {
        this.isAmendment = isAmendment;
    }

    public String getContractReference() {
        return contractReference;
    }

    public void setContractReference(String contractReference) {
        this.contractReference = contractReference;
    }

    public String getExportRefNo() {
        return exportRefNo;
    }

    public void setExportRefNo(String exportRefNo) {
        this.exportRefNo = exportRefNo;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public EnsFillingType getEnsFilingType() {
        return ensFilingType;
    }

    public void setEnsFilingType(EnsFillingType ensFilingType) {
        this.ensFilingType = ensFilingType;
    }

    public EnsType getEnsType() {
        return ensType;
    }

    public void setEnsType(EnsType ensType) {
        this.ensType = ensType;
    }

    public YesNoFlag getT2lFiling() {
        return t2lFiling;
    }

    public void setT2lFiling(YesNoFlag t2lFiling) {
        this.t2lFiling = t2lFiling;
    }

    public List<BookingCustomerV2> getCustomers() {
        return customers;
    }

    public void setCustomers(List<BookingCustomerV2> customers) {
        this.customers = customers;
    }

    public List<BookingContainerV2> getContainers() {
        return containers;
    }

    public void setContainers(List<BookingContainerV2> containers) {
        this.containers = containers;
    }

    public BookingRouteV2 getRoute() {
        return route;
    }

    public void setRoute(BookingRouteV2 route) {
        this.route = route;
    }

    public List<BookingAttachmentV2> getFiles() {
        return files;
    }

    public void setFiles(List<BookingAttachmentV2> files) {
        this.files = files;
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

    public BookingSource getBookingSource() {
        return bookingSource;
    }

    public void setBookingSource(BookingSource bookingSource) {
        this.bookingSource = bookingSource;
    }
}

