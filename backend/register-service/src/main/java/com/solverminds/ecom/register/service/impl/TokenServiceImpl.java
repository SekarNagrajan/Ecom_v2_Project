package com.solverminds.ecom.register.service.impl;

import com.solverminds.ecom.register.entity.EcomUserDetails;
import com.solverminds.ecom.register.repo.EcomUserDetailsRepository;
import com.solverminds.ecom.register.service.TokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(transactionManager = "ecomTransactionManager")
public class TokenServiceImpl implements TokenService {

    @Autowired
    private EcomUserDetailsRepository userRepository;

    private final SecureRandom secureRandom = new SecureRandom();
    private final String SECRET_KEY = "simple-secret-for-dev-only"; // Same as JwtUtil

    @Override
    public String issueAccessToken(EcomUserDetails user, int ttlSeconds) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId());
        claims.put("roles", new String[]{user.getUserRole()});
        claims.put("customerCode", user.getCustomerCode());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmailID())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ttlSeconds * 1000L))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    @Override
    public String issueRefreshToken() {
        // Generate secure random 32 bytes -> hex (64 chars)
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : randomBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Override
    public String rotateRefreshToken(EcomUserDetails user) {
        String newRefreshToken = issueRefreshToken();
        userRepository.setRefreshToken(user.getId(), newRefreshToken, LocalDateTime.now());
        return newRefreshToken;
    }
}
