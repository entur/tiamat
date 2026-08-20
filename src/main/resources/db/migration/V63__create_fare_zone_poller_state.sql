-- Single-row record of the snapshot FareZonePoller last imported, shared by all replicas.
CREATE TABLE fare_zone_poller_state (
    id SMALLINT NOT NULL PRIMARY KEY,
    last_imported_hash VARCHAR(64),
    last_imported_at TIMESTAMP WITHOUT TIME ZONE
);

INSERT INTO fare_zone_poller_state (id) VALUES (1);
