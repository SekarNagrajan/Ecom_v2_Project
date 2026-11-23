package com.solverminds.sibl.service;

import com.solverminds.sibl.dto.*;
import com.svm.ecom.common.enums.*;
import com.solverminds.sibl.repo.*;
import com.solverminds.sibl.repo.BookingDetailRepository.BookingDetail;
import com.solverminds.edi.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service to export SIBL (Shipping Instruction / Bill of Lading) data to EDI temp tables.
 * Same approach as booking EdiExportService: when called, generates a new unique edireaderid and does a full insert into
 * editempbooking, editempbooking_cargo, editempbooking_commodity, editempbooking_cust, editempbooking_reference,
 * editempbooking_routing, editempbooking_remarks, editempbooking_conf, edidocstatus, edireadermaster.
 * Called on SI create (status 'S') and SI update (status 'S') so editempbooking and child tables are inserted.
 * Bean name "siblEdiExportService" avoids conflict with booking-service's EdiExportService when both are on the classpath.
 */
@Service("siblEdiExportService")
@Transactional(transactionManager = "ediTransactionManager")
public class EdiExportService {

    private static final Logger logger = LoggerFactory.getLogger(EdiExportService.class);

    private static final String DEFAULT_BOOK_SOURCE = "C";
    private static final String DEFAULT_CARRIER_CODE = "TSH";
    private static final String DEFAULT_MESSAGE_SENDER = "EcomOnlineSI"; // per spec
    private static final String DEFAULT_BOOK_TYPE = "online";
    private static final String DEFAULT_TRANS_TYPE = "9"; // per spec
    private static final String DEFAULT_USERCR = "Ecom"; // per spec
    private static final String DEFAULT_PAYMENT_TERMS_ID = "pay"; // per spec
    private static final String DEFAULT_MESSAGE_TYPE = "IFTMIN";

    private final EdiTempBookingRepository ediTempBookingRepo;
    private final EdiTempBookingCargoRepository ediTempBookingCargoRepo;
    private final EdiTempBookingCommodityRepository ediTempBookingCommodityRepo;
    private final EdiTempBookingCustRepository ediTempBookingCustRepo;
    private final EdiTempBookingReferenceRepository ediTempBookingReferenceRepo;
    private final EdiTempBookingRoutingRepository ediTempBookingRoutingRepo;
    private final EdiTempBookingRemarksRepository ediTempBookingRemarksRepo;
    private final EdiTempBookingConfRepository ediTempBookingConfRepo;
    private final EdiTempBookingHeaderRepository ediTempBookingHeaderRepo;
    private final EdiDocStatusRepository ediDocStatusRepo;
    private final EdiReaderMasterRepository ediReaderMasterRepo;
    private final ModuleIndexService moduleIndexService;
    private final BookingDetailRepository bookingDetailRepository;

    public EdiExportService(
            @Qualifier("siblEdiTempBookingRepository") EdiTempBookingRepository ediTempBookingRepo,
            @Qualifier("siblEdiTempBookingCargoRepository") EdiTempBookingCargoRepository ediTempBookingCargoRepo,
            @Qualifier("siblEdiTempBookingCommodityRepository") EdiTempBookingCommodityRepository ediTempBookingCommodityRepo,
            @Qualifier("siblEdiTempBookingCustRepository") EdiTempBookingCustRepository ediTempBookingCustRepo,
            @Qualifier("siblEdiTempBookingReferenceRepository") EdiTempBookingReferenceRepository ediTempBookingReferenceRepo,
            @Qualifier("siblEdiTempBookingRoutingRepository") EdiTempBookingRoutingRepository ediTempBookingRoutingRepo,
            @Qualifier("siblEdiTempBookingRemarksRepository") EdiTempBookingRemarksRepository ediTempBookingRemarksRepo,
            @Qualifier("siblEdiTempBookingConfRepository") EdiTempBookingConfRepository ediTempBookingConfRepo,
            @Qualifier("siblEdiTempBookingHeaderRepository") EdiTempBookingHeaderRepository ediTempBookingHeaderRepo,
            @Qualifier("siblEdiDocStatusRepository") EdiDocStatusRepository ediDocStatusRepo,
            @Qualifier("siblEdiReaderMasterRepository") EdiReaderMasterRepository ediReaderMasterRepo,
            @Qualifier("siblModuleIndexService") ModuleIndexService moduleIndexService,
            @Qualifier("siblBookingDetailRepository") BookingDetailRepository bookingDetailRepository) {
        this.ediTempBookingRepo = ediTempBookingRepo;
        this.ediTempBookingCargoRepo = ediTempBookingCargoRepo;
        this.ediTempBookingCommodityRepo = ediTempBookingCommodityRepo;
        this.ediTempBookingCustRepo = ediTempBookingCustRepo;
        this.ediTempBookingReferenceRepo = ediTempBookingReferenceRepo;
        this.ediTempBookingRoutingRepo = ediTempBookingRoutingRepo;
        this.ediTempBookingRemarksRepo = ediTempBookingRemarksRepo;
        this.ediTempBookingConfRepo = ediTempBookingConfRepo;
        this.ediTempBookingHeaderRepo = ediTempBookingHeaderRepo;
        this.ediDocStatusRepo = ediDocStatusRepo;
        this.ediReaderMasterRepo = ediReaderMasterRepo;
        this.moduleIndexService = moduleIndexService;
        this.bookingDetailRepository = bookingDetailRepository;
    }

    /**
     * Export SIBL to EDI temp tables. If booking already exists, only updates edicleanstatus to "P".
     *
     * @param bookNo           The booking number
     * @param dto              The SIBL request DTO
     * @param loginNameFromJwt Login name from JWT for audit
     * @return Map with success, bookNo, ediReaderId, message; existing=true if only status was updated
     */
    /**
     * Update edidocstatus set status='V' where book_no=? (e.g. when SI is submitted on update).
     */
    @Transactional(transactionManager = "ediTransactionManager")
    public void updateEdiDocStatusToV(String bookNo) {
        if (bookNo == null || bookNo.trim().isEmpty()) return;
        try {
            List<EdiDocStatus> list = ediDocStatusRepo.findByBookNo(bookNo.trim());
            for (EdiDocStatus e : list) {
                e.setStatus("V");
                ediDocStatusRepo.save(e);
            }
            if (!list.isEmpty()) {
                logger.debug("Updated edidocstatus to V for book_no: {}", bookNo);
            }
        } catch (Exception ex) {
            logger.error("Error updating edidocstatus to V for book_no {}: {}", bookNo, ex.getMessage());
        }
    }

