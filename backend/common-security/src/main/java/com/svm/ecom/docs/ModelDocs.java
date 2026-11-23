package com.svm.ecom.docs;

/**
 * Centralized constants for Swagger/OpenAPI model documentation
 * Contains all descriptions and examples for DTOs, request/response objects, and model fields
 * to ensure consistency across the entire API documentation
 */
public final class ModelDocs {

    // Private constructor to prevent instantiation
    private ModelDocs() {}

    /**
     * Common field descriptions and examples used across multiple DTOs
     */
    public static final class Common {
        /**
         * Date format description: ISO-8601 date format (yyyy-MM-dd)
         */
        public static final String DATE_FMT = "Date in ISO-8601 format (yyyy-MM-dd)";
        
        /**
         * DateTime format description for schedule-related fields (local time, no timezone)
         * Used for: Departure Date, Arrival Date, ETD, ETA, polAt, podAt
         */
        public static final String DATETIME_FMT = "DateTime in local time format without timezone (yyyy-MM-dd'T'HH:mm:ss). Used for schedule-related fields stored as local time.";
        
        /**
         * DateTime format description for system-generated fields (UTC with Z suffix)
         * Used for: Created Date, Updated Date, Booking Date, Confirmed Date
         */
        public static final String DATETIME_TZ_FMT = "DateTime in RFC 3339 UTC format with Z suffix (yyyy-MM-dd'T'HH:mm:ssZ). Used for system-generated fields stored in UTC.";
        
        // Common examples
        public static final String EX_EMAIL = "john.doe@example.com";
        public static final String EX_CUSTOMER = "20001374";
        public static final String EX_PHONE = "+1-234-567-8900";
        public static final String EX_COMPANY = "Acme Corporation";
        public static final String EX_USER_NAME = "John Doe";
        public static final String EX_PASSWORD = "password123";
        public static final String EX_JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        public static final String EX_REFRESH_TOKEN = "rt_abc123def456";
        public static final String EX_USER_ID = "USR001";
        public static final String EX_DATE = "2025-07-04";
        
        /**
         * Example for local date-time (schedule-related fields)
         * Format: yyyy-MM-dd'T'HH:mm:ss (no timezone)
         */
        public static final String EX_DATETIME = "2025-07-11T00:00:00";
        
        /**
         * Example for UTC date-time (system-generated fields)
         * Format: yyyy-MM-dd'T'HH:mm:ssZ (with Z suffix)
         */
        public static final String EX_DATETIME_TZ = "2025-01-22T10:30:00Z";
        
        // Common field descriptions
        public static final String USER_ID = "Unique user identifier";
        public static final String CUSTOMER_CODE = "Customer code identifier";
        public static final String STATUS = "Status indicator";
        public static final String TIMESTAMP = "Timestamp when the operation occurred";
        
        /**
         * Created Date field description - system-generated, stored in UTC
         */
        public static final String CREATED_DATE = "Date when record was created. Stored in UTC and returned in RFC 3339 format with Z suffix.";
        
        /**
         * Updated Date field description - system-generated, stored in UTC
         */
        public static final String UPDATED_DATE = "Date when record was last updated. Stored in UTC and returned in RFC 3339 format with Z suffix.";
    }

    /**
     * ErrorResponse model documentation
     */
    public static final class ErrorResponse {
        public static final String DESC = "DCSA-style standardized error response format";
        public static final String STATUS_CODE = "HTTP status code";
        public static final String STATUS_CODE_TEXT = "HTTP status code text";
        public static final String ERROR_DATETIME = "Timestamp when the error occurred (with timezone)";
        public static final String ERRORS = "Array of detailed error information";
        
        public static final String EX_STATUS_CODE = "400";
        public static final String EX_STATUS_CODE_TEXT = "Bad Request";
    }

    /**
     * ErrorDetail model documentation
     */
    public static final class ErrorDetail {
        public static final String DESC = "Detailed error information";
        public static final String ERROR_CODE = "Implementation-specific error code";
        public static final String FIELD = "Name of the field that caused the error";
        public static final String VALUE = "Value that caused the error";
        public static final String REASON = "Machine-friendly error classification. Possible values: validationError, badRequest, unauthorized, forbidden, businessRule, internalError, methodNotAllowed, unsupportedMediaType, serviceUnavailable";
        public static final String MESSAGE = "Human-readable error message";
        
        public static final String EX_ERROR_CODE = "1001";
        public static final String EX_FIELD = "username";
        public static final String EX_VALUE = "ecomapi";
        public static final String EX_REASON = "validationError";
        public static final String EX_MESSAGE = "UserName is required";
    }

