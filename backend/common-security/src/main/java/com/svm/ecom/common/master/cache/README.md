# Customer Cache Implementation

This document describes the common customer cache implementation that provides efficient customer code validation and lookup across all ECOM modules.

## Overview

The customer cache system provides:
- Fast in-memory customer code validation
- Customer name lookup by code
- Bulk operations for multiple customers
- Search functionality by customer name
- Automatic cache refresh every 30 minutes

## Architecture

```
┌─────────────────────────────────────────┐
│ CustomerCacheService                    │
│ (Clean API for customer operations)     │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│ CommonMasterDataCacheService            │
│ (Core caching with 30-min refresh)      │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│ MasterDataRepository                    │
│ (Database queries with JdbcTemplate)    │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│ Database: customermaster table          │
│ Query: SELECT custcode, custname        │
│        FROM customermaster (nolock)     │
│        WHERE status='A'                 │
└─────────────────────────────────────────┘
```

## Components

### 1. CustomerDetail DTO
```java
public class CustomerDetail {
    private String custCode;  // Customer code (uppercase)
    private String custName;  // Customer name
}
```

### 2. CustomerCacheService
The main service interface for customer operations:

```java
@Service("customerCacheService")
public class CustomerCacheService {
    // Validation
    boolean isValidCustomer(String customerCode)
    
    // Lookup operations
    CustomerDetail getCustomerDetail(String customerCode)
    String getCustomerName(String customerCode)
    
    // Bulk operations
    Map<String, Boolean> validateCustomerCodes(List<String> customerCodes)
    Map<String, CustomerDetail> getCustomerDetails(List<String> customerCodes)
    
    // Search and retrieval
    List<CustomerDetail> searchCustomersByName(String partialName)
    Set<String> getAllCustomerCodes()
    Map<String, CustomerDetail> getAllCustomerDetails()
}
```

### 3. CommonMasterDataCacheService
Enhanced to include customer details caching alongside existing master data.

### 4. MasterDataRepository
Enhanced with new method:
```java
Map<String, CustomerDetail> getAllActiveCustomerDetails()
```

## Usage Examples

### Basic Usage

```java
@Service
public class MyService {
    
    @Autowired
    private CustomerCacheService customerCacheService;
    
    public void validateCustomer(String customerCode) {
        // Check if customer exists
        if (!customerCacheService.isValidCustomer(customerCode)) {
            throw new IllegalArgumentException("Invalid customer code: " + customerCode);
        }
        
        // Get customer name for display
        String customerName = customerCacheService.getCustomerName(customerCode);
        System.out.println("Customer: " + customerCode + " - " + customerName);
    }
}
```

### Contract Validation Example

```java
@Service
public class ContractService {
    
    @Autowired
    private CustomerCacheService customerCacheService;
    
    public boolean validateContractParties(String shipper, String consignee, String notifyParty) {
        List<String> customerCodes = Arrays.asList(shipper, consignee, notifyParty);
        Map<String, Boolean> validationResults = customerCacheService.validateCustomerCodes(customerCodes);
        
        return validationResults.values().stream().allMatch(Boolean::booleanValue);
    }
    
    public Map<String, String> getCustomerNames(List<String> customerCodes) {
        return customerCacheService.getCustomerDetails(customerCodes)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().getCustName()
                ));
    }
}
```

### Search and Autocomplete

```java
@RestController
public class CustomerController {
    
    @Autowired
    private CustomerCacheService customerCacheService;
    
    @GetMapping("/customers/search")
    public List<CustomerDetail> searchCustomers(@RequestParam String query) {
        return customerCacheService.searchCustomersByName(query);
    }
    
    @GetMapping("/customers/all")
    public Set<String> getAllCustomerCodes() {
        return customerCacheService.getAllCustomerCodes();
    }
}
```

## Module Integration

### 1. Add Dependency
Ensure your module depends on `common-security`:

```xml
<dependency>
    <groupId>com.svm.ecom</groupId>
    <artifactId>common-security</artifactId>
    <version>${project.version}</version>
</dependency>
```

### 2. Inject the Service
```java
@Service
public class YourService {
    
    private final CustomerCacheService customerCacheService;
    
    public YourService(CustomerCacheService customerCacheService) {
        this.customerCacheService = customerCacheService;
    }
}
```

### 3. Use in Your Logic
```java
// Validate customer before processing
if (!customerCacheService.isValidCustomer(customerCode)) {
    throw new ValidationException("Invalid customer: " + customerCode);
}

// Get customer name for response
String customerName = customerCacheService.getCustomerName(customerCode);
response.setCustomerName(customerName);
```

## Performance Characteristics

- **Cache Type**: In-memory ConcurrentHashMap
- **Refresh Frequency**: Every 30 minutes (automatic)
- **Startup**: Cache loaded on application startup (@PostConstruct)
- **Thread Safety**: Fully thread-safe using ConcurrentHashMap
- **Memory Usage**: Minimal - only stores customer codes and names
- **Query Performance**: O(1) lookup time for customer validation and retrieval

## Database Query

The cache is populated using this SQL query:
```sql
SELECT custcode, custname 
FROM customermaster (nolock) 
WHERE status='A' AND custcode IS NOT NULL
```

## Cache Refresh

- **Automatic**: Every 30 minutes via @Scheduled annotation
- **Manual**: Can be triggered by calling `refreshAllCaches()` on CommonMasterDataCacheService
- **Startup**: Initialized automatically when the application starts
- **Error Handling**: Failures are logged but don't stop the application

## Monitoring

Check cache status:
```java
// Check if cache is ready
boolean ready = customerCacheService.isCacheInitialized();

// Get last refresh time
LocalDateTime lastRefresh = customerCacheService.getLastRefreshTime();

// Get cache size
int customerCount = customerCacheService.getAllCustomerCodes().size();
```

## Migration from Direct Database Queries

### Before (Direct Query)
```java
@Repository
public class MyRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public boolean isValidCustomer(String customerCode) {
        String sql = "SELECT COUNT(*) FROM customermaster WHERE custcode = ? AND status = 'A'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, customerCode);
        return count != null && count > 0;
    }
}
```

### After (Using Cache)
```java
@Service
public class MyService {
    
    @Autowired
    private CustomerCacheService customerCacheService;
    
    public boolean isValidCustomer(String customerCode) {
        return customerCacheService.isValidCustomer(customerCode);
    }
}
```

## Benefits

1. **Performance**: O(1) lookup vs database query
2. **Reduced Database Load**: Eliminates repeated customer validation queries
3. **Consistency**: Single source of truth for customer data across modules
4. **Reliability**: In-memory cache continues working even if database is temporarily unavailable
5. **Simplicity**: Clean API hides caching complexity from business logic
6. **Automatic Updates**: Cache refreshes automatically to stay current

## Best Practices

1. **Always check cache initialization** before using in critical paths
2. **Use bulk operations** when validating multiple customers
3. **Handle null returns** gracefully for invalid customer codes
4. **Log validation failures** for debugging and monitoring
5. **Consider search functionality** for user-facing features like autocomplete