    public Map<String, Object> exportToEdiTempTables(String bookNo, SIBLRequestDTO dto, String loginNameFromJwt) {
        Map<String, Object> result = new HashMap<>();

        try {
            logger.info("Exporting SIBL for booking {} to EDI temp tables (same as booking: full insert)", bookNo);

            // Same as booking EdiExportService: generate new edireaderid and do full insert (no existing-path).
            String ediReaderId = generateUniqueEdiReaderId();

            // Booking table uses book_no = VDS booking number; lookup by dto.getBookingNumber(), not by bookNo (SI number)
            String bookingTableKey = nullIfBlank(dto.getBookingNumber());
            Optional<BookingDetail> bookingDetail = bookingTableKey != null
                ? bookingDetailRepository.getBookingByBookNo(bookingTableKey)
                : Optional.empty();
            EdiTempBooking ediBooking = createEdiTempBookingFromDto(bookNo, dto, loginNameFromJwt, ediReaderId, bookingDetail);
            ediBooking.setEdicleanstatus("P");
            ediTempBookingRepo.save(ediBooking);
            logger.debug("Created EDI temp booking: {}", bookNo);

            // EDI temp customer records: take AP, BP from booking_cust (same as EdiTempBooking from booking)
            if (bookingTableKey != null) {
                List<BookingDetailRepository.BookingCustDetail> bookingCusts = bookingDetailRepository.getBookingCustomersByBookNoApBp(bookingTableKey);
                for (BookingDetailRepository.BookingCustDetail row : bookingCusts) {
                    EdiTempBookingCust ediCust = createEdiTempCustFromBookingCust(bookNo, row, ediReaderId);
                    ediTempBookingCustRepo.save(ediCust);
                }
                logger.debug("Created {} EDI temp customer records (AP/BP from booking_cust) for booking {}", bookingCusts.size(), bookNo);
            }
            // EDI temp customer records from request body (shipper, consignee, notify, etc.)
            insertCustomersFromDto(bookNo, dto, ediReaderId);

            insertCommoditiesFromDto(bookNo, dto, ediReaderId);

            if (dto.getContainers() != null && !dto.getContainers().isEmpty()) {
                for (SIBLContainerDTO container : dto.getContainers()) {
                    EdiTempBookingCargo ediCargo = createEdiTempCargoFromDto(bookNo, container, dto.getCommodities(), ediReaderId);
                    ediTempBookingCargoRepo.save(ediCargo);
                }
                logger.debug("Created {} EDI temp cargo records for booking {}", dto.getContainers().size(), bookNo);
            }

            createReferenceRecords(bookNo, dto, ediReaderId);
            logger.debug("Created EDI temp reference records for booking {}", bookNo);

            insertRoutingFromDto(bookNo, dto, ediReaderId);

            if (dto.getAdditionalRemarks() != null && !dto.getAdditionalRemarks().trim().isEmpty()) {
                EdiTempBookingRemarks ediRemarks = createEdiTempRemarksFromDto(bookNo, dto, ediReaderId);
                ediTempBookingRemarksRepo.save(ediRemarks);
                logger.debug("Created EDI temp remarks record for booking {}", bookNo);
            }

            EdiTempBookingConf ediConf = createEdiTempConfFromDto(bookNo, ediReaderId);
            ediTempBookingConfRepo.save(ediConf);
            logger.debug("Created EDI temp conf record for booking {}", bookNo);

            EdiTempBookingHeader ediHeader = createEdiTempBookingHeaderFromDto(bookNo, dto, ediReaderId);
            ediTempBookingHeaderRepo.save(ediHeader);
            logger.debug("Created EDI temp booking header for booking {}", bookNo);

            EdiDocStatus ediDocStatus = createEdiDocStatusFromDto(bookNo, ediReaderId);
            ediDocStatusRepo.save(ediDocStatus);
            logger.debug("Created EDI doc status record for booking {}", bookNo);

            LocalDateTime now = LocalDateTime.now();
            // agencycode/locationcode: from DTO.agencyRefNo or BookingDetail, else customerCode per spec
            String agencyCode =  "";
            if (bookingTableKey != null && bookingDetail.isPresent() && (agencyCode == null || agencyCode.isEmpty())) {
                BookingDetail bd = bookingDetail.get();
                if (bd.getAgencyid() != null && !bd.getAgencyid().trim().isEmpty()) {
                    agencyCode = bd.getAgencyid().trim();
                }
            }
            if (agencyCode == null) agencyCode = "";
            // ediid "EDI110" hardcoded per spec
            ediReaderMasterRepo.insertWithIdentity(
                    ediReaderId,
                    "EDI110",
                    agencyCode,
                    agencyCode,
                    now,
                    "Completed",
                    now,
                    "TP001");
            logger.debug("Created EDI reader master record for edireaderid {}", ediReaderId);

            result.put("success", true);
            result.put("bookNo", bookNo);
            result.put("ediReaderId", ediReaderId);
            result.put("message", "Successfully exported SIBL to EDI temp tables");
            result.put("existing", false);
            logger.info("Successfully exported SIBL for booking {} to EDI temp tables with ediReaderId: {}", bookNo, ediReaderId);

        } catch (Exception e) {
            logger.error("Error exporting SIBL for booking {} to EDI temp tables: {}", bookNo, e.getMessage(), e);
            result.put("success", false);
            result.put("bookNo", bookNo);
            result.put("message", "Error exporting to EDI temp tables: " + e.getMessage());
        }

        return result;
    }

