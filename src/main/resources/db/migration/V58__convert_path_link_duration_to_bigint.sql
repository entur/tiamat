-- Convert duration columns from bytea to bigint (nanoseconds) for java.time.Duration compatibility
-- Hibernate 6 maps java.time.Duration to bigint representing nanoseconds

ALTER TABLE path_link
    ALTER COLUMN default_duration TYPE bigint USING NULL,
    ALTER COLUMN frequent_traveller_duration TYPE bigint USING NULL,
    ALTER COLUMN mobility_restricted_traveller_duration TYPE bigint USING NULL,
    ALTER COLUMN occasional_traveller_duration TYPE bigint USING NULL;