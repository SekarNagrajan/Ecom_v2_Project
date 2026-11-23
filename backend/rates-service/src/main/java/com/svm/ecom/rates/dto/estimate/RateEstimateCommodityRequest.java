package com.svm.ecom.rates.dto.estimate;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(name = "RateEstimateCommodityRequest", description = "Commodity details for a container line")
public class RateEstimateCommodityRequest {

    @NotBlank
    @Schema(description = "Commodity group / code used by surcharge engine", example = "FAK",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String commodityGroup;

    @Schema(description = "HS code (not used by surcharge engine)", example = "080390")
    private String hsCode;

    @NotNull
    @DecimalMin("0.0")
    @Schema(description = "Cargo weight (fallback when container grossWeight is missing)",
            example = "120.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal cargoWeight;

    @NotBlank
    @Schema(description = "Cargo weight unit", example = "KG", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cargoWeightUnit;

    @Valid
    @Schema(description = "Dangerous goods details — required when container isDG is true")
    private RateEstimateDangerousGoodsRequest dangerousGoodsInfo;

    public String getCommodityGroup() { return commodityGroup; }
    public void setCommodityGroup(String commodityGroup) { this.commodityGroup = commodityGroup; }
    public String getHsCode() { return hsCode; }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }
    public BigDecimal getCargoWeight() { return cargoWeight; }
    public void setCargoWeight(BigDecimal cargoWeight) { this.cargoWeight = cargoWeight; }
    public String getCargoWeightUnit() { return cargoWeightUnit; }
    public void setCargoWeightUnit(String cargoWeightUnit) { this.cargoWeightUnit = cargoWeightUnit; }
    public RateEstimateDangerousGoodsRequest getDangerousGoodsInfo() { return dangerousGoodsInfo; }
    public void setDangerousGoodsInfo(RateEstimateDangerousGoodsRequest dangerousGoodsInfo) {
        this.dangerousGoodsInfo = dangerousGoodsInfo;
    }
}
