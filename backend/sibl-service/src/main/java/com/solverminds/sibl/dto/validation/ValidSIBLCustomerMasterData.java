package com.solverminds.sibl.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Customer code and country code must exist in master data cache (same as Booking ValidCustomerMasterData).
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SIBLCustomerMasterDataValidator.class)
public @interface ValidSIBLCustomerMasterData {
    String message() default "Invalid customer master data";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
