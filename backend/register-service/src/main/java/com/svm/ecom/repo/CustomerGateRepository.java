package com.svm.ecom.repo;

import com.svm.ecom.dto.CustomerGateDto;
import com.svm.ecom.enums.GateStatus;
import com.svm.ecom.mapper.CustomerGateRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;


@Repository
public class CustomerGateRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomerGateRepository.class);

    // language=SQL
    private static final String SQL =
        "WITH gate AS ( " +
        "  SELECT " +
        "    e.Cust_Code AS custCode, " +
        "    e.Comp_Name AS companyName, " +
        "    e.Address AS address, " +
        "    e.Address2 AS address1, " +
        "    e.City AS city, " +
        "    e.Pin_Code AS postalCode, " +
        "    e.Country AS countryCode, " +
        "    e.Comp_Domain AS companyDomain, " +
        "    e.Comp_Phone AS companyPhone, " +
        "    e.Title AS title, " +
        "    e.First_Name AS firstName, " +
        "    e.Last_Name AS lastName, " +
        "    e.User_Phone AS userPhone, " +
        "    e.User_Mobile AS userMobile, " +
        "    e.User_Fax AS userFax, " +
        "    e.User_Email AS userEmail, " +
        "    " + GateStatus.caseExpression() + " AS gateStatus, " +
        "    e.status AS recordStatus, " +
        "    e.reg_no AS regNo, " +
        "    e.control_location AS controlLocation, " +
        "    e.Registered_Date AS registeredDate, " +
        "    e.ActionBy AS actionBy, " +
        "    e.ActionDate AS actionDate, " +
        "    e.Remarks AS remarks, " +
        "    e.RegType AS regType, " +
        "    e.recentbl AS recentBL, " +
        "    ed.logindate AS lastLoginDate, " +
        "    COALESCE(e.ActionDate, e.Registered_Date) AS updatedTs " +
        "  FROM eCom_Reg_Details e WITH (NOLOCK) " +
        "  LEFT JOIN ecomuserdetails ed WITH (NOLOCK) ON e.User_Email = ed.EmailID " +
        ") " +

        "SELECT " +
        "  custCode, companyName, address, address1, city, postalCode, countryCode, " +
        "  companyDomain, companyPhone, title, firstName, lastName, userPhone, userMobile, " +
        "  userFax, userEmail, gateStatus, recordStatus, regNo, controlLocation, " +
        "  registeredDate, actionBy, actionDate, remarks, regType, recentBL, lastLoginDate " +
        "FROM gate " +
        "WHERE gateStatus IS NOT NULL " +
        "  AND (? IS NULL OR gateStatus = ?) " +
        "  AND (? IS NULL OR updatedTs > ?) " +
        "GROUP BY " +
        "  custCode, companyName, address, address1, city, postalCode, countryCode, " +
        "  companyDomain, companyPhone, title, firstName, lastName, userPhone, userMobile, " +
        "  userFax, userEmail, gateStatus, recordStatus, regNo, controlLocation, " +
        "  registeredDate, actionBy, actionDate, remarks, regType, recentBL, lastLoginDate " +
        "ORDER BY MAX(COALESCE(actionDate, registeredDate)) ASC, custCode ASC";

    private final JdbcTemplate ecomJdbcTemplate;

    public CustomerGateRepository(@Qualifier("ecomJdbcTemplate") JdbcTemplate ecomJdbcTemplate) {
        this.ecomJdbcTemplate = ecomJdbcTemplate;
    }

    private void ensureEcomDb() {
        if (ecomJdbcTemplate == null) {
            throw new IllegalStateException("ecomdb is not configured");
        }
    }

    /**
     * @param gateStatus   single-letter filter (N,E,I,P,M,R,Y), already validated by the
     *                     caller via {@link GateStatus#fromValue}; or null for no filter
     * @param updatedSince exclusive lower bound on {@code updatedTs}, or null for no filter
     * @return all matching rows, ordered by updatedTs ASC, custCode ASC; empty list (not null)
     *         when nothing matches
     */
    public List<CustomerGateDto> findAll(String gateStatus, Timestamp updatedSince) {
        ensureEcomDb();
        logger.debug("Executing ecomCustomerGate query (gateStatus={}, updatedSince={})", gateStatus, updatedSince);
        return ecomJdbcTemplate.query(SQL, new GateParamsSetter(gateStatus, updatedSince), new CustomerGateRowMapper());
    }


    private static final class GateParamsSetter implements PreparedStatementSetter {
        private final String gateStatus;
        private final Timestamp updatedSince;

        GateParamsSetter(String gateStatus, Timestamp updatedSince) {
            this.gateStatus = gateStatus;
            this.updatedSince = updatedSince;
        }

        @Override
        public void setValues(PreparedStatement ps) throws SQLException {
            if (gateStatus != null && !gateStatus.trim().isEmpty()) {
                ps.setString(1, gateStatus.trim());
                ps.setString(2, gateStatus.trim());
            } else {
                ps.setNull(1, Types.VARCHAR);
                ps.setNull(2, Types.VARCHAR);
            }
            if (updatedSince != null) {
                ps.setTimestamp(3, updatedSince);
                ps.setTimestamp(4, updatedSince);
            } else {
                ps.setNull(3, Types.TIMESTAMP);
                ps.setNull(4, Types.TIMESTAMP);
            }
        }
    }
}
