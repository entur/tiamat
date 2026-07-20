package org.rutebanken.tiamat.ext.fintraffic.db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

/**
 * Creates the {@code parking_vehicle_entrances} collection table required by
 * {@link org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking#getVehicleEntrances()}.
 * <p>
 * {@code entrance_type} stores the NeTEx {@code EntranceEnumeration} value string
 * (e.g. {@code door}, {@code gate}).  A CHECK constraint enforces the allowed set;
 * nullable because the attribute is optional in the NeTEx schema.
 */
public class V5__FintrafficParkingVehicleEntrances extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        String sql = """
                CREATE TABLE IF NOT EXISTS parking_vehicle_entrances (
                    parking_id    BIGINT NOT NULL REFERENCES parking(id),
                    label         VARCHAR(255),
                    entrance_type VARCHAR(64) CHECK (entrance_type IN (
                        'opening', 'openDoor', 'door', 'swingDoor', 'revolvingDoor',
                        'automaticDoor', 'ticketBarrier', 'gate', 'other'
                    )),
                    width         NUMERIC(10, 2),
                    height        NUMERIC(10, 2),
                    is_entry      BOOLEAN,
                    is_exit       BOOLEAN,
                    public_code   VARCHAR(64)
                );

                CREATE INDEX IF NOT EXISTS idx_parking_vehicle_entrances_parking_id
                    ON parking_vehicle_entrances (parking_id);
                """;

        try (Statement stmt = context.getConnection().createStatement()) {
            stmt.execute(sql);
        }
    }
}
