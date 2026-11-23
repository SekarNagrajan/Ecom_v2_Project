package com.solverminds.ecom.register.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordVerifier {

    private final BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();
    private final CryptoLibraryCompat legacyCrypto = new CryptoLibraryCompat();

    /**
     * Verify password:
     * 1) If BCrypt hash -> bcrypt verify
     * 2) Else if likely legacy ciphertext -> decrypt and compare
     * 3) Else -> as last fallback, plain text compare (temporary)
     */
    public boolean verify(String plainPassword, String storedPassword) {
        if (storedPassword == null) return false;

        if (isBCryptHash(storedPassword)) {
            return bcryptEncoder.matches(plainPassword, storedPassword);
        }

        // Legacy encrypted path
        if (isLikelyLegacyCiphertext(storedPassword)) {
            try {
                String decrypted = legacyCrypto.decrypt(storedPassword);
                if (plainPassword != null && plainPassword.equals(decrypted)) {
                    return true;
                }
            } catch (SecurityException ignore) {
                // fall through to final plain compare
            }
        }

        // Temporary fallback for any truly-plaintext legacy rows
        return plainPassword != null && plainPassword.equals(storedPassword);
    }

    /**
     * If the stored password was legacy and matches, return a new BCrypt hash to migrate.
     * Otherwise return null.
     */
    public String rehashIfLegacyMatch(String plainPassword, String storedPassword) {
        if (storedPassword == null || isBCryptHash(storedPassword)) return null;

        try {
            String decrypted = legacyCrypto.decrypt(storedPassword);
            if (plainPassword != null && plainPassword.equals(decrypted)) {
                return bcryptEncoder.encode(plainPassword);
            }
        } catch (SecurityException ignore) {
            // ignore
        }

        // also consider plaintext legacy
        if (plainPassword != null && plainPassword.equals(storedPassword)) {
            return bcryptEncoder.encode(plainPassword);
        }
        return null;
    }

    public String hash(String plainPassword) {
        return bcryptEncoder.encode(plainPassword);
    }

    private boolean isBCryptHash(String password) {
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }

    private boolean isLikelyLegacyCiphertext(String s) {
        // very lightweight: Base64-like AND not BCrypt
        if (s == null || s.length() < 16) return false;
        if (isBCryptHash(s)) return false;
        // Base64 chars only
        return s.matches("^[A-Za-z0-9+/=]+$");
    }
}
