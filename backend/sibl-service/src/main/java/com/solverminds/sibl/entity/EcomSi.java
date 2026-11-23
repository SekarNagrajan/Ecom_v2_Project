package com.solverminds.sibl.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "ecomSI", schema = "dbo")
public class EcomSi implements Serializable {

    // ===== Primary Key =====
    @Id
    @Column(name = "eSINo", length = 20, nullable = false)
    private String eSINo;

    // ===== Core Fields =====
    @Column(name = "blno", length = 20)
    private String blno;

    @Column(name = "custcode", length = 50)
    private String custcode;

    @Column(name = "bookno", length = 20, nullable = false)
    private String bookno;

    @Column(name = "status", length = 1)
    private String status;

    // ===== Vessel and Voyage Fields =====
    @Column(name = "Vessel", length = 50)
    private String vessel;

    @Column(name = "Voyage", length = 50)
    private String voyage;

    @Column(name = "bound", length = 1)
    private String bound;

    // ===== Audit Fields =====
    @Column(name = "CrUser", length = 100)
    private String crUser;

    @Column(name = "CrDate")
    private LocalDateTime crDate;

    @Column(name = "ChgUser", length = 100)
    private String chgUser;

    @Column(name = "ChgDate")
    private LocalDateTime chgDate;

    // ===== Business Fields =====
    @Column(name = "editbl")
    private Integer editbl;

    @Column(name = "blversion")
    private Integer blversion;

    @Column(name = "AESNumber", length = 50)
    private String aesNumber;

    @Column(name = "releasetype", length = 20)
    private String releasetype;

    @Column(name = "blType", length = 100)
    private String blType;

    @Column(name = "fcnno", length = 50)
    private String fcnno;

    @Column(name = "ACID", length = 50)
    private String acid;

    @Column(name = "freight", length = 50)
    private String freight;

    @Column(name = "packinglst", length = 100)
    private String packinglst;

    @Column(name = "invoiceupload", length = 100)
    private String invoiceupload;

    @Column(name = "telexrls", length = 1)
    private String telexrls;

    @Column(name = "custremarks", length = 500)
    private String custremarks;

    // ===== Location Fields =====
    @Column(name = "pla", length = 5)
    private String pla;

    @Column(name = "pol", length = 5)
    private String pol;

    @Column(name = "pod", length = 5)
    private String pod;

    @Column(name = "pld", length = 5)
    private String pld;

    // ===== Header Name Fields =====
    @Column(name = "plaHeaderName", length = 150)
    private String plaHeaderName;

    @Column(name = "polHeaderName", length = 150)
    private String polHeaderName;

    @Column(name = "podHeaderName", length = 150)
    private String podHeaderName;

    @Column(name = "pldHeaderName", length = 150)
    private String pldHeaderName;

    // ===== Additional Fields =====
    @Column(name = "declined_remarks", length = 1000)
    private String declinedRemarks;

    @Column(name = "ccmailid", length = 500)
    private String ccmailid;

    @Column(name = "appversion")
    private Integer appversion;

    @Column(name = "AESDiscliamer", length = 50)
    private String aesDiscliamer;

    @Column(name = "cargoprotectid")
    private Integer cargoprotectid;

    // ===== Party Fields =====
    @Column(name = "shipper", length = 500)
    private String shipper;

    @Column(name = "shippername", length = 150)
    private String shippername;

    @Column(name = "consigee", length = 500)
    private String consigee;

    @Column(name = "consigeename", length = 150)
    private String consigeename;

    @Column(name = "notify", length = 500)
    private String notify;

    @Column(name = "notifyname", length = 150)
    private String notifyname;

    // ===== Additional Fields =====
    @Column(name = "declaredvalue", length = 1500)
    private String declaredvalue;

    @Column(name = "t2lfiling", length = 1)
    private String t2lfiling;

    // Note: ensfiling column removed as per requirements
    // @Column(name = "ensfiling", length = 1)
    // private String ensfiling;

    @Column(name = "ensType", length = 1)
    private String ensType;

    // ===== Constructors =====
    public EcomSi() {
    }

    public EcomSi(String eSINo) {
        this.eSINo = eSINo;
    }

    // ===== Getters/Setters =====
    
    public String geteSINo() {
        return eSINo;
    }

    public void seteSINo(String eSINo) {
        this.eSINo = eSINo;
    }

    public String getBlno() {
        return blno;
    }

    public void setBlno(String blno) {
        this.blno = blno;
    }

    public String getCustcode() {
        return custcode;
    }

    public void setCustcode(String custcode) {
        this.custcode = custcode;
    }

    public String getBookno() {
        return bookno;
    }

