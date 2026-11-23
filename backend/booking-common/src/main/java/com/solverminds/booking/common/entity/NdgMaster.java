package com.solverminds.booking.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Maps {@code ndgmaster}: links an LRP / partial DG reference to a DG number ({@code dgNo}).
 */
@Entity
@Table(name = "ndgmaster")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NdgMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * DG number (natural key for joining {@link NdgDetail}).
     */
    @Id
    @Column(name = "dgNo", length = 20, nullable = false)
    private String dgNo;

    /**
     * Booking / partial DG reference (e.g. LRP booking number); matches {@code book_no} for lookups.
     */
    @Column(name = "PartDgRefNo", length = 50)
    private String partDgRefNo;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NdgMaster ndgMaster = (NdgMaster) o;
        return Objects.equals(dgNo, ndgMaster.dgNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dgNo);
    }
}
