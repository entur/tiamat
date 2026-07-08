-- Create sequence for persistable_multi_polygon
CREATE SEQUENCE persistable_multi_polygon_seq START WITH 1 INCREMENT BY 10;

-- Create table for persistable multi polygon
CREATE TABLE persistable_multi_polygon (
    id BIGINT NOT NULL PRIMARY KEY,
    multi_polygon GEOMETRY(MULTIPOLYGON, 4326)
);

-- Add multi_surface_id column to tables that inherit from Zone_VersionStructure
ALTER TABLE tariff_zone ADD COLUMN multi_surface_id BIGINT REFERENCES persistable_multi_polygon(id);
ALTER TABLE fare_zone ADD COLUMN multi_surface_id BIGINT REFERENCES persistable_multi_polygon(id);
ALTER TABLE topographic_place ADD COLUMN multi_surface_id BIGINT REFERENCES persistable_multi_polygon(id);
ALTER TABLE equipment_place ADD COLUMN multi_surface_id BIGINT REFERENCES persistable_multi_polygon(id);
ALTER TABLE stop_place ADD COLUMN multi_surface_id BIGINT REFERENCES persistable_multi_polygon(id);
ALTER TABLE parking ADD COLUMN multi_surface_id BIGINT REFERENCES persistable_multi_polygon(id);
ALTER TABLE quay ADD COLUMN multi_surface_id BIGINT REFERENCES persistable_multi_polygon(id);
ALTER TABLE access_space ADD COLUMN multi_surface_id BIGINT REFERENCES persistable_multi_polygon(id);
ALTER TABLE boarding_position ADD COLUMN multi_surface_id BIGINT REFERENCES persistable_multi_polygon(id);
ALTER TABLE parking_area ADD COLUMN multi_surface_id BIGINT REFERENCES persistable_multi_polygon(id);