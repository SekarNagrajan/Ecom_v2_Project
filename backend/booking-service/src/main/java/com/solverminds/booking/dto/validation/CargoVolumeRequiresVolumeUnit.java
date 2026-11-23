package com.solverminds.booking.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When {@code cargoVolume} is non-null, the volume unit must be provided (aligns with ECOM {@code volumnUnit}).
 */
@Documented
@Constraint(validatedBy = CargoVolumeRequiresVolumeUnitValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CargoVolumeRequiresVolumeUnit {
    String message() default "Volume unit is required when cargo volume is provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
