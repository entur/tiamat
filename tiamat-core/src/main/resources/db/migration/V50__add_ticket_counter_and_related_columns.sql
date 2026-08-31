ALTER TABLE installed_equipment_version_structure
    ADD COLUMN ticket_counter boolean,
    ADD COLUMN induction_loops boolean,
    ADD COLUMN low_counter_access boolean;