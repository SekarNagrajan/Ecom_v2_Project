package com.solverminds.booking.service;

import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.entity.EcomBooking;
import com.solverminds.booking.exception.BookingNotFoundException;
import com.solverminds.booking.repo.EcomBookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Resolves the API path booking id to {@code ecombooking.ebookno} for update.
 * BKON ids pass through unchanged; unlinked LRP ids bootstrap a shell row.
 */
@Service
@Transactional(transactionManager = "ecomTransactionManager")
public class BookingUpdateIdResolverService {

    private static final Logger logger = LoggerFactory.getLogger(BookingUpdateIdResolverService.class);
    private static final String BKON_PREFIX = "BKON";

    private final EcomBookingRepository ecomBookingRepository;
    private final LrpBookingLinkValidationService lrpBookingLinkValidationService;
    private final BookingCreateService bookingCreateService;
    private final LrpDakReferenceService lrpDakReferenceService;

    public BookingUpdateIdResolverService(
            EcomBookingRepository ecomBookingRepository,
            LrpBookingLinkValidationService lrpBookingLinkValidationService,
            BookingCreateService bookingCreateService,
            LrpDakReferenceService lrpDakReferenceService) {
        this.ecomBookingRepository = ecomBookingRepository;
        this.lrpBookingLinkValidationService = lrpBookingLinkValidationService;
        this.bookingCreateService = bookingCreateService;
        this.lrpDakReferenceService = lrpDakReferenceService;
    }

    /**
     * @param pathBookingId Path variable from PUT (BKON or LRP {@code book_no})
     * @param req           Update payload (optional port/agency hints for shell row)
     * @param customerCode  JWT customer code
     * @param loginName     JWT login name
     * @return {@code ebookno} to pass to existing {@link BookingUpdateService#updateBooking}
     */
    public String resolveOnlineBookNoForUpdate(String pathBookingId, BookingRequestDTO req,
                                               String customerCode, String loginName) {
        String id = pathBookingId != null ? pathBookingId.trim() : "";
        if (id.isEmpty()) {
            return id;
        }
        if (id.toUpperCase(Locale.ROOT).startsWith(BKON_PREFIX)) {
            return id;
        }

        List<EcomBooking> linkedInEcom = ecomBookingRepository.findByExtBookNo(id);
        if (!linkedInEcom.isEmpty()) {
            logger.debug("LRP book_no {} already linked in ecombooking.extBookNo — use BKON for update", id);
            throw new BookingNotFoundException("Booking not found for onlineBookNo: " + id);
        }

        lrpBookingLinkValidationService.validateForUpdateLink(id, customerCode);
        String newBkOn = bookingCreateService.createEcomShellForLrpLink(id, req, customerCode, loginName);
        lrpDakReferenceService.ensureDakReference(id, newBkOn);
        logger.info("Created ECOM shell {} for LRP book_no {}", newBkOn, id);
        return newBkOn;
    }
}
