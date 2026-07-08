ALTER TABLE parking_area ADD CONSTRAINT parking_area_netex_id_version_constraint UNIQUE (netex_id, version);
ALTER TABLE parking ADD CONSTRAINT parking_netex_id_version_constraint UNIQUE (netex_id, version);
