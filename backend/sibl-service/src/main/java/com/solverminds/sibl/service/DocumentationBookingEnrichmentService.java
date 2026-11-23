package com.solverminds.sibl.service;

import com.solverminds.booking.common.entity.Booking;
import com.solverminds.booking.common.repository.BookingViewRepository;
import com.solverminds.sibl.dto.SIBLRequestDTO;
import com.svm.ecom.common.enums.EnsFillingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for enriching SIBL view data from the Documentation database (booking table).
 * Same pattern as booking-service {@code DocumentationBookingViewService}: dedicated service
 * for documentation DB reads with its own transaction manager.
 * <p>
 * Used when bill-of-lading view is loaded by onlineSINo: ecomsi has bookno but not
 * serviceCode, groupCallId, polTerminal, podTerminal; these are filled from the booking table.
 */
@Service
@Transactional(transactionManager = "documentationTransactionManager", readOnly = true)
public class DocumentationBookingEnrichmentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentationBookingEnrichmentService.class);

    private final BookingViewRepository bookingViewRepository;

    public DocumentationBookingEnrichmentService(BookingViewRepository bookingViewRepository) {
        this.bookingViewRepository = bookingViewRepository;
    }

    /**
     * Enrich SIBLRequestDTO with serviceCode, groupCallId, polTerminal, podTerminal from the
     * booking table when view is by onlineSINo (ecomsi has bookno but not these booking-only fields).
     * Only sets fields on the DTO that are currently empty, so existing SI data is not overwritten.
     *
     * @param dto    the SIBL request DTO to enrich (must not be null)
     * @param bookNo booking number (book_no) to look up in Documentation DB
     */
    public void enrichSIBLFromBooking(SIBLRequestDTO dto, String bookNo) {
        if (dto == null || bookNo == null || bookNo.trim().isEmpty()) {
            return;
        }
        Optional<Booking> bookingOpt = bookingViewRepository.findByBookNo(bookNo.trim());
        if (!bookingOpt.isPresent()) {
            logger.debug("Booking not found for enrichment: {}", bookNo);
            return;
        }
        Booking booking = bookingOpt.get();
        if (empty(dto.getServiceCode())) {
            dto.setServiceCode(emptyToNull(booking.getServiceCode()));
        }
        if (empty(dto.getGroupCallId())) {
            dto.setGroupCallId(emptyToNull(booking.getCallId()));
        }
        if (empty(dto.getPolTerminal())) {
            dto.setPolTerminal(emptyToNull(booking.getPolTerminal()));
        }
        if (empty(dto.getPodTerminal())) {
            dto.setPodTerminal(emptyToNull(booking.getPodTerminal()));
        }
        if (dto.getEnsFilling() == null) {
            dto.setEnsFilling(safeEnsFillingType(booking.getEnsFiling()));
        }
    }

    private static EnsFillingType safeEnsFillingType(String v) {
        if (v == null || v.trim().isEmpty()) return null;
        try {
            return EnsFillingType.fromValue(v.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean empty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String emptyToNull(String s) {
        return (s != null && !s.trim().isEmpty()) ? s.trim() : "";
    }
}
