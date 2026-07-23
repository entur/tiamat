package org.rutebanken.tiamat.ext.fintraffic.db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

/**
 * Adds DDL required by {@code FintrafficParking}:
 * <ul>
 *   <li>{@code parking_payment_methods} collection table for persisted payment methods</li>
 * </ul>
 * Note: the {@code dtype} discriminator column is added by the core Flyway migration V62,
 * because {@code FintrafficParking} is compiled into the same jar and Hibernate always
 * requires {@code dtype} regardless of active Spring profiles.
 */
public class V3__FintrafficParkingExtensions extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        String sql = """
                CREATE TABLE IF NOT EXISTS parking_payment_methods (
                    parking_id  BIGINT      NOT NULL REFERENCES parking(id),
                    payment_method VARCHAR(64) NOT NULL
                );

                CREATE INDEX IF NOT EXISTS idx_parking_payment_methods_parking_id
                    ON parking_payment_methods (parking_id);
                """;

        try (Statement stmt = context.getConnection().createStatement()) {
            stmt.execute(sql);
        }
    }
}
