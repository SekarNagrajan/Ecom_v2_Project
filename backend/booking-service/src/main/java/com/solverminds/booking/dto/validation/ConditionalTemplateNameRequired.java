package com.solverminds.booking.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validates that templateName is required when status is 'T' (Template).
 */
@Documented
@Constraint(validatedBy = ConditionalTemplateNameRequiredValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalTemplateNameRequired {
    String message() default "Template name is required when status is T";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
