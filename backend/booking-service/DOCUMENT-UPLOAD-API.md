# Booking Document Upload API

## Overview

This feature allows uploading and listing documents for bookings. Documents are stored directly in a shared folder structure, with the path configured in the `emssetting` table and automatically selected based on the OS.

## Endpoints

### 1. Upload Documents

**Endpoint:** `POST /api/v1/booking/{bookingId}/documents`

**Description:** Upload 1-5 documents (max 10MB per file, max 50MB total request) to the booking's shared folder.

**Path Parameters:**
- `bookingId` (String, required): The booking number (ebookno)

**Query Parameters:**
- `overwrite` (Boolean, default: false): Whether to overwrite existing files with the same name

**Headers:**
- `Authorization` (required): Bearer JWT token
- `Idempotency-Key` (optional): Unique key to ensure idempotent uploads (cached for 24 hours)

**Request:**
- Content-Type: `multipart/form-data`
- Form parameter: `files` (1-5 files)

**Validations:**
- Minimum 1 file, maximum 5 files per request
- Each file max size: 10 MB
- Max total request size: 50 MB
- No duplicate file names in the same request
- File type restrictions (forbidden extensions and MIME types):
  - **Executables:** exe, jar, bat, dat, sh, cmd, msi
  - **Audio:** mp3, wav, aac, wma, flac
  - **Video:** mp4, avi, mkv, mov, wmv, 3gp
- If `overwrite=false` and file exists → 409 Conflict

**Response (200 OK):**
```json
[
  {
    "fileName": "invoice.pdf",
    "sizeBytes": 123456
  },
  {
    "fileName": "rejected.exe",
    "error": "File type not allowed: exe"
  }
]
```

**Error Responses:**
- `400 Bad Request`: Invalid file count, size, type, or duplicate names
- `401 Unauthorized`: Missing or invalid JWT token
- `404 Not Found`: Booking not found or folder doesn't exist on share
- `409 Conflict`: File already exists and overwrite=false
- `500 Internal Server Error`: Unexpected error

---

### 2. List Documents

**Endpoint:** `GET /api/v1/booking/{bookingId}/documents`

**Description:** List all non-hidden documents in the booking's shared folder.

**Path Parameters:**
- `bookingId` (String, required): The booking number (ebookno)

**Query Parameters:**
- `includeChecksum` (Boolean, default: false): Include SHA-256 checksums for each file

**Headers:**
- `Authorization` (required): Bearer JWT token

**Response (200 OK):**
```json
[
  {
    "fileName": "invoice.pdf",
    "sizeBytes": 123456,
    "lastModified": "2025-10-22T10:30:00",
    "checksum": "sha256:abc123..."
  },
  {
    "fileName": "packing_list.xlsx",
    "sizeBytes": 89012,
    "lastModified": "2025-10-22T11:15:00"
  }
]
```

**Error Responses:**
- `400 Bad Request`: Invalid booking ID
- `401 Unauthorized`: Missing or invalid JWT token
- `404 Not Found`: Booking not found or folder doesn't exist on share
- `500 Internal Server Error`: Unexpected error

---

## Configuration

### Database Tables

#### 1. `emssetting`
Stores system-wide settings including document paths.

**Columns:**
- `setting_key` (VARCHAR(100), PK): Setting identifier
- `setting_value` (VARCHAR(500)): Setting value
- `description` (VARCHAR(255)): Setting description
- `created_at`, `updated_at`: Timestamps

**Required Settings:**
- `BL_DOCS_ECOM_WIN`: UNC path for Windows (e.g., `\\Share\BL_DOCS\Ecom`)
- `BL_DOCS_ECOM_LIN`: Mount path for Linux (e.g., `/mnt/share/BL_DOCS/Ecom`)

#### 2. `upload_idempotency`
Tracks idempotent upload requests.

**Columns:**
- `idempotency_key` (VARCHAR(255), PK): Client-provided idempotency key
- `booking_id` (VARCHAR(20)): Associated booking number
- `response_payload` (VARCHAR(4000)): Cached response JSON
- `created_at`, `expires_at`: Timestamps (24-hour expiry)

**Indexes:**
- IX_upload_idempotency_booking_id (on booking_id)
- IX_upload_idempotency_expires_at (on expires_at)

### Folder Structure

