# EDI Integration - Implementation Summary

## Overview

This implementation enables the booking-service to process EDI (Electronic Data Interchange) temporary booking records with status 'S' (Success) and automatically insert them into the ecom booking tables. The integration leverages the existing `edi-common` module which contains all EDI entity definitions.

## What Was Implemented

### 1. Entity Scanning Configuration ✅

**File**: `BookingServiceApplication.java`

Updated the main application class to scan both booking and EDI entities:

```java
@EntityScan(basePackages = {
    "com.solverminds.booking.entity",  // Booking service entities
    "com.solverminds.edi.entity"       // EDI common entities
})
// Repository scanning is handled by ECOMDataConfig in common-security
// which includes "com.solverminds.booking.repo" with proper EntityManagerFactory configuration
```

### 2. EDI Repositories ✅

Created 8 new repository interfaces to access EDI tables:

1. **EdiTempBookingRepository**
   - `findByStatus(String status)` - Find bookings by status
   - `findByBookNo(String bookNo)` - Find booking by number
   - `findPendingSuccessfulBookings()` - Find status='S' and not yet processed

2. **EdiTempBookingCargoRepository**
   - Access cargo/container data for EDI bookings

3. **EdiTempBookingCommodityRepository**
   - Access commodity data for EDI bookings

4. **EdiTempBookingCustRepository**
   - Access customer/party data for EDI bookings

5. **EdiTempBookingRoutingRepository**
   - Access routing data for EDI bookings

6. **EdiTempBookingImoRepository**
   - Access dangerous goods (IMO) data for EDI bookings

7. **EdiDocConfigRepository**
   - Access EDI configuration data

8. **EdiReaderMasterRepository**
   - Access EDI reader master data

### 3. EDI Processing Service ✅

**File**: `EdiProcessingService.java`

Core service that handles the entire EDI to ecom booking conversion:

#### Main Method: `processEdiBookings()`

Processes all EDI temp bookings with status 'S':
- Reads all `editempbooking` records where `status = 'S'`
- For each record:
  - Creates main ecom booking
  - Processes containers/cargo
  - Processes commodities
  - Processes customers (shipper, consignee, etc.)
  - Processes routing information
  - Processes dangerous goods (if any)
  - Marks EDI record as processed (`edicleanstatus = 'P'`)
- Returns summary with success/error counts

#### Data Mapping Methods

- `createEcomBookingFromEdi()` - Maps main booking fields
- `createContainerFromEdiCargo()` - Maps cargo to containers
- `createCommodityFromEdi()` - Maps commodity details
- `createCustomerFromEdi()` - Maps customer/party information
- `createRouteFromEdi()` - Maps routing/leg information
- `createDgFromEdiImo()` - Maps dangerous goods details

### 4. REST API Endpoint ✅

**File**: `EdiController.java`

Provides REST API endpoints for EDI operations:

#### POST `/api/edi/process-bookings`

Triggers processing of EDI bookings with status 'S'.

**Authorization**: Requires `ADMIN` or `EDI_PROCESSOR` role

**Response Example**:
```json
{
  "success": true,
  "totalProcessed": 10,
  "successCount": 9,
  "errorCount": 1,
  "successBookings": ["BK001", "BK002", ...],
  "errors": ["BK005: Error message"]
}
```

#### GET `/api/edi/health`

Health check for EDI service.

**Response**:
```json
{
  "status": "UP",
  "service": "EDI Processing Service",
  "message": "EDI service is running"
}
```

#### GET `/api/edi/stats`

Get EDI processing statistics (coming soon).

## Database Tables Involved

### Source Tables (EDI Tables)
- `editempbooking` - Main EDI booking data
- `editempbooking_cargo` - Container/cargo data
- `editempbooking_commodity` - Commodity data
- `editempbooking_cust` - Customer/party data
- `editempbooking_routing` - Route/leg data
- `editempbooking_imo` - Dangerous goods data

### Target Tables (Ecom Tables)
- `ecombooking` - Main booking
- `ecombooking_cargo` - Containers
- `ecombooking_commodity` - Commodities
- `ecombooking_cust` - Customers
- `ecombooking_leg` - Routes
- `ecombooking_dg` - Dangerous goods

## Field Mappings

### Main Booking Fields

