package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_agency", schema = "dbo")
public class EdiTempBookingAgency implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Long cid;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "version")
    private Short version;

    @Column(name = "ts1agency", length = 6)
    private String ts1agency;

    @Column(name = "ts2agency", length = 6)
    private String ts2agency;

    @Column(name = "ts3agency", length = 6)
    private String ts3agency;

    @Column(name = "ts4agency", length = 6)
    private String ts4agency;

    @Column(name = "ts5agency", length = 6)
    private String ts5agency;

    @Column(name = "plaagency", length = 6)
    private String plaagency;

    @Column(name = "polagency", length = 6)
    private String polagency;

    @Column(name = "pldagency", length = 6)
    private String pldagency;

    @Column(name = "podagency", length = 6)
    private String podagency;

    // ===== Constructors =====
    public EdiTempBookingAgency() {
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

    public String getTs1agency() {
        return ts1agency;
    }

    public void setTs1agency(String ts1agency) {
        this.ts1agency = ts1agency;
    }

    public String getTs2agency() {
        return ts2agency;
    }

    public void setTs2agency(String ts2agency) {
        this.ts2agency = ts2agency;
    }

    public String getTs3agency() {
        return ts3agency;
    }

    public void setTs3agency(String ts3agency) {
        this.ts3agency = ts3agency;
    }

    public String getTs4agency() {
        return ts4agency;
    }

    public void setTs4agency(String ts4agency) {
        this.ts4agency = ts4agency;
    }

    public String getTs5agency() {
        return ts5agency;
    }

    public void setTs5agency(String ts5agency) {
        this.ts5agency = ts5agency;
    }

    public String getPlaagency() {
        return plaagency;
    }

    public void setPlaagency(String plaagency) {
        this.plaagency = plaagency;
    }

    public String getPolagency() {
        return polagency;
    }

    public void setPolagency(String polagency) {
        this.polagency = polagency;
    }

    public String getPldagency() {
        return pldagency;
    }

    public void setPldagency(String pldagency) {
        this.pldagency = pldagency;
    }

    public String getPodagency() {
        return podagency;
    }

    public void setPodagency(String podagency) {
        this.podagency = podagency;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBookingAgency that = (EdiTempBookingAgency) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "EdiTempBookingAgency{" +
                "cid=" + cid +
                ", bookNo='" + bookNo + '\'' +
                ", edireaderid='" + edireaderid + '\'' +
                '}';
    }
}

