package com.solverminds.booking.dto.v2.vgm;

/**
 * Shared OpenAPI / validation constants for VGM schemas (aligned with customer specification).
 */
public final class VgmSchemaConstants {

    /** Runtime validation accepts any case; values are normalized to uppercase before persistence. */
    public static final String COUNTRY_CODE_PATTERN = "^[A-Za-z]{2}$";
    public static final String COUNTRY_CODE_DESCRIPTION = "The ISO 3166-1 alpha-2 country code.";

    private VgmSchemaConstants() {
    }
}
