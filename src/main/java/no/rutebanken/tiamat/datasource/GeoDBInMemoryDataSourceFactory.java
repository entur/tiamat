package no.rutebanken.tiamat.datasource;

import geodb.GeoDB;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class GeoDBInMemoryDataSourceFactory extends SingleConnectionDataSource {

    public GeoDBInMemoryDataSourceFactory() {
        setDriverClassName("org.h2.Driver");
        setUrl("jdbc:h2:mem:test;DB_CLOSE_ON_EXIT=FALSE");
        setSuppressClose(true);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = super.getConnection();
        GeoDB.InitGeoDB(conn);
        return conn;
    }
}