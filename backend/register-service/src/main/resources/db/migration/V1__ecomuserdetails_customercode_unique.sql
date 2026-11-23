-- One-time migration: unique dummy/provisional customer codes on ECOMUSERDETAILS
-- Run manually against Ecomm_JSF if not already applied.
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'uq_ecomuserdetails_customercode'
      AND object_id = OBJECT_ID('ECOMUSERDETAILS')
)
BEGIN
    ALTER TABLE ECOMUSERDETAILS
        ADD CONSTRAINT uq_ecomuserdetails_customercode UNIQUE (CustomerCode);
END
