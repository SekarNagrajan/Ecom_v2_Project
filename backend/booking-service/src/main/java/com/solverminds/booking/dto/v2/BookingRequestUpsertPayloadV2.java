package com.solverminds.booking.dto.v2;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.svm.ecom.common.enums.BookingStatus;
import com.svm.ecom.common.enums.CarriageType;
import com.svm.ecom.common.enums.EnsFillingType;
import com.svm.ecom.common.enums.EnsType;
import com.svm.ecom.common.enums.FreightPaymentType;
import com.svm.ecom.common.enums.YesNoFlag;
import com.svm.ecom.constants.DateFormatConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Schema to create or update a booking for V2 API.
 */
@Schema(name = "BookingRequestUpsertPayload", description = "Schema to create or update a booking")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingRequestUpsertPayloadV2 {

    @Schema(description = "Confirmed booking id (e.g. TSHG...). Optional; used in update payload for reference.", example = "TSHGRTM26000361")
    private String id;

    @Schema(description = "Booking request id (e.g. BKON...). Optional; used in update payload for reference.", example = "BKON730")
    private String bookingRequestId;

    @NotBlank
    @Schema(description = "Origin location (LocationID)", example = "CNNGB")
    private String originId;

    @NotBlank
    @Schema(description = "Delivery location (LocationID)", example = "ESBCN")
    private String deliveryId;

    @NotBlank
    @Schema(description = "Port of loading (LocationID)", example = "CNNGB")
    private String polId;

    @NotBlank
    @Schema(description = "Port of discharge (LocationID)", example = "ESBCN")
    private String podId;

    @NotNull
    @Schema(description = "Estimated time of departure (ISO 8601 with timezone offset).", example = "2025-09-19T03:00:00+05:30", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.ISO_DATE_TIME_ZONE)
    private OffsetDateTime etd;

    @NotNull
    @Schema(description = "Estimated time of arrival (ISO 8601 with timezone offset).", example = "2025-09-26T03:00:00+05:30", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.ISO_DATE_TIME_ZONE)
    private OffsetDateTime eta;

    @NotNull
    @Schema(description = "Booking status code (BookingStatus): D – ECOM Draft, S – Submitted, P – In-Progress, C – Confirmed, L – Cancelled, V – Rejected, T – Template, I – Void. Use enum code only.", example = "D", implementation = BookingStatus.class)
    private BookingStatus status;

    @Schema(description = "Agency code (AgencyID)", example = "CNMNGB")
    private String agencyId;

    @Schema(description = "Agency reference number", example = "AEG1232")
    private String agencyRefNo;

    @Schema(description = "Pre-carriage type (CarriageType)", example = "C")
    private CarriageType preCarriage;

    @Schema(description = "On-carriage type (CarriageType)", example = "C")
    private CarriageType onCarriage;

    @Schema(description = "Any additional remarks or instructions.", example = "Handle with care")
    private String remarks;

    @Schema(description = "Ocean freight payment type (FreightPaymentType)", example = "P")
    private FreightPaymentType oceanFreight;

    @Schema(description = "Name of the booking template.", example = "MUM_AEJ_20DC")
    private String templateName;

    @NotNull
    @Schema(description = "Date when cargo is ready.", example = "2026-01-02T08:00:00Z", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime cargoReadyDate;

    @NotNull
    @Schema(description = "Indicates whether this booking is an amendment.", example = "false")
    private Boolean isAmendment;

    @Schema(description = "Contract reference number (also known as rateRefNo)", example = "CNMNGB78271")
    private String contractReference;

    @Schema(description = "Export reference number", example = "E98211")
    private String exportRefNo;

    @Size(max = 200, message = "customerReference must not exceed 200 characters")
    @Schema(
        description = "Customer reference number",
        example = "PO829281",
        maxLength = 200)
    private String customerReference;

    @Schema(description = "ENS filing type (EnsFillingType)", example = "S")
    private EnsFillingType ensFilingType;

    @Schema(description = "ENS type (EnsType)", example = "S")
    private EnsType ensType;

    @Schema(description = "T2L filing flag (YesNoFlag: Y - Yes, N - No)", example = "N")
    private YesNoFlag t2lFiling;

    @NotEmpty
    @Valid
    @Schema(description = "List of customer details (BookingCustomer)")
    private List<BookingCustomerV2> customers;

    @NotEmpty
    @Valid
    @Schema(description = "List of container details (BookingRequestUpsertContainer)")
    private List<BookingContainerV2> containers;

    @NotNull
    @Valid
    @Schema(description = "Route details (BookingRoute)")
    private BookingRouteV2 route;

    public BookingRequestUpsertPayloadV2() {
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

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getPolId() {
        return polId;
    }

    public void setPolId(String polId) {
        this.polId = polId;
    }

    public String getPodId() {
        return podId;
    }

    public void setPodId(String podId) {
        this.podId = podId;
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
}

