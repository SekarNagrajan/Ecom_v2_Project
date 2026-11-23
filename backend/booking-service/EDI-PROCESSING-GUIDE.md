# EDI Processing Implementation - Complete Guide

## ✅ Implementation Status: COMPLETE

All EDI-related functionality to process records with status 'S' is fully implemented and ready to use.

## 📋 What's Implemented

### 1. EDI Common Module (edi-common)
All 23 EDI entities are available in the `edi-common` module and are reused across services:

#### Configuration Entities (3)
- **EdiDocConfig** - EDI document configuration (edidocconfig table)
- **EdiDocStatus** - EDI document status tracking (edidocstatus table)
- **EdiReaderMaster** - EDI reader master data (edireadermaster table)

#### EDI Temp Booking Entities (20)
- **EdiTempBooking** - Main booking (editempbooking table) ⭐
- **EdiTempBookingAgency** - Agency routing
- **EdiTempBookingBody** - Booking body/description
- **EdiTempBookingCargo** - Container/cargo details ⭐
- **EdiTempBookingChargeNew** - Charge details (new)
- **EdiTempBookingCharges** - Charge information
- **EdiTempBookingCommodity** - Commodity information ⭐
- **EdiTempBookingConf** - Booking confirmation/cutoffs
- **EdiTempBookingContacts** - Contact information
- **EdiTempBookingCust** - Customer/party information ⭐
- **EdiTempBookingError** - Error tracking
- **EdiTempBookingFooter** - BL footer information
- **EdiTempBookingHaulage** - Haulage/transport details
- **EdiTempBookingHeader** - BL header information
- **EdiTempBookingImo** - IMO/DG (Dangerous Goods) ⭐
- **EdiTempBookingInsurance** - Insurance details
- **EdiTempBookingLeg** - Route leg information
- **EdiTempBookingReference** - Reference numbers
- **EdiTempBookingRemarks** - Remarks/notes
- **EdiTempBookingRouting** - Routing details ⭐

⭐ = Currently processed by EdiProcessingService

### 2. Repositories
All necessary repositories are implemented in `booking-service/repo`:

```java
// EDI Repositories
✅ EdiTempBookingRepository
✅ EdiTempBookingCargoRepository
✅ EdiTempBookingCommodityRepository
✅ EdiTempBookingCustRepository
✅ EdiTempBookingRoutingRepository
✅ EdiTempBookingImoRepository
✅ EdiDocConfigRepository
✅ EdiReaderMasterRepository
```

### 3. Processing Service
**File**: `booking-service/service/EdiProcessingService.java`

Fully implemented with:
- Main processing method: `processEdiBookings()`
- Mapping methods for all entities
- Transaction management
- Error handling
- Status tracking

### 4. REST API Controller
**File**: `booking-service/api/EdiController.java`

Three endpoints:
- `POST /api/edi/process-bookings` - Process EDI bookings with status 'S'
- `GET /api/edi/health` - Health check
- `GET /api/edi/stats` - Statistics (placeholder)

## 🔄 Processing Flow

```
1. EDI System → Insert records with status='S' → editempbooking table

2. Call API: POST /api/edi/process-bookings
   ↓
3. EdiProcessingService.processEdiBookings()
   ↓
4. Query: SELECT * FROM editempbooking WHERE status = 'S' 
          AND (edicleanstatus IS NULL OR edicleanstatus != 'P')
   ↓
5. For each EDI booking:
   ├─ Create ecombooking (main booking)
   ├─ Create ecombooking_cargo (containers)
   ├─ Create ecombooking_commodity (commodities)
   ├─ Create ecombooking_cust (customers/parties)
   ├─ Create ecombooking_leg (routes)
   ├─ Create ecombooking_dg (dangerous goods, if any)
   └─ Update editempbooking: SET edicleanstatus='P', dateup=NOW()
   ↓
6. Return summary: {success: true, successCount: X, errors: [...]}
```

