package com.solverminds.ecom.register.service;

import com.solverminds.ecom.register.dto.*;

public interface RegAuthService {

    /**
     * Login with lockout mechanism
     */
    RegLoginResponse login(RegLoginRequest request);

    /**
     * Refresh access token
     */
    RegLoginResponse refresh(RefreshTokenRequest request);

    /**
     * Change password (authenticated or reset flow)
     */
    void changePassword(ChangePasswordRequest request, String authorizationHeader);

    /**
     * Secure change password for logged-in users
     */
    ChangePasswordResponse secureChangePassword(ChangePasswordRequest request, String token);

    /**
     * Validate JWT token and extract username
     */
    String validateTokenAndExtractUsername(String token) throws SecurityException;
}
