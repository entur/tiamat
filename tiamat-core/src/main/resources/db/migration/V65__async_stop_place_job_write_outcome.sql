-- The job now reports what a write produced, and why a write failed, in a form a client can
-- branch on. See https://github.com/entur/tiamat/issues/459.
--
-- created_ids held the generated ids of a create only. It now holds one entry for every stop
-- place that the job wrote, whatever the operation, and each entry carries the version that the
-- write produced. The column is renamed to match.
--
-- No data migration accompanies the rename. The write API is off by default
-- (tiamat.write-api.enabled=false) and has no clients, so any existing row is development data.
-- An old entry deserializes with a null version and a null netexId.

ALTER TABLE async_stop_place_job RENAME COLUMN created_ids TO written_stop_places;

ALTER TABLE async_stop_place_job ADD COLUMN reason_code TEXT;
ALTER TABLE async_stop_place_job ADD COLUMN current_version BIGINT;
