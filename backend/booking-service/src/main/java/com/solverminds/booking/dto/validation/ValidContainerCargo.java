package com.solverminds.booking.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validates that container type must match cargo requirements.
 * Each container must have corresponding commodity linked by cargoNo and containerNo.
 */
@Documented
@Constraint(validatedBy = ContainerCargoValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidContainerCargo {
    String message() default "Container type must match cargo requirements";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
