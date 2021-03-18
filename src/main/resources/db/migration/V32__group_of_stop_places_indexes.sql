CREATE INDEX group_of_stop_places_version_index ON group_of_stop_places(version);
CREATE SPATIAL INDEX group_of_stop_places_centroid_index ON group_of_stop_places(centroid) WITH
    (
    BOUNDING_BOX= (xmin=5, ymin=45, xmax=35, ymax=70),
    GRIDS= ( LEVEL_3= HIGH, LEVEL_2 = HIGH )
    );
CREATE INDEX group_of_stop_places_name_value_index ON group_of_stop_places(name_value);
CREATE INDEX group_of_stop_places_netex_id_index ON group_of_stop_places(netex_id);
CREATE INDEX group_of_stop_places_trgm_idx ON group_of_stop_places(name_value);
