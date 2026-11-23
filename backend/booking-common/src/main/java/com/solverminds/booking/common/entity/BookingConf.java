package com.solverminds.booking.common.entity;

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
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing the booking_conf table.
 * To maintain data related booking confirmation tab of bookings.
 */
@Entity
@Table(name = "booking_conf")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingConf implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Booking number (Primary Key and Foreign Key to booking table)
     */
    @Id
    @Column(name = "book_no", length = 17, nullable = false)
    private String bookNo;

    /**
     * The name of the shipper
     */
    @Column(name = "shipperName", length = 200)
    private String shipperName;

    /**
     * The address of the Shipper
     */
    @Column(name = "shipperAddress", length = 500)
    private String shipperAddress;

    /**
     * The Shipper Reference number
     */
    @Column(name = "shipperReference", length = 20)
    private String shipperReference;

    /**
     * The forwarder name
     */
    @Column(name = "fwdName", length = 200)
    private String fwdName;

    /**
     * The forwarder address
     */
    @Column(name = "fwdAddress", length = 500)
    private String fwdAddress;

    /**
     * The Reference No for the Forwarder
     */
    @Column(name = "fwdReference", length = 20)
    private String fwdReference;

    /**
     * Empty pickup details
     */
    @Column(name = "emptyPickup", length = 500)
    private String emptyPickup;

    /**
     * The delivery details
     */
    @Column(name = "delivery", length = 500)
    private String delivery;

    /**
     * The remarks
     */
    @Column(name = "remarks", length = 500)
    private String remarks;

    /**
     * No of free days at the loading port
     */
    @Column(name = "loadPortfreeDays")
    private Integer loadPortFreeDays;

    /**
     * User who has created
     */
    @Column(name = "usercr", length = 15)
    private String usercr;

    /**
     * Date of creation
     */
    @Column(name = "datecr")
    private LocalDateTime datecr;

    /**
     * User who has updated
     */
    @Column(name = "userup", length = 15)
    private String userup;

    /**
     * Date of updation
     */
    @Column(name = "dateup")
    private LocalDateTime dateup;

    /**
     * The mode of transportation
     */
    @Column(name = "mode", columnDefinition = "char(1)")
    private String mode;

    /**
     * The address of the stuffing place
     */
    @Column(name = "stuffpoint", length = 500)
    private String stuffPoint;

    /**
     * Container Release terminal
     */
    @Column(name = "releaseterminal", length = 10)
    private String releaseTerminal;

    /**
     * Container acceptance terminal (POD)
     */
    @Column(name = "acceptanceterminal", length = 10)
    private String acceptanceTerminal;

    /**
     * Optional Port of delivery
     */
    @Column(name = "optionalpod", length = 5)
    private String optionalPod;

    /**
     * Shipper cust code
     */
    @Column(name = "SHcustcode", length = 20)
    private String shCustCode;

    /**
     * Forwarder customer code
     */
    @Column(name = "FWcustcode", length = 20)
    private String fwCustCode;

    /**
     * To show whether booking bound in print
     */
    @Column(name = "showbound", columnDefinition = "char(1)")
    private String showBound;

    /**
     * To store operation cro remarks in booking print
     */
    @Column(name = "opscroremarks", length = 500)
    private String opsCroRemarks;

    /**
     * To store pickup address
     */
    @Column(name = "chasispickup", length = 10)
    private String chasisPickup;

    /**
     * To store destination address
     */
    @Column(name = "chasisdropoff", length = 10)
    private String chasisDropoff;

    /**
     * Not in use
     */
    @Column(name = "sg_to", length = 50)
    private String sgTo;

    /**
     * Not in use
     */
    @Column(name = "sg_from", length = 50)
    private String sgFrom;

    /**
     * Not in use
     */
    @Column(name = "sg_attn", length = 50)
    private String sgAttn;

    /**
     * Not in use
     */
    @Column(name = "sg_tel", length = 50)
    private String sgTel;

    /**
     * Not in use
     */
    @Column(name = "sg_fax", length = 50)
    private String sgFax;

    /**
     * Not in use
     */
    @Column(name = "sg_email", length = 50)
    private String sgEmail;

    /**
     * Not in use
     */
    @Column(name = "sg_cc", length = 50)
    private String sgCc;

    /**
     * Not in use
     */
    @Column(name = "sg_closingtime")
    private LocalDateTime sgClosingTime;

    /**
     * Not in use
     */
    @Column(name = "sg_scncode", length = 15)
    private String sgScnCode;

    /**
     * Not in use
     */
    @Column(name = "sg_vesselid", length = 25)
    private String sgVesselId;

    /**
     * Not in use
     */
    @Column(name = "sg_agentcode", length = 25)
    private String sgAgentCode;

    /**
     * Not in use
     */
    @Column(name = "sg_mainvsl", length = 15)
    private String sgMainVsl;

    /**
     * Not in use
     */
    @Column(name = "sg_tos", length = 15)
    private String sgTos;

    /**
     * Not in use
     */
    @Column(name = "sg_commodity", length = 200)
    private String sgCommodity;

    /**
     * Not in use
     */
    @Column(name = "TripType", length = 2)
    private String tripType;

    /**
     * Not in use
     */
    @Column(name = "PickupDate")
    private LocalDateTime pickupDate;

    /**
     * Not in use
     */
    @Column(name = "RtnDate")
    private LocalDateTime rtnDate;

    /**
     * To Show the booking charge in Jasper
     */
    @Column(name = "printcharges", columnDefinition = "char(1)")
    private String printCharges;

    /**
     * Not in use
     */
    @Column(name = "bkgCutoff")
    private LocalDateTime bkgCutoff;

    /**
     * Not in use
     */
    @Column(name = "docCutoff")
    private LocalDateTime docCutoff;

    /**
     * Not in use
     */
    @Column(name = "vgmCutoff")
    private LocalDateTime vgmCutoff;

    /**
     * Not in use
     */
    @Column(name = "bbroroCutoff")
    private LocalDateTime bbroroCutoff;

    /**
     * Not in use
     */
    @Column(name = "hazCutoff")
    private LocalDateTime hazCutoff;

    /**
     * Not in use
     */
    @Column(name = "portentryCutoff")
    private LocalDateTime portEntryCutoff;

    /**
     * Not in use
     */
    @Column(name = "cutoff_timezone", columnDefinition = "char(1)")
    private String cutoffTimezone;

    /**
     * Not in use
     */
    @Column(name = "partner_vessel", length = 500)
    private String partnerVessel;

    /**
     * To store release remarks in booking print
     */
    @Column(name = "release_remarks", length = 500)
    private String releaseRemarks;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingConf that = (BookingConf) o;
        return Objects.equals(bookNo, that.bookNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookNo);
    }

    @Override
    public String toString() {
        return "BookingConf{" +
                "bookNo='" + bookNo + '\'' +
                '}';
    }
}

