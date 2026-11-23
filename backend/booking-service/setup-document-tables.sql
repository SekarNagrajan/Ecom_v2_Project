-- ==============================================================================
-- Document Upload Feature - Database Setup Script
-- ==============================================================================
-- This script creates the necessary tables for the booking document upload feature
-- Run this on your ReportDB database (or whichever database contains ecombooking)
-- ==============================================================================

USE [ReportDB]
GO

-- ==============================================================================
-- 1. Verify emssetting table exists (should already exist in ReportDB)
-- ==============================================================================
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'emssetting')
BEGIN
    PRINT 'Table emssetting exists (OK).';
    
    -- Check if Bill of Lading setting exists
    IF EXISTS (SELECT 1 FROM [dbo].[emssetting] WHERE modulename = 'Bill of Lading' AND status = 'A')
    BEGIN
        PRINT 'Bill of Lading setting found (OK).';
    END
    ELSE
    BEGIN
        PRINT 'WARNING: Bill of Lading setting NOT found!';
        PRINT 'Please ensure a row exists with: modulename=''Bill of Lading'', status=''A''';
    END
END
ELSE
BEGIN
    PRINT 'WARNING: Table emssetting does NOT exist!';
    PRINT 'Expected structure:';
    PRINT '  - id (identity primary key)';
    PRINT '  - modulename VARCHAR(100)';
    PRINT '  - path VARCHAR(500)';
    PRINT '  - status VARCHAR(1)';
END
GO

-- ==============================================================================
-- 2. Create upload_idempotency table
-- ==============================================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'upload_idempotency')
BEGIN
    CREATE TABLE [dbo].[upload_idempotency] (
        [idempotency_key] VARCHAR(255) NOT NULL PRIMARY KEY,
        [booking_id] VARCHAR(20) NOT NULL,
        [response_payload] VARCHAR(4000) NULL,
        [created_at] DATETIME2 NOT NULL DEFAULT GETDATE(),
        [expires_at] DATETIME2 NOT NULL
    );
    
    -- Create index on booking_id for faster lookups
    CREATE INDEX IX_upload_idempotency_booking_id ON [dbo].[upload_idempotency]([booking_id]);
    
    -- Create index on expires_at for cleanup operations
    CREATE INDEX IX_upload_idempotency_expires_at ON [dbo].[upload_idempotency]([expires_at]);
    
    PRINT 'Table upload_idempotency created successfully.';
END
ELSE
BEGIN
    PRINT 'Table upload_idempotency already exists.';
END
GO

-- ==============================================================================
-- 3. Verify Bill of Lading path setting
-- ==============================================================================
-- Note: The path in emssetting should be the BASE path only
--       The API will automatically add "/Ecom/<bookingNo>" to it
--
-- Example paths (update according to your environment):
--   Windows: \\Share\BL_DOCS
--   Linux:   /mnt/share/BL_DOCS
--
-- Query used by API: select path from emssetting(nolock) 
--                    where modulename='Bill of Lading' and status='A'
--
-- Final path constructed: <path>/Ecom/<bookingNo>
--   Example: \\Share\BL_DOCS\Ecom\BKON1442

PRINT '';
PRINT '==============================================================================';
PRINT 'Checking Bill of Lading setting...';
PRINT '==============================================================================';

DECLARE @CurrentPath VARCHAR(500);
SELECT @CurrentPath = [path] 
FROM [dbo].[emssetting] WITH (NOLOCK)
WHERE modulename = 'Bill of Lading' AND status = 'A';

IF @CurrentPath IS NOT NULL
BEGIN
    PRINT 'Current Bill of Lading path: ' + @CurrentPath;
    PRINT '';
    PRINT 'The API will construct final paths as:';
    PRINT '  Full path = ' + @CurrentPath + '\Ecom\<bookingNo>';
    PRINT '';
    PRINT 'Example: If bookingNo = BKON1442';
    PRINT '  Full path = ' + @CurrentPath + '\Ecom\BKON1442';
END
ELSE
BEGIN
    PRINT 'ERROR: Bill of Lading setting NOT found!';
    PRINT '';
    PRINT 'To fix this, run the following INSERT (update path for your environment):';
    PRINT '';
    PRINT '-- For Windows Server:';
    PRINT 'INSERT INTO emssetting (modulename, path, status)';
    PRINT 'VALUES (''Bill of Lading'', ''\\Share\BL_DOCS'', ''A'');';
    PRINT '';
    PRINT '-- For Linux Server:';
    PRINT 'INSERT INTO emssetting (modulename, path, status)';
    PRINT 'VALUES (''Bill of Lading'', ''/mnt/share/BL_DOCS'', ''A'');';
END

PRINT '==============================================================================';
GO

-- ==============================================================================
-- 4. Create cleanup stored procedure for expired idempotency records
-- ==============================================================================
IF EXISTS (SELECT * FROM sys.procedures WHERE name = 'usp_cleanup_expired_idempotency')
BEGIN
    DROP PROCEDURE [dbo].[usp_cleanup_expired_idempotency];
END
GO

CREATE PROCEDURE [dbo].[usp_cleanup_expired_idempotency]
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @DeletedCount INT;
    
    DELETE FROM [dbo].[upload_idempotency]
    WHERE [expires_at] < GETDATE();
    
    SET @DeletedCount = @@ROWCOUNT;
    
    PRINT 'Cleanup completed. Deleted ' + CAST(@DeletedCount AS VARCHAR(10)) + ' expired records.';
END
GO

PRINT 'Stored procedure usp_cleanup_expired_idempotency created successfully.';
GO

-- ==============================================================================
-- 5. Verification queries
-- ==============================================================================
PRINT '';
PRINT '==============================================================================';
PRINT 'Setup completed successfully!';
PRINT '==============================================================================';
PRINT '';
PRINT 'Verification:';
SELECT 'emssetting' AS TableName, COUNT(*) AS RecordCount FROM [dbo].[emssetting];
SELECT 'upload_idempotency' AS TableName, COUNT(*) AS RecordCount FROM [dbo].[upload_idempotency];
PRINT '';
PRINT 'Bill of Lading document path setting:';
SELECT [id], [modulename], [path], [status] 
FROM [dbo].[emssetting] WITH (NOLOCK)
WHERE [modulename] = 'Bill of Lading' AND [status] = 'A';
PRINT '';
PRINT '==============================================================================';
PRINT 'IMPORTANT NOTES:';
PRINT '==============================================================================';
PRINT '1. The path in emssetting should be the BASE path only';
PRINT '   - Windows example: \\Share\BL_DOCS';
PRINT '   - Linux example:   /mnt/share/BL_DOCS';
PRINT '';
PRINT '2. The API automatically adds: /Ecom/<bookingNo>';
PRINT '   - Final path: <base_path>\Ecom\<bookingNo>';
PRINT '';
PRINT '3. Ensure folders exist with proper permissions:';
PRINT '   - Base folder: <path>';
PRINT '   - Ecom folder: <path>\Ecom';
PRINT '   - Booking folders: <path>\Ecom\<bookingNo>';
PRINT '';
PRINT '4. The path value differs based on environment (Windows vs Linux server)';
PRINT '==============================================================================';
GO

