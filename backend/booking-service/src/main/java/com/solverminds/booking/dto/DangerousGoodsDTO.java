package com.solverminds.booking.dto;

import com.svm.ecom.common.enums.TemperatureUnit;
import com.svm.ecom.common.enums.YesNoFlag;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Internal dangerous goods row (persistence / view mapping). Not part of the booking API surface.
 */
@Schema(hidden = true)
public class DangerousGoodsDTO {

    private Integer cargoNo;
    private String containerNo;
    private Integer itemNo;
    private YesNoFlag marinePollutant;
    private String unNo;
    private Integer flashPt;
    private TemperatureUnit flashPtUnit;
    private String classCode;

    private String variant;
    private String imdgAmendment;
    private String technicalName;
    private String subRisk1;
    private String subRisk2;
    private String packagingGroup;
    private String ems;
    private String state;
    private String properShippingName;
    private String stowageInstruction;
    private String imoStow;
    private String segregation;
    private String contactName;
    private String emgCountryCode;
    private String emgAreaCode;
    private String emgLocalNumber;
    private String outerPackCode;
    private String outerPackDesc;
    private Integer outerPackQty;
    private String innerPackCode;
    private String innerPackDesc;
    private Integer innerPackQty;
    private Double grossWeight;
    private Double netWeight;
    private Double netExplosiveWeight;
    private String fullPackagingCode;
    private Double sadt;
    private Double controlTemperature;
    private Double emergencyTemperature;
    private TemperatureUnit temperatureUnit;
    private YesNoFlag limitedQuantity;
    private YesNoFlag exceptedQuantity;
    private YesNoFlag reportableQuantity;
    private YesNoFlag waste;

