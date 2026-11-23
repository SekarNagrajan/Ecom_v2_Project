package com.solverminds.booking.service;

import com.solverminds.booking.repo.BookingReferenceJdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ensures a DAK row exists on {@code booking_reference} linking LRP {@code book_no} to BKON.
 * Invoked only when a new ECOM shell is created for a direct LRP booking.
 */
@Service
public class LrpDakReferenceService {

    private static final Logger logger = LoggerFactory.getLogger(LrpDakReferenceService.class);

    private final BookingReferenceJdbcRepository bookingReferenceJdbcRepository;

    public LrpDakReferenceService(BookingReferenceJdbcRepository bookingReferenceJdbcRepository) {
        this.bookingReferenceJdbcRepository = bookingReferenceJdbcRepository;
    }

    /**
     * @param lrpBookNo    LRP booking number ({@code booking.book_no} / {@code ecombooking.extBookNo})
     * @param onlineBookNo Generated BKON ({@code ecombooking.ebookno})
     */
    @Transactional(transactionManager = "documentationTransactionManager")
    public void ensureDakReference(String lrpBookNo, String onlineBookNo) {
        if (lrpBookNo == null || lrpBookNo.trim().isEmpty()
                || onlineBookNo == null || onlineBookNo.trim().isEmpty()) {
            logger.debug("Skipping DAK reference insert: missing lrpBookNo or onlineBookNo");
            return;
        }
        try {
            bookingReferenceJdbcRepository.insertDakReferenceIfAbsent(lrpBookNo.trim(), onlineBookNo.trim());
        } catch (Exception e) {
            // Non-blocking: shell + update must proceed; GET may lack onlineBookNo until DAK is backfilled
            logger.warn("Could not insert booking_reference DAK for book_no {} -> {}: {}",
                    lrpBookNo.trim(), onlineBookNo.trim(), e.getMessage());
        }
    }
}
