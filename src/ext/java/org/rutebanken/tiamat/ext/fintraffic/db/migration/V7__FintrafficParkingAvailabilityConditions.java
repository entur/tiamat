package org.rutebanken.tiamat.ext.fintraffic.db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

public class V7__FintrafficParkingAvailabilityConditions extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        String sql = """
                CREATE TABLE IF NOT EXISTS parking_availability_conditions (
                    parking_id   BIGINT NOT NULL REFERENCES parking(id),
                    day_type_ref VARCHAR(128) NOT NULL,
                    is_available BOOLEAN NOT NULL DEFAULT TRUE,
                    start_time   TIME,
                    end_time     TIME
                );

                CREATE INDEX IF NOT EXISTS idx_parking_avail_cond_parking_id
                    ON parking_availability_conditions (parking_id);
                """;

        try (Statement stmt = context.getConnection().createStatement()) {
            stmt.execute(sql);
        }
    }
}
