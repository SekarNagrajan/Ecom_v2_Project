package com.solverminds.booking.dto.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.media.Schema;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Cursor for cursor-based pagination in V2 Bookings API.
 * 
 * Cursor format (opaque Base64URL-encoded JSON):
 * {
 *   "v": 1,                          // cursor version
 *   "s": "-createdAt",               // sort used
 *   "f": "abc123",                   // filter hash (for validation)
 *   "k": {                           // last record key
 *     "datecr": "YYYY-MM-DDTHH:mm:ss",
 *     "trust_book_no": "string",
 *     "onlinebookno": "string"
 *   }
 * }
 */
@Schema(description = "Cursor data for pagination", hidden = true)
public class BookingCursorV2 {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    // Current cursor version
    public static final int CURRENT_VERSION = 1;

    @JsonProperty("v")
    @Schema(description = "Cursor version", example = "1")
    private int version = CURRENT_VERSION;

    @JsonProperty("s")
    @Schema(description = "Sort value used when cursor was created", example = "-createdAt")
    private String sort;

    @JsonProperty("f")
    @Schema(description = "Filter hash for validation", example = "abc123def456")
    private String filterHash;

    @JsonProperty("k")
    @Schema(description = "Last record key for seek pagination")
    private CursorKey key;

    /**
     * Inner class for cursor key (last record position)
     */
    public static class CursorKey {
        @JsonProperty("datecr")
        private String datecr;

        @JsonProperty("trust_book_no")
        private String trustBookNo;

        @JsonProperty("onlinebookno")
        private String onlineBookNo;

        public CursorKey() {
        }

        public CursorKey(LocalDateTime datecr, String trustBookNo, String onlineBookNo) {
            this.datecr = datecr != null ? datecr.format(DATE_FORMATTER) : null;
            this.trustBookNo = trustBookNo != null ? trustBookNo : "";
            this.onlineBookNo = onlineBookNo != null ? onlineBookNo : "";
        }

        public String getDatecr() {
            return datecr;
        }

        public void setDatecr(String datecr) {
            this.datecr = datecr;
        }

        @JsonIgnore
        public LocalDateTime getDatecrAsLocalDateTime() {
            if (datecr == null || datecr.isEmpty()) {
                return null;
            }
            try {
                return LocalDateTime.parse(datecr, DATE_FORMATTER);
            } catch (Exception e) {
                return null;
            }
        }

        public String getTrustBookNo() {
            return trustBookNo;
        }

        public void setTrustBookNo(String trustBookNo) {
            this.trustBookNo = trustBookNo;
        }

        public String getOnlineBookNo() {
            return onlineBookNo;
        }

        public void setOnlineBookNo(String onlineBookNo) {
            this.onlineBookNo = onlineBookNo;
        }
    }

    public BookingCursorV2() {
    }

    /**
     * Create a cursor from the last record of a page.
     */
    public BookingCursorV2(String sort, String filterHash, LocalDateTime datecr, 
                           String trustBookNo, String onlineBookNo) {
        this.version = CURRENT_VERSION;
        this.sort = sort;
        this.filterHash = filterHash;
        this.key = new CursorKey(datecr, trustBookNo, onlineBookNo);
    }

    /**
     * Encode cursor to Base64URL string for use in API responses.
     * @return Base64URL-encoded JSON string (without padding)
     */
    @JsonIgnore
    public String encode() {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(this);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to encode cursor", e);
        }
    }

    /**
     * Decode cursor from Base64URL string.
     * @param encoded Base64URL-encoded cursor string
     * @return Decoded cursor object, or null if invalid
     */
    public static BookingCursorV2 decode(String encoded) {
        if (encoded == null || encoded.trim().isEmpty()) {
            return null;
        }
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(encoded);
            String json = new String(decoded, StandardCharsets.UTF_8);
            return OBJECT_MAPPER.readValue(json, BookingCursorV2.class);
        } catch (Exception e) {
            // Invalid cursor format
            return null;
        }
    }

    /**
     * Check if cursor version is supported.
     * @return true if version is supported
     */
    @JsonIgnore
    public boolean isVersionSupported() {
        return version == CURRENT_VERSION;
    }

    /**
     * Validate that cursor sort matches the current request sort.
     * @param currentSort Current sort parameter from request
     * @return true if sort matches
     */
    @JsonIgnore
    public boolean isSortMatch(String currentSort) {
        if (sort == null && currentSort == null) return true;
        if (sort == null || currentSort == null) return false;
        return sort.equals(currentSort);
    }

    /**
     * Validate that cursor filter hash matches the current request filters.
     * @param currentFilterHash Current filter hash from request
     * @return true if filter hash matches
     */
    @JsonIgnore
    public boolean isFilterMatch(String currentFilterHash) {
        // Empty/null filter hashes are considered equal
        String cursorHash = (filterHash == null) ? "" : filterHash;
        String requestHash = (currentFilterHash == null) ? "" : currentFilterHash;
        return cursorHash.equals(requestHash);
    }

    /**
     * Check if cursor has valid position data for pagination.
     * @return true if cursor has key with position data
     */
    @JsonIgnore
    public boolean hasPosition() {
        return key != null && (key.datecr != null || key.trustBookNo != null || key.onlineBookNo != null);
    }

    // Getters and Setters

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getFilterHash() {
        return filterHash;
    }

    public void setFilterHash(String filterHash) {
        this.filterHash = filterHash;
    }

    public CursorKey getKey() {
        return key;
    }

    public void setKey(CursorKey key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "BookingCursorV2{" +
                "version=" + version +
                ", sort='" + sort + '\'' +
                ", filterHash='" + filterHash + '\'' +
                ", key=" + (key != null ? "{datecr=" + key.datecr + ", trustBookNo=" + key.trustBookNo + ", onlineBookNo=" + key.onlineBookNo + "}" : "null") +
                '}';
    }
}
