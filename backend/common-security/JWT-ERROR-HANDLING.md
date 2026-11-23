# JWT Token Error Handling - Enhanced Error Messages

## Overview

The JWT authentication system provides **specific error messages** for different token validation failures, helping clients understand exactly what went wrong with their authentication.

## Error Flow

```
┌─────────────────────┐
│  Client Request     │
│  with JWT Token     │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────────────────────────┐
│  JwtAuthenticationFilter                │
│  ├── Extract token from Authorization   │
│  ├── Validate token (JwtUtil)           │
│  └── Catch exceptions                   │
└──────────┬──────────────────────────────┘
           │
           │ (On error)
           ▼
┌─────────────────────────────────────────┐
│  Store error in request attribute       │
│  request.setAttribute("JWT_ERROR", msg) │
└──────────┬──────────────────────────────┘
           │
           ▼
┌─────────────────────────────────────────┐
│  RestAuthenticationEntryPoint           │
│  ├── Check for JWT_ERROR attribute      │
│  ├── Use specific message if available  │
│  └── Return 401 with error details      │
└─────────────────────────────────────────┘
```

## Token Validation Errors

### 1. Expired Token

**Scenario**: Token's expiration time has passed

**Error Response**:
```json
{
  "statusCode": 401,
  "statusCodeText": "Unauthorized",
  "errorDateTime": "2025-11-18T10:30:45",
  "errors": [
    {
      "errorCode": 1001,
      "field": null,
      "value": null,
      "reason": "unauthorized",
      "message": "Token expired"
    }
  ]
}
```

**Client Action**: Request new token using refresh token or re-login

---

### 2. Invalid Token Format

**Scenario**: Token is malformed or corrupted

**Error Response**:
```json
{
  "statusCode": 401,
  "statusCodeText": "Unauthorized",
  "errorDateTime": "2025-11-18T10:30:45",
  "errors": [
    {
      "errorCode": 1001,
      "field": null,
      "value": null,
      "reason": "unauthorized",
      "message": "Invalid token format"
    }
  ]
}
```

**Client Action**: Re-login to get a valid token

---

### 3. Invalid Token Signature

**Scenario**: Token signature doesn't match or token was tampered with

**Error Response**:
```json
{
  "statusCode": 401,
  "statusCodeText": "Unauthorized",
  "errorDateTime": "2025-11-18T10:30:45",
  "errors": [
    {
      "errorCode": 1001,
      "field": null,
      "value": null,
      "reason": "unauthorized",
      "message": "Invalid token signature"
    }
  ]
}
```

**Client Action**: Re-login to get a new valid token

---

### 4. Token Not in Active Sessions

**Scenario**: Token is valid but not registered in active sessions (session validation enabled)

**Error Response**:
```json
{
  "statusCode": 401,
  "statusCodeText": "Unauthorized",
  "errorDateTime": "2025-11-18T10:30:45",
  "errors": [
    {
      "errorCode": 1001,
      "field": null,
      "value": null,
      "reason": "unauthorized",
      "message": "Token not found in active sessions. Please login again."
    }
  ]
}
```

**Client Action**: Re-login to register token in active sessions

---

### 5. Missing or No Token

**Scenario**: No Authorization header or no Bearer token provided

**Error Response**:
```json
{
  "statusCode": 401,
  "statusCodeText": "Unauthorized",
  "errorDateTime": "2025-11-18T10:30:45",
  "errors": [
    {
      "errorCode": 1001,
      "field": null,
      "value": null,
      "reason": "unauthorized",
      "message": "Full authentication is required to access this resource"
    }
  ]
}
```

**Client Action**: Include valid Bearer token in Authorization header

---

## Token Expiration Times

### Access Token (Default)
- **Duration**: 5 minutes (300 seconds)
- **Configured in**: `JwtUtil.java` line 33
- **Usage**: All API requests

```java
.setExpiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
```

### Refresh Token
- **Duration**: 60 minutes (3600 seconds)
- **Configured in**: `JwtUtil.java` line 42
- **Usage**: Obtaining new access token

```java
.setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
```

## Implementation Details

### JwtAuthenticationFilter.java

