package com.solverminds.ecom.register.service.impl;

import com.solverminds.ecom.register.dto.CompanyDto;
import com.solverminds.ecom.register.dto.CustomerContactDto;
import com.solverminds.ecom.register.dto.CustomerDetailsDto;
import com.solverminds.ecom.register.dto.ProfileDto;
import com.solverminds.ecom.register.entity.EcomUserDetails;
import com.solverminds.ecom.register.repo.CustomerReportRepository;
import com.solverminds.ecom.register.repo.EcomUserDetailsRepository;
import com.solverminds.ecom.register.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true, transactionManager = "ecomTransactionManager")
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private EcomUserDetailsRepository userRepository;

    @Autowired
    private CustomerReportRepository customerReportRepository;

    @Override
    public ProfileDto getProfile(String email) {
        Optional<EcomUserDetails> userOpt = userRepository.findByEmailIDIgnoreCase(email);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("ECOM-AUTH-FAILED");
        }

        EcomUserDetails user = userOpt.get();
        
        ProfileDto profile = new ProfileDto();
        profile.setUserId(user.getId().toString());
        profile.setName(user.getEmailID()); // Using email as name for now
        profile.setEmail(user.getEmailID());
        profile.setCompany(user.getCustomerCode());
        
        // Get detailed company information using native query
        CompanyDto companyDetails = getCompanyDetailsByCustCode(user.getCustomerCode());
        profile.setCompanyDetails(companyDetails);
        
        // Get customer contact information using native query with email as webid
        List<CustomerContactDto> customerContacts = getCustomerContactsByWebId(email);
        profile.setCustomerContacts(customerContacts);
        
        profile.setRoles(Arrays.asList(user.getUserRole()));
        profile.setTimezone(user.getPreferredTimezone());

        return profile;
    }

    /**
     * Get company details by customer code using native query
     * Maps CustomerDetailsDto to CompanyDto for backward compatibility
     */
    private CompanyDto getCompanyDetailsByCustCode(String custCode) {
        if (custCode == null || custCode.trim().isEmpty()) {
            return null;
        }

        List<CustomerDetailsDto> customerDetailsList = customerReportRepository.getCustomerDetailsByCustCode(custCode);
        
        if (customerDetailsList == null || customerDetailsList.isEmpty()) {
            return null;
        }

        // Get the first result and convert to CompanyDto
        CustomerDetailsDto customerDetails = customerDetailsList.get(0);
        return convertToCompanyDto(customerDetails);
    }

    /**
     * Convert CustomerDetailsDto to CompanyDto
     */
    private CompanyDto convertToCompanyDto(CustomerDetailsDto customerDetails) {
        if (customerDetails == null) {
            return null;
        }

        CompanyDto companyDto = new CompanyDto();
        
        companyDto.setCmpCode(customerDetails.getCmpCode());
        companyDto.setCustName(customerDetails.getCustName());
        companyDto.setAddress1(customerDetails.getAddress1());
        companyDto.setAddress2(customerDetails.getAddress2());
        companyDto.setCity(customerDetails.getCity());
        companyDto.setCountryName(customerDetails.getCountryName());
        companyDto.setFax(customerDetails.getFax());
        companyDto.setEmail(customerDetails.getEmail());
        companyDto.setPhone(customerDetails.getPhone());
        companyDto.setWebsite(customerDetails.getWebsite());
        companyDto.setOthersCity(customerDetails.getOthersCity());
        companyDto.setPoBox(customerDetails.getPoBox());
        companyDto.setCountryId(customerDetails.getCountryId());
        companyDto.setZipCode(customerDetails.getZipCode());
        companyDto.setState(customerDetails.getState());
        companyDto.setControlLocation(customerDetails.getControlLocation());
        companyDto.setEori(customerDetails.getEori());
        companyDto.setPersonType(customerDetails.getPersonType());

        return companyDto;
    }

    /**
     * Get customer contact information by web ID using native query
     * Uses email as webid to fetch customer contact details
     */
    private List<CustomerContactDto> getCustomerContactsByWebId(String webId) {
        if (webId == null || webId.trim().isEmpty()) {
            return null;
        }

        try {
            return customerReportRepository.getCustomerContactByWebId(webId);
        } catch (Exception e) {
            // Log error and return empty list if customer contacts not found
            return null;
        }
    }
}
