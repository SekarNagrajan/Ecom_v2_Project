package com.solverminds.booking.service;

import com.solverminds.booking.dto.*;
import com.solverminds.booking.dto.v2.DangerousGoodsInfoV2;
import com.solverminds.booking.repo.*;
import com.solverminds.edi.entity.*;
import com.svm.ecom.common.enums.WeightUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

import com.svm.ecom.common.util.PortLocalTimeUtil;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Service to export booking data from DTOs to EDI temp tables.
 * Based on VDS (Vessel Data Service) insert queries and default values.
 * 
 * Mapping: BookingRequestDTO and child DTOs → EDI temp tables
 * - BookingRequestDTO → editempbooking
 * - CustomerDTO → editempbooking_cust
 * - ContainerDTO → editempbooking_cargo
 * - CommodityDTO → editempbooking_commodity
 * - RouteDTO → editempbooking_routing, editempbooking_leg
 * - References → editempbooking_reference
 * - Remarks → editempbooking_remarks
 */
@Service
@Transactional(transactionManager = "ediTransactionManager")
public class EdiExportService {

    private static final Logger logger = LoggerFactory.getLogger(EdiExportService.class);

    // VDS Default Constants
    private static final String DEFAULT_BOOK_SOURCE = "C"; // C = ECOM API
    private static final String DEFAULT_CARRIER_CODE = "TSH"; // Default carrier
    private static final String DEFAULT_MESSAGE_SENDER = "EcomOnlineBkg";
    private static final String DEFAULT_BOOK_TYPE = "online"; // Normal
    private static final String DEFAULT_TRANS_TYPE = "9"; // VDS uses "9" for new bookings
    private static final String RESUBMIT_TRANS_TYPE = "5"; // TransType "5" for resubmit/amendments
    private static final String DEFAULT_MESSAGE_TYPE = "IFTMBF"; // Booking confirmation

    // EDI Repositories
    private final EdiTempBookingRepository ediTempBookingRepo;
    private final EdiTempBookingCargoRepository ediTempBookingCargoRepo;
    private final EdiTempBookingCommodityRepository ediTempBookingCommodityRepo;
    private final EdiTempBookingCustRepository ediTempBookingCustRepo;
    private final EdiTempBookingRoutingRepository ediTempBookingRoutingRepo;
    private final EdiTempBookingLegRepository ediTempBookingLegRepo;
    private final EdiTempBookingReferenceRepository ediTempBookingReferenceRepo;
    private final EdiTempBookingRemarksRepository ediTempBookingRemarksRepo;
    private final EdiTempBookingConfRepository ediTempBookingConfRepo;
    private final EdiDocStatusRepository ediDocStatusRepo;
    private final EdiReaderMasterRepository ediReaderMasterRepo;

    // Service for EDI Reader ID generation (uses ECOM transaction)
    private final ModuleIndexService moduleIndexService;

    // Repository for agency lookups
    private final AgencyLocationRepository agencyLocationRepository;

    // Repository for equipment tare weight lookups
    private final EquipmentRepository equipmentRepository;

    // Repository for routing detail lookups
    private final RoutingDetailRepository routingDetailRepository;

    // Cache service for port names (used for originPortDesc, finalPortDesc)
    private final MasterDataCacheService masterDataCacheService;

    public EdiExportService(
            EdiTempBookingRepository ediTempBookingRepo,
            EdiTempBookingCargoRepository ediTempBookingCargoRepo,
            EdiTempBookingCommodityRepository ediTempBookingCommodityRepo,
            EdiTempBookingCustRepository ediTempBookingCustRepo,
            EdiTempBookingRoutingRepository ediTempBookingRoutingRepo,
            EdiTempBookingLegRepository ediTempBookingLegRepo,
            EdiTempBookingReferenceRepository ediTempBookingReferenceRepo,
            EdiTempBookingRemarksRepository ediTempBookingRemarksRepo,
            EdiTempBookingConfRepository ediTempBookingConfRepo,
            EdiDocStatusRepository ediDocStatusRepo,
            EdiReaderMasterRepository ediReaderMasterRepo,
            ModuleIndexService moduleIndexService,
            AgencyLocationRepository agencyLocationRepository,
            EquipmentRepository equipmentRepository,
            RoutingDetailRepository routingDetailRepository,
            MasterDataCacheService masterDataCacheService) {
        this.ediTempBookingRepo = ediTempBookingRepo;
        this.ediTempBookingCargoRepo = ediTempBookingCargoRepo;
        this.ediTempBookingCommodityRepo = ediTempBookingCommodityRepo;
        this.ediTempBookingCustRepo = ediTempBookingCustRepo;
        this.ediTempBookingRoutingRepo = ediTempBookingRoutingRepo;
        this.ediTempBookingLegRepo = ediTempBookingLegRepo;
        this.ediTempBookingReferenceRepo = ediTempBookingReferenceRepo;
        this.ediTempBookingRemarksRepo = ediTempBookingRemarksRepo;
        this.ediTempBookingConfRepo = ediTempBookingConfRepo;
        this.ediDocStatusRepo = ediDocStatusRepo;
        this.ediReaderMasterRepo = ediReaderMasterRepo;
        this.moduleIndexService = moduleIndexService;
        this.agencyLocationRepository = agencyLocationRepository;
        this.equipmentRepository = equipmentRepository;
        this.routingDetailRepository = routingDetailRepository;
        this.masterDataCacheService = masterDataCacheService;
    }

    /**
     * Export booking DTO to EDI temp tables (VDS-compatible)
     * 
     * @param bookNo           The booking number
     * @param dto              The booking request DTO
     * @param loginNameFromJwt Login name from JWT for audit
     * @return Map containing success status and details
     */
    public Map<String, Object> exportToEdiTempTables(String bookNo, BookingRequestDTO dto,
            String loginNameFromJwt) {
        return exportToEdiTempTables(bookNo, dto, loginNameFromJwt, null, false);
    }

    /**
     * Export booking data to EDI temp tables (VDS-compatible format).
     * Overloaded version with extBookNo for amendments.
     * 
     * @param bookNo           The booking number (ebookno)
     * @param dto              The booking request DTO
     * @param loginNameFromJwt Login name from JWT for audit
     * @param extBookNo        The ERP booking number (for amendments), null for initial submit
     * @return Map containing success status and details
     */
    public Map<String, Object> exportToEdiTempTables(String bookNo, BookingRequestDTO dto,
            String loginNameFromJwt, String extBookNo) {
        return exportToEdiTempTables(bookNo, dto, loginNameFromJwt, extBookNo, false);
    }

