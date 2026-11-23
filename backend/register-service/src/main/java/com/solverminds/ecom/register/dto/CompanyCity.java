package com.solverminds.ecom.register.dto;

/**
 * DTO for customer master validation response
 * Contains company name and city from ecomm_jsf.customermaster
 */
public class CompanyCity {
    private String companyName;
    private String city;

    public CompanyCity() {
    }

    public CompanyCity(String companyName, String city) {
        this.companyName = companyName;
        this.city = city;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
