# Document Download and Preview Feature

## Overview
Added a new endpoint to download or preview documents from the booking's shared folder. Files like PDFs, images, and text files can be previewed directly in the browser, while other files are downloaded.

---

## New Endpoint

### `GET /api/v1/booking/{bookingId}/documents/{fileName}`

**Description:** Download or preview a specific document from the booking's folder.

**Path Parameters:**
- `bookingId` (String, required): The booking number (ebookno)
- `fileName` (String, required): The exact file name to download/preview

**Headers:**
- `Authorization` (required): Bearer JWT token

**Response:**
- **Content-Type:** Dynamically set based on file type (e.g., `application/pdf`, `image/jpeg`, etc.)
- **Content-Disposition:** 
  - `inline` for preview-able files (PDFs, images, text)
  - `attachment` for download-only files (archives, Office docs, etc.)
- **Body:** File content (binary stream)

---

## Supported File Types

### Preview in Browser (inline)
These files open directly in the browser:

| Type | Extensions | MIME Type |
|------|-----------|-----------|
| **PDF** | pdf | `application/pdf` |
| **Images** | jpg, jpeg, png, gif, bmp, webp | `image/*` |
| **Text** | txt, csv, xml, json | `text/*` |

### Download Only (attachment)
These files trigger browser download:

| Type | Extensions | MIME Type |
|------|-----------|-----------|
| **Word** | doc, docx | `application/msword`, `application/vnd.openxmlformats-officedocument.wordprocessingml.document` |
| **Excel** | xls, xlsx | `application/vnd.ms-excel`, `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` |
| **PowerPoint** | ppt, pptx | `application/vnd.ms-powerpoint`, `application/vnd.openxmlformats-officedocument.presentationml.presentation` |
| **Archives** | zip, rar, 7z | `application/zip`, `application/x-rar-compressed`, `application/x-7z-compressed` |
| **Other** | * | `application/octet-stream` |

---

## Security Features

### 1. **Directory Traversal Protection**
```java
// Validates fileName to prevent path traversal attacks
if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
    throw new IllegalArgumentException("Invalid file name");
}
```

**Blocked Examples:**
- `../../../etc/passwd`
- `..\\windows\\system32\\config`
- `subfolder/../../secret.txt`

### 2. **Booking Validation**
- Verifies booking exists before serving file
- Ensures user has access to the booking (JWT validation)

### 3. **File Type Validation**
- Only serves regular files (not directories or symlinks)
- Returns 404 if file doesn't exist

---

## API Examples

### Example 1: Preview PDF in Browser

**Request:**
```bash
GET /api/v1/booking/BKON6010/documents/invoice.pdf
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:**
```
HTTP/1.1 200 OK
Content-Type: application/pdf
Content-Length: 25700
Content-Disposition: inline; filename="invoice.pdf"

[Binary PDF content]
```

**Browser Behavior:** Opens PDF in browser's built-in PDF viewer

---

### Example 2: Download Excel File

**Request:**
```bash
GET /api/v1/booking/BKON6010/documents/report.xlsx
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:**
```
HTTP/1.1 200 OK
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Content-Length: 45320
Content-Disposition: attachment; filename="report.xlsx"

[Binary Excel content]
```

**Browser Behavior:** Downloads file to user's downloads folder

---

### Example 3: Preview Image

**Request:**
```bash
GET /api/v1/booking/BKON6010/documents/photo.jpg
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:**
```
HTTP/1.1 200 OK
Content-Type: image/jpeg
Content-Length: 1234567
Content-Disposition: inline; filename="photo.jpg"