    /**
     * LoginRequest model documentation
     */
    public static final class LoginRequest {
        public static final String DESC = "Request object for user login authentication";
        public static final String USERNAME = "Username for authentication";
        public static final String PASSWORD = "Password for authentication";
        
        public static final String EX_USERNAME = "username";
    }

    /**
     * LoginResponse model documentation
     */
    public static final class LoginResponse {
        public static final String DESC = "Login response containing JWT access token";
        public static final String TOKEN = "JWT access token. Include in Authorization header as 'Bearer {token}'";
    }

    /**
     * RegLoginRequest model documentation
     */
    public static final class RegLoginRequest {
        public static final String DESC = "Request object for user login";
        public static final String EMAIL = "User's email address (username)";
        public static final String PASSWORD = "User's password";
    }

    /**
     * RegLoginResponse model documentation
     */
    public static final class RegLoginResponse {
        public static final String DESC = "Response object for successful authentication";
        public static final String ACCESS_TOKEN = "JWT access token for API authentication";
        public static final String REFRESH_TOKEN = "Refresh token for obtaining new access tokens";
        public static final String TOKEN_TYPE = "Token type";
        public static final String EXPIRES_IN = "Token expiration time in seconds";
        
        public static final String EX_TOKEN_TYPE = "Bearer";
        public static final String EX_EXPIRES_IN = "3600";
    }

    /**
     * RegistrationRequest model documentation
     */
    public static final class RegistrationRequest {
        public static final String DESC = "Request object for user registration";
        public static final String NAME = "User's full name";
        public static final String EMAIL = "User's email address";
        public static final String COMPANY = "User's company name";
        public static final String PHONE = "User's phone number";
    }

    /**
     * RegistrationResponse model documentation
     */
    public static final class RegistrationResponse {
        public static final String DESC = "Response object for user registration";
        public static final String USER_ID = Common.USER_ID;
        public static final String STATUS = "Registration status";
        
        public static final String EX_STATUS = "SUCCESS";
    }

    /**
     * TrackingRequest model documentation
     */
    public static final class TrackingRequest {
        public static final String DESC = "Request object for tracking container information";
        public static final String KEY = "Booking number, BL number, or container number for tracking. Can be a booking reference, bill of lading number, or container number";
        public static final String CONTAINER = "Specific container number to display from the booking. Optional: Used to filter and display only this specific container from the booking/BL. If not provided, returns all containers for the key.";
        
        public static final String EX_KEY = "TSHGCGP23000031";
        public static final String EX_CONTAINER = "RCDU7017035";
    }

    /**
     * BookingSummaryResponseDto model documentation
     */
    public static final class BookingSummaryResponseDto {
        public static final String DESC = "DTO for transformed Booking Summary response format";
        public static final String BOOKING_NUMBER = "LRP booking number";
        public static final String ONLINE_BOOK_NO = "Online booking number";
        public static final String ORIGIN_PORT_ID = "Origin port identifier";
        public static final String FINAL_PORT_ID = "Final destination port identifier";
        public static final String CREATED_DATE = Common.CREATED_DATE + " " + Common.DATETIME_TZ_FMT;
        public static final String CHANGE_DATE = Common.UPDATED_DATE + " " + Common.DATETIME_TZ_FMT;
        public static final String STATUS = "Booking Status: D (Draft), S (Submitted), P (In-Progress), C (Confirmed), L (Cancelled), V (Rejected), T (Template)";
        public static final String ECOM_LRP_STATUS = "ECOM LRP status indicator (E=Electronic, others may vary)";
        public static final String SUBMITTED_BY = "Email of user who submitted the booking";
        public static final String AGENCY_REF_NO = "Agency reference number (may be empty)";
        public static final String AGENCY_ID = "Agency identifier code";
        public static final String CONFIRMED_DATE = "Date when booking was confirmed. Stored in UTC and returned in RFC 3339 format with Z suffix (may be empty).";
        public static final String STATUS_DESC = "Human-readable status description (e.g., Submitted, Active)";
        public static final String TUES_COUNT = "TEUS count value";
        public static final String BOOKING_CREATED_USER = "Username of user who created the booking";
        public static final String DEPARTURE_DATE = "Planned departure date in local time format yyyy-MM-dd'T'HH:mm:ss";
        public static final String BOOKING_SOURCE = "Booking Source: C \u2013 ECOM Booking, E \u2013 EDI Booking, T \u2013 LRP Booking";
        
