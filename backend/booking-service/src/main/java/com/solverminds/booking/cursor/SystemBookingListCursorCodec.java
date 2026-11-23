package com.solverminds.booking.cursor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.Base64;

/**
 * Opaque, tamper-evident cursor: Base64URL({@code createdEpochMillis|bookingId|source|hmac}).
 * {@code source} is included because {@code usp_getbookingsummary_system} requires {@code @cursorSource}.
 */
@Component
public class SystemBookingListCursorCodec {

    private static final String HMAC_ALG = "HmacSHA256";
    private static final String SEPARATOR = "|";

    private final byte[] secret;

    public SystemBookingListCursorCodec(
            @Value("${booking.system-list.cursor-secret:system-booking-list-cursor-v1}") String secret) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("booking.system-list.cursor-secret must not be empty");
        }
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String encode(Timestamp createdDate, String bookingId, String source) {
        if (createdDate == null || bookingId == null || bookingId.trim().isEmpty()) {
            throw new InvalidCursorException("Cannot encode cursor without createdDate and bookingId");
        }
        String src = source == null ? "" : source.trim();
        String payload = createdDate.getTime() + SEPARATOR + bookingId.trim() + SEPARATOR + src;
        String token = payload + SEPARATOR + hmacHex(payload);
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    public CursorKey decode(String cursor) {
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
        if (parts.length != 4) {
            throw new InvalidCursorException("Malformed cursor");
        }
        String payload = parts[0] + SEPARATOR + parts[1] + SEPARATOR + parts[2];
        if (!MessageDigest.isEqual(
                hmacHex(payload).getBytes(StandardCharsets.UTF_8),
                parts[3].getBytes(StandardCharsets.UTF_8))) {
            throw new InvalidCursorException("Malformed cursor");
        }
        long millis;
        try {
            millis = Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            throw new InvalidCursorException("Malformed cursor");
        }
        String bookingId = parts[1];
        if (bookingId.isEmpty() || bookingId.length() > 20) {
            throw new InvalidCursorException("Malformed cursor");
        }
        String source = parts[2];
        if (source.length() > 1) {
            throw new InvalidCursorException("Malformed cursor");
        }
        return new CursorKey(new Timestamp(millis), bookingId, source.isEmpty() ? null : source);
    }

    private String hmacHex(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALG);
            mac.init(new SecretKeySpec(secret, HMAC_ALG));
            byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(raw.length * 2);
            for (int i = 0; i < raw.length; i++) {
                sb.append(String.format("%02x", raw[i] & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Cursor HMAC failed", e);
        }
    }

    public static final class CursorKey {
        private final Timestamp createdDate;
        private final String bookingId;
        private final String source;

        public CursorKey(Timestamp createdDate, String bookingId, String source) {
            this.createdDate = createdDate;
            this.bookingId = bookingId;
            this.source = source;
        }

        public Timestamp getCreatedDate() {
            return createdDate;
        }

        public String getBookingId() {
            return bookingId;
        }

        public String getSource() {
            return source;
        }
    }

    public static final class InvalidCursorException extends IllegalArgumentException {
        public InvalidCursorException(String message) {
            super(message);
        }
    }
}
