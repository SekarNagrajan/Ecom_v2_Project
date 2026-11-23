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
 * Entity representing the booking_routing table.
 * To maintain the Booking Routing details.
 */
@Entity
@Table(name = "booking_routing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRouting implements Serializable {

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
    @JoinColumn(name = "book_no", referencedColumnName = "book_no", nullable = false)
    private Booking booking;

    /**
     * The Place of origin
     */
    @Column(name = "origin", length = 5)
    private String origin;

    /**
     * The mode of transport (rail/truck/barge)
     */
    @Column(name = "originMode", length = 50)
    private String originMode;

    /**
     * The initial loading port
     */
    @Column(name = "pol", length = 5)
    private String pol;

    /**
     * The loading port terminal
     */
    @Column(name = "polter", length = 10)
    private String polTer;

    /**
     * The loading port service
     */
    @Column(name = "polSer", length = 4)
    private String polSer;

    /**
     * The mode of service (mainline/feeder)
     */
    @Column(name = "polmode", length = 5)
    private String polMode;

    /**
     * The loading port of transhipment 1
     */
    @Column(name = "trans1", length = 5)
    private String trans1;

    /**
     * The loading port of terminal on transhipment1
     */
    @Column(name = "trans1Ter", length = 10)
    private String trans1Ter;

    /**
     * The service of Transhipment1
     */
    @Column(name = "trans1Ser", length = 4)
    private String trans1Ser;

    /**
     * The mode of service (mainline/feeder)
     */
    @Column(name = "trans1mode", length = 5)
    private String trans1Mode;

    /**
     * The loading port of transhipment 2
     */
    @Column(name = "trans2", length = 5)
    private String trans2;

    /**
     * The loading port of terminal on transhipment2
     */
    @Column(name = "trans2Ter", length = 10)
    private String trans2Ter;

    /**
     * The mode of service (mainline/feeder)
     */
    @Column(name = "trans2Ser", length = 4)
    private String trans2Ser;

    /**
     * The service of Transhipment2
     */
    @Column(name = "trans2mode", length = 5)
    private String trans2Mode;

    /**
     * The loading port of transhipment 3
     */
    @Column(name = "trans3", length = 5)
    private String trans3;

    /**
     * The loading port of terminal on transhipment3
     */
    @Column(name = "trans3Ter", length = 10)
    private String trans3Ter;

    /**
     * The mode of service (mainline/feeder)
     */
    @Column(name = "trans3Ser", length = 4)
    private String trans3Ser;

    /**
     * The service of Transhipment3
     */
    @Column(name = "trans3mode", length = 5)
    private String trans3Mode;

    /**
     * The Port of delivery
     */
    @Column(name = "pod", length = 5)
    private String pod;

    /**
     * The terminal of delivery port
     */
    @Column(name = "podTer", length = 10)
    private String podTer;

    /**
     * The mode of delivery (rail/truck/barge)
     */
    @Column(name = "podMode", length = 50)
    private String podMode;

    /**
     * Place of delivery
     */
    @Column(name = "delivery", length = 5)
    private String delivery;

    /**
     * The Main service detail
     */
    @Column(name = "svcCode", length = 4)
    private String svcCode;

    /**
     * Whether default route or not
     */
    @Column(name = "defRout", length = 5)
    private String defRout;

    /**
     * Transhipment mode we mention in booking level data taken from routing table
     */
    @Column(name = "opstrans1mde", length = 5)
    private String opsTrans1Mode;

    /**
     * Transhipment mode we mention in booking level data taken from routing table
     */
    @Column(name = "opstrans2mde", length = 5)
    private String opsTrans2Mode;

    /**
     * Not in Use
     */
    @Column(name = "trans4", length = 5)
    private String trans4;

    /**
     * Not in Use
     */
    @Column(name = "trans4Ter", length = 10)
    private String trans4Ter;

    /**
     * Not in Use
     */
    @Column(name = "trans4Ser", length = 4)
    private String trans4Ser;

    /**
     * Not in Use
     */
    @Column(name = "trans4mode", length = 5)
    private String trans4Mode;

    /**
     * Not in Use
     */
    @Column(name = "trans5", length = 5)
    private String trans5;

    /**
     * Not in Use
     */
    @Column(name = "trans5Ter", length = 10)
    private String trans5Ter;

    /**
     * Not in Use
     */
    @Column(name = "trans5Ser", length = 4)
    private String trans5Ser;

    /**
     * Not in Use
     */
    @Column(name = "trans5mode", length = 5)
    private String trans5Mode;

    /**
     * Not in Use
     */
    @Column(name = "opstrans3mde", length = 5)
    private String opsTrans3Mode;

    /**
     * Not in Use
     */
    @Column(name = "opstrans4mde", length = 5)
    private String opsTrans4Mode;

    /**
     * Not in Use
     */
    @Column(name = "routeid")
    private Integer routeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingRouting that = (BookingRouting) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BookingRouting{" +
                "id=" + id +
                ", origin='" + origin + '\'' +
                ", pod='" + pod + '\'' +
                '}';
    }
}

