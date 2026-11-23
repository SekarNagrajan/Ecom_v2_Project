package com.solverminds.booking.service;

import com.solverminds.booking.exception.BookingCancellationConflictException;
import com.solverminds.booking.exception.BookingNotFoundException;
import com.solverminds.booking.exception.LrpBookingLinkNotAllowedException;
import com.solverminds.booking.repo.LrpBookingLinkJdbcRepository;
import com.solverminds.booking.repo.LrpBookingLinkJdbcRepository.LrpBookingLinkRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

/**
 * Validates an LRP {@code book_no} before creating an ECOM shell for update-by-LRP flow.
 */
@Service
public class LrpBookingLinkValidationService {

    private static final Logger logger = LoggerFactory.getLogger(LrpBookingLinkValidationService.class);

    private final LrpBookingLinkJdbcRepository lrpBookingLinkJdbcRepository;
    private final BookingValidationService bookingValidationService;

    public LrpBookingLinkValidationService(
            LrpBookingLinkJdbcRepository lrpBookingLinkJdbcRepository,
            BookingValidationService bookingValidationService) {
        this.lrpBookingLinkJdbcRepository = lrpBookingLinkJdbcRepository;
        this.bookingValidationService = bookingValidationService;
    }

    /**
     * @param lrpBookNo    LRP booking number from API path
     * @param customerCode Customer code from JWT
     * @return Resolved LRP row when validation passes
     */
    public LrpBookingLinkRow validateForUpdateLink(String lrpBookNo, String customerCode) {
        String bookNo = lrpBookNo != null ? lrpBookNo.trim() : "";
        if (bookNo.isEmpty()) {
            throw new BookingNotFoundException("Booking not found: " + lrpBookNo);
        }

        Optional<LrpBookingLinkRow> rowOpt =
                lrpBookingLinkJdbcRepository.findBookingForCustomer(bookNo, customerCode);
        if (!rowOpt.isPresent()) {
            logger.debug("LRP booking not found or customer mismatch: bookNo={}", bookNo);
            throw new BookingNotFoundException("Booking not found: " + bookNo);
        }

        LrpBookingLinkRow row = rowOpt.get();
        String status = row.lrpStatus != null ? row.lrpStatus.trim().toUpperCase(Locale.ROOT) : "";

        if ("C".equals(status)) {
            throw new LrpBookingLinkNotAllowedException(status,
                    "Booking is Confirmed in ERP (LRP=C). No status changes or edits are allowed.");
        }
        if ("V".equals(status)) {
            throw new LrpBookingLinkNotAllowedException(status,
                    "Booking is Rejected in ERP (LRP=V). No status changes or edits are allowed.");
        }
        if (!"D".equals(status)) {
            throw new LrpBookingLinkNotAllowedException(status,
                    "Booking cannot be updated. LRP status must be Draft (D). Current status: " + status);
        }

        assertOperationalEligibility(row.bookNo);
        return row;
    }

    private void assertOperationalEligibility(String bookNo) {
        String validation = bookingValidationService.validate(bookNo);
        switch (validation) {
            case BookingValidationService.CRO:
                throw new BookingCancellationConflictException(
                        "CRO_EXISTS", "CRO is done for this Booking");
            case BookingValidationService.BL:
                throw new BookingCancellationConflictException(
                        "BL_EXISTS", "BL is available for this Booking");
            case BookingValidationService.OPERATION:
                throw new BookingCancellationConflictException(
                        "OPERATION_EXISTS", "Load Planned is done for this Booking");
            case BookingValidationService.EMS:
                throw new BookingCancellationConflictException(
                        "EMS_EXISTS",
                        "CRO and following Container already Sent to Shipper. You cannot update this booking");
            case BookingValidationService.VALID:
                break;
            default:
                throw new BookingCancellationConflictException(
                        "VALIDATION_ERROR", "Booking validation could not be completed");
        }
    }
}
