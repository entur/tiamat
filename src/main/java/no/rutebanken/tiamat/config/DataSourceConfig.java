package no.rutebanken.tiamat.config;

import no.rutebanken.tiamat.datasource.GeoDBInMemoryDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource geoDbDataSource() {

        return new GeoDBInMemoryDataSourceFactory();
    }
}
