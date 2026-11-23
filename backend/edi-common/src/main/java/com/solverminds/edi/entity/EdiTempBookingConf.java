package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_conf", schema = "dbo")
public class EdiTempBookingConf implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Long cid;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "bkgCutoff")
    private LocalDateTime bkgCutoff;

    @Column(name = "docCutoff")
    private LocalDateTime docCutoff;

    @Column(name = "vgmCutoff")
    private LocalDateTime vgmCutoff;

    @Column(name = "bbroroCutoff")
    private LocalDateTime bbroroCutoff;

    @Column(name = "hazCutoff")
    private LocalDateTime hazCutoff;

    @Column(name = "portentryCutoff")
    private LocalDateTime portentryCutoff;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "version")
    private Short version;

    @Column(name = "remarks", length = 500)
    private String remarks;

    // ===== Constructors =====
    public EdiTempBookingConf() {
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

    public LocalDateTime getBkgCutoff() {
        return bkgCutoff;
    }

    public void setBkgCutoff(LocalDateTime bkgCutoff) {
        this.bkgCutoff = bkgCutoff;
    }

    public LocalDateTime getDocCutoff() {
        return docCutoff;
    }

    public void setDocCutoff(LocalDateTime docCutoff) {
        this.docCutoff = docCutoff;
    }

    public LocalDateTime getVgmCutoff() {
        return vgmCutoff;
    }

    public void setVgmCutoff(LocalDateTime vgmCutoff) {
        this.vgmCutoff = vgmCutoff;
    }

    public LocalDateTime getBbroroCutoff() {
        return bbroroCutoff;
    }

    public void setBbroroCutoff(LocalDateTime bbroroCutoff) {
        this.bbroroCutoff = bbroroCutoff;
    }

    public LocalDateTime getHazCutoff() {
        return hazCutoff;
    }

    public void setHazCutoff(LocalDateTime hazCutoff) {
        this.hazCutoff = hazCutoff;
    }

    public LocalDateTime getPortentryCutoff() {
        return portentryCutoff;
    }

    public void setPortentryCutoff(LocalDateTime portentryCutoff) {
        this.portentryCutoff = portentryCutoff;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBookingConf that = (EdiTempBookingConf) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "EdiTempBookingConf{" +
                "cid=" + cid +
                ", bookNo='" + bookNo + '\'' +
                '}';
    }
}