```java
try {
    String username = jwtUtil.extractUsername(token);
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        if (jwtUtil.validateToken(token, username)) {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null, null);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }
} catch (RuntimeException e) {
    // Store JWT error in request attribute for later handling
    request.setAttribute("JWT_ERROR", e.getMessage());
    System.out.println("JWT validation failed in filter: " + e.getMessage());
}
```

### RestAuthenticationEntryPoint.java

```java
@Override
public void commence(HttpServletRequest request, HttpServletResponse response,
                     org.springframework.security.core.AuthenticationException authException) throws IOException {
    HttpStatus status = HttpStatus.UNAUTHORIZED;
    
    // Check if JWT filter set a specific error message
    String jwtError = (String) request.getAttribute("JWT_ERROR");
    String errorMessage = jwtError != null ? jwtError : authException.getMessage();
    
    ErrorResponse body = new ErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            LocalDateTime.now()
    );
    body.addError(ErrorCodes.UNAUTHORIZED, null, null, "unauthorized", errorMessage);

    response.setStatus(status.value());
    response.setContentType("application/json");
    mapper.writeValue(response.getOutputStream(), body);
}
```

## Client-Side Handling

### Recommended Client Implementation

```javascript
// Example: JavaScript/TypeScript client
async function callApi(endpoint, options) {
    const response = await fetch(endpoint, {
        ...options,
        headers: {
            'Authorization': `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
            ...options.headers
        }
    });
    
    if (response.status === 401) {
        const error = await response.json();
        const message = error.errors[0]?.message;
        
        if (message === 'Token expired') {
            // Try to refresh token
            const newToken = await refreshAccessToken();
            if (newToken) {
                // Retry request with new token
                return callApi(endpoint, options);
            } else {
                // Refresh failed, redirect to login
                redirectToLogin();
            }
        } else if (message === 'Token not found in active sessions. Please login again.') {
            // Session invalidated, must re-login
            redirectToLogin();
        } else {
            // Other auth errors - redirect to login
            redirectToLogin();
        }
    }
    
    return response;
}
```

## Testing Token Expiration

### 1. Test Expired Token

```bash
# Wait 5 minutes after login, then make request
curl -X GET "http://localhost:8080/ecomapi-service/api/v1/booking/summary" \
  -H "Authorization: Bearer <expired_token>"
```

**Expected Response**: 401 with "Token expired"

### 2. Test Invalid Token

```bash
curl -X GET "http://localhost:8080/ecomapi-service/api/v1/booking/summary" \
  -H "Authorization: Bearer invalid.token.here"
```

**Expected Response**: 401 with "Invalid token format" or "Invalid token signature"

### 3. Test Missing Token

```bash
curl -X GET "http://localhost:8080/ecomapi-service/api/v1/booking/summary"
```

**Expected Response**: 401 with "Full authentication is required to access this resource"

## Configuration

### Enable/Disable Session Validation

In `application.properties`:

```properties
# Enable session validation (default: true)
jwt.session.validation.enabled=true

# Disable session validation (allows token sharing)
jwt.session.validation.enabled=false
```

When disabled, tokens remain valid until expiration even if user logs out from one device.

## Security Considerations

1. **Short Access Token Lifetime** (5 minutes)
   - Minimizes damage if token is compromised
   - Forces regular token refresh

2. **Refresh Token Strategy**
   - Longer lifetime (60 minutes) for better UX
   - Used to obtain new access tokens

3. **Session Management**
   - Optional session validation prevents token sharing
   - Tokens registered per user on login
   - Cleared on logout

4. **Error Message Clarity**
   - Specific error messages help clients handle different scenarios
   - No sensitive information leaked in error messages

## Troubleshooting

### Issue: Always Getting "Token expired" Immediately

**Possible Causes**:
1. Server time and client time are out of sync
2. Token generation issue

**Solution**: Check server time and ensure clocks are synchronized

### Issue: "Token not found in active sessions" After Login

**Possible Causes**:
1. Session validation enabled but token not registered on login
2. Session storage cleared

**Solution**: Verify login endpoint properly registers token in session manager

### Issue: Getting Generic "Full authentication required" Instead of Specific Message

**Possible Causes**:
1. JWT_ERROR attribute not being set
2. RestAuthenticationEntryPoint not checking attribute

**Solution**: This has been fixed in the current implementation

---

**Date**: 2025-11-18  
**Status**: ✅ IMPLEMENTED  
**Impact**: Enhanced error messages for better client-side error handling and user experience

