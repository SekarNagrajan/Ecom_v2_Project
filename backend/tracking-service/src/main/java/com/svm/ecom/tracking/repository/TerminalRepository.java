package com.svm.ecom.tracking.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository for terminal data lookup operations
 */
@Repository
public class TerminalRepository {

    private static final Logger logger = LoggerFactory.getLogger(TerminalRepository.class);
    
    private final JdbcTemplate reportJdbcTemplate;

    public TerminalRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    private void ensureReportDb() {
        if (reportJdbcTemplate == null) {
            throw new IllegalStateException("reportdb is not configured");
        }
    }

    /**
     * Get terminal name by terminal code
     * @param terminalCode The terminal code to lookup
     * @return Terminal name or null if not found
     */
    public String getTerminalNameByCode(String terminalCode) {
        if (terminalCode == null || terminalCode.trim().isEmpty()) {
            return null;
        }
        
        ensureReportDb();
        
        try {
            String sql = "select terminalname from terminals(nolock) where terminalcode=?";
            logger.debug("Executing terminal lookup query for terminal code: {}", terminalCode);
            
            String terminalName = reportJdbcTemplate.queryForObject(sql, String.class, terminalCode);
            logger.debug("Found terminal name: {} for code: {}", terminalName, terminalCode);
            
            return terminalName;
            
        } catch (Exception e) {
            logger.warn("Failed to fetch terminal name for code: {} - Error: {}", terminalCode, e.getMessage());
            return null;
        }
    }
}
