package com.solverminds.sibl.security;

import com.svm.ecom.security.JwtUtil; // from common-security
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component("siblCustomerContext")
public class CustomerContext {

    private JwtUtil jwtUtil;

    @Autowired(required = false)
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Extract and validate JWT token, then extract customerCode claim
     */
    public String extractCustomerCodeFromToken(String token) {
        // Fallback mode for development - return a default customer code
        if (jwtUtil == null) {
            System.out.println("WARNING: JwtUtil not available, using fallback customer code for development");
            return "DEFAULT_CUSTOMER";
        }
        
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
        // Fallback mode for development
        if (jwtUtil == null) {
            System.out.println("WARNING: JwtUtil not available, using fallback login for development");
            return "DEFAULT_USER";
        }
        
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
        if (jwtUtil == null) {
            throw new RuntimeException("JwtUtil not available for token processing");
        }
        
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
