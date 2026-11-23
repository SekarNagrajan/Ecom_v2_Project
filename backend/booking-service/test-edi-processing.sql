-- ============================================================================
-- EDI PROCESSING TEST SCRIPT
-- ============================================================================
-- Purpose: Insert test data with status='S' to test EDI processing
-- Usage: Execute this script, then call POST /api/edi/process-bookings
-- ============================================================================

-- ===========================================
-- CLEANUP (Optional - run if reusing book_no)
-- ===========================================
-- Uncomment these if you need to clean up previous test runs
/*
DELETE FROM ecombooking_dg WHERE ebookno = 'EDI-TEST-001';
DELETE FROM ecombooking_leg WHERE ebookno = 'EDI-TEST-001';
DELETE FROM ecombooking_cust WHERE ebookno = 'EDI-TEST-001';
DELETE FROM ecombooking_commodity WHERE ebookno = 'EDI-TEST-001';
DELETE FROM ecombooking_cargo WHERE ebookno = 'EDI-TEST-001';
DELETE FROM ecombooking WHERE ebookno = 'EDI-TEST-001';

DELETE FROM editempbooking_imo WHERE book_no = 'EDI-TEST-001';
DELETE FROM editempbooking_routing WHERE book_no = 'EDI-TEST-001';
DELETE FROM editempbooking_cust WHERE book_no = 'EDI-TEST-001';
DELETE FROM editempbooking_commodity WHERE book_no = 'EDI-TEST-001';
DELETE FROM editempbooking_cargo WHERE book_no = 'EDI-TEST-001';
DELETE FROM editempbooking WHERE book_no = 'EDI-TEST-001';
*/

-- ===========================================
-- TEST DATA - EDI Booking with Status 'S'
-- ===========================================

PRINT '========================================';
PRINT 'Inserting EDI Test Data';
PRINT '========================================';

-- 1. INSERT MAIN EDI BOOKING (Status = 'S')
PRINT 'Step 1: Inserting main EDI booking...';

INSERT INTO editempbooking (
    book_no, 
    agencyid, 
    status,                    -- 'S' = Success (ready to process)
    originPortId, 
    finalPortId,
    loadPortId, 
    dischargePortId, 
    bookDate, 
    bookReady,
    preCarriage, 
    onCarriage, 
    custRefNo, 
    agencyRefNo,
    shipTerms, 
    raterefNo, 
    remarks,
    datecr, 
    usercr, 
    edicleanstatus,           -- NULL = not processed yet
    version
) VALUES (
    'EDI-TEST-001',            -- Booking number
    'DXB',                     -- Agency ID (Dubai)
    'S',                       -- ⭐ Status = 'S' (Success)
    'AEJEA',                   -- Origin: Jebel Ali
    'USLAX',                   -- Final: Los Angeles
    'AEJEA',                   -- POL: Jebel Ali
    'USLAX',                   -- POD: Los Angeles
    GETDATE(),                 -- Booking date
    DATEADD(day, 2, GETDATE()),-- Cargo ready date (+2 days)
    'N',                       -- Pre-carriage: No
    'N',                       -- On-carriage: No
    'CUST-REF-123',            -- Customer reference
    'AGY-REF-456',             -- Agency reference
    'FOB',                     -- Shipping terms
    'RATE-001',                -- Rate reference
    'Test EDI booking created for automated processing verification', 
    GETDATE(),                 -- Creation date
    'EDITEST',                 -- Created by
    NULL,                      -- Clean status (NULL = not processed)
    0                          -- Version
);

PRINT 'Main booking inserted: EDI-TEST-001';

-- 2. INSERT CARGO/CONTAINERS
PRINT 'Step 2: Inserting cargo/container data...';

INSERT INTO editempbooking_cargo (
    book_no, 
    cargoNo,                   -- Container sequence number
    eqpId,                     -- Container number
    eqpTypeId,                 -- Equipment size/type
    type,                      -- F=Full, E=Empty
    eqpStatus,                 -- Equipment status
    grossWeight, 
    tareWeight, 
    cargoWeight, 
    weightUnit,                -- K=Kilograms
    version,
    edireaderid
) VALUES
-- Container 1: 20' General Purpose
('EDI-TEST-001', 1, 'TESU1234567', '20GP', 'F', 'F', 
 15000.00, 2200.00, 12800.00, 'K', 0, 'EDI-READER-001'),
-- Container 2: 40' High Cube
('EDI-TEST-001', 2, 'TESU2345678', '40HC', 'F', 'F', 
 25000.00, 3800.00, 21200.00, 'K', 0, 'EDI-READER-001'),
