package com.svm.ecom.schedule.util;

import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

/**
 * Shared date helpers for schedule cutoff calculations.
 * Used by {@link com.svm.ecom.schedule.service.CutoffCalculator} to shift anchor times by configured hours.
 */
public final class DateConversion {

    private DateConversion() {
    }

    /**
     * Parses {@code arrDate} as ISO-8601 {@link OffsetDateTime}, subtracts {@code hours}, and returns
     * an ISO-8601 string for use with {@link OffsetDateTime#parse(String)} in API responses.
     */
    public static String convertsubHours(String arrDate, int hours) {
        if (!StringUtils.hasText(arrDate)) {
            return null;
        }
        try {
            OffsetDateTime odt = OffsetDateTime.parse(arrDate.trim());
            return odt.minusHours(hours).toString();
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