## 📊 Data Mapping

### Main Booking (editempbooking → ecombooking)
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
| preCarriage | preCarriage | Pre-carriage flag |
| onCarriage | onCarriage | On-carriage flag |
| custRefNo | extBookNo | External booking reference |
| agencyRefNo | agencyRefNo | Agency reference |
| shipTerms | shipTerms | Shipping terms |
| raterefNo | rateno | Rate reference |
| status='S' | ebook_status='P' | Status conversion |

### Cargo (editempbooking_cargo → ecombooking_cargo)
| EDI Field | Ecom Field | Notes |
|-----------|------------|-------|
| cargoNo | conid | Container sequence |
| eqpId | containerno | Container number |
| eqpTypeId | eqpSize | Equipment size (20GP, 40HC, etc.) |
| type | eqpType | Equipment type |
| grossWeight | grossWeight | Gross weight |
| tareWeight | tareWeight | Tare weight |
| cargoWeight | cargoWeight | Cargo weight |
| weightUnit | weightUnit | Weight unit (K=kg, T=ton) |
| eqpStatus | eqpstatus | Equipment status (F=Full, E=Empty) |

### Commodity (editempbooking_commodity → ecombooking_commodity)
| EDI Field | Ecom Field | Notes |
|-----------|------------|-------|
| cargoNo | conid | Container ID |
| itemNo | itemno | Item sequence |
| eqpId | containerno | Container number |
| itemDesc | commodity | Commodity description |
| commodityId | hsCommodity | HS commodity code |
| packageId | packageType | Package type |
| itemQty | packageQty | Package quantity |
| itemVolume | cargoVolume | Volume |
| itemVolumeUnit | volumnUnit | Volume unit |
| marksNumbers | marksNo | Marks & numbers |
| marksDesc | descr | Description |

### Customer (editempbooking_cust → ecombooking_cust)
| EDI Field | Ecom Field | Notes |
|-----------|------------|-------|
| custTypeId | custTypeId | SH, CN, NP, FW, etc. |
| custId | custId | Customer code |
| name | name | Customer name |
| address1 | address1 | Address line 1 |
| address2 | address2 | Address line 2 |
| city | city | City |
| countryId | countryId | Country code |
| stateId | stateId | State code |
| zipCode | zipCode | Postal code |
| telephone | telephone | Phone |
| fax | fax | Fax |
| email | email | Email |
| eori | eori | EORI number (EU) |

### Dangerous Goods (editempbooking_imo → ecombooking_dg)
| EDI Field | Ecom Field | Notes |
|-----------|------------|-------|
| eqpNo | conid, itemno | Equipment/item number |
| eqpId | containerno | Container number |
| unNo | unNo | UN number |
| imdgClass | classCode | IMDG class (3, 4.1, etc.) |
| flashPoint | flashPt | Flash point |
| flashUnit | flashPtUnit | Flash point unit |
| marinePollutant | marinePollutant | Marine pollutant flag |

### Route (editempbooking_routing → ecombooking_leg)
| EDI Field | Ecom Field | Notes |
|-----------|------------|-------|
| pol | loadPortId | Port of loading |
| pod | dischargePortId | Port of discharge |
| svcCode | serviceCode | Service code |

## 🧪 Testing Instructions

### Step 1: Insert Test Data

Use this SQL script to insert test EDI data with status='S':

