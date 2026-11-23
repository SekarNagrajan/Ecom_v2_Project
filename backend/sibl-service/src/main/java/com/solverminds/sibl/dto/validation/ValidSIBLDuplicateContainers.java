package com.solverminds.sibl.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Container numbers must be unique within the same SI (trim, uppercase for comparison).
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SIBLDuplicateContainersValidator.class)
public @interface ValidSIBLDuplicateContainers {
    String message() default "Duplicate container number within the same Shipping Instruction";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
