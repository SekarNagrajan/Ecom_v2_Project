package com.solverminds.booking.service;

import com.solverminds.booking.dto.*;
import com.solverminds.booking.entity.*;
import com.solverminds.booking.mapper.BookingMapperV2;
import com.solverminds.booking.mapper.DangerousGoodsMapper;
import com.svm.ecom.common.enums.*;
import com.solverminds.booking.repo.*;
import com.solverminds.booking.validation.BookingStatusValidationService;
import com.svm.ecom.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.svm.ecom.common.util.PortLocalTimeUtil;

/**
 * Service for retrieving booking details with all related data.
 * Supports two data sources:
 * 1. ECOM database (via onlineBookNo/ebookno) - uses EcomBooking* entities
 * 2. Documentation database (via bookingNumber/book_no) - uses booking-common entities
 */
@Service
@Transactional(transactionManager = "ecomTransactionManager", readOnly = true)
public class BookingViewService {

    private static final Logger logger = LoggerFactory.getLogger(BookingViewService.class);

    private final EcomBookingRepository bookingRepo;
    private final EcomBookingCustomerRepository customerRepo;
    private final EcomBookingRouteRepository routeRepo;
    private final EcomBookingContainerRepository containerRepo;
    private final EcomBookingCommodityRepository commodityRepo;
    private final EcomBookingReeferRepository reeferRepo;
    private final EcomBookingDgRepository dgRepo;
    private final EcomBookingOogRepository oogRepo;
    private final MasterDataCacheService masterDataCacheService;
    private final TeusCalculationService teusCalculationService;
    private final BookingMapperV2 bookingMapperV2;
    private final DangerousGoodsMapper dangerousGoodsMapper;
    private final DangerousGoodsEnrichmentService dangerousGoodsEnrichmentService;
    private final BookingStatusValidationService bookingStatusValidationService;

    // Service for fetching from Documentation database when bookingNumber is provided
    private DocumentationBookingViewService documentationBookingViewService;
    // Service for listing documents (files) - setter injection to avoid circular dependency
    private DocumentService documentService;

    public BookingViewService(
            EcomBookingRepository bookingRepo,
            EcomBookingCustomerRepository customerRepo,
            EcomBookingRouteRepository routeRepo,
            EcomBookingContainerRepository containerRepo,
            EcomBookingCommodityRepository commodityRepo,
            EcomBookingReeferRepository reeferRepo,
            EcomBookingDgRepository dgRepo,
            EcomBookingOogRepository oogRepo,
            MasterDataCacheService masterDataCacheService,
            TeusCalculationService teusCalculationService,
            BookingMapperV2 bookingMapperV2,
            DangerousGoodsMapper dangerousGoodsMapper,
            DangerousGoodsEnrichmentService dangerousGoodsEnrichmentService,
            BookingStatusValidationService bookingStatusValidationService) {
        this.bookingRepo = bookingRepo;
        this.customerRepo = customerRepo;
        this.routeRepo = routeRepo;
        this.containerRepo = containerRepo;
        this.commodityRepo = commodityRepo;
        this.reeferRepo = reeferRepo;
        this.dgRepo = dgRepo;
        this.oogRepo = oogRepo;
        this.masterDataCacheService = masterDataCacheService;
        this.teusCalculationService = teusCalculationService;
        this.bookingMapperV2 = bookingMapperV2;
        this.dangerousGoodsMapper = dangerousGoodsMapper;
        this.dangerousGoodsEnrichmentService = dangerousGoodsEnrichmentService;
        this.bookingStatusValidationService = bookingStatusValidationService;
    }

    /**
     * Setter for DocumentationBookingViewService (optional dependency injection).
     * Using setter injection to avoid circular dependency issues.
     */
    @Autowired(required = false)
    public void setDocumentationBookingViewService(DocumentationBookingViewService documentationBookingViewService) {
        this.documentationBookingViewService = documentationBookingViewService;
    }

