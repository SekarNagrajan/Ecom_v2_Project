package com.svm.ecom.docs;

/**
 * Centralized constants for Swagger/OpenAPI documentation
 * Contains all commonly used summaries, descriptions, and response messages
 * to ensure consistency across the entire API documentation
 */
public final class SwaggerDocs {

    // Private constructor to prevent instantiation
    private SwaggerDocs() {}

    /**
     * Common response descriptions
     */
    public static final class Common {
        // Success responses
        public static final String SUCCESS_200 = "Operation completed successfully";
        public static final String CREATED_201 = "Resource created successfully";
        public static final String NO_CONTENT_204 = "Operation completed successfully with no content to return";
        
        // Error responses
        public static final String BAD_REQUEST_400 = "Bad Request - Invalid request parameters or validation errors";
        public static final String UNAUTHORIZED_401 = "Unauthorized - Authentication required or invalid token";
        public static final String FORBIDDEN_403 = "Forbidden - Access denied for this resource";
        public static final String NOT_FOUND_404 = "Not Found - Requested resource not found";
        public static final String METHOD_NOT_ALLOWED_405 = "Method Not Allowed - HTTP method not supported for this endpoint";
        public static final String UNPROCESSABLE_ENTITY_422 = "Unprocessable Entity - Business rule violation";
        public static final String SERVER_ERROR_500 = "Internal Server Error - An unexpected error occurred";
        public static final String SERVICE_UNAVAILABLE_503 = "Service Unavailable - Service temporarily unavailable";
    }

    /**
     * Booking service documentation constants
     */
    public static final class Booking {
        public static final String SUMMARY_GET_SUMMARY = "Get booking summary";
        public static final String DESC_GET_SUMMARY = "Retrieves ecom booking summary with transformed field names for the authenticated customer using usp_getecombookingsummary stored procedure";
    }

    /**
     * Summary service documentation constants
     */
    public static final class Summary {
        public static final String SUMMARY_DASHBOARD = "Get dashboard summary";
        public static final String DESC_DASHBOARD = "Retrieves dashboard booking summary for the authenticated customer";
        
        public static final String SUMMARY_CRO = "Get CRO details";
        public static final String DESC_CRO = "Retrieves Container Release Order (CRO) details for the specified date range";
        
        public static final String SUMMARY_ARRIVAL_NOTICE = "Get arrival notices";
        public static final String DESC_ARRIVAL_NOTICE = "Retrieves arrival notice information for the specified date range";
        
        public static final String SUMMARY_DELIVERY_ORDER = "Get delivery orders";
        public static final String DESC_DELIVERY_ORDER = "Retrieves delivery order information for the specified date range";
    }

    /**
     * Master data service documentation constants
     */
    public static final class Master {
        public static final String SUMMARY_GET_VESSELS = "List active vessels";
        public static final String DESC_GET_VESSELS = "Returns distinct active vessels excluding codes starting with 'xx'. Requires valid JWT authentication token.";
        
        public static final String SUMMARY_GET_PORTS = "List active ports";
        public static final String DESC_GET_PORTS = "Returns active ports excluding codes starting with 'xx'. Requires valid JWT authentication token.";

        public static final String SUMMARY_GET_COUNTRY = "List active countries";
        public static final String DESC_GET_COUNTRY = "Returns active countries excluding codes starting with 'xx'. Requires valid JWT authentication token.";

        public static final String SUMMARY_GET_PACKAGE = "List active packages";
        public static final String DESC_GET_PACKAGE = "Returns active packages excluding codes starting with 'xx'. Requires valid JWT authentication token.";

        public static final String SUMMARY_GET_EQUIPMENT = "List active equipment";
        public static final String DESC_GET_EQUIPMENT = "Returns active equipment excluding codes starting with 'xx'. Requires valid JWT authentication token.";

        public static final String SUMMARY_GET_TERMINAL = "List active terminals";
        public static final String DESC_GET_TERMINAL = "Returns active terminals excluding codes starting with 'xx'. Requires valid JWT authentication token.";

