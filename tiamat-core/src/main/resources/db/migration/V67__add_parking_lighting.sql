ALTER TABLE IF EXISTS parking
    ADD COLUMN lighting varchar(255) CHECK (lighting IN ('WELL_LIT','POORLY_LIT','UNLIT','UNKNOWN','OTHER'));
