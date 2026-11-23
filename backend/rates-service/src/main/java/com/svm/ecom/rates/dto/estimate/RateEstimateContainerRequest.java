package com.svm.ecom.rates.dto.estimate;

import com.svm.ecom.common.enums.EquipmentStatus;
import com.svm.ecom.common.enums.EquipmentType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Schema(name = "RateEstimateContainerRequest", description = "Container line for Auto Rate estimate")
public class RateEstimateContainerRequest {

    @NotNull
    @Min(1)
    @Schema(description = "Number of containers", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @NotNull
    @Schema(description = "Container type: D (Dry) or R (Reefer)", example = "D", requiredMode = Schema.RequiredMode.REQUIRED)
    private EquipmentType type;

    @NotBlank
    @Schema(description = "Container ISO code",
            example = "40HR", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotNull
    @Schema(description = "Container load status: F (Full) or M (Empty)", example = "F",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private EquipmentStatus status;

    @Schema(description = "Out-of-gauge flag (defaults to false when omitted)", example = "false")
    private Boolean isOOG;

    @NotNull
    @Schema(description = "Shipper-owned container flag", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isSOC;

    @Schema(description = "Dangerous goods flag (defaults to false when omitted)", example = "false")
    private Boolean isDG;

    @Schema(description = "Non-operating reefer flag (defaults to false when omitted)", example = "false")
    private Boolean isNOR;

    @DecimalMin("0.0")
    @Schema(description = "Empty container weight", example = "10.00")
    private BigDecimal tareWeight;

    @NotNull
    @DecimalMin("0.0")
    @Schema(description = "Gross cargo weight", example = "120.00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal grossWeight;

    @NotBlank
    @Schema(description = "Rate reference (Booking cargo rateno). For TAR: tariff rate no e.g. M26070800002",
            example = "M26070800002", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rateRefNo;

    @NotBlank
    @Schema(description = "Booking ratetype: SRR, CRA, SER, TEN, TAR, FMC, CMS, SCR, TAF, SRA",
            example = "CMS", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rateType;

    @NotBlank
    @Schema(description = "Booking cargo itemno / orderno (rate line). Required. "
            + "Used with rateRefNo + rateType + amendmentNumber for Commercial OFR lookup.",
            example = "14", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rateItem;

    @NotBlank
    @Schema(description = "Booking cargo amendmentno. Required.",
            example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String amendmentNumber;

    @Valid
    @Schema(description = "Reefer settings applicable for reefer containers")
    private RateEstimateReeferSettings reeferSettings;

    @NotEmpty
    @Valid
    @Schema(description = "Commodities details", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RateEstimateCommodityRequest> commodities = new ArrayList<>();

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public EquipmentType getType() { return type; }
    public void setType(EquipmentType type) { this.type = type; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public EquipmentStatus getStatus() { return status; }
    public void setStatus(EquipmentStatus status) { this.status = status; }
    public Boolean getIsOOG() { return isOOG; }
    public void setIsOOG(Boolean isOOG) { this.isOOG = isOOG; }
    public Boolean getIsSOC() { return isSOC; }
    public void setIsSOC(Boolean isSOC) { this.isSOC = isSOC; }
    public Boolean getIsDG() { return isDG; }
    public void setIsDG(Boolean isDG) { this.isDG = isDG; }
    public Boolean getIsNOR() { return isNOR; }
    public void setIsNOR(Boolean isNOR) { this.isNOR = isNOR; }
    public BigDecimal getTareWeight() { return tareWeight; }
    public void setTareWeight(BigDecimal tareWeight) { this.tareWeight = tareWeight; }
    public BigDecimal getGrossWeight() { return grossWeight; }
    public void setGrossWeight(BigDecimal grossWeight) { this.grossWeight = grossWeight; }
    public String getRateRefNo() { return rateRefNo; }
    public void setRateRefNo(String rateRefNo) { this.rateRefNo = rateRefNo; }
    public String getRateType() { return rateType; }
    public void setRateType(String rateType) { this.rateType = rateType; }
    public String getRateItem() { return rateItem; }
    public void setRateItem(String rateItem) { this.rateItem = rateItem; }
    public String getAmendmentNumber() { return amendmentNumber; }
    public void setAmendmentNumber(String amendmentNumber) { this.amendmentNumber = amendmentNumber; }
    public RateEstimateReeferSettings getReeferSettings() { return reeferSettings; }
    public void setReeferSettings(RateEstimateReeferSettings reeferSettings) { this.reeferSettings = reeferSettings; }
    public List<RateEstimateCommodityRequest> getCommodities() { return commodities; }
    public void setCommodities(List<RateEstimateCommodityRequest> commodities) {
        this.commodities = commodities != null ? commodities : new ArrayList<>();
    }
}