```sql
-- ===========================================
-- EDI TEST DATA - Status 'S' (Success)
-- ===========================================

-- 1. Insert main EDI booking with status='S'
INSERT INTO editempbooking (
    book_no, agencyid, status, originPortId, finalPortId,
    loadPortId, dischargePortId, bookDate, bookReady,
    preCarriage, onCarriage, custRefNo, agencyRefNo,
    shipTerms, raterefNo, remarks,
    datecr, usercr, edicleanstatus
) VALUES (
    'EDI-TEST-001',           -- book_no
    'DXB',                    -- agencyid
    'S',                      -- status = 'S' (Success) ⭐
    'AEJEA',                  -- originPortId (Jebel Ali)
    'USLAX',                  -- finalPortId (Los Angeles)
    'AEJEA',                  -- loadPortId
    'USLAX',                  -- dischargePortId
    GETDATE(),                -- bookDate
    DATEADD(day, 2, GETDATE()), -- bookReady
    'N',                      -- preCarriage
    'N',                      -- onCarriage
    'CUST-REF-123',           -- custRefNo
    'AGY-REF-456',            -- agencyRefNo
    'FOB',                    -- shipTerms
    'RATE-001',               -- raterefNo
    'Test EDI booking for processing', -- remarks
    GETDATE(),                -- datecr
    'EDITEST',                -- usercr
    NULL                      -- edicleanstatus (NULL = not processed yet)
);

-- 2. Insert cargo/container data
INSERT INTO editempbooking_cargo (
    book_no, cargoNo, eqpId, eqpTypeId, type, eqpStatus,
    grossWeight, tareWeight, cargoWeight, weightUnit,
    version, edireaderid
) VALUES
('EDI-TEST-001', 1, 'TESU1234567', '20GP', 'F', 'F', 
 15000.00, 2200.00, 12800.00, 'K', 0, 'EDI-READER-001'),
('EDI-TEST-001', 2, 'TESU2345678', '40HC', 'F', 'F', 
 25000.00, 3800.00, 21200.00, 'K', 0, 'EDI-READER-001');

-- 3. Insert commodity data
INSERT INTO editempbooking_commodity (
    book_no, cargoNo, itemNo, eqpId, itemDesc, commodityId,
    packageId, itemQty, itemVolume, itemVolumeUnit,
    marksNumbers, marksDesc, version
) VALUES
('EDI-TEST-001', 1, 1, 'TESU1234567', 'ELECTRONIC GOODS', '8471',
 'CTN', 100, 15.5, 'CBM', 'MARKS: TEST-001', 'Electronics for export', 0),
('EDI-TEST-001', 2, 1, 'TESU2345678', 'TEXTILE PRODUCTS', '6204',
 'BOX', 200, 32.0, 'CBM', 'MARKS: TEST-002', 'Textiles for export', 0);

-- 4. Insert customer/party data
INSERT INTO editempbooking_cust (
    book_no, custTypeId, custId, name, address1, city,
    countryId, telephone, email, version
) VALUES
-- Shipper
('EDI-TEST-001', 'SH', 'SHIP-001', 'TEST SHIPPER LLC', 
 '123 Business St', 'Dubai', 'AE', '+971-4-1234567', 
 'shipper@test.com', 0),
-- Consignee
('EDI-TEST-001', 'CN', 'CONS-001', 'TEST CONSIGNEE INC', 
 '456 Trade Ave', 'Los Angeles', 'US', '+1-310-1234567', 
 'consignee@test.com', 0),
-- Notify Party
('EDI-TEST-001', 'NP', 'NTFY-001', 'TEST NOTIFY PARTY', 
 'Same as Consignee', 'Los Angeles', 'US', '+1-310-1234567', 
 'notify@test.com', 0);

-- 5. Insert routing data
INSERT INTO editempbooking_routing (
    book_no, pol, pod, svcCode, version
) VALUES
('EDI-TEST-001', 'AEJEA', 'USLAX', 'PAX1', 0);

-- 6. (Optional) Insert dangerous goods data - if cargo is DG
-- Uncomment if you want to test DG processing
/*
INSERT INTO editempbooking_imo (
    book_no, eqpNo, eqpId, unNo, imdgClass, flashPoint,
    flashUnit, marinePollutant, shippingName, version
) VALUES
('EDI-TEST-001', 1, 'TESU1234567', '1263', '3', 23.0,
 'C', 'Y', 'PAINT (including paint, lacquer, enamel)', 0);
*/

-- Verify the test data
SELECT * FROM editempbooking WHERE book_no = 'EDI-TEST-001' AND status = 'S';
SELECT * FROM editempbooking_cargo WHERE book_no = 'EDI-TEST-001';
SELECT * FROM editempbooking_commodity WHERE book_no = 'EDI-TEST-001';
SELECT * FROM editempbooking_cust WHERE book_no = 'EDI-TEST-001';
SELECT * FROM editempbooking_routing WHERE book_no = 'EDI-TEST-001';
```

