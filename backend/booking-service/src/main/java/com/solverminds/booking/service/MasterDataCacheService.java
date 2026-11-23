package com.solverminds.booking.service;

import com.solverminds.booking.repo.EquipmentRepository;
import com.solverminds.booking.repo.MasterDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache service for master data used in validation.
 * Loads data from reportDB on startup and refreshes periodically.
 * 
 * Cached data includes:
 * - Equipment gross weights (for weight capacity validation)
 * - Valid port codes
 * - Valid customer codes
 * - Valid country codes
 * - Valid commodity codes
 * - Valid package codes
 * - Valid terminal codes
 * - Valid service codes
 * - Valid vessel codes
 * - Port timezone (portCode -> ZoneId/offset for port local time conversion)
 */
@Service
public class MasterDataCacheService {

    private static final Logger logger = LoggerFactory.getLogger(MasterDataCacheService.class);

    private final EquipmentRepository equipmentRepository;
    private final MasterDataRepository masterDataRepository;

    // Cache stores
    private final Map<String, Float> equipmentGrossWeights = new ConcurrentHashMap<>();
    private final Map<String, Float> equipmentTareWeights = new ConcurrentHashMap<>();
    private final Set<String> validPortCodes = ConcurrentHashMap.newKeySet();
    // Port code -> Port name mapping
    private final Map<String, String> portNames = new ConcurrentHashMap<>();
    private final Set<String> validCustomerCodes = ConcurrentHashMap.newKeySet();
    private final Set<String> validCountryCodes = ConcurrentHashMap.newKeySet();
    private final Set<String> validCommodityCodes = ConcurrentHashMap.newKeySet();
    /** Normalized comdtycode (HS) → distinct grpcode values for hsCode vs commodityGroup validation. */
    private final Map<String, Set<String>> commodityHsCodeToGroupCodes = new ConcurrentHashMap<>();
    private final Set<String> validPackageCodes = ConcurrentHashMap.newKeySet();
    private final Set<String> validTerminalCodes = ConcurrentHashMap.newKeySet();
    private final Set<String> validServiceCodes = ConcurrentHashMap.newKeySet();
    private final Set<String> validVesselCodes = ConcurrentHashMap.newKeySet();
    private final Set<String> validEquipmentSizes = ConcurrentHashMap.newKeySet();
    // Dangerous goods cache: UN number -> IMO class code (for combination validation if needed)
    private final Map<String, String> dangerousGoodsClassMap = new ConcurrentHashMap<>();
    // Separate sets for independent validation
    private final Set<String> validUnNumbers = ConcurrentHashMap.newKeySet();
    private final Set<String> validClassCodes = ConcurrentHashMap.newKeySet();
    // Active route IDs cache
    private final Set<Integer> validRouteIds = ConcurrentHashMap.newKeySet();
    // Port code -> timezone (IANA ZoneId or offset string, e.g. "Asia/Colombo" or "+05:30")
    private final Map<String, String> timezoneOffsets = new ConcurrentHashMap<>();

    private volatile LocalDateTime lastRefreshTime;
    private volatile boolean initialized = false;

    public MasterDataCacheService(EquipmentRepository equipmentRepository,
                                   MasterDataRepository masterDataRepository) {
        this.equipmentRepository = equipmentRepository;
        this.masterDataRepository = masterDataRepository;
    }

    /**
     * Initialize cache on application startup.
     */
    @PostConstruct
    public void initializeCache() {
        logger.info("Initializing master data cache...");
        refreshAllCaches();
        initialized = true;
        logger.info("Master data cache initialized successfully");
    }

    /**
     * Refresh all caches periodically (every 30 minutes).
     * Cron: second minute hour day-of-month month day-of-week
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes in milliseconds
    public void scheduledRefresh() {
        if (initialized) {
            logger.info("Scheduled refresh of master data cache...");
            refreshAllCaches();
        }
    }

    /**
     * Manual refresh endpoint - can be called via API if needed.
     */
    public void refreshAllCaches() {
        try {
            refreshEquipmentCache();
            refreshPortCache();
            refreshCustomerCache();
            refreshCountryCache();
            refreshCommodityCache();
            refreshPackageCache();
            refreshTerminalCache();
            refreshServiceCache();
            refreshVesselCache();
            refreshDangerousGoodsCache();
            refreshRouteCache();
            refreshTimezoneCache();
            lastRefreshTime = LocalDateTime.now();
            logger.info("All caches refreshed at {}", lastRefreshTime);
        } catch (Exception e) {
            logger.error("Error refreshing caches", e);
        }
    }

