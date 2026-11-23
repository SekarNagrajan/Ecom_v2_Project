package com.solverminds.booking.dto.validation;

import com.solverminds.booking.dto.BookingRequestDTO;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator that ensures templateName is provided when status is 'T' (Template).
 * Note: Template status (T) has been removed from BookingStatus enum.
 * This validator is kept for backward compatibility but always returns true.
 */
public class ConditionalTemplateNameRequiredValidator implements ConstraintValidator<ConditionalTemplateNameRequired, BookingRequestDTO> {

    @Override
    public void initialize(ConditionalTemplateNameRequired constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(BookingRequestDTO bookingRequest, ConstraintValidatorContext context) {
        // Template status (T) has been removed from BookingStatus enum
        // Valid statuses are now: D (ECOM Draft), S (Submitted), P (In-Progress), 
        // C (Confirmed), L (Cancelled), V (Rejected)
        return true;
    }
}
