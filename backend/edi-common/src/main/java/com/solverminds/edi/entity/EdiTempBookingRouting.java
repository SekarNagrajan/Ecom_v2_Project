package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_routing", schema = "dbo")
public class EdiTempBookingRouting implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "origin", length = 5)
    private String origin;

    @Column(name = "originMode", length = 50)
    private String originMode;

    @Column(name = "pol", length = 5)
    private String pol;

    @Column(name = "polter", length = 10)
    private String polter;

    @Column(name = "polSer", length = 4)
    private String polSer;

    @Column(name = "polmode", length = 5)
    private String polmode;

    @Column(name = "trans1", length = 5)
    private String trans1;

    @Column(name = "trans1Ter", length = 10)
    private String trans1Ter;

    @Column(name = "trans1Ser", length = 4)
    private String trans1Ser;

    @Column(name = "trans1mode", length = 5)
    private String trans1mode;

    @Column(name = "trans2", length = 5)
    private String trans2;

    @Column(name = "trans2Ter", length = 10)
    private String trans2Ter;

    @Column(name = "trans2Ser", length = 4)
    private String trans2Ser;

    @Column(name = "trans2mode", length = 5)
    private String trans2mode;

    @Column(name = "trans3", length = 5)
    private String trans3;

    @Column(name = "trans3Ter", length = 10)
    private String trans3Ter;

    @Column(name = "trans3Ser", length = 4)
    private String trans3Ser;

    @Column(name = "trans3mode", length = 5)
    private String trans3mode;

    @Column(name = "pod", length = 5)
    private String pod;

    @Column(name = "podTer", length = 10)
    private String podTer;

    @Column(name = "podMode", length = 50)
    private String podMode;

    @Column(name = "delivery", length = 5)
    private String delivery;

    @Column(name = "svcCode", length = 4)
    private String svcCode;

    @Column(name = "defRout", length = 5)
    private String defRout;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "version")
    private Short version;

    @Column(name = "opsTrans1Mde", length = 20)
    private String opsTrans1Mde;

    @Column(name = "opsTrans2Mde", length = 20)
    private String opsTrans2Mde;

    @Column(name = "routeid")
    private Integer routeid;

    // ===== Constructors =====
    public EdiTempBookingRouting() {
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

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOriginMode() {
        return originMode;
    }

    public void setOriginMode(String originMode) {
        this.originMode = originMode;
    }

    public String getPol() {
        return pol;
    }

    public void setPol(String pol) {
        this.pol = pol;
    }

    public String getPolter() {
        return polter;
    }

    public void setPolter(String polter) {
        this.polter = polter;
    }

    public String getPolSer() {
        return polSer;
    }

    public void setPolSer(String polSer) {
        this.polSer = polSer;
    }

    public String getPolmode() {
        return polmode;
    }

    public void setPolmode(String polmode) {
        this.polmode = polmode;
    }

    public String getTrans1() {
        return trans1;
    }

    public void setTrans1(String trans1) {
        this.trans1 = trans1;
    }

    public String getTrans1Ter() {
        return trans1Ter;
    }

    public void setTrans1Ter(String trans1Ter) {
        this.trans1Ter = trans1Ter;
    }

    public String getTrans1Ser() {
        return trans1Ser;
    }

    public void setTrans1Ser(String trans1Ser) {
        this.trans1Ser = trans1Ser;
    }

    public String getTrans1mode() {
        return trans1mode;
    }

    public void setTrans1mode(String trans1mode) {
        this.trans1mode = trans1mode;
    }

    public String getTrans2() {
        return trans2;
    }

    public void setTrans2(String trans2) {
        this.trans2 = trans2;
    }

    public String getTrans2Ter() {
        return trans2Ter;
    }

    public void setTrans2Ter(String trans2Ter) {
        this.trans2Ter = trans2Ter;
    }

    public String getTrans2Ser() {
        return trans2Ser;
    }

    public void setTrans2Ser(String trans2Ser) {
        this.trans2Ser = trans2Ser;
    }

    public String getTrans2mode() {
        return trans2mode;
    }

    public void setTrans2mode(String trans2mode) {
        this.trans2mode = trans2mode;
    }

    public String getTrans3() {
        return trans3;
    }

    public void setTrans3(String trans3) {
        this.trans3 = trans3;
    }

    public String getTrans3Ter() {
        return trans3Ter;
    }

    public void setTrans3Ter(String trans3Ter) {
        this.trans3Ter = trans3Ter;
    }

    public String getTrans3Ser() {
        return trans3Ser;
    }

    public void setTrans3Ser(String trans3Ser) {
        this.trans3Ser = trans3Ser;
    }

    public String getTrans3mode() {
        return trans3mode;
    }

    public void setTrans3mode(String trans3mode) {
        this.trans3mode = trans3mode;
    }

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }

    public String getPodTer() {
        return podTer;
    }

    public void setPodTer(String podTer) {
        this.podTer = podTer;
    }

    public String getPodMode() {
        return podMode;
    }

    public void setPodMode(String podMode) {
        this.podMode = podMode;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getSvcCode() {
        return svcCode;
    }

    public void setSvcCode(String svcCode) {
        this.svcCode = svcCode;
    }

    public String getDefRout() {
        return defRout;
    }

    public void setDefRout(String defRout) {
        this.defRout = defRout;
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

    public String getOpsTrans1Mde() {
        return opsTrans1Mde;
    }

    public void setOpsTrans1Mde(String opsTrans1Mde) {
        this.opsTrans1Mde = opsTrans1Mde;
    }

    public String getOpsTrans2Mde() {
        return opsTrans2Mde;
    }

    public void setOpsTrans2Mde(String opsTrans2Mde) {
        this.opsTrans2Mde = opsTrans2Mde;
    }

    public Integer getRouteid() {
        return routeid;
    }

    public void setRouteid(Integer routeid) {
        this.routeid = routeid;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBookingRouting that = (EdiTempBookingRouting) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EdiTempBookingRouting{" +
                "id=" + id +
                ", bookNo='" + bookNo + '\'' +
                ", origin='" + origin + '\'' +
                ", pol='" + pol + '\'' +
                ", pod='" + pod + '\'' +
                '}';
    }
}

