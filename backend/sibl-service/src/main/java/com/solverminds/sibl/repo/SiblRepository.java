package com.solverminds.sibl.repo;

import com.solverminds.sibl.dto.SiSummaryDto;
import com.svm.ecom.common.enums.BlStatus;
import com.svm.ecom.common.enums.BookingStatus;
import com.svm.ecom.common.enums.EcomPrintStatus;
import com.svm.ecom.common.enums.SiStatus;
import com.solverminds.sibl.dto.BlSummaryDto;
import com.solverminds.sibl.dto.SIBLRequestDTO;
import com.solverminds.sibl.dto.SICreateResponse;
import com.solverminds.sibl.dto.BookingRequestDTO;
import com.solverminds.sibl.service.SiCreateService;
import com.solverminds.sibl.service.SiUpdateService;
import com.solverminds.sibl.service.SiViewService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Repository("siblSiblRepository")
public class SiblRepository {

    private final JdbcTemplate reportJdbcTemplate;
    private final SiCreateService siCreateService;
    private final SiUpdateService siUpdateService;
    private final SiViewService siViewService;

    public SiblRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate,
            SiCreateService siCreateService,
            SiUpdateService siUpdateService,
            SiViewService siViewService) {
        this.reportJdbcTemplate = reportJdbcTemplate;
        this.siCreateService = siCreateService;
        this.siUpdateService = siUpdateService;
        this.siViewService = siViewService;
    }

    private void ensureReportDb() {
        if (reportJdbcTemplate == null) {
            throw new IllegalStateException("reportdb is not configured");
        }
    }

    /**
     * Get SI summary data by executing stored procedure usp_ecomSISummaryNew
     */
    public List<SiSummaryDto> getSiSummary(String customerCode) {
        ensureReportDb();
        String sql = "EXEC [usp_ecomSISummaryNew] ?";
        return reportJdbcTemplate.query(sql, new Object[] { customerCode }, new SiSummaryRowMapper());
    }

    /**
     * Get BL summary data by executing stored procedure usp_EcomBLSummary
     */
    public List<BlSummaryDto> getBlSummary(String customerCode, String types, String flag) {
        ensureReportDb();
        String sql = "EXEC [usp_EcomBLSummary] ?, ?, ?";
        return reportJdbcTemplate.query(sql, new Object[] { customerCode, types, flag }, new BlSummaryRowMapper());
    }

    /**
     * Create SI (Shipping Instruction) using the SiCreateService
     * This method delegates to the service layer for proper SI creation
     * 
     * @param loginName Login username from JWT (used for CrUser per spec)
     */
    public SICreateResponse createSi(String customerCode, String loginName, SIBLRequestDTO request) {
        return siCreateService.createSi(customerCode, loginName, request);
    }

    /**
     * Update an existing SI for a given customer.
     * Save type: D=DraftUpdate (update in place), S=Submit (delete all ecomSI +
     * insert newly).
     * 
     * @param loginName Login username from JWT (for ChgUser and EDI export when
     *                  status=S)
     */
    public SICreateResponse updateSi(String customerCode, String loginName, String onlineSINo, SIBLRequestDTO request) {
        return siUpdateService.updateSi(customerCode, loginName, onlineSINo, request);
    }

    /**
     * View bill-of-lading by bookingNo, onlineSINo, or blNumber (priority:
     * bookingNo onlineSINo ? blNumber).
     * Returns SIBLRequestDTO (same shape as SI create payload). No status
     * filtering.
     *
     * @param customerCode The customer code (from JWT)
     * @param bookingNo    Optional booking number (same logic as booking view,
     *                     in-process)
     * @param onlineSINo   Optional online SI number (fetch from ecomSI)
     * @param blNumber     Optional BL number (fetch from bl table)
     * @return Optional with SIBLRequestDTO if found; empty otherwise
     */
    public Optional<SIBLRequestDTO> viewBillOfLading(String customerCode, String bookingNo,
            String onlineSINo, String blNumber) {
        if (customerCode == null || customerCode.trim().isEmpty()) {
            return Optional.empty();
        }
        return siViewService.findBillOfLadingView(customerCode, bookingNo, onlineSINo, blNumber);
    }

    /**
     * View a booking/SI with optional filters (legacy; returns BookingRequestDTO).
     * Prefer {@link #viewBillOfLading} for unified SIBLRequestDTO response.
     */
    public Optional<BookingRequestDTO> viewBooking(String customerCode, String onlineSINo,
            String blNumber, String status) {
        if (customerCode == null || customerCode.trim().isEmpty()) {
            return Optional.empty();
        }
        return siViewService.findSI(customerCode, onlineSINo, blNumber, status);
    }

    /**
     * Update an existing BL for a given customer.
     * This method should be implemented with proper stored procedure call
     * and database logic to update the BL request data.
     */
    public SICreateResponse updateBl(String customerCode, String blNumber, String onlineSINo, SIBLRequestDTO request) {
        // TODO: Implement actual database logic to update BL
        // This is a placeholder implementation - you'll need to:
        // 1. Call appropriate stored procedure to update BL
        // 2. Handle nested data updates (customers, containers, commodities, etc.)
        // 3. Validate that the BL exists and belongs to the customer
        // 4. Optionally use onlineSINo for additional validation/linking
        // 5. Return proper response with updated BL details

        String effectiveSINo = (onlineSINo != null && !onlineSINo.trim().isEmpty()) ? onlineSINo : null;
        return new SICreateResponse(effectiveSINo, SiStatus.D, "BL updated successfully");
    }

    /**
     * Try timestamp first, then parse string to LocalDateTime (ISO or
     * space-separated).
     */
    private static LocalDateTime toLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        if (ts != null)
            return ts.toLocalDateTime();
        String s = rs.getString(column);
        if (s == null || s.trim().isEmpty())
            return null;
        s = s.trim();
        for (String pattern : new String[] { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss.SSS" }) {
            try {
                return LocalDateTime.parse(s, DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException ignored) {
            }
        }
        try {
            return LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {
        }
        return null;
    }

    /**
     * Row mapper for SI Summary data
     */
    private static class SiSummaryRowMapper implements RowMapper<SiSummaryDto> {
        @Override
        public SiSummaryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            SiSummaryDto dto = new SiSummaryDto();
            dto.setBookNo(rs.getString("book_no"));
            dto.setDocId(rs.getString("docid"));
            String sistatus = rs.getString("sistatus");
            dto.setSiStatus(safeSiStatus(sistatus));
            dto.setBookSource(rs.getString("booksource"));
            dto.setBlNo(rs.getString("bl_no"));
            dto.setBlStatus(safeBlStatus(rs.getString("blstatus")));
            dto.setLoadPortId(rs.getString("loadportid"));
            dto.setDischargePortId(rs.getString("dischargeportid"));
            Timestamp polatTs = rs.getTimestamp("polat");
            dto.setPolAt(polatTs != null ? polatTs.toLocalDateTime().atOffset(ZoneOffset.UTC) : null);
            dto.setCreatedSi(rs.getString("createdsi"));
            dto.setRequestedSi(rs.getString("requestedsi"));
            dto.setBkStatus(safeBookingStatus(rs.getString("bkstatus")));
            dto.setAgencyRefNo(rs.getString("agencyrefno"));
            dto.setAppVersion(rs.getString("appversion"));
            dto.setOriginPortId(rs.getString("originportid"));
            dto.setFinalPortId(rs.getString("finalportid"));
            dto.setIsSiLocked(rs.getBoolean("issilocked"));

            Timestamp datecr = rs.getTimestamp("datecr");
            if (datecr != null)
                dto.setDateCr(datecr.toLocalDateTime());

            Timestamp dateup = rs.getTimestamp("dateup");
            if (dateup != null)
                dto.setDateUp(dateup.toLocalDateTime());

            return dto;
        }
    }

    /**
     * Row mapper for BL Summary data
     */
    private static class BlSummaryRowMapper implements RowMapper<BlSummaryDto> {
        @Override
        public BlSummaryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            BlSummaryDto dto = new BlSummaryDto();
            dto.setBookNo(rs.getString("book_no"));
            dto.setRefval(rs.getString("refval"));
            dto.setAgencyrefno(rs.getString("agencyrefno"));
            dto.setBlNo(rs.getString("bl_no"));
            dto.setOriginPortId(rs.getString("originportid"));
            dto.setLoadportid(rs.getString("loadportid"));
            dto.setDischargeportid(rs.getString("dischargeportid"));
            dto.setFinalPortId(rs.getString("finalportid"));
            Timestamp polatTs = rs.getTimestamp("polat");
            dto.setPolat(polatTs != null ? polatTs.toLocalDateTime().atOffset(ZoneOffset.UTC) : null);
            dto.setStatus(safeBlStatus(rs.getString("status")));
            dto.setIsFCN(rs.getBoolean("isfcn"));
            dto.setDocumentalAgencyId(rs.getString("documentalAgencyId"));
            dto.setBlType(rs.getString("bltype"));
            dto.setSystembltype(rs.getString("systembltype"));

            Timestamp datecr = rs.getTimestamp("datecr");
            if (datecr != null)
                dto.setDatecr(datecr.toLocalDateTime());

            Timestamp dateup = rs.getTimestamp("dateup");
            if (dateup != null)
                dto.setDateup(dateup.toLocalDateTime());

            dto.setShipmentStatus(rs.getString("shipmentstatus"));

            Timestamp confirmeddate = rs.getTimestamp("confirmeddate");
            if (confirmeddate != null)
                dto.setConfirmeddate(confirmeddate.toLocalDateTime());

            dto.setEcomPrntstatus(safeEcomPrintStatus(rs.getString("ecomprntstatus")));
            dto.setOriginals(rs.getInt("originals"));
            dto.setCopies(rs.getInt("copies"));
            dto.setAgencyissueid(rs.getString("agencyissueid"));

            Timestamp receiptshipdate = rs.getTimestamp("receiptshipdate");
            dto.setReceiptshipdate(
                    receiptshipdate != null ? receiptshipdate.toLocalDateTime().atOffset(ZoneOffset.UTC)
                            : null);

            dto.setFcnverno(rs.getString("fcnverno"));
            dto.setFcnno(rs.getString("fcnno"));
            dto.setEcomsino(rs.getString("ecomsino"));
            dto.setPolicyNo(rs.getString("policy_no"));

            return dto;
        }
    }

  
    private static SiStatus safeSiStatus(String value) {
        if (value == null || value.trim().isEmpty())
            return null;
        try {
            return SiStatus.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Safely convert string to BookingStatus enum, returns null for
     * null/empty/invalid values
     */
    private static BookingStatus safeBookingStatus(String value) {
        if (value == null || value.trim().isEmpty())
            return null;
        try {
            return BookingStatus.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Safely convert string to BlStatus enum, returns null for null/empty/invalid
     * values
     */
    private static BlStatus safeBlStatus(String value) {
        if (value == null || value.trim().isEmpty())
            return null;
        try {
            return BlStatus.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Safely convert string to EcomPrintStatus enum, returns null for
     * null/empty/invalid values
     */
    private static EcomPrintStatus safeEcomPrintStatus(String value) {
        if (value == null || value.trim().isEmpty())
            return null;
        try {
            return EcomPrintStatus.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }
}