    @Autowired(required = false)
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Find a booking by customer code and optional search criteria.
     * 
     * Logic:
     * - If onlineBookNo is provided -> search in ECOM database (ebooking tables)
     * - If only bookingNumber is provided -> search in Documentation database (booking tables)
     * - If both are provided -> try ECOM first, then fall back to Documentation when ECOM miss
     *
     * <p>For GET /view status-aware routing (Accepted → LRP redirect), use {@link #findBookingForView}.</p>
     *
     * @param customerCode The customer code (from JWT)
     * @param onlineBookNo Optional online booking number (ebookno from ECOM)
     * @param bookingNumber Optional booking number (book_no from Documentation)
     * @param status Optional booking status
     * @return Optional containing the complete booking DTO if found
     */
    public Optional<BookingRequestDTO> findBooking(String customerCode, String onlineBookNo,
                                                     String bookingNumber, String status) {
        
        // Case 1: If onlineBookNo is provided, search in ECOM database first
        if (onlineBookNo != null && !onlineBookNo.trim().isEmpty()) {
            logger.debug("Searching ECOM database with onlineBookNo: {}", onlineBookNo);
            Optional<BookingRequestDTO> ecomResult = findFromEcomDatabase(customerCode, onlineBookNo, bookingNumber, status);
            if (ecomResult.isPresent()) {
                return ecomResult;
            }
        }
        
        // Case 2: If bookingNumber is provided (and onlineBookNo not found or not provided),
        // search in Documentation database
        if (bookingNumber != null && !bookingNumber.trim().isEmpty()) {
            logger.debug("Searching Documentation database with bookingNumber: {}", bookingNumber);
            return findFromDocumentationDatabase(bookingNumber);
        }
        
        // Case 3: No valid search criteria provided
        logger.debug("No valid search criteria provided for booking lookup");
        return Optional.empty();
    }

    /**
     * View-specific lookup with status-aware routing for GET /api/v1/booking/view.
     * <ul>
     *   <li>D/S/T/V → ECOM details (bookingNumber ignored when ECOM hit)</li>
     *   <li>Accepted + valid bookingNumber → Documentation (LRP) details</li>
     *   <li>Accepted + missing/invalid bookingNumber → 422</li>
     *   <li>onlineBookNo not in ecombooking → 404</li>
     *   <li>Both onlineBookNo and bookingNumber provided → bookingNumber must match extBookNo</li>
     *   <li>Other non-active statuses (P, C, …) → 422</li>
     * </ul>
     */
    public BookingViewLookupResult findBookingForView(String customerCode, String onlineBookNo,
            String bookingNumber, String status) {

        String obn = trimToNull(onlineBookNo);
        String bkn = trimToNull(bookingNumber);

        if (obn != null) {
            Optional<EcomBooking> ecomOpt = bookingRepo.findByEbookno(obn);
            if (!ecomOpt.isPresent()) {
                return BookingViewLookupResult.notFound("onlineBookNo", obn);
            }

            EcomBooking ecomRow = ecomOpt.get();
            if (bkn != null) {
                Optional<ErrorResponse> bookingNumberError =
                        bookingStatusValidationService.validateBookingNumberMatchesExtBookNo(
                                ecomRow.getExtBookNo(), bkn);
                if (bookingNumberError.isPresent()) {
                    return BookingViewLookupResult.unprocessable(bookingNumberError.get());
                }
            }

            String ecomStatus = ecomRow.getEbookStatus();

            if (bookingStatusValidationService.isActiveEcomStatus(ecomStatus)) {
                Optional<BookingRequestDTO> ecomDto =
                        findFromEcomDatabase(customerCode, obn, bkn, status);
                if (ecomDto.isPresent()) {
                    return BookingViewLookupResult.success(ecomDto.get());
                }
                // Active ECOM row but customer mismatch — fall through to bookingNumber
            } else if (bookingStatusValidationService.isAcceptedStatus(ecomStatus)) {
                if (bkn != null) {
                    Optional<BookingRequestDTO> lrp = findFromDocumentationDatabase(bkn);
                    if (lrp.isPresent() && belongsToCustomer(lrp.get(), customerCode)) {
                        return BookingViewLookupResult.success(lrp.get());
                    }
                }
                return BookingViewLookupResult.unprocessable(
                        bookingStatusValidationService.buildViewStatusMismatchError(obn, ecomStatus));
            } else {
                return BookingViewLookupResult.unprocessable(
                        bookingStatusValidationService.buildViewStatusMismatchError(obn, ecomStatus));
            }
        }

        if (bkn != null) {
            Optional<BookingRequestDTO> lrp = findFromDocumentationDatabase(bkn);
            return (lrp.isPresent() && belongsToCustomer(lrp.get(), customerCode))
                    ? BookingViewLookupResult.success(lrp.get())
                    : BookingViewLookupResult.notFound("bookingNumber", bkn);
        }

        return BookingViewLookupResult.notFound();
    }

    /**
     * Authorization check for LRP (Documentation) bookings: the caller's {@code customerCode}
     * (from JWT) must match the Agreement Party (AP) or Booking Party (BP) on the booking.
     * A blank {@code customerCode} disables the check (treated as no filter, consistent with
     * {@link #matchesCustomer}).
     */
    private boolean belongsToCustomer(BookingRequestDTO dto, String customerCode) {
        if (customerCode == null || customerCode.trim().isEmpty()) {
            return true;
        }
        if (dto == null || dto.getCustomers() == null) {
            return false;
        }
        String code = customerCode.trim();
        return dto.getCustomers().stream()
                .filter(c -> c.getCustomerType() == CustomerType.AP
                        || c.getCustomerType() == CustomerType.BP)
                .anyMatch(c -> code.equalsIgnoreCase(safe(c.getCustomerCode())));
    }

    private static String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    /**
     * Find booking from ECOM database (original logic).
     */
    private Optional<BookingRequestDTO> findFromEcomDatabase(String customerCode, String onlineBookNo,
                                                              String bookingNumber, String status) {
        // Find the main booking entity
        Optional<EcomBooking> bookingOpt = findBookingEntity(customerCode, onlineBookNo, bookingNumber, status);
        
        if (!bookingOpt.isPresent()) {
            return Optional.empty();
        }

        EcomBooking booking = bookingOpt.get();
        String ebookno = booking.getEbookno();
        if (ebookno == null || ebookno.trim().isEmpty()) {
            logger.debug("ECOM booking entity has null or blank ebookno, treating as not found");
            return Optional.empty();
        }

        // Build the complete DTO
        BookingRequestDTO dto = new BookingRequestDTO();
        
        // Map master booking fields
        mapMasterFields(booking, dto);
        
        // Fetch and map related entities (single load of ecombooking_cargo for containers + commodity line weights)
        List<EcomBookingContainer> containerEntities = containerRepo.findByEbooknoOrderByConid(ebookno);
        dto.setCustomers(fetchCustomers(ebookno));
        dto.setContainers(fetchContainers(ebookno, containerEntities));
        dto.setCommodities(fetchCommodities(ebookno));
        dto.setRoutes(fetchRoutes(ebookno));
        dto.setFiles(fetchFiles(ebookno, booking.getExtBookNo()));
        
        // Calculate TEUs (ecombooking_cargo + ecom_equipments logic)
        if (teusCalculationService != null) {
            dto.setTeus(teusCalculationService.calculateTeus(ebookno));
        }
        
        return Optional.of(dto);
    }

    /**
     * Find booking from Documentation database using booking-common entities.
     */
    private Optional<BookingRequestDTO> findFromDocumentationDatabase(String bookingNumber) {
        if (documentationBookingViewService == null) {
            logger.warn("DocumentationBookingViewService not available, cannot search Documentation database");
            return Optional.empty();
        }
        
        return documentationBookingViewService.findByBookingNumber(bookingNumber);
    }

    /**
     * Find the booking entity based on search criteria
     */
    private Optional<EcomBooking> findBookingEntity(String customerCode, String onlineBookNo,
                                                     String bookingNumber, String status) {
        // Try to find by onlineBookNo first (most specific)
        if (onlineBookNo != null && !onlineBookNo.trim().isEmpty()) {
            Optional<EcomBooking> booking = bookingRepo.findByEbookno(onlineBookNo.trim());
            if (booking.isPresent() && matchesCustomer(booking.get(), customerCode)) {
                return booking;
            }
            // If onlineBookNo was provided but not found, return empty - don't fall through
            // This prevents returning a random booking when a specific one was requested
            return Optional.empty();
        }

        // Only use fallback search when onlineBookNo was NOT provided
        // This searches by bookingNumber and/or status
        List<EcomBooking> allBookings = bookingRepo.findAll();
        
        return allBookings.stream()
                .filter(b -> matchesCustomer(b, customerCode))
                .filter(b -> bookingNumber == null || bookingNumber.trim().isEmpty() 
                        || bookingNumber.trim().equalsIgnoreCase(safe(b.getExtBookNo())))
                .filter(b -> status == null || status.trim().isEmpty() 
                        || status.trim().equalsIgnoreCase(safe(b.getEbookStatus())))
                .findFirst();
    }

    /**
     * Check if booking belongs to the customer
     */
    private boolean matchesCustomer(EcomBooking booking, String customerCode) {
        if (customerCode == null || customerCode.trim().isEmpty()) {
            return true;
        }
        return customerCode.equalsIgnoreCase(safe(booking.getCustCode()));
    }

    /**
     * Map master booking fields to DTO
     */
    private void mapMasterFields(EcomBooking entity, BookingRequestDTO dto) {
        dto.setOnlineBookNo(entity.getEbookno());
        dto.setAgencyId(entity.getAgencyid());
        dto.setOriginPortId(entity.getOriginPortId());
        dto.setFinalPortId(entity.getFinalPortId());
        dto.setLoadPortId(entity.getLoadPortId());
        dto.setDischargePortId(entity.getDischargePortId());
        dto.setBookDate(entity.getBookDate());
        dto.setCargoReadyDate(entity.getBookReady() != null ? entity.getBookReady().toLocalDate() : null);
        dto.setPreCarriage(entity.getPreCarriage() != null ? CarriageType.fromValue(entity.getPreCarriage()) : null);
        dto.setOnCarriage(entity.getOnCarriage() != null ? CarriageType.fromValue(entity.getOnCarriage()) : null);
        dto.setRemarks(entity.getRemarks());
        dto.setStatus(safeBookingStatus(entity.getEbookStatus()));
        dto.setBookingNumber(entity.getExtBookNo());
        dto.setCustomerCode(entity.getCustCode());
        dto.setTemplateName(entity.getTemplatename());
        dto.setRateRefNo(entity.getRateno());
        dto.setSubmittedDate(entity.getSubmittedDate());
        dto.setDatecr(entity.getDatecr());
        dto.setDateup(entity.getDateup());
        dto.setRouteId(entity.getRouteId() != null ? entity.getRouteId().intValue() : null);
        dto.setAgencyRefNo(entity.getAgencyRefNo());
        dto.setOceanFreight(entity.getOceanfreight() != null ? FreightPaymentType.fromValue(entity.getOceanfreight()) : null);
        dto.setIsAmendment(entity.getIsAmendment() != null ? YesNoFlag.fromValue(entity.getIsAmendment()) : null);
        dto.setExportRefNo(entity.getExportRef());
        dto.setCustomerPO(entity.getCustPO());
        dto.setEnsFilling(entity.getEnsfiling() != null ? EnsFillingType.fromValue(entity.getEnsfiling()) : null);
        dto.setT2lFilling(entity.getT2lfiling() != null ? YesNoFlag.fromValue(entity.getT2lfiling()) : null);
        dto.setEnsType(entity.getEnsType() != null ? EnsType.fromValue(entity.getEnsType()) : null);
        dto.setCreatedBy(entity.getUsercr());
        // confirmedAt is null for ECOM bookings (only applicable for LRP bookings)

        // Booking source (read-only): ECOM database has no source column, and D/S/T/V bookings
        // are always ECOM-originated, so this is a hardcoded default rather than a column read.
        dto.setBookingSource(BookingSource.C);
    }

    /**
     * Fetch customers for a booking
     */
    private List<CustomerDTO> fetchCustomers(String ebookno) {
        List<EcomBookingCustomer> entities = customerRepo.findByEbooknoOrderByCid(ebookno);
        List<CustomerDTO> dtos = new ArrayList<>();
        
        for (EcomBookingCustomer entity : entities) {
            CustomerDTO dto = new CustomerDTO();
            dto.setCustomerType(entity.getCustTypeId() != null ? CustomerType.fromValue(entity.getCustTypeId()) : null);
            dto.setCustomerCode(entity.getCustId());
            dto.setCountryId(entity.getCountryId());
            dto.setCustomerName(entity.getName());
            dto.setAddress(entity.getAddress1());
            dto.setAddress1(entity.getAddress2());
            dto.setCity(entity.getCity());
            dto.setZipCode(entity.getZipCode());
            dto.setTelephone(entity.getTelephone());
            dto.setEmail(entity.getEmail());
            dtos.add(dto);
        }
        
        return dtos;
    }

    /**
     * Fetch containers with reefer settings for a booking
     */
    private List<ContainerDTO> fetchContainers(String ebookno) {
        return fetchContainers(ebookno, containerRepo.findByEbooknoOrderByConid(ebookno));
    }

    /**
     * Map pre-loaded {@code ecombooking_cargo} rows to container DTOs (avoids duplicate query when commodities need cargo weights).
     */
    private List<ContainerDTO> fetchContainers(String ebookno, List<EcomBookingContainer> entities) {
        List<ContainerDTO> dtos = new ArrayList<>();
        
        for (EcomBookingContainer entity : entities) {
            ContainerDTO dto = new ContainerDTO();
            dto.setCargoNo(entity.getConid());
            dto.setOogFlag(entity.getOog() != null ? YesNoFlag.fromValue(entity.getOog()) : null);
            dto.setEquipmentQty(entity.getQuantity());
            dto.setEquipmentType(entity.getEqpType() != null ? EquipmentType.fromValue(entity.getEqpType()) : null);
            dto.setEquipmentSize(entity.getEqpSize());
            dto.setSocFlag(entity.getSoc() != null ? YesNoFlag.fromValue(entity.getSoc()) : null);
            dto.setDgFlag(entity.getDgFlag() != null ? YesNoFlag.fromValue(entity.getDgFlag()) : null);
            dto.setEquipmentStatus(entity.getEqpstatus() != null ? EquipmentStatus.fromValue(entity.getEqpstatus()) : null);
            dto.setCargoWeight(entity.getCargoWeight() != null ? entity.getCargoWeight().intValue() : null);
            dto.setGrossWeight(entity.getGrossWeight());
            dto.setTareWeight(entity.getTareWeight());
            dto.setWeightUnit(entity.getWeightUnit() != null ? WeightUnit.fromValue(entity.getWeightUnit()) : null);
            dto.setNor(entity.getRefNor() != null ? YesNoFlag.fromValue(entity.getRefNor()) : null);
            dto.setContainerNo(entity.getContainerno());
            
            // Fetch reefer settings if equipmentType is R
            if ("R".equalsIgnoreCase(entity.getEqpType())) {
                ReeferSettingDTO reefer = fetchReeferForContainer(ebookno, entity.getConid(), entity.getContainerno());
                dto.setReefer(reefer);
            }
            
            // Fetch OOG dimensions if oogFlag is Y
            if ("Y".equalsIgnoreCase(entity.getOog())) {
                fetchOogForContainer(ebookno, entity.getConid(), dto);
            }
            
            dtos.add(dto);
        }
        
        return dtos;
    }

    /**
     * Fetch OOG dimensions for a specific container and populate into DTO
     */
    private void fetchOogForContainer(String ebookno, Integer conid, ContainerDTO dto) {
        List<EcomBookingOog> oogList = oogRepo.findByEbooknoAndConid(ebookno, conid);
        if (!oogList.isEmpty()) {
            EcomBookingOog oog = oogList.get(0);
            dto.setOogLeftFt(oog.getLeftft());
            dto.setOogRightFt(oog.getRightft());
            dto.setOogForward(oog.getForward());
            dto.setOogAft(oog.getAlt());
            dto.setOogTopFt(oog.getTopft());
            dto.setOogUnit(safeLengthUnit(oog.getUnit()));
        }
    }

    /**
     * Fetch reefer settings for a specific container
     */
    private ReeferSettingDTO fetchReeferForContainer(String ebookno, Integer conid, String containerNo) {
        Optional<EcomBookingReefer> reeferOpt = reeferRepo.findByEbooknoAndConid(ebookno, conid);
        return reeferOpt.map(com.solverminds.booking.util.ReeferMappingHelper::toViewDto).orElse(null);
    }

    /**
     * Fetch commodities with dangerous goods for a booking.
     * {@code itemWeight} / {@code itemWeightUnit} are mapped from {@code ecombooking_commodity}.
     */
    private List<CommodityDTO> fetchCommodities(String ebookno) {
        List<EcomBookingCommodity> entities = commodityRepo.findByEbooknoOrderByConidAscItemnoAsc(ebookno);
        List<CommodityDTO> dtos = new ArrayList<>();
        
        for (EcomBookingCommodity entity : entities) {
            CommodityDTO dto = new CommodityDTO();
            dto.setCargoNo(entity.getConid());
            dto.setContainerNo(entity.getContainerno());
            dto.setItemNo(entity.getItemno());
            dto.setCommodityGroup(entity.getCommodity()); // String preserves leading zeros e.g. "09"
            dto.setHsCode(entity.getHsCommodity());
            dto.setPackageType(entity.getPackageType());
            dto.setPackageQty(entity.getPackageQty());
            dto.setCargoVolume(entity.getCargoVolume() != null ? entity.getCargoVolume().doubleValue() : null);
            dto.setVolumnUnit(VolumeUnit.fromValue(entity.getVolumnUnit()));
            dto.setNettwt(entity.getNettwt() != null ? entity.getNettwt() : null);
            dto.setItemWeight(entity.getItemWeight());
            dto.setItemWeightUnit(safeWeightUnit(entity.getItemWeightUnit()));
            dto.setBuyerCode(entity.getBycode());
            dto.setBuyerName(entity.getByname());
            dto.setSellerCode(entity.getSlcode());
            dto.setSellerName(entity.getSlname());
            dto.setCommodityHBLNo(entity.getCmdtyHblno());
            
            // Fetch dangerous goods for this commodity
            List<DangerousGoodsDTO> dgRows = fetchDangerousGoodsForCommodity(
                    ebookno, entity.getConid(), entity.getItemno(), entity.getContainerno());
            if (dgRows != null && !dgRows.isEmpty()) {
                com.solverminds.booking.dto.v2.DangerousGoodsInfoV2 dgi =
                        bookingMapperV2.toV2DangerousGoods(dgRows.get(0));
                // Re-enrich packaging names / IMDG read-only if staging row is thin
                if (dgi != null && dangerousGoodsEnrichmentService != null) {
                    dangerousGoodsEnrichmentService.enrich(dgi);
                }
                dto.setDangerousGoods(dgi);
            }
            
            dtos.add(dto);
        }
        
        return dtos;
    }

    /**
     * Fetch dangerous goods for a specific commodity
     */
    private List<DangerousGoodsDTO> fetchDangerousGoodsForCommodity(String ebookno, Integer conid, 
                                                                      Integer itemNo, String containerNo) {
        List<EcomBookingDg> entities = dgRepo.findByEbooknoAndConidAndItemno(ebookno, conid, itemNo);
        List<DangerousGoodsDTO> dtos = new ArrayList<>();
        
        for (EcomBookingDg entity : entities) {
            DangerousGoodsDTO dto = dangerousGoodsMapper.fromEntity(entity);
            if (containerNo != null && !containerNo.trim().isEmpty()) {
                dto.setContainerNo(containerNo);
            }
            dtos.add(dto);
        }
        
        return dtos;
    }

    /**
     * Fetch routes for a booking
     */
    private List<RouteDTO> fetchRoutes(String ebookno) {
        List<EcomBookingRoute> entities = routeRepo.findByEbooknoOrderByLegNo(ebookno);
        List<RouteDTO> dtos = new ArrayList<>();
        
        for (EcomBookingRoute entity : entities) {
            RouteDTO dto = new RouteDTO();
            dto.setLegNo(entity.getLegNo() != null ? entity.getLegNo().intValue() : null);
            dto.setLoadBound(Bound.fromValue(entity.getBound()));
            dto.setServiceCode(entity.getServiceCode());
            dto.setVesselCode(entity.getVesselCode());
            dto.setVoyageCode(entity.getVoyageCode());
            dto.setServiceType(TransportMode.fromValue(entity.getLegTypeId()));
            dto.setLoadPortId(entity.getLoadPortId());
            dto.setLoadTerminal(entity.getLoadTerminal());
            dto.setDischargePortId(entity.getDischargePortId());
            dto.setDischargeTerminal(entity.getDischargeTerminal());
            OffsetDateTime etdOdt = entity.getEtdDate() != null ? entity.getEtdDate().toLocalDateTime().atOffset(ZoneOffset.UTC) : null;
            OffsetDateTime etaOdt = entity.getEtaDate() != null ? entity.getEtaDate().toLocalDateTime().atOffset(ZoneOffset.UTC) : null;
            String tzEtd = masterDataCacheService != null ? PortLocalTimeUtil.normalizeTimezoneValue(masterDataCacheService.getTimezoneOffset(entity.getLoadPortId())) : null;
            String tzEta = masterDataCacheService != null ? PortLocalTimeUtil.normalizeTimezoneValue(masterDataCacheService.getTimezoneOffset(entity.getDischargePortId())) : null;
            dto.setEtdDate(etdOdt != null ? PortLocalTimeUtil.labelWithOffset(etdOdt, tzEtd) : null);
            dto.setEtaDate(etaOdt != null ? PortLocalTimeUtil.labelWithOffset(etaOdt, tzEta) : null);
            dto.setLoadType(PortCallType.fromValue(entity.getLoadType()));
            dto.setDischType(PortCallType.fromValue(entity.getDischType()));
            dto.setGroupCallId(entity.getGroupcallid());
            dto.setDischargeCallId(entity.getCallIdDischarge());
            dto.setLoadCallId(entity.getCallIdLoading());
            dto.setDischargeBound(Bound.fromValue(entity.getDbound()));
            dtos.add(dto);
        }
        
        return dtos;
    }

    /**
     * Fetch file attachments for a booking from document folder (same as documents list endpoint).
     * Tries extBookNo (BKG folder) first when available, then ebookno (Ecom folder).
     */
    private List<FileAttachmentDTO> fetchFiles(String ebookno, String extBookNo) {
        if (documentService == null) {
            return new ArrayList<>();
        }
        List<com.solverminds.booking.dto.DocumentListResponse> docs = new ArrayList<>();
        // Try extBookNo first (BKG folder) - documents often stored by confirmed ID
        if (extBookNo != null && !extBookNo.trim().isEmpty()) {
            docs = documentService.listDocumentsForBooking(extBookNo.trim());
        }
        // Fallback to ebookno (Ecom folder) when extBookNo empty or no docs found
        if (docs.isEmpty() && ebookno != null && !ebookno.trim().isEmpty()) {
            docs = documentService.listDocumentsForBooking(ebookno.trim());
        }
        List<FileAttachmentDTO> result = new ArrayList<>();
        for (com.solverminds.booking.dto.DocumentListResponse doc : docs) {
            if (doc.getFileName() != null && !doc.getFileName().trim().isEmpty()) {
                result.add(new FileAttachmentDTO("DOC", doc.getFileName(),
                    doc.getSizeBytes() != null ? doc.getSizeBytes() : 0L));
            }
        }
        return result;
    }

    /**
     * Safe string helper
     */
    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    /**
     * Safe integer parsing helper
     */
    private Integer parseIntSafe(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Safe YesNoFlag conversion
     */
    private YesNoFlag safeYesNoFlag(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return YesNoFlag.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Safe LengthUnit conversion
     */
    private LengthUnit safeLengthUnit(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return LengthUnit.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private WeightUnit safeWeightUnit(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return WeightUnit.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
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

