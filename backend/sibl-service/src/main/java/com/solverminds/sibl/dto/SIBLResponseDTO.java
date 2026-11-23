package com.solverminds.sibl.dto;

import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.common.enums.CustomerType;
import com.svm.ecom.common.enums.EnsFillingType;
import com.svm.ecom.common.enums.EnsType;
import com.svm.ecom.common.enums.FreightPaymentType;
import com.svm.ecom.common.enums.SiStatus;
import com.svm.ecom.common.enums.TransportMode;
import com.svm.ecom.common.enums.YesNoFlag;
import com.solverminds.sibl.util.DtoUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.svm.ecom.constants.DateFormatConstants;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Shipping Instruction (SI) / Bill of Lading (BL) View Response DTO.
 * Same structure as SIBLRequestDTO so create payload and view response align.
 * Maps to ecomSI / bl / booking data when viewing by bookingNo, onlineSINo, or blNumber.
 */
@Schema(name = "SIBLResponseDTO", description = "SI/BL view response - same shape as SI create payload (SIBLRequestDTO)")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SIBLResponseDTO {

    // ==================== PRIMARY IDENTIFICATION (Columns 1-3) ====================

    @Schema(hidden = true) @JsonIgnore
    private String onlineSINo;            // Column 1: eSINo

    @Schema(description = "Bill of Lading number", example = "SVMINDMUM68034")
    private String blNo;                  // Column 2: blno

    @Schema(description = "Booking number", example = "SVMDMU2588929")
    private String bookingNumber;         // Column 3: bookno

    // ==================== VESSEL & VOYAGE DETAILS (Columns 4-6) ====================

    @Schema(description = "Vessel code", example = "MSC123")
    private String vesselCode;            // Column 4: Vessel

    @Schema(description = "Voyage code ", example = "685")
    private String voyageCode;             // Column 5: Voyage

    @Schema(description = "Bound direction (E, W, N, S)")
    private Bound loadBound;              // Column 6: bound

    // ==================== PORT DETAILS (Columns 7-10) ====================

    @Schema(description = "Port of Loading code", example = "INNSA")
    private String loadPortId;            // Column 7: pol

    @Schema(description = "Port of Discharge code", example = "AEJEA")
    private String dischargePortId;       // Column 8: pod

    @Schema(description = "Place of Acceptance code", example = "INNSA")
    private String originPortId;          // Column 9: pla

    @Schema(description = "Place of Delivery code", example = "AEJEA")
    private String finalPortId;           // Column 10: pld

    // ==================== USER & AUDIT DETAILS (Columns 11-15) ====================

    @Schema(hidden = true) @JsonIgnore
    private String customerCodeFromJWT;   // Column 11: custcode

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
    private Integer editBl = 0;           // Column 16: editbl

    @Schema(hidden = true) @JsonIgnore
    private Integer blVersion = 0;        // Column 17: blversion

    // ==================== STATUS & REMARKS (Columns 18-20) ====================

    @Schema(description = "SI status: D (Draft), S (Submitted), A (Accepted), V (Declined)", example = "D")
    private SiStatus status;              // Column 18: status

    @Schema(description = "Decline remarks", example = "")
    private String declinedRemarks;       // Column 19: declined_remarks

    @Schema(description = "Customer remarks / additional info", example = "Handle with care")
    private String additionalRemarks;     // Column 20: custremarks

    // ==================== HEADER NAME FIELDS (Columns 21-24) ====================

    @Schema(description = "Place of Acceptance display name", example = "NHAVA SHEVA,MUMBAI,MH,IN")
    private String plaHeaderName;         // Column 21: plaHeaderName

    @Schema(description = "Port of Loading display name", example = "NHAVA SHEVA,MUMBAI,MH,IN")
    private String polHeaderName;         // Column 22: polHeaderName

    @Schema(description = "Port of Discharge display name", example = "JEBEL ALI,AE,AE")
    private String podHeaderName;         // Column 23: podHeaderName

    @Schema(description = "Place of Delivery display name", example = "JEBEL ALI,AE,AE")
    private String pldHeaderName;         // Column 24: pldHeaderName

    // ==================== APPLICATION INFO (Columns 25-26) ====================

    @Schema(hidden = true) @JsonIgnore
    private Integer appVersion = 2;       // Column 25: appversion

    @Schema(description = "CC Email IDs (comma-separated)", example = "cc1@test.com,cc2@test.com")
    private String ccMailId;              // Column 26: ccmailid

    // ==================== CUSTOMER HEADER INFO (Columns 27-32) ====================

    @Schema(description = "Shipper full address block", example = "Dubai Fort Fortress Dubai Plaza Ajman BEJ UNITED ARAB EMIRATES T: +971-444-2334234")
    private String shipperAddress;        // Column 27: shipper

    @Schema(description = "Shipper display name", example = "DUBAI NXTON")
    private String shipperName;           // Column 28: shippername

    @Schema(description = "Consignee full address block", example = "chennai Chennai 603103 INDIA T: +9-9876-876543456")
    private String consigeeAddress;       // Column 29: consigee

    @Schema(description = "Consignee display name", example = "ARULPRAKASH S")
    private String consigeeName;          // Column 30: consigeename

    @Schema(description = "Notify party full address block", example = "chennai Chennai 603103 INDIA")
    private String notifyAddress;        // Column 31: notify

    @Schema(description = "Notify party display name", example = "ARULPRAKASH S")
    private String notifyName;            // Column 32: notifyname

    // ==================== AES & COMPLIANCE (Columns 33-35) ====================

    @Schema(hidden = true) @JsonIgnore
    private String aesNumber;             // Column 33: AESNumber - US Export AES Number (hidden from Swagger)

    @Schema(hidden = true) @JsonIgnore
    private String aesDisclaimer;         // Column 34: AESDiscliamer - AES disclaimer text (hidden from Swagger)

    @Schema(hidden = true) @JsonIgnore
    private String releaseType;           // Column 35: releasetype (hidden from Swagger)

    // ==================== ADDITIONAL DETAILS (Columns 36-47) ====================

    @Schema(description = "Declared cargo value", example = "10000.00")
    private String declaredValue;         // Column 36: declaredvalue

    @Schema(hidden = true) @JsonIgnore
    private String blType;                // Column 37: blType (hidden from Swagger)

    @Schema(hidden = true) @JsonIgnore
    private String acid;                  // Column 38: ACID - ACID Number (hidden from Swagger)

    @Schema(hidden = true, description = "Ocean freight payment type: P (Prepaid), C (Collect)") @JsonIgnore
    private FreightPaymentType freight;  // Column 39: Freight - P=Prepaid C=Collect (hidden from Swagger)

    @Schema(hidden = true) @JsonIgnore
    private Integer cargoProtectId;       // Column 40: cargoprotectid - Cargo Protect ID (hidden from Swagger)

    @Schema(description = "T2L filling flag: Y (Yes), N (No)")
    private YesNoFlag t2lFilling;         // Column 41: t2lfiling

    @Schema(hidden = true) @JsonIgnore
    private String packingList;           // Column 42: packinglst - Packing list uploaded flag (hidden from Swagger)

    @Schema(hidden = true) @JsonIgnore
    private String invoiceUpload;         // Column 43: invoiceupload - Invoice uploaded flag (hidden from Swagger)

    @Schema(hidden = true) @JsonIgnore
    private String telexRelease;          // Column 44: telexrls - Y/N telexrls (hidden from Swagger)

    @Schema(description = "ENS Type: S (Straight B/L), C (Consolidated)")
    private EnsType ensType;              // Column 45: enstype

    @Schema(hidden = true) @JsonIgnore
    private String blTypeUAE;             // Column 46: bltype_UAE - UAE BL Type (hidden from Swagger)

    @Schema(hidden = true) @JsonIgnore
    private String mpciId;                // Column 47: mpci_id - MPCI ID (hidden from Swagger)

    // ==================== ADDITIONAL SERVICE/ROUTE FIELDS ====================

    @Schema(description = "Service type - M (Mainline) or F (Feeder)", example = "M")
    private TransportMode serviceType;

    @Schema(description = "Service code for voyage validation", example = "AE1")
    private String serviceCode;

    @Schema(description = "Group Call ID for voyage validation", example = "12345")
    private String groupCallId;

    @Schema(description = "Terminal code at POL for voyage closure check", example = "NSIGT")
    private String polTerminal;

    @Schema(description = "Terminal code at POD (Port of Discharge)", example = "AEJEA01")
    private String podTerminal;

    // ==================== ENS FILLING ====================

    @Schema(description = "ENS filling type: S (Single), P (Partial), N (N/A)")
    private EnsFillingType ensFilling;

    // ==================== MASTER DATA FIELDS ====================

    @Schema(description = "Agency reference number", example = "REF001")
    private String agencyRefNo;

    // ==================== COUNT FIELDS ====================

    @Schema(description = "Requested SI count", example = "1")
    private Integer requestedSI;

    @Schema(description = "Created SI count", example = "1")
    private Integer createdSI;

    @Schema(description = "OOG (Out of Gauge) flag: Y (Yes), N (No)")
    private YesNoFlag oogFlag;

    // ==================== LEGACY HIDDEN FIELDS ====================

    @Schema(hidden = true) @JsonIgnore
    private String fcnno;

    @Schema(hidden = true) @JsonIgnore
    private String customerCode;

    // ===== Collections =====

    @Schema(description = "Customer parties")
    private List<SIBLCustomerDTO> customers;

    @Schema(description = "Container/equipment details")
    private List<SIBLContainerDTO> containers;

    @Schema(description = "Commodity details (optional; can be empty for container-only SI)")
    private List<SIBLCommodityDTO> commodities;

    public SIBLResponseDTO() {}

    /**
     * Copy from SIBLRequestDTO so view response has the same shape as create payload.
     */
    public static SIBLResponseDTO from(SIBLRequestDTO src) {
        if (src == null) return null;
        SIBLResponseDTO r = new SIBLResponseDTO();
        r.setOnlineSINo(src.getOnlineSINo());
        r.setBlNo(src.getBlNo());
        r.setBookingNumber(src.getBookingNumber());
        r.setVesselCode(src.getVesselCode());
        r.setVoyageCode(src.getVoyageCode());
        r.setLoadBound(src.getLoadBound());
        r.setLoadPortId(src.getLoadPortId());
        r.setDischargePortId(src.getDischargePortId());
        r.setOriginPortId(src.getOriginPortId());
        r.setFinalPortId(src.getFinalPortId());
        r.setCustomerCodeFromJWT(src.getCustomerCodeFromJWT());
        r.setCreatedUser(src.getCreatedUser());
        r.setCreatedDate(src.getCreatedDate());
        r.setChangeUser(src.getChangeUser());
        r.setChangeDate(src.getChangeDate());
        r.setEditBl(src.getEditBl());
        r.setBlVersion(src.getBlVersion());
        r.setStatus(src.getStatus());
        r.setDeclinedRemarks(src.getDeclinedRemarks());
        r.setAdditionalRemarks(src.getAdditionalRemarks());
        r.setPlaHeaderName(src.getPlaHeaderName());
        r.setPolHeaderName(src.getPolHeaderName());
        r.setPodHeaderName(src.getPodHeaderName());
        r.setPldHeaderName(src.getPldHeaderName());
        r.setAppVersion(src.getAppVersion());
        r.setCcMailId(src.getCcMailId());
        r.setShipperAddress(src.getShipperAddress());
        r.setShipperName(src.getShipperName());
        r.setConsigeeAddress(src.getConsigeeAddress());
        r.setConsigeeName(src.getConsigeeName());
        r.setNotifyAddress(src.getNotifyAddress());
        r.setNotifyName(src.getNotifyName());
        r.setAesNumber(src.getAesNumber());
        r.setAesDisclaimer(src.getAesDisclaimer());
        r.setReleaseType(src.getReleaseType());
        r.setDeclaredValue(src.getDeclaredValue());
        r.setBlType(src.getBlType());
        r.setAcid(src.getAcid());
        r.setFreight(src.getFreight());
        r.setCargoProtectId(src.getCargoProtectId());
        r.setT2lFilling(src.getT2lFilling());
        r.setPackingList(src.getPackingList());
        r.setInvoiceUpload(src.getInvoiceUpload());
        r.setTelexRelease(src.getTelexRelease());
        r.setEnsType(src.getEnsType());
        r.setBlTypeUAE(src.getBlTypeUAE());
        r.setMpciId(src.getMpciId());
        r.setServiceType(src.getServiceType());
        r.setServiceCode(src.getServiceCode());
        r.setGroupCallId(src.getGroupCallId());
        r.setPolTerminal(src.getPolTerminal());
        r.setPodTerminal(src.getPodTerminal());
        r.setEnsFilling(src.getEnsFilling());
        r.setAgencyRefNo(src.getAgencyRefNo());
        r.setRequestedSI(src.getRequestedSI());
        r.setCreatedSI(src.getCreatedSI());
        r.setFcnno(src.getFcnno());
        r.setCustomerCode(src.getCustomerCode());
        // 1. Customer validation (ecomSI-Cust): do not show customer types AP, BP in response
        r.setCustomers(filterCustomersExcludeApBp(src.getCustomers()));
        // 2. Reefer/DG/OOG validation: only show nested DTOs when flags are Y; reefer temps: convertEmptyToNull; OOG only in commodity list
        r.setContainers(copyContainersWithValidation(src.getContainers()));
        r.setCommodities(copyCommoditiesWithValidation(src.getCommodities(), src.getContainers()));
        r.setOogFlag(computeOogFlag(src));
        return r;
    }

    /** Exclude customer types AP and BP from response (ecomSI-Cust validation). */
    private static List<SIBLCustomerDTO> filterCustomersExcludeApBp(List<SIBLCustomerDTO> customers) {
        if (customers == null || customers.isEmpty()) return customers != null ? new ArrayList<>() : null;
        List<SIBLCustomerDTO> out = new ArrayList<>();
        for (SIBLCustomerDTO c : customers) {
            if (c == null) continue;
            CustomerType type = c.getCustomerType();
            if (type == null || (type != CustomerType.AP && type != CustomerType.BP)) {
                out.add(c);
            }
        }
        return out;
    }

    /** Copy containers: only show reefer when reeferFlag=Y (with temp convertEmptyToNull); DG is at commodity level only. Remove OOG from container list (show only in commodity when oog=Y). */
    private static List<SIBLContainerDTO> copyContainersWithValidation(List<SIBLContainerDTO> containers) {
        if (containers == null || containers.isEmpty()) return containers != null ? new ArrayList<>() : null;
        List<SIBLContainerDTO> out = new ArrayList<>();
        for (SIBLContainerDTO src : containers) {
            if (src == null) continue;
            SIBLContainerDTO copy = copyContainer(src);
            if (src.getReefer() != YesNoFlag.Y) {
                copy.setReeferSettings(null);
            } else {
                copy.setReeferSettings(src.getReeferSettings() != null
                        ? copyReeferWithTempConvertEmptyToNull(src.getReeferSettings())
                        : new SIBLReeferDTO());
            }
            // OOG details: do not show in container list; show only in commodity list when container.oog=Y
            copy.setOogDimensions(null);
            out.add(copy);
        }
        return out;
    }

    /** Copy commodities: only show dangerousGoods when hazardous=Y; only show oog when container (cargoNo) has oog=Y. When hazardous=Y but dangerousGoods null, set empty DTO so key appears per commodity. */
    private static List<SIBLCommodityDTO> copyCommoditiesWithValidation(List<SIBLCommodityDTO> commodities, List<SIBLContainerDTO> containers) {
        if (commodities == null || commodities.isEmpty()) return commodities != null ? new ArrayList<>() : null;
        Set<Integer> cargoNosWithOog = cargoNosWithOogFlagY(containers);
        List<SIBLCommodityDTO> out = new ArrayList<>();
        for (SIBLCommodityDTO src : commodities) {
            if (src == null) continue;
            SIBLCommodityDTO copy = copyCommodity(src);
            if (src.getHazardous() != YesNoFlag.Y) {
                copy.setDangerousGoods(null);
            } else {
                if (copy.getDangerousGoods() == null) copy.setDangerousGoods(new SIBLDangerousGoodsDTO());
            }
            // OOG: only show when container (cargoNo) has oog=Y
            if (src.getCargoNo() == null || !cargoNosWithOog.contains(src.getCargoNo())) copy.setOog(null);
            out.add(copy);
        }
        return out;
    }

    /** Cargo numbers (conId) where container has oog=Y (ecomSI-OOG). */
    private static Set<Integer> cargoNosWithOogFlagY(List<SIBLContainerDTO> containers) {
        Set<Integer> set = new HashSet<>();
        if (containers == null) return set;
        for (SIBLContainerDTO c : containers) {
            if (c != null && c.getOogFlag() == YesNoFlag.Y && c.getCargoNo() != null) set.add(c.getCargoNo());
        }
        return set;
    }

    private static SIBLContainerDTO copyContainer(SIBLContainerDTO src) {
        SIBLContainerDTO c = new SIBLContainerDTO();
        c.setOnlineSINo(src.getOnlineSINo());
        c.setEquipmentType(src.getEquipmentType());
        c.setCargoNo(src.getCargoNo());
        c.setEquipmentSize(src.getEquipmentSize());
        c.setContainerNo(src.getContainerNo());
        c.setSocFlag(src.getSocFlag());
        c.setCarrierSeal(src.getCarrierSeal());
        c.setShipperSeal(src.getShipperSeal());
        c.setTareWeight(src.getTareWeight());
        c.setCargoWeight(src.getCargoWeight());
        c.setGrossweight(src.getGrossweight());
        c.setWeightUnit(src.getWeightUnit());
        c.setNor(src.getNor());
        c.setEquipmentStatus(src.getEquipmentStatus());
        c.setOogFlag(src.getOogFlag());
        c.setRateEquipmentTypeId(src.getRateEquipmentTypeId());
        c.setReefer(src.getReefer());
        c.setReeferSettings(src.getReeferSettings());
        c.setOogDimensions(src.getOogDimensions());
        return c;
    }

    private static SIBLCommodityDTO copyCommodity(SIBLCommodityDTO src) {
        SIBLCommodityDTO c = new SIBLCommodityDTO();
        c.setOnlineSINo(src.getOnlineSINo());
        c.setCargoNo(src.getCargoNo());
        c.setContainerNo(src.getContainerNo());
        c.setItemNo(src.getItemNo());
        c.setCommodityGroup(src.getCommodityGroup());
        c.setPackageType(src.getPackageType());
        c.setPackageQty(src.getPackageQty());
        c.setItemWeight(src.getItemWeight());
        c.setItemWeightUnit(src.getItemWeightUnit());
        c.setNettwt(src.getNettwt());
        c.setItemVolume(src.getItemVolume());
        c.setItemVolumeUnit(src.getItemVolumeUnit());
        c.setHsCode(src.getHsCode());
        c.setMarksNo(src.getMarksNo());
        c.setMarksDesc(src.getMarksDesc());
        c.setCommodityHBLNo(src.getCommodityHBLNo());
        c.setBuyerCode(src.getBuyerCode());
        c.setSellerCode(src.getSellerCode());
        c.setBuyerName(src.getBuyerName());
        c.setSellerName(src.getSellerName());
        c.setHazardous(src.getHazardous());
        c.setOog(src.getOog());
        c.setDangerousGoods(src.getDangerousGoods());
        return c;
    }

    /** Copy reefer and set temperature values to null when empty/zero (ecomSI-reefer convertEmptyToNull). */
    private static SIBLReeferDTO copyReeferWithTempConvertEmptyToNull(SIBLReeferDTO src) {
        if (src == null) return null;
        SIBLReeferDTO r = new SIBLReeferDTO();
        r.setTempSet(DtoUtil.nullIfEmpty(src.getTempSet()));
        r.setTempMin(DtoUtil.nullIfEmpty(src.getTempMin()));
        r.setTempMax(DtoUtil.nullIfEmpty(src.getTempMax()));
        r.setTempSetUnit(src.getTempSetUnit());
        r.setVentilation(src.getVentilation());
        r.setVentSet(src.getVentSet());
        r.setHumidity(src.getHumidity());
        r.setHumidityFlag(src.getHumidityFlag());
        r.setO2Level(src.getO2Level());
        r.setCo2Level(src.getCo2Level());
        r.setDrains(src.getDrains());
        return r;
    }

    /** Set oogFlag = Y if any container has oogFlag=Y or any commodity has oog set (ecomSI-OOG). */
    private static YesNoFlag computeOogFlag(SIBLRequestDTO src) {
        if (src == null) return YesNoFlag.N;
        if (src.getContainers() != null) {
            for (SIBLContainerDTO c : src.getContainers()) {
                if (c != null && c.getOogFlag() == YesNoFlag.Y) return YesNoFlag.Y;
            }
        }
        if (src.getCommodities() != null) {
            for (SIBLCommodityDTO c : src.getCommodities()) {
                if (c != null && c.getOog() != null) return YesNoFlag.Y;
            }
        }
        return YesNoFlag.N;
    }

    /** Safe conversion from String to YesNoFlag. */
    private static YesNoFlag safeYesNoFlag(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return YesNoFlag.fromValue(s.trim()); } catch (Exception e) { return null; }
    }

    // ==================== GETTERS & SETTERS ====================

    public String getOnlineSINo() { return DtoUtil.emptyIfNull(onlineSINo); }
    public void setOnlineSINo(String onlineSINo) { this.onlineSINo = onlineSINo; }
    public String getBlNo() { return DtoUtil.emptyIfNull(blNo); }
    public void setBlNo(String blNo) { this.blNo = blNo; }
    public String getBookingNumber() { return DtoUtil.emptyIfNull(bookingNumber); }
    public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }

    public String getVesselCode() { return DtoUtil.emptyIfNull(vesselCode); }
    public void setVesselCode(String vesselCode) { this.vesselCode = vesselCode; }
    public String getVoyageCode() { return DtoUtil.emptyIfNull(voyageCode); }
    public void setVoyageCode(String voyageCode) { this.voyageCode = voyageCode; }
    public Bound getLoadBound() { return loadBound; }
    public void setLoadBound(Bound loadBound) { this.loadBound = loadBound; }

    public String getLoadPortId() { return DtoUtil.emptyIfNull(loadPortId); }
    public void setLoadPortId(String loadPortId) { this.loadPortId = loadPortId; }
    public String getDischargePortId() { return DtoUtil.emptyIfNull(dischargePortId); }
    public void setDischargePortId(String dischargePortId) { this.dischargePortId = dischargePortId; }
    public String getOriginPortId() { return DtoUtil.emptyIfNull(originPortId); }
    public void setOriginPortId(String originPortId) { this.originPortId = originPortId; }
    public String getFinalPortId() { return DtoUtil.emptyIfNull(finalPortId); }
    public void setFinalPortId(String finalPortId) { this.finalPortId = finalPortId; }

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

    public Integer getEditBl() { return editBl; }
    public void setEditBl(Integer editBl) { this.editBl = editBl; }
    public Integer getBlVersion() { return blVersion; }
    public void setBlVersion(Integer blVersion) { this.blVersion = blVersion; }

    public SiStatus getStatus() { return status; }
    public void setStatus(SiStatus status) { this.status = status; }
    public String getDeclinedRemarks() { return DtoUtil.emptyIfNull(declinedRemarks); }
    public void setDeclinedRemarks(String declinedRemarks) { this.declinedRemarks = declinedRemarks; }
    public String getAdditionalRemarks() { return DtoUtil.emptyIfNull(additionalRemarks); }
    public void setAdditionalRemarks(String additionalRemarks) { this.additionalRemarks = additionalRemarks; }

    public String getPlaHeaderName() { return DtoUtil.emptyIfNull(plaHeaderName); }
    public void setPlaHeaderName(String plaHeaderName) { this.plaHeaderName = plaHeaderName; }
    public String getPolHeaderName() { return DtoUtil.emptyIfNull(polHeaderName); }
    public void setPolHeaderName(String polHeaderName) { this.polHeaderName = polHeaderName; }
    public String getPodHeaderName() { return DtoUtil.emptyIfNull(podHeaderName); }
    public void setPodHeaderName(String podHeaderName) { this.podHeaderName = podHeaderName; }
    public String getPldHeaderName() { return DtoUtil.emptyIfNull(pldHeaderName); }
    public void setPldHeaderName(String pldHeaderName) { this.pldHeaderName = pldHeaderName; }

    public Integer getAppVersion() { return appVersion; }
    public void setAppVersion(Integer appVersion) { this.appVersion = appVersion; }
    public String getCcMailId() { return DtoUtil.emptyIfNull(ccMailId); }
    public void setCcMailId(String ccMailId) { this.ccMailId = ccMailId; }

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

    public String getAesNumber() { return DtoUtil.emptyIfNull(aesNumber); }
    public void setAesNumber(String aesNumber) { this.aesNumber = aesNumber; }
    public String getAesDisclaimer() { return DtoUtil.emptyIfNull(aesDisclaimer); }
    public void setAesDisclaimer(String aesDisclaimer) { this.aesDisclaimer = aesDisclaimer; }
    public String getReleaseType() { return DtoUtil.emptyIfNull(releaseType); }
    public void setReleaseType(String releaseType) { this.releaseType = releaseType; }

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

    public EnsFillingType getEnsFilling() { return ensFilling; }
    public void setEnsFilling(EnsFillingType ensFilling) { this.ensFilling = ensFilling; }
    public String getAgencyRefNo() { return DtoUtil.emptyIfNull(agencyRefNo); }
    public void setAgencyRefNo(String agencyRefNo) { this.agencyRefNo = agencyRefNo; }

    public Integer getRequestedSI() { return DtoUtil.emptyIntIfNull(requestedSI); }
    public void setRequestedSI(Integer requestedSI) { this.requestedSI = requestedSI; }
    public Integer getCreatedSI() { return DtoUtil.emptyIntIfNull(createdSI); }
    public void setCreatedSI(Integer createdSI) { this.createdSI = createdSI; }

    public YesNoFlag getOogFlag() { return oogFlag; }
    public void setOogFlag(YesNoFlag oogFlag) { this.oogFlag = oogFlag; }

    public String getFcnno() { return DtoUtil.emptyIfNull(fcnno); }
    public void setFcnno(String fcnno) { this.fcnno = fcnno; }
    public String getCustomerCode() { return DtoUtil.emptyIfNull(customerCode); }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    public List<SIBLCustomerDTO> getCustomers() { return DtoUtil.emptyListIfNull(customers); }
    public void setCustomers(List<SIBLCustomerDTO> customers) { this.customers = customers; }
    public List<SIBLContainerDTO> getContainers() { return DtoUtil.emptyListIfNull(containers); }
    public void setContainers(List<SIBLContainerDTO> containers) { this.containers = containers; }
    public List<SIBLCommodityDTO> getCommodities() { return DtoUtil.emptyListIfNull(commodities); }
    public void setCommodities(List<SIBLCommodityDTO> commodities) { this.commodities = commodities; }
}
