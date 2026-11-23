package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_body", schema = "dbo")
public class EdiTempBookingBody implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Long cid;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "line_No")
    private Short lineNo;

    @Column(name = "marksNo", columnDefinition = "varchar(max)")
    private String marksNo;

    @Column(name = "description", columnDefinition = "varchar(max)")
    private String description;

    @Column(name = "grossNweight", columnDefinition = "varchar(max)")
    private String grossNweight;

    @Column(name = "measurement", columnDefinition = "varchar(max)")
    private String measurement;

    @Column(name = "carriercode", length = 3)
    private String carriercode;

    @Column(name = "containerno", columnDefinition = "varchar(max)")
    private String containerno;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "edicleanstatus", length = 1)
    private String edicleanstatus;

    @Column(name = "version")
    private Short version;

    // ===== Constructors =====
    public EdiTempBookingBody() {
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

    public Short getLineNo() {
        return lineNo;
    }

    public void setLineNo(Short lineNo) {
        this.lineNo = lineNo;
    }

    public String getMarksNo() {
        return marksNo;
    }

    public void setMarksNo(String marksNo) {
        this.marksNo = marksNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGrossNweight() {
        return grossNweight;
    }

    public void setGrossNweight(String grossNweight) {
        this.grossNweight = grossNweight;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getCarriercode() {
        return carriercode;
    }

    public void setCarriercode(String carriercode) {
        this.carriercode = carriercode;
    }

    public String getContainerno() {
        return containerno;
    }

    public void setContainerno(String containerno) {
        this.containerno = containerno;
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
        EdiTempBookingBody that = (EdiTempBookingBody) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "EdiTempBookingBody{" +
                "cid=" + cid +
                ", bookNo='" + bookNo + '\'' +
                ", lineNo=" + lineNo +
                '}';
    }
}

