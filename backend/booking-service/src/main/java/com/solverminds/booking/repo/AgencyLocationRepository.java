package com.solverminds.booking.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Repository("bookingAgencyLocationRepository")
public class AgencyLocationRepository {

    private static final Logger logger = LoggerFactory.getLogger(AgencyLocationRepository.class);

    private static final String SQL_GET_AGENCY_CODE = 
        "SELECT agencycode FROM agencylocations (nolock) WHERE status='A' AND portcode=?";

    private static final String SQL_GET_CORP_AGENCY = 
        "SELECT corpagency FROM agencymatrix (nolock) WHERE status='A' AND locagency=?";

    private final JdbcTemplate reportJdbcTemplate;

    public AgencyLocationRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    /**
     * Fetches the active agency code for a given port code from reportDB
     * @param portCode The port code to lookup
     * @return The agency code if found, null otherwise
     */
    public String getAgencyCodeByPortCode(String portCode) {
        if (portCode == null || portCode.trim().isEmpty()) {
            logger.warn("Port code is null or empty, cannot fetch agency code");
            return null;
        }

        try {
            logger.debug("Fetching agency code for port: {}", portCode);
            
            List<String> results = reportJdbcTemplate.query(
                SQL_GET_AGENCY_CODE,
                ps -> ps.setString(1, portCode.trim()),
                (rs, rowNum) -> rs.getString("agencycode")
            );

            if (results.isEmpty()) {
                logger.warn("No active agency found for port code: {}", portCode);
                return null;
            }

            String agencyCode = results.get(0);
            logger.debug("Found agency code: {} for port: {}", agencyCode, portCode);
            
            // If multiple results, log warning and return first
            if (results.size() > 1) {
                logger.warn("Multiple agencies found for port {}, using first: {}", portCode, agencyCode);
            }
            
            return agencyCode != null ? agencyCode.trim() : null;
            
        } catch (Exception e) {
            logger.error("Error fetching agency code for port: " + portCode, e);
            return null;
        }
    }

    /**
     * Fetches the corporate agency code for a given local agency code from agencymatrix
     * @param locAgency The local agency code to lookup
     * @return The corporate agency code if found, null otherwise
     */
    public String getCorpAgencyByLocAgency(String locAgency) {
        if (locAgency == null || locAgency.trim().isEmpty()) {
            logger.warn("Local agency code is null or empty, cannot fetch corp agency code");
            return null;
        }

        try {
            logger.debug("Fetching corp agency code for local agency: {}", locAgency);
            
            List<String> results = reportJdbcTemplate.query(
                SQL_GET_CORP_AGENCY,
                ps -> ps.setString(1, locAgency.trim()),
                (rs, rowNum) -> rs.getString("corpagency")
            );

            if (results.isEmpty()) {
                logger.warn("No active corp agency found for local agency code: {}", locAgency);
                return null;
            }

            String corpAgencyCode = results.get(0);
            logger.debug("Found corp agency code: {} for local agency: {}", corpAgencyCode, locAgency);
            
            // If multiple results, log warning and return first
            if (results.size() > 1) {
                logger.warn("Multiple corp agencies found for local agency {}, using first: {}", locAgency, corpAgencyCode);
            }
            
            return corpAgencyCode != null ? corpAgencyCode.trim() : null;
            
        } catch (Exception e) {
            logger.error("Error fetching corp agency code for local agency: " + locAgency, e);
            return null;
        }
    }
}

