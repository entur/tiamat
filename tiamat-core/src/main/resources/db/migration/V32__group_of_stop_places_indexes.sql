CREATE INDEX IF NOT EXISTS group_of_stop_places_version_index ON group_of_stop_places(version);
CREATE INDEX IF NOT EXISTS group_of_stop_places_centroid_index ON group_of_stop_places USING gist (centroid);
CREATE INDEX IF NOT EXISTS group_of_stop_places_name_value_index ON group_of_stop_places USING btree (name_value);
CREATE INDEX IF NOT EXISTS group_of_stop_places_netex_id_index ON group_of_stop_places USING btree (netex_id);
CREATE INDEX IF NOT EXISTS group_of_stop_places_trgm_idx ON group_of_stop_places USING gist (name_value gist_trgm_ops);
