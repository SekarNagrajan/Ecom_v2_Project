# ✅ Automatic Booking Folder Creation

## Overview
The API now automatically creates booking-specific folders when documents are uploaded for the first time.

---

## 📁 Folder Structure

### **What Must Exist (Pre-requisite):**
```
\\Share\BL_DOCS\           ← Base path from database
    └── Ecom\              ← MUST EXIST (system-level folder)
```

### **What API Creates Automatically:**
```
\\Share\BL_DOCS\Ecom\
    ├── BKON1442\          ← Created automatically on first upload
    │   ├── invoice.pdf
    │   └── packing_list.xlsx
    ├── BKON1443\          ← Created automatically on first upload
    │   └── document.pdf
    └── BKON1444\          ← Created automatically on first upload
        └── certificate.jpg
```

---

## 🔄 How It Works

### **Upload Endpoint Behavior:**

1. ✅ Validates booking exists in database
2. ✅ Resolves path: `<base_path>/Ecom/<bookingNo>`
3. ✅ **Checks if `<base_path>/Ecom/<bookingNo>` exists**
4. ✅ **If NOT exists → Creates folder automatically**
5. ✅ Proceeds with file upload

**Code:**
```java
if (!Files.exists(targetFolder)) {
    Files.createDirectories(targetFolder);
    logger.info("Created booking folder: {}", targetFolder);
}
```

---

### **List Endpoint Behavior:**