    /**
     * Export booking data to EDI temp tables (VDS-compatible format).
     * Full version with all parameters.
     * 
     * @param bookNo           The booking number (ebookno)
     * @param dto              The booking request DTO
     * @param loginNameFromJwt Login name from JWT for audit
     * @param extBookNo        The ERP booking number (for amendments), null for initial submit
     * @param isResubmit       True for S→S or P→S resubmit (uses transtype=5)
     * @return Map containing success status and details
     */
    public Map<String, Object> exportToEdiTempTables(String bookNo, BookingRequestDTO dto,
            String loginNameFromJwt, String extBookNo, boolean isResubmit) {
        Map<String, Object> result = new HashMap<>();

        try {
            logger.info("Exporting booking {} to EDI temp tables (VDS-compatible)", bookNo);

            // Generate EDI reader ID for this session
            String ediReaderId = generateEdiReaderId();
            String agencyCode = dto.getAgencyId();
            
            // Fetch routing detail from routedetail table based on routeId
            RoutingDetailRepository.RoutingDetail routingDetail = null;
            if (dto.getRouteId() != null) {
                Optional<RoutingDetailRepository.RoutingDetail> routingDetailOpt = 
                    routingDetailRepository.getRoutingDetailByRouteId(dto.getRouteId());
                if (routingDetailOpt.isPresent()) {
                    routingDetail = routingDetailOpt.get();
                    logger.debug("Fetched routing detail for routeId: {}", dto.getRouteId());
                } else {
                    logger.warn("No routing detail found for routeId: {}", dto.getRouteId());
                }
            }

            // 1. Create main EDI temp booking
            EdiTempBooking ediBooking = createEdiTempBookingFromDto(bookNo, dto, agencyCode,
                    loginNameFromJwt, ediReaderId, dto.getRoutes(), routingDetail, extBookNo, isResubmit);
            ediTempBookingRepo.save(ediBooking);
            logger.debug("Created EDI temp booking: {}", bookNo);

            // 2. Create customer records
            if (dto.getCustomers() != null && !dto.getCustomers().isEmpty()) {
                for (CustomerDTO customer : dto.getCustomers()) {
                    EdiTempBookingCust ediCust = createEdiTempCustFromDto(bookNo, customer, ediReaderId);
                    ediTempBookingCustRepo.save(ediCust);
                }
                logger.debug("Created {} EDI temp customer records for booking {}",
                        dto.getCustomers().size(), bookNo);
            }

            // 3. Expand containers based on equipmentQty and create cargo/container records
            // Expanded containers list tracks: original cargoNo -> list of generated containerNos
            Map<Integer, List<String>> cargoNoToExpandedContainers = new HashMap<>();
            List<ExpandedContainer> expandedContainers = new ArrayList<>();
            
            if (dto.getContainers() != null && !dto.getContainers().isEmpty()) {
                // Bulk fetch tare weights for all unique equipment sizes (single DB query)
                Map<String, Float> tareWeightMap = getTareWeightsForContainers(dto.getContainers());
                logger.debug("Bulk fetched tare weights for {} equipment types", tareWeightMap.size());

                int globalCargoNo = 1; // Global cargoNo sequence for EDI
                // Track sequence per equipment size for container number generation
                // Format: {equipmentSize}TBN{seq} (e.g., 20DCTBN1, 20DCTBN2, 40DCTBN1, 40DCTBN2)
                Map<String, Integer> equipmentSizeSeqMap = new HashMap<>();
                
                for (ContainerDTO container : dto.getContainers()) {
                    int qty = (container.getEquipmentQty() != null && container.getEquipmentQty() > 0) 
                              ? container.getEquipmentQty() : 1;
                    
                    List<String> expandedContainerNos = new ArrayList<>();
                    String equipmentSize = container.getEquipmentSize() != null ? container.getEquipmentSize().trim() : "CONT";
                    
                    for (int i = 0; i < qty; i++) {
                        // Get and increment sequence for this equipment size
                        int sizeSeq = equipmentSizeSeqMap.getOrDefault(equipmentSize, 0) + 1;
                        equipmentSizeSeqMap.put(equipmentSize, sizeSeq);
                        
                        // Generate container number: {equipmentSize}TBN{seq} (e.g., 20DCTBN1, 40DCTBN2)
                        String generatedContainerNo = generateContainerNumber(
                                equipmentSize, 
                                container.getContainerNo(), 
                                sizeSeq);
                        
                        // Create expanded container record
                        EdiTempBookingCargo ediCargo = createEdiTempCargoFromDtoExpanded(
                                bookNo, container, ediReaderId, tareWeightMap, 
                                globalCargoNo, generatedContainerNo);
                        ediTempBookingCargoRepo.save(ediCargo);
                        
                        expandedContainerNos.add(generatedContainerNo);
                        expandedContainers.add(new ExpandedContainer(
                                container.getCargoNo(), 
                                generatedContainerNo, 
                                globalCargoNo,
                                container));
                        
                        globalCargoNo++;
                    }
                    
                    // Track cargoNo -> expanded container numbers mapping
                    cargoNoToExpandedContainers.put(container.getCargoNo(), expandedContainerNos);
                }
                
                logger.debug("Created {} EDI temp cargo records (expanded from {} containers) for booking {}",
                        expandedContainers.size(), dto.getContainers().size(), bookNo);
            }

            // 4. Create commodity records - replicate for each expanded container
            if (dto.getCommodities() != null && !dto.getCommodities().isEmpty()) {
                int commodityCount = 0;
                
                // Group commodities by container number to assign itemNo starting from 1 per container
                Map<String, List<CommodityDTO>> containerToCommodities = new LinkedHashMap<>();
                List<CommodityDTO> fallbackCommodities = new ArrayList<>();
                
                for (CommodityDTO commodity : dto.getCommodities()) {
                    // Find expanded containers for this commodity's cargoNo
                    List<String> expandedContainerNos = cargoNoToExpandedContainers.get(commodity.getCargoNo());
                    
                    if (expandedContainerNos != null && !expandedContainerNos.isEmpty()) {
                        // Group this commodity under each expanded container
                        for (String expandedContainerNo : expandedContainerNos) {
                            containerToCommodities
                                .computeIfAbsent(expandedContainerNo, k -> new ArrayList<>())
                                .add(commodity);
                        }
                    } else {
                        // Collect commodities without expansion for fallback processing
                        fallbackCommodities.add(commodity);
                    }
                }
                
                // Process commodities grouped by container (itemNo starts from 1 per container)
                for (Map.Entry<String, List<CommodityDTO>> entry : containerToCommodities.entrySet()) {
                    String expandedContainerNo = entry.getKey();
                    List<CommodityDTO> commoditiesForContainer = entry.getValue();
                    
                    int itemNo = 1; // Start itemNo from 1 for each container
                    for (CommodityDTO commodity : commoditiesForContainer) {
                        EdiTempBookingCommodity ediCommodity = createEdiTempCommodityFromDtoExpanded(
                                bookNo, commodity, ediReaderId, expandedContainerNo, itemNo,
                                findExpandedContainer(expandedContainers, expandedContainerNo));
                        ediTempBookingCommodityRepo.save(ediCommodity);
                        commodityCount++;
                        itemNo++;
                    }
                }
                
                // Process fallback commodities (no expansion)
                for (CommodityDTO commodity : fallbackCommodities) {
                    EdiTempBookingCommodity ediCommodity = createEdiTempCommodityFromDto(bookNo, commodity,
                            ediReaderId, dto.getContainers());
                    ediTempBookingCommodityRepo.save(ediCommodity);
                    commodityCount++;
                }
                
                logger.debug("Created {} EDI temp commodity records (expanded) for booking {}",
                        commodityCount, bookNo);

                // 4a. Create DG (Dangerous Goods) remarks records if any commodities have DG info
                // Use expanded container numbers for DG remarks
                createDgRemarksRecordsExpanded(bookNo, dto.getCommodities(), cargoNoToExpandedContainers, ediReaderId);
            }

            // 5. Create routing record - use routing detail from routedetail table
            if (dto.getRouteId() != null && routingDetail != null) {
                EdiTempBookingRouting ediRouting = createEdiTempRoutingFromDto(bookNo, dto, routingDetail, ediReaderId);
                ediTempBookingRoutingRepo.save(ediRouting);
                logger.debug("Created EDI temp routing record for booking {} from routing detail", bookNo);
            } else {
                logger.warn("Cannot create routing record: routeId={}, routingDetail={}", 
                    dto.getRouteId(), routingDetail != null);
            }

            // 6. Create leg records for each route
            if (dto.getRoutes() != null && !dto.getRoutes().isEmpty()) {
                short legNo = 1;
                Short prevLeg = (short) 0;
                for (RouteDTO route : dto.getRoutes()) {
                    EdiTempBookingLeg ediLeg = createEdiTempLegFromDto(bookNo, route, dto, legNo, prevLeg, ediReaderId);
                    ediTempBookingLegRepo.save(ediLeg);
                    prevLeg = legNo;
                    legNo++;
                }
                logger.debug("Created {} EDI temp leg records for booking {}",
                        dto.getRoutes().size(), bookNo);
            }

            // 7. Create reference records
            createReferenceRecords(bookNo, dto, ediReaderId);
            logger.debug("Created EDI temp reference records for booking {}", bookNo);

            // 8. Create remarks record if remarks present
            if (dto.getRemarks() != null && !dto.getRemarks().trim().isEmpty()) {
                EdiTempBookingRemarks ediRemarks = createEdiTempRemarksFromDto(bookNo, dto, ediReaderId);
                ediTempBookingRemarksRepo.save(ediRemarks);
                logger.debug("Created EDI temp remarks record for booking {}", bookNo);
            }

            // 9. Create conf record (cutoff dates placeholder)
            EdiTempBookingConf ediConf = createEdiTempConfFromDto(bookNo, ediReaderId);
            ediTempBookingConfRepo.save(ediConf);
            logger.debug("Created EDI temp conf record for booking {}", bookNo);

            // 10. Create edidocstatus record to track document status
            EdiDocStatus ediDocStatus = createEdiDocStatusFromDto(bookNo, ediReaderId, dto, isResubmit, extBookNo);
            ediDocStatusRepo.save(ediDocStatus);
            logger.debug("Created EDI doc status record for booking {}", bookNo);

            // 11. Create edireadermaster record to track EDI processing
            // Using native insert with IDENTITY_INSERT ON to insert explicit edireaderid
            // value
            LocalDateTime now = LocalDateTime.now();
            // Get corporate agency code from agencymatrix using local agency
            String corpAgencyCode = agencyLocationRepository.getCorpAgencyByLocAgency(dto.getAgencyId());
            EdiReaderMaster ediReaderMaster = createEdiReaderMasterFromDto(ediReaderId, dto, loginNameFromJwt,
                    corpAgencyCode);
            ediReaderMasterRepo.insertWithIdentity(
                    ediReaderId,
                    "EDI109",
                    ediReaderMaster.getAgencycode(),
                    agencyCode,
                    now,
                    "Completed",
                    now,
                    "TP001");
            logger.debug("Created EDI reader master record for edireaderid {}", ediReaderId);

            result.put("success", true);
            result.put("bookNo", bookNo);
            result.put("ediReaderId", ediReaderId);
            result.put("message", "Successfully exported booking to EDI temp tables");
            logger.info("Successfully exported booking {} to EDI temp tables with ediReaderId: {}", bookNo,
                    ediReaderId);

        } catch (Exception e) {
            logger.error("Error exporting booking {} to EDI temp tables: {}", bookNo, e.getMessage(), e);
            result.put("success", false);
            result.put("bookNo", bookNo);
            result.put("message", "Error exporting to EDI temp tables: " + e.getMessage());
        }

        return result;
    }

