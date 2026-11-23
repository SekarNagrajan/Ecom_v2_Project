package com.solverminds.booking.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Route/Leg entity for booking legs
 * Table: ecombooking_leg
 * Composite PK: ebookno, leg_no
 */
@Entity
@Table(name = "ecombooking_leg", schema = "dbo")
public class EcomBookingRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "ebookno", length = 20, nullable = false)
    private String ebookno;

    @Column(name = "leg_no", nullable = false)
    private Short legNo;

    @Column(name = "bound", length = 5)
    private String bound;

    @Column(name = "serviceCode", length = 4)
    private String serviceCode;

    @Column(name = "vesselCode", length = 6)
    private String vesselCode;

    @Column(name = "voyageCode", length = 6)
    private String voyageCode;

    @Column(name = "legTypeId", length = 1)
    private String legTypeId;

    @Column(name = "loadPortId", length = 5)
    private String loadPortId;

    @Column(name = "loadTerminal", length = 10)
    private String loadTerminal;

    @Column(name = "dischargePortId", length = 5)
    private String dischargePortId;

    @Column(name = "dischargeTerminal", length = 10)
    private String dischargeTerminal;

    @Column(name = "etdDate")
    private Timestamp etdDate;

    @Column(name = "etaDate")
    private Timestamp etaDate;

    @Column(name = "loadType", length = 1)
    private String loadType;

    @Column(name = "dischType", length = 1)
    private String dischType;

    @Column(name = "groupcallid", length = 50)
    private String groupcallid;

    @Column(name = "callIdDischarge", length = 50)
    private String callIdDischarge;

    @Column(name = "callIdLoading", length = 50)
    private String callIdLoading;

    @Column(name = "dbound", length = 5)
    private String dbound;

    public EcomBookingRoute() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEbookno() {
        return ebookno;
    }

    public void setEbookno(String ebookno) {
        this.ebookno = ebookno;
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

    public String getLoadTerminal() {
        return loadTerminal;
    }

    public void setLoadTerminal(String loadTerminal) {
        this.loadTerminal = loadTerminal;
    }

    public String getDischargePortId() {
        return dischargePortId;
    }

    public void setDischargePortId(String dischargePortId) {
        this.dischargePortId = dischargePortId;
    }

    public String getDischargeTerminal() {
        return dischargeTerminal;
    }

    public void setDischargeTerminal(String dischargeTerminal) {
        this.dischargeTerminal = dischargeTerminal;
    }

    public Timestamp getEtdDate() {
        return etdDate;
    }

    public void setEtdDate(Timestamp etdDate) {
        this.etdDate = etdDate;
    }

    public Timestamp getEtaDate() {
        return etaDate;
    }

    public void setEtaDate(Timestamp etaDate) {
        this.etaDate = etaDate;
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

    public String getGroupcallid() {
        return groupcallid;
    }

    public void setGroupcallid(String groupcallid) {
        this.groupcallid = groupcallid;
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

    public String getDbound() {
        return dbound;
    }

    public void setDbound(String dbound) {
        this.dbound = dbound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomBookingRoute)) return false;
        EcomBookingRoute that = (EcomBookingRoute) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EcomBookingRoute{" +
                "id=" + id +
                ", bound='" + bound + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", vesselCode='" + vesselCode + '\'' +
                ", voyageCode='" + voyageCode + '\'' +
                '}';
    }
}
