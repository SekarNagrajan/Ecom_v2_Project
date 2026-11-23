package com.svm.ecom.constants;

/**
 * Constants for standardized date formatting across all DTOs.
 * Ensures consistent ISO-8601 compliance throughout the application.
 */
public final class DateFormatConstants {
    
    /**
     * ISO-8601 date format: YYYY-MM-DD
     * Used for LocalDate fields
     */
    public static final String ISO_DATE = "yyyy-MM-dd";
    
    /**
     * ISO-8601 date-time format: YYYY-MM-DD HH:mm:ss
     * Used for LocalDateTime fields
     */
    public static final String ISO_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * ISO-8601 date-time with timezone format: YYYY-MM-DDTHH:mm:ssXXX
     * Used for ZonedDateTime fields and for port local date-time in API responses
     * (e.g. schedule arrdate/depdate). Example: 2023-11-23T14:30:00+05:30
     */
    public static final String ISO_DATE_TIME_ZONE = "yyyy-MM-dd'T'HH:mm:ssXXX";
    
    /**
     * RFC 3339 UTC date-time format with Z suffix: YYYY-MM-DDTHH:mm:ssZ
     * Used for system-generated fields like Created Date, Updated Date, and Booking Date.
     * These fields are stored in UTC and returned with Z suffix indicating UTC timezone.
     * Example: 2023-11-23T14:30:00Z
     */
    public static final String UTC_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    /**
     * Local date-time format without timezone offset: YYYY-MM-DDTHH:mm:ss
     * Used for internal/local representation. For schedule API response arrdate/depdate,
     * use port local time with offset (ISO_DATE_TIME_ZONE) e.g. 2023-11-23T14:30:00+05:30.
     */
    public static final String LOCAL_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss";
    
    private DateFormatConstants() {
        // Utility class - prevent instantiation
    }
}
