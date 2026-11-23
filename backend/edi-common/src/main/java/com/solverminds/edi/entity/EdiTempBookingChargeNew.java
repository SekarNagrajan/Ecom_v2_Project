package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_chargenew", schema = "dbo")
public class EdiTempBookingChargeNew implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Long cid;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "chargeNo")
    private Integer chargeNo;

    @Column(name = "chargeId", length = 3)
    private String chargeId;

    @Column(name = "baseRate")
    private Float baseRate;

    @Column(name = "baseRateUnit", length = 2)
    private String baseRateUnit;

    @Column(name = "currencyId", length = 3)
    private String currencyId;

    @Column(name = "payAgencyId", length = 5)
    private String payAgencyId;

    @Column(name = "custTypeId", length = 2)
    private String custTypeId;

    @Column(name = "chargeQty")
    private Integer chargeQty;

    @Column(name = "prepaidCollect", length = 5)
    private String prepaidCollect;

    @Column(name = "chargeAmount")
    private Float chargeAmount;

    @Column(name = "eqpTypeId", length = 4)
    private String eqpTypeId;

    @Column(name = "eqpId", length = 11)
    private String eqpId;

    @Column(name = "autoRated", length = 1)
    private String autoRated;

    @Column(name = "chargetype", length = 1)
    private String chargetype;

    @Column(name = "carriercode", length = 3)
    private String carriercode;

    @Column(name = "isVatos", length = 1)
    private String isVatos;

    @Column(name = "raterefno", length = 30)
    private String raterefno;

    @Column(name = "isedited", length = 1)
    private String isedited;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "edicleanstatus", length = 1)
    private String edicleanstatus;

    @Column(name = "version")
    private Short version;

    // ===== Constructors =====
    public EdiTempBookingChargeNew() {
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

    public Integer getChargeNo() {
        return chargeNo;
    }

    public void setChargeNo(Integer chargeNo) {
        this.chargeNo = chargeNo;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public Float getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(Float baseRate) {
        this.baseRate = baseRate;
    }

    public String getBaseRateUnit() {
        return baseRateUnit;
    }

    public void setBaseRateUnit(String baseRateUnit) {
        this.baseRateUnit = baseRateUnit;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getPayAgencyId() {
        return payAgencyId;
    }

    public void setPayAgencyId(String payAgencyId) {
        this.payAgencyId = payAgencyId;
    }

    public String getCustTypeId() {
        return custTypeId;
    }

    public void setCustTypeId(String custTypeId) {
        this.custTypeId = custTypeId;
    }

    public Integer getChargeQty() {
        return chargeQty;
    }

    public void setChargeQty(Integer chargeQty) {
        this.chargeQty = chargeQty;
    }

    public String getPrepaidCollect() {
        return prepaidCollect;
    }

    public void setPrepaidCollect(String prepaidCollect) {
        this.prepaidCollect = prepaidCollect;
    }

    public Float getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(Float chargeAmount) {
        this.chargeAmount = chargeAmount;
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

    public String getAutoRated() {
        return autoRated;
    }

    public void setAutoRated(String autoRated) {
        this.autoRated = autoRated;
    }

    public String getChargetype() {
        return chargetype;
    }

    public void setChargetype(String chargetype) {
        this.chargetype = chargetype;
    }

    public String getCarriercode() {
        return carriercode;
    }

    public void setCarriercode(String carriercode) {
        this.carriercode = carriercode;
    }

    public String getIsVatos() {
        return isVatos;
    }

    public void setIsVatos(String isVatos) {
        this.isVatos = isVatos;
    }

    public String getRaterefno() {
        return raterefno;
    }

    public void setRaterefno(String raterefno) {
        this.raterefno = raterefno;
    }

    public String getIsedited() {
        return isedited;
    }

    public void setIsedited(String isedited) {
        this.isedited = isedited;
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
        EdiTempBookingChargeNew that = (EdiTempBookingChargeNew) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "EdiTempBookingChargeNew{" +
                "cid=" + cid +
                ", bookNo='" + bookNo + '\'' +
                ", chargeNo=" + chargeNo +
                ", chargeId='" + chargeId + '\'' +
                '}';
    }
}