    /**
     * Upsert customer reference fields into existing EDI temp booking rows.
     *
     * Why: Booking update (PUT) can change customerPO/customerReference without triggering a full EDI export
     * (status transition). If an EDI temp booking already exists for this bookNo, we must ensure:
     * - dbo.editempbooking.custRefNo = customerPO
     * - dbo.editempbooking_reference has refid='SHR', refval=customerPO
     *
     * If there is no EDI temp booking for the given bookNo yet, this is a no-op.
     */
    @Transactional(transactionManager = "ediTransactionManager")
    public void upsertCustomerReferenceForExistingEdiTemp(String bookNo, String customerPO) {
        if (bookNo == null || bookNo.trim().isEmpty()) {
            return;
        }
        String normalizedBookNo = bookNo.trim();
        String normalizedCustomerPO = nullIfBlank(customerPO);
        if (normalizedCustomerPO == null) {
            return;
        }

        try {
            EdiTempBooking ediBooking = ediTempBookingRepo.findByBookNo(normalizedBookNo);
            if (ediBooking == null) {
                return; // No EDI temp booking exists yet; nothing to upsert.
            }

            // 1) Ensure custRefNo is populated
            ediBooking.setCustRefNo(normalizedCustomerPO);
            ediTempBookingRepo.save(ediBooking);

            // 2) Ensure SHR reference exists and is updated
            List<EdiTempBookingReference> shrRefs =
                    ediTempBookingReferenceRepo.findByBookNoAndRefid(normalizedBookNo, "SHR");

            if (shrRefs != null && !shrRefs.isEmpty()) {
                boolean updatedAny = false;
                for (EdiTempBookingReference r : shrRefs) {
                    if (r != null && (r.getRefval() == null || !normalizedCustomerPO.equals(r.getRefval()))) {
                        r.setRefval(normalizedCustomerPO);
                        if (r.getEdireaderid() == null || r.getEdireaderid().trim().isEmpty()) {
                            r.setEdireaderid(ediBooking.getEdireaderid());
                        }
                        ediTempBookingReferenceRepo.save(r);
                        updatedAny = true;
                    }
                }
                if (updatedAny) {
                    logger.debug("Updated existing SHR reference(s) for booking {}", normalizedBookNo);
                }
            } else {
                EdiTempBookingReference ref = new EdiTempBookingReference();
                ref.setBookNo(normalizedBookNo);
                ref.setRefid("SHR");
                ref.setRefval(normalizedCustomerPO);
                ref.setEdireaderid(ediBooking.getEdireaderid());
                ref.setIsedited("N");
                ref.setVersion((short) 0);
                ediTempBookingReferenceRepo.save(ref);
                logger.debug("Inserted SHR reference for booking {}", normalizedBookNo);
            }
        } catch (Exception e) {
            logger.warn("Failed to upsert customer reference into EDI temp tables for {}: {}", normalizedBookNo, e.getMessage());
        }
    }

    /**
     * Generate unique EDI reader ID using module index table
     * Delegates to ModuleIndexService which runs in ECOM transaction
     * 
     * @return Generated EDI reader ID in format: prefix + index (e.g., "EBKON1234")
     */
    private String generateEdiReaderId() {
        return moduleIndexService.generateEdiReaderId();
    }

    /**
     * Map BookingRequestDTO to EdiTempBooking entity (VDS-compatible)
     * 
     * @param bookNo The ECOM booking number (ebookno)
     * @param dto The booking request DTO
     * @param agencyCode The agency code
     * @param loginNameFromJwt Login name from JWT
     * @param ediReaderId EDI reader ID for this session
     * @param routes List of route DTOs
     * @param routingDetail Routing detail from routedetail table
     * @param extBookNo The ERP booking number (for amendments), null for initial submit
     */
    private EdiTempBooking createEdiTempBookingFromDto(String bookNo, BookingRequestDTO dto,
            String agencyCode, String loginNameFromJwt,
            String ediReaderId, List<RouteDTO> routes,
            RoutingDetailRepository.RoutingDetail routingDetail,
            String extBookNo, boolean isResubmit) {
        EdiTempBooking edi = new EdiTempBooking();
        LocalDateTime now = LocalDateTime.now();

        // Primary booking info
        edi.setBookNo(bookNo);
        edi.setAgencyid(agencyCode);
        edi.setAgencyRefNo(dto.getAgencyRefNo());
        // Customer PO / reference is stored on ecombooking.custPO only; do not map to editempbooking.custRefNo
        edi.setCustRefNo(null);

        // Port information
        edi.setOriginPortId(dto.getOriginPortId());
        edi.setOriginPortDesc(masterDataCacheService.getPortName(dto.getOriginPortId()));
        edi.setFinalPortId(dto.getFinalPortId());
        edi.setFinalPortDesc(masterDataCacheService.getPortName(dto.getFinalPortId()));
        edi.setLoadPortId(dto.getLoadPortId());
        edi.setDischargePortId(dto.getDischargePortId());

        // Determine legs for mapping based on service type
        // - Generic fields use first Feeder (F) / Mainline (M) leg
        // - Ocean fields use route matching routing main service code with service type "M"
        RouteDTO genericLeg = findFirstByServiceTypeOrFirst(routes);
        
        // Find ocean leg by matching with routing main service code from routing detail
        RouteDTO oceanLeg = findOceanLegByRoutingMainService(routes, routingDetail);
        
        // Ocean fields from mainline leg matching routing main service code
        if (oceanLeg != null) {
            edi.setOceanVslCode(oceanLeg.getVesselCode());
            edi.setOceanVoyage(oceanLeg.getVoyageCode());
            edi.setOceanSerCode(oceanLeg.getServiceCode());
            edi.setOceanBound(oceanLeg.getLoadBound().getValue());
        }

        // Generic service/vessel/voyage fields from feeder leg (or fallback)
        if (genericLeg != null) {
            edi.setServiceCode(genericLeg.getServiceCode());
            edi.setVesselCode(genericLeg.getVesselCode());
            edi.setVoyageCode(genericLeg.getVoyageCode());
            edi.setBound(genericLeg.getLoadBound().getValue());
            edi.setFirstSvcType(genericLeg.getServiceType().getValue()); // M=MainLine, F=Feeder
            edi.setPolAt(parseRouteDateToLocalDateTimeFromOdt(genericLeg.getEtdDate()));
            edi.setPodAt(parseRouteDateToLocalDateTimeFromOdt(genericLeg.getEtaDate()));
            edi.setCallId(genericLeg.getGroupCallId());
        }

        // Terminal information from routing detail (polTerminal and podTerminal)
        if (routingDetail != null) {
            edi.setPolTerminal(nullIfBlank(routingDetail.getPolTerminal()));
            edi.setPodTerminal(nullIfBlank(routingDetail.getPodTerminal()));
        }      

        // Dates
        edi.setBookDate(now); // VDS uses getdate()
        edi.setBookReady(dto.getCargoReadyDate() != null ? dto.getCargoReadyDate().atStartOfDay() : null);

        // Carriage flags
        edi.setPreCarriage(dto.getPreCarriage() != null ? dto.getPreCarriage().getValue() : null);
        edi.setOnCarriage(dto.getOnCarriage() != null ? dto.getOnCarriage().getValue() : null);

        // Remarks
        edi.setRemarks(""); // VDS default: empty string
        edi.setCustremarks(dto.getRemarks()); // Customer remarks

        // Status - VDS uses "D" (Draft) for new bookings
        edi.setStatus("D");
        edi.setActStatus("O"); // O = Open

        // Rate reference
        edi.setRaterefNo(dto.getRateRefNo());

        // ENS/T2L filing
        edi.setEnsfiling(dto.getEnsFilling() != null ? dto.getEnsFilling().getValue() : null);
        edi.setT2lfiling(dto.getT2lFilling() != null ? dto.getT2lFilling().getValue() : null);
        edi.setEnsType(dto.getEnsType() != null ? dto.getEnsType().getValue() : null);

        // Export reference
        edi.setExpref(dto.getExportRefNo());

        // Audit fields
        edi.setUsercr("ECOM");
        edi.setDatecr(now);

        // EDI fields
        edi.setEdireaderid(ediReaderId);
        edi.setMessagetype(DEFAULT_MESSAGE_TYPE);
        edi.setTranstype(isResubmit ? RESUBMIT_TRANS_TYPE : DEFAULT_TRANS_TYPE);

        // VDS Default values
        edi.setBooksource(DEFAULT_BOOK_SOURCE);
        edi.setMessagesender(DEFAULT_MESSAGE_SENDER);
        edi.setCarrierCode(DEFAULT_CARRIER_CODE);
        edi.setBookType(DEFAULT_BOOK_TYPE);
        edi.setPriority("M");
        edi.setShipmentType("NR");
        edi.setThirdParty("N");
        edi.setPrepareOPS("N");
        edi.setPrepareBL("N");
        edi.setCrossBook(""); // VDS default: empty string
        edi.setIsupdate("N");
        edi.setIsedited("N");
        edi.setIsMailSent("N");
        edi.setIsDGBook("N");
        edi.setTbookNo(extBookNo != null && !extBookNo.trim().isEmpty() ? extBookNo.trim() : ""); // ERP booking number for amendments
        edi.setFcnno(""); // VDS default: empty string
        edi.setVersion((short) 0);

        // Agency IDs (set same as main agency if not available)
        edi.setDocumentalAgencyId(agencyCode);
        edi.setAgencyIssueId(agencyCode);
        edi.setAgencypolid(agencyCode);

        return edi;
    }

