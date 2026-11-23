package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_haulage", schema = "dbo")
public class EdiTempBookingHaulage implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Long cid;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "eqpId", length = 11)
    private String eqpId;

    @Column(name = "eqptypeid", length = 4)
    private String eqptypeid;

    @Column(name = "loadLocation", length = 50)
    private String loadLocation;

    @Column(name = "loadlocationname", length = 100)
    private String loadlocationname;

    @Column(name = "contactPerson", length = 200)
    private String contactPerson;

    @Column(name = "contactAddress", columnDefinition = "varchar(max)")
    private String contactAddress;

    @Column(name = "contactPhone", length = 25)
    private String contactPhone;

    @Column(name = "contactEmail", length = 100)
    private String contactEmail;

    @Column(name = "referenceNumber", length = 50)
    private String referenceNumber;

    @Column(name = "dropDate")
    private LocalDateTime dropDate;

    @Column(name = "remarks", columnDefinition = "varchar(max)")
    private String remarks;

    @Column(name = "docType", length = 20)
    private String docType;

    @Column(name = "overWeight", length = 2)
    private String overWeight;

    @Column(name = "stopSequence")
    private Integer stopSequence;

    @Column(name = "TMSRefNo", length = 200)
    private String tmsRefNo;

    @Column(name = "TMSClientCode", length = 10)
    private String tmsClientCode;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "version")
    private Short version;

    // ===== Constructors =====
    public EdiTempBookingHaulage() {
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

    public String getEqpId() {
        return eqpId;
    }

    public void setEqpId(String eqpId) {
        this.eqpId = eqpId;
    }

    public String getEqptypeid() {
        return eqptypeid;
    }

    public void setEqptypeid(String eqptypeid) {
        this.eqptypeid = eqptypeid;
    }

    public String getLoadLocation() {
        return loadLocation;
    }

    public void setLoadLocation(String loadLocation) {
        this.loadLocation = loadLocation;
    }

    public String getLoadlocationname() {
        return loadlocationname;
    }

    public void setLoadlocationname(String loadlocationname) {
        this.loadlocationname = loadlocationname;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public LocalDateTime getDropDate() {
        return dropDate;
    }

    public void setDropDate(LocalDateTime dropDate) {
        this.dropDate = dropDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getOverWeight() {
        return overWeight;
    }

    public void setOverWeight(String overWeight) {
        this.overWeight = overWeight;
    }

    public Integer getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(Integer stopSequence) {
        this.stopSequence = stopSequence;
    }

    public String getTmsRefNo() {
        return tmsRefNo;
    }

    public void setTmsRefNo(String tmsRefNo) {
        this.tmsRefNo = tmsRefNo;
    }

    public String getTmsClientCode() {
        return tmsClientCode;
    }

    public void setTmsClientCode(String tmsClientCode) {
        this.tmsClientCode = tmsClientCode;
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

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBookingHaulage that = (EdiTempBookingHaulage) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "EdiTempBookingHaulage{" +
                "cid=" + cid +
                ", bookNo='" + bookNo + '\'' +
                ", eqpId='" + eqpId + '\'' +
                '}';
    }
}

