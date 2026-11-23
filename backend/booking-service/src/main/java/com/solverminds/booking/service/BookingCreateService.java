package com.solverminds.booking.service;

import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.dto.CustomerDTO;
import com.solverminds.booking.dto.RouteDTO;
import com.solverminds.booking.dto.ContainerDTO;
import com.solverminds.booking.dto.CommodityDTO;
import com.solverminds.booking.dto.DangerousGoodsDTO;
import com.solverminds.booking.dto.v2.DangerousGoodsInfoV2;
import com.solverminds.booking.mapper.BookingMapperV2;
import com.solverminds.booking.mapper.DangerousGoodsMapper;
import com.solverminds.booking.dto.ReeferSettingDTO;
import com.solverminds.booking.entity.*;
import com.solverminds.booking.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.solverminds.booking.util.BookingCargoWeightNormalizer;
import com.svm.ecom.common.enums.WeightUnit;
import com.svm.ecom.common.util.PortLocalTimeUtil;

@Service
@Transactional(transactionManager = "ecomTransactionManager")
public class BookingCreateService {

    private static final Logger logger = LoggerFactory.getLogger(BookingCreateService.class);

    private final EcomBookingRepository bookingRepo;
    private final EcomBookingCustomerRepository customerRepo;
    private final EcomBookingRouteRepository routeRepo;
    private final EcomBookingContainerRepository containerRepo;
    private final EcomBookingCommodityRepository commodityRepo;
    private final EcomBookingReeferRepository reeferRepo;
    private final EcomBookingOogRepository oogRepo;
    private final EcomBookingDgRepository dgRepo;
    private final ModuleIndexService moduleIndexService;
    private final AgencyLocationRepository agencyLocationRepo;
    private final EquipmentRepository equipmentRepo;
    private final BookingMapperV2 bookingMapperV2;
    private final DangerousGoodsMapper dangerousGoodsMapper;
    private final DangerousGoodsEnrichmentService dangerousGoodsEnrichmentService;

    public BookingCreateService(EcomBookingRepository bookingRepo, 
                              EcomBookingCustomerRepository customerRepo,
                              EcomBookingRouteRepository routeRepo,
                              EcomBookingContainerRepository containerRepo,
                              EcomBookingCommodityRepository commodityRepo,
                              EcomBookingReeferRepository reeferRepo,
                              EcomBookingOogRepository oogRepo,
                              EcomBookingDgRepository dgRepo,
                              ModuleIndexService moduleIndexService,
                              @Qualifier("bookingAgencyLocationRepository") AgencyLocationRepository agencyLocationRepo,
                              EquipmentRepository equipmentRepo,
                              BookingMapperV2 bookingMapperV2,
                              DangerousGoodsMapper dangerousGoodsMapper,
                              DangerousGoodsEnrichmentService dangerousGoodsEnrichmentService) {
        this.bookingRepo = bookingRepo;
        this.customerRepo = customerRepo;
        this.routeRepo = routeRepo;
        this.containerRepo = containerRepo;
        this.commodityRepo = commodityRepo;
        this.reeferRepo = reeferRepo;
        this.oogRepo = oogRepo;
        this.dgRepo = dgRepo;
        this.moduleIndexService = moduleIndexService;
        this.agencyLocationRepo = agencyLocationRepo;
        this.equipmentRepo = equipmentRepo;
        this.bookingMapperV2 = bookingMapperV2;
        this.dangerousGoodsMapper = dangerousGoodsMapper;
        this.dangerousGoodsEnrichmentService = dangerousGoodsEnrichmentService;
    }

