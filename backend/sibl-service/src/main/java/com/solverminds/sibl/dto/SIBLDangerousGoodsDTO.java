package com.solverminds.sibl.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.svm.ecom.common.enums.TemperatureUnit;
import com.svm.ecom.common.enums.YesNoFlag;
import com.solverminds.sibl.util.DtoUtil;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Dangerous Goods details at commodity level (required when commodity hazardous = "Y").
 * Schema exposes only: unNo, classCode, flashPoint, flashPointUnit, isMarinePollutant.
 */
@Schema(name = "DangerousGoods", description = "Details about a dangerous good (required when commodity hazardous=Y)")
public class SIBLDangerousGoodsDTO {

    private YesNoFlag marinePollutant;

    @Schema(description = "UN number of the dangerous good", example = "1100")
    private String unNo;

    @JsonProperty("classCode")
    @JsonAlias("dgClass")
    @Schema(description = "Class code of the dangerous good", example = "3")
    private String imdgClass;

    @JsonProperty("flashPoint")
    @Schema(description = "Flash point of the dangerous good in the provided unit", example = "11")
    private Double flashPt;

    @JsonProperty("flashPointUnit")
    @Schema(description = "Temperature Unit: C - Celsius, F - Fahrenheit", example = "C")
    private TemperatureUnit flashPtUnit;

    @Schema(description = "IMO contact person name", example = "IMO CONTACT NAME", hidden = true)
    private String imoContact;

    @Schema(description = "IMO contact phone number", example = "+919876543210", hidden = true)
    private String imoPhone;

    // ===== Constructors =====
    public SIBLDangerousGoodsDTO() {}

    // ===== Getters & Setters =====
    @JsonIgnore
    public YesNoFlag getMarinePollutant() { return marinePollutant; }
    public void setMarinePollutant(YesNoFlag marinePollutant) { this.marinePollutant = marinePollutant; }

    @JsonProperty("isMarinePollutant")
    @Schema(description = "Is the commodity a marine pollutant", example = "true")
    public Boolean getIsMarinePollutant() {
        return marinePollutant != null && marinePollutant == YesNoFlag.Y;
    }

    /** Accepts boolean from request field isMarinePollutant (e.g. dangerousGoodsInfo.isMarinePollutant). */
    @JsonSetter("isMarinePollutant")
    public void setIsMarinePollutant(Boolean value) {
        this.marinePollutant = (value != null && value) ? YesNoFlag.Y : YesNoFlag.N;
    }

    public String getUnNo() { return DtoUtil.emptyIfNull(unNo); }
    public void setUnNo(String unNo) { this.unNo = unNo; }

    public String getImdgClass() { return DtoUtil.emptyIfNull(imdgClass); }
    public void setImdgClass(String imdgClass) { this.imdgClass = imdgClass; }

    public Double getFlashPt() { return flashPt; }
    public void setFlashPt(Double flashPt) { this.flashPt = flashPt; }

    public TemperatureUnit getFlashPtUnit() { return flashPtUnit; }
    public void setFlashPtUnit(TemperatureUnit flashPtUnit) { this.flashPtUnit = flashPtUnit; }

    public String getImoContact() { return DtoUtil.emptyIfNull(imoContact); }
    public void setImoContact(String imoContact) { this.imoContact = imoContact; }

    public String getImoPhone() { return DtoUtil.emptyIfNull(imoPhone); }
    public void setImoPhone(String imoPhone) { this.imoPhone = imoPhone; }
}
