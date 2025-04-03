CREATE OR REPLACE VIEW stop_place_max_version AS
SELECT DISTINCT ON (netex_id) netex_id, version, id
FROM stop_place
ORDER BY netex_id, version DESC;
