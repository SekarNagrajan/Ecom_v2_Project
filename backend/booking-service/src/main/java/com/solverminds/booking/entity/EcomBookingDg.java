package com.solverminds.booking.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * Dangerous Goods (DG) details entity for hazardous materials.
 * Table: ecombooking_dg
 */
@Entity
@Table(name = "ecombooking_dg", schema = "dbo")
public class EcomBookingDg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "ebookno", length = 20, nullable = false)
    private String ebookno;

    @Column(name = "conid", nullable = false)
    private Integer conid;

    @Column(name = "itemno", nullable = false)
    private Integer itemno;

    @Column(name = "containerno", length = 11)
    private String containerno;

    @Column(name = "MarinePollutant", length = 1)
    private String marinePollutant;

    @Column(name = "unNo", length = 10)
    private String unNo;

    @Column(name = "class", length = 10)
    private String classCode;

    @Column(name = "FlashPt")
    private Float flashPt;

    @Column(name = "FlashPtUnit", length = 1)
    private String flashPtUnit;

    @Column(name = "variant", length = 10)
    private String variant;

    @Column(name = "imdgAmendment", length = 10)
    private String imdgAmendment;

    @Column(name = "technicalName", length = 500)
    private String technicalName;

    @Column(name = "subRisk1", length = 50)
    private String subRisk1;

    @Column(name = "subRisk2", length = 50)
    private String subRisk2;

    @Column(name = "packagingGroup", length = 10)
    private String packagingGroup;

    @Column(name = "ems", length = 50)
    private String ems;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "properShippingName", length = 500)
    private String properShippingName;

    @Column(name = "stowageInstruction", length = 500)
    private String stowageInstruction;

    @Column(name = "imoStow", length = 100)
    private String imoStow;

    @Column(name = "segregation", length = 500)
    private String segregation;

    @Column(name = "ContactName", length = 100)
    private String contactName;

    @Column(name = "emgCountryCode", length = 10)
    private String emgCountryCode;

    @Column(name = "emgAreaCode", length = 10)
    private String emgAreaCode;

    @Column(name = "emgLocalNumber", length = 30)
    private String emgLocalNumber;

    @Column(name = "outerPackCode", length = 20)
    private String outerPackCode;

    @Column(name = "outerPackDesc", length = 200)
    private String outerPackDesc;

    @Column(name = "outerPackQty")
    private Integer outerPackQty;

    @Column(name = "innerPackCode", length = 20)
    private String innerPackCode;

    @Column(name = "innerPackDesc", length = 200)
    private String innerPackDesc;

    @Column(name = "innerPackQty")
    private Integer innerPackQty;

    @Column(name = "grossWt")
    private Float grossWt;

    @Column(name = "netWt")
    private Float netWt;

    @Column(name = "netExplWt")
    private Float netExplWt;

    @Column(name = "fullPackagingCode", length = 50)
    private String fullPackagingCode;

    @Column(name = "SADTTemp", length = 20)
    private String sadtTemp;

    @Column(name = "ControlTemp", length = 20)
    private String controlTemp;

    @Column(name = "EmergencyTemp", length = 20)
    private String emergencyTemp;

    @Column(name = "TempUnit", length = 5)
    private String tempUnit;

    @Column(name = "LimitedQty", length = 1)
    private String limitedQty;

    @Column(name = "ExceptedQty", length = 1)
    private String exceptedQty;

    @Column(name = "ReportableQty", length = 1)
    private String reportableQty;

    @Column(name = "Waste", length = 1)
    private String waste;

    public EcomBookingDg() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEbookno() { return ebookno; }
    public void setEbookno(String ebookno) { this.ebookno = ebookno; }
    public Integer getConid() { return conid; }
    public void setConid(Integer conid) { this.conid = conid; }
    public Integer getItemno() { return itemno; }
    public void setItemno(Integer itemno) { this.itemno = itemno; }
    public String getContainerno() { return containerno; }
    public void setContainerno(String containerno) { this.containerno = containerno; }
    public String getMarinePollutant() { return marinePollutant; }
    public void setMarinePollutant(String marinePollutant) { this.marinePollutant = marinePollutant; }
    public String getUnNo() { return unNo; }
    public void setUnNo(String unNo) { this.unNo = unNo; }
    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }
    public Float getFlashPt() { return flashPt; }
    public void setFlashPt(Float flashPt) { this.flashPt = flashPt; }
    public String getFlashPtUnit() { return flashPtUnit; }
    public void setFlashPtUnit(String flashPtUnit) { this.flashPtUnit = flashPtUnit; }
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
    public Float getGrossWt() { return grossWt; }
    public void setGrossWt(Float grossWt) { this.grossWt = grossWt; }
    public Float getNetWt() { return netWt; }
    public void setNetWt(Float netWt) { this.netWt = netWt; }
    public Float getNetExplWt() { return netExplWt; }
    public void setNetExplWt(Float netExplWt) { this.netExplWt = netExplWt; }
    public String getFullPackagingCode() { return fullPackagingCode; }
    public void setFullPackagingCode(String fullPackagingCode) { this.fullPackagingCode = fullPackagingCode; }
    public String getSadtTemp() { return sadtTemp; }
    public void setSadtTemp(String sadtTemp) { this.sadtTemp = sadtTemp; }
    public String getControlTemp() { return controlTemp; }
    public void setControlTemp(String controlTemp) { this.controlTemp = controlTemp; }
    public String getEmergencyTemp() { return emergencyTemp; }
    public void setEmergencyTemp(String emergencyTemp) { this.emergencyTemp = emergencyTemp; }
    public String getTempUnit() { return tempUnit; }
    public void setTempUnit(String tempUnit) { this.tempUnit = tempUnit; }
    public String getLimitedQty() { return limitedQty; }
    public void setLimitedQty(String limitedQty) { this.limitedQty = limitedQty; }
    public String getExceptedQty() { return exceptedQty; }
    public void setExceptedQty(String exceptedQty) { this.exceptedQty = exceptedQty; }
    public String getReportableQty() { return reportableQty; }
    public void setReportableQty(String reportableQty) { this.reportableQty = reportableQty; }
    public String getWaste() { return waste; }
    public void setWaste(String waste) { this.waste = waste; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomBookingDg)) return false;
        EcomBookingDg that = (EcomBookingDg) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