    /**
     * Map CustomerDTO to EdiTempBookingCust entity (VDS-compatible)
     */
    private EdiTempBookingCust createEdiTempCustFromDto(String bookNo, CustomerDTO dto, String ediReaderId) {
        EdiTempBookingCust edi = new EdiTempBookingCust();

        edi.setBookNo(bookNo);
        edi.setCustTypeId(dto.getCustomerType() != null ? dto.getCustomerType().getValue() : null);
        edi.setCustId(nullIfBlank(dto.getCustomerCode()));
        edi.setCountryId(dto.getCountryId());
        edi.setName(dto.getCustomerName());
        edi.setAddress1(nullIfBlank(dto.getAddress()));
        edi.setAddress2(nullIfBlank(dto.getAddress1()));
        edi.setCity(dto.getCity());
        edi.setZipCode(nullIfBlank(dto.getZipCode()));
        edi.setTelephone(nullIfBlank(dto.getTelephone()));
        edi.setEmail(nullIfBlank(dto.getEmail()));
        edi.setEdireaderid(ediReaderId);
        edi.setIsedited("N");
        edi.setVersion((short) 0);

        return edi;
    }

    /**
     * Map ContainerDTO to EdiTempBookingCargo entity (VDS-compatible)
     * 
     * @param bookNo        The booking number
     * @param dto           The container DTO
     * @param ediReaderId   The EDI reader ID
     * @param tareWeightMap Map of equipment size to tare weight (from bulk fetch)
     */
    private EdiTempBookingCargo createEdiTempCargoFromDto(String bookNo, ContainerDTO dto, String ediReaderId,
            Map<String, Float> tareWeightMap) {
        EdiTempBookingCargo edi = new EdiTempBookingCargo();

        edi.setBookNo(bookNo);
        edi.setCargoNo(dto.getCargoNo());
        edi.setEqpId(dto.getContainerNo());
        edi.setType(dto.getEquipmentType() != null ? dto.getEquipmentType().getDbValue() : null);
        edi.setEqpTypeId(dto.getEquipmentSize());
        edi.setEqpStatus(dto.getEquipmentStatus() != null ? dto.getEquipmentStatus().getValue() : null);
        edi.setShipperOwn(dto.getSocFlag() != null ? dto.getSocFlag().getValue() : "N");
        edi.setNor(dto.getNor() != null ? dto.getNor().getValue() : "N");
        edi.setDgflag(dto.getDgFlag() != null ? dto.getDgFlag().getValue() : "N");
        edi.setOog(dto.getOogFlag() != null ? dto.getOogFlag().getValue() : "N");

        // Get tare weight from pre-fetched map (no additional DB call)
        Float tareWeight = dto.getEquipmentSize() != null ? tareWeightMap.get(dto.getEquipmentSize().trim()) : null;
        edi.setTareWeight(tareWeight);

        Float cargoWeight = dto.getCargoWeight() != null ? dto.getCargoWeight().floatValue() : null;
        edi.setCargoWeight(cargoWeight);

        // Calculate gross weight = tare weight + cargo weight
        Float grossWeight = calculateGrossWeight(tareWeight, cargoWeight);
        edi.setGrossWeight(grossWeight);

        edi.setWeightUnit(dto.getWeightUnit() != null ? dto.getWeightUnit().getValue() : null);

        // Reefer settings if available (SI-aligned export)
        com.solverminds.booking.util.ReeferMappingHelper.populateEdiCargo(edi, dto.getReefer());

        // VDS default values
        edi.setLoadingCondition("FF");
        edi.setEdireaderid(ediReaderId);
        edi.setIsDummy("Y");
        edi.setRoundTrip("N");
        edi.setActStatus("O");
        edi.setStatus("A");
        edi.setIsedited("N");
        edi.setVersion((short) 0);

        return edi;
    }

    /**
     * Map CommodityDTO to EdiTempBookingCommodity entity (VDS-compatible)
     */
    private EdiTempBookingCommodity createEdiTempCommodityFromDto(String bookNo, CommodityDTO dto, 
            String ediReaderId, List<ContainerDTO> containers) {
        EdiTempBookingCommodity edi = new EdiTempBookingCommodity();

        edi.setBookNo(bookNo);
        edi.setCargoNo(dto.getCargoNo());
        edi.setEqpId(dto.getContainerNo());
        edi.setItemNo(dto.getItemNo());
        edi.setComdtygrp(nullIfBlank(dto.getCommodityGroup()));
        edi.setCommodityId(nullIfBlank(dto.getHsCode())); // hsCode → commodityId
        edi.setPackageId(nullIfBlank(dto.getPackageType()));
        edi.setItemQty(dto.getPackageQty());
        edi.setItemVolume(dto.getCargoVolume() != null ? dto.getCargoVolume().floatValue() : null);
        edi.setItemVolumeUnit(dto.getVolumnUnit() != null ? nullIfBlank(dto.getVolumnUnit().getValue()) : null);
        edi.setNettwt(dto.getNettwt() != null ? dto.getNettwt().floatValue() : null);
        edi.setBycode(nullIfBlank(dto.getBuyerCode()));
        edi.setByname(nullIfBlank(dto.getBuyerName()));
        edi.setSlcode(nullIfBlank(dto.getSellerCode()));
        edi.setSlname(nullIfBlank(dto.getSellerName()));
        edi.setCmdtyHblno(nullIfBlank(dto.getCommodityHBLNo()));

        // Commodity-level itemWeight (aligned with ecombooking_commodity.nettwt); hazardous from matching container
        String hazardous = "N";
        applyCommodityItemWeightToEdi(edi, dto);
        if (containers != null && dto.getContainerNo() != null) {
            ContainerDTO matchedContainer = containers.stream()
                .filter(c -> dto.getContainerNo().equals(c.getContainerNo()))
                .findFirst()
                .orElse(null);
            if (matchedContainer != null
                    && matchedContainer.getDgFlag() != null
                    && "Y".equalsIgnoreCase(matchedContainer.getDgFlag().getValue())) {
                hazardous = "Y";
            }
        }

        // VDS default values
        edi.setEdireaderid(ediReaderId);
        edi.setHazardous(hazardous);
        edi.setAlcohol("N");
        edi.setMilitarycargo("N");
        edi.setIsedited("N");
        edi.setVersion((short) 0);
        edi.setCarriercode(DEFAULT_CARRIER_CODE);

        return edi;
    }

