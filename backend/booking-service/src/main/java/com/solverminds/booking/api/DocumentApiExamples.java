package com.solverminds.booking.api;

/**
 * Swagger example constants for V2 Document API error responses.
 * Organised per endpoint and per status code so that each @ApiResponse
 * shows a contextually correct detail message, instance URI, and code.
 */
final class DocumentApiExamples {

    private DocumentApiExamples() {}

    // ======================== LIST DOCUMENTS (GET /api/v2/bookings/{bookingId}/documents) ========================

    static final String LIST_401 = "{\n" +
        "  \"detail\": \"Missing or invalid Bearer token.\",\n" +
        "  \"title\": \"Unauthorized\",\n" +
        "  \"status\": 401,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents\",\n" +
        "  \"code\": \"UNAUTHORIZED\"\n" +
        "}";

    static final String LIST_403 = "{\n" +
        "  \"detail\": \"You do not have permission to access documents for this booking.\",\n" +
        "  \"title\": \"Forbidden\",\n" +
        "  \"status\": 403,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents\",\n" +
        "  \"code\": \"FORBIDDEN\"\n" +
        "}";

    static final String LIST_404 = "{\n" +
        "  \"detail\": \"Booking 'BKON123' was not found.\",\n" +
        "  \"title\": \"Not Found\",\n" +
        "  \"status\": 404,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents\",\n" +
        "  \"code\": \"NOT_FOUND\"\n" +
        "}";

    static final String LIST_500 = "{\n" +
        "  \"detail\": \"An unexpected error occurred while listing documents.\",\n" +
        "  \"title\": \"Internal Server Error\",\n" +
        "  \"status\": 500,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents\",\n" +
        "  \"code\": \"INTERNAL_SERVER_ERROR\"\n" +
        "}";

    static final String LIST_503 = "{\n" +
        "  \"detail\": \"The service is currently not available. Please try again later.\",\n" +
        "  \"title\": \"Service Unavailable\",\n" +
        "  \"status\": 503,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents\",\n" +
        "  \"code\": \"SERVICE_UNAVAILABLE\"\n" +
        "}";

    // ======================== UPLOAD DOCUMENTS (POST /api/v2/bookings/{bookingId}/documents) ========================

    static final String UPLOAD_400 = "{\n" +
        "  \"detail\": \"File type not allowed: exe.\",\n" +
        "  \"title\": \"Bad Request\",\n" +
        "  \"status\": 400,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents\",\n" +
        "  \"code\": \"BAD_REQUEST\"\n" +
        "}";

    static final String UPLOAD_401 = "{\n" +
        "  \"detail\": \"Missing or invalid Bearer token.\",\n" +
        "  \"title\": \"Unauthorized\",\n" +
        "  \"status\": 401,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents\",\n" +
        "  \"code\": \"UNAUTHORIZED\"\n" +
        "}";

    static final String UPLOAD_403 = "{\n" +
        "  \"detail\": \"You do not have permission to upload documents for this booking.\",\n" +
        "  \"title\": \"Forbidden\",\n" +
        "  \"status\": 403,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents\",\n" +
        "  \"code\": \"FORBIDDEN\"\n" +
        "}";

    static final String UPLOAD_409 = "{\n" +
        "  \"detail\": \"A file named 'invoice.pdf' already exists for this booking.\",\n" +
        "  \"title\": \"Conflict\",\n" +
        "  \"status\": 409,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents\",\n" +
        "  \"code\": \"CONFLICT\"\n" +
        "}";

    static final String UPLOAD_500 = "{\n" +
        "  \"detail\": \"An unexpected error occurred while uploading documents.\",\n" +
        "  \"title\": \"Internal Server Error\",\n" +
        "  \"status\": 500,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents\",\n" +
        "  \"code\": \"INTERNAL_SERVER_ERROR\"\n" +
        "}";

    static final String UPLOAD_503 = "{\n" +
        "  \"detail\": \"The service is currently not available. Please try again later.\",\n" +
        "  \"title\": \"Service Unavailable\",\n" +
        "  \"status\": 503,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents\",\n" +
        "  \"code\": \"SERVICE_UNAVAILABLE\"\n" +
        "}";

