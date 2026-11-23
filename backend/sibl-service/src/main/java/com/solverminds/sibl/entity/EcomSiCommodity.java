package com.solverminds.sibl.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ecomSI_commodity", schema = "dbo")
public class EcomSiCommodity implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // ===== Foreign Keys =====
    @Column(name = "eSINo", length = 20, nullable = false)
    private String eSINo;

    @Column(name = "conId", nullable = false)
    private Integer conId;

    @Column(name = "containerno", length = 11, nullable = false)
    private String containerno;

    // ===== Core Fields =====
    @Column(name = "itemno", nullable = false)
    private Integer itemno;

    @Column(name = "commodity", length = 10, nullable = false)
    private String commodity;

    @Column(name = "package", length = 4, nullable = false)
    private String packageType;

    @Column(name = "packageQty", nullable = false)
    private Integer packageQty;

    @Column(name = "HSCommodity", length = 150, nullable = false)
    private String hsCommodity;

    @Column(name = "hazardous", length = 1)
    private String hazardous;

    // ===== Additional Fields =====
    @Column(name = "itemWeight")
    private Double itemWeight;

    @Column(name = "itemWeightUnit", length = 1)
    private String itemWeightUnit;

    @Column(name = "nettwt")
    private Double nettwt;

    @Column(name = "itemVolume")
    private Double itemVolume;

    @Column(name = "itemVolumeUnit", length = 1)
    private String itemVolumeUnit;

    @Column(name = "marksNo", columnDefinition = "varchar(max)")
    private String marksNo;

    @Column(name = "marksDesc", columnDefinition = "varchar(max)")
    private String marksDesc;

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
    public EcomSiCommodity() {
    }

    public EcomSiCommodity(String eSINo, Integer conId, String containerno, String commodity, String packageType, Integer packageQty, String hsCommodity) {
        this.eSINo = eSINo;
        this.conId = conId;
        this.containerno = containerno;
        this.commodity = commodity;
        this.packageType = packageType;
        this.packageQty = packageQty;
        this.hsCommodity = hsCommodity;
    }

    // ===== Getters/Setters =====
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String geteSINo() {
        return eSINo;
    }

    public void seteSINo(String eSINo) {
        this.eSINo = eSINo;
    }

    public Integer getConId() {
        return conId;
    }

    public void setConId(Integer conId) {
        this.conId = conId;
    }

    public String getContainerno() {
        return containerno;
    }

    public void setContainerno(String containerno) {
        this.containerno = containerno;
    }

    public Integer getItemno() {
        return itemno;
    }

    public void setItemno(Integer itemno) {
        this.itemno = itemno;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
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

    public String getHsCommodity() {
        return hsCommodity;
    }

    public void setHsCommodity(String hsCommodity) {
        this.hsCommodity = hsCommodity;
    }

    public String getHazardous() {
        return hazardous;
    }

    public void setHazardous(String hazardous) {
        this.hazardous = hazardous;
    }

    public Double getItemWeight() {
        return itemWeight;
    }

    public void setItemWeight(Double itemWeight) {
        this.itemWeight = itemWeight;
    }

    public String getItemWeightUnit() {
        return itemWeightUnit;
    }

    public void setItemWeightUnit(String itemWeightUnit) {
        this.itemWeightUnit = itemWeightUnit;
    }

    public Double getNettwt() {
        return nettwt;
    }

    public void setNettwt(Double nettwt) {
        this.nettwt = nettwt;
    }

    public Double getItemVolume() {
        return itemVolume;
    }

    public void setItemVolume(Double itemVolume) {
        this.itemVolume = itemVolume;
    }

    public String getItemVolumeUnit() {
        return itemVolumeUnit;
    }

    public void setItemVolumeUnit(String itemVolumeUnit) {
        this.itemVolumeUnit = itemVolumeUnit;
    }

    public String getMarksNo() {
        return marksNo;
    }

    public void setMarksNo(String marksNo) {
        this.marksNo = marksNo;
    }

    public String getMarksDesc() {
        return marksDesc;
    }

    public void setMarksDesc(String marksDesc) {
        this.marksDesc = marksDesc;
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

    // ===== equals/hashCode by ID =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomSiCommodity)) return false;
        EcomSiCommodity that = (EcomSiCommodity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EcomSiCommodity{" +
                "id=" + id +
                ", eSINo='" + eSINo + '\'' +
                ", conId=" + conId +
                ", containerno='" + containerno + '\'' +
                ", itemno=" + itemno +
                ", commodity=" + commodity +
                ", packageType='" + packageType + '\'' +
                ", packageQty=" + packageQty +
                ", hsCommodity='" + hsCommodity + '\'' +
                '}';
    }
}
