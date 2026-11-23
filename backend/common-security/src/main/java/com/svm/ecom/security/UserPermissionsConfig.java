package com.svm.ecom.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration class for managing user permissions in a table-driven manner.
 * This allows dynamic configuration of which users can access which API endpoints.
 */
@Component
@ConfigurationProperties(prefix = "ecom.security.permissions")
public class UserPermissionsConfig {

    private Map<String, UserPermission> users = new HashMap<>();
    private List<String> defaultPermissions;

    public Map<String, UserPermission> getUsers() {
        return users;
    }

    public void setUsers(Map<String, UserPermission> users) {
        this.users = users;
    }

    public List<String> getDefaultPermissions() {
        return defaultPermissions;
    }

    public void setDefaultPermissions(List<String> defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }

    /**
     * Get permissions for a specific user
     */
    public UserPermission getUserPermission(String username) {
        return users.getOrDefault(username, createDefaultUserPermission());
    }

    /**
     * Check if a user has permission to access a specific endpoint
     */
    public boolean hasPermission(String username, String endpoint) {
        UserPermission userPermission = getUserPermission(username);
        return userPermission.getEndpoints().contains(endpoint) || 
               userPermission.getEndpoints().contains("*");
    }

    /**
     * Check if a user can see a specific API group in Swagger
     */
    public boolean canViewSwaggerGroup(String username, String apiGroup) {
        UserPermission userPermission = getUserPermission(username);
        return userPermission.getSwaggerGroups().contains(apiGroup) || 
               userPermission.getSwaggerGroups().contains("*");
    }

    private UserPermission createDefaultUserPermission() {
        UserPermission defaultPerm = new UserPermission();
        if (defaultPermissions != null) {
            defaultPerm.setEndpoints(defaultPermissions);
            // By default, users can see swagger groups for endpoints they have access to
            defaultPerm.setSwaggerGroups(defaultPermissions);
        }
        return defaultPerm;
    }

    /**
     * Inner class representing user permissions
     */
    public static class UserPermission {
        private List<String> endpoints;
        private List<String> swaggerGroups;
        private String role;
        private boolean enabled = true;

        public List<String> getEndpoints() {
            return endpoints;
        }

        public void setEndpoints(List<String> endpoints) {
            this.endpoints = endpoints;
        }

        public List<String> getSwaggerGroups() {
            return swaggerGroups;
        }

        public void setSwaggerGroups(List<String> swaggerGroups) {
            this.swaggerGroups = swaggerGroups;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