    public Integer getCargoNo() { return cargoNo; }
    public void setCargoNo(Integer cargoNo) { this.cargoNo = cargoNo; }
    public String getContainerNo() { return containerNo; }
    public void setContainerNo(String containerNo) { this.containerNo = containerNo; }
    public Integer getItemNo() { return itemNo; }
    public void setItemNo(Integer itemNo) { this.itemNo = itemNo; }
    public YesNoFlag getMarinePollutant() { return marinePollutant; }
    public void setMarinePollutant(YesNoFlag marinePollutant) { this.marinePollutant = marinePollutant; }
    public String getUnNo() { return unNo; }
    public void setUnNo(String unNo) { this.unNo = unNo; }
    public Integer getFlashPt() { return flashPt; }
    public void setFlashPt(Integer flashPt) { this.flashPt = flashPt; }
    public TemperatureUnit getFlashPtUnit() { return flashPtUnit; }
    public void setFlashPtUnit(TemperatureUnit flashPtUnit) { this.flashPtUnit = flashPtUnit; }
    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }
    public String getVariant() { return variant; }
    public void setVariant(String variant) { this.variant = variant; }
    public String getImdgAmendment() { return imdgAmendment; }
    public void setImdgAmendment(String imdgAmendment) { this.imdgAmendment = imdgAmendment; }
    public String getTechnicalName() { return technicalName; }
    public void setTechnicalName(String technicalName) { this.technicalName = technicalName; }
    public String getSubRisk1() { return subRisk1; }
    public void setSubRisk1(String subRisk1) { this.subRisk1 = subRisk1; }
    public String getSubRisk2() { return subRisk2; }
    public void setSubRisk2(String subRisk2) { this.subRisk2 = subRisk2; }
    public String getPackagingGroup() { return packagingGroup; }
    public void setPackagingGroup(String packagingGroup) { this.packagingGroup = packagingGroup; }
    public String getEms() { return ems; }
    public void setEms(String ems) { this.ems = ems; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getProperShippingName() { return properShippingName; }
    public void setProperShippingName(String properShippingName) { this.properShippingName = properShippingName; }
    public String getStowageInstruction() { return stowageInstruction; }
    public void setStowageInstruction(String stowageInstruction) { this.stowageInstruction = stowageInstruction; }
    public String getImoStow() { return imoStow; }
    public void setImoStow(String imoStow) { this.imoStow = imoStow; }
    public String getSegregation() { return segregation; }
    public void setSegregation(String segregation) { this.segregation = segregation; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getEmgCountryCode() { return emgCountryCode; }
    public void setEmgCountryCode(String emgCountryCode) { this.emgCountryCode = emgCountryCode; }
    public String getEmgAreaCode() { return emgAreaCode; }
    public void setEmgAreaCode(String emgAreaCode) { this.emgAreaCode = emgAreaCode; }
    public String getEmgLocalNumber() { return emgLocalNumber; }
    public void setEmgLocalNumber(String emgLocalNumber) { this.emgLocalNumber = emgLocalNumber; }
    public String getOuterPackCode() { return outerPackCode; }
    public void setOuterPackCode(String outerPackCode) { this.outerPackCode = outerPackCode; }
    public String getOuterPackDesc() { return outerPackDesc; }
    public void setOuterPackDesc(String outerPackDesc) { this.outerPackDesc = outerPackDesc; }
    public Integer getOuterPackQty() { return outerPackQty; }
    public void setOuterPackQty(Integer outerPackQty) { this.outerPackQty = outerPackQty; }
    public String getInnerPackCode() { return innerPackCode; }
    public void setInnerPackCode(String innerPackCode) { this.innerPackCode = innerPackCode; }
    public String getInnerPackDesc() { return innerPackDesc; }
    public void setInnerPackDesc(String innerPackDesc) { this.innerPackDesc = innerPackDesc; }
    public Integer getInnerPackQty() { return innerPackQty; }
    public void setInnerPackQty(Integer innerPackQty) { this.innerPackQty = innerPackQty; }
    public Double getGrossWeight() { return grossWeight; }
    public void setGrossWeight(Double grossWeight) { this.grossWeight = grossWeight; }
    public Double getNetWeight() { return netWeight; }
    public void setNetWeight(Double netWeight) { this.netWeight = netWeight; }
    public Double getNetExplosiveWeight() { return netExplosiveWeight; }
    public void setNetExplosiveWeight(Double netExplosiveWeight) { this.netExplosiveWeight = netExplosiveWeight; }
    public String getFullPackagingCode() { return fullPackagingCode; }
    public void setFullPackagingCode(String fullPackagingCode) { this.fullPackagingCode = fullPackagingCode; }
    public Double getSadt() { return sadt; }
    public void setSadt(Double sadt) { this.sadt = sadt; }
    public Double getControlTemperature() { return controlTemperature; }
    public void setControlTemperature(Double controlTemperature) { this.controlTemperature = controlTemperature; }
    public Double getEmergencyTemperature() { return emergencyTemperature; }
    public void setEmergencyTemperature(Double emergencyTemperature) { this.emergencyTemperature = emergencyTemperature; }
    public TemperatureUnit getTemperatureUnit() { return temperatureUnit; }
    public void setTemperatureUnit(TemperatureUnit temperatureUnit) { this.temperatureUnit = temperatureUnit; }
    public YesNoFlag getLimitedQuantity() { return limitedQuantity; }
    public void setLimitedQuantity(YesNoFlag limitedQuantity) { this.limitedQuantity = limitedQuantity; }
    public YesNoFlag getExceptedQuantity() { return exceptedQuantity; }
    public void setExceptedQuantity(YesNoFlag exceptedQuantity) { this.exceptedQuantity = exceptedQuantity; }
    public YesNoFlag getReportableQuantity() { return reportableQuantity; }
    public void setReportableQuantity(YesNoFlag reportableQuantity) { this.reportableQuantity = reportableQuantity; }
    public YesNoFlag getWaste() { return waste; }
    public void setWaste(YesNoFlag waste) { this.waste = waste; }
}
