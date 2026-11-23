package com.svm.ecom.tracking.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for converting various date formats to ISO 8601 format.
 * Handles multiple input date formats from external tracking API.
 */
public class DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    /**
     * Target ISO 8601 format: yyyy-MM-dd'T'HH:mm:ss
     */
    public static final String ISO_OUTPUT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern(ISO_OUTPUT_FORMAT);

    /**
     * List of input date formats to try parsing (order matters - most specific first)
     */
    private static final List<DateTimeFormatter> INPUT_FORMATTERS = Arrays.asList(
            // Format: "10-Mar-2024 02:48" (dd-MMM-yyyy HH:mm)
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm", Locale.ENGLISH),
            
            // Format: "2024-01-20 07:18:00.0" (yyyy-MM-dd HH:mm:ss.S)
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S", Locale.ENGLISH),
            
            // Format: "2024-03-09 02:48:00" (yyyy-MM-dd HH:mm:ss)
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH),
            
            // Format: "2024-01-21 05:20:00.0" with multiple decimal places
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH),
            
            // Additional common formats
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH)
    );

    /**
     * Private constructor to prevent instantiation
     */
    private DateUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Converts a date string from various formats to ISO 8601 format.
     * 
     * @param dateString the input date string in any supported format
     * @return the date string in ISO 8601 format (yyyy-MM-dd'T'HH:mm:ss), 
     *         or the original string if parsing fails or input is null/empty
     */
    public static String toIsoFormat(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return dateString;
        }

        String trimmedDate = dateString.trim();

        // Check if already in ISO format
        if (isIsoFormat(trimmedDate)) {
            return trimmedDate;
        }

        // Try each formatter until one succeeds
        for (DateTimeFormatter formatter : INPUT_FORMATTERS) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(trimmedDate, formatter);
                String result = dateTime.format(OUTPUT_FORMATTER);
                logger.debug("Successfully converted date '{}' to ISO format '{}'", dateString, result);
                return result;
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }

        // If no formatter worked, log warning and return original
        logger.warn("Unable to parse date '{}' with any known format. Returning original value.", dateString);
        return dateString;
    }

    /**
     * Checks if the date string is already in ISO 8601 format.
     * 
     * @param dateString the date string to check
     * @return true if the string matches ISO 8601 format pattern
     */
    private static boolean isIsoFormat(String dateString) {
        // Pattern: yyyy-MM-ddTHH:mm:ss
        return dateString.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");
    }

    /**
     * Batch converts multiple date strings to ISO format.
     * Useful for converting arrays of dates efficiently.
     * 
     * @param dateStrings array of date strings
     * @return array of converted date strings in ISO format
     */
    public static String[] toIsoFormat(String[] dateStrings) {
        if (dateStrings == null) {
            return null;
        }
        
        String[] result = new String[dateStrings.length];
        for (int i = 0; i < dateStrings.length; i++) {
            result[i] = toIsoFormat(dateStrings[i]);
        }
        return result;
    }
}

