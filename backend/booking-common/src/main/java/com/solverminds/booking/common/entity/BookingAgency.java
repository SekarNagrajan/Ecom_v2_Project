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
 * Entity representing the Booking_Agency table.
 * To capture the booking related agency details.
 */
@Entity
@Table(name = "Booking_Agency")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingAgency implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Auto increment primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Long cid;

    /**
     * Book Number
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_no", referencedColumnName = "book_no")
    private Booking booking;

    /**
     * Booking First Transhipment Port Agency
     */
    @Column(name = "ts1agency", length = 6)
    private String ts1Agency;

    /**
     * Booking Second Transhipment Port Agency
     */
    @Column(name = "ts2agency", length = 6)
    private String ts2Agency;

    /**
     * Booking Third Transhipment Port Agency
     */
    @Column(name = "ts3agency", length = 6)
    private String ts3Agency;

    /**
     * Not in use
     */
    @Column(name = "ts4agency", length = 6)
    private String ts4Agency;

    /**
     * Not in use
     */
    @Column(name = "ts5agency", length = 6)
    private String ts5Agency;

    /**
     * Booking PLA agency
     */
    @Column(name = "plaagency", length = 6)
    private String plaAgency;

    /**
     * Booking POL agency
     */
    @Column(name = "polagency", length = 6)
    private String polAgency;

    /**
     * Booking PLD agency
     */
    @Column(name = "pldagency", length = 6)
    private String pldAgency;

    /**
     * Booking POD agency
     */
    @Column(name = "podagency", length = 6)
    private String podAgency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingAgency that = (BookingAgency) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "BookingAgency{" +
                "cid=" + cid +
                '}';
    }
}

