package com.svm.ecom.common.master.cache;

import com.svm.ecom.common.master.dto.CustomerDetail;
import com.svm.ecom.common.master.repo.EquipmentRepository;
import com.svm.ecom.common.master.repo.MasterDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Common master data cache. Loads from reportDB on startup and refreshes every 30 minutes.
 * Used by SIBL service for SI validation and cache/validation. Bean name: {@value #BEAN_NAME}.
 * Booking service uses {@code MasterDataCacheService} with the same commodity (comdtycode + grpcode) load semantics.
 */
@Service(CommonMasterDataCacheService.BEAN_NAME)
public class CommonMasterDataCacheService {

    /** Explicit bean name for injection (e.g. @Qualifier(CommonMasterDataCacheService.BEAN_NAME)). */
    public static final String BEAN_NAME = "commonMasterDataCacheService";

    private static final Logger logger = LoggerFactory.getLogger(CommonMasterDataCacheService.class);

    private final EquipmentRepository equipmentRepository;
    private final MasterDataRepository masterDataRepository;

    private final Set<String> validPortCodes = ConcurrentHashMap.newKeySet();
    private final Set<String> validCustomerCodes = ConcurrentHashMap.newKeySet();
    /** Customer code (uppercase) → CustomerDetail for efficient lookups */
    private final Map<String, CustomerDetail> customerDetailsByCode = new ConcurrentHashMap<>();
    private final Set<String> validCountryCodes = ConcurrentHashMap.newKeySet();
    private final Set<String> validCommodityCodes = ConcurrentHashMap.newKeySet();
    /** Normalized comdtycode (HS) → distinct grpcode values for hsCode vs commodityGroup validation. */
    private final Map<String, Set<String>> commodityHsCodeToGroupCodes = new ConcurrentHashMap<>();
    private final Set<String> validPackageCodes = ConcurrentHashMap.newKeySet();
    private final Set<String> validTerminalCodes = ConcurrentHashMap.newKeySet();
    /** Terminal code → name for SI view (from MasterDataRepository.getTerminalNameMap()). */
    private final Map<String, String> terminalNameByCode = new ConcurrentHashMap<>();
    private final Set<String> validServiceCodes = ConcurrentHashMap.newKeySet();
    private final Set<String> validVesselCodes = ConcurrentHashMap.newKeySet();
    /** Valid equipment type codes only (no tare/gross); refreshed from DB. Tare/gross fetched by query when needed. */
    private final Set<String> validEquipmentSizes = ConcurrentHashMap.newKeySet();

    private volatile LocalDateTime lastRefreshTime;
    private volatile boolean initialized = false;

    public CommonMasterDataCacheService(EquipmentRepository equipmentRepository,
                                        MasterDataRepository masterDataRepository) {
        this.equipmentRepository = equipmentRepository;
        this.masterDataRepository = masterDataRepository;
    }

    @PostConstruct
    public void initializeCache() {
        logger.info("Initializing common master data cache (30-min refresh)...");
        refreshAllCaches();
        initialized = true;
        logger.info("Common master data cache initialized successfully");
    }

    @Scheduled(fixedRate = 1800000) // 30 minutes in milliseconds
    public void scheduledRefresh() {
        if (initialized) {
            logger.info("Scheduled refresh of common master data cache...");
            refreshAllCaches();
        }
    }

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
            lastRefreshTime = LocalDateTime.now();
            logger.info("All common master caches refreshed at {}", lastRefreshTime);
        } catch (Exception e) {
            logger.error("Error refreshing common master caches", e);
        }
    }

    private void refreshEquipmentCache() {
        try {
            Set<String> activeTypes = equipmentRepository.getAllActiveEquipTypes();
            validEquipmentSizes.clear();
            validEquipmentSizes.addAll(activeTypes);
            logger.info("Equipment cache refreshed: {} valid equipment types (tare/gross fetched by query when needed)", activeTypes.size());
        } catch (Exception e) {
            logger.error("Error refreshing equipment cache", e);
        }
    }

    private void refreshPortCache() {
        try {
            Set<String> ports = masterDataRepository.getAllActivePortCodes();
            validPortCodes.clear();
            validPortCodes.addAll(ports);
            logger.info("Port cache refreshed: {} ports loaded", ports.size());
        } catch (Exception e) {
            logger.error("Error refreshing port cache", e);
        }
    }

    private void refreshCustomerCache() {
        try {
            // Load customer codes for backward compatibility
            Set<String> customers = masterDataRepository.getAllActiveCustomerCodes();
            validCustomerCodes.clear();
            validCustomerCodes.addAll(customers);
            
            // Load customer details (code + name) for enhanced lookups
            Map<String, CustomerDetail> customerDetails = masterDataRepository.getAllActiveCustomerDetails();
            customerDetailsByCode.clear();
            customerDetailsByCode.putAll(customerDetails);
            
            logger.info("Customer cache refreshed: {} customers loaded (codes + details)", customers.size());
        } catch (Exception e) {
            logger.error("Error refreshing customer cache", e);
        }
    }

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

    private void refreshTerminalCache() {
        try {
            Set<String> terminals = masterDataRepository.getAllActiveTerminalCodes();
            validTerminalCodes.clear();
            validTerminalCodes.addAll(terminals);
            Map<String, String> names = masterDataRepository.getTerminalNameMap();
            terminalNameByCode.clear();
            terminalNameByCode.putAll(names);
            logger.info("Terminal cache refreshed: {} terminals loaded (codes + names)", terminals.size());
        } catch (Exception e) {
            logger.error("Error refreshing terminal cache", e);
        }
    }

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

    /** Query-based: gross weight for one equipment type (no cache). */
    public Float getEquipmentGrossWeight(String equipmentSize) {
        if (equipmentSize == null) return null;
        return equipmentRepository.getGrossWeightByEquipType(equipmentSize);
    }

    /** Query-based: tare weight for one equipment type (no cache). */
    public Float getEquipmentTareWeight(String equipmentSize) {
        if (equipmentSize == null) return null;
        return equipmentRepository.getTareWeightByEquipType(equipmentSize);
    }

    /**
     * Bulk: fetch tare weights for all given equipment types in one DB query.
     *
     * @param equipTypes equipment types from container list (e.g. request.getContainers() -> equipmentSize)
     * @return Map equiptype (uppercase) -> tare weight (kg); only types found in DB are present
     */
    public Map<String, Float> getTareWeightsByEquipTypes(Collection<String> equipTypes) {
        if (equipTypes == null || equipTypes.isEmpty()) return Collections.emptyMap();
        return equipmentRepository.getTareWeightsByEquipTypes(equipTypes);
    }

    /**
     * Bulk: fetch gross weights for all given equipment types in one DB query.
     */
    public Map<String, Float> getGrossWeightsByEquipTypes(Collection<String> equipTypes) {
        if (equipTypes == null || equipTypes.isEmpty()) return Collections.emptyMap();
        return equipmentRepository.getGrossWeightsByEquipTypes(equipTypes);
    }

    public boolean isValidEquipmentSize(String equipmentSize) {
        if (equipmentSize == null) return false;
        return validEquipmentSizes.contains(equipmentSize.trim().toUpperCase());
    }

    public boolean isValidPortCode(String portCode) {
        if (portCode == null) return false;
        return validPortCodes.contains(portCode.trim().toUpperCase());
    }

    public Set<String> getAllValidPortCodes() {
        return Collections.unmodifiableSet(validPortCodes);
    }

    public boolean isValidCustomerCode(String customerCode) {
        if (customerCode == null) return false;
        return validCustomerCodes.contains(customerCode.trim().toUpperCase());
    }

    public Set<String> getAllValidCustomerCodes() {
        return Collections.unmodifiableSet(validCustomerCodes);
    }

    /**
     * Get customer detail by customer code.
     * 
     * @param customerCode customer code to lookup
     * @return CustomerDetail if found, null otherwise
     */
    public CustomerDetail getCustomerDetail(String customerCode) {
        if (customerCode == null) return null;
        return customerDetailsByCode.get(customerCode.trim().toUpperCase());
    }

    /**
     * Get customer name by customer code.
     * 
     * @param customerCode customer code to lookup
     * @return customer name if found, null otherwise
     */
    public String getCustomerName(String customerCode) {
        CustomerDetail detail = getCustomerDetail(customerCode);
        return detail != null ? detail.getCustName() : null;
    }

    /**
     * Get all customer details as an unmodifiable map.
     * 
     * @return Map of customer code → CustomerDetail
     */
    public Map<String, CustomerDetail> getAllCustomerDetails() {
        return Collections.unmodifiableMap(customerDetailsByCode);
    }

    public boolean isValidCountryCode(String countryCode) {
        if (countryCode == null) return false;
        return validCountryCodes.contains(countryCode.trim().toUpperCase());
    }

    public Set<String> getAllValidCountryCodes() {
        return Collections.unmodifiableSet(validCountryCodes);
    }

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

    public boolean isValidPackageCode(String packageCode) {
        if (packageCode == null) return false;
        return validPackageCodes.contains(packageCode.trim().toUpperCase());
    }

    public Set<String> getAllValidPackageCodes() {
        return Collections.unmodifiableSet(validPackageCodes);
    }

    public boolean isValidTerminalCode(String terminalCode) {
        if (terminalCode == null) return false;
        return validTerminalCodes.contains(terminalCode.trim().toUpperCase());
    }

    public Set<String> getAllValidTerminalCodes() {
        return Collections.unmodifiableSet(validTerminalCodes);
    }

    /** Terminal code → name for SI view (polTerminal/podTerminal display). Same source as booking service. */
    public Map<String, String> getTerminalNameMap() {
        return Collections.unmodifiableMap(terminalNameByCode);
    }

    public boolean isValidServiceCode(String serviceCode) {
        if (serviceCode == null) return false;
        return validServiceCodes.contains(serviceCode.trim().toUpperCase());
    }

    public Set<String> getAllValidServiceCodes() {
        return Collections.unmodifiableSet(validServiceCodes);
    }

    public boolean isValidVesselCode(String vesselCode) {
        if (vesselCode == null) return false;
        return validVesselCodes.contains(vesselCode.trim().toUpperCase());
    }

    public Set<String> getAllValidVesselCodes() {
        return Collections.unmodifiableSet(validVesselCodes);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public LocalDateTime getLastRefreshTime() {
        return lastRefreshTime;
    }
}