    /**
     * Resolve agency code by looking up in reportDB based on origin port.
     * Falls back to the provided fallback agency ID if not found.
     * @param originPortId The origin port ID to lookup
     * @param fallbackAgencyId The fallback agency ID to use if lookup fails
     * @return Resolved agency code
     */
    public String resolveAgencyCode(String originPortId, String fallbackAgencyId) {
        String agencyCode = agencyLocationRepo.getAgencyCodeByPortCode(originPortId);
        if (agencyCode == null || agencyCode.trim().isEmpty()) {
            // Fallback to provided value if not found in reportDB
            return fallbackAgencyId;
        }
        return agencyCode;
    }

    /**
     * Create master booking record with updated field mappings
     * @param req The booking request DTO
     * @param customerCodeFromJwt Customer code from JWT
     * @param loginNameFromJwt Login name from JWT 
     * @return Generated booking number
     */
    public String createMaster(BookingRequestDTO req, String customerCodeFromJwt, String loginNameFromJwt) {
        
        // Generate booking number
        String bookNo = generateBookingNumber();
        String agencyCode = req.getAgencyId();
        LocalDateTime now = LocalDateTime.now();
        
        // Build EcomBooking entity with updated field mappings using setters
        EcomBooking entity = new EcomBooking();
        
        entity.setEbookno(bookNo);
        entity.setAgencyid(agencyCode);
        entity.setOriginPortId(req.getOriginPortId());
        entity.setFinalPortId(req.getFinalPortId());
        entity.setLoadPortId(req.getLoadPortId());
        entity.setDischargePortId(req.getDischargePortId());
        entity.setBookDate(req.getBookDate() != null ? req.getBookDate() : now);
        entity.setBookReady(req.getCargoReadyDate() == null ? null : 
            req.getCargoReadyDate().atStartOfDay()); // Convert LocalDate to LocalDateTime
        entity.setPreCarriage(req.getPreCarriage() != null ? req.getPreCarriage().getValue() : null);
        entity.setOnCarriage(req.getOnCarriage() != null ? req.getOnCarriage().getValue() : null);
        entity.setRemarks(req.getRemarks());

        // CHANGED: usercr = login from JWT (NOT customerCode)
        entity.setUsercr(loginNameFromJwt);
        entity.setDatecr(now);
        entity.setEbookStatus(req.getStatus() != null ? req.getStatus().getValue() : null);
        entity.setCustCode(customerCodeFromJwt);

        // CHANGED: Set from DTO using new getters
        entity.setTemplatename(req.getTemplatename());        // Uses alias method
        entity.setRateno(req.getRateno());                    // Uses alias method
        entity.setRouteId(req.getRouteId() != null ? req.getRouteId().longValue() : null);
        entity.setAgencyRefNo(req.getAgencyRefNo());          // Direct method
        entity.setOceanfreight(req.getOceanFreight() != null ? req.getOceanFreight().getValue() : null);
        entity.setTrptsvccode(null);
        entity.setEmptypickup("N");
        entity.setEmptypickupdate(null);
        entity.setVersion(0);
        entity.setIntransitport(null);
        entity.setSubmittedDate(now);

        // CHANGED: dateup = null (do NOT set now)
        entity.setDateup(null);

        entity.setUserup(null);
        entity.setNatCode(null);
        entity.setPickupPoint(null);
        entity.setAppversion(2);
        entity.setSalesagreedrate(null);
        entity.setCcmailid(null);
        entity.setHaulercode(null);
        entity.setHaulagetype(null);
        entity.setPickupdate(null);
        entity.setDropdate(null);
        entity.setExportRef(req.getExportRefNo());
        entity.setReleasetype(null);
        entity.setCustPO(req.getCustomerPO());
        entity.setCustomerreference(null);
        entity.setAcid(null);
        entity.setCargoprotectid(null);
        entity.setShipperReference(null);
        entity.setEnsfiling(req.getEnsFilling() != null ? req.getEnsFilling().getValue() : null);
        entity.setT2lfiling(req.getT2lFilling() != null ? req.getT2lFilling().getValue() : null);
        entity.setSisubmitparty(null);
        entity.setEnsType(req.getEnsType() != null ? req.getEnsType().getValue() : null);

        // Save entity
        bookingRepo.save(entity);
        
        return bookNo;
    }

