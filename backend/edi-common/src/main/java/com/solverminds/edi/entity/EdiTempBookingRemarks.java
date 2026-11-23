package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_remarks", schema = "dbo")
public class EdiTempBookingRemarks implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "remarksref", length = 100)
    private String remarksref;

    @Column(name = "remarksval", columnDefinition = "varchar(max)")
    private String remarksval;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "edicleanstatus", length = 1)
    private String edicleanstatus;

    @Column(name = "version")
    private Integer version;

    @Column(name = "category", length = 1)
    private String category;

    @Column(name = "transtype", length = 3)
    private String transtype;

    // ===== Constructors =====
    public EdiTempBookingRemarks() {
    }

    // ===== Getters and Setters =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getRemarksref() {
        return remarksref;
    }

    public void setRemarksref(String remarksref) {
        this.remarksref = remarksref;
    }

    public String getRemarksval() {
        return remarksval;
    }

    public void setRemarksval(String remarksval) {
        this.remarksval = remarksval;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTranstype() {
        return transtype;
    }

    public void setTranstype(String transtype) {
        this.transtype = transtype;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBookingRemarks that = (EdiTempBookingRemarks) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EdiTempBookingRemarks{" +
                "id=" + id +
                ", bookNo='" + bookNo + '\'' +
                ", remarksref='" + remarksref + '\'' +
                '}';
    }
}

