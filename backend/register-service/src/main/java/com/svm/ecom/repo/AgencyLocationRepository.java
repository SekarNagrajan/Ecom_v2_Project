package com.svm.ecom.repo;

import com.svm.ecom.dto.AgencyLocationDTO;
import com.svm.ecom.exception.ReportDbAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Repository
public class AgencyLocationRepository {

    private static final Logger logger = LoggerFactory.getLogger(AgencyLocationRepository.class);

    private static final String SQL_WITH_PORTS =
        "select distinct ports.portcode, ports.portname, al.agencycode " +
        "from ports (nolock) " +
        "inner join agency (nolock) on agency.location = ports.PortCode " +
        "inner join agencylocations (nolock) al on al.portcode = agency.location " +
        "where agency.status='A' and agency.type='o' and ports.status='A' and agency.Status_Agy='A'";

    private static final String SQL_FALLBACK =
        "select distinct ports.portcode, ports.portname, agency.agencycode " +
        "from ports (nolock) " +
        "inner join agency (nolock) on agency.location = ports.PortCode " +
        "where agency.status='A' and agency.type='o' and ports.status='A' and agency.Status_Agy='A'";

    private final JdbcTemplate jdbc;

    public AgencyLocationRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Fetches all agency locations using fallback logic:
     * 1. Try query with agencylocations table
     * 2. If no results, try fallback query without agencylocations table
     * 
     * @return List of AgencyLocationDTO objects
     * @throws ReportDbAccessException if both queries fail
     */
    public List<AgencyLocationDTO> fetchAll() {
        try {
            logger.debug("Executing primary query: getecomagencylocwithports");
            List<AgencyLocationDTO> primary = query(SQL_WITH_PORTS);
            
            if (primary.isEmpty()) {
                logger.debug("Primary query returned 0 rows, trying fallback query: getagencylocationdetails");
                List<AgencyLocationDTO> fallback = query(SQL_FALLBACK);
                logger.info("Fallback query returned {} rows", fallback.size());
                return deduplicate(fallback);
            }
            
            logger.info("Primary query returned {} rows", primary.size());
            return deduplicate(primary);
            
        } catch (Exception e) {
            logger.error("Failed to fetch agency locations from reportdb", e);
            throw new ReportDbAccessException("Could not retrieve agency locations: " + e.getMessage(), e);
        }
    }

    /**
     * Execute SQL query and map results to DTOs
     */
    private List<AgencyLocationDTO> query(String sql) {
        return jdbc.query(sql, (rs, rowNum) -> {
            AgencyLocationDTO dto = new AgencyLocationDTO();
            dto.setPortCode(trimOrNull(rs.getString("portcode")));
            dto.setPortName(trimOrNull(rs.getString("portname")));
            dto.setAgencyCode(trimOrNull(rs.getString("agencycode")));
            return dto;
        });
    }

    /**
     * Safely trim string values, returning null for null inputs
     */
    private String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }

    /**
     * Deduplicate results by (portCode, agencyCode) combination
     */
    private List<AgencyLocationDTO> deduplicate(List<AgencyLocationDTO> locations) {
        if (locations == null || locations.isEmpty()) {
            return new ArrayList<>();
        }

        // Use a Set with custom key to remove duplicates
        Map<String, AgencyLocationDTO> uniqueMap = new LinkedHashMap<>();
        
        for (AgencyLocationDTO location : locations) {
            String key = (location.getPortCode() != null ? location.getPortCode() : "") + "|" + 
                        (location.getAgencyCode() != null ? location.getAgencyCode() : "");
            
            // Keep first occurrence of each unique combination
            if (!uniqueMap.containsKey(key)) {
                uniqueMap.put(key, location);
            }
        }
        
        return new ArrayList<>(uniqueMap.values());
    }
}
