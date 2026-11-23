package com.solverminds.booking.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validates unique commodity combination in {@code commodities[]}: rejects only when
 * {@code containerNo}, {@code commodityGroup}, {@code hsCode}, and {@code packageType}
 * are all identical (trim + uppercase comparison).
 */
@Documented
@Constraint(validatedBy = NoDuplicateCommodityCombinationsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNoDuplicateCommodityCombinations {
    String message() default "Duplicate commodity found. Combination already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
