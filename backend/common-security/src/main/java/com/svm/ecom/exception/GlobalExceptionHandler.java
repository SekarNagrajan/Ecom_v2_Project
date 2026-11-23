package com.svm.ecom.exception;

import com.svm.ecom.dto.ErrorMessage;
import com.svm.ecom.dto.ErrorResponse;
import com.svm.ecom.dto.NestedErrorMessage;
import com.svm.ecom.error.ErrorCodes;
import com.svm.ecom.error.ErrorReason;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Field-specific error code mapping: "objectName.fieldName" -> errorCode
    private static final Map<String, Integer> FIELD_ERROR_CODES = new HashMap<>();
    
    static {
        // BookingRequestDTO validations (4101-4112)
        FIELD_ERROR_CODES.put("bookingRequestDTO.originPortId", ErrorCodes.BOOKING_ORIGIN_PORT_REQUIRED);
        FIELD_ERROR_CODES.put("bookingRequestDTO.finalPortId", ErrorCodes.BOOKING_FINAL_PORT_REQUIRED);
        FIELD_ERROR_CODES.put("bookingRequestDTO.loadPortId", ErrorCodes.BOOKING_LOAD_PORT_REQUIRED);
        FIELD_ERROR_CODES.put("bookingRequestDTO.dischargePortId", ErrorCodes.BOOKING_DISCHARGE_PORT_REQUIRED);
        FIELD_ERROR_CODES.put("bookingRequestDTO.cargoReadyDate", ErrorCodes.BOOKING_CARGO_READY_DATE_REQUIRED);
        FIELD_ERROR_CODES.put("bookingRequestDTO.routeId", ErrorCodes.BOOKING_ROUTE_ID_REQUIRED);
        FIELD_ERROR_CODES.put("bookingRequestDTO.customers", ErrorCodes.BOOKING_CUSTOMERS_REQUIRED);
        FIELD_ERROR_CODES.put("bookingRequestDTO.containers", ErrorCodes.BOOKING_CONTAINERS_REQUIRED);
        FIELD_ERROR_CODES.put("bookingRequestDTO.commodities", ErrorCodes.BOOKING_COMMODITIES_REQUIRED);
        FIELD_ERROR_CODES.put("bookingRequestDTO.routes", ErrorCodes.BOOKING_ROUTES_REQUIRED);
        
        // RouteDTO validations (4113-4121)
        FIELD_ERROR_CODES.put("routeDTO.legNo", ErrorCodes.ROUTE_LEG_NO_REQUIRED);
        FIELD_ERROR_CODES.put("routeDTO.vesselCode", ErrorCodes.ROUTE_VESSEL_CODE_REQUIRED);
        FIELD_ERROR_CODES.put("routeDTO.voyageCode", ErrorCodes.ROUTE_VOYAGE_CODE_REQUIRED);
        FIELD_ERROR_CODES.put("routeDTO.loadPortId", ErrorCodes.ROUTE_LOAD_PORT_REQUIRED);
        FIELD_ERROR_CODES.put("routeDTO.dischargePortId", ErrorCodes.ROUTE_DISCHARGE_PORT_REQUIRED);
        FIELD_ERROR_CODES.put("routeDTO.etdDate", ErrorCodes.ROUTE_ETD_DATE_REQUIRED);
        FIELD_ERROR_CODES.put("routeDTO.etaDate", ErrorCodes.ROUTE_ETA_DATE_REQUIRED);
        // Nested routes in booking
        FIELD_ERROR_CODES.put("routes.legNo", ErrorCodes.ROUTE_LEG_NO_REQUIRED);
        FIELD_ERROR_CODES.put("routes.vesselCode", ErrorCodes.ROUTE_VESSEL_CODE_REQUIRED);
        FIELD_ERROR_CODES.put("routes.voyageCode", ErrorCodes.ROUTE_VOYAGE_CODE_REQUIRED);
        FIELD_ERROR_CODES.put("routes.loadPortId", ErrorCodes.ROUTE_LOAD_PORT_REQUIRED);
        FIELD_ERROR_CODES.put("routes.dischargePortId", ErrorCodes.ROUTE_DISCHARGE_PORT_REQUIRED);
        FIELD_ERROR_CODES.put("routes.etdDate", ErrorCodes.ROUTE_ETD_DATE_REQUIRED);
        FIELD_ERROR_CODES.put("routes.etaDate", ErrorCodes.ROUTE_ETA_DATE_REQUIRED);
        
        // CustomerDTO validations (4201-4214)
        FIELD_ERROR_CODES.put("customerDTO.customerType", ErrorCodes.CUSTOMER_TYPE_REQUIRED);
        FIELD_ERROR_CODES.put("customerDTO.customerCode", ErrorCodes.CUSTOMER_CODE_INVALID_FORMAT);
        FIELD_ERROR_CODES.put("customerDTO.countryId", ErrorCodes.CUSTOMER_COUNTRY_REQUIRED);
        FIELD_ERROR_CODES.put("customerDTO.customerName", ErrorCodes.CUSTOMER_NAME_REQUIRED);
        FIELD_ERROR_CODES.put("customerDTO.address", ErrorCodes.CUSTOMER_ADDRESS_REQUIRED);
        FIELD_ERROR_CODES.put("customerDTO.city", ErrorCodes.CUSTOMER_CITY_REQUIRED);
        FIELD_ERROR_CODES.put("customerDTO.zipCode", ErrorCodes.CUSTOMER_ZIPCODE_INVALID_FORMAT);
        FIELD_ERROR_CODES.put("customerDTO.telephone", ErrorCodes.CUSTOMER_TELEPHONE_INVALID_FORMAT);
        FIELD_ERROR_CODES.put("customerDTO.email", ErrorCodes.CUSTOMER_EMAIL_INVALID_FORMAT);
        // Nested customers in booking
        FIELD_ERROR_CODES.put("customers.customerType", ErrorCodes.CUSTOMER_TYPE_REQUIRED);
        FIELD_ERROR_CODES.put("customers.customerCode", ErrorCodes.CUSTOMER_CODE_INVALID_FORMAT);
        FIELD_ERROR_CODES.put("customers.countryId", ErrorCodes.CUSTOMER_COUNTRY_REQUIRED);
        FIELD_ERROR_CODES.put("customers.customerName", ErrorCodes.CUSTOMER_NAME_REQUIRED);
        FIELD_ERROR_CODES.put("customers.address", ErrorCodes.CUSTOMER_ADDRESS_REQUIRED);
        FIELD_ERROR_CODES.put("customers.city", ErrorCodes.CUSTOMER_CITY_REQUIRED);
        FIELD_ERROR_CODES.put("customers.zipCode", ErrorCodes.CUSTOMER_ZIPCODE_INVALID_FORMAT);
        FIELD_ERROR_CODES.put("customers.telephone", ErrorCodes.CUSTOMER_TELEPHONE_INVALID_FORMAT);
        FIELD_ERROR_CODES.put("customers.email", ErrorCodes.CUSTOMER_EMAIL_INVALID_FORMAT);
        
        // ContainerDTO validations (4301-4317)
        FIELD_ERROR_CODES.put("containerDTO.cargoNo", ErrorCodes.CONTAINER_CARGO_NO_REQUIRED);
        FIELD_ERROR_CODES.put("containerDTO.equipmentQty", ErrorCodes.CONTAINER_EQUIPMENT_QTY_REQUIRED);
        FIELD_ERROR_CODES.put("containerDTO.equipmentType", ErrorCodes.CONTAINER_EQUIPMENT_TYPE_REQUIRED);
        FIELD_ERROR_CODES.put("containerDTO.equipmentSize", ErrorCodes.CONTAINER_EQUIPMENT_SIZE_REQUIRED);
        FIELD_ERROR_CODES.put("containerDTO.equipmentStatus", ErrorCodes.CONTAINER_EQUIPMENT_STATUS_REQUIRED);
        FIELD_ERROR_CODES.put("containerDTO.cargoWeight", ErrorCodes.CONTAINER_CARGO_WEIGHT_REQUIRED);
        FIELD_ERROR_CODES.put("containerDTO.tareWeight", ErrorCodes.CONTAINER_TARE_WEIGHT_NEGATIVE);
        FIELD_ERROR_CODES.put("containerDTO.grossWeight", ErrorCodes.CONTAINER_GROSS_WEIGHT_NEGATIVE);
        FIELD_ERROR_CODES.put("containerDTO.weightUnit", ErrorCodes.CONTAINER_WEIGHT_UNIT_REQUIRED);
        FIELD_ERROR_CODES.put("containerDTO.nor", ErrorCodes.CONTAINER_NOR_REQUIRED);
        FIELD_ERROR_CODES.put("containerDTO.containerNo", ErrorCodes.CONTAINER_NUMBER_INVALID_FORMAT);
        // Nested containers in booking
        FIELD_ERROR_CODES.put("containers.cargoNo", ErrorCodes.CONTAINER_CARGO_NO_REQUIRED);
        FIELD_ERROR_CODES.put("containers.equipmentQty", ErrorCodes.CONTAINER_EQUIPMENT_QTY_REQUIRED);
        FIELD_ERROR_CODES.put("containers.equipmentType", ErrorCodes.CONTAINER_EQUIPMENT_TYPE_REQUIRED);
        FIELD_ERROR_CODES.put("containers.equipmentSize", ErrorCodes.CONTAINER_EQUIPMENT_SIZE_REQUIRED);
        FIELD_ERROR_CODES.put("containers.equipmentStatus", ErrorCodes.CONTAINER_EQUIPMENT_STATUS_REQUIRED);
        FIELD_ERROR_CODES.put("containers.cargoWeight", ErrorCodes.CONTAINER_CARGO_WEIGHT_REQUIRED);
        FIELD_ERROR_CODES.put("containers.tareWeight", ErrorCodes.CONTAINER_TARE_WEIGHT_NEGATIVE);
        FIELD_ERROR_CODES.put("containers.grossWeight", ErrorCodes.CONTAINER_GROSS_WEIGHT_NEGATIVE);
        FIELD_ERROR_CODES.put("containers.weightUnit", ErrorCodes.CONTAINER_WEIGHT_UNIT_REQUIRED);
        FIELD_ERROR_CODES.put("containers.nor", ErrorCodes.CONTAINER_NOR_REQUIRED);
        FIELD_ERROR_CODES.put("containers.containerNo", ErrorCodes.CONTAINER_NUMBER_INVALID_FORMAT);
        
        // CommodityDTO validations (4318-4322)
        FIELD_ERROR_CODES.put("commodityDTO.cargoNo", ErrorCodes.COMMODITY_CARGO_NO_REQUIRED);
        FIELD_ERROR_CODES.put("commodityDTO.containerNo", ErrorCodes.COMMODITY_CONTAINER_NO_REQUIRED);
        FIELD_ERROR_CODES.put("commodityDTO.itemNo", ErrorCodes.COMMODITY_ITEM_NO_REQUIRED);
        FIELD_ERROR_CODES.put("commodityDTO.commodityGroup", ErrorCodes.COMMODITY_GROUP_REQUIRED);
        FIELD_ERROR_CODES.put("commodityDTO.packageQty", ErrorCodes.COMMODITY_PACKAGE_QTY_REQUIRED);
        // Nested commodities in booking
        FIELD_ERROR_CODES.put("commodities.cargoNo", ErrorCodes.COMMODITY_CARGO_NO_REQUIRED);
        FIELD_ERROR_CODES.put("commodities.containerNo", ErrorCodes.COMMODITY_CONTAINER_NO_REQUIRED);
        FIELD_ERROR_CODES.put("commodities.itemNo", ErrorCodes.COMMODITY_ITEM_NO_REQUIRED);
        FIELD_ERROR_CODES.put("commodities.commodityGroup", ErrorCodes.COMMODITY_GROUP_REQUIRED);
        FIELD_ERROR_CODES.put("commodities.packageQty", ErrorCodes.COMMODITY_PACKAGE_QTY_REQUIRED);
        
        // ReeferSettingDTO validations (4326)
        FIELD_ERROR_CODES.put("reeferSettingDTO.setTemp", ErrorCodes.REEFER_SET_TEMP_REQUIRED);
        // Nested reefer in containers
        FIELD_ERROR_CODES.put("reefer.setTemp", ErrorCodes.REEFER_SET_TEMP_REQUIRED);
        FIELD_ERROR_CODES.put("containers.reefer.setTemp", ErrorCodes.REEFER_SET_TEMP_REQUIRED);
        
        // DangerousGoodsDTO validations (4327-4331)
        FIELD_ERROR_CODES.put("dangerousGoodsDTO.cargoNo", ErrorCodes.DG_CARGO_NO_REQUIRED);
        FIELD_ERROR_CODES.put("dangerousGoodsDTO.containerNo", ErrorCodes.DG_CONTAINER_NO_REQUIRED);
        FIELD_ERROR_CODES.put("dangerousGoodsDTO.itemNo", ErrorCodes.DG_ITEM_NO_REQUIRED);
        FIELD_ERROR_CODES.put("dangerousGoodsDTO.unNo", ErrorCodes.DG_UN_NUMBER_REQUIRED);
        FIELD_ERROR_CODES.put("dangerousGoodsDTO.classCode", ErrorCodes.DG_CLASS_CODE_REQUIRED);
        // Nested dangerous goods in commodities
        FIELD_ERROR_CODES.put("dangerousGoods.cargoNo", ErrorCodes.DG_CARGO_NO_REQUIRED);
        FIELD_ERROR_CODES.put("dangerousGoods.containerNo", ErrorCodes.DG_CONTAINER_NO_REQUIRED);
        FIELD_ERROR_CODES.put("dangerousGoods.itemNo", ErrorCodes.DG_ITEM_NO_REQUIRED);
        FIELD_ERROR_CODES.put("dangerousGoods.unNo", ErrorCodes.DG_UN_NUMBER_REQUIRED);
        FIELD_ERROR_CODES.put("dangerousGoods.classCode", ErrorCodes.DG_CLASS_CODE_REQUIRED);
        FIELD_ERROR_CODES.put("commodities.dangerousGoods.cargoNo", ErrorCodes.DG_CARGO_NO_REQUIRED);
        FIELD_ERROR_CODES.put("commodities.dangerousGoods.containerNo", ErrorCodes.DG_CONTAINER_NO_REQUIRED);
        FIELD_ERROR_CODES.put("commodities.dangerousGoods.itemNo", ErrorCodes.DG_ITEM_NO_REQUIRED);
        FIELD_ERROR_CODES.put("commodities.dangerousGoods.unNo", ErrorCodes.DG_UN_NUMBER_REQUIRED);
        FIELD_ERROR_CODES.put("commodities.dangerousGoods.classCode", ErrorCodes.DG_CLASS_CODE_REQUIRED);
        FIELD_ERROR_CODES.put("dangerousGoods.unNo", ErrorCodes.DG_UN_NUMBER_REQUIRED);
        FIELD_ERROR_CODES.put("dangerousGoods.classCode", ErrorCodes.DG_CLASS_CODE_REQUIRED);
        FIELD_ERROR_CODES.put("dangerousGoodsInfo.unNo", ErrorCodes.DG_UN_NUMBER_REQUIRED);
        FIELD_ERROR_CODES.put("dangerousGoodsInfo.classCode", ErrorCodes.DG_CLASS_CODE_REQUIRED);
        FIELD_ERROR_CODES.put("commodities.dangerousGoodsInfo.unNo", ErrorCodes.DG_UN_NUMBER_REQUIRED);
        FIELD_ERROR_CODES.put("commodities.dangerousGoodsInfo.classCode", ErrorCodes.DG_CLASS_CODE_REQUIRED);
        FIELD_ERROR_CODES.put("commodities.dangerousGoods.unNo", ErrorCodes.DG_UN_NUMBER_REQUIRED);
        FIELD_ERROR_CODES.put("commodities.dangerousGoods.classCode", ErrorCodes.DG_CLASS_CODE_REQUIRED);

        // SIBLRequestDTO validations (5101-5110) - SI create/update body
        FIELD_ERROR_CODES.put("body.bookingNumber", ErrorCodes.SI_BOOKING_NUMBER_REQUIRED);
        FIELD_ERROR_CODES.put("body.vesselCode", ErrorCodes.SI_VESSEL_REQUIRED);
        FIELD_ERROR_CODES.put("body.voyageCode", ErrorCodes.SI_VOYAGE_REQUIRED);
        FIELD_ERROR_CODES.put("body.loadBound", ErrorCodes.SI_LOAD_BOUND_REQUIRED);
        FIELD_ERROR_CODES.put("body.loadPortId", ErrorCodes.SI_LOAD_PORT_REQUIRED);
        FIELD_ERROR_CODES.put("body.dischargePortId", ErrorCodes.SI_DISCHARGE_PORT_REQUIRED);
        FIELD_ERROR_CODES.put("body.status", ErrorCodes.SI_STATUS_INVALID);
        FIELD_ERROR_CODES.put("body.customers", ErrorCodes.SI_CUSTOMERS_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.bookingNumber", ErrorCodes.SI_BOOKING_NUMBER_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.vesselCode", ErrorCodes.SI_VESSEL_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.voyageCode", ErrorCodes.SI_VOYAGE_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.loadBound", ErrorCodes.SI_LOAD_BOUND_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.loadPortId", ErrorCodes.SI_LOAD_PORT_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.dischargePortId", ErrorCodes.SI_DISCHARGE_PORT_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.status", ErrorCodes.SI_STATUS_INVALID);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.customers", ErrorCodes.SI_CUSTOMERS_REQUIRED);
        // SIBLCustomerDTO nested (52xx)
        FIELD_ERROR_CODES.put("body.customers.customerType", ErrorCodes.SI_CUSTOMER_TYPE_REQUIRED);
        FIELD_ERROR_CODES.put("body.customers.customerCode", ErrorCodes.SI_CUSTOMER_CODE_INVALID);
        FIELD_ERROR_CODES.put("body.customers.countryId", ErrorCodes.SI_COUNTRY_CODE_INVALID);
        FIELD_ERROR_CODES.put("body.customers.telephone", ErrorCodes.SI_CUSTOMER_TELEPHONE_INVALID);
        FIELD_ERROR_CODES.put("body.customers.email", ErrorCodes.SI_CUSTOMER_EMAIL_INVALID);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.customers.customerType", ErrorCodes.SI_CUSTOMER_TYPE_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.customers.customerCode", ErrorCodes.SI_CUSTOMER_CODE_INVALID);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.customers.countryId", ErrorCodes.SI_COUNTRY_CODE_INVALID);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.customers.telephone", ErrorCodes.SI_CUSTOMER_TELEPHONE_INVALID);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.customers.email", ErrorCodes.SI_CUSTOMER_EMAIL_INVALID);
        // SIBLContainerDTO nested (53xx)
        FIELD_ERROR_CODES.put("body.containers.cargoNo", ErrorCodes.SI_CONTAINER_CARGO_NO_REQUIRED);
        FIELD_ERROR_CODES.put("body.containers.equipmentSize", ErrorCodes.SI_CONTAINER_EQUIPMENT_SIZE_REQUIRED);
        FIELD_ERROR_CODES.put("body.containers.cargoWeight", ErrorCodes.SI_CONTAINER_CARGO_WEIGHT_REQUIRED);
        FIELD_ERROR_CODES.put("body.containers.containerNo", ErrorCodes.SI_CONTAINER_NO_REQUIRED);
        FIELD_ERROR_CODES.put("body.containers.tareWeight", ErrorCodes.SI_CONTAINER_TARE_WEIGHT_REQUIRED);
        FIELD_ERROR_CODES.put("body.containers.equipmentStatus", ErrorCodes.SI_CONTAINER_EQUIPMENT_STATUS_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.containers.cargoNo", ErrorCodes.SI_CONTAINER_CARGO_NO_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.containers.equipmentSize", ErrorCodes.SI_CONTAINER_EQUIPMENT_SIZE_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.containers.cargoWeight", ErrorCodes.SI_CONTAINER_CARGO_WEIGHT_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.containers.containerNo", ErrorCodes.SI_CONTAINER_NO_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.containers.tareWeight", ErrorCodes.SI_CONTAINER_TARE_WEIGHT_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.containers.equipmentStatus", ErrorCodes.SI_CONTAINER_EQUIPMENT_STATUS_REQUIRED);
        // SIBLCommodityDTO: itemWeight and itemWeightUnit required (same as container cargoWeight/weightUnit)
        FIELD_ERROR_CODES.put("commodities.itemWeight", ErrorCodes.SI_COMMODITY_ITEM_WEIGHT_REQUIRED);
        FIELD_ERROR_CODES.put("commodities.itemWeightUnit", ErrorCodes.SI_COMMODITY_ITEM_WEIGHT_UNIT_REQUIRED);
        FIELD_ERROR_CODES.put("body.commodities.itemWeight", ErrorCodes.SI_COMMODITY_ITEM_WEIGHT_REQUIRED);
        FIELD_ERROR_CODES.put("body.commodities.itemWeightUnit", ErrorCodes.SI_COMMODITY_ITEM_WEIGHT_UNIT_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.commodities.itemWeight", ErrorCodes.SI_COMMODITY_ITEM_WEIGHT_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.commodities.itemWeightUnit", ErrorCodes.SI_COMMODITY_ITEM_WEIGHT_UNIT_REQUIRED);
        FIELD_ERROR_CODES.put("body.commodities.itemNo", ErrorCodes.SI_COMMODITY_ITEM_NO_REQUIRED);
        FIELD_ERROR_CODES.put("sIBLRequestDTO.commodities.itemNo", ErrorCodes.SI_COMMODITY_ITEM_NO_REQUIRED);
    }

    // Global error code mapping: message pattern -> errorCode
    private static final Map<String, Integer> GLOBAL_ERROR_CODES = new HashMap<>();
    
    static {
        // BookingRequestDTO class-level validations
        GLOBAL_ERROR_CODES.put("Container type must match cargo requirements", ErrorCodes.BOOKING_CONTAINER_CARGO_MISMATCH);
        GLOBAL_ERROR_CODES.put("Duplicate container number(s) found", ErrorCodes.BOOKING_DUPLICATE_CONTAINER_NO);
        GLOBAL_ERROR_CODES.put("Duplicate cargo number(s) found", ErrorCodes.BOOKING_DUPLICATE_CARGO_NO);
        GLOBAL_ERROR_CODES.put("Template name is required when status is T", ErrorCodes.BOOKING_TEMPLATE_NAME_REQUIRED);
        GLOBAL_ERROR_CODES.put("Cargo ready date cannot be in the past", ErrorCodes.BOOKING_CARGO_READY_DATE_PAST);
        
        // RouteDTO class-level validations
        GLOBAL_ERROR_CODES.put("POL and POD cannot be the same", ErrorCodes.ROUTE_POL_EQUALS_POD);
        GLOBAL_ERROR_CODES.put("Vessel name must be selected when serviceType is F (Feeder) or M (Mainline)", ErrorCodes.ROUTE_VESSEL_CODE_REQUIRED);
        GLOBAL_ERROR_CODES.put("Voyage number is required when serviceType is F (Feeder) or M (Mainline)", ErrorCodes.ROUTE_VOYAGE_CODE_REQUIRED);
        
        // CustomerDTO class-level validations (BP/AP rules)
        GLOBAL_ERROR_CODES.put("customerName.Field must be provided", ErrorCodes.CUSTOMER_BP_AP_NAME_REQUIRED);
        GLOBAL_ERROR_CODES.put("address.Field must be provided", ErrorCodes.CUSTOMER_BP_AP_ADDRESS_REQUIRED);
        GLOBAL_ERROR_CODES.put("city.Field must be provided", ErrorCodes.CUSTOMER_BP_AP_CITY_REQUIRED);
        GLOBAL_ERROR_CODES.put("countryId.Field must be provided", ErrorCodes.CUSTOMER_BP_AP_COUNTRY_REQUIRED);
        
        // ContainerDTO class-level validations
        GLOBAL_ERROR_CODES.put("Reefer settings are required when equipmentType is R", ErrorCodes.CONTAINER_REEFER_SETTINGS_REQUIRED);
        GLOBAL_ERROR_CODES.put("dgFlag cannot be Y", ErrorCodes.CONTAINER_DG_FLAG_INVALID);
        GLOBAL_ERROR_CODES.put("Weight cannot exceed container capacity", ErrorCodes.CONTAINER_WEIGHT_EXCEEDS_CAPACITY);
        GLOBAL_ERROR_CODES.put("Out-of-Gauge cargo needs dimensions", ErrorCodes.CONTAINER_OOG_DIMENSIONS_REQUIRED);
        GLOBAL_ERROR_CODES.put("Gross weight must equal cargo weight plus tare weight", ErrorCodes.CONTAINER_GROSS_WEIGHT_MISMATCH);
        
        // Port validation (invalid port codes - doesn't exist in master data)
        GLOBAL_ERROR_CODES.put("Invalid origin port code", ErrorCodes.BOOKING_ORIGIN_PORT_INVALID);
        GLOBAL_ERROR_CODES.put("Invalid final port code", ErrorCodes.BOOKING_FINAL_PORT_INVALID);
        GLOBAL_ERROR_CODES.put("Invalid load port code", ErrorCodes.BOOKING_LOAD_PORT_INVALID);
        GLOBAL_ERROR_CODES.put("Invalid discharge port code", ErrorCodes.BOOKING_DISCHARGE_PORT_INVALID);
        
        // Route validation (invalid route ID - doesn't exist or not active in routedetail)
        GLOBAL_ERROR_CODES.put("Invalid route ID", ErrorCodes.BOOKING_ROUTE_ID_INVALID);
        
        // Route master data validation (doesn't exist in master data)
        GLOBAL_ERROR_CODES.put("Invalid route load port code", ErrorCodes.ROUTE_LOAD_PORT_INVALID);
        GLOBAL_ERROR_CODES.put("Invalid route discharge port code", ErrorCodes.ROUTE_DISCHARGE_PORT_INVALID);
        GLOBAL_ERROR_CODES.put("Invalid route service code", ErrorCodes.ROUTE_SERVICE_CODE_INVALID);
        GLOBAL_ERROR_CODES.put("Invalid route vessel code", ErrorCodes.ROUTE_VESSEL_CODE_INVALID);
        GLOBAL_ERROR_CODES.put("Invalid route load terminal", ErrorCodes.ROUTE_LOAD_TERMINAL_INVALID);
        GLOBAL_ERROR_CODES.put("Invalid route discharge terminal", ErrorCodes.ROUTE_DISCHARGE_TERMINAL_INVALID);
        
        // Customer master data validation (doesn't exist in master data)
        GLOBAL_ERROR_CODES.put("Invalid customer code", ErrorCodes.CUSTOMER_CODE_NOT_FOUND);
        GLOBAL_ERROR_CODES.put("Invalid country code", ErrorCodes.CUSTOMER_COUNTRY_NOT_FOUND);
        
        // Container/Commodity master data validation (doesn't exist in master data)
        GLOBAL_ERROR_CODES.put("Invalid equipment size", ErrorCodes.CONTAINER_EQUIPMENT_SIZE_NOT_FOUND);
        GLOBAL_ERROR_CODES.put("Invalid commodity group", ErrorCodes.COMMODITY_GROUP_NOT_FOUND);
        GLOBAL_ERROR_CODES.put("Invalid HS code", ErrorCodes.COMMODITY_HS_CODE_NOT_FOUND);
        GLOBAL_ERROR_CODES.put("Invalid package type", ErrorCodes.COMMODITY_PACKAGE_TYPE_NOT_FOUND);
        
        // Dangerous goods master data validation
        GLOBAL_ERROR_CODES.put("Invalid UN number", ErrorCodes.DG_UN_NUMBER_INVALID);
        GLOBAL_ERROR_CODES.put("Invalid class code", ErrorCodes.DG_CLASS_CODE_INVALID);
        GLOBAL_ERROR_CODES.put("Class code mismatch", ErrorCodes.DG_CLASS_CODE_MISMATCH);

        // SIBL Shipping Instruction — Bean Validation messages (nested path mapping may not apply)
        GLOBAL_ERROR_CODES.put("containerNo is required", ErrorCodes.SI_CONTAINER_NO_REQUIRED);
        GLOBAL_ERROR_CODES.put("tareWeight is required", ErrorCodes.SI_CONTAINER_TARE_WEIGHT_REQUIRED);
        GLOBAL_ERROR_CODES.put("equipmentStatus is required", ErrorCodes.SI_CONTAINER_EQUIPMENT_STATUS_REQUIRED);
        GLOBAL_ERROR_CODES.put("itemNo is required", ErrorCodes.SI_COMMODITY_ITEM_NO_REQUIRED);
    }

    /** True if request is for V2 API (error response must use client standard ErrorMessage). */
    private static boolean isV2Request(HttpServletRequest req) {
        String uri = req != null ? req.getRequestURI() : null;
        return uri != null && (uri.startsWith("/api/v2/") || uri.contains("/api/v2/"));
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status) {
        ErrorResponse body = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(body);
    }

    /** Build error response in client standard format (ErrorMessage) for V2 APIs. */
    private ResponseEntity<ErrorMessage> buildV2(HttpStatus status, String detail, String title, String code,
                                                 List<NestedErrorMessage> nested, HttpServletRequest req) {
        ErrorMessage body = new ErrorMessage();
        body.setDetail(detail != null ? detail : status.getReasonPhrase());
        body.setTitle(title != null ? title : status.getReasonPhrase());
        body.setStatus(status.value());
        body.setCode(code);
        if (req != null && req.getRequestURI() != null) {
            body.setInstance(req.getRequestURI());
        }
        if (nested != null && !nested.isEmpty()) {
            body.setErrors(nested);
        }
        return ResponseEntity.status(status).body(body);
    }
    
    /**
     * Get field-specific error code based on object name and field name
     */
    private int getFieldErrorCode(FieldError fe) {
        String objectName = fe.getObjectName();
        String fieldName = fe.getField();
        
        // Try exact match with object name
        String key = objectName + "." + fieldName;
        if (FIELD_ERROR_CODES.containsKey(key)) {
            return FIELD_ERROR_CODES.get(key);
        }

        // Nested lists: objectName + normalized path (e.g. body + containers[0].containerNo -> body.containers.containerNo)
        String normalizedField = normalizeFieldPath(fieldName);
        String nestedKey = objectName + "." + normalizedField;
        if (FIELD_ERROR_CODES.containsKey(nestedKey)) {
            return FIELD_ERROR_CODES.get(nestedKey);
        }

        // Try with just field name (for nested fields like "routes[0].vesselCode")
        if (FIELD_ERROR_CODES.containsKey(normalizedField)) {
            return FIELD_ERROR_CODES.get(normalizedField);
        }
        
        // Check message content against global error codes (for class-level validations)
        String message = fe.getDefaultMessage();
        if (message != null) {
            for (Map.Entry<String, Integer> entry : GLOBAL_ERROR_CODES.entrySet()) {
                if (message.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        
        // Check if it's a pattern/format error vs required error
        if (message != null && (message.contains("pattern") || message.contains("format") || 
            message.contains("must be") || message.contains("invalid"))) {
            return ErrorCodes.VALIDATION_INVALID_FORMAT;
        }
        
        // Default to missing field error
        return ErrorCodes.VALIDATION_MISSING_FIELD;
    }
    
    /**
     * Normalize field path by removing array indices: "routes[0].vesselCode" -> "routes.vesselCode"
     */
    private String normalizeFieldPath(String fieldPath) {
        return fieldPath.replaceAll("\\[\\d+\\]", "");
    }
    
    /**
     * Format value for error response. Returns null for complex objects (DTOs)
     * to avoid showing internal class references like "com.example.DTO@12345".
     * When {@code fieldPath} is present, resolves the leaf property (e.g. {@code containerNo}
     * from {@code commodities[2].containerNo}) so class-level validators report the invalid field value.
     */
    private String formatValue(Object value, String fieldPath) {
        if (value == null) {
            return null;
        }
        // For simple types, return the string representation
        if (value instanceof String || value instanceof Number ||
            value instanceof Boolean || value instanceof Enum ||
            value instanceof java.time.temporal.Temporal) {
            return String.valueOf(value);
        }
        if (fieldPath != null && !fieldPath.isEmpty()) {
            String fromPath = extractValueAtFieldPath(value, fieldPath);
            if (fromPath != null) {
                return fromPath;
            }
            String leafField = extractLeafFieldName(fieldPath);
            if (leafField != null) {
                String leafValue = readPropertyAsString(value, leafField);
                if (leafValue != null) {
                    return leafValue;
                }
            }
        }
        // For complex objects (DTOs), try to extract a meaningful identifier
        return extractDtoIdentifier(value);
    }
    
    private String extractLeafFieldName(String fieldPath) {
        if (fieldPath == null || fieldPath.isEmpty()) {
            return null;
        }
        String normalized = normalizeValidationFieldPath(fieldPath);
        int dot = normalized.lastIndexOf('.');
        String last = dot >= 0 ? normalized.substring(dot + 1) : normalized;
        java.util.regex.Matcher matcher =
                java.util.regex.Pattern.compile("^(\\w+)(?:\\[\\d+\\])?$").matcher(last);
        return matcher.matches() ? matcher.group(1) : null;
    }

    private String normalizeValidationFieldPath(String fieldPath) {
        if (fieldPath == null) {
            return null;
        }
        int containersIdx = fieldPath.indexOf("containers");
        if (containersIdx >= 0) {
            return fieldPath.substring(containersIdx);
        }
        int commoditiesIdx = fieldPath.indexOf("commodities");
        if (commoditiesIdx >= 0) {
            return fieldPath.substring(commoditiesIdx);
        }
        return fieldPath;
    }

    private String extractValueAtFieldPath(Object root, String fieldPath) {
        if (root == null || fieldPath == null || fieldPath.isEmpty()) {
            return null;
        }
        try {
            Object current = root;
            String normalized = normalizeValidationFieldPath(fieldPath);
            for (String segment : normalized.split("\\.")) {
                if (current == null) {
                    return null;
                }
                java.util.regex.Matcher matcher =
                        java.util.regex.Pattern.compile("^(\\w+)(?:\\[(\\d+)\\])?$").matcher(segment);
                if (!matcher.matches()) {
                    return null;
                }
                String propertyName = matcher.group(1);
                String indexValue = matcher.group(2);
                current = invokePropertyGetter(current, propertyName);
                if (indexValue != null) {
                    if (!(current instanceof java.util.List)) {
                        return null;
                    }
                    java.util.List<?> list = (java.util.List<?>) current;
                    int index = Integer.parseInt(indexValue);
                    if (index < 0 || index >= list.size()) {
                        return null;
                    }
                    current = list.get(index);
                }
            }
            return readSimpleValueAsString(current);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Object invokePropertyGetter(Object target, String propertyName) throws Exception {
        String getterName = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        return target.getClass().getMethod(getterName).invoke(target);
    }

    private String readPropertyAsString(Object target, String propertyName) {
        if (target == null || propertyName == null || propertyName.isEmpty()) {
            return null;
        }
        try {
            return readSimpleValueAsString(invokePropertyGetter(target, propertyName));
        } catch (Exception ignored) {
            return null;
        }
    }

    private String readSimpleValueAsString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String || value instanceof Number ||
            value instanceof Boolean || value instanceof Enum ||
            value instanceof java.time.temporal.Temporal) {
            return String.valueOf(value);
        }
        return null;
    }

    /**
     * Extract a meaningful identifier from DTO objects using reflection.
     * Tries common identifier fields: containerNo, cargoNo, customerCode, etc.
     */
    private String extractDtoIdentifier(Object dto) {
        if (dto == null) {
            return null;
        }
        // List of common identifier field names to try (in order of preference)
        String[] identifierFields = {"containerNo", "cargoNo", "customerCode", "bookingNumber", "blNo", "id"};
        
        for (String fieldName : identifierFields) {
            try {
                // Try getter method (e.g., getContainerNo)
                String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                java.lang.reflect.Method getter = dto.getClass().getMethod(getterName);
                Object fieldValue = getter.invoke(dto);
                if (fieldValue != null) {
                    return String.valueOf(fieldValue);
                }
            } catch (Exception ignored) {
                // Field doesn't exist or can't be accessed, try next
            }
        }
        // No identifier found, return null instead of class reference
        return null;
    }
    
    /**
     * Get global error code based on error message
     */
    private int getGlobalErrorCode(ObjectError ge) {
        String message = ge.getDefaultMessage();
        if (message != null) {
            for (Map.Entry<String, Integer> entry : GLOBAL_ERROR_CODES.entrySet()) {
                if (message.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return ErrorCodes.VALIDATION_GENERIC;
    }

    // 400 - DTO @Valid errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest req) {
        if (isV2Request(req)) {
            List<NestedErrorMessage> nested = new ArrayList<>();
            ex.getBindingResult().getFieldErrors().forEach(fe ->
                nested.add(NestedErrorMessage.of(fe.getDefaultMessage(),
                    fe.getField() != null ? "#/" + fe.getField() : null,
                    String.valueOf(getFieldErrorCode(fe))))
            );
            ex.getBindingResult().getGlobalErrors().forEach(ge ->
                nested.add(NestedErrorMessage.of(ge.getDefaultMessage(), null, String.valueOf(getGlobalErrorCode(ge))))
            );
            String detail = nested.isEmpty() ? "Validation failed." : nested.get(0).getDetail();
            return buildV2(HttpStatus.BAD_REQUEST, detail, "Validation Failed", "VALIDATION_FAILED", nested, req);
        }
        ResponseEntity<ErrorResponse> resp = build(HttpStatus.BAD_REQUEST);
        ErrorResponse body = resp.getBody();
        if (body != null) {
            ex.getBindingResult().getFieldErrors().forEach(fe ->
                body.addError(getFieldErrorCode(fe), fe.getField(),
                    formatValue(fe.getRejectedValue(), fe.getField()),
                    ErrorReason.VALIDATION_ERROR,
                    fe.getDefaultMessage())
            );
            ex.getBindingResult().getGlobalErrors().forEach(ge ->
                body.addError(getGlobalErrorCode(ge), null, null, ErrorReason.VALIDATION_ERROR, ge.getDefaultMessage())
            );
        }
        return resp;
    }

    // 400 - binding / query param errors
    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException ex, HttpServletRequest req) {
        if (isV2Request(req)) {
            List<NestedErrorMessage> nested = new ArrayList<>();
            ex.getBindingResult().getFieldErrors().forEach(fe ->
                nested.add(NestedErrorMessage.of(fe.getDefaultMessage(),
                    fe.getField() != null ? "#/" + fe.getField() : null,
                    String.valueOf(getFieldErrorCode(fe))))
            );
            String detail = nested.isEmpty() ? "Validation failed." : nested.get(0).getDetail();
            return buildV2(HttpStatus.BAD_REQUEST, detail, "Validation Failed", "VALIDATION_FAILED", nested, req);
        }
        ResponseEntity<ErrorResponse> resp = build(HttpStatus.BAD_REQUEST);
        ErrorResponse body = resp.getBody();
        if (body != null) {
            ex.getBindingResult().getFieldErrors().forEach(fe ->
                body.addError(getFieldErrorCode(fe), fe.getField(),
                    formatValue(fe.getRejectedValue(), fe.getField()),
                    ErrorReason.VALIDATION_ERROR,
                    fe.getDefaultMessage())
            );
        }
        return resp;
    }

    // 400 - @Validated on method params
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        if (isV2Request(req)) {
            List<NestedErrorMessage> nested = new ArrayList<>();
            ex.getConstraintViolations().forEach(v -> {
                String path = v.getPropertyPath() != null ? v.getPropertyPath().toString() : null;
                nested.add(NestedErrorMessage.of(v.getMessage(), path != null ? "#/" + path : null,
                    String.valueOf(getConstraintViolationErrorCode(v))));
            });
            String detail = nested.isEmpty() ? "Validation failed." : nested.get(0).getDetail();
            return buildV2(HttpStatus.BAD_REQUEST, detail, "Validation Failed", "VALIDATION_FAILED", nested, req);
        }
        ResponseEntity<ErrorResponse> resp = build(HttpStatus.BAD_REQUEST);
        ErrorResponse body = resp.getBody();
        if (body != null) {
            ex.getConstraintViolations().forEach(v ->
                body.addError(getConstraintViolationErrorCode(v),
                    v.getPropertyPath() == null ? null : v.getPropertyPath().toString(),
                    formatValue(v.getInvalidValue(),
                        v.getPropertyPath() == null ? null : v.getPropertyPath().toString()),
                    ErrorReason.VALIDATION_ERROR,
                    v.getMessage())
            );
        }
        return resp;
    }
    
    /**
     * Get error code for constraint violations
     */
    private int getConstraintViolationErrorCode(ConstraintViolation<?> v) {
        String propertyPath = v.getPropertyPath() != null ? v.getPropertyPath().toString() : "";
        String normalizedPath = normalizeFieldPath(propertyPath);
        
        if (FIELD_ERROR_CODES.containsKey(normalizedPath)) {
            return FIELD_ERROR_CODES.get(normalizedPath);
        }
        
        // Check message content against global error codes (for class-level validations)
        String message = v.getMessage();
        if (message != null) {
            for (Map.Entry<String, Integer> entry : GLOBAL_ERROR_CODES.entrySet()) {
                if (message.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        
        return ErrorCodes.VALIDATION_MISSING_FIELD;
    }

    // 400 - parameter type mismatch (e.g., invalid date format)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        if (isV2Request(req)) {
            List<NestedErrorMessage> nested = new ArrayList<>();
            
            // Check if this is a date format error
            String paramName = ex.getName();
            String detail;
            if (paramName != null && (paramName.contains("date") || paramName.contains("Date") || paramName.contains("At_after"))) {
                detail = String.format("Invalid date format for '%s' parameter. Expected format: ISO 8601 date-time (e.g., 2026-01-01T00:00:00Z)", paramName);
            } else {
                detail = String.format("Invalid format for parameter '%s'. Expected type: %s", 
                    paramName, ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
            }
            
            NestedErrorMessage error = new NestedErrorMessage();
            error.setDetail(detail);
            error.setParameter(paramName);
            error.setCode(String.valueOf(ErrorCodes.VALIDATION_INVALID_FORMAT));
            nested.add(error);
            
            return buildV2(HttpStatus.BAD_REQUEST, detail, "Validation Failed", "VALIDATION_FAILED", nested, req);
        } else {
            ErrorResponse response = new ErrorResponse(400, "Bad Request", LocalDateTime.now());
            
            response.addError(ErrorCodes.VALIDATION_INVALID_FORMAT, ex.getName(), 
                ex.getValue() != null ? ex.getValue().toString() : null, 
                ErrorReason.VALIDATION_ERROR, 
                String.format("Invalid format for parameter '%s'", ex.getName()));
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 400 - bad body / missing params
    @ExceptionHandler({ HttpMessageNotReadableException.class, MissingServletRequestParameterException.class })
    public ResponseEntity<?> handleBadRequest(Exception ex, HttpServletRequest req) {
        if (isV2Request(req)) {
            List<NestedErrorMessage> nested = new ArrayList<>();
            nested.add(NestedErrorMessage.of(ex.getMessage() != null ? ex.getMessage() : "Bad Request", null, "BAD_REQUEST"));
            return buildV2(HttpStatus.BAD_REQUEST, ex.getMessage(), "Bad Request", "BAD_REQUEST", nested, req);
        }
        ResponseEntity<ErrorResponse> resp = build(HttpStatus.BAD_REQUEST);
        ErrorResponse body = resp.getBody();
        if (body != null) {
            body.addError(ErrorCodes.BAD_REQUEST, null, null, ErrorReason.BAD_REQUEST, ex.getMessage());
        }
        return resp;
    }

    // 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        if (isV2Request(req)) {
            List<NestedErrorMessage> nested = new ArrayList<>();
            nested.add(NestedErrorMessage.of(ex.getMessage(), null, "METHOD_NOT_ALLOWED"));
            return buildV2(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), "Method Not Allowed", "METHOD_NOT_ALLOWED", nested, req);
        }
        ResponseEntity<ErrorResponse> resp = build(HttpStatus.METHOD_NOT_ALLOWED);
        ErrorResponse body = resp.getBody();
        if (body != null) {
            body.addError(ErrorCodes.METHOD_NOT_ALLOWED, null, null, ErrorReason.METHOD_NOT_ALLOWED, ex.getMessage());
        }
        return resp;
    }

    // 415
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        if (isV2Request(req)) {
            List<NestedErrorMessage> nested = new ArrayList<>();
            nested.add(NestedErrorMessage.of(ex.getMessage(), null, "UNSUPPORTED_MEDIA_TYPE"));
            return buildV2(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage(), "Unsupported Media Type", "UNSUPPORTED_MEDIA_TYPE", nested, req);
        }
        ResponseEntity<ErrorResponse> resp = build(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        ErrorResponse body = resp.getBody();
        if (body != null) {
            body.addError(ErrorCodes.UNSUPPORTED_MEDIA_TYPE, null, null, ErrorReason.UNSUPPORTED_MEDIA_TYPE, ex.getMessage());
        }
        return resp;
    }

    // 422 - business errors
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusiness(BusinessException ex, HttpServletRequest req) {
        HttpStatus status = (ex.getHttpStatusOverride() > 0) ? HttpStatus.valueOf(ex.getHttpStatusOverride())
                                                             : HttpStatus.UNPROCESSABLE_ENTITY;
        if (isV2Request(req)) {
            List<NestedErrorMessage> nested = new ArrayList<>();
            ex.getDetails().forEach(d ->
                nested.add(NestedErrorMessage.of(d.getMessage(), d.getField() != null ? "#/" + d.getField() : null, String.valueOf(d.getErrorCode())))
            );
            String detail = nested.isEmpty() ? ex.getMessage() : nested.get(0).getDetail();
            return buildV2(status, detail, status.getReasonPhrase(), "BUSINESS_RULE_VIOLATION", nested, req);
        }
        ResponseEntity<ErrorResponse> resp = build(status);
        ErrorResponse body = resp.getBody();
        if (body != null) {
            ex.getDetails().forEach(d ->
                body.addError(d.getErrorCode(), d.getField(), d.getValue(), d.getReason(), d.getMessage())
            );
        }
        return resp;
    }

  @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbort(ClientAbortException ex, HttpServletRequest req) {
        if (logger.isDebugEnabled()) {
            logger.debug("Client aborted connection for {} {} before the response finished writing: {}",
                req.getMethod(), req.getRequestURI(), ex.getMessage());
        }
    }

    // 500 - catch-all
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex, HttpServletRequest req) {
        if (isV2Request(req)) {
            List<NestedErrorMessage> nested = new ArrayList<>();
            nested.add(NestedErrorMessage.of(ex.getMessage() != null ? ex.getMessage() : "Internal server error", null, "INTERNAL_ERROR"));
            return buildV2(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server-side error occurred.", "Internal Server Error", "INTERNAL_ERROR", nested, req);
        }
        ResponseEntity<ErrorResponse> resp = build(HttpStatus.INTERNAL_SERVER_ERROR);
        ErrorResponse body = resp.getBody();
        if (body != null) {
            body.addError(ErrorCodes.INTERNAL_ERROR, null, null, ErrorReason.INTERNAL_ERROR, ex.getMessage());
        }
        return resp;
    }
}