    // ==================== Equipment Cache ====================

    private void refreshEquipmentCache() {
        try {
            Map<String, Float> grossWeights = equipmentRepository.getAllEquipmentGrossWeights();
            Map<String, Float> tareWeights = equipmentRepository.getAllEquipmentTareWeights();
            
            equipmentGrossWeights.clear();
            equipmentGrossWeights.putAll(grossWeights);
            
            equipmentTareWeights.clear();
            equipmentTareWeights.putAll(tareWeights);
            
            validEquipmentSizes.clear();
            validEquipmentSizes.addAll(grossWeights.keySet());
            
            logger.info("Equipment cache refreshed: {} equipment types loaded", grossWeights.size());
        } catch (Exception e) {
            logger.error("Error refreshing equipment cache", e);
        }
    }

    /**
     * Get max gross weight for an equipment type from cache.
     * @param equipmentSize Equipment type (e.g., "40DC", "20DC")
     * @return Gross weight in kg, or null if not found
     */
    public Float getEquipmentGrossWeight(String equipmentSize) {
        if (equipmentSize == null) return null;
        return equipmentGrossWeights.get(equipmentSize.trim().toUpperCase());
    }

    /**
     * Get tare weight for an equipment type from cache.
     * @param equipmentSize Equipment type (e.g., "40DC", "20DC")
     * @return Tare weight in kg, or null if not found
     */
    public Float getEquipmentTareWeight(String equipmentSize) {
        if (equipmentSize == null) return null;
        return equipmentTareWeights.get(equipmentSize.trim().toUpperCase());
    }

    /**
     * Check if an equipment size is valid.
     */
    public boolean isValidEquipmentSize(String equipmentSize) {
        if (equipmentSize == null) return false;
        return validEquipmentSizes.contains(equipmentSize.trim().toUpperCase());
    }

    // ==================== Port Cache ====================

    private void refreshPortCache() {
        try {
            Set<String> ports = masterDataRepository.getAllActivePortCodes();
            validPortCodes.clear();
            validPortCodes.addAll(ports);
            
            // Load port names
            Map<String, String> names = masterDataRepository.getAllActivePortNames();
            portNames.clear();
            portNames.putAll(names);
            
            logger.info("Port cache refreshed: {} ports loaded, {} port names loaded", ports.size(), names.size());
        } catch (Exception e) {
            logger.error("Error refreshing port cache", e);
        }
    }

    /**
     * Check if a port code is valid.
     */
    public boolean isValidPortCode(String portCode) {
        if (portCode == null) return false;
        return validPortCodes.contains(portCode.trim().toUpperCase());
    }

    public Set<String> getAllValidPortCodes() {
        return Collections.unmodifiableSet(validPortCodes);
    }

    /**
     * Get port name by port code.
     * @param portCode Port code (e.g., "ESBCN")
     * @return Port name, or null if not found
     */
    public String getPortName(String portCode) {
        if (portCode == null) return null;
        return portNames.get(portCode.trim().toUpperCase());
    }

    /**
     * Get all port names (for debugging/monitoring).
     */
    public Map<String, String> getAllPortNames() {
        return Collections.unmodifiableMap(portNames);
    }

    // ==================== Customer Cache ====================

    private void refreshCustomerCache() {
        try {
            Set<String> customers = masterDataRepository.getAllActiveCustomerCodes();
            validCustomerCodes.clear();
            validCustomerCodes.addAll(customers);
            logger.info("Customer cache refreshed: {} customers loaded", customers.size());
        } catch (Exception e) {
            logger.error("Error refreshing customer cache", e);
        }
    }

    /**
     * Check if a customer code is valid.
     */
    public boolean isValidCustomerCode(String customerCode) {
        if (customerCode == null) return false;
        return validCustomerCodes.contains(customerCode.trim().toUpperCase());
    }

    public Set<String> getAllValidCustomerCodes() {
        return Collections.unmodifiableSet(validCustomerCodes);
    }

    // ==================== Country Cache ====================

