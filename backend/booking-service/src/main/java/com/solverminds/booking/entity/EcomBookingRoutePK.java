package com.solverminds.booking.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite Primary Key for EcomBookingRoute
 * PK: ebookno, leg_no
 */
@Embeddable
public class EcomBookingRoutePK implements Serializable {

    @Column(name = "ebookno", length = 20, nullable = false)
    private String ebookno;

    @Column(name = "leg_no", nullable = false)
    private Short legNo;

    public EcomBookingRoutePK() {
    }

    public EcomBookingRoutePK(String ebookno, Short legNo) {
        this.ebookno = ebookno;
        this.legNo = legNo;
    }

    public String getEbookno() {
        return ebookno;
    }

    public void setEbookno(String ebookno) {
        this.ebookno = ebookno;
    }

    public Short getLegNo() {
        return legNo;
    }

    public void setLegNo(Short legNo) {
        this.legNo = legNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomBookingRoutePK)) return false;
        EcomBookingRoutePK that = (EcomBookingRoutePK) o;
        return Objects.equals(ebookno, that.ebookno) &&
               Objects.equals(legNo, that.legNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ebookno, legNo);
    }
}

