# SIBL Document Endpoints - Implementation Summary

## Overview
Successfully implemented document upload, list, and download/preview endpoints for the SIBL (Shipping Instruction and Bill of Lading) service. These endpoints follow the same pattern as the booking service document endpoints.

## Endpoints Created

### 1. Upload Documents
**POST** `/api/v1/bill-of-lading/{bookingNumber}/documents`

- Upload 1-5 files per request (max 10 MB per file, max 50 MB total request)
- File type validation (both extension and MIME type)
- Forbidden types: executables, audio, video files
- Duplicate name detection within request
- Overwrite support via query parameter
- SHA-256 checksum calculation for integrity
- OS-aware path resolution (Windows UNC vs Linux mount)

**Query Parameters:**
- `overwrite` (Boolean, default: false): Whether to overwrite existing files with the same name

**Request:**
- Content-Type: `multipart/form-data`
- Form parameter: `files` (1-5 files)

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

---

### 2. List Documents
**GET** `/api/v1/bill-of-lading/{bookingNumber}/documents`

- List all non-hidden files in booking folder
- Optional checksum calculation
- File metadata: name, size, last modified, path
- Supports both Windows and Linux paths

**Query Parameters:**
- `includeChecksum` (Boolean, default: false): Include SHA-256 checksum for each file

**Response (200 OK):**
```json
[
  {
    "fileName": "invoice.pdf",
    "sizeBytes": 123456,
    "lastModified": "2025-10-22T10:30:00"
  },
  {
    "fileName": "packing_list.xlsx",
    "sizeBytes": 89012,
    "lastModified": "2025-10-22T11:15:00",
    "checksum": "sha256:4c75e52322464a4426dcf38fb0245ab63ac80d046024b0e62ac756aacc5eaff9"
  }
]
```

---

### 3. Download/Preview Document
**GET** `/api/v1/bill-of-lading/{bookingNumber}/documents/{fileName}`

- Download or preview a specific document
- PDFs and images preview in browser (inline)
- Other files download to disk (attachment)
- Returns file content with appropriate Content-Type

**Path Parameters:**
- `bookingNumber` (String, required): The booking number (ebookno)
- `fileName` (String, required): The exact file name to download/preview

**Response:**
- **Content-Type:** Dynamically set based on file type
- **Content-Disposition:** 
  - `inline` for preview-able files (PDFs, images, text)
  - `attachment` for download-only files (archives, Office docs, etc.)
- **Body:** File content (binary stream)

---

## Files Created

### 1. DocumentService.java
**Location:** `sibl-service/src/main/java/com/solverminds/sibl/service/DocumentService.java`

- Handles document upload, list, and download operations
- Validates booking exists in report database
- Resolves folder paths from `emssetting` table
- Validates file types, sizes, and counts
- Calculates SHA-256 checksums

### 2. DocumentUploadResponse.java
**Location:** `sibl-service/src/main/java/com/solverminds/sibl/dto/DocumentUploadResponse.java`

- Response DTO for document upload operations
- Contains: fileName, sizeBytes, error (optional)

### 3. DocumentListResponse.java
**Location:** `sibl-service/src/main/java/com/solverminds/sibl/dto/DocumentListResponse.java`

- Response DTO for document listing operations
- Contains: fileName, sizeBytes, lastModified, checksum (optional)

### 4. SiblController.java (Updated)
**Location:** `sibl-service/src/main/java/com/solverminds/sibl/api/SiblController.java`

- Added three new endpoints:
  - `POST /{bookingNumber}/documents` - Upload documents
  - `GET /{bookingNumber}/documents` - List documents
  - `GET /{bookingNumber}/documents/{fileName}` - Download/preview document
- Added helper methods:
  - `determineContentType()` - Maps file extensions to MIME types
  - `isPreviewable()` - Determines if file can be previewed inline
  - `conflict()` - Creates 409 Conflict error response

---

## Information Required

### Database Configuration
The service requires access to the **Report Database** with the following:

1. **ecomSI table** - To validate SI exists
   ```sql
   SELECT * FROM ecomSI (NOLOCK) WHERE eSINo = ?
   ```

2. **emssetting table** - To resolve document folder paths
   ```sql
   SELECT path FROM emssetting WITH (NOLOCK) 
   WHERE modulename = 'Bill of Lading' AND status = 'A'
   ```

