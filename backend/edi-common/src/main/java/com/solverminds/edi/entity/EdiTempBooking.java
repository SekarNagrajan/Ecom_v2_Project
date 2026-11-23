package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "editempbooking", schema = "dbo")
public class EdiTempBooking implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "agencyid", length = 6)
    private String agencyid;

    @Column(name = "custRefNo", length = 10)
    private String custRefNo;

    @Column(name = "shipperRefNo", length = 10)
    private String shipperRefNo;

    @Column(name = "custrefNo1", length = 20)
    private String custrefNo1;

    @Column(name = "agencyRefNo", length = 20)
    private String agencyRefNo;

    @Column(name = "documentalAgencyId", length = 6)
    private String documentalAgencyId;

    @Column(name = "agencyIssueId", length = 6)
    private String agencyIssueId;

    @Column(name = "slotType", length = 3)
    private String slotType;

    @Column(name = "bookType", length = 10)
    private String bookType;

    @Column(name = "typeNo", length = 20)
    private String typeNo;

    @Column(name = "thirdParty", length = 1)
    private String thirdParty;

    @Column(name = "thirdPartyOper", length = 10)
    private String thirdPartyOper;

    @Column(name = "prepareOPS", length = 1)
    private String prepareOPS;

    @Column(name = "prepareBL", length = 1)
    private String prepareBL;

    @Column(name = "isDGBook", length = 1)
    private String isDGBook;

    @Column(name = "transportTermId", length = 2)
    private String transportTermId;

    @Column(name = "transportServiceId", length = 3)
    private String transportServiceId;

    @Column(name = "paymenttermsId", length = 3)
    private String paymenttermsId;

    @Column(name = "carrierCode", length = 3)
    private String carrierCode;

    @Column(name = "oceanCallId", length = 50)
    private String oceanCallId;

    @Column(name = "oceanVslCode", length = 6)
    private String oceanVslCode;

    @Column(name = "oceanVoyage", length = 6)
    private String oceanVoyage;

    @Column(name = "oceanSerCode", length = 4)
    private String oceanSerCode;

    @Column(name = "oceanBound", length = 1)
    private String oceanBound;

    @Column(name = "callId", length = 50)
    private String callId;

    @Column(name = "firstSvcType", length = 1)
    private String firstSvcType;

    @Column(name = "serviceCode", length = 4)
    private String serviceCode;

    @Column(name = "vesselCode", length = 6)
    private String vesselCode;

    @Column(name = "voyageCode", length = 6)
    private String voyageCode;

    @Column(name = "bound", length = 1)
    private String bound;

    @Column(name = "originPortId", length = 5)
    private String originPortId;

    @Column(name = "finalPortId", length = 5)
    private String finalPortId;

    @Column(name = "loadPortId", length = 20)
    private String loadPortId;

    @Column(name = "dischargePortId", length = 20)
    private String dischargePortId;

    @Column(name = "originPortDesc", length = 150)
    private String originPortDesc;

    @Column(name = "finalPortDesc", length = 150)
    private String finalPortDesc;

    @Column(name = "polTerminal", length = 10)
    private String polTerminal;

    @Column(name = "polAt")
    private LocalDateTime polAt;

    @Column(name = "podTerminal", length = 10)
    private String podTerminal;

    @Column(name = "podAt")
    private LocalDateTime podAt;

    @Column(name = "bookDate")
    private LocalDateTime bookDate;

    @Column(name = "bookReady")
    private LocalDateTime bookReady;

    @Column(name = "bookDeadline")
    private LocalDateTime bookDeadline;

    @Column(name = "preCarriage", length = 1)
    private String preCarriage;

    @Column(name = "onCarriage", length = 1)
    private String onCarriage;

    @Column(name = "agencypolid", length = 10)
    private String agencypolid;

    @Column(name = "crossBook", length = 1)
    private String crossBook;

    @Column(name = "remarks", columnDefinition = "varchar(max)")
    private String remarks;

    @Column(name = "Isupdate", length = 1)
    private String isupdate;

    @Column(name = "status", length = 1)
    private String status;

    @Column(name = "actStatus", length = 2)
    private String actStatus;

    @Column(name = "usercr", length = 150)
    private String usercr;

    @Column(name = "datecr")
    private LocalDateTime datecr;

    @Column(name = "userup", length = 150)
    private String userup;

    @Column(name = "dateup")
    private LocalDateTime dateup;

    @Column(name = "booksource", length = 5)
    private String booksource;

    @Column(name = "priority", length = 1)
    private String priority;

    @Column(name = "shipment_type", length = 50)
    private String shipmentType;

    @Column(name = "isedited", length = 1)
    private String isedited;

    @Column(name = "ratevaliditydate")
    private LocalDateTime ratevaliditydate;

    @Column(name = "remarks_cancel", length = 500)
    private String remarksCancel;

    @Column(name = "optionalpod", length = 5)
    private String optionalpod;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "edicleanstatus", length = 1)
    private String edicleanstatus;

    @Column(name = "expref", length = 50)
    private String expref;

    @Column(name = "transtype", length = 50)
    private String transtype;

    @Column(name = "messagesender", length = 50)
    private String messagesender;

    @Column(name = "comcontact", length = 50)
    private String comcontact;

    @Column(name = "typeofcomm", length = 50)
    private String typeofcomm;

    @Column(name = "version")
    private Short version;

    @Column(name = "ediparty", length = 20)
    private String ediparty;

    @Column(name = "messagetype", length = 20)
    private String messagetype;

    @Column(name = "tbook_no", columnDefinition = "varchar(max)")
    private String tbookNo;

    @Column(name = "tbl_no", columnDefinition = "varchar(max)")
    private String tblNo;

    @Column(name = "bloffice", length = 6)
    private String bloffice;

    @Column(name = "isMailSent", length = 1)
    private String isMailSent;

    @Column(name = "UNBREF", length = 50)
    private String unbref;

    @Column(name = "tbl_number", length = 20)
    private String tblNumber;

    @Column(name = "ecomBLStatus", length = 5)
    private String ecomBLStatus;

    @Column(name = "shipTerms", length = 4)
    private String shipTerms;

    @Column(name = "raterefNo", length = 20)
    private String raterefNo;

    @Column(name = "custremarks", columnDefinition = "varchar(max)")
    private String custremarks;

    @Column(name = "inTransitPort", length = 6)
    private String inTransitPort;

    @Column(name = "inTransitPortName", length = 150)
    private String inTransitPortName;

    @Column(name = "nat_code", length = 11)
    private String natCode;

    @Column(name = "emailloc", length = 250)
    private String emailloc;

    @Column(name = "fcnno", length = 20)
    private String fcnno;

    @Column(name = "cargoprotectid")
    private Integer cargoprotectid;

    @Column(name = "ensfiling", length = 1)
    private String ensfiling;

    @Column(name = "t2lfiling", length = 1)
    private String t2lfiling;

    @Column(name = "ensType", length = 1)
    private String ensType;

    // ===== Constructors =====
    public EdiTempBooking() {
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

    public String getAgencyid() {
        return agencyid;
    }

    public void setAgencyid(String agencyid) {
        this.agencyid = agencyid;
    }

    public String getCustRefNo() {
        return custRefNo;
    }

    public void setCustRefNo(String custRefNo) {
        this.custRefNo = custRefNo;
    }

    public String getShipperRefNo() {
        return shipperRefNo;
    }

    public void setShipperRefNo(String shipperRefNo) {
        this.shipperRefNo = shipperRefNo;
    }

    public String getCustrefNo1() {
        return custrefNo1;
    }

    public void setCustrefNo1(String custrefNo1) {
        this.custrefNo1 = custrefNo1;
    }

    public String getAgencyRefNo() {
        return agencyRefNo;
    }

    public void setAgencyRefNo(String agencyRefNo) {
        this.agencyRefNo = agencyRefNo;
    }

    public String getDocumentalAgencyId() {
        return documentalAgencyId;
    }

    public void setDocumentalAgencyId(String documentalAgencyId) {
        this.documentalAgencyId = documentalAgencyId;
    }

    public String getAgencyIssueId() {
        return agencyIssueId;
    }

    public void setAgencyIssueId(String agencyIssueId) {
        this.agencyIssueId = agencyIssueId;
    }

    public String getSlotType() {
        return slotType;
    }

    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
    }

    public String getThirdParty() {
        return thirdParty;
    }

    public void setThirdParty(String thirdParty) {
        this.thirdParty = thirdParty;
    }

    public String getThirdPartyOper() {
        return thirdPartyOper;
    }

    public void setThirdPartyOper(String thirdPartyOper) {
        this.thirdPartyOper = thirdPartyOper;
    }

    public String getPrepareOPS() {
        return prepareOPS;
    }

    public void setPrepareOPS(String prepareOPS) {
        this.prepareOPS = prepareOPS;
    }

    public String getPrepareBL() {
        return prepareBL;
    }

    public void setPrepareBL(String prepareBL) {
        this.prepareBL = prepareBL;
    }

    public String getIsDGBook() {
        return isDGBook;
    }

    public void setIsDGBook(String isDGBook) {
        this.isDGBook = isDGBook;
    }

    public String getTransportTermId() {
        return transportTermId;
    }

    public void setTransportTermId(String transportTermId) {
        this.transportTermId = transportTermId;
    }

    public String getTransportServiceId() {
        return transportServiceId;
    }

    public void setTransportServiceId(String transportServiceId) {
        this.transportServiceId = transportServiceId;
    }

    public String getPaymenttermsId() {
        return paymenttermsId;
    }

    public void setPaymenttermsId(String paymenttermsId) {
        this.paymenttermsId = paymenttermsId;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getOceanCallId() {
        return oceanCallId;
    }

    public void setOceanCallId(String oceanCallId) {
        this.oceanCallId = oceanCallId;
    }

    public String getOceanVslCode() {
        return oceanVslCode;
    }

    public void setOceanVslCode(String oceanVslCode) {
        this.oceanVslCode = oceanVslCode;
    }

    public String getOceanVoyage() {
        return oceanVoyage;
    }

    public void setOceanVoyage(String oceanVoyage) {
        this.oceanVoyage = oceanVoyage;
    }

    public String getOceanSerCode() {
        return oceanSerCode;
    }

    public void setOceanSerCode(String oceanSerCode) {
        this.oceanSerCode = oceanSerCode;
    }

    public String getOceanBound() {
        return oceanBound;
    }

    public void setOceanBound(String oceanBound) {
        this.oceanBound = oceanBound;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getFirstSvcType() {
        return firstSvcType;
    }

    public void setFirstSvcType(String firstSvcType) {
        this.firstSvcType = firstSvcType;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getVesselCode() {
        return vesselCode;
    }

    public void setVesselCode(String vesselCode) {
        this.vesselCode = vesselCode;
    }

    public String getVoyageCode() {
        return voyageCode;
    }

    public void setVoyageCode(String voyageCode) {
        this.voyageCode = voyageCode;
    }

    public String getBound() {
        return bound;
    }

    public void setBound(String bound) {
        this.bound = bound;
    }

    public String getOriginPortId() {
        return originPortId;
    }

    public void setOriginPortId(String originPortId) {
        this.originPortId = originPortId;
    }

    public String getFinalPortId() {
        return finalPortId;
    }

    public void setFinalPortId(String finalPortId) {
        this.finalPortId = finalPortId;
    }

    public String getLoadPortId() {
        return loadPortId;
    }

    public void setLoadPortId(String loadPortId) {
        this.loadPortId = loadPortId;
    }

    public String getDischargePortId() {
        return dischargePortId;
    }

    public void setDischargePortId(String dischargePortId) {
        this.dischargePortId = dischargePortId;
    }

    public String getOriginPortDesc() {
        return originPortDesc;
    }

    public void setOriginPortDesc(String originPortDesc) {
        this.originPortDesc = originPortDesc;
    }

    public String getFinalPortDesc() {
        return finalPortDesc;
    }

    public void setFinalPortDesc(String finalPortDesc) {
        this.finalPortDesc = finalPortDesc;
    }

    public String getPolTerminal() {
        return polTerminal;
    }

    public void setPolTerminal(String polTerminal) {
        this.polTerminal = polTerminal;
    }

    public LocalDateTime getPolAt() {
        return polAt;
    }

    public void setPolAt(LocalDateTime polAt) {
        this.polAt = polAt;
    }

    public String getPodTerminal() {
        return podTerminal;
    }

    public void setPodTerminal(String podTerminal) {
        this.podTerminal = podTerminal;
    }

    public LocalDateTime getPodAt() {
        return podAt;
    }

    public void setPodAt(LocalDateTime podAt) {
        this.podAt = podAt;
    }

    public LocalDateTime getBookDate() {
        return bookDate;
    }

    public void setBookDate(LocalDateTime bookDate) {
        this.bookDate = bookDate;
    }

    public LocalDateTime getBookReady() {
        return bookReady;
    }

    public void setBookReady(LocalDateTime bookReady) {
        this.bookReady = bookReady;
    }

    public LocalDateTime getBookDeadline() {
        return bookDeadline;
    }

    public void setBookDeadline(LocalDateTime bookDeadline) {
        this.bookDeadline = bookDeadline;
    }

    public String getPreCarriage() {
        return preCarriage;
    }

    public void setPreCarriage(String preCarriage) {
        this.preCarriage = preCarriage;
    }

    public String getOnCarriage() {
        return onCarriage;
    }

    public void setOnCarriage(String onCarriage) {
        this.onCarriage = onCarriage;
    }

    public String getAgencypolid() {
        return agencypolid;
    }

    public void setAgencypolid(String agencypolid) {
        this.agencypolid = agencypolid;
    }

    public String getCrossBook() {
        return crossBook;
    }

    public void setCrossBook(String crossBook) {
        this.crossBook = crossBook;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getIsupdate() {
        return isupdate;
    }

    public void setIsupdate(String isupdate) {
        this.isupdate = isupdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActStatus() {
        return actStatus;
    }

    public void setActStatus(String actStatus) {
        this.actStatus = actStatus;
    }

    public String getUsercr() {
        return usercr;
    }

    public void setUsercr(String usercr) {
        this.usercr = usercr;
    }

    public LocalDateTime getDatecr() {
        return datecr;
    }

    public void setDatecr(LocalDateTime datecr) {
        this.datecr = datecr;
    }

    public String getUserup() {
        return userup;
    }

    public void setUserup(String userup) {
        this.userup = userup;
    }

    public LocalDateTime getDateup() {
        return dateup;
    }

    public void setDateup(LocalDateTime dateup) {
        this.dateup = dateup;
    }

    public String getBooksource() {
        return booksource;
    }

    public void setBooksource(String booksource) {
        this.booksource = booksource;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getShipmentType() {
        return shipmentType;
    }

    public void setShipmentType(String shipmentType) {
        this.shipmentType = shipmentType;
    }

    public String getIsedited() {
        return isedited;
    }

    public void setIsedited(String isedited) {
        this.isedited = isedited;
    }

    public LocalDateTime getRatevaliditydate() {
        return ratevaliditydate;
    }

    public void setRatevaliditydate(LocalDateTime ratevaliditydate) {
        this.ratevaliditydate = ratevaliditydate;
    }

    public String getRemarksCancel() {
        return remarksCancel;
    }

    public void setRemarksCancel(String remarksCancel) {
        this.remarksCancel = remarksCancel;
    }

    public String getOptionalpod() {
        return optionalpod;
    }

    public void setOptionalpod(String optionalpod) {
        this.optionalpod = optionalpod;
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

    public String getExpref() {
        return expref;
    }

    public void setExpref(String expref) {
        this.expref = expref;
    }

    public String getTranstype() {
        return transtype;
    }

    public void setTranstype(String transtype) {
        this.transtype = transtype;
    }

    public String getMessagesender() {
        return messagesender;
    }

    public void setMessagesender(String messagesender) {
        this.messagesender = messagesender;
    }

    public String getComcontact() {
        return comcontact;
    }

    public void setComcontact(String comcontact) {
        this.comcontact = comcontact;
    }

    public String getTypeofcomm() {
        return typeofcomm;
    }

    public void setTypeofcomm(String typeofcomm) {
        this.typeofcomm = typeofcomm;
    }

    public Short getVersion() {
        return version;
    }

    public void setVersion(Short version) {
        this.version = version;
    }

    public String getEdiparty() {
        return ediparty;
    }

    public void setEdiparty(String ediparty) {
        this.ediparty = ediparty;
    }

    public String getMessagetype() {
        return messagetype;
    }

    public void setMessagetype(String messagetype) {
        this.messagetype = messagetype;
    }

    public String getTbookNo() {
        return tbookNo;
    }

    public void setTbookNo(String tbookNo) {
        this.tbookNo = tbookNo;
    }

    public String getTblNo() {
        return tblNo;
    }

    public void setTblNo(String tblNo) {
        this.tblNo = tblNo;
    }

    public String getBloffice() {
        return bloffice;
    }

    public void setBloffice(String bloffice) {
        this.bloffice = bloffice;
    }

    public String getIsMailSent() {
        return isMailSent;
    }

    public void setIsMailSent(String isMailSent) {
        this.isMailSent = isMailSent;
    }

    public String getUnbref() {
        return unbref;
    }

    public void setUnbref(String unbref) {
        this.unbref = unbref;
    }

    public String getTblNumber() {
        return tblNumber;
    }

    public void setTblNumber(String tblNumber) {
        this.tblNumber = tblNumber;
    }

    public String getEcomBLStatus() {
        return ecomBLStatus;
    }

    public void setEcomBLStatus(String ecomBLStatus) {
        this.ecomBLStatus = ecomBLStatus;
    }

    public String getShipTerms() {
        return shipTerms;
    }

    public void setShipTerms(String shipTerms) {
        this.shipTerms = shipTerms;
    }

    public String getRaterefNo() {
        return raterefNo;
    }

    public void setRaterefNo(String raterefNo) {
        this.raterefNo = raterefNo;
    }

    public String getCustremarks() {
        return custremarks;
    }

    public void setCustremarks(String custremarks) {
        this.custremarks = custremarks;
    }

    public String getInTransitPort() {
        return inTransitPort;
    }

    public void setInTransitPort(String inTransitPort) {
        this.inTransitPort = inTransitPort;
    }

    public String getInTransitPortName() {
        return inTransitPortName;
    }

    public void setInTransitPortName(String inTransitPortName) {
        this.inTransitPortName = inTransitPortName;
    }

    public String getNatCode() {
        return natCode;
    }

    public void setNatCode(String natCode) {
        this.natCode = natCode;
    }

    public String getEmailloc() {
        return emailloc;
    }

    public void setEmailloc(String emailloc) {
        this.emailloc = emailloc;
    }

    public String getFcnno() {
        return fcnno;
    }

    public void setFcnno(String fcnno) {
        this.fcnno = fcnno;
    }

    public Integer getCargoprotectid() {
        return cargoprotectid;
    }

    public void setCargoprotectid(Integer cargoprotectid) {
        this.cargoprotectid = cargoprotectid;
    }

    public String getEnsfiling() {
        return ensfiling;
    }

    public void setEnsfiling(String ensfiling) {
        this.ensfiling = ensfiling;
    }

    public String getT2lfiling() {
        return t2lfiling;
    }

    public void setT2lfiling(String t2lfiling) {
        this.t2lfiling = t2lfiling;
    }

    public String getEnsType() {
        return ensType;
    }

    public void setEnsType(String ensType) {
        this.ensType = ensType;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBooking that = (EdiTempBooking) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EdiTempBooking{" +
                "id=" + id +
                ", bookNo='" + bookNo + '\'' +
                ", agencyid='" + agencyid + '\'' +
                ", edireaderid='" + edireaderid + '\'' +
                '}';
    }
}

