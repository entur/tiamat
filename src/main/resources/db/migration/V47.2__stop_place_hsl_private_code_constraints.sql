CREATE INDEX IF NOT EXISTS stop_place_private_code ON stop_place(private_code_value);

-- Drop constraint if created in earlier migration version.
ALTER table stop_place DROP CONSTRAINT IF EXISTS has_valid_hsl_private_code;

-- Update existing data to have proper Private Code Type
UPDATE stop_place
SET private_code_type = 'HSL/JORE-3'
WHERE private_code_value ~ '^[012345689]\d{5}$';

UPDATE stop_place
SET private_code_type = 'HSL/JORE-4'
WHERE private_code_value ~ '^[7]\d{5}$';

UPDATE stop_place
SET private_code_type = 'HSL/TEST'
WHERE private_code_type = 'HSL';

-- Add in the DB constraint
ALTER TABLE stop_place
    ADD CONSTRAINT has_valid_hsl_private_code
        CHECK (
            -- Dont enforce format for Terminals
            parent_stop_place OR
            -- Nor for non HSL codes
            private_code_type NOT LIKE 'HSL%'  OR

            -- StopAreas created in Jore 4 need to start with 7 and be 6-numbers long
            (private_code_type = 'HSL/JORE-4' AND private_code_value ~ '^[7]\d{5}$') OR
            -- StopAreas created in Jore 3 do not start with 7 and are 6-numbers long
            (private_code_type = 'HSL/JORE-3' AND private_code_value ~ '^[012345689]\d{5}$') OR
            -- Allow any random string as the code in test data.
            -- So that we don't have to rewrite all the test cases/data.
            (private_code_type = 'HSL/TEST')
        );
