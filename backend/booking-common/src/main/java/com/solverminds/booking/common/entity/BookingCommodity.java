package com.solverminds.booking.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity representing the booking_commodity table.
 * To maintain booking related commodity details.
 */
@Entity
@Table(name = "booking_commodity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCommodity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Auto increment primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Long cid;

    /**
     * Booking Number
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_no", referencedColumnName = "book_no")
    private Booking booking;

    /**
     * Cargo number
     */
    @Column(name = "cargoNo", nullable = false)
    private Integer cargoNo;

    /**
     * Container Number
     */
    @Column(name = "eqpId", length = 11)
    private String eqpId;

    /**
     * Items Number or sequence number for a commodity in equipment
     */
    @Column(name = "itemNo")
    private Integer itemNo;

    /**
     * The commodity group for the container
     */
    @Column(name = "comdtygrp", length = 50, nullable = false)
    private String commodityGroup;

    /**
     * The child commodity for the container
     */
    @Column(name = "commodityId", length = 10)
    private String commodityId;

    /**
     * Package Identification for the container
     */
    @Column(name = "packageId", length = 4)
    private String packageId;

    /**
     * Remarks on commodities
     */
    @Column(name = "marksNumbers", columnDefinition = "varchar(max)")
    private String marksNumbers;

    /**
     * Description on the remarks of commodity
     */
    @Column(name = "marksDesc", columnDefinition = "varchar(max)")
    private String marksDesc;

    /**
     * No of items for the container
     */
    @Column(name = "itemQty", nullable = false)
    private Integer itemQty;

    /**
     * Items Description
     */
    @Column(name = "itemDesc", length = 256)
    private String itemDesc;

    /**
     * Weight of item
     */
    @Column(name = "itemWeight")
    private Double itemWeight;

    /**
     * Unit of weight
     */
    @Column(name = "itemWeightUnit", columnDefinition = "char(1)")
    private String itemWeightUnit;

    /**
     * Volume of item
     */
    @Column(name = "itemVolume")
    private Double itemVolume;

    /**
     * Unit of volume
     */
    @Column(name = "itemVolumeUnit", columnDefinition = "char(1)")
    private String itemVolumeUnit;

    /**
     * If OverSized equipment exists, then how much to be adjusted on left side
     */
    @Column(name = "itemLeft")
    private Double itemLeft;

    /**
     * If OverSized equipment exists, then how much to be adjusted on front side
     */
    @Column(name = "itemForward")
    private Double itemForward;

    /**
     * If OverSized equipment exists, then how much to be adjusted on top side
     */
    @Column(name = "itemTop")
    private Double itemTop;

    /**
     * If OverSized equipment exists, then how much to be adjusted on right side
     */
    @Column(name = "itemRight")
    private Double itemRight;

    /**
     * If OverSized equipment exists, then how much to be adjusted on aft side
     */
    @Column(name = "itemAft")
    private Double itemAft;

    /**
     * Unit of length
     */
    @Column(name = "itemLengthUnit", length = 5)
    private String itemLengthUnit;

    /**
     * Whether item is hazardous
     */
    @Column(name = "hazardous", columnDefinition = "char(1)", nullable = false)
    private String hazardous;

    /**
     * IMO DG contact
     */
    @Column(name = "imoContact", length = 50)
    private String imoContact;

    /**
     * Phone Number for IMO
     */
    @Column(name = "imoPhone", length = 25)
    private String imoPhone;

    /**
     * Carrier code
     */
    @Column(name = "carriercode", length = 3)
    private String carrierCode;

    /**
     * Whether item is alcohol
     */
    @Column(name = "Alcohol", columnDefinition = "char(1)", nullable = false)
    private String alcohol;

    /**
     * Whether item is military cargo
     */
    @Column(name = "militarycargo", columnDefinition = "char(1)", nullable = false)
    private String militaryCargo;

    /**
     * The given PR Number
     */
    @Column(name = "prNo", length = 20)
    private String prNo;

    /**
     * Not in use
     */
    @Column(name = "finaldestination", length = 5)
    private String finalDestination;

    /**
     * The net weight of the commodity
     */
    @Column(name = "nettwt")
    private Double netWeight;

    /**
     * The net unit of the weight
     */
    @Column(name = "netwtunit", columnDefinition = "char(1)")
    private String netWeightUnit;

    /**
     * Whether commodity details edited or not
     */
    @Column(name = "isedited", columnDefinition = "char(1)", nullable = false)
    private String isEdited;

    /**
     * Not in use
     */
    @Column(name = "Reworkblno", length = 20)
    private String reworkBlNo;

    /**
     * Not in use
     */
    @Column(name = "hsSuffixcode", length = 4)
    private String hsSuffixCode;

    /**
     * Not in use
     */
    @Column(name = "skuid", length = 20)
    private String skuId;

    /**
     * Not in use
     */
    @Column(name = "box")
    private Integer box;

    /**
     * Not in use
     */
    @Column(name = "tier", length = 15)
    private String tier;

    /**
     * Not in use
     */
    @Column(name = "bay", length = 15)
    private String bay;

    /**
     * Not in use
     */
    @Column(name = "hatch", length = 15)
    private String hatch;

    /**
     * To store the storage information of VIN no
     */
    @Column(name = "vinno", length = 200)
    private String vinNo;

    /**
     * To store the storage information of VIN no1
     */
    @Column(name = "vinno1", length = 200)
    private String vinNo1;

    /**
     * Buyer code
     */
    @Column(name = "bycode", length = 15)
    private String byCode;

    /**
     * Buyer name
     */
    @Column(name = "byname", length = 150)
    private String byName;

    /**
     * Seller code
     */
    @Column(name = "slcode", length = 15)
    private String slCode;

    /**
     * Seller name
     */
    @Column(name = "slname", length = 150)
    private String slName;

    /**
     * Commodity HBL number
     */
    @Column(name = "cmdty_hblno", length = 20)
    private String commodityHblNo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingCommodity that = (BookingCommodity) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "BookingCommodity{" +
                "cid=" + cid +
                ", commodityGroup='" + commodityGroup + '\'' +
                '}';
    }
}

