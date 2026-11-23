package com.solverminds.sibl.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity for ecomsi_ics_new table (ENS Declarant)
 * Insert only if ENS filing type is provided
 */
@Entity
@Table(name = "ecomsi_ics_new", schema = "dbo")
public class EcomSiIcsNew implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "eSINo", length = 20, nullable = false)
    private String eSINo;

    @Column(name = "eucustom", length = 1)
    private String eucustom;

    @Column(name = "bill_type", length = 20)
    private String billType;

    @Column(name = "ens_type", length = 20)
    private String ensType;

    @Column(name = "payment_mode", length = 20)
    private String paymentMode;

    @Column(name = "cust_name", length = 150)
    private String custName;

    @Column(name = "address", length = 150)
    private String address;

    @Column(name = "address1", length = 150)
    private String address1;

    @Column(name = "city", length = 150)
    private String city;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "telephone", length = 25)
    private String telephone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "enseori", length = 20)
    private String enseori;

    @Column(name = "filing_type", length = 20)
    private String filingType;

    @Column(name = "zipcode", length = 15)
    private String zipcode;

    // ===== Constructors =====
    public EcomSiIcsNew() {}

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String geteSINo() { return eSINo; }
    public void seteSINo(String eSINo) { this.eSINo = eSINo; }

    public String getEucustom() { return eucustom; }
    public void setEucustom(String eucustom) { this.eucustom = eucustom; }

    public String getBillType() { return billType; }
    public void setBillType(String billType) { this.billType = billType; }

    public String getEnsType() { return ensType; }
    public void setEnsType(String ensType) { this.ensType = ensType; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getCustName() { return custName; }
    public void setCustName(String custName) { this.custName = custName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEnseori() { return enseori; }
    public void setEnseori(String enseori) { this.enseori = enseori; }

    public String getFilingType() { return filingType; }
    public void setFilingType(String filingType) { this.filingType = filingType; }

    public String getZipcode() { return zipcode; }
    public void setZipcode(String zipcode) { this.zipcode = zipcode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EcomSiIcsNew)) return false;
        EcomSiIcsNew that = (EcomSiIcsNew) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