    /**
     * Map RoutingDetail to EdiTempBookingRouting entity (VDS-compatible)
     * Uses routing information from routing table with all transit ports and operations modes
     */
    private EdiTempBookingRouting createEdiTempRoutingFromDto(String bookNo, BookingRequestDTO bookingDto,
            RoutingDetailRepository.RoutingDetail routingDetail, String ediReaderId) {
        EdiTempBookingRouting edi = new EdiTempBookingRouting();

        edi.setBookNo(bookNo);
        
        // Origin information from routing
        String origin = routingDetail.getOrigin() != null ? routingDetail.getOrigin() : bookingDto.getOriginPortId();
        edi.setOrigin(origin);
        
        // POL (Port of Loading) information from routing
        String pol = routingDetail.getPol() != null ? routingDetail.getPol() : bookingDto.getLoadPortId();
        edi.setPol(pol);
        
        // Set origin mode: if origin and pol are different and orgMode is empty/null, default to "T"
        String originMode = nullIfBlank(routingDetail.getOriginMode());
        if (originMode == null && origin != null && pol != null && !origin.equals(pol)) {
            originMode = "T";
        }
        edi.setOriginMode(originMode);
        edi.setPolter(nullIfBlank(routingDetail.getPolTerminal()));
        edi.setPolSer(nullIfBlank(routingDetail.getPolSer()));
        edi.setPolmode(nullIfBlank(routingDetail.getPolMode()));
        
        // Transit port 1 information
        edi.setTrans1(nullIfBlank(routingDetail.getTrans1()));
        edi.setTrans1Ter(nullIfBlank(routingDetail.getTrans1Ter()));
        edi.setTrans1Ser(nullIfBlank(routingDetail.getTrans1Ser()));
        edi.setTrans1mode(nullIfBlank(routingDetail.getTrans1Mode()));
        
        // Transit port 2 information
        edi.setTrans2(nullIfBlank(routingDetail.getTrans2()));
        edi.setTrans2Ter(nullIfBlank(routingDetail.getTrans2Ter()));
        edi.setTrans2Ser(nullIfBlank(routingDetail.getTrans2Ser()));
        edi.setTrans2mode(nullIfBlank(routingDetail.getTrans2Mode()));
        
        // Transit port 3 information
        edi.setTrans3(nullIfBlank(routingDetail.getTrans3()));
        edi.setTrans3Ter(nullIfBlank(routingDetail.getTrans3Ter()));
        edi.setTrans3Ser(nullIfBlank(routingDetail.getTrans3Ser()));
        edi.setTrans3mode(nullIfBlank(routingDetail.getTrans3Mode()));
        
        // POD (Port of Discharge) information from routing
        String pod = routingDetail.getPod() != null ? routingDetail.getPod() : bookingDto.getDischargePortId();
        edi.setPod(pod);
        edi.setPodTer(nullIfBlank(routingDetail.getPodTerminal()));
        
        // Delivery information
        String delivery = routingDetail.getDelivery() != null ? routingDetail.getDelivery() : bookingDto.getFinalPortId();
        edi.setDelivery(delivery);
        
        // Set pod mode: if pod and delivery are different and delMode is empty/null, default to "T"
        String podMode = nullIfBlank(routingDetail.getPodMode());
        if (podMode == null && pod != null && delivery != null && !pod.equals(delivery)) {
            podMode = "T";
        }
        edi.setPodMode(podMode);
        
        // Service code and default route
        edi.setSvcCode(nullIfBlank(routingDetail.getSvcCode()));
        edi.setDefRout(nullIfBlank(routingDetail.getDefRoute()));
        
        // Operations transit modes
        edi.setOpsTrans1Mde(nullIfBlank(routingDetail.getOpsTrans1Mde()));
        edi.setOpsTrans2Mde(nullIfBlank(routingDetail.getOpsTrans2Mde()));
        
        // Route ID and EDI reader ID
        edi.setRouteid(bookingDto.getRouteId());
        edi.setEdireaderid(ediReaderId);
        edi.setVersion((short) 0);

        return edi;
    }

    /**
     * Map RouteDTO to EdiTempBookingLeg entity (VDS-compatible)
     */
    private EdiTempBookingLeg createEdiTempLegFromDto(String bookNo, RouteDTO dto, BookingRequestDTO bookingDto,
            short legNo, Short previousLeg, String ediReaderId) {
        EdiTempBookingLeg edi = new EdiTempBookingLeg();
        LocalDateTime now = LocalDateTime.now();

        edi.setBookNo(bookNo);
        edi.setLegNo(legNo);
        edi.setBound(dto.getLoadBound() != null ? nullIfBlank(dto.getLoadBound().getValue()) : null);
        edi.setServiceCode(nullIfBlank(dto.getServiceCode()));
        edi.setVesselCode(nullIfBlank(dto.getVesselCode()));
        edi.setVoyageCode(nullIfBlank(dto.getVoyageCode()));
        edi.setLoadPortId(dto.getLoadPortId());
        edi.setDischargePortId(dto.getDischargePortId());
        edi.setEtdDate(parseRouteDateToLocalDateTimeFromOdt(dto.getEtdDate()));
        edi.setEtaDate(parseRouteDateToLocalDateTimeFromOdt(dto.getEtaDate()));
        edi.setLegTypeId(dto.getServiceType() != null ? nullIfBlank(dto.getServiceType().getValue()) : null); // M=MainLine, F=Feeder, B=Barge
        edi.setPreCarriage(bookingDto.getPreCarriage() != null ? bookingDto.getPreCarriage().getValue() : null);
        edi.setOnCarriage(bookingDto.getOnCarriage() != null ? bookingDto.getOnCarriage().getValue() : null);
        edi.setLoadType(dto.getLoadType() != null ? nullIfBlank(dto.getLoadType().getValue()) : null); // L=Local, T=Transit
        edi.setDischType(dto.getDischType() != null ? nullIfBlank(dto.getDischType().getValue()) : null);
        edi.setPreviousLeg(previousLeg);
        edi.setGroupcallid(nullIfBlank(dto.getGroupCallId()));
        edi.setRouteid(bookingDto.getRouteId() != null ? bookingDto.getRouteId().toString() : null);
        edi.setCallIdLoading(nullIfBlank(dto.getLoadCallId()));
        edi.setCallIdDischarge(nullIfBlank(dto.getDischargeCallId()));
        edi.setDbound(dto.getDischargeBound() != null ? nullIfBlank(dto.getDischargeBound().getValue()) : null);
        edi.setPolTerminal(nullIfBlank(dto.getLoadTerminal()));
        edi.setPodTerminal(nullIfBlank(dto.getDischargeTerminal()));

        // VDS defaults
        edi.setEdireaderid(ediReaderId);
        edi.setDateup(now);
        edi.setVersion((short) 0);
        edi.setIsedited("N");

        return edi;
    }

    /**
     * Create reference records (VDS pattern)
     */
    private void createReferenceRecords(String bookNo, BookingRequestDTO dto, String ediReaderId) {
        // DAK - bookingRequestId (the created BKON number)
        saveReference(bookNo, "DAK", bookNo, ediReaderId);

        // EOR - contractReference (rateRefNo)
        if (dto.getRateRefNo() != null && !dto.getRateRefNo().trim().isEmpty()) {
            saveReference(bookNo, "EOR", dto.getRateRefNo(), ediReaderId);
        }

        // ERF - exportRefNo
        if (dto.getExportRefNo() != null && !dto.getExportRefNo().trim().isEmpty()) {
            saveReference(bookNo, "ERF", dto.getExportRefNo(), ediReaderId);
        }

        // SHR - customerReference (customerPO)
        if (dto.getCustomerPO() != null && !dto.getCustomerPO().trim().isEmpty()) {
            saveReference(bookNo, "SHR", dto.getCustomerPO(), ediReaderId);
        }
    }

    /**
     * Save a single reference record
     */
    private void saveReference(String bookNo, String refId, String refVal, String ediReaderId) {
        EdiTempBookingReference edi = new EdiTempBookingReference();
        edi.setBookNo(bookNo);
        edi.setRefid(refId);
        edi.setRefval(refVal);
        edi.setEdireaderid(ediReaderId);
        edi.setIsedited("N");
        edi.setVersion((short) 0);
        ediTempBookingReferenceRepo.save(edi);
    }

    /**
     * Map BookingRequestDTO remarks to EdiTempBookingRemarks entity
     */
    private EdiTempBookingRemarks createEdiTempRemarksFromDto(String bookNo, BookingRequestDTO dto,
            String ediReaderId) {
        EdiTempBookingRemarks edi = new EdiTempBookingRemarks();

        edi.setBookNo(bookNo);
        edi.setRemarksref("CUST_REMARKS");
        edi.setRemarksval(dto.getRemarks());
        edi.setEdireaderid(ediReaderId);
        edi.setVersion(0);
        edi.setCategory("C"); // C = Customer remarks
        edi.setTranstype("BKG"); // Booking

        return edi;
    }