        public static final String EX_BOOKING_NUMBER = "SVMDMU2588541";
        public static final String EX_ONLINE_BOOK_NO = "BKON5862";
        public static final String EX_ORIGIN_PORT = "INNSA";
        public static final String EX_FINAL_PORT = "DEHAM";
        public static final String EX_STATUS_CODE = "S";
        public static final String EX_AGENCY_REF = "";
        public static final String EX_AGENCY_ID = "CNMNGB";
        public static final String EX_STATUS_DESC = "Submitted";
        public static final String EX_TUES_COUNT = "1";
        public static final String EX_USER = "arul.prakash@solverminds.com";
        public static final String EX_ECOM_LRP_STATUS = "E";
        public static final String EX_BOOKING_CREATED_USER = "SUPERUSER";
        public static final String EX_CONFIRMED_DATE = "2025-07-04T14:30:00Z";
        public static final String EX_CREATED_DATE = "2025-07-04";
        public static final String EX_CHANGE_DATE = "2025-07-04";
        public static final String EX_DEPARTURE_DATE = "2025-07-11T00:00:00";
        public static final String EX_BOOKING_SOURCE = "C";
    }

    /**
     * DashboardSummaryDto model documentation
     */
    public static final class DashboardSummaryDto {
        public static final String DESC = "Dashboard summary data for authenticated customer from stored procedure";
        public static final String ONLINE_BOOK_NO = "Online booking reference number";
        public static final String ORIGIN_PORT_ID = "Origin port identifier";
        public static final String FINAL_PORT_ID = "Final destination port identifier";
        public static final String ORIGIN_PORT_DESC = "Origin port description";
        public static final String FINAL_PORT_DESC = "Final port description";
        public static final String AGENCY_ID = "Agency identifier";
        public static final String DEPARTURE_DATE = "Departure date in local time format yyyy-MM-dd'T'HH:mm:ss";
        public static final String LOAD_PORT_ID = "Load port identifier (UN/LOCODE)";
        public static final String DISCHARGE_PORT_ID = "Discharge port identifier (UN/LOCODE)";
        public static final String BOOK_DATE = "Booking date when the booking was created. Stored in UTC and returned in RFC 3339 format with Z suffix.";
        public static final String BOOKING_NUMBER = "Booking number";
        public static final String STATUS = Common.STATUS;
        public static final String BL_NUMBER = "Bill of Lading number";
        public static final String CONTAINER_NO = "Container number";
        public static final String LATEST_ACTIVITY_CODE = "Latest activity code";
        public static final String TUES_COUNT = "TUES count value";
        
        public static final String EX_ONLINE_BOOK_NO = "BKON5305";
        public static final String EX_ORIGIN_PORT = "HKHKG";
        public static final String EX_FINAL_PORT = "INNSA";
        public static final String EX_ORIGIN_PORT_DESC = "HONG KONG";
        public static final String EX_FINAL_PORT_DESC = "JAWAHARLAL NEHRU (NHAVA SHEVA), INDIA";
        public static final String EX_AGENCY_ID = "CHNSCN";
        public static final String EX_DEPARTURE_DATE = "2025-03-27T00:00:00";
        public static final String EX_LOAD_PORT = "HKHKG";
        public static final String EX_DISCHARGE_PORT = "INNSA";
        public static final String EX_BOOK_DATE = "2025-03-19T21:53:00Z";
        public static final String EX_BOOKING_NUMBER = "SVMNSC25000032";
        public static final String EX_STATUS = "";
        public static final String EX_BL_NUMBER = "";
        public static final String EX_CONTAINER_NO = "20DCTNB12";
        public static final String EX_LATEST_ACTIVITY = "";
        public static final String EX_TUES_COUNT = "1";
    }

    /**
     * CustomerReleaseOrderDto model documentation
     */
    public static final class CustomerReleaseOrderDto {
        public static final String DESC = "Customer Release Order (CRO) details containing booking and container information";
        public static final String BOOKING_NUMBER = "Booking number identifier";
        public static final String SERVICE_CODE = "Service code for the shipping route";
        public static final String VESSEL_CODE = "Vessel code identifier";
        public static final String VOYAGE_CODE = "Voyage code for the specific sailing";
        public static final String BOUND = "Direction bound (E=East, W=West, N=North, S=South)";
        public static final String TERMINAL = "Terminal name where containers are located";
        public static final String ORIGIN_PORT_ID = "Origin port identifier (UN/LOCODE)";
        public static final String FINAL_PORT_ID = "Final destination port identifier (UN/LOCODE)";
        public static final String LOAD_PORT_ID = "Loading port identifier (UN/LOCODE)";
        public static final String DISCHARGE_PORT_ID = "Discharge port identifier (UN/LOCODE)";
        public static final String EQUIPMENT_TYPE = "Equipment/container type (e.g., DV20, DV40, HC40)";
        public static final String BOOKING_QTY = "Total quantity booked";
        public static final String RELEASED_QTY = "Quantity released for collection";
        public static final String AGENCY_CODE = "Agency code identifier";
        