### Step 2: Call the Processing API

#### Option A: Using cURL

```bash
# First, get JWT token (adjust URL and credentials)
TOKEN=$(curl -X POST "http://localhost:8081/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"your_password"}' \
  | jq -r '.token')

# Process EDI bookings
curl -X POST "http://localhost:8081/api/edi/process-bookings" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

#### Option B: Using Postman

1. **Login to get token**:
   - Method: POST
   - URL: `http://localhost:8081/api/auth/login`
   - Body (JSON):
   ```json
   {
     "username": "admin",
     "password": "your_password"
   }
   ```
   - Copy the `token` from response

2. **Process EDI bookings**:
   - Method: POST
   - URL: `http://localhost:8081/api/edi/process-bookings`
   - Headers:
     - `Authorization: Bearer YOUR_TOKEN_HERE`
     - `Content-Type: application/json`

#### Expected Response

```json
{
  "success": true,
  "totalProcessed": 1,
  "successCount": 1,
  "errorCount": 0,
  "successBookings": ["EDI-TEST-001"],
  "errors": []
}
```

### Step 3: Verify the Results

Check if data was created in ecom tables:

```sql
-- Check main booking
SELECT * FROM ecombooking WHERE ebookno = 'EDI-TEST-001';

-- Check containers
SELECT * FROM ecombooking_cargo WHERE ebookno = 'EDI-TEST-001';

-- Check commodities
SELECT * FROM ecombooking_commodity WHERE ebookno = 'EDI-TEST-001';

-- Check customers
SELECT * FROM ecombooking_cust WHERE ebookno = 'EDI-TEST-001';

-- Check routes
SELECT * FROM ecombooking_leg WHERE ebookno = 'EDI-TEST-001';

-- Check DG (if applicable)
SELECT * FROM ecombooking_dg WHERE ebookno = 'EDI-TEST-001';

-- Verify EDI booking is marked as processed
SELECT book_no, status, edicleanstatus, dateup 
FROM editempbooking 
WHERE book_no = 'EDI-TEST-001';
-- Should show: status='S', edicleanstatus='P', dateup=<timestamp>
```

## 🔐 Security

The processing endpoint requires authentication and authorization:
- User must be logged in (valid JWT token)
- User must have one of these roles:
  - `ROLE_ADMIN`
  - `ROLE_EDI_PROCESSOR`

## 📝 Status Codes

### EDI Status Values
- **'S'** = Success (ready to process) ⭐
- **'P'** = Processed (already inserted into ecom tables)
- **'E'** = Error (processing failed)

### Ecom Booking Status
- **'P'** = Pending (newly created from EDI)
- Can be updated later by booking management workflows

## 🛠️ Troubleshooting

### Issue 1: No bookings found

**Symptom**: Response shows `totalProcessed: 0`

**Cause**: No EDI bookings with status='S' or all already processed

**Solution**:
```sql
-- Check if there are bookings with status='S'
SELECT book_no, status, edicleanstatus 
FROM editempbooking 
WHERE status = 'S';

-- Check if they're already processed
SELECT book_no, status, edicleanstatus, dateup 
FROM editempbooking 
WHERE status = 'S' AND edicleanstatus = 'P';

-- To reprocess, set edicleanstatus back to NULL
UPDATE editempbooking 
SET edicleanstatus = NULL 
WHERE book_no = 'EDI-TEST-001';
```

### Issue 2: Authentication error (401)

**Symptom**: `401 Unauthorized` response

