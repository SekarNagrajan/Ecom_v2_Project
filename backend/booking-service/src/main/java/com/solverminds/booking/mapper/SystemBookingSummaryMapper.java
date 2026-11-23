package com.solverminds.booking.mapper;

import com.solverminds.booking.dto.BookingSummaryResponseDto;
import com.solverminds.booking.dto.SystemBookingSummaryResponseDto;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Same mapping as {@link BookingSummaryMapper}, plus the Agreement Party / Booking Party
 * customer code and name returned by {@code usp_getbookingsummary_system}.
 * Columns are read by name; a NULL value, or a missing column, leaves the field null.
 */
public class SystemBookingSummaryMapper extends BookingSummaryMapper {

    @Override
    protected BookingSummaryResponseDto newDto() {
        return new SystemBookingSummaryResponseDto();
    }

    @Override
    public SystemBookingSummaryResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        SystemBookingSummaryResponseDto dto = (SystemBookingSummaryResponseDto) super.mapRow(rs, rowNum);
        dto.setAgreementPartyCustomerCode(readString(rs, "agreementPartyCustomerCode"));
        dto.setAgreementPartyCustomerName(readString(rs, "agreementPartyCustomerName"));
        dto.setBookingPartyCustomerCode(readString(rs, "bookingPartyCustomerCode"));
        dto.setBookingPartyCustomerName(readString(rs, "bookingPartyCustomerName"));
        return dto;
    }

    /** Reads a column by name; returns null when the value is NULL or the column is absent. */
    private static String readString(ResultSet rs, String column) {
        try {
            return rs.getString(column);
        } catch (SQLException e) {
            return null;
        }
    }
}