    /**
     * Create DG (Dangerous Goods) remarks records in editempbooking_remarks table.
     * Groups DG by classCode and unNo, then collects container numbers for each group.
     * Format: "CNTR1,CNTR2,CNTR3 - DG CLASS:{classCode} UN NO:{unNo}"
     * 
     * @param bookNo The booking number
     * @param commodities List of commodity DTOs containing DG information
     * @param ediReaderId The EDI reader ID
     */
    private void createDgRemarksRecords(String bookNo, List<CommodityDTO> commodities, String ediReaderId) {
        if (commodities == null || commodities.isEmpty()) {
            return;
        }

        // Collect all DG entries with their container numbers
        // Key: "classCode|unNo", Value: List of container numbers
        Map<String, List<String>> dgGroupToContainers = new LinkedHashMap<>();

        for (CommodityDTO commodity : commodities) {
            DangerousGoodsInfoV2 dgi = commodity.getDangerousGoods();
            if (dgi != null) {
                String classCode = nullIfBlank(dgi.getClassCode());
                String unNo = nullIfBlank(dgi.getUnNo());
                String containerNo = nullIfBlank(commodity.getContainerNo());

                if (classCode == null || unNo == null || containerNo == null) {
                    continue;
                }

                String groupKey = classCode + "|" + unNo;

                dgGroupToContainers.computeIfAbsent(groupKey, k -> new ArrayList<>());
                if (!dgGroupToContainers.get(groupKey).contains(containerNo)) {
                    dgGroupToContainers.get(groupKey).add(containerNo);
                }
            }
        }

        // Create a remarks record for each unique DG group
        for (Map.Entry<String, List<String>> entry : dgGroupToContainers.entrySet()) {
            String[] parts = entry.getKey().split("\\|");
            String classCode = parts[0];
            String unNo = parts[1];
            List<String> containers = entry.getValue();

            // Format: "CNTR1,CNTR2,CNTR3 - DG CLASS:9 UN NO:3480"
            String containersStr = String.join(",", containers);
            String remarksVal = containersStr + " - DG CLASS:" + classCode + " UN NO:" + unNo;

            // Create and save the remarks record
            EdiTempBookingRemarks dgRemarks = new EdiTempBookingRemarks();
            dgRemarks.setBookNo(bookNo);
            dgRemarks.setRemarksref("DG");
            dgRemarks.setRemarksval(remarksVal);
            dgRemarks.setEdireaderid(ediReaderId);
            dgRemarks.setVersion(0);
            dgRemarks.setCategory("E"); // E = EDI/Export remarks for DG
            dgRemarks.setTranstype("BKG"); // Booking

            ediTempBookingRemarksRepo.save(dgRemarks);
            logger.debug("Created DG remarks record for booking {}: {}", bookNo, remarksVal);
        }

        if (!dgGroupToContainers.isEmpty()) {
            logger.info("Created {} DG remarks records for booking {}", dgGroupToContainers.size(), bookNo);
        }
    }

    /**
     * Create conf record (cutoff dates placeholder)
     */
    private EdiTempBookingConf createEdiTempConfFromDto(String bookNo, String ediReaderId) {
        EdiTempBookingConf edi = new EdiTempBookingConf();

        edi.setBookNo(bookNo);
        edi.setEdireaderid(ediReaderId);
        edi.setVersion((short) 0);
        // Cutoff dates will be null - to be filled later by operations

        return edi;
    }

    /**
     * Create EdiDocStatus record for tracking document status (VDS-compatible)
     * INSERT INTO edidocstatus (book_no, edireaderid, transtype, status, ediparty,
     * receivdate, message, servprovname, amendment_version)
     * 
     * @param isResubmit true for S→S or P→S resubmit (uses transtype=5)
     * @param extBookNo external booking number/ID - if has value, transtype=5, else transtype=9
     */
    private EdiDocStatus createEdiDocStatusFromDto(String bookNo, String ediReaderId, BookingRequestDTO dto, boolean isResubmit, String extBookNo) {
        EdiDocStatus ediDocStatus = new EdiDocStatus();
        LocalDateTime now = LocalDateTime.now();

        // Primary fields
        ediDocStatus.setBookNo(bookNo); // book_no: generated booking number
        ediDocStatus.setEdireaderid(ediReaderId); // edireaderid: generated EDI reader ID
        // Set transtype: 5 if id (extBookNo) has value, else 9
        boolean hasId = extBookNo != null && !extBookNo.trim().isEmpty();
        ediDocStatus.setTranstype(hasId ? RESUBMIT_TRANS_TYPE : DEFAULT_TRANS_TYPE); // transtype: "5" if id exists, "9" for new
        ediDocStatus.setStatus("R"); // status: document received/pending processing
        ediDocStatus.setEdiparty("TSHONLINE BOOKING"); // ediparty: agency as EDI party
        ediDocStatus.setReceivdate(now); // receivdate: current timestamp (getDate())
        ediDocStatus.setMessage(DEFAULT_MESSAGE_TYPE); // message: "IFTMBF" booking confirmation
        ediDocStatus.setServprovname("TSHONLINE BOOKING"); // servprovname: "EcomOnlineBkg"
        // Set amendment_version: 0 by default, but increment if previous version has status 'P'
        int amendmentVersion = calculateAmendmentVersion(bookNo);
        ediDocStatus.setAmendmentVersion(amendmentVersion);

        return ediDocStatus;
    }

    /**
     * Calculate amendment version based on previous EDI doc status records.
     * Default is 0, but increment by 1 if there's a previous version with status 'P'.
     * 
     * @param bookNo The booking number
     * @return The amendment version to use
     */
    private int calculateAmendmentVersion(String bookNo) {
        try {
            // Find all previous EDI doc status records for this booking
            List<EdiDocStatus> previousRecords = ediDocStatusRepo.findByBookNo(bookNo);
            
            if (previousRecords == null || previousRecords.isEmpty()) {
                return 0; // No previous records, start with 0
            }
            
            // Find the maximum amendment_version among records with status 'P'
            int maxAmendmentVersionWithStatusP = previousRecords.stream()
                .filter(record -> "P".equals(record.getStatus()))
                .mapToInt(record -> record.getAmendmentVersion() != null ? record.getAmendmentVersion() : 0)
                .max()
                .orElse(-1); // -1 if no records with status 'P' found
            
            if (maxAmendmentVersionWithStatusP >= 0) {
                // Found previous version with status 'P', increment by 1
                return maxAmendmentVersionWithStatusP + 1;
            } else {
                // No previous version with status 'P', use default 0
                return 0;
            }
        } catch (Exception e) {
            logger.warn("Error calculating amendment version for booking {}: {}", bookNo, e.getMessage());
            return 0; // Fallback to default
        }
    }

    /**
     * Create EdiReaderMaster record for tracking EDI processing (VDS-compatible)
     * INSERT INTO edireadermaster (ediid, edireaderid, datecr, requestdate,
     * agencycode, locationcode,
     * uploadstatus, edifilename, edifile, usercr, edisource, edipartycode,
     * edifilepath, erhsender, startedon)
     */
    private EdiReaderMaster createEdiReaderMasterFromDto(String ediReaderId, BookingRequestDTO dto,
            String loginNameFromJwt, String corpAgencyCode) {
        EdiReaderMaster ediReaderMaster = new EdiReaderMaster();
        LocalDateTime now = LocalDateTime.now();

        // Primary key
        ediReaderMaster.setEdireaderid(ediReaderId); // edireaderid: generated EDI reader ID (PK)

        // EDI identification
        ediReaderMaster.setEdiid("EDI109"); // ediid: message type (IFTMBF)

        // Timestamps
        ediReaderMaster.setDatecr(now); // datecr: getDate() - current timestamp
        ediReaderMaster.setRequestdate(now); // requestdate: getDate() - current timestamp
        ediReaderMaster.setStartedon(now); // startedon: start time - current timestamp

        // Agency and location - use corporate agency code from agencymatrix
        ediReaderMaster.setAgencycode(corpAgencyCode); // agencycode: corporate agency from agencymatrix
        ediReaderMaster.setLocationcode(dto.getAgencyId()); // locationcode: from load port (location)

        // Status and file info (for API, no physical file)
        ediReaderMaster.setUploadstatus("Completed"); // uploadstatus: initial status
        ediReaderMaster.setEdifilename(null); // edifilename: API booking (no file)
        ediReaderMaster.setEdifile(null); // edifile: API request (no file content)
        ediReaderMaster.setEdifilepath(null); // edifilepath: null for API
        // isprocess: not yet processed
        ediReaderMaster.setPartnerid("TP001"); // partnerid: default partner

        return ediReaderMaster;
    }

    /**
     * Helper method to return null for blank strings
     */
    private static String nullIfBlank(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    /** Parse route etd/eta string (ISO with or without offset) to LocalDateTime in UTC for EDI. */
    private static LocalDateTime parseRouteDateToLocalDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return null;
        Instant inst = PortLocalTimeUtil.parseToInstant(dateTimeStr);
        return inst == null ? null : LocalDateTime.ofInstant(inst, ZoneOffset.UTC);
    }

    /** Convert OffsetDateTime (route etd/eta) to LocalDateTime for EDI. */
    private static LocalDateTime parseRouteDateToLocalDateTimeFromOdt(java.time.OffsetDateTime odt) {
        return odt == null ? null : odt.toLocalDateTime();
    }