    /**
     * Creates EdiTempBooking per spec: from Booking table when available, else from DTO.
     * Spec: Booking table → agencyid, ocean*, servicecode, origin/final port, pol/pod, dates, carriage, agency*, originportdesc, finalportdesc, intransitport*, transport*.
     * DTO → vesselcode, voyagecode, bound, loadportid, dischargeportid, tbook_no (bookingNumber), t2lfiling, ensType, ensfiling.
     * Hardcoded: status "C", usercr "Ecom", transtype "S", booktype "online", paymenttermsid "pay", booksource "C", messagesender "EcomOnlineSI", carriercode "TSH", version 0, tbl_no null.
     * Empty: remarks, crossbook, shipment_type, callid, custremarks.
     */
    private EdiTempBooking createEdiTempBookingFromDto(String bookNo, SIBLRequestDTO dto,
            String loginNameFromJwt, String ediReaderId, Optional<BookingDetail> bookingDetailOpt) {
        EdiTempBooking edi = new EdiTempBooking();
        LocalDateTime now = LocalDateTime.now();
        Optional<BookingDetail> safeBooking = Optional.ofNullable(bookingDetailOpt).flatMap(o -> o);
        boolean fromBooking = safeBooking.isPresent();

        // book_no: method param (SI number)
        edi.setBookNo(bookNo);

        // From Booking table when present, else from DTO
        if (fromBooking) {
            BookingDetail b = safeBooking.get();
            edi.setAgencyid(nullIfBlank(b.getAgencyid()));
            edi.setAgencyRefNo(nullIfBlank(b.getAgencyRefNo()));
            edi.setOriginPortId(nullIfBlank(b.getOriginPortId()));
            edi.setFinalPortId(nullIfBlank(b.getFinalPortId()));
            edi.setServiceCode(nullIfBlank(b.getServiceCode()));
            edi.setOceanVslCode(nullIfBlank(b.getOceanVslCode()));
            edi.setOceanVoyage(nullIfBlank(b.getOceanVoyage()));
            edi.setOceanSerCode(nullIfBlank(b.getOceanSerCode()));
            edi.setOceanBound(nullIfBlank(b.getOceanBound()));
            edi.setPolTerminal(nullIfBlank(b.getPolTerminal()));
            edi.setPolAt(b.getPolAt());
            edi.setPodTerminal(nullIfBlank(b.getPodTerminal()));
            edi.setPodAt(b.getPodAt());
            edi.setBookDate(b.getBookDate() != null ? b.getBookDate() : now);
            edi.setBookReady(null); // SI create: bookReady empty per spec
            edi.setFirstSvcType(nullIfBlank(b.getFirstSvcType())); // from booking table
            edi.setPreCarriage(nullIfBlank(b.getPreCarriage()));
            edi.setOnCarriage(nullIfBlank(b.getOnCarriage()));
            edi.setDocumentalAgencyId(nullIfBlank(b.getDocumentalAgencyId()));
            edi.setAgencyIssueId(nullIfBlank(b.getAgencyIssueId()));
            edi.setAgencypolid(nullIfBlank(b.getAgencypolid()));
            edi.setOriginPortDesc(nullIfBlank(b.getOriginPortDesc()));
            edi.setFinalPortDesc(nullIfBlank(b.getFinalPortDesc()));
            edi.setInTransitPort(nullIfBlank(b.getInTransitPort()));
            edi.setInTransitPortName(nullIfBlank(b.getInTransitPortName()));
            edi.setTransportServiceId(nullIfBlank(b.getTransportServiceId()));
            edi.setTransportTermId(nullIfBlank(b.getTransportTermId()));
        } else {
            edi.setAgencyid(truncateToMaxLength(nullIfBlank(dto.getCustomerCodeFromJWT()), 5));
            edi.setAgencyRefNo(nullIfBlank(dto.getAgencyRefNo()));
            edi.setOriginPortId(nullIfBlank(dto.getOriginPortId()));
            edi.setFinalPortId(nullIfBlank(dto.getFinalPortId()));
            edi.setServiceCode(nullIfBlank(dto.getServiceCode()));
            edi.setOceanVslCode(truncateToMaxLength(nullIfBlank(dto.getVesselCode()), 5));
            edi.setOceanVoyage(truncateToMaxLength(nullIfBlank(dto.getVoyageCode()), 5));
            edi.setOceanSerCode(nullIfBlank(dto.getServiceCode()));
            edi.setOceanBound(nullIfBlank(dto.getLoadBound() != null ? dto.getLoadBound().getValue() : null));
            edi.setPolTerminal(null);
            edi.setPolAt(null);
            edi.setPodTerminal(null);
            edi.setPodAt(null);
            edi.setBookDate(now);
            edi.setBookReady(null); // SI create: bookReady empty
            edi.setFirstSvcType(null); // no booking row
            edi.setPreCarriage(null);
            edi.setOnCarriage(null);
            edi.setDocumentalAgencyId(truncateToMaxLength(nullIfBlank(dto.getCustomerCodeFromJWT()), 5));
            edi.setAgencyIssueId(truncateToMaxLength(nullIfBlank(dto.getCustomerCodeFromJWT()), 5));
            edi.setAgencypolid(truncateToMaxLength(nullIfBlank(dto.getCustomerCodeFromJWT()), 9));
            edi.setOriginPortDesc(null);
            edi.setFinalPortDesc(null);
            edi.setInTransitPort(null);
            edi.setInTransitPortName(null);
            edi.setTransportServiceId(null);
            edi.setTransportTermId(null);
        }
        edi.setCustRefNo(null);
        edi.setLoadPortId(nullIfBlank(dto.getLoadPortId())); // DTO
        edi.setDischargePortId(nullIfBlank(dto.getDischargePortId())); // DTO
        edi.setVesselCode(truncateToMaxLength(nullIfBlank(dto.getVesselCode()), 5)); // DTO
        edi.setVoyageCode(truncateToMaxLength(nullIfBlank(dto.getVoyageCode()), 5)); // DTO
        edi.setBound(nullIfBlank(dto.getLoadBound() != null ? dto.getLoadBound().getValue() : null)); // DTO
        edi.setCallId(""); // per spec: empty

        // per spec: empty
        edi.setRemarks("");
        edi.setCustremarks("");
        edi.setCrossBook("");
        edi.setShipmentType("");
        // per spec: hardcoded "C"
        edi.setStatus("C");
        edi.setActStatus("O");
        // DTO
        edi.setEnsfiling(nullIfBlank(dto.getEnsFilling() != null ? dto.getEnsFilling().getValue() : null));
        edi.setT2lfiling(nullIfBlank(dto.getT2lFilling() != null ? dto.getT2lFilling().getValue() : null));
        edi.setEnsType(nullIfBlank(dto.getEnsType() != null ? dto.getEnsType().getValue() : null));
        // per spec: usercr "Ecom"
        edi.setUsercr(DEFAULT_USERCR);
        edi.setDatecr(now);
        edi.setEdireaderid(ediReaderId);
        edi.setMessagetype(DEFAULT_MESSAGE_TYPE);
        edi.setTranstype(DEFAULT_TRANS_TYPE);
        edi.setBooksource(DEFAULT_BOOK_SOURCE);
        edi.setMessagesender(DEFAULT_MESSAGE_SENDER);
        edi.setCarrierCode(DEFAULT_CARRIER_CODE);
        edi.setBookType(DEFAULT_BOOK_TYPE);
        edi.setPaymenttermsId(DEFAULT_PAYMENT_TERMS_ID);
        edi.setPriority("M");
        edi.setThirdParty("N");
        edi.setPrepareOPS("N");
        edi.setPrepareBL("N");
        edi.setIsupdate("N");
        edi.setIsedited("N");
        edi.setIsMailSent("N");
        edi.setIsDGBook("N");
        edi.setTbookNo(dto.getBookingNumber() != null ? dto.getBookingNumber() : ""); // DTO bookingNumber
        edi.setTblNo(null); // per spec
        edi.setFcnno("");
        edi.setVersion((short) 0);

        return edi;
    }

