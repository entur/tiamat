CREATE OR REPLACE FUNCTION check_group_of_stop_places_unique_names()
RETURNS TRIGGER AS $$
BEGIN
    -- Find if there are any groups of stop places with the same name with overlapping validity periods
    IF EXISTS (
        SELECT 1
        FROM group_of_stop_places gosp
        WHERE gosp.name_value = NEW.name_value
        AND gosp.version = (
            SELECT MAX(version)
            FROM group_of_stop_places
            WHERE netex_id = gosp.netex_id AND netex_id != NEW.netex_id
        )
        AND tstzrange(gosp.from_date, gosp.to_date, '[)') && tstzrange(NEW.from_date, NEW.to_date, '[)')
    ) THEN
        RAISE EXCEPTION 'GROUP_OF_STOP_PLACES_UNIQUE_NAME : Name % already exists for groups of stop places.', NEW.name_value;
    END IF;
    -- Find if there are any groups of stop places with the same description with overlapping validity periods
    IF EXISTS (
        SELECT 1
        FROM group_of_stop_places gosp
        WHERE gosp.description_value = NEW.description_value
        AND gosp.version = (
            SELECT MAX(version)
            FROM group_of_stop_places
            WHERE netex_id = gosp.netex_id AND netex_id != NEW.netex_id
        )
        AND tstzrange(gosp.from_date, gosp.to_date, '[)') && tstzrange(NEW.from_date, NEW.to_date, '[)')
    ) THEN
        RAISE EXCEPTION 'GROUP_OF_STOP_PLACES_UNIQUE_DESCRIPTION : Description % already exists for groups of stop places.', NEW.description_value;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER group_of_stop_places_unique_names_trigger
BEFORE INSERT OR UPDATE ON group_of_stop_places
FOR EACH ROW
EXECUTE FUNCTION check_group_of_stop_places_unique_names();
