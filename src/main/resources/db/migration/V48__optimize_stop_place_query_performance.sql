-- Optimize stop place query performance
-- These indexes address the performance issues in StopPlaceRepositoryImpl.findStopPlacesWithin

-- Composite index for efficient MAX(version) queries grouped by netex_id
-- This will significantly speed up the subquery that finds latest versions
CREATE INDEX IF NOT EXISTS idx_stop_place_netex_version
    ON stop_place (netex_id, version DESC);

-- Index for parent_stop_place boolean filter
-- Partial index only for non-parent stops since query uses "parent_stop_place = false"
CREATE INDEX IF NOT EXISTS idx_stop_place_not_parent
    ON stop_place (parent_stop_place)
    WHERE parent_stop_place = false;

-- Composite index for the parent join operation
-- Combines both columns used in the JOIN condition for better performance
CREATE INDEX IF NOT EXISTS idx_stop_place_parent_ref_composite
    ON stop_place (parent_site_ref, parent_site_ref_version)
    WHERE parent_site_ref IS NOT NULL;

-- Additional index to help with the GROUP BY operation when filtering
CREATE INDEX IF NOT EXISTS idx_stop_place_centroid_netex
    ON stop_place (netex_id)
    WHERE parent_stop_place = false;

-- Functional index for parent joins with cast operation
-- This supports queries like: CAST(s.parent_site_ref_version AS bigint) = p.version
CREATE INDEX IF NOT EXISTS idx_stop_place_parent_ref_version_cast
    ON stop_place (parent_site_ref, CAST(parent_site_ref_version AS bigint))
    WHERE parent_site_ref IS NOT NULL AND parent_site_ref_version IS NOT NULL;

-- Similar functional index for topographic places if needed
CREATE INDEX IF NOT EXISTS idx_topographic_place_parent_ref_version_cast
    ON topographic_place (parent_ref, CAST(parent_ref_version AS bigint))
    WHERE parent_ref IS NOT NULL AND parent_ref_version IS NOT NULL;
