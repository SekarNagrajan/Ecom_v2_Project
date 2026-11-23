package com.solverminds.booking.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "ecombooking_cust", schema = "dbo")
public class EcomBookingCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Long cid;

    @Column(name = "ebookno", length = 20, nullable = false)
    private String ebookno;

    @Column(name = "custTypeId", length = 2, nullable = false)
    private String custTypeId;

    @Column(name = "custId", length = 20, nullable = false)
    private String custId;

    @Column(name = "countryId", length = 2)
    private String countryId;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "address1", length = 150)
    private String address1;

    @Column(name = "address2", length = 150)
    private String address2;

    @Column(name = "city", length = 150)
    private String city;

    @Column(name = "zipCode", length = 15)
    private String zipCode;

    @Column(name = "telephone", length = 25)
    private String telephone;

    @Column(name = "fax", length = 25)
    private String fax;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "flag", length = 1)
    private String flag;

    @Column(name = "toorder", length = 1)
    private String toorder;

    @Column(name = "stateId", length = 4)
    private String stateId;

    @Column(name = "eori", length = 17)
    private String eori;

    @Column(name = "persontype", length = 50)
    private String persontype;

    public EcomBookingCustomer() {
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
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

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getToorder() {
        return toorder;
    }

    public void setToorder(String toorder) {
        this.toorder = toorder;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getEori() {
        return eori;
    }

    public void setEori(String eori) {
        this.eori = eori;
    }

    public String getPersontype() {
        return persontype;
    }

    public void setPersontype(String persontype) {
        this.persontype = persontype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomBookingCustomer)) return false;
        EcomBookingCustomer that = (EcomBookingCustomer) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "EcomBookingCustomer{" +
                "cid=" + cid +
                ", custId='" + custId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
