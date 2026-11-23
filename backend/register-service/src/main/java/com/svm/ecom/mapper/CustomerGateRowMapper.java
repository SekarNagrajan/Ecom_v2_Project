package com.svm.ecom.mapper;

import com.svm.ecom.dto.CustomerGateDto;
import com.svm.ecom.enums.GateStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * RowMapper for the {@code GET /api/v1/ecomCustomerGate} query result set (26 fixed IAD
 * fields). Uses the same defensive per-column mapping style as
 * {@code com.solverminds.booking.mapper.BookingSummaryMapper} in booking-service: a single
 * bad/missing column never fails the whole row.
 */
public class CustomerGateRowMapper implements RowMapper<CustomerGateDto> {

    private static final Logger logger = LoggerFactory.getLogger(CustomerGateRowMapper.class);

    @Override
    public CustomerGateDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        CustomerGateDto dto = new CustomerGateDto();

        setString(rs, "custCode", dto::setCustCode);
        setString(rs, "companyName", dto::setCompanyName);
        setString(rs, "address", dto::setAddress);
        setString(rs, "address1", dto::setAddress1);
        setString(rs, "city", dto::setCity);
        setString(rs, "postalCode", dto::setPostalCode);
        setString(rs, "countryCode", dto::setCountryCode);
        setString(rs, "companyDomain", dto::setCompanyDomain);
        setString(rs, "companyPhone", dto::setCompanyPhone);
        dto.setFirstName(composeFirstName(getStringQuiet(rs, "title"), getStringQuiet(rs, "firstName")));
        setString(rs, "lastName", dto::setLastName);
        setString(rs, "userPhone", dto::setUserPhone);
        setString(rs, "userMobile", dto::setUserMobile);
        setString(rs, "userFax", dto::setUserFax);
        setString(rs, "userEmail", dto::setUserEmail);

        dto.setGateStatus(safeGateStatus(getStringQuiet(rs, "gateStatus")));

        setString(rs, "recordStatus", dto::setRecordStatus);
        setString(rs, "regNo", dto::setRegNo);
        setString(rs, "controlLocation", dto::setControlLocation);
        setDateTime(rs, "registeredDate", dto::setRegisteredDate);
        setString(rs, "actionBy", dto::setActionBy);
        setDateTime(rs, "actionDate", dto::setActionDate);
        setString(rs, "remarks", dto::setRemarks);

        setString(rs, "regType", dto::setRegType);

        setString(rs, "recentBL", dto::setRecentBL);
        setDateTime(rs, "lastLoginDate", dto::setLastLoginDate);

        return dto;
    }

   
    static String composeFirstName(String title, String first) {
        if (first == null) {
            return null;
        }
        if (title == null || title.trim().isEmpty()) {
            return first;
        }
        return title.trim() + first;
    }

    /**
     * gateStatus arrives already computed by {@link GateStatus#caseExpression()} in the SQL,
     * so it is guaranteed to be one of N/E/I/P/M/R/Y or NULL. The try/catch here is a pure
     * defensive fallback (e.g. if the SQL is ever changed out of sync with the enum) \u2014 it
     * must never turn a data-shape surprise into a 500 for the whole row.
     */
    private static GateStatus safeGateStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return GateStatus.fromValue(value.trim());
        } catch (IllegalArgumentException e) {
            logger.warn("Unrecognized gateStatus value from query: '{}' - mapping to null. "
                + "This should be impossible if GateStatus.caseExpression() and this enum are in sync.", value);
            return null;
        }
    }

    private static void setString(ResultSet rs, String column, Consumer<String> setter) {
        try {
            String v = rs.getString(column);
            setter.accept(v != null ? v.trim() : null);
        } catch (SQLException e) {
            logger.warn("Missing/invalid column: {} - {}", column, e.getMessage());
        }
    }

    private static void setDateTime(ResultSet rs, String column, Consumer<LocalDateTime> setter) {
        try {
            Timestamp ts = rs.getTimestamp(column);
            setter.accept(ts != null ? ts.toLocalDateTime() : null);
        } catch (SQLException e) {
            logger.warn("Missing/invalid column: {} - {}", column, e.getMessage());
        }
    }

    private static String getStringQuiet(ResultSet rs, String column) {
        try {
            String v = rs.getString(column);
            return v != null ? v.trim() : null;
        } catch (SQLException e) {
            return null;
        }
    }
}