    // ======================== DOWNLOAD DOCUMENT (GET /api/v2/bookings/{bookingId}/documents/{fileName}) ========================

    static final String DOWNLOAD_401 = "{\n" +
        "  \"detail\": \"Missing or invalid Bearer token.\",\n" +
        "  \"title\": \"Unauthorized\",\n" +
        "  \"status\": 401,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents/invoice.pdf\",\n" +
        "  \"code\": \"UNAUTHORIZED\"\n" +
        "}";

    static final String DOWNLOAD_403 = "{\n" +
        "  \"detail\": \"You do not have permission to download this document.\",\n" +
        "  \"title\": \"Forbidden\",\n" +
        "  \"status\": 403,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents/invoice.pdf\",\n" +
        "  \"code\": \"FORBIDDEN\"\n" +
        "}";

    static final String DOWNLOAD_404 = "{\n" +
        "  \"detail\": \"Document 'invoice.pdf' was not found for booking 'BKON123'.\",\n" +
        "  \"title\": \"Not Found\",\n" +
        "  \"status\": 404,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents/invoice.pdf\",\n" +
        "  \"code\": \"NOT_FOUND\"\n" +
        "}";

    static final String DOWNLOAD_500 = "{\n" +
        "  \"detail\": \"An unexpected error occurred while downloading the document.\",\n" +
        "  \"title\": \"Internal Server Error\",\n" +
        "  \"status\": 500,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents/invoice.pdf\",\n" +
        "  \"code\": \"INTERNAL_SERVER_ERROR\"\n" +
        "}";

    static final String DOWNLOAD_503 = "{\n" +
        "  \"detail\": \"The service is currently not available. Please try again later.\",\n" +
        "  \"title\": \"Service Unavailable\",\n" +
        "  \"status\": 503,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents/invoice.pdf\",\n" +
        "  \"code\": \"SERVICE_UNAVAILABLE\"\n" +
        "}";

    // ======================== DELETE DOCUMENT (DELETE /api/v2/bookings/{bookingId}/documents/{fileName}) ========================

    static final String DELETE_401 = "{\n" +
        "  \"detail\": \"Missing or invalid Bearer token.\",\n" +
        "  \"title\": \"Unauthorized\",\n" +
        "  \"status\": 401,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents/invoice.pdf\",\n" +
        "  \"code\": \"UNAUTHORIZED\"\n" +
        "}";

    static final String DELETE_403 = "{\n" +
        "  \"detail\": \"You do not have permission to delete this document.\",\n" +
        "  \"title\": \"Forbidden\",\n" +
        "  \"status\": 403,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents/invoice.pdf\",\n" +
        "  \"code\": \"FORBIDDEN\"\n" +
        "}";

    static final String DELETE_404 = "{\n" +
        "  \"detail\": \"Document 'invoice.pdf' was not found for booking 'BKON123'.\",\n" +
        "  \"title\": \"Not Found\",\n" +
        "  \"status\": 404,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents/invoice.pdf\",\n" +
        "  \"code\": \"NOT_FOUND\"\n" +
        "}";

    static final String DELETE_409 = "{\n" +
        "  \"detail\": \"The file 'invoice.pdf' is currently in use and cannot be deleted.\",\n" +
        "  \"title\": \"Conflict\",\n" +
        "  \"status\": 409,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents/invoice.pdf\",\n" +
        "  \"code\": \"CONFLICT\"\n" +
        "}";

    static final String DELETE_500 = "{\n" +
        "  \"detail\": \"An unexpected error occurred while deleting the document.\",\n" +
        "  \"title\": \"Internal Server Error\",\n" +
        "  \"status\": 500,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents/invoice.pdf\",\n" +
        "  \"code\": \"INTERNAL_SERVER_ERROR\"\n" +
        "}";

    static final String DELETE_503 = "{\n" +
        "  \"detail\": \"The service is currently not available. Please try again later.\",\n" +
        "  \"title\": \"Service Unavailable\",\n" +
        "  \"status\": 503,\n" +
        "  \"instance\": \"/api/v2/bookings/BKON123/documents/invoice.pdf\",\n" +
        "  \"code\": \"SERVICE_UNAVAILABLE\"\n" +
        "}";
}
