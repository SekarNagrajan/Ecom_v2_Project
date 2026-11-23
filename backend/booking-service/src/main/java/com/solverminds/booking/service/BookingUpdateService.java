package com.solverminds.booking.service;

import com.solverminds.booking.dto.BookingCreateResponse;
import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.exception.InvalidStatusTransitionException;
import com.svm.ecom.common.enums.BookingStatus;
import com.solverminds.booking.entity.*;
import com.solverminds.booking.repo.*;
import com.solverminds.booking.service.BookingStatusTransitionValidator.TransitionResult;
import com.solverminds.booking.service.BookingStatusTransitionValidator.EdiUpdateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(transactionManager = "ecomTransactionManager")
public class BookingUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(BookingUpdateService.class);

    @PersistenceContext(unitName = "ecomPersistenceUnit")
    private EntityManager entityManager;

    private final EcomBookingRepository bookingRepo;
    private final EcomBookingCustomerRepository customerRepo;
    private final EcomBookingRouteRepository routeRepo;
    private final EcomBookingContainerRepository containerRepo;
    private final EcomBookingCommodityRepository commodityRepo;
    private final EcomBookingReeferRepository reeferRepo;
    private final EcomBookingOogRepository oogRepo;
    private final EcomBookingDgRepository dgRepo;
    private final BookingCreateService bookingCreateService;
    private final AgencyLocationRepository agencyLocationRepo;
    private final BookingStatusTransitionValidator statusTransitionValidator;
    private final LrpStatusLookupService lrpStatusLookupService;
    private final EdiExportService ediExportService;

    public BookingUpdateService(
            EcomBookingRepository bookingRepo,
            EcomBookingCustomerRepository customerRepo,
            EcomBookingRouteRepository routeRepo,
            EcomBookingContainerRepository containerRepo,
            EcomBookingCommodityRepository commodityRepo,
            EcomBookingReeferRepository reeferRepo,
            EcomBookingOogRepository oogRepo,
            EcomBookingDgRepository dgRepo,
            BookingCreateService bookingCreateService,
            @Qualifier("bookingAgencyLocationRepository") AgencyLocationRepository agencyLocationRepo,
            BookingStatusTransitionValidator statusTransitionValidator,
            LrpStatusLookupService lrpStatusLookupService,
            EdiExportService ediExportService) {
        this.bookingRepo = bookingRepo;
        this.customerRepo = customerRepo;
        this.routeRepo = routeRepo;
        this.containerRepo = containerRepo;
        this.commodityRepo = commodityRepo;
        this.reeferRepo = reeferRepo;
        this.oogRepo = oogRepo;
        this.dgRepo = dgRepo;
        this.bookingCreateService = bookingCreateService;
        this.agencyLocationRepo = agencyLocationRepo;
        this.statusTransitionValidator = statusTransitionValidator;
        this.lrpStatusLookupService = lrpStatusLookupService;
        this.ediExportService = ediExportService;
    }

    /**
     * Update an existing booking
     * @param onlineBookNo The booking number to update
     * @param req The updated booking request DTO
     * @param customerCodeFromJwt Customer code from JWT
     * @param loginNameFromJwt Login name from JWT
     * @return BookingCreateResponse with update status, or null if booking not found
     * @throws InvalidStatusTransitionException if status transition is not allowed
     */
    public BookingCreateResponse updateBooking(String onlineBookNo, BookingRequestDTO req, 
                                               String customerCodeFromJwt, String loginNameFromJwt) {
        
        // 1. Check if booking exists
        Optional<EcomBooking> existingBooking = bookingRepo.findByEbookno(onlineBookNo);
        if (!existingBooking.isPresent()) {
            logger.warn("Booking not found for update: {}", onlineBookNo);
            return null; // Booking not found
        }

        EcomBooking entity = existingBooking.get();
        
        // 2. Verify customer code matches (security check)
        if (!customerCodeFromJwt.equalsIgnoreCase(entity.getCustCode())) {
            logger.error("Customer code mismatch. JWT: {}, DB: {}", customerCodeFromJwt, entity.getCustCode());
            throw new SecurityException("Customer code mismatch - unauthorized access");
        }

        // 3. Validate status transition
        String currentEcomStatus = entity.getEbookStatus();
        String requestedStatus = req.getStatus() != null ? req.getStatus().getValue() : currentEcomStatus;
        
        // Lookup LRP status if current status is P (In-Progress) or A (legacy Active)
        String lrpStatus = null;
        if ("P".equalsIgnoreCase(currentEcomStatus) || "A".equalsIgnoreCase(currentEcomStatus)) {
            lrpStatus = lrpStatusLookupService.getLrpStatus(onlineBookNo);
            logger.debug("LRP status for booking {}: {}", onlineBookNo, lrpStatus);
        }
        
        // Validate and throw exception if transition is not allowed
        TransitionResult transitionResult = statusTransitionValidator.validateAndThrow(
            currentEcomStatus, lrpStatus, requestedStatus);
        
        logger.debug("Status transition validated: {} -> {} (LRP: {}), requiresEdi: {}", 
            currentEcomStatus, requestedStatus, lrpStatus, transitionResult.isRequiresEdiUpdate());

        // 4. Delete existing child records
        deleteChildRecords(onlineBookNo);
        
        // CRITICAL: Flush deletes to database before inserting new records
        // This prevents primary key violations on composite keys (ebookno, leg_no)
        entityManager.flush();
        logger.debug("Flushed delete operations for booking: {}", onlineBookNo);

        // 5. Update master booking record
        updateMasterBooking(entity, req, customerCodeFromJwt, loginNameFromJwt);

           // 5a. If EDI temp booking already exists, keep CustRefNo / SHR in sync even when no EDI export runs
           try {
            ediExportService.upsertCustomerReferenceForExistingEdiTemp(onlineBookNo, req.getCustomerPO());
        } catch (Exception e) {
            // Non-blocking: booking update must succeed even if EDI temp sync fails
            logger.warn("Could not sync customerPO into EDI temp tables for {}: {}", onlineBookNo, e.getMessage());
        }

        // 6. Insert new child records using BookingCreateService methods
        
        // 6.1) Insert customers if present
        if (req.getCustomers() != null && !req.getCustomers().isEmpty()) {
            bookingCreateService.addCustomers(onlineBookNo, req.getCustomers(), loginNameFromJwt);
        }

        // 6.2) Insert routes if present
        if (req.getRoutes() != null && !req.getRoutes().isEmpty()) {
            bookingCreateService.addRoutes(onlineBookNo, req.getRoutes(), loginNameFromJwt);
        }

        // 6.3) Insert containers if present
        if (req.getContainers() != null && !req.getContainers().isEmpty()) {
            bookingCreateService.addContainers(onlineBookNo, req.getContainers(), req.getCommodities(), loginNameFromJwt);
        }

        // 6.4) Insert commodities if present
        if (req.getCommodities() != null && !req.getCommodities().isEmpty()) {
            bookingCreateService.addCommodities(onlineBookNo, req.getCommodities(), req.getContainers(), loginNameFromJwt);
        }

        // 7. Handle EDI updates based on transition result
        if (transitionResult.isRequiresEdiUpdate()) {
            // Resolve agencyId if not provided (same as POST /create)
            if (req.getAgencyId() == null || req.getAgencyId().trim().isEmpty()) {
                String agencyCode = bookingCreateService.resolveAgencyCode(req.getOriginPortId(), req.getAgencyId());
                req.setAgencyId(agencyCode);
            }
            // Get extBookNo from existing entity for amendments
            String extBookNo = entity.getExtBookNo();
            handleEdiUpdate(onlineBookNo, req, loginNameFromJwt, transitionResult.getEdiUpdateType(), extBookNo);
        }

        logger.info("Booking updated successfully: {}", onlineBookNo);

        // 8. Return success response
        return new BookingCreateResponse(
            onlineBookNo,
            req.getStatus() != null ? req.getStatus() : safeBookingStatus(entity.getEbookStatus()),
            "Booking updated successfully"
        );
    }

    /**
     * Handle EDI updates based on the transition type.
     * Uses the same logic as POST /create - calls exportToEdiTempTables directly.
     * 
     * @param onlineBookNo The booking number
     * @param req The booking request DTO (must have agencyId resolved)
     * @param loginNameFromJwt Login name from JWT
     * @param ediUpdateType The type of EDI update required
     * @param extBookNo The ERP booking number for amendments (null for initial submit)
     */
    private void handleEdiUpdate(String onlineBookNo, BookingRequestDTO req, 
                                  String loginNameFromJwt, EdiUpdateType ediUpdateType, String extBookNo) {
        try {
            switch (ediUpdateType) {
                case INITIAL_SUBMIT:
                    // D → S: Initial submit - same as POST creation (no extBookNo, tbookNo will be empty)
                    logger.info("Handling D→S initial submit EDI export for booking: {}", onlineBookNo);
                    ediExportService.exportToEdiTempTables(onlineBookNo, req, loginNameFromJwt, null);
                    break;
                    
                case RESUBMIT_FROM_SUBMITTED:
                    // S → S: Update existing R to V, then insert new R with extBookNo
                    logger.info("Handling S→S resubmit EDI update for booking: {} (extBookNo: {})", onlineBookNo, extBookNo);
                    ediExportService.resubmitFromSubmitted(onlineBookNo, req, loginNameFromJwt, extBookNo);
                    break;
                    
                case RESUBMIT_FROM_IN_PROGRESS:
                    // P (LRP=D) → S: Insert new R with extBookNo (no R→V update needed)
                    logger.info("Handling P→S resubmit EDI update for booking: {} (extBookNo: {})", onlineBookNo, extBookNo);
                    ediExportService.resubmitFromInProgress(onlineBookNo, req, loginNameFromJwt, extBookNo);
                    break;
                    
                default:
                    logger.warn("Unknown EDI update type: {}", ediUpdateType);
            }
        } catch (Exception e) {
            logger.error("Error handling EDI update for booking {}: {}", onlineBookNo, e.getMessage(), e);
            // Continue with the update even if EDI fails - log the error but don't fail the transaction
        }
    }

    /**
     * Delete all child records for a booking
     * @param ebookno The booking number
     */
    private void deleteChildRecords(String ebookno) {
        logger.debug("Deleting child records for booking: {}", ebookno);
        
        // Delete in reverse dependency order to avoid FK violations
        dgRepo.deleteByEbookno(ebookno);
        oogRepo.deleteByEbookno(ebookno);
        reeferRepo.deleteByEbookno(ebookno);
        commodityRepo.deleteByEbookno(ebookno);
        containerRepo.deleteByEbookno(ebookno);
        routeRepo.deleteByEbookno(ebookno);
        customerRepo.deleteByEbookno(ebookno);
        
        logger.debug("Child records deleted for booking: {}", ebookno);
    }

    /**
     * Update the master booking record
     * @param entity Existing booking entity
     * @param req Updated booking request DTO
     * @param customerCodeFromJwt Customer code from JWT
     * @param loginNameFromJwt Login name from JWT
     */
    private void updateMasterBooking(EcomBooking entity, BookingRequestDTO req, 
                                    String customerCodeFromJwt, String loginNameFromJwt) {
        
        // Fetch agency code from reportDB based on origin port
        String agencyCode = agencyLocationRepo.getAgencyCodeByPortCode(req.getOriginPortId());
        if (agencyCode == null || agencyCode.trim().isEmpty()) {
            // Fallback to request if not found in reportDB
            agencyCode = req.getAgencyId();
        }
        
        LocalDateTime now = LocalDateTime.now();

        // Update master booking fields
        entity.setAgencyid(agencyCode);
        entity.setOriginPortId(req.getOriginPortId());
        entity.setFinalPortId(req.getFinalPortId());
        entity.setLoadPortId(req.getLoadPortId());
        entity.setDischargePortId(req.getDischargePortId());
        
        // Update bookDate only if provided
        if (req.getBookDate() != null) {
            entity.setBookDate(req.getBookDate());
        }
        
        entity.setBookReady(req.getCargoReadyDate() == null ? null : 
            req.getCargoReadyDate().atStartOfDay());
        entity.setPreCarriage(req.getPreCarriage() != null ? req.getPreCarriage().getValue() : null);
        entity.setOnCarriage(req.getOnCarriage() != null ? req.getOnCarriage().getValue() : null);
        entity.setRemarks(req.getRemarks());
        entity.setIsAmendment("Y");
        
        // Update user and timestamp
        entity.setUserup(loginNameFromJwt);
        entity.setDateup(now);
        
        entity.setEbookStatus(req.getStatus() != null ? req.getStatus().getValue() : null);
        entity.setCustCode(customerCodeFromJwt);
        
        // Update other fields
        entity.setTemplatename(req.getTemplatename());
        entity.setRateno(req.getRateno());
        entity.setRouteId(req.getRouteId() != null ? req.getRouteId().longValue() : null);
        entity.setAgencyRefNo(req.getAgencyRefNo());
        entity.setOceanfreight(req.getOceanFreight() != null ? req.getOceanFreight().getValue() : null);
        entity.setExportRef(req.getExportRefNo());
        entity.setCustPO(req.getCustomerPO());
        entity.setEnsfiling(req.getEnsFilling() != null ? req.getEnsFilling().getValue() : null);
        entity.setT2lfiling(req.getT2lFilling() != null ? req.getT2lFilling().getValue() : null);
        entity.setEnsType(req.getEnsType() != null ? req.getEnsType().getValue() : null);

        // Increment version for optimistic locking
        Integer currentVersion = entity.getVersion();
        entity.setVersion(currentVersion != null ? currentVersion + 1 : 1);

        // Save updated entity
        bookingRepo.save(entity);
        
        logger.debug("Master booking updated: {}", entity.getEbookno());
    }

    /**
     * Safe BookingStatus conversion.
     * Maps legacy 'A' (Active) to 'P' (In-Progress) for backward compatibility.
     */
    private BookingStatus safeBookingStatus(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            String normalizedValue = value.trim();
            if ("A".equalsIgnoreCase(normalizedValue)) {
                normalizedValue = "P";
            }
            return BookingStatus.fromValue(normalizedValue);
        } catch (Exception e) {
            return null;
        }
    }
}

