package com.solverminds.sibl.service;

import com.solverminds.sibl.dto.*;
import com.solverminds.sibl.entity.*;
import com.solverminds.sibl.repo.*;
import com.solverminds.sibl.repo.BookingDetailRepository.BookingCustDetail;
import com.solverminds.sibl.service.SIValidationService.ValidationResult;
import com.svm.ecom.common.enums.SiStatus;
import com.svm.ecom.common.enums.WeightUnit;
import com.svm.ecom.common.master.cache.CommonMasterDataCacheService;
import com.solverminds.sibl.exception.SIValidationException;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

/**
 * SI Create Service - Handles complete SI creation with validation
 * Following the same patterns as BookingCreateService
 */
@Service
@Transactional(transactionManager = "ecomTransactionManager")
public class SiCreateService {

    private static final Logger logger = LoggerFactory.getLogger(SiCreateService.class);

    @Autowired
    private EcomSiRepository ecomSiRepository;

    @Autowired
    private EcomSiCustomerRepository ecomSiCustomerRepository;

    @Autowired
    private EcomSiContainerRepository ecomSiContainerRepository;

    @Autowired
    private EcomSiCommodityRepository ecomSiCommodityRepository;

    @Autowired
    private SIValidationService validationService;

    @Autowired(required = false)
    @Qualifier(CommonMasterDataCacheService.BEAN_NAME)
    private CommonMasterDataCacheService masterDataCacheService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired(required = false)
    @Qualifier("siblEcomSiRequestMasterRepository")
    private EcomSiRequestMasterRepository ecomSiRequestMasterRepository;

    @Autowired(required = false)
    private EcomSiReeferRepository ecomSiReeferRepository;

    @Autowired(required = false)
    private EcomSiDgRepository ecomSiDgRepository;

    @Autowired(required = false)
    private EcomSiOogRepository ecomSiOogRepository;

    @Autowired(required = false)
    private EcomSiChargesRepository ecomSiChargesRepository;

    @Autowired(required = false)
    private EcomSiIcsNewRepository ecomSiIcsNewRepository;

    @Autowired(required = false)
    @Qualifier("siblBookingDetailRepository")
    private BookingDetailRepository bookingDetailRepository;

    /**
     * Generate SI number using module index table
     * This method retrieves the current index from ecomMdlIndex table,
     * increments it, updates the database, and returns the new SI number
     * @return Generated SI number in format: prefix + index
     */
    private String generateSINumber() {
        try {
            logger.info("[ECOM] SiCreateService - Starting SI number generation");
            
            if (entityManager == null) {
                logger.warn("[ECOM] SiCreateService - EntityManager not available, using fallback");
                String fallbackSINo = "SIREQ" + System.currentTimeMillis();
                logger.info("[ECOM] SiCreateService - Generated fallback SI number: " + fallbackSINo);
                return fallbackSINo;
            }
            
            // Determine module ID for SI generation
            String moduleId = "SI";
            logger.info("[ECOM] SiCreateService - Looking for module: " + moduleId);
            
            // Use native SQL to avoid entity casting issues
            // First, get the current values
            Query selectQuery = entityManager.createNativeQuery(
                "SELECT mdlPrefix, mdlIndex FROM ecomMdlIndex WHERE mdlId = ?"
            );
            selectQuery.setParameter(1, moduleId);
            
            Object[] result = (Object[]) selectQuery.getSingleResult();
            if (result == null || result.length < 2) {
                logger.error("[ECOM] SiCreateService - Module index not found for module: " + moduleId);
                throw new IllegalStateException(
                    "Module index not found for module: " + moduleId + 
                    ". Please ensure ecomMdlIndex table has entry for this module.");
            }
            
            String prefix = result[0] != null ? result[0].toString() : "";
            Integer currentIndex = result[1] != null ? Integer.parseInt(result[1].toString()) : 0;
            
            logger.info("[ECOM] SiCreateService - Current prefix: " + prefix + ", index: " + currentIndex);
            
            // Increment the index
            int newIndex = currentIndex + 1;
            
            // Update the database with new index using a transaction-safe approach
            Query updateQuery = entityManager.createNativeQuery(
                "UPDATE ecomMdlIndex SET mdlIndex = ? WHERE mdlId = ?"
            );
            updateQuery.setParameter(1, newIndex);
            updateQuery.setParameter(2, moduleId);
            int updateCount = updateQuery.executeUpdate();
            
            if (updateCount == 0) {
                logger.error("[ECOM] SiCreateService - Failed to update module index for module: " + moduleId);
                throw new IllegalStateException("Failed to update module index in database");
            }
            
            logger.info("[ECOM] SiCreateService - Successfully updated module index to: " + newIndex);
            
            // Generate and return SI number: prefix + newIndex
            String siNumber = prefix + newIndex;
            
            logger.info("[ECOM] SiCreateService - Generated SI number: " + siNumber);
            return siNumber;
             
         } catch (Exception e) {
             // Fallback: Generate SI number using timestamp if database fails
             logger.error("[ECOM] SiCreateService - Error generating SI number from database: " + e.getMessage());
             logger.error("[ECOM] SiCreateService - Using fallback timestamp-based generation");
             
             String fallbackSINo = "SIREQ" + System.currentTimeMillis();
             logger.info("[ECOM] SiCreateService - Generated fallback SI number: " + fallbackSINo);
             return fallbackSINo;
         }
     }






