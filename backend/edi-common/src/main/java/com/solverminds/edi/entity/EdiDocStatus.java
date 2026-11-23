package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "edidocstatus", schema = "dbo")
public class EdiDocStatus implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // ===== Columns =====
    @Column(name = "edireaderid", length = 15)
    private String edireaderid;

    @Column(name = "servprovname", length = 50)
    private String servprovname;

    @Column(name = "receivdate")
    private LocalDateTime receivdate;

    @Column(name = "processdate")
    private LocalDateTime processdate;

    @Column(name = "message", length = 10)
    private String message;

    @Column(name = "transtype", length = 10)
    private String transtype;

    @Column(name = "status", length = 15)
    private String status;

    @Column(name = "transrefno", length = 10)
    private String transrefno;

    @Column(name = "errcode", length = 50)
    private String errcode;

    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "ediparty", length = 20)
    private String ediparty;

    @Column(name = "filename", length = 500)
    private String filename;

    @Column(name = "amendment_version")
    private Integer amendmentVersion;

    // ===== Constructors =====
    public EdiDocStatus() {
    }

    // ===== Getters and Setters =====
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEdireaderid() {
        return edireaderid;
    }

    public void setEdireaderid(String edireaderid) {
        this.edireaderid = edireaderid;
    }

    public String getServprovname() {
        return servprovname;
    }

    public void setServprovname(String servprovname) {
        this.servprovname = servprovname;
    }

    public LocalDateTime getReceivdate() {
        return receivdate;
    }

    public void setReceivdate(LocalDateTime receivdate) {
        this.receivdate = receivdate;
    }

    public LocalDateTime getProcessdate() {
        return processdate;
    }

    public void setProcessdate(LocalDateTime processdate) {
        this.processdate = processdate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTranstype() {
        return transtype;
    }

    public void setTranstype(String transtype) {
        this.transtype = transtype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransrefno() {
        return transrefno;
    }

    public void setTransrefno(String transrefno) {
        this.transrefno = transrefno;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getEdiparty() {
        return ediparty;
    }

    public void setEdiparty(String ediparty) {
        this.ediparty = ediparty;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getAmendmentVersion() {
        return amendmentVersion;
    }

    public void setAmendmentVersion(Integer amendmentVersion) {
        this.amendmentVersion = amendmentVersion;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiDocStatus that = (EdiDocStatus) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EdiDocStatus{" +
                "id=" + id +
                ", edireaderid='" + edireaderid + '\'' +
                ", servprovname='" + servprovname + '\'' +
                ", receivdate=" + receivdate +
                ", processdate=" + processdate +
                ", message='" + message + '\'' +
                ", transtype='" + transtype + '\'' +
                ", status='" + status + '\'' +
                ", transrefno='" + transrefno + '\'' +
                ", errcode='" + errcode + '\'' +
                ", bookNo='" + bookNo + '\'' +
                ", ediparty='" + ediparty + '\'' +
                ", filename='" + filename + '\'' +
                ", amendmentVersion=" + amendmentVersion +
                '}';
    }
}

