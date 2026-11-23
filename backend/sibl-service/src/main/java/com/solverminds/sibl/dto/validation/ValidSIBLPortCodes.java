package com.solverminds.sibl.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation to check if port codes exist in the master data cache (same as Booking ValidPortCodes).
 * Validates: originPortId, finalPortId, loadPortId, dischargePortId
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SIBLPortCodesValidator.class)
public @interface ValidSIBLPortCodes {
    String message() default "Invalid port code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