-- Container 3: 20' Reefer (for variety)
('EDI-TEST-001', 3, 'TESU3456789', '20RF', 'F', 'F', 
 18000.00, 2800.00, 15200.00, 'K', 0, 'EDI-READER-001');

PRINT 'Inserted 3 cargo records';

-- 3. INSERT COMMODITY DATA
PRINT 'Step 3: Inserting commodity data...';

INSERT INTO editempbooking_commodity (
    book_no, 
    cargoNo,                   -- Links to cargo
    itemNo,                    -- Item sequence per container
    eqpId,                     -- Container number
    itemDesc,                  -- Commodity description
    commodityId,               -- HS Code
    packageId,                 -- Package type
    itemQty,                   -- Number of packages
    itemVolume,                -- Volume
    itemVolumeUnit,            -- CBM = Cubic Meters
    marksNumbers,              -- Marks and numbers
    marksDesc,                 -- Marks description
    version,
    bycode,                    -- Buyer code
    byname,                    -- Buyer name
    slcode,                    -- Seller code
    slname                     -- Seller name
) VALUES
-- Container 1 - Item 1
('EDI-TEST-001', 1, 1, 'TESU1234567', 
 'ELECTRONIC GOODS - COMPUTERS AND PERIPHERALS', '8471',
 'CTN', 100, 15.5, 'CBM', 
 'MARKS: TEST-001-A', 'Electronics for export - Handle with care', 0,
 'BUY001', 'ABC Electronics Inc', 'SELL001', 'XYZ Manufacturing Ltd'),
-- Container 1 - Item 2 (multiple items per container)
('EDI-TEST-001', 1, 2, 'TESU1234567', 
 'COMPUTER ACCESSORIES', '8473',
 'BOX', 50, 5.2, 'CBM', 
 'MARKS: TEST-001-B', 'Accessories - Keep dry', 0,
 'BUY001', 'ABC Electronics Inc', 'SELL002', 'Tech Parts Co'),
-- Container 2 - Item 1
('EDI-TEST-001', 2, 1, 'TESU2345678', 
 'TEXTILE PRODUCTS - GARMENTS', '6204',
 'BOX', 200, 32.0, 'CBM', 
 'MARKS: TEST-002', 'Textiles for export', 0,
 'BUY002', 'Fashion Imports LLC', 'SELL003', 'Global Textiles Inc'),
-- Container 3 - Item 1
('EDI-TEST-001', 3, 1, 'TESU3456789', 
 'FRESH FRUITS - APPLES', '0808',
 'PLT', 40, 20.0, 'CBM', 
 'MARKS: TEST-003', 'Perishable - Keep refrigerated at 2°C', 0,
 'BUY003', 'Fresh Foods Market', 'SELL004', 'Fruit Growers Association');

PRINT 'Inserted 4 commodity records';

-- 4. INSERT CUSTOMER/PARTY DATA
PRINT 'Step 4: Inserting customer/party data...';

INSERT INTO editempbooking_cust (
    book_no, 
    custTypeId,                -- SH=Shipper, CN=Consignee, NP=Notify, FW=Forwarder
    custId,                    -- Customer code
    name,                      -- Customer name
    address1,                  -- Address line 1
    address2,                  -- Address line 2
    city, 
    countryId,                 -- ISO country code
    stateId,                   -- State code
    zipCode,                   -- Postal code
    telephone, 
    fax,
    email, 
    version,
    eori,                      -- EU EORI number
    persontype                 -- Legal or Natural person
) VALUES
-- Shipper (Exporter)
('EDI-TEST-001', 'SH', 'SHIP-001', 
 'TEST SHIPPER LLC', 
 '123 Business Street, Office 456', 
 'Al Quoz Industrial Area',
 'Dubai', 'AE', 'DXB', '00000',
 '+971-4-1234567', '+971-4-1234568',
 'exports@testshipper.ae', 0,
 NULL, 'L'),

-- Consignee (Importer)
('EDI-TEST-001', 'CN', 'CONS-001', 
 'TEST CONSIGNEE INC', 
 '456 Trade Avenue, Suite 789', 
 'Commerce District',
 'Los Angeles', 'US', 'CA', '90001',
 '+1-310-1234567', '+1-310-1234568',
 'imports@testconsignee.com', 0,
 NULL, 'L'),

-- Notify Party (Usually same as consignee or customs broker)
('EDI-TEST-001', 'NP', 'NTFY-001', 
 'TEST NOTIFY PARTY', 
 'Same as Consignee', 
 NULL,
 'Los Angeles', 'US', 'CA', '90001',
 '+1-310-1234567', '+1-310-1234568',
 'notify@testconsignee.com', 0,
 NULL, 'L'),

