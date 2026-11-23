-- ============================================================================
-- Setup Script for ecomMdlIndex Table
-- ============================================================================
-- This script sets up the module index table for booking number generation
-- Run this script before using the booking number generation feature

-- Check if table exists (SQL Server syntax)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'ecomMdlIndex' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    PRINT 'Creating ecomMdlIndex table...'
    
    CREATE TABLE dbo.ecomMdlIndex (
        mdlId VARCHAR(5) NOT NULL PRIMARY KEY,
        mdlPrefix VARCHAR(10) NULL,
        mdlIndex INT NULL DEFAULT 0
    );
    
    PRINT 'Table ecomMdlIndex created successfully.'
END
ELSE
BEGIN
    PRINT 'Table ecomMdlIndex already exists.'
END
GO

-- Insert default module index record for ECOM module if not exists
IF NOT EXISTS (SELECT 1 FROM dbo.ecomMdlIndex WHERE mdlId = 'ECOM')
BEGIN
    PRINT 'Inserting default ECOM module index...'
    
    INSERT INTO dbo.ecomMdlIndex (mdlId, mdlPrefix, mdlIndex)
    VALUES ('ECOM', 'BK', 0);
    
    PRINT 'Default ECOM module index inserted. Booking numbers will start from BK1'
END
ELSE
BEGIN
    PRINT 'ECOM module index already exists.'
    
    -- Display current values
    SELECT 
        mdlId,
        mdlPrefix,
        mdlIndex,
        CONCAT(mdlPrefix, mdlIndex + 1) AS NextBookingNumber
    FROM dbo.ecomMdlIndex 
    WHERE mdlId = 'ECOM';
END
GO

-- ============================================================================
-- Optional: Insert additional module indexes for different agencies or types
-- ============================================================================

-- Example: Agency-specific prefixes
-- UNCOMMENT AND MODIFY AS NEEDED:

/*
-- Agency 1 booking numbers (starts with AG1)
IF NOT EXISTS (SELECT 1 FROM dbo.ecomMdlIndex WHERE mdlId = 'AGN1')
BEGIN
    INSERT INTO dbo.ecomMdlIndex (mdlId, mdlPrefix, mdlIndex)
    VALUES ('AGN1', 'AG1', 0);
    PRINT 'Agency 1 module index inserted.'
END

-- Agency 2 booking numbers (starts with AG2)
IF NOT EXISTS (SELECT 1 FROM dbo.ecomMdlIndex WHERE mdlId = 'AGN2')
BEGIN
    INSERT INTO dbo.ecomMdlIndex (mdlId, mdlPrefix, mdlIndex)
    VALUES ('AGN2', 'AG2', 0);
    PRINT 'Agency 2 module index inserted.'
END

-- Export bookings (starts with EX)
IF NOT EXISTS (SELECT 1 FROM dbo.ecomMdlIndex WHERE mdlId = 'EXPRT')
BEGIN
    INSERT INTO dbo.ecomMdlIndex (mdlId, mdlPrefix, mdlIndex)
    VALUES ('EXPRT', 'EX', 0);
    PRINT 'Export module index inserted.'
END

-- Import bookings (starts with IM)
IF NOT EXISTS (SELECT 1 FROM dbo.ecomMdlIndex WHERE mdlId = 'IMPRT')
BEGIN
    INSERT INTO dbo.ecomMdlIndex (mdlId, mdlPrefix, mdlIndex)
    VALUES ('IMPRT', 'IM', 0);
    PRINT 'Import module index inserted.'
END
*/

-- ============================================================================
-- Verify Setup
-- ============================================================================
PRINT ''
PRINT '============================================================================'
PRINT 'Current Module Index Configuration:'
PRINT '============================================================================'

SELECT 
    mdlId AS [Module ID],
    mdlPrefix AS [Prefix],
    mdlIndex AS [Current Index],
    CONCAT(mdlPrefix, mdlIndex + 1) AS [Next Booking Number]
FROM dbo.ecomMdlIndex
ORDER BY mdlId;

PRINT ''
PRINT 'Setup complete!'
PRINT ''
PRINT 'NOTES:'
PRINT '- To customize module IDs, modify the determineModuleId() method in BookingCreateService.java'
PRINT '- To reset the sequence, update the mdlIndex value in the table'
PRINT '- To change the prefix, update the mdlPrefix value in the table'
PRINT ''