        public static final String SUMMARY_GET_COMMODITY = "List active commodity";
        public static final String DESC_GET_COMMODITY = "Returns active commodity excluding codes starting with 'xx'. Requires valid JWT authentication token.";

        public static final String SUMMARY_GET_HS = "List active HS";
        public static final String DESC_GET_HS = "Returns active HS excluding codes starting with 'xx'. Requires valid JWT authentication token.";

        public static final String SUMMARY_GET_DG = "List active DG";
        public static final String DESC_GET_DG = "Returns active DG excluding codes starting with 'xx'. Requires valid JWT authentication token.";
    }

    /**
     * Master data v2 ? matches client request standard wording and format.
     */
    public static final class MasterV2 {
        /** Tag name (client: "Masterdata") */
        public static final String TAG_NAME = "Masterdata";
        /** Tag description (client: "Operations for managing and retrieving master data") */
        public static final String TAG_DESCRIPTION = "Operations for managing and retrieving master data";

        // ?? Customers ??
        public static final String SUMMARY_GET_CUSTOMERS = "List Customers";
        public static final String DESC_GET_CUSTOMERS = "Retrieves a list of customers.";
        public static final String RESPONSE_200_CUSTOMERS = "A list of customers.";

        // ?? Agencies ??
        public static final String SUMMARY_GET_AGENCIES = "List Agencies";
        public static final String DESC_GET_AGENCIES = "Retrieves a list of agencies";
        public static final String RESPONSE_200_AGENCIES = "A list of agencies.";

        // ?? Commodities ??
        public static final String SUMMARY_GET_COMMODITIES = "List Commodities";
        public static final String DESC_GET_COMMODITIES = "Retrieves a list of commodities";
        public static final String RESPONSE_200_COMMODITIES = "A list of commodities.";

        // ?? DG Packagings ??
        public static final String SUMMARY_GET_DG_PACKAGINGS = "List DG Packagings";
        public static final String DESC_GET_DG_PACKAGINGS =
                "Retrieves DG packaging master entries. Optional usage filter is exact: OUTER (O), INNER (I), or BOTH (B).";
        public static final String RESPONSE_200_DG_PACKAGINGS = "A list of DG packagings.";

        // ?? IMDG Entries ??
        public static final String SUMMARY_GET_IMDG_ENTRIES = "List IMDG Entries";
        public static final String DESC_GET_IMDG_ENTRIES =
                "Retrieves IMDG detail rows for a UN number from the current amendment (one row per variant).";
        public static final String RESPONSE_200_IMDG_ENTRIES = "A list of IMDG entries.";

        // ?? Shared error responses ??
        public static final String RESPONSE_400 = "The server could not understand the request due to invalid syntax or parameters.";
        public static final String RESPONSE_401 = "Authentication failed (Missing or invalid Bearer token).";
        public static final String RESPONSE_403 = "The client does not have access rights to the content.";
        public static final String RESPONSE_500 = "An unexpected server-side error occurred.";
        public static final String RESPONSE_503 = "The service is currently not available.";

        /** @deprecated use {@link #RESPONSE_200_CUSTOMERS} */
        @Deprecated
        public static final String RESPONSE_200 = RESPONSE_200_CUSTOMERS;
    }

    /**
     * Authentication service documentation constants
     */
    public static final class Auth {
        public static final String SUMMARY_LOGIN = "User authentication";
        public static final String DESC_LOGIN = "Authenticate user and return JWT token";
        
        public static final String SUMMARY_REFRESH = "Refresh JWT token";
        public static final String DESC_REFRESH = "Refresh existing JWT token to extend session";
        
        public static final String SUMMARY_LOGOUT = "User logout";
        public static final String DESC_LOGOUT = "Invalidate JWT token and end user session";
    }

    /**
     * Schedule service documentation constants
     */
    public static final class Schedule {
        public static final String SUMMARY_GET_SCHEDULE = "Get point-to-point route schedules";
        public static final String DESC_GET_SCHEDULE = "Retrieves comprehensive sailing schedules between origin and destination ports including vessel details, port pairs, transit times, transshipments, and routing information. Returns multiple route options with complete journey breakdown showing all vessel changes and port calls.";

