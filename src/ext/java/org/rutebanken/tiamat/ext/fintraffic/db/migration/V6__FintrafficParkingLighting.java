package org.rutebanken.tiamat.ext.fintraffic.db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

/**
 * Creates the {@code parking_fintraffic_lighting} collection table used by
 * {@link org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking} to persist the
 * {@code lighting} field that is {@code @Transient} in the core {@code SiteElement} model.
 * <p>
 * Using a separate collection table (rather than a column on {@code parking}) keeps
 * Entur's core DDL unmodified and prevents Hibernate from selecting ext-only columns in
 * core tests that do not run the Fintraffic Flyway migrations.
 */
public class V6__FintrafficParkingLighting extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        try (Statement stmt = context.getConnection().createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS parking_fintraffic_lighting (
                        parking_id BIGINT NOT NULL REFERENCES parking(id),
                        lighting   VARCHAR(64) NOT NULL
                    )
                    """);
        }
    }
}
