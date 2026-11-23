package com.solverminds.sibl.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Container-specific business rules (aligned with attached rules and booking-service):
 * Rule 1–2–4: containerNo required, max 11 chars, must exist in containerprofile with equipmentType.
 * Rule 7–12: Reefer/DG/OOG required when flags set; equipment size in master; cargo+tare ≤ gross capacity; equipment status F/M.
 * Rule 5 (not in another booking) and Rule 6 (unique within SI) are handled by SIValidationService and ValidSIBLDuplicateContainers.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SIBLContainerRulesValidator.class)
public @interface ValidSIBLContainerRules {
    String message() default "Invalid container rules";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
