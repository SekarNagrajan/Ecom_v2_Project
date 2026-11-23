package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "edidocconfig", schema = "dbo")
public class EdiDocConfig implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // ===== Columns =====
    @Column(name = "EDIId", length = 20)
    private String ediId;

    @Column(name = "EDIName", length = 150)
    private String ediName;

    @Column(name = "EDIMsgtype", length = 8)
    private String ediMsgtype;

    @Column(name = "EDIType", length = 50)
    private String ediType;

    @Column(name = "EDIScope", length = 20)
    private String ediScope;

    @Column(name = "EDIConDec", length = 1)
    private String ediConDec;

    @Column(name = "AccEDIId", length = 30)
    private String accEdiId;

    @Column(name = "DecEDIId", length = 30)
    private String decEdiId;

    @Column(name = "EDISenderId", length = 50)
    private String ediSenderId;

    @Column(name = "EDIDecline", length = 1)
    private String ediDecline;

    @Column(name = "ismail", length = 5)
    private String ismail;

    @Column(name = "accdec_prefix", length = 20)
    private String accdecPrefix;

    @Column(name = "accdec_senderid", length = 20)
    private String accdecSenderid;

    @Column(name = "isbledit", length = 5)
    private String isbledit;

    @Column(name = "isbkcr", length = 5)
    private String isbkcr;

    @Column(name = "isbkup", length = 5)
    private String isbkup;

    @Column(name = "status", length = 1)
    private String status;

    @Column(name = "cruser", length = 50)
    private String cruser;

    @Column(name = "upuser", length = 50)
    private String upuser;

    @Column(name = "dateup")
    private LocalDateTime dateup;

    @Column(name = "datecr")
    private LocalDateTime datecr;

    // ===== Constructors =====
    public EdiDocConfig() {
    }

    // ===== Getters and Setters =====
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEdiId() {
        return ediId;
    }

    public void setEdiId(String ediId) {
        this.ediId = ediId;
    }

    public String getEdiName() {
        return ediName;
    }

    public void setEdiName(String ediName) {
        this.ediName = ediName;
    }

    public String getEdiMsgtype() {
        return ediMsgtype;
    }

    public void setEdiMsgtype(String ediMsgtype) {
        this.ediMsgtype = ediMsgtype;
    }

    public String getEdiType() {
        return ediType;
    }

    public void setEdiType(String ediType) {
        this.ediType = ediType;
    }

    public String getEdiScope() {
        return ediScope;
    }

    public void setEdiScope(String ediScope) {
        this.ediScope = ediScope;
    }

    public String getEdiConDec() {
        return ediConDec;
    }

    public void setEdiConDec(String ediConDec) {
        this.ediConDec = ediConDec;
    }

    public String getAccEdiId() {
        return accEdiId;
    }

    public void setAccEdiId(String accEdiId) {
        this.accEdiId = accEdiId;
    }

    public String getDecEdiId() {
        return decEdiId;
    }

    public void setDecEdiId(String decEdiId) {
        this.decEdiId = decEdiId;
    }

    public String getEdiSenderId() {
        return ediSenderId;
    }

    public void setEdiSenderId(String ediSenderId) {
        this.ediSenderId = ediSenderId;
    }

    public String getEdiDecline() {
        return ediDecline;
    }

    public void setEdiDecline(String ediDecline) {
        this.ediDecline = ediDecline;
    }

    public String getIsmail() {
        return ismail;
    }

    public void setIsmail(String ismail) {
        this.ismail = ismail;
    }

    public String getAccdecPrefix() {
        return accdecPrefix;
    }

    public void setAccdecPrefix(String accdecPrefix) {
        this.accdecPrefix = accdecPrefix;
    }

    public String getAccdecSenderid() {
        return accdecSenderid;
    }

    public void setAccdecSenderid(String accdecSenderid) {
        this.accdecSenderid = accdecSenderid;
    }

    public String getIsbledit() {
        return isbledit;
    }

    public void setIsbledit(String isbledit) {
        this.isbledit = isbledit;
    }

    public String getIsbkcr() {
        return isbkcr;
    }

    public void setIsbkcr(String isbkcr) {
        this.isbkcr = isbkcr;
    }

    public String getIsbkup() {
        return isbkup;
    }

    public void setIsbkup(String isbkup) {
        this.isbkup = isbkup;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCruser() {
        return cruser;
    }

    public void setCruser(String cruser) {
        this.cruser = cruser;
    }

    public String getUpuser() {
        return upuser;
    }

    public void setUpuser(String upuser) {
        this.upuser = upuser;
    }

    public LocalDateTime getDateup() {
        return dateup;
    }

    public void setDateup(LocalDateTime dateup) {
        this.dateup = dateup;
    }

    public LocalDateTime getDatecr() {
        return datecr;
    }

    public void setDatecr(LocalDateTime datecr) {
        this.datecr = datecr;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiDocConfig that = (EdiDocConfig) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EdiDocConfig{" +
                "id=" + id +
                ", ediId='" + ediId + '\'' +
                ", ediName='" + ediName + '\'' +
                ", ediMsgtype='" + ediMsgtype + '\'' +
                ", ediType='" + ediType + '\'' +
                ", ediScope='" + ediScope + '\'' +
                ", ediConDec='" + ediConDec + '\'' +
                ", accEdiId='" + accEdiId + '\'' +
                ", decEdiId='" + decEdiId + '\'' +
                ", ediSenderId='" + ediSenderId + '\'' +
                ", ediDecline='" + ediDecline + '\'' +
                ", ismail='" + ismail + '\'' +
                ", accdecPrefix='" + accdecPrefix + '\'' +
                ", accdecSenderid='" + accdecSenderid + '\'' +
                ", isbledit='" + isbledit + '\'' +
                ", isbkcr='" + isbkcr + '\'' +
                ", isbkup='" + isbkup + '\'' +
                ", status='" + status + '\'' +
                ", cruser='" + cruser + '\'' +
                ", upuser='" + upuser + '\'' +
                ", dateup=" + dateup +
                ", datecr=" + datecr +
                '}';
    }
}

