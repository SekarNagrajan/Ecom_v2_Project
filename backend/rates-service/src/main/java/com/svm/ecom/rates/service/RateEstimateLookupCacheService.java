package com.svm.ecom.rates.service;

import com.svm.ecom.rates.repository.OfrSrrChargeRepository;
import com.svm.ecom.rates.repository.OfrSrrChargeRepository.ChargeMasterMaps;
import com.svm.ecom.rates.repository.RouteDetailRepository;
import com.svm.ecom.rates.repository.RouteDetailRepository.RouteDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Small master-data cache for Rate Estimate lookups — same pattern as Booking
 * {@code MasterDataCacheService} / {@code CommonMasterDataCacheService}:
 * load on startup, refresh every 30 minutes.
 * <p>
 * Cached (avoid repeat DB hits on every estimate click):
 * <ul>
 *   <li>{@code transportservice} name/code → {@code trptsvccode}</li>
 *   <li>{@code charges} — {@code chargeeqptype} + {@code chargename} by charge code</li>
 *   <li>{@code Routedetail} by route id (lazy fill; cleared on refresh)</li>
 * </ul>
 * Does not cache OFR rates or CommonSurcharge tariff rows — charge business logic unchanged.
 */
@Service
public class RateEstimateLookupCacheService {

    private static final Logger logger = LoggerFactory.getLogger(RateEstimateLookupCacheService.class);

    private final OfrSrrChargeRepository ofrSrrChargeRepository;
    private final RouteDetailRepository routeDetailRepository;

    /** transport name/code keys → trptsvccode */
    private final Map<String, String> transportCodeByKey = new ConcurrentHashMap<>();
    /** chargecode (uppercase) → chargeeqptype */
    private final Map<String, String> chargeEqpTypeByCode = new ConcurrentHashMap<>();
    /** chargecode (uppercase) → chargename */
    private final Map<String, String> chargeNameByCode = new ConcurrentHashMap<>();
    /** routeId → Routedetail projection (lazy); cleared on scheduled refresh */
    private final Map<Integer, RouteDetail> routeDetailById = new ConcurrentHashMap<>();
    /** Negative cache for missing route ids (cleared on refresh) */
    private final Map<Integer, Boolean> routeMissById = new ConcurrentHashMap<>();

    private volatile LocalDateTime lastRefreshTime;
    private volatile boolean initialized = false;

    public RateEstimateLookupCacheService(OfrSrrChargeRepository ofrSrrChargeRepository,
                                          RouteDetailRepository routeDetailRepository) {
        this.ofrSrrChargeRepository = ofrSrrChargeRepository;
        this.routeDetailRepository = routeDetailRepository;
    }

    @PostConstruct
    public void initializeCache() {
        logger.info("Initializing Rate Estimate lookup cache (30-min refresh)...");
        refreshAllCaches();
        initialized = true;
        logger.info("Rate Estimate lookup cache initialized");
    }

    @Scheduled(fixedRate = 1800000) // 30 minutes — same as Booking / CommonMasterDataCacheService
    public void scheduledRefresh() {
        if (initialized) {
            logger.info("Scheduled refresh of Rate Estimate lookup cache...");
            refreshAllCaches();
        }
    }

    public void refreshAllCaches() {
        try {
            refreshTransportServiceCache();
            refreshChargeMasterCache();
            routeDetailById.clear();
            routeMissById.clear();
            lastRefreshTime = LocalDateTime.now();
            logger.info("Rate Estimate lookup caches refreshed at {} (transport={}, charges={}, routes cleared)",
                    lastRefreshTime, transportCodeByKey.size(), chargeNameByCode.size());
        } catch (Exception e) {
            logger.error("Error refreshing Rate Estimate lookup caches", e);
        }
    }

    private void refreshTransportServiceCache() {
        try {
            Map<String, String> loaded = ofrSrrChargeRepository.loadAllTransportServiceKeys();
            transportCodeByKey.clear();
            if (loaded != null) {
                transportCodeByKey.putAll(loaded);
            }
            logger.info("Transport service cache refreshed: {} keys", transportCodeByKey.size());
        } catch (Exception e) {
            logger.error("Error refreshing transportservice cache", e);
        }
    }

    /**
     * One Commercial query: {@code select chargecode, chargeeqptype, chargename from charges where status='A'}.
     */
    private void refreshChargeMasterCache() {
        try {
            ChargeMasterMaps loaded = ofrSrrChargeRepository.loadAllChargeMaster();
            chargeEqpTypeByCode.clear();
            chargeNameByCode.clear();
            if (loaded != null) {
                if (loaded.getEqpTypeByCode() != null) {
                    chargeEqpTypeByCode.putAll(loaded.getEqpTypeByCode());
                }
                if (loaded.getNameByCode() != null) {
                    chargeNameByCode.putAll(loaded.getNameByCode());
                }
            }
            logger.info("Charges master cache refreshed: {} eqptypes, {} names",
                    chargeEqpTypeByCode.size(), chargeNameByCode.size());
        } catch (Exception e) {
            logger.error("Error refreshing charges master cache", e);
        }
    }

