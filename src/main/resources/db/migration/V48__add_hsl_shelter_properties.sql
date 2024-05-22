-- HSL specific shelter features

ALTER TABLE installed_equipment_version_structure ADD COLUMN shelter_type text;
ALTER TABLE installed_equipment_version_structure ADD COLUMN shelter_electricity text;
ALTER TABLE installed_equipment_version_structure ADD COLUMN shelter_lighting boolean;
ALTER TABLE installed_equipment_version_structure ADD COLUMN shelter_condition text;
ALTER TABLE installed_equipment_version_structure ADD COLUMN timetable_cabinets integer;
ALTER TABLE installed_equipment_version_structure ADD COLUMN trash_can boolean;
ALTER TABLE installed_equipment_version_structure ADD COLUMN shelter_has_display boolean;
ALTER TABLE installed_equipment_version_structure ADD COLUMN bicycle_parking boolean;
ALTER TABLE installed_equipment_version_structure ADD COLUMN leaning_rail boolean;
ALTER TABLE installed_equipment_version_structure ADD COLUMN outside_bench boolean;
ALTER TABLE installed_equipment_version_structure ADD COLUMN shelter_fascia_board_taping boolean;

COMMENT ON COLUMN installed_equipment_version_structure.shelter_type IS 'Katoksen tyyppi: Lasikatos (glass) / Teräskatos (steel) / Tolppa (post) / Virtuaali (virtual) / Jää pois (leaveOff)';
COMMENT ON COLUMN installed_equipment_version_structure.shelter_electricity IS 'Katoksen sähköt: Jatkuva sähkö (continuous) / Valosähkö (light) / Jatkuva rakenteilla (continuousUnderConstruction) / Jatkuva suunniteltu (continuousPlanned) / Tilapäisesti pois (temporarilyOff) / Ei sähköä (none)';
COMMENT ON COLUMN installed_equipment_version_structure.shelter_lighting IS 'Katoksessa valot';
COMMENT ON COLUMN installed_equipment_version_structure.shelter_condition IS 'Katoksen kunto: Hyvä (good), Välttävä (mediocre), Huono (bad)';
COMMENT ON COLUMN installed_equipment_version_structure.timetable_cabinets IS 'Aikataulukaappien lukumäärä';
COMMENT ON COLUMN installed_equipment_version_structure.trash_can IS 'Katoksessa roska-astia';
COMMENT ON COLUMN installed_equipment_version_structure.shelter_has_display IS 'Katoksesssa näyttö';
COMMENT ON COLUMN installed_equipment_version_structure.bicycle_parking IS 'Pyöräpysäköinti';
COMMENT ON COLUMN installed_equipment_version_structure.leaning_rail IS 'Nojailutanko';
COMMENT ON COLUMN installed_equipment_version_structure.outside_bench IS 'Ulkopenkki';
COMMENT ON COLUMN installed_equipment_version_structure.shelter_fascia_board_taping IS 'Pysäkkikatoksen otsalaudan teippaus';