| EDI Field | Ecom Field | Notes |
|-----------|------------|-------|
| book_no | ebookno | Booking number |
| agencyid | agencyid | Agency code |
| originPortId | originPortId | Origin port |
| finalPortId | finalPortId | Final destination |
| loadPortId | loadPortId | Port of loading |
| dischargePortId | dischargePortId | Port of discharge |
| bookDate | bookDate | Booking date |
| bookReady | bookReady | Cargo ready date |
| status | ebook_status | Status (S → P) |
| custRefNo | extBookNo | External booking reference |
| agencyRefNo | agencyRefNo | Agency reference |
| shipTerms | shipTerms | Shipping terms |
| raterefNo | rateno | Rate reference number |

### Container/Cargo Fields

| EDI Field | Ecom Field | Notes |
|-----------|------------|-------|
| cargoNo | conid | Container sequence |
| eqpId | containerno | Container number |
| eqpTypeId | eqpSize | Equipment size |
| type | eqpType | Equipment type |
| grossWeight | grossWeight | Gross weight |
| tareWeight | tareWeight | Tare weight |
| cargoWeight | cargoWeight | Cargo weight |
| weightUnit | weightUnit | Weight unit |
| eqpStatus | eqpstatus | Equipment status |

### Commodity Fields

| EDI Field | Ecom Field | Notes |
|-----------|------------|-------|
| cargoNo | conid | Container ID |
| itemNo | itemno | Item sequence |
| eqpId | containerno | Container number |
| itemDesc | commodity | Commodity description |
| commodityId | hsCommodity | HS commodity code |
| packageId | package | Package type |
| itemQty | packageQty | Package quantity |
| itemVolume | cargoVolume | Volume |
| itemVolumeUnit | volumnUnit | Volume unit |
| marksNumbers | marksNo | Marks & numbers |
| marksDesc | descr | Description |

### Customer Fields

| EDI Field | Ecom Field | Notes |
|-----------|------------|-------|
| custTypeId | custTypeId | Customer type (SH, CN, etc.) |
| custId | custId | Customer code |
| name | name | Customer name |
| address1 | address1 | Address line 1 |
| address2 | address2 | Address line 2 |
| city | city | City |
| countryId | countryId | Country code |
| stateId | stateId | State code |
| zipCode | zipCode | Postal code |
| telephone | telephone | Phone number |
| fax | fax | Fax number |
| email | email | Email address |
| eori | eori | EORI number |
| persontype | persontype | Person type |

### Dangerous Goods Fields

| EDI Field | Ecom Field | Notes |
|-----------|------------|-------|
| eqpNo | conid, itemno | Equipment/item number |
| eqpId | containerno | Container number |
| unNo | unNo | UN number |
| imdgClass | class | IMDG class |
| flashPoint | FlashPt | Flash point |
| flashUnit | FlashPtUnit | Flash point unit |
| marinePollutant | MarinePollutant | Marine pollutant flag |

## Usage Instructions

### Option 1: Using REST API

#### Step 1: Authenticate
Obtain a JWT token with `ADMIN` or `EDI_PROCESSOR` role.

#### Step 2: Call the API
```bash
curl -X POST "http://localhost:8081/api/edi/process-bookings" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Step 3: Review Response
The API returns a summary of the processing:
- Total records processed
- Success count
- Error count
- List of successful booking numbers
- List of errors with details

### Option 2: Programmatic Usage

You can inject the `EdiProcessingService` into any service:

```java
@Service
public class YourService {
    
    private final EdiProcessingService ediProcessingService;
    
    public YourService(EdiProcessingService ediProcessingService) {
        this.ediProcessingService = ediProcessingService;
    }
    
    public void processEdiData() {
        Map<String, Object> result = ediProcessingService.processEdiBookings();
        
        if (Boolean.TRUE.equals(result.get("success"))) {
            Integer successCount = (Integer) result.get("successCount");
            logger.info("Successfully processed {} bookings", successCount);
        } else {
            logger.error("Processing failed: {}", result.get("message"));
        }
    }
}
```

## Processing Flow

```
1. Query EDI Tables
   ├── Find editempbooking WHERE status = 'S'
   └── For each booking found:

2. Create Main Booking
   ├── Map EDI fields to ecom fields
   ├── Insert into ecombooking
   └── Get booking number

3. Process Related Data
   ├── Process Containers
   │   ├── Query editempbooking_cargo
   │   ├── Map cargo to container fields
   │   └── Insert into ecombooking_cargo
   │
   ├── Process Commodities
   │   ├── Query editempbooking_commodity
   │   ├── Map commodity fields
   │   └── Insert into ecombooking_commodity
   │
   ├── Process Customers
   │   ├── Query editempbooking_cust
   │   ├── Map customer fields
   │   └── Insert into ecombooking_cust
   │
   ├── Process Routing
   │   ├── Query editempbooking_routing
   │   ├── Map route fields
   │   └── Insert into ecombooking_leg
   │
   └── Process Dangerous Goods (if applicable)
       ├── Query editempbooking_imo
       ├── Map DG fields
       └── Insert into ecombooking_dg