-- Forwarder (Optional - freight forwarder)
('EDI-TEST-001', 'FW', 'FRWD-001', 
 'GLOBAL LOGISTICS PARTNERS', 
 '789 Freight Lane', 
 'Logistics Hub',
 'Dubai', 'AE', 'DXB', '00000',
 '+971-4-9876543', '+971-4-9876544',
 'operations@globallogistics.ae', 0,
 NULL, 'L');

PRINT 'Inserted 4 customer records';

-- 5. INSERT ROUTING DATA
PRINT 'Step 5: Inserting routing data...';

INSERT INTO editempbooking_routing (
    book_no, 
    pol,                       -- Port of Loading
    pod,                       -- Port of Discharge
    svcCode,                   -- Service code
    origin,                    -- Origin location
    delivery,                  -- Delivery location
    polter,                    -- POL Terminal
    podTer,                    -- POD Terminal
    version
) VALUES
('EDI-TEST-001', 
 'AEJEA',                      -- POL: Jebel Ali
 'USLAX',                      -- POD: Los Angeles
 'PAX1',                       -- Service: Pacific Express 1
 'AEJEA',                      -- Origin
 'USLAX',                      -- Delivery
 'DP',                         -- Terminal: DP World
 'APM',                        -- Terminal: APM Terminals
 0);

PRINT 'Inserted routing data';

-- 6. (OPTIONAL) INSERT DANGEROUS GOODS DATA
-- Uncomment this section if you want to test DG/IMO processing
PRINT 'Step 6: Inserting dangerous goods data (optional)...';

/*
INSERT INTO editempbooking_imo (
    book_no, 
    eqpNo,                     -- Equipment/cargo number
    eqpId,                     -- Container number
    unNo,                      -- UN Number
    imdgClass,                 -- IMDG Class
    packingGrp,                -- Packing group
    flashPoint,                -- Flash point
    flashUnit,                 -- C=Celsius, F=Fahrenheit
    marinePollutant,           -- Y/N
    shippingName,              -- Proper shipping name
    techname,                  -- Technical name
    emsNo,                     -- EMS number
    subsidiaryLabel,           -- Subsidiary risk
    netWt,                     -- Net weight
    grossWt,                   -- Gross weight
    version
) VALUES
-- Dangerous goods for container 1
('EDI-TEST-001', 1, 'TESU1234567', 
 '1263',                       -- UN Number: PAINT
 '3',                          -- Class 3: Flammable liquid
 'III',                        -- Packing group III
 23.0,                         -- Flash point 23°C
 'C',                          -- Celsius
 'Y',                          -- Marine pollutant: Yes
 'PAINT (including paint, lacquer, enamel, stain, shellac)', 
 'Alkyd resin based paint',
 'F-E',                        -- EMS: Fire schedule E
 NULL,                         -- No subsidiary risk
 100.0,                        -- Net weight (kg)
 150.0,                        -- Gross weight (kg)
 0);
*/

PRINT 'Dangerous goods data skipped (commented out)';

-- ===========================================
-- VERIFICATION QUERIES
-- ===========================================

PRINT '';
PRINT '========================================';
PRINT 'Verification - EDI Tables';
PRINT '========================================';

-- Main booking
SELECT 
    book_no, 
    agencyid, 
    status,                    -- Should be 'S'
    edicleanstatus,            -- Should be NULL
    originPortId, 
    finalPortId,
    loadPortId, 
    dischargePortId,
    custRefNo,
    agencyRefNo,
    datecr,
    usercr
FROM editempbooking 
WHERE book_no = 'EDI-TEST-001';

PRINT '';
PRINT 'Main Booking: 1 record expected';
PRINT '';

-- Cargo count
SELECT COUNT(*) as CargoCount 
FROM editempbooking_cargo 
WHERE book_no = 'EDI-TEST-001';

PRINT 'Cargo Records: 3 expected';
PRINT '';

-- Commodity count
SELECT COUNT(*) as CommodityCount 
FROM editempbooking_commodity 
WHERE book_no = 'EDI-TEST-001';

PRINT 'Commodity Records: 4 expected';
PRINT '';

-- Customer count
SELECT COUNT(*) as CustomerCount 
FROM editempbooking_cust 
WHERE book_no = 'EDI-TEST-001';

PRINT 'Customer Records: 4 expected (SH, CN, NP, FW)';
PRINT '';

-- Routing count
SELECT COUNT(*) as RoutingCount 
FROM editempbooking_routing 
WHERE book_no = 'EDI-TEST-001';

