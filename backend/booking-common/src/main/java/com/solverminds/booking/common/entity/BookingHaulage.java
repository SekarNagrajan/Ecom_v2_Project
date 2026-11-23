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
 * Entity representing the booking_haulage table.
 * Not in use - table maintained for legacy purposes.
 */
@Entity
@Table(name = "booking_haulage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingHaulage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Auto increment primary key - Not in use
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Long cid;

    /**
     * Booking Number - Not in use
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_no", referencedColumnName = "book_no")
    private Booking booking;

    /**
     * Not in use
     */
    @Column(name = "loadLocation", length = 50)
    private String loadLocation;

    /**
     * Not in use
     */
    @Column(name = "eqpId", length = 11)
    private String eqpId;

    /**
     * Not in use
     */
    @Column(name = "contactPerson", length = 200)
    private String contactPerson;

    /**
     * Not in use
     */
    @Column(name = "contactAddress", columnDefinition = "varchar(max)")
    private String contactAddress;

    /**
     * Not in use
     */
    @Column(name = "contactPhone", length = 25)
    private String contactPhone;

    /**
     * Not in use
     */
    @Column(name = "contactEmail", length = 100)
    private String contactEmail;

    /**
     * Not in use
     */
    @Column(name = "referenceNumber", length = 50)
    private String referenceNumber;

    /**
     * Not in use
     */
    @Column(name = "dropDate")
    private LocalDateTime dropDate;

    /**
     * Not in use
     */
    @Column(name = "remarks", columnDefinition = "varchar(max)")
    private String remarks;

    /**
     * Not in use
     */
    @Column(name = "docType", length = 20)
    private String docType;

    /**
     * Not in use
     */
    @Column(name = "overWeight", length = 2)
    private String overWeight;

    /**
     * Not in use
     */
    @Column(name = "stopSequence")
    private Integer stopSequence;

    /**
     * Not in use
     */
    @Column(name = "loadlocationname", length = 100)
    private String loadLocationName;

    /**
     * Not in use
     */
    @Column(name = "TMSRefNo", length = 200)
    private String tmsRefNo;

    /**
     * Not in use
     */
    @Column(name = "TMSClientCode", length = 10)
    private String tmsClientCode;

    /**
     * Not in use
     */
    @Column(name = "eqptypeid", length = 4)
    private String eqpTypeId;

    /**
     * Not in use
     */
    @Column(name = "bkgreference", length = 100)
    private String bkgReference;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingHaulage that = (BookingHaulage) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "BookingHaulage{" +
                "cid=" + cid +
                '}';
    }
}

