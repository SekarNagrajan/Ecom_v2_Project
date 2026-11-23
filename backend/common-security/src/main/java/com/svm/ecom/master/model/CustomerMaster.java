package com.svm.ecom.master.model;

import javax.persistence.*;

@Entity
@Table(name = "customermaster")
public class CustomerMaster {

    public CustomerMaster() {}

    public CustomerMaster(String custCode, String cmpCode, String custName, String address1, String address2, 
                         String city, String countryId, String fax, String email, String phone, String website, 
                         String othersCity, String poBox, String zipCode, String stateCode, String controlLocation, 
                         String eori, String personType, String status) {
        this.custCode = custCode;
        this.cmpCode = cmpCode;
        this.custName = custName;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.countryId = countryId;
        this.fax = fax;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.othersCity = othersCity;
        this.poBox = poBox;
        this.zipCode = zipCode;
        this.stateCode = stateCode;
        this.controlLocation = controlLocation;
        this.eori = eori;
        this.personType = personType;
        this.status = status;
    }

    @Id
    @Column(name = "custcode", length = 20)
    private String custCode;

    @Column(name = "cmpcode", length = 50)
    private String cmpCode;

    @Column(name = "custname", length = 150)
    private String custName;

    @Column(name = "address1", length = 200)
    private String address1;

    @Column(name = "address2", length = 200)
    private String address2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "countryId", length = 10)
    private String countryId;

    @Column(name = "fax", length = 50)
    private String fax;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "website", length = 200)
    private String website;

    @Column(name = "otherscity", length = 100)
    private String othersCity;

    @Column(name = "pobox", length = 50)
    private String poBox;

    @Column(name = "zipcode", length = 20)
    private String zipCode;

    @Column(name = "statecode", length = 20)
    private String stateCode;

    @Column(name = "controllocation", length = 100)
    private String controlLocation;

    @Column(name = "eori", length = 100)
    private String eori;

    @Column(name = "persontype", length = 10)
    private String personType;

    @Column(name = "status", length = 1)
    private String status;

    // Getters and Setters
    public String getCustCode() { return custCode; }
    public void setCustCode(String custCode) { this.custCode = custCode; }

    public String getCmpCode() { return cmpCode; }
    public void setCmpCode(String cmpCode) { this.cmpCode = cmpCode; }

    public String getCustName() { return custName; }
    public void setCustName(String custName) { this.custName = custName; }

    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }

    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountryId() { return countryId; }
    public void setCountryId(String countryId) { this.countryId = countryId; }

    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getOthersCity() { return othersCity; }
    public void setOthersCity(String othersCity) { this.othersCity = othersCity; }

    public String getPoBox() { return poBox; }
    public void setPoBox(String poBox) { this.poBox = poBox; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getStateCode() { return stateCode; }
    public void setStateCode(String stateCode) { this.stateCode = stateCode; }

    public String getControlLocation() { return controlLocation; }
    public void setControlLocation(String controlLocation) { this.controlLocation = controlLocation; }

    public String getEori() { return eori; }
    public void setEori(String eori) { this.eori = eori; }

    public String getPersonType() { return personType; }
    public void setPersonType(String personType) { this.personType = personType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