Documents are stored in:
- **Windows:** `<BL_DOCS_ECOM_WIN>\<bookingNo>\`
  - Example: `\\Share\BL_DOCS\Ecom\BKON1442\`
- **Linux:** `<BL_DOCS_ECOM_LIN>/<bookingNo>/`
  - Example: `/mnt/share/BL_DOCS/Ecom/BKON1442/`

**Important Notes:**
- Folders must exist before upload (API does NOT create them)
- Application must have read/write permissions
- OS is auto-detected: `System.getProperty("os.name")`

---

## Setup Instructions

### 1. Database Setup

Run the SQL script to create tables and insert sample settings:

```bash
# Execute setup-document-tables.sql on your ReportDB database
sqlcmd -S <server> -d ReportDB -i setup-document-tables.sql
```

### 2. Update Paths

Update the path values in `emssetting` table to match your environment:

```sql
-- Update Windows path
UPDATE emssetting 
SET setting_value = '\\YourShare\YourPath\Ecom'
WHERE setting_key = 'BL_DOCS_ECOM_WIN';

-- Update Linux path
UPDATE emssetting 
SET setting_value = '/your/mount/path/Ecom'
WHERE setting_key = 'BL_DOCS_ECOM_LIN';
```

### 3. Create Folder Structure

Ensure the base folder exists with proper permissions:

**Windows:**
```cmd
mkdir \\YourShare\YourPath\Ecom
# Grant read/write permissions to application service account
```

**Linux:**
```bash
mkdir -p /your/mount/path/Ecom
chmod 755 /your/mount/path/Ecom
# Ensure application user has write permissions
```

### 4. Create Booking Folders

For each booking, create a subfolder:

```cmd
# Windows
mkdir \\YourShare\YourPath\Ecom\BKON1442

# Linux
mkdir -p /your/mount/path/Ecom/BKON1442
```

**Note:** The API expects these folders to exist. If missing, it returns `404 Not Found`.

---

## Testing

### Upload Documents

```bash
curl -X POST "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents?overwrite=false" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Idempotency-Key: unique-request-id-12345" \
  -F "files=@/path/to/invoice.pdf" \
  -F "files=@/path/to/packing_list.xlsx"
```

### List Documents

```bash
curl -X GET "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents?includeChecksum=true" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Maintenance

### Cleanup Expired Idempotency Records

Run the cleanup stored procedure periodically (e.g., daily via scheduled job):

```sql
EXEC usp_cleanup_expired_idempotency;
```

Or schedule it:

```sql
-- Create SQL Server Agent job to run daily at 2 AM
USE msdb;
GO
EXEC sp_add_job @job_name = 'Cleanup Expired Upload Idempotency';
-- Add job steps and schedule as needed
```

---

## Security Considerations

1. **JWT Authentication**: All endpoints require valid JWT token
2. **File Type Validation**: Both extension and MIME type are checked
3. **Size Limits**: 10 MB per file, max 50 MB total request, max 5 files per request
4. **Path Traversal Protection**: Files are saved only within the designated booking folder
5. **Idempotency**: Prevents duplicate uploads with same key within 24 hours
6. **Permissions**: Ensure application has minimal necessary file system permissions

---

## Troubleshooting

### "Booking folder not found on share"
- Verify the folder exists: `<base_path>/<bookingNo>/`
- Check application has read permissions on the folder

### "File already exists and overwrite=false"
- Set `overwrite=true` query parameter to replace existing file
- Or use a different file name

### "Setting not found in emssetting table"
- Ensure `BL_DOCS_ECOM_WIN` or `BL_DOCS_ECOM_LIN` exists in `emssetting`
- Run the database setup script

### "File type not allowed"
- Check the forbidden extensions and MIME types list
- Use allowed file types (PDF, images, Office documents, etc.)

---

## Architecture Notes

### Components Created

1. **Entities:**
   - `EmsSetting`: System settings
   - `UploadIdempotency`: Idempotency tracking

2. **Repositories:**
   - `EmsSettingRepository`
   - `UploadIdempotencyRepository`

3. **DTOs:**
   - `DocumentUploadResponse`: Upload response
   - `DocumentListResponse`: List response

4. **Service:**
   - `DocumentService`: Core business logic for upload/list operations

5. **Controller:**
   - `BookingController`: Added two new endpoints

### Key Features

- **OS Detection**: Automatically selects Windows or Linux path
- **File Validation**: Extension + MIME type checks
- **Checksum Calculation**: SHA-256 for integrity verification
- **Idempotency Support**: Prevents duplicate uploads
- **Error Handling**: Clear error messages for all validation failures
- **Logging**: Comprehensive logging for debugging
- **Swagger Documentation**: Auto-generated API docs

---

## API Versioning

Current version: **v1**

Future enhancements may include:
- Automatic folder creation
- Document metadata storage in database
- Document versioning
- Bulk download
- Document preview/thumbnail generation

