package com.solverminds.ecom.register.service.impl;

import com.solverminds.ecom.register.config.BeansConfig.EcomAuthProperties;
import com.solverminds.ecom.register.dto.ChangePasswordRequest;
import com.solverminds.ecom.register.dto.ChangePasswordResponse;
import com.solverminds.ecom.register.dto.RegLoginRequest;
import com.solverminds.ecom.register.dto.RegLoginResponse;
import com.solverminds.ecom.register.dto.RefreshTokenRequest;
import com.solverminds.ecom.register.entity.EcomUserDetails;
import com.solverminds.ecom.register.repo.EcomUserDetailsRepository;
import com.solverminds.ecom.register.service.RegAuthService;
import com.solverminds.ecom.register.service.TokenService;
import com.solverminds.ecom.register.util.PasswordVerifier;
import com.solverminds.ecom.register.util.CryptoLibraryCompat;
import com.svm.ecom.security.JwtUtil;
import com.svm.ecom.security.TokenSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(transactionManager = "ecomTransactionManager")
public class RegServiceImpl implements RegAuthService {

    private static final Logger logger = LoggerFactory.getLogger(RegServiceImpl.class);

    @Autowired
    private EcomUserDetailsRepository userRepository;

    @Autowired
    private PasswordVerifier passwordVerifier;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CryptoLibraryCompat cryptoLibraryCompat;

    @Autowired
    private EcomAuthProperties authProperties;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenSessionManager tokenSessionManager;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public RegLoginResponse login(RegLoginRequest request) {
        // 1. Load user by EmailID (case-insensitive)
        Optional<EcomUserDetails> userOpt = userRepository.findByEmailIDIgnoreCase(request.getUsername());
        if (!userOpt.isPresent()) {
            throw new RuntimeException("ECOM-AUTH-FAILED");
        }

        EcomUserDetails user = userOpt.get();
        LocalDateTime now = LocalDateTime.now();

        // 2. Check if user is locked
        if (user.isLocked()) {
            LocalDateTime unlockTime = user.getLockedTime().plusMinutes(authProperties.getLockMinutes());
            if (now.isBefore(unlockTime)) {
                throw new RuntimeException("ECOM-AUTH-LOCKED");
            } else {
                // Auto-unlock
                userRepository.resetOnSuccess(user.getId(), now);
                user.setLockedStatus("N");
                user.setWrgPassAtmpt(0);
            }
        }

        // 3. Check if user may log in (active or provisional dummy)
        if (!user.isLoginAllowed()) {
            throw new RuntimeException("ECOM-AUTH-INACTIVE");
        }

        // 4. Verify password (legacy-aware)
        boolean passwordMatches = passwordVerifier.verify(request.getPassword(), user.getPassword());
        if (!passwordMatches) {
            // Increment wrong attempts
            userRepository.incWrongAttempts(user.getId());
            int currentAttempts = (user.getWrgPassAtmpt() != null ? user.getWrgPassAtmpt() : 0) + 1;

            // Lock if max attempts reached
            if (currentAttempts >= authProperties.getMaxWrongAttempts()) {
                userRepository.lock(user.getId(), now);
            }

            throw new RuntimeException("ECOM-AUTH-FAILED");
        }

        // If password matched and stored password is legacy, seamlessly migrate to
        // BCrypt
        // String maybeNewHash =
        // passwordVerifier.rehashIfLegacyMatch(request.getPassword(),
        // user.getPassword());
        // if (maybeNewHash != null) {
        // user.setPassword(maybeNewHash);
        // userRepository.save(user);
        // }

        // 5. Success - reset attempts and unlock
        userRepository.resetOnSuccess(user.getId(), now);

        // 6. Generate tokens
        String accessToken = tokenService.issueAccessToken(user, authProperties.getAccessTtlSeconds());
        String refreshToken = tokenService.rotateRefreshToken(user);

        // 7. Register token in session manager
        String tokenSignature = tokenSessionManager.extractTokenSignature(accessToken);
        tokenSessionManager.registerToken(user.getEmailID(), tokenSignature);

        return new RegLoginResponse(accessToken, refreshToken, authProperties.getAccessTtlSeconds(), authProperties.getRefreshTtlSeconds());
    }

    @Override
    public RegLoginResponse refresh(RefreshTokenRequest request) {
        // Find user by refresh token
        Optional<EcomUserDetails> userOpt = userRepository.findByApptoken(request.getRefreshToken());
        if (!userOpt.isPresent()) {
            throw new RuntimeException("ECOM-AUTH-FAILED");
        }

        EcomUserDetails user = userOpt.get();

        // Check if refresh token has expired
        LocalDateTime tokenCreatedAt = user.getApptokenCreatedAt();
        if (tokenCreatedAt != null) {
            LocalDateTime expirationTime = tokenCreatedAt.plusSeconds(authProperties.getRefreshTtlSeconds());
            if (LocalDateTime.now().isAfter(expirationTime)) {
                throw new RuntimeException("ECOM-REFRESH-TOKEN-EXPIRED");
            }
        }

        // Issue new access token
        String accessToken = tokenService.issueAccessToken(user, authProperties.getAccessTtlSeconds());

        // Rotate refresh token
        String newRefreshToken = tokenService.rotateRefreshToken(user);

        // Register token in session manager (required for session validation)
        String tokenSignature = tokenSessionManager.extractTokenSignature(accessToken);
        tokenSessionManager.registerToken(user.getEmailID(), tokenSignature);

        return new RegLoginResponse(accessToken, newRefreshToken, authProperties.getAccessTtlSeconds(), authProperties.getRefreshTtlSeconds());
    }

