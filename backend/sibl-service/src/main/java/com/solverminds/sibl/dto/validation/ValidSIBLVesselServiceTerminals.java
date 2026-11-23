package com.solverminds.sibl.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * When serviceType is M (Mainline) or F (Feeder): vessel must exist in vessel master,
 * serviceCode must exist in service master, polTerminal and podTerminal must exist in terminal master (same as Booking RoutePortsValidator).
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SIBLVesselServiceTerminalsValidator.class)
public @interface ValidSIBLVesselServiceTerminals {
    String message() default "Invalid vessel, service code or terminal";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
