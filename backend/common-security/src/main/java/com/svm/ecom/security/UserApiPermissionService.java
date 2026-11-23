package com.svm.ecom.security;

import com.svm.ecom.domain.user.UserApiPermissionRecord;
import com.svm.ecom.infrastructure.user.UserApiPermissionJdbcRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves API endpoint permissions for internal-auth users.
 * Uses JDBC repository + in-memory cache (booking-style: repo query, service cache).
 * Users with no active rows bypass {@link com.svm.ecom.security.shippeo.ShippeoApiAuthorizationGuardFilter}.
 */
@Service
public class UserApiPermissionService {

    private final UserApiPermissionJdbcRepository permissionRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final ConcurrentHashMap<String, List<UserApiPermissionRecord>> permissionCache =
            new ConcurrentHashMap<>();

    public UserApiPermissionService(UserApiPermissionJdbcRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    /**
     * @return {@code true} when the user has at least one active permission row ({@code access_flag = 'Y'}).
     */
    public boolean hasEnabledPermissions(String username) {
        return !getEnabledPermissions(username).isEmpty();
    }

    /**
     * @return {@code true} when the request path matches a stored permission endpoint.
     *         A matched endpoint grants access for all HTTP methods. The {@code requestMethod}
     *         argument is retained for API compatibility but is not used for matching.
     */
    public boolean isRequestAllowed(String username, String requestPath, String requestMethod) {
        List<UserApiPermissionRecord> permissions = getEnabledPermissions(username);
        if (permissions.isEmpty()) {
            return true;
        }

        for (UserApiPermissionRecord permission : permissions) {
            if (matchesEndpoint(requestPath, permission.getApiEndpoint())) {
                return true;
            }
        }
        return false;
    }

    public List<UserApiPermissionRecord> getEnabledPermissions(String username) {
        if (!StringUtils.hasText(username)) {
            return Collections.emptyList();
        }
        String cacheKey = username.trim().toUpperCase();
        return permissionCache.computeIfAbsent(cacheKey,
                key -> permissionRepository.findActiveByUsername(username.trim()));
    }

    /** Clears cached permissions (e.g. after admin updates). */
    public void clearCache() {
        permissionCache.clear();
    }

    public void evictUser(String username) {
        if (StringUtils.hasText(username)) {
            permissionCache.remove(username.trim().toUpperCase());
        }
    }

    private boolean matchesEndpoint(String requestPath, String apiEndpoint) {
        if (!StringUtils.hasText(requestPath) || !StringUtils.hasText(apiEndpoint)) {
            return false;
        }
        return pathMatcher.match(apiEndpoint.trim(), requestPath);
    }
}
