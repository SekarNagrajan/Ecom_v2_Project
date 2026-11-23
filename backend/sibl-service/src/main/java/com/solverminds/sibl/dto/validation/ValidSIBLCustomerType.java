package com.solverminds.sibl.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * SI Create/Update: only customer types SH, CN, NT, N1, N2, FW, SL, BY are allowed in the customers block.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SIBLCustomerTypeValidator.class)
public @interface ValidSIBLCustomerType {
    String message() default "Customer type for SI must be one of: SH, CN, NT, N1, N2, FW, SL, BY";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
