package com.solverminds.booking.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Batch lookup of schedule terminals from {@code allschedules} by {@code groupcallid}.
 * Used by Documentation Booking View to resolve invalid ({@code N}) leg terminals.
 */
@Repository
public class ScheduleTerminalRepository {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleTerminalRepository.class);

    private final JdbcTemplate reportJdbcTemplate;

    public ScheduleTerminalRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    /**
     * @return map of trimmed {@code groupcallid} → {@link ScheduleTerminal}; first non-blank terminal per call id wins
     */
    public Map<String, ScheduleTerminal> findByGroupCallIds(Collection<String> groupCallIds) {
        Set<String> ids = normalizeIds(groupCallIds);
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT groupcallid, port, terminal, service FROM allschedules (NOLOCK) "
                + "WHERE groupcallid IN (" + placeholders + ")";

        try {
            Object[] args = ids.toArray();
            Map<String, ScheduleTerminal> result = new LinkedHashMap<>();
            reportJdbcTemplate.query(sql, args, rs -> {
                String groupCallId = rs.getString("groupcallid");
                if (groupCallId == null || groupCallId.trim().isEmpty()) {
                    return;
                }
                String key = groupCallId.trim();
                if (result.containsKey(key)) {
                    return;
                }
                String terminal = rs.getString("terminal");
                if (terminal == null || terminal.trim().isEmpty()) {
                    return;
                }
                result.put(key, new ScheduleTerminal(
                        key,
                        trimToNull(rs.getString("port")),
                        terminal.trim(),
                        trimToNull(rs.getString("service"))));
            });
            logger.debug("Resolved {} schedule terminal(s) for {} group call id(s)", result.size(), ids.size());
            return result;
        } catch (Exception e) {
            logger.warn("Failed to fetch schedule terminals for group call ids: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private static Set<String> normalizeIds(Collection<String> groupCallIds) {
        Set<String> ids = new LinkedHashSet<>();
        if (groupCallIds == null) {
            return ids;
        }
        for (String id : groupCallIds) {
            if (id != null && !id.trim().isEmpty()) {
                ids.add(id.trim());
            }
        }
        return ids;
    }

    private static String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    public static final class ScheduleTerminal {
        private final String groupCallId;
        private final String port;
        private final String terminal;
        private final String service;

        public ScheduleTerminal(String groupCallId, String port, String terminal, String service) {
            this.groupCallId = groupCallId;
            this.port = port;
            this.terminal = terminal;
            this.service = service;
        }

        public String getGroupCallId() {
            return groupCallId;
        }

        public String getPort() {
            return port;
        }

        public String getTerminal() {
            return terminal;
        }

        public String getService() {
            return service;
        }
    }
}
