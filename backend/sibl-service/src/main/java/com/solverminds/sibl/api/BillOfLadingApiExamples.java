package com.solverminds.sibl.api;

/**
 * Swagger example constants for V2 Bill of Lading Download API error responses.
 */
final class BillOfLadingApiExamples {

    private BillOfLadingApiExamples() {}

    // ======================== DOWNLOAD BL (GET /api/v2/billOfLading/{blId}/download) ========================

    static final String DOWNLOAD_401 = "{\n" +
        "  \"detail\": \"Authentication failed (Missing or invalid Bearer token).\",\n" +
        "  \"title\": \"Unauthorized\",\n" +
        "  \"status\": 401,\n" +
        "  \"instance\": \"/api/v2/billOfLading/TSHG2500001/download\",\n" +
        "  \"code\": \"UNAUTHORIZED\"\n" +
        "}";

    static final String DOWNLOAD_403 = "{\n" +
        "  \"detail\": \"You do not have permission to access Bill of Lading 'TSHG2500001'.\",\n" +
        "  \"title\": \"Forbidden\",\n" +
        "  \"status\": 403,\n" +
        "  \"instance\": \"/api/v2/billOfLading/TSHG2500001/download\",\n" +
        "  \"code\": \"FORBIDDEN\"\n" +
        "}";

    static final String DOWNLOAD_404 = "{\n" +
        "  \"detail\": \"Bill of Lading 'TSHG2500001' was not found.\",\n" +
        "  \"title\": \"Not Found\",\n" +
        "  \"status\": 404,\n" +
        "  \"instance\": \"/api/v2/billOfLading/TSHG2500001/download\",\n" +
        "  \"code\": \"NOT_FOUND\"\n" +
        "}";

    static final String DOWNLOAD_500 = "{\n" +
        "  \"detail\": \"An unexpected error occurred while downloading the Bill of Lading.\",\n" +
        "  \"title\": \"Internal Server Error\",\n" +
        "  \"status\": 500,\n" +
        "  \"instance\": \"/api/v2/billOfLading/TSHG2500001/download\",\n" +
        "  \"code\": \"INTERNAL_SERVER_ERROR\"\n" +
        "}";

    static final String DOWNLOAD_503 = "{\n" +
        "  \"detail\": \"The service is currently not available. Please try again later.\",\n" +
        "  \"title\": \"Service Unavailable\",\n" +
        "  \"status\": 503,\n" +
        "  \"instance\": \"/api/v2/billOfLading/TSHG2500001/download\",\n" +
        "  \"code\": \"SERVICE_UNAVAILABLE\"\n" +
        "}";
}
