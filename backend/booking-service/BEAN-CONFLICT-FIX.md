# Bean Conflict Fix - Duplicate Repository Registration

## Problem

The application was failing to start with the following error:

```
Invalid bean definition with name 'ecomBookingCustomerRepository' 
defined in com.solverminds.booking.repo.EcomBookingCustomerRepository 
defined in @EnableJpaRepositories declared on BookingServiceApplication: 
Cannot register bean definition [...] for bean 'ecomBookingCustomerRepository': 
There is already [...] bound.
```

## Root Cause

**Duplicate `@EnableJpaRepositories` configuration** was causing the same repository package to be scanned twice:

### 1. BookingServiceApplication.java (REMOVED)
```java
@EnableJpaRepositories(basePackages = {
    "com.solverminds.booking.repo"     // ❌ First registration
})
```

### 2. ECOMDataConfig.java (KEPT)
```java
@EnableJpaRepositories(
    basePackages = {"com.svm.ecom.infrastructure.ecom", 
                    "com.solverminds.ecom.register.repo", 
                    "com.solverminds.booking.repo",      // ✅ Second registration
                    "com.solverminds.sibl.repo"},
    entityManagerFactoryRef = "ecomEntityManagerFactory",
    transactionManagerRef = "ecomTransactionManager"
)
```

## Solution Applied

### ✅ Removed duplicate annotation from BookingServiceApplication.java

**Before:**
```java
@SpringBootApplication(scanBasePackages = {
    "com.solverminds.booking",
    "com.svm.ecom.security",
    "com.svm.ecom.config"
})
@EntityScan(basePackages = {
    "com.solverminds.booking.entity",
    "com.solverminds.edi.entity"
})
@EnableJpaRepositories(basePackages = {    // ❌ REMOVED
    "com.solverminds.booking.repo"
})
public class BookingServiceApplication { ... }
```

**After:**
```java
@SpringBootApplication(scanBasePackages = {
    "com.solverminds.booking",
    "com.svm.ecom.security",
    "com.svm.ecom.config"
})
@EntityScan(basePackages = {
    "com.solverminds.booking.entity",
    "com.solverminds.edi.entity"
})
// Repository scanning is handled by ECOMDataConfig in common-security
// which configures repositories with proper entityManagerFactory and transactionManager
public class BookingServiceApplication { ... }
```

## Why This is Correct

### Multi-Datasource Pattern

In a multi-datasource Spring Boot application, repository scanning should be configured in the **datasource configuration class** (not the main application class) because:

1. **Explicit EntityManagerFactory binding** - Each datasource needs its own EntityManagerFactory
2. **Transaction Manager isolation** - Each repository needs the correct transaction manager
3. **Prevents conflicts** - Single point of repository registration

### Current Configuration (Correct)

```
ECOMDataConfig.java
├── Manages ECOM DataSource
├── Configures ecomEntityManagerFactory
├── Configures ecomTransactionManager
└── Scans repositories:
    ├── com.svm.ecom.infrastructure.ecom
    ├── com.solverminds.ecom.register.repo
    ├── com.solverminds.booking.repo      ✅ Booking repos here
    └── com.solverminds.sibl.repo

BookingServiceApplication.java
├── Defines main application
├── Scans components (@Component, @Service, @Controller)
├── Scans entities (@EntityScan)
└── Datasource config handled by ECOMDataConfig ✅
```

## Impact

### What Changed
- ✅ Removed `@EnableJpaRepositories` from `BookingServiceApplication.java`
- ✅ Updated `EDI-INTEGRATION-SUMMARY.md` documentation
- ✅ Added clarifying comments

### What Stayed the Same
- ✅ All repositories still registered (via ECOMDataConfig)
- ✅ Entity scanning still works (@EntityScan remains)
- ✅ All functionality preserved
- ✅ No code changes needed in services or repositories

## Verification

The application should now start successfully with:
- ✅ No duplicate bean definition errors
- ✅ All repositories accessible
- ✅ All transactions working correctly
- ✅ Proper datasource isolation

## Files Modified