PRINT 'Routing Records: 1 expected';
PRINT '';

-- Summary
SELECT 
    'editempbooking' as TableName,
    COUNT(*) as RecordCount
FROM editempbooking 
WHERE book_no = 'EDI-TEST-001'
UNION ALL
SELECT 
    'editempbooking_cargo',
    COUNT(*)
FROM editempbooking_cargo 
WHERE book_no = 'EDI-TEST-001'
UNION ALL
SELECT 
    'editempbooking_commodity',
    COUNT(*)
FROM editempbooking_commodity 
WHERE book_no = 'EDI-TEST-001'
UNION ALL
SELECT 
    'editempbooking_cust',
    COUNT(*)
FROM editempbooking_cust 
WHERE book_no = 'EDI-TEST-001'
UNION ALL
SELECT 
    'editempbooking_routing',
    COUNT(*)
FROM editempbooking_routing 
WHERE book_no = 'EDI-TEST-001';

PRINT '';
PRINT '========================================';
PRINT 'Test Data Insertion Complete!';
PRINT '========================================';
PRINT '';
PRINT 'Next Steps:';
PRINT '1. Call API: POST /api/edi/process-bookings';
PRINT '2. Check response for success';
PRINT '3. Run verification queries (below)';
PRINT '';

-- ===========================================
-- POST-PROCESSING VERIFICATION QUERIES
-- ===========================================
-- Run these AFTER calling the API

PRINT '========================================';
PRINT 'Post-Processing Verification Queries';
PRINT '(Run these AFTER API call)';
PRINT '========================================';
PRINT '';

-- Check if EDI booking is marked as processed
PRINT '-- 1. Check EDI booking status (should show edicleanstatus=P after processing)';
PRINT 'SELECT book_no, status, edicleanstatus, dateup FROM editempbooking WHERE book_no = ''EDI-TEST-001'';';
PRINT '';

-- Check ecom booking created
PRINT '-- 2. Check if ecom booking was created';
PRINT 'SELECT * FROM ecombooking WHERE ebookno = ''EDI-TEST-001'';';
PRINT '';

-- Check ecom cargo created
PRINT '-- 3. Check if ecom cargo was created';
PRINT 'SELECT ebookno, conid, containerno, eqpSize, eqpType, grossWeight FROM ecombooking_cargo WHERE ebookno = ''EDI-TEST-001'';';
PRINT '';

-- Check ecom commodity created
PRINT '-- 4. Check if ecom commodity was created';
PRINT 'SELECT ebookno, conid, itemno, commodity, packageType, packageQty FROM ecombooking_commodity WHERE ebookno = ''EDI-TEST-001'';';
PRINT '';

-- Check ecom customers created
PRINT '-- 5. Check if ecom customers were created';
PRINT 'SELECT ebookno, custTypeId, custId, name, city, countryId FROM ecombooking_cust WHERE ebookno = ''EDI-TEST-001'';';
PRINT '';

-- Check ecom route created
PRINT '-- 6. Check if ecom route was created';
PRINT 'SELECT ebookno, legNo, loadPortId, dischargePortId, serviceCode FROM ecombooking_leg WHERE ebookno = ''EDI-TEST-001'';';
PRINT '';

-- Summary comparison
PRINT '-- 7. Summary comparison (EDI vs Ecom)';
PRINT 'SELECT ''EDI Cargo'' as Source, COUNT(*) as Count FROM editempbooking_cargo WHERE book_no = ''EDI-TEST-001''';
PRINT 'UNION ALL';
PRINT 'SELECT ''Ecom Cargo'', COUNT(*) FROM ecombooking_cargo WHERE ebookno = ''EDI-TEST-001''';
PRINT 'UNION ALL';
PRINT 'SELECT ''EDI Commodity'', COUNT(*) FROM editempbooking_commodity WHERE book_no = ''EDI-TEST-001''';
PRINT 'UNION ALL';
PRINT 'SELECT ''Ecom Commodity'', COUNT(*) FROM ecombooking_commodity WHERE ebookno = ''EDI-TEST-001''';
PRINT 'UNION ALL';
PRINT 'SELECT ''EDI Customer'', COUNT(*) FROM editempbooking_cust WHERE book_no = ''EDI-TEST-001''';
PRINT 'UNION ALL';
PRINT 'SELECT ''Ecom Customer'', COUNT(*) FROM ecombooking_cust WHERE ebookno = ''EDI-TEST-001'';';
PRINT '';

PRINT '========================================';
PRINT 'Script Complete';
PRINT '========================================';

-- End of script

