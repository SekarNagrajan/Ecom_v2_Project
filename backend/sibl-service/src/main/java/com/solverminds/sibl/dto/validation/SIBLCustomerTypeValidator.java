package com.solverminds.sibl.dto.validation;

import com.solverminds.sibl.dto.SIBLCustomerDTO;
import com.svm.ecom.common.enums.CustomerType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * SI Create/Update: only customer types SH, CN, NT, N1, N2, FW, SL, BY are allowed in the customers block.
 * AP, BP, DL, PL and any other types are rejected.
 */
public class SIBLCustomerTypeValidator implements ConstraintValidator<ValidSIBLCustomerType, SIBLCustomerDTO> {

    @Override
    public boolean isValid(SIBLCustomerDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;
        CustomerType type = dto.getCustomerType();
        if (type == null) return true;

        if (!SIBLCustomerTypeConstants.ALLOWED_FOR_SI.contains(type)) {
            ctx.disableDefaultConstraintViolation();
            addViolation(ctx, "customerType",
                    "Customer type for SI must be one of: " + SIBLCustomerTypeConstants.ALLOWED_VALUES_STRING + ". Got: " + type.getValue());
            return false;
        }
        return true;
    }

    private static void addViolation(ConstraintValidatorContext ctx, String property, String message) {
        ctx.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(property)
                .addConstraintViolation();
    }
}
