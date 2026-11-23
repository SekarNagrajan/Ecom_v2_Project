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
 * Entity representing the booking_chargenew table.
 * To maintain booking related charges details.
 */
@Entity
@Table(name = "booking_chargenew")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingChargeNew implements Serializable {

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
     * Charge number (Serial number)
     */
    @Column(name = "chargeNo")
    private Integer chargeNo;

    /**
     * Booking Charge name
     */
    @Column(name = "chargeId", length = 3, nullable = false)
    private String chargeId;

    /**
     * Base rate amount
     */
    @Column(name = "baseRate", nullable = false)
    private Double baseRate;

    /**
     * Base rate unit
     */
    @Column(name = "baseRateUnit", length = 2, nullable = false)
    private String baseRateUnit;

    /**
     * Amount of currency
     */
    @Column(name = "currencyId", length = 3, nullable = false)
    private String currencyId;

    /**
     * Amount paying agency
     */
    @Column(name = "payAgencyId", length = 5, nullable = false)
    private String payAgencyId;

    /**
     * Agency Who is suppose to pay
     */
    @Column(name = "custTypeId", length = 7)
    private String custTypeId;

    /**
     * Charge quantity
     */
    @Column(name = "chargeQty")
    private Double chargeQty;

    /**
     * Charge pay mode
     */
    @Column(name = "prepaidCollect", length = 5)
    private String prepaidCollect;

    /**
     * Amount
     */
    @Column(name = "chargeAmount", nullable = false)
    private Double chargeAmount;

    /**
     * Equipment type
     */
    @Column(name = "eqpTypeId", length = 4)
    private String eqpTypeId;

    /**
     * Not in Use
     */
    @Column(name = "eqpId", length = 11)
    private String eqpId;

    /**
     * Whether the charge came from surcharge / Rates
     */
    @Column(name = "autoRated", columnDefinition = "char(1)", nullable = false)
    private String autoRated;

    /**
     * Charge behaviour (Manifest or Non-manifest)
     */
    @Column(name = "chargetype", columnDefinition = "char(1)", nullable = false)
    private String chargeType;

    /**
     * Carrier code
     */
    @Column(name = "carriercode", length = 3, nullable = false)
    private String carrierCode;

    /**
     * Charge amount vatous or Non-Vatous
     */
    @Column(name = "isVatos", columnDefinition = "char(1)")
    private String isVatos;

    /**
     * Rate reference
     */
    @Column(name = "raterefno", length = 30)
    private String rateRefNo;

    /**
     * Whether charges details edited in booking doc or not
     */
    @Column(name = "isedited", columnDefinition = "char(1)")
    private String isEdited;

    /**
     * Formula charges
     */
    @Column(name = "formula", length = 500)
    private String formula;

    /**
     * To show charge flag
     */
    @Column(name = "Chargeflag", length = 5)
    private String chargeFlag;

    /**
     * To store charge indication payment mode
     */
    @Column(name = "payIndicator", columnDefinition = "char(1)")
    private String payIndicator;

    /**
     * To store charge is all in or not
     */
    @Column(name = "Allin", columnDefinition = "char(1)")
    private String allIn;

    /**
     * Rate reference only
     */
    @Column(name = "raterefonly", length = 50)
    private String rateRefOnly;

    /**
     * Rate reference item no only
     */
    @Column(name = "rateitemonly")
    private Integer rateItemOnly;

    /**
     * To maintain the pay agency code
     */
    @Column(name = "pay_agency", length = 6)
    private String payAgency;

    /**
     * Not in Use
     */
    @Column(name = "invcurrencyId", length = 3)
    private String invCurrencyId;

    /**
     * To store the payat Pay mode in detail
     */
    @Column(name = "PayMode_Detail", length = 2)
    private String payModeDetail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingChargeNew that = (BookingChargeNew) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "BookingChargeNew{" +
                "cid=" + cid +
                ", chargeId='" + chargeId + '\'' +
                '}';
    }
}

