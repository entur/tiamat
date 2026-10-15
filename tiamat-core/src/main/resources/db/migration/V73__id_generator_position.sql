-- Where the gapless ID generator continues its search, per entity type.
-- Before this migration the generator kept this state in Hazelcast.
CREATE TABLE id_generator_position (
    table_name character varying(50) PRIMARY KEY,
    last_id bigint NOT NULL
);

-- Generates the next free ID for an entity type in one round trip. The advisory lock lets one caller
-- at a time move the position, and it is released when the calling statement's transaction ends.
-- An ID that is claimed between the search and the insert is skipped, so claims need no lock.
CREATE FUNCTION next_gapless_id(entity_type character varying, lock_namespace integer, lock_name text, search_window integer)
RETURNS bigint
LANGUAGE plpgsql
AS $$
DECLARE
    search_from bigint;
    free_id bigint;
BEGIN
    PERFORM pg_advisory_xact_lock(lock_namespace, hashtext(lock_name));

    SELECT last_id + 1 INTO search_from FROM id_generator_position WHERE table_name = entity_type;
    search_from := coalesce(search_from, 1);

    LOOP
        SELECT candidate INTO free_id
        FROM generate_series(search_from, search_from + search_window - 1) candidate
        WHERE NOT EXISTS (SELECT 1 FROM id_generator WHERE table_name = entity_type AND id_value = candidate)
        ORDER BY candidate
        LIMIT 1;

        IF free_id IS NULL THEN
            search_from := search_from + search_window;
        ELSE
            INSERT INTO id_generator (table_name, id_value) VALUES (entity_type, free_id)
            ON CONFLICT (table_name, id_value) DO NOTHING;
            EXIT WHEN FOUND;
            search_from := free_id + 1;
        END IF;
    END LOOP;

    INSERT INTO id_generator_position (table_name, last_id) VALUES (entity_type, free_id)
    ON CONFLICT (table_name) DO UPDATE SET last_id = excluded.last_id;

    RETURN free_id;
END $$;

-- Before this migration, claimed IDs were held in Hazelcast for up to 15 seconds before they were
-- written to id_generator. If every instance stopped within that window, an ID that was already in
-- use could be generated again. Record every generated ID that is in use, so that cannot happen.
DO $$
DECLARE
    entity_table record;
BEGIN
    FOR entity_table IN
        SELECT table_name FROM information_schema.columns
        WHERE table_schema = current_schema() AND column_name = 'netex_id'
    LOOP
        EXECUTE format(
            'INSERT INTO id_generator (table_name, id_value)
             SELECT parts[1], parts[2]::bigint
             FROM (SELECT regexp_match(netex_id, %L) AS parts FROM %I) ids
             WHERE parts IS NOT NULL
             ON CONFLICT (table_name, id_value) DO NOTHING',
            '^${netexValidPrefix}:([A-Za-z]+):([0-9]+)$',
            entity_table.table_name);
    END LOOP;
END $$;
