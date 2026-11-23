package com.solverminds.edi.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "editempbooking_cust", schema = "dbo")
public class EdiTempBookingCust implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid", nullable = false)
    private Long cid;

    // ===== Columns =====
    @Column(name = "book_no", length = 50)
    private String bookNo;

    @Column(name = "custTypeId", length = 2)
    private String custTypeId;

    @Column(name = "custId", length = 35)
    private String custId;

    @Column(name = "countryId", length = 50)
    private String countryId;

    @Column(name = "stateId", length = 4)
    private String stateId;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "address1", length = 150)
    private String address1;

    @Column(name = "address2", length = 150)
    private String address2;

    @Column(name = "POBox", length = 70)
    private String poBox;

    @Column(name = "city", length = 150)
    private String city;

    @Column(name = "zipCode", length = 15)
    private String zipCode;

    @Column(name = "telephone", length = 50)
    private String telephone;

    @Column(name = "fax", length = 25)
    private String fax;

    @Column(name = "contact", length = 40)
    private String contact;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "carriercode", length = 10)
    private String carriercode;

    @Column(name = "isedited", length = 1)
    private String isedited;

    @Column(name = "edireaderid", length = 50)
    private String edireaderid;

    @Column(name = "edicleanstatus", length = 1)
    private String edicleanstatus;

    @Column(name = "version")
    private Short version;

    @Column(name = "UascCustCode", length = 20)
    private String uascCustCode;

    @Column(name = "ESLcustcode", length = 250)
    private String eslcustcode;

    @Column(name = "chkPhone", length = 20)
    private String chkPhone;

    @Column(name = "chkFax", length = 1)
    private String chkFax;

    @Column(name = "eori", length = 17)
    private String eori;

    @Column(name = "persontype", length = 50)
    private String persontype;

    // ===== Constructors =====
    public EdiTempBookingCust() {
    }

    // ===== Getters and Setters =====
    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
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

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
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

    public String getPoBox() {
        return poBox;
    }

    public void setPoBox(String poBox) {
        this.poBox = poBox;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCarriercode() {
        return carriercode;
    }

    public void setCarriercode(String carriercode) {
        this.carriercode = carriercode;
    }

    public String getIsedited() {
        return isedited;
    }

    public void setIsedited(String isedited) {
        this.isedited = isedited;
    }

    public String getEdireaderid() {
        return edireaderid;
    }

    public void setEdireaderid(String edireaderid) {
        this.edireaderid = edireaderid;
    }

    public String getEdicleanstatus() {
        return edicleanstatus;
    }

    public void setEdicleanstatus(String edicleanstatus) {
        this.edicleanstatus = edicleanstatus;
    }

    public Short getVersion() {
        return version;
    }

    public void setVersion(Short version) {
        this.version = version;
    }

    public String getUascCustCode() {
        return uascCustCode;
    }

    public void setUascCustCode(String uascCustCode) {
        this.uascCustCode = uascCustCode;
    }

    public String getEslcustcode() {
        return eslcustcode;
    }

    public void setEslcustcode(String eslcustcode) {
        this.eslcustcode = eslcustcode;
    }

    public String getChkPhone() {
        return chkPhone;
    }

    public void setChkPhone(String chkPhone) {
        this.chkPhone = chkPhone;
    }

    public String getChkFax() {
        return chkFax;
    }

    public void setChkFax(String chkFax) {
        this.chkFax = chkFax;
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

    // ===== equals, hashCode, toString =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdiTempBookingCust that = (EdiTempBookingCust) o;
        return Objects.equals(cid, that.cid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public String toString() {
        return "EdiTempBookingCust{" +
                "cid=" + cid +
                ", bookNo='" + bookNo + '\'' +
                ", custTypeId='" + custTypeId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

