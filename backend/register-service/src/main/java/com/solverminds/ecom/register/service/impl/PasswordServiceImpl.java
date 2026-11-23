package com.solverminds.ecom.register.service.impl;

import com.solverminds.ecom.register.dto.ForgotPasswordRequest;
import com.solverminds.ecom.register.dto.ResetPasswordRequest;
import com.solverminds.ecom.register.entity.EcomUserDetails;
import com.solverminds.ecom.register.repo.EcomUserDetailsRepository;
import com.solverminds.ecom.register.service.PasswordService;
import com.solverminds.ecom.register.util.CryptoLibraryCompat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(transactionManager = "ecomTransactionManager")
public class PasswordServiceImpl implements PasswordService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordServiceImpl.class);

    @Autowired
    private EcomUserDetailsRepository userRepository;

    @Autowired
    private CryptoLibraryCompat cryptoLibraryCompat;

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<EcomUserDetails> userOpt = userRepository.findByEmailIDIgnoreCase(request.getEmail());
        
        if (userOpt.isPresent()) {
            // TODO: Implement password reset logic
            // - Generate secure reset token
            // - Send email with reset link
            // - Store token with expiry
            logger.info("Password reset requested for email: {}", request.getEmail());
        } else {
            // Don't reveal if email exists - just log
            logger.info("Password reset requested for non-existent email: {}", request.getEmail());
        }
        
        // Always return success for security (don't reveal if email exists)
    }

    @Override
    public boolean resetPassword(ResetPasswordRequest request) {
        // 1. Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("ECOM-VALIDATION-MISMATCH");
        }

        // 2. Check if user exists
        Optional<EcomUserDetails> userOpt = userRepository.findByEmailIDIgnoreCase(request.getEmail());
        if (!userOpt.isPresent()) {
            logger.info("Password reset attempted for non-existent email: {}", request.getEmail());
            return false; // User not found
        }

        EcomUserDetails user = userOpt.get();

        // 3. Encrypt and update password
        String encryptedPassword = cryptoLibraryCompat.encrypt(request.getNewPassword());
        userRepository.updatePassword(user.getId(), encryptedPassword, LocalDateTime.now());

        logger.info("Password reset completed for email: {}", request.getEmail());
        return true;
    }
}
