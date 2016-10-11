package org.rutebanken.tiamat.config;

import org.rutebanken.tiamat.datasource.GeoDBInMemoryDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("geodb")
public class GeoDBDataSourceConfig {

    @Bean
    public DataSource geoDbDataSource() {

        return new GeoDBInMemoryDataSourceFactory();
    }
}
