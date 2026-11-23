package com.solverminds.booking.dto.validation;

import com.solverminds.booking.dto.CustomerDTO;
import com.svm.ecom.common.enums.CustomerType;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomerTypeValidator implements ConstraintValidator<ValidCustomerType, CustomerDTO> {
    
    // Error codes for BP/AP validation (from ErrorCodes class)
    private static final int BP_AP_NAME_REQUIRED = 4211;
    private static final int BP_AP_ADDRESS_REQUIRED = 4212;
    private static final int BP_AP_CITY_REQUIRED = 4213;
    private static final int BP_AP_COUNTRY_REQUIRED = 4214;
    
    @Override
    public boolean isValid(CustomerDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;
        CustomerType type = dto.getCustomerType();
        
        // For AP (Agreement Party) and BP (Booking Party), validate required fields
        if (type == CustomerType.BP || type == CustomerType.AP) {
            boolean isValid = true;
            ctx.disableDefaultConstraintViolation();
            
            // Validate customerName (Error Code: 4211)
            if (isEmpty(dto.getCustomerName())) {
                ctx.buildConstraintViolationWithTemplate("Field must be provided (non-empty).")
                   .addPropertyNode("customerName")
                   .addConstraintViolation();
                isValid = false;
            }
            
            // Validate address (Error Code: 4212)
            if (isEmpty(dto.getAddress())) {
                ctx.buildConstraintViolationWithTemplate("Field must be provided (non-empty).")
                   .addPropertyNode("address") 
                   .addConstraintViolation();
                isValid = false;
            }
            
            // Validate city (Error Code: 4213)
            if (isEmpty(dto.getCity())) {
                ctx.buildConstraintViolationWithTemplate("Field must be provided (non-empty).")
                   .addPropertyNode("city")
                   .addConstraintViolation();
                isValid = false;
            }
            
            // Validate countryId (Error Code: 4214)
            if (isEmpty(dto.getCountryId())) {
                ctx.buildConstraintViolationWithTemplate("Field must be provided (non-empty).")
                   .addPropertyNode("countryId")
                   .addConstraintViolation();
                isValid = false;
            }
            
            return isValid;
        }
        return true;
    }
    
    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
