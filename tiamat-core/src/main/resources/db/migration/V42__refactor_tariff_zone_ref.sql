ALTER TABLE stop_place_tariff_zones DROP COLUMN tariff_zones_id;
ALTER TABLE stop_place_tariff_zones ADD COLUMN ref varchar(255);
ALTER TABLE stop_place_tariff_zones ADD COLUMN version varchar(255);


ALTER TABLE fare_zone_neighbours DROP COLUMN neighbours_id;
ALTER TABLE fare_zone_neighbours ADD COLUMN ref varchar(255);
ALTER TABLE fare_zone_neighbours ADD COLUMN version varchar(255);


DROP TABLE tariff_zone_ref;
