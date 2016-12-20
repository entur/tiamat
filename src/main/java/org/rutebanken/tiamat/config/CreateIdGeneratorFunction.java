package org.rutebanken.tiamat.config;

import com.vividsolutions.jts.geom.GeometryFactory;
import org.rutebanken.tiamat.gtfs.GtfsStopsReader;
import org.rutebanken.tiamat.nvdb.service.NvdbStopPlaceRetrievalService;
import org.rutebanken.tiamat.service.StopPlaceFromQuaysCorrelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Create function in database for generating IDs
 * See https://rutebanken.atlassian.net/browse/NRP-23 for liquibase implementation.
 */
@Configuration
public class CreateIdGeneratorFunction implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(CreateIdGeneratorFunction.class);

    private static final String SQL = "CREATE OR REPLACE FUNCTION generate_next_available_id(entity text, desired_id integer) RETURNS integer AS $$\n" +
            "DECLARE\n" +
            "    next_value integer;\n" +
            "\n" +
            "BEGIN\n" +
            "    IF desired_id > 0 THEN\n" +
            "      IF EXISTS (SELECT id_value FROM id_generator WHERE id_value = desired_id AND table_name = entity) THEN\n" +
            "        RAISE 'Desired entity ID is already taken: % for entity %', desired_id, entity USING ERRCODE = 'unique_violation';\n" +
            "      ELSE\n" +
            "        INSERT INTO id_generator(table_name, id_value) VALUES(entity, desired_id);\n" +
            "        next_value := desired_id;\n" +
            "      END IF;\n" +
            "    ELSE\n" +
            "      SELECT id FROM (SELECT id_value + 1 AS id\n" +
            "        FROM id_generator g1\n" +
            "        WHERE NOT EXISTS(SELECT null FROM id_generator g2 WHERE g2.id_value = g1.id_value + 1 AND g2.table_name = entity)\n" +
            "          UNION\n" +
            "          SELECT 1 AS id\n" +
            "          WHERE NOT EXISTS (SELECT null FROM id_generator WHERE id_value = 1 AND table_name = entity)\n" +
            "          ) ot\n" +
            "        ORDER BY 1 INTO next_value;\n" +
            "      INSERT INTO id_generator(table_name, id_value) values(entity, next_value);\n" +
            "    END IF;\n" +
            "  RETURN next_value;\n" +
            "END;\n" +
            "$$ LANGUAGE 'plpgsql'\n";

    private static final AtomicInteger h2IdCounter = new AtomicInteger();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        if(jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductName().contains("H2")) {
            logger.info("H2 detected. Creating alias to method generateNextAvailableId.");
            jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS generate_next_available_id FOR \"org.rutebanken.tiamat.config.CreateIdGeneratorFunction.generateNextAvailableId\"");

        } else {
            logger.info("Executing create function\n{}", SQL);
            jdbcTemplate.execute(SQL);
            logger.info("Executed create function");
        }
    }

    private static Set<Integer> usedIds = new HashSet<>();

    public static Integer generateNextAvailableId(String entity, Integer desiredId) throws SQLException {

        if(desiredId > 0) {
            if (usedIds.contains(desiredId)) {
                throw new SQLException("Desired entity ID is already taken: " + desiredId + " for entity " + entity);
            } else {
                usedIds.add(desiredId);
                return desiredId;
            }
        }

        int i = 0;
        while(usedIds.contains(++i)) {
            logger.debug("Looking for next available ID. {} is taken", i);
        }

        usedIds.add(i);
        return i;

    }
}
