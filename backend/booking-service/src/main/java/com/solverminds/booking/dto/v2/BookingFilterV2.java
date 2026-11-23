package com.solverminds.booking.dto.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines valid filter keys to filter bookings in V2 API (client snake_case).
 * Pass as query params: filter[id_contains]=..., filter[status_equals]=D, etc.
 *
 * For Booking Summary V2, the SP output does not return origin/delivery names,
 * so origin_name_contains and delivery_name_contains filters are not supported.
 */
@Schema(
    name = "BookingFilter",
    description = "Filtering criteria for booking items (optional). Pass as query params: filter[key]=value. Keys use snake_case.",
    example = "{\"id_contains\": \"TSHG123\", \"status_equals\": \"D\", \"origin_id_contains\": \"CNNGB\", \"origin_name_contains\": \"NINGBO\", \"delivery_id_contains\": \"HUBUD\", \"delivery_name_contains\": \"BUDAPEST\"}"
)
public class BookingFilterV2 {

    @JsonProperty("id_contains")
    @Schema(description = "Filter by booking id, performs a 'contains' search", example = "BKON")
    private String idContains;

    @JsonProperty("status_equals")
    @Schema(description = "Filter by booking status, performs an 'equals' search", example = "D")
    private String statusEquals;

    @JsonProperty("origin_id_contains")
    @Schema(description = "Filter by booking origin id, performs a 'contains' search", example = "INNSA")
    private String originIdContains;

    @JsonProperty("origin_name_contains")
    @Schema(description = "Filter by booking origin name, performs a 'contains' search", example = "NINGBO")
    private String originNameContains;

    @JsonProperty("delivery_id_contains")
    @Schema(description = "Filter by booking delivery id, performs a 'contains' search", example = "HUBUD")
    private String deliveryIdContains;

    @JsonProperty("delivery_name_contains")
    @Schema(description = "Filter by booking delivery name, performs a 'contains' search", example = "BUDAPEST")
    private String deliveryNameContains;

    public BookingFilterV2() {
    }

    public String getIdContains() {
        return idContains;
    }

    public void setIdContains(String idContains) {
        this.idContains = idContains;
    }

    public String getStatusEquals() {
        return statusEquals;
    }

    public void setStatusEquals(String statusEquals) {
        this.statusEquals = statusEquals;
    }

    public String getOriginIdContains() {
        return originIdContains;
    }

    public void setOriginIdContains(String originIdContains) {
        this.originIdContains = originIdContains;
    }

    public String getOriginNameContains() {
        return originNameContains;
    }

    public void setOriginNameContains(String originNameContains) {
        this.originNameContains = originNameContains;
    }

    public String getDeliveryIdContains() {
        return deliveryIdContains;
    }

    public void setDeliveryIdContains(String deliveryIdContains) {
        this.deliveryIdContains = deliveryIdContains;
    }

    public String getDeliveryNameContains() {
        return deliveryNameContains;
    }

    public void setDeliveryNameContains(String deliveryNameContains) {
        this.deliveryNameContains = deliveryNameContains;
    }

    /**
     * Check if any filter is set
     */
    public boolean hasAnyFilter() {
        return idContains != null || statusEquals != null || originIdContains != null ||
               originNameContains != null || deliveryIdContains != null || deliveryNameContains != null;
    }

    /**
     * Check if any V2-supported filter is set (excludes name filters).
     */
    public boolean hasAnySupportedFilter() {
        return idContains != null || statusEquals != null || 
               originIdContains != null || deliveryIdContains != null;
    }

    /**
     * Get list of unsupported filter names for Booking Summary V2.
     * The SP output does not return origin/delivery names, so these filters cannot be applied.
     * 
     * @return List of unsupported filter names that are set, or empty list if none
     */
    public List<String> getUnsupportedFiltersForV2() {
        List<String> unsupported = new ArrayList<>();
        if (originNameContains != null && !originNameContains.isEmpty()) {
            unsupported.add("filter[origin_name_contains]");
        }
        if (deliveryNameContains != null && !deliveryNameContains.isEmpty()) {
            unsupported.add("filter[delivery_name_contains]");
        }
        return unsupported;
    }

    /**
     * Check if any unsupported filter is set for V2.
     * 
     * @return true if originNameContains or deliveryNameContains is set
     */
    public boolean hasUnsupportedFiltersForV2() {
        return (originNameContains != null && !originNameContains.isEmpty()) ||
               (deliveryNameContains != null && !deliveryNameContains.isEmpty());
    }

    /**
     * Compute a consistent hash of the filter state for cursor validation.
     * The hash is used to ensure a cursor is only valid when used with the same filters.
     * Only considers V2-supported filters (excludes name filters).
     * 
     * @return SHA-256 hash of filter values (first 16 chars), or empty string if no supported filters
     */
    public String computeHash() {
        if (!hasAnySupportedFilter()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        // Use consistent ordering and null-safe concatenation
        // Only include V2-supported filters (not name filters)
        sb.append("id:").append(idContains != null ? idContains.toLowerCase() : "").append("|");
        sb.append("status:").append(statusEquals != null ? statusEquals.toLowerCase() : "").append("|");
        sb.append("originId:").append(originIdContains != null ? originIdContains.toLowerCase() : "").append("|");
        sb.append("deliveryId:").append(deliveryIdContains != null ? deliveryIdContains.toLowerCase() : "");

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            // Convert first 8 bytes to hex (16 chars) for shorter cursor
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < 8 && i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback: use simple hash code
            return String.valueOf(sb.toString().hashCode());
        }
    }
}

