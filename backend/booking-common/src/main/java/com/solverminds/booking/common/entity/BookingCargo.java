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
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing the booking_cargo table.
 * To maintain booking container details.
 */
@Entity
@Table(name = "booking_cargo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCargo implements Serializable {

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
     * Container number order (Serial number)
     */
    @Column(name = "cargoNo")
    private Integer cargoNo;

    /**
     * Equipment status (Empty-M/LADEN-F)
     */
    @Column(name = "eqpStatus", columnDefinition = "char(1)", nullable = false)
    private String eqpStatus;

    /**
     * REEFER Cargo
     */
    @Column(name = "type", columnDefinition = "char(1)", nullable = false)
    private String type;

    /**
     * Oversize cargo (Y/N)
     */
    @Column(name = "oog", columnDefinition = "char(1)", nullable = false)
    private String oog;

    /**
     * ID represents the Container equipment type
     */
    @Column(name = "eqpTypeId", length = 4)
    private String eqpTypeId;

    /**
     * Booking Container Number
     */
    @Column(name = "eqpId", length = 11)
    private String eqpId;

    /**
     * Container owned in Shipper (Y/N)
     */
    @Column(name = "shipperOwn", columnDefinition = "char(1)", nullable = false)
    private String shipperOwn;

    /**
     * Not in use
     */
    @Column(name = "roundTrip", columnDefinition = "char(1)", nullable = false)
    private String roundTrip;

    /**
     * Shipper Seal
     */
    @Column(name = "seal1No", length = 17)
    private String seal1No;

    /**
     * Carrier seal
     */
    @Column(name = "seal2No", length = 40)
    private String seal2No;

    /**
     * The other seals
     */
    @Column(name = "otherSeal", length = 50)
    private String otherSeal;

    /**
     * Non Operator REEFER
     */
    @Column(name = "nor", columnDefinition = "char(1)", nullable = false)
    private String nor;

    /**
     * Maximum Temperature
     */
    @Column(name = "tempMax")
    private Double tempMax;

    /**
     * Minimum temperature
     */
    @Column(name = "tempMin")
    private Double tempMin;

    /**
     * Maintain defined temperature
     */
    @Column(name = "setTemp")
    private Double setTemp;

    /**
     * Units of Temp SPECIFICATION
     */
    @Column(name = "tempUnit", columnDefinition = "char(1)")
    private String tempUnit;

    /**
     * Container Tare Weight
     */
    @Column(name = "tareWeight")
    private Double tareWeight;

    /**
     * Container Cargo Weight
     */
    @Column(name = "cargoWeight")
    private Double cargoWeight;

    /**
     * Container Gross Weight (container+commodity)
     */
    @Column(name = "grossWeight")
    private Double grossWeight;

    /**
     * Unit of weight (Kilograms/Tonnes)
     */
    @Column(name = "weightUnit", columnDefinition = "char(1)", nullable = false)
    private String weightUnit;

    /**
     * Ventilation Settings
     */
    @Column(name = "ventSetting", nullable = false)
    private Integer ventSetting;

    /**
     * Position Of Container (eg: above DECK)
     */
    @Column(name = "containerPosition", columnDefinition = "char(2)")
    private String containerPosition;

    /**
     * Load condition (whether full or half)
     */
    @Column(name = "loadingCondition", columnDefinition = "char(2)", nullable = false)
    private String loadingCondition;

    /**
     * Container Specification ID
     */
    @Column(name = "chassisId", length = 11)
    private String chassisId;

    /**
     * Id of Genset on REEFER Container
     */
    @Column(name = "gensetId", length = 11)
    private String gensetId;

    /**
     * Equipment Volume
     */
    @Column(name = "eqpVolume")
    private Double eqpVolume;

    /**
     * Unit of volume
     */
    @Column(name = "eqpVolumeUnit", columnDefinition = "char(1)")
    private String eqpVolumeUnit;

    /**
     * Carrier code
     */
    @Column(name = "carriercode", length = 3, nullable = false)
    private String carrierCode;

    /**
     * Operation status for the container load planning status (o-not planned, p-Planned)
     */
    @Column(name = "actStatus", columnDefinition = "char(1)", nullable = false)
    private String actStatus;

    /**
     * Rate agreed item number
     */
    @Column(name = "orderno", length = 50)
    private String orderNo;

    /**
     * IF the container not in Profile then Dummy (Y-Dummy, N-Actual container)
     */
    @Column(name = "isDummy", columnDefinition = "char(1)", nullable = false)
    private String isDummy;

    /**
     * Booking agreed the rate Reference number (eg: SRR number, CMS)
     */
    @Column(name = "raterefno", length = 50)
    private String rateRefNo;

    /**
     * The type of Rate request (Eg: SRR, CMS, TAF, .)
     */
    @Column(name = "ratetype", length = 5)
    private String rateType;

    /**
     * Whether the cargo is Dangerous cargo
     */
    @Column(name = "dgflag", columnDefinition = "char(1)")
    private String dgFlag;

    /**
     * Container status
     */
    @Column(name = "status", columnDefinition = "char(2)")
    private String status;

    /**
     * Not in use
     */
    @Column(name = "remarks", length = 1000)
    private String remarks;

    /**
     * Whether cargo details edited in booking doc or not
     */
    @Column(name = "isedited", columnDefinition = "char(1)", nullable = false)
    private String isEdited;

    /**
     * OOG container specification
     */
    @Column(name = "slotloss")
    private Integer slotLoss;

    /**
     * To identity easy reference of rate
     */
    @Column(name = "raterule", length = 3)
    private String rateRule;

    /**
     * Not in use
     */
    @Column(name = "SHcustcode", length = 20)
    private String shCustCode;

    /**
     * Not in use
     */
    @Column(name = "FWcustcode", length = 20)
    private String fwCustCode;

    /**
     * To store container's information or update
     */
    @Column(name = "movetype", length = 10)
    private String moveType;

    /**
     * Not in use
     */
    @Column(name = "payIndicator", columnDefinition = "char(1)")
    private String payIndicator;

    /**
     * Not in use
     */
    @Column(name = "GateClearance", columnDefinition = "char(1)")
    private String gateClearance;

    /**
     * Not in use
     */
    @Column(name = "GateInClearanceDate")
    private LocalDateTime gateInClearanceDate;

    /**
     * Not in use
     */
    @Column(name = "GateDocDate")
    private LocalDateTime gateDocDate;

    /**
     * Not in use
     */
    @Column(name = "GateReferenceRemarks", length = 300)
    private String gateReferenceRemarks;

    /**
     * Not in use
     */
    @Column(name = "GateClearanceMode", columnDefinition = "char(1)")
    private String gateClearanceMode;

    /**
     * Not in use
     */
    @Column(name = "GateRefRemarks1", length = 300)
    private String gateRefRemarks1;

    /**
     * Not in use
     */
    @Column(name = "GateRefRemarks2", length = 300)
    private String gateRefRemarks2;

    /**
     * To store the ventilation value
     */
    @Column(name = "VentPercentage", length = 5)
    private String ventPercentage;

    /**
     * Not in use
     */
    @Column(name = "dummyeqpid", length = 12)
    private String dummyEqpId;

    /**
     * Not in use
     */
    @Column(name = "oneway", columnDefinition = "char(1)")
    private String oneWay;

    /**
     * To store containerized or non containerized
     */
    @Column(name = "containerized", columnDefinition = "char(1)")
    private String containerized;

    /**
     * To store theoretical slot Unit
     */
    @Column(name = "slotunit", length = 5)
    private String slotUnit;

    /**
     * To store theoretical slot Value
     */
    @Column(name = "slotvalue")
    private Integer slotValue;

    /**
     * To store the humidity value
     */
    @Column(name = "Humidity", length = 5)
    private String humidity;

    /**
     * To store the humidity flag
     */
    @Column(name = "Humidity_flag")
    private Boolean humidityFlag;

    /**
     * To save the drain details
     */
    @Column(name = "Drains", columnDefinition = "char(1)")
    private String drains;

    /**
     * Not in use
     */
    @Column(name = "CO2Value", length = 10)
    private String co2Value;

    /**
     * Not in use
     */
    @Column(name = "O2Value", length = 10)
    private String o2Value;

    /**
     * Easy identify rates in booking and commercial rates amendment number
     */
    @Column(name = "amendmentNo", length = 5)
    private String amendmentNo;

    /**
     * Equipment substitute
     */
    @Column(name = "rate_eqpTypeid", length = 4)
    private String rateEqpTypeId;

    /**
     * To save Rate nor
     */
    @Column(name = "rate_nor", columnDefinition = "char(1)")
    private String rateNor;

    /**
     * Not in use
     */
    @Column(name = "expfree", length = 20)
    private String expFree;

    /**
     * Not in use
     */
    @Column(name = "impfree", length = 20)
    private String impFree;

    /**
     * To store cargograde data based on the master table data
     */
    @Column(name = "cargograde", length = 50)
    private String cargoGrade;

    /**
     * Not in use
     */
    @Column(name = "impstatus", columnDefinition = "char(1)")
    private String impStatus;

    /**
     * Not in use
     */
    @Column(name = "expstatus", columnDefinition = "char(1)")
    private String expStatus;

    /**
     * To store user input in check box
     */
    @Column(name = "oogaccept", columnDefinition = "char(1)")
    private String oogAccept;

    /**
     * Not in use
     */
    @Column(name = "caunit", columnDefinition = "char(1)")
    private String caUnit;

    /**
     * Not in use
     */
    @Column(name = "pickupDate")
    private LocalDateTime pickupDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingCargo that = (BookingCargo) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "BookingCargo{" +
                "cid=" + cid +
                ", eqpId='" + eqpId + '\'' +
                '}';
    }
}

