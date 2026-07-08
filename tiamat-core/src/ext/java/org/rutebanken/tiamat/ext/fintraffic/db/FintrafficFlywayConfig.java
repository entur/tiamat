package org.rutebanken.tiamat.ext.fintraffic.db;

import jakarta.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.migration.JavaMigration;
import org.rutebanken.tiamat.ext.fintraffic.db.migration.V2__CreateExtFintrafficNetexEntityTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.List;

@Profile("fintraffic-read-api")
@Configuration
public class FintrafficFlywayConfig {
    private final Logger logger = LoggerFactory.getLogger(FintrafficFlywayConfig.class);

    private final DataSource dataSource;

    private static final List<Class<? extends JavaMigration>> migrations = List.of(
        V2__CreateExtFintrafficNetexEntityTable.class
    );

    public FintrafficFlywayConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void migrateExtFintrafficDb() {
        logger.info("Starting Fintraffic Flyway migration. Number of migrations: {}", migrations.size());
        Flyway flyway = Flyway.configure()
                .sqlMigrationPrefix("ext_fintraffic_")
                .javaMigrationClassProvider(() -> migrations)
                .dataSource(dataSource)
                .baselineOnMigrate(true)
                .table("ext_fintraffic_schema_version")
                .load();
        flyway.migrate();
        logger.info("Finished Fintraffic Flyway migration.");
    }
}