    private void refreshCountryCache() {
        try {
            Set<String> countries = masterDataRepository.getAllActiveCountryCodes();
            validCountryCodes.clear();
            validCountryCodes.addAll(countries);
            logger.info("Country cache refreshed: {} countries loaded", countries.size());
        } catch (Exception e) {
            logger.error("Error refreshing country cache", e);
        }
    }

    /**
     * Check if a country code is valid.
     */
    public boolean isValidCountryCode(String countryCode) {
        if (countryCode == null) return false;
        return validCountryCodes.contains(countryCode.trim().toUpperCase());
    }

    public Set<String> getAllValidCountryCodes() {
        return Collections.unmodifiableSet(validCountryCodes);
    }

    // ==================== Commodity Cache ====================

    private void refreshCommodityCache() {
        try {
            Set<String> codes = new HashSet<>();
            Map<String, Set<String>> loaded = new HashMap<>();
            masterDataRepository.populateCommodityMasterData(codes, loaded);
            validCommodityCodes.clear();
            validCommodityCodes.addAll(codes);
            commodityHsCodeToGroupCodes.clear();
            loaded.forEach((k, v) -> {
                Set<String> inner = ConcurrentHashMap.newKeySet();
                inner.addAll(v);
                commodityHsCodeToGroupCodes.put(k, inner);
            });
            logger.info("Commodity cache refreshed: {} commodities, {} hsCode→group mappings",
                    codes.size(), commodityHsCodeToGroupCodes.size());
        } catch (Exception e) {
            logger.error("Error refreshing commodity cache", e);
        }
    }

    /**
     * Check if a commodity code is valid.
     */
    public boolean isValidCommodityCode(String commodityCode) {
        if (commodityCode == null) return false;
        return validCommodityCodes.contains(commodityCode.trim().toUpperCase());
    }

    /**
     * True when {@code hsCode} and {@code commodityGroup} are non-blank, both exist as active commodity master codes,
     * and either no grpcode mappings exist for that hsCode or {@code commodityGroup} is one of them.
     */
    public boolean isHsCodeApplicableForCommodityGroup(String hsCode, String commodityGroup) {
        if (hsCode == null || commodityGroup == null) {
            return false;
        }
        String h = hsCode.trim();
        String g = commodityGroup.trim();
        if (h.isEmpty() || g.isEmpty()) {
            return false;
        }
        h = h.toUpperCase();
        g = g.toUpperCase();
        if (!validCommodityCodes.contains(h)) {
            return false;
        }
        Set<String> groups = commodityHsCodeToGroupCodes.get(h);
        if (groups == null || groups.isEmpty()) {
            return true;
        }
        return groups.contains(g);
    }

    public Set<String> getAllValidCommodityCodes() {
        return Collections.unmodifiableSet(validCommodityCodes);
    }

    // ==================== Package Cache ====================

    private void refreshPackageCache() {
        try {
            Set<String> packages = masterDataRepository.getAllActivePackageCodes();
            validPackageCodes.clear();
            validPackageCodes.addAll(packages);
            logger.info("Package cache refreshed: {} packages loaded", packages.size());
        } catch (Exception e) {
            logger.error("Error refreshing package cache", e);
        }
    }

    /**
     * Check if a package code is valid.
     */
    public boolean isValidPackageCode(String packageCode) {
        if (packageCode == null) return false;
        return validPackageCodes.contains(packageCode.trim().toUpperCase());
    }

    public Set<String> getAllValidPackageCodes() {
        return Collections.unmodifiableSet(validPackageCodes);
    }

    // ==================== Terminal Cache ====================

    private void refreshTerminalCache() {
        try {
            Set<String> terminals = masterDataRepository.getAllActiveTerminalCodes();
            validTerminalCodes.clear();
            validTerminalCodes.addAll(terminals);
            logger.info("Terminal cache refreshed: {} terminals loaded", terminals.size());
        } catch (Exception e) {
            logger.error("Error refreshing terminal cache", e);
        }
    }

    /**
     * Check if a terminal code is valid.
     */
    public boolean isValidTerminalCode(String terminalCode) {
        if (terminalCode == null) return false;
        return validTerminalCodes.contains(terminalCode.trim().toUpperCase());
    }

    public Set<String> getAllValidTerminalCodes() {
        return Collections.unmodifiableSet(validTerminalCodes);
    }

    // ==================== Service Cache ====================

