package no.rutebanken.tiamat.datasource;

import geodb.GeoDB;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Data source factory for initialization of GeoDB.
 *
 * http://kofler.nonblocking.at/2014/03/unit-testing-of-spatial-queries/
 */
public class GeoDBInMemoryDataSourceFactory extends SingleConnectionDataSource {

    public GeoDBInMemoryDataSourceFactory() {
        setDriverClassName("org.h2.Driver");
        setUrl("jdbc:h2:mem:tiamat;DB_CLOSE_ON_EXIT=FALSE");
        setSuppressClose(true);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = super.getConnection();
        GeoDB.InitGeoDB(conn);
        return conn;
    }
}