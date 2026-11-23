package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_insurance", schema = "dbo")
public class EdiTempBookingInsurance implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "lrpbook_no", length = 50)
    private String lrpbookNo;

    @Column(name = "eqpid", length = 20)
    private String eqpid;

    @Column(name = "commodity", length = 500)
    private String commodity;

    @Column(name = "quoteNo", length = 50)
    private String quoteNo;

    @Column(name = "policy_no", length = 50)
    private String policyNo;

    @Column(name = "insurance_amount")
    private Float insuranceAmount;

    @Column(name = "partyType", length = 2)
    private String partyType;

    @Column(name = "lrpbl_no", length = 50)
    private String lrpblNo;

    @Column(name = "policyStatus", length = 1)
    private String policyStatus;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "version")
    private Short version;

    @Column(name = "suminsured", length = 500)
    private String suminsured;

    // ===== Constructors =====
    public EdiTempBookingInsurance() {
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

    public String getLrpbookNo() {
        return lrpbookNo;
    }

    public void setLrpbookNo(String lrpbookNo) {
        this.lrpbookNo = lrpbookNo;
    }

    public String getEqpid() {
        return eqpid;
    }

    public void setEqpid(String eqpid) {
        this.eqpid = eqpid;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getQuoteNo() {
        return quoteNo;
    }

    public void setQuoteNo(String quoteNo) {
        this.quoteNo = quoteNo;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public Float getInsuranceAmount() {
        return insuranceAmount;
    }

    public void setInsuranceAmount(Float insuranceAmount) {
        this.insuranceAmount = insuranceAmount;
    }

    public String getPartyType() {
        return partyType;
    }

    public void setPartyType(String partyType) {
        this.partyType = partyType;
    }

    public String getLrpblNo() {
        return lrpblNo;
    }

    public void setLrpblNo(String lrpblNo) {
        this.lrpblNo = lrpblNo;
    }

    public String getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(String policyStatus) {
        this.policyStatus = policyStatus;
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

    public String getSuminsured() {
        return suminsured;
    }

    public void setSuminsured(String suminsured) {
        this.suminsured = suminsured;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBookingInsurance that = (EdiTempBookingInsurance) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EdiTempBookingInsurance{" +
                "id=" + id +
                ", bookNo='" + bookNo + '\'' +
                ", policyNo='" + policyNo + '\'' +
                '}';
    }
}

