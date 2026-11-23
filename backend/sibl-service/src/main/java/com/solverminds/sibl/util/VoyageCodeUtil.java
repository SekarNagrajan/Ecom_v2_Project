package com.solverminds.sibl.util;

/**
 * Normalizes voyage code to numeric-only format for consistent API and storage.
 * Converts combined strings (e.g. "JADRANA/685/E") to the numeric part ("685").
 */
public final class VoyageCodeUtil {

    private VoyageCodeUtil() {}

    /**
     * Normalize voyage code to contain only digits.
     * <ul>
     *   <li>Combined format "PREFIX/NUMBER/SUFFIX" (e.g. "JADRANA/685/E") → "685"</li>
     *   <li>Alphanumeric (e.g. "001E") → digits only "001"</li>
     *   <li>Already numeric (e.g. "685") → unchanged</li>
     * </ul>
     *
     * @param voyageCode raw voyage code from DB or request; may be null or empty
     * @return normalized numeric-only string, or null/empty if input is null/empty
     */
    public static String normalizeToNumeric(String voyageCode) {
        if (voyageCode == null) return null;
        String s = voyageCode.trim();
        if (s.isEmpty()) return "";
        if (s.contains("/")) {
            for (String segment : s.split("/")) {
                String t = segment.trim();
                if (!t.isEmpty() && t.chars().allMatch(Character::isDigit)) {
                    return t;
                }
            }
        }
        return s.replaceAll("\\D+", "");
    }
}
