package com.svm.ecom.rates.dto.mrg;

import java.util.regex.Pattern;

/**
 * MRG No ({@code MRGMaster.mrgCode}) format derived from live Commercial data:
 * every exposed code is length 12 — letter {@code M} or {@code F} followed by exactly 11 digits
 * (881 {@code M…} + 2 {@code F…} rows; no other shapes).
 */
public final class MrgNoFormat {

    /** Matches all current MRGMaster.mrgCode values (e.g. M25070800001, F20100900001). */
    public static final Pattern PATTERN = Pattern.compile("^[MF][0-9]{11}$");

    public static final String EXPECTED_FORMAT = "M or F followed by 11 digits (e.g. M25070800001)";

    public static final int MAX_ECHO_LENGTH = 50;

    private MrgNoFormat() {
    }

    public static boolean isValid(String mrgNo) {
        return mrgNo != null && PATTERN.matcher(mrgNo).matches();
    }

    /** Truncate untrusted input before putting it in an error message. */
    public static String safeEcho(String value) {
        if (value == null) {
            return "";
        }
        return value.length() <= MAX_ECHO_LENGTH ? value : value.substring(0, MAX_ECHO_LENGTH);
    }
}