        public static final String EX_BOOKING_NUMBER = "SVMMUN24000408";
        public static final String EX_SERVICE_CODE = "8072";
        public static final String EX_VESSEL_CODE = "266";
        public static final String EX_VOYAGE_CODE = "446622";
        public static final String EX_BOUND = "W";
        public static final String EX_TERMINAL = "DUBAI DEPOT";
        public static final String EX_ORIGIN_PORT_ID = "INNSA";
        public static final String EX_FINAL_PORT_ID = "MYPKG";
        public static final String EX_LOAD_PORT_ID = "INNSA";
        public static final String EX_DISCHARGE_PORT_ID = "MYPKG";
        public static final String EX_EQUIPMENT_TYPE = "DV20";
        public static final String EX_BOOKING_QTY = "3";
        public static final String EX_RELEASED_QTY = "3";
        public static final String EX_AGENCY_CODE = "INDMUN";
    }

    /**
     * ArrivalNoticeDto model documentation
     */
    public static final class ArrivalNoticeDto {
        public static final String DESC = "Arrival notice information for containers and cargo";
        public static final String CUSTOMER_CODE = Common.CUSTOMER_CODE;
        public static final String BL_NUMBER = "Bill of Lading number";
        public static final String ARRIVAL_NOTICE_NO = "Arrival notice number";
        public static final String ORIGIN_PORT_ID = "Origin port identifier";
        public static final String FINAL_PORT_ID = "Final port identifier";
        public static final String VESSEL_CODE = "Vessel code";
        public static final String VOYAGE = "Voyage number";
        public static final String BOUND = "Bound direction - E (East), W (West), N (North), S (South)";
        public static final String HBL_NUMBER = "House Bill of Lading number";
        public static final String ARR_DATE = "Arrival date";
        public static final String POD_TERMINAL = "Port of discharge terminal";
        public static final String ECOM_PRINT_STATUS = "ECOM print status: 0 (Disable), 1 (Enable)";
        
        public static final String EX_CUSTOMER_CODE = "20001374";
        public static final String EX_BL_NUMBER = "BL20240115001";
        public static final String EX_ARRIVAL_NOTICE_NO = "AN20240115001";
        public static final String EX_ORIGIN_PORT = "INMAA";
        public static final String EX_FINAL_PORT = "SGSIN";
        public static final String EX_VESSEL_CODE = "BUX";
        public static final String EX_VOYAGE = "001N";
        public static final String EX_BOUND = "N";
        public static final String EX_HBL_NUMBER = "HBL20240115001";
        public static final String EX_ARR_DATE = "2024-02-15T00:00:00";
        public static final String EX_POD_TERMINAL = "PSA";
        public static final String EX_ECOM_PRINT_STATUS = "1";
    }

    /**
     * DeliveryOrderDto model documentation
     */
    public static final class DeliveryOrderDto {
        public static final String DESC = "Delivery order information for container release";
        public static final String PRINT_STATUS = "Print status";
        public static final String ECOM_PRINT_STATUS = "ECOM print status";
        public static final String DEL_ORD_NO = "Delivery order number";
        public static final String DO_VALIDITY_DATE = "Delivery order validity date";
        public static final String DEL_ORD_DATE = "Delivery order date";
        public static final String BL_NUMBER = "Bill of Lading number";
        public static final String VESSEL = "Vessel name";
        public static final String VOYAGE = "Voyage number";
        public static final String BOUND = "Bound direction - E (East), W (West), N (North), S (South)";
        public static final String LOAD_PORT = "Load port";
        public static final String DISCHARGE_PORT = "Discharge port";
        public static final String TERMINAL = "Terminal";
        public static final String ARR_DATE = "Arrival date";
        