    @Override
    public void changePassword(ChangePasswordRequest request, String authorizationHeader) {
        EcomUserDetails user = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Authenticated flow - parse JWT
            String token = authorizationHeader.substring(7);
            String email = jwtUtil.extractUsername(token);

            Optional<EcomUserDetails> userOpt = userRepository.findByEmailIDIgnoreCase(email);
            if (!userOpt.isPresent()) {
                throw new RuntimeException("ECOM-AUTH-FAILED");
            }

            user = userOpt.get();

            // Verify current password
            if (!passwordVerifier.verify(request.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("ECOM-AUTH-FAILED");
            }
        } else {
            // Reset flow - treat as token stub (simplified for now)
            throw new RuntimeException("ECOM-AUTH-FAILED");
        }

        // Store new password as BCrypt hash
        String hashedPassword = passwordVerifier.hash(request.getNewPassword());
        user.setPassword(hashedPassword);
        user.setLastpwdup(LocalDateTime.now());
        userRepository.save(user);

        // Optionally rotate refresh token for security
        tokenService.rotateRefreshToken(user);
    }

    @Override
    public ChangePasswordResponse secureChangePassword(ChangePasswordRequest request, String token) {
        // 1. Validate JWT token and extract username (email)
        String email = validateTokenAndExtractUsername(token);

        // 2. Load user with Status = 'A'
        Optional<EcomUserDetails> userOpt = userRepository.findByEmailIDAndStatus(email, "A");
        if (!userOpt.isPresent()) {
            throw new RuntimeException("ECOM-AUTH-FORBIDDEN");
        }

        EcomUserDetails user = userOpt.get();

        // 3. Validate new password equals confirm new password
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("ECOM-VALIDATION-MISMATCH");
        }

        // 4. Decrypt stored password and compare with current password
        String storedPassword = user.getPassword();
        String currentPassword = request.getCurrentPassword();

        try {
            String decryptedPassword = cryptoLibraryCompat.decrypt(storedPassword);
            if (!currentPassword.equals(decryptedPassword)) {
                throw new RuntimeException("ECOM-AUTH-CURRENT-INVALID");
            }
        } catch (SecurityException e) {
            // If decryption fails, password is incorrect
            throw new RuntimeException("ECOM-AUTH-CURRENT-INVALID");
        }

        // 5. Encrypt new password using CryptoLibrary and update DB
        String encryptedNewPassword = cryptoLibraryCompat.encrypt(request.getNewPassword());
        String oldPassword = user.getPassword();
        LocalDateTime now = LocalDateTime.now();

        // 6. Update password and lastpwdup fields with debugging
        logger.debug("About to update password for user ID: {}", user.getId());
        logger.debug("Old password length: {}", oldPassword);
        logger.debug("New password length: {}", encryptedNewPassword);
        logger.debug("Password changed: {}", !oldPassword.equals(encryptedNewPassword));
        
        // Use targeted update query instead of full entity save
        int updateCount = userRepository.updatePassword(user.getId(), encryptedNewPassword, now);
        
        // Force immediate database write
        entityManager.flush();
        
        logger.debug("Updated {} rows for user ID: {}", updateCount, user.getId());
        
        // Verify the change was actually persisted by reloading from database
        EcomUserDetails reloadedUser = userRepository.findById(user.getId()).orElse(null);
        if (reloadedUser != null) {
            logger.debug("Reloaded password length: {}", reloadedUser.getPassword());
            logger.debug("Database contains new password: {}", encryptedNewPassword.equals(reloadedUser.getPassword()));
        }

        // 8. Return success response
        return new ChangePasswordResponse("Password changed successfully.");
    }

    @Override
    public String validateTokenAndExtractUsername(String token) throws SecurityException {
        try {
            // 1. Extract username from token (this also validates signature and expiration)
            String username = jwtUtil.extractUsername(token);
            if (username == null || username.trim().isEmpty()) {
                throw new SecurityException("Token does not contain valid username");
            }

            // 2. Validate JWT token structure and signature with username
            if (!jwtUtil.validateToken(token, username)) {
                throw new SecurityException("Invalid or expired JWT token");
            }

            // 3. Validate token session (if session validation is enabled)
            String tokenSignature = tokenSessionManager.extractTokenSignature(token);
            if (!tokenSessionManager.isTokenRegisteredForUser(username, tokenSignature)) {
                throw new SecurityException("Token session is invalid or expired");
            }

            return username;

        } catch (SecurityException e) {
            throw e; // Re-throw security exceptions as-is
        } catch (Exception e) {
            throw new SecurityException("Token validation failed: " + e.getMessage());
        }
    }
}
