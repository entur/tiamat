package org.rutebanken.tiamat.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Create funciton in database for generating IDs
 * See https://rutebanken.atlassian.net/browse/NRP-23 for liquibase implementation.
 */
@Configuration
public class H2Functions implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(H2Functions.class);

    private static double similarity = 1;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        if(jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductName().contains("H2")) {
            logger.info("H2 detected. Creating alias to method similarity.");
            jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS similarity FOR \"org.rutebanken.tiamat.config.H2Functions.similarity\"");

        }
    }

    /**
     * @param value from 0 to 1
     */
    public static void setSimilarity(double value) {
        similarity = value;
    }

    public static double similarity(String value, String value2) {
        logger.info("Return similarity 1");
        return similarity;
    }
}