        public static final String EX_PRINT_STATUS = "PRINTED";
        public static final String EX_ECOM_PRINT_STATUS = "READY";
        public static final String EX_DEL_ORD_NO = "DO20240115001";
        public static final String EX_DO_VALIDITY_DATE = "2024-03-15";
        public static final String EX_DEL_ORD_DATE = "2024-02-15";
        public static final String EX_BL_NUMBER = "BL20240115001";
        public static final String EX_VESSEL = "BUNGA RAYA DUA";
        public static final String EX_VOYAGE = "001N";
        public static final String EX_BOUND = "N";
        public static final String EX_LOAD_PORT = "INMAA";
        public static final String EX_DISCHARGE_PORT = "SGSIN";
        public static final String EX_TERMINAL = "PSA";
        public static final String EX_ARR_DATE = "2024-02-15T14:30:00";
    }

    /**
     * VesselView model documentation
     */
    public static final class VesselView {
        public static final String DESC = "Vessel information";
        public static final String VESSEL_CODE = "Vessel code identifier";
        public static final String VESSEL_NAME = "Vessel name";
        
        public static final String EX_VESSEL_CODE = "BUX";
        public static final String EX_VESSEL_NAME = "BUNGA RAYA DUA";
    }

    /**
     * PortView model documentation
     */
    public static final class PortView {
        public static final String DESC = "Port information";
        public static final String PORT_CODE = "Port code identifier (UN/LOCODE)";
        public static final String PORT_NAME = "Port name";
        public static final String PORT_TYPE = "Port type indicator";
        
        public static final String EX_PORT_CODE = "SGSIN";
        public static final String EX_PORT_NAME = "SINGAPORE";
        public static final String EX_PORT_TYPE = "P";
    }

    /**
     * ScheduleResponse model documentation
     */
    public static final class ScheduleResponse {
        public static final String DESC = "Schedule information response";
        public static final String SCHEDULE_DATA = "Array of schedule entries containing vessel information, voyage details, port schedules with arrival/departure times, transit times, and routing information for the requested schedule query";
        public static final String MESSAGE_CODE = "Response message code";
        public static final String MESSAGE = "Response message";
        public static final String TIMESTAMP = Common.TIMESTAMP + ". " + Common.DATETIME_TZ_FMT;
        public static final String PROCESSED_BY = "User who processed the request";
        
        public static final String EX_MESSAGE_CODE = "200";
        public static final String EX_MESSAGE = "Success";
        public static final String EX_PROCESSED_BY = "user1";
    }

    /**
     * PortPair model documentation
     */
    public static final class PortPair {
        public static final String DESC = "Port pair information containing vessel and schedule details between two ports";
        public static final String POL = "Port of loading code (UN/LOCODE format)";
        public static final String POD = "Port of discharge code (UN/LOCODE format)";
        public static final String POL_NAME = "Port of loading full name";
        public static final String POD_NAME = "Port of discharge full name";
        public static final String ETD = "Estimated time of departure from POL";
        public static final String ETA = "Estimated time of arrival at POD";
        public static final String SERVICE_CODE = "Shipping line service code";
        public static final String SERVICE_NAME = "Shipping line service name";
        public static final String VESSEL_CODE = "Vessel code identifier";
        public static final String VESSEL_NAME = "Vessel name";
        public static final String VESSEL_IMO = "Vessel IMO number (International Maritime Organization identifier)";
        public static final String VESSEL_OPERATOR = "Vessel operator code";
        public static final String VESSEL_OPERATOR_NAME = "Vessel operator company name";
        public static final String VESSEL_OWNER_TYPE = "Type of vessel ownership (e.g., Chartered In, Owned)";
        public static final String VOYAGE_NUMBER = "Voyage number for this sailing";
        public static final String BOUND = "Bound direction (E=Eastbound, W=Westbound, N=Northbound, S=Southbound)";
        public static final String SCHEDULE_TYPE = "Type of schedule (Mainline, Feeder, Barge)";
        public static final String POL_TERMINAL = "Port of loading terminal code";
        public static final String POD_TERMINAL = "Port of discharge terminal code";
        public static final String POL_TERMINAL_NAME = "Port of loading terminal name";
        public static final String POD_TERMINAL_NAME = "Port of discharge terminal name";
        
