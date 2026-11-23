package com.solverminds.sibl.service;

import com.svm.ecom.common.util.PortLocalTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Port code to timezone cache for SIBL summary responses (SI summary, BL summary).
 * Loads from report DB timezone table; normalizes numeric values (e.g. "8" -> "+08:00").
 */
@Service
public class SiblTimezoneCacheService {

    private static final Logger logger = LoggerFactory.getLogger(SiblTimezoneCacheService.class);

    private static final String SQL = "SELECT portcode, timeOffSet FROM timezone (nolock) WHERE status='A' AND portcode IS NOT NULL";
    private static final String SQL_ALT = "SELECT portcode, TimeOffset FROM timezone (nolock) WHERE status='A' AND portcode IS NOT NULL";

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    private static final Map<String, String> FALLBACK;
    static {
        Map<String, String> m = new HashMap<>();
        m.put("CNNGB", "+08:00");
        m.put("CNSHA", "+08:00");
        m.put("HKHKG", "+08:00");
        m.put("SGSIN", "+08:00");
        m.put("NLRTM", "+01:00");
        m.put("ESBCN", "+01:00");
        m.put("INMUN", "+05:30");
        FALLBACK = Collections.unmodifiableMap(m);
    }

    private final JdbcTemplate reportJdbcTemplate;

    public SiblTimezoneCacheService(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    @PostConstruct
    public void load() {
        refresh();
    }

    @Scheduled(fixedRate = 1800000)
    public void refresh() {
        if (reportJdbcTemplate == null) return;
        try {
            cache.clear();
            reportJdbcTemplate.query(SQL, (ResultSet rs) -> {
                String port = rs.getString("portcode");
                String tz = rs.getString("timeOffSet");
                if (port != null && !port.trim().isEmpty() && tz != null && !tz.trim().isEmpty())
                    cache.put(port.trim().toUpperCase(), tz.trim());
            });
            logger.info("SIBL timezone cache refreshed: {} entries", cache.size());
        } catch (Exception e) {
            try {
                cache.clear();
                reportJdbcTemplate.query(SQL_ALT, (ResultSet rs) -> {
                    String port = rs.getString("portcode");
                    String tz = rs.getString("TimeOffset");
                    if (port != null && !port.trim().isEmpty() && tz != null && !tz.trim().isEmpty())
                        cache.put(port.trim().toUpperCase(), tz.trim());
                });
                logger.info("SIBL timezone cache refreshed (alt column): {} entries", cache.size());
            } catch (Exception e2) {
                logger.warn("SIBL timezone cache load failed: {}", e2.getMessage());
            }
        }
    }

    public String getTimezoneForPort(String portCode) {
        if (portCode == null) return null;
        String key = portCode.trim().toUpperCase();
        String tz = cache.get(key);
        if (tz == null) tz = FALLBACK.get(key);
        return PortLocalTimeUtil.normalizeTimezoneValue(tz);
    }
}
