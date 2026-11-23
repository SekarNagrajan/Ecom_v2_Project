package com.svm.ecom.schedule.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Loads cut-off configuration from {@code ecom_cutoffconfig} on the ECOM database (Ecomm_JSF) via
 * {@code ecomJdbcTemplate} — the same {@link JdbcTemplate} bean and {@code ecom.datasource.*} settings as
 * booking-service (e.g. {@code TeusCalculationService}, {@code DocumentService}).
 * Map keys are {@code port + "&&" + terminal} for {@link com.svm.ecom.schedule.service.CutoffCalculator}.
 */
@Repository
public class CutoffConfigRepository {

    private static final Logger logger = LoggerFactory.getLogger(CutoffConfigRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public CutoffConfigRepository(@Qualifier("ecomJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** Port schedule API: one query for the requested port plus {@code All} rows. */
    public Map<String, Map<String, String>> getCutoffConfigDetails(String port) {
        if (!StringUtils.hasText(port)) {
            return Collections.emptyMap();
        }
        return getCutoffConfigDetails(Collections.singleton(port.trim()));
    }

    /** Vessel schedule API: one query for all distinct ports plus {@code All}. */
    public Map<String, Map<String, String>> getCutoffConfigDetails(Set<String> ports) {
        if (ports == null || ports.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<String> normalized = ports.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.toSet());
        if (normalized.isEmpty()) {
            return Collections.emptyMap();
        }

        String placeholders = normalized.stream().map(p -> "?").collect(Collectors.joining(", "));
        String sql = "SELECT port, terminal, docclosing, vgmclosing, "
                + "fullcntrclosing, excludeweekend "
                + "FROM ecom_cutoffconfig (nolock) "
                + "WHERE port IN (" + placeholders + ", 'All')";

        Object[] args = normalized.toArray();
        try {
            return jdbcTemplate.query(sql, args, this::extractConfigMap);
        } catch (Exception e) {
            logger.error("Failed to load ecom_cutoffconfig: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private Map<String, Map<String, String>> extractConfigMap(ResultSet rs) throws SQLException {
        Map<String, Map<String, String>> configMap = new HashMap<>();
        while (rs.next()) {
            Map<String, String> config = new HashMap<>();
            String portCode = nullSafe(rs.getString("port"));
            String terminal = nullSafe(rs.getString("terminal"));
            config.put("docclosing", nullSafe(rs.getString("docclosing")));
            config.put("vgmclosing", nullSafe(rs.getString("vgmclosing")));
            config.put("fullcntrclosing", nullSafe(rs.getString("fullcntrclosing")));
            config.put("excludeweekend", nullSafe(rs.getString("excludeweekend")));
            configMap.put(portCode + "&&" + terminal, config);
        }
        return configMap;
    }

    private static String nullSafe(String val) {
        return val != null ? val : "";
    }
}
