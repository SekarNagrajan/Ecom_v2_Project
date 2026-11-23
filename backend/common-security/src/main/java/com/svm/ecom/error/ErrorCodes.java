package com.svm.ecom.error;

public final class ErrorCodes {
    private ErrorCodes() {}

    // 1000–1999: API/Technical
    public static final int INTERNAL_ERROR = 1000;
    public static final int UNAUTHORIZED = 1001;
    public static final int FORBIDDEN = 1002;
    public static final int SERVICE_UNAVAILABLE = 1003;
    public static final int METHOD_NOT_ALLOWED = 1004;
    public static final int UNSUPPORTED_MEDIA_TYPE = 1005;
    public static final int BAD_REQUEST = 1006;
    public static final int NOT_FOUND = 1007;
    public static final int CONFLICT = 1008;
    
    public static final int RATE_LIMITED = 1009;

    // 2000–2999: Functional/Validation (Generic)
    public static final int VALIDATION_GENERIC = 2000;
    public static final int VALIDATION_MISSING_FIELD = 2001;
    public static final int VALIDATION_INVALID_FORMAT = 2002;
    public static final int BUSINESS_RULE_VIOLATION = 2003;
    public static final int BUSINESS_RULE_CANNOT_AMEND = 2004;
    public static final int BUSINESS_RULE_POL_EQUALS_POD = 2005;

    // 2006–2010: Authentication Errors
    public static final int AUTH_MISSING_JWT = 2006;
    public static final int AUTH_INVALID_JWT = 2007;
    public static final int AUTH_CUSTOMER_CODE_MISSING = 2008;
    public static final int AUTH_LOGIN_NAME_MISSING = 2009;
    public static final int AUTH_CUSTOMER_CODE_MISMATCH = 2010;

    // 2011: Registration completion required (Quick Registration guard)
    public static final int AUTH_QUICK_REGISTRATION_INCOMPLETE = 2011;

    // 4100–4199: BookingRequestDTO Validations
    public static final int BOOKING_ORIGIN_PORT_REQUIRED = 4101;
    public static final int BOOKING_FINAL_PORT_REQUIRED = 4102;
    public static final int BOOKING_LOAD_PORT_REQUIRED = 4103;
    public static final int BOOKING_DISCHARGE_PORT_REQUIRED = 4104;
    public static final int BOOKING_CARGO_READY_DATE_REQUIRED = 4105;
    public static final int BOOKING_ROUTE_ID_REQUIRED = 4106;
    public static final int BOOKING_CUSTOMERS_REQUIRED = 4107;
    public static final int BOOKING_CONTAINERS_REQUIRED = 4108;
    public static final int BOOKING_COMMODITIES_REQUIRED = 4109;
    public static final int BOOKING_ROUTES_REQUIRED = 4110;
    public static final int BOOKING_CONTAINER_CARGO_MISMATCH = 4111;
    public static final int BOOKING_TEMPLATE_NAME_REQUIRED = 4112;
    
    // 4141-4146: Invalid Port Code / Route Validations (doesn't exist in master data)
    public static final int BOOKING_ORIGIN_PORT_INVALID = 4141;
    public static final int BOOKING_FINAL_PORT_INVALID = 4142;
    public static final int BOOKING_LOAD_PORT_INVALID = 4143;
    public static final int BOOKING_DISCHARGE_PORT_INVALID = 4144;
    public static final int BOOKING_CARGO_READY_DATE_PAST = 4145;
    public static final int BOOKING_ROUTE_ID_INVALID = 4146;
    public static final int BOOKING_DUPLICATE_CONTAINER_NO = 4147;
    public static final int BOOKING_DUPLICATE_CARGO_NO = 4148;

    // 4113–4121: RouteDTO Validations
    public static final int ROUTE_LEG_NO_REQUIRED = 4113;
    public static final int ROUTE_VESSEL_CODE_REQUIRED = 4114;
    public static final int ROUTE_VOYAGE_CODE_REQUIRED = 4115;
    public static final int ROUTE_LOAD_PORT_REQUIRED = 4116;
    public static final int ROUTE_DISCHARGE_PORT_REQUIRED = 4117;
    public static final int ROUTE_ETD_DATE_REQUIRED = 4118;
    public static final int ROUTE_ETD_DATE_FUTURE = 4119;
    public static final int ROUTE_ETA_DATE_REQUIRED = 4120;
    public static final int ROUTE_POL_EQUALS_POD = 4121;
    
