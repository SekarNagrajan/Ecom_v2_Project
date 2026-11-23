# EmsSetting Query Implementation Summary

## ✅ Final Approach: Native Query with reportJdbcTemplate

### Implementation Details

**Pattern:** Same as `BookingRepository.getBookingSummary()` - uses `reportJdbcTemplate` directly

**Files:**
- ✅ `DocumentService.java` - Uses native query
- ❌ `EmsSetting.java` - **DELETED** (not needed)
- ❌ `EmsSettingRepository.java` - **DELETED** (not needed)

---

## Code Implementation

### DocumentService.java

```java
@Service
public class DocumentService {
    
    private final EcomBookingRepository bookingRepository;
    private final JdbcTemplate reportJdbcTemplate;  // ← Injected from common-security
    
    public DocumentService(
        EcomBookingRepository bookingRepository,
        @Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.bookingRepository = bookingRepository;
        this.reportJdbcTemplate = reportJdbcTemplate;
    }
    
    private Path resolveBookingFolder(String bookingNo) throws Exception {
        // Native SQL query to ReportDB
        final String sql = "SELECT path FROM emssetting WITH (NOLOCK) " +
                          "WHERE modulename = ? AND status = ?";
        
        try {
            String basePath = reportJdbcTemplate.queryForObject(
                sql, 
                String.class, 
                "Bill of Lading",  // modulename
                "A"                // status (Active)
            );
            
            if (basePath == null || basePath.trim().isEmpty()) {
                throw new IllegalStateException("Path is empty in emssetting");
            }

            // Construct path: <basePath>/Ecom/<bookingNo>
            Path fullPath = Paths.get(basePath, "Ecom", bookingNo);
            return fullPath;
            
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalStateException(
                "Bill of Lading setting not found in emssetting table"
            );
        }
    }
}
```

---

## SQL Query

```sql
SELECT path 
FROM emssetting WITH (NOLOCK) 
WHERE modulename = 'Bill of Lading' 
  AND status = 'A'
```

**Query Parameters:**
- `modulename = 'Bill of Lading'` - Identifies the document module
- `status = 'A'` - Only active settings

**Expected Result:**
- **Windows:** `\\Share\BL_DOCS`
- **Linux:** `/mnt/share/BL_DOCS`

**Final Path Construction:**
- Upload path: `<basePath>/Ecom/<bookingNo>/`
- Example: `\\Share\BL_DOCS\Ecom\BKON1234\`

---

## Advantages of Native Query Approach

### ✅ Simplicity
- No JPA entity class needed
- No repository interface needed
- Just one service method with SQL query

### ✅ Consistency
- Matches existing pattern in `BookingRepository`
- Matches existing pattern in `AgencyLocationRepository`
- Team is already familiar with this approach

### ✅ Performance
- Direct JDBC call (no ORM overhead)
- No entity mapping/hydration
- Simple result type (String)

### ✅ Configuration
- No changes to `ReportDataConfig` needed
- No package scanning configuration
- No entity manager factory updates
- Uses existing `reportJdbcTemplate` bean

### ✅ Maintenance
- SQL queries are visible and easy to modify
- No entity-table mapping to maintain
- No Hibernate dialect issues
- Clear error messages from JDBC

---

## Comparison: JPA vs Native Query

| Aspect | JPA Entity + Repository | Native Query (Current) |
|--------|------------------------|------------------------|
| **Files Required** | 3 (Entity + Repo + Service) | 1 (Service only) |
| **Code Lines** | ~100 lines | ~30 lines |
| **Configuration** | Must update `ReportDataConfig` | None (uses existing bean) |
| **Dependencies** | Spring Data JPA | Spring JDBC (already present) |
| **Performance** | ORM overhead | Direct JDBC |
| **Debugging** | Harder (generated SQL) | Easier (explicit SQL) |
| **Maintenance** | Entity + mapping | SQL only |
| **Type Safety** | Compile-time | Runtime |
| **Flexibility** | Limited by ORM | Full SQL control |

---

## Database Verification

### Check emssetting Data
```sql
-- Verify the record exists
SELECT id, modulename, path, status 
FROM emssetting WITH (NOLOCK)
WHERE modulename = 'Bill of Lading' AND status = 'A';
```

**Expected Result:**
```
id | modulename       | path              | status
---+------------------+-------------------+-------
1  | Bill of Lading   | \\Share\BL_DOCS   | A
```

### Insert if Missing
```sql
-- Windows server
INSERT INTO emssetting (modulename, path, status)
VALUES ('Bill of Lading', '\\Share\BL_DOCS', 'A');