        public static final String EX_POL = "CNNGB";
        public static final String EX_POD = "ESBCN";
        public static final String EX_POL_NAME = "NINGBO, CHINA";
        public static final String EX_POD_NAME = "BARCELONA, SPAIN";
        public static final String EX_ETD = "2024-02-07T06:40:00";
        public static final String EX_ETA = "2024-03-14T12:30:00";
        public static final String EX_SERVICE_CODE = "PAX";
        public static final String EX_SERVICE_NAME = "PANDA EXPRESS";
        public static final String EX_VESSEL_CODE = "BUX";
        public static final String EX_VESSEL_NAME = "BUXWAVE";
        public static final String EX_VESSEL_IMO = "9320427";
        public static final String EX_VESSEL_OPERATOR = "TSH";
        public static final String EX_VESSEL_OPERATOR_NAME = "TAILWIND SHIPPING LINE";
        public static final String EX_VESSEL_OWNER_TYPE = "Chartered In";
        public static final String EX_VOYAGE_NUMBER = "403";
        public static final String EX_BOUND = "W";
        public static final String EX_SCHEDULE_TYPE = "Mainline";
        public static final String EX_POL_TERMINAL = "CNNGBCTT00";
        public static final String EX_POD_TERMINAL = "ESBCNHPB00";
        public static final String EX_POL_TERMINAL_NAME = "Ningbo Beilun Second Container Terminals Co.,LTD";
        public static final String EX_POD_TERMINAL_NAME = "Hutchison Ports BEST";
    }

    /**
     * ScheduleEntry model documentation
     */
    public static final class ScheduleEntry {
        public static final String DESC = "Complete schedule entry containing port pairs and complete voyage information from origin to destination";
        public static final String PORTPAIR = "Array of port pairs showing the complete routing with vessel changes and transshipments";
        public static final String ORIGIN = "Origin port code (first port in the journey)";
        public static final String DELIVERY = "Final delivery port code (last port in the journey)";
        public static final String ORIGIN_NAME = "Origin port full name";
        public static final String DELIVERY_NAME = "Final delivery port full name";
        public static final String ETD = "Overall estimated time of departure from origin";
        public static final String ETA = "Overall estimated time of arrival at final destination";
        public static final String SERVICE_CODE = "Primary service code for the route";
        public static final String SERVICE_NAME = "Primary service name for the route";
        public static final String VESSEL_CODE = "Primary vessel code";
        public static final String VESSEL_NAME = "Primary vessel name";
        public static final String VESSEL_IMO = "Primary vessel IMO number";
        public static final String VESSEL_OPERATOR = "Primary vessel operator code";
        public static final String VESSEL_OPERATOR_NAME = "Primary vessel operator name";
        public static final String VESSEL_OWNER_TYPE = "Primary vessel owner type";
        public static final String VOYAGE_NUMBER = "Primary voyage number";
        public static final String BOUND = "Primary bound direction";
        public static final String LEG_TYPE = "Type of leg (Single, Multiple)";
        public static final String DEFAULT_ROUTE = "Whether this is the default/recommended route (Y/N)";
        public static final String TRANSIT_TIME = "Total transit time in days from origin to destination";
        public static final String LOAD_CALLID = "Loading call ID";
        public static final String DISCH_CALLID = "Discharge call ID";
        public static final String DEFAULT_ROUTE_ORDER = "Order of this route if multiple default routes exist";
        public static final String INLAND_FLAG = "Whether inland transportation is involved (Y/N)";
        public static final String TRANSSHIPMENT_COUNT = "Number of transshipments/vessel changes";
        public static final String SOURCE_PORT_NAME = "Source port name (same as origin_name)";
        public static final String DESTINATION_PORT_NAME = "Destination port name (same as delivery_name)";
        public static final String SOURCE_COUNTRY = "Source country name";
        public static final String DESTINATION_COUNTRY = "Destination country name";
        public static final String NO_OF_TRANSSHIPMENT = "Number of transshipments (same as transshipment_count)";
        public static final String FEEDER_INVOLVED = "Whether feeder services are involved in the route";
        public static final String SECONDARY1 = "Secondary flag 1 for additional routing information";
        public static final String SECONDARY2 = "Secondary flag 2 for additional routing information";
        
        public static final String EX_ORIGIN = "CNNGB";
        public static final String EX_DELIVERY = "NLRTM";
        public static final String EX_ORIGIN_NAME = "NINGBO, CHINA";
        public static final String EX_DELIVERY_NAME = "ROTTERDAM, NETHERLANDS";
        public static final String EX_ETD = "2024-02-07T06:40:00";
        public static final String EX_ETA = "2024-03-22T18:18:00";
        public static final String EX_LEG_TYPE = "Multiple";
        public static final String EX_DEFAULT_ROUTE = "Y";
        public static final String EX_TRANSIT_TIME = "44";
        public static final String EX_LOAD_CALLID = "GrpCall-5499";
        public static final String EX_DISCH_CALLID = "GrpCall-5449";
        public static final String EX_DEFAULT_ROUTE_ORDER = "1";
        public static final String EX_INLAND_FLAG = "Y";
        public static final String EX_TRANSSHIPMENT_COUNT = "1";
        public static final String EX_SOURCE_COUNTRY = "CHINA";
        public static final String EX_DESTINATION_COUNTRY = "NETHERLANDS";
        public static final String EX_NO_OF_TRANSSHIPMENT = "1";
        public static final String EX_FEEDER_INVOLVED = "false";
        public static final String EX_SECONDARY1 = "N";
        public static final String EX_SECONDARY2 = "N";
    }

