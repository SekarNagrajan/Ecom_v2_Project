package com.solverminds.sibl.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "ecomSIRequestMaster", schema = "dbo")
public class EcomSiRequestMaster implements Serializable {

    // ===== Primary Key =====
    @Id
    @Column(name = "reqID", length = 20)
    private String reqID;

    // ===== Core Fields =====
    @Column(name = "userId", length = 150)
    private String userId;

    @Column(name = "requestedSI")
    private Integer requestedSI;

    @Column(name = "createdSI")
    private Integer createdSI;

    @Column(name = "bookno", length = 20, nullable = false)
    private String bookno;

    @Column(name = "custcode", length = 20, nullable = false)
    private String custcode;

    @Column(name = "crUser", length = 150)
    private String crUser;

    @Column(name = "crDate")
    private LocalDateTime crDate;

    @Column(name = "chgUser", length = 150)
    private String chgUser;

    @Column(name = "chgDate")
    private LocalDateTime chgDate;

    // ===== Constructors =====
    public EcomSiRequestMaster() {
        // Default values as per schema
        this.requestedSI = 1;
        this.createdSI = 1;
    }

    // ===== Getters and Setters =====
    public String getReqID() {
        return reqID;
    }

    public void setReqID(String reqID) {
        this.reqID = reqID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getRequestedSI() {
        return requestedSI;
    }

    public void setRequestedSI(Integer requestedSI) {
        this.requestedSI = requestedSI;
    }

    public Integer getCreatedSI() {
        return createdSI;
    }

    public void setCreatedSI(Integer createdSI) {
        this.createdSI = createdSI;
    }

    public String getBookno() {
        return bookno;
    }

    public void setBookno(String bookno) {
        this.bookno = bookno;
    }

    public String getCustcode() {
        return custcode;
    }

    public void setCustcode(String custcode) {
        this.custcode = custcode;
    }

    public String getCrUser() {
        return crUser;
    }

    public void setCrUser(String crUser) {
        this.crUser = crUser;
    }

    public LocalDateTime getCrDate() {
        return crDate;
    }

    public void setCrDate(LocalDateTime crDate) {
        this.crDate = crDate;
    }

    public String getChgUser() {
        return chgUser;
    }

    public void setChgUser(String chgUser) {
        this.chgUser = chgUser;
    }

    public LocalDateTime getChgDate() {
        return chgDate;
    }

    public void setChgDate(LocalDateTime chgDate) {
        this.chgDate = chgDate;
    }

    // ===== equals and hashCode =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EcomSiRequestMaster that = (EcomSiRequestMaster) o;
        return Objects.equals(reqID, that.reqID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reqID);
    }

    @Override
    public String toString() {
        return "EcomSiRequestMaster{" +
                "reqID='" + reqID + '\'' +
                ", userId='" + userId + '\'' +
                ", requestedSI=" + requestedSI +
                ", createdSI=" + createdSI +
                ", bookno='" + bookno + '\'' +
                ", custcode='" + custcode + '\'' +
                ", crUser='" + crUser + '\'' +
                ", crDate=" + crDate +
                ", chgUser='" + chgUser + '\'' +
                ", chgDate=" + chgDate +
                '}';
    }
}
