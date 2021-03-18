package org.rutebanken.tiamat.config;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.rutebanken.tiamat.properties.NsrTiamatDatabaseProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!geodb")
public class SqlServerDBDataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(SqlServerDBDataSourceConfig.class);
    @Bean
    @ConfigurationProperties("app.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("app.datasource")
    public DataSource sqlServerDbDataSource() {
        logger.info("Setting up SQL Server connection through sqlServerDbDataSource");
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(NsrTiamatDatabaseProperties.getDbDriver());
        hikariConfig.setJdbcUrl(NsrTiamatDatabaseProperties.getDbUrl());
        hikariConfig.setUsername(NsrTiamatDatabaseProperties.getDbUser());
        hikariConfig.setPassword(NsrTiamatDatabaseProperties.getDbPassword());
        try {
            HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
            logger.info("Finished setting up a new SQL Server connection through sqlServerDbDataSource");
            return hikariDataSource;
        } catch (Exception e) {
            logger.error("Failed to set up a new SQL Server connection through sqlServerDbDataSource: " + e.getMessage());
            throw e;
        }
    }
}
