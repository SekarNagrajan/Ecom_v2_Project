package com.solverminds.booking.dto.validation;

import com.solverminds.booking.dto.BookingRequestDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Rejects duplicate commodity lines when the composite key
 * {@code containerNo|commodityGroup|hsCode|packageType} matches (SI-aligned).
 */
public class NoDuplicateCommodityCombinationsValidator
        implements ConstraintValidator<ValidNoDuplicateCommodityCombinations, BookingRequestDTO> {

    static final String MESSAGE_PREFIX = "Duplicate commodity found. Combination already exists";

    @Override
    public void initialize(ValidNoDuplicateCommodityCombinations constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(BookingRequestDTO booking, ConstraintValidatorContext context) {
        if (booking == null) {
            return true;
        }

        List<BookingDuplicateFieldCollector.IndexedFieldDuplicate> duplicates =
                BookingDuplicateFieldCollector.findCommodityCombinationDuplicates(booking);
        if (duplicates.isEmpty()) {
            return true;
        }

        BookingDuplicateFieldCollector.addFieldViolations(context, MESSAGE_PREFIX, duplicates);
        return false;
    }
}
