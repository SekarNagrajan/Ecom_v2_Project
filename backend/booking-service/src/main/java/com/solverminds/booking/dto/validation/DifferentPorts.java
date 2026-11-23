package com.solverminds.booking.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validates that POL (loadPortId) and POD (dischargePortId) are not the same.
 */
@Documented
@Constraint(validatedBy = DifferentPortsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DifferentPorts {
    String message() default "POL and POD cannot be the same";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
