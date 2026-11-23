# Quick Start: Document Upload Feature

## Prerequisites
- SQL Server with ReportDB database
- Booking service running (Spring Boot 2.7.x, Java 8)
- Valid JWT token for authentication
- Access to shared folder path

## 5-Minute Setup

### Step 1: Create Database Tables (2 minutes)

```sql
-- Connect to ReportDB database
USE [ReportDB];
GO

-- Run the setup script
-- Execute: setup-document-tables.sql
-- Or copy the contents from that file and run it
```

**Result:** Tables `emssetting` and `upload_idempotency` will be created with sample data.

### Step 2: Update Paths (1 minute)

Update the paths to match your environment:

```sql
-- For Windows
UPDATE emssetting 
SET setting_value = '\\YourServer\YourShare\BL_DOCS\Ecom'
WHERE setting_key = 'BL_DOCS_ECOM_WIN';

-- For Linux
UPDATE emssetting 
SET setting_value = '/your/mount/path/BL_DOCS/Ecom'
WHERE setting_key = 'BL_DOCS_ECOM_LIN';

-- Verify
SELECT * FROM emssetting WHERE setting_key LIKE 'BL_DOCS%';
```

### Step 3: Create Test Folder (1 minute)

Create a folder for testing:

**Windows:**
```cmd
mkdir \\YourServer\YourShare\BL_DOCS\Ecom\BKON1442
```

**Linux:**
```bash
mkdir -p /your/mount/path/BL_DOCS/Ecom/BKON1442
chmod 755 /your/mount/path/BL_DOCS/Ecom/BKON1442
```

### Step 4: Test Upload (1 minute)

**Using curl:**
```bash
# Upload a test file
curl -X POST "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "files=@/path/to/test.pdf"
```

**Using Postman:**
1. Method: POST
2. URL: `http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents`
3. Headers: `Authorization: Bearer YOUR_JWT_TOKEN`
4. Body: form-data
   - Key: `files` (type: File)
   - Value: Select your test file

**Expected Response (200 OK):**
```json
[
  {
    "fileName": "test.pdf",
    "sizeBytes": 12345
  }
]
```

### Step 5: Test List (30 seconds)

**Using curl:**
```bash
# List documents
curl -X GET "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Using Postman:**
1. Method: GET
2. URL: `http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents`
3. Headers: `Authorization: Bearer YOUR_JWT_TOKEN`

**Expected Response (200 OK):**
```json
[
  {
    "fileName": "test.pdf",
    "sizeBytes": 12345,
    "lastModified": "2025-10-22T10:30:00"
  }
]
```

---

## Common Issues & Quick Fixes

### ❌ "Booking folder not found on share"
**Fix:** Create the folder:
```bash
mkdir <base_path>/BKON1442
```

### ❌ "Setting not found in emssetting table"
**Fix:** Run the setup SQL script:
```bash
setup-document-tables.sql
```

### ❌ "File type not allowed: exe"
**Fix:** Use allowed file types (PDF, images, Office docs, etc.)

### ❌ "Missing or invalid JWT token"
**Fix:** Include Authorization header:
```
Authorization: Bearer YOUR_ACTUAL_JWT_TOKEN
```

### ❌ "File already exists and overwrite=false"
**Fix:** Add query parameter:
```
?overwrite=true
```

---

## Test Scenarios

### ✅ Test 1: Upload Single File
```bash
curl -X POST "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents" \
  -H "Authorization: Bearer TOKEN" \
  -F "files=@invoice.pdf"
```

### ✅ Test 2: Upload Multiple Files
```bash
curl -X POST "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents" \
  -H "Authorization: Bearer TOKEN" \
  -F "files=@invoice.pdf" \
  -F "files=@packing_list.xlsx" \
  -F "files=@certificate.jpg"
```

### ✅ Test 3: Upload with Overwrite
```bash
curl -X POST "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents?overwrite=true" \
  -H "Authorization: Bearer TOKEN" \
  -F "files=@invoice.pdf"
```

### ✅ Test 4: Upload with Idempotency
```bash
curl -X POST "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents" \
  -H "Authorization: Bearer TOKEN" \
  -H "Idempotency-Key: unique-key-123" \
  -F "files=@invoice.pdf"

# Run again with same key - should return cached response
curl -X POST "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents" \
  -H "Authorization: Bearer TOKEN" \
  -H "Idempotency-Key: unique-key-123" \
  -F "files=@invoice.pdf"
