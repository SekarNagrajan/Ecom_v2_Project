package com.solverminds.booking.dto.v2;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.solverminds.booking.serializer.TwoDecimalSerializer;
import com.solverminds.booking.dto.validation.CargoVolumeRequiresVolumeUnit;
import com.svm.ecom.common.enums.VolumeUnit;
import com.svm.ecom.common.enums.WeightUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Details about a commodity in a booked container for V2 API.
 */
@Schema(name = "BookingContainerCommodity", description = "Details about a commodity in a booked container")
@CargoVolumeRequiresVolumeUnit
public class BookingContainerCommodityV2 {

    @NotBlank
    @Schema(description = "Commodity group code", example = "90")
    @Size(max = 150, message = "commodityGroup must not exceed 150 characters")
    private String commodityGroup;

    @NotBlank
    @Schema(description = "Harmonized System Code (HS) code", example = "9001101000")
    @Size(max = 150, message = "hsCode must not exceed 150 characters")
    private String hsCode;

    @NotBlank
    @Schema(description = "Package type", example = "APBW")
    @Size(max = 4, message = "packageType must not exceed 4 characters")
    private String packageType;

    @NotNull
    @Min(value = 0, message = "packageQuantity must not be negative")
    @Schema(description = "Number of packages", example = "50")
    private Integer packageQuantity;

    @NotNull
    @Schema(description = "Total cargo weight in provided unit", example = "10000.00")
    @JsonSerialize(using = TwoDecimalSerializer.class)
    private Double cargoWeight;

    @NotNull
    @Schema(description = "Weight unit", example = "K")
    private WeightUnit cargoWeightUnit;

    @DecimalMin(value = "0", inclusive = true, message = "cargoVolume must not be negative")
    @Schema(description = "Cargo volume in provided unit", example = "120.00", minimum = "0")
    @JsonSerialize(using = TwoDecimalSerializer.class)
    private Double cargoVolume;

    @Schema(description = "Volume unit — required when cargoVolume is set", example = "M")
    private VolumeUnit cargoVolumeUnit;

    @DecimalMin(value = "0", inclusive = true, message = "netWeight must not be negative")
    @Schema(description = "Net weight in kilograms", example = "7500.00", minimum = "0")
    @JsonSerialize(using = TwoDecimalSerializer.class)
    private Double netWeight;

    @Schema(description = "Buyer code of the commodity", example = "BUY001")
    @Size(max = 15, message = "buyerCode must not exceed 15 characters")
    private String buyerCode;

    @Schema(description = "Seller code of the commodity", example = "SEL001")
    @Size(max = 15, message = "sellerCode must not exceed 15 characters")
    private String sellerCode;

    @Schema(description = "Buyer name of the commodity", example = "ABC Traders Ltd")
    @Size(max = 150, message = "buyerName must not exceed 150 characters")
    private String buyerName;

    @Schema(description = "Seller name of the commodity", example = "XYZ Exports Inc")
    @Size(max = 150, message = "sellerName must not exceed 150 characters")
    private String sellerName;

    @Schema(description = "Commodity HBL number", example = "HBL12345")
    @Size(max = 20, message = "commodityHBLNo must not exceed 20 characters")
    private String commodityHBLNo;

    @Valid
    @JsonAlias("dangerousGoods")
    @Schema(description = "Dangerous goods information (JSON may use dangerousGoods as an alias)")
    private DangerousGoodsInfoV2 dangerousGoodsInfo;

    public BookingContainerCommodityV2() {
    }

    public String getCommodityGroup() {
        return commodityGroup;
    }

    public void setCommodityGroup(String commodityGroup) {
        this.commodityGroup = commodityGroup;
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

    public Integer getPackageQuantity() {
        return packageQuantity;
    }

    public void setPackageQuantity(Integer packageQuantity) {
        this.packageQuantity = packageQuantity;
    }

    public Double getCargoWeight() {
        return cargoWeight;
    }

    public void setCargoWeight(Double cargoWeight) {
        this.cargoWeight = cargoWeight;
    }

    public WeightUnit getCargoWeightUnit() {
        return cargoWeightUnit;
    }

    public void setCargoWeightUnit(WeightUnit cargoWeightUnit) {
        this.cargoWeightUnit = cargoWeightUnit;
    }

    public Double getCargoVolume() {
        return cargoVolume;
    }

    public void setCargoVolume(Double cargoVolume) {
        this.cargoVolume = cargoVolume;
    }

    public VolumeUnit getCargoVolumeUnit() {
        return cargoVolumeUnit;
    }

    public void setCargoVolumeUnit(VolumeUnit cargoVolumeUnit) {
        this.cargoVolumeUnit = cargoVolumeUnit;
    }

    public Double getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Double netWeight) {
        this.netWeight = netWeight;
    }

    public String getBuyerCode() {
        return buyerCode;
    }

    public void setBuyerCode(String buyerCode) {
        this.buyerCode = buyerCode;
    }

    public String getSellerCode() {
        return sellerCode;
    }

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
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

    public DangerousGoodsInfoV2 getDangerousGoodsInfo() {
        return dangerousGoodsInfo;
    }

    public void setDangerousGoodsInfo(DangerousGoodsInfoV2 dangerousGoodsInfo) {
        this.dangerousGoodsInfo = dangerousGoodsInfo;
    }
}

