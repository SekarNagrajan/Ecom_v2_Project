package com.solverminds.booking.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite Primary Key for EcomBookingCustomer
 * PK: ebookno, custTypeId, cid
 */
@Embeddable
public class EcomBookingCustomerPK implements Serializable {

    @Column(name = "ebookno", length = 20, nullable = false)
    private String ebookno;

    @Column(name = "custTypeId", length = 2, nullable = false)
    private String custTypeId;

    @Column(name = "cid", nullable = false)
    private Long cid;

    public EcomBookingCustomerPK() {
    }

    public EcomBookingCustomerPK(String ebookno, String custTypeId, Long cid) {
        this.ebookno = ebookno;
        this.custTypeId = custTypeId;
        this.cid = cid;
    }

    public String getEbookno() {
        return ebookno;
    }

    public void setEbookno(String ebookno) {
        this.ebookno = ebookno;
    }

    public String getCustTypeId() {
        return custTypeId;
    }

    public void setCustTypeId(String custTypeId) {
        this.custTypeId = custTypeId;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomBookingCustomerPK)) return false;
        EcomBookingCustomerPK that = (EcomBookingCustomerPK) o;
        return Objects.equals(ebookno, that.ebookno) &&
               Objects.equals(custTypeId, that.custTypeId) &&
               Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ebookno, custTypeId, cid);
    }
}

