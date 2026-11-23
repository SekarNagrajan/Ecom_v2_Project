package com.solverminds.booking.dto.v2.vgm;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Outcome of legacy-style tare resolution for VGM validation (profile + equipment fallback + SOC gate).
 */
public final class TareResolutionResult {

    public enum Kind {
        /** Positive tare in kg — enforce VGM &gt; tare. */
        NUMERIC,
        /** Shipper-owned line without TNB/TBN; legacy does not apply strict tare check. */
        SKIPPED_SOC,
        /** No usable tare after allowed lookups — cannot validate VGM. */
        UNAVAILABLE,
        /** Profile had a non-empty tare that is not positive / not numeric and equipment fallback is blocked. */
        INVALID_PROFILE_TARE
    }

    private final Kind kind;
    private final BigDecimal tareKg;

    private TareResolutionResult(Kind kind, BigDecimal tareKg) {
        this.kind = kind;
        this.tareKg = tareKg;
    }

    public static TareResolutionResult numeric(BigDecimal tareKg) {
        return new TareResolutionResult(Kind.NUMERIC, tareKg);
    }

    public static TareResolutionResult skippedSoc() {
        return new TareResolutionResult(Kind.SKIPPED_SOC, null);
    }

    public static TareResolutionResult unavailable() {
        return new TareResolutionResult(Kind.UNAVAILABLE, null);
    }

    public static TareResolutionResult invalidProfileTare() {
        return new TareResolutionResult(Kind.INVALID_PROFILE_TARE, null);
    }

    public Kind getKind() {
        return kind;
    }

    public Optional<BigDecimal> getTareKg() {
        return Optional.ofNullable(tareKg);
    }
}
