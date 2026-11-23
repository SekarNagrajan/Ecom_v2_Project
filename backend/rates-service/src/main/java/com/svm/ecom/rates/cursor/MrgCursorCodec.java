package com.svm.ecom.rates.cursor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Opaque, tamper-evident keyset cursor for {@code GET /api/v1/rates/mrg}:
 * Base64URL({@code mrgCode|hmac}). The key is the last {@code mrgCode} of the previous page
 * (the page order is {@code mrgCode ASC}).
 */
@Component
public class MrgCursorCodec {

    private static final String HMAC_ALG = "HmacSHA256";
    private static final String SEPARATOR = "|";
    private static final int MAX_MRG_CODE_LENGTH = 15; // MRGMaster.mrgCode varchar(15)
    private static final Pattern MRG_CODE = Pattern.compile("[A-Za-z0-9_\\-]+");

    private final byte[] secret;

    public MrgCursorCodec(@Value("${rates.mrg.cursor-secret:mrg-list-cursor-v1}") String secret) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("rates.mrg.cursor-secret must not be empty");
        }
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String encode(String lastMrgCode) {
        if (lastMrgCode == null || lastMrgCode.trim().isEmpty()) {
            throw new InvalidCursorException("Cannot encode cursor without mrgCode");
        }
        String payload = lastMrgCode.trim();
        String token = payload + SEPARATOR + hmacHex(payload);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @return last mrgCode of the previous page, or {@code null} when no cursor was supplied (first page)
     */
    public String decode(String cursor) {
        if (cursor == null || cursor.trim().isEmpty()) {
            return null;
        }
        String decoded;
        try {
            decoded = new String(Base64.getUrlDecoder().decode(cursor.trim()), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new InvalidCursorException("Malformed cursor");
        }
        String[] parts = decoded.split("\\|", -1);
        if (parts.length != 2) {
            throw new InvalidCursorException("Malformed cursor");
        }
        String mrgCode = parts[0];
        if (mrgCode.isEmpty() || mrgCode.length() > MAX_MRG_CODE_LENGTH || !MRG_CODE.matcher(mrgCode).matches()) {
            throw new InvalidCursorException("Malformed cursor");
        }
        if (!MessageDigest.isEqual(hmacHex(mrgCode).getBytes(StandardCharsets.UTF_8),
                parts[1].getBytes(StandardCharsets.UTF_8))) {
            throw new InvalidCursorException("Malformed cursor");
        }
        return mrgCode;
    }

    private String hmacHex(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALG);
            mac.init(new SecretKeySpec(secret, HMAC_ALG));
            byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(raw.length * 2);
            for (byte b : raw) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Cursor HMAC failed", e);
        }
    }

    public static final class InvalidCursorException extends IllegalArgumentException {
        public InvalidCursorException(String message) {
            super(message);
        }
    }
}