```

### ✅ Test 5: List with Checksums
```bash
curl -X GET "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents?includeChecksum=true" \
  -H "Authorization: Bearer TOKEN"
```

### ❌ Test 6: Upload Forbidden File Type
```bash
curl -X POST "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents" \
  -H "Authorization: Bearer TOKEN" \
  -F "files=@malware.exe"
# Expected: 400 Bad Request - "File type not allowed: exe"
```

### ❌ Test 7: Upload Too Many Files
```bash
curl -X POST "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents" \
  -H "Authorization: Bearer TOKEN" \
  -F "files=@file1.pdf" \
  -F "files=@file2.pdf" \
  -F "files=@file3.pdf" \
  -F "files=@file4.pdf" \
  -F "files=@file5.pdf" \
  -F "files=@file6.pdf"
# Expected: 400 Bad Request - "Maximum 5 files allowed per request"
```

---

## Swagger UI Testing

1. Open: `http://localhost:8080/ecomapi-service/swagger-ui.html`
2. Find: **Booking** section
3. Expand: **POST /api/v1/booking/{bookingId}/documents**
4. Click: **Try it out**
5. Enter:
   - bookingId: `BKON1442`
   - files: Upload your test files
   - overwrite: `false`
6. Add Authorization header in Swagger UI's "Authorize" button
7. Click: **Execute**

---

## Verification Checklist

After setup, verify these items:

- [x] Database tables created successfully
  ```sql
  SELECT * FROM emssetting;
  SELECT * FROM upload_idempotency;
  ```

- [x] Paths configured correctly
  ```sql
  SELECT setting_key, setting_value 
  FROM emssetting 
  WHERE setting_key LIKE 'BL_DOCS%';
  ```

- [x] Test folder exists with permissions
  ```bash
  # Windows
  dir \\YourServer\YourShare\BL_DOCS\Ecom\BKON1442
  
  # Linux
  ls -la /your/mount/path/BL_DOCS/Ecom/BKON1442
  ```

- [x] Upload endpoint works (returns 200)
- [x] List endpoint works (returns 200)
- [x] Files appear in shared folder
- [x] Checksums calculated correctly
- [x] Idempotency works (same key returns cached response)
- [x] Forbidden file types rejected (exe, mp3, mp4, etc.)
- [x] File size limit enforced (> 10 MB rejected)

---

## Next Steps

1. **Configure Production Paths:** Update emssetting for production environment
2. **Create Booking Folders:** Create folders for existing bookings
3. **Setup Cleanup Job:** Schedule daily cleanup of expired idempotency records
   ```sql
   -- Run daily
   EXEC usp_cleanup_expired_idempotency;
   ```
4. **Monitor Logs:** Check application logs for any issues
5. **Load Testing:** Test with concurrent uploads
6. **Backup Strategy:** Ensure shared folders are included in backup plan

---

## Support

- **Full Documentation:** See `DOCUMENT-UPLOAD-API.md`
- **Implementation Details:** See `IMPLEMENTATION-SUMMARY-DOCUMENTS.md`
- **Database Setup:** See `setup-document-tables.sql`

---

## Quick Reference

### Endpoints
```
POST   /api/v1/booking/{bookingId}/documents      # Upload
GET    /api/v1/booking/{bookingId}/documents      # List
```

### Query Parameters
```
?overwrite=true|false           # Upload: Overwrite existing files
?includeChecksum=true|false     # List: Include SHA-256 checksums
```

### Headers
```
Authorization: Bearer {JWT_TOKEN}        # Required for both
Idempotency-Key: {unique-key}           # Optional for upload
```

### File Restrictions
```
Max Files:        5 per request
Max Size:         10 MB per file, 50 MB total request
Forbidden Types:  exe, jar, bat, sh, cmd, msi, mp3, wav, mp4, avi, etc.
```

---

## Troubleshooting Commands

```sql
-- Check settings
SELECT * FROM emssetting WHERE setting_key LIKE 'BL_DOCS%';

-- Check idempotency records
SELECT * FROM upload_idempotency ORDER BY created_at DESC;

-- Count idempotency records
SELECT COUNT(*) FROM upload_idempotency;

-- Clean up expired records
EXEC usp_cleanup_expired_idempotency;

-- Delete specific idempotency key (for testing)
DELETE FROM upload_idempotency WHERE idempotency_key = 'test-key-123';
```

---

**Ready to Go!** 🚀

If you completed all steps above, your document upload feature is ready for testing!

