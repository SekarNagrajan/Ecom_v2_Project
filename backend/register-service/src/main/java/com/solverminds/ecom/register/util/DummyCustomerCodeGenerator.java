package com.solverminds.ecom.register.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Year;
import java.util.regex.Pattern;

/**
 * Generates provisional customer codes in the format D-YYYY-NNNN.
 */
@Component
public class DummyCustomerCodeGenerator {

    public static final Pattern CODE_PATTERN = Pattern.compile("^D-\\d{4}-\\d{4}$");

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generate() {
        int year = Year.now().getValue();
        int suffix = SECURE_RANDOM.nextInt(10_000);
        return String.format("D-%d-%04d", year, suffix);
    }
}
