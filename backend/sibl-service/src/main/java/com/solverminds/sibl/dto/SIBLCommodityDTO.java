package com.solverminds.sibl.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.enums.VolumeUnit;
import com.svm.ecom.common.enums.WeightUnit;
import com.svm.ecom.common.enums.YesNoFlag;
import com.solverminds.sibl.util.DtoUtil;
import com.solverminds.sibl.dto.validation.ValidSIBLCommodityMasterData;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Commodity line item mapped from SI Commodity sheet.
 * When commodities are present in the request, itemWeight and itemWeightUnit are mandatory
 * (same as cargoWeight and weightUnit at container level). Used for cargoWeight = sum(itemWeight) derivation.
 */
@ValidSIBLCommodityMasterData
@Schema(name = "SIBLCommodityDTO", description = "Commodity details linked to a container within the SI. When present, itemWeight and itemWeightUnit are required (same as container cargoWeight/weightUnit).")
public class SIBLCommodityDTO {

    // ===== Hidden / server-managed =====
    @Schema(hidden = true) @JsonIgnore
    private String onlineSINo;               // eSINo (link to SI) - hidden

    @NotNull(message = "itemWeight is required ")
    @Min(value = 0, message = "itemWeight must be non-negative")
    @JsonProperty("itemWeight")
    @Schema(description = "Item/cargo weight (required). Mandatory when commodities are sent; aligns with container cargoWeight rule.", example = "5000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer itemWeight;             // itemWeight

    @NotNull(message = "itemWeightUnit is required")
    @JsonProperty("itemWeightUnit")
    @Schema(description = "Weight unit - K (Kilos) or T (Metric Tonnes). Required when commodity is present.", example = "K", requiredMode = Schema.RequiredMode.REQUIRED)
    private WeightUnit itemWeightUnit;       // itemWeightUnit

    @JsonProperty("netWeight")
    @Schema(description = "Net weight.", example = "4800")
    private Integer nettwt;                  // nettwt

    // ===== Visible / request fields =====

    @NotNull @Min(1)
    @Schema(description = "Cargo/Container group number (links to container conId).",
            example = "1", required = true)
    private Integer cargoNo;                 // conId

    @NotBlank
    @Schema(description = "Linked container number.",
            example = "AASU1205051", required = true)
    private String containerNo;              // containerno

    @NotNull(message = "itemNo is required")
    @Min(value = 1, message = "itemNo must be at least 1")
    @Schema(description = "Line item sequence within this cargo group (per container/cargo line).",
            example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer itemNo;                  // itemno

    @NotBlank
    @Schema(description = "Commodity group/class code (string). Required.",
            example = "09", requiredMode = Schema.RequiredMode.REQUIRED)
    private String commodityGroup;          // commodity

    @NotBlank
    @Schema(description = "Package type code.",
            example = "CMPK", required = true)
    private String packageType;              // package

    @NotNull @Min(0)
    @Schema(description = "Number of packages.",
            example = "1", required = true)
    private Integer packageQty;              // packageQty

    @JsonProperty("cargoVolume")
    @Schema(description = "Item/cargo volume (use volumnUnit for unit).",
            example = "2")
    private Double itemVolume;               // itemVolume

    @JsonProperty("volumnUnit")
    @Schema(description = "Volume unit: M (Cubic Meter) or F (Cubic Feet).",
            example = "M")
    private VolumeUnit itemVolumeUnit;       // itemVolumeUnit (same enum as booking)

    @Schema(description = "Marks & Numbers (short).",
            example = "NA")
    private String marksNo;                  // marksNo

    @Schema(description = "Marks & Numbers / Description (long).",
            example = "COMMODITY FOR COFFEE OR TEA")
    private String marksDesc;                // marksDesc

    @NotBlank
    @Schema(description = "HS code of the commodity.",
            example = "9101", required = true)
    private String hsCode;                   // HSCommodity

    @Schema(description = "Hazardous indicator - Y (Yes) or N (No).",
            example = "Y")
    private YesNoFlag hazardous;             // hazardous

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "IMO contact person (when hazardous=Y).", example = "IMO CONTACT NAME", hidden = true)
    private String imoContact;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "IMO contact phone (when hazardous=Y).", example = "+919876543210", hidden = true)
    private String imoPhone;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "OOG (Out of Gauge) dimensions for this commodity line.", hidden = true)
    private SIBLOogDTO oog;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "HS suffix code, if any.", example = "", hidden = true)
    private String hsSuffixcode;             // hsSuffixcode

    @Schema(description = "Buyer code.", example = "")
    private String buyerCode;                // bycode

    @Schema(description = "Seller code.", example = "")
    private String sellerCode;               // slcode

    @Schema(description = "Buyer name.", example = "")
    private String buyerName;                // byname

    @Schema(description = "Seller name.", example = "")
    private String sellerName;               // slname

    @Schema(description = "Commodity HBL No (if applicable).", example = "")
    private String commodityHBLNo;           // cmdty_hblno

    @JsonProperty("dangerousGoods")

    @Schema(description = "Dangerous goods details object: unNo, classCode, flashPoint, flashPointUnit, isMarinePollutant (required when hazardous=Y). Accepts dangerousGoods or dangerousGoodsInfo.")
    private SIBLDangerousGoodsDTO dangerousGoods;

    // ===== Constructors =====
    public SIBLCommodityDTO() {}

    // ===== Getters & Setters =====
    public String getOnlineSINo() { return DtoUtil.emptyIfNull(onlineSINo); }
    public void setOnlineSINo(String onlineSINo) { this.onlineSINo = onlineSINo; }

    public Integer getItemWeight() { return DtoUtil.emptyIntIfNull(itemWeight); }
    public void setItemWeight(Integer itemWeight) { this.itemWeight = itemWeight; }

    public WeightUnit getItemWeightUnit() { return itemWeightUnit; }
    public void setItemWeightUnit(WeightUnit itemWeightUnit) { this.itemWeightUnit = itemWeightUnit; }

    public Integer getNettwt() { return DtoUtil.emptyIntIfNull(nettwt); }
    public void setNettwt(Integer nettwt) { this.nettwt = nettwt; }

    public Integer getCargoNo() { return cargoNo; }
    public void setCargoNo(Integer cargoNo) { this.cargoNo = cargoNo; }

    public String getContainerNo() { return DtoUtil.emptyIfNull(containerNo); }
    public void setContainerNo(String containerNo) { this.containerNo = containerNo; }

    public Integer getItemNo() { return itemNo; }
    public void setItemNo(Integer itemNo) { this.itemNo = itemNo; }

    public String getCommodityGroup() { return DtoUtil.emptyIfNull(commodityGroup); }
    public void setCommodityGroup(String commodityGroup) { this.commodityGroup = commodityGroup; }

    public String getPackageType() { return DtoUtil.emptyIfNull(packageType); }
    public void setPackageType(String packageType) { this.packageType = packageType; }

    public Integer getPackageQty() { return packageQty; }
    public void setPackageQty(Integer packageQty) { this.packageQty = packageQty; }

    public Double getItemVolume() { return DtoUtil.emptyDoubleIfNull(itemVolume); }
    public void setItemVolume(Double itemVolume) { this.itemVolume = itemVolume; }

    public VolumeUnit getItemVolumeUnit() { return itemVolumeUnit; }
    public void setItemVolumeUnit(VolumeUnit itemVolumeUnit) { this.itemVolumeUnit = itemVolumeUnit; }

    public String getMarksNo() { return DtoUtil.emptyIfNull(marksNo); }
    public void setMarksNo(String marksNo) { this.marksNo = marksNo; }

    public String getMarksDesc() { return DtoUtil.emptyIfNull(marksDesc); }
    public void setMarksDesc(String marksDesc) { this.marksDesc = marksDesc; }

    public String getHsCode() { return DtoUtil.emptyIfNull(hsCode); }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }

    public YesNoFlag getHazardous() { return hazardous; }
    public void setHazardous(YesNoFlag hazardous) { this.hazardous = hazardous; }

    public String getHsSuffixcode() { return DtoUtil.emptyIfNull(hsSuffixcode); }
    public void setHsSuffixcode(String hsSuffixcode) { this.hsSuffixcode = hsSuffixcode; }

    public String getBuyerCode() { return DtoUtil.emptyIfNull(buyerCode); }
    public void setBuyerCode(String buyerCode) { this.buyerCode = buyerCode; }

    public String getSellerCode() { return DtoUtil.emptyIfNull(sellerCode); }
    public void setSellerCode(String sellerCode) { this.sellerCode = sellerCode; }

    public String getBuyerName() { return DtoUtil.emptyIfNull(buyerName); }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getSellerName() { return DtoUtil.emptyIfNull(sellerName); }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getCommodityHBLNo() { return DtoUtil.emptyIfNull(commodityHBLNo); }
    public void setCommodityHBLNo(String commodityHBLNo) { this.commodityHBLNo = commodityHBLNo; }

    public String getImoContact() { return DtoUtil.emptyIfNull(imoContact); }
    public void setImoContact(String imoContact) { this.imoContact = imoContact; }

    public String getImoPhone() { return DtoUtil.emptyIfNull(imoPhone); }
    public void setImoPhone(String imoPhone) { this.imoPhone = imoPhone; }

    /** Returns OOG dimensions; null when not set (SI view: omit from JSON when container.oog=N). */
    public SIBLOogDTO getOog() { return oog; }
    public void setOog(SIBLOogDTO oog) { this.oog = oog; }

    /** Returns dangerous goods; null when not set (SI view: omit from JSON when hazardous=N). */
    public SIBLDangerousGoodsDTO getDangerousGoods() { return dangerousGoods; }
    public void setDangerousGoods(SIBLDangerousGoodsDTO dangerousGoods) { this.dangerousGoods = dangerousGoods; }
}