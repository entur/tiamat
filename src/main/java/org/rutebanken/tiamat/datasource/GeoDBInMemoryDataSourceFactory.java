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