-- Optimize path link query performance
-- These indexes address the performance issues in PathLinkRepositoryImpl.findByStopPlaceIds

-- Index for path_link_end place_ref lookups
-- This is crucial for the JOIN on ple.place_ref = s.netex_id OR ple.place_ref = q.netex_id
CREATE INDEX IF NOT EXISTS idx_path_link_end_place_ref
    ON path_link_end (place_ref);

-- Composite index for path_link_end place_ref and place_version
-- Helps with conditions like: ple.place_ref = s.netex_id AND (ple.place_version = cast(s.version AS TEXT) OR ple.place_version is NULL)
CREATE INDEX IF NOT EXISTS idx_path_link_end_place_ref_version
    ON path_link_end (place_ref, place_version);

-- Index for path_link from_id and to_id for efficient JOIN operations
-- Helps with: pl2.from_id = ple.id OR pl2.to_id = ple.id
CREATE INDEX IF NOT EXISTS idx_path_link_from_id
    ON path_link (from_id);

CREATE INDEX IF NOT EXISTS idx_path_link_to_id
    ON path_link (to_id);

-- Composite index for path_link netex_id and version for sorting and grouping
-- Helps with: ORDER BY pl.netex_id, pl.version
CREATE INDEX IF NOT EXISTS idx_path_link_netex_version
    ON path_link (netex_id, version);

-- Index for stop_place_quays join table
-- This helps with the LEFT OUTER JOIN stop_place_quays
CREATE INDEX IF NOT EXISTS idx_stop_place_quays_stop_place_id
    ON stop_place_quays (stop_place_id);

CREATE INDEX IF NOT EXISTS idx_stop_place_quays_quays_id
    ON stop_place_quays (quays_id);

-- Composite index for stop_place_quays for covering index on joins
CREATE INDEX IF NOT EXISTS idx_stop_place_quays_composite
    ON stop_place_quays (stop_place_id, quays_id);

-- Index for quay netex_id and version
-- Helps with join conditions on quay
CREATE INDEX IF NOT EXISTS idx_quay_netex_version
    ON quay (netex_id, version);

-- Index for path_link_key_values for the findByKeyValue query
CREATE INDEX IF NOT EXISTS idx_path_link_key_values_key
    ON path_link_key_values (key_values_key);

CREATE INDEX IF NOT EXISTS idx_path_link_key_values_path_link_id
    ON path_link_key_values (path_link_id);

-- Composite index for path_link_key_values
CREATE INDEX IF NOT EXISTS idx_path_link_key_values_composite
    ON path_link_key_values (key_values_key, path_link_id);

-- Index for value_items used in path_link key-value searches
CREATE INDEX IF NOT EXISTS idx_value_items_value_id
    ON value_items (value_id);

-- Partial index for path_link_end where place_version is NULL
-- This helps with OR conditions checking for NULL place_version
CREATE INDEX IF NOT EXISTS idx_path_link_end_place_ref_null_version
    ON path_link_end (place_ref)
    WHERE place_version IS NULL;

-- Index for tag table (used in tag-based searches)
CREATE INDEX IF NOT EXISTS idx_tag_id_reference
    ON tag (netex_reference);

CREATE INDEX IF NOT EXISTS idx_tag_removed
    ON tag (removed)
    WHERE removed IS NULL;

-- Composite index for tag searches
CREATE INDEX IF NOT EXISTS idx_tag_id_reference_removed
    ON tag (netex_reference)
    WHERE removed IS NULL;

-- Additional indexes for tariff zones and fare zones relationships
CREATE INDEX IF NOT EXISTS idx_tariff_zone_netex_version
    ON tariff_zone (netex_id, version DESC);

CREATE INDEX IF NOT EXISTS idx_fare_zone_netex_version
    ON fare_zone (netex_id, version DESC);

-- Index for stop_place_tariff_zones join table
CREATE INDEX IF NOT EXISTS idx_stop_place_tariff_zones_composite
    ON stop_place_tariff_zones (stop_place_id, ref);

-- ANALYZE tables to update statistics for query planner
ANALYZE path_link;
ANALYZE path_link_end;
ANALYZE path_link_key_values;
ANALYZE stop_place_quays;
ANALYZE quay;
ANALYZE stop_place;
ANALYZE tag;
ANALYZE tariff_zone;
ANALYZE fare_zone;
ANALYZE stop_place_tariff_zones;