    // 4122-4127: Invalid Route Master Data Validations (doesn't exist in master data)
    public static final int ROUTE_LOAD_PORT_INVALID = 4122;
    public static final int ROUTE_DISCHARGE_PORT_INVALID = 4123;
    public static final int ROUTE_SERVICE_CODE_INVALID = 4124;
    public static final int ROUTE_VESSEL_CODE_INVALID = 4125;
    public static final int ROUTE_LOAD_TERMINAL_INVALID = 4126;
    public static final int ROUTE_DISCHARGE_TERMINAL_INVALID = 4127;

    // 4201–4214: CustomerDTO Validations
    public static final int CUSTOMER_TYPE_REQUIRED = 4201;
    public static final int CUSTOMER_CODE_INVALID_FORMAT = 4202;
    public static final int CUSTOMER_COUNTRY_REQUIRED = 4203;
    public static final int CUSTOMER_COUNTRY_INVALID_FORMAT = 4204;
    public static final int CUSTOMER_NAME_REQUIRED = 4205;
    public static final int CUSTOMER_ADDRESS_REQUIRED = 4206;
    public static final int CUSTOMER_CITY_REQUIRED = 4207;
    public static final int CUSTOMER_ZIPCODE_INVALID_FORMAT = 4208;
    public static final int CUSTOMER_TELEPHONE_INVALID_FORMAT = 4209;
    public static final int CUSTOMER_EMAIL_INVALID_FORMAT = 4210;
    public static final int CUSTOMER_BP_AP_NAME_REQUIRED = 4211;
    public static final int CUSTOMER_BP_AP_ADDRESS_REQUIRED = 4212;
    public static final int CUSTOMER_BP_AP_CITY_REQUIRED = 4213;
    public static final int CUSTOMER_BP_AP_COUNTRY_REQUIRED = 4214;
    
    // 4215-4216: Invalid Customer Master Data Validations (doesn't exist in master data)
    public static final int CUSTOMER_CODE_NOT_FOUND = 4215;
    public static final int CUSTOMER_COUNTRY_NOT_FOUND = 4216;

    // 4301–4317: ContainerDTO Validations
    public static final int CONTAINER_CARGO_NO_REQUIRED = 4301;
    public static final int CONTAINER_EQUIPMENT_QTY_REQUIRED = 4302;
    public static final int CONTAINER_EQUIPMENT_TYPE_REQUIRED = 4303;
    public static final int CONTAINER_EQUIPMENT_SIZE_REQUIRED = 4304;
    public static final int CONTAINER_EQUIPMENT_SIZE_INVALID_FORMAT = 4305;
    public static final int CONTAINER_EQUIPMENT_STATUS_REQUIRED = 4306;
    public static final int CONTAINER_CARGO_WEIGHT_REQUIRED = 4307;
    public static final int CONTAINER_TARE_WEIGHT_NEGATIVE = 4308;
    public static final int CONTAINER_GROSS_WEIGHT_NEGATIVE = 4309;
    public static final int CONTAINER_WEIGHT_UNIT_REQUIRED = 4310;
    public static final int CONTAINER_NOR_REQUIRED = 4311;
    public static final int CONTAINER_NUMBER_INVALID_FORMAT = 4312;
    public static final int CONTAINER_REEFER_SETTINGS_REQUIRED = 4313;
    public static final int CONTAINER_DG_FLAG_INVALID = 4314;
    public static final int CONTAINER_WEIGHT_EXCEEDS_CAPACITY = 4315;
    public static final int CONTAINER_OOG_DIMENSIONS_REQUIRED = 4316;
    public static final int CONTAINER_GROSS_WEIGHT_MISMATCH = 4317;

    // 4318–4322: CommodityDTO Validations
    public static final int COMMODITY_CARGO_NO_REQUIRED = 4318;
    public static final int COMMODITY_CONTAINER_NO_REQUIRED = 4319;
    public static final int COMMODITY_ITEM_NO_REQUIRED = 4320;
    public static final int COMMODITY_GROUP_REQUIRED = 4321;
    public static final int COMMODITY_PACKAGE_QTY_REQUIRED = 4322;
    
    // 4350-4353: Master Data Not Found Validations (container/commodity)
    public static final int CONTAINER_EQUIPMENT_SIZE_NOT_FOUND = 4350;
    public static final int COMMODITY_GROUP_NOT_FOUND = 4351;
    public static final int COMMODITY_HS_CODE_NOT_FOUND = 4352;
    public static final int COMMODITY_PACKAGE_TYPE_NOT_FOUND = 4353;

