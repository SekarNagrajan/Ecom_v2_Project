package com.solverminds.ecom.register.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Legacy-compatible crypto helper for decrypting passwords encrypted with PBEWithMD5AndDES.
 * This class maintains compatibility with the old CryptoLibrary while using standard Java APIs.
 */
@Component
public class CryptoLibraryCompat {

    private final Cipher encryptCipher;
    private final Cipher decryptCipher;

    public CryptoLibraryCompat() {
        try {
            // NOTE: keep these identical to legacy so stored ciphertexts can be decrypted
            char[] pass = "CHANGE THIS TO A BUNCH OF RANDOM CHARACTERS".toCharArray();
            byte[] salt = new byte[] { -93, 33, 36, 44, -14, -46, 62, 25 };
            int iterations = 20; // legacy class effectively used 20 in PBEParameterSpec

            PBEParameterSpec ps = new PBEParameterSpec(salt, iterations);
            SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey k = kf.generateSecret(new PBEKeySpec(pass));

            encryptCipher = Cipher.getInstance("PBEWithMD5AndDES/CBC/PKCS5Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, k, ps);

            decryptCipher = Cipher.getInstance("PBEWithMD5AndDES/CBC/PKCS5Padding");
            decryptCipher.init(Cipher.DECRYPT_MODE, k, ps);
        } catch (Exception e) {
            throw new SecurityException("Could not initialize CryptoLibraryCompat: " + e.getMessage(), e);
        }
    }

    /**
     * Encrypt plain text to Base64-encoded ciphertext
     */
    public synchronized String encrypt(String plain) {
        try {
            byte[] utf8 = plain.getBytes(StandardCharsets.UTF_8);
            byte[] enc = encryptCipher.doFinal(utf8);
            return Base64.getEncoder().encodeToString(enc);
        } catch (Exception e) {
            throw new SecurityException("Could not encrypt: " + e.getMessage(), e);
        }
    }

    /**
     * Decrypt Base64-encoded ciphertext to plain text
     */
    public synchronized String decrypt(String base64) {
        try {
            byte[] dec = Base64.getDecoder().decode(base64);
            byte[] utf8 = decryptCipher.doFinal(dec);
            return new String(utf8, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SecurityException("Could not decrypt: " + e.getMessage(), e);
        }
    }
}
