package org.rutebanken.tiamat.ext.fintraffic.db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

/**
 * Creates the {@code parking_info_links} collection table required by
 * {@link org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking#getInfoLinks()}.
 * <p>
 * {@code typeOfInfoLink} stores the NeTEx {@code TypeOfInfolinkEnumeration} value string
 * (e.g. {@code resource}, {@code info}).  A CHECK constraint enforces the allowed
 * set; nullable because the attribute is optional in the NeTEx schema.
 */
public class V4__FintrafficParkingInfoLinks extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        String sql = """
                CREATE TABLE IF NOT EXISTS parking_info_links (
                    parking_id          BIGINT NOT NULL REFERENCES parking(id),
                    uri                 VARCHAR(512) NOT NULL,
                    type_of_info_link   VARCHAR(64) CHECK (type_of_info_link IN (
                        'contact', 'resource', 'info', 'image', 'document',
                        'timetableDocument', 'fareSheet', 'dataLicence',
                        'mobileAppDownload', 'mobileAppInstallCheck', 'map', 'icon', 'other'
                    ))
                );

                CREATE INDEX IF NOT EXISTS idx_parking_info_links_parking_id
                    ON parking_info_links (parking_id);
                """;

        try (Statement stmt = context.getConnection().createStatement()) {
            stmt.execute(sql);
        }
    }
}
