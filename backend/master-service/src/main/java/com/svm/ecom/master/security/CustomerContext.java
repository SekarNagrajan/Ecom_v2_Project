package com.svm.ecom.master.security;

import com.svm.ecom.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

@Component("masterCustomerContext")
public class CustomerContext {

    private final JwtUtil jwtUtil;

    public CustomerContext(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String extractCustomerCodeFromToken(String token) {
        try {
            jwtUtil.validateTokenWithSession(token);

            Claims claims = extractClaimsFromToken(token);
            Object customerCodeObj = claims.get("customerCode");

            if (customerCodeObj == null) {
                throw new SecurityException("customerCode claim missing in token");
            }

            return String.valueOf(customerCodeObj);

        } catch (SecurityException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new SecurityException("JWT validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new SecurityException("Token processing failed: " + e.getMessage());
        }
    }

    /**
     * Validates the token and extracts the customerCode claim.
     * Returns null if the token is a global auth token (no customerCode claim)
     * instead of throwing an exception.
     */
    public String extractCustomerCodeOrNull(String token) {
        try {
            jwtUtil.validateTokenWithSession(token);

            Claims claims = extractClaimsFromToken(token);
            Object customerCodeObj = claims.get("customerCode");

            if (customerCodeObj == null) {
                return null;
            }

            String code = String.valueOf(customerCodeObj).trim();
            return code.isEmpty() ? null : code;

        } catch (SecurityException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new SecurityException("JWT validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new SecurityException("Token processing failed: " + e.getMessage());
        }
    }

    private Claims extractClaimsFromToken(String token) {
        try {
            java.lang.reflect.Method getClaimsMethod = JwtUtil.class.getDeclaredMethod("getClaims", String.class);
            getClaimsMethod.setAccessible(true);
            return (Claims) getClaimsMethod.invoke(jwtUtil, token);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract claims from token: " + e.getMessage());
        }
    }
}