    private void refreshServiceCache() {
        try {
            Set<String> services = masterDataRepository.getAllActiveServiceCodes();
            validServiceCodes.clear();
            validServiceCodes.addAll(services);
            logger.info("Service cache refreshed: {} services loaded", services.size());
        } catch (Exception e) {
            logger.error("Error refreshing service cache", e);
        }
    }

    /**
     * Check if a service code is valid.
     */
    public boolean isValidServiceCode(String serviceCode) {
        if (serviceCode == null) return false;
        return validServiceCodes.contains(serviceCode.trim().toUpperCase());
    }

    public Set<String> getAllValidServiceCodes() {
        return Collections.unmodifiableSet(validServiceCodes);
    }

    // ==================== Vessel Cache ====================

    private void refreshVesselCache() {
        try {
            Set<String> vessels = masterDataRepository.getAllActiveVesselCodes();
            validVesselCodes.clear();
            validVesselCodes.addAll(vessels);
            logger.info("Vessel cache refreshed: {} vessels loaded", vessels.size());
        } catch (Exception e) {
            logger.error("Error refreshing vessel cache", e);
        }
    }

    /**
     * Check if a vessel code is valid.
     */
    public boolean isValidVesselCode(String vesselCode) {
        if (vesselCode == null) return false;
        return validVesselCodes.contains(vesselCode.trim().toUpperCase());
    }

    public Set<String> getAllValidVesselCodes() {
        return Collections.unmodifiableSet(validVesselCodes);
    }

    // ==================== Dangerous Goods Cache ====================

    private void refreshDangerousGoodsCache() {
        try {
            Map<String, String> dgData = masterDataRepository.getAllActiveDangerousGoods();
            
            // Clear and populate the map
            dangerousGoodsClassMap.clear();
            dangerousGoodsClassMap.putAll(dgData);
            
            // Populate separate sets for independent validation
            validUnNumbers.clear();
            validClassCodes.clear();
            
            for (Map.Entry<String, String> entry : dgData.entrySet()) {
                validUnNumbers.add(entry.getKey()); // UN number (already uppercase)
                if (entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                    validClassCodes.add(entry.getValue()); // Class code (already uppercase)
                }
            }
            
            logger.info("Dangerous goods cache refreshed: {} UN numbers, {} class codes loaded", 
                validUnNumbers.size(), validClassCodes.size());
        } catch (Exception e) {
            logger.error("Error refreshing dangerous goods cache", e);
        }
    }

    /**
     * Check if a UN number is valid (exists in subs table with state='A').
     * @param unNo UN number to validate
     * @return true if UN number exists, false otherwise
     */
    public boolean isValidUnNo(String unNo) {
        if (unNo == null) return false;
        return validUnNumbers.contains(unNo.trim().toUpperCase());
    }

    /**
     * Check if a class code is valid (exists in subs table with state='A').
     * @param classCode Class code to validate
     * @return true if class code exists, false otherwise
     */
    public boolean isValidClassCode(String classCode) {
        if (classCode == null) return false;
        return validClassCodes.contains(classCode.trim().toUpperCase());
    }

    /**
     * Get the IMO class code for a UN number.
     * @param unNo UN number
     * @return Class code, or null if UN number not found
     */
    public String getClassCodeForUnNo(String unNo) {
        if (unNo == null) return null;
        return dangerousGoodsClassMap.get(unNo.trim().toUpperCase());
    }

    /**
     * Validate that a UN number exists and its class code matches.
     * @param unNo UN number to validate
     * @param classCode Class code to validate
     * @return true if both UN number exists and class code matches, false otherwise
     */
    public boolean isValidDangerousGoods(String unNo, String classCode) {
        if (unNo == null || classCode == null) return false;
        String cachedClass = dangerousGoodsClassMap.get(unNo.trim().toUpperCase());
        if (cachedClass == null) return false;
        return cachedClass.equalsIgnoreCase(classCode.trim());
    }

    public Map<String, String> getAllDangerousGoodsClassMap() {
        return Collections.unmodifiableMap(dangerousGoodsClassMap);
    }

    public Set<String> getAllValidUnNumbers() {
        return Collections.unmodifiableSet(validUnNumbers);
    }

    public Set<String> getAllValidClassCodes() {
        return Collections.unmodifiableSet(validClassCodes);
    }

