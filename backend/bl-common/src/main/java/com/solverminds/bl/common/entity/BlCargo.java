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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Entity representing the bl_cargo table in Documentation database.
 * Bill of Lading container/cargo details.
 * Primary key: composite (bl_no, eqpId). bl_no is FK to bl.bl_no.
 */
@Entity
@Table(name = "bl_cargo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(BlCargoId.class)
public class BlCargo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "bl_no", length = 17, nullable = false)
    private String blNo;

    @Id
    @Column(name = "eqpId", length = 11, nullable = false)
    private String eqpId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bl_no", referencedColumnName = "bl_no", insertable = false, updatable = false)
    private Bl bl;

    @Column(name = "cid")
    private Long cid;

    @Column(name = "cargoNo")
    private Integer cargoNo;

    @Column(name = "eqpstatus", columnDefinition = "char(1)")
    private String eqpStatus;

    @Column(name = "oog", columnDefinition = "char(1)")
    private String oog;

    @Column(name = "eqptypeid", length = 4)
    private String eqpTypeId;

    @Column(name = "type", columnDefinition = "char(1)")
    private String type;

    @Column(name = "shipperown", columnDefinition = "char(1)")
    private String shipperOwn;

    @Column(name = "seal1No", length = 17)
    private String seal1No;

    @Column(name = "seal2No", length = 40)
    private String seal2No;

    @Column(name = "nor", columnDefinition = "char(1)")
    private String nor;

    @Column(name = "tempMax")
    private Double tempMax;

    @Column(name = "tempMin")
    private Double tempMin;

    @Column(name = "setTemp")
    private Double setTemp;

    @Column(name = "tempunit", length = 1)
    private String tempUnit;

    @Column(name = "tareweight")
    private Double tareWeight;

    @Column(name = "cargoWeight")
    private Double cargoWeight;

    @Column(name = "grossWeight")
    private Double grossWeight;

    @Column(name = "weightUnit", length = 1)
    private String weightUnit;

    @Column(name = "ventSetting")
    private Integer ventSetting;

    @Column(name = "VentPercentage", length = 5)
    private String ventPercentage;

    @Column(name = "chassisId", length = 11)
    private String chassisId;

    @Column(name = "gensetId", length = 11)
    private String gensetId;

    @Column(name = "eqpVolume")
    private Double eqpVolume;

    @Column(name = "eqpVolumeUnit", length = 5)
    private String eqpVolumeUnit;

    @Column(name = "otherseals", length = 50)
    private String otherSeals;

    @Column(name = "roundtrip", columnDefinition = "char(1)")
    private String roundTrip;

    @Column(name = "status", length = 2)
    private String status;

    @Column(name = "orderno", length = 25)
    private String orderNo;

    @Column(name = "raterefno", length = 50)
    private String rateRefNo;

    @Column(name = "ratetype", length = 5)
    private String rateType;

    @Column(name = "dgflag", columnDefinition = "char(1)")
    private String dgFlag;

    @Column(name = "rate_eqpTypeid", length = 4)
    private String rateEqpTypeId;

    @Column(name = "humidity", length = 5)
    private String humidity;

    @Column(name = "humidity_flag")
    private Boolean humidityFlag;

    @Column(name = "drains", length = 1)
    private String drains;

    @Column(name = "co2value", length = 10)
    private String co2Value;

    @Column(name = "o2value", length = 10)
    private String o2Value;

    @Column(name = "uniqueid", length = 60)
    private String uniqueId;
}
