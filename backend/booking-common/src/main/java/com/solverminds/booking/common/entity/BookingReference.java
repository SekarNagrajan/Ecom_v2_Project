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
 * Entity representing the booking_reference table.
 * Customer service created the Booking EDI reference details.
 */
@Entity
@Table(name = "booking_reference")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingReference implements Serializable {

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
     * Reference id maintained by liner
     */
    @Column(name = "refid", length = 3, nullable = false)
    private String refId;

    /**
     * Value generated for EDI purpose
     */
    @Column(name = "refval", length = 50)
    private String refVal;

    /**
     * Whether booking_reference details edited or not
     */
    @Column(name = "isedited", columnDefinition = "char(1)")
    private String isEdited;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingReference that = (BookingReference) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BookingReference{" +
                "id=" + id +
                ", refId='" + refId + '\'' +
                '}';
    }
}

