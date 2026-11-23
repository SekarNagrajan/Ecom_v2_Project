package com.solverminds.booking.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation to check if route port codes exist in the master data cache.
 * Validates: loadPortId, dischargePortId in RouteDTO
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = RoutePortsValidator.class)
public @interface ValidRoutePorts {
    String message() default "Invalid route port code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