        public static final String SUMMARY_GET_COASTAL_SCHEDULE = "Get coastal schedule details";
        public static final String DESC_GET_COASTAL_SCHEDULE = "Returns the full coastal voyage and port-call schedule for each requested service, vessel, and date range. One request item produces one coastalSchedule list. An empty list means no port calls were found.";

        public static final String SUMMARY_GET_LONG_RANGE_SCHEDULE = "Get long range schedule info";
        public static final String DESC_GET_LONG_RANGE_SCHEDULE = "Returns the long range schedule report for each requested service and date range. weekStartDay defaults to Monday (M). One request item produces one longRangeSchedule list. An empty list means no rows were found.";
    }

    /**
     * Tracking service documentation constants
     */
    public static final class Tracking {
        public static final String SUMMARY_TRACK_CONTAINER = "Track container";
        public static final String DESC_TRACK_CONTAINER = "Retrieves tracking information for the specified container";
        
        public static final String SUMMARY_TRACK_BOOKING = "Track booking";
        public static final String DESC_TRACK_BOOKING = "Retrieves tracking information for the specified booking reference";
    }

    /**
     * Registration service documentation constants
     */
    public static final class Registration {
        public static final String SUMMARY_REGISTER_USER = "Register new user";
        public static final String DESC_REGISTER_USER = "Register a new user account with the system";
        
        public static final String SUMMARY_VERIFY_EMAIL = "Verify email address";
        public static final String DESC_VERIFY_EMAIL = "Verify user email address using verification token";
        
        public static final String SUMMARY_RESET_PASSWORD = "Reset password";
        public static final String DESC_RESET_PASSWORD = "Initiate password reset process for user account";

        public static final String SUMMARY_RESET_PASSWORD_CONFIRM = "Reset password (forgot password flow)";
        public static final String DESC_RESET_PASSWORD_CONFIRM = "Reset password for users who forgot their password. Consumer/Frontend handles email verification and OTP validation before calling this endpoint. This endpoint validates user existence and updates the password.";
    }

    /**
     * Rates / Auto Rate estimate documentation constants
     */
    public static final class Rates {
        public static final String TAG_NAME = "Rates";
        public static final String TAG_DESCRIPTION = "Operations related to rate and surcharge estimation.";

        public static final String SUMMARY_ESTIMATE = "Estimate shipment surcharges";
        public static final String DESC_ESTIMATE = "Calculates estimated shipment surcharges for the provided routing, "
                + "equipment and commodity profile using the Auto Rate surcharge engine.";
        public static final String OPERATION_ID_ESTIMATE = "estimateRates";
        public static final String RESPONSE_200_ESTIMATE = "Response object containing calculated surcharge charges.";
    }

    /**
     * Contract service documentation constants (V2 API)
     */
    public static final class Contract {
        /** Tag name */
        public static final String TAG_NAME = "Contracts";
        /** Tag description */
        public static final String TAG_DESCRIPTION = "Operations related to contract management.";

        // Get all customer contracts
        public static final String SUMMARY_GET_CONTRACTS = "Get all customer contracts";
        public static final String DESC_GET_CONTRACTS = "Retrieves a list of customer contracts for the authenticated user.";
        public static final String OPERATION_ID_GET_CONTRACTS = "getCustomerContracts";
        public static final String RESPONSE_200_GET_CONTRACTS = "Response object containing a list of customer contracts.";

        // Get customer contract by ID
        public static final String SUMMARY_GET_CONTRACT_BY_ID = "Get customer contract by id";
        public static final String DESC_GET_CONTRACT_BY_ID = "Retrieves detailed information about a specific customer contract including port pairs for the given contract id and amendment number.";
        public static final String OPERATION_ID_GET_CONTRACT_BY_ID = "getCustomerContractById";
        public static final String RESPONSE_200_GET_CONTRACT_BY_ID = "Response object containing a single customer contract.";

        // Parameter descriptions
        public static final String PARAM_CONTRACT_ID = "Contract id, might be a contract number or request number";
        public static final String PARAM_CONTRACT_ID_EXAMPLE = "20250173";
        public static final String PARAM_AMENDMENT_NUMBER = "Amendment number to retrieve specific contract amendment";
        public static final String PARAM_AMENDMENT_NUMBER_EXAMPLE = "1";

