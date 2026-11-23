package com.solverminds.bl.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Entity representing the bl_header table in Documentation database.
 * Bill of Lading header information including party details.
 * Primary key: bl_no (FK to bl.bl_no).
 */
@Entity
@Table(name = "bl_header")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "bl_no", length = 17, nullable = false)
    private String blNo;

    @Column(name = "id")
    private Integer id;

    @Column(name = "book_no", length = 16)
    private String bookNo;

    @Column(name = "Service", length = 4)
    private String service;

    @Column(name = "shippername", length = 150)
    private String shipperName;

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

    @Column(name = "forwarderName", length = 100)
    private String forwarderName;

    @Column(name = "Forwarder", length = 500)
    private String forwarder;

    @Column(name = "precarriage", length = 1)
    private String preCarriage;

    @Column(name = "PreCarriage", length = 75)
    private String preCarriageDesc;

    @Column(name = "originofcountry", length = 50)
    private String originOfCountry;

    @Column(name = "PreCarriagePOR", length = 50)
    private String preCarriagePOR;

    @Column(name = "pol", length = 5)
    private String pol;

    @Column(name = "POL", length = 150)
    private String polDesc;

    @Column(name = "LoadVSL", length = 150)
    private String loadVsl;

    @Column(name = "pod", length = 5)
    private String pod;

    @Column(name = "POD", length = 150)
    private String podDesc;

    @Column(name = "DischargeVSL", length = 50)
    private String dischargeVsl;

    @Column(name = "origin", length = 5)
    private String origin;

    @Column(name = "Origin", length = 150)
    private String originDesc;

    @Column(name = "delivery", length = 5)
    private String delivery;

    @Column(name = "Delivery", length = 150)
    private String deliveryDesc;

    @Column(name = "Route", length = 255)
    private String route;

    @Column(name = "remarks", length = 255)
    private String remarks;

    @Column(name = "carrierCode", length = 3)
    private String carrierCode;

    @Column(name = "toorder", length = 3)
    private String toOrder;

    @Column(name = "sameasconsignee", columnDefinition = "char(1)")
    private String sameAsConsignee;

    @Column(name = "shipper_fontsize")
    private Integer shipperFontSize;

    @Column(name = "consignee_fontsize")
    private Integer consigneeFontSize;

    @Column(name = "notify_fontsize")
    private Integer notifyFontSize;

    @Column(name = "isDomRtgChecked", length = 1)
    private String isDomRtgChecked;

    @Column(name = "destagency", length = 1)
    private String destAgency;

    @Column(name = "proposedRouting", length = 250)
    private String proposedRouting;

    @Column(name = "precarriagemode", length = 10)
    private String preCarriageMode;

    @Column(name = "telex_release", columnDefinition = "char(1)")
    private String telexRelease;
}
