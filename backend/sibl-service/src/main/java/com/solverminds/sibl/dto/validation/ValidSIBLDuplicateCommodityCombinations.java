package com.solverminds.sibl.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validates unique commodity combination in {@code commodities[]}: rejects only when
 * {@code containerNo}, {@code commodityGroup}, {@code hsCode}, and {@code packageType}
 * are all identical (trim + uppercase comparison).
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SIBLDuplicateCommodityCombinationsValidator.class)
public @interface ValidSIBLDuplicateCommodityCombinations {
    String message() default "Duplicate commodity found. Combination already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
