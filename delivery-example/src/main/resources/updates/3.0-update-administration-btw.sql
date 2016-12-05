BEGIN;

ALTER TABLE reproductions ADD COLUMN adminstrationcostsbtwpercentage integer NULL DEFAULT 0;
ALTER TABLE reproductions ADD COLUMN adminstrationcostsbtwprice numeric(7,2) NULL DEFAULT 0;
ALTER TABLE reproductions ADD CONSTRAINT reproductions_adminstrationcostsbtwpercentage_check CHECK (adminstrationcostsbtwpercentage <= 100);

COMMIT;
