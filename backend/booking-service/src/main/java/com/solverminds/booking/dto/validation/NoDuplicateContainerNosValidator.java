package com.solverminds.booking.dto.validation;

import com.solverminds.booking.dto.BookingRequestDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Validates unique {@code containerNo} in {@code containers[]} only.
 * Multiple commodity rows may share the same {@code containerNo} (SI-aligned).
 */
public class NoDuplicateContainerNosValidator implements ConstraintValidator<ValidNoDuplicateContainerNos, BookingRequestDTO> {

    static final String MESSAGE_PREFIX = "Duplicate container number(s) found";

    @Override
    public void initialize(ValidNoDuplicateContainerNos constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(BookingRequestDTO booking, ConstraintValidatorContext context) {
        if (booking == null) {
            return true;
        }

        List<BookingDuplicateFieldCollector.IndexedFieldDuplicate> duplicates =
                BookingDuplicateFieldCollector.findContainerNoDuplicates(booking);
        if (duplicates.isEmpty()) {
            return true;
        }

        BookingDuplicateFieldCollector.addFieldViolations(context, MESSAGE_PREFIX, duplicates);
        return false;
    }
}
