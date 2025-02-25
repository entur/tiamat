CREATE OR REPLACE FUNCTION get_stop_place_key_value(stop_id BIGINT, values_key VARCHAR)
RETURNS VARCHAR AS $$
    SELECT vi.items AS value
    FROM stop_place_key_values spkv
    INNER JOIN value_items vi ON spkv.key_values_id = vi.value_id
    WHERE spkv.stop_place_id = stop_id AND spkv.key_values_key = values_key
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_stop_place_validity_period(stop_id BIGINT)
RETURNS TABLE(id BIGINT, date_range TSTZRANGE) AS $$
    SELECT stop_id, TSTZRANGE(TO_DATE(range_start, 'YYYY-MM-DD'),
           TO_DATE(range_end, 'YYYY-MM-DD'), '[)')
    FROM get_stop_place_key_value(stop_id, 'validityEnd') range_end
    INNER JOIN get_stop_place_key_value(stop_id, 'validityStart') range_start ON TRUE
    WHERE range_start IS NOT NULL
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION check_stop_place_unique_names()
RETURNS TRIGGER AS $$
BEGIN
    -- Find if there are any stop places with the same name with overlapping validity periods
    IF EXISTS (
        SELECT 1 FROM (
            SELECT old_range.date_range AS old_range,
                   new_range.date_range AS new_range
            FROM stop_place sp
            INNER JOIN get_stop_place_validity_period(sp.id) old_range ON sp.id = old_range.id
            INNER JOIN get_stop_place_validity_period(NEW.id) new_range ON NEW.id = new_range.id
            WHERE sp.name_value = NEW.name_value
            AND sp.version = (
                SELECT MAX(version)
                FROM stop_place
                WHERE netex_id = sp.netex_id AND netex_id != NEW.netex_id
            )
        ) stop_with_dates WHERE old_range && new_range
    ) THEN
        RAISE EXCEPTION 'STOP_PLACE_UNIQUE_NAME : Name % already exists for a stop place.', NEW.name_value;
    END IF;
    -- Find if there are any stop places with the same private code with overlapping validity periods
    IF EXISTS (
        SELECT 1 FROM (
            SELECT old_range.date_range AS old_range,
                   new_range.date_range AS new_range
            FROM stop_place sp
            INNER JOIN get_stop_place_validity_period(sp.id) old_range ON sp.id = old_range.id
            INNER JOIN get_stop_place_validity_period(NEW.id) new_range ON NEW.id = new_range.id
            WHERE sp.private_code_value = NEW.private_code_value
            AND sp.version = (
                SELECT MAX(version)
                FROM stop_place
                WHERE netex_id = sp.netex_id AND netex_id != NEW.netex_id
            )
        ) stop_with_dates WHERE old_range && new_range
    ) THEN
        RAISE EXCEPTION 'STOP_PLACE_UNIQUE_PRIVATE_CODE : Private code % already exists for a stop place.', NEW.private_code_value;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER stop_place_unique_names_trigger
AFTER INSERT OR UPDATE ON stop_place DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION check_stop_place_unique_names();
