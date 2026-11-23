package com.svm.ecom.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
/**
 * Utility for converting UTC timestamps to port local time with timezone offset.
 * Output format is ISO 8601 / RFC 3339 (e.g. 2023-11-23T14:30:00+05:30).
 * Used for schedule and other API responses where arrdate/depdate (or equivalent)
 * must be returned in the port's local time.
 */
public final class PortLocalTimeUtil {

    private static final DateTimeFormatter ISO_OFFSET = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private PortLocalTimeUtil() {}

    /**
     * Convert a UTC instant to port local time and format as ISO 8601 with offset.
     *
     * @param utcInstant the instant in UTC (e.g. from database storage)
     * @param timezoneStr IANA zone ID (e.g. "Asia/Colombo") or offset (e.g. "+05:30"); null or empty = return UTC with Z
     * @return formatted string e.g. "2023-11-23T14:30:00+05:30", or "2023-11-23T09:00:00Z" if timezone is null/empty
     */
    public static String formatUtcToPortLocal(Instant utcInstant, String timezoneStr) {
        if (utcInstant == null) return null;
        if (timezoneStr == null || timezoneStr.trim().isEmpty()) {
            return utcInstant.toString();
        }
        String trimmed = timezoneStr.trim();
        try {
            ZoneId zone = resolveZoneId(trimmed);
            return utcInstant.atZone(zone).format(ISO_OFFSET);
        } catch (Exception e) {
            return utcInstant.toString();
        }
    }

    /**
     * Convert a LocalDateTime interpreted as UTC to port local time and format as ISO 8601 with offset.
     *
     * @param utcDateTime the date-time in UTC (e.g. from DB as local representation of UTC)
     * @param timezoneStr IANA zone ID or offset; null or empty = return UTC with Z
     * @return formatted string with offset, or UTC with Z if timezone is null/empty
     */
    public static String formatUtcToPortLocal(LocalDateTime utcDateTime, String timezoneStr) {
        if (utcDateTime == null) return null;
        return formatUtcToPortLocal(utcDateTime.atZone(ZoneOffset.UTC).toInstant(), timezoneStr);
    }

    /**
     * Convert a UTC instant to port local time as OffsetDateTime.
     *
     * @param utcInstant the instant in UTC (e.g. from database or API)
     * @param timezoneStr IANA zone ID (e.g. "Asia/Colombo") or offset (e.g. "+05:30"); null or empty = UTC
     * @return OffsetDateTime in the given zone, or at UTC if timezone is null/empty
     */
    public static OffsetDateTime toOffsetDateTime(Instant utcInstant, String timezoneStr) {
        if (utcInstant == null) return null;
        if (timezoneStr == null || timezoneStr.trim().isEmpty()) {
            return utcInstant.atOffset(ZoneOffset.UTC);
        }
        try {
            ZoneId zone = resolveZoneId(timezoneStr.trim());
            return utcInstant.atZone(zone).toOffsetDateTime();
        } catch (Exception e) {
            return utcInstant.atOffset(ZoneOffset.UTC);
        }
    }

    /**
     * Label a date-time with a timezone offset WITHOUT converting/shifting the time.
     * Use when the DB stores dates already in port local time (not UTC).
     * The time values remain unchanged; only the offset label is attached.
     *
     * @param dateTime OffsetDateTime (typically labeled as UTC from the mapper, but the actual value is port local)
     * @param timezoneStr IANA zone ID or offset (e.g. "+08:00"); null or empty = return as-is
     * @return OffsetDateTime with the same date/time but the correct offset label
     */
    public static OffsetDateTime labelWithOffset(OffsetDateTime dateTime, String timezoneStr) {
        if (dateTime == null) return null;
        if (timezoneStr == null || timezoneStr.trim().isEmpty()) return dateTime;
        try {
            ZoneId zone = resolveZoneId(timezoneStr.trim());
            LocalDateTime ldt = dateTime.toLocalDateTime();
            return ldt.atZone(zone).toOffsetDateTime();
        } catch (Exception e) {
            return dateTime;
        }
    }

