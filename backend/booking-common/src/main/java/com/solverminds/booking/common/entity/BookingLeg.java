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
 * Entity representing the booking_leg table.
 * To maintain booking related connecting voyages details.
 */
@Entity
@Table(name = "booking_leg")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingLeg implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Auto increment primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Booking Number uniquely identifies location code with unique serial numbers
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_no", referencedColumnName = "book_no")
    private Booking booking;

    /**
     * Leg number Or route number (may starting or intermediate or ending)
     */
    @Column(name = "leg_no", nullable = false)
    private Short legNo;

    /**
     * Direction
     */
    @Column(name = "bound", length = 5)
    private String bound;

    /**
     * Service code for a leg
     */
    @Column(name = "serviceCode", length = 4)
    private String serviceCode;

    /**
     * Vessel code of vessel will be used for a leg
     */
    @Column(name = "vesselCode", length = 6)
    private String vesselCode;

    /**
     * Voyage code will be used for a leg
     */
    @Column(name = "voyageCode", length = 6)
    private String voyageCode;

    /**
     * Scheduling Call ID discharge
     */
    @Column(name = "callIdDischarge", length = 50)
    private String callIdDischarge;

    /**
     * Scheduling Call ID loading
     */
    @Column(name = "callIdLoading", length = 50)
    private String callIdLoading;

    /**
     * Whether it is marine/feeder/truck/rail
     */
    @Column(name = "legTypeId", columnDefinition = "char(1)", nullable = false)
    private String legTypeId;

    /**
     * Load port Identification
     */
    @Column(name = "loadPortId", length = 5)
    private String loadPortId;

    /**
     * Discharge port Identification
     */
    @Column(name = "dischargePortId", length = 5)
    private String dischargePortId;

    /**
     * Date of departure (POL ETD)
     */
    @Column(name = "etdDate", nullable = false)
    private LocalDateTime etdDate;

    /**
     * Date of arrival (POD ETA)
     */
    @Column(name = "etaDate")
    private LocalDateTime etaDate;

    /**
     * Type of operation
     */
    @Column(name = "operationType", columnDefinition = "char(1)")
    private String operationType;

    /**
     * Not in use
     */
    @Column(name = "status", columnDefinition = "char(1)")
    private String status;

    /**
     * Not in use
     */
    @Column(name = "statusLoad", columnDefinition = "char(1)")
    private String statusLoad;

    /**
     * Not in use
     */
    @Column(name = "statusDischarge", columnDefinition = "char(1)")
    private String statusDischarge;

    /**
     * Not in use
     */
    @Column(name = "agencyUp", columnDefinition = "char(3)")
    private String agencyUp;

    /**
     * Previous leg number
     */
    @Column(name = "previousLeg")
    private Short previousLeg;

    /**
     * Vendor code of truck
     */
    @Column(name = "vendorCode", length = 17)
    private String vendorCode;

    /**
     * Vendor name
     */
    @Column(name = "vendorName", length = 150)
    private String vendorName;

    /**
     * Not in use
     */
    @Column(name = "loadAgencyId", columnDefinition = "char(3)")
    private String loadAgencyId;

    /**
     * Not in use
     */
    @Column(name = "dischargeAgencyId", columnDefinition = "char(3)")
    private String dischargeAgencyId;

    /**
     * Not in use
     */
    @Column(name = "statusApproved", columnDefinition = "char(1)")
    private String statusApproved;

    /**
     * Whether loading local/transhipment
     */
    @Column(name = "loadType", columnDefinition = "char(1)")
    private String loadType;

    /**
     * Whether discharging local/transhipment
     */
    @Column(name = "dischType", columnDefinition = "char(1)")
    private String dischType;

    /**
     * Carrier code
     */
    @Column(name = "carriercode", length = 3)
    private String carrierCode;

    /**
     * Whether pre carriage is haulage/Merchant
     */
    @Column(name = "preCarriage", columnDefinition = "char(1)")
    private String preCarriage;

    /**
     * Whether on carriage is haulage/Merchant
     */
    @Column(name = "onCarriage", columnDefinition = "char(1)")
    private String onCarriage;

    /**
     * User who updated
     */
    @Column(name = "userup", length = 15)
    private String userup;

    /**
     * Date of updated
     */
    @Column(name = "dateup")
    private LocalDateTime dateup;

    /**
     * Whether booking leg details edited
     */
    @Column(name = "isedited", columnDefinition = "char(1)")
    private String isEdited;

    /**
     * Not in use
     */
    @Column(name = "dgNo", length = 20)
    private String dgNo;

    /**
     * Discharge bound
     */
    @Column(name = "dbound", length = 5)
    private String dbound;

    /**
     * POL Terminal
     */
    @Column(name = "polTerminal", length = 10)
    private String polTerminal;

    /**
     * POD Terminal
     */
    @Column(name = "podTerminal", length = 10)
    private String podTerminal;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingLeg that = (BookingLeg) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BookingLeg{" +
                "id=" + id +
                ", legNo=" + legNo +
                '}';
    }
}