    public void setBookno(String bookno) {
        this.bookno = bookno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Integer getEditbl() {
        return editbl;
    }

    public void setEditbl(Integer editbl) {
        this.editbl = editbl;
    }

    public Integer getBlversion() {
        return blversion;
    }

    public void setBlversion(Integer blversion) {
        this.blversion = blversion;
    }

    public String getAesNumber() {
        return aesNumber;
    }

    public void setAesNumber(String aesNumber) {
        this.aesNumber = aesNumber;
    }

    public String getReleasetype() {
        return releasetype;
    }

    public void setReleasetype(String releasetype) {
        this.releasetype = releasetype;
    }

    public String getBlType() {
        return blType;
    }

    public void setBlType(String blType) {
        this.blType = blType;
    }

    public String getFcnno() {
        return fcnno;
    }

    public void setFcnno(String fcnno) {
        this.fcnno = fcnno;
    }

    public String getAcid() {
        return acid;
    }

    public void setAcid(String acid) {
        this.acid = acid;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
    }

    public String getPackinglst() {
        return packinglst;
    }

    public void setPackinglst(String packinglst) {
        this.packinglst = packinglst;
    }

    public String getInvoiceupload() {
        return invoiceupload;
    }

    public void setInvoiceupload(String invoiceupload) {
        this.invoiceupload = invoiceupload;
    }

    public String getTelexrls() {
        return telexrls;
    }

    public void setTelexrls(String telexrls) {
        this.telexrls = telexrls;
    }

    public String getCustremarks() {
        return custremarks;
    }

    public void setCustremarks(String custremarks) {
        this.custremarks = custremarks;
    }

    public String getPla() {
        return pla;
    }

    public void setPla(String pla) {
        this.pla = pla;
    }

    public String getPol() {
        return pol;
    }

    public void setPol(String pol) {
        this.pol = pol;
    }

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }

    public String getPld() {
        return pld;
    }

    public void setPld(String pld) {
        this.pld = pld;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public String getShippername() {
        return shippername;
    }

    public void setShippername(String shippername) {
        this.shippername = shippername;
    }

    public String getConsigee() {
        return consigee;
    }

    public void setConsigee(String consigee) {
        this.consigee = consigee;
    }

    public String getConsigeename() {
        return consigeename;
    }

    public void setConsigeename(String consigeename) {
        this.consigeename = consigeename;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public String getNotifyname() {
        return notifyname;
    }

    public void setNotifyname(String notifyname) {
        this.notifyname = notifyname;
    }

    public String getDeclaredvalue() {
        return declaredvalue;
    }

    public void setDeclaredvalue(String declaredvalue) {
        this.declaredvalue = declaredvalue;
    }

    public String getT2lfiling() {
        return t2lfiling;
    }

    public void setT2lfiling(String t2lfiling) {
        this.t2lfiling = t2lfiling;
    }

    // Note: ensfiling getter/setter removed as per requirements
    // public String getEnsfiling() {
    //     return ensfiling;
    // }

    // public void setEnsfiling(String ensfiling) {
    //     this.ensfiling = ensfiling;
    // }

    public String getEnsType() {
        return ensType;
    }

    public void setEnsType(String ensType) {
        this.ensType = ensType;
    }

    // ===== New Field Getters/Setters =====
    
    public String getVessel() {
        return vessel;
    }

    public void setVessel(String vessel) {
        this.vessel = vessel;
    }

    public String getVoyage() {
        return voyage;
    }

    public void setVoyage(String voyage) {
        this.voyage = voyage;
    }

    public String getBound() {
        return bound;
    }

    public void setBound(String bound) {
        this.bound = bound;
    }

    public String getPlaHeaderName() {
        return plaHeaderName;
    }

    public void setPlaHeaderName(String plaHeaderName) {
        this.plaHeaderName = plaHeaderName;
    }

    public String getPolHeaderName() {
        return polHeaderName;
    }

    public void setPolHeaderName(String polHeaderName) {
        this.polHeaderName = polHeaderName;
    }

    public String getPodHeaderName() {
        return podHeaderName;
    }

    public void setPodHeaderName(String podHeaderName) {
        this.podHeaderName = podHeaderName;
    }

    public String getPldHeaderName() {
        return pldHeaderName;
    }

    public void setPldHeaderName(String pldHeaderName) {
        this.pldHeaderName = pldHeaderName;
    }

    public String getDeclinedRemarks() {
        return declinedRemarks;
    }

    public void setDeclinedRemarks(String declinedRemarks) {
        this.declinedRemarks = declinedRemarks;
    }

    public String getCcmailid() {
        return ccmailid;
    }

    public void setCcmailid(String ccmailid) {
        this.ccmailid = ccmailid;
    }

    public Integer getAppversion() {
        return appversion;
    }

    public void setAppversion(Integer appversion) {
        this.appversion = appversion;
    }

    public String getAesDiscliamer() {
        return aesDiscliamer;
    }

    public void setAesDiscliamer(String aesDiscliamer) {
        this.aesDiscliamer = aesDiscliamer;
    }

    public Integer getCargoprotectid() {
        return cargoprotectid;
    }

    public void setCargoprotectid(Integer cargoprotectid) {
        this.cargoprotectid = cargoprotectid;
    }

    // ===== equals/hashCode by ID =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomSi)) return false;
        EcomSi ecomSi = (EcomSi) o;
        return Objects.equals(eSINo, ecomSi.eSINo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eSINo);
    }

    @Override
    public String toString() {
        return "EcomSi{" +
                "eSINo='" + eSINo + '\'' +
                ", blno='" + blno + '\'' +
                ", custcode='" + custcode + '\'' +
                ", bookno='" + bookno + '\'' +
                ", status='" + status + '\'' +
                ", crUser='" + crUser + '\'' +
                ", crDate=" + crDate +
                ", chgUser='" + chgUser + '\'' +
                ", chgDate=" + chgDate +
                '}';
    }
}
