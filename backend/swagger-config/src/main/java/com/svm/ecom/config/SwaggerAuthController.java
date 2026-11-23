package com.svm.ecom.config;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/**
 * Controller to provide Swagger UI with user permission information.
 * This helps the UI understand which endpoints should be visible after authentication.
 * Hidden from Swagger documentation as these are internal support endpoints.
 * 
 * NOTE: Modified to work without PermissionService (temporarily disabled).
 * Returns default permissions for all authenticated users.
 */
@RestController
@RequestMapping("/swagger-auth")
@Hidden
public class SwaggerAuthController {

    // Note: PermissionService dependency removed as part of UserPermission system disabling

    @GetMapping("/user-permissions")
    public ResponseEntity<Map<String, Object>> getUserPermissions() {
        String username = getCurrentUsername();
        
        Map<String, Object> response = new HashMap<>();
        
        if (username == null) {
            response.put("authenticated", false);
            response.put("username", null);
            response.put("endpointPermissions", Collections.emptySet());
            response.put("swaggerGroupPermissions", Collections.emptySet());
        } else {
            // Since PermissionService is disabled, return default permissions for all authenticated users
            Set<String> endpointPermissions = getDefaultEndpointPermissions();
            Set<String> swaggerGroupPermissions = getDefaultSwaggerGroupPermissions();
            
            response.put("authenticated", true);
            response.put("username", username);
            response.put("endpointPermissions", endpointPermissions);
            response.put("swaggerGroupPermissions", swaggerGroupPermissions);
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth-status")
    public ResponseEntity<Map<String, Object>> getAuthStatus() {
        String username = getCurrentUsername();
        
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", username != null);
        response.put("username", username);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get the current authenticated username using Spring Security
     */
    private String getCurrentUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null && !auth.getName().equals("anonymousUser")) {
                return auth.getName();
            }
        } catch (Exception e) {
            // Log error if needed, but return null for now
        }
        return null;
    }

    /**
     * Return default endpoint permissions for authenticated users
     * Since PermissionService is disabled, grant access to common endpoints
     */
    private Set<String> getDefaultEndpointPermissions() {
        Set<String> permissions = new HashSet<>();
        // Grant access to main endpoint categories by default
        permissions.add("schedule");
        permissions.add("tracking");
        permissions.add("vessel");
        permissions.add("port");
        permissions.add("arrival-notice");
        // Note: admin permissions excluded by default for security
        return permissions;
    }

    /**
     * Return default swagger group permissions for authenticated users
     */
    private Set<String> getDefaultSwaggerGroupPermissions() {
        Set<String> permissions = new HashSet<>();
        // Grant access to main API groups by default
        permissions.add("schedule-api");
        permissions.add("tracking-api");
        permissions.add("ecom-api");
        // Note: admin-api excluded by default for security
        return permissions;
    }
}
