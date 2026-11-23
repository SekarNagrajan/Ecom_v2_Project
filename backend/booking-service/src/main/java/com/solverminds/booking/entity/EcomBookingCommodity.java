package com.solverminds.booking.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * Commodity entity for booking commodities.
 * Table: ecombooking_commodity
 * Composite PK: ebookno, conid, itemno
 * Per-line net weight (kg) is stored in {@code nettwt}; commodity-level weight is in {@code itemWeight} / {@code itemWeightUnit}.
 */
@Entity
@Table(name = "ecombooking_commodity", schema = "dbo")
public class EcomBookingCommodity {

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

    @Column(name = "commodityFlag", length = 1)
    private String commodityFlag;

    @Column(name = "commodity", length = 150)
    private String commodity;

    @Column(name = "HSCommodity", length = 150)
    private String hsCommodity;

    @Column(name = "package", length = 4)
    private String packageType;

    @Column(name = "packageQty")
    private Integer packageQty;

    @Column(name = "cargoVolume")
    private Float cargoVolume;

    @Column(name = "volumnUnit", length = 1)
    private String volumnUnit;

    @Column(name = "marksNo")
    private String marksNo; // varchar(MAX)

    @Column(name = "descr")
    private String descr; // varchar(MAX)

    @Column(name = "nettwt")
    private Double nettwt;

    @Column(name = "itemWeight")
    private Float itemWeight;

    @Column(name = "itemWeightUnit", length = 1)
    private String itemWeightUnit;

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

    public EcomBookingCommodity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEbookno() {
        return ebookno;
    }

    public void setEbookno(String ebookno) {
        this.ebookno = ebookno;
    }

    public Integer getConid() {
        return conid;
    }

    public void setConid(Integer conid) {
        this.conid = conid;
    }

    public Integer getItemno() {
        return itemno;
    }

    public void setItemno(Integer itemno) {
        this.itemno = itemno;
    }

    public String getContainerno() {
        return containerno;
    }

    public void setContainerno(String containerno) {
        this.containerno = containerno;
    }

    public String getCommodityFlag() {
        return commodityFlag;
    }

    public void setCommodityFlag(String commodityFlag) {
        this.commodityFlag = commodityFlag;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getHsCommodity() {
        return hsCommodity;
    }

    public void setHsCommodity(String hsCommodity) {
        this.hsCommodity = hsCommodity;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public Integer getPackageQty() {
        return packageQty;
    }

    public void setPackageQty(Integer packageQty) {
        this.packageQty = packageQty;
    }

    public Float getCargoVolume() {
        return cargoVolume;
    }

    public void setCargoVolume(Float cargoVolume) {
        this.cargoVolume = cargoVolume;
    }

    public String getVolumnUnit() {
        return volumnUnit;
    }

    public void setVolumnUnit(String volumnUnit) {
        this.volumnUnit = volumnUnit;
    }

    public String getMarksNo() {
        return marksNo;
    }

    public void setMarksNo(String marksNo) {
        this.marksNo = marksNo;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public Double getNettwt() {
        return nettwt;
    }

    public void setNettwt(Double nettwt) {
        this.nettwt = nettwt;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomBookingCommodity)) return false;
        EcomBookingCommodity that = (EcomBookingCommodity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EcomBookingCommodity{" +
                "id=" + id +
                ", commodity='" + commodity + '\'' +
                ", packageQty=" + packageQty +
                '}';
    }
}