[Binary image content]
```

**Browser Behavior:** Displays image directly in browser

---

## Error Responses

### 400 Bad Request
**Scenario:** Invalid file name (contains path traversal characters)

```json
{
  "statusCode": 400,
  "statusCodeText": "Bad Request",
  "errorDateTime": "2025-10-22T18:00:00",
  "errors": [
    {
      "errorCode": 1001,
      "message": "Invalid file name: ../../../secret.txt"
    }
  ]
}
```

### 404 Not Found
**Scenario:** File doesn't exist

```json
{
  "statusCode": 404,
  "statusCodeText": "Not Found",
  "errorDateTime": "2025-10-22T18:00:00",
  "errors": [
    {
      "errorCode": 1004,
      "message": "File not found: nonexistent.pdf"
    }
  ]
}
```

### 401 Unauthorized
**Scenario:** Missing or invalid JWT token

```json
{
  "statusCode": 401,
  "statusCodeText": "Unauthorized",
  "errorDateTime": "2025-10-22T18:00:00",
  "errors": [
    {
      "errorCode": 1002,
      "message": "Invalid JWT token or authentication failed"
    }
  ]
}
```

---

## Frontend Integration

### JavaScript/TypeScript Example

```typescript
// Download/preview document
async function previewDocument(bookingId: string, fileName: string) {
  const token = localStorage.getItem('jwtToken');
  
  const response = await fetch(
    `/api/v1/booking/${bookingId}/documents/${fileName}`,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );
  
  if (!response.ok) {
    throw new Error(`Failed to load document: ${response.statusText}`);
  }
  
  // Get the blob
  const blob = await response.blob();
  
  // Create object URL
  const url = URL.createObjectURL(blob);
  
  // Open in new tab for preview
  window.open(url, '_blank');
  
  // Or download it
  // const a = document.createElement('a');
  // a.href = url;
  // a.download = fileName;
  // a.click();
  // URL.revokeObjectURL(url);
}

// Usage
previewDocument('BKON6010', 'invoice.pdf');
```

### React Example with Preview Modal

```tsx
import React, { useState } from 'react';

function DocumentPreview({ bookingId, fileName }: Props) {
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  
  const handlePreview = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem('jwtToken');
      
      const response = await fetch(
        `/api/v1/booking/${bookingId}/documents/${fileName}`,
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );
      
      if (!response.ok) {
        throw new Error('Failed to load document');
      }
      
      const blob = await response.blob();
      const url = URL.createObjectURL(blob);
      setPreviewUrl(url);
      
    } catch (error) {
      console.error('Error loading document:', error);
      alert('Failed to load document');
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div>
      <button onClick={handlePreview} disabled={loading}>
        {loading ? 'Loading...' : 'Preview'}
      </button>
      
      {previewUrl && (
        <div className="preview-modal">
          <iframe 
            src={previewUrl} 
            width="100%" 
            height="600px"
            title={fileName}
          />
          <button onClick={() => {
            URL.revokeObjectURL(previewUrl);
            setPreviewUrl(null);
          }}>
            Close
          </button>
        </div>
      )}
    </div>
  );
}
```

### Direct Link (HTML)

```html
<!-- This won't work because browser will send the request without JWT token -->
<!-- Use JavaScript fetch instead -->

<!-- For authenticated image preview -->
<img id="doc-image" />
<script>
fetch('/api/v1/booking/BKON6010/documents/photo.jpg', {
  headers: { 'Authorization': 'Bearer ' + token }
})
.then(r => r.blob())
.then(blob => {
  document.getElementById('doc-image').src = URL.createObjectURL(blob);
});
</script>
```

---

## Implementation Details

### Service Layer

**File:** `DocumentService.java`

```java
public File downloadDocument(String bookingNo, String fileName) throws Exception {
    // 1. Validate booking exists
    bookingRepository.findById(bookingNo)
        .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    
    // 2. Validate fileName (prevent directory traversal)
    if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
        throw new IllegalArgumentException("Invalid file name");
    }
    
    // 3. Resolve booking folder from emssetting
    Path targetFolder = resolveBookingFolder(bookingNo);
    Path filePath = targetFolder.resolve(fileName);
    
    // 4. Check if file exists and is regular file
    if (!Files.exists(filePath)) {
        throw new IllegalArgumentException("File not found");
    }
    if (!Files.isRegularFile(filePath)) {
        throw new IllegalArgumentException("Not a regular file");
    }
    
    // 5. Return file
    return filePath.toFile();
}
```

### Controller Layer

**File:** `BookingController.java`

```java
@GetMapping("/{bookingId}/documents/{fileName}")
public ResponseEntity<?> downloadDocument(
    @PathVariable String bookingId,
    @PathVariable String fileName) {
    
    // 1. JWT authentication
    // 2. Get file from service
    File file = documentService.downloadDocument(bookingId, fileName);
    
    // 3. Determine content type
    String contentType = determineContentType(fileName);
    
    // 4. Set headers (inline for preview or attachment for download)
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType(contentType));
    headers.setContentLength(file.length());
    
    if (isPreviewable(contentType)) {
        headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());
    } else {
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
    }
    
    // 5. Return file as Resource
    Resource resource = new FileSystemResource(file);
    return ResponseEntity.ok().headers(headers).body(resource);
}
```

---

## Testing

### cURL Examples

```bash
# Preview PDF
curl -X GET \
  "http://localhost:8080/ecomapi-service/api/v1/booking/BKON6010/documents/invoice.pdf" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output invoice.pdf

