package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_leg", schema = "dbo")
public class EdiTempBookingLeg implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "leg_no")
    private Short legNo;

    @Column(name = "bound", length = 5)
    private String bound;

    @Column(name = "serviceCode", length = 4)
    private String serviceCode;

    @Column(name = "vesselCode", length = 6)
    private String vesselCode;

    @Column(name = "voyageCode", length = 6)
    private String voyageCode;

    @Column(name = "callIdDischarge", length = 50)
    private String callIdDischarge;

    @Column(name = "callIdLoading", length = 50)
    private String callIdLoading;

    @Column(name = "legTypeId", length = 1)
    private String legTypeId;

    @Column(name = "loadPortId", length = 5)
    private String loadPortId;

    @Column(name = "dischargePortId", length = 5)
    private String dischargePortId;

    @Column(name = "etdDate")
    private LocalDateTime etdDate;

    @Column(name = "etaDate")
    private LocalDateTime etaDate;

    @Column(name = "operationType", length = 1)
    private String operationType;

    @Column(name = "status", length = 1)
    private String status;

    @Column(name = "statusLoad", length = 1)
    private String statusLoad;

    @Column(name = "statusDischarge", length = 1)
    private String statusDischarge;

    @Column(name = "agencyUp", length = 3)
    private String agencyUp;

    @Column(name = "previousLeg")
    private Short previousLeg;

    @Column(name = "vendorCode", length = 17)
    private String vendorCode;

    @Column(name = "vendorName", length = 50)
    private String vendorName;

    @Column(name = "loadAgencyId", length = 3)
    private String loadAgencyId;

    @Column(name = "dischargeAgencyId", length = 3)
    private String dischargeAgencyId;

    @Column(name = "statusApproved", length = 1)
    private String statusApproved;

    @Column(name = "loadType", length = 1)
    private String loadType;

    @Column(name = "dischType", length = 1)
    private String dischType;

    @Column(name = "carriercode", length = 3)
    private String carriercode;

    @Column(name = "preCarriage", length = 1)
    private String preCarriage;

    @Column(name = "onCarriage", length = 1)
    private String onCarriage;

    @Column(name = "userup", length = 15)
    private String userup;

    @Column(name = "dateup")
    private LocalDateTime dateup;

    @Column(name = "isedited", length = 1)
    private String isedited;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "version")
    private Short version;

    @Column(name = "groupcallid", length = 50)
    private String groupcallid;

    @Column(name = "routeid", length = 20)
    private String routeid;

    @Column(name = "dbound", length = 5)
    private String dbound;

    @Column(name = "polTerminal", length = 10)
    private String polTerminal;

    @Column(name = "podTerminal", length = 10)
    private String podTerminal;

    // ===== Constructors =====
    public EdiTempBookingLeg() {
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

    public Short getLegNo() {
        return legNo;
    }

    public void setLegNo(Short legNo) {
        this.legNo = legNo;
    }

    public String getBound() {
        return bound;
    }

    public void setBound(String bound) {
        this.bound = bound;
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

    public String getCallIdDischarge() {
        return callIdDischarge;
    }

    public void setCallIdDischarge(String callIdDischarge) {
        this.callIdDischarge = callIdDischarge;
    }

    public String getCallIdLoading() {
        return callIdLoading;
    }

    public void setCallIdLoading(String callIdLoading) {
        this.callIdLoading = callIdLoading;
    }

    public String getLegTypeId() {
        return legTypeId;
    }

    public void setLegTypeId(String legTypeId) {
        this.legTypeId = legTypeId;
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

    public LocalDateTime getEtdDate() {
        return etdDate;
    }

    public void setEtdDate(LocalDateTime etdDate) {
        this.etdDate = etdDate;
    }

    public LocalDateTime getEtaDate() {
        return etaDate;
    }

    public void setEtaDate(LocalDateTime etaDate) {
        this.etaDate = etaDate;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusLoad() {
        return statusLoad;
    }

    public void setStatusLoad(String statusLoad) {
        this.statusLoad = statusLoad;
    }

    public String getStatusDischarge() {
        return statusDischarge;
    }

    public void setStatusDischarge(String statusDischarge) {
        this.statusDischarge = statusDischarge;
    }

    public String getAgencyUp() {
        return agencyUp;
    }

    public void setAgencyUp(String agencyUp) {
        this.agencyUp = agencyUp;
    }

    public Short getPreviousLeg() {
        return previousLeg;
    }

    public void setPreviousLeg(Short previousLeg) {
        this.previousLeg = previousLeg;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getLoadAgencyId() {
        return loadAgencyId;
    }

    public void setLoadAgencyId(String loadAgencyId) {
        this.loadAgencyId = loadAgencyId;
    }

    public String getDischargeAgencyId() {
        return dischargeAgencyId;
    }

    public void setDischargeAgencyId(String dischargeAgencyId) {
        this.dischargeAgencyId = dischargeAgencyId;
    }

    public String getStatusApproved() {
        return statusApproved;
    }

    public void setStatusApproved(String statusApproved) {
        this.statusApproved = statusApproved;
    }

    public String getLoadType() {
        return loadType;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    public String getDischType() {
        return dischType;
    }

    public void setDischType(String dischType) {
        this.dischType = dischType;
    }

    public String getCarriercode() {
        return carriercode;
    }

    public void setCarriercode(String carriercode) {
        this.carriercode = carriercode;
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

    public Short getVersion() {
        return version;
    }

    public void setVersion(Short version) {
        this.version = version;
    }

    public String getGroupcallid() {
        return groupcallid;
    }

    public void setGroupcallid(String groupcallid) {
        this.groupcallid = groupcallid;
    }

    public String getRouteid() {
        return routeid;
    }

    public void setRouteid(String routeid) {
        this.routeid = routeid;
    }

    public String getDbound() {
        return dbound;
    }

    public void setDbound(String dbound) {
        this.dbound = dbound;
    }

    public String getPolTerminal() {
        return polTerminal;
    }

    public void setPolTerminal(String polTerminal) {
        this.polTerminal = polTerminal;
    }

    public String getPodTerminal() {
        return podTerminal;
    }

    public void setPodTerminal(String podTerminal) {
        this.podTerminal = podTerminal;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBookingLeg that = (EdiTempBookingLeg) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EdiTempBookingLeg{" +
                "id=" + id +
                ", bookNo='" + bookNo + '\'' +
                ", legNo=" + legNo +
                '}';
    }
}

