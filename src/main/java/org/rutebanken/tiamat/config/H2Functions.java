/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.config;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
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

        try {
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            if (connection.getMetaData().getDatabaseProductName().contains("H2")) {
                logger.info("H2 detected. Creating alias to method similarity.");
                jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS similarity FOR \"org.rutebanken.tiamat.config.H2Functions.similarity\"");

                logger.info("H2. Creating alias to method generate_series");
                jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS generate_series FOR \"org.rutebanken.tiamat.config.H2Functions.generateSeries\"");
            }
            connection.close();
        } catch (SQLException sqlException) {
            logger.warn("Cannot create h2 aliases", sqlException);

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

    public static Set<BigInteger> generateSeries(BigInteger start, BigInteger stop) {
        Set<BigInteger> rangeSet = ContiguousSet.create(Range.closed(start, stop), DiscreteDomain.bigIntegers());
        logger.info("Generated range set: {}", rangeSet);
        return rangeSet;
    }
}
