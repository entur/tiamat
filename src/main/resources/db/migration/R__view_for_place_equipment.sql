DROP VIEW IF EXISTS place_equipment;

CREATE VIEW place_equipment AS

SELECT ievs.*,
       ievsie.place_equipment_id as place_equipment_id

FROM installed_equipment_version_structure_installed_equipment AS ievsie

    INNER JOIN installed_equipment_version_structure ievs on ievsie.installed_equipment_id = ievs.id

ORDER BY netex_id ASC, version DESC

