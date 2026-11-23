package com.svm.ecom.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final String SECRET_KEY = "simple-secret-for-dev-only";

    @Value("${jwt.session.validation.enabled:true}")
    private boolean sessionValidationEnabled;

    @Autowired
    private TokenSessionManager tokenSessionManager;

    public String generateToken(String username) {
        return generateToken(username, null);
    }

    /**
     * Issue an internal-auth JWT ({@code /api/v1/auth/login}).
     * Role is read once at login and embedded in the token; callers must not re-query the DB per request.
     *
     * @param username token subject
     * @param role     value from {@code ecomapi_users.role}, or null to omit the claim
     */
    public String generateToken(String username, String role) {
        io.jsonwebtoken.JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
        if (role != null && !role.trim().isEmpty()) {
            builder.claim("roles", new String[]{role.trim()});
        }
        return builder.signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    /**
     * True when the token carries ECOM-specific claims ({@code uid} or {@code customerCode}).
     */
    public boolean isEcomCustomerToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.containsKey("uid") || claims.containsKey("customerCode");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * True for internal-auth SYSUSER read-only tokens: has {@code SYSUSER} role and is not an ECOM customer token.
     */
    public boolean isSysUserReadOnlyToken(String token) {
        return !isEcomCustomerToken(token) && hasRole(token, JwtRoleNames.SYSUSER);
    }

    /**
     * Checks {@code roles} claim in the JWT only (no database lookup).
     */
    public boolean hasRole(String token, String role) {
        if (role == null || role.trim().isEmpty()) {
            return false;
        }
        String expected = role.trim();
        for (String r : extractRoles(token)) {
            if (expected.equalsIgnoreCase(r)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns role strings from the JWT {@code roles} claim (empty if absent or unparseable).
     */
    public List<String> extractRoles(String token) {
        try {
            return parseRolesClaim(getClaims(token).get("roles"));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * True when the token was issued by internal auth login ({@code /api/v1/auth/login}):
     * subject-only JWT without ecom-specific claims.
     */
    public boolean isInternalAuthLoginSubject(String token, String expectedSubject) {
        try {
            Claims claims = getClaims(token);
            String subject = claims.getSubject();
            return !isEcomCustomerToken(token)
                    && subject != null
                    && expectedSubject.equalsIgnoreCase(subject);
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> parseRolesClaim(Object rolesObj) {
        if (rolesObj == null) {
            return Collections.emptyList();
        }
        List<String> roles = new ArrayList<>();
        if (rolesObj instanceof String[]) {
            for (String r : (String[]) rolesObj) {
                if (r != null && !r.trim().isEmpty()) {
                    roles.add(r.trim());
                }
            }
        } else if (rolesObj instanceof List) {
            for (Object item : (List<?>) rolesObj) {
                if (item != null) {
                    String r = item.toString().trim();
                    if (!r.isEmpty()) {
                        roles.add(r);
                    }
                }
            }
        } else {
            String r = rolesObj.toString().trim();
            if (!r.isEmpty()) {
                roles.add(r);
            }
        }
        return roles;
    }

    public String extractUsername(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired");
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Invalid token format");
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid token signature");
        } catch (Exception e) {
            throw new RuntimeException("Token validation failed");
        }
    }

    public boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            return extractedUsername.equals(username) && !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired");
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Invalid token format");
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid token signature");
        } catch (Exception e) {
            throw new RuntimeException("Token validation failed");
        }
    }

    /**
     * Validate token with user context to prevent token theft/sharing
     * @param token JWT token to validate
     * @param requestingUser The user making the request (from authentication)
     * @return username from token if valid and belongs to requesting user
     * @throws RuntimeException if token is invalid or doesn't belong to requesting user
     */
    public String validateTokenWithUserContext(String token, String requestingUser) {
        try {
            String tokenUsername = extractUsername(token);
            
            // Critical security check: ensure token belongs to the requesting user
            if (!tokenUsername.equals(requestingUser)) {
                throw new RuntimeException("Token does not belong to requesting user. Token owner: " + tokenUsername + ", Requesting user: " + requestingUser);
            }
            
            if (!validateToken(token, tokenUsername)) {
                throw new RuntimeException("Token validation failed");
            }
            
            return tokenUsername;
        } catch (RuntimeException e) {
            throw e; // Re-throw runtime exceptions (including our security check)
        } catch (Exception e) {
            throw new RuntimeException("Token validation failed: " + e.getMessage());
        }
    }

    /**
     * Comprehensive token validation with optional session management
     * This is the most secure validation method that should be used in all services
     * @param token JWT token to validate
     * @return username from token if all validations pass
     * @throws RuntimeException if token is invalid, expired, or not in active sessions
     */
    public String validateTokenWithSession(String token) {
        try {
            // Step 1: Extract username from token (validates signature and expiration)
            String username = extractUsername(token);
            
            // Step 2: Validate token structure and expiration
            if (!validateToken(token, username)) {
                throw new RuntimeException("Token validation failed");
            }
            
            // Step 3: Check if token is registered in active sessions (configurable security check)
            if (sessionValidationEnabled) {
                String tokenSignature = tokenSessionManager.extractTokenSignature(token);
                if (!tokenSessionManager.isTokenRegisteredForUser(username, tokenSignature)) {
                    throw new RuntimeException("Token not found in active sessions. Please login again.");
                }
            } else {
                // Session validation disabled - log for security awareness
                logger.warn("WARNING: Session validation is DISABLED. Token sharing prevention is not active.");
            }
            
            return username;
        } catch (RuntimeException e) {
            throw e; // Re-throw runtime exceptions (including our security checks)
        } catch (Exception e) {
            throw new RuntimeException("Token validation failed: " + e.getMessage());
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // Token is expired
        }
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw e; // Re-throw to be caught by calling methods
        } catch (Exception e) {
            throw e; // Re-throw other exceptions
        }
    }
}
