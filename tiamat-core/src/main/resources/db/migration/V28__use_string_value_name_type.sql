ALTER TABLE alternative_name ALTER COLUMN name_type TYPE VARCHAR(11) USING name_type::text;

UPDATE alternative_name SET name_type = 'ALIAS' WHERE name_type = '0';
UPDATE alternative_name SET name_type = 'TRANSLATION' WHERE name_type = '1';
UPDATE alternative_name SET name_type = 'COPY' WHERE name_type = '2';
UPDATE alternative_name SET name_type = 'LABEL' WHERE name_type = '3';
UPDATE alternative_name SET name_type = 'OTHER' WHERE name_type = '4';