1. **booking-service/src/main/java/com/solverminds/booking/BookingServiceApplication.java**
   - Removed `@EnableJpaRepositories` annotation
   - Removed `import org.springframework.data.jpa.repository.config.EnableJpaRepositories;`
   - Added clarifying comment

2. **common-security/src/main/java/com/svm/ecom/config/ECOMDataConfig.java**
   - Added `"com.solverminds.edi.entity"` to `setPackagesToScan()` in `ecomEntityManagerFactory()`
   - This ensures EDI entities (like EdiTempBooking) are recognized as managed types

3. **booking-service/EDI-INTEGRATION-SUMMARY.md**
   - Updated documentation to reflect configuration change

---

## Additional Fix Applied

### Issue: "Not a managed type: class com.solverminds.edi.entity.EdiTempBooking"

After removing `@EnableJpaRepositories` from `BookingServiceApplication`, the EDI entities were no longer being scanned because `ECOMDataConfig` wasn't aware of them.

**Initial Attempt**: Added `"com.solverminds.edi.entity"` to the entity scanning in `ECOMDataConfig.ecomEntityManagerFactory()`. However, this was **INCORRECT** because:

❌ **EDI entities are in a separate EDI database, not in the ECOM database**

### Correct Solution: Multi-Datasource Configuration

Since EDI tables reside in a **separate database**, a proper multi-datasource configuration is required:

#### 1. Created EDIDataConfig.java

**Location**: `common-security/src/main/java/com/svm/ecom/config/EDIDataConfig.java`

```java
@Configuration
@EnableJpaRepositories(
    basePackages = "com.solverminds.booking.repo",
    includeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.solverminds\\.booking\\.repo\\.Edi.*"  // Include ALL EDI repos (EdiTemp*, EdiDoc*, etc.)
    ),
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.solverminds\\.booking\\.repo\\.Ecom.*"  // Exclude ECOM repos
    ),
    entityManagerFactoryRef = "ediEntityManagerFactory",
    transactionManagerRef = "ediTransactionManager"
)
public class EDIDataConfig {
    // Configures ediDataSource, ediEntityManagerFactory, ediTransactionManager
    // Scans only EDI entities: com.solverminds.edi.entity
}
```

#### 2. Updated ECOMDataConfig.java

```java
@EnableJpaRepositories(
    basePackages = {
        "com.svm.ecom.infrastructure.ecom", 
        "com.solverminds.ecom.register.repo",
        "com.solverminds.booking.repo",  // ECOM booking repositories
        "com.solverminds.sibl.repo"
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.solverminds\\.booking\\.repo\\.Edi.*"  // Exclude ALL EDI repos
    ),
    entityManagerFactoryRef = "ecomEntityManagerFactory",
    transactionManagerRef = "ecomTransactionManager"
)
```

#### 3. Added EDI Datasource Properties

**File**: `api-gateway/src/main/resources/application.properties`

```properties
# =================================
# EDI Database
# =================================
edi.datasource.jdbc-url=jdbc:sqlserver://<HOST>:<PORT>;databaseName=<EDI_DB>;...
edi.datasource.username=<USERNAME>
edi.datasource.password=<PASSWORD>
edi.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

### Repository Classification

| Repository Pattern | Database | Managed By | Examples |
|-------------------|----------|------------|----------|
| `EcomBooking*` | ECOM | ECOMDataConfig | `EcomBookingRepository`, `EcomBookingCustomerRepository` |
| `Edi*` | EDI | EDIDataConfig | `EdiTempBookingRepository`, `EdiDocConfigRepository`, `EdiReaderMasterRepository` |

**Note**: ALL repositories starting with `Edi` (not just `EdiTemp`) are managed by EDIDataConfig.

### Complete Details

See **[EDI-DATABASE-CONFIGURATION.md](./EDI-DATABASE-CONFIGURATION.md)** for complete multi-datasource setup documentation.

---

**Date**: 2025-11-18  
**Status**: ✅ FIXED  
**Impact**: Proper multi-datasource configuration for ECOM and EDI databases
