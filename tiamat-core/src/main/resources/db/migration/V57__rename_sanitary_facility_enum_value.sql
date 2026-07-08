-- Rename enum value WHEEL_CHAIR_ACCESS_TOILET to WHEELCHAIR_ACCESS_TOILET
-- to match netex-java-model 2.0.15

-- First, update existing data to use the new enum name
UPDATE sanitary_equipment_sanitary_facility_list
SET sanitary_facility_list = 'WHEELCHAIR_ACCESS_TOILET'
WHERE sanitary_facility_list = 'WHEEL_CHAIR_ACCESS_TOILET';

-- Drop the existing check constraint (PostgreSQL auto-truncates long names to 63 chars)
ALTER TABLE sanitary_equipment_sanitary_facility_list
DROP CONSTRAINT IF EXISTS sanitary_equipment_sanitary_facili_sanitary_facility_list_check;

-- Add new check constraint with updated enum value
ALTER TABLE sanitary_equipment_sanitary_facility_list
ADD CONSTRAINT sanitary_equipment_sanitary_facili_sanitary_facility_list_check
CHECK (sanitary_facility_list IN ('NONE','TOILET','WHEELCHAIR_ACCESS_TOILET','SHOWER','WASHING_AND_CHANGE_FACILITIES','BABY_CHANGE','WHEELCHAIR_BABY_CHANGE','SHOE_SHINER','OTHER'));