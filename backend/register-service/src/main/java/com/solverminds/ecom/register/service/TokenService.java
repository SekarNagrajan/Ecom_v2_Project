package com.solverminds.ecom.register.service;

import com.solverminds.ecom.register.entity.EcomUserDetails;

public interface TokenService {

    /**
     * Issue access token with custom TTL
     */
    String issueAccessToken(EcomUserDetails user, int ttlSeconds);

    /**
     * Issue secure refresh token
     */
    String issueRefreshToken();

    /**
     * Rotate refresh token for user and return new token
     */
    String rotateRefreshToken(EcomUserDetails user);
}
