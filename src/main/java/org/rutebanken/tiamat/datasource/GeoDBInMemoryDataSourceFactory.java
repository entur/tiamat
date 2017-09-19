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

package org.rutebanken.tiamat.datasource;

import com.zaxxer.hikari.HikariDataSource;
import geodb.GeoDB;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Data source factory for initialization of GeoDB.
 *
 * http://kofler.nonblocking.at/2014/03/unit-testing-of-spatial-queries/
 */
public class GeoDBInMemoryDataSourceFactory extends HikariDataSource {

    public GeoDBInMemoryDataSourceFactory() {
        setDriverClassName("org.h2.Driver");
        setJdbcUrl("jdbc:h2:mem:tiamat;DB_CLOSE_ON_EXIT=FALSE"); //;TRACE_LEVEL_FILE=4
        //setSuppressClose(true);
        setMaximumPoolSize(15);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        GeoDB.InitGeoDB(connection);
        return connection;
    }
}