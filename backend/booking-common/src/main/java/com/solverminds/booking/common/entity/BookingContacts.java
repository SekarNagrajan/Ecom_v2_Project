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
 * Entity representing the booking_contacts table.
 * To maintain booking related contacts.
 */
@Entity
@Table(name = "booking_contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingContacts implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Auto increment primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Integer cid;

    /**
     * Booking number
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_no", referencedColumnName = "book_no", nullable = false)
    private Booking booking;

    /**
     * Container Number
     */
    @Column(name = "eqpid", length = 11)
    private String eqpId;

    /**
     * The exports contact person
     */
    @Column(name = "excontactperson", length = 50)
    private String exContactPerson;

    /**
     * Exports contact person phone number
     */
    @Column(name = "exphone", length = 25)
    private String exPhone;

    /**
     * Exports contact person FAX number
     */
    @Column(name = "exfax", length = 25)
    private String exFax;

    /**
     * Exports contact person Email id
     */
    @Column(name = "exemail", length = 50)
    private String exEmail;

    /**
     * Exports contact person remarks
     */
    @Column(name = "exportremarks", length = 250)
    private String exportRemarks;

    /**
     * Imports contact person
     */
    @Column(name = "imcontactperson", length = 50)
    private String imContactPerson;

    /**
     * Imports contact person phone number
     */
    @Column(name = "imphone", length = 25)
    private String imPhone;

    /**
     * Imports contact person FAX number
     */
    @Column(name = "imfax", length = 25)
    private String imFax;

    /**
     * Imports contact person Email id
     */
    @Column(name = "imemail", length = 50)
    private String imEmail;

    /**
     * Imports contact person remarks
     */
    @Column(name = "importremarks", length = 250)
    private String importRemarks;

    /**
     * Whether details edited or not
     */
    @Column(name = "isedited", columnDefinition = "char(1)", nullable = false)
    private String isEdited;

    /**
     * Dangerous goods contact person
     */
    @Column(name = "dgContact", length = 2)
    private String dgContact;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingContacts that = (BookingContacts) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "BookingContacts{" +
                "cid=" + cid +
                '}';
    }
}

