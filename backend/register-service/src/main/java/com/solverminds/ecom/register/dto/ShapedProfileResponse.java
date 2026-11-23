package com.solverminds.ecom.register.dto;

import java.util.List;

/**
 * Shaped response DTO for /api/v1/account/profile endpoint
 * Contains only companyDetails and customerContacts in specified format
 */
public class ShapedProfileResponse {

    private ShapedCompanyDetails companyDetails;
    private List<ShapedCustomerContact> customerContacts;

    public ShapedProfileResponse() {
    }

    public ShapedProfileResponse(ShapedCompanyDetails companyDetails, List<ShapedCustomerContact> customerContacts) {
        this.companyDetails = companyDetails;
        this.customerContacts = customerContacts;
    }

    public ShapedCompanyDetails getCompanyDetails() {
        return companyDetails;
    }

    public void setCompanyDetails(ShapedCompanyDetails companyDetails) {
        this.companyDetails = companyDetails;
    }

    public List<ShapedCustomerContact> getCustomerContacts() {
        return customerContacts;
    }

    public void setCustomerContacts(List<ShapedCustomerContact> customerContacts) {
        this.customerContacts = customerContacts;
    }
}
