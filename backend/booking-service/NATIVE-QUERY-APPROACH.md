# Native Query Approach for emssetting

## Overview
The document upload feature queries the `emssetting` table in **ReportDB** to retrieve the base document storage path. We use **native SQL queries with `reportJdbcTemplate`** instead of JPA entities/repositories.

---

## Why Native Query?

### ✅ Advantages
1. **Simpler** - No entity or repository classes needed
2. **Consistent** - Same pattern as `BookingRepository.getBookingSummary()`
3. **Performance** - Direct JDBC, no ORM overhead
4. **Flexible** - Easy to modify query without entity changes
5. **No Configuration** - Uses existing `reportJdbcTemplate` bean

### ❌ Avoided Complexity
- No need to add booking packages to `@EnableJpaRepositories`
- No need to add booking packages to `@EntityScan`
- No transaction manager conflicts
- No entity mapping maintenance

---

## Implementation

### 1. **Service Injection**
**File:** `DocumentService.java`

```java
@Service
public class DocumentService {
    
    private final JdbcTemplate reportJdbcTemplate;
    
    public DocumentService(
        EcomBookingRepository bookingRepository,
        @Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.bookingRepository = bookingRepository;
        this.reportJdbcTemplate = reportJdbcTemplate;  // ← Injected from common-security
    }
}
```

### 2. **Native Query Execution**
```java
private Path resolveBookingFolder(String bookingNo) throws Exception {
    // Native SQL query with NOLOCK hint
    final String sql = "SELECT path FROM emssetting WITH (NOLOCK) " +
                      "WHERE modulename = ? AND status = ?";
    
    try {
        // Execute query and get path directly
        String basePath = reportJdbcTemplate.queryForObject(
            sql, 
            String.class,           // Return type
            "Bill of Lading",       // param 1: modulename
            "A"                     // param 2: status
        );
        
        // Validate result
        if (basePath == null || basePath.trim().isEmpty()) {
            throw new IllegalStateException("Path is empty in emssetting");
        }

        // Construct full path: <basePath>/Ecom/<bookingNo>
        Path fullPath = Paths.get(basePath, "Ecom", bookingNo);
        
        return fullPath;
        
    } catch (EmptyResultDataAccessException e) {
        // No rows found
        throw new IllegalStateException(
            "Bill of Lading setting not found in emssetting table"
        );
    }
}
```

---

## Query Details

### SQL Query
```sql
SELECT path 
FROM emssetting WITH (NOLOCK) 
WHERE modulename = 'Bill of Lading' 
  AND status = 'A'
```

### Parameters
| Parameter | Value | Description |
|-----------|-------|-------------|
| `modulename` | `'Bill of Lading'` | Module identifier |
| `status` | `'A'` | Active status only |

### Expected Result
- **One row** with `path` column
- **Windows example:** `\\Share\BL_DOCS`
- **Linux example:** `/mnt/share/BL_DOCS`

---

## Error Handling

### 1. No Results Found
```java
catch (EmptyResultDataAccessException e) {
    throw new IllegalStateException(
        "Bill of Lading setting not found in emssetting table " +
        "(modulename='Bill of Lading', status='A')"
    );
}
```

**HTTP Response:** 500 Internal Server Error

### 2. Empty Path
```java
if (basePath == null || basePath.trim().isEmpty()) {
    throw new IllegalStateException(
        "Path is empty in emssetting for Bill of Lading module"
    );
}
```

**HTTP Response:** 500 Internal Server Error

### 3. Multiple Results
- `queryForObject()` automatically throws `IncorrectResultSizeDataAccessException`
- Wrapped in 500 error by global exception handler

---

## Database Setup

### Prerequisites
1. **ReportDB connection configured** in `application.properties`:
   ```properties
   report.datasource.jdbc-url=jdbc:sqlserver://[host]:[port];databaseName=ReportDB;...
   report.datasource.username=[username]
   report.datasource.password=[password]
   ```

2. **reportJdbcTemplate bean available** (configured in `common-security` module)

3. **emssetting table exists** in ReportDB with data:
   ```sql
   -- Verify setup
   SELECT id, modulename, path, status 
   FROM emssetting WITH (NOLOCK)
   WHERE modulename = 'Bill of Lading' AND status = 'A';
   
   -- Expected: 1 row with valid path
   ```

---

## Comparison with JPA Approach

| Aspect | Native Query (Current) | JPA Entity/Repository |
|--------|------------------------|----------------------|
| **Code Files** | 1 (service only) | 3 (entity + repository + service) |
| **Configuration** | None (uses existing bean) | Must add packages to scan |
| **Performance** | Direct JDBC | ORM overhead |
| **Maintenance** | Simple SQL updates | Entity + mapping updates |
| **Transaction Mgr** | Not needed | Must specify `@Transactional` |
| **Complexity** | Low | Medium |

---

## Testing

### Unit Test Example
```java
@Test
void testResolveBookingFolder() {
    // Arrange
    when(reportJdbcTemplate.queryForObject(
        anyString(), 
        eq(String.class), 
        eq("Bill of Lading"), 
        eq("A")
    )).thenReturn("\\\\Share\\BL_DOCS");
    
    // Act
    Path result = documentService.resolveBookingFolder("BKON1234");
    
    // Assert
    assertEquals("\\\\Share\\BL_DOCS\\Ecom\\BKON1234", result.toString());
}
```

### Integration Test
```java
@Test
@Sql("/test-data/emssetting-test-data.sql")
void testQueryEmsSettingFromReportDB() {
    // Verify reportJdbcTemplate can query ReportDB
    String path = reportJdbcTemplate.queryForObject(
        "SELECT path FROM emssetting WHERE modulename = ? AND status = ?",
        String.class,
        "Bill of Lading",
        "A"
    );
    
    assertNotNull(path);
    assertFalse(path.trim().isEmpty());
}
```

---

## Troubleshooting

### Issue: reportJdbcTemplate is null
**Cause:** `common-security` dependency not included or bean not configured

**Fix:** Verify `pom.xml` includes:
```xml
<dependency>
    <groupId>com.svm.ecom</groupId>
    <artifactId>common-security</artifactId>
    <version>${project.version}</version>
</dependency>
```

### Issue: "Bill of Lading setting not found"
**Cause:** No active record in `emssetting` table

**Fix:** Insert record:
```sql
INSERT INTO emssetting (modulename, path, status)
VALUES ('Bill of Lading', '\\Share\BL_DOCS', 'A');
```

### Issue: Path is empty
**Cause:** `path` column is NULL or empty string

**Fix:** Update record:
```sql
UPDATE emssetting 
SET path = '\\Share\BL_DOCS'  -- Windows
-- or path = '/mnt/share/BL_DOCS'  -- Linux
WHERE modulename = 'Bill of Lading' AND status = 'A';
```

---

## References
- `BookingRepository.getBookingSummary()` - Similar native query pattern
- `AgencyLocationRepository.getAgencyCodeByPortCode()` - Another native query example
- `ReportDataConfig.reportJdbcTemplate()` - Bean configuration

