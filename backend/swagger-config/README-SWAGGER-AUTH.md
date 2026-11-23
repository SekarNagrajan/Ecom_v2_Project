# Swagger UI Authentication and Role-Based Visibility

This document explains how the Swagger UI authentication and role-based endpoint visibility system works in the ECOM API platform.

## Overview

The system implements a sophisticated authentication-aware Swagger UI that:

1. **Hides all endpoints until login** - Only authentication endpoints are visible to unauthenticated users
2. **Shows role-based API groups** - After login, users only see entire API groups they have permission to access
3. **Hides related models** - Models/schemas are only visible for API groups the user can access
4. **Real-time updates** - API group visibility updates when authentication state changes (with page refresh)

## Architecture Components

### 1. ConditionalApiOperationBuilderPlugin

**Location**: `swagger-config/src/main/java/com/svm/ecom/config/ConditionalApiOperationBuilderPlugin.java`

This is the core component that controls endpoint visibility in Swagger UI.

**Key behaviors**:
- If no user is authenticated → Hide ALL endpoints except authentication
- If user is authenticated → Check permissions for each endpoint type
- Uses database-driven permission service to determine access

**Supported endpoint types**:
- `/api/v1/auth/**` - Always visible (public)
- `/api/v1/master/**` - Always visible (public endpoints)
- `/trackingendpoint/**` - Requires `tracking` permission
- `/api/v1/schedule/route` - Requires `schedule` permission  
- `/api/v1/schedule/port` - Requires `port` permission
- `/api/v1/schedule/vessel` - Requires `vessel` permission
- `/api/v1/vessel/**` - Requires `vessel` permission
- `/api/v1/port/**` - Requires `port` permission
- `/api/v1/arrival-notice/**` - Requires `arrival-notice` permission

### 2. SwaggerAuthController

**Location**: `swagger-config/src/main/java/com/svm/ecom/config/SwaggerAuthController.java`

Provides endpoints that Swagger UI can call to understand the current authentication state.

**Endpoints**:
- `GET /swagger-auth/user-permissions` - Returns detailed user permissions
- `GET /swagger-auth/auth-status` - Returns basic authentication status

### 3. SwaggerGroupController

**Location**: `swagger-config/src/main/java/com/svm/ecom/config/SwaggerGroupController.java`

Controls which API groups appear in the Swagger UI dropdown based on user permissions.

**Key behaviors**:
- Always shows "Authentication API" group
- Shows other API groups only if user has appropriate permissions
- Dynamically filters the group list based on authentication state

### 4. Custom JavaScript Enhancement

**Location**: `swagger-config/src/main/resources/static/swagger-ui-custom.js`

Enhances the Swagger UI with:
- Real-time authentication monitoring
- Visual authentication status display
- Automatic UI refresh when auth state changes
- Debug capabilities for troubleshooting

### 5. Security Configuration Updates

**Location**: `common-security/src/main/java/com/svm/ecom/security/SecurityConfig.java`

Updated to allow:
- Swagger UI resources without authentication
- New `/swagger-auth/**` endpoints
- Custom JavaScript files

## User Experience Flow

### 1. Initial Load (No Authentication)
```
User visits Swagger UI
├── Only sees Authentication API endpoints
├── See "🔐 Authentication Required" message
└── All other endpoints are hidden
```

### 2. After Authentication
```
User authenticates via JWT
├── JavaScript detects authentication
├── Shows "Refresh API Groups" button
├── User clicks refresh or refreshes page manually
├── SwaggerGroupController returns filtered API groups
└── UI shows only authorized API groups and their models
```

### 3. Permission-Based Group Visibility
```
For each API group:
├── Check if user is authenticated
├── If yes → Check group-specific permission
├── If permission granted → Show entire API group and models
└── If permission denied → Hide entire API group and models
```

## Configuration

### Database Permissions

The system uses the existing database-driven permission system:

