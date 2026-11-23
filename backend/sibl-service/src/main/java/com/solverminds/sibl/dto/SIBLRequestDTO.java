package com.solverminds.sibl.dto;

import com.solverminds.sibl.util.DtoUtil;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.svm.ecom.constants.DateFormatConstants;
import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.common.enums.EnsFillingType;
import com.svm.ecom.common.enums.EnsType;
import com.svm.ecom.common.enums.FreightPaymentType;
import com.svm.ecom.common.enums.SiStatus;
import com.svm.ecom.common.enums.TransportMode;
import com.svm.ecom.common.enums.YesNoFlag;

import io.swagger.v3.oas.annotations.media.Schema;

import com.solverminds.sibl.dto.validation.ValidSIBLDuplicateCommodityCombinations;
import com.solverminds.sibl.dto.validation.ValidSIBLDuplicateContainers;
import com.solverminds.sibl.dto.validation.ValidSIBLPolPod;
import com.solverminds.sibl.dto.validation.ValidSIBLPortCodes;
import com.solverminds.sibl.dto.validation.ValidSIBLVesselServiceTerminals;

/**
 * Shipping Instruction (SI) Request DTO
 * Maps to ecomSI table with all 47 columns for complete SI creation
 * Validation aligned with booking-service: class-level validators for ports, POL!=POD, vessel/service/terminals, duplicate containers.
 */
@ValidSIBLPortCodes
@ValidSIBLPolPod
@ValidSIBLVesselServiceTerminals
@ValidSIBLDuplicateContainers
@ValidSIBLDuplicateCommodityCombinations
@Schema(name = "SIBLRequestDTO", description = "Shipping Instruction (SI) creation payload - Complete 47 column mapping")
public class SIBLRequestDTO {

    // ==================== PRIMARY IDENTIFICATION (Columns 1-3) ====================
    
    @Schema(hidden = true) @JsonIgnore
    private String onlineSINo;            // Column 1: eSINo - Auto-generated SI number

    @Schema(description = "Bill of Lading number (NULL on insert, assigned on BL creation)", example = "SVMINDMUM68034")
    private String blNo;                  // Column 2: blno

    @NotBlank(message = "Booking Number is required")
    @Schema(description = "Booking number (required)", example = "SVMDMU2588929", required = true)
    private String bookingNumber;         // Column 3: bookno

    // ==================== VESSEL & VOYAGE DETAILS (Columns 4-6) ====================
    
    @NotBlank(message = "Vessel is required")
    @Schema(description = "Vessel code (required)", example = "MSC123", required = true)
    private String vesselCode;            // Column 4: Vessel

    @NotBlank(message = "Voyage is required")
    @Schema(description = "Voyage code  (required).", example = "685", required = true)
    private String voyageCode;            // Column 5: Voyage

    @Schema(description = "Bound direction (E, W, N, S, T, I, O, L)", example = "I")
    private Bound loadBound;             // Column 6: bound

    // ==================== PORT DETAILS (Columns 7-10) ====================
    
    @NotBlank(message = "Port of Loading is required")
    @Schema(description = "Port of Loading code (required)", example = "INNSA", required = true)
    private String loadPortId;            // Column 7: pol

    @NotBlank(message = "Port of Discharge is required")
    @Schema(description = "Port of Discharge code (required)", example = "AEJEA", required = true)
    private String dischargePortId;       // Column 8: pod

    @Schema(description = "Place of Acceptance code (defaults to POL if empty)", example = "INNSA")
    private String originPortId;          // Column 9: pla

    @Schema(description = "Place of Delivery code (defaults to POD if empty)", example = "AEJEA")
    private String finalPortId;           // Column 10: pld

    // ==================== USER & AUDIT DETAILS (Columns 11-15) ====================
    
    @Schema(hidden = true) @JsonIgnore
    private String customerCodeFromJWT;   // Column 11: custcode (from JWT)

    @Schema(hidden = true) @JsonIgnore
    private String createdUser;           // Column 12: CrUser

    @Schema(type = "string", format = "date-time", description = "Created date (UTC, ISO-8601)", example = "2025-09-10T00:00:00Z")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime createdDate;    // Column 13: CrDate

    @Schema(hidden = true) @JsonIgnore
    private String changeUser;            // Column 14: ChgUser

