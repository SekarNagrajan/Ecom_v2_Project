# ✅ Verification Guide - Using Existing emssetting Table

## Status: No Database Changes Needed!

Since you already have the `emssetting` table with data in ReportDB, you just need to **verify** and **test**.

---

## 🔍 Step 1: Verify Your Existing Data

Run this query in **ReportDB**:

```sql
-- Check your existing Bill of Lading setting
SELECT id, modulename, path, status 
FROM emssetting WITH (NOLOCK)
WHERE modulename = 'Bill of Lading' AND status = 'A';
```

### Expected Result:

| id | modulename | path | status |
|----|------------|------|--------|
| X  | Bill of Lading | `\\YourServer\Share\Path` or `/your/mount/path` | A |

**✅ If you see one row** → Perfect! The API will use this path.

**❌ If no rows** → You may need to add the setting (but you said data exists, so this should be fine).

---

## 📁 Step 2: Understand How API Uses Your Data

### Your Database Has:
```
modulename: 'Bill of Lading'
path:       '\\Share\BL_DOCS'    (or your actual path)
status:     'A'
```

### API Will Construct:
```
Base from DB:     \\Share\BL_DOCS
+ "Ecom":         \\Share\BL_DOCS\Ecom
+ Booking number: \\Share\BL_DOCS\Ecom\BKON1442
+ File name:      \\Share\BL_DOCS\Ecom\BKON1442\invoice.pdf
```

**Important:** The API automatically adds `/Ecom/<bookingNo>` to whatever path is in your database.

---

## 🗂️ Step 3: Create Only the Booking Folders

You **don't need to modify the database**. Just create the folder structure:

### Windows Example:
```cmd
# If your database has: path = '\\Share\BL_DOCS'
# Then create:

mkdir \\Share\BL_DOCS\Ecom
mkdir \\Share\BL_DOCS\Ecom\BKON1442
mkdir \\Share\BL_DOCS\Ecom\BKON1443
# ... etc for each booking
```

### Linux Example:
```bash
# If your database has: path = '/mnt/share/BL_DOCS'
# Then create:

mkdir -p /mnt/share/BL_DOCS/Ecom
mkdir -p /mnt/share/BL_DOCS/Ecom/BKON1442
mkdir -p /mnt/share/BL_DOCS/Ecom/BKON1443
chmod -R 755 /mnt/share/BL_DOCS/Ecom
```

---

## 🧪 Step 4: Test Upload

### Quick Test:

```bash
curl -X POST "http://localhost:8080/ecomapi-service/api/v1/booking/BKON1442/documents" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "files=@test.pdf"
```

### What the API Does:

1. ✅ Validates booking `BKON1442` exists in `ecombooking` table
2. ✅ Queries ReportDB: `SELECT path FROM emssetting WHERE modulename='Bill of Lading' AND status='A'`
3. ✅ Gets your existing path (e.g., `\\Share\BL_DOCS`)
4. ✅ Constructs: `\\Share\BL_DOCS\Ecom\BKON1442`
5. ✅ Checks if folder exists
6. ✅ Validates file (size, type, etc.)
7. ✅ Saves to: `\\Share\BL_DOCS\Ecom\BKON1442\test.pdf`

### Success Response:
```json
[
  {
    "fileName": "test.pdf",
    "sizeBytes": 12345,
    "checksum": "sha256:abc123...",
    "savedPath": "\\\\Share\\BL_DOCS\\Ecom\\BKON1442\\test.pdf"
  }
]
```

---

## ❌ Common Issues & Solutions

### Issue 1: "Bill of Lading setting not found"

**Cause:** No record with `modulename='Bill of Lading' AND status='A'`

**Check:**
```sql
-- See all emssetting records
SELECT modulename, path, status FROM emssetting;

-- Check for inactive records
SELECT modulename, path, status 
FROM emssetting 
WHERE modulename = 'Bill of Lading';
```

**Fix:** Make sure there's an active record (status='A') for Bill of Lading.

---

### Issue 2: "Booking folder not found on share"

**Cause:** Folder `<path>\Ecom\<bookingNo>` doesn't exist

**Example:** 
- Database has: `\\Share\BL_DOCS`
- Booking number: `BKON1442`
- API looks for: `\\Share\BL_DOCS\Ecom\BKON1442` ❌ (doesn't exist)

**Fix:**
```cmd
mkdir \\Share\BL_DOCS\Ecom\BKON1442
```

---

### Issue 3: "Booking not found with number: BKON1442"

**Cause:** Booking doesn't exist in `ecombooking` table

**Check:**
```sql
SELECT ebookno, cust_code, ebook_status 
FROM ecombooking 
WHERE ebookno = 'BKON1442';
```

**Fix:** Use an existing booking number from your database.

---

## 🎯 One-Time Setup Checklist

Since your database is already set up:

- [x] ✅ emssetting table exists in ReportDB (you confirmed)
- [x] ✅ Bill of Lading setting exists with status='A' (you confirmed)
- [ ] 🔧 Create `Ecom` folder at your base path
- [ ] 🔧 Create booking subfolders (one per booking)
- [ ] 🧪 Test with one booking upload
- [ ] 📊 Verify file saved to correct location

---

## 📋 SQL Scripts Available (Optional)

If you want to run the setup script to verify everything, you can still run:

```bash
sqlcmd -S <server> -d ReportDB -i setup-document-tables.sql
```

**This script will:**
- ✅ Verify emssetting table exists
- ✅ Check if Bill of Lading setting exists
- ✅ Display your current configuration
- ✅ Create upload_idempotency table (for idempotency support)
- ❌ **NOT** create or modify emssetting (since it exists)

---

## 🔍 Verify Your Environment

### Windows Server:
```sql
-- Your database should have something like:
SELECT path FROM emssetting 
WHERE modulename = 'Bill of Lading' AND status = 'A';

-- Result example:
-- \\ShareServer\Documents\BillOfLading
```

**Then folders should be:**
```
\\ShareServer\Documents\BillOfLading\Ecom\BKON1442
```

### Linux Server:
```sql
-- Your database should have something like:
SELECT path FROM emssetting 
WHERE modulename = 'Bill of Lading' AND status = 'A';

-- Result example:
-- /mnt/nfs/documents/billoflading
```

**Then folders should be:**
```
/mnt/nfs/documents/billoflading/Ecom/BKON1442
```

---

## 📞 Quick Troubleshooting

### Test the Database Query:
```sql
-- This is exactly what the API executes
SELECT path 
FROM emssetting WITH (NOLOCK) 
WHERE modulename = 'Bill of Lading' AND status = 'A';
```

**If this returns a path → API will work!**

### Check Permissions:
```bash
# Windows
icacls "\\Share\BL_DOCS\Ecom" /grant Users:F

# Linux
ls -la /mnt/share/BL_DOCS/Ecom
chmod 755 /mnt/share/BL_DOCS/Ecom
```

---

## ✅ Summary

**Database: No changes needed!** ✅

**You only need to:**
1. ✅ Verify existing Bill of Lading setting in emssetting table
2. 🔧 Create `Ecom` subfolder at your base path
3. 🔧 Create booking folders (`<base>/Ecom/<bookingNo>`)
4. 🧪 Test upload endpoint

**That's it!** The implementation is ready to use your existing database setup.

---

## 🎉 Ready to Test!

The API will:
- ✅ Connect to **ReportDB**
- ✅ Query your **existing emssetting** table
- ✅ Use your **existing path** value
- ✅ Add **`/Ecom/<bookingNo>`** automatically
- ✅ Save files to the constructed path

**No database modifications required!**

