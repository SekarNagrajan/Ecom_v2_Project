package com.svm.ecom.security.decommissioned;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Toggle decommissioned API blocking at runtime (filter layer).
 * OpenAPI exclusions for Booking V2 CRUD are applied regardless when the filter bean is active.
 */
@ConfigurationProperties(prefix = "ecom.api.decommissioned")
public class DecommissionedEndpointProperties {

    /**
     * Master switch for {@link DecommissionedEndpointGuardFilter}.
     */
    private boolean enabled = true;

    /**
     * Block GET/POST {@code /api/v2/bookings} and GET/PUT {@code /api/v2/bookings/{bookingId}}.
     * Default false (re-enabled for TSHG-3000 Phase 2 Booking DG).
     */
    private boolean bookingV2Crud = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isBookingV2Crud() {
        return bookingV2Crud;
    }

    public void setBookingV2Crud(boolean bookingV2Crud) {
        this.bookingV2Crud = bookingV2Crud;
    }
}
