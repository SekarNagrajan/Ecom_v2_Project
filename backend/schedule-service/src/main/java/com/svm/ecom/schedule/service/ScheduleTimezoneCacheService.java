package com.svm.ecom.schedule.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.svm.ecom.common.util.PortLocalTimeUtil;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache of port code to timezone (IANA ZoneId or offset string) for schedule response conversion.
 * Loads from report DB timezone table; used to convert arrdate/depdate from UTC to port local time.
 */
@Service
public class ScheduleTimezoneCacheService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleTimezoneCacheService.class);

    // Column for timezone: try timeOffSet (booking-service), then TimeOffset, then time_offset (snake_case)
    private static final String SQL_LOAD_TIMEZONES =
            "SELECT portcode, timeOffSet FROM timezone (nolock) WHERE status='A' AND portcode IS NOT NULL";
    private static final String SQL_LOAD_TIMEZONES_ALT =
            "SELECT portcode, TimeOffset FROM timezone (nolock) WHERE status='A' AND portcode IS NOT NULL";
    private static final String SQL_LOAD_TIMEZONES_ALT2 =
            "SELECT portcode, time_offset FROM timezone (nolock) WHERE status='A' AND portcode IS NOT NULL";

    private final Map<String, String> portToTimezone = new ConcurrentHashMap<>();

    /** Fallback when timezone table is empty or DB column names differ. Port code -> offset (e.g. +08:00). */
    private static final Map<String, String> FALLBACK_PORT_TIMEZONE;
    static {
        Map<String, String> m = new HashMap<>();
        m.put("CNNGB", "+08:00");   // Ningbo, China
        m.put("CNSHA", "+08:00");   // Shanghai, China
        m.put("CNYTN", "+08:00");   // Yantian, China
        m.put("CNXMN", "+08:00");   // Xiamen, China
        m.put("CNQIN", "+08:00");   // Qingdao, China
        m.put("CNTXG", "+08:00");   // Tianjin, China
        m.put("CNDLC", "+08:00");   // Dalian, China
        m.put("HKHKG", "+08:00");   // Hong Kong
        m.put("SGSIN", "+08:00");   // Singapore
        m.put("TWKHH", "+08:00");   // Kaohsiung, Taiwan
        m.put("JPYOK", "+09:00");   // Yokohama, Japan
        m.put("JPOSA", "+09:00");   // Osaka, Japan
        m.put("KRPUS", "+09:00");   // Busan, Korea
        m.put("NLRTM", "+01:00");   // Rotterdam (CET; DST may apply)
        m.put("ESBCN", "+01:00");   // Barcelona, Spain
        m.put("DEHAM", "+01:00");   // Hamburg, Germany
        m.put("INMUN", "+05:30");   // Mumbai, India
        m.put("INCCU", "+05:30");   // Kolkata, India
        FALLBACK_PORT_TIMEZONE = Collections.unmodifiableMap(m);
    }

    private final EntityManagerFactory reportEntityManagerFactory;

    public ScheduleTimezoneCacheService(
            @Qualifier("reportEntityManagerFactory") EntityManagerFactory reportEntityManagerFactory) {
        this.reportEntityManagerFactory = reportEntityManagerFactory;
    }

    @PostConstruct
    public void loadCache() {
        refreshCache();
    }

    @Scheduled(fixedRate = 1800000)
    public void refreshCache() {
        EntityManager em = null;
        String sqlToUse = SQL_LOAD_TIMEZONES;
        try {
            em = reportEntityManagerFactory.createEntityManager();
            @SuppressWarnings("unchecked")
            java.util.List<Object[]> rows = em.createNativeQuery(sqlToUse).getResultList();
            portToTimezone.clear();
            if (rows != null) {
                for (Object[] row : rows) {
                    String portCode = row[0] != null ? row[0].toString().trim().toUpperCase() : null;
                    String tzValue = row.length > 1 && row[1] != null ? row[1].toString().trim() : null;
                    if (portCode != null && !portCode.isEmpty() && tzValue != null && !tzValue.isEmpty()) {
                        portToTimezone.put(portCode, tzValue);
                    }
                }
            }
            logger.info("Schedule timezone cache refreshed: {} entries (e.g. for port CNNGB: {})",
                    portToTimezone.size(), portToTimezone.get("CNNGB"));
        } catch (Exception e) {
            logger.warn("Schedule timezone cache load failed (timeOffSet): {}, trying TimeOffset column", e.getMessage());
            if (em != null && em.isOpen()) {
                em.close();
                em = null;
            }
            portToTimezone.clear();
            try {
                em = reportEntityManagerFactory.createEntityManager();
                    @SuppressWarnings("unchecked")
                    java.util.List<Object[]> rows = em.createNativeQuery(SQL_LOAD_TIMEZONES_ALT).getResultList();
                    portToTimezone.clear();
                    if (rows != null) {
                        for (Object[] row : rows) {
                            String portCode = row[0] != null ? row[0].toString().trim().toUpperCase() : null;
                            String tzValue = row.length > 1 && row[1] != null ? row[1].toString().trim() : null;
                            if (portCode != null && !portCode.isEmpty() && tzValue != null && !tzValue.isEmpty()) {
                                portToTimezone.put(portCode, tzValue);
                            }
                        }
                    }
                    logger.info("Schedule timezone cache refreshed (alt column): {} entries", portToTimezone.size());
                } catch (Exception e2) {
                    logger.warn("Schedule timezone cache (TimeOffset) failed: {}, trying time_offset", e2.getMessage());
                    if (em != null && em.isOpen()) { em.close(); em = null; }
                    try {
                        em = reportEntityManagerFactory.createEntityManager();
                        @SuppressWarnings("unchecked")
                        java.util.List<Object[]> rows = em.createNativeQuery(SQL_LOAD_TIMEZONES_ALT2).getResultList();
                        portToTimezone.clear();
                        if (rows != null) {
                            for (Object[] row : rows) {
                                String portCode = row[0] != null ? row[0].toString().trim().toUpperCase() : null;
                                String tzValue = row.length > 1 && row[1] != null ? row[1].toString().trim() : null;
                                if (portCode != null && !portCode.isEmpty() && tzValue != null && !tzValue.isEmpty()) {
                                    portToTimezone.put(portCode, tzValue);
                                }
                            }
                        }
                        logger.info("Schedule timezone cache refreshed (time_offset column): {} entries", portToTimezone.size());
                    } catch (Exception e3) {
                        logger.error("Error refreshing schedule timezone cache (tried timeOffSet, TimeOffset, time_offset)", e3);
                    }
                }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Get timezone string for a port code (IANA ZoneId or offset e.g. +05:30 or +08:00).
     * Uses DB-backed cache first; if missing, uses a static fallback map so port schedule
     * responses still return arrdate/depdate with timezone offset (e.g. CNNGB -> +08:00).
     * @return timezone string, or null only if port has no fallback (then response uses UTC with Z)
     */
    public String getTimezoneForPort(String portCode) {
        if (portCode == null) return null;
        String key = portCode.trim().toUpperCase();
        String tz = portToTimezone.get(key);
        if (tz != null) {
            tz = PortLocalTimeUtil.normalizeTimezoneValue(tz);
            logger.debug("getTimezoneForPort('{}') -> from cache: {}", key, tz);
            return tz;
        }
        tz = FALLBACK_PORT_TIMEZONE.get(key);
        if (tz != null) {
            logger.debug("getTimezoneForPort('{}') -> from fallback: {}", key, tz);
            return tz;
        }
        logger.info("getTimezoneForPort('{}') -> null (no cache or fallback; arrdate/depdate will use UTC)", key);
        return null;
    }

    public Map<String, String> getAllTimezones() {
        return Collections.unmodifiableMap(portToTimezone);
    }
}