    /**
     * Insert EDI temp commodity records from request DTO. Used for both new and existing EDI temp bookings.
     * When request has no "commodities" array, derives one commodity per container from container DG/cargo so
     * editempbooking_commodity gets rows (e.g. container-only SI with DG).
     */
    private void insertCommoditiesFromDto(String bookNo, SIBLRequestDTO dto, String ediReaderId) {
        if (dto.getCommodities() != null && !dto.getCommodities().isEmpty()) {
            for (SIBLCommodityDTO commodity : dto.getCommodities()) {
                EdiTempBookingCommodity ediCommodity = createEdiTempCommodityFromDto(bookNo, commodity, ediReaderId);
                ediTempBookingCommodityRepo.save(ediCommodity);
            }
            logger.debug("Created {} EDI temp commodity records for booking {}", dto.getCommodities().size(), bookNo);
        } else if (dto.getContainers() != null && !dto.getContainers().isEmpty()) {
            for (SIBLContainerDTO container : dto.getContainers()) {
                EdiTempBookingCommodity ediCommodity = createEdiTempCommodityFromContainer(bookNo, container, ediReaderId);
                ediTempBookingCommodityRepo.save(ediCommodity);
            }
            logger.debug("Created {} EDI temp commodity records (derived from containers) for booking {}", dto.getContainers().size(), bookNo);
        }
    }

    /**
     * Create one EdiTempBookingCommodity from a container when request has no commodities array.
     * Uses container cargo/DG data: cargoNo, containerNo, weight, DG class, IMO contact/phone.
     */
    private EdiTempBookingCommodity createEdiTempCommodityFromContainer(String bookNo, SIBLContainerDTO container, String ediReaderId) {
        EdiTempBookingCommodity edi = new EdiTempBookingCommodity();
        edi.setBookNo(bookNo);
        edi.setCargoNo(container.getCargoNo());
        edi.setEqpId(nullIfBlank(container.getContainerNo()));
        edi.setItemNo(1);
        edi.setCommodityId(null);
        edi.setPackageId(null);
        edi.setItemQty(1);
        Float weight = container.getCargoWeight() != null ? container.getCargoWeight().floatValue() : null;
        edi.setItemWeight(weight);
        edi.setItemWeightUnit(nullIfBlank(container.getWeightUnit() != null ? container.getWeightUnit().getValue() : null));
        edi.setItemVolume(null);
        edi.setItemVolumeUnit(null);
        edi.setNettwt(weight);
        edi.setNetwtunit(nullIfBlank(container.getWeightUnit() != null ? container.getWeightUnit().getValue() : null));
        edi.setHazardous("N");  // DG at commodity level only; container-only path has no DG
        edi.setImoContact(null);
        edi.setImoPhone(null);
        if (edi.getCommodityId() == null) {
            edi.setCommodityId("GEN");
        }
        edi.setEdireaderid(ediReaderId);
        edi.setAlcohol("N");
        edi.setMilitarycargo("N");
        edi.setIsedited("N");
        edi.setVersion((short) 0);
        edi.setCarriercode(DEFAULT_CARRIER_CODE);
        return edi;
    }

    /**
     * Insert EDI temp customer records from request body (dto.getCustomers()). Used for both new and existing EDI temp bookings.
     */
    private void insertCustomersFromDto(String bookNo, SIBLRequestDTO dto, String ediReaderId) {
        if (dto.getCustomers() != null && !dto.getCustomers().isEmpty()) {
            for (SIBLCustomerDTO customer : dto.getCustomers()) {
                EdiTempBookingCust ediCust = createEdiTempCustFromDto(bookNo, customer, ediReaderId);
                ediTempBookingCustRepo.save(ediCust);
            }
            logger.debug("Created {} EDI temp customer records from request body for booking {}", dto.getCustomers().size(), bookNo);
        }
    }