        // Download contract PDF
        public static final String SUMMARY_DOWNLOAD_CONTRACT = "Download specified contract document as PDF";
        public static final String DESC_DOWNLOAD_CONTRACT = "Downloads the contract PDF document for the given contract ID and amendment number. "
            + "Requires JWT authentication. The authenticated customer must have access to the contract. "
            + "Returns the PDF as binary content (application/octet-stream).";
        public static final String OPERATION_ID_DOWNLOAD_CONTRACT = "downloadContract";
        public static final String RESPONSE_200_DOWNLOAD_CONTRACT = "The binary file content.";

        // Error responses specific to contracts
        public static final String RESPONSE_404_CONTRACT = "The contract could not be found or you don't have access to it.";
    }

    /**
     * SIBL service documentation constants
     */
    public static final class Sibl {
        /** SI Summary */
        public static final String SUMMARY_SI_SUMMARY = "Get SI summary";
        public static final String DESC_SI_SUMMARY = "Retrieves SI summary list for the authenticated customer";

        /** BL Summary */
        public static final String SUMMARY_BL_SUMMARY = "Get BL summary";
        public static final String DESC_BL_SUMMARY = "Retrieves Bill of Lading summary list for the authenticated customer. Filter by types (AP, BP, DP) and flag (Y/N).";

        /** Generic get data (legacy) */
        public static final String SUMMARY_GET_DATA = "Get SIBL data";
        public static final String DESC_GET_DATA = "Retrieves SIBL-related information for the authenticated user";

        /** SI Create */
        public static final String SUMMARY_SI_CREATE = "Create Shipping Instruction (SI)";
        public static final String DESC_SI_CREATE = "Creates a new shipping instruction with containers, commodities, customers (SH, CN, NT required), and optional file attachments. Returns online SI number and status. EDI temp tables (editempbooking) are populated when status is Submit (S).";

        /** SI Update */
        public static final String SUMMARY_SI_UPDATE = "Update Shipping Instruction (SI)";
        public static final String DESC_SI_UPDATE = "Updates an existing shipping instruction identified by onlineSINo. Draft (D) updates in place; Submit (S) deletes and re-inserts ecomSI data and runs EDI export. Requires JWT; customer scope is derived from token.";

        /** BL Update */
        public static final String SUMMARY_BL_UPDATE = "Update Bill of Lading (BL)";
        public static final String DESC_BL_UPDATE = "Updates an existing Bill of Lading. Path: '/blupdate/BL_NUMBER' or '/blupdate/BL_NUMBER,ONLINE_SI_NO'. Requires JWT; customer scope is derived from token.";

        /** SI / Bill-of-Lading View */
        public static final String SUMMARY_SI_VIEW = "View Shipping Instruction / Bill of Lading";
        public static final String DESC_SI_VIEW = "Fetch SI/BL data by bookingNo, onlineSINo, or blNumber. At least one parameter is required. Priority when multiple are provided: bookingNo, onlineSINo, blNumber. Response shape matches SI create payload (SIBLRequestDTO) for unified UI.";
        public static final String BAD_REQUEST_VIEW_400 = "Bad Request - at least one of bookingNo, onlineSINo, or blNumber is required";
        public static final String NOT_FOUND_VIEW_404 = "Not Found - no SI/BL found for the given parameters";

        /** Document upload (path: {onlineSINo}) */
        public static final String SUMMARY_DOC_UPLOAD = "Upload documents for online SI";
        public static final String DESC_DOC_UPLOAD = "Upload 1-5 documents (max 10MB per file, max 50MB total) to the online SI's shared folder. Path parameter: onlineSINo. Same folder structure as booking service.";

        /** Document list (path: {onlineSINo}) */
        public static final String SUMMARY_DOC_LIST = "List documents for online SI";
        public static final String DESC_DOC_LIST = "List all documents in the online SI folder (path parameter: onlineSINo). Optional includeChecksum=true returns SHA-256 hash per file. Returns empty array if no documents.";