    /**
     * Insert a minimal {@code ecombooking} master row for a direct LRP booking that has no BKON yet.
     * Child tables and EDI are left to the existing update flow.
     *
     * @param extBookNo LRP {@code booking.book_no}
     * @return New {@code ebookno} (BKON…)
     */
    public String createEcomShellForLrpLink(String extBookNo, BookingRequestDTO req,
                                            String customerCodeFromJwt, String loginNameFromJwt) {
        String lrpBookNo = extBookNo != null ? extBookNo.trim() : "";
        if (lrpBookNo.isEmpty()) {
            throw new IllegalArgumentException("extBookNo is required for ECOM shell creation");
        }

        String bookNo = generateBookingNumber();
        LocalDateTime now = LocalDateTime.now();

        EcomBooking entity = new EcomBooking();
        entity.setEbookno(bookNo);
        entity.setExtBookNo(lrpBookNo);
        entity.setEbookStatus("P");
        entity.setCustCode(customerCodeFromJwt);
        entity.setUsercr(loginNameFromJwt);
        entity.setDatecr(now);
        entity.setSubmittedDate(now);
        entity.setVersion(0);
        entity.setAppversion(2);
        entity.setEmptypickup("N");
        entity.setIsAmendment("N");

        if (req != null) {
            String agencyCode = resolveAgencyCode(req.getOriginPortId(), req.getAgencyId());
            entity.setAgencyid(agencyCode);
            entity.setOriginPortId(req.getOriginPortId());
            entity.setFinalPortId(req.getFinalPortId());
            entity.setLoadPortId(req.getLoadPortId());
            entity.setDischargePortId(req.getDischargePortId());
        }

        bookingRepo.save(entity);
        logger.info("Created ECOM shell booking {} linked to LRP {}", bookNo, lrpBookNo);
        return bookNo;
    }
    
    /**
     * Generate booking number using module index table
     * Delegates to ModuleIndexService for centralized ID generation
     * @return Generated booking number in format: prefix + index (e.g., "BKON1234")
     */
    private String generateBookingNumber() {
        String bookingNumber = moduleIndexService.generateBookingNumber();
        System.out.println("[ECOM] BookingCreateService - Generated booking number: " + bookingNumber);
        return bookingNumber;
    }
    
    /**
     * Add customers for a booking in a separate transactional method
     * @param ebookno The booking number
     * @param customers List of customer DTOs
     * @param loginNameFromJwt Login name from JWT for audit
     */
    public void addCustomers(String ebookno, List<CustomerDTO> customers, String loginNameFromJwt) {
        if (customers == null || customers.isEmpty()) return;

        List<EcomBookingCustomer> rows = new ArrayList<>(customers.size());
        for (int idx = 0; idx < customers.size(); idx++) {
            CustomerDTO c = customers.get(idx);
            
            EcomBookingCustomer row = new EcomBookingCustomer();
            // id is auto-generated (IDENTITY)
            row.setEbookno(ebookno);
            row.setCustTypeId(c.getCustomerType() != null ? c.getCustomerType().getValue() : null);
            row.setCid((long) (idx + 1)); // sequence number
            row.setCustId(nullIfBlank(c.getCustomerCode())); // customerCode -> custId
            row.setCountryId(c.getCountryId());
            row.setName(c.getCustomerName()); // customerName -> name
            row.setAddress1(nullIfBlank(c.getAddress())); // address -> address1
            row.setAddress2(nullIfBlank(c.getAddress1())); // address1 -> address2
            row.setCity(c.getCity());
            row.setZipCode(nullIfBlank(c.getZipCode()));
            row.setTelephone(nullIfBlank(c.getTelephone()));
            row.setFax(null); // Not in DTO, set to null
            row.setEmail(nullIfBlank(c.getEmail()));
            row.setFlag(null); // Not in DTO, set to null
            row.setToorder(null); // Not in DTO, set to null
            row.setStateId(null); // Not in DTO, set to null
            row.setEori(null); // Not in DTO, set to null
            row.setPersontype(null); // Not in DTO, set to null
            rows.add(row);
        }

        customerRepo.saveAll(rows);
    }

