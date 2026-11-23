package com.solverminds.booking.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite Primary Key for EcomBookingReefer
 * PK: ebookno, conid
 */
@Embeddable
public class EcomBookingReeferPK implements Serializable {

    @Column(name = "ebookno", length = 20, nullable = false)
    private String ebookno;

    @Column(name = "conid", nullable = false)
    private Integer conid;

    public EcomBookingReeferPK() {
    }

    public EcomBookingReeferPK(String ebookno, Integer conid) {
        this.ebookno = ebookno;
        this.conid = conid;
    }

    public String getEbookno() {
        return ebookno;
    }

    public void setEbookno(String ebookno) {
        this.ebookno = ebookno;
    }

    public Integer getConid() {
        return conid;
    }

    public void setConid(Integer conid) {
        this.conid = conid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomBookingReeferPK)) return false;
        EcomBookingReeferPK that = (EcomBookingReeferPK) o;
        return Objects.equals(ebookno, that.ebookno) &&
               Objects.equals(conid, that.conid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ebookno, conid);
    }
}