    // 4323–4326: ReeferSettingDTO Validations
    public static final int REEFER_CARGO_NO_REQUIRED = 4323;
    public static final int REEFER_CONTAINER_NO_REQUIRED = 4324;
    public static final int REEFER_ITEM_NO_REQUIRED = 4325;
    public static final int REEFER_SET_TEMP_REQUIRED = 4326;

    // 4327–4334: DangerousGoodsDTO Validations
    public static final int DG_CARGO_NO_REQUIRED = 4327;
    public static final int DG_CONTAINER_NO_REQUIRED = 4328;
    public static final int DG_ITEM_NO_REQUIRED = 4329;
    public static final int DG_UN_NUMBER_REQUIRED = 4330;
    public static final int DG_CLASS_CODE_REQUIRED = 4331;
    public static final int DG_UN_NUMBER_INVALID = 4332;
    public static final int DG_CLASS_CODE_MISMATCH = 4333;
    public static final int DG_CLASS_CODE_INVALID = 4334;

    // 4401–4412: Document Upload Validations
    public static final int DOC_BOOKING_NOT_FOUND = 4401;
    public static final int DOC_FOLDER_NOT_FOUND = 4402;
    public static final int DOC_MIN_FILE_COUNT = 4403;
    public static final int DOC_MAX_FILE_COUNT = 4404;
    public static final int DOC_FILE_SIZE_EXCEEDED = 4405;
    public static final int DOC_FILE_NAME_EMPTY = 4406;
    public static final int DOC_FILE_NAME_DUPLICATE = 4407;
    public static final int DOC_FILE_EXTENSION_FORBIDDEN = 4408;
    public static final int DOC_MIME_TYPE_FORBIDDEN = 4409;
    public static final int DOC_FILE_EXISTS_NO_OVERWRITE = 4410;
    public static final int DOC_SETTING_NOT_FOUND = 4411;
    public static final int DOC_BASE_PATH_EMPTY = 4412;

    // 5001: SI (Shipping Instruction) General / Reference Errors
    public static final int SI_BOOKING_NOT_FOUND = 5001;
    public static final int SI_BOOKING_NOT_CONFIRMED = 5002;       // Booking status must be C (confirmed)
    public static final int SI_ALREADY_EXISTS_FOR_BOOKING = 5003;   // Multiple SI submissions not allowed for same booking
    public static final int SI_CONTAINERS_MUST_MATCH_BOOKING = 5004; // No add/remove containers; must match booking_cargo
    public static final int SI_COMMODITY_CONTAINER_NOT_IN_BOOKING = 5005; // Commodities only for existing booking container numbers
    public static final int SI_VALUE_DIFFERS_FROM_BOOKING = 5006;   // Request must use same booking values; no structural changes

    // 5101–5116: SIBLRequestDTO Validations (aligned with booking 41xx pattern)
    public static final int SI_BOOKING_NUMBER_REQUIRED = 5101;
    public static final int SI_VESSEL_REQUIRED = 5102;
    public static final int SI_VOYAGE_CODE_REQUIRED = 5103;
    public static final int SI_LOAD_BOUND_REQUIRED = 5104;
    public static final int SI_LOAD_BOUND_INVALID = 5104;
    public static final int SI_LOAD_PORT_REQUIRED = 5105;
    public static final int SI_VOYAGE_REQUIRED = 5106;
    public static final int SI_DISCHARGE_PORT_REQUIRED = 5107;
    public static final int SI_CUSTOMER_CODE_REQUIRED = 5108;
    public static final int SI_CUSTOMERS_REQUIRED = 5109;
    public static final int SI_STATUS_INVALID = 5110;
    public static final int SI_POL_POD_SAME = 5111;
    public static final int SI_VESSEL_INVALID = 5112;
    public static final int SI_PORT_INVALID = 5113;
    public static final int SI_VOYAGE_INVALID = 5114;
    public static final int SI_VOYAGE_ROE_NOT_FOUND = 5115;
    public static final int SI_VOYAGE_MANIFEST_CLOSED = 5116;
    public static final int SI_SERVICE_CODE_INVALID = 5117;
    public static final int SI_POL_TERMINAL_INVALID = 5118;
    public static final int SI_POD_TERMINAL_INVALID = 5119;

