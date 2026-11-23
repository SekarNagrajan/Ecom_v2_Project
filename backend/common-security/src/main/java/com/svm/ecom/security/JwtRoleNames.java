package com.svm.ecom.security;

/**
 * Role values embedded in JWT {@code roles} claims for internal auth tokens
 * ({@code /api/v1/auth/login}). ECOM customer tokens use {@code ecomuserdetails.userRole}
 * (single char) via register-service and are not listed here.
 */
public final class JwtRoleNames {

    public static final String SYSUSER = "SYSUSER";

  
    public static final String CUSTOMER_GATE_READ = "customer-gate:accounts:read";

    private JwtRoleNames() {}
}
