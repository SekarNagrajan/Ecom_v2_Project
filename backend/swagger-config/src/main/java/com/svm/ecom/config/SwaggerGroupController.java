package com.svm.ecom.config;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller to provide filtered Swagger group information based on user permissions.
 * This helps hide entire API groups from users who don't have access.
 * 
 * NOTE: DISABLED - This controller conflicts with the new GroupedOpenApi configuration
 * in SwaggerConfig.java. The new configuration handles group management automatically.
 */
@RestController
@RequestMapping("/swagger-resources-old")  // Changed path to avoid conflicts
@Hidden
@Deprecated
@ConditionalOnProperty(name = "swagger.legacy.enabled", havingValue = "true")  // Disabled by default
public class SwaggerGroupController {

    // Note: PermissionService dependency removed as part of UserPermission system disabling

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> swaggerResources() {
        List<Map<String, Object>> resources = new ArrayList<>();
        
        String currentUsername = getCurrentUsername();
        
        // Always include Authentication API
        resources.add(createResource("Authentication API", "/v3/api-docs/Authentication API"));
        
        // Always include Master Data API (public endpoints)
        resources.add(createResource("Master Data API", "/v3/api-docs/Master Data API"));
        
        // Since PermissionService is disabled, show all main API groups for authenticated users
        if (currentUsername != null) {
            resources.add(createResource("Schedule API", "/v3/api-docs/Schedule API"));
            resources.add(createResource("Tracking API", "/v3/api-docs/Tracking API"));
            resources.add(createResource("Vessel API", "/v3/api-docs/Vessel API"));
            resources.add(createResource("Port API", "/v3/api-docs/Port API"));
            
            // Note: Admin API excluded by default for security since permissions are disabled
        }
        
        return ResponseEntity.ok(resources);
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

    private Map<String, Object> createResource(String name, String url) {
        Map<String, Object> resource = new HashMap<>();
        resource.put("name", name);
        resource.put("url", url);
        resource.put("swaggerVersion", "3.0");
        resource.put("location", url);
        return resource;
    }
}