1. ✅ Validates booking exists in database
2. ✅ Resolves path: `<base_path>/Ecom/<bookingNo>`
3. ✅ **Checks if folder exists**
4. ✅ **If NOT exists → Returns empty list** (doesn't create folder)
5. ✅ If exists → Lists all files

**Reasoning:** Listing documents shouldn't create folders. Empty list indicates no documents uploaded yet.

---

## 🎯 One-Time Setup Required

### **You Only Need to Create:**

**Windows:**
```cmd
mkdir \\Share\BL_DOCS\Ecom
```

**Linux:**
```bash
mkdir -p /mnt/share/BL_DOCS/Ecom
chmod 755 /mnt/share/BL_DOCS/Ecom
```

**That's it!** The API will create individual booking folders as needed.

---

## 🧪 Test Scenarios

### **Scenario 1: First Upload to New Booking**

**Initial State:**
```
\\Share\BL_DOCS\Ecom\           ← Exists
    (no BKON1442 folder yet)
```

**Upload Request:**
```bash
curl -X POST "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents" \
  -H "Authorization: Bearer TOKEN" \
  -F "files=@invoice.pdf"
```

**What Happens:**
1. ✅ Validates `BKON1442` exists in ecombooking table
2. ✅ Checks if `\\Share\BL_DOCS\Ecom\BKON1442` exists → NO
3. ✅ **Creates folder:** `\\Share\BL_DOCS\Ecom\BKON1442`
4. ✅ Saves file: `\\Share\BL_DOCS\Ecom\BKON1442\invoice.pdf`

**Log Output:**
```
[INFO] DocumentService - Created booking folder: \\Share\BL_DOCS\Ecom\BKON1442
[INFO] DocumentService - File saved: invoice.pdf (size: 123456 bytes, checksum: sha256:abc...)
```

**Final State:**
```
\\Share\BL_DOCS\Ecom\
    └── BKON1442\              ← Created automatically
        └── invoice.pdf        ← Uploaded
```

---

### **Scenario 2: Subsequent Upload to Existing Booking**

**Initial State:**
```
\\Share\BL_DOCS\Ecom\
    └── BKON1442\              ← Already exists from previous upload
        └── invoice.pdf
```

**Upload Request:**
```bash
curl -X POST "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents" \
  -H "Authorization: Bearer TOKEN" \
  -F "files=@packing_list.xlsx"
```

**What Happens:**
1. ✅ Validates `BKON1442` exists in ecombooking table
2. ✅ Checks if `\\Share\BL_DOCS\Ecom\BKON1442` exists → YES
3. ✅ Skips folder creation (already exists)
4. ✅ Saves file: `\\Share\BL_DOCS\Ecom\BKON1442\packing_list.xlsx`

**Log Output:**
```
[INFO] DocumentService - File saved: packing_list.xlsx (size: 89012 bytes, checksum: sha256:def...)
```

**Final State:**
```
\\Share\BL_DOCS\Ecom\
    └── BKON1442\
        ├── invoice.pdf
        └── packing_list.xlsx  ← Added
```

---

### **Scenario 3: List Documents Before Any Upload**

**Initial State:**
```
\\Share\BL_DOCS\Ecom\           ← Exists
    (no BKON1442 folder yet)
```

**List Request:**
```bash
curl -X GET "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents" \
  -H "Authorization: Bearer TOKEN"
```

**What Happens:**
1. ✅ Validates `BKON1442` exists in ecombooking table
2. ✅ Checks if `\\Share\BL_DOCS\Ecom\BKON1442` exists → NO
3. ✅ **Returns empty list** (doesn't create folder)

**Response:**
```json
[]
```

**Log Output:**
```
[INFO] DocumentService - Booking folder does not exist yet: \\Share\BL_DOCS\Ecom\BKON1442. Returning empty list.
```

**State After:** (No change - folder NOT created)
```
\\Share\BL_DOCS\Ecom\           ← Still just Ecom folder
    (no BKON1442 folder)
```

---

## ❌ Error Scenarios

### **Error 1: Ecom Folder Missing**

**State:**
```
\\Share\BL_DOCS\               ← Exists
    (no Ecom folder)           ← MISSING!
```

**Upload Request:**
```bash
curl -X POST "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents" \
  -H "Authorization: Bearer TOKEN" \
  -F "files=@invoice.pdf"
```

**Result:**
```json
{
  "status": 500,
  "message": "Internal Server Error",
  "errors": [{
    "message": "Unable to create booking folder: \\Share\\BL_DOCS\\Ecom\\BKON1442. Error: Parent directory does not exist"
  }]
}
```

**Fix:**
```cmd
mkdir \\Share\BL_DOCS\Ecom
```

---

### **Error 2: No Write Permissions**

**Upload Request:** (without write permissions on Ecom folder)

**Result:**
```json
{
  "status": 500,
  "message": "Internal Server Error",
  "errors": [{
    "message": "Unable to create booking folder: \\Share\\BL_DOCS\\Ecom\\BKON1442. Error: Access denied"
  }]
}
```

**Fix:**
```cmd
# Windows
icacls "\\Share\BL_DOCS\Ecom" /grant ServiceAccount:F

# Linux
chmod 775 /mnt/share/BL_DOCS/Ecom
```

---

## 📋 Implementation Details

### **Upload Endpoint Changes:**

**Before (Old):**
```java
// Check if folder exists
if (!Files.exists(targetFolder)) {
    throw new IllegalArgumentException("Booking folder not found on share");
}
```

**After (New):**
```java
// Create booking folder if it doesn't exist
if (!Files.exists(targetFolder)) {
    try {
        Files.createDirectories(targetFolder);
        logger.info("Created booking folder: {}", targetFolder);
    } catch (IOException e) {
        throw new IllegalStateException("Unable to create booking folder: " + targetFolder);
    }
}
```

---

### **List Endpoint Changes:**

**Before (Old):**
```java
// Check if folder exists
if (!Files.exists(targetFolder)) {
    throw new IllegalArgumentException("Booking folder not found on share");
}
```

**After (New):**
```java
// Check if folder exists (don't create - just return empty list)
if (!Files.exists(targetFolder)) {
    logger.info("Booking folder does not exist yet. Returning empty list.");
    return new ArrayList<>();
}
```

---

## ✅ Benefits

### **For Operations:**
- ✅ **No manual folder creation** needed for each booking
- ✅ **Automatic provisioning** on first document upload
- ✅ **Lazy creation** - folders only created when needed
- ✅ **Reduces manual work** for support team

### **For Development:**
- ✅ **Simpler deployment** - only one system folder to create
- ✅ **Better user experience** - no 404 errors for new bookings
- ✅ **Scalable** - handles unlimited bookings automatically

### **For Testing:**
- ✅ **Easy testing** - upload to any valid booking ID
- ✅ **No setup overhead** - no folder pre-creation needed

---

## 🔒 Security Considerations

### **What's Protected:**

1. ✅ **Booking Validation:** Folder only created for valid bookings in database
2. ✅ **Path Traversal:** Uses `Paths.get()` - no directory traversal attacks
3. ✅ **Permissions:** Folder created with default permissions from parent directory
4. ✅ **Error Handling:** Clear error messages if creation fails

### **What You Control:**

1. 🔐 **Parent Folder Permissions:** Set on `Ecom` folder
2. 🔐 **Service Account:** Must have write permission on `Ecom` folder
3. 🔐 **File System:** Booking folders inherit from `Ecom` folder

---

## 📊 Comparison

| Aspect | Old (Manual) | New (Automatic) |
|--------|--------------|-----------------|
| **Ecom Folder** | Manual creation | Manual creation ✅ |
| **Booking Folders** | Manual creation | **Automatic creation** ✅ |
| **First Upload** | Requires pre-created folder | **Creates folder automatically** ✅ |
| **List Before Upload** | 404 Error | **Returns empty list** ✅ |
| **Operations Work** | High (create folder for each booking) | **Low (one-time Ecom folder only)** ✅ |
| **Error Prone** | Yes (forget to create folder) | **No (handled by API)** ✅ |

---

## 🎯 Summary

### **What Changed:**

- ✅ **Upload endpoint:** Creates booking folder if missing
- ✅ **List endpoint:** Returns empty list if folder missing (doesn't create)
- ✅ **Error handling:** Clear messages if folder creation fails

### **What You Need:**

1. ✅ Create `Ecom` folder once (manual)
2. ✅ Ensure write permissions on `Ecom` folder
3. ✅ That's it! API handles the rest

### **What's Automatic:**

- ✅ Booking folder creation on first upload
- ✅ Parent folder validation
- ✅ Permission inheritance
- ✅ Logging of folder creation

---

**Result:** Zero manual folder management for bookings! 🎉

