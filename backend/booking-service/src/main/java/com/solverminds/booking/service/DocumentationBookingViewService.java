package com.solverminds.booking.service;

import com.solverminds.booking.dto.*;
import com.solverminds.booking.common.entity.*;
import com.solverminds.booking.mapper.BookingMapperV2;
import com.solverminds.booking.repo.ScheduleTerminalRepository;
import com.svm.ecom.common.enums.*;
import com.solverminds.booking.common.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.svm.ecom.common.util.PortLocalTimeUtil;

/**
 * Service for retrieving booking details from Documentation database (booking tables).
 * Used when bookingNumber (book_no) parameter is provided instead of onlineBookNo (ebookno).
 * 
 * Maps from booking-common entities to BookingRequestDTO.
 */
@Service
@Transactional(transactionManager = "documentationTransactionManager", readOnly = true)
public class DocumentationBookingViewService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentationBookingViewService.class);

    private final BookingViewRepository bookingRepo;
    private final BookingCustViewRepository custRepo;
    private final BookingRoutingViewRepository routingRepo;
    private final BookingReferenceViewRepository referenceRepo;
    private final BookingCargoViewRepository cargoRepo;
    private final BookingCommodityViewRepository commodityRepo;
    private final NdgMasterViewRepository ndgMasterRepo;
    private final NdgDetailViewRepository ndgDetailRepo;
    private final BookingLegViewRepository legRepo;
    private final MasterDataCacheService masterDataCacheService;
    private final DocumentService documentService;
    private final TeusCalculationService teusCalculationService;
    private final BookingMapperV2 bookingMapperV2;
    private final ScheduleTerminalRepository scheduleTerminalRepository;

    public DocumentationBookingViewService(
            BookingViewRepository bookingRepo,
            BookingCustViewRepository custRepo,
            BookingRoutingViewRepository routingRepo,
            BookingReferenceViewRepository referenceRepo,
            BookingCargoViewRepository cargoRepo,
            BookingCommodityViewRepository commodityRepo,
            NdgMasterViewRepository ndgMasterRepo,
            NdgDetailViewRepository ndgDetailRepo,
            BookingLegViewRepository legRepo,
            MasterDataCacheService masterDataCacheService,
            DocumentService documentService,
            TeusCalculationService teusCalculationService,
            BookingMapperV2 bookingMapperV2,
            ScheduleTerminalRepository scheduleTerminalRepository) {
        this.bookingRepo = bookingRepo;
        this.custRepo = custRepo;
        this.routingRepo = routingRepo;
        this.referenceRepo = referenceRepo;
        this.cargoRepo = cargoRepo;
        this.commodityRepo = commodityRepo;
        this.ndgMasterRepo = ndgMasterRepo;
        this.ndgDetailRepo = ndgDetailRepo;
        this.legRepo = legRepo;
        this.masterDataCacheService = masterDataCacheService;
        this.documentService = documentService;
        this.teusCalculationService = teusCalculationService;
        this.bookingMapperV2 = bookingMapperV2;
        this.scheduleTerminalRepository = scheduleTerminalRepository;
    }

    /**
     * Find a booking by bookingNumber (book_no from Documentation database).
     * 
     * @param bookingNumber The booking number (book_no) to search for
     * @return Optional containing the complete booking DTO if found
     */
    public Optional<BookingRequestDTO> findByBookingNumber(String bookingNumber) {
        if (bookingNumber == null || bookingNumber.trim().isEmpty()) {
            return Optional.empty();
        }

        logger.debug("Looking up booking from Documentation DB: {}", bookingNumber);

        // Find the main booking entity
        Optional<Booking> bookingOpt = bookingRepo.findByBookNo(bookingNumber.trim());
        
        if (!bookingOpt.isPresent()) {
            logger.debug("Booking not found in Documentation DB: {}", bookingNumber);
            return Optional.empty();
        }

        Booking booking = bookingOpt.get();
        String bookNo = booking.getBookNo();

        // Build the complete DTO
        BookingRequestDTO dto = new BookingRequestDTO();
        
        // Map master booking fields
        mapMasterFields(booking, dto, bookNo);
        
        // Fetch and map related entities (single load of booking_cargo for containers + commodity line weights)
        List<BookingCargo> cargoes = cargoRepo.findByBookNoOrderByCargoNo(bookNo);
        dto.setCustomers(fetchCustomers(bookNo));
        dto.setContainers(fetchContainers(bookNo, cargoes));
        dto.setCommodities(fetchCommodities(bookNo, cargoes));
        dto.setRoutes(fetchRoutes(booking, bookNo));
        dto.setFiles(fetchFiles(bookNo));
        
        // TEUs: use onlineBookNo from DAK reference when available (links to ECOM)
        if (dto.getOnlineBookNo() != null && !dto.getOnlineBookNo().trim().isEmpty()) {
            dto.setTeus(teusCalculationService.calculateTeus(dto.getOnlineBookNo().trim()));
        }
        
        logger.debug("Successfully retrieved booking from Documentation DB: {}", bookingNumber);
        return Optional.of(dto);
    }

    /**
     * Map master booking fields from Booking entity to DTO.
     * Based on mapping from Booking.xlsx:
     * - bookingNumber -> book_no
     * - agencyId -> agencyid
     * - originPortId -> originportid
     * - finalPortId -> finalportid
     * - loadPortId -> loadportid
     * - dischargePortId -> dischargeportid
     * - bookDate -> bookdate
     * - cargoReadyDate -> bookReady
     * - preCarriage -> precarriage (enum)
     * - onCarriage -> oncarriage (enum)
     * - remarks -> custremarks
     * - status -> status (enum)
     * - ensFilling -> ensfiling (enum)
     * - t2lFilling -> t2lfiling (enum)
     * - ensType -> enstype (enum)
     * - agencyRefNo -> agencyrefno
     * - submittedDate -> datecr (usercr date)
     */
    private void mapMasterFields(Booking entity, BookingRequestDTO dto, String bookNo) {
        // Primary key and identifiers
        dto.setBookingNumber(entity.getBookNo());
        dto.setAgencyId(entity.getAgencyId());
        
        // No onlineBookNo in Documentation DB (EDI doesn't store ebookno)
        dto.setOnlineBookNo(null);
        
        // Port information
        dto.setOriginPortId(entity.getOriginPortId());
        dto.setFinalPortId(entity.getFinalPortId());
        dto.setLoadPortId(entity.getLoadPortId());
        dto.setDischargePortId(entity.getDischargePortId());
        
        // Dates
        dto.setBookDate(entity.getBookDate());
        dto.setCargoReadyDate(entity.getBookReady() != null ? entity.getBookReady().toLocalDate() : null);
        dto.setSubmittedDate(entity.getDatecr());
        dto.setDatecr(entity.getDatecr());
        dto.setDateup(entity.getDateup());
        
        // Carriage types (convert String to enum)
        dto.setPreCarriage(safeCarriageType(entity.getPreCarriage()));
        dto.setOnCarriage(safeCarriageType(entity.getOnCarriage()));
        
        // Remarks - from custremarks field
        dto.setRemarks(entity.getCustRemarks());
        
        // Status: booking table D -> P (In-Progress) for LRP display
        String statusFromDb = entity.getStatus();
        if (statusFromDb != null && "D".equalsIgnoreCase(statusFromDb.trim())) {
            dto.setStatus(BookingStatus.P);
        } else {
            dto.setStatus(safeBookingStatus(statusFromDb));
        }
        
        // Agency reference
        dto.setAgencyRefNo(entity.getAgencyRefNo());
        
        // ENS/T2L fields
        dto.setEnsFilling(safeEnsFillingType(entity.getEnsFiling()));
        dto.setT2lFilling(safeYesNoFlag(entity.getT2lFiling()));
        dto.setEnsType(safeEnsType(entity.getEnsType()));
        
        // Created by and confirmed date (LRP specific)
        dto.setCreatedBy(entity.getUsercr());
        dto.setConfirmedAt(entity.getConfirmedDate());

        // Booking source (read-only): booksource column already loaded on entity, response-layer mapping only
        dto.setBookingSource(safeBookingSource(entity.getBookSource()));
        
        // Get customerCode from booking_cust (find customer with BP type)
        Optional<BookingCust> bpCustomer = custRepo.findByBookNoAndCustTypeId(bookNo, "BP");
        if (bpCustomer.isPresent()) {
            dto.setCustomerCode(bpCustomer.get().getCustId());
        }
        
        // Get routeId from booking_routing
        Optional<BookingRouting> routing = routingRepo.findFirstByBookNo(bookNo);
        if (routing.isPresent() && routing.get().getRouteId() != null) {
            dto.setRouteId(routing.get().getRouteId());
        }
        
        // Get references: customerPO (SHR), rateRefNo (EOR), exportRefNo (ERF), oceanFreight (EOF)
        mapReferences(bookNo, dto);
        
        // Template name not available in EDI
        dto.setTemplateName(null);
    }

    /**
     * Map booking references to DTO fields.
     * Reference types:
     * - SHR -> customerPO
     * - EOR -> rateRefNo
     * - ERF -> exportRefNo
     * - EOF -> oceanFreight
     * - DAK -> onlineBookNo (ECOM booking number)
     */
    private void mapReferences(String bookNo, BookingRequestDTO dto) {
        List<BookingReference> references = referenceRepo.findByBookNoOrderById(bookNo);
        
        for (BookingReference ref : references) {
            if (ref.getRefId() == null) continue;
            
            String refId = ref.getRefId().trim().toUpperCase();
            String refVal = ref.getRefVal();
            
            switch (refId) {
                case "SHR":
                    dto.setCustomerPO(refVal);
                    break;
                case "EOR":
                    dto.setRateRefNo(refVal);
                    break;
                case "ERF":
                    dto.setExportRefNo(refVal);
                    break;
                case "EOF":
                    dto.setOceanFreight(safeFreightPaymentType(refVal));
                    break;
                case "DAK":
                    // DAK reference contains the ECOM online booking number
                    dto.setOnlineBookNo(refVal);
                    break;
                default:
                    // Other reference types - ignore for now
                    break;
            }
        }
    }

    /**
     * Fetch customers for a booking from booking_cust table.
     */
    private List<CustomerDTO> fetchCustomers(String bookNo) {
        List<BookingCust> entities = custRepo.findByBookNoOrderByCid(bookNo);
        List<CustomerDTO> dtos = new ArrayList<>();
        
        for (BookingCust entity : entities) {
            CustomerDTO dto = new CustomerDTO();
            dto.setCustomerType(safeCustomerType(entity.getCustTypeId()));
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
     * Fetch containers from booking_cargo table.
     */
    private List<ContainerDTO> fetchContainers(String bookNo) {
        return fetchContainers(bookNo, cargoRepo.findByBookNoOrderByCargoNo(bookNo));
    }

    /**
     * Map pre-loaded {@code booking_cargo} rows to container DTOs (avoids duplicate query when commodities need cargo weights).
     */
    private List<ContainerDTO> fetchContainers(String bookNo, List<BookingCargo> entities) {
        List<ContainerDTO> dtos = new ArrayList<>();
        
        for (BookingCargo entity : entities) {
            ContainerDTO dto = new ContainerDTO();
            dto.setCargoNo(entity.getCargoNo());
            dto.setOogFlag(safeYesNoFlag(entity.getOog()));
            dto.setEquipmentQty(1); // Default - Documentation doesn't have quantity per row
            dto.setEquipmentType(safeEquipmentType(entity.getType()));
            dto.setEquipmentSize(entity.getEqpTypeId());
            dto.setSocFlag(safeYesNoFlag(entity.getShipperOwn()));
            dto.setDgFlag(safeYesNoFlag(entity.getDgFlag()));
            dto.setEquipmentStatus(safeEquipmentStatus(entity.getEqpStatus()));
            dto.setCargoWeight(entity.getCargoWeight() != null ? entity.getCargoWeight().intValue() : null);
            dto.setWeightUnit(safeWeightUnit(entity.getWeightUnit()));
            dto.setNor(safeYesNoFlag(entity.getNor()));
            dto.setContainerNo(entity.getEqpId());
            dto.setTareWeight(entity.getTareWeight() != null ? entity.getTareWeight().floatValue() : null);
            dto.setGrossWeight(entity.getGrossWeight() != null ? entity.getGrossWeight().floatValue() : null);
            
            // Fetch reefer settings if type is R
            if ("R".equalsIgnoreCase(entity.getType())) {
                ReeferSettingDTO reefer = buildReeferFromCargo(entity);
                dto.setReefer(reefer);
            }
            
            dtos.add(dto);
        }
        
        return dtos;
    }

    /**
     * Build reefer settings from cargo entity.
     */
    private ReeferSettingDTO buildReeferFromCargo(BookingCargo cargo) {
        ReeferSettingDTO reefer = new ReeferSettingDTO();
        reefer.setSetTemp(cargo.getSetTemp() != null ? cargo.getSetTemp().intValue() : null);
        if (cargo.getTempUnit() != null && !cargo.getTempUnit().trim().isEmpty()) {
            reefer.setTempUnit(TemperatureUnit.fromValue(cargo.getTempUnit()));
        }
        if (cargo.getTempMin() != null) {
            reefer.setMinTemp(cargo.getTempMin());
        }
        if (cargo.getTempMax() != null) {
            reefer.setMaxTemp(cargo.getTempMax());
        }
        if (cargo.getHumidity() != null && !cargo.getHumidity().isEmpty()) {
            Double humidity = com.solverminds.booking.util.ReeferMappingHelper.parseDoubleSafe(cargo.getHumidity());
            if (humidity != null) {
                reefer.setHumidity(humidity);
            }
        }
        if (cargo.getHumidityFlag() != null) {
            reefer.setHumidityFlag(Boolean.TRUE.equals(cargo.getHumidityFlag()) ? YesNoFlag.Y : YesNoFlag.N);
        }
        if (cargo.getDrains() != null && !cargo.getDrains().isEmpty()) {
            reefer.setDrains(cargo.getDrains());
        }
        if (cargo.getO2Value() != null && !cargo.getO2Value().isEmpty()) {
            Double o2 = com.solverminds.booking.util.ReeferMappingHelper.parseDoubleSafe(cargo.getO2Value());
            if (o2 != null) {
                reefer.setO2Value(o2);
            }
        }
        if (cargo.getCo2Value() != null && !cargo.getCo2Value().isEmpty()) {
            Double co2 = com.solverminds.booking.util.ReeferMappingHelper.parseDoubleSafe(cargo.getCo2Value());
            if (co2 != null) {
                reefer.setCo2Value(co2);
            }
        }
        String ventPct = cargo.getVentPercentage();
        if (ventPct != null && !ventPct.trim().isEmpty()) {
            reefer.setVentilation(ventPct.trim());
        }
        if (cargo.getVentSetting() != null) {
            reefer.setVentSet(cargo.getVentSetting());
        }
        return reefer;
    }

    /**
     * Fetch commodities from booking_commodity table with dangerous goods.
     * {@code itemWeight} / {@code itemWeightUnit} are derived from {@code booking_cargo} ({@code cargoWeight} / {@code weightUnit}) by cargo number / equipment id.
     */
    private List<CommodityDTO> fetchCommodities(String bookNo, List<BookingCargo> cargoes) {
        Map<Integer, BookingCargo> cargoByCargoNo = new HashMap<>();
        Map<String, BookingCargo> cargoByEqpId = new HashMap<>();
        for (BookingCargo c : cargoes) {
            if (c.getCargoNo() != null) {
                cargoByCargoNo.put(c.getCargoNo(), c);
            }
            if (c.getEqpId() != null && !c.getEqpId().trim().isEmpty()) {
                cargoByEqpId.put(c.getEqpId().trim(), c);
            }
        }

        List<BookingCommodity> entities = commodityRepo.findByBookNoOrderByCargoNoAndItemNo(bookNo);
        List<CommodityDTO> dtos = new ArrayList<>();
        
        for (BookingCommodity entity : entities) {
            CommodityDTO dto = new CommodityDTO();
            dto.setCargoNo(entity.getCargoNo());
            dto.setContainerNo(entity.getEqpId());
            dto.setItemNo(entity.getItemNo());
            dto.setCommodityGroup(entity.getCommodityGroup()); // String preserves leading zeros e.g. "09"
            dto.setHsCode(entity.getCommodityId()); // from commodityId column
            dto.setPackageType(entity.getPackageId());
            dto.setPackageQty(entity.getItemQty());
            dto.setCargoVolume(entity.getItemVolume() != null ? entity.getItemVolume().doubleValue() : null);
            dto.setVolumnUnit(VolumeUnit.fromValue(entity.getItemVolumeUnit()));
            dto.setNettwt(entity.getNetWeight() != null ? entity.getNetWeight().doubleValue() : null);
            applyItemWeightFromBookingCargo(dto, entity, cargoByCargoNo, cargoByEqpId);
            dto.setBuyerCode(entity.getByCode());
            dto.setBuyerName(entity.getByName());
            dto.setSellerCode(entity.getSlCode());
            dto.setSellerName(entity.getSlName());
            dto.setCommodityHBLNo(entity.getCommodityHblNo());
            
            // Fetch dangerous goods if hazardous flag is Y (ndgmaster / ndgdetail)
            if ("Y".equalsIgnoreCase(entity.getHazardous())) {
                List<DangerousGoodsDTO> dgList = fetchDangerousGoods(
                        bookNo, entity.getEqpId(), entity.getCargoNo(), entity.getItemNo());
                if (dgList != null && !dgList.isEmpty()) {
                    dto.setDangerousGoods(bookingMapperV2.toV2DangerousGoods(dgList.get(0)));
                }
            }
            
            dtos.add(dto);
        }
        
        return dtos;
    }

    private void applyItemWeightFromBookingCargo(CommodityDTO dto, BookingCommodity entity,
            Map<Integer, BookingCargo> cargoByCargoNo,
            Map<String, BookingCargo> cargoByEqpId) {
        BookingCargo cargo = null;
        if (entity.getCargoNo() != null) {
            cargo = cargoByCargoNo.get(entity.getCargoNo());
        }
        if (cargo == null && entity.getEqpId() != null && !entity.getEqpId().trim().isEmpty()) {
            cargo = cargoByEqpId.get(entity.getEqpId().trim());
        }
        if (cargo != null) {
            if (cargo.getCargoWeight() != null) {
                dto.setItemWeight(cargo.getCargoWeight().floatValue());
            }
            dto.setItemWeightUnit(safeWeightUnit(cargo.getWeightUnit()));
        }
    }

    /**
     * Fetch dangerous goods from {@code ndgmaster} / {@code ndgdetail} for LRP (documentation) bookings.
     * Resolves {@code dgNo} by {@code PartDgRefNo = book_no}, then loads detail rows.
     */
    private List<DangerousGoodsDTO> fetchDangerousGoods(String bookNo, String eqpId,
            Integer cargoNo, Integer itemNo) {
        List<NdgMaster> masters = ndgMasterRepo.findByPartDgRefNo(bookNo.trim());
        if (masters.isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> dgNos = masters.stream()
                .map(NdgMaster::getDgNo)
                .filter(d -> d != null && !d.trim().isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (dgNos.isEmpty()) {
            return new ArrayList<>();
        }
        List<NdgDetail> rows = ndgDetailRepo.findByDgNoInOrderById(dgNos);
        if (eqpId != null && !eqpId.trim().isEmpty()) {
            String eqp = eqpId.trim();
            rows = rows.stream()
                    .filter(r -> r.getContainerNo() != null && eqp.equalsIgnoreCase(r.getContainerNo().trim()))
                    .collect(Collectors.toList());
        }
        List<DangerousGoodsDTO> dtos = new ArrayList<>();
        for (NdgDetail row : rows) {
            DangerousGoodsDTO dto = new DangerousGoodsDTO();
            dto.setCargoNo(cargoNo);
            dto.setItemNo(itemNo);
            dto.setContainerNo(row.getContainerNo() != null ? row.getContainerNo().trim() : eqpId);
            dto.setMarinePollutant(safeYesNoFlag(row.getMarinePollutant()));
            dto.setUnNo(row.getUnNo());
            dto.setFlashPt(row.getFlashPoint() != null ? row.getFlashPoint().intValue() : null);
            dto.setFlashPtUnit(TemperatureUnit.fromValue(normalizeNdgFlashUnit(row.getFlashUnit())));
            dto.setClassCode(row.getImdgClass());
            dtos.add(dto);
        }
        return dtos;
    }

    /**
     * ndgdetail may store {@code N} for flash unit when no C/F applies; treat as empty for {@link TemperatureUnit}.
     */
    private static String normalizeNdgFlashUnit(String flashUnit) {
        if (flashUnit == null) {
            return null;
        }
        String t = flashUnit.trim();
        if (t.isEmpty() || "N".equalsIgnoreCase(t)) {
            return null;
        }
        return t;
    }

    /**
     * Fetch routes from booking_leg table.
     */
    private List<RouteDTO> fetchRoutes(Booking booking, String bookNo) {
        List<BookingLeg> entities = legRepo.findByBookNoOrderByLegNo(bookNo);
        List<RouteDTO> dtos = new ArrayList<>();
        
        for (BookingLeg entity : entities) {
            RouteDTO dto = new RouteDTO();
            dto.setLegNo(entity.getLegNo() != null ? entity.getLegNo().intValue() : null);
            dto.setLoadBound(Bound.fromValue(entity.getBound()));
            dto.setServiceCode(entity.getServiceCode());
            dto.setVesselCode(entity.getVesselCode());
            dto.setVoyageCode(entity.getVoyageCode());
            dto.setServiceType(TransportMode.fromValue(entity.getLegTypeId()));
            dto.setLoadPortId(entity.getLoadPortId());
            dto.setLoadTerminal(entity.getPolTerminal());
            dto.setDischargePortId(entity.getDischargePortId());
            dto.setDischargeTerminal(entity.getPodTerminal());
            OffsetDateTime etdOdt = entity.getEtdDate() != null ? entity.getEtdDate().atOffset(ZoneOffset.UTC) : null;
            OffsetDateTime etaOdt = entity.getEtaDate() != null ? entity.getEtaDate().atOffset(ZoneOffset.UTC) : null;
            String tzEtd = masterDataCacheService != null ? PortLocalTimeUtil.normalizeTimezoneValue(masterDataCacheService.getTimezoneOffset(entity.getLoadPortId())) : null;
            String tzEta = masterDataCacheService != null ? PortLocalTimeUtil.normalizeTimezoneValue(masterDataCacheService.getTimezoneOffset(entity.getDischargePortId())) : null;
            dto.setEtdDate(etdOdt != null ? PortLocalTimeUtil.labelWithOffset(etdOdt, tzEtd) : null);
            dto.setEtaDate(etaOdt != null ? PortLocalTimeUtil.labelWithOffset(etaOdt, tzEta) : null);
            dto.setLoadType(PortCallType.fromValue(entity.getLoadType()));
            dto.setDischType(PortCallType.fromValue(entity.getDischType()));
            dto.setGroupCallId(entity.getCallIdLoading());
            dto.setDischargeCallId(entity.getCallIdDischarge());
            dto.setLoadCallId(entity.getCallIdLoading());
            dto.setDischargeBound(Bound.fromValue(entity.getDbound()));
            dtos.add(dto);
        }

        resolveMissingTerminals(booking, dtos);
        return dtos;
    }

    /**
     * Post-process route legs: replace invalid ({@code N}) terminals using booking master
     * and, for multi-leg bookings, a single batch schedule lookup.
     */
    private void resolveMissingTerminals(Booking booking, List<RouteDTO> routes) {
        if (booking == null || routes == null || routes.isEmpty() || !hasInvalidTerminal(routes)) {
            return;
        }

        if (routes.size() == 1) {
            RouteDTO leg = routes.get(0);
            if (isInvalidTerminal(leg.getLoadTerminal())) {
                leg.setLoadTerminal(trimToNull(booking.getPolTerminal()));
            }
            if (isInvalidTerminal(leg.getDischargeTerminal())) {
                leg.setDischargeTerminal(trimToNull(booking.getPodTerminal()));
            }
            return;
        }

        int lastIndex = routes.size() - 1;
        RouteDTO firstLeg = routes.get(0);
        RouteDTO lastLeg = routes.get(lastIndex);
        if (isInvalidTerminal(firstLeg.getLoadTerminal())) {
            firstLeg.setLoadTerminal(trimToNull(booking.getPolTerminal()));
        }
        if (isInvalidTerminal(lastLeg.getDischargeTerminal())) {
            lastLeg.setDischargeTerminal(trimToNull(booking.getPodTerminal()));
        }

        Set<String> callIds = new LinkedHashSet<>();
        for (RouteDTO route : routes) {
            if (isInvalidTerminal(route.getLoadTerminal())) {
                addCallId(callIds, route.getLoadCallId());
            }
            if (isInvalidTerminal(route.getDischargeTerminal())) {
                addCallId(callIds, route.getDischargeCallId());
            }
        }
        if (callIds.isEmpty()) {
            return;
        }

        Map<String, ScheduleTerminalRepository.ScheduleTerminal> terminalsByCallId =
                scheduleTerminalRepository.findByGroupCallIds(callIds);
        if (terminalsByCallId.isEmpty()) {
            return;
        }

        for (RouteDTO route : routes) {
            if (isInvalidTerminal(route.getLoadTerminal())) {
                String resolved = resolveTerminalFromSchedule(terminalsByCallId, route.getLoadCallId());
                if (resolved != null) {
                    route.setLoadTerminal(resolved);
                }
            }
            if (isInvalidTerminal(route.getDischargeTerminal())) {
                String resolved = resolveTerminalFromSchedule(terminalsByCallId, route.getDischargeCallId());
                if (resolved != null) {
                    route.setDischargeTerminal(resolved);
                }
            }
        }
    }

    private static boolean hasInvalidTerminal(List<RouteDTO> routes) {
        for (RouteDTO route : routes) {
            if (isInvalidTerminal(route.getLoadTerminal()) || isInvalidTerminal(route.getDischargeTerminal())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInvalidTerminal(String terminal) {
        return terminal != null && "N".equalsIgnoreCase(terminal.trim());
    }

    private static void addCallId(Set<String> callIds, String callId) {
        if (callId != null && !callId.trim().isEmpty()) {
            callIds.add(callId.trim());
        }
    }

    private static String resolveTerminalFromSchedule(
            Map<String, ScheduleTerminalRepository.ScheduleTerminal> terminalsByCallId, String callId) {
        if (callId == null || callId.trim().isEmpty()) {
            return null;
        }
        ScheduleTerminalRepository.ScheduleTerminal row = terminalsByCallId.get(callId.trim());
        return row != null ? row.getTerminal() : null;
    }

    private static String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    /**
     * Fetch file attachments from document folder (same as documents list endpoint).
     */
    private List<FileAttachmentDTO> fetchFiles(String bookNo) {
        if (documentService == null) {
            return new ArrayList<>();
        }
        List<DocumentListResponse> docs = documentService.listDocumentsForBooking(bookNo);
        List<FileAttachmentDTO> result = new ArrayList<>();
        for (DocumentListResponse doc : docs) {
            if (doc.getFileName() != null && !doc.getFileName().trim().isEmpty()) {
                result.add(new FileAttachmentDTO("DOC", doc.getFileName(),
                    doc.getSizeBytes() != null ? doc.getSizeBytes() : 0L));
            }
        }
        return result;
    }

    // ===== Safe enum conversion helpers =====

    private CarriageType safeCarriageType(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return CarriageType.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private BookingStatus safeBookingStatus(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return BookingStatus.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private YesNoFlag safeYesNoFlag(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return YesNoFlag.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private EnsFillingType safeEnsFillingType(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return EnsFillingType.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private EnsType safeEnsType(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return EnsType.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private FreightPaymentType safeFreightPaymentType(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return FreightPaymentType.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private CustomerType safeCustomerType(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return CustomerType.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Safe BookingSource conversion. Handles null/blank/unrecognized DB values by returning null
     * instead of throwing, so the {@code booksource} column value can never fail the View response.
     */
    private BookingSource safeBookingSource(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return BookingSource.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private EquipmentType safeEquipmentType(String value) {
        if (value == null || value.trim().isEmpty()) return EquipmentType.D; // Default to Dry
        String v = value.trim().toUpperCase();
        // Handle various database values
        if ("R".equals(v)) {
            return EquipmentType.R; // Reefer
        } else {
            return EquipmentType.D; // Default: Dry (D, N, or any other value)
        }
    }

    private EquipmentStatus safeEquipmentStatus(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return EquipmentStatus.fromValue(value.trim());
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

    private Integer parseIntSafe(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

