package com.solverminds.sibl.util;

import java.util.Collections;
import java.util.List;

/**
 * Common DTO helpers. Use in getters so JSON serialization returns empty string/list instead of null.
 */
public final class DtoUtil {

    private DtoUtil() {}

    /**
     * Returns empty string if the value is null; otherwise returns the value.
     * Use in DTO String getters so that API response shows "" instead of null.
     */
    public static String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    /**
     * Returns an empty list if the value is null; otherwise returns the list.
     * Use in DTO list getters so that API response shows [] instead of null.
     */
    public static <T> List<T> emptyListIfNull(List<T> value) {
        return value == null ? Collections.<T>emptyList() : value;
    }

    /**
     * Returns 0 if the value is null; otherwise returns the value.
     * Use in DTO Integer getters so that API response shows 0 instead of null.
     */
    public static Integer emptyIntIfNull(Integer value) {
        return value == null ? 0 : value;
    }

    /**
     * Returns 0.0 if the value is null; otherwise returns the value.
     * Use in DTO Double getters so that API response shows 0.0 instead of null.
     */
    public static Double emptyDoubleIfNull(Double value) {
        return value == null ? 0.0 : value;
    }

    /**
     * Returns null if the value is null or 0.0 (empty); otherwise returns the value.
     * Use for temperature fields in SI view response (convertEmptyToNull).
     */
    public static Double nullIfEmpty(Double value) {
        return (value == null || value.doubleValue() == 0.0) ? null : value;
    }
}
