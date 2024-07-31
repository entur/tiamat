-- HSL specific stop signage features

ALTER TABLE installed_equipment_version_structure ADD COLUMN number_of_frames integer;
ALTER TABLE installed_equipment_version_structure ADD COLUMN line_signage boolean;
ALTER TABLE installed_equipment_version_structure ADD COLUMN replaces_rail_sign boolean;
ALTER TABLE installed_equipment_version_structure ADD COLUMN main_line_sign boolean;
ALTER TABLE installed_equipment_version_structure ADD COLUMN note_value text;
ALTER TABLE installed_equipment_version_structure ADD COLUMN note_lang text;