    /**
     * ChangePasswordRequest model documentation
     */
    public static final class ChangePasswordRequest {
        public static final String DESC = "Request for changing user password";
        public static final String CURRENT_PASSWORD = "User's current password";
        public static final String NEW_PASSWORD = "New password to set";
        public static final String CONFIRM_PASSWORD = "Confirmation of new password";
    }

    /**
     * ChangePasswordResponse model documentation
     */
    public static final class ChangePasswordResponse {
        public static final String DESC = "Response for password change operation";
        public static final String SUCCESS = "Indicates if password change was successful";
        public static final String MESSAGE = "Status message";
        
        public static final String EX_SUCCESS = "true";
        public static final String EX_MESSAGE = "Password changed successfully";
    }

    /**
     * ForgotPasswordRequest model documentation
     */
    public static final class ForgotPasswordRequest {
        public static final String DESC = "Request to initiate password reset process";
        public static final String EMAIL = "Email address for password reset";
    }

    /**
     * ResetPasswordRequest model documentation
     */
    public static final class ResetPasswordRequest {
        public static final String DESC = "Request to reset password (forgot password flow - no authentication required)";
        public static final String EMAIL = "User's registered email address";
        public static final String NEW_PASSWORD = "New password to set";
        public static final String CONFIRM_PASSWORD = "Confirmation of new password (must match new password)";
    }

    /**
     * ResetPasswordResponse model documentation
     */
    public static final class ResetPasswordResponse {
        public static final String DESC = "Response for password reset operation";
        public static final String SUCCESS = "Indicates if password reset was successful";
        public static final String MESSAGE = "Status message";
        
        public static final String EX_SUCCESS = "true";
        public static final String EX_MESSAGE = "Password reset successfully";
    }

    /**
     * RefreshTokenRequest model documentation
     */
    public static final class RefreshTokenRequest {
        public static final String DESC = "Request to refresh JWT access token";
        public static final String REFRESH_TOKEN = "Valid refresh token";
    }

    /**
     * SiSummaryDto model documentation
     */
    public static final class SiSummaryDto {
        public static final String DESC = "Shipping Instruction Summary data from stored procedure";
        public static final String BOOK_NO = "Booking number";
        public static final String DOC_ID = "Document ID";
        public static final String SI_STATUS = "SI Status: D (Draft), S (Submitted), A (Accepted), R (Declined)";
        public static final String BOOK_SOURCE = "Book source";
        public static final String BL_NO = "Bill of Lading number";
        public static final String BL_STATUS = "BL Status: C (Confirmed), I (Issued), D (Draft), A (Accepted), R (Rejected), S (Send Correction)";
        public static final String LOAD_PORT_ID = "Load port ID";
        public static final String DISCHARGE_PORT_ID = "Discharge port ID";
        public static final String POL_AT = "POL at";
        public static final String CREATED_SI = "Created SI";
        public static final String REQUESTED_SI = "Requested SI";
        public static final String BK_STATUS = "Booking Status: D (Draft), S (Submitted), P (In-Progress), C (Confirmed), L (Cancelled), V (Rejected), T (Template)";
        public static final String AGENCY_REF_NO = "Agency reference number";
        public static final String APP_VERSION = "Application version";
        public static final String ORIGIN_PORT_ID = "Origin port ID";
        public static final String FINAL_PORT_ID = "Final port ID";
        public static final String IS_SI_LOCKED = "Is SI locked";
        public static final String DATE_CR = Common.CREATED_DATE + " " + Common.DATETIME_TZ_FMT;
        public static final String DATE_UP = Common.UPDATED_DATE + " " + Common.DATETIME_TZ_FMT;
        
