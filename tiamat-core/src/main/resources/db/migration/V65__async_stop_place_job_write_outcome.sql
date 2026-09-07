-- The job now reports what a write produced, and why a write failed. See
-- https://github.com/entur/tiamat/issues/459.
--
-- created_ids held the generated ids of a create only. It now holds one entry for every stop
-- place that the job wrote, whatever the operation. Each entry carries the version that the
-- write produced. The new name matches.
--
-- No data migration accompanies the rename, and a row written before it cannot be read after it.
-- Hibernate reads this column with its own ObjectMapper, and that mapper rejects a property it
-- does not know. So the old createdId raises UnrecognizedPropertyException. It does not become
-- null.
--
-- This costs nothing today. The write API is off by default (tiamat.write-api.enabled=false) and
-- has no clients, so any existing row is development data. Truncate the table if an old row is
-- in the way.

ALTER TABLE async_stop_place_job RENAME COLUMN created_ids TO written_stop_places;

ALTER TABLE async_stop_place_job ADD COLUMN reason_code TEXT;
ALTER TABLE async_stop_place_job ADD COLUMN current_version BIGINT;
