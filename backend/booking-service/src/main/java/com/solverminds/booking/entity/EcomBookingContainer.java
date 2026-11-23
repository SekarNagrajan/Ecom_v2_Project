package com.solverminds.booking.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * Container entity for booking cargo
 * Table: ecombooking_cargo
 * Composite PK: ebookno, conid
 */
@Entity
@Table(name = "ecombooking_cargo", schema = "dbo")
public class EcomBookingContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "ebookno", length = 20, nullable = false)
    private String ebookno;

    @Column(name = "conid", nullable = false)
    private Integer conid;

    @Column(name = "containerno", length = 11)
    private String containerno;

    @Column(name = "eqpType", length = 1)
    private String eqpType;

    @Column(name = "eqpSize", length = 10)
    private String eqpSize;

    @Column(name = "soc", length = 1)
    private String soc;

    @Column(name = "ref_nor", length = 1)
    private String refNor;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "dgFlag", length = 1)
    private String dgFlag;

    @Column(name = "tareWeight")
    private Float tareWeight;

    @Column(name = "cargoWeight")
    private Float cargoWeight;

    @Column(name = "grossWeight")
    private Float grossWeight;

    @Column(name = "weightUnit", length = 1)
    private String weightUnit;

    @Column(name = "eqpstatus", length = 1)
    private String eqpstatus;

    @Column(name = "oog", length = 1)
    private String oog;

    public EcomBookingContainer() {
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

    public String getContainerno() {
        return containerno;
    }

    public void setContainerno(String containerno) {
        this.containerno = containerno;
    }

    public String getEqpType() {
        return eqpType;
    }

    public void setEqpType(String eqpType) {
        this.eqpType = eqpType;
    }

    public String getEqpSize() {
        return eqpSize;
    }

    public void setEqpSize(String eqpSize) {
        this.eqpSize = eqpSize;
    }

    public String getSoc() {
        return soc;
    }

    public void setSoc(String soc) {
        this.soc = soc;
    }

    public String getRefNor() {
        return refNor;
    }

    public void setRefNor(String refNor) {
        this.refNor = refNor;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDgFlag() {
        return dgFlag;
    }

    public void setDgFlag(String dgFlag) {
        this.dgFlag = dgFlag;
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

    public String getEqpstatus() {
        return eqpstatus;
    }

    public void setEqpstatus(String eqpstatus) {
        this.eqpstatus = eqpstatus;
    }

    public String getOog() {
        return oog;
    }

    public void setOog(String oog) {
        this.oog = oog;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomBookingContainer)) return false;
        EcomBookingContainer that = (EcomBookingContainer) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EcomBookingContainer{" +
                "id=" + id +
                ", containerno='" + containerno + '\'' +
                ", eqpType='" + eqpType + '\'' +
                ", eqpSize='" + eqpSize + '\'' +
                '}';
    }
}