        public static final String EX_BOOK_NO = "BKG001";
        public static final String EX_DOC_ID = "DOC001";
        public static final String EX_SI_STATUS = "D";
        public static final String EX_BOOK_SOURCE = "WEB";
        public static final String EX_BL_NO = "BL001";
        public static final String EX_BL_STATUS = "C";
        public static final String EX_LOAD_PORT = "INMAA";
        public static final String EX_DISCHARGE_PORT = "SGSIN";
        public static final String EX_POL_AT = "Mumbai";
        public static final String EX_CREATED_SI = "SI001";
        public static final String EX_REQUESTED_SI = "SI002";
        public static final String EX_BK_STATUS = "C";
        public static final String EX_AGENCY_REF = "AGY001";
        public static final String EX_APP_VERSION = "1.0";
        public static final String EX_IS_SI_LOCKED = "false";
    }

    /**
     * BlSummaryDto model documentation
     */
    public static final class BlSummaryDto {
        public static final String DESC = "Bill of Lading Summary data from stored procedure";
        public static final String BOOK_NO = "Booking number";
        public static final String REFVAL = "Reference value";
        public static final String AGENCY_REF_NO = "Agency reference number";
        public static final String BL_NO = "Bill of Lading number";
        public static final String ORIGIN_PORT_ID = "Origin port ID";
        public static final String LOAD_PORT_ID = "Load port ID";
        public static final String DISCHARGE_PORT_ID = "Discharge port ID";
        public static final String FINAL_PORT_ID = "Final port ID";
        public static final String POL_AT = "POL at";
        public static final String STATUS = "BL Status: D (Draft), C (Confirmed), I (Issued), V (Cancelled)";
        public static final String IS_FCN = "Is FCN";
        public static final String DOCUMENTAL_AGENCY_ID = "Documental agency ID";
        public static final String BL_TYPE = "BL type";
        public static final String SYSTEM_BL_TYPE = "System BL type";
        public static final String DATE_CR = Common.CREATED_DATE + " " + Common.DATETIME_TZ_FMT;
        public static final String DATE_UP = Common.UPDATED_DATE + " " + Common.DATETIME_TZ_FMT;
        public static final String SHIPMENT_STATUS = "Shipment status";
        public static final String CONFIRMED_DATE = "Confirmed date";
        public static final String ECOM_PRNT_STATUS = "Ecom print status: 0 (Disable), 1 (Enable)";
        public static final String ORIGINALS = "Number of originals";
        public static final String COPIES = "Number of copies";
        public static final String AGENCY_ISSUE_ID = "Agency issue ID";
        public static final String RECEIPT_SHIP_DATE = "Receipt ship date";
        public static final String FCN_VER_NO = "FCN version";
        public static final String FCN_NO = "FCN number";
        public static final String ECOM_SI_NO = "Ecom SI number";
        public static final String POLICY_NO = "Policy number";
        
        public static final String EX_BOOK_NO = "BKG001";
        public static final String EX_REFVAL = "REF001";
        public static final String EX_AGENCY_REF = "AGY001";
        public static final String EX_BL_NO = "BL20240115001";
        public static final String EX_ORIGIN_PORT = "INMAA";
        public static final String EX_LOAD_PORT = "INMAA";
        public static final String EX_DISCHARGE_PORT = "SGSIN";
        public static final String EX_FINAL_PORT = "SGSIN";
        public static final String EX_POL_AT = "Mumbai";
        public static final String EX_STATUS = "C";
        public static final String EX_IS_FCN = "true";
        public static final String EX_DOC_AGENCY_ID = "DOCA001";
        public static final String EX_BL_TYPE = "AP";
        public static final String EX_SYSTEM_BL_TYPE = "SYSTEM_AP";
        public static final String EX_SHIPMENT_STATUS = "SHIPPED";
        public static final String EX_ECOM_PRNT_STATUS = "1";
        public static final String EX_ORIGINALS = "3";
        public static final String EX_COPIES = "5";
        public static final String EX_AGENCY_ISSUE_ID = "ISSUE001";
        public static final String EX_FCN_VER_NO = "V1";
        public static final String EX_FCN_NO = "FCN001";
        public static final String EX_ECOM_SI_NO = "ECSI001";
        public static final String EX_POLICY_NO = "POL001";
    }

    /**
     * ApiResponse model documentation (for SIBL service)
     */
    public static final class ApiResponse {
        public static final String DESC = "Generic API response wrapper";
        public static final String TYPE = "Response type identifier";
        public static final String DATA = "Response data array";
        public static final String TIMESTAMP = Common.TIMESTAMP + ". " + Common.DATETIME_FMT;
        public static final String TOTAL_RECORDS = "Total number of records";
        public static final String SUCCESS = "Indicates if operation was successful";
        
        public static final String EX_TYPE = "summaryData";
        public static final String EX_TOTAL_RECORDS = "10";
        public static final String EX_SUCCESS = "true";
    }
}
