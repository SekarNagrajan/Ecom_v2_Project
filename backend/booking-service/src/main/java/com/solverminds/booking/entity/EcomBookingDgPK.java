package com.solverminds.booking.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite Primary Key for EcomBookingDg
 * PK: ebookno, conid, itemno
 */
@Embeddable
public class EcomBookingDgPK implements Serializable {

    @Column(name = "ebookno", length = 20, nullable = false)
    private String ebookno;

    @Column(name = "conid", nullable = false)
    private Integer conid;

    @Column(name = "itemno", nullable = false)
    private Integer itemno;

    public EcomBookingDgPK() {
    }

    public EcomBookingDgPK(String ebookno, Integer conid, Integer itemno) {
        this.ebookno = ebookno;
        this.conid = conid;
        this.itemno = itemno;
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

    public Integer getItemno() {
        return itemno;
    }

    public void setItemno(Integer itemno) {
        this.itemno = itemno;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomBookingDgPK)) return false;
        EcomBookingDgPK that = (EcomBookingDgPK) o;
        return Objects.equals(ebookno, that.ebookno) &&
               Objects.equals(conid, that.conid) &&
               Objects.equals(itemno, that.itemno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ebookno, conid, itemno);
    }
}

