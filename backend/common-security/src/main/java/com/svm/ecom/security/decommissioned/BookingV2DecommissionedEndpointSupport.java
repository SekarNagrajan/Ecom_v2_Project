package com.svm.ecom.security.decommissioned;

import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Shared rules for decommissioned Booking V2 CRUD endpoints (list, create, get-by-id, update-by-id).
 * Cancel, VGM, and document sub-resources remain available.
 */
public final class BookingV2DecommissionedEndpointSupport {

    public static final String OPENAPI_PATH_COLLECTION = "/api/v2/bookings";
    public static final String OPENAPI_PATH_BY_ID = "/api/v2/bookings/{bookingId}";

    public static final Set<String> OPENAPI_PATHS_TO_REMOVE = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            OPENAPI_PATH_COLLECTION,
            OPENAPI_PATH_BY_ID
    )));

    public static final Set<String> DECOMMISSIONED_OPERATION_IDS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "getBookings",
            "createBooking",
            "getBookingById",
            "updateBookingById"
    )));

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private static final String ANT_COLLECTION = "/api/v2/bookings";
    private static final String ANT_BY_ID = "/api/v2/bookings/*";

    private BookingV2DecommissionedEndpointSupport() {
    }

    /**
     * @param requestPath servlet path or URI path without context (e.g. {@code /api/v2/bookings} or {@code /ecomapi-service/api/v2/bookings})
     */
    public static boolean isDecommissionedBookingV2CrudRequest(String requestPath, String httpMethod) {
        if (requestPath == null || httpMethod == null) {
            return false;
        }
        String path = normalizePath(requestPath);
        String method = httpMethod.trim().toUpperCase();

        if (PATH_MATCHER.match(ANT_COLLECTION, path)) {
            return "GET".equals(method) || "POST".equals(method);
        }

        if (PATH_MATCHER.match(ANT_BY_ID, path)) {
            return "GET".equals(method) || "PUT".equals(method);
        }

        return false;
    }

    /**
     * Strips {@code /ecomapi-service} context prefix when present.
     */
    public static String normalizePath(String path) {
        if (path == null) {
            return "";
        }
        String normalized = path.trim();
        if (normalized.isEmpty()) {
            return normalized;
        }
        final String contextPrefix = "/ecomapi-service";
        if (normalized.startsWith(contextPrefix + "/")) {
            return normalized.substring(contextPrefix.length());
        }
        if (normalized.equals(contextPrefix)) {
            return "/";
        }
        return normalized;
    }
}
