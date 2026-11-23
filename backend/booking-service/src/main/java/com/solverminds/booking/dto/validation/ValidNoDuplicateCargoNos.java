package com.solverminds.booking.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validates unique {@code cargoNo} independently in {@code containers[]} and {@code commodities[]}
 * (not combined with {@code containerNo} or {@code itemNo}).
 */
@Documented
@Constraint(validatedBy = NoDuplicateCargoNosValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNoDuplicateCargoNos {
    String message() default "Duplicate cargo number(s) found";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
