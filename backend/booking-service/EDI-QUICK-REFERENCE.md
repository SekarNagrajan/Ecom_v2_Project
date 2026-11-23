# EDI Processing - Quick Reference Card

## 🎯 Purpose
Process EDI temp bookings with **status='S'** and insert them into ecom booking tables.

---

## 📋 Quick Facts

| Item | Value |
|------|-------|
| **Status to Process** | `'S'` (Success) |
| **Processing Marker** | `edicleanstatus='P'` (Processed) |
| **API Endpoint** | `POST /api/edi/process-bookings` |
| **Required Role** | `ADMIN` or `EDI_PROCESSOR` |
| **Main Entity** | `EdiTempBooking` (from edi-common) |

---

## 🔄 Data Flow

```
EDI Tables (status='S')  →  API Call  →  Ecom Tables (status='P')
                                ↓
                        edicleanstatus='P'
```

---

## 📊 Table Mappings

| EDI Table | → | Ecom Table |
|-----------|---|------------|
| `editempbooking` | → | `ecombooking` |
| `editempbooking_cargo` | → | `ecombooking_cargo` |
| `editempbooking_commodity` | → | `ecombooking_commodity` |
| `editempbooking_cust` | → | `ecombooking_cust` |
| `editempbooking_routing` | → | `ecombooking_leg` |
| `editempbooking_imo` | → | `ecombooking_dg` |

---

## 🚀 Usage

### 1. Insert Test Data
```bash
sqlcmd -S server -d database -i test-edi-processing.sql
```

### 2. Get JWT Token
```bash
curl -X POST "http://localhost:8081/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

### 3. Process EDI Bookings
```bash
curl -X POST "http://localhost:8081/api/edi/process-bookings" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 4. Check Results
```sql
SELECT * FROM ecombooking WHERE ebookno = 'EDI-TEST-001';
```

---

## 📝 Common Queries

### Find Pending EDI Bookings
```sql
SELECT book_no, agencyid, originPortId, finalPortId
FROM editempbooking 
WHERE status = 'S' 
  AND (edicleanstatus IS NULL OR edicleanstatus != 'P');
```

### Check Processed Bookings
```sql
SELECT book_no, status, edicleanstatus, dateup
FROM editempbooking 
WHERE status = 'S' AND edicleanstatus = 'P'
ORDER BY dateup DESC;
```

### Verify Data Transfer
```sql
-- Count by table
SELECT 'EDI Bookings' as Source, COUNT(*) as Count 
FROM editempbooking WHERE status = 'S' AND edicleanstatus = 'P'
UNION ALL
SELECT 'Ecom Bookings', COUNT(*) 
FROM ecombooking WHERE ebook_status = 'P';
```

### Reset for Reprocessing
```sql
UPDATE editempbooking 
SET edicleanstatus = NULL 
WHERE book_no = 'EDI-TEST-001';
```

---

## ⚡ API Response

### Success
```json
{
  "success": true,
  "totalProcessed": 5,
  "successCount": 5,
  "errorCount": 0,
  "successBookings": ["BK001", "BK002", ...],
  "errors": []
}
```

### With Errors
```json
{
  "success": true,
  "totalProcessed": 5,
  "successCount": 4,
  "errorCount": 1,
  "successBookings": ["BK001", "BK002", "BK003", "BK004"],
  "errors": ["BK005: Missing required field: agencyid"]
}
```

---

## 🔧 Troubleshooting

| Problem | Solution |
|---------|----------|
| **No bookings found** | Check if records exist with `status='S'` and `edicleanstatus IS NULL` |
| **401 Unauthorized** | Verify JWT token is valid and user has correct role |
| **Duplicate key error** | Delete existing ecom booking or use different book_no |
| **Foreign key error** | Verify referenced data exists (ports, agencies, etc.) |
| **Partial success** | Check errors array in response for specific failures |

---

## 📂 Key Files

| File | Purpose |
|------|---------|
| `EdiProcessingService.java` | Main processing logic |
| `EdiController.java` | REST API endpoints |
| `EdiTempBookingRepository.java` | Data access for EDI tables |
| `test-edi-processing.sql` | Test data script |
| `EDI-PROCESSING-GUIDE.md` | Detailed documentation |

---

## 🎨 Entity Packages

