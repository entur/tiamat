CREATE INDEX stop_place_centroid_index ON stop_place USING GIST ( centroid );

DROP TABLE IF EXISTS id_generator;
CREATE TABLE id_generator(table_name text, id_value bigint);

CREATE OR REPLACE FUNCTION generate_next_available_id(entity text) RETURNS integer AS $$
DECLARE
    next_value integer;
BEGIN
    SELECT id FROM (SELECT id_value + 1 AS id
      FROM id_generator g1
      WHERE NOT EXISTS(SELECT null FROM id_generator g2 WHERE g2.id_value = g1.id_value + 1 AND g2.table_name = entity)
      UNION
      SELECT 1 AS id
      WHERE NOT EXISTS (SELECT null FROM id_generator WHERE id_value = 1 AND table_name = entity)
      ) ot
    ORDER BY 1 INTO next_value;

    INSERT INTO id_generator(table_name, id_value) values(entity, next_value);
  RETURN next_value;
END;
$$ LANGUAGE 'plpgsql';