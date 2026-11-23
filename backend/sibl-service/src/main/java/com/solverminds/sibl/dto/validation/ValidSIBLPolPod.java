package com.solverminds.sibl.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * POL (loadPortId) and POD (dischargePortId) must be different (same as Booking DifferentPorts).
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SIBLPolPodValidator.class)
public @interface ValidSIBLPolPod {
    String message() default "POL and POD cannot be the same";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
