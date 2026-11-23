package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_cargo", schema = "dbo")
public class EdiTempBookingCargo implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Long cid;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "cargoNo")
    private Integer cargoNo;

    @Column(name = "eqpStatus", length = 1)
    private String eqpStatus;

    @Column(name = "isDummy", length = 1)
    private String isDummy;

    @Column(name = "type", length = 1)
    private String type;

    @Column(name = "oog", length = 1)
    private String oog;

    @Column(name = "eqpTypeId", length = 4)
    private String eqpTypeId;

    @Column(name = "eqpId", length = 11)
    private String eqpId;

    @Column(name = "shipperOwn", length = 1)
    private String shipperOwn;

    @Column(name = "roundTrip", length = 1)
    private String roundTrip;

    @Column(name = "seal1No", length = 17)
    private String seal1No;

    @Column(name = "seal2No", length = 40)
    private String seal2No;

    @Column(name = "otherSeal", length = 50)
    private String otherSeal;

    @Column(name = "nor", length = 1)
    private String nor;

    @Column(name = "tempMax")
    private Float tempMax;

    @Column(name = "tempMin")
    private Float tempMin;

    @Column(name = "setTemp")
    private Float setTemp;

    @Column(name = "tempUnit", length = 1)
    private String tempUnit;

    @Column(name = "tareWeight")
    private Float tareWeight;

    @Column(name = "cargoWeight")
    private Float cargoWeight;

    @Column(name = "grossWeight")
    private Float grossWeight;

    @Column(name = "weightUnit", length = 1)
    private String weightUnit;

    @Column(name = "ventSetting")
    private Integer ventSetting;

    @Column(name = "containerPosition", length = 2)
    private String containerPosition;

    @Column(name = "loadingCondition", length = 2)
    private String loadingCondition;

    @Column(name = "chassisId", length = 11)
    private String chassisId;

    @Column(name = "gensetId", length = 11)
    private String gensetId;

    @Column(name = "eqpVolume")
    private Float eqpVolume;

    @Column(name = "eqpVolumeUnit", length = 1)
    private String eqpVolumeUnit;

    @Column(name = "carriercode", length = 3)
    private String carriercode;

    @Column(name = "actStatus", length = 1)
    private String actStatus;

    @Column(name = "orderno", length = 10)
    private String orderno;

    @Column(name = "raterefno", length = 50)
    private String raterefno;

    @Column(name = "ratetype", length = 5)
    private String ratetype;

    @Column(name = "dgflag", length = 1)
    private String dgflag;

    @Column(name = "status", length = 1)
    private String status;

    @Column(name = "remarks", length = 200)
    private String remarks;

    @Column(name = "isedited", length = 1)
    private String isedited;

    @Column(name = "slotloss")
    private Integer slotloss;

    @Column(name = "raterule", length = 3)
    private String raterule;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "edicleanstatus", length = 1)
    private String edicleanstatus;

    @Column(name = "version")
    private Short version;

    @Column(name = "empty_pickup", length = 30)
    private String emptyPickup;

    @Column(name = "empty_retrun", length = 30)
    private String emptyRetrun;

    @Column(name = "dgNo", length = 5)
    private String dgNo;

    @Column(name = "Flashpoint")
    private Float flashpoint;

    @Column(name = "Flashunit", length = 2)
    private String flashunit;

    @Column(name = "Rid")
    private Integer rid;

    @Column(name = "Humidity", length = 5)
    private String humidity;

    @Column(name = "Humidity_flag")
    private Boolean humidityFlag;

    @Column(name = "Drains", length = 1)
    private String drains;

    @Column(name = "CO2Value", length = 10)
    private String co2Value;

    @Column(name = "O2Value", length = 10)
    private String o2Value;

    @Column(name = "rate_eqpTypeid", length = 4)
    private String rateEqpTypeid;

    @Column(name = "VentPercentage", length = 5)
    private String ventPercentage;

    @Column(name = "amendmentNo", length = 5)
    private String amendmentNo;

    @Column(name = "cargograde", length = 50)
    private String cargograde;

    @Column(name = "expfree", length = 20)
    private String expfree;

    @Column(name = "impfree", length = 20)
    private String impfree;

    @Column(name = "impstatus", length = 1)
    private String impstatus;

    @Column(name = "expstatus", length = 1)
    private String expstatus;

    @Column(name = "expdtnfreedays")
    private Integer expdtnfreedays;

    @Column(name = "expstrfreedays")
    private Integer expstrfreedays;

    @Column(name = "impdtnfreedays")
    private Integer impdtnfreedays;

    @Column(name = "impstrfreedays")
    private Integer impstrfreedays;

    @Column(name = "mrgtier", length = 10)
    private String mrgtier;

    // ===== Constructors =====
    public EdiTempBookingCargo() {
    }

    // ===== Getters and Setters =====
    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public Integer getCargoNo() {
        return cargoNo;
    }

    public void setCargoNo(Integer cargoNo) {
        this.cargoNo = cargoNo;
    }

    public String getEqpStatus() {
        return eqpStatus;
    }

    public void setEqpStatus(String eqpStatus) {
        this.eqpStatus = eqpStatus;
    }

    public String getIsDummy() {
        return isDummy;
    }

    public void setIsDummy(String isDummy) {
        this.isDummy = isDummy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOog() {
        return oog;
    }

    public void setOog(String oog) {
        this.oog = oog;
    }

    public String getEqpTypeId() {
        return eqpTypeId;
    }

    public void setEqpTypeId(String eqpTypeId) {
        this.eqpTypeId = eqpTypeId;
    }

    public String getEqpId() {
        return eqpId;
    }

    public void setEqpId(String eqpId) {
        this.eqpId = eqpId;
    }

    public String getShipperOwn() {
        return shipperOwn;
    }

    public void setShipperOwn(String shipperOwn) {
        this.shipperOwn = shipperOwn;
    }

    public String getRoundTrip() {
        return roundTrip;
    }

    public void setRoundTrip(String roundTrip) {
        this.roundTrip = roundTrip;
    }

    public String getSeal1No() {
        return seal1No;
    }

    public void setSeal1No(String seal1No) {
        this.seal1No = seal1No;
    }

    public String getSeal2No() {
        return seal2No;
    }

    public void setSeal2No(String seal2No) {
        this.seal2No = seal2No;
    }

    public String getOtherSeal() {
        return otherSeal;
    }

    public void setOtherSeal(String otherSeal) {
        this.otherSeal = otherSeal;
    }

    public String getNor() {
        return nor;
    }

    public void setNor(String nor) {
        this.nor = nor;
    }

    public Float getTempMax() {
        return tempMax;
    }

    public void setTempMax(Float tempMax) {
        this.tempMax = tempMax;
    }

    public Float getTempMin() {
        return tempMin;
    }

    public void setTempMin(Float tempMin) {
        this.tempMin = tempMin;
    }

    public Float getSetTemp() {
        return setTemp;
    }

    public void setSetTemp(Float setTemp) {
        this.setTemp = setTemp;
    }

    public String getTempUnit() {
        return tempUnit;
    }

    public void setTempUnit(String tempUnit) {
        this.tempUnit = tempUnit;
    }

    public Float getTareWeight() {
        return tareWeight;
    }

    public void setTareWeight(Float tareWeight) {
        this.tareWeight = tareWeight;
    }

    public Float getCargoWeight() {
        return cargoWeight;
    }

    public void setCargoWeight(Float cargoWeight) {
        this.cargoWeight = cargoWeight;
    }

    public Float getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(Float grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public Integer getVentSetting() {
        return ventSetting;
    }

    public void setVentSetting(Integer ventSetting) {
        this.ventSetting = ventSetting;
    }

    public String getContainerPosition() {
        return containerPosition;
    }

    public void setContainerPosition(String containerPosition) {
        this.containerPosition = containerPosition;
    }

    public String getLoadingCondition() {
        return loadingCondition;
    }

    public void setLoadingCondition(String loadingCondition) {
        this.loadingCondition = loadingCondition;
    }

    public String getChassisId() {
        return chassisId;
    }

    public void setChassisId(String chassisId) {
        this.chassisId = chassisId;
    }

    public String getGensetId() {
        return gensetId;
    }

    public void setGensetId(String gensetId) {
        this.gensetId = gensetId;
    }

    public Float getEqpVolume() {
        return eqpVolume;
    }

    public void setEqpVolume(Float eqpVolume) {
        this.eqpVolume = eqpVolume;
    }

    public String getEqpVolumeUnit() {
        return eqpVolumeUnit;
    }

    public void setEqpVolumeUnit(String eqpVolumeUnit) {
        this.eqpVolumeUnit = eqpVolumeUnit;
    }

    public String getCarriercode() {
        return carriercode;
    }

    public void setCarriercode(String carriercode) {
        this.carriercode = carriercode;
    }

    public String getActStatus() {
        return actStatus;
    }

    public void setActStatus(String actStatus) {
        this.actStatus = actStatus;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getRaterefno() {
        return raterefno;
    }

    public void setRaterefno(String raterefno) {
        this.raterefno = raterefno;
    }

    public String getRatetype() {
        return ratetype;
    }

    public void setRatetype(String ratetype) {
        this.ratetype = ratetype;
    }

    public String getDgflag() {
        return dgflag;
    }

    public void setDgflag(String dgflag) {
        this.dgflag = dgflag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getIsedited() {
        return isedited;
    }

    public void setIsedited(String isedited) {
        this.isedited = isedited;
    }

    public Integer getSlotloss() {
        return slotloss;
    }

    public void setSlotloss(Integer slotloss) {
        this.slotloss = slotloss;
    }

    public String getRaterule() {
        return raterule;
    }

    public void setRaterule(String raterule) {
        this.raterule = raterule;
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

    public String getEmptyPickup() {
        return emptyPickup;
    }

    public void setEmptyPickup(String emptyPickup) {
        this.emptyPickup = emptyPickup;
    }

    public String getEmptyRetrun() {
        return emptyRetrun;
    }

    public void setEmptyRetrun(String emptyRetrun) {
        this.emptyRetrun = emptyRetrun;
    }

    public String getDgNo() {
        return dgNo;
    }

    public void setDgNo(String dgNo) {
        this.dgNo = dgNo;
    }

    public Float getFlashpoint() {
        return flashpoint;
    }

    public void setFlashpoint(Float flashpoint) {
        this.flashpoint = flashpoint;
    }

    public String getFlashunit() {
        return flashunit;
    }

    public void setFlashunit(String flashunit) {
        this.flashunit = flashunit;
    }

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public Boolean getHumidityFlag() {
        return humidityFlag;
    }

    public void setHumidityFlag(Boolean humidityFlag) {
        this.humidityFlag = humidityFlag;
    }

    public String getDrains() {
        return drains;
    }

    public void setDrains(String drains) {
        this.drains = drains;
    }

    public String getCo2Value() {
        return co2Value;
    }

    public void setCo2Value(String co2Value) {
        this.co2Value = co2Value;
    }

    public String getO2Value() {
        return o2Value;
    }

    public void setO2Value(String o2Value) {
        this.o2Value = o2Value;
    }

    public String getRateEqpTypeid() {
        return rateEqpTypeid;
    }

    public void setRateEqpTypeid(String rateEqpTypeid) {
        this.rateEqpTypeid = rateEqpTypeid;
    }

    public String getVentPercentage() {
        return ventPercentage;
    }

    public void setVentPercentage(String ventPercentage) {
        this.ventPercentage = ventPercentage;
    }

    public String getAmendmentNo() {
        return amendmentNo;
    }

    public void setAmendmentNo(String amendmentNo) {
        this.amendmentNo = amendmentNo;
    }

    public String getCargograde() {
        return cargograde;
    }

    public void setCargograde(String cargograde) {
        this.cargograde = cargograde;
    }

    public String getExpfree() {
        return expfree;
    }

    public void setExpfree(String expfree) {
        this.expfree = expfree;
    }

    public String getImpfree() {
        return impfree;
    }

    public void setImpfree(String impfree) {
        this.impfree = impfree;
    }

    public String getImpstatus() {
        return impstatus;
    }

    public void setImpstatus(String impstatus) {
        this.impstatus = impstatus;
    }

    public String getExpstatus() {
        return expstatus;
    }

    public void setExpstatus(String expstatus) {
        this.expstatus = expstatus;
    }

    public Integer getExpdtnfreedays() {
        return expdtnfreedays;
    }

    public void setExpdtnfreedays(Integer expdtnfreedays) {
        this.expdtnfreedays = expdtnfreedays;
    }

    public Integer getExpstrfreedays() {
        return expstrfreedays;
    }

    public void setExpstrfreedays(Integer expstrfreedays) {
        this.expstrfreedays = expstrfreedays;
    }

    public Integer getImpdtnfreedays() {
        return impdtnfreedays;
    }

    public void setImpdtnfreedays(Integer impdtnfreedays) {
        this.impdtnfreedays = impdtnfreedays;
    }

    public Integer getImpstrfreedays() {
        return impstrfreedays;
    }

    public void setImpstrfreedays(Integer impstrfreedays) {
        this.impstrfreedays = impstrfreedays;
    }

    public String getMrgtier() {
        return mrgtier;
    }

    public void setMrgtier(String mrgtier) {
        this.mrgtier = mrgtier;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBookingCargo that = (EdiTempBookingCargo) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "EdiTempBookingCargo{" +
                "cid=" + cid +
                ", bookNo='" + bookNo + '\'' +
                ", cargoNo=" + cargoNo +
                ", eqpId='" + eqpId + '\'' +
                '}';
    }
}

