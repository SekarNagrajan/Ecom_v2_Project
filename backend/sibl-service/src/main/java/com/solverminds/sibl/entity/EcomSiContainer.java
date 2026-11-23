package com.solverminds.sibl.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ecomSI_cargo", schema = "dbo")
public class EcomSiContainer implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // ===== Foreign Key =====
    @Column(name = "eSINo", length = 20, nullable = false)
    private String eSINo;

    // ===== Core Fields =====
    @Column(name = "conId", nullable = false)
    private Integer conId;

    @Column(name = "eqpSize", length = 10, nullable = false)
    private String eqpSize;

    @Column(name = "containerno", length = 50)
    private String containerno;

    @Column(name = "cargoWeight", nullable = false)
    private Integer cargoWeight;

    @Column(name = "weightUnit", length = 1, nullable = false)
    private String weightUnit;

    @Column(name = "eqpstatus", length = 1)
    private String equipmentStatus;

    @Column(name = "dgflag", length = 1)
    private String dgFlag;

    // ===== Additional Fields =====
    @Column(name = "tareWeight")
    private Integer tareWeight;

    @Column(name = "grossweight")
    private Integer grossweight;

    @Column(name = "eqpType", length = 1)
    private String eqpType;

    @Column(name = "oog", length = 1)
    private String oog;

    @Column(name = "rate_eqpTypeid", length = 10)
    private String rateEquipmentSize;

    @Column(name = "shipperOwn", length = 1)
    private String shipperOwn;

    @Column(name = "carrierSeal", length = 50)
    private String carrierSeal;

    @Column(name = "ShipperSeal", length = 50)
    private String shipperSeal;

    @Column(name = "ref_nor", length = 1)
    private String nor;

    // ===== Constructors =====
    public EcomSiContainer() {
    }

    public EcomSiContainer(String eSINo, Integer conId, String eqpSize, Integer cargoWeight, String weightUnit) {
        this.eSINo = eSINo;
        this.conId = conId;
        this.eqpSize = eqpSize;
        this.cargoWeight = cargoWeight;
        this.weightUnit = weightUnit;
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

    public String getEqpSize() {
        return eqpSize;
    }

    public void setEqpSize(String eqpSize) {
        this.eqpSize = eqpSize;
    }

    public String getContainerno() {
        return containerno;
    }

    public void setContainerno(String containerno) {
        this.containerno = containerno;
    }

    public Integer getCargoWeight() {
        return cargoWeight;
    }

    public void setCargoWeight(Integer cargoWeight) {
        this.cargoWeight = cargoWeight;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(String equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    public String getDgFlag() {
        return dgFlag;
    }

    public void setDgFlag(String dgFlag) {
        this.dgFlag = dgFlag;
    }

    public Integer getTareWeight() {
        return tareWeight;
    }

    public void setTareWeight(Integer tareWeight) {
        this.tareWeight = tareWeight;
    }

    public Integer getGrossweight() {
        return grossweight;
    }

    public void setGrossweight(Integer grossweight) {
        this.grossweight = grossweight;
    }

    public String getEqpType() {
        return eqpType;
    }

    public void setEqpType(String eqpType) {
        this.eqpType = eqpType;
    }

    public String getOog() {
        return oog;
    }

    public void setOog(String oog) {
        this.oog = oog;
    }

    public String getRateEquipmentSize() {
        return rateEquipmentSize;
    }

    public void setRateEquipmentSize(String rateEquipmentSize) {
        this.rateEquipmentSize = rateEquipmentSize;
    }

    public String getShipperOwn() {
        return shipperOwn;
    }

    public void setShipperOwn(String shipperOwn) {
        this.shipperOwn = shipperOwn;
    }

    public String getCarrierSeal() {
        return carrierSeal;
    }

    public void setCarrierSeal(String carrierSeal) {
        this.carrierSeal = carrierSeal;
    }

    public String getShipperSeal() {
        return shipperSeal;
    }

    public void setShipperSeal(String shipperSeal) {
        this.shipperSeal = shipperSeal;
    }

    public String getNor() {
        return nor;
    }

    public void setNor(String nor) {
        this.nor = nor;
    }

    // ===== equals/hashCode by ID =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomSiContainer)) return false;
        EcomSiContainer that = (EcomSiContainer) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EcomSiContainer{" +
                "id=" + id +
                ", eSINo='" + eSINo + '\'' +
                ", conId=" + conId +
                ", eqpSize='" + eqpSize + '\'' +
                ", containerno='" + containerno + '\'' +
                ", cargoWeight=" + cargoWeight +
                ", weightUnit='" + weightUnit + '\'' +
                '}';
    }
}
