package com.solverminds.booking.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solverminds.booking.dto.v2.DangerousGoodsInfoV2;
import com.solverminds.booking.dto.validation.CargoVolumeRequiresVolumeUnit;
import com.solverminds.booking.dto.validation.ValidCommodityMasterData;
import com.svm.ecom.common.enums.VolumeUnit;
import com.svm.ecom.common.enums.WeightUnit;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.solverminds.booking.dto.validation.ValidationGroups;

/**
 * Commodity details linked to a container/cargo.
 * Dangerous goods are sent as {@code dangerousGoods} (object: unNo, classCode, flashPoint, flashPointUnit, isMarinePollutant).
 * JSON alias {@code dangerousGoodsInfo} is accepted for backward compatibility.
 */
@Schema(description = "Commodity details with optional dangerous goods")
@ValidCommodityMasterData
@CargoVolumeRequiresVolumeUnit
public class CommodityDTO {

    @NotNull
    @Schema(description = "Cargo number linking to container", example = "1")
    private Integer cargoNo;

    @NotBlank(groups = ValidationGroups.UpdateBooking.class,
              message = "containerNo is required for booking amendments")
    @Schema(description = "Container number. Optional for new bookings (linked via cargoNo), required for amendments.", 
            example = "20DCTBN1")
    @Size(max = 11, message = "containerNo must not exceed 11 characters")
    private String containerNo;

    @NotNull
    @Schema(description = "Item sequence number", example = "1")
    private Integer itemNo;

    @NotBlank(message = "Commodity group code must be provided (non-empty).")
    @Schema(description = "Commodity group code. Required.", example = "09", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 150, message = "commodityGroup must not exceed 150 characters")
    private String commodityGroup;

    /**
     * @deprecated Use {@link #getCommodityGroup()} instead. Not part of the API; retained only for backward compatibility during deserialization.
     */
    @Deprecated
    @Schema(hidden = true, accessMode = Schema.AccessMode.READ_ONLY, description = "Removed from API. Use commodityGroup (string) only.")
    private String commodityGroupCode;

    @Schema(description = "HS (Harmonized System) code", example = "9004909000")
    @Size(max = 150, message = "hsCode must not exceed 150 characters")
    private String hsCode;

    @Schema(description = "Package type", example = "PACK")
    @Size(max = 4, message = "packageType must not exceed 4 characters")
    private String packageType;

    @NotNull
    @Min(0)
    @Schema(description = "Number of packages", example = "50", minimum = "0")
    private Integer packageQty;

    @DecimalMin(value = "0", inclusive = true, message = "cargoVolume must not be negative")
    @Schema(description = "Cargo volume", example = "12000.5")
    private Double cargoVolume;

    @Schema(description = "Volume unit - M (Cubic Meter) or F (Cubic Feet). Required when cargoVolume is set.", example = "M")
    private VolumeUnit volumnUnit;

    @DecimalMin(value = "0", inclusive = true, message = "nettwt must not be negative")
    @Schema(description = "Net weight", example = "7500.25")
    private Double nettwt;

    @Schema(description = "Item/Cargo weight", example = "100")
    private Float itemWeight;

    @Schema(description = "Item weight unit - K (Kilograms) or T (Tonnes)", example = "K")
    private WeightUnit itemWeightUnit;

    @Schema(description = "Buyer Code of the commodity", example = "BUY001")
    @Size(max = 15, message = "buyerCode must not exceed 15 characters")
    private String buyerCode;

    @Schema(description = "Buyer Name of the commodity", example = "ABC Traders Ltd")
    @Size(max = 150, message = "buyerName must not exceed 150 characters")
    private String buyerName;

    @Schema(description = "Seller Code of the commodity", example = "SEL001")
    @Size(max = 15, message = "sellerCode must not exceed 15 characters")
    private String sellerCode;

    @Schema(description = "Seller Name of the commodity", example = "XYZ Exports Inc")
    @Size(max = 150, message = "sellerName must not exceed 150 characters")
    private String sellerName;

    @Schema(description = "Commodity HBLNo of the commodity", example = "HBL12345")
    @Size(max = 20, message = "commodityHBLNo must not exceed 20 characters")
    private String commodityHBLNo;

    @Valid
    @JsonAlias("dangerousGoodsInfo")
    @Schema(description = "Details about a dangerous good (required when commodity is hazardous / container dgFlag is Y)",
            implementation = DangerousGoodsInfoV2.class)
    private DangerousGoodsInfoV2 dangerousGoods;

