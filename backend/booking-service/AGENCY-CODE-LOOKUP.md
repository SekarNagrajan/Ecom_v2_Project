# Agency Code Lookup from ReportDB

## Overview
When creating a booking, the agency code is automatically fetched from the ReportDB `agencylocations` table based on the origin port code, ensuring accurate agency assignment for each booking.

## Implementation Details

### Database Query
The system queries the `agencylocations` table in ReportDB with the following criteria:
- **Table**: `agencylocations` (with NOLOCK hint)
- **Filter**: `status='A'` (Active agencies only)
- **Lookup**: `portcode = originPortId`

### Query SQL
```sql
SELECT agencycode 
FROM agencylocations (nolock) 
WHERE status='A' AND portcode=?
```

## Components Created/Modified

### 1. AgencyLocationRepository
**File**: `repo/AgencyLocationRepository.java`

**Purpose**: Provides data access to the `agencylocations` table in ReportDB

**Key Method**: `getAgencyCodeByPortCode(String portCode)`
- Queries ReportDB for active agency code
- Returns first matching agency code
- Returns `null` if no match found
- Logs warnings for multiple matches

**Features**:
- Uses `reportJdbcTemplate` (injected via `@Qualifier`)
- SQL Server NOLOCK hint for read performance
- Null safety checks
- Comprehensive logging

### 2. BookingCreateService
**File**: `service/BookingCreateService.java`

**Changes**:
- Injected `AgencyLocationRepository` dependency
- Updated `createMaster()` method to fetch agency code from ReportDB

**Logic Flow**:
```java
// 1. Fetch agency code from ReportDB based on origin port
String agencyCode = agencyLocationRepo.getAgencyCodeByPortCode(req.getOriginPortId());

// 2. Fallback to request value if not found
if (agencyCode == null || agencyCode.trim().isEmpty()) {
    agencyCode = req.getAgencyId();
}

// 3. Use agency code in booking entity
entity.setAgencyid(agencyCode);
```

## Usage Example

### Scenario 1: Agency Found in ReportDB
```
Request:
- originPortId: "INNSA" (Nhava Sheva)
- agencyId: "DUMMY" (from request)

ReportDB Query Result:
- agencycode: "INDMUM"

Result:
✓ Agency code "INDMUM" is used in the booking
✓ Request value "DUMMY" is ignored
```

### Scenario 2: Agency Not Found (Fallback)
```
Request:
- originPortId: "XXXXX" (Unknown port)
- agencyId: "AGENCY123" (from request)

ReportDB Query Result:
- No records found

Result:
✓ Fallback to request value
✓ Agency code "AGENCY123" is used in the booking
⚠ Warning logged: "No active agency found for port code: XXXXX"
```

### Scenario 3: Multiple Agencies Found
```
Request:
- originPortId: "AEJEA" (Jebel Ali)

ReportDB Query Result:
- agencycode: "AEJEA1"
- agencycode: "AEJEA2"

Result:
✓ First agency code "AEJEA1" is used
⚠ Warning logged: "Multiple agencies found for port AEJEA, using first: AEJEA1"
```

## Error Handling

### Database Connection Issues
If ReportDB is unavailable or query fails:
- Exception is caught and logged
- Returns `null`
- Fallback to request value

### Invalid Port Code
If port code is `null` or empty:
- Warning is logged
- Returns `null`
- Fallback to request value

### No Matching Agency
If no active agency found for port:
- Warning is logged
- Returns `null`
- Fallback to request value

## Benefits

### 1. Data Consistency
- Agency codes are always sourced from the master data (ReportDB)
- Eliminates manual data entry errors
- Ensures valid agency-port relationships

### 2. Automatic Updates
- Changes in ReportDB are immediately reflected
- No code changes needed for new agencies
- Centralized master data management

### 3. Fallback Mechanism
- Graceful degradation if ReportDB is unavailable
- Still accepts agency code from request as backup
- System remains operational during issues

### 4. Audit Trail
- Comprehensive logging at DEBUG and WARN levels
- Easy troubleshooting of agency lookup issues
- Performance monitoring capabilities

## Database Setup Requirements

### ReportDB Table Structure
Ensure the `agencylocations` table exists in ReportDB:

```sql
-- Verify table structure
SELECT TOP 1 * FROM agencylocations;

-- Required columns:
-- portcode (varchar) - Port code identifier
-- agencycode (varchar) - Agency code for the port
-- status (char) - Status flag ('A' = Active)
```

### Sample Data
```sql
-- Example active agency locations
SELECT portcode, agencycode, status 
FROM agencylocations 
WHERE status = 'A'
ORDER BY portcode;

-- Example result:
-- portcode | agencycode | status
-- INNSA    | INDMUM     | A
-- AEJEA    | AEJEA1     | A
-- SGSIN    | SGSIN1     | A
```

## Configuration

### ReportDB Connection
The ReportDB connection is configured in `common-security` module:
- Bean name: `reportJdbcTemplate`
- Configuration prefix: `report.datasource`
- Database: ReportDB (SQL Server)

### Application Properties
Ensure these properties are set in your environment:

```properties
report.datasource.jdbc-url=jdbc:sqlserver://[host]:[port];databaseName=ReportDB;encrypt=true;trustServerCertificate=true
report.datasource.username=[username]
report.datasource.password=[password]
report.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

## Logging

### Log Levels

**DEBUG**: Successful lookups
```
Fetching agency code for port: INNSA
Found agency code: INDMUM for port: INNSA
```

**WARN**: No results or multiple results
```
No active agency found for port code: XXXXX
Multiple agencies found for port AEJEA, using first: AEJEA1
Port code is null or empty, cannot fetch agency code
```

**ERROR**: Database or system errors
```
Error fetching agency code for port: INNSA
```

### Enable Debug Logging
To see all agency lookup operations:

```properties
logging.level.com.solverminds.booking.repo.AgencyLocationRepository=DEBUG
```

## Testing

### Unit Test Example
```java
@Test
void testAgencyCodeLookup() {
    // Mock reportJdbcTemplate
    when(reportJdbcTemplate.query(
        anyString(), 
        any(PreparedStatementSetter.class), 
        any(RowMapper.class)
    )).thenReturn(Arrays.asList("INDMUM"));
    
    // Test the method
    String agencyCode = agencyLocationRepo.getAgencyCodeByPortCode("INNSA");
    
    // Verify
    assertEquals("INDMUM", agencyCode);
}

@Test
void testFallbackWhenNotFound() {
    // Mock empty result
    when(reportJdbcTemplate.query(...)).thenReturn(Collections.emptyList());
    
    BookingRequestDTO req = new BookingRequestDTO();
    req.setOriginPortId("XXXXX");
    req.setAgencyId("FALLBACK123");
    
    // Create booking
    String bookNo = bookingCreateService.createMaster(req, "CUST001", "user1");
    
    // Verify fallback was used
    EcomBooking booking = bookingRepo.findByEbookno(bookNo).get();
    assertEquals("FALLBACK123", booking.getAgencyid());
}
```

### Integration Test
Ensure ReportDB has test data:
```sql
-- Insert test agency location
INSERT INTO agencylocations (portcode, agencycode, status)
VALUES ('TEST01', 'TESTAGENCY', 'A');
```

## Performance Considerations

### Query Optimization
- Uses NOLOCK hint for read-only queries
- No transaction overhead
- Direct SQL query (no ORM overhead)
- Indexed portcode column recommended

### Caching (Future Enhancement)
Consider adding cache for frequently accessed ports:
```java
@Cacheable(value = "agencyCodes", key = "#portCode")
public String getAgencyCodeByPortCode(String portCode) {
    // Implementation
}
```

## Troubleshooting

### Issue: Always getting fallback value
**Symptoms**: Agency code from request is always used, never from ReportDB

**Possible Causes**:
1. ReportDB connection not configured
2. `agencylocations` table doesn't exist
3. No active records for the port (`status != 'A'`)
4. Port code case mismatch

**Solution**:
```sql
-- Check if port exists with different case or status
SELECT * FROM agencylocations WHERE portcode LIKE '%INNSA%';
SELECT * FROM agencylocations WHERE UPPER(portcode) = 'INNSA';
SELECT * FROM agencylocations WHERE portcode = 'INNSA' AND status = 'A';
```

### Issue: Slow booking creation
**Symptoms**: Booking creation takes longer than expected

**Possible Causes**:
1. ReportDB connection timeout
2. Missing index on portcode column
3. Network latency to ReportDB

**Solution**:
```sql
-- Add index if missing
CREATE INDEX idx_agencylocations_portcode 
ON agencylocations(portcode) 
WHERE status = 'A';

-- Check query performance
SET STATISTICS TIME ON;
SELECT agencycode FROM agencylocations (nolock) 
WHERE status='A' AND portcode='INNSA';
SET STATISTICS TIME OFF;
```

### Issue: Multiple agencies warning in logs
**Symptoms**: Warning about multiple agencies for a port

**Solution**:
```sql
-- Find duplicate entries
SELECT portcode, COUNT(*) as count
FROM agencylocations
WHERE status = 'A'
GROUP BY portcode
HAVING COUNT(*) > 1;

-- Review and clean up duplicates in ReportDB
```

## Migration Notes

### From Old EJB Code
The old EJB code might have used:
- Request-provided agency code only
- Or manual agency lookup with JDBC

### Benefits of New Implementation
✓ Automatic agency lookup from master data  
✓ Consistent with ReportDB as source of truth  
✓ Proper fallback mechanism  
✓ Better error handling and logging  
✓ Uses Spring's dependency injection  
✓ Easier to test and maintain  

## Related Features

- **Booking Number Generation**: See [BOOKING-NUMBER-GENERATION.md](BOOKING-NUMBER-GENERATION.md)
- **Module Index Setup**: See [setup-module-index.sql](setup-module-index.sql)
- **ReportDB Configuration**: See `common-security` module

## Future Enhancements

1. **Caching**: Add Spring Cache for frequently accessed ports
2. **Validation**: Validate port-agency relationship before booking creation
3. **Multi-agency**: Support multiple agencies per port with selection logic
4. **Audit**: Track which agency code was used (ReportDB vs fallback)
5. **API**: Expose agency lookup as a separate API endpoint for validation