    // ==================== Route Cache ====================

    private void refreshRouteCache() {
        try {
            Set<Integer> routeIds = masterDataRepository.getAllActiveRouteIds();
            validRouteIds.clear();
            validRouteIds.addAll(routeIds);
            logger.info("Route cache refreshed: {} active route IDs loaded", routeIds.size());
        } catch (Exception e) {
            logger.error("Error refreshing route cache", e);
        }
    }

    /**
     * Check if a route ID is valid (exists in routedetail table with status='A' and current date between fromdate and todate).
     * @param routeId Route ID to validate
     * @return true if route ID exists and is active, false otherwise
     */
    public boolean isValidRouteId(Integer routeId) {
        if (routeId == null) return false;
        return validRouteIds.contains(routeId);
    }

    public Set<Integer> getAllValidRouteIds() {
        return Collections.unmodifiableSet(validRouteIds);
    }

    // ==================== Timezone Cache ====================

    private void refreshTimezoneCache() {
        try {
            Map<String, String> timezones = masterDataRepository.getAllActiveTimezones();
            if (timezones != null) {
                timezoneOffsets.clear();
                timezoneOffsets.putAll(timezones);
                logger.info("Timezone cache refreshed: {} timezone entries loaded", timezones.size());
            } else {
                logger.warn("Timezone cache refresh skipped: repository returned null");
            }
        } catch (Exception e) {
            logger.error("Error refreshing timezone cache", e);
        }
    }

    /**
     * Get timezone offset for a port code.
     * @param portCode Port code (e.g., "INNSA")
     * @return Timezone offset string (IANA ZoneId or offset, e.g. "Asia/Colombo" or "+05:30"), or null if not found
     */
    public String getTimezoneOffset(String portCode) {
        if (portCode == null) return null;
        return timezoneOffsets.get(portCode.trim().toUpperCase());
    }

    /**
     * Check if a port has timezone data.
     */
    public boolean hasTimezoneData(String portCode) {
        if (portCode == null) return false;
        return timezoneOffsets.containsKey(portCode.trim().toUpperCase());
    }

    /**
     * Get all timezone offsets (for debugging/monitoring).
     */
    public Map<String, String> getAllTimezoneOffsets() {
        return Collections.unmodifiableMap(timezoneOffsets);
    }

    // ==================== Cache Status ====================

    /**
     * Get cache statistics for monitoring.
     */
    public CacheStats getCacheStats() {
        return new CacheStats(
            equipmentGrossWeights.size(),
            validPortCodes.size(),
            validCustomerCodes.size(),
            validCountryCodes.size(),
            validCommodityCodes.size(),
            validPackageCodes.size(),
            validTerminalCodes.size(),
            validServiceCodes.size(),
            validVesselCodes.size(),
            dangerousGoodsClassMap.size(),
            validRouteIds.size(),
            timezoneOffsets.size(),
            lastRefreshTime
        );
    }

    /**
     * Check if cache is initialized and ready.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Cache statistics for monitoring.
     */
    public static class CacheStats {
        public final int equipmentCount;
        public final int portCount;
        public final int customerCount;
        public final int countryCount;
        public final int commodityCount;
        public final int packageCount;
        public final int terminalCount;
        public final int serviceCount;
        public final int vesselCount;
        public final int dangerousGoodsCount;
        public final int routeCount;
        public final int timezoneCount;
        public final LocalDateTime lastRefreshTime;

        public CacheStats(int equipmentCount, int portCount, int customerCount,
                         int countryCount, int commodityCount, int packageCount,
                         int terminalCount, int serviceCount, int vesselCount,
                         int dangerousGoodsCount, int routeCount, int timezoneCount, 
                         LocalDateTime lastRefreshTime) {
            this.equipmentCount = equipmentCount;
            this.portCount = portCount;
            this.customerCount = customerCount;
            this.countryCount = countryCount;
            this.commodityCount = commodityCount;
            this.packageCount = packageCount;
            this.terminalCount = terminalCount;
            this.serviceCount = serviceCount;
            this.vesselCount = vesselCount;
            this.dangerousGoodsCount = dangerousGoodsCount;
            this.routeCount = routeCount;
            this.timezoneCount = timezoneCount;
            this.lastRefreshTime = lastRefreshTime;
        }
    }
}

