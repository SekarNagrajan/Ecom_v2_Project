package com.solverminds.sibl.repo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fetches port code → port name from report DB (ports table).
 * Used by SI view when loading by bookingNo to set plaHeaderName, polHeaderName, podHeaderName, pldHeaderName.
 */
@Repository("siblPortNameRepository")
public class PortNameRepository {

    private static final String SQL_PORT_NAMES =
        "SELECT portcode, portname FROM ports (nolock) WHERE status = 'A'";

    private final JdbcTemplate reportJdbcTemplate;

    public PortNameRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    /**
     * Returns a map of port code → port name for all active ports (report DB).
     * Empty map if report DB is not configured or query fails.
     */
    public Map<String, String> getPortNameMap() {
        try {
            List<Map<String, Object>> rows = reportJdbcTemplate.queryForList(SQL_PORT_NAMES);
            Map<String, String> map = new LinkedHashMap<>();
            for (Map<String, Object> row : rows) {
                Object code = row.get("portcode");
                Object name = row.get("portname");
                if (code != null && code.toString() != null && !code.toString().trim().isEmpty()) {
                    map.put(code.toString().trim(), name != null ? name.toString().trim() : "");
                }
            }
            return map;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