    /**
     * Create EdiTempBookingCust from request DTO (SIBLCustomerDTO).
     */
    private EdiTempBookingCust createEdiTempCustFromDto(String bookNo, SIBLCustomerDTO dto, String ediReaderId) {
        EdiTempBookingCust edi = new EdiTempBookingCust();
        edi.setBookNo(bookNo);
        edi.setCustTypeId(nullIfBlank(dto.getCustomerType() != null ? dto.getCustomerType().getValue() : null));
        edi.setCustId(nullIfBlank(dto.getCustomerCode()));
        edi.setCountryId(nullIfBlank(dto.getCountryId()));
        edi.setStateId(nullIfBlank(dto.getStateId()));
        edi.setName(nullIfBlank(dto.getCustomerName()));
        edi.setAddress1(nullIfBlank(dto.getAddress()));
        edi.setAddress2(nullIfBlank(dto.getAddress2()));
        edi.setPoBox(null);
        edi.setCity(nullIfBlank(dto.getCity()));
        edi.setZipCode(nullIfBlank(dto.getZipCode()));
        edi.setTelephone(nullIfBlank(dto.getTelephone()));
        edi.setFax(nullIfBlank(dto.getFax()));
        edi.setContact(null);
        edi.setEmail(nullIfBlank(dto.getEmail()));
        edi.setCarriercode(DEFAULT_CARRIER_CODE);
        edi.setIsedited("N");
        edi.setPersontype(nullIfBlank(dto.getPersontype()));
        edi.setEori(nullIfBlank(dto.getEori()));
        edi.setEdireaderid(ediReaderId);
        edi.setVersion((short) 0);
        return edi;
    }

    /**
     * Create EdiTempBookingCust from booking_cust row (AP, BP).
     * Same as EdiTempBooking takes from booking; columns: book_no, custTypeId, custId, countryId, stateId, name,
     * address1, address2, POBox, city, zipCode, telephone, fax, contact, email, carriercode, isedited, persontype, eori.
     */
    private EdiTempBookingCust createEdiTempCustFromBookingCust(String bookNo, BookingDetailRepository.BookingCustDetail row, String ediReaderId) {
        EdiTempBookingCust edi = new EdiTempBookingCust();
        edi.setBookNo(bookNo);
        edi.setCustTypeId(nullIfBlank(row.getCustTypeId()));
        edi.setCustId(nullIfBlank(row.getCustId()));
        edi.setCountryId(nullIfBlank(row.getCountryId()));
        edi.setStateId(nullIfBlank(row.getStateId()));
        edi.setName(nullIfBlank(row.getName()));
        edi.setAddress1(nullIfBlank(row.getAddress1()));
        edi.setAddress2(nullIfBlank(row.getAddress2()));
        edi.setPoBox(nullIfBlank(row.getPoBox()));
        edi.setCity(nullIfBlank(row.getCity()));
        edi.setZipCode(nullIfBlank(row.getZipCode()));
        edi.setTelephone(nullIfBlank(row.getTelephone()));
        edi.setFax(nullIfBlank(row.getFax()));
        edi.setContact(nullIfBlank(row.getContact()));
        edi.setEmail(nullIfBlank(row.getEmail()));
        edi.setCarriercode(nullIfBlank(row.getCarriercode()));
        String ised = nullIfBlank(row.getIsedited());
        edi.setIsedited(ised != null ? ised : "N");
        edi.setPersontype(nullIfBlank(row.getPersontype()));
        edi.setEori(nullIfBlank(row.getEori()));
        edi.setEdireaderid(ediReaderId);
        edi.setVersion((short) 0);
        return edi;
    }

