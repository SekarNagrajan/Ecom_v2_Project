package com.solverminds.booking.service;

import com.solverminds.booking.repo.BookingCancelJdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 4 sequential validation checks against booking.book_no.
 * All checks use bookNo (from booking JOIN result) — NOT ebookNo or extBookNo.
 */
@Service
public class BookingValidationService {

    private static final Logger logger =
            LoggerFactory.getLogger(BookingValidationService.class);

    public static final String VALID     = "valid";
    public static final String CRO       = "cro";
    public static final String BL        = "bl";
    public static final String OPERATION = "operation";
    public static final String EMS       = "ems";

    private final BookingCancelJdbcRepository repo;

    public BookingValidationService(BookingCancelJdbcRepository repo) {
        this.repo = repo;
    }

    /**
     * @param bookNo  booking.book_no from LRP JOIN result — NOT ebookNo
     */
    public String validate(String bookNo) {
        logger.debug("validate: bookNo={}", bookNo);

        // CHECK 1 — plotdomaster
        if (repo.hasCroRecord(bookNo)) {
            logger.debug("Blocked by CRO: {}", bookNo);
            return CRO;
        }
        // CHECK 2 — bl
        if (repo.hasBlRecord(bookNo)) {
            logger.debug("Blocked by BL: {}", bookNo);
            return BL;
        }
        // CHECK 3 — operationdetail
        if (repo.hasOperationRecord(bookNo)) {
            logger.debug("Blocked by Operation: {}", bookNo);
            return OPERATION;
        }
        // CHECK 4 — usp_emsBookingContMovement
        if (repo.hasEmsMovement(bookNo)) {
            logger.debug("Blocked by EMS: {}", bookNo);
            return EMS;
        }

        logger.debug("Validation passed: {}", bookNo);
        return VALID;
    }
}
