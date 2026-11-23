package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_commodity", schema = "dbo")
public class EdiTempBookingCommodity implements Serializable {

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

    @Column(name = "eqpId", length = 50)
    private String eqpId;

    @Column(name = "itemNo")
    private Integer itemNo;

    @Column(name = "comdtygrp", length = 10)
    private String comdtygrp;

    @Column(name = "commodityId", length = 10)
    private String commodityId;

    @Column(name = "packageId", length = 4)
    private String packageId;

    @Column(name = "marksNumbers", columnDefinition = "varchar(max)")
    private String marksNumbers;

    @Column(name = "marksDesc", columnDefinition = "varchar(max)")
    private String marksDesc;

    @Column(name = "itemQty")
    private Integer itemQty;

    @Column(name = "itemDesc", length = 256)
    private String itemDesc;

    @Column(name = "itemWeight")
    private Float itemWeight;

    @Column(name = "itemWeightUnit", length = 1)
    private String itemWeightUnit;

    @Column(name = "itemVolume")
    private Float itemVolume;

    @Column(name = "itemVolumeUnit", length = 5)
    private String itemVolumeUnit;

    @Column(name = "itemLeft")
    private Float itemLeft;

    @Column(name = "itemForward")
    private Float itemForward;

    @Column(name = "itemTop")
    private Float itemTop;

    @Column(name = "itemRight")
    private Float itemRight;

    @Column(name = "itemAft")
    private Float itemAft;

    @Column(name = "itemLengthUnit", length = 1)
    private String itemLengthUnit;

    @Column(name = "hazardous", length = 1)
    private String hazardous;

    @Column(name = "imoContact", length = 50)
    private String imoContact;

    @Column(name = "imoPhone", length = 50)
    private String imoPhone;

    @Column(name = "carriercode", length = 3)
    private String carriercode;

    @Column(name = "Alcohol", length = 1)
    private String alcohol;

    @Column(name = "militarycargo", length = 1)
    private String militarycargo;

    @Column(name = "prNo", length = 50)
    private String prNo;

    @Column(name = "finaldestination", length = 5)
    private String finaldestination;

    @Column(name = "nettwt")
    private Float nettwt;

    @Column(name = "netwtunit", length = 1)
    private String netwtunit;