    private EdiTempBookingCargo createEdiTempCargoFromDto(String bookNo, SIBLContainerDTO dto, List<SIBLCommodityDTO> commodities, String ediReaderId) {
        EdiTempBookingCargo edi = new EdiTempBookingCargo();
        edi.setBookNo(bookNo);
        // Direct mappings: COLUMN <- REQUEST DTO
        edi.setCargoNo(dto.getCargoNo() != null ? dto.getCargoNo() : null);
        edi.setEqpId(nullIfBlank(dto.getContainerNo()));           // eqpId <- containerNo
        edi.setEqpTypeId(nullIfBlank(dto.getEquipmentSize()));      // eqpTypeId <- equipmentSize
        edi.setEqpStatus(nullIfBlank(dto.getEquipmentStatus() != null ? dto.getEquipmentStatus().getValue() : null));    // eqpStatus <- equipmentStatus
        edi.setType(dto.getEquipmentType() != null ? nullIfBlank(dto.getEquipmentType().getValue()) : null);  // type <- equipmentType
        edi.setShipperOwn(dto.getSocFlag() == YesNoFlag.Y ? "Y" : "N");  // shipperOwn <- soc
        edi.setSeal1No(nullIfBlank(dto.getCarrierSeal()));          // seal1No <- carrierSeal
        edi.setSeal2No(nullIfBlank(dto.getShipperSeal()));          // seal2No <- shipperSeal
        edi.setNor(dto.getNor() == YesNoFlag.Y ? "Y" : "N");
        // DG at commodity level: derive from first hazardous commodity for this container
        SIBLDangerousGoodsDTO dgFromCommodity = findFirstDangerousGoodsForContainer(commodities, dto.getCargoNo(), dto.getContainerNo());
        edi.setDgflag(dgFromCommodity != null ? "Y" : "N");
        edi.setOog(dto.getOogFlag() == YesNoFlag.Y ? "Y" : "N");
        edi.setTareWeight(dto.getTareWeight() != null ? dto.getTareWeight().floatValue() : null);  // tareWeight <- tareWeight
        // When commodities present: use derived cargoWeight = SUM(itemWeight) per container (ignore request cargoWeight)
        Float cargoWeight;
        if (commodities != null && !commodities.isEmpty()) {
            int sumKg = sumCommodityItemWeightKgForContainer(commodities, dto.getCargoNo(), dto.getContainerNo());
            cargoWeight = (float) sumKg;
            edi.setWeightUnit("K");
        } else {
            cargoWeight = dto.getCargoWeight() != null ? dto.getCargoWeight().floatValue() : null;
            edi.setWeightUnit(nullIfBlank(dto.getWeightUnit() != null ? dto.getWeightUnit().getValue() : null));
        }
        edi.setCargoWeight(cargoWeight);
        Float grossWeightVal = dto.getGrossweight() != null ? dto.getGrossweight().floatValue() : null;
        if (commodities != null && !commodities.isEmpty() && cargoWeight != null && dto.getTareWeight() != null) {
            edi.setGrossWeight(dto.getTareWeight().floatValue() + cargoWeight);
        } else {
            edi.setGrossWeight(grossWeightVal != null ? grossWeightVal : cargoWeight);
        }
        edi.setRateEqpTypeid(nullIfBlank(dto.getRateEquipmentTypeId()));  // rate_eqpTypeid <- rateEquipmentTypeId
        // Reefer nested DTO: setTemp, minTemp, maxTemp, tempUnit, humidity, humidityFlag, drains, co2Value, o2Value, ventilation
        if (dto.getReeferSettings() != null) {
            SIBLReeferDTO reefer = dto.getReeferSettings();
            if (reefer.getTempSet() != null) {
                edi.setSetTemp(reefer.getTempSet().floatValue());
            }
            if (reefer.getTempMin() != null) {
                edi.setTempMin(reefer.getTempMin().floatValue());
            }
            if (reefer.getTempMax() != null) {
                edi.setTempMax(reefer.getTempMax().floatValue());
            }
            edi.setTempUnit(nullIfBlank(reefer.getTempSetUnit() != null ? reefer.getTempSetUnit().getValue() : null));
            if (reefer.getHumidity() != null) {
                edi.setHumidity(reefer.getHumidity().toString());
            }
            if (reefer.getHumidityFlag() != null) {
                edi.setHumidityFlag(reefer.getHumidityFlag() == YesNoFlag.Y);
            }
            edi.setDrains(nullIfBlank(reefer.getDrains()));
            if (reefer.getCo2Level() != null) {
                edi.setCo2Value(reefer.getCo2Level().toString());
            }
            if (reefer.getO2Level() != null) {
                edi.setO2Value(reefer.getO2Level().toString());
            }
            if (reefer.getVentilation() != null) {
                edi.setVentPercentage(reefer.getVentilation());  // ventSetting/ventilation from reefer
            }
            if (edi.getVentPercentage() == null && reefer.getVentSet() != null) {
                edi.setVentSetting(reefer.getVentSet().intValue());
            }
        }
        if (edi.getVentSetting() == null) {
            edi.setVentSetting(0);
        }
        // DG at commodity level: use first hazardous commodity's dangerousGoods for this container
        if (dgFromCommodity != null) {
            edi.setDgNo(nullIfBlank(dgFromCommodity.getImdgClass()));
            if (dgFromCommodity.getFlashPt() != null) {
                edi.setFlashpoint(dgFromCommodity.getFlashPt().floatValue());
            }
            edi.setFlashunit(nullIfBlank(dgFromCommodity.getFlashPtUnit() != null ? dgFromCommodity.getFlashPtUnit().getValue() : null));
        }
        // containerPosition is char(2) in DB - use null when no position (do not set string "NULL")
        edi.setContainerPosition(null);
        edi.setLoadingCondition("FF");
        edi.setStatus("A");
        edi.setIsedited("N");
        edi.setEdireaderid(ediReaderId);
        edi.setIsDummy("Y");
        edi.setRoundTrip("N");
        edi.setActStatus("O");
        edi.setVersion((short) 0);
        return edi;
    }

    /** Sum of commodity itemWeight for the given container, normalized to kg (T → * 1000, K → as-is). Aligns with SiCreateService derived cargoWeight. */
    private static int sumCommodityItemWeightKgForContainer(List<SIBLCommodityDTO> commodities, Integer cargoNo, String containerNo) {
        if (commodities == null) return 0;
        int sum = 0;
        for (SIBLCommodityDTO c : commodities) {
            if (cargoNo != null && !cargoNo.equals(c.getCargoNo())) continue;
            if (containerNo != null && !containerNo.equals(c.getContainerNo())) continue;
            Integer w = c.getItemWeight();
            if (w == null) continue;
            sum += (c.getItemWeightUnit() == WeightUnit.T) ? w * 1000 : w;
        }
        return sum;
    }

    /** DG at commodity level: return dangerousGoods of first hazardous commodity for this container. */
    private static SIBLDangerousGoodsDTO findFirstDangerousGoodsForContainer(List<SIBLCommodityDTO> commodities, Integer cargoNo, String containerNo) {
        if (commodities == null || cargoNo == null) return null;
        for (SIBLCommodityDTO c : commodities) {
            if (!cargoNo.equals(c.getCargoNo())) continue;
            if (containerNo != null && !containerNo.equals(c.getContainerNo())) continue;
            if (c.getHazardous() == YesNoFlag.Y && c.getDangerousGoods() != null) {
                return c.getDangerousGoods();
            }
        }
        return null;
    }

