package com.solverminds.booking.entity;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite Primary Key for EcomBookingCommodity
 * PK: ebookno, conid, itemno
 */
@Embeddable
public class EcomBookingCommodityPK implements Serializable {

    @Column(name = "ebookno", length = 20, nullable = false)
    private String ebookno;

    @Column(name = "conid", nullable = false)
    private Integer conid;

    @Column(name = "itemno", nullable = false)
    private Integer itemno;

    public EcomBookingCommodityPK() {
    }

    public EcomBookingCommodityPK(String ebookno, Integer conid, Integer itemno) {
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
        if (!(o instanceof EcomBookingCommodityPK)) return false;
        EcomBookingCommodityPK that = (EcomBookingCommodityPK) o;
        return Objects.equals(ebookno, that.ebookno) &&
               Objects.equals(conid, that.conid) &&
               Objects.equals(itemno, that.itemno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ebookno, conid, itemno);
    }
}

