package com.solverminds.booking.security;

import com.svm.ecom.security.JwtUtil; // from common-security
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component("bookingCustomerContext")
public class CustomerContext {

    private final JwtUtil jwtUtil;

    public CustomerContext(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * For booking-detail GET endpoints. Returns {@code null} for internal-auth SYSUSER tokens
     * (no {@code customerCode} claim). All other tokens use {@link #extractCustomerCodeFromToken}.
     */
    public String resolveCustomerCodeForBookingDetailRead(String token) {
        jwtUtil.validateTokenWithSession(token);
        if (jwtUtil.isSysUserReadOnlyToken(token)) {
            return null;
        }
        return extractCustomerCodeFromToken(token);
    }

    /**
     * Extract and validate JWT token, then extract customerCode claim
     */
    public String extractCustomerCodeFromToken(String token) {
        try {
            // 1. Validate the token first
            jwtUtil.validateTokenWithSession(token);
            
            // 2. Get the claims to extract customerCode
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
     * Extract login/username from JWT token claims.
     * Tries common claim keys in priority order.
     */
    public String extractLoginFromToken(String token) {
        try {
            // 1. Validate the token first
            jwtUtil.validateTokenWithSession(token);
            
            // 2. Get all claims as a Map
            Map<String, Object> claims = getAllClaims(token);
            
            // 3. Try common keys in order of preference
            String[] keys = {"preferred_username", "username", "login", "name", "sub"};
            for (String key : keys) {
                Object value = claims.get(key);
                if (value != null && value.toString().trim().length() > 0) {
                    return value.toString().trim();
                }
            }
            
            // 4. Fallback to customerCode if absolutely nothing else
            String fallback = (String) claims.getOrDefault("customerCode", "");
            return (fallback == null ? "" : fallback.trim());
            
        } catch (SecurityException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new SecurityException("JWT validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new SecurityException("Token processing failed: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to extract claims from JWT token using reflection
     * since getClaims is private in JwtUtil
     */
    private Claims extractClaimsFromToken(String token) {
        try {
            // Use reflection to access the private getClaims method
            java.lang.reflect.Method getClaimsMethod = JwtUtil.class.getDeclaredMethod("getClaims", String.class);
            getClaimsMethod.setAccessible(true);
            return (Claims) getClaimsMethod.invoke(jwtUtil, token);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract claims from token: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to get all claims as a Map for easier access
     */
    private Map<String, Object> getAllClaims(String token) {
        Claims claims = extractClaimsFromToken(token);
        return claims;
    }
}