```java
// EDI entities (from edi-common)
import com.solverminds.edi.entity.EdiTempBooking;
import com.solverminds.edi.entity.EdiTempBookingCargo;
import com.solverminds.edi.entity.EdiTempBookingCommodity;
import com.solverminds.edi.entity.EdiTempBookingCust;
import com.solverminds.edi.entity.EdiTempBookingRouting;
import com.solverminds.edi.entity.EdiTempBookingImo;

// Ecom entities (from booking-service)
import com.solverminds.booking.entity.EcomBooking;
import com.solverminds.booking.entity.EcomBookingContainer;
import com.solverminds.booking.entity.EcomBookingCommodity;
import com.solverminds.booking.entity.EcomBookingCustomer;
import com.solverminds.booking.entity.EcomBookingRoute;
import com.solverminds.booking.entity.EcomBookingDg;
```

---

## 🔑 Status Values

### EDI Status
- `'N'` - New (not processed by EDI system)
- `'S'` - Success (ready for ecom processing) ⭐
- `'E'` - Error (EDI processing failed)

### EDI Clean Status
- `NULL` - Not processed into ecom
- `'P'` - Processed into ecom ✅
- `'E'` - Error during processing

### Ecom Status
- `'P'` - Pending (newly created from EDI)
- `'A'` - Active
- `'C'` - Completed
- `'X'` - Cancelled

---

## 📞 Endpoints

| Method | Endpoint | Purpose | Auth |
|--------|----------|---------|------|
| `POST` | `/api/edi/process-bookings` | Process all status='S' bookings | Required |
| `GET` | `/api/edi/health` | Health check | Not required |
| `GET` | `/api/edi/stats` | Statistics (placeholder) | Required |

---

## 🎯 Processing Steps

```
1. Query: WHERE status='S' AND edicleanstatus IS NULL
   ↓
2. For each booking:
   • Create ecombooking
   • Create ecombooking_cargo
   • Create ecombooking_commodity
   • Create ecombooking_cust
   • Create ecombooking_leg
   • Create ecombooking_dg (if DG)
   ↓
3. Update: SET edicleanstatus='P'
   ↓
4. Return summary
```

---

## ⚙️ Configuration

### Application Setup
```java
@EntityScan(basePackages = {
    "com.solverminds.booking.entity",  // Ecom entities
    "com.solverminds.edi.entity"       // EDI entities
})
```

### Dependencies (pom.xml)
```xml
<dependency>
    <groupId>com.svm.ecom</groupId>
    <artifactId>edi-common</artifactId>
    <version>${project.version}</version>
</dependency>
```

---

## 📊 Monitoring Queries

### Today's Processing
```sql
SELECT 
    COUNT(*) as processed_today,
    MIN(dateup) as first_processed,
    MAX(dateup) as last_processed
FROM editempbooking
WHERE edicleanstatus = 'P'
  AND CAST(dateup AS DATE) = CAST(GETDATE() AS DATE);
```

### Processing Summary
```sql
SELECT 
    status,
    edicleanstatus,
    COUNT(*) as count,
    MAX(dateup) as last_update
FROM editempbooking
GROUP BY status, edicleanstatus
ORDER BY status, edicleanstatus;
```

### Failed Bookings
```sql
SELECT book_no, status, edicleanstatus, remarks
FROM editempbooking
WHERE status = 'S' 
  AND edicleanstatus = 'E'
ORDER BY dateup DESC;
```

---

## 📚 Documentation

| Document | Description |
|----------|-------------|
| `EDI-PROCESSING-GUIDE.md` | Complete usage guide |
| `EDI-INTEGRATION-SUMMARY.md` | Integration overview |
| `EDI-IMPLEMENTATION-COMPLETE.md` | Implementation summary |
| `EDI-QUICK-REFERENCE.md` | This quick reference |
| `test-edi-processing.sql` | Test data script |

---

## ✅ Checklist

Before Processing:
- [ ] EDI tables have records with status='S'
- [ ] JWT token is valid
- [ ] User has ADMIN or EDI_PROCESSOR role
- [ ] booking-service is running

After Processing:
- [ ] Check API response for success
- [ ] Verify records in ecom tables
- [ ] Confirm edicleanstatus='P' in EDI table
- [ ] Review logs for any errors

---

## 🎉 Quick Start (3 Steps)

```bash
# 1. Insert test data
sqlcmd -i test-edi-processing.sql

# 2. Get token and process
TOKEN=$(curl -X POST "http://localhost:8081/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"pwd"}' | jq -r '.token')

curl -X POST "http://localhost:8081/api/edi/process-bookings" \
  -H "Authorization: Bearer $TOKEN"

# 3. Verify
sqlcmd -Q "SELECT * FROM ecombooking WHERE ebookno='EDI-TEST-001'"
```

---

**Last Updated**: November 13, 2025  
**Version**: 1.0.0  
**Status**: Production Ready ✅

