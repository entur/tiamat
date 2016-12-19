CREATE INDEX stop_place_centroid_index ON stop_place USING GIST ( centroid );

DROP TABLE IF EXISTS id_generator;
CREATE TABLE id_generator(table_name text, id_value bigint);