package com.svm.ecom.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Customer Gate status : "
    + "N \u2013 New Customer, E \u2013 Expired User, I \u2013 Pending with Customer, "
    + "P \u2013 Pending with Agency, M \u2013 Pending with Line, R \u2013 Rejected, Y \u2013 Accepted "
    + "", enumAsRef = true, nullable = true)
public enum GateStatus {

    @Schema(description = "N")
    N("N"),

    @Schema(description = "E")
    E("E"),

    @Schema(description = "I")
    I("I"),

    @Schema(description = "P")
    P("P"),

    @Schema(description = "M")
    M("M"),

    @Schema(description = "R")
    R("R"),

    @Schema(description = "Y")
    Y("Y");

    private final String code;

    GateStatus(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }

    /**
     * Strict parse for the {@code gateStatus} query parameter: throws for any value that
     * isn't exactly one of N, E, I, P, M, R, Y (case-insensitive). The controller maps this
     * to HTTP 400.
     */
    @JsonCreator
    public static GateStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String trimmed = value.trim();
        for (GateStatus s : values()) {
            if (s.code.equalsIgnoreCase(trimmed)) {
                return s;
            }
        }
        throw new IllegalArgumentException(
            "Invalid gateStatus value: " + value + ". Valid values are: N, E, I, P, M, R, Y");
    }

    /**
     * The single T-SQL CASE expression used by {@code CustomerGateRepository} inside the
     * {@code gate} CTE to compute {@code gateStatus}. References the {@code e} alias
     * (eCom_Reg_Details) only \u2014 no agency clause anywhere, per DATA SCOPE (default LINE
     * user, no agency concept).
     */
    public static String caseExpression() {
        return "CASE "
            + "WHEN e.isValid = 'N' AND e.status = 'A' AND e.type = 'N' THEN 'N' "
            + "WHEN (e.isValid = 'X' AND e.status = 'V') OR (e.isValid = 'Y' AND e.status = 'A') THEN 'Y' "
            + "WHEN e.isValid = 'R' THEN 'R' "
            + "WHEN e.isValid = 'P' AND e.status IN ('A','L') THEN 'P' "
            + "WHEN e.isValid = 'M' AND e.status = 'A' THEN 'M' "
            + "WHEN e.isValid = 'I' THEN 'I' "
            + "WHEN e.isValid = 'E' THEN 'E' "
            + "ELSE NULL END";
    }

    /**
     * Java-side mirror of {@link #caseExpression()} so the mapping can be unit-tested
     * without a database connection (DoD: "each of 7 status letters \u2192 correct predicate
     * set"). MUST stay in sync with {@link #caseExpression()}.
     *
     * @return the derived status, or {@code null} if no branch matches (the row would be
     *         excluded server-side by {@code WHERE gateStatus IS NOT NULL}).
     */
    public static GateStatus deriveFrom(String type, String isValid, String status) {
        if ("N".equalsIgnoreCase(isValid) && "A".equalsIgnoreCase(status) && "N".equalsIgnoreCase(type)) {
            return N;
        }
        if (("X".equalsIgnoreCase(isValid) && "V".equalsIgnoreCase(status))
                || ("Y".equalsIgnoreCase(isValid) && "A".equalsIgnoreCase(status))) {
            return Y;
        }
        if ("R".equalsIgnoreCase(isValid)) {
            return R;
        }
        if ("P".equalsIgnoreCase(isValid) && ("A".equalsIgnoreCase(status) || "L".equalsIgnoreCase(status))) {
            return P;
        }
        if ("M".equalsIgnoreCase(isValid) && "A".equalsIgnoreCase(status)) {
            return M;
        }
        if ("I".equalsIgnoreCase(isValid)) {
            return I;
        }
        if ("E".equalsIgnoreCase(isValid)) {
            return E;
        }
        return null;
    }
}