    // 5201–5214: SIBLCustomerDTO Validations (aligned with booking CustomerDTO 42xx)
    public static final int SI_CUSTOMER_TYPE_REQUIRED = 5201;
    public static final int SI_CUSTOMER_CODE_INVALID = 5202;   // Customer must exist in customermaster (same as Booking CustomerMasterDataValidator)
    public static final int SI_COUNTRY_CODE_INVALID = 5203;    // Country must exist in country master (ISO 2-letter; same as Booking CustomerMasterDataValidator)
    public static final int SI_CUSTOMER_EMAIL_INVALID = 5210;
    public static final int SI_CUSTOMER_TYPE_AP_BP_NOT_ALLOWED = 5211;  // AP/BP are maintained in Booking; not allowed in SI customers
    public static final int SI_CUSTOMER_TELEPHONE_INVALID = 5209;
    public static final int SI_CUSTOMER_STATE_INVALID = 5208;

    // 5301–5330: SIBLContainerDTO / Reefer / OOG / DG / Commodity Validations
    public static final int SI_CONTAINER_EQUIPMENT_SIZE_REQUIRED = 5301;
    public static final int SI_CONTAINER_EQUIPMENT_SIZE_INVALID = 5304;   // Equipment size must exist in equipment master
    public static final int SI_CONTAINER_WEIGHT_EXCEEDS_CAPACITY = 5315;  // cargoWeight + tareWeight must not exceed gross capacity (same as Booking 4315)
    public static final int SI_CONTAINER_NUMBER_INVALID = 5317;           // ISO 6346 format (4 letters + 6 digits + check digit)
    public static final int SI_CONTAINER_DUPLICATE_IN_SI = 5319;          // Duplicate container number within same SI
    public static final int SI_CONTAINER_ALREADY_IN_USE = 5320;           // Container already in use in another booking
    public static final int SI_EQUIPMENT_STATUS_INVALID = 5318;           // equipmentStatus must be F (Full) or M (Empty)
    public static final int SI_CONTAINER_CARGO_NO_REQUIRED = 5302;
    public static final int SI_CONTAINER_CARGO_WEIGHT_REQUIRED = 5303;
    public static final int SI_CONTAINER_REEFER_SETTINGS_REQUIRED = 5313;
    public static final int SI_CONTAINER_OOG_DIMENSIONS_REQUIRED = 5316;
    public static final int SI_CONTAINER_DG_DETAILS_REQUIRED = 5321;
    public static final int SI_REEFER_SET_TEMP_REQUIRED = 5314;
    public static final int SI_DG_UN_NO_REQUIRED = 5327;
    public static final int SI_DG_ITEM_NO_REQUIRED = 5329;
    public static final int SI_DG_PACKAGE_QTY_REQUIRED = 5330;
    public static final int SI_COMMODITY_HS_CODE_REQUIRED = 5324;
    public static final int SI_COMMODITY_DANGEROUS_GOODS_REQUIRED = 5323;
    public static final int SI_COMMODITY_GROUP_INVALID = 5325;   // Commodity group must exist in commodity master (CommodityMasterDataValidator)
    public static final int SI_HS_CODE_INVALID = 5326;          // HS code must exist in commodity master when provided
    public static final int SI_PACKAGE_TYPE_INVALID = 5328;     // Package type must exist in package master when provided
    public static final int SI_COMMODITY_ITEM_WEIGHT_REQUIRED = 5331;   // itemWeight required when commodity present (same as container cargoWeight)
    public static final int SI_COMMODITY_ITEM_WEIGHT_UNIT_REQUIRED = 5332; // itemWeightUnit required when commodity present (same as container weightUnit)
    /** At least one commodity per SI container line (cargoNo + containerNo pairing). */
    public static final int SI_CONTAINER_COMMODITY_REQUIRED = 5333;
    /** Commodity line must map to booking cargo via (cargoNo, eqpTypeId) and match container equipmentSize + containerNo. */
    public static final int SI_COMMODITY_CONTAINER_MAPPING_INVALID = 5334;
    public static final int SI_CONTAINER_NO_REQUIRED = 5335;
    public static final int SI_CONTAINER_TARE_WEIGHT_REQUIRED = 5336;
    public static final int SI_CONTAINER_EQUIPMENT_STATUS_REQUIRED = 5337;
    public static final int SI_COMMODITY_ITEM_NO_REQUIRED = 5338;
}
