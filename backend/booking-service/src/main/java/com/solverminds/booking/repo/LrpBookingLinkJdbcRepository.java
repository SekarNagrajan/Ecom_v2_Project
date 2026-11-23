package com.solverminds.booking.repo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Read-only LRP lookups for linking a direct {@code booking.book_no} to a new ECOM shell.
 */
@Repository
public class LrpBookingLinkJdbcRepository {

    private static final String FIND_LRP_BOOKING_FOR_CUSTOMER =
            "SELECT b.status AS lrpStatus, b.book_no AS bookNo " +
            "FROM   booking b (NOLOCK) " +
            "INNER  JOIN booking_cust bc (NOLOCK) " +
            "       ON  b.book_no = bc.book_no " +
            "WHERE  b.book_no = ? " +
            "  AND  bc.custId = ? " +
            "  AND  (bc.custTypeId = 'AP' OR bc.custTypeId = 'BP')";

    private final JdbcTemplate lrpJdbc;

    public LrpBookingLinkJdbcRepository(@Qualifier("lrpJdbcTemplate") JdbcTemplate lrpJdbc) {
        this.lrpJdbc = lrpJdbc;
    }

    public Optional<LrpBookingLinkRow> findBookingForCustomer(String bookNo, String customerCode) {
        if (bookNo == null || bookNo.trim().isEmpty() || customerCode == null || customerCode.trim().isEmpty()) {
            return Optional.empty();
        }
        return lrpJdbc.query(
                FIND_LRP_BOOKING_FOR_CUSTOMER,
                ps -> {
                    ps.setString(1, bookNo.trim());
                    ps.setString(2, customerCode.trim());
                },
                rs -> {
                    if (rs.next()) {
                        return Optional.of(new LrpBookingLinkRow(
                                rs.getString("lrpStatus"),
                                rs.getString("bookNo")));
                    }
                    return Optional.empty();
                });
    }

    public static final class LrpBookingLinkRow {
        public final String lrpStatus;
        public final String bookNo;

        public LrpBookingLinkRow(String lrpStatus, String bookNo) {
            this.lrpStatus = lrpStatus;
            this.bookNo = bookNo;
        }
    }
}
