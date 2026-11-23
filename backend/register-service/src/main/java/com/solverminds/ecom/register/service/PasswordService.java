package com.solverminds.ecom.register.service;

import com.solverminds.ecom.register.dto.ForgotPasswordRequest;
import com.solverminds.ecom.register.dto.ResetPasswordRequest;

public interface PasswordService {

    /**
     * Initiate forgot password process
     */
    void forgotPassword(ForgotPasswordRequest request);

    /**
     * Reset password for forgot password flow (no authentication required)
     * Consumer handles email/OTP validation before calling this method
     * @param request containing email, newPassword, and confirmPassword
     * @return true if user exists and password updated, false if user not found
     */
    boolean resetPassword(ResetPasswordRequest request);
}
