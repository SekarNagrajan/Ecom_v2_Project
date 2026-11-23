package com.solverminds.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.svm.ecom.constants.DateFormatConstants;
import com.solverminds.booking.dto.validation.ConditionalTemplateNameRequired;
import com.solverminds.booking.dto.validation.ValidContainerCargo;
import com.solverminds.booking.dto.validation.ValidNoDuplicateCargoNos;
import com.solverminds.booking.dto.validation.ValidNoDuplicateCommodityCombinations;
import com.solverminds.booking.dto.validation.ValidNoDuplicateContainerNos;
import com.solverminds.booking.dto.validation.ValidPortCodes;
import com.svm.ecom.common.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Booking request DTO for creation operations.
 * Uses pure Java with standard getters/setters for Jackson mapping.
 */
@ConditionalTemplateNameRequired
@ValidContainerCargo
@ValidNoDuplicateContainerNos
@ValidNoDuplicateCargoNos
@ValidNoDuplicateCommodityCombinations
@ValidPortCodes
public class BookingRequestDTO {

    // Keep names matching JSON via getters/setters
    @Schema(description = "Auto Generate running number with BKON prefix and If ebookno exists then it is amendment", example = "BKON1234")
    private String onlineBookNo;
    @Schema(description = "Get agency based on originPortId", example = "CNMNGB")
    private String agencyId;
    @NotBlank
    @Schema(description = "required", example = "INNSA")
    private String originPortId;
    @NotBlank
    @Schema(description = "required", example = "AEJEA")
    private String finalPortId;
    @NotBlank
    @Schema(description = "required", example = "INNSA")
    private String loadPortId;
    @NotBlank
    @Schema(description = "required", example = "AEJEA")
    private String dischargePortId;
    @Schema(
        type = "string",
        format = "date-time",
        description = "created date of booking",
        example = "2025-09-10T00:00:00Z"
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime bookDate;
    @NotNull
    @FutureOrPresent(message = "Cargo ready date cannot be in the past")
    @Schema(
        type = "string",
        format = "date",
        description = "required - must be today or a future date",
        example = "2025-09-10"
    )
    @JsonFormat(pattern = DateFormatConstants.ISO_DATE)
    private LocalDate cargoReadyDate;
    @Schema(description = "Pre-carriage type: C (Carrier), M (Merchant), N (Not Applicable)", example = "C")
    private CarriageType preCarriage;
    @Schema(description = "On-carriage type: C (Carrier), M (Merchant), N (Not Applicable)", example = "C")
    private CarriageType onCarriage;
    @Schema(description = "")
    private String remarks;
    @Schema(description = "Booking status: D (Draft), S (Submitted), T (Template)", example = "D")
    private BookingStatus status;
    @Schema(example = "INDMUM09817121")
    private String bookingNumber;
    @Schema(description = "Customer code will be overwritten from JWT", example = "20121345938")
    private String customerCode;
    @Schema(description = "available only when status is T", example = "MUM_AEJ_20DC")
    private String templateName;
    @Schema(description = "Rate Reference number", example = "INDMUM78271")
    private String rateRefNo;
    @Schema(
        type = "string",
        format = "date-time",
        description = "Submitted date timestamp",
        example = "2025-09-10T00:00:00Z"
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime submittedDate;
    /** Record creation timestamp (ecombooking.datecr) - used for API createdAt */
    private LocalDateTime datecr;
    /** Record last update timestamp (ecombooking.dateup) - used for API updatedAt */
    private LocalDateTime dateup;
    @NotNull
    @Min(1)
    @Schema(description = "required", example = "18272")
    private Integer routeId;
    @Schema(example = "AEG1232")
    private String agencyRefNo;
    @Schema(description = "Ocean freight payment type: P (Prepaid), C (Collect)", example = "P")
    private FreightPaymentType oceanFreight;
    @Schema(description = "Amendment flag: Y (Yes - Amendment), N (No - New Booking)", example = "N")
    private YesNoFlag isAmendment;
    @Schema(example = "E98211")
    private String exportRefNo;
    @Size(max = 200, message = "customerPO must not exceed 200 characters")
    @Schema(
        description = "Customer reference",
        example = "PO829281",
        maxLength = 200)
    private String customerPO;
    @Schema(description = "ENS filling type: S (Single), P (Partial), N (N/A)")
    private EnsFillingType ensFilling;
    @Schema(description = "T2L filling flag: Y (Yes), N (No)")
    private YesNoFlag t2lFilling;
    @Schema(description = "ENS Type: S (Straight B/L), C (Consolidated)")
    private EnsType ensType;

    @NotEmpty
    @Valid
    @Schema(description = "List of customer details")
    private List<CustomerDTO> customers;
    @NotEmpty
    @Valid
    @Schema(description = "List of container details")
    private List<ContainerDTO> containers;
    @NotEmpty
    @Valid
    @Schema(description = "List of commodity details")
    private List<CommodityDTO> commodities;
    @NotEmpty
    @Valid
    @Schema(description = "List of route details")
    private List<RouteDTO> routes;
    @Valid
    @Schema(description = "List of file attachments")
    private List<FileAttachmentDTO> files;

    @Schema(description = "User who created the booking")
    private String createdBy;

    @Schema(description = "Confirmed date (LRP bookings only)")
    private LocalDateTime confirmedAt;

    @Schema(description = "Total TEUs for the booking (calculated from ecombooking_cargo + ecom_equipments)")
    private Integer teus;

    @Schema(
        description = "Booking source",
        accessMode = Schema.AccessMode.READ_ONLY,
        example = "C",
        implementation = BookingSource.class
    )
    private BookingSource bookingSource;

    public BookingRequestDTO() {
    }

    // Getters and setters for all fields
    public String getOnlineBookNo() {
        return onlineBookNo;
    }

    public void setOnlineBookNo(String onlineBookNo) {
        this.onlineBookNo = onlineBookNo;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
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

    public LocalDateTime getBookDate() {
        return bookDate;
    }

    public void setBookDate(LocalDateTime bookDate) {
        this.bookDate = bookDate;
    }

    public LocalDate getCargoReadyDate() {
        return cargoReadyDate;
    }

    public void setCargoReadyDate(LocalDate cargoReadyDate) {
        this.cargoReadyDate = cargoReadyDate;
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

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getRateRefNo() {
        return rateRefNo;
    }

    public void setRateRefNo(String rateRefNo) {
        this.rateRefNo = rateRefNo;
    }

    public LocalDateTime getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(LocalDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public LocalDateTime getDatecr() {
        return datecr;
    }

    public void setDatecr(LocalDateTime datecr) {
        this.datecr = datecr;
    }

    public LocalDateTime getDateup() {
        return dateup;
    }

    public void setDateup(LocalDateTime dateup) {
        this.dateup = dateup;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getAgencyRefNo() {
        return agencyRefNo;
    }

    public void setAgencyRefNo(String agencyRefNo) {
        this.agencyRefNo = agencyRefNo;
    }

    public FreightPaymentType getOceanFreight() {
        return oceanFreight;
    }

    public void setOceanFreight(FreightPaymentType oceanFreight) {
        this.oceanFreight = oceanFreight;
    }

    public YesNoFlag getIsAmendment() {
        return isAmendment;
    }

    public void setIsAmendment(YesNoFlag isAmendment) {
        this.isAmendment = isAmendment;
    }

    public String getExportRefNo() {
        return exportRefNo;
    }

    public void setExportRefNo(String exportRefNo) {
        this.exportRefNo = exportRefNo;
    }

    public String getCustomerPO() {
        return customerPO;
    }

    public void setCustomerPO(String customerPO) {
        this.customerPO = customerPO;
    }

    public EnsFillingType getEnsFilling() {
        return ensFilling;
    }

    public void setEnsFilling(EnsFillingType ensFilling) {
        this.ensFilling = ensFilling;
    }

    public YesNoFlag getT2lFilling() {
        return t2lFilling;
    }

    public void setT2lFilling(YesNoFlag t2lFilling) {
        this.t2lFilling = t2lFilling;
    }

    public EnsType getEnsType() {
        return ensType;
    }

    public void setEnsType(EnsType ensType) {
        this.ensType = ensType;
    }

    public List<CustomerDTO> getCustomers() {
        return customers;
    }

    public void setCustomers(List<CustomerDTO> customers) {
        this.customers = customers;
    }

    public List<ContainerDTO> getContainers() {
        return containers;
    }

    public void setContainers(List<ContainerDTO> containers) {
        this.containers = containers;
    }

    public List<CommodityDTO> getCommodities() {
        return commodities;
    }

    public void setCommodities(List<CommodityDTO> commodities) {
        this.commodities = commodities;
    }

    public List<RouteDTO> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteDTO> routes) {
        this.routes = routes;
    }


    public List<FileAttachmentDTO> getFiles() {
        return files;
    }

    public void setFiles(List<FileAttachmentDTO> files) {
        this.files = files;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Integer getTeus() {
        return teus;
    }

    public void setTeus(Integer teus) {
        this.teus = teus;
    }

    public BookingSource getBookingSource() {
        return bookingSource;
    }

    public void setBookingSource(BookingSource bookingSource) {
        this.bookingSource = bookingSource;
    }

    // ===== Alias methods for service layer compatibility =====
    
    /**
     * Alias for getTemplateName() - required by service layer
     */
    public String getTemplatename() {
        return getTemplateName();
    }
    
    /**
     * Alias for getRateRefNo() - required by service layer
     */
    public String getRateno() {
        return getRateRefNo();
    }

}
