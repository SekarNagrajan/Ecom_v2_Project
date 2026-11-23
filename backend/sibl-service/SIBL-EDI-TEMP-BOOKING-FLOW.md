# SIBL EDI Temp Booking Insertion - Business Logic & Flow Documentation

## Overview
This document explains the business logic, working flow, and database tables involved when creating a Shipping Instruction (SI) and inserting records into the EDI temp booking tables.

## Business Flow

### 1. SI Creation Flow
```
User Request (POST /api/v1/bill-of-lading)
    ↓
SiblController.createSi()
    ↓
1. Extract JWT token & validate
2. Extract customerCode and loginName from JWT
3. Validate request body
4. Call repo.createSi() → SiCreateService.createSi()
    ↓
SiCreateService creates:
- EcomSi (main SI record)
- EcomSiCustomer (customer records)
- EcomSiContainer (container/cargo records)
- EcomSiCommodity (commodity records)
- EcomSiReefer (reefer records, if applicable)
- EcomSiDg (dangerous goods, if applicable)
- EcomSiOog (out-of-gauge, if applicable)
- EcomSiRequestMaster (request master record)
    ↓
5. Check if status = "S" (Submitted)
    ↓
6. If status = "S" AND bookingNumber is provided:
    → Process EDI Temp Booking Insertion
```

### 2. EDI Temp Booking Insertion Flow

```
IF status = "S" AND bookingNumber != null:
    ↓
1. Check if EDI temp booking already exists
   ediTempBookingRepository.findByBookNo(bookingNumber)
    ↓
2. IF EXISTS:
   → Update existing booking's edicleanstatus to "P"
   → Use updateEdiStatusSeparately()
    ↓
3. IF NOT EXISTS:
   → Create new EdiTempBooking entity
   → Populate from SIBLRequestDTO
   → Save using saveEdiTempBookingWithStatus()
   → Transaction commits automatically
```

## Database Tables Involved

### ECOM Database (Primary)
These tables are created/updated during SI creation:

1. **ecomSi** - Main SI record
2. **ecomSiCustomer** - Customer/party information
3. **ecomSiContainer** - Container/cargo details
4. **ecomSiCommodity** - Commodity information
5. **ecomSiReefer** - Reefer container details (if applicable)
6. **ecomSiDg** - Dangerous goods information (if applicable)
7. **ecomSiOog** - Out-of-gauge cargo (if applicable)
8. **ecomSiRequestMaster** - Request master record

### EDI Database (Secondary)
These tables are created/updated when status = "S":

1. **editempbooking** (dbo.editempbooking) - Main EDI temp booking record
   - **Primary Key**: `id` (auto-generated)
   - **Key Fields**:
     - `book_no` - Booking number (from SIBLRequestDTO.bookingNumber)
     - `status` - Booking status (default "D" for Draft, set to "S" if submitted)
     - `edicleanstatus` - EDI processing status (set to "P" after insertion)
     - `edireaderid` - EDI reader ID (generated from ecomMdlIndex module "EDI", same as booking: prefix + index, e.g. "EBKON1234")
     - `messagetype` - Message type (default "IFTMBF")
     - `transtype` - Transaction type (default "9")
     - `booksource` - Source (default "C" for ECOM API)
     - `messagesender` - Message sender (default "EcomOnlineBkg")
     - `carrierCode` - Carrier code (default "TSH")
     - `bookType` - Booking type (default "online")
     - `priority` - Priority (default "M")
     - `shipmentType` - Shipment type (default "NR")
     - Port information (originPortId, finalPortId, loadPortId, dischargePortId)
     - Vessel information (vesselCode, voyageCode, serviceCode)
     - Dates (bookDate, datecr)
     - User information (usercr, loginNameFromJwt)
     - ENS/T2L filing information
     - And many more fields...

## Key Code Locations

### Controller
- **File**: `sibl-service/src/main/java/com/solverminds/sibl/api/SiblController.java`
- **Method**: `createSi()` (lines ~391-475)
- **Key Methods**:
  - `createEdiTempBookingFromSIBL()` - Creates EdiTempBooking entity from DTO
  - `saveEdiTempBookingWithStatus()` - Saves with transaction management
  - `updateEdiStatusSeparately()` - Updates existing bookings

### Service
- **File**: `sibl-service/src/main/java/com/solverminds/sibl/service/SiCreateService.java`
- **Method**: `createSi()` - Creates SI records in ECOM database

### Repository
- **File**: `sibl-service/src/main/java/com/solverminds/sibl/repo/EdiTempBookingRepository.java`
- **Interface**: Extends `JpaRepository<EdiTempBooking, Integer>`
- **Key Methods**:
  - `findByBookNo(String bookNo)` - Find existing booking
  - `save(EdiTempBooking)` - Save/update booking

### Entity
- **File**: `edi-common/src/main/java/com/solverminds/edi/entity/EdiTempBooking.java`
- **Table**: `dbo.editempbooking`
- **Primary Key**: `id` (auto-generated)

## Transaction Management

### Problem Identified
The original code had a **critical issue**:
- `saveEdiTempBookingWithStatus()` was a `private` method with `@Transactional("ediTransactionManager")`
- When called from the same class (self-invocation), Spring AOP doesn't intercept it
- The transaction was not being committed properly
- **Result**: Records were not being inserted into the database

