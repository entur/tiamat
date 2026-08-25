package org.rutebanken.tiamat.ext.fintraffic.db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

/**
 * Adds the {@code access_modes} column to {@code parking_vehicle_entrances}, storing the
 * NeTEx {@code AccessModes} value for {@link org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParkingEntranceForVehicles}.
 * <p>
 * {@code AccessModes} is an XML list-typed field (a single element containing
 * space-separated {@code AccessModeEnumeration} tokens, e.g. {@code "foot bicycle"}),
 * so it is stored verbatim as a single column rather than a nested collection table.
 */
public class V8__FintrafficParkingVehicleEntranceAccessModes extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        String sql = """
                ALTER TABLE parking_vehicle_entrances
                    ADD COLUMN IF NOT EXISTS access_modes VARCHAR(128);
                """;

        try (Statement stmt = context.getConnection().createStatement()) {
            stmt.execute(sql);
        }
    }
}
