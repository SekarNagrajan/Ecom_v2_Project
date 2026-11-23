package com.solverminds.booking.dto.v2;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.svm.ecom.common.enums.TemperatureUnit;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.IOException;

/**
 * Dangerous goods details for V2 booking APIs (TSHG-3000).
 * Nested under {@code containers[].commodities[].dangerousGoodsInfo}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "DangerousGoods", description = "Details about a dangerous good")
public class DangerousGoodsInfoV2 {

    @Schema(description = "UN number (4 digits)", example = "2779")
    private String unNo;

    @Schema(description = "IMDG variant when UN has multiple variants", example = "~")
    private String variant;

    @JsonAlias("classCode")
    @Schema(description = "Primary IMO hazard class", example = "6.1")
    private String imoClass;

    @Schema(description = "Technical name", example = "DINITRO-ORTHO-CRESOL")
    private String technicalName;

    @JsonAlias("flashPt")
    @Schema(description = "Flash point", example = "23.00")
    private Double flashPoint;

    @Schema(description = "Flash point unit", example = "C")
    private TemperatureUnit flashPointUnit;

    @Schema(description = "Emergency contact")
    private DgEmergencyContactV2 emergencyContact;

    @Schema(description = "Outer packing reference")
    private DgPackagingReferenceV2 outerPacking;

    @Schema(description = "Number of outer packings", example = "20")
    private Integer outerPackingQuantity;

    @Schema(description = "Inner packing reference")
    private DgPackagingReferenceV2 innerPacking;

    @Schema(description = "Number of inner packings", example = "1500")
    private Integer innerPackingQuantity;

    @Schema(description = "Gross weight kg", example = "25500.00")
    private Double grossWeight;

    @Schema(description = "Net weight kg", example = "25000.00")
    private Double netWeight;

    @Schema(description = "Net explosive weight kg (class 1.x)", example = "100.00")
    private Double netExplosiveWeight;

    @Schema(description = "Full packaging code", example = "4G/Y30/S/26")
    private String fullPackagingCode;

    @Schema(description = "Self-accelerating decomposition temperature")
    private Double sadt;

    @Schema(description = "Control temperature")
    private Double controlTemperature;

    @Schema(description = "Emergency temperature")
    private Double emergencyTemperature;

    @Schema(description = "Temperature unit for SADT/control/emergency", example = "C")
    private TemperatureUnit temperatureUnit;

    @JsonDeserialize(using = YesNoOrBooleanDeserializer.class)
    @Schema(description = "Limited quantity flag", example = "false")
    private Boolean isLimitedQuantity;

    @JsonDeserialize(using = YesNoOrBooleanDeserializer.class)
    @Schema(description = "Excepted quantity flag", example = "false")
    private Boolean isExceptedQuantity;

    @JsonDeserialize(using = YesNoOrBooleanDeserializer.class)
    @Schema(description = "Reportable quantity flag", example = "false")
    private Boolean isReportableQuantity;

    @JsonDeserialize(using = YesNoOrBooleanDeserializer.class)
    @Schema(description = "Waste flag", example = "false")
    private Boolean isWaste;

    @JsonAlias("marinePollutant")
    @JsonDeserialize(using = YesNoOrBooleanDeserializer.class)
    @Schema(description = "Marine pollutant flag", example = "true")
    private Boolean isMarinePollutant;

    @Schema(description = "Segregation group text", example = "ACIDS")
    private String segregation;

    // ---- read-only enrichment (ignored on input) ----

    @Schema(description = "First subsidiary hazard class", accessMode = Schema.AccessMode.READ_ONLY)
    private String subRisk1;

    @Schema(description = "Second subsidiary hazard class", accessMode = Schema.AccessMode.READ_ONLY)
    private String subRisk2;

    @Schema(description = "Packing group", accessMode = Schema.AccessMode.READ_ONLY, allowableValues = {"I", "II", "III"})
    private String packagingGroup;

    @Schema(description = "EMS codes", accessMode = Schema.AccessMode.READ_ONLY)
    private String ems;

    @Schema(description = "Physical state", accessMode = Schema.AccessMode.READ_ONLY)
    private String state;

    @Schema(description = "Proper shipping name", accessMode = Schema.AccessMode.READ_ONLY)
    private String properShippingName;

    @Schema(description = "Stowage instruction", accessMode = Schema.AccessMode.READ_ONLY)
    private String stowageInstruction;

    @Schema(description = "IMO stowage category", accessMode = Schema.AccessMode.READ_ONLY)
    private String imoStow;

    @Schema(description = "IMDG amendment used at save", accessMode = Schema.AccessMode.READ_ONLY)
    private String imdgAmendment;

    public DangerousGoodsInfoV2() {
    }

    public String getUnNo() {
        return unNo;
    }

    public void setUnNo(String unNo) {
        this.unNo = unNo;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getImoClass() {
        return imoClass;
    }

    public void setImoClass(String imoClass) {
        this.imoClass = imoClass;
    }

    /** Java alias for existing callers; not serialized (JSON uses {@code imoClass}). */
    @JsonIgnore
    public String getClassCode() {
        return imoClass;
    }

    @JsonIgnore
    public void setClassCode(String classCode) {
        this.imoClass = classCode;
    }

    public String getTechnicalName() {
        return technicalName;
    }

    public void setTechnicalName(String technicalName) {
        this.technicalName = technicalName;
    }

    public Double getFlashPoint() {
        return flashPoint;
    }

    public void setFlashPoint(Double flashPoint) {
        this.flashPoint = flashPoint;
    }

    public TemperatureUnit getFlashPointUnit() {
        return flashPointUnit;
    }

    public void setFlashPointUnit(TemperatureUnit flashPointUnit) {
        this.flashPointUnit = flashPointUnit;
    }

    public DgEmergencyContactV2 getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(DgEmergencyContactV2 emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public DgPackagingReferenceV2 getOuterPacking() {
        return outerPacking;
    }

    public void setOuterPacking(DgPackagingReferenceV2 outerPacking) {
        this.outerPacking = outerPacking;
    }

    public Integer getOuterPackingQuantity() {
        return outerPackingQuantity;
    }

    public void setOuterPackingQuantity(Integer outerPackingQuantity) {
        this.outerPackingQuantity = outerPackingQuantity;
    }

    public DgPackagingReferenceV2 getInnerPacking() {
        return innerPacking;
    }

    public void setInnerPacking(DgPackagingReferenceV2 innerPacking) {
        this.innerPacking = innerPacking;
    }

    public Integer getInnerPackingQuantity() {
        return innerPackingQuantity;
    }

    public void setInnerPackingQuantity(Integer innerPackingQuantity) {
        this.innerPackingQuantity = innerPackingQuantity;
    }

    public Double getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(Double grossWeight) {
        this.grossWeight = grossWeight;
    }

    public Double getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Double netWeight) {
        this.netWeight = netWeight;
    }

    public Double getNetExplosiveWeight() {
        return netExplosiveWeight;
    }

    public void setNetExplosiveWeight(Double netExplosiveWeight) {
        this.netExplosiveWeight = netExplosiveWeight;
    }

    public String getFullPackagingCode() {
        return fullPackagingCode;
    }

    public void setFullPackagingCode(String fullPackagingCode) {
        this.fullPackagingCode = fullPackagingCode;
    }

    public Double getSadt() {
        return sadt;
    }

    public void setSadt(Double sadt) {
        this.sadt = sadt;
    }

    public Double getControlTemperature() {
        return controlTemperature;
    }

    public void setControlTemperature(Double controlTemperature) {
        this.controlTemperature = controlTemperature;
    }

    public Double getEmergencyTemperature() {
        return emergencyTemperature;
    }

    public void setEmergencyTemperature(Double emergencyTemperature) {
        this.emergencyTemperature = emergencyTemperature;
    }

    public TemperatureUnit getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(TemperatureUnit temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public Boolean getIsLimitedQuantity() {
        return isLimitedQuantity;
    }

    public void setIsLimitedQuantity(Boolean isLimitedQuantity) {
        this.isLimitedQuantity = isLimitedQuantity;
    }

    public Boolean getIsExceptedQuantity() {
        return isExceptedQuantity;
    }

    public void setIsExceptedQuantity(Boolean isExceptedQuantity) {
        this.isExceptedQuantity = isExceptedQuantity;
    }

    public Boolean getIsReportableQuantity() {
        return isReportableQuantity;
    }

    public void setIsReportableQuantity(Boolean isReportableQuantity) {
        this.isReportableQuantity = isReportableQuantity;
    }

    public Boolean getIsWaste() {
        return isWaste;
    }

    public void setIsWaste(Boolean isWaste) {
        this.isWaste = isWaste;
    }

    public Boolean getIsMarinePollutant() {
        return isMarinePollutant;
    }

    public void setIsMarinePollutant(Boolean isMarinePollutant) {
        this.isMarinePollutant = isMarinePollutant;
    }

    public String getSegregation() {
        return segregation;
    }

    public void setSegregation(String segregation) {
        this.segregation = segregation;
    }

    public String getSubRisk1() {
        return subRisk1;
    }

    public void setSubRisk1(String subRisk1) {
        this.subRisk1 = subRisk1;
    }

    public String getSubRisk2() {
        return subRisk2;
    }

    public void setSubRisk2(String subRisk2) {
        this.subRisk2 = subRisk2;
    }

    public String getPackagingGroup() {
        return packagingGroup;
    }

    public void setPackagingGroup(String packagingGroup) {
        this.packagingGroup = packagingGroup;
    }

    public String getEms() {
        return ems;
    }

    public void setEms(String ems) {
        this.ems = ems;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProperShippingName() {
        return properShippingName;
    }

    public void setProperShippingName(String properShippingName) {
        this.properShippingName = properShippingName;
    }

    public String getStowageInstruction() {
        return stowageInstruction;
    }

    public void setStowageInstruction(String stowageInstruction) {
        this.stowageInstruction = stowageInstruction;
    }

    public String getImoStow() {
        return imoStow;
    }

    public void setImoStow(String imoStow) {
        this.imoStow = imoStow;
    }

    public String getImdgAmendment() {
        return imdgAmendment;
    }

    public void setImdgAmendment(String imdgAmendment) {
        this.imdgAmendment = imdgAmendment;
    }

    /** Accepts true/false or legacy {@code Y}/{@code N} strings. */
    public static final class YesNoOrBooleanDeserializer extends JsonDeserializer<Boolean> {
        @Override
        public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken t = p.currentToken();
            if (t == JsonToken.VALUE_NULL) {
                return null;
            }
            if (t == JsonToken.VALUE_TRUE) {
                return true;
            }
            if (t == JsonToken.VALUE_FALSE) {
                return false;
            }
            if (t == JsonToken.VALUE_STRING) {
                String s = p.getText().trim();
                if (s.isEmpty()) {
                    return null;
                }
                if ("Y".equalsIgnoreCase(s) || "true".equalsIgnoreCase(s)) {
                    return true;
                }
                if ("N".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) {
                    return false;
                }
            }
            return (Boolean) ctxt.handleUnexpectedToken(Boolean.class, p);
        }
    }
}
