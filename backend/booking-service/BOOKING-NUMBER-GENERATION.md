# Booking Number Generation - JPA Implementation

## Overview
This document describes the JPA-based implementation for generating booking numbers, replacing the old EJB JDBC approach.

## Changes Made

### 1. Entity Created
**File:** `entity/EcomModuleIndex.java`

Maps to the `ecomMdlIndex` database table with the following structure:
- `mdlId` (varchar 5) - Primary Key
- `mdlPrefix` (varchar 10) - Prefix for booking numbers
- `mdlIndex` (int) - Current sequence number

### 2. Repository Created
**File:** `repo/EcomModuleIndexRepository.java`

Key features:
- Extends `JpaRepository<EcomModuleIndex, String>`
- Uses `@Lock(LockModeType.PESSIMISTIC_WRITE)` for thread-safe operations
- Method: `findByMdlIdWithLock(String mdlId)` - Retrieves module index with database-level lock

### 3. Service Updated
**File:** `service/BookingCreateService.java`

#### Method: `generateBookingNumber(BookingRequestDTO req)`

**Logic:**
1. Determines the module ID (default: "ECOM")
2. Retrieves the module index record with pessimistic write lock
3. Gets current `mdlIndex` and `mdlPrefix` values
4. Increments `mdlIndex` by 1
5. Saves the updated index back to database
6. Returns booking number: `prefix + newIndex`

**Example:**
```
Initial state in DB:
mdlId = "ECOM"
mdlPrefix = "BK"
mdlIndex = 1000

After generation:
- Database updated: mdlIndex = 1001
- Returned booking number: "BK1001"
```

## Key Improvements Over EJB Implementation

### 1. Database-Level Locking
- Uses `PESSIMISTIC_WRITE` lock instead of Java `synchronized`
- Prevents race conditions across multiple application instances
- Better for distributed/clustered environments

### 2. Cleaner Code
- No manual JDBC connection management
- No manual resource cleanup (try-catch-finally blocks)
- Spring manages transactions automatically

### 3. Type Safety
- Uses JPA entities instead of raw SQL result sets
- Compile-time type checking
- Better IDE support and refactoring

### 4. Error Handling
- Clear exception messages when module ID is not found
- Proper null handling with meaningful defaults

### 5. Testability
- Easy to mock repositories for unit testing
- No dependency on database connections in test code

## Configuration Requirements

### Database Setup
Ensure the `ecomMdlIndex` table exists with at least one record:

```sql
-- Example: Insert default module index record
INSERT INTO ecomMdlIndex (mdlId, mdlPrefix, mdlIndex)
VALUES ('ECOM', 'BK', 0);
```

### Customization
To use different module IDs based on business logic, modify the `determineModuleId()` method in `BookingCreateService`:

```java
private String determineModuleId(BookingRequestDTO req) {
    // Example: Use different prefixes based on agency
    if ("AGENCY1".equals(req.getAgencyId())) {
        return "AGN1";
    }
    return "ECOM"; // default
}
```

## Thread Safety

The implementation is thread-safe through:
1. `@Transactional` annotation on the service class
2. `PESSIMISTIC_WRITE` lock in the repository query
3. Database-level locking ensures only one transaction can update at a time

## Migration from EJB

### Old EJB Code
- Used JDBC with manual connection handling
- Java-level `synchronized` method
- Manual SQL queries and updates
- Try-catch-finally for resource cleanup

### New JPA Code
- Uses Spring Data JPA
- Database-level pessimistic locking
- Entity-based operations
- Automatic resource management

### Behavior Compatibility
The new implementation maintains the same behavior:
- Retrieves current index from database
- Increments by 1
- Updates database with new value
- Returns prefix + new index value

## Testing

### Unit Test Example
```java
@Test
void testGenerateBookingNumber() {
    // Mock the repository
    EcomModuleIndex moduleIndex = new EcomModuleIndex();
    moduleIndex.setMdlId("ECOM");
    moduleIndex.setMdlPrefix("BK");
    moduleIndex.setMdlIndex(1000);
    
    when(moduleIndexRepo.findByMdlIdWithLock("ECOM"))
        .thenReturn(Optional.of(moduleIndex));
    
    // Call the method
    BookingRequestDTO req = new BookingRequestDTO();
    String bookingNo = bookingCreateService.createMaster(req, "CUST001", "user1");
    
    // Verify
    assertEquals("BK1001", bookingNo);
    verify(moduleIndexRepo).save(argThat(e -> e.getMdlIndex() == 1001));
}
```

### Integration Test
Ensure the database has the required module index record before running integration tests.

## Performance Considerations

1. **Locking Duration**: The pessimistic lock is held only during the transaction
2. **Concurrency**: Sequential booking number generation is inherently sequential
3. **Scalability**: For high-throughput scenarios, consider:
   - Pre-allocating number ranges
   - Using database sequences (if supported)
   - Implementing a number pool pattern

## Troubleshooting

### Issue: "Module index not found"
**Solution:** Insert the required module record in `ecomMdlIndex` table

### Issue: Deadlocks
**Solution:** Ensure all transactions accessing `ecomMdlIndex` use the same lock order

### Issue: Concurrent update exceptions
**Solution:** Verify `@Transactional` annotation is present and transaction manager is configured correctly