### File System Configuration
Documents are stored in the following structure:
```
<basePath from emssetting>/Ecom/<bookingNumber>/
```

The base path is retrieved from the `emssetting` table where:
- `modulename = 'Bill of Lading'`
- `status = 'A'`

### Example Paths
- **Windows:** `\\Share\BL_DOCS\Ecom\BKON6014\`
- **Linux:** `/mnt/share/BL_DOCS/Ecom/BKON6014/`

---

## Security Features

1. ✅ **JWT Authentication** - All requests require valid JWT token
2. ✅ **Customer Code Validation** - Extracted from JWT token
3. ✅ **Booking Validation** - Ensures booking exists before serving files
4. ✅ **Path Traversal Prevention** - Validates fileName for malicious paths
5. ✅ **File Type Validation** - Only serves regular files, not directories
6. ✅ **File Size Limits** - Max 10MB per file, 50MB total per request
7. ✅ **Forbidden File Types** - Blocks executables, audio, video files

---

## Example Usage

### Upload Documents
```bash
curl -X POST "http://localhost:8080/api/v1/bill-of-lading/BKON6014/documents?overwrite=false" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -F "files=@invoice.pdf" \
  -F "files=@packing_list.xlsx"
```

### List Documents
```bash
curl -X GET "http://localhost:8080/api/v1/bill-of-lading/BKON6014/documents?includeChecksum=true" \
  -H "Authorization: Bearer <your_jwt_token>"
```

### Download/Preview Document
```bash
# Preview PDF in browser
curl -X GET "http://localhost:8080/api/v1/bill-of-lading/BKON6014/documents/invoice.pdf" \
  -H "Authorization: Bearer <your_jwt_token>"

# Download Excel file
curl -X GET "http://localhost:8080/api/v1/bill-of-lading/BKON442/documents/ECOMAPI-TailwindList.xlsx" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -o ECOMAPI-TailwindList.xlsx
```

---

## Error Responses

### 400 Bad Request
- Invalid file count, size, type, or duplicate names
- Invalid booking number format

### 401 Unauthorized
- Missing or invalid JWT token
- Customer code missing in JWT

### 404 Not Found
- Booking not found in database
- File not found in booking folder

### 409 Conflict
- File already exists and overwrite=false

### 500 Internal Server Error
- Unexpected server error
- Folder creation failure

---

## Notes

1. **Booking Number Format**: The endpoints use `bookingNumber` (ebookno) as the identifier, which matches the booking service pattern.

2. **Folder Structure**: Documents are stored in the same folder structure as booking service documents (`<basePath>/Ecom/<bookingNumber>/`).

3. **Database Access**: The service uses `reportJdbcTemplate` to query the report database for booking validation and folder path resolution.

4. **File Validation**: Same validation rules as booking service:
   - Min 1 file, max 5 files per request
   - Max 10MB per file, 50MB total
   - Forbidden: exe, jar, bat, audio, video files

5. **Content Types**: Supports preview for PDFs, images, and text files. Office documents and archives are downloaded.

---

## Testing Checklist

- [ ] Upload single document
- [ ] Upload multiple documents (2-5 files)
- [ ] Upload with overwrite=true
- [ ] Upload with overwrite=false (should fail if file exists)
- [ ] List documents without checksum
- [ ] List documents with checksum
- [ ] Download PDF (should preview in browser)
- [ ] Download Excel file (should download to disk)
- [ ] Download non-existent file (should return 404)
- [ ] Upload with invalid booking number (should return 404)
- [ ] Upload forbidden file type (should return error)
- [ ] Upload file exceeding size limit (should return error)

---

## Integration with API Gateway

When accessed through the API gateway (`/ecomapi-service`), the endpoints will be:
- `POST /ecomapi-service/api/v1/bill-of-lading/{bookingNumber}/documents`
- `GET /ecomapi-service/api/v1/bill-of-lading/{bookingNumber}/documents`
- `GET /ecomapi-service/api/v1/bill-of-lading/{bookingNumber}/documents/{fileName}`

Example:
```
POST /ecomapi-service/api/v1/bill-of-lading/BKON6014/documents
GET /ecomapi-service/api/v1/bill-of-lading/BKON442/documents
GET /ecomapi-service/api/v1/bill-of-lading/BKON442/documents/ECOMAPI-TailwindList.xlsx
```
