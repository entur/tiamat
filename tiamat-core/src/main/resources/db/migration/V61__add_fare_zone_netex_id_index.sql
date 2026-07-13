CREATE INDEX idx_fare_zone_netex_id ON fare_zone (netex_id);
CREATE INDEX idx_fare_zone_netex_id_version ON fare_zone (netex_id, version);
CREATE INDEX idx_fare_zone_validity ON fare_zone (netex_id, version, from_date, to_date);