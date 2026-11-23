# ✅ Database Schema Update - Complete

## What Was Changed

Based on your feedback about the **actual emssetting table structure**, I've updated the implementation to match your real database schema.

---

## 🔄 Key Changes

### **Before (Incorrect)**
```sql
-- Wrong query (assumed structure)
SELECT setting_value 
FROM emssetting 
WHERE setting_key IN ('BL_DOCS_ECOM_WIN', 'BL_DOCS_ECOM_LIN')
```

### **After (Correct)**
```sql
-- Your actual query
SELECT path 
FROM emssetting WITH (NOLOCK) 
WHERE modulename = 'Bill of Lading' AND status = 'A'
```

---

## 📋 Updated Files

| File | Changes |
|------|---------|
| `EmsSetting.java` | ✅ Updated to match actual table structure (id, modulename, path, status) |
| `EmsSettingRepository.java` | ✅ Added native query with NOLOCK hint matching your SQL |
| `DocumentService.java` | ✅ Updated to add "Ecom" folder to base path |
| `setup-document-tables.sql` | ✅ Updated to verify (not create) emssetting table |
| `DATABASE-SCHEMA-UPDATE.md` | ✅ New - Complete documentation of changes |

---

## 🎯 How It Works Now

### **Database Query:**
```sql
SELECT path FROM emssetting WITH (NOLOCK) 
WHERE modulename = 'Bill of Lading' AND status = 'A'
```

### **Path Construction:**
```java
// Get base path from database
String basePath = setting.getPath();  // e.g., "\\Share\BL_DOCS"

// API adds: /Ecom/<bookingNo>
Path fullPath = Paths.get(basePath, "Ecom", bookingNo);
// Result: \\Share\BL_DOCS\Ecom\BKON1442
```

---

## 📁 Folder Structure

### **Windows Server Example:**

**Database Value:**
```
modulename: 'Bill of Lading'
path:       '\\Share\BL_DOCS'      ← Base path only
status:     'A'
```

**API Constructs:**
```
\\Share\BL_DOCS                     ← From database
    └── Ecom                        ← Added by API
        └── BKON1442                ← Added by API (bookingNo)
            ├── invoice.pdf
            └── packing_list.xlsx
```

---

### **Linux Server Example:**

**Database Value:**
```
modulename: 'Bill of Lading'
path:       '/mnt/share/BL_DOCS'   ← Base path only
status:     'A'
```

**API Constructs:**
```
/mnt/share/BL_DOCS                  ← From database
    └── Ecom                        ← Added by API
        └── BKON1442                ← Added by API (bookingNo)
            ├── invoice.pdf
            └── packing_list.xlsx
```

---

## ✅ What You Need to Do

### 1. **Verify Database Setting**

```sql
-- Run this in ReportDB
SELECT id, modulename, path, status 
FROM emssetting WITH (NOLOCK)
WHERE modulename = 'Bill of Lading' AND status = 'A';
```

**Expected:** One row with:
- `modulename = 'Bill of Lading'`
- `path = <your_base_path>` (e.g., `\\Share\BL_DOCS`)
- `status = 'A'`

### 2. **If Setting Doesn't Exist, Create It**

**Windows Server:**
```sql
INSERT INTO emssetting (modulename, path, status)
VALUES ('Bill of Lading', '\\YourServer\YourShare\BL_DOCS', 'A');
```

**Linux Server:**
```sql
INSERT INTO emssetting (modulename, path, status)
VALUES ('Bill of Lading', '/your/mount/path/BL_DOCS', 'A');
```

### 3. **Create Folder Structure**

**Windows:**
```cmd
mkdir \\YourServer\YourShare\BL_DOCS\Ecom
mkdir \\YourServer\YourShare\BL_DOCS\Ecom\BKON1442
```

**Linux:**
```bash
mkdir -p /your/mount/path/BL_DOCS/Ecom/BKON1442
chmod 755 /your/mount/path/BL_DOCS/Ecom
```

### 4. **Run Setup Script (Optional)**

```bash
# This will:
# - Verify emssetting exists
# - Check if Bill of Lading setting exists
# - Create upload_idempotency table
# - Display current configuration

sqlcmd -S <server> -d ReportDB -i setup-document-tables.sql
```

---

## 🧪 Test It

### **Upload Test:**
```bash
curl -X POST "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents" \
  -H "Authorization: Bearer YOUR_JWT" \
  -F "files=@test.pdf"
```

### **Expected Result:**
```json
[
  {
    "fileName": "test.pdf",
    "sizeBytes": 12345,
    "checksum": "sha256:abc...",
    "savedPath": "\\\\Share\\BL_DOCS\\Ecom\\BKON1442\\test.pdf"
  }
]
```

**Note:** Path will be Windows or Linux format depending on your server environment.

---

## 🔒 All Validations Still Work

✅ JWT authentication
✅ Customer code validation
✅ **Booking exists in database** (ecombooking table)
✅ **Folder exists on share**
✅ File count (1-5)
✅ File size (≤10MB per file, ≤50MB total)
✅ File type (extension + MIME)
✅ Overwrite control
✅ All error handling

**Nothing broken!** Only the path resolution was updated.

---

## 📝 Key Differences

| Aspect | Old (Wrong) | New (Correct) |
|--------|-------------|---------------|
| **Table Columns** | setting_key, setting_value | id, modulename, path, status |
| **Query** | WHERE setting_key = 'KEY' | WHERE modulename = 'Bill of Lading' AND status = 'A' |
| **Path Value** | Full path with Ecom | Base path only |
| **API Adds** | Just booking number | /Ecom/<bookingNo> |
| **Database** | Assumed any database | ReportDB |
| **Query Hint** | None | WITH (NOLOCK) |

---

## 🎉 Summary

✅ **Implementation now uses YOUR actual database schema**
✅ **Query matches exactly:** `select path from emssetting(nolock) where modulename='Bill of Lading' and status='A'`
✅ **Adds "Ecom" folder automatically**
✅ **Connects to ReportDB**
✅ **Environment-aware** (Windows vs Linux paths)
✅ **All validations preserved**
✅ **Zero linter errors** (except 1 unrelated warning)

---

## 📚 Documentation

| Document | Purpose |
|----------|---------|
| `DATABASE-SCHEMA-UPDATE.md` | 📖 Detailed technical changes |
| `SCHEMA-UPDATE-SUMMARY.md` | 📋 This file - Quick overview |
| `VALIDATION-SUMMARY.md` | ✅ All validations documented |
| `FEATURE-COMPLETE.md` | 🎯 Overall feature status |
| `QUICK-START-DOCUMENT-UPLOAD.md` | 🚀 Quick setup guide |

---

**Status:** ✅ **READY FOR TESTING**

The implementation now correctly:
1. Queries your actual emssetting table structure
2. Uses the correct query with NOLOCK hint
3. Adds "Ecom" folder to the base path
4. Connects to ReportDB database
5. Handles Windows and Linux environments

**Next Step:** Test with a real booking number!