    @Schema(type = "string", format = "date-time", description = "Last modified date (UTC, ISO-8601)", example = "2025-09-10T00:00:00Z")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime changeDate;     // Column 15: ChgDate

    // ==================== BL VERSION CONTROL (Columns 16-17) ====================
    
    @Schema(hidden = true) @JsonIgnore
    private Integer editBl = 0;           // Column 16: editbl (0 for new)

    @Schema(hidden = true) @JsonIgnore
    private Integer blVersion = 0;        // Column 17: blversion (0 for SI)

    // ==================== STATUS & REMARKS (Columns 18-20) ====================
    
    @Schema(description = "SI status: D=Draft, S=Submitted, A=Accepted, V=Declined", example = "D")
    private SiStatus status;                // Column 18: status

    @Schema(description = "Decline remarks (populated when SI is declined)", example = "")
    private String declinedRemarks;       // Column 19: declined_remarks

    @Schema(description = "Customer remarks / additional info", example = "Handle with care")
    private String additionalRemarks;     // Column 20: custremarks

    // ==================== HEADER NAME FIELDS (Columns 21-24) ====================
    
    @NotBlank(message = "Place of Acceptance header name is required")
    @Schema(description = "Place of Acceptance display name", example = "NHAVA SHEVA,MUMBAI,MH,IN", required = true)
    private String plaHeaderName;         // Column 21: plaHeaderName

    @NotBlank(message = "Port of Loading header name is required")
    @Schema(description = "Port of Loading display name", example = "NHAVA SHEVA,MUMBAI,MH,IN", required = true)
    private String polHeaderName;         // Column 22: polHeaderName

    @NotBlank(message = "Port of Discharge header name is required")
    @Schema(description = "Port of Discharge display name", example = "JEBEL ALI,AE,AE", required = true)
    private String podHeaderName;         // Column 23: podHeaderName

    @NotBlank(message = "Place of Delivery header name is required")
    @Schema(description = "Place of Delivery display name", example = "JEBEL ALI,AE,AE", required = true)
    private String pldHeaderName;         // Column 24: pldHeaderName

    // ==================== APPLICATION INFO (Columns 25-26) ====================
    
    @Schema(hidden = true) @JsonIgnore
    private Integer appVersion = 2;       // Column 25: appversion (default 2)

    @Schema(description = "CC Email IDs (comma-separated)", example = "cc1@test.com,cc2@test.com")
    private String ccMailId;              // Column 26: ccmailid

    // ==================== CUSTOMER HEADER INFO (Columns 27-32) ====================
    
    @NotBlank(message = "Shipper address is required")
    @Schema(description = "Shipper full address block", example = "Dubai Fort Fortress Dubai Plaza Ajman BEJ UNITED ARAB EMIRATES T: +971-444-2334234", required = true)
    private String shipperAddress;        // Column 27: shipper

    @NotBlank(message = "Shipper name is required")
    @Schema(description = "Shipper display name", example = "DUBAI NXTON", required = true)
    private String shipperName;           // Column 28: shippername

    @NotBlank(message = "Consignee address is required")
    @Schema(description = "Consignee full address block", example = "chennai Chennai 603103 INDIA T: +9-9876-876543456", required = true)
    private String consigeeAddress;       // Column 29: consigee

    @NotBlank(message = "Consignee name is required")
    @Schema(description = "Consignee display name", example = "ARULPRAKASH S", required = true)
    private String consigeeName;          // Column 30: consigeename
    @NotBlank(message = "Notify address is required")
    @Schema(description = "Notify party full address block", example = "chennai Chennai 603103 INDIA", required = true)
    private String notifyAddress;         // Column 31: notify
    @NotBlank(message = "Notify name is required")
    @Schema(description = "Notify party display name", example = "ARULPRAKASH S", required = true)
    private String notifyName;            // Column 32: notifyname

    // ==================== AES & COMPLIANCE (Columns 33-35) ====================
    
    @Schema(hidden = true)
    private String aesNumber;             // Column 33: AESNumber - US Export AES Number (hidden from Swagger)

    @Schema(hidden = true)
    private String aesDisclaimer;         // Column 34: AESDiscliamer - AES disclaimer text (hidden from Swagger)