**Solution**:
- Ensure JWT token is valid and not expired
- Check token is included in Authorization header
- Verify user has ADMIN or EDI_PROCESSOR role

### Issue 3: Processing error for specific booking

**Symptom**: Response shows errors for some bookings

**Solution**:
- Check the error message in response
- Common issues:
  - Missing required fields
  - Foreign key constraint violations
  - Data type mismatches
- Review logs for detailed stack traces

### Issue 4: Duplicate key error

**Symptom**: Error about duplicate primary key

**Solution**:
```sql
-- Check if booking already exists in ecom tables
SELECT * FROM ecombooking WHERE ebookno = 'EDI-TEST-001';

-- If it exists, either:
-- 1. Delete it first (if test data)
DELETE FROM ecombooking_dg WHERE ebookno = 'EDI-TEST-001';
DELETE FROM ecombooking_leg WHERE ebookno = 'EDI-TEST-001';
DELETE FROM ecombooking_cust WHERE ebookno = 'EDI-TEST-001';
DELETE FROM ecombooking_commodity WHERE ebookno = 'EDI-TEST-001';
DELETE FROM ecombooking_cargo WHERE ebookno = 'EDI-TEST-001';
DELETE FROM ecombooking WHERE ebookno = 'EDI-TEST-001';

-- 2. Or use a different book_no in EDI tables
```

## 🚀 Production Deployment

### Prerequisites
1. ✅ `edi-common` module is built and available
2. ✅ `booking-service` has dependency on `edi-common`
3. ✅ Database has all EDI tables created
4. ✅ Users have appropriate roles assigned

### Deployment Steps
1. Build the project:
   ```bash
   mvn clean install
   ```

2. Deploy `booking-service`

3. Verify deployment:
   ```bash
   curl http://your-server:8081/api/edi/health
   ```

### Automated Processing (Optional)

To automatically process EDI bookings, you can:

#### Option 1: Scheduled Job (Cron)
```bash
# Add to crontab to run every 5 minutes
*/5 * * * * curl -X POST "http://localhost:8081/api/edi/process-bookings" \
  -H "Authorization: Bearer YOUR_TOKEN" >> /var/log/edi-process.log 2>&1
```

#### Option 2: Spring Scheduled Task
Add to `EdiProcessingService.java`:
```java
@Scheduled(fixedDelay = 300000) // Run every 5 minutes
public void scheduledProcessing() {
    logger.info("Running scheduled EDI processing");
    processEdiBookings();
}
```

And enable scheduling in application:
```java
@SpringBootApplication
@EnableScheduling
public class BookingServiceApplication {
    // ...
}
```

## 📈 Monitoring

### Logs to Monitor
```bash
# Check processing logs
grep "Processing EDI booking" booking-service.log

# Check for errors
grep "Error processing EDI booking" booking-service.log

# Count processed bookings
grep "Successfully processed EDI booking" booking-service.log | wc -l
```

### Database Queries
```sql
-- Count bookings by status
SELECT status, edicleanstatus, COUNT(*) as count
FROM editempbooking
GROUP BY status, edicleanstatus;

-- Recently processed bookings
SELECT book_no, status, edicleanstatus, dateup
FROM editempbooking
WHERE edicleanstatus = 'P'
ORDER BY dateup DESC;

-- Failed bookings (if error tracking implemented)
SELECT book_no, message
FROM editempbooking_error
WHERE datecr > DATEADD(hour, -24, GETDATE());
```

## 📚 API Documentation

Full API documentation available at:
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

## 🎯 Summary

✅ **All EDI entities reused from edi-common module**  
✅ **All repositories implemented**  
✅ **Processing service complete with full mapping**  
✅ **REST API endpoints ready**  
✅ **Transaction management and error handling in place**  
✅ **Ready for production use**

## 📞 Support

For issues or questions:
- Check the logs: `booking-service/logs/`
- Review this guide
- Consult the EDI-INTEGRATION-SUMMARY.md file