### Solution Implemented
1. **Removed `@Transactional` annotation** from private methods
2. **Injected `PlatformTransactionManager`** (ediTransactionManager) into controller
3. **Created `TransactionTemplate`** for programmatic transaction management
4. **Used `ediTransactionTemplate.execute()`** to ensure transactions are properly committed

### Transaction Flow
```
Controller Method (No transaction)
    ↓
ediTransactionTemplate.execute()
    ↓
1. Begin transaction (ediTransactionManager)
2. Execute save operation
3. Commit transaction (if successful)
4. Rollback transaction (if exception)
```

## Configuration

### Transaction Manager
- **Bean Name**: `ediTransactionManager`
- **Configuration**: `common-security/src/main/java/com/svm/ecom/config/EDIDataConfig.java`
- **Database**: EDI database (separate from ECOM database)

### DataSource Configuration
Required in `application.properties`:
```properties
edi.datasource.jdbc-url=jdbc:sqlserver://<HOST>:<PORT>;databaseName=<EDI_DATABASE>;encrypt=true;trustServerCertificate=true
edi.datasource.username=<USERNAME>
edi.datasource.password=<PASSWORD>
edi.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

## Field Mapping: SIBLRequestDTO → EdiTempBooking

| SIBLRequestDTO Field | EdiTempBooking Field | Notes |
|---------------------|---------------------|-------|
| bookingNumber | bookNo | Required |
| originPortId | originPortId | Port of origin |
| finalPortId | finalPortId | Final destination port |
| loadPortId | loadPortId | Port of loading |
| dischargePortId | dischargePortId | Port of discharge |
| vesselCode | vesselCode, oceanVslCode | Vessel information |
| voyageCode | voyageCode, oceanVoyage | Voyage information |
| serviceCode | serviceCode, oceanSerCode | Service code |
| loadBound | bound, oceanBound | Bound direction |
| groupCallId | callId | Call ID |
| ensFilling | ensfiling | ENS filing flag |
| t2lFilling | t2lfiling | T2L filing flag |
| ensType | ensType | ENS type |
| additionalRemarks | custremarks | Customer remarks |
| (generated) | edireaderid | From ecomMdlIndex module EDI (prefix + index, same as booking e.g. EBKON1234) |
| (default) | status | "D" (Draft) |
| (default) | actStatus | "O" (Open) |
| (default) | edicleanstatus | "P" (after save) |
| (default) | messagetype | "IFTMBF" |
| (default) | transtype | "9" |
| (default) | booksource | "C" |
| (default) | messagesender | "EcomOnlineBkg" |
| (default) | carrierCode | "TSH" |
| (default) | bookType | "online" |
| (default) | priority | "M" |
| (default) | shipmentType | "NR" |
| loginNameFromJwt | usercr | User who created |
| (current time) | datecr | Creation date |
| (current time) | bookDate | Booking date |

## Status Values

### SI Status (body.getStatus())
- **"D"** - Draft
- **"S"** - Submitted (triggers EDI temp booking insertion)

### EDI Clean Status (edicleanstatus)
- **null** - Not processed
- **"P"** - Processed (set after insertion)

### Act Status (actStatus)
- **"O"** - Open

## Error Handling

### Exceptions Caught
1. **NullPointerException** - If bookingNumber is null
2. **DataAccessException** - Database errors
3. **TransactionException** - Transaction errors

### Error Logging
- All errors are logged with full stack traces
- Errors don't fail the SI creation (non-blocking)
- System.out.println statements for debugging
- Logger statements for production logging

## Debugging

### System.out.println Statements
The code includes comprehensive System.out.println statements with prefixes:
- `[CONTROLLER]` - Controller layer
- `[SERVICE]` - Service layer
- `[EDI]` - EDI processing

### Logger Statements
- `logger.info()` - Information messages
- `logger.warn()` - Warning messages
- `logger.error()` - Error messages with stack traces
- `logger.debug()` - Debug messages

### Key Checkpoints
1. **Before SI creation**: `[CONTROLLER] About to call repo.createSi()...`
2. **After SI creation**: `[CONTROLLER] repo.createSi() returned successfully`
3. **Before EDI processing**: `=== Starting EDI Temp Booking Processing ===`
4. **During save**: `[EDI] Saving EDI temp booking with bookNo: ...`
5. **After save**: `[EDI] Transaction committed. Booking ID: ...`
6. **Completion**: `=== EDI Temp Booking Processing Completed ===`

## Testing Checklist

- [ ] Verify SI creation succeeds
- [ ] Verify EDI temp booking is created when status = "S"
- [ ] Verify EDI temp booking is NOT created when status != "S"
- [ ] Verify EDI temp booking is NOT created when bookingNumber is null
- [ ] Verify existing EDI temp booking is updated (not duplicated)
- [ ] Verify transaction commits successfully
- [ ] Verify edicleanstatus is set to "P" after save
- [ ] Verify ID is generated after save
- [ ] Check database to confirm record exists
- [ ] Verify error handling doesn't break SI creation

## Related Documentation

- `booking-service/EDI-DATABASE-CONFIGURATION.md` - EDI database setup
- `booking-service/EDI-PROCESSING-GUIDE.md` - EDI processing guide
- `sibl-service/SIBL-DOCUMENT-ENDPOINTS.md` - API documentation