        /** Document download (path: {onlineSINo}) */
        public static final String SUMMARY_DOC_DOWNLOAD = "Download or preview a document";
        public static final String DESC_DOC_DOWNLOAD = "Download or preview a document by onlineSINo and fileName. PDFs and images preview in browser; other files download. Returns file content with appropriate Content-Type.";
    }

    /**
     * Bill of Lading V2 API documentation constants
     */
    public static final class BillOfLading {
        public static final String TAG_NAME = "Bill of Lading";
        public static final String TAG_DESCRIPTION = "Operations related to managing bill of lading.";

        public static final String SUMMARY_DOWNLOAD = "Download Bill of Lading document with the specified id";
        public static final String DESC_DOWNLOAD = "Downloads the Bill of Lading PDF document for the given BL ID. "
            + "Requires JWT authentication. The authenticated customer must have access to the BL. "
            + "Returns the PDF as binary content (application/octet-stream).";
        public static final String OPERATION_ID_DOWNLOAD = "downloadBillOfLading";

        public static final String PARAM_BL_ID = "Bill of Lading ID";
        public static final String PARAM_BL_ID_EXAMPLE = "TSHG2500001";

        public static final String RESPONSE_200_DOWNLOAD = "The binary file content.";
        public static final String RESPONSE_404_BL = "Bill of Lading not found or PDF not available.";
    }

    /**
     * Parameter descriptions
     */
    public static final class Parameters {
        public static final String FROM_DATE = "Start date (YYYY-MM-DD)";
        public static final String TO_DATE = "End date (YYYY-MM-DD)";
        public static final String CONTAINER_NUMBER = "Container number to track";
        public static final String BOOKING_REFERENCE = "Booking reference number to track";
        public static final String VESSEL_CODE = "Vessel code to filter by";
        public static final String PORT_CODE = "Port code to filter by";
    }

    /** Reusable example values */
    public static final class ExampleValues {
        public static final String EX_DATETIME_TZ = "2025-01-22T10:30:00Z";
        /** ISO 8601 date-time for API dates (e.g. submitted date timestamp). */
        public static final String EX_DATE_TIME = "2025-09-10T00:00:00Z";
    }

    /** Status-specific example payloads */
    public static final class Examples {
        public static final String ERROR_VALIDATION =
            "{\n" +
            "  \"statusCode\": 400,\n" +
            "  \"statusCodeText\": \"Bad Request\",\n" +
            "  \"errorDateTime\": \"" + ExampleValues.EX_DATETIME_TZ + "\",\n" +
            "  \"errors\": [\n" +
            "    {\"errorCode\":2001,\"field\":\"username\",\"reason\":\"validationError\",\"message\":\"UserName is required\"}\n" +
            "  ]\n" +
            "}";

        public static final String ERROR_INTERNAL =
            "{\n" +
            "  \"statusCode\": 500,\n" +
            "  \"statusCodeText\": \"Internal Server Error\",\n" +
            "  \"errorDateTime\": \"" + ExampleValues.EX_DATETIME_TZ + "\",\n" +
            "  \"errors\": [\n" +
            "    {\"errorCode\":1000,\"reason\":\"internalError\",\"message\":\"Unexpected server error\"}\n" +
            "  ]\n" +
            "}";

        public static final String ERROR_UNAUTHORIZED =
            "{\n" +
            "  \"statusCode\": 401,\n" +
            "  \"statusCodeText\": \"Unauthorized\",\n" +
            "  \"errorDateTime\": \"" + ExampleValues.EX_DATETIME_TZ + "\",\n" +
            "  \"errors\": [\n" +
            "    {\"errorCode\":1001,\"reason\":\"unauthorized\",\"message\":\"Authentication required or invalid token\"}\n" +
            "  ]\n" +
            "}";

        public static final String ERROR_FORBIDDEN =
            "{\n" +
            "  \"statusCode\": 403,\n" +
            "  \"statusCodeText\": \"Forbidden\",\n" +
            "  \"errorDateTime\": \"" + ExampleValues.EX_DATETIME_TZ + "\",\n" +
            "  \"errors\": [\n" +
            "    {\"errorCode\":1002,\"reason\":\"forbidden\",\"message\":\"You do not have permission to access this resource\"}\n" +
            "  ]\n" +
            "}";

