package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_header", schema = "dbo")
public class EdiTempBookingHeader implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "Service", length = 4)
    private String service;

    @Column(name = "shippername", length = 150)
    private String shippername;

    @Column(name = "Shipper", length = 500)
    private String shipper;

    @Column(name = "ExportRef", length = 500)
    private String exportRef;

    @Column(name = "consigneeName", length = 150)
    private String consigneeName;

    @Column(name = "Consignee", length = 500)
    private String consignee;

    @Column(name = "notifierName", length = 150)
    private String notifierName;

    @Column(name = "Notify", length = 500)
    private String notify;

    @Column(name = "forwarderName", length = 150)
    private String forwarderName;

    @Column(name = "Forwarder", length = 500)
    private String forwarder;

    @Column(name = "toorder", length = 3)
    private String toorder;

    @Column(name = "sameasconsignee", length = 1)
    private String sameasconsignee;

    @Column(name = "PreCarriage", length = 75)
    private String preCarriage;

    @Column(name = "originofcountry", length = 50)
    private String originofcountry;

    @Column(name = "PreCarriagePOR", length = 50)
    private String preCarriagePOR;

    @Column(name = "POL", length = 150)
    private String pol;

    @Column(name = "LoadVSL", length = 150)
    private String loadVSL;

    @Column(name = "POD", length = 150)
    private String pod;

    @Column(name = "DischargeVSL", length = 50)
    private String dischargeVSL;

    @Column(name = "Origin", length = 150)
    private String origin;

    @Column(name = "Delivery", length = 150)
    private String delivery;

    @Column(name = "Route", length = 255)
    private String route;

    @Column(name = "remarks", length = 255)
    private String remarks;

    @Column(name = "carrierCode", length = 3)
    private String carrierCode;

    @Column(name = "shipper_fontsize")
    private Integer shipperFontsize;

    @Column(name = "consignee_fontsize")
    private Integer consigneeFontsize;

    @Column(name = "notify_fontsize")
    private Integer notifyFontsize;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "edicleanstatus", length = 1)
    private String edicleanstatus;

    @Column(name = "version")
    private Short version;

    // ===== Constructors =====
    public EdiTempBookingHeader() {
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

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getShippername() {
        return shippername;
    }

    public void setShippername(String shippername) {
        this.shippername = shippername;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public String getExportRef() {
        return exportRef;
    }

    public void setExportRef(String exportRef) {
        this.exportRef = exportRef;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getNotifierName() {
        return notifierName;
    }

    public void setNotifierName(String notifierName) {
        this.notifierName = notifierName;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public String getForwarderName() {
        return forwarderName;
    }

    public void setForwarderName(String forwarderName) {
        this.forwarderName = forwarderName;
    }

    public String getForwarder() {
        return forwarder;
    }

    public void setForwarder(String forwarder) {
        this.forwarder = forwarder;
    }

    public String getToorder() {
        return toorder;
    }

    public void setToorder(String toorder) {
        this.toorder = toorder;
    }

    public String getSameasconsignee() {
        return sameasconsignee;
    }

    public void setSameasconsignee(String sameasconsignee) {
        this.sameasconsignee = sameasconsignee;
    }

    public String getPreCarriage() {
        return preCarriage;
    }

    public void setPreCarriage(String preCarriage) {
        this.preCarriage = preCarriage;
    }

    public String getOriginofcountry() {
        return originofcountry;
    }

    public void setOriginofcountry(String originofcountry) {
        this.originofcountry = originofcountry;
    }

    public String getPreCarriagePOR() {
        return preCarriagePOR;
    }

    public void setPreCarriagePOR(String preCarriagePOR) {
        this.preCarriagePOR = preCarriagePOR;
    }

    public String getPol() {
        return pol;
    }

    public void setPol(String pol) {
        this.pol = pol;
    }

    public String getLoadVSL() {
        return loadVSL;
    }

    public void setLoadVSL(String loadVSL) {
        this.loadVSL = loadVSL;
    }

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }

    public String getDischargeVSL() {
        return dischargeVSL;
    }

    public void setDischargeVSL(String dischargeVSL) {
        this.dischargeVSL = dischargeVSL;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public Integer getShipperFontsize() {
        return shipperFontsize;
    }

    public void setShipperFontsize(Integer shipperFontsize) {
        this.shipperFontsize = shipperFontsize;
    }

    public Integer getConsigneeFontsize() {
        return consigneeFontsize;
    }

    public void setConsigneeFontsize(Integer consigneeFontsize) {
        this.consigneeFontsize = consigneeFontsize;
    }

    public Integer getNotifyFontsize() {
        return notifyFontsize;
    }

    public void setNotifyFontsize(Integer notifyFontsize) {
        this.notifyFontsize = notifyFontsize;
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

    public Short getVersion() {
        return version;
    }

    public void setVersion(Short version) {
        this.version = version;
    }

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBookingHeader that = (EdiTempBookingHeader) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EdiTempBookingHeader{" +
                "id=" + id +
                ", bookNo='" + bookNo + '\'' +
                '}';
    }
}