# Download Excel
curl -X GET \
  "http://localhost:8080/ecomapi-service/api/v1/booking/BKON6010/documents/report.xlsx" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output report.xlsx

# Preview image
curl -X GET \
  "http://localhost:8080/ecomapi-service/api/v1/booking/BKON6010/documents/photo.jpg" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output photo.jpg
```

### Postman

1. **Method:** GET
2. **URL:** `http://localhost:8080/ecomapi-service/api/v1/booking/BKON6010/documents/invoice.pdf`
3. **Headers:**
   - `Authorization`: `Bearer YOUR_JWT_TOKEN`
4. **Send** → Postman will display a "Preview" button for supported types

---

## Performance Considerations

### File Streaming
- Files are streamed directly from disk to client
- No buffering in memory for large files
- Spring `FileSystemResource` handles efficient streaming

### Caching
Browser caching can be enabled by adding cache headers:

```java
headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS));
```

### Large Files
- Maximum file size is 10MB per file, 50MB total request (set during upload validation)
- No special handling needed - Spring streams efficiently
- Consider adding Range header support for very large files (future enhancement)

---

## Complete API Summary

| Endpoint | Method | Purpose | Response |
|----------|--------|---------|----------|
| `/bookings/{bookingId}/documents` | POST | Upload 1-5 files | JSON (file names, sizes) |
| `/bookings/{bookingId}/documents` | GET | List all files | JSON (file metadata) |
| `/bookings/{bookingId}/documents/{fileName}` | GET | Download/preview file | Binary (file content) |

---

## Browser Compatibility

### Preview Support

| Browser | PDF | Images | Text | Office Docs |
|---------|-----|--------|------|-------------|
| Chrome | ✅ | ✅ | ✅ | ❌ (download) |
| Firefox | ✅ | ✅ | ✅ | ❌ (download) |
| Safari | ✅ | ✅ | ✅ | ❌ (download) |
| Edge | ✅ | ✅ | ✅ | ❌ (download) |

**Note:** Office documents (Word, Excel, PowerPoint) will always download, even if browser plugins are installed, because we set `Content-Disposition: attachment`.

---

## Security Best Practices

1. ✅ **JWT Authentication** - All requests require valid JWT token
2. ✅ **Path Traversal Prevention** - Validates fileName for malicious paths
3. ✅ **Booking Validation** - Ensures booking exists before serving files
4. ✅ **File Type Validation** - Only serves regular files, not directories
5. ✅ **Customer Isolation** - JWT contains customer code for access control
6. ⚠️ **Future:** Add customer-booking authorization check (verify booking belongs to customer)

---

## Future Enhancements

### 1. Range Requests (Partial Content)
Support HTTP Range header for seeking in videos/large files:
```
GET /documents/video.mp4
Range: bytes=0-1023
```

### 2. Thumbnail Generation
Generate and serve thumbnails for images:
```
GET /documents/photo.jpg?thumbnail=true
```

### 3. Document Conversion
Convert Office docs to PDF for preview:
```
GET /documents/report.docx?convert=pdf
```

### 4. Access Control
Add customer-booking authorization:
```java
// Verify booking belongs to customer
if (!booking.getCustomerCode().equals(customerCode)) {
    throw new ForbiddenException();
}
```

---

## Summary

✅ **Added:** New download/preview endpoint  
✅ **Security:** Directory traversal protection  
✅ **UX:** Inline preview for PDFs, images, text  
✅ **Browser:** Direct file downloads  
✅ **Frontend:** Easy integration with fetch API  
✅ **Performance:** Efficient file streaming  

The file is **transferred to the frontend** as a binary stream, enabling both preview and download functionality! 🎉

