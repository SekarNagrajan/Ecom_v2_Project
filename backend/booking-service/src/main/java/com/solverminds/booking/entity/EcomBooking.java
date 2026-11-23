package com.solverminds.booking.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "ecombooking", schema = "dbo") // SQL Server default schema
public class EcomBooking implements Serializable {

    // ===== Primary Key =====
    @Id
    @Column(name = "ebookno", length = 20, nullable = false)
    private String ebookno;

    // ===== Columns =====
    @Column(name = "agencyid", length = 6)
    private String agencyid;

    @Column(name = "originPortId", length = 5)
    private String originPortId;

    @Column(name = "finalPortId", length = 5)
    private String finalPortId;

    @Column(name = "loadPortId", length = 5)
    private String loadPortId;

    @Column(name = "dischargePortId", length = 5)
    private String dischargePortId;

    @Column(name = "bookDate")
    private LocalDateTime bookDate;

    @Column(name = "bookReady")
    private LocalDateTime bookReady;

    @Column(name = "preCarriage", length = 1)
    private String preCarriage;

    @Column(name = "onCarriage", length = 1)
    private String onCarriage;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "usercr")
    private String usercr;

    @Column(name = "datecr")
    private LocalDateTime datecr;

    @Column(name = "userup", length = 100)
    private String userup;

    @Column(name = "dateup")
    private LocalDateTime dateup;

    @Column(name = "ebook_status", length = 1)
    private String ebookStatus;

    @Column(name = "extBookNo", length = 50)
    private String extBookNo;

    @Column(name = "cust_code", length = 50)
    private String custCode;

    @Column(name = "templatename", length = 30)
    private String templatename;

    @Column(name = "shipTerms", length = 3)
    private String shipTerms;

    @Column(name = "rateno", length = 20)
    private String rateno;

    @Column(name = "submitted_date")
    private LocalDateTime submittedDate;

    @Column(name = "declined_remarks", length = 1000)
    private String declinedRemarks;

    @Column(name = "routeId")
    private Long routeId; // bigint

    @Column(name = "agencyRefNo", length = 20)
    private String agencyRefNo;

    @Column(name = "oceanfreight", length = 1)
    private String oceanfreight;

    @Column(name = "trptsvccode", length = 3)
    private String trptsvccode;

    @Column(name = "emptypickup", length = 10)
    private String emptypickup;

    @Column(name = "emptypickupdate")
    private LocalDateTime emptypickupdate;

    @Column(name = "version")
    private Integer version; // int

    @Column(name = "isAmendment", length = 1)
    private String isAmendment;

    @Column(name = "intransitport", length = 6)
    private String intransitport;

    @Column(name = "nat_code", length = 11)
    private String natCode;

    @Column(name = "pickup_point", length = 1)
    private String pickupPoint;

    @Column(name = "appversion")
    private Integer appversion;

    @Column(name = "salesagreedrate", length = 200)
    private String salesagreedrate;

    @Column(name = "ccmailid", length = 500)
    private String ccmailid;

    @Column(name = "haulercode", length = 20)
    private String haulercode;

    @Column(name = "haulagetype", length = 1)
    private String haulagetype;

    @Column(name = "pickupdate")
    private LocalDateTime pickupdate;

    @Column(name = "dropdate")
    private LocalDateTime dropdate;

    @Column(name = "exportRef", length = 50)
    private String exportRef;

    @Column(name = "releasetype", length = 10)
    private String releasetype;

    @Column(name = "custPO", length = 200)
    private String custPO;

    @Column(name = "customerreference", length = 150)
    private String customerreference;

    @Column(name = "ACID", length = 20)
    private String acid;

    @Column(name = "cargoprotectid")
    private Integer cargoprotectid; // int

    @Column(name = "ShipperReference", length = 45)
    private String shipperReference;

    @Column(name = "ensfiling", length = 1)
    private String ensfiling;

    @Column(name = "t2lfiling", length = 1)
    private String t2lfiling;

    @Column(name = "sisubmitparty", length = 2)
    private String sisubmitparty;

    @Column(name = "ensType", length = 1)
    private String ensType;

    // ===== Lifecycle Callbacks =====
    @PrePersist
    public void prePersist() {
        if (this.datecr == null) {
            this.datecr = LocalDateTime.now();
        }
        if (this.submittedDate == null) {
            this.submittedDate = LocalDateTime.now();
        }
        if (this.version == null) {
            this.version = 0;
        }
    }

    // ===== Getters/Setters =====

    public String getEbookno() { return ebookno; }
    public void setEbookno(String ebookno) { this.ebookno = ebookno; }

    public String getAgencyid() { return agencyid; }
    public void setAgencyid(String agencyid) { this.agencyid = agencyid; }

    public String getOriginPortId() { return originPortId; }
    public void setOriginPortId(String originPortId) { this.originPortId = originPortId; }

    public String getFinalPortId() { return finalPortId; }
    public void setFinalPortId(String finalPortId) { this.finalPortId = finalPortId; }

    public String getLoadPortId() { return loadPortId; }
    public void setLoadPortId(String loadPortId) { this.loadPortId = loadPortId; }

    public String getDischargePortId() { return dischargePortId; }
    public void setDischargePortId(String dischargePortId) { this.dischargePortId = dischargePortId; }

    public LocalDateTime getBookDate() { return bookDate; }
    public void setBookDate(LocalDateTime bookDate) { this.bookDate = bookDate; }

    public LocalDateTime getBookReady() { return bookReady; }
    public void setBookReady(LocalDateTime bookReady) { this.bookReady = bookReady; }

    public String getPreCarriage() { return preCarriage; }
    public void setPreCarriage(String preCarriage) { this.preCarriage = preCarriage; }

    public String getOnCarriage() { return onCarriage; }
    public void setOnCarriage(String onCarriage) { this.onCarriage = onCarriage; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getUsercr() { return usercr; }
    public void setUsercr(String usercr) { this.usercr = usercr; }

    public LocalDateTime getDatecr() { return datecr; }
    public void setDatecr(LocalDateTime datecr) { this.datecr = datecr; }

    public String getUserup() { return userup; }
    public void setUserup(String userup) { this.userup = userup; }

    public LocalDateTime getDateup() { return dateup; }
    public void setDateup(LocalDateTime dateup) { this.dateup = dateup; }

    public String getEbookStatus() { return ebookStatus; }
    public void setEbookStatus(String ebookStatus) { this.ebookStatus = ebookStatus; }

    public String getExtBookNo() { return extBookNo; }
    public void setExtBookNo(String extBookNo) { this.extBookNo = extBookNo; }

    public String getCustCode() { return custCode; }
    public void setCustCode(String custCode) { this.custCode = custCode; }

    public String getTemplatename() { return templatename; }
    public void setTemplatename(String templatename) { this.templatename = templatename; }

    public String getShipTerms() { return shipTerms; }
    public void setShipTerms(String shipTerms) { this.shipTerms = shipTerms; }

    public String getRateno() { return rateno; }
    public void setRateno(String rateno) { this.rateno = rateno; }

    public LocalDateTime getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDateTime submittedDate) { this.submittedDate = submittedDate; }

    public String getDeclinedRemarks() { return declinedRemarks; }
    public void setDeclinedRemarks(String declinedRemarks) { this.declinedRemarks = declinedRemarks; }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public String getAgencyRefNo() { return agencyRefNo; }
    public void setAgencyRefNo(String agencyRefNo) { this.agencyRefNo = agencyRefNo; }

    public String getOceanfreight() { return oceanfreight; }
    public void setOceanfreight(String oceanfreight) { this.oceanfreight = oceanfreight; }

    public String getTrptsvccode() { return trptsvccode; }
    public void setTrptsvccode(String trptsvccode) { this.trptsvccode = trptsvccode; }

    public String getEmptypickup() { return emptypickup; }
    public void setEmptypickup(String emptypickup) { this.emptypickup = emptypickup; }

    public LocalDateTime getEmptypickupdate() { return emptypickupdate; }
    public void setEmptypickupdate(LocalDateTime emptypickupdate) { this.emptypickupdate = emptypickupdate; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public String getIsAmendment() { return isAmendment; }
    public void setIsAmendment(String isAmendment) { this.isAmendment = isAmendment; }

    public String getIntransitport() { return intransitport; }
    public void setIntransitport(String intransitport) { this.intransitport = intransitport; }

    public String getNatCode() { return natCode; }
    public void setNatCode(String natCode) { this.natCode = natCode; }

    public String getPickupPoint() { return pickupPoint; }
    public void setPickupPoint(String pickupPoint) { this.pickupPoint = pickupPoint; }

    public Integer getAppversion() { return appversion; }
    public void setAppversion(Integer appversion) { this.appversion = appversion; }

    public String getSalesagreedrate() { return salesagreedrate; }
    public void setSalesagreedrate(String salesagreedrate) { this.salesagreedrate = salesagreedrate; }

    public String getCcmailid() { return ccmailid; }
    public void setCcmailid(String ccmailid) { this.ccmailid = ccmailid; }

    public String getHaulercode() { return haulercode; }
    public void setHaulercode(String haulercode) { this.haulercode = haulercode; }

    public String getHaulagetype() { return haulagetype; }
    public void setHaulagetype(String haulagetype) { this.haulagetype = haulagetype; }

    public LocalDateTime getPickupdate() { return pickupdate; }
    public void setPickupdate(LocalDateTime pickupdate) { this.pickupdate = pickupdate; }

    public LocalDateTime getDropdate() { return dropdate; }
    public void setDropdate(LocalDateTime dropdate) { this.dropdate = dropdate; }

    public String getExportRef() { return exportRef; }
    public void setExportRef(String exportRef) { this.exportRef = exportRef; }

    public String getReleasetype() { return releasetype; }
    public void setReleasetype(String releasetype) { this.releasetype = releasetype; }

    public String getCustPO() { return custPO; }
    public void setCustPO(String custPO) { this.custPO = custPO; }

    public String getCustomerreference() { return customerreference; }
    public void setCustomerreference(String customerreference) { this.customerreference = customerreference; }

    public String getAcid() { return acid; }
    public void setAcid(String acid) { this.acid = acid; }

    public Integer getCargoprotectid() { return cargoprotectid; }
    public void setCargoprotectid(Integer cargoprotectid) { this.cargoprotectid = cargoprotectid; }

    public String getShipperReference() { return shipperReference; }
    public void setShipperReference(String shipperReference) { this.shipperReference = shipperReference; }

    public String getEnsfiling() { return ensfiling; }
    public void setEnsfiling(String ensfiling) { this.ensfiling = ensfiling; }

    public String getT2lfiling() { return t2lfiling; }
    public void setT2lfiling(String t2lfiling) { this.t2lfiling = t2lfiling; }

    public String getSisubmitparty() { return sisubmitparty; }
    public void setSisubmitparty(String sisubmitparty) { this.sisubmitparty = sisubmitparty; }

    public String getEnsType() { return ensType; }
    public void setEnsType(String ensType) { this.ensType = ensType; }

    // ===== equals/hashCode by ID =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomBooking)) return false;
        EcomBooking that = (EcomBooking) o;
        return Objects.equals(ebookno, that.ebookno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ebookno);
    }

    @Override
    public String toString() {
        return "EcomBooking{ebookno='" + ebookno + "', originPortId='" + originPortId +
                "', finalPortId='" + finalPortId + "', status='" + ebookStatus + "'}";
    }
}