    @Schema(hidden = true)
    private String releaseType;           // Column 35: releasetype (hidden from Swagger)

    // ==================== ADDITIONAL DETAILS (Columns 36-47) ====================
    
    @Schema(description = "Declared cargo value", example = "10000.00")
    private String declaredValue;         // Column 36: declaredvalue

    @Schema(hidden = true)
    private String blType;                // Column 37: blType (hidden from Swagger)

    @Schema(hidden = true)
    private String acid;                  // Column 38: ACID - ACID Number (hidden from Swagger)

    @Schema(hidden = true)
    private FreightPaymentType freight;  // Column 39: Freight - P=Prepaid C=Collect (hidden from Swagger)

    @Schema(hidden = true)
    private Integer cargoProtectId;       // Column 40: cargoprotectid - Cargo Protect ID (hidden from Swagger)

    @NotNull(message = "t2lFilling is required; valid values: Y, N")
    @Schema(description = "T2L filling flag: Y (Yes), N (No)", required = true, example = "Y")
    private YesNoFlag t2lFilling;         // Column 41: t2lfiling

    @Schema(hidden = true)
    private String packingList;           // Column 42: packinglst - Packing list uploaded flag (hidden from Swagger)

    @Schema(hidden = true)
    private String invoiceUpload;         // Column 43: invoiceupload - Invoice uploaded flag (hidden from Swagger)

    @Schema(hidden = true)
    private String telexRelease;          // Column 44: telexrls - Y/N telexrls (hidden from Swagger)

    @NotNull(message = "ensType is required; valid values: S (Straight B/L), C (Consolidated)")
    @Schema(description = "ENS Type: S=Straight B/L, C=Consolidated", required = true, example = "S")
    private EnsType ensType;              // Column 45: enstype (same enum as booking)

    @Schema(hidden = true)
    private String blTypeUAE;             // Column 46: bltype_UAE - UAE BL Type (hidden from Swagger)

    @Schema(hidden = true)
    private String mpciId;                // Column 47: mpci_id - MPCI ID (hidden from Swagger)

    // ==================== ADDITIONAL SERVICE/ROUTE FIELDS (for validation) ====================
    
    @Schema(description = "Service type - M (Mainline) or F (Feeder) triggers serviceCode validation against service master", example = "M")
    private TransportMode serviceType;    // Same as Booking RouteDTO; when M or F, serviceCode must exist in service master

    @Schema(description = "Service code for voyage validation; validated against service master when serviceType is M or F", example = "AE1")
    private String serviceCode;           // For voyage validation

    @Schema(description = "Group Call ID for voyage validation", example = "12345")
    private String groupCallId;           // For voyage validation

    @Schema(description = "Terminal code at POL for voyage closure check", example = "NSIGT")
    private String polTerminal;           // For voyage closure validation

    @Schema(description = "Terminal code at POD (Port of Discharge)", example = "AEJEA01")
    private String podTerminal;           // POD terminal

    // ==================== ENS FILLING (Additional field) ====================
    
    @NotNull(message = "ensFilling is required; valid values: S (Single Filing), P (Partial Filing), N (NA)")
    @Schema(description = "ENS filing: S=Single, P=Partial, N=N/A", required = true, example = "N")
    private EnsFillingType ensFilling;     // ensfiling (same enum name as booking)

    // ==================== MASTER DATA FIELDS ====================

    @Schema(description = "Agency reference number", example = "REF001")
    private String agencyRefNo;           // Agency reference number

    // ==================== COUNT FIELDS ====================
    
    @Schema(description = "Requested SI count", example = "1")
    private Integer requestedSI;

    @Schema(description = "Created SI count", example = "1")
    private Integer createdSI;

    // ==================== LEGACY HIDDEN FIELDS (for backward compatibility) ====================
    
    @Schema(hidden = true) @JsonIgnore
    private String fcnno;                 // FCN No (legacy)

    @Schema(hidden = true) @JsonIgnore
    private String customerCode;          // Alias for customerCodeFromJWT

    // ===== Collections =====

    @Valid
    @NotNull @Size(min = 1)
    @Schema(description = "Customer parties", required = true)
    private List<SIBLCustomerDTO> customers;