        public static final String ERROR_SERVICE_UNAVAILABLE =
            "{\n" +
            "  \"statusCode\": 503,\n" +
            "  \"statusCodeText\": \"Service Unavailable\",\n" +
            "  \"errorDateTime\": \"" + ExampleValues.EX_DATETIME_TZ + "\",\n" +
            "  \"errors\": [\n" +
            "    {\"errorCode\":1003,\"reason\":\"serviceUnavailable\",\"message\":\"Downstream service timeout\"}\n" +
            "  ]\n" +
            "}";

        /** V2 APIs: client request standard (ErrorMessage schema) ? Customer API validation example; type is hidden in schema */
        public static final String ERROR_V2_VALIDATION =
            "{\"detail\":\"Invalid date format for 'updatedAt_after' parameter. Expected format: ISO 8601 date-time (e.g., 2026-01-01T00:00:00Z)\",\"title\":\"Validation Failed\",\"status\":400,\"instance\":\"/api/v2/master/customers\",\"code\":\"VALIDATION_FAILED\",\"errors\":[{\"detail\":\"Invalid date format for 'updatedAt_after' parameter. Expected format: ISO 8601 date-time (e.g., 2026-01-01T00:00:00Z)\",\"parameter\":\"updatedAt_after\",\"code\":\"2002\"}]}";
        public static final String ERROR_V2_UNAUTHORIZED =
            "{\"detail\":\"Authentication failed (Missing or invalid Bearer token).\","
            + "\"title\":\"Unauthorized\",\"status\":401,"
            + "\"instance\":\"/api/v2/contracts\",\"code\":\"UNAUTHORIZED\","
            + "\"errors\":[{\"detail\":\"Missing or invalid Bearer token.\",\"code\":\"INVALID_TOKEN\"}]}";
        public static final String ERROR_V2_FORBIDDEN =
            "{\"detail\":\"The client does not have access rights to the content.\","
            + "\"title\":\"Forbidden\",\"status\":403,"
            + "\"instance\":\"/api/v2/contracts\",\"code\":\"FORBIDDEN\","
            + "\"errors\":[{\"detail\":\"You do not have permission to access this resource.\",\"code\":\"ACCESS_DENIED\"}]}";
        public static final String ERROR_V2_INTERNAL =
            "{\"detail\":\"An unexpected server-side error occurred.\","
            + "\"title\":\"Internal Server Error\",\"status\":500,"
            + "\"instance\":\"/api/v2/contracts\",\"code\":\"INTERNAL_ERROR\","
            + "\"errors\":[{\"detail\":\"An unexpected error occurred. Please try again later.\",\"code\":\"SERVER_ERROR\"}]}";
        public static final String ERROR_V2_SERVICE_UNAVAILABLE =
            "{\"detail\":\"The service is currently not available.\","
            + "\"title\":\"Service Unavailable\",\"status\":503,"
            + "\"instance\":\"/api/v2/contracts\",\"code\":\"SERVICE_UNAVAILABLE\","
            + "\"errors\":[{\"detail\":\"The service is temporarily unavailable. Please try again later.\",\"code\":\"SERVICE_DOWN\"}]}";

        /** V2 Agency API - 400 validation example */
        public static final String ERROR_V2_AGENCY_VALIDATION =
            "{\"detail\":\"Agency data validation failed. Please review the request.\",\"title\":\"Validation Failed\",\"status\":400,\"instance\":\"/api/v2/master/agencies\",\"code\":\"VALIDATION_FAILED\",\"errors\":[{\"detail\":\"Invalid or missing query parameter.\",\"parameter\":\"agencyCode\",\"code\":\"INVALID_PARAMETER\"}]}";

        /** V2 Commodity API - 400 validation example */
        public static final String ERROR_V2_COMMODITY_VALIDATION =
            "{\"detail\":\"Commodity data validation failed. Please review the request.\",\"title\":\"Validation Failed\",\"status\":400,\"instance\":\"/api/v2/master/commodities\",\"code\":\"VALIDATION_FAILED\",\"errors\":[{\"detail\":\"Invalid or missing query parameter.\",\"parameter\":\"commodityCode\",\"code\":\"INVALID_PARAMETER\"}]}";

