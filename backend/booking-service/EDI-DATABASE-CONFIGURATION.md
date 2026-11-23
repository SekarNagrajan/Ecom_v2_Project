# EDI Database Configuration - Multi-Datasource Setup

## Overview

The EDI (Electronic Data Interchange) tables reside in a **separate database** from the ECOM tables. This requires a proper multi-datasource Spring Boot configuration to manage both databases independently.

## Database Architecture

```
┌─────────────────────────┐          ┌─────────────────────────┐
│   ECOM Database         │          │   EDI Database          │
│   (Ecomm_JSF)           │          │   (EDI_DATABASE_NAME)   │
├─────────────────────────┤          ├─────────────────────────┤
│ • ecombooking           │          │ • editempbooking        │
│ • ecombooking_cargo     │          │ • editempbooking_cargo  │
│ • ecombooking_commodity │          │ • editempbooking_commodity │
│ • ecombooking_cust      │          │ • editempbooking_cust   │
│ • ecombooking_leg       │          │ • editempbooking_routing│
│ • ecombooking_dg        │          │ • editempbooking_imo    │
│ • ports, agency, etc.   │          │ • edidocconfig          │
└─────────────────────────┘          └─────────────────────────┘
         ↑                                    ↑
         │                                    │
    ECOMDataConfig                      EDIDataConfig
```

## Configuration Files

### 1. ECOMDataConfig.java

**Location**: `common-security/src/main/java/com/svm/ecom/config/ECOMDataConfig.java`

**Purpose**: Manages ECOM database connection and ECOM-related repositories

**Key Features**:
- Configures `ecomDataSource` for ECOM database
- Scans ECOM entities: `com.solverminds.booking.entity` (EcomBooking*)
- Scans ECOM repositories: `com.solverminds.booking.repo` (EcomBooking*)
- **Excludes** ALL EDI repositories using filter: `Edi.*` (matches EdiTemp*, EdiDoc*, etc.)

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

### 2. EDIDataConfig.java (NEW)

**Location**: `common-security/src/main/java/com/svm/ecom/config/EDIDataConfig.java`

**Purpose**: Manages EDI database connection and EDI-related repositories

**Key Features**:
- Configures `ediDataSource` for EDI database
- Scans EDI entities: `com.solverminds.edi.entity` (from edi-common module)
- Scans EDI repositories: All `Edi*` repositories (EdiTemp*, EdiDoc*, etc.)
- **Includes** only EDI repositories using filter: `Edi.*`
- **Excludes** ECOM repositories using filter: `Ecom.*`

```java
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
```

## Repository Classification

### ECOM Repositories (Managed by ECOMDataConfig)
- `EcomBookingRepository`
- `EcomBookingContainerRepository`
- `EcomBookingCommodityRepository`
- `EcomBookingCustomerRepository`
- `EcomBookingRouteRepository`
- `EcomBookingDgRepository`
- `EcomBookingOogRepository`
- `EcomBookingReeferRepository`

### EDI Repositories (Managed by EDIDataConfig)
All repositories starting with `Edi*`:
- `EdiTempBookingRepository`
- `EdiTempBookingCargoRepository`
- `EdiTempBookingCommodityRepository`
- `EdiTempBookingCustRepository`
- `EdiTempBookingRoutingRepository`
- `EdiTempBookingImoRepository`
- `EdiDocConfigRepository` ⭐
- `EdiReaderMasterRepository` ⭐
- And any other `Edi*` repositories

⭐ = Not `EdiTemp*` pattern but still EDI repositories

## Application Properties Configuration

### Required Properties

Add these to your `application.properties`:

```properties
# =================================
# EDI Database Configuration
# =================================
edi.datasource.jdbc-url=jdbc:sqlserver://<HOST>:<PORT>;databaseName=<EDI_DATABASE_NAME>;encrypt=true;trustServerCertificate=true
edi.datasource.username=<USERNAME>
edi.datasource.password=<PASSWORD>
edi.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

### Example Configuration

```properties
# Local Environment
edi.datasource.jdbc-url=jdbc:sqlserver://192.168.11.219:1433;databaseName=EDI;encrypt=true;trustServerCertificate=true
edi.datasource.username=Hkdemojboss
edi.datasource.password=mIargtionbSSj876
edi.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

