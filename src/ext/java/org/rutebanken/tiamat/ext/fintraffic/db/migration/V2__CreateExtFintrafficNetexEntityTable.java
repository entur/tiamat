package org.rutebanken.tiamat.ext.fintraffic.db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

public class V2__CreateExtFintrafficNetexEntityTable extends BaseJavaMigration {
    public void migrate(Context context) throws Exception {
        String sql =
                """
                -- Drop the old table if it exists
                DROP TABLE IF EXISTS ext_fintraffix_netex_entity;
                CREATE TABLE ext_fintraffic_netex_entity (
                    id TEXT PRIMARY KEY,
                    type TEXT NOT NULL,
                    search_key JSONB NOT NULL,
                    xml TEXT NOT NULL,
                    version INT NOT NULL,
                    changed BIGINT NOT NULL,
                    status VARCHAR(255) NOT NULL CHECK ( status = 'STALE' OR status = 'CURRENT' OR status = 'DELETED' ),
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    parent_refs TEXT[] NOT NULL
                );

                CREATE INDEX idx_ext_fintraffic_netex_entity_parent_refs
                    ON ext_fintraffic_netex_entity using gin (parent_refs);

                CREATE INDEX idx_ext_fintraffic_netex_entity_id_version_changed
                    ON ext_fintraffic_netex_entity (id, version, changed);
                """;

        try (Statement stmt = context.getConnection().createStatement()) {
            stmt.execute(sql);
        }
    }
}
