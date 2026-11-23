package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_contacts", schema = "dbo")
public class EdiTempBookingContacts implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Integer cid;

    // ===== Columns =====
    @Column(name = "book_no", length = 16)
    private String bookNo;

    @Column(name = "eqpid", length = 11)
    private String eqpid;

    @Column(name = "excontactperson", length = 50)
    private String excontactperson;

    @Column(name = "exphone", length = 25)
    private String exphone;

    @Column(name = "exfax", length = 25)
    private String exfax;

    @Column(name = "exemail", length = 50)
    private String exemail;

    @Column(name = "exportremarks", length = 250)
    private String exportremarks;

    @Column(name = "imcontactperson", length = 50)
    private String imcontactperson;

    @Column(name = "imphone", length = 25)
    private String imphone;

    @Column(name = "imfax", length = 25)
    private String imfax;

    @Column(name = "imemail", length = 50)
    private String imemail;

    @Column(name = "importremarks", length = 250)
    private String importremarks;

    @Column(name = "isedited", length = 1)
    private String isedited;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "edicleanstatus", length = 1)
    private String edicleanstatus;

    @Column(name = "version")
    private Short version;

    // ===== Constructors =====
    public EdiTempBookingContacts() {
    }

    // ===== Getters and Setters =====
    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getEqpid() {
        return eqpid;
    }

    public void setEqpid(String eqpid) {
        this.eqpid = eqpid;
    }

    public String getExcontactperson() {
        return excontactperson;
    }

    public void setExcontactperson(String excontactperson) {
        this.excontactperson = excontactperson;
    }

    public String getExphone() {
        return exphone;
    }

    public void setExphone(String exphone) {
        this.exphone = exphone;
    }

    public String getExfax() {
        return exfax;
    }

    public void setExfax(String exfax) {
        this.exfax = exfax;
    }

    public String getExemail() {
        return exemail;
    }

    public void setExemail(String exemail) {
        this.exemail = exemail;
    }

    public String getExportremarks() {
        return exportremarks;
    }

    public void setExportremarks(String exportremarks) {
        this.exportremarks = exportremarks;
    }

    public String getImcontactperson() {
        return imcontactperson;
    }

    public void setImcontactperson(String imcontactperson) {
        this.imcontactperson = imcontactperson;
    }

    public String getImphone() {
        return imphone;
    }

    public void setImphone(String imphone) {
        this.imphone = imphone;
    }

    public String getImfax() {
        return imfax;
    }

    public void setImfax(String imfax) {
        this.imfax = imfax;
    }

    public String getImemail() {
        return imemail;
    }

    public void setImemail(String imemail) {
        this.imemail = imemail;
    }

    public String getImportremarks() {
        return importremarks;
    }

    public void setImportremarks(String importremarks) {
        this.importremarks = importremarks;
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

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBookingContacts that = (EdiTempBookingContacts) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "EdiTempBookingContacts{" +
                "cid=" + cid +
                ", bookNo='" + bookNo + '\'' +
                ", eqpid='" + eqpid + '\'' +
                '}';
    }
}