    @Valid
    @NotNull @Size(min = 1)
    @Schema(description = "Container/equipment details", required = true)
    private List<SIBLContainerDTO> containers;

    @Valid
    @Schema(description = "Commodity details (optional; can be empty for container-only SI)")
    private List<SIBLCommodityDTO> commodities;

    public SIBLRequestDTO() {}

    // ==================== GETTERS & SETTERS ====================

    // --- Primary Identification (Columns 1-3) ---
    public String getOnlineSINo() { return DtoUtil.emptyIfNull(onlineSINo); }
    public void setOnlineSINo(String onlineSINo) { this.onlineSINo = onlineSINo; }
    public String getBlNo() { return DtoUtil.emptyIfNull(blNo); }
    public void setBlNo(String blNo) { this.blNo = blNo; }
    public String getBookingNumber() { return DtoUtil.emptyIfNull(bookingNumber); }
    public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }

    // --- Vessel & Voyage (Columns 4-6) ---
    public String getVesselCode() { return DtoUtil.emptyIfNull(vesselCode); }
    public void setVesselCode(String vesselCode) { this.vesselCode = vesselCode; }
    public String getVoyageCode() { return DtoUtil.emptyIfNull(voyageCode); }
    public void setVoyageCode(String voyageCode) { this.voyageCode = voyageCode; }
    public Bound getLoadBound() { return loadBound; }
    public void setLoadBound(Bound loadBound) { this.loadBound = loadBound; }

    // --- Port Details (Columns 7-10) ---
    public String getLoadPortId() { return DtoUtil.emptyIfNull(loadPortId); }
    public void setLoadPortId(String loadPortId) { this.loadPortId = loadPortId; }
    public String getDischargePortId() { return DtoUtil.emptyIfNull(dischargePortId); }
    public void setDischargePortId(String dischargePortId) { this.dischargePortId = dischargePortId; }
    public String getOriginPortId() { return DtoUtil.emptyIfNull(originPortId); }
    public void setOriginPortId(String originPortId) { this.originPortId = originPortId; }
    public String getFinalPortId() { return DtoUtil.emptyIfNull(finalPortId); }
    public void setFinalPortId(String finalPortId) { this.finalPortId = finalPortId; }

    // --- User & Audit (Columns 11-15) ---
    public String getCustomerCodeFromJWT() { return DtoUtil.emptyIfNull(customerCodeFromJWT); }
    public void setCustomerCodeFromJWT(String customerCodeFromJWT) { this.customerCodeFromJWT = customerCodeFromJWT; }
    public String getCreatedUser() { return DtoUtil.emptyIfNull(createdUser); }
    public void setCreatedUser(String createdUser) { this.createdUser = createdUser; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    public String getChangeUser() { return DtoUtil.emptyIfNull(changeUser); }
    public void setChangeUser(String changeUser) { this.changeUser = changeUser; }
    public LocalDateTime getChangeDate() { return changeDate; }
    public void setChangeDate(LocalDateTime changeDate) { this.changeDate = changeDate; }

    // --- BL Version Control (Columns 16-17) ---
    public Integer getEditBl() { return editBl; }
    public void setEditBl(Integer editBl) { this.editBl = editBl; }
    public Integer getBlVersion() { return blVersion; }
    public void setBlVersion(Integer blVersion) { this.blVersion = blVersion; }

    // --- Status & Remarks (Columns 18-20) ---
    public SiStatus getStatus() { return status; }
    public void setStatus(SiStatus status) { this.status = status; }
    public String getDeclinedRemarks() { return DtoUtil.emptyIfNull(declinedRemarks); }
    public void setDeclinedRemarks(String declinedRemarks) { this.declinedRemarks = declinedRemarks; }
    public String getAdditionalRemarks() { return DtoUtil.emptyIfNull(additionalRemarks); }
    public void setAdditionalRemarks(String additionalRemarks) { this.additionalRemarks = additionalRemarks; }

    // --- Header Names (Columns 21-24) ---
    public String getPlaHeaderName() { return DtoUtil.emptyIfNull(plaHeaderName); }
    public void setPlaHeaderName(String plaHeaderName) { this.plaHeaderName = plaHeaderName; }
    public String getPolHeaderName() { return DtoUtil.emptyIfNull(polHeaderName); }
    public void setPolHeaderName(String polHeaderName) { this.polHeaderName = polHeaderName; }
    public String getPodHeaderName() { return DtoUtil.emptyIfNull(podHeaderName); }
    public void setPodHeaderName(String podHeaderName) { this.podHeaderName = podHeaderName; }
    public String getPldHeaderName() { return DtoUtil.emptyIfNull(pldHeaderName); }
    public void setPldHeaderName(String pldHeaderName) { this.pldHeaderName = pldHeaderName; }

    // --- Application Info (Columns 25-26) ---
    public Integer getAppVersion() { return appVersion; }
    public void setAppVersion(Integer appVersion) { this.appVersion = appVersion; }
    public String getCcMailId() { return DtoUtil.emptyIfNull(ccMailId); }
    public void setCcMailId(String ccMailId) { this.ccMailId = ccMailId; }

    // --- Customer Header Info (Columns 27-32) ---
    public String getShipperAddress() { return DtoUtil.emptyIfNull(shipperAddress); }
    public void setShipperAddress(String shipperAddress) { this.shipperAddress = shipperAddress; }
    public String getShipperName() { return DtoUtil.emptyIfNull(shipperName); }
    public void setShipperName(String shipperName) { this.shipperName = shipperName; }
    public String getConsigeeAddress() { return DtoUtil.emptyIfNull(consigeeAddress); }
    public void setConsigeeAddress(String consigeeAddress) { this.consigeeAddress = consigeeAddress; }
    public String getConsigeeName() { return DtoUtil.emptyIfNull(consigeeName); }
    public void setConsigeeName(String consigeeName) { this.consigeeName = consigeeName; }
    public String getNotifyAddress() { return DtoUtil.emptyIfNull(notifyAddress); }
    public void setNotifyAddress(String notifyAddress) { this.notifyAddress = notifyAddress; }
    public String getNotifyName() { return DtoUtil.emptyIfNull(notifyName); }
    public void setNotifyName(String notifyName) { this.notifyName = notifyName; }

    // --- AES & Compliance (Columns 33-35) ---
    public String getAesNumber() { return DtoUtil.emptyIfNull(aesNumber); }
    public void setAesNumber(String aesNumber) { this.aesNumber = aesNumber; }
    public String getAesDisclaimer() { return DtoUtil.emptyIfNull(aesDisclaimer); }
    public void setAesDisclaimer(String aesDisclaimer) { this.aesDisclaimer = aesDisclaimer; }
    public String getReleaseType() { return DtoUtil.emptyIfNull(releaseType); }
    public void setReleaseType(String releaseType) { this.releaseType = releaseType; }

    // --- Additional Details (Columns 36-47) ---
    public String getDeclaredValue() { return DtoUtil.emptyIfNull(declaredValue); }
    public void setDeclaredValue(String declaredValue) { this.declaredValue = declaredValue; }
    public String getBlType() { return DtoUtil.emptyIfNull(blType); }
    public void setBlType(String blType) { this.blType = blType; }
    public String getAcid() { return DtoUtil.emptyIfNull(acid); }
    public void setAcid(String acid) { this.acid = acid; }
    public FreightPaymentType getFreight() { return freight; }
    public void setFreight(FreightPaymentType freight) { this.freight = freight; }
    public Integer getCargoProtectId() { return DtoUtil.emptyIntIfNull(cargoProtectId); }
    public void setCargoProtectId(Integer cargoProtectId) { this.cargoProtectId = cargoProtectId; }
    public YesNoFlag getT2lFilling() { return t2lFilling; }
    public void setT2lFilling(YesNoFlag t2lFilling) { this.t2lFilling = t2lFilling; }
    public String getPackingList() { return DtoUtil.emptyIfNull(packingList); }
    public void setPackingList(String packingList) { this.packingList = packingList; }
    public String getInvoiceUpload() { return DtoUtil.emptyIfNull(invoiceUpload); }
    public void setInvoiceUpload(String invoiceUpload) { this.invoiceUpload = invoiceUpload; }
    public String getTelexRelease() { return DtoUtil.emptyIfNull(telexRelease); }
    public void setTelexRelease(String telexRelease) { this.telexRelease = telexRelease; }
    public EnsType getEnsType() { return ensType; }
    public void setEnsType(EnsType ensType) { this.ensType = ensType; }
    public String getBlTypeUAE() { return DtoUtil.emptyIfNull(blTypeUAE); }
    public void setBlTypeUAE(String blTypeUAE) { this.blTypeUAE = blTypeUAE; }
    public String getMpciId() { return DtoUtil.emptyIfNull(mpciId); }
    public void setMpciId(String mpciId) { this.mpciId = mpciId; }

    // --- Service/Route fields for validation ---
    public TransportMode getServiceType() { return serviceType; }
    public void setServiceType(TransportMode serviceType) { this.serviceType = serviceType; }
    public String getServiceCode() { return DtoUtil.emptyIfNull(serviceCode); }
    public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }
    public String getGroupCallId() { return DtoUtil.emptyIfNull(groupCallId); }
    public void setGroupCallId(String groupCallId) { this.groupCallId = groupCallId; }
    public String getPolTerminal() { return DtoUtil.emptyIfNull(polTerminal); }
    public void setPolTerminal(String polTerminal) { this.polTerminal = polTerminal; }
    public String getPodTerminal() { return DtoUtil.emptyIfNull(podTerminal); }
    public void setPodTerminal(String podTerminal) { this.podTerminal = podTerminal; }

    // --- ENS & Master Data fields ---
    public EnsFillingType getEnsFilling() { return ensFilling; }
    public void setEnsFilling(EnsFillingType ensFilling) { this.ensFilling = ensFilling; }
    public String getAgencyRefNo() { return DtoUtil.emptyIfNull(agencyRefNo); }
    public void setAgencyRefNo(String agencyRefNo) { this.agencyRefNo = agencyRefNo; }
    
    // --- Count fields ---
    public Integer getRequestedSI() { return DtoUtil.emptyIntIfNull(requestedSI); }
    public void setRequestedSI(Integer requestedSI) { this.requestedSI = requestedSI; }
    public Integer getCreatedSI() { return DtoUtil.emptyIntIfNull(createdSI); }
    public void setCreatedSI(Integer createdSI) { this.createdSI = createdSI; }

    // --- Legacy hidden fields ---
    public String getFcnno() { return DtoUtil.emptyIfNull(fcnno); }
    public void setFcnno(String fcnno) { this.fcnno = fcnno; }
    public String getCustomerCode() { return DtoUtil.emptyIfNull(customerCode); }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    // --- Collections ---
    public List<SIBLCustomerDTO> getCustomers() { return DtoUtil.emptyListIfNull(customers); }
    public void setCustomers(List<SIBLCustomerDTO> customers) { this.customers = customers; }
    public List<SIBLContainerDTO> getContainers() { return DtoUtil.emptyListIfNull(containers); }
    public void setContainers(List<SIBLContainerDTO> containers) { this.containers = containers; }
    public List<SIBLCommodityDTO> getCommodities() { return DtoUtil.emptyListIfNull(commodities); }
    public void setCommodities(List<SIBLCommodityDTO> commodities) { this.commodities = commodities; }

    // --- Alias methods for backward compatibility (JsonIgnore to prevent Jackson conflicts) ---
    @JsonIgnore
    public String getAddtionalInfo() { return DtoUtil.emptyIfNull(additionalRemarks); }
    public void setAddtionalInfo(String addtionalInfo) { this.additionalRemarks = addtionalInfo; }
    
    // Legacy AES getter for service layer
    @JsonIgnore
    public String getAESNumber() { return DtoUtil.emptyIfNull(aesNumber); }
    public void setAESNumber(String aesNumber) { this.aesNumber = aesNumber; }
    
    // Legacy ACID getter for service layer
    @JsonIgnore
    public String getACID() { return DtoUtil.emptyIfNull(acid); }
    public void setACID(String acid) { this.acid = acid; }
    
    // Legacy release type getter
    @JsonIgnore
    public String getReleaseTypeHidden() { return DtoUtil.emptyIfNull(releaseType); }
    public void setReleaseTypeHidden(String releaseType) { this.releaseType = releaseType; }
    
    // Legacy telex getter
    @JsonIgnore
    public String getTelexReleaseHidden() { return DtoUtil.emptyIfNull(telexRelease); }
    public void setTelexReleaseHidden(String telexRelease) { this.telexRelease = telexRelease; }
}