    /**
     * Map SIBLCommodityDTO to EdiTempBookingCommodity per column mapping:
     * commodityid<-hsCode, itemqty<-packageQty, itemweight<-weight, itemweightunit<-weightUnit,
     * hazardous<-hazardous, imocontact<-imoContact, imophone<-imoPhone, packageid<-packageType,
     * itemvolume<-itemVolume, itemvolumeunit<-itemVolumeUnit, nettwt<-netWeight,
     * itemforward/itemleft/itemtop/itemaft/itemright<-oog, version<-0, eqpid<-containerNo,
     * alcohol<-N, militarycargo<-N, cargono<-cargoNo, netwtunit<-weightUnit when netWeight present.
     */
    private EdiTempBookingCommodity createEdiTempCommodityFromDto(String bookNo, SIBLCommodityDTO dto, String ediReaderId) {
        EdiTempBookingCommodity edi = new EdiTempBookingCommodity();
        edi.setBookNo(bookNo);
        // Request DTO: cargoNo (same as parent container), eqpid <- containerNo (cntno)
        edi.setCargoNo(dto.getCargoNo());
        edi.setEqpId(nullIfBlank(dto.getContainerNo()));
        edi.setItemNo(dto.getItemNo());
        edi.setComdtygrp(nullIfBlank(dto.getCommodityGroup()));
        edi.setCommodityId(nullIfBlank(dto.getHsCode()));           // commodityid <- HS Code (commodity)
        edi.setPackageId(nullIfBlank(dto.getPackageType()));        // packageid <- Package type
        edi.setItemQty(dto.getPackageQty());                        // itemqty <- Quantity
        edi.setItemWeight(dto.getItemWeight() != null ? dto.getItemWeight().floatValue() : null);  // itemweight <- Weight
        edi.setItemWeightUnit(nullIfBlank(dto.getItemWeightUnit() != null ? dto.getItemWeightUnit().getValue() : null)); // itemweightunit <- Weight unit
        edi.setItemVolume(dto.getItemVolume() != null ? dto.getItemVolume().floatValue() : null);
        edi.setItemVolumeUnit(nullIfBlank(dto.getItemVolumeUnit() != null ? dto.getItemVolumeUnit().getValue() : null));
        edi.setNettwt(dto.getNettwt() != null ? dto.getNettwt().floatValue() : null);  // nettwt <- Net weight
        // netwtunit: if netweight not empty → use unit (same as itemweightunit); else not set
        if (dto.getNettwt() != null) {
            String netUnit = nullIfBlank(dto.getItemWeightUnit() != null ? dto.getItemWeightUnit().getValue() : null);
            if (netUnit != null) {
                edi.setNetwtunit(netUnit);
            }
        }
        edi.setHazardous(dto.getHazardous() == YesNoFlag.Y ? "Y" : "N");
        edi.setImoContact(nullIfBlank(dto.getImoContact()));         // imocontact <- IMO contact
        edi.setImoPhone(nullIfBlank(dto.getImoPhone()));              // imophone <- IMO phone
        // OOG: itemforward, itemleft, itemtop, itemaft, itemright <- dto.oog
        if (dto.getOog() != null) {
            SIBLOogDTO oog = dto.getOog();
            if (oog.getForward() != null) edi.setItemForward(oog.getForward().floatValue());
            if (oog.getLeftFt() != null) edi.setItemLeft(oog.getLeftFt().floatValue());
            if (oog.getTopFt() != null) edi.setItemTop(oog.getTopFt().floatValue());
            if (oog.getAft() != null) edi.setItemAft(oog.getAft().floatValue());
            if (oog.getRightFt() != null) edi.setItemRight(oog.getRightFt().floatValue());
        }
        edi.setBycode(nullIfBlank(dto.getBuyerCode()));
        edi.setByname(nullIfBlank(dto.getBuyerName()));
        edi.setSlcode(nullIfBlank(dto.getSellerCode()));
        edi.setSlname(nullIfBlank(dto.getSellerName()));
        edi.setCmdtyHblno(nullIfBlank(dto.getCommodityHBLNo()));
        edi.setMarksNumbers(nullIfBlank(dto.getMarksNo()));
        edi.setMarksDesc(nullIfBlank(dto.getMarksDesc()));
        edi.setEdireaderid(ediReaderId);
        edi.setAlcohol("N");                                         // alcohol <- hardcoded N
        edi.setMilitarycargo("N");                                   // militarycargo <- hardcoded N
        edi.setIsedited("N");
        edi.setVersion((short) 0);                                   // version <- hardcoded 0
        edi.setCarriercode(DEFAULT_CARRIER_CODE);
        return edi;
    }

    private void createReferenceRecords(String bookNo, SIBLRequestDTO dto, String ediReaderId) {
        saveReference(bookNo, "DAK", bookNo, ediReaderId);
        if (dto.getAgencyRefNo() != null && !dto.getAgencyRefNo().trim().isEmpty()) {
            saveReference(bookNo, "AGY", dto.getAgencyRefNo(), ediReaderId);
        }
    }

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
     * Insert EDI temp routing record (editempbooking_route). Used for both new and existing EDI temp bookings.
     */
    private void insertRoutingFromDto(String bookNo, SIBLRequestDTO dto, String ediReaderId) {
        EdiTempBookingRouting ediRouting = createEdiTempRoutingFromDto(bookNo, dto, ediReaderId);
        ediTempBookingRoutingRepo.save(ediRouting);
        logger.debug("Created EDI temp routing record for booking {}", bookNo);
    }

    /**
     * Create EdiTempBookingRouting from SIBL request DTO per spec:
     * origin=bookingNumber, originmod=Y, pol=loadPortId, polter=polHeaderName, polser=serviceCode,
     * polmode=empty, trans1-3 and related=empty, pod=dischargePortId, podter=podHeaderName, podmode=N,
     * delivery=empty, svccode=serviceCode, book_no=bookNo, edireaderid=ediReaderId, defrout=N.
     */
    private EdiTempBookingRouting createEdiTempRoutingFromDto(String bookNo, SIBLRequestDTO dto, String ediReaderId) {
        EdiTempBookingRouting edi = new EdiTempBookingRouting();
        edi.setBookNo(bookNo);
        edi.setEdireaderid(ediReaderId);
        edi.setVersion((short) 0);
        // origin = bookingNumber from request
        edi.setOrigin(nullIfBlank(dto.getOriginPortId()) != null ? dto.getOriginPortId().trim() : bookNo);
        edi.setOriginMode("Y");
        edi.setPol(nullIfBlank(dto.getLoadPortId()));
        edi.setPolter(nullIfBlank(dto.getPolTerminal()));
        edi.setPolSer(nullIfBlank(dto.getServiceCode()));
        edi.setPolmode("");
        edi.setTrans1("");
        edi.setTrans1Ter("");
        edi.setTrans1Ser("");
        edi.setTrans1mode("");
        edi.setTrans2("");
        edi.setTrans2Ter("");
        edi.setTrans2Ser("");
        edi.setTrans2mode("");
        edi.setTrans3("");
        edi.setTrans3Ter("");
        edi.setTrans3Ser("");
        edi.setTrans3mode("");
        edi.setPod(nullIfBlank(dto.getFinalPortId()));
        edi.setPodTer(nullIfBlank(dto.getPodTerminal()));
        edi.setPodMode("N");
        edi.setDelivery("");
        edi.setSvcCode(nullIfBlank(dto.getServiceCode()));
        edi.setDefRout("N");
        return edi;
    }

    private EdiTempBookingRemarks createEdiTempRemarksFromDto(String bookNo, SIBLRequestDTO dto, String ediReaderId) {
        EdiTempBookingRemarks edi = new EdiTempBookingRemarks();
        edi.setBookNo(bookNo);
        edi.setRemarksref("CUST_REMARKS");
        edi.setRemarksval(dto.getAdditionalRemarks());
        edi.setEdireaderid(ediReaderId);
        edi.setVersion(0);
        edi.setCategory("C");
        edi.setTranstype("BKG");
        return edi;
    }

    private EdiTempBookingConf createEdiTempConfFromDto(String bookNo, String ediReaderId) {
        EdiTempBookingConf edi = new EdiTempBookingConf();
        edi.setBookNo(bookNo);
        edi.setEdireaderid(ediReaderId);
        edi.setVersion((short) 0);
        return edi;
    }

    /**
     * Create EdiTempBookingHeader from SIBLRequestDTO per DTO mapping.
     * Table: editempbooking_header. book_no = SI number (bookNo); Service, shipper/consignee/notify, ports, remarks from DTO; defaults toorder/sameasconsignee "N", version 0.
     */
    private EdiTempBookingHeader createEdiTempBookingHeaderFromDto(String bookNo, SIBLRequestDTO dto, String ediReaderId) {
        EdiTempBookingHeader h = new EdiTempBookingHeader();
        h.setBookNo(truncateToMaxLength(bookNo, 50));
        h.setService(truncateToMaxLength(nullIfBlank(dto.getServiceCode()), 4));
        h.setShippername(truncateToMaxLength(nullIfBlank(dto.getShipperName()), 150));
        h.setShipper(truncateToMaxLength(nullIfBlank(dto.getShipperAddress()), 500));
        h.setExportRef(truncateToMaxLength(nullIfBlank(dto.getAgencyRefNo()), 500));
        h.setConsigneeName(truncateToMaxLength(nullIfBlank(dto.getConsigeeName()), 150)); // DTO: consigeeName (spelling)
        h.setConsignee(truncateToMaxLength(nullIfBlank(dto.getConsigeeAddress()), 500));
        h.setNotifierName(truncateToMaxLength(nullIfBlank(dto.getNotifyName()), 150));
        h.setNotify(truncateToMaxLength(nullIfBlank(dto.getNotifyAddress()), 500));
        h.setForwarderName(null);
        h.setForwarder(null);
        h.setToorder("N");
        h.setSameasconsignee("N");
        h.setPreCarriage(null);
        h.setOriginofcountry(null);
        h.setPreCarriagePOR(truncateToMaxLength(nullIfBlank(dto.getOriginPortId()), 50));
        h.setPol(truncateToMaxLength(nullIfBlank(dto.getPolHeaderName()), 150));
        h.setLoadVSL(null);
        h.setPod(truncateToMaxLength(nullIfBlank(dto.getPodHeaderName()), 150));
        h.setDischargeVSL(null);
        h.setOrigin(truncateToMaxLength(nullIfBlank(dto.getPlaHeaderName()), 150));
        h.setDelivery(truncateToMaxLength(nullIfBlank(dto.getPldHeaderName()), 150));
        h.setRoute(null);
        h.setRemarks(truncateToMaxLength(nullIfBlank(dto.getAdditionalRemarks()), 255));
        h.setCarrierCode(null);
        h.setShipperFontsize(null);
        h.setConsigneeFontsize(null);
        h.setNotifyFontsize(null);
        h.setEdireaderid(truncateToMaxLength(ediReaderId, 50));
        h.setEdicleanstatus(null);
        h.setVersion((short) 0);
        return h;
    }

    private EdiDocStatus createEdiDocStatusFromDto(String bookNo, String ediReaderId) {
        EdiDocStatus ediDocStatus = new EdiDocStatus();
        LocalDateTime now = LocalDateTime.now();
        ediDocStatus.setBookNo(bookNo);
        ediDocStatus.setEdireaderid(ediReaderId); // size-safe: from module index (prefix + index), same as booking
        ediDocStatus.setTranstype(DEFAULT_TRANS_TYPE);
        ediDocStatus.setStatus("R");
        ediDocStatus.setEdiparty("TSH ECOMONLINE");
        ediDocStatus.setReceivdate(now);
        ediDocStatus.setMessage(DEFAULT_MESSAGE_TYPE);
        ediDocStatus.setServprovname("TSHONLINE SI");
        ediDocStatus.setAmendmentVersion(0);
        return ediDocStatus;
    }

    /**
     * Generate a new edireaderid that is unique in edireadermaster (same idea as booking: each export gets a new id).
     */
    private String generateUniqueEdiReaderId() {
        final int maxAttempts = 20;
        for (int i = 0; i < maxAttempts; i++) {
            String id = moduleIndexService.generateEdiReaderId();
            if (!ediReaderMasterRepo.existsById(id)) {
                return id;
            }
            logger.warn("Generated edireaderid {} already exists in edireadermaster, generating again (attempt {})", id, i + 1);
        }
        throw new IllegalStateException("Could not generate unique edireaderid after " + maxAttempts + " attempts");
    }

    private static String nullIfBlank(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    /**
     * Truncates a string to the specified maximum length.
     * Returns null if input is null, otherwise truncates to maxLength characters.
     * This is needed because the actual database column lengths may be shorter than the entity @Column(length) definition.
     *
     * @param s         The string to truncate
     * @param maxLength Maximum length (e.g., 5 for columns that should be length=6 but DB is actually 5)
     * @return Truncated string or null if input is null
     */
    private static String truncateToMaxLength(String s, int maxLength) {
        if (s == null) {
            return null;
        }
        String trimmed = s.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.length() > maxLength) {
            logger.warn("Truncating value '{}' from {} to {} characters", trimmed, trimmed.length(), maxLength);
            return trimmed.substring(0, maxLength);
        }
        return trimmed;
    }
}