    /**
     * Label an Instant (whose UTC date/time is actually port local time from DB) with a timezone offset
     * WITHOUT converting/shifting the time.
     *
     * @param instant Instant whose date/time components (interpreted at UTC) represent port local time
     * @param timezoneStr IANA zone ID or offset; null or empty = return at UTC
     * @return OffsetDateTime with the same date/time but the correct offset label
     */
    public static OffsetDateTime labelWithOffset(java.time.Instant instant, String timezoneStr) {
        if (instant == null) return null;
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        if (timezoneStr == null || timezoneStr.trim().isEmpty()) {
            return ldt.atOffset(ZoneOffset.UTC);
        }
        try {
            ZoneId zone = resolveZoneId(timezoneStr.trim());
            return ldt.atZone(zone).toOffsetDateTime();
        } catch (Exception e) {
            return ldt.atOffset(ZoneOffset.UTC);
        }
    }

    /**
     * Parse a date or date-time string (UTC or no offset) and return as OffsetDateTime in the given port timezone.
     * Handles date-only "yyyy-MM-dd" (start of day in port) and date-time strings.
     *
     * @param dateTimeStr date or date-time string (no offset)
     * @param timezoneStr IANA zone ID or offset for the port
     * @return OffsetDateTime in port zone, or null if input null/empty or unparseable
     */
    public static OffsetDateTime parseToOffsetDateTimeInPort(String dateTimeStr, String timezoneStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return null;
        if (timezoneStr == null || timezoneStr.trim().isEmpty()) {
            Instant inst = parseToInstant(dateTimeStr);
            return inst != null ? inst.atOffset(ZoneOffset.UTC) : null;
        }
        String s = dateTimeStr.trim();
        ZoneId zone = resolveZoneId(timezoneStr.trim());
        try {
            if (s.length() <= 10) {
                return LocalDate.parse(s).atStartOfDay(zone).toOffsetDateTime();
            }
            Instant inst = parseToInstant(s);
            return inst != null ? inst.atZone(zone).toOffsetDateTime() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /** Accepts "yyyy-MM-dd HH:mm:ss" or "yyyy-MM-dd HH:mm:ss.S" (e.g. from external schedule API). */
    private static final DateTimeFormatter SPACE_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter SPACE_DATETIME_FRAC = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    /**
     * Parse an ISO date-time string to Instant. If the string has an offset (or Z), use it; otherwise treat as UTC.
     * Also accepts external API format with space and fraction: "2026-03-03 17:00:00.0".
     *
     * @param dateTimeStr ISO string e.g. "2026-03-02T17:00:00", "2026-03-03 17:00:00.0", or "2026-03-02T17:00:00+08:00"
     * @return Instant or null if input is null/empty or unparseable
     */
    public static Instant parseToInstant(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return null;
        String s = dateTimeStr.trim();
        try {
            if (s.endsWith("Z") || s.contains("+") || (s.contains("-") && s.lastIndexOf('-') > 10)) {
                return OffsetDateTime.parse(s).toInstant();
            }
            if (s.contains(" ")) {
                LocalDateTime ldt;
                if (s.contains(".")) {
                    try {
                        ldt = LocalDateTime.parse(s, SPACE_DATETIME_FRAC);
                    } catch (DateTimeParseException e) {
                        ldt = LocalDateTime.parse(s.substring(0, s.indexOf('.')), SPACE_DATETIME);
                    }
                } else {
                    ldt = LocalDateTime.parse(s, SPACE_DATETIME);
                }
                return ldt.atZone(ZoneOffset.UTC).toInstant();
            }
            return LocalDateTime.parse(s).atZone(ZoneOffset.UTC).toInstant();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Format a date or date-time string in origin port local time with offset.
     * Used for cutoff: concatenates date with time (or uses start of day if date-only) in the given port zone.
     *
     * @param dateTimeStr date "yyyy-MM-dd" or date-time "yyyy-MM-ddTHH:mm:ss" (no offset)
     * @param timezoneStr IANA zone ID or offset for the origin port
     * @return ISO 8601 with offset e.g. "2025-12-07T20:00:00+08:00", or null if input/timezone null or unparseable
     */
    public static String formatDateOrDateTimeInPort(String dateTimeStr, String timezoneStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return null;
        if (timezoneStr == null || timezoneStr.trim().isEmpty()) return dateTimeStr;
        String s = dateTimeStr.trim();
        ZoneId zone = resolveZoneId(timezoneStr.trim());
        try {
            if (s.length() <= 10) {
                LocalDate date = LocalDate.parse(s);
                return date.atStartOfDay(zone).format(ISO_OFFSET);
            }
            if (s.endsWith("Z") || s.matches(".*[+-]\\d{2}:?\\d{2}$")) {
                Instant inst = parseToInstant(s);
                return inst != null ? formatUtcToPortLocal(inst, timezoneStr) : null;
            }
            LocalDateTime ldt;
            if (s.contains(" ")) {
                if (s.contains(".")) {
                    try {
                        ldt = LocalDateTime.parse(s, SPACE_DATETIME_FRAC);
                    } catch (DateTimeParseException e) {
                        ldt = LocalDateTime.parse(s.substring(0, s.indexOf('.')), SPACE_DATETIME);
                    }
                } else {
                    ldt = LocalDateTime.parse(s, SPACE_DATETIME);
                }
            } else {
                ldt = LocalDateTime.parse(s);
            }
            return ldt.atZone(zone).format(ISO_OFFSET);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Normalize timezone value from DB. DB may store hour offset as number (e.g. "8", "5.5", "-5").
     * Returns ISO offset string (e.g. "+08:00") so that resolveZoneId/formatUtcToPortLocal work correctly.
     * If value already starts with + or -, returns as-is. If not a number, returns as-is (e.g. IANA zone).
     */
    public static String normalizeTimezoneValue(String value) {
        if (value == null || value.trim().isEmpty()) return value;
        String t = value.trim();
        if (t.startsWith("+") || t.startsWith("-")) return t;
        try {
            String numStr = t.replace(',', '.');
            double hours = Double.parseDouble(numStr);
            int h = (int) hours;
            int m = (int) Math.round((hours - h) * 60);
            if (m == 60) { m = 0; h += 1; } else if (m == -60) { m = 0; h -= 1; }
            if (h >= 0) return String.format("+%02d:%02d", h, Math.abs(m));
            return String.format("-%02d:%02d", -h, Math.abs(m));
        } catch (NumberFormatException e) {
            return t;
        }
    }

    /**
     * Resolve a timezone string to ZoneId. Accepts IANA (e.g. "Asia/Colombo") or offset (e.g. "+05:30").
     */
    public static ZoneId resolveZoneId(String timezoneStr) {
        if (timezoneStr == null || timezoneStr.trim().isEmpty()) {
            return ZoneOffset.UTC;
        }
        String t = timezoneStr.trim();
        try {
            if (t.startsWith("+") || t.startsWith("-")) {
                return parseOffset(t);
            }
            return ZoneId.of(t);
        } catch (Exception e) {
            try {
                return ZoneId.of(t);
            } catch (Exception e2) {
                return ZoneOffset.UTC;
            }
        }
    }

    private static ZoneOffset parseOffset(String offsetStr) {
        if (offsetStr == null || offsetStr.isEmpty()) return ZoneOffset.UTC;
        if ("Z".equals(offsetStr) || "+00:00".equals(offsetStr)) return ZoneOffset.UTC;
        try {
            int sign = offsetStr.startsWith("-") ? -1 : 1;
            String rest = offsetStr.substring(1).trim();
            if (rest.isEmpty()) return ZoneOffset.UTC;
            String[] parts = rest.split(":");
            int hours = parts.length > 0 ? Integer.parseInt(parts[0].trim()) : 0;
            int minutes = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 0;
            return ZoneOffset.ofHoursMinutes(sign * hours, sign * minutes);
        } catch (Exception e) {
            return ZoneOffset.UTC;
        }
    }
}
