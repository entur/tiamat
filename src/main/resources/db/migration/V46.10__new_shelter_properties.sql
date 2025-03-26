ALTER TABLE installed_equipment_version_structure ADD COLUMN shelter_number integer;
ALTER TABLE installed_equipment_version_structure ADD COLUMN shelter_external_id text;

COMMENT ON COLUMN installed_equipment_version_structure.shelter_number IS 'Internal shelter number';
COMMENT ON COLUMN installed_equipment_version_structure.shelter_external_id IS 'Extrernal shelter id';