```sql
-- Example permissions in user_permissions table
INSERT INTO user_permissions (username, permission_type, permission_value)
VALUES 
  ('user1', 'ENDPOINT', 'tracking'),
  ('user1', 'ENDPOINT', 'schedule'),
  ('user2', 'ENDPOINT', 'vessel'),
  ('user2', 'ENDPOINT', 'port');
```

### Permission Types

- **tracking** - Access to tracking endpoints
- **schedule** - Access to schedule route endpoints
- **vessel** - Access to vessel-related endpoints
- **port** - Access to port-related endpoints
- **arrival-notice** - Access to arrival notice endpoints

## Security Features

### 1. Endpoint Visibility Security
- Endpoints are hidden at the Swagger documentation level
- Actual API access is still controlled by Spring Security and `@PreAuthorize` annotations
- This provides defense in depth

### 2. JWT Token Integration
- Automatically detects JWT tokens set in Swagger UI
- Passes tokens to permission checking endpoints
- Monitors token expiration and updates UI accordingly

### 3. Real-time Updates
- Checks authentication status every 5 seconds
- Immediately refreshes UI when auth state changes
- Prevents stale endpoint visibility

## Development and Debugging

### Enable Debug Mode
Set `debug: true` in the JavaScript configuration to enable console logging:

```javascript
const config = {
    debug: true  // Enable debug logging
};
```

### Debug Information Available
- Authentication state changes
- Permission check results
- API call details
- UI refresh events

### Browser Console Access
When debug mode is enabled, access debugging functions:

```javascript
// Check current auth status
swaggerAuth.checkAuthStatus()

// Get user permissions
swaggerAuth.getUserPermissions()

// Manually refresh UI
swaggerAuth.refreshSwaggerUI()

// Get current JWT token
swaggerAuth.getAuthToken()
```

## Troubleshooting

### Common Issues

1. **Endpoints not appearing after login**
   - Check database permissions for the user
   - Verify JWT token is properly set in Swagger UI
   - Check browser console for errors

2. **JavaScript not loading**
   - Verify `/swagger-ui-custom.js` is accessible
   - Check security configuration allows static resources
   - Ensure correct base path configuration

3. **Permission checks failing**
   - Verify `PermissionService` is properly injected
   - Check database connectivity
   - Ensure user exists in `ecomapi_users` table

### Monitoring

Monitor the following for proper operation:
- Authentication status endpoint: `/swagger-auth/auth-status`
- User permissions endpoint: `/swagger-auth/user-permissions`
- Browser console for JavaScript errors
- Server logs for permission service errors

## Extending the System

### Adding New Endpoint Types

1. **Update ConditionalApiOperationBuilderPlugin**:
```java
else if (requestMappingPattern.contains("/api/v1/new-endpoint/")) {
    hasPermission = permissionService.hasEndpointPermission("new-endpoint");
}
```

2. **Add permission method to PermissionService** (if needed):
```java
public boolean canAccessNewEndpoint() {
    return hasEndpointPermission("new-endpoint");
}
```

3. **Update SwaggerConfig paths**:
```java
.paths(PathSelectors.regex("...existing...|(/api/v1/new-endpoint/.*)"))
```

### Customizing Authentication Messages

Modify the JavaScript file to customize user messages:

```javascript
function showAuthMessage(message, type = 'info') {
    // Customize message display logic here
}
```

## Best Practices

1. **Always use both Swagger visibility AND endpoint security**
   - Swagger hiding is for UX, not security
   - Actual security is enforced by Spring Security

2. **Keep permissions granular**
   - Use specific permission names for different endpoint types
   - Avoid overly broad permissions

3. **Monitor authentication state**
   - Log authentication changes for security auditing
   - Monitor failed permission checks

4. **Test with different user roles**
   - Verify each role sees only appropriate endpoints
   - Test authentication state transitions

## Performance Considerations

- Permission checks are cached by `DatabasePermissionService`
- JavaScript polling interval is configurable (default 5 seconds)
- Swagger UI refresh is throttled to prevent excessive re-rendering
- Database queries are optimized with proper indexing on user permissions

This implementation provides a seamless, secure, and user-friendly authentication experience in Swagger UI while maintaining proper security controls at the API level.