4. Mark as Processed
   ├── Update editempbooking
   ├── SET edicleanstatus = 'P'
   └── SET dateup = current timestamp

5. Return Result
   └── Return success/error summary
```

## Error Handling

The service includes comprehensive error handling:

1. **Transaction Management**:
   - Each booking is processed in its own transaction
   - If one booking fails, others continue processing

2. **Error Logging**:
   - All errors are logged with full stack traces
   - Error details are included in the response

3. **Status Tracking**:
   - Successfully processed bookings are marked with `edicleanstatus = 'P'`
   - Failed bookings remain unmarked for retry

## Testing

### Manual Testing

1. **Insert Test Data** into EDI tables:
```sql
-- Insert test booking with status 'S'
INSERT INTO editempbooking (
    book_no, agencyid, status, originPortId, finalPortId,
    loadPortId, dischargePortId, bookDate, datecr, usercr
) VALUES (
    'TEST001', 'DXB', 'S', 'AEJEA', 'USLAX',
    'AEJEA', 'USLAX', GETDATE(), GETDATE(), 'TESTUSER'
);

-- Insert test cargo
INSERT INTO editempbooking_cargo (
    book_no, cargoNo, eqpTypeId, eqpId, eqpStatus, 
    grossWeight, weightUnit
) VALUES (
    'TEST001', 1, '20GP', 'TESTU1234567', 'F', 
    15000, 'K'
);
```

2. **Call the API**:
```bash
curl -X POST "http://localhost:8081/api/edi/process-bookings" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

3. **Verify Data** in ecom tables:
```sql
SELECT * FROM ecombooking WHERE ebookno = 'TEST001';
SELECT * FROM ecombooking_cargo WHERE ebookno = 'TEST001';
```

### Automated Testing

Create integration tests:

```java
@SpringBootTest
@Transactional
public class EdiProcessingIntegrationTest {
    
    @Autowired
    private EdiProcessingService ediProcessingService;
    
    @Test
    public void testProcessEdiBookings() {
        // Insert test data
        // ...
        
        // Process bookings
        Map<String, Object> result = ediProcessingService.processEdiBookings();
        
        // Verify results
        assertTrue((Boolean) result.get("success"));
        assertEquals(1, result.get("successCount"));
    }
}
```

## Configuration

No additional configuration is required. The service uses the existing:
- Database connections (configured in `application.properties`)
- Security configuration (JWT authentication)
- Transaction management

## Monitoring

### Logging

The service logs at various levels:

```
INFO  - Processing EDI bookings with status 'S'
INFO  - Found X EDI bookings with status 'S'
DEBUG - Created ecom booking: BK001
DEBUG - Found Y cargo records for booking BK001
ERROR - Error processing EDI booking: BK002 - [error details]
```

### Metrics

Monitor the API endpoint:
- Response times
- Success/failure rates
- Number of records processed per batch

## Troubleshooting

### Common Issues

1. **No bookings found**:
   - Verify EDI tables have records with `status = 'S'`
   - Check `edicleanstatus` is NULL or not 'P'

2. **Authentication errors**:
   - Ensure JWT token has `ADMIN` or `EDI_PROCESSOR` role
   - Check token is not expired

3. **Mapping errors**:
   - Verify data types match between EDI and ecom tables
   - Check for NULL values in required fields

4. **Database errors**:
   - Check foreign key constraints
   - Verify unique key constraints
   - Ensure sufficient database permissions

## Future Enhancements

Potential improvements:

1. **Scheduled Processing**: 
   - Add cron job to automatically process EDI bookings
   - Configure processing frequency

2. **Batch Processing**: 
   - Process large volumes more efficiently
   - Add pagination support

3. **Notification System**: 
   - Email/SMS notifications on processing completion
   - Alert on errors

4. **Dashboard**: 
   - Web UI to monitor EDI processing
   - View statistics and history

5. **Retry Mechanism**: 
   - Automatic retry for failed records
   - Configurable retry attempts

6. **Data Validation**: 
   - Pre-processing validation rules
   - Business rule checks

## Support

For issues or questions:
- Check logs in `booking-service` logs directory
- Review error messages in API response
- Consult database for data integrity issues

## Version History

- **v1.0.0** (2025-11-13): Initial implementation
  - EDI to ecom booking conversion
  - REST API endpoints
  - Basic error handling and logging

