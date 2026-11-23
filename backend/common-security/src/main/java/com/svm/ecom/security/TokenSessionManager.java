package com.svm.ecom.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Session manager to track active JWT tokens per user
 * Prevents token sharing and provides token invalidation capabilities
 */
@Component
public class TokenSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(TokenSessionManager.class);
    
    // Map of username -> Set of active token signatures
    private final ConcurrentHashMap<String, Set<String>> userActiveSessions = new ConcurrentHashMap<>();
    
    /**
     * Register a new token for a user
     * @param username the user who owns the token
     * @param tokenSignature unique identifier for the token (last part of JWT)
     */
    public void registerToken(String username, String tokenSignature) {
        logger.info("REGISTERING token for user: {} | Signature: {} | Total active users: {}", 
                   username, tokenSignature.substring(0, Math.min(10, tokenSignature.length())) + "...", 
                   userActiveSessions.size());
        userActiveSessions.computeIfAbsent(username, k -> new CopyOnWriteArraySet<>()).add(tokenSignature);
        logger.info("Token registered successfully. User {} now has {} active sessions", 
                   username, getActiveSessionCount(username));
    }
    
    /**
     * Check if a token is registered for a specific user
     * @param username the user to check
     * @param tokenSignature the token signature to verify
     * @return true if token is registered for this user
     */
    public boolean isTokenRegisteredForUser(String username, String tokenSignature) {
        Set<String> userTokens = userActiveSessions.get(username);
        boolean isRegistered = userTokens != null && userTokens.contains(tokenSignature);
        
        logger.info("CHECKING token for user: {} | Signature: {} | Total active users: {} | User has {} sessions | Token found: {}", 
                   username, 
                   tokenSignature.substring(0, Math.min(10, tokenSignature.length())) + "...", 
                   userActiveSessions.size(),
                   userTokens != null ? userTokens.size() : 0,
                   isRegistered);
        
        if (!isRegistered && userTokens != null) {
            logger.warn("ALERT: Token not found for user {}. Available signatures for this user:", username);
            for (String sig : userTokens) {
                logger.warn("   Available: {}", sig.substring(0, Math.min(10, sig.length())) + "...");
            }
        }
        
        return isRegistered;
    }
    
    /**
     * Invalidate a specific token for a user
     * @param username the user who owns the token
     * @param tokenSignature the token signature to invalidate
     * @return true if token was found and removed
     */
    public boolean invalidateToken(String username, String tokenSignature) {
        Set<String> userTokens = userActiveSessions.get(username);
        if (userTokens != null) {
            boolean removed = userTokens.remove(tokenSignature);
            if (removed) {
                logger.info("Token invalidated for user: {}", username);
            }
            return removed;
        }
        return false;
    }
    
    /**
     * Invalidate all tokens for a user (useful for logout)
     * @param username the user whose tokens should be invalidated
     * @return number of tokens invalidated
     */
    public int invalidateAllTokensForUser(String username) {
        Set<String> userTokens = userActiveSessions.remove(username);
        int count = userTokens != null ? userTokens.size() : 0;
        if (count > 0) {
            logger.info("Invalidated {} tokens for user: {}", count, username);
        }
        return count;
    }
    
    /**
     * Get the number of active sessions for a user
     * @param username the user to check
     * @return number of active sessions
     */
    public int getActiveSessionCount(String username) {
        Set<String> userTokens = userActiveSessions.get(username);
        return userTokens != null ? userTokens.size() : 0;
    }
    
    /**
     * Extract token signature from JWT token (last part after last dot)
     * @param token the full JWT token
     * @return the signature part of the token
     */
    public String extractTokenSignature(String token) {
        if (token == null || !token.contains(".")) {
            throw new IllegalArgumentException("Invalid token format");
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token format");
        }
        return parts[2]; // Return signature part
    }
    
    /**
     * Get total number of users with active sessions
     * @return number of users with active sessions
     */
    public int getTotalActiveUsers() {
        return userActiveSessions.size();
    }
    
    /**
     * Clean up empty user entries
     */
    public void cleanup() {
        userActiveSessions.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        logger.debug("Session cleanup completed. Active users: {}", userActiveSessions.size());
    }
}
