package com.solverminds.booking.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validates unique {@code containerNo} in {@code containers[]} only.
 * Commodity duplicate detection uses {@link ValidNoDuplicateCommodityCombinations}.
 */
@Documented
@Constraint(validatedBy = NoDuplicateContainerNosValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNoDuplicateContainerNos {
    String message() default "Duplicate container number(s) found";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