    /**
     * Create SI (Shipping Instruction) with all related data
     * Following the same pattern as BookingController.create()
     *
     * @param customerCode the customer code from JWT
     * @param loginName    the login username from JWT (used for CrUser per spec: /ecom/auth/login username)
     * @param request      the SI request DTO
     * @return SICreateResponse with the created SI details
     */
    public SICreateResponse createSi(String customerCode, String loginName, SIBLRequestDTO request) {
        try {
            logger.info("[ECOM] SiCreateService - Starting COMPLETE SI creation for customer: " + customerCode);
            logger.info("[ECOM] SiCreateService - Request booking number: " + (request != null ? request.getBookingNumber() : "null"));
            
            // Step 1: Set customer code from JWT for validation
            request.setCustomerCodeFromJWT(customerCode);
            
            // Step 2: Perform comprehensive validation
            logger.info("[ECOM] SiCreateService - Running validation checks...");
            ValidationResult validationResult = validationService.validateSIRequest(request, customerCode, true);
            
            if (!validationResult.isValid()) {
                throw new SIValidationException(validationResult.getErrors());
            }
            logger.info("[ECOM] SiCreateService - Validation passed successfully");
            
            // Step 3: Check repository availability
            logger.info("[ECOM] SiCreateService - EntityManager available: " + (entityManager != null));
            logger.info("[ECOM] SiCreateService - EcomSiRepository available: " + (ecomSiRepository != null));
            
            // Step 4: Generate unique SI number
            logger.info("[ECOM] SiCreateService - Calling generateSINumber()...");
            String generatedSINo = generateSINumber();
            logger.info("[ECOM] SiCreateService - Generated SI number: " + generatedSINo);
            
            // Step 5: Set server-managed fields (CrUser = login username per spec)
            LocalDateTime now = LocalDateTime.now();
            request.setOnlineSINo(generatedSINo);
            request.setCreatedUser(loginName != null ? loginName : customerCode); // CrUser = username from /ecom/auth/login
            request.setCreatedDate(now);
            request.setChangeUser(null);  // NULL on insert (like BookingController)
            request.setChangeDate(null);  // NULL on insert
            
            // Step 6: Set default values for optional fields
            if (request.getOriginPortId() == null || request.getOriginPortId().trim().isEmpty()) {
                request.setOriginPortId(request.getLoadPortId()); // PLA defaults to POL
            }
            if (request.getFinalPortId() == null || request.getFinalPortId().trim().isEmpty()) {
                request.setFinalPortId(request.getDischargePortId()); // PLD defaults to POD
            }
            if (request.getStatus() == null) {
                request.setStatus(com.svm.ecom.common.enums.SiStatus.D); // Default to Draft
            }
            
            // Step 7: Create main SI record (all 47 columns)
            EcomSi si = createMainSiRecord(request);
            EcomSi savedSi = ecomSiRepository.save(si);
            logger.info("[ECOM] SiCreateService - Saved main SI record with ID: " + savedSi.geteSINo());

            insertAllChildren(savedSi, customerCode, loginName, request);
            
            logger.info("[ECOM] SiCreateService - COMPLETE SI creation completed successfully: " + generatedSINo);
            System.out.println("[SERVICE] SiCreateService - COMPLETE SI creation completed successfully: " + generatedSINo);
            System.out.flush(); // Force flush to ensure output is written
            
            SICreateResponse response = new SICreateResponse(generatedSINo, SiStatus.D, "SI created successfully");
            System.out.println("[SERVICE] About to return SICreateResponse with SINo: " + response.getOnlineSINo());
            System.out.flush(); // Force flush before returning
            logger.info("[ECOM] SiCreateService - Returning response with SINo: " + response.getOnlineSINo());
            
            return response;
            
        } catch (SIValidationException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            // Validation errors - re-throw directly for proper 400 handling in controller
            logger.warn("[ECOM] SiCreateService - Validation error: " + e.getMessage());
            throw e;
        } catch (IllegalStateException e) {
            // Service unavailable errors - re-throw directly for proper 503 handling
            logger.error("[ECOM] SiCreateService - Service unavailable: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            // Unexpected errors - re-throw so controller returns 500 (same as booking-service)
            logger.error("[ECOM] SiCreateService - Unexpected error creating SI: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Map DTO to EcomSi entity (shared by create and update).
     * Caller must set request.onlineSINo, request.createdUser/createdDate (create) or request.changeUser/changeDate (update).
     */
    public void mapRequestToEcomSi(SIBLRequestDTO request, EcomSi si) {
        // ===== PRIMARY IDENTIFICATION (Columns 1-3) =====
        si.seteSINo(request.getOnlineSINo());
        si.setBookno(request.getBookingNumber());
        // blno: only set in createMainSiRecord (null on insert); update leaves existing blno unchanged

        // ===== VESSEL & VOYAGE DETAILS (Columns 4-6) =====
        si.setVessel(request.getVesselCode());
        si.setVoyage(com.solverminds.sibl.util.VoyageCodeUtil.normalizeToNumeric(request.getVoyageCode()));
        si.setBound(request.getLoadBound() != null ? request.getLoadBound().getValue() : null);

        // ===== PORT DETAILS (Columns 7-10) =====
        si.setPol(request.getLoadPortId());
        si.setPod(request.getDischargePortId());
        si.setPla(request.getOriginPortId());
        si.setPld(request.getFinalPortId());

        // ===== USER & AUDIT DETAILS (Columns 11-15) =====
        si.setCustcode(request.getCustomerCodeFromJWT());
        si.setCrUser(request.getCreatedUser());
        si.setCrDate(request.getCreatedDate());
        si.setChgUser(request.getChangeUser());
        si.setChgDate(request.getChangeDate());

        // ===== BL VERSION CONTROL (Columns 16-17) =====
        si.setEditbl(request.getEditBl() != null ? request.getEditBl() : 0);
        si.setBlversion(request.getBlVersion() != null ? request.getBlVersion() : 0);

        // ===== STATUS & REMARKS (Columns 18-20) =====
        si.setStatus(request.getStatus() != null ? request.getStatus().getValue() : "D");
        si.setDeclinedRemarks(request.getDeclinedRemarks());
        si.setCustremarks(request.getAdditionalRemarks());

        // ===== HEADER NAME FIELDS (Columns 21-24) =====
        si.setPlaHeaderName(request.getPlaHeaderName());
        si.setPolHeaderName(request.getPolHeaderName());
        si.setPodHeaderName(request.getPodHeaderName());
        si.setPldHeaderName(request.getPldHeaderName());

        // ===== APPLICATION INFO (Columns 25-26) =====
        // appversion: "1" -> 1, else 2 per spec
        si.setAppversion(request.getAppVersion() != null && request.getAppVersion() == 1 ? 1 : 2);
        si.setCcmailid(request.getCcMailId());

        // ===== CUSTOMER HEADER INFO (Columns 27-32) =====
        si.setShipper(request.getShipperAddress());
        si.setShippername(request.getShipperName());
        si.setConsigee(request.getConsigeeAddress());
        si.setConsigeename(request.getConsigeeName());
        si.setNotify(request.getNotifyAddress());
        si.setNotifyname(request.getNotifyName());

        // ===== AES & COMPLIANCE (Columns 33-35) =====
        si.setAesNumber(request.getAesNumber());
        si.setAesDiscliamer(request.getAesDisclaimer());
        si.setReleasetype(request.getReleaseType());

        // ===== ADDITIONAL DETAILS (Columns 36-47) =====
        si.setDeclaredvalue(request.getDeclaredValue());
        si.setBlType(request.getBlType());
        si.setAcid(request.getAcid());
        si.setFreight(request.getFreight() != null ? request.getFreight().getValue() : null);
        si.setCargoprotectid(request.getCargoProtectId());
        si.setT2lfiling(request.getT2lFilling() != null ? request.getT2lFilling().getValue() : null);
        si.setPackinglst(request.getPackingList());
        si.setInvoiceupload(request.getInvoiceUpload());
        si.setTelexrls(request.getTelexRelease());
        si.setEnsType(request.getEnsType() != null ? request.getEnsType().getValue() : null);
    }

    /**
     * Create the main SI record with all columns (uses shared mapping).
     */
    public EcomSi createMainSiRecord(SIBLRequestDTO request) {
        EcomSi si = new EcomSi();
        si.setBlno(null); // NULL on insert
        mapRequestToEcomSi(request, si);
        return si;
    }

    /**
     * Insert all child records (customers, containers, commodities, reefer, dg, oog, request master).
     * Same mapping and validations as SI create; used by createSi and SiUpdateService (Submit path).
     * @param loginName login username from JWT (e.g. arul.prakash@solverminds.com); used for ecomSIRequestMaster.userId and chgUser. May be null for update path.
     */
    public void insertAllChildren(EcomSi savedSi, String customerCode, String loginName, SIBLRequestDTO request) {
        String bookingTableKey = request.getBookingNumber() != null ? request.getBookingNumber().trim() : null;
        if (bookingTableKey != null && !bookingTableKey.isEmpty() && bookingDetailRepository != null) {
            List<BookingCustDetail> apBpCusts = bookingDetailRepository.getBookingCustomersByBookNoApBp(bookingTableKey);
            for (BookingCustDetail row : apBpCusts) {
                ecomSiCustomerRepository.save(createCustomerRecordFromBookingCust(savedSi, row));
            }
        }
        if (request.getCustomers() != null && !request.getCustomers().isEmpty()) {
            for (SIBLCustomerDTO customerDTO : request.getCustomers()) {
                ecomSiCustomerRepository.save(createCustomerRecord(savedSi, customerDTO));
            }
        }
        if (request.getContainers() != null && !request.getContainers().isEmpty()) {
            // When commodities are present: ignore request cargoWeight; use derived cargoWeight = SUM(itemWeight) per container (in kg).
            Map<String, Integer> derivedCargoWeightKg = computeDerivedCargoWeightByContainer(request);
            for (SIBLContainerDTO containerDTO : request.getContainers()) {
                EcomSiContainer container = createContainerRecord(savedSi, containerDTO, derivedCargoWeightKg);
                ecomSiContainerRepository.save(container);
                boolean isReefer = containerDTO.getReefer() == com.svm.ecom.common.enums.YesNoFlag.Y || (containerDTO.getEquipmentType() != null && containerDTO.getEquipmentType() == com.svm.ecom.common.enums.EquipmentType.R);
                if (isReefer && containerDTO.getNor() != com.svm.ecom.common.enums.YesNoFlag.Y && containerDTO.getReeferSettings() != null && ecomSiReeferRepository != null) {
                    ecomSiReeferRepository.save(createReeferRecord(savedSi, containerDTO));
                }
                if (containerDTO.getOogFlag() == com.svm.ecom.common.enums.YesNoFlag.Y && ecomSiOogRepository != null && containerDTO.getOogDimensions() != null) {
                    ecomSiOogRepository.save(createOogRecord(savedSi, containerDTO, 1));
                }
                // DG is at commodity level only; no container-level DG creation
            }
        }
        if (request.getCommodities() != null && !request.getCommodities().isEmpty()) {
            int itemIndex = 0;
            for (SIBLCommodityDTO commodityDTO : request.getCommodities()) {
                itemIndex++;
                ecomSiCommodityRepository.save(createCommodityRecord(savedSi, commodityDTO));
                if (commodityDTO.getHazardous() == com.svm.ecom.common.enums.YesNoFlag.Y && ecomSiDgRepository != null && commodityDTO.getDangerousGoods() != null) {
                    ecomSiDgRepository.save(createDgRecord(savedSi, commodityDTO, itemIndex));
                }
                if (commodityDTO.getOog() != null && ecomSiOogRepository != null) {
                    ecomSiOogRepository.save(createOogRecordFromCommodity(savedSi, commodityDTO, itemIndex));
                }
            }
        } else if (request.getContainers() != null && !request.getContainers().isEmpty()) {
            int itemIndex = 0;
            for (SIBLContainerDTO containerDTO : request.getContainers()) {
                itemIndex++;
                ecomSiCommodityRepository.save(createCommodityRecordFromContainer(savedSi, containerDTO, itemIndex));
            }
        }
        if (ecomSiRequestMasterRepository != null) {
            ecomSiRequestMasterRepository.save(createRequestMasterRecord(savedSi, customerCode, loginName, request));
        }
    }

    /**
     * Create customer record from DTO (same field mapping as EdiExportService.createEdiTempCustFromDto).
     */
    private EcomSiCustomer createCustomerRecord(EcomSi si, SIBLCustomerDTO customerDTO) {
        EcomSiCustomer customer = new EcomSiCustomer();
        customer.seteSINo(si.geteSINo());
        customer.setCustTypeId(customerDTO.getCustomerType() != null ? customerDTO.getCustomerType().getValue() : "");
        customer.setCustId(customerDTO.getCustomerCode() != null ? customerDTO.getCustomerCode() : "");
        customer.setCountryId(customerDTO.getCountryId() != null ? customerDTO.getCountryId() : "");
        customer.setName(customerDTO.getCustomerName() != null ? customerDTO.getCustomerName() : "");
        customer.setAddress1(customerDTO.getAddress() != null ? customerDTO.getAddress() : "");
        customer.setCity(customerDTO.getCity() != null ? customerDTO.getCity() : "");
        customer.setAddress2(customerDTO.getAddress2());
        customer.setStateId(customerDTO.getStateId());
        customer.setTelephone(customerDTO.getTelephone());
        customer.setEmail(customerDTO.getEmail());
        return customer;
    }

    /**
     * Create customer record from booking_cust row (AP, BP) - same as EdiExportService.createEdiTempCustFromBookingCust.
     */
    private EcomSiCustomer createCustomerRecordFromBookingCust(EcomSi si, BookingCustDetail row) {
        EcomSiCustomer customer = new EcomSiCustomer();
        customer.seteSINo(si.geteSINo());
        customer.setCustTypeId(row.getCustTypeId() != null ? row.getCustTypeId() : "");
        customer.setCustId(row.getCustId() != null ? row.getCustId() : "");
        customer.setCountryId(row.getCountryId() != null ? row.getCountryId() : "");
        customer.setName(row.getName() != null ? row.getName() : "");
        customer.setAddress1(row.getAddress1() != null ? row.getAddress1() : "");
        customer.setCity(row.getCity() != null ? row.getCity() : "");
        customer.setAddress2(row.getAddress2());
        customer.setStateId(row.getStateId());
        customer.setZipCode(row.getZipCode());
        customer.setTelephone(row.getTelephone());
        customer.setFax(row.getFax());
        customer.setEmail(row.getEmail());
        customer.setEori(row.getEori());
        customer.setPersontype(row.getPersontype());
        return customer;
    }

    /**
     * Compute derived cargoWeight per container: SUM(itemWeight) of all commodities for that container, normalized to kg.
     * Key format: "cargoNo|containerNo". Used when commodities are present; request cargoWeight is then ignored.
     */
    private Map<String, Integer> computeDerivedCargoWeightByContainer(SIBLRequestDTO request) {
        Map<String, Integer> sumByKey = new HashMap<>();
        if (request.getCommodities() == null || request.getCommodities().isEmpty()) {
            return sumByKey;
        }
        for (SIBLCommodityDTO c : request.getCommodities()) {
            Integer cargoNo = c.getCargoNo() != null ? c.getCargoNo() : 1;
            String containerNo = c.getContainerNo() != null ? c.getContainerNo() : "";
            String key = cargoNo + "|" + containerNo;
            int itemWeightKg = toKg(c.getItemWeight(), c.getItemWeightUnit());
            sumByKey.merge(key, itemWeightKg, Integer::sum);
        }
        return sumByKey;
    }

    /** Convert commodity itemWeight to kg: T (tonnes) * 1000, K (kilos) as-is. */
    private static int toKg(Integer itemWeight, WeightUnit unit) {
        if (itemWeight == null) return 0;
        if (unit == WeightUnit.T) return itemWeight * 1000;
        return itemWeight;
    }

    /**
     * Create container/cargo record from DTO.
     * When derivedCargoWeightKg contains an entry for this container (commodities path), cargoWeight and weightUnit
     * are set from the sum of commodity itemWeights (kg); otherwise request values are used (no-commodities path).
     * Per spec: shipperOwn/nor/oog/dgflag default 'N', weightUnit default 'K', eqpType from equipmentType (required).
     * Tare weight is set from pre-fetched map (one DB query for all containers) by equipment type.
     *
     * @param derivedCargoWeightKg Map from "cargoNo|containerNo" to cargo weight in kg (sum of commodity itemWeights); empty when no commodities
     */
    private EcomSiContainer createContainerRecord(EcomSi si, SIBLContainerDTO containerDTO, Map<String, Integer> derivedCargoWeightKg) {
        EcomSiContainer container = new EcomSiContainer();
        
        // Set SI reference
        container.seteSINo(si.geteSINo());
        
        // Map container fields (required: conId, eqpSize, containerno, tareWeight, weightUnit, eqpstatus, eqpType)
        container.setConId(containerDTO.getCargoNo() != null ? containerDTO.getCargoNo() : 1);
        container.setEqpSize(containerDTO.getEquipmentSize() != null ? containerDTO.getEquipmentSize() : "");
        container.setContainerno(containerDTO.getContainerNo() != null ? containerDTO.getContainerNo() : "");
        // When commodities present: ignore request cargoWeight; use derived cargoWeight = SUM(itemWeight) in kg with unit K
        String containerKey = (containerDTO.getCargoNo() != null ? containerDTO.getCargoNo() : 1) + "|" + (containerDTO.getContainerNo() != null ? containerDTO.getContainerNo() : "");
        if (derivedCargoWeightKg != null && derivedCargoWeightKg.containsKey(containerKey)) {
            container.setCargoWeight(derivedCargoWeightKg.get(containerKey));
            container.setWeightUnit("K");
        } else {
            container.setCargoWeight(containerDTO.getCargoWeight() != null ? containerDTO.getCargoWeight() : 0);
            container.setWeightUnit(containerDTO.getWeightUnit() != null ? containerDTO.getWeightUnit().getValue() : "K");
        }
        
        // Defaults per spec: shipperOwn 'N', ref_nor 'N', oog 'N', dgflag 'N'
        container.setShipperOwn(containerDTO.getSocFlag() != null ? containerDTO.getSocFlag().getValue() : "N");
        container.setNor(containerDTO.getNor() != null ? containerDTO.getNor().getValue() : "N");
        container.setOog(containerDTO.getOogFlag() != null ? containerDTO.getOogFlag().getValue() : "N");
        container.setDgFlag("N");  // DG is at commodity level only
        
        container.setCarrierSeal(containerDTO.getCarrierSeal());
        container.setShipperSeal(containerDTO.getShipperSeal());
        container.setEquipmentStatus(containerDTO.getEquipmentStatus() != null ? containerDTO.getEquipmentStatus().getValue() : null);
        // Tare weight: required on request (Bean Validation); persisted from DTO
        container.setTareWeight(containerDTO.getTareWeight() != null ? containerDTO.getTareWeight() : 0);
        if (derivedCargoWeightKg != null && derivedCargoWeightKg.containsKey(containerKey)) {
            container.setGrossweight(container.getTareWeight() + container.getCargoWeight());
        } else if (containerDTO.getGrossweight() != null) {
            container.setGrossweight(containerDTO.getGrossweight());
        }
        if (containerDTO.getRateEquipmentTypeId() != null) {
            container.setRateEquipmentSize(containerDTO.getRateEquipmentTypeId());
        }
        // eqpType required per spec; D (Dry) stored as N in DB
        container.setEqpType(containerDTO.getEquipmentType() != null ? containerDTO.getEquipmentType().getDbValue() : "");
        
        return container;
    }

    /**
     * Create request master record from DTO.
     * userId and chgUser = login username (e.g. arul.prakash@solverminds.com); chgDate = now when not provided; requestedSI/createdSI default 1.
     */
    private EcomSiRequestMaster createRequestMasterRecord(EcomSi si, String customerCode, String loginName, SIBLRequestDTO requestDTO) {
        EcomSiRequestMaster requestMaster = new EcomSiRequestMaster();
        
        // Set reqID to the SI number (auto-generated)
        requestMaster.setReqID(si.geteSINo());
        
        // Set user ID to customer code
        requestMaster.setUserId(customerCode);
        
        // requestedSI and createdSI default to 1 (never 0 for new SI)
        requestMaster.setRequestedSI(requestDTO.getRequestedSI() != null && requestDTO.getRequestedSI() > 0 ? requestDTO.getRequestedSI() : 1);
        requestMaster.setCreatedSI(requestDTO.getCreatedSI() != null && requestDTO.getCreatedSI() > 0 ? requestDTO.getCreatedSI() : 1);
        
        // Set booking number and customer code
        requestMaster.setBookno(requestDTO.getBookingNumber());
        requestMaster.setCustcode(requestDTO.getCustomerCodeFromJWT());
        
        // CrUser / CrDate from request (set on create to login name and now)
        requestMaster.setCrUser(requestDTO.getCreatedUser());
        requestMaster.setCrDate(requestDTO.getCreatedDate());
        requestMaster.setChgUser(requestDTO.getChangeUser());
        requestMaster.setChgDate(requestDTO.getChangeDate());
        
        return requestMaster;
    }

    /**
     * Create commodity record from DTO
     */
    private EcomSiCommodity createCommodityRecord(EcomSi si, SIBLCommodityDTO commodityDTO) {
        EcomSiCommodity commodity = new EcomSiCommodity();
        
        // Set SI reference
        commodity.seteSINo(si.geteSINo());
        
        // Map commodity fields
        commodity.setConId(commodityDTO.getCargoNo());
        commodity.setContainerno(commodityDTO.getContainerNo());
        commodity.setItemno(commodityDTO.getItemNo());
        commodity.setCommodity(commodityDTO.getCommodityGroup() != null && !commodityDTO.getCommodityGroup().trim().isEmpty() ? commodityDTO.getCommodityGroup().trim() : "");
        commodity.setPackageType(commodityDTO.getPackageType());
        commodity.setPackageQty(commodityDTO.getPackageQty());
        commodity.setHsCommodity(commodityDTO.getHsCode());
        
        // Map additional fields if provided
        if (commodityDTO.getItemWeight() != null) {
            commodity.setItemWeight(commodityDTO.getItemWeight().doubleValue());
        }
        if (commodityDTO.getItemWeightUnit() != null) {
            commodity.setItemWeightUnit(commodityDTO.getItemWeightUnit().getValue());
        }
        if (commodityDTO.getItemVolume() != null) {
            commodity.setItemVolume(commodityDTO.getItemVolume());
        }
        if (commodityDTO.getItemVolumeUnit() != null) {
            commodity.setItemVolumeUnit(commodityDTO.getItemVolumeUnit().getValue());
        }
        if (commodityDTO.getMarksNo() != null) {
            commodity.setMarksNo(commodityDTO.getMarksNo());
        }
        if (commodityDTO.getMarksDesc() != null) {
            commodity.setMarksDesc(commodityDTO.getMarksDesc());
        }
        if (commodityDTO.getHazardous() != null) {
            commodity.setHazardous(commodityDTO.getHazardous().getValue());
        }
        if (commodityDTO.getNettwt() != null) {
            commodity.setNettwt(commodityDTO.getNettwt().doubleValue());
        }
        if (commodityDTO.getBuyerCode() != null) {
            commodity.setBycode(commodityDTO.getBuyerCode());
        }
        if (commodityDTO.getSellerCode() != null) {
            commodity.setSlcode(commodityDTO.getSellerCode());
        }
        if (commodityDTO.getBuyerName() != null) {
            commodity.setByname(commodityDTO.getBuyerName());
        }
        if (commodityDTO.getSellerName() != null) {
            commodity.setSlname(commodityDTO.getSellerName());
        }
        if (commodityDTO.getCommodityHBLNo() != null) {
            commodity.setCmdtyHblno(commodityDTO.getCommodityHBLNo());
        }
        
        return commodity;
    }

    /**
     * Create commodity record from container when request has no commodities array (same as EdiExportService.createEdiTempCommodityFromContainer).
     */
    private EcomSiCommodity createCommodityRecordFromContainer(EcomSi si, SIBLContainerDTO container, int itemNo) {
        EcomSiCommodity commodity = new EcomSiCommodity();
        commodity.seteSINo(si.geteSINo());
        commodity.setConId(container.getCargoNo() != null ? container.getCargoNo() : 1);
        commodity.setContainerno(container.getContainerNo() != null ? container.getContainerNo() : "");
        commodity.setItemno(itemNo);
        commodity.setCommodity("GEN");
        commodity.setPackageType("CTN");
        commodity.setPackageQty(1);
        commodity.setHsCommodity("GEN");  // DG at commodity level only; no container-level DG
        commodity.setHazardous("N");
        if (container.getCargoWeight() != null) {
            commodity.setItemWeight(container.getCargoWeight().doubleValue());
            commodity.setNettwt(container.getCargoWeight().doubleValue());
        }
        commodity.setItemWeightUnit(container.getWeightUnit() != null ? container.getWeightUnit().getValue() : null);
        return commodity;
    }

    /**
     * Create reefer record from container DTO.
     * Insert only if: reefer=Y (or equipment type R) AND nor!=Y; SIBLReeferDTO required.
     * Temperature values: only set when present (convertEmptyToNull semantics).
     */
    private EcomSiReefer createReeferRecord(EcomSi si, SIBLContainerDTO containerDTO) {
        EcomSiReefer reefer = new EcomSiReefer();
        SIBLReeferDTO reeferDTO = containerDTO.getReeferSettings();
        
        reefer.seteSINo(si.geteSINo());
        reefer.setConId(containerDTO.getCargoNo() != null ? containerDTO.getCargoNo() : 1);
        reefer.setContainerno(containerDTO.getContainerNo() != null ? containerDTO.getContainerNo() : "");
        
        if (reeferDTO != null) {
            // Temperature values: convertEmptyToNull - only set when present
            Double tempSet = reeferDTO.getTempSet();
            if (tempSet != null && !Double.isNaN(tempSet)) {
                reefer.setRefSetTemp(tempSet.floatValue());
            }
            Double tempMin = reeferDTO.getTempMin();
            if (tempMin != null && !Double.isNaN(tempMin)) {
                reefer.setRefMinTemp(tempMin.floatValue());
            }
            Double tempMax = reeferDTO.getTempMax();
            if (tempMax != null && !Double.isNaN(tempMax)) {
                reefer.setRefMaxTemp(tempMax.floatValue());
            }
            // ref_tempUnit: default 'C' per spec
            reefer.setRefTempUnit(reeferDTO.getTempSetUnit() != null ? reeferDTO.getTempSetUnit().getValue() : "C");
            
            // Map ventilation - convert Double to String
            if (reeferDTO.getVentSet() != null) {
                reefer.setVentPercentage(reeferDTO.getVentSet().toString());
            }
            
            // Map humidity - convert Double to String
            if (reeferDTO.getHumidity() != null) {
                reefer.setHumidity(reeferDTO.getHumidity().toString());
            }
            // humidity_flag: default 0 (false) per spec
            if (reeferDTO.getHumidityFlag() != null) {
                reefer.setHumidityFlag(reeferDTO.getHumidityFlag() == com.svm.ecom.common.enums.YesNoFlag.Y);
            } else {
                reefer.setHumidityFlag(false);
            }
            
            // Map drains and ventilation
            if (reeferDTO.getDrains() != null) {
                reefer.setDrains(reeferDTO.getDrains());
            }
            if (reeferDTO.getVentilation() != null) {
                reefer.setVentPercentage(reeferDTO.getVentilation());
            } else if (reeferDTO.getVentSet() != null) {
                reefer.setVentPercentage(reeferDTO.getVentSet().toString());
            }
            
            // Map O2 and CO2 - convert Double to String
            if (reeferDTO.getO2Level() != null) {
                reefer.setO2Value(reeferDTO.getO2Level().toString());
            }
            if (reeferDTO.getCo2Level() != null) {
                reefer.setCo2Value(reeferDTO.getCo2Level().toString());
            }
        } else {
            // Defaults when DTO is empty: ref_tempUnit 'C', humidity_flag 0 per spec
            reefer.setRefTempUnit("C");
            reefer.setHumidityFlag(false);
        }
        
        return reefer;
    }

    /**
     * Create OOG record from container DTO
     * Insert only if: oogFlag=Y
     */
    private EcomSiOog createOogRecord(EcomSi si, SIBLContainerDTO containerDTO, int comItemNo) {
        EcomSiOog oog = new EcomSiOog();
        SIBLOogDTO oogDTO = containerDTO.getOogDimensions();
        
        oog.seteSINo(si.geteSINo());
        oog.setConId(containerDTO.getCargoNo());
        oog.setComItemNo(comItemNo);
        oog.setContainerno(containerDTO.getContainerNo());
        
        if (oogDTO != null) {
            oog.setLeftft(oogDTO.getLeftFt());
            oog.setRightft(oogDTO.getRightFt());
            oog.setForward(oogDTO.getForward());
            oog.setAlt(oogDTO.getAft());
            oog.setTopft(oogDTO.getTopFt());
            // Unit default 'M' per spec
            oog.setUnit(oogDTO.getUnit() != null ? oogDTO.getUnit().getValue() : "M");
        }
        
        return oog;
    }

    /**
     * Create DG (Dangerous Goods) record from commodity DTO
     * Insert only if: hazardous=Y and (dangerousGoods or imoContact/imoPhone present)
     */
    private EcomSiDg createDgRecord(EcomSi si, SIBLCommodityDTO commodityDTO, int itemNo) {
        EcomSiDg dg = new EcomSiDg();
        SIBLDangerousGoodsDTO dgDTO = commodityDTO.getDangerousGoods();
        
        dg.seteSINo(si.geteSINo());
        dg.setConId(commodityDTO.getCargoNo());
        dg.setContainerno(commodityDTO.getContainerNo());
        dg.setItemno(itemNo);
        
        if (dgDTO != null) {
            mapDangerousGoodsToEntity(dg, dgDTO);
        }
        // Fallback to commodity-level imoContact/imoPhone when dangerousGoods not provided
        if (commodityDTO.getImoContact() != null) {
            dg.setImoContact(commodityDTO.getImoContact());
        }
        if (commodityDTO.getImoPhone() != null) {
            dg.setImoPhone(commodityDTO.getImoPhone());
        }
        
        return dg;
    }

    /**
     * Create OOG record from commodity DTO when commodity has oog dimensions
     */
    private EcomSiOog createOogRecordFromCommodity(EcomSi si, SIBLCommodityDTO commodityDTO, int itemNo) {
        EcomSiOog oog = new EcomSiOog();
        SIBLOogDTO oogDTO = commodityDTO.getOog();
        
        oog.seteSINo(si.geteSINo());
        oog.setConId(commodityDTO.getCargoNo());
        oog.setComItemNo(itemNo);
        oog.setContainerno(commodityDTO.getContainerNo());
        
        if (oogDTO != null) {
            oog.setLeftft(oogDTO.getLeftFt());
            oog.setRightft(oogDTO.getRightFt());
            oog.setForward(oogDTO.getForward());
            oog.setAlt(oogDTO.getAft());
            oog.setTopft(oogDTO.getTopFt());
            // Unit default 'M' per spec
            oog.setUnit(oogDTO.getUnit() != null ? oogDTO.getUnit().getValue() : "M");
        }
        
        return oog;
    }

    private void mapDangerousGoodsToEntity(EcomSiDg dg, SIBLDangerousGoodsDTO dgDTO) {
        dg.setMarinePollutant(dgDTO.getMarinePollutant() != null ? dgDTO.getMarinePollutant().getValue() : null);
        dg.setUnNo(dgDTO.getUnNo());
        dg.setImdgClass(dgDTO.getImdgClass());
        dg.setFlashPt(dgDTO.getFlashPt());
        dg.setFlashPtUnit(dgDTO.getFlashPtUnit() != null ? dgDTO.getFlashPtUnit().getValue() : null);
        dg.setImoContact(dgDTO.getImoContact());
        dg.setImoPhone(dgDTO.getImoPhone());
    }

    /**
     * Create charge record from charge DTO
     */
    private EcomSiCharges createChargeRecord(EcomSi si, SIBLChargeDTO chargeDTO) {
        EcomSiCharges charge = new EcomSiCharges();
        
        charge.setEsino(si.geteSINo());
        charge.setChargeid(chargeDTO.getChargeId());
        charge.setPayagencyid(chargeDTO.getPayAgencyId());
        charge.setCusttypeid(chargeDTO.getCustTypeId() != null ? chargeDTO.getCustTypeId().getValue() : null);
        charge.setPrepaidcollect(chargeDTO.getPrepaidCollect() != null ? chargeDTO.getPrepaidCollect().getValue() : null);
        charge.setEqpTypeId(chargeDTO.getEqpTypeId());
        
        return charge;
    }

    /**
     * Create ICS New (ENS Declarant) record from DTO
     * Insert only if: filingType is provided
     */
    private EcomSiIcsNew createIcsNewRecord(EcomSi si, SIBLEnsDeclarantDTO ensDTO) {
        EcomSiIcsNew ics = new EcomSiIcsNew();
        
        ics.seteSINo(si.geteSINo());
        ics.setEucustom(ensDTO.getEuCustom());
        ics.setBillType(ensDTO.getBillType());
        ics.setEnsType(ensDTO.getEnsType() != null ? ensDTO.getEnsType().getValue() : null);
        ics.setPaymentMode(ensDTO.getPaymentMode());
        ics.setCustName(ensDTO.getCustName());
        ics.setAddress(ensDTO.getAddress());
        ics.setAddress1(ensDTO.getAddress1());
        ics.setCity(ensDTO.getCity());
        ics.setCountry(ensDTO.getCountry());
        ics.setState(ensDTO.getState());
        ics.setTelephone(ensDTO.getTelephone());
        ics.setEmail(ensDTO.getEmail());
        ics.setEnseori(ensDTO.getEnsEori());
        ics.setFilingType(ensDTO.getFilingType() != null ? ensDTO.getFilingType().getValue() : null);
        ics.setZipcode(ensDTO.getZipCode());
        
        return ics;
    }
}
