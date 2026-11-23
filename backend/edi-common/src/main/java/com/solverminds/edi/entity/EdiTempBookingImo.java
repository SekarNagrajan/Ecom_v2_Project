package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_imo", schema = "dbo")
public class EdiTempBookingImo implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "eqpId", length = 11)
    private String eqpId;

    @Column(name = "dgNo", length = 20)
    private String dgNo;

    @Column(name = "pageno", length = 10)
    private String pageno;

    @Column(name = "eqpNo")
    private Integer eqpNo;

    @Column(name = "eqpType", length = 4)
    private String eqpType;

    @Column(name = "validityDate")
    private LocalDateTime validityDate;

    @Column(name = "outerQty")
    private Integer outerQty;

    @Column(name = "outerMaterial", length = 50)
    private String outerMaterial;

    @Column(name = "outerKind", length = 15)
    private String outerKind;

    @Column(name = "innerQty")
    private Integer innerQty;

    @Column(name = "innerMaterial", length = 50)
    private String innerMaterial;

    @Column(name = "innerKind", length = 15)
    private String innerKind;

    @Column(name = "Stow1", length = 50)
    private String stow1;

    @Column(name = "Stow2", length = 50)
    private String stow2;

    @Column(name = "UNNo", length = 10)
    private String unNo;

    @Column(name = "ShippingName", length = 500)
    private String shippingName;

    @Column(name = "NoOfPackages", length = 10)
    private String noOfPackages;

    @Column(name = "TypePackages", length = 10)
    private String typePackages;

    @Column(name = "KindPackages", length = 10)
    private String kindPackages;

    @Column(name = "qtyUnit", length = 2)
    private String qtyUnit;

    @Column(name = "IMDGClass", length = 10)
    private String imdgClass;

    @Column(name = "PackingGrp", length = 10)
    private String packingGrp;

    @Column(name = "SubsidiaryLabel", length = 10)
    private String subsidiaryLabel;

    @Column(name = "EMS", length = 10)
    private String ems;

    @Column(name = "MFAG", length = 10)
    private String mfag;

    @Column(name = "FlashPoint")
    private Float flashPoint;

    @Column(name = "flashUnit", length = 2)
    private String flashUnit;

    @Column(name = "NetExpoMass")
    private Float netExpoMass;

    @Column(name = "RQ", length = 1)
    private String rq;

    @Column(name = "EmptyPackage", length = 1)
    private String emptyPackage;

    @Column(name = "LimitedQty", length = 1)
    private String limitedQty;

    @Column(name = "MarinePollutant", length = 1)
    private String marinePollutant;

    @Column(name = "Waste", length = 1)
    private String waste;

    @Column(name = "grossWt")
    private Float grossWt;

    @Column(name = "netWt")
    private Float netWt;

    @Column(name = "volume")
    private Float volume;

    @Column(name = "Weight")
    private Float weight;

    @Column(name = "EmergencyComm", length = 20)
    private String emergencyComm;

    @Column(name = "techname", length = 500)
    private String techname;

    @Column(name = "cargoType", length = 1)
    private String cargoType;

    @Column(name = "ctrTemp")
    private Float ctrTemp;

    @Column(name = "packCode", length = 15)
    private String packCode;

    @Column(name = "isEmpty", length = 1)
    private String isEmpty;

    @Column(name = "emptyDesc", length = 50)
    private String emptyDesc;

    @Column(name = "qtyType", length = 1)
    private String qtyType;

    @Column(name = "observation", length = 150)
    private String observation;

    @Column(name = "lineRemarks", length = 150)
    private String lineRemarks;

    @Column(name = "ctrTempUnit", length = 50)
    private String ctrTempUnit;

    @Column(name = "netExpUnit", length = 5)
    private String netExpUnit;

    @Column(name = "carriercode", length = 3)
    private String carriercode;

    @Column(name = "isedited", length = 1)
    private String isedited;

    @Column(name = "dgcolumn", length = 50)
    private String dgcolumn;

    @Column(name = "dgcolumn1", length = 50)
    private String dgcolumn1;

    @Column(name = "additionallabel", length = 500)
    private String additionallabel;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "edicleanstatus", length = 1)
    private String edicleanstatus;

    @Column(name = "version")
    private Short version;

    // ===== Constructors =====
    public EdiTempBookingImo() {
    }

    // ===== Getters and Setters =====
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getEqpId() {
        return eqpId;
    }

    public void setEqpId(String eqpId) {
        this.eqpId = eqpId;
    }

    public String getDgNo() {
        return dgNo;
    }

    public void setDgNo(String dgNo) {
        this.dgNo = dgNo;
    }

    public String getPageno() {
        return pageno;
    }

    public void setPageno(String pageno) {
        this.pageno = pageno;
    }

    public Integer getEqpNo() {
        return eqpNo;
    }

    public void setEqpNo(Integer eqpNo) {
        this.eqpNo = eqpNo;
    }

    public String getEqpType() {
        return eqpType;
    }

    public void setEqpType(String eqpType) {
        this.eqpType = eqpType;
    }

    public LocalDateTime getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(LocalDateTime validityDate) {
        this.validityDate = validityDate;
    }

    public Integer getOuterQty() {
        return outerQty;
    }

    public void setOuterQty(Integer outerQty) {
        this.outerQty = outerQty;
    }

    public String getOuterMaterial() {
        return outerMaterial;
    }

    public void setOuterMaterial(String outerMaterial) {
        this.outerMaterial = outerMaterial;
    }

    public String getOuterKind() {
        return outerKind;
    }

    public void setOuterKind(String outerKind) {
        this.outerKind = outerKind;
    }

    public Integer getInnerQty() {
        return innerQty;
    }

    public void setInnerQty(Integer innerQty) {
        this.innerQty = innerQty;
    }

    public String getInnerMaterial() {
        return innerMaterial;
    }

    public void setInnerMaterial(String innerMaterial) {
        this.innerMaterial = innerMaterial;
    }

    public String getInnerKind() {
        return innerKind;
    }

    public void setInnerKind(String innerKind) {
        this.innerKind = innerKind;
    }

    public String getStow1() {
        return stow1;
    }

    public void setStow1(String stow1) {
        this.stow1 = stow1;
    }

    public String getStow2() {
        return stow2;
    }

    public void setStow2(String stow2) {
        this.stow2 = stow2;
    }

    public String getUnNo() {
        return unNo;
    }

    public void setUnNo(String unNo) {
        this.unNo = unNo;
    }

    public String getShippingName() {
        return shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public String getNoOfPackages() {
        return noOfPackages;
    }

    public void setNoOfPackages(String noOfPackages) {
        this.noOfPackages = noOfPackages;
    }

    public String getTypePackages() {
        return typePackages;
    }

    public void setTypePackages(String typePackages) {
        this.typePackages = typePackages;
    }

    public String getKindPackages() {
        return kindPackages;
    }

    public void setKindPackages(String kindPackages) {
        this.kindPackages = kindPackages;
    }

    public String getQtyUnit() {
        return qtyUnit;
    }

    public void setQtyUnit(String qtyUnit) {
        this.qtyUnit = qtyUnit;
    }

    public String getImdgClass() {
        return imdgClass;
    }

    public void setImdgClass(String imdgClass) {
        this.imdgClass = imdgClass;
    }

    public String getPackingGrp() {
        return packingGrp;
    }

    public void setPackingGrp(String packingGrp) {
        this.packingGrp = packingGrp;
    }

    public String getSubsidiaryLabel() {
        return subsidiaryLabel;
    }

    public void setSubsidiaryLabel(String subsidiaryLabel) {
        this.subsidiaryLabel = subsidiaryLabel;
    }

    public String getEms() {
        return ems;
    }

    public void setEms(String ems) {
        this.ems = ems;
    }

    public String getMfag() {
        return mfag;
    }

    public void setMfag(String mfag) {
        this.mfag = mfag;
    }

    public Float getFlashPoint() {
        return flashPoint;
    }

    public void setFlashPoint(Float flashPoint) {
        this.flashPoint = flashPoint;
    }

    public String getFlashUnit() {
        return flashUnit;
    }

    public void setFlashUnit(String flashUnit) {
        this.flashUnit = flashUnit;
    }

    public Float getNetExpoMass() {
        return netExpoMass;
    }

    public void setNetExpoMass(Float netExpoMass) {
        this.netExpoMass = netExpoMass;
    }

    public String getRq() {
        return rq;
    }

    public void setRq(String rq) {
        this.rq = rq;
    }

    public String getEmptyPackage() {
        return emptyPackage;
    }

    public void setEmptyPackage(String emptyPackage) {
        this.emptyPackage = emptyPackage;
    }

    public String getLimitedQty() {
        return limitedQty;
    }

    public void setLimitedQty(String limitedQty) {
        this.limitedQty = limitedQty;
    }

    public String getMarinePollutant() {
        return marinePollutant;
    }

    public void setMarinePollutant(String marinePollutant) {
        this.marinePollutant = marinePollutant;
    }

    public String getWaste() {
        return waste;
    }

    public void setWaste(String waste) {
        this.waste = waste;
    }

    public Float getGrossWt() {
        return grossWt;
    }

    public void setGrossWt(Float grossWt) {
        this.grossWt = grossWt;
    }

    public Float getNetWt() {
        return netWt;
    }

    public void setNetWt(Float netWt) {
        this.netWt = netWt;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getEmergencyComm() {
        return emergencyComm;
    }

    public void setEmergencyComm(String emergencyComm) {
        this.emergencyComm = emergencyComm;
    }

    public String getTechname() {
        return techname;
    }

    public void setTechname(String techname) {
        this.techname = techname;
    }

    public String getCargoType() {
        return cargoType;
    }

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }

    public Float getCtrTemp() {
        return ctrTemp;
    }

    public void setCtrTemp(Float ctrTemp) {
        this.ctrTemp = ctrTemp;
    }

    public String getPackCode() {
        return packCode;
    }

    public void setPackCode(String packCode) {
        this.packCode = packCode;
    }

    public String getIsEmpty() {
        return isEmpty;
    }

    public void setIsEmpty(String isEmpty) {
        this.isEmpty = isEmpty;
    }

    public String getEmptyDesc() {
        return emptyDesc;
    }

    public void setEmptyDesc(String emptyDesc) {
        this.emptyDesc = emptyDesc;
    }

    public String getQtyType() {
        return qtyType;
    }

    public void setQtyType(String qtyType) {
        this.qtyType = qtyType;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getLineRemarks() {
        return lineRemarks;
    }

    public void setLineRemarks(String lineRemarks) {
        this.lineRemarks = lineRemarks;
    }

    public String getCtrTempUnit() {
        return ctrTempUnit;
    }

    public void setCtrTempUnit(String ctrTempUnit) {
        this.ctrTempUnit = ctrTempUnit;
    }

    public String getNetExpUnit() {
        return netExpUnit;
    }

    public void setNetExpUnit(String netExpUnit) {
        this.netExpUnit = netExpUnit;
    }

    public String getCarriercode() {
        return carriercode;
    }

    public void setCarriercode(String carriercode) {
        this.carriercode = carriercode;
    }

    public String getIsedited() {
        return isedited;
    }

    public void setIsedited(String isedited) {
        this.isedited = isedited;
    }

    public String getDgcolumn() {
        return dgcolumn;
    }

    public void setDgcolumn(String dgcolumn) {
        this.dgcolumn = dgcolumn;
    }

    public String getDgcolumn1() {
        return dgcolumn1;
    }

    public void setDgcolumn1(String dgcolumn1) {
        this.dgcolumn1 = dgcolumn1;
    }

    public String getAdditionallabel() {
        return additionallabel;
    }

    public void setAdditionallabel(String additionallabel) {
        this.additionallabel = additionallabel;
    }

    public String getEdireaderid() {
        return edireaderid;
    }

    public void setEdireaderid(String edireaderid) {
        this.edireaderid = edireaderid;
    }

    public String getEdicleanstatus() {
        return edicleanstatus;
    }

    public void setEdicleanstatus(String edicleanstatus) {
        this.edicleanstatus = edicleanstatus;
    }

    public Short getVersion() {
        return version;
    }

    public void setVersion(Short version) {
        this.version = version;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBookingImo that = (EdiTempBookingImo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EdiTempBookingImo{" +
                "id=" + id +
                ", bookNo='" + bookNo + '\'' +
                ", eqpId='" + eqpId + '\'' +
                ", dgNo='" + dgNo + '\'' +
                ", unNo='" + unNo + '\'' +
                '}';
    }
}

