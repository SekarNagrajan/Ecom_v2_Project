package com.solverminds.booking.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Writes to LRP {@code booking_reference} (documentation database).
 */
@Repository
public class BookingReferenceJdbcRepository {

    private static final Logger logger = LoggerFactory.getLogger(BookingReferenceJdbcRepository.class);
    private static final String REF_ID_DAK = "DAK";

    private static final String EXISTS_DAK =
            "SELECT 1 FROM booking_reference WITH (NOLOCK) "
                    + "WHERE book_no = ? AND refid = ?";

    private static final String INSERT_DAK_IF_ABSENT =
            "INSERT INTO booking_reference (book_no, refid, refval, isedited) "
                    + "SELECT ?, ?, ?, 'N' "
                    + "WHERE NOT EXISTS ("
                    + "  SELECT 1 FROM booking_reference "
                    + "  WHERE book_no = ? AND refid = ?"
                    + ")";

    private final JdbcTemplate documentationJdbc;

    public BookingReferenceJdbcRepository(
            @Qualifier("documentationJdbcTemplate") JdbcTemplate documentationJdbc) {
        this.documentationJdbc = documentationJdbc;
    }

    public boolean existsDakReference(String lrpBookNo) {
        if (lrpBookNo == null || lrpBookNo.trim().isEmpty()) {
            return false;
        }
        Boolean found = documentationJdbc.query(
                EXISTS_DAK,
                rs -> rs.next() ? Boolean.TRUE : Boolean.FALSE,
                lrpBookNo.trim(),
                REF_ID_DAK);
        return Boolean.TRUE.equals(found);
    }

    /**
     * @return rows inserted (0 if DAK already exists, 1 on success)
     */
    public int insertDakReferenceIfAbsent(String lrpBookNo, String onlineBookNo) {
        if (lrpBookNo == null || lrpBookNo.trim().isEmpty()
                || onlineBookNo == null || onlineBookNo.trim().isEmpty()) {
            return 0;
        }
        String bookNo = lrpBookNo.trim();
        String bkon = onlineBookNo.trim();
        int rows = documentationJdbc.update(
                INSERT_DAK_IF_ABSENT,
                bookNo,
                REF_ID_DAK,
                bkon,
                bookNo,
                REF_ID_DAK);
        if (rows > 0) {
            logger.info("Inserted booking_reference DAK: book_no={}, refval={}", bookNo, bkon);
        } else {
            logger.debug("booking_reference DAK already exists for book_no={}", bookNo);
        }
        return rows;
    }
}