    @Column(name = "isedited", length = 1)
    private String isedited;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "edicleanstatus", length = 1)
    private String edicleanstatus;

    @Column(name = "version")
    private Short version;

    @Column(name = "Rid")
    private Integer rid;

    @Column(name = "package_desc", length = 500)
    private String packageDesc;

    @Column(name = "hsSuffixcode", length = 4)
    private String hsSuffixcode;

    @Column(name = "bycode", length = 15)
    private String bycode;

    @Column(name = "slcode", length = 15)
    private String slcode;

    @Column(name = "byname", length = 150)
    private String byname;

    @Column(name = "slname", length = 150)
    private String slname;

    @Column(name = "cmdty_hblno", length = 20)
    private String cmdtyHblno;

    // ===== Constructors =====
    public EdiTempBookingCommodity() {
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

    public String getEqpId() {
        return eqpId;
    }

    public void setEqpId(String eqpId) {
        this.eqpId = eqpId;
    }

    public Integer getItemNo() {
        return itemNo;
    }

    public void setItemNo(Integer itemNo) {
        this.itemNo = itemNo;
    }

    public String getComdtygrp() {
        return comdtygrp;
    }

    public void setComdtygrp(String comdtygrp) {
        this.comdtygrp = comdtygrp;
    }

    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getMarksNumbers() {
        return marksNumbers;
    }

    public void setMarksNumbers(String marksNumbers) {
        this.marksNumbers = marksNumbers;
    }

    public String getMarksDesc() {
        return marksDesc;
    }

    public void setMarksDesc(String marksDesc) {
        this.marksDesc = marksDesc;
    }

    public Integer getItemQty() {
        return itemQty;
    }

    public void setItemQty(Integer itemQty) {
        this.itemQty = itemQty;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public Float getItemWeight() {
        return itemWeight;
    }

    public void setItemWeight(Float itemWeight) {
        this.itemWeight = itemWeight;
    }

    public String getItemWeightUnit() {
        return itemWeightUnit;
    }

    public void setItemWeightUnit(String itemWeightUnit) {
        this.itemWeightUnit = itemWeightUnit;
    }

    public Float getItemVolume() {
        return itemVolume;
    }

    public void setItemVolume(Float itemVolume) {
        this.itemVolume = itemVolume;
    }

    public String getItemVolumeUnit() {
        return itemVolumeUnit;
    }

    public void setItemVolumeUnit(String itemVolumeUnit) {
        this.itemVolumeUnit = itemVolumeUnit;
    }

    public Float getItemLeft() {
        return itemLeft;
    }

    public void setItemLeft(Float itemLeft) {
        this.itemLeft = itemLeft;
    }

    public Float getItemForward() {
        return itemForward;
    }

    public void setItemForward(Float itemForward) {
        this.itemForward = itemForward;
    }

    public Float getItemTop() {
        return itemTop;
    }

    public void setItemTop(Float itemTop) {
        this.itemTop = itemTop;
    }

    public Float getItemRight() {
        return itemRight;
    }

    public void setItemRight(Float itemRight) {
        this.itemRight = itemRight;
    }

    public Float getItemAft() {
        return itemAft;
    }

    public void setItemAft(Float itemAft) {
        this.itemAft = itemAft;
    }

    public String getItemLengthUnit() {
        return itemLengthUnit;
    }

    public void setItemLengthUnit(String itemLengthUnit) {
        this.itemLengthUnit = itemLengthUnit;
    }

    public String getHazardous() {
        return hazardous;
    }

    public void setHazardous(String hazardous) {
        this.hazardous = hazardous;
    }

    public String getImoContact() {
        return imoContact;
    }

    public void setImoContact(String imoContact) {
        this.imoContact = imoContact;
    }

    public String getImoPhone() {
        return imoPhone;
    }

    public void setImoPhone(String imoPhone) {
        this.imoPhone = imoPhone;
    }

    public String getCarriercode() {
        return carriercode;
    }

    public void setCarriercode(String carriercode) {
        this.carriercode = carriercode;
    }

    public String getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(String alcohol) {
        this.alcohol = alcohol;
    }

    public String getMilitarycargo() {
        return militarycargo;
    }

    public void setMilitarycargo(String militarycargo) {
        this.militarycargo = militarycargo;
    }

    public String getPrNo() {
        return prNo;
    }

    public void setPrNo(String prNo) {
        this.prNo = prNo;
    }

    public String getFinaldestination() {
        return finaldestination;
    }

    public void setFinaldestination(String finaldestination) {
        this.finaldestination = finaldestination;
    }

    public Float getNettwt() {
        return nettwt;
    }

    public void setNettwt(Float nettwt) {
        this.nettwt = nettwt;
    }

    public String getNetwtunit() {
        return netwtunit;
    }

    public void setNetwtunit(String netwtunit) {
        this.netwtunit = netwtunit;
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

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public String getPackageDesc() {
        return packageDesc;
    }

    public void setPackageDesc(String packageDesc) {
        this.packageDesc = packageDesc;
    }

    public String getHsSuffixcode() {
        return hsSuffixcode;
    }

    public void setHsSuffixcode(String hsSuffixcode) {
        this.hsSuffixcode = hsSuffixcode;
    }

    public String getBycode() {
        return bycode;
    }

    public void setBycode(String bycode) {
        this.bycode = bycode;
    }

    public String getSlcode() {
        return slcode;
    }

    public void setSlcode(String slcode) {
        this.slcode = slcode;
    }

    public String getByname() {
        return byname;
    }

    public void setByname(String byname) {
        this.byname = byname;
    }

    public String getSlname() {
        return slname;
    }

    public void setSlname(String slname) {
        this.slname = slname;
    }

    public String getCmdtyHblno() {
        return cmdtyHblno;
    }

    public void setCmdtyHblno(String cmdtyHblno) {
        this.cmdtyHblno = cmdtyHblno;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBookingCommodity that = (EdiTempBookingCommodity) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "EdiTempBookingCommodity{" +
                "cid=" + cid +
                ", bookNo='" + bookNo + '\'' +
                ", cargoNo=" + cargoNo +
                ", itemNo=" + itemNo +
                '}';
    }
}

