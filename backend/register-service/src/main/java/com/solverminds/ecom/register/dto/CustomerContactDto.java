package com.solverminds.ecom.register.dto;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for customer contact information query results
 * Maps to the first SQL query for customercontacts join with customermaster
 */
@NoArgsConstructor
@AllArgsConstructor
public class CustomerContactDto {

    private String custCode;
    private String countryId;
    private String contactName;
    private String custName;
    private String address1;
    private String phone;
    private String fax;
    private String email;
    private String mobile;
    private String onlineActive;
    private String city;
    private String webId;
    private String admin;
    private String custType;

    public String getCustCode() {
        return custCode;
    }

    public void setCustCode(String custCode) {
        this.custCode = custCode;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOnlineActive() {
        return onlineActive;
    }

    public void setOnlineActive(String onlineActive) {
        this.onlineActive = onlineActive;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWebId() {
        return webId;
    }

    public void setWebId(String webId) {
        this.webId = webId;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType;
    }
}
