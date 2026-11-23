package com.solverminds.booking.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation to check if customer code and country code exist in the master data cache.
 * Validates: customerCode (against customermaster), countryId (against country)
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CustomerMasterDataValidator.class)
public @interface ValidCustomerMasterData {
    String message() default "Invalid customer master data";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

