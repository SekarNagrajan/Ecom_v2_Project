package com.solverminds.booking.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomerTypeValidator.class)
@Documented
public @interface ValidCustomerType {
    String message() default "customerCode is required when customerType is BP or AP";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
