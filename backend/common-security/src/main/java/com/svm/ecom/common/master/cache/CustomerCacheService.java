package com.svm.ecom.common.master.cache;

import com.svm.ecom.common.master.dto.CustomerDetail;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Dedicated customer cache service that provides customer-specific operations.
 * This service wraps the CommonMasterDataCacheService to provide a clean interface
 * for customer-related caching operations across all modules.
 * 
 * Usage example:
 * <pre>
 * &#64;Autowired
 * private CustomerCacheService customerCacheService;
 * 
 * // Check if customer exists
 * boolean exists = customerCacheService.isValidCustomer("CUST001");
 * 
 * // Get customer name
 * String name = customerCacheService.getCustomerName("CUST001");
 * 
 * // Get customer detail
 * CustomerDetail detail = customerCacheService.getCustomerDetail("CUST001");
 * 
 * // Get all active customers
 * Set&lt;String&gt; allCodes = customerCacheService.getAllCustomerCodes();
 * Map&lt;String, CustomerDetail&gt; allDetails = customerCacheService.getAllCustomerDetails();
 * </pre>
 */
@Service("customerCacheService")
public class CustomerCacheService {

    private final CommonMasterDataCacheService commonMasterDataCacheService;

    public CustomerCacheService(@Qualifier(CommonMasterDataCacheService.BEAN_NAME) 
                               CommonMasterDataCacheService commonMasterDataCacheService) {
        this.commonMasterDataCacheService = commonMasterDataCacheService;
    }

    /**
     * Check if a customer code is valid (exists in cache).
     * 
     * @param customerCode customer code to validate
     * @return true if customer exists and is active, false otherwise
     */
    public boolean isValidCustomer(String customerCode) {
        return commonMasterDataCacheService.isValidCustomerCode(customerCode);
    }

    /**
     * Get customer detail by customer code.
     * 
     * @param customerCode customer code to lookup
     * @return CustomerDetail if found, null otherwise
     */
    public CustomerDetail getCustomerDetail(String customerCode) {
        return commonMasterDataCacheService.getCustomerDetail(customerCode);
    }

    /**
     * Get customer name by customer code.
     * 
     * @param customerCode customer code to lookup
     * @return customer name if found, null otherwise
     */
    public String getCustomerName(String customerCode) {
        return commonMasterDataCacheService.getCustomerName(customerCode);
    }

    /**
     * Get all valid customer codes.
     * 
     * @return Set of all active customer codes (uppercase)
     */
    public Set<String> getAllCustomerCodes() {
        return commonMasterDataCacheService.getAllValidCustomerCodes();
    }

    /**
     * Get all customer details.
     * 
     * @return Map of customer code → CustomerDetail for all active customers
     */
    public Map<String, CustomerDetail> getAllCustomerDetails() {
        return commonMasterDataCacheService.getAllCustomerDetails();
    }

    /**
     * Validate multiple customer codes at once.
     * 
     * @param customerCodes list of customer codes to validate
     * @return Map of customer code → validation result (true/false)
     */
    public Map<String, Boolean> validateCustomerCodes(List<String> customerCodes) {
        if (customerCodes == null || customerCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        
        return customerCodes.stream()
                .collect(Collectors.toMap(
                    code -> code,
                    this::isValidCustomer
                ));
    }

    /**
     * Get customer details for multiple customer codes.
     * 
     * @param customerCodes list of customer codes to lookup
     * @return Map of customer code → CustomerDetail (only valid customers included)
     */
    public Map<String, CustomerDetail> getCustomerDetails(List<String> customerCodes) {
        if (customerCodes == null || customerCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        
        return customerCodes.stream()
                .filter(this::isValidCustomer)
                .collect(Collectors.toMap(
                    code -> code,
                    this::getCustomerDetail
                ));
    }

    /**
     * Search customers by partial name match (case-insensitive).
     * 
     * @param partialName partial customer name to search for
     * @return List of CustomerDetail matching the search criteria
     */
    public List<CustomerDetail> searchCustomersByName(String partialName) {
        if (partialName == null || partialName.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String searchTerm = partialName.trim().toLowerCase();
        return getAllCustomerDetails().values().stream()
                .filter(detail -> detail.getCustName() != null && 
                               detail.getCustName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Check if the customer cache is initialized and ready for use.
     * 
     * @return true if cache is initialized, false otherwise
     */
    public boolean isCacheInitialized() {
        return commonMasterDataCacheService.isInitialized();
    }

    /**
     * Get the last cache refresh time.
     * 
     * @return LocalDateTime of last refresh, null if not yet refreshed
     */
    public java.time.LocalDateTime getLastRefreshTime() {
        return commonMasterDataCacheService.getLastRefreshTime();
    }
}