        /** V2 DG Packaging API - 400 validation example */
        public static final String ERROR_V2_DG_PACKAGING_VALIDATION =
            "{\"detail\":\"DG packaging data validation failed. Please review the request.\","
            + "\"title\":\"Validation Failed\",\"status\":400,"
            + "\"instance\":\"/api/v2/master/dg-packagings\",\"code\":\"VALIDATION_FAILED\","
            + "\"errors\":[{\"detail\":\"Invalid usage value. Valid values are: OUTER, INNER.\","
            + "\"parameter\":\"usage\",\"code\":\"INVALID_PARAMETER\"}]}";

        /** V2 IMDG Entries API - 400 validation example */
        public static final String ERROR_V2_IMDG_VALIDATION =
            "{\"detail\":\"IMDG data validation failed. Please review the request.\","
            + "\"title\":\"Validation Failed\",\"status\":400,"
            + "\"instance\":\"/api/v2/master/imdg-entries\",\"code\":\"VALIDATION_FAILED\","
            + "\"errors\":[{\"detail\":\"unNo is required and must be 4 digits.\","
            + "\"parameter\":\"unNo\",\"code\":\"DG_UNNO_INVALID\"}]}";

        /** V2 DG Packaging list - 200 response example */
        public static final String DG_PACKAGING_LIST_200 =
            "[{\"code\":\"1G\",\"name\":\"FIBRE DRUM\",\"packagingType\":\"OUTER\"},"
            + "{\"code\":\"CYLGA\",\"name\":\"CYLINDERS\",\"packagingType\":\"BOTH\"}]";

        /** V2 IMDG entries list - 200 response example */
        public static final String IMDG_ENTRY_LIST_200 =
            "[{\"unNo\":\"2779\",\"variant\":\"~\",\"imdgAmendment\":\"42\",\"imoClass\":\"6.1\","
            + "\"packagingGroup\":\"II\",\"ems\":\"F-A,S-A\",\"state\":\"SOLID\","
            + "\"properShippingName\":\"SUBSTITUTED NITROPHENOL PESTICIDE, SOLID, TOXIC\","
            + "\"technicalName\":\"DINITRO-ORTHO-CRESOL\","
            + "\"stowageInstruction\":\"SW2 Clear of living quarters\",\"imoStow\":\"On Deck Only\"}]";

        /** V2 Agency list - 200 response example */
        public static final String AGENCY_LIST_200 =
            "[{\"code\":\"CNMNGB\",\"name\":\"Moov Logistics\",\"type\":\"Office\","
            + "\"address\":{\"lines\":[\"Floor 4, Unit 12\",\"123 Innovation Drive\"],\"locality\":\"Ningbo\","
            + "\"postalCode\":null,\"countryCode\":\"CN\"},\"email\":\"cnmngb@example.com\","
            + "\"unPortCode\":\"CNNGB\","
            + "\"controllingPorts\":[{\"code\":\"CNAQG\",\"validFrom\":\"2022-01-01\",\"validTo\":\"2030-12-31\","
            + "\"type\":\"Export\"}]},"
            + "{\"code\":\"DEHAM\",\"name\":\"Hamburg Central\",\"type\":null,"
            + "\"address\":{\"lines\":[\"\"],\"locality\":\"Hamburg\",\"postalCode\":null,\"countryCode\":\"DE\"},"
            + "\"email\":\"contact@example.com\",\"unPortCode\":\"DEHAM\",\"controllingPorts\":[]}]";

        /** V2 Commodity list ? 200 response example */
        public static final String COMMODITY_LIST_200 =
            "[{\"code\":\"0802520000\",\"name\":\"Fresh or dried pistachios, shelled\",\"type\":\"23\","
            + "\"group\":{\"code\":\"08\",\"name\":\"Fruit and nuts, edible; peel of citrus fruit or melons\"},"
            + "\"isRestricted\":false,\"isExempt\":true}]";

