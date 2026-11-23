package com.solverminds.booking.service;

import com.solverminds.booking.entity.EcomBooking;
import com.solverminds.booking.repo.EcomBookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for looking up LRP (ERP) status for a booking.
 * 
 * The LRP status is stored in the documentation database (reportDB) in the booking table.
 * The link between ECOM booking (ebookno) and ERP booking (book_no) is via:
 * - ecombooking.extBookNo field which contains the ERP booking number
 */
@Service
public class LrpStatusLookupService {

    private static final Logger logger = LoggerFactory.getLogger(LrpStatusLookupService.class);

    private final JdbcTemplate reportJdbcTemplate;
    private final EcomBookingRepository ecomBookingRepository;

    public LrpStatusLookupService(
            @Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate,
            EcomBookingRepository ecomBookingRepository) {
        this.reportJdbcTemplate = reportJdbcTemplate;
        this.ecomBookingRepository = ecomBookingRepository;
    }

    /**
     * Lookup the LRP (ERP) status for a booking by its online booking number.
     * 
     * The query finds the booking.status by:
     * 1. Getting the extBookNo from ecombooking table
     * 2. Using extBookNo to get the status from the booking table in reportDB
     * 
     * @param onlineBookNo The ECOM online booking number (ebookno)
     * @return The LRP status (D=Draft, C=Confirmed, V=Rejected, etc.), or null if not found
     */
    public String getLrpStatus(String onlineBookNo) {
        if (onlineBookNo == null || onlineBookNo.trim().isEmpty()) {
            return null;
        }

        try {
            // Step 1: Get the extBookNo from ecombooking table
            Optional<EcomBooking> ecomBookingOpt = ecomBookingRepository.findByEbookno(onlineBookNo.trim());
            if (!ecomBookingOpt.isPresent()) {
                logger.debug("ECOM booking not found for onlineBookNo: {}", onlineBookNo);
                return null;
            }

            String extBookNo = ecomBookingOpt.get().getExtBookNo();
            if (extBookNo == null || extBookNo.trim().isEmpty()) {
                logger.debug("extBookNo is empty for onlineBookNo: {}", onlineBookNo);
                return null;
            }

            // Step 2: Get the LRP status from reportDB using extBookNo
            return getLrpStatusByBookNo(extBookNo.trim());

        } catch (Exception e) {
            logger.error("Error looking up LRP status for onlineBookNo {}: {}", onlineBookNo, e.getMessage());
            return null;
        }
    }

    /**
     * Lookup the LRP status by ERP booking number directly.
     * 
     * @param bookNo The ERP booking number (book_no / extBookNo)
     * @return The LRP status, or null if not found
     */
    public String getLrpStatusByBookNo(String bookNo) {
        if (bookNo == null || bookNo.trim().isEmpty()) {
            return null;
        }

        if (reportJdbcTemplate == null) {
            logger.warn("reportJdbcTemplate not configured, cannot lookup LRP status");
            return null;
        }

        try {
            String sql = "SELECT status FROM booking WITH (NOLOCK) WHERE book_no = ?";

            String lrpStatus = reportJdbcTemplate.query(sql, rs -> {
                if (rs.next()) {
                    return rs.getString("status");
                }
                return null;
            }, bookNo.trim());

            logger.debug("LRP status for bookNo {}: {}", bookNo, lrpStatus);
            return lrpStatus;

        } catch (Exception e) {
            logger.error("Error looking up LRP status for bookNo {}: {}", bookNo, e.getMessage());
            return null;
        }
    }
}
