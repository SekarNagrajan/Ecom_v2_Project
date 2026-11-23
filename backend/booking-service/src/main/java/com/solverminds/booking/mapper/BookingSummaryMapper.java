package com.solverminds.booking.mapper;

import com.solverminds.booking.dto.BookingSummaryResponseDto;
import com.svm.ecom.common.enums.BookingSource;
import com.svm.ecom.common.enums.BookingStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * RowMapper for mapping ResultSet to BookingSummaryResponseDto.
 * Used for usp_getecombookingsummary_paged result set: base columns plus originName, deliveryName,
 * polName, podName (from ports) and blNumber, blStatus (from BL by trust_book_no, Master BL, non-cancelled).
 */
public class BookingSummaryMapper implements RowMapper<BookingSummaryResponseDto> {

    @Override
    public BookingSummaryResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        BookingSummaryResponseDto dto = newDto();

        try {
            // Base columns (from usp_getecombookingsummary via paged SP)
            
            try { dto.setBookingNumber(rs.getString("trust_book_no")); } 
            catch (SQLException e) { System.err.println("Missing/invalid column: trust_book_no - " + e.getMessage()); }
            
            try { dto.setOnlineBookNo(rs.getString("onlinebookno")); } 
            catch (SQLException e) { System.err.println("Missing/invalid column: onlinebookno - " + e.getMessage()); }
            
            try { dto.setOriginPortId(rs.getString("originPortId")); } 
            catch (SQLException e) { System.err.println("Missing/invalid column: originPortId - " + e.getMessage()); }
            
            try { dto.setFinalPortId(rs.getString("finalPortId")); } 
            catch (SQLException e) { System.err.println("Missing/invalid column: finalPortId - " + e.getMessage()); }
            
            try { 
                String datecr = rs.getString("datecr");
                if (datecr != null && !datecr.trim().isEmpty()) {
                    try {
                        dto.setCreatedDate(LocalDateTime.parse(datecr.replace(" ", "T")));
                    } catch (DateTimeParseException pe) {
                        Timestamp datecrTs = rs.getTimestamp("datecr");
                        if (datecrTs != null) dto.setCreatedDate(datecrTs.toLocalDateTime());
                    }
                }
            } catch (SQLException e) { System.err.println("Missing/invalid column: datecr - " + e.getMessage()); }
            
            try { 
                String dateup = rs.getString("dateup");
                if (dateup != null && !dateup.trim().isEmpty()) {
                    try {
                        dto.setChangeDate(LocalDateTime.parse(dateup.replace(" ", "T")));
                    } catch (DateTimeParseException pe) {
                        Timestamp dateupTs = rs.getTimestamp("dateup");
                        if (dateupTs != null) dto.setChangeDate(dateupTs.toLocalDateTime());
                    }
                }
            } catch (SQLException e) { System.err.println("Missing/invalid column: dateup - " + e.getMessage()); }
            
            try { dto.setStatus(safeBookingStatus(rs.getString("status"))); } 
            catch (SQLException e) { System.err.println("Missing/invalid column: status - " + e.getMessage()); }
            
            try { dto.setEcomLrpStatus(rs.getString("ecomlrpstatus")); } 
            catch (SQLException e) { System.err.println("Missing/invalid column: ecomlrpstatus - " + e.getMessage()); }
            
            try { dto.setSubmittedBy(rs.getString("submittedby")); } 
            catch (SQLException e) { System.err.println("Missing/invalid column: submittedby - " + e.getMessage()); }
            
            try { dto.setAgencyRefNo(rs.getString("agencyrefno")); } 
            catch (SQLException e) { System.err.println("Missing/invalid column: agencyrefno - " + e.getMessage()); }
            
            try { dto.setAgencyId(rs.getString("agencyid")); } 
            catch (SQLException e) { System.err.println("Missing/invalid column: agencyid - " + e.getMessage()); }
            
            // appVersion and dgStatus fields not needed in BookingSummaryResponseDto
            
            try { 
                String confirmeddate = rs.getString("confirmeddate");
                if (confirmeddate != null && !confirmeddate.trim().isEmpty()) {
                    try {
                        dto.setConfirmedDate(LocalDateTime.parse(confirmeddate.replace(" ", "T")));
                    } catch (DateTimeParseException pe) {
                        Timestamp confirmeddateTs = rs.getTimestamp("confirmeddate");
                        if (confirmeddateTs != null) dto.setConfirmedDate(confirmeddateTs.toLocalDateTime());
                    }
                }
            } catch (SQLException e) { System.err.println("Missing/invalid column: confirmeddate - " + e.getMessage()); }
            
            try { dto.setStatusDesc(rs.getString("statusdesc")); } 
            catch (SQLException e) { System.err.println("Missing/invalid column: statusdesc - " + e.getMessage()); }
            
            // showRefNo field not needed in BookingSummaryResponseDto
            
            try { dto.setBookingCreatedUser(rs.getString("lrpusercr")); } 
            catch (SQLException e) { System.err.println("Missing/invalid column: lrpusercr - " + e.getMessage()); }
            
            try { 
                String poletd = rs.getString("poletd");
                if (poletd != null && !poletd.trim().isEmpty()) {
                    try {
                        dto.setDepartureDate(LocalDateTime.parse(poletd.replace(" ", "T")).atOffset(java.time.ZoneOffset.UTC));
                    } catch (DateTimeParseException pe) {
                        Timestamp poletdTs = rs.getTimestamp("poletd");
                        if (poletdTs != null) dto.setDepartureDate(poletdTs.toLocalDateTime().atOffset(java.time.ZoneOffset.UTC));
                    }
                }
            } catch (SQLException e) { System.err.println("Missing/invalid column: poletd - " + e.getMessage()); }
            
            // haulageFlag field not needed in BookingSummaryResponseDto
            
            // Handle nullable integer fields
            try { 
                Integer tuesCount = rs.getObject("tuescount", Integer.class);
                dto.setTuesCount(tuesCount);
            } catch (SQLException e) { 
                System.err.println("Missing/invalid column: tuescount - " + e.getMessage()); 
            }

            // Port names from paged SP (originName, deliveryName, polName, podName from ports table)
            setIfPresent(dto::setOriginName, rs, "originName");
            setIfPresent(dto::setDeliveryName, rs, "deliveryName");
            setIfPresent(dto::setPolName, rs, "polName");
            setIfPresent(dto::setPodName, rs, "podName");
            // BL number and status from paged SP (BL table by trust_book_no, Master BL, status <> 'V')
            setIfPresent(dto::setBlNumber, rs, "blNumber", "bl_no");
            setIfPresent(dto::setBlStatus, rs, "blStatus", "bl_status");
            // POL/POD IDs
            setIfPresent(dto::setLoadPortId, rs, "loadPortId");
            setIfPresent(dto::setDischargePortId, rs, "dischargePortId");

            // Booking source: SP returns the raw "booksource" column from the booking (LRP) table
            // when the row originated there (real stored value, e.g. E/T, is preserved as-is).
            // Rows sourced only from ecombooking (no LRP link) have no booksource column, so the SP
            // returns NULL for them; those are always ECOM-originated, so default to C here,
            // mirroring the same rule used by GET /api/v1/booking/view (BookingViewService#mapMasterFields).
            String rawBookSource = null;
            try { rawBookSource = rs.getString("booksource"); } catch (SQLException ignored) { /* column may be absent in older SP */ }
            BookingSource bookingSource = safeBookingSource(rawBookSource);
            dto.setBookingSource(bookingSource != null ? bookingSource : BookingSource.C);
            // AP/BP code and name are mapped only for GET /api/v1/bookings
            // (SystemBookingSummaryMapper / SystemBookingSummaryResponseDto).
            // eta from SP (last leg when available)
            try {
                Timestamp etaTs = rs.getTimestamp("eta");
                if (etaTs != null) dto.setEta(etaTs.toLocalDateTime());
            } catch (SQLException ignored) { /* column may be absent in older SP */ }

        } catch (Exception e) {
            // General catch for any other non-SQL errors
            System.err.println("Unexpected error mapping booking summary row: " + e.getMessage());
            e.printStackTrace();
        }
        
        return dto;
    }

    /** Factory hook so a subclass can map into a DTO subtype; default behaviour is unchanged. */
    protected BookingSummaryResponseDto newDto() {
        return new BookingSummaryResponseDto();
    }

    /** Safely convert string to BookingStatus enum, returns null for null/empty/invalid values */
    private static BookingStatus safeBookingStatus(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return BookingStatus.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Safely convert string to BookingSource enum, returns null for null/empty/invalid values
     * (caller applies the C default), same tolerant pattern as
     * DocumentationBookingViewService#safeBookingSource.
     */
    private static BookingSource safeBookingSource(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return BookingSource.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /** Try column names in order (handles SQL Server alias/casing); set value if non-null. */
    private static void setIfPresent(java.util.function.Consumer<String> setter, ResultSet rs, String... columnNames) {
        if (setter == null || columnNames == null || columnNames.length == 0) return;
        for (String col : columnNames) {
            try {
                String v = rs.getString(col);
                if (v != null) {
                    setter.accept(v);
                    return;
                }
            } catch (SQLException ignored) { /* try next */ }
        }
    }
}
