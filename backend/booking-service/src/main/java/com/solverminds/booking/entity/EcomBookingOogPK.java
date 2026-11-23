package com.solverminds.booking.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite Primary Key for EcomBookingOog
 * PK: ebookno, conid, comItemNo
 */
@Embeddable
public class EcomBookingOogPK implements Serializable {

    @Column(name = "ebookno", length = 20, nullable = false)
    private String ebookno;

    @Column(name = "conid", nullable = false)
    private Integer conid;

    @Column(name = "comItemNo", nullable = false)
    private Integer comItemNo;

    public EcomBookingOogPK() {
    }

    public EcomBookingOogPK(String ebookno, Integer conid, Integer comItemNo) {
        this.ebookno = ebookno;
        this.conid = conid;
        this.comItemNo = comItemNo;
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

    public Integer getComItemNo() {
        return comItemNo;
    }

    public void setComItemNo(Integer comItemNo) {
        this.comItemNo = comItemNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomBookingOogPK)) return false;
        EcomBookingOogPK that = (EcomBookingOogPK) o;
        return Objects.equals(ebookno, that.ebookno) &&
               Objects.equals(conid, that.conid) &&
               Objects.equals(comItemNo, that.comItemNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ebookno, conid, comItemNo);
    }
}

