package com.solverminds.sibl.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ecomSI_cust", schema = "dbo")
public class EcomSiCustomer implements Serializable {

    // ===== Primary Key =====
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // ===== Foreign Key =====
    @Column(name = "eSINo", length = 20, nullable = false)
    private String eSINo;

    // ===== Core Fields =====
    @Column(name = "custTypeId", length = 10, nullable = false)
    private String custTypeId;

    @Column(name = "custId", length = 50, nullable = false)
    private String custId;

    @Column(name = "countryId", length = 5, nullable = false)
    private String countryId;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "address1", length = 500, nullable = false)
    private String address1;

    @Column(name = "address2", length = 500)
    private String address2;

    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @Column(name = "stateId", length = 50)
    private String stateId;

    @Column(name = "zipCode", length = 20)
    private String zipCode;

    @Column(name = "telephone", length = 50)
    private String telephone;

    @Column(name = "fax", length = 50)
    private String fax;

    @Column(name = "email", length = 100)
    private String email;

    // ===== Hidden Fields =====
    @Column(name = "flag", length = 1)
    private String flag;

    @Column(name = "toorder", length = 1)
    private String toorder;

    @Column(name = "eori", length = 50)
    private String eori;

    @Column(name = "persontype", length = 20)
    private String persontype;

    // ===== Constructors =====
    public EcomSiCustomer() {
    }

    public EcomSiCustomer(String eSINo, String custTypeId, String custId, String countryId, String name, String address1, String city) {
        this.eSINo = eSINo;
        this.custTypeId = custTypeId;
        this.custId = custId;
        this.countryId = countryId;
        this.name = name;
        this.address1 = address1;
        this.city = city;
    }

    // ===== Getters/Setters =====
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String geteSINo() {
        return eSINo;
    }

    public void seteSINo(String eSINo) {
        this.eSINo = eSINo;
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

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
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

    // ===== equals/hashCode by ID =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomSiCustomer)) return false;
        EcomSiCustomer that = (EcomSiCustomer) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EcomSiCustomer{" +
                "id=" + id +
                ", eSINo='" + eSINo + '\'' +
                ", custTypeId='" + custTypeId + '\'' +
                ", custId='" + custId + '\'' +
                ", countryId='" + countryId + '\'' +
                ", name='" + name + '\'' +
                ", address1='" + address1 + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