    /**
     * Resolve transport name/code → {@code trptsvccode}. Cache hit preferred;
     * miss falls back to DB once and stores the result (same rules as repository).
     */
    public String getTransportServiceCode(String transportServiceNameOrCode) {
        if (transportServiceNameOrCode == null || transportServiceNameOrCode.trim().isEmpty()) {
            return "";
        }
        String input = transportServiceNameOrCode.trim();
        String cached = lookupTransport(input);
        if (cached != null) {
            return cached;
        }
        String fromDb = ofrSrrChargeRepository.findTransportServiceCode(input);
        if (fromDb != null && !fromDb.isEmpty()) {
            putTransportKey(input, fromDb);
            String spaced = input.replace("/", " / ").replaceAll("\\s+", " ").trim();
            putTransportKey(spaced, fromDb);
            putTransportKey(fromDb, fromDb);
            return fromDb;
        }
        return "";
    }

    private String lookupTransport(String input) {
        String hit = transportCodeByKey.get(input);
        if (hit != null) {
            return hit;
        }
        hit = transportCodeByKey.get(input.toUpperCase(Locale.ROOT));
        if (hit != null) {
            return hit;
        }
        String spaced = input.replace("/", " / ").replaceAll("\\s+", " ").trim();
        if (!spaced.equals(input)) {
            hit = transportCodeByKey.get(spaced);
            if (hit != null) {
                return hit;
            }
            hit = transportCodeByKey.get(spaced.toUpperCase(Locale.ROOT));
            if (hit != null) {
                return hit;
            }
        }
        return null;
    }

    private void putTransportKey(String key, String code) {
        if (key == null || key.isEmpty() || code == null || code.isEmpty()) {
            return;
        }
        transportCodeByKey.put(key, code);
        transportCodeByKey.put(key.toUpperCase(Locale.ROOT), code);
    }

    /**
     * {@code charges.chargeeqptype} for charge code. Cache hit preferred; DB fallback on miss.
     */
    public String getChargeEqpType(String chargeCode) {
        if (chargeCode == null || chargeCode.trim().isEmpty()) {
            return "";
        }
        String key = chargeCode.trim().toUpperCase(Locale.ROOT);
        if (chargeEqpTypeByCode.containsKey(key)) {
            String v = chargeEqpTypeByCode.get(key);
            return v == null ? "" : v;
        }
        String fromDb = ofrSrrChargeRepository.findChargeEqpType(chargeCode.trim());
        chargeEqpTypeByCode.put(key, fromDb == null ? "" : fromDb);
        return fromDb == null ? "" : fromDb;
    }

    /**
     * {@code charges.chargename} for charge code (Commercial master). Empty if unknown.
     */
    public String getChargeName(String chargeCode) {
        if (chargeCode == null || chargeCode.trim().isEmpty()) {
            return "";
        }
        String key = chargeCode.trim().toUpperCase(Locale.ROOT);
        String cached = chargeNameByCode.get(key);
        return cached == null ? "" : cached;
    }

    /**
     * Prefer existing non-blank name; otherwise resolve from {@code charges} cache.
     * Never returns null — blank miss → {@code "-"}.
     */
    public String resolveChargeName(String chargeCode, String existingName) {
        if (existingName != null) {
            String t = existingName.trim();
            if (!t.isEmpty() && !"-".equals(t)) {
                return t;
            }
        }
        String fromCache = getChargeName(chargeCode);
        if (fromCache != null && !fromCache.isEmpty()) {
            return fromCache;
        }
        return "-";
    }

    /** Booking LP lump-sum check via cached chargeeqptype. */
    public boolean isLumpSumCharge(String chargeCode) {
        return "LP".equalsIgnoreCase(getChargeEqpType(chargeCode));
    }

    /**
     * Routedetail by id — lazy cache; same Optional semantics as repository.
     */
    public Optional<RouteDetail> findRouteDetail(Integer routeId) {
        if (routeId == null) {
            return Optional.empty();
        }
        RouteDetail cached = routeDetailById.get(routeId);
        if (cached != null) {
            return Optional.of(cached);
        }
        if (routeMissById.containsKey(routeId)) {
            return Optional.empty();
        }
        Optional<RouteDetail> fromDb = routeDetailRepository.findByRouteId(routeId);
        if (fromDb.isPresent()) {
            routeDetailById.put(routeId, fromDb.get());
        } else {
            routeMissById.put(routeId, Boolean.TRUE);
        }
        return fromDb;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public LocalDateTime getLastRefreshTime() {
        return lastRefreshTime;
    }

    public Map<String, String> getTransportCodeSnapshot() {
        return Collections.unmodifiableMap(transportCodeByKey);
    }
}