## Transaction Management

### ECOM Transactions
```java
@Transactional("ecomTransactionManager")
public void processEcomBooking() {
    // Operations on ECOM database
    ecomBookingRepository.save(...);
}
```

### EDI Transactions
```java
@Transactional("ediTransactionManager")
public void processEdiBooking() {
    // Operations on EDI database
    ediTempBookingRepository.save(...);
}
```

### Cross-Database Transactions
For operations spanning both databases (e.g., EDI processing that reads from EDI and writes to ECOM):

```java
@Transactional("ecomTransactionManager")  // Primary transaction
public void processEdiToEcom() {
    // Read from EDI (read-only, doesn't need transaction)
    List<EdiTempBooking> ediBookings = ediTempBookingRepository.findByStatus("S");
    
    // Write to ECOM (transactional)
    for (EdiTempBooking edi : ediBookings) {
        EcomBooking ecom = mapToEcom(edi);
        ecomBookingRepository.save(ecom);
    }
    
    // Update EDI status (separate transaction)
    updateEdiStatusSeparately(ediBookings);
}

@Transactional("ediTransactionManager")
private void updateEdiStatusSeparately(List<EdiTempBooking> bookings) {
    // Update EDI records
    for (EdiTempBooking booking : bookings) {
        booking.setEdicleanstatus("P");
        ediTempBookingRepository.save(booking);
    }
}
```

## Beans Created

### EDI Database Beans
- `ediDataSource` - DataSource for EDI database
- `ediEntityManagerFactory` - EntityManagerFactory for EDI entities
- `ediTransactionManager` - TransactionManager for EDI operations
- `ediJdbcTemplate` - JdbcTemplate for raw SQL on EDI database

### ECOM Database Beans  
- `ecomDataSource` - DataSource for ECOM database (Primary)
- `ecomEntityManagerFactory` - EntityManagerFactory for ECOM entities (Primary)
- `ecomTransactionManager` - TransactionManager for ECOM operations
- `ecomJdbcTemplate` - JdbcTemplate for raw SQL on ECOM database (Primary)

## Troubleshooting

### Issue: "Not a managed type: class EdiTempBooking"
**Cause**: EDI entities not scanned or wrong datasource  
**Fix**: Ensure `EDIDataConfig` is in component scan path and EDI datasource is configured

### Issue: "Cannot register bean 'ediTempBookingRepository'"  
**Cause**: Duplicate repository registration  
**Fix**: Check excludeFilters/includeFilters in both configs

### Issue: Wrong database connection for EDI repos
**Cause**: EDI repositories using ECOM datasource  
**Fix**: Verify `entityManagerFactoryRef = "ediEntityManagerFactory"` in `EDIDataConfig`

## Verification

### 1. Check Bean Registration
```bash
# Start application with debug logging
java -jar app.jar --debug

# Look for:
# - Creating bean 'ediDataSource'
# - Creating bean 'ediEntityManagerFactory'
# - Creating bean 'ediTransactionManager'
```

### 2. Test EDI Repository Access
```java
@Autowired
private EdiTempBookingRepository ediRepo;

@Test
public void testEdiConnection() {
    List<EdiTempBooking> bookings = ediRepo.findByStatus("S");
    assertNotNull(bookings);
}
```

### 3. Verify Correct Database
```sql
-- Run on EDI database - should return results
SELECT COUNT(*) FROM editempbooking;

-- Run on ECOM database - should return results  
SELECT COUNT(*) FROM ecombooking;
```

## Migration Checklist

- [ ] Create `EDIDataConfig.java` in `common-security/src/main/java/com/svm/ecom/config/`
- [ ] Update `ECOMDataConfig.java` with `excludeFilters`
- [ ] Add EDI datasource properties to `application.properties`
- [ ] Update all environment-specific properties files (local, UAT, prod)
- [ ] Remove `@EnableJpaRepositories` from `BookingServiceApplication` (if present)
- [ ] Test application startup - no bean conflicts
- [ ] Test EDI repository access
- [ ] Test ECOM repository access
- [ ] Test EDI processing service

---

**Date**: 2025-11-18  
**Status**: ✅ IMPLEMENTED  
**Impact**: Proper multi-datasource configuration for EDI and ECOM databases

