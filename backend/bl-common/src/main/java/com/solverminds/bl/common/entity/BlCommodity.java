package com.solverminds.bl.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Entity representing the bl_commodity table in Documentation database.
 * Bill of Lading commodity details.
 * Primary key: composite (bl_no, eqpId). bl_no is FK to bl.bl_no; (bl_no, eqpId) FK to bl_cargo.
 */
@Entity
@Table(name = "bl_commodity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(BlCommodityId.class)
public class BlCommodity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "bl_no", length = 17, nullable = false)
    private String blNo;

    @Id
    @Column(name = "eqpId", length = 11, nullable = false)
    private String eqpId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "bl_no", referencedColumnName = "bl_no", insertable = false, updatable = false),
        @JoinColumn(name = "eqpId", referencedColumnName = "eqpId", insertable = false, updatable = false)
    })
    private BlCargo blCargo;

    @Column(name = "cid")
    private Long cid;

    @Column(name = "cargoNo")
    private Integer cargoNo;

    @Column(name = "itemNo")
    private Integer itemNo;

    @Column(name = "comdtygrp", length = 20)
    private String commodityGroup;

    @Column(name = "commodityId", length = 10)
    private String commodityId;

    @Column(name = "packageId", length = 4)
    private String packageId;

    @Column(name = "marksNumbers", columnDefinition = "varchar(max)")
    private String marksNumbers;

    @Column(name = "marksDesc", columnDefinition = "varchar(max)")
    private String marksDesc;

    @Column(name = "itemQty")
    private Integer itemQty;

    @Column(name = "itemDesc", length = 255)
    private String itemDesc;

    @Column(name = "itemweight")
    private Double itemWeight;

    @Column(name = "itemWeightUnit", columnDefinition = "char(1)")
    private String itemWeightUnit;

    @Column(name = "itemVolume")
    private Double itemVolume;

    @Column(name = "itemVolumeUnit", columnDefinition = "char(1)")
    private String itemVolumeUnit;

    @Column(name = "itemleft")
    private Double itemLeft;

    @Column(name = "itemforward")
    private Double itemForward;

    @Column(name = "itemtop")
    private Double itemTop;

    @Column(name = "itemright")
    private Double itemRight;

    @Column(name = "itemaft")
    private Double itemAft;

    @Column(name = "itemlengthUnit", length = 5)
    private String itemLengthUnit;

    @Column(name = "hazardous", columnDefinition = "char(1)")
    private String hazardous;

    @Column(name = "imocontact", length = 50)
    private String imoContact;

    @Column(name = "imophone", length = 50)
    private String imoPhone;

    @Column(name = "remarks", length = 30)
    private String remarks;

    @Column(name = "carriercode", length = 3)
    private String carrierCode;

    @Column(name = "PRNo", length = 20)
    private String prNo;

    @Column(name = "finaldestination", length = 5)
    private String finalDestination;

    @Column(name = "nettwt")
    private Double netWeight;

    @Column(name = "netwtunit", columnDefinition = "char(1)")
    private String netWtUnit;

    @Column(name = "comsplititemno")
    private Integer compSplitItemNo;

    @Column(name = "istransrwdone", length = 1)
    private String isTransRwDone;

    @Column(name = "uniqueid", length = 60)
    private String uniqueId;

    @Column(name = "hsSuffixcode", length = 4)
    private String hsSuffixCode;

    @Column(name = "tier", length = 15)
    private String tier;

    @Column(name = "bay", length = 15)
    private String bay;

    @Column(name = "hatch", length = 15)
    private String hatch;

    @Column(name = "skuid", length = 20)
    private String skuId;

    @Column(name = "box")
    private Integer box;

    @Column(name = "vinno", length = 200)
    private String vinNo;

    @Column(name = "vinno1", length = 200)
    private String vinNo1;

    @Column(name = "bycode", length = 15)
    private String byCode;

    @Column(name = "byname", length = 150)
    private String byName;

    @Column(name = "slcode", length = 15)
    private String slCode;

    @Column(name = "slname", length = 150)
    private String slName;

    @Column(name = "cmdty_hblno", length = 20)
    private String cmdtyHblNo;

    @Column(name = "militarycargo", columnDefinition = "char(1)")
    private String militaryCargo;

    @Column(name = "alcohol", columnDefinition = "char(1)")
    private String alcohol;
}
