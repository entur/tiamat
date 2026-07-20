package org.rutebanken.tiamat.ext.fintraffic.db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

/**
 * Adds a {@code lighting} column to the {@code parking} table, used by
 * {@link org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking} to persist the
 * {@code lighting} field that is {@code @Transient} in the core {@code SiteElement} model.
 * <p>
 * Only rows with {@code dtype = 'FintrafficParking'} will have non-null values.
 */
public class V6__FintrafficParkingLighting extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        try (Statement stmt = context.getConnection().createStatement()) {
            stmt.execute("ALTER TABLE parking ADD COLUMN IF NOT EXISTS lighting VARCHAR(64)");
        }
    }
}