    /**
     * Find the first route with the specified service type.
     * 
     * @param routes      List of routes to search
     * @param serviceType The service type to find ("F" for Feeder, "M" for
     *                    Mainline)
     * @return The first matching RouteDTO, or null if none found
     */
    private static RouteDTO findFirstByServiceType(List<RouteDTO> routes, String serviceType) {
        if (routes == null || routes.isEmpty() || serviceType == null) {
            return null;
        }
        return routes.stream()
                .filter(r -> r != null && serviceType.equals(r.getServiceType()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find the first route that is either the specified service type or "M"
     * (Mainline),
     * whichever comes first in the list.
     * 
     * @param routes      List of routes to search
     * @param serviceType The service type to find ("F" for Feeder)
     * @return The first RouteDTO that is either the specified service type or "M",
     *         or null if none found
     */
    private static RouteDTO findFirstByServiceTypeOrFirst(List<RouteDTO> routes) {
        if (routes == null || routes.isEmpty()) {
            return null;
        }
        // Return the first route that is either the specified service type or "M"
        return routes.stream()
                .filter(r -> r != null && ("F".equalsIgnoreCase(r.getServiceType().getValue()) || "M".equalsIgnoreCase(r.getServiceType().getValue())))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find ocean leg by matching with routing main service code.
     * Finds the route that has serviceCode matching the routing main service code
     * and serviceType = "M" (Mainline).
     * 
     * @param routes List of routes to search
     * @param routingDetail The routing detail containing main service code
     * @return The matching RouteDTO with serviceType "M" and matching service code, or null if none found
     */
    private RouteDTO findOceanLegByRoutingMainService(List<RouteDTO> routes, 
            RoutingDetailRepository.RoutingDetail routingDetail) {
        if (routes == null || routes.isEmpty()) {
            return null;
        }
        
        // If routing detail is available, use main service code to find matching route
        if (routingDetail != null && routingDetail.getMainServiceCode() != null) {
            String mainServiceCode = routingDetail.getMainServiceCode();
            RouteDTO matchingRoute = findRouteByServiceCodeAndType(routes, mainServiceCode, "M");
            if (matchingRoute != null) {
                logger.debug("Found ocean leg matching main service code: {}", mainServiceCode);
                return matchingRoute;
            } else {
                logger.warn("No mainline route found matching main service code: {}", mainServiceCode);
            }
        }
        
        // Fallback to first mainline route if no match found or routing detail not available
        return findFirstByServiceType(routes, "M");
    }

    /**
     * Find a route by service code and service type.
     * 
     * @param routes List of routes to search
     * @param serviceCode The service code to match
     * @param serviceType The service type to match ("M" for Mainline, "F" for Feeder)
     * @return The matching RouteDTO, or null if none found
     */
    private static RouteDTO findRouteByServiceCodeAndType(List<RouteDTO> routes, String serviceCode, String serviceType) {
        if (routes == null || routes.isEmpty() || serviceCode == null || serviceType == null) {
            return null;
        }
        return routes.stream()
                .filter(r -> r != null 
                        && serviceType.equalsIgnoreCase(r.getServiceType().getValue())
                        && serviceCode.equalsIgnoreCase(r.getServiceCode()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Bulk fetch tare weights for all containers in a single database query.
     * Collects unique equipment sizes and fetches their tare weights.
     * 
     * @param containers List of container DTOs
     * @return Map of equipment size to tare weight
     */
    private Map<String, Float> getTareWeightsForContainers(java.util.List<ContainerDTO> containers) {
        if (containers == null || containers.isEmpty()) {
            return new HashMap<>();
        }

        // Collect unique equipment sizes
        java.util.Set<String> equipmentSizes = containers.stream()
                .map(ContainerDTO::getEquipmentSize)
                .filter(size -> size != null && !size.trim().isEmpty())
                .collect(java.util.stream.Collectors.toSet());

        // Bulk fetch tare weights from database
        return equipmentRepository.getTareWeightsByEquipTypes(equipmentSizes);
    }

    /**
     * Calculate gross weight = tare weight + cargo weight.
     * Returns cargo weight if tare weight is not available.
     * 
     * @param tareWeight  The tare weight (can be null)
     * @param cargoWeight The cargo weight (can be null)
     * @return The calculated gross weight
     */
    private static Float calculateGrossWeight(Float tareWeight, Float cargoWeight) {
        if (tareWeight != null && cargoWeight != null) {
            return tareWeight + cargoWeight;
        } else if (cargoWeight != null) {
            // If tare weight not available, return cargo weight only
            return cargoWeight;
        }
        return null;
    }

    // ================== Container Expansion Helper Methods ==================

    /**
     * Helper class to track expanded container information.
     */
    private static class ExpandedContainer {
        final Integer originalCargoNo;
        final String containerNo;
        final int cargoNo; // Expanded cargoNo (sequence)
        final ContainerDTO originalContainer;

        ExpandedContainer(Integer originalCargoNo, String containerNo, int cargoNo, ContainerDTO originalContainer) {
            this.originalCargoNo = originalCargoNo;
            this.containerNo = containerNo;
            this.cargoNo = cargoNo;
            this.originalContainer = originalContainer;
        }
    }

    /**
     * Generate container number for expanded container.
     * Format: {equipmentSize}TBN{sequence} (e.g., 20DCTBN1, 20DCTBN2, 40DCTBN1, 40DCTBN2)
     * Sequence resets per equipment size.
     * 
     * @param equipmentSize The equipment size (e.g., "20DC", "40RF")
     * @param providedContainerNo The container number provided in request (may be null)
     * @param sequence The sequence number for this equipment size
     * @return Generated container number
     */
    private String generateContainerNumber(String equipmentSize, String providedContainerNo, int sequence) {
        // If container number is provided and this is the first in sequence, use it
        if (providedContainerNo != null && !providedContainerNo.trim().isEmpty() && sequence == 1) {
            return providedContainerNo.trim();
        }
        
        // Generate: {equipmentSize}TBN{seq} (e.g., 20DCTBN1, 40DCTBN2)
        String eqpSize = (equipmentSize != null) ? equipmentSize.trim() : "CONT";
        return String.format("%sTBN%d", eqpSize, sequence);
    }

    /**
     * Create EdiTempBookingCargo for an expanded container.
     */
    private EdiTempBookingCargo createEdiTempCargoFromDtoExpanded(String bookNo, ContainerDTO dto, 
            String ediReaderId, Map<String, Float> tareWeightMap, int cargoNo, String containerNo) {
        EdiTempBookingCargo edi = new EdiTempBookingCargo();

        edi.setBookNo(bookNo);
        edi.setCargoNo(cargoNo); // Use expanded cargoNo (sequence)
        edi.setEqpId(containerNo); // Use generated container number
        edi.setType(dto.getEquipmentType() != null ? dto.getEquipmentType().getDbValue() : null);
        edi.setEqpTypeId(dto.getEquipmentSize());
        edi.setEqpStatus(dto.getEquipmentStatus() != null ? dto.getEquipmentStatus().getValue() : null);
        edi.setShipperOwn(dto.getSocFlag() != null ? dto.getSocFlag().getValue() : "N");
        edi.setNor(dto.getNor() != null ? dto.getNor().getValue() : "N");
        edi.setDgflag(dto.getDgFlag() != null ? dto.getDgFlag().getValue() : "N");
        edi.setOog(dto.getOogFlag() != null ? dto.getOogFlag().getValue() : "N");

        // Get tare weight from pre-fetched map
        Float tareWeight = dto.getEquipmentSize() != null ? tareWeightMap.get(dto.getEquipmentSize().trim()) : null;
        edi.setTareWeight(tareWeight);

        Float cargoWeight = dto.getCargoWeight() != null ? dto.getCargoWeight().floatValue() : null;
        edi.setCargoWeight(cargoWeight);

        // Calculate gross weight
        Float grossWeight = calculateGrossWeight(tareWeight, cargoWeight);
        edi.setGrossWeight(grossWeight);

        edi.setWeightUnit(dto.getWeightUnit() != null ? dto.getWeightUnit().getValue() : null);

        // Reefer settings if available (SI-aligned export)
        com.solverminds.booking.util.ReeferMappingHelper.populateEdiCargo(edi, dto.getReefer());

        // VDS default values
        edi.setLoadingCondition("FF");
        edi.setEdireaderid(ediReaderId);
        edi.setIsDummy("Y");
        edi.setRoundTrip("N");
        edi.setActStatus("O");
        edi.setStatus("A");
        edi.setIsedited("N");
        edi.setVersion((short) 0);

        return edi;
    }

    /**
     * Create EdiTempBookingCommodity for an expanded container.
     */
    private EdiTempBookingCommodity createEdiTempCommodityFromDtoExpanded(String bookNo, CommodityDTO dto,
            String ediReaderId, String expandedContainerNo, int itemNo, ExpandedContainer expandedContainer) {
        EdiTempBookingCommodity edi = new EdiTempBookingCommodity();

        edi.setBookNo(bookNo);
        edi.setCargoNo(expandedContainer != null ? expandedContainer.cargoNo : dto.getCargoNo());
        edi.setEqpId(expandedContainerNo); // Use expanded container number
        edi.setItemNo(itemNo); // Use sequence-based item number
        edi.setComdtygrp(nullIfBlank(dto.getCommodityGroup()));
        edi.setCommodityId(nullIfBlank(dto.getHsCode()));
        edi.setPackageId(nullIfBlank(dto.getPackageType()));
        edi.setItemQty(dto.getPackageQty());
        edi.setItemVolume(dto.getCargoVolume() != null ? dto.getCargoVolume().floatValue() : null);
        edi.setItemVolumeUnit(dto.getVolumnUnit() != null ? nullIfBlank(dto.getVolumnUnit().getValue()) : null);
        edi.setNettwt(dto.getNettwt() != null ? dto.getNettwt().floatValue() : null);
        edi.setBycode(nullIfBlank(dto.getBuyerCode()));
        edi.setByname(nullIfBlank(dto.getBuyerName()));
        edi.setSlcode(nullIfBlank(dto.getSellerCode()));
        edi.setSlname(nullIfBlank(dto.getSellerName()));
        edi.setCmdtyHblno(nullIfBlank(dto.getCommodityHBLNo()));

        // Commodity-level itemWeight (aligned with ecombooking_commodity.nettwt); hazardous from expanded container
        String hazardous = "N";
        applyCommodityItemWeightToEdi(edi, dto);
        if (expandedContainer != null && expandedContainer.originalContainer != null) {
            ContainerDTO container = expandedContainer.originalContainer;
            if (container.getDgFlag() != null && "Y".equalsIgnoreCase(container.getDgFlag().getValue())) {
                hazardous = "Y";
            }
        }

        // VDS default values
        edi.setEdireaderid(ediReaderId);
        edi.setHazardous(hazardous);
        edi.setAlcohol("N");
        edi.setMilitarycargo("N");
        edi.setIsedited("N");
        edi.setVersion((short) 0);
        edi.setCarriercode(DEFAULT_CARRIER_CODE);

        return edi;
    }

    /**
     * Maps commodity-level weight to {@code editempbooking_commodity.itemWeight},
     * aligned with {@code ecombooking_commodity.nettwt} (itemWeight preferred, then nettwt).
     */
    private void applyCommodityItemWeightToEdi(EdiTempBookingCommodity edi, CommodityDTO dto) {
        if (edi == null || dto == null) {
            return;
        }
        if (dto.getItemWeight() != null) {
            edi.setItemWeight(dto.getItemWeight());
            edi.setItemWeightUnit(dto.getItemWeightUnit() != null
                    ? dto.getItemWeightUnit().getValue() : WeightUnit.K.getValue());
        } else if (dto.getNettwt() != null) {
            edi.setItemWeight(dto.getNettwt().floatValue());
            edi.setItemWeightUnit(WeightUnit.K.getValue());
        }
    }

    /**
     * Find expanded container by container number.
     */
    private ExpandedContainer findExpandedContainer(List<ExpandedContainer> expandedContainers, String containerNo) {
        if (expandedContainers == null || containerNo == null) {
            return null;
        }
        return expandedContainers.stream()
                .filter(ec -> containerNo.equals(ec.containerNo))
                .findFirst()
                .orElse(null);
    }

    /**
     * Create DG remarks records using expanded container numbers.
     */
    private void createDgRemarksRecordsExpanded(String bookNo, List<CommodityDTO> commodities,
            Map<Integer, List<String>> cargoNoToExpandedContainers, String ediReaderId) {
        if (commodities == null || commodities.isEmpty()) {
            return;
        }

        // Collect all DG entries with their expanded container numbers
        // Key: "classCode|unNo", Value: List of container numbers
        Map<String, List<String>> dgGroupToContainers = new LinkedHashMap<>();

        for (CommodityDTO commodity : commodities) {
            DangerousGoodsInfoV2 dgi = commodity.getDangerousGoods();
            if (dgi != null) {
                List<String> expandedContainerNos = cargoNoToExpandedContainers.get(commodity.getCargoNo());

                String classCode = nullIfBlank(dgi.getClassCode());
                String unNo = nullIfBlank(dgi.getUnNo());

                if (classCode == null || unNo == null) {
                    continue;
                }

                String groupKey = classCode + "|" + unNo;

                if (expandedContainerNos != null && !expandedContainerNos.isEmpty()) {
                    for (String containerNo : expandedContainerNos) {
                        dgGroupToContainers.computeIfAbsent(groupKey, k -> new ArrayList<>());
                        if (!dgGroupToContainers.get(groupKey).contains(containerNo)) {
                            dgGroupToContainers.get(groupKey).add(containerNo);
                        }
                    }
                } else {
                    String containerNo = nullIfBlank(commodity.getContainerNo());
                    if (containerNo != null) {
                        dgGroupToContainers.computeIfAbsent(groupKey, k -> new ArrayList<>());
                        if (!dgGroupToContainers.get(groupKey).contains(containerNo)) {
                            dgGroupToContainers.get(groupKey).add(containerNo);
                        }
                    }
                }
            }
        }

        // Create a remarks record for each unique DG group
        for (Map.Entry<String, List<String>> entry : dgGroupToContainers.entrySet()) {
            String[] parts = entry.getKey().split("\\|");
            String classCode = parts[0];
            String unNo = parts[1];
            List<String> containers = entry.getValue();

            // Format: "CNTR1,CNTR2,CNTR3 - DG CLASS:9 UN NO:3480"
            String containersStr = String.join(",", containers);
            String remarksVal = containersStr + " - DG CLASS:" + classCode + " UN NO:" + unNo;

            // Create and save the remarks record
            EdiTempBookingRemarks dgRemarks = new EdiTempBookingRemarks();
            dgRemarks.setBookNo(bookNo);
            dgRemarks.setRemarksref("DG");
            dgRemarks.setRemarksval(remarksVal);
            dgRemarks.setEdireaderid(ediReaderId);
            dgRemarks.setVersion(0);
            dgRemarks.setCategory("E");
            dgRemarks.setTranstype("BKG");

            ediTempBookingRemarksRepo.save(dgRemarks);
            logger.debug("Created DG remarks record for booking {}: {}", bookNo, remarksVal);
        }

        if (!dgGroupToContainers.isEmpty()) {
            logger.info("Created {} DG remarks records for booking {}", dgGroupToContainers.size(), bookNo);
        }
    }

    // ================== Re-submit Methods for Status Transitions ==================

    /**
     * Fetch the ERP booking number (extBookNo) for a given ECOM booking.
     * 
     * @param bookNo The ECOM booking number (ebookno)
     * @return The ERP booking number (extBookNo), or null if not found
     */

    /**
     * Handle re-submit from Submitted (S) status.
     * S → S transition: Update existing EDI docstatus R→V, then create new EDI export with extBookNo.
     * 
     * @param bookNo The booking number
     * @param dto The booking request DTO (must have agencyId resolved)
     * @param loginNameFromJwt Login name from JWT for audit
     * @param extBookNo The ERP booking number for tbook_no field
     */
    public void resubmitFromSubmitted(String bookNo, BookingRequestDTO dto, String loginNameFromJwt, String extBookNo) {
        logger.info("Resubmit from Submitted (S→S) for booking: {} (extBookNo: {})", bookNo, extBookNo);
        
        // 1. Update existing EDI docstatus records from R to V
        int updatedCount = ediDocStatusRepo.updateStatusToVoid(bookNo);
        logger.info("Updated {} EDI docstatus records to V for booking: {}", updatedCount, bookNo);
        
        // 2. Re-export to EDI temp tables with extBookNo and transtype=5 for resubmit
        exportToEdiTempTables(bookNo, dto, loginNameFromJwt, extBookNo, true);
        
        logger.info("Completed S→S resubmit for booking: {} (extBookNo: {})", bookNo, extBookNo);
    }

    /**
     * Handle re-submit from In-Progress (P) status with LRP=D (ERP Draft).
     * P (LRP=D) → S transition: Create new EDI export with extBookNo (no R→V update needed).
     * 
     * @param bookNo The booking number
     * @param dto The booking request DTO (must have agencyId resolved)
     * @param loginNameFromJwt Login name from JWT for audit
     * @param extBookNo The ERP booking number for tbook_no field
     */
    public void resubmitFromInProgress(String bookNo, BookingRequestDTO dto, String loginNameFromJwt, String extBookNo) {
        logger.info("Resubmit from In-Progress (P→S) for booking: {} (extBookNo: {})", bookNo, extBookNo);
        
        if (extBookNo == null || extBookNo.trim().isEmpty()) {
            logger.warn("No extBookNo provided for P→S resubmit of booking: {}", bookNo);
        }
        
        // Re-export to EDI temp tables with extBookNo and transtype=5 for resubmit
        // No R→V update needed since EDI was already processed when status became P
        exportToEdiTempTables(bookNo, dto, loginNameFromJwt, extBookNo, true);
        
        logger.info("Completed P→S resubmit for booking: {} (extBookNo: {})", bookNo, extBookNo);
    }
}
