package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_charges", schema = "dbo")
public class EdiTempBookingCharges implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "chargeid", length = 3)
    private String chargeid;

    @Column(name = "payagencyid", length = 5)
    private String payagencyid;

    @Column(name = "custtypeid", length = 2)
    private String custtypeid;

    @Column(name = "prepaidcollect", length = 5)
    private String prepaidcollect;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "version")
    private Short version;

    @Column(name = "eqpTypeId", length = 4)
    private String eqpTypeId;

    // ===== Constructors =====
    public EdiTempBookingCharges() {
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

    public String getChargeid() {
        return chargeid;
    }

    public void setChargeid(String chargeid) {
        this.chargeid = chargeid;
    }

    public String getPayagencyid() {
        return payagencyid;
    }

    public void setPayagencyid(String payagencyid) {
        this.payagencyid = payagencyid;
    }

    public String getCustomtypeid() {
        return custtypeid;
    }

    public void setCustomtypeid(String custtypeid) {
        this.custtypeid = custtypeid;
    }

    public String getPrepaidcollect() {
        return prepaidcollect;
    }

    public void setPrepaidcollect(String prepaidcollect) {
        this.prepaidcollect = prepaidcollect;
    }

    public String getEdireaderid() {
        return edireaderid;
    }

    public void setEdireaderid(String edireaderid) {
        this.edireaderid = edireaderid;
    }

    public Short getVersion() {
        return version;
    }

    public void setVersion(Short version) {
        this.version = version;
    }

    public String getEqpTypeId() {
        return eqpTypeId;
    }

    public void setEqpTypeId(String eqpTypeId) {
        this.eqpTypeId = eqpTypeId;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBookingCharges that = (EdiTempBookingCharges) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EdiTempBookingCharges{" +
                "id=" + id +
                ", bookNo='" + bookNo + '\'' +
                ", chargeid='" + chargeid + '\'' +
                '}';
    }
}