    private static String nullIfBlank(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    /**
     * Add routes for a booking in a separate transactional method
     * @param ebookno The booking number
     * @param routes List of route DTOs 
     * @param loginNameFromJwt Login name from JWT for audit
     */
    public void addRoutes(String ebookno, List<RouteDTO> routes, String loginNameFromJwt) {
        if (routes == null || routes.isEmpty()) return;

        List<EcomBookingRoute> rows = new ArrayList<>(routes.size());

        for (RouteDTO r : routes) {
            EcomBookingRoute row = new EcomBookingRoute();
            // id is auto-generated (IDENTITY)
            row.setEbookno(ebookno);
            row.setLegNo(r.getLegNo() != null ? r.getLegNo().shortValue() : null);
            row.setBound(r.getLoadBound() != null ? r.getLoadBound().getValue() : null);
            row.setServiceCode(r.getServiceCode());
            row.setVesselCode(r.getVesselCode());
            row.setVoyageCode(r.getVoyageCode());
            row.setLegTypeId(r.getServiceType() != null ? r.getServiceType().getValue() : null); // serviceType -> legTypeId
            row.setLoadPortId(r.getLoadPortId());
            row.setLoadTerminal(nullIfBlank(r.getLoadTerminal()));
            row.setDischargePortId(r.getDischargePortId());
            row.setDischargeTerminal(nullIfBlank(r.getDischargeTerminal()));
            row.setEtdDate(r.getEtdDate() != null ? Timestamp.valueOf(r.getEtdDate().toLocalDateTime()) : null);
            row.setEtaDate(r.getEtaDate() != null ? Timestamp.valueOf(r.getEtaDate().toLocalDateTime()) : null);
            row.setLoadType(r.getLoadType() != null ? r.getLoadType().getValue() : null);
            row.setDischType(r.getDischType() != null ? r.getDischType().getValue() : null);
            row.setGroupcallid(r.getGroupCallId());
            row.setCallIdDischarge(nullIfBlank(r.getDischargeCallId()));
            row.setCallIdLoading(nullIfBlank(r.getLoadCallId()));
            row.setDbound(r.getDischargeBound() != null ? r.getDischargeBound().getValue() : null); // dischargeBound -> dbound

            rows.add(row);
        }

        routeRepo.saveAll(rows);
    }

    /**
     * Add containers for a booking with optional child details (reefer/OOG/DG).
     * When {@code commodities} are present with {@code itemWeight}, container {@code cargoWeight} and
     * {@code grossWeight} are derived from SUM(itemWeight) keyed by cargoNo|containerNo (SI-aligned).
     */
    public void addContainers(String ebookno, List<ContainerDTO> containers, List<CommodityDTO> commodities,
                              String loginNameFromJwt) {
        if (containers == null || containers.isEmpty()) return;

        List<EcomBookingContainer> rows = new ArrayList<>(containers.size());
        Map<String, Integer> derivedCargoWeightKg =
                BookingCargoWeightNormalizer.computeDerivedCargoWeightByContainer(commodities);

        // Bulk fetch tare weights for all unique equipment sizes (single DB query)
        java.util.Map<String, Float> tareWeightMap = getTareWeightsForContainers(containers);
        logger.debug("Bulk fetched tare weights for {} equipment types", tareWeightMap.size());

        for (int idx = 0; idx < containers.size(); idx++) {
            ContainerDTO c = containers.get(idx);
            
            EcomBookingContainer row = new EcomBookingContainer();
            // id is auto-generated (IDENTITY)
            row.setEbookno(ebookno);
            row.setConid(idx + 1);  // 1-based sequence
            row.setContainerno(c.getContainerNo());
            row.setEqpType(c.getEquipmentType() != null ? c.getEquipmentType().getDbValue() : null);
            row.setEqpSize(c.getEquipmentSize());
            row.setSoc(c.getSocFlag() != null ? c.getSocFlag().getValue() : null);
            row.setRefNor(c.getNor() != null ? c.getNor().getValue() : null);
            row.setQuantity(c.getEquipmentQty());
            row.setDgFlag(c.getDgFlag() != null ? c.getDgFlag().getValue() : null);
            
            // Get tare weight from pre-fetched map (no additional DB call)
            Float tareWeight = c.getEquipmentSize() != null ? tareWeightMap.get(c.getEquipmentSize().trim()) : null;
            row.setTareWeight(tareWeight);

            String containerKey = BookingCargoWeightNormalizer.containerKey(c.getCargoNo(), c.getContainerNo());
            Float cargoWeight;
            String weightUnit;
            if (derivedCargoWeightKg.containsKey(containerKey)) {
                cargoWeight = derivedCargoWeightKg.get(containerKey).floatValue();
                weightUnit = WeightUnit.K.getValue();
            } else {
                cargoWeight = c.getCargoWeight() != null ? c.getCargoWeight().floatValue() : null;
                weightUnit = c.getWeightUnit() != null ? c.getWeightUnit().getValue() : null;
            }
            row.setCargoWeight(cargoWeight);
            row.setGrossWeight(calculateGrossWeight(tareWeight, cargoWeight));
            row.setWeightUnit(weightUnit);
            row.setEqpstatus(c.getEquipmentStatus() != null ? c.getEquipmentStatus().getValue() : null);
            row.setOog(c.getOogFlag() != null ? c.getOogFlag().getValue() : null);

            rows.add(row);
        }

        // Save all container rows first
        containerRepo.saveAll(rows);

        // Process child entities based on flags
        for (int idx = 0; idx < containers.size(); idx++) {
            ContainerDTO c = containers.get(idx);
            int seq = idx + 1;

            // Reefer details - create if equipmentType is R and reefer data exists
            if (c.getEquipmentType() == com.svm.ecom.common.enums.EquipmentType.R && c.getReefer() != null) {
                EcomBookingReefer reefer = new EcomBookingReefer();
                reefer.setEbookno(ebookno);
                Integer conid = c.getCargoNo() != null ? c.getCargoNo() : seq;
                com.solverminds.booking.util.ReeferMappingHelper.populateEntity(
                        reefer, c.getReefer(), conid, c.getContainerNo());
                reeferRepo.save(reefer);
            }

            // OOG details - create if oogFlag is Y
            if (c.getOogFlag() == com.svm.ecom.common.enums.YesNoFlag.Y) {
                EcomBookingOog oog = new EcomBookingOog();
                // id is auto-generated (IDENTITY)
                oog.setEbookno(ebookno);
                oog.setConid(seq);
                oog.setComItemNo(0); // Default value since not in ContainerDTO
                oog.setContainerno(c.getContainerNo());
                // Set OOG dimensions from ContainerDTO
                oog.setLeftft(c.getOogLeftFt());
                oog.setRightft(c.getOogRightFt());
                oog.setForward(c.getOogForward());
                oog.setAlt(c.getOogAft());
                oog.setTopft(c.getOogTopFt());
                oog.setUnit(c.getOogUnit() != null ? c.getOogUnit().getValue() : null);
                oogRepo.save(oog);
            }

            // NOTE: DG details are saved in addCommodities() from commodities[].dangerousGoods
        }
    }

    /**
     * Insert commodities; requires containers list to derive containerNo -> conid mapping (1..n).
     * Also saves nested dangerous goods for each commodity.
     * @param ebookno The booking number
     * @param commodities List of commodity DTOs
     * @param containers List of container DTOs (used for containerNo -> conid mapping)
     * @param loginNameFromJwt Login name from JWT for audit
     */
    public void addCommodities(String ebookno,
                               java.util.List<CommodityDTO> commodities,
                               java.util.List<ContainerDTO> containers,
                               String loginNameFromJwt) {
        if (commodities == null || commodities.isEmpty()) return;

        // Build containerNo -> conid map from the containers[] order (same order used for container insert)
        java.util.Map<String, Integer> containerConidByNo = new java.util.HashMap<>();
        if (containers != null) {
            for (int i = 0; i < containers.size(); i++) {
                String no = safe(containers.get(i).getContainerNo());
                if (!no.isEmpty()) containerConidByNo.put(no, i + 1); // 1-based conid
            }
        }

        java.util.List<EcomBookingCommodity> rows = new java.util.ArrayList<>(commodities.size());
        java.util.List<EcomBookingDg> dgRows = new java.util.ArrayList<>();

        // Enrich DG read-only IMDG / packaging fields before persist (stores amendment + derived fields)
        BookingRequestDTO enrichHolder = new BookingRequestDTO();
        enrichHolder.setCommodities(commodities);
        dangerousGoodsEnrichmentService.enrichRequest(enrichHolder);

        for (CommodityDTO c : commodities) {
            String cNo = safe(c.getContainerNo());
            Integer conid = containerConidByNo.get(cNo);

            // If for some reason mapping is missing, either skip or throw; here we fail-fast.
            if (conid == null) {
                throw new IllegalArgumentException("Container number not found for commodity: " + cNo);
            }

            EcomBookingCommodity row = new EcomBookingCommodity();
            // id is auto-generated (IDENTITY)
            row.setEbookno(ebookno);
            row.setConid(conid);
            row.setItemno(c.getItemNo());
            row.setContainerno(c.getContainerNo());
            row.setCommodityFlag("N"); // Not in DTO
            row.setCommodity(c.getCommodityGroup() != null && !c.getCommodityGroup().trim().isEmpty() ? c.getCommodityGroup().trim() : null);
            row.setHsCommodity(n(c.getHsCode()));
            row.setPackageType(n(c.getPackageType()));
            row.setPackageQty(c.getPackageQty());
            row.setCargoVolume(c.getCargoVolume() != null ? c.getCargoVolume().floatValue() : null);
            row.setVolumnUnit(c.getVolumnUnit() != null ? c.getVolumnUnit().getValue() : null);
            row.setMarksNo(null); // Not in DTO
            row.setDescr(null); // Not in DTO
            row.setNettwt(c.getNettwt());
            BookingCargoWeightNormalizer.applyCommodityItemWeight(row, c);
            row.setBycode(n(c.getBuyerCode()));
            row.setSlcode(n(c.getSellerCode()));
            row.setByname(n(c.getBuyerName()));
            row.setSlname(n(c.getSellerName()));
            row.setCmdtyHblno(n(c.getCommodityHBLNo()));

            rows.add(row);

            DangerousGoodsInfoV2 dgi = c.getDangerousGoods();
            if (dgi != null && hasDangerousGoodsPayload(dgi)) {
                DangerousGoodsDTO dg = bookingMapperV2.toV1DangerousGoods(dgi);
                dg.setCargoNo(c.getCargoNo());
                dg.setContainerNo(c.getContainerNo());
                dg.setItemNo(c.getItemNo() != null ? c.getItemNo() : dg.getItemNo());
                EcomBookingDg dgEntity = dangerousGoodsMapper.toEntity(dg, ebookno, conid, c.getContainerNo());
                if (dgEntity.getItemno() == null) {
                    dgEntity.setItemno(c.getItemNo());
                }
                dgRows.add(dgEntity);
                logger.debug("Adding DG record for booking {}, container {}, itemNo {}, UN: {}, Class: {}",
                        ebookno, conid, dgEntity.getItemno(), dgEntity.getUnNo(), dgEntity.getClassCode());
            }
        }

        // Save all commodity rows
        commodityRepo.saveAll(rows);
        
        // Save all dangerous goods rows
        if (!dgRows.isEmpty()) {
            dgRepo.saveAll(dgRows);
            logger.info("Saved {} dangerous goods records for booking {}", dgRows.size(), ebookno);
        }
    }

    private static boolean hasDangerousGoodsPayload(DangerousGoodsInfoV2 dgi) {
        if (dgi == null) {
            return false;
        }
        if (notBlank(dgi.getUnNo()) || notBlank(dgi.getImoClass()) || notBlank(dgi.getTechnicalName())
                || notBlank(dgi.getVariant()) || notBlank(dgi.getSegregation())
                || notBlank(dgi.getFullPackagingCode())) {
            return true;
        }
        if (dgi.getFlashPoint() != null || dgi.getFlashPointUnit() != null) {
            return true;
        }
        if (dgi.getIsMarinePollutant() != null || dgi.getIsLimitedQuantity() != null
                || dgi.getIsExceptedQuantity() != null || dgi.getIsReportableQuantity() != null
                || dgi.getIsWaste() != null) {
            return true;
        }
        if (dgi.getGrossWeight() != null || dgi.getNetWeight() != null || dgi.getNetExplosiveWeight() != null
                || dgi.getOuterPackingQuantity() != null || dgi.getInnerPackingQuantity() != null) {
            return true;
        }
        if (dgi.getSadt() != null || dgi.getControlTemperature() != null
                || dgi.getEmergencyTemperature() != null || dgi.getTemperatureUnit() != null) {
            return true;
        }
        if (dgi.getEmergencyContact() != null) {
            if (notBlank(dgi.getEmergencyContact().getContactName())
                    || notBlank(dgi.getEmergencyContact().getCountryCode())
                    || notBlank(dgi.getEmergencyContact().getAreaCode())
                    || notBlank(dgi.getEmergencyContact().getLocalNumber())) {
                return true;
            }
        }
        if (dgi.getOuterPacking() != null && notBlank(dgi.getOuterPacking().getCode())) {
            return true;
        }
        return dgi.getInnerPacking() != null && notBlank(dgi.getInnerPacking().getCode());
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static String safe(String s){ return s == null ? "" : s.trim(); }
    private static String n(String s){ return (s == null || s.trim().isEmpty()) ? null : s.trim(); }

    /**
     * Bulk fetch tare weights for all containers in a single database query.
     * Collects unique equipment sizes and fetches their tare weights.
     * 
     * @param containers List of container DTOs
     * @return Map of equipment size to tare weight
     */
    public java.util.Map<String, Float> getTareWeightsForContainers(List<ContainerDTO> containers) {
        if (containers == null || containers.isEmpty()) {
            return new java.util.HashMap<>();
        }
        
        // Collect unique equipment sizes
        java.util.Set<String> equipmentSizes = containers.stream()
                .map(ContainerDTO::getEquipmentSize)
                .filter(size -> size != null && !size.trim().isEmpty())
                .collect(java.util.stream.Collectors.toSet());
        
        // Bulk fetch tare weights from database
        return equipmentRepo.getTareWeightsByEquipTypes(equipmentSizes);
    }

    /**
     * Calculate gross weight = tare weight + cargo weight.
     * Returns cargo weight if tare weight is not available.
     * 
     * @param tareWeight The tare weight (can be null)
     * @param cargoWeight The cargo weight (can be null)
     * @return The calculated gross weight
     */
    public static Float calculateGrossWeight(Float tareWeight, Float cargoWeight) {
        if (tareWeight != null && cargoWeight != null) {
            return tareWeight + cargoWeight;
        } else if (cargoWeight != null) {
            // If tare weight not available, return cargo weight only
            return cargoWeight;
        }
        return null;
    }
}
