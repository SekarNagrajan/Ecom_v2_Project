package com.solverminds.booking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * TSHG-3000 DG feature flags.
 * {@code require-dg-when-is-dg} is off at first release (phase-in): missing DG with {@code isDG=true} does not 400.
 */
@Component
@ConfigurationProperties(prefix = "ecom.booking.dangerous-goods")
public class DangerousGoodsProperties {

    /**
     * When true, {@code isDG=true} without {@code dangerousGoodsInfo} yields {@code DG_FLAG_MISMATCH}.
     * Default false for Release N phase-in.
     */
    private boolean requireDgWhenIsDg = false;

    public boolean isRequireDgWhenIsDg() {
        return requireDgWhenIsDg;
    }

    public void setRequireDgWhenIsDg(boolean requireDgWhenIsDg) {
        this.requireDgWhenIsDg = requireDgWhenIsDg;
    }
}
