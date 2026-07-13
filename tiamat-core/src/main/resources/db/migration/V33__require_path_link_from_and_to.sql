
DELETE FROM path_link where to_id is null OR from_id is null;
ALTER TABLE path_link ALTER COLUMN to_id SET NOT NULL;
ALTER TABLE path_link ALTER COLUMN from_id SET NOT NULL;