package com.solverminds.booking.repo;

import com.solverminds.booking.dto.BookingSummaryResponseDto;
import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.dto.BookingCreateResponse;
import com.solverminds.booking.dto.PagedBookingSummaryResult;
import com.solverminds.booking.dto.v2.BookingFilterV2;
import com.solverminds.booking.entity.EcomBooking;
import com.solverminds.booking.mapper.BookingSummaryMapper;
import com.solverminds.booking.service.BookingViewService;
import com.solverminds.booking.service.BookingViewLookupResult;
import com.solverminds.booking.service.BookingUpdateService;
import com.solverminds.booking.service.BookingUpdateIdResolverService;
import com.svm.ecom.common.enums.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class BookingRepository {

    private static final Logger logger = LoggerFactory.getLogger(BookingRepository.class);

    private final JdbcTemplate reportJdbcTemplate;
    private final BookingViewService bookingViewService;
    private final BookingUpdateService bookingUpdateService;
    private final BookingUpdateIdResolverService bookingUpdateIdResolverService;
    private final EcomBookingRepository ecomBookingRepository;

    public BookingRepository(
        @Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate,
        BookingViewService bookingViewService,
        BookingUpdateService bookingUpdateService,
        BookingUpdateIdResolverService bookingUpdateIdResolverService,
        EcomBookingRepository ecomBookingRepository) {
        this.reportJdbcTemplate = reportJdbcTemplate;
        this.bookingViewService = bookingViewService;
        this.bookingUpdateService = bookingUpdateService;
        this.bookingUpdateIdResolverService = bookingUpdateIdResolverService;
        this.ecomBookingRepository = ecomBookingRepository;
    }

    private void ensureReportDb() {
        if (reportJdbcTemplate == null) {
            throw new IllegalStateException("reportdb is not configured");
        }
    }

    /**
     * Maps API sort field names to the names expected by usp_getecombookingsummary_paged.
     * SP uses 'origineid' (typo) for origin; API uses 'originId'. Other fields pass through.
     */
    private static String toSpSortField(String apiSortField) {
        if (apiSortField == null) return null;
        if ("originId".equals(apiSortField)) return "origineid";
        if ("-originId".equals(apiSortField)) return "-origineid";
        return apiSortField;
    }

    /**
     * Get booking summary using the native query exec usp_getecombookingsummary
     * Parameters: customerCode, param2, status, param4, param5, param6
     * Default values: '20001374','','A','','','S'
     */
    public List<BookingSummaryResponseDto> getBookingSummary(String customerCode) {
        ensureReportDb();
        
        // Native query as specified: exec usp_getecombookingsummary '20001374','','A','','','S'
        // Using the customerCode from JWT token and default values for other parameters
        final String sql = "exec usp_getecombookingsummary ?, '', 'A', '', '', 'S'";
        
        return reportJdbcTemplate.query(sql, new BookingSummaryMapper(), customerCode);
    }

    /**
     * Get paged booking summary for V2 API. Single call to usp_getecombookingsummary_paged
     * returns total count (OUTPUT) and paged result set. Filter, sort, and pagination are done in the SP.
     * <p>
     * If you see "Column name or number of supplied values does not match table definition" (SQL 213),
     * the cause is in the database: the paged SP's #tmp expects 22 columns from usp_getecombookingsummary
     * (no port names; includes loadPortId, dischargePortId). Paged SP adds originName, deliveryName, polName, podName and bl_no, bl_status.
     * Ensure the deployed base SP returns 22 columns. See booking-service/docs/usp_getecombookingsummary_paged_analysis.md if needed.
     *
     * @param customerCode Customer code from JWT
     * @param filter Filter criteria (optional)
     * @param sortFields List of sort fields (e.g. "-createdAt", "originName"); comma-separated string is passed to SP
     * @param offset Number of records to skip
     * @param limit Number of records to fetch
     * @return PagedBookingSummaryResult with totalCount and items
     */
    public PagedBookingSummaryResult getBookingSummaryPagedWithCount(
            String customerCode,
            BookingFilterV2 filter,
            List<String> sortFields,
            int offset,
            int limit) {
        ensureReportDb();

        List<String> spSortFields = (sortFields != null && !sortFields.isEmpty())
                ? sortFields.stream()
                        .map(BookingRepository::toSpSortField)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
                : null;
        String sortString = (spSortFields != null && !spSortFields.isEmpty())
                ? String.join(",", spSortFields)
                : null;

        // SP expects limit >= 1 (FETCH NEXT 0 ROWS returns nothing); ensure we never pass 0
        int safeLimit = (limit < 1) ? 1 : Math.min(limit, 200);
        int safeOffset = Math.max(0, offset);

        CallableStatementCreator creator = con -> con.prepareCall("{call usp_getecombookingsummary_paged(?,?,?,?,?,?,?,?,?,?,?)}");
        CallableStatementCallback<PagedBookingSummaryResult> callback = cs -> {
                    cs.setString(1, customerCode);
                    cs.setInt(2, safeOffset);
                    cs.setInt(3, safeLimit);
                    cs.setString(4, sortString);
                    setFilterParam(cs, 5, filter != null ? filter.getIdContains() : null);
                    setFilterParam(cs, 6, filter != null ? filter.getStatusEquals() : null);
                    setFilterParam(cs, 7, filter != null ? filter.getOriginIdContains() : null);
                    setFilterParam(cs, 8, filter != null ? filter.getOriginNameContains() : null);
                    setFilterParam(cs, 9, filter != null ? filter.getDeliveryIdContains() : null);
                    setFilterParam(cs, 10, filter != null ? filter.getDeliveryNameContains() : null);
                    cs.registerOutParameter(11, Types.INTEGER);

                    cs.execute();
                    // SQL Server JDBC: consume result set(s) BEFORE reading OUTPUT parameter.
                    // SP may produce multiple result sets (e.g. count vs data); use the one with booking columns.
                    List<BookingSummaryResponseDto> items = new ArrayList<>();
                    ResultSet rs = cs.getResultSet();
                    while (rs != null) {
                        if (resultSetHasBookingColumns(rs)) {
                            BookingSummaryMapper mapper = new BookingSummaryMapper();
                            int rowNum = 0;
                            while (rs.next()) {
                                items.add(mapper.mapRow(rs, rowNum++));
                            }
                            rs.close();
                            // Drain remaining result sets so OUTPUT param is available
                            while (cs.getMoreResults()) {
                                ResultSet next = cs.getResultSet();
                                if (next != null) next.close();
                            }
                            break;
                        }
                        rs.close();
                        if (!cs.getMoreResults()) break;
                        rs = cs.getResultSet();
                    }
                    int total = cs.getInt(11);
            logger.debug("Booking summary paged: total={}, returned={}", total, items.size());
            return new PagedBookingSummaryResult(total, items);
        };
        return reportJdbcTemplate.execute(creator, callback);
    }

    private static void setFilterParam(CallableStatement cs, int index, String value) throws java.sql.SQLException {
        if (value != null && !value.trim().isEmpty()) {
            cs.setString(index, value.trim());
        } else {
            cs.setNull(index, Types.VARCHAR);
        }
    }

    /** True if this result set has the booking summary columns (e.g. trust_book_no from usp_getecombookingsummary_paged). */
    private static boolean resultSetHasBookingColumns(ResultSet rs) throws java.sql.SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        for (int i = 1; i <= cols; i++) {
            if ("trust_book_no".equalsIgnoreCase(meta.getColumnName(i))) return true;
        }
        return false;
    }

    /**
     * Create a new booking from the provided request.
     * TODO: Implement actual booking creation logic
     * 
     * @param request The booking request DTO
     * @return BookingCreateResponse with the created booking details
     */
    public BookingCreateResponse createBooking(BookingRequestDTO request) {
        // TODO: Implement actual booking creation logic
        // This is a placeholder implementation
        
        String generatedBookingNo = request.getOnlineBookNo();
        if (generatedBookingNo == null || generatedBookingNo.trim().isEmpty()) {
            generatedBookingNo = "BKON" + System.currentTimeMillis();
        }
        
        return new BookingCreateResponse(
            generatedBookingNo,
            request.getStatus() != null ? request.getStatus() : BookingStatus.D,
            "Booking created successfully"
        );
    }

    /**
     * Update an existing booking by onlineBookNo.
     * 
     * @param onlineBookNo The online booking number to update
     * @param request The booking request DTO with updated data
     * @param customerCodeFromJwt Customer code from JWT for authorization
     * @param loginNameFromJwt Login name from JWT for audit
     * @return BookingCreateResponse with update confirmation, or null if not found
     */
    public BookingCreateResponse updateBooking(String onlineBookNo, BookingRequestDTO request,
                                               String customerCodeFromJwt, String loginNameFromJwt) {
        if (onlineBookNo == null || onlineBookNo.trim().isEmpty()) {
            return null; // Booking not found
        }
        
        // Delegate to the update service to handle the update logic
        return bookingUpdateService.updateBooking(onlineBookNo, request, customerCodeFromJwt, loginNameFromJwt);
    }

    /**
     * Resolves a PUT path id (BKON or LRP {@code book_no}) to {@code ecombooking.ebookno}.
     * For unlinked LRP bookings, creates an ECOM shell before the existing update runs.
     */
    public String resolveOnlineBookNoForUpdate(String pathBookingId, BookingRequestDTO request,
                                               String customerCodeFromJwt, String loginNameFromJwt) {
        return bookingUpdateIdResolverService.resolveOnlineBookNoForUpdate(
                pathBookingId, request, customerCodeFromJwt, loginNameFromJwt);
    }

    /**
     * Find a booking by customer code and optional search criteria.
     * Delegates to BookingViewService to retrieve complete booking details.
     * 
     * @param customerCode Customer code from JWT, or {@code null} for SYSUSER read-only (no customer scope)
     * @param onlineBookNo Optional online booking number
     * @param bookingNumber Optional booking number
     * @param status Optional booking status
     * @return Optional<BookingRequestDTO> containing the booking if found
     */
    public Optional<BookingRequestDTO> findBooking(String customerCode, String onlineBookNo, 
                                                   String bookingNumber, String status) {
        return bookingViewService.findBooking(customerCode, onlineBookNo, bookingNumber, status);
    }

    /**
     * View-specific lookup with status-aware routing for GET /api/v1/booking/view.
     *
     * @param customerCode Customer code from JWT, or {@code null} for SYSUSER read-only (no customer scope)
     */
    public BookingViewLookupResult findBookingForView(String customerCode, String onlineBookNo,
            String bookingNumber, String status) {
        return bookingViewService.findBookingForView(customerCode, onlineBookNo, bookingNumber, status);
    }

    /**
     * Get cancelled/rejected bookings for a customer.
     * These bookings (status V or L) are excluded from the main stored procedure
     * and need to be fetched separately from the ecombooking table.
     * 
     * Note: This method returns limited data compared to the stored procedure results,
     * as the full booking details (vessel, route, etc.) are not available for
     * cancelled bookings in ecombooking.
     * 
     * @param customerCode Customer code from JWT
     * @return List of cancelled booking summaries
     */
    public List<BookingSummaryResponseDto> getCancelledBookingSummary(String customerCode) {
        if (customerCode == null || customerCode.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<EcomBooking> cancelledBookings = ecomBookingRepository.findCancelledBookingsByCustCode(customerCode);
        List<BookingSummaryResponseDto> summaries = new ArrayList<>();

        for (EcomBooking booking : cancelledBookings) {
            BookingSummaryResponseDto dto = new BookingSummaryResponseDto();
            dto.setOnlineBookNo(booking.getEbookno());
            dto.setOriginPortId(booking.getOriginPortId());
            dto.setFinalPortId(booking.getFinalPortId());
            dto.setCreatedDate(booking.getDatecr());
            dto.setChangeDate(booking.getDateup());
            dto.setStatus(safeBookingStatus(booking.getEbookStatus()));
            dto.setStatusDesc(getStatusDescription(booking.getEbookStatus()));
            dto.setAgencyId(booking.getAgencyid());
            dto.setAgencyRefNo(booking.getAgencyRefNo());
            summaries.add(dto);
        }

        logger.debug("Found {} cancelled bookings for customer {}", summaries.size(), customerCode);
        return summaries;
    }

    /**
     * Get booking summary including cancelled bookings.
     * Combines results from the main stored procedure with cancelled bookings.
     * 
     * @param customerCode Customer code from JWT
     * @param includeInactive If true, includes cancelled (V, L) bookings
     * @return Combined list of booking summaries
     */
    public List<BookingSummaryResponseDto> getBookingSummaryWithInactive(String customerCode, boolean includeInactive) {
        List<BookingSummaryResponseDto> results = getBookingSummary(customerCode);
        
        if (includeInactive) {
            List<BookingSummaryResponseDto> cancelled = getCancelledBookingSummary(customerCode);
            results.addAll(cancelled);
            logger.debug("Added {} cancelled bookings to summary for customer {}", cancelled.size(), customerCode);
        }
        
        return results;
    }

    /**
     * Safe BookingStatus conversion for cancelled bookings.
     */
    private BookingStatus safeBookingStatus(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            String normalizedValue = value.trim().toUpperCase();
            if ("A".equals(normalizedValue)) {
                normalizedValue = "P";
            }
            return BookingStatus.fromValue(normalizedValue);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get human-readable status description for cancelled bookings.
     */
    private String getStatusDescription(String status) {
        if (status == null) return null;
        switch (status.toUpperCase()) {
            case "V": return "Rejected";
            case "L": return "Cancelled";
            case "D": return "Draft";
            case "S": return "Submitted";
            case "P":
            case "A": return "In-Progress";
            case "C": return "Confirmed";
            case "T": return "Template";
            default: return status;
        }
    }
}
