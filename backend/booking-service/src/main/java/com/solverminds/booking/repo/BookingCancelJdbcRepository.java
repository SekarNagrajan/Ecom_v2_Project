package com.solverminds.booking.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class BookingCancelJdbcRepository {

    private static final Logger logger =
            LoggerFactory.getLogger(BookingCancelJdbcRepository.class);

    // ══════════════════════════════════════════════════════════════
    // SQL CONSTANTS — grouped by key used
    // ══════════════════════════════════════════════════════════════

    // ── ECOMDB — key: ebookNo (BKON...) ──────────────────────────
    private static final String GET_ECOM_BOOKING =
            "SELECT ebook_status, extBookNo, cust_code AS CustomerCode " +
            "FROM   ecombooking " +
            "WHERE  ebookno = ?";                             // ? = ebookNo

    /** Status-only update: does not touch {@code declined_remarks}. */
    private static final String CANCEL_ECOM_BOOKING =
            "UPDATE ecombooking " +
            "SET    ebook_status = ? " +
            "WHERE  ebookno      = ?";                        // ? = ebookNo

    /**
     * Path A / Path B (draft): {@code ebook_status} plus API cancellation text on
     * {@code declined_remarks} (NVARCHAR length 1000 on {@code ecombooking}).
     */
    private static final String CANCEL_ECOM_BOOKING_PATH_A =
            "UPDATE ecombooking " +
            "SET    ebook_status     = ?, " +
            "       declined_remarks = ? " +
            "WHERE  ebookno          = ?";

    private static final int ECOMBOOKING_DECLINED_REMARKS_MAX_LEN = 1000;

    // ── EDI DB — key: ebookNo (BKON...) — edidocstatus lives on EDI datasource in this codebase
    // edidocstatus uses ebookNo directly — NOT extBookNo
    private static final String GET_EDI_STATUS =
            "SELECT amendment_version, status " +
            "FROM   edidocstatus (NOLOCK) " +
            "WHERE  book_no    = ? " +                        // ? = ebookNo
            "ORDER  BY receivdate DESC";

    private static final String VOID_EDI_STATUS =
            "UPDATE edidocstatus " +
            "SET    status  = 'V' " +
            "WHERE  status  IN ('R', 'E') " +
            "  AND  book_no = ?";                             // ? = ebookNo

    // ── LRP / Report DB — key: extBookNo (from ecombooking.extBookNo) ──────
    // Resolves the actual booking.book_no from extBookNo
    private static final String GET_LRP_BOOKING_STATUS =
            "SELECT b.status  AS lrpStatus, " +
            "       b.book_no AS bookNo " +
            "FROM   booking b (NOLOCK) " +
            "INNER  JOIN booking_cust bc (NOLOCK) " +
            "       ON  b.book_no     = bc.book_no " +
            "      AND  (bc.custtypeid = 'AP' OR bc.custtypeid = 'BP') " +
            "WHERE  b.status  IN ('C', 'D', 'V') " +
            "  AND  b.book_no = ?";                           // ? = extBookNo

    private static final String GET_LRP_REMARKS =
            "SELECT remarks " +
            "FROM   booking (NOLOCK) " +
            "WHERE  book_no = ?";                             // ? = extBookNo

    // ── LRP / Report DB — key: bookNo (booking.book_no from JOIN result) ───
    private static final String CANCEL_LRP_BOOKING =
            "UPDATE booking " +
            "SET    status         = 'V', " +
            "       remarks_cancel = ?, " +
            "       userup         = ?, " +
            "       dateup         = GETDATE(), " +
            "       remarks        = ? " +
            "WHERE  book_no        = ?";                      // ? = bookNo

    private static final String CHECK_CRO =
            "SELECT DISTINCT flsbookingno " +
            "FROM   plotdomaster " +
            "WHERE  flsbookingno = ?";                        // ? = bookNo

    private static final String CHECK_BL =
            "SELECT TOP 1 book_no " +
            "FROM   bl " +
            "WHERE  status  IN ('I', 'C', 'D') " +
            "  AND  book_no = ?";                             // ? = bookNo

    private static final String CHECK_OPERATIONS =
            "SELECT TOP 1 bookno " +
            "FROM   operationdetail " +
            "WHERE  bookno = ?";                              // ? = bookNo

    private static final String CHECK_EMS_SP =
            "{call usp_emsBookingContMovement(?)}";           // ? = bookNo

    // ══════════════════════════════════════════════════════════════
    // JDBC TEMPLATE INJECTION
    // ══════════════════════════════════════════════════════════════

    private final JdbcTemplate ecomJdbc;   // ECOMDB
    /** EDI datasource — {@code edidocstatus}. */
    private final JdbcTemplate ediJdbc;
    /** LRP/report operational datasource — {@code booking}, checks, SP. */
    private final JdbcTemplate lrpJdbc;

    public BookingCancelJdbcRepository(
            @Qualifier("ecomJdbcTemplate") JdbcTemplate ecomJdbc,
            @Qualifier("ediJdbcTemplate") JdbcTemplate ediJdbc,
            @Qualifier("lrpJdbcTemplate") JdbcTemplate lrpJdbc) {
        this.ecomJdbc = ecomJdbc;
        this.ediJdbc = ediJdbc;
        this.lrpJdbc = lrpJdbc;
    }

    // ══════════════════════════════════════════════════════════════
    // ECOMDB METHODS — all use ebookNo
    // ══════════════════════════════════════════════════════════════

    /**
     * Reads ebook_status, extBookNo, CustomerCode from ecombooking.
     * @param ebookNo  BKON... from API path
     */
    public Optional<EcomBookingCancelRow> findByEcomBookNo(String ebookNo) {
        return ecomJdbc.query(
                GET_ECOM_BOOKING,
                ps -> ps.setString(1, ebookNo),
                rs -> {
                    if (rs.next()) {
                        return Optional.of(new EcomBookingCancelRow(
                                rs.getString("ebook_status"),
                                rs.getString("extBookNo"),
                                rs.getString("CustomerCode")
                        ));
                    }
                    return Optional.empty();
                }
        );
    }

    /**
     * Updates {@code ebook_status} only. Does not modify {@code declined_remarks}.
     * @param ebookNo    BKON... from API path
     * @param newStatus  'V' (had LRP) or 'I' (no LRP)
     */
    public void cancelEcomBooking(String ebookNo, String newStatus) {
        ecomJdbc.update(CANCEL_ECOM_BOOKING, newStatus, ebookNo);
        logger.debug("cancelEcomBooking: ebookNo={} → {}", ebookNo, newStatus);
    }

    /**
     * Path A and Path B (draft LRP): updates {@code ebook_status} and persists the API
     * cancellation reason into {@code declined_remarks}. Long text is truncated to fit the column.
     *
     * @param ebookNo         BKON... from API path
     * @param newStatus       'V' (had LRP / draft cancel) or 'I' (Path A, no LRP)
     * @param declinedRemarks cancellation reason (may be empty; never null after service trim)
     */
    public void cancelEcomBookingPathA(String ebookNo, String newStatus, String declinedRemarks) {
        String remarks = declinedRemarks != null ? declinedRemarks : "";
        if (remarks.length() > ECOMBOOKING_DECLINED_REMARKS_MAX_LEN) {
            remarks = remarks.substring(0, ECOMBOOKING_DECLINED_REMARKS_MAX_LEN);
        }
        ecomJdbc.update(CANCEL_ECOM_BOOKING_PATH_A, newStatus, remarks, ebookNo);
        logger.debug("cancelEcomBookingPathA: ebookNo={} → status={}", ebookNo, newStatus);
    }

    // ══════════════════════════════════════════════════════════════
    // EDI METHODS — all use ebookNo (direct link)
    // ══════════════════════════════════════════════════════════════

    /**
     * Reads the latest EDI document status from edidocstatus.
     * @param ebookNo  BKON... from API path — direct link to edidocstatus.book_no
     */
    public Optional<EdiStatusRow> getEdiStatus(String ebookNo) {
        return ediJdbc.query(
                GET_EDI_STATUS,
                ps -> ps.setString(1, ebookNo),
                rs -> {
                    if (rs.next()) {
                        return Optional.of(new EdiStatusRow(
                                rs.getString("status"),
                                rs.getString("amendment_version")
                        ));
                    }
                    return Optional.empty();
                }
        );
    }

    /**
     * Voids EDI document — only rows with status IN ('R','E').
     * @param ebookNo  BKON... from API path — direct link to edidocstatus.book_no
     */
    public void voidEdiStatus(String ebookNo) {
        int rows = ediJdbc.update(VOID_EDI_STATUS, ebookNo);
        logger.debug("voidEdiStatus: ebookNo={} rows affected={}", ebookNo, rows);
    }

    // ══════════════════════════════════════════════════════════════
    // LRPDB BOOKING METHODS — use extBookNo to resolve, bookNo for ops
    // ══════════════════════════════════════════════════════════════

    /**
     * Resolves LRP booking status and book_no from extBookNo.
     * @param extBookNo  ecombooking.extBookNo → joins to booking.book_no
     */
    public Optional<LrpBookingRow> getLrpBookingStatus(String extBookNo) {
        return lrpJdbc.query(
                GET_LRP_BOOKING_STATUS,
                ps -> ps.setString(1, extBookNo),
                rs -> {
                    if (rs.next()) {
                        return Optional.of(new LrpBookingRow(
                                rs.getString("lrpStatus"),
                                rs.getString("bookNo")
                        ));
                    }
                    return Optional.empty();
                }
        );
    }

    /**
     * Reads existing remarks from booking table.
     * @param extBookNo  ecombooking.extBookNo
     */
    public String getLrpBookingRemarks(String extBookNo) {
        return lrpJdbc.query(
                GET_LRP_REMARKS,
                ps -> ps.setString(1, extBookNo),
                rs -> rs.next() ? rs.getString("remarks") : ""
        );
    }

    /**
     * Cancels the LRP booking.
     * @param bookNo  booking.book_no from the JOIN result
     */
    public void cancelLrpBooking(String bookNo,
                                  String remarksCancel,
                                  String userUp,
                                  String remarks) {
        lrpJdbc.update(CANCEL_LRP_BOOKING,
                remarksCancel, userUp, remarks, bookNo);
        logger.debug("cancelLrpBooking: bookNo={}", bookNo);
    }

    // ══════════════════════════════════════════════════════════════
    // LRPDB VALIDATION CHECKS — all use bookNo (from JOIN result)
    // ══════════════════════════════════════════════════════════════

    /** @param bookNo  booking.book_no from JOIN result */
    public boolean hasCroRecord(String bookNo) {
        List<Map<String, Object>> rows =
                lrpJdbc.queryForList(CHECK_CRO, bookNo);
        return !rows.isEmpty();
    }

    /** @param bookNo  booking.book_no from JOIN result */
    public boolean hasBlRecord(String bookNo) {
        List<Map<String, Object>> rows =
                lrpJdbc.queryForList(CHECK_BL, bookNo);
        return !rows.isEmpty();
    }

    /** @param bookNo  booking.book_no from JOIN result */
    public boolean hasOperationRecord(String bookNo) {
        List<Map<String, Object>> rows =
                lrpJdbc.queryForList(CHECK_OPERATIONS, bookNo);
        return !rows.isEmpty();
    }

    /** @param bookNo  booking.book_no from JOIN result */
    public boolean hasEmsMovement(String bookNo) {
        try {
            List<Map<String, Object>> rows =
                    lrpJdbc.queryForList(CHECK_EMS_SP, bookNo);
            return !rows.isEmpty();
        } catch (Exception e) {
            logger.error("EMS SP check failed bookNo={}: {}", bookNo, e.getMessage());
            return false;   // fail-open: do not block cancel if SP unavailable
        }
    }

    // ══════════════════════════════════════════════════════════════
    // INNER ROW VOs
    // ══════════════════════════════════════════════════════════════

    public static class EcomBookingCancelRow {
        public final String eBookStatus;
        public final String extBookNo;    // links to LRPDB booking.book_no
        public final String custCode;     // customer ownership check

        public EcomBookingCancelRow(String eBookStatus,
                                    String extBookNo,
                                    String custCode) {
            this.eBookStatus = eBookStatus;
            this.extBookNo   = extBookNo;
            this.custCode    = custCode;
        }
    }

    public static class EdiStatusRow {
        public final String status;
        public final String amendmentVersion;

        public EdiStatusRow(String status, String amendmentVersion) {
            this.status           = status;
            this.amendmentVersion = amendmentVersion;
        }
    }

    public static class LrpBookingRow {
        public final String lrpStatus;
        public final String bookNo;       // booking.book_no — use for all LRP ops

        public LrpBookingRow(String lrpStatus, String bookNo) {
            this.lrpStatus = lrpStatus;
            this.bookNo    = bookNo;
        }
    }
}