    public CommodityDTO() {
    }

    public CommodityDTO(Integer cargoNo, String containerNo, Integer itemNo, String commodityGroup,
                       String hsCode, String packageType, Integer packageQty, Double cargoVolume,
                       VolumeUnit volumnUnit, Double nettwt, String buyerCode, String buyerName,
                       String sellerCode, String sellerName, String commodityHBLNo,
                       DangerousGoodsInfoV2 dangerousGoods) {
        this.cargoNo = cargoNo;
        this.containerNo = containerNo;
        this.itemNo = itemNo;
        this.commodityGroup = commodityGroup;
        this.hsCode = hsCode;
        this.packageType = packageType;
        this.packageQty = packageQty;
        this.cargoVolume = cargoVolume;
        this.volumnUnit = volumnUnit;
        this.nettwt = nettwt;
        this.buyerCode = buyerCode;
        this.buyerName = buyerName;
        this.sellerCode = sellerCode;
        this.sellerName = sellerName;
        this.commodityHBLNo = commodityHBLNo;
        this.dangerousGoods = dangerousGoods;
    }

    public Integer getCargoNo() {
        return cargoNo;
    }

    public void setCargoNo(Integer cargoNo) {
        this.cargoNo = cargoNo;
    }

    public String getContainerNo() {
        return containerNo;
    }

    public void setContainerNo(String containerNo) {
        this.containerNo = containerNo;
    }

    public Integer getItemNo() {
        return itemNo;
    }

    public void setItemNo(Integer itemNo) {
        this.itemNo = itemNo;
    }

    public String getCommodityGroup() {
        return commodityGroup;
    }

    public void setCommodityGroup(String commodityGroup) {
        this.commodityGroup = commodityGroup;
    }

    /** @deprecated Removed from API. Use {@link #getCommodityGroup()}. Not included in Swagger or response. */
    @Deprecated
    @JsonIgnore
    public String getCommodityGroupCode() {
        return commodityGroupCode;
    }

    /** @deprecated Use {@link #setCommodityGroup(String)}. If commodityGroup is blank, maps value to commodityGroup for backward compatibility. */
    @Deprecated
    public void setCommodityGroupCode(String commodityGroupCode) {
        this.commodityGroupCode = commodityGroupCode;
        if (commodityGroupCode != null && !commodityGroupCode.trim().isEmpty() && (commodityGroup == null || commodityGroup.trim().isEmpty())) {
            this.commodityGroup = commodityGroupCode.trim();
        }
    }

    public String getHsCode() {
        return hsCode;
    }

    public void setHsCode(String hsCode) {
        this.hsCode = hsCode;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public Integer getPackageQty() {
        return packageQty;
    }

    public void setPackageQty(Integer packageQty) {
        this.packageQty = packageQty;
    }

    public Double getCargoVolume() {
        return cargoVolume;
    }

    public void setCargoVolume(Double cargoVolume) {
        this.cargoVolume = cargoVolume;
    }

    public VolumeUnit getVolumnUnit() {
        return volumnUnit;
    }

    public void setVolumnUnit(VolumeUnit volumnUnit) {
        this.volumnUnit = volumnUnit;
    }

    public Double getNettwt() {
        return nettwt;
    }

    public void setNettwt(Double nettwt) {
        this.nettwt = nettwt;
    }

    public Float getItemWeight() {
        return itemWeight;
    }

    public void setItemWeight(Float itemWeight) {
        this.itemWeight = itemWeight;
    }

    public WeightUnit getItemWeightUnit() {
        return itemWeightUnit;
    }

    public void setItemWeightUnit(WeightUnit itemWeightUnit) {
        this.itemWeightUnit = itemWeightUnit;
    }

    public String getBuyerCode() {
        return buyerCode;
    }

    public void setBuyerCode(String buyerCode) {
        this.buyerCode = buyerCode;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSellerCode() {
        return sellerCode;
    }

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getCommodityHBLNo() {
        return commodityHBLNo;
    }

    public void setCommodityHBLNo(String commodityHBLNo) {
        this.commodityHBLNo = commodityHBLNo;
    }

    public DangerousGoodsInfoV2 getDangerousGoods() {
        return dangerousGoods;
    }

    public void setDangerousGoods(DangerousGoodsInfoV2 dangerousGoods) {
        this.dangerousGoods = dangerousGoods;
    }
}
