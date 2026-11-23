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
 * Entity representing the booking_cust table.
 * To maintain booking related customer details.
 */
@Entity
@Table(name = "booking_cust")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCust implements Serializable {

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
     * Identification for customer Type
     */
    @Column(name = "custTypeId", length = 7)
    private String custTypeId;

    /**
     * Customer code
     */
    @Column(name = "custId", length = 17)
    private String custId;

    /**
     * Country Identification
     */
    @Column(name = "countryId", length = 2)
    private String countryId;

    /**
     * State Identification
     */
    @Column(name = "stateId", length = 4)
    private String stateId;

    /**
     * Name of Customer
     */
    @Column(name = "name", length = 150)
    private String name;

    /**
     * First address of customer
     */
    @Column(name = "address1", length = 150)
    private String address1;

    /**
     * Secondary ADDRESS
     */
    @Column(name = "address2", length = 150)
    private String address2;

    /**
     * PO BOX number
     */
    @Column(name = "POBox", length = 50)
    private String poBox;

    /**
     * City name
     */
    @Column(name = "city", length = 150)
    private String city;

    /**
     * Zipcode
     */
    @Column(name = "zipCode", length = 15)
    private String zipCode;

    /**
     * Phone number
     */
    @Column(name = "telephone", length = 25)
    private String telephone;

    /**
     * Fax number
     */
    @Column(name = "fax", length = 25)
    private String fax;

    /**
     * Contact details
     */
    @Column(name = "contact", length = 20)
    private String contact;

    /**
     * E-Mail Id of customer
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * Carrier Code
     */
    @Column(name = "carriercode", length = 4)
    private String carrierCode;

    /**
     * Whether customer details edited or not
     */
    @Column(name = "isedited", columnDefinition = "char(1)", nullable = false)
    private String isEdited;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingCust that = (BookingCust) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "BookingCust{" +
                "cid=" + cid +
                ", custId='" + custId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