-- Linux server
INSERT INTO emssetting (modulename, path, status)
VALUES ('Bill of Lading', '/mnt/share/BL_DOCS', 'A');
```

---

## Error Handling

### 1. Setting Not Found
**Error:** `EmptyResultDataAccessException`
**Response:** 500 Internal Server Error
```json
{
  "statusCode": 500,
  "message": "Bill of Lading setting not found in emssetting table"
}
```

### 2. Empty Path
**Error:** `IllegalStateException`
**Response:** 500 Internal Server Error
```json
{
  "statusCode": 500,
  "message": "Path is empty in emssetting for Bill of Lading module"
}
```

### 3. Multiple Results
**Error:** `IncorrectResultSizeDataAccessException`
**Response:** 500 Internal Server Error
```json
{
  "statusCode": 500,
  "message": "Incorrect result size: expected 1, actual 2"
}
```

---

## Testing

### Unit Test
```java
@Test
void testResolveBookingFolder_Success() {
    // Mock reportJdbcTemplate
    when(reportJdbcTemplate.queryForObject(
        anyString(), 
        eq(String.class), 
        eq("Bill of Lading"), 
        eq("A")
    )).thenReturn("\\\\Share\\BL_DOCS");
    
    // Execute
    Path result = documentService.resolveBookingFolder("BKON1234");
    
    // Verify
    assertEquals("\\\\Share\\BL_DOCS\\Ecom\\BKON1234", result.toString());
}

@Test
void testResolveBookingFolder_NotFound() {
    // Mock empty result
    when(reportJdbcTemplate.queryForObject(
        anyString(), 
        eq(String.class), 
        any(), 
        any()
    )).thenThrow(new EmptyResultDataAccessException(1));
    
    // Execute & Verify
    assertThrows(IllegalStateException.class, () -> {
        documentService.resolveBookingFolder("BKON1234");
    });
}
```

### Integration Test
```java
@SpringBootTest
@Sql("/test-data/emssetting-test-data.sql")
class DocumentServiceIntegrationTest {
    
    @Autowired
    private DocumentService documentService;
    
    @Test
    void testResolveBookingFolder_IntegrationTest() {
        // Execute (queries real ReportDB)
        Path result = documentService.resolveBookingFolder("BKON1234");
        
        // Verify
        assertNotNull(result);
        assertTrue(result.toString().contains("Ecom"));
        assertTrue(result.toString().contains("BKON1234"));
    }
}
```

---

## Related Examples in Codebase

### 1. BookingRepository.getBookingSummary()
```java
public List<BookingSummaryResponseDto> getBookingSummary(String customerCode) {
    final String sql = "exec usp_getecombookingsummary ?, '', 'A', '', '', 'S'";
    return reportJdbcTemplate.query(sql, new BookingSummaryMapper(), customerCode);
}
```

### 2. AgencyLocationRepository.getAgencyCodeByPortCode()
```java
public String getAgencyCodeByPortCode(String portCode) {
    final String sql = "SELECT agencycode FROM agencylocations (nolock) " +
                      "WHERE status='A' AND portcode=?";
    List<String> results = reportJdbcTemplate.queryForList(sql, String.class, portCode);
    return results.isEmpty() ? null : results.get(0);
}
```

---

## Configuration Requirements

### 1. common-security Dependency
Ensure `pom.xml` includes:
```xml
<dependency>
    <groupId>com.svm.ecom</groupId>
    <artifactId>common-security</artifactId>
    <version>${project.version}</version>
</dependency>
```

### 2. ReportDB Connection
Ensure `application.properties` has:
```properties
report.datasource.jdbc-url=jdbc:sqlserver://[host]:[port];databaseName=ReportDB;encrypt=true;trustServerCertificate=true
report.datasource.username=[username]
report.datasource.password=[password]
report.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

### 3. reportJdbcTemplate Bean
Bean is auto-configured by `common-security` module:
- Class: `com.svm.ecom.config.ReportDataConfig`
- Bean name: `reportJdbcTemplate`
- Available for `@Autowired` or constructor injection

---

## Benefits Summary

✅ **Simpler** - Less code, fewer files  
✅ **Cleaner** - No entity/repository boilerplate  
✅ **Faster** - No ORM overhead  
✅ **Consistent** - Matches existing patterns  
✅ **Maintainable** - Clear SQL queries  
✅ **Testable** - Easy to mock JdbcTemplate  
✅ **No Config Changes** - Uses existing beans  

---

## Next Steps

1. ✅ **Code Complete** - Native query implemented
2. ✅ **Entity/Repository Removed** - Cleaned up unnecessary files
3. ⏳ **Testing** - Verify with actual ReportDB connection
4. ⏳ **Deployment** - Test in DEV/UAT environment

---

## Documentation References
- `NATIVE-QUERY-APPROACH.md` - Detailed native query documentation
- `DATABASE-SCHEMA-UPDATE.md` - Schema and query details
- `DOCUMENT-UPLOAD-API.md` - API documentation
- `VERIFY-EXISTING-SETUP.md` - Setup verification guide