        /** V2 Contract list - 200 response example */
        public static final String CONTRACT_LIST_200 =
            "[{\"id\":\"20250173\",\"name\":\"Hellmann Nov/ Dec 25\",\"amendmentNumber\":1,"
            + "\"agreementParty\":{\"code\":\"2024003615\",\"name\":\"SND SQUARE INTERNATIONAL LTD.\"},"
            + "\"namedAccounts\":[{\"code\":\"2023000722\",\"name\":\"ENUS LOGISTICS, S.L.\"}],"
            + "\"validFrom\":\"2026-01-01\",\"validTo\":\"2027-12-31\",\"effectiveDate\":\"2026-01-01\","
            + "\"status\":\"A\",\"portPairsCount\":10}]";

        /** V2 Contract detail - 200 response example */
        public static final String CONTRACT_DETAIL_200 =
            "{\"id\":\"20260251\",\"name\":\"Sample Contract\",\"amendmentNumber\":1,"
            + "\"agreementParty\":{\"code\":\"2024003615\",\"name\":\"SND SQUARE INTERNATIONAL LTD.\"},"
            + "\"namedAccounts\":[{\"code\":\"2023000722\",\"name\":\"ENUS LOGISTICS, S.L.\"}],"
            + "\"validFrom\":\"2026-04-26\",\"validTo\":\"2027-05-31\",\"effectiveDate\":\"2026-04-26\","
            + "\"status\":\"A\","
            + "\"portPairs\":["
            + "{"
            + "\"origin\":\"CNNGB\",\"pol\":\"CNNGB\",\"pod\":\"ESBCN\",\"delivery\":\"ESBCN\","
            + "\"equipmentType\":\"40HC\","
            + "\"transportService\":\"CY / CY\","
            + "\"serviceCode\":\"PAX\","
            + "\"isDG\":true,\"dgDetails\":\"0000~99\","
            + "\"isOOG\":true,\"oogDetails\":\"MM~~0.00*0.00*695.00*695.00*1724.00~12\","
            + "\"isSOC\":false,\"isNOR\":false,"
            + "\"ofr\":\"A\","
            + "\"oceanFreight\":6109.53,\"baseRate\":6109.53,\"totalFreight\":7671.27,"
            + "\"quantity\":1.0,\"currency\":\"EUR\","
            + "\"remarks\":\"Incl. BAF/SDD, subj. locals both ends + EPF\","
            + "\"commodityClass\":\"1\","
            + "\"validFrom\":\"2026-04-26\",\"validTo\":\"2027-05-31\","
            + "\"status\":\"A\","
            + "\"exportContainerRent\":14,\"importContainerRent\":15,"
            + "\"exportTerminalStorage\":7,\"importTerminalStorage\":4"
            + "}"
            + "]}";

        /** V2 Contract API - 400 bad request example */
        public static final String ERROR_V2_CONTRACT_BAD_REQUEST =
            "{\"detail\":\"Invalid request parameters. Please review the provided contractId and amendmentNumber.\","
            + "\"title\":\"Bad Request\",\"status\":400,"
            + "\"instance\":\"/api/v2/contracts/{contractId}\",\"code\":\"VALIDATION_FAILED\","
            + "\"errors\":["
            + "{\"detail\":\"contractId must not be empty.\",\"parameter\":\"contractId\",\"code\":\"INVALID_CONTRACT_ID\"},"
            + "{\"detail\":\"amendmentNumber must be a valid positive integer.\",\"parameter\":\"amendmentNumber\",\"code\":\"INVALID_AMENDMENT_NUMBER\"}"
            + "]}";

        /** V2 Contract API - 404 not found example */
        public static final String ERROR_V2_CONTRACT_NOT_FOUND =
            "{\"detail\":\"The contract could not be found or you don't have access to it.\","
            + "\"title\":\"Not Found\",\"status\":404,"
            + "\"instance\":\"/api/v2/contracts/20250173\",\"code\":\"CONTRACT_NOT_FOUND\","
            + "\"errors\":[{\"detail\":\"No contract found with the given id.\",\"parameter\":\"contractId\",\"code\":\"NOT_FOUND\"}]}";
    }
}
