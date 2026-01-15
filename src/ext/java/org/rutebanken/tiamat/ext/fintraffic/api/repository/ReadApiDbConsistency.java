package org.rutebanken.tiamat.ext.fintraffic.api.repository;

import org.rutebanken.netex.model.EntityInVersionStructure;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.PassengerStopAssignment;
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.StopPlace;

class ReadApiDbConsistency {
    protected static <T extends EntityInVersionStructure> String getDbConsistencySql(Class<T> entityClass) {
        if (entityClass.equals(Parking.class)) {
            return PARKING_CONSISTENCY_SQL;
        } else if (entityClass.equals(PassengerStopAssignment.class)) {
            return PASSENGER_STOP_ASSIGNMENT_CONSISTENCY_SQL;
        } else if (entityClass.equals(ScheduledStopPoint.class)) {
            return SCHEDULED_STOP_POINT_CONSISTENCY_SQL;
        } else if (entityClass.equals(StopPlace.class)) {
            return STOP_PLACE_CONSISTENCY_SQL;
        } else {
            throw new IllegalArgumentException("Unknown entity type: " + entityClass.getSimpleName());
        }
    }

    private static final String STOP_PLACE_CONSISTENCY_SQL = """
            WITH current_stopplace AS (
                SELECT s.id,
                       s.netex_id,
                       s.version
                FROM stop_place s
                         LEFT JOIN stop_place p
                                   ON s.parent_site_ref = p.netex_id
                                       AND CAST(s.parent_site_ref_version AS bigint) = p.version
                WHERE (
                    (
                        s.from_date <= current_timestamp + interval '2 hours'
                            AND (
                            s.to_date >= current_timestamp + interval '2 hours'
                                OR s.to_date IS NULL
                            )
                        )
                        OR (
                        p.from_date <= current_timestamp + interval '2 hours'
                            AND (
                            p.to_date >= current_timestamp + interval '2 hours'
                                OR p.to_date IS NULL
                            )
                        )
                    )
                  AND s.parent_stop_place = false
            )
            -- Missing entities in ext_fintraffic_netex_entity (MISS)
            SELECT cs.netex_id, cs.version, 'MISS' AS issue
            FROM current_stopplace cs
                     LEFT JOIN ext_fintraffic_netex_entity e
                               ON cs.netex_id = e.id
                                   AND cs.version = e.version
                                   AND e.status != 'DELETED'
            WHERE e.id IS NULL
            UNION ALL
            -- EXTRA: entries in ext_fintraffic_netex_entity not present in the current model
            SELECT e.id        AS netex_id,
                   e.version   AS version,
                   'EXTRA'     AS issue
            FROM ext_fintraffic_netex_entity e
                     LEFT JOIN current_stopplace x
                               ON e.id = x.netex_id
                                   AND e.version = x.version
            WHERE x.netex_id IS NULL
              AND e.type = 'StopPlace'
              AND e.status != 'DELETED'
            """;

    private static final String PARKING_CONSISTENCY_SQL = """
            WITH current_stopplace AS (
                SELECT s.id,
                       s.netex_id,
                       s.version
                FROM stop_place s
                         LEFT JOIN stop_place p
                                   ON s.parent_site_ref = p.netex_id
                                       AND CAST(s.parent_site_ref_version AS bigint) = p.version
                WHERE (
                    (
                        s.from_date <= current_timestamp + interval '2 hours'
                            AND (
                            s.to_date >= current_timestamp + interval '2 hours'
                                OR s.to_date IS NULL
                            )
                        )
                        OR (
                        p.from_date <= current_timestamp + interval '2 hours'
                            AND (
                            p.to_date >= current_timestamp + interval '2 hours'
                                OR p.to_date IS NULL
                            )
                        )
                    )
                  AND s.parent_stop_place = false
            ), current_parking AS (
              SELECT p.netex_id,
                     p.version
              FROM parking p JOIN current_stopplace s ON p.parent_site_ref = s.netex_id
            )
            -- Missing entities in ext_fintraffic_netex_entity (MISS)
            SELECT p.netex_id, p.version, 'MISS' AS issue
            FROM current_parking p
                     LEFT JOIN ext_fintraffic_netex_entity e
                               ON p.netex_id = e.id
                                   AND p.version = e.version
                                   AND e.status != 'DELETED'
            WHERE e.id IS NULL
            UNION ALL
            -- EXTRA: entries in ext_fintraffic_netex_entity not present in the current model
            SELECT e.id        AS netex_id,
                   e.version   AS version,
                   'EXTRA'     AS issue
            FROM ext_fintraffic_netex_entity e
                     LEFT JOIN current_parking x
                               ON e.id = x.netex_id
                                   AND e.version = x.version
            WHERE x.netex_id IS NULL
              AND e.type = 'Parking'
              AND e.status != 'DELETED'
            """;

    private static final String SCHEDULED_STOP_POINT_CONSISTENCY_SQL = """
            WITH current_stopplace AS (
                SELECT s.id,
                       s.netex_id,
                       s.version
                FROM stop_place s
                         LEFT JOIN stop_place p
                                   ON s.parent_site_ref = p.netex_id
                                       AND CAST(s.parent_site_ref_version AS bigint) = p.version
                WHERE (
                    (
                        s.from_date <= current_timestamp + interval '2 hours'
                            AND (
                            s.to_date >= current_timestamp + interval '2 hours'
                                OR s.to_date IS NULL
                            )
                        )
                        OR (
                        p.from_date <= current_timestamp + interval '2 hours'
                            AND (
                            p.to_date >= current_timestamp + interval '2 hours'
                                OR p.to_date IS NULL
                            )
                        )
                    )
                  AND s.parent_stop_place = false
            ), current_quay_scheduled_stop_point AS (
              SELECT q.netex_id,
                     q.version
              FROM current_stopplace cs
              JOIN stop_place_quays spqs ON spqs.stop_place_id = cs.id
              JOIN quay q ON spqs.quays_id = q.id
            ), all_scheduled_stop_point AS (
                SELECT split_part(netex_id, ':', 1) || ':ScheduledStopPoint:Q' || split_part(netex_id, ':', 3) AS netex_id, version
                       FROM current_quay_scheduled_stop_point
                       UNION ALL
                SELECT split_part(netex_id, ':', 1) || ':ScheduledStopPoint:S' || split_part(netex_id, ':', 3) AS netex_id, version
                    FROM current_stopplace
            )
            -- Missing entities in ext_fintraffic_netex_entity (MISS)
            SELECT p.netex_id, p.version, 'MISS' AS issue
            FROM all_scheduled_stop_point p
                     LEFT JOIN ext_fintraffic_netex_entity e
                               ON p.netex_id = e.id
                                   AND p.version = e.version
                                   AND e.status != 'DELETED'
            WHERE e.id IS NULL
            UNION ALL
            -- EXTRA: entries in ext_fintraffic_netex_entity not present in the current model
            SELECT e.id        AS netex_id,
                   e.version   AS version,
                   'EXTRA'     AS issue
            FROM ext_fintraffic_netex_entity e
                     LEFT JOIN all_scheduled_stop_point x
                               ON e.id = x.netex_id
                                   AND e.version = x.version
            WHERE x.netex_id IS NULL
              AND e.type = 'ScheduledStopPoint'
              AND e.status != 'DELETED'
            """;

    private static final String PASSENGER_STOP_ASSIGNMENT_CONSISTENCY_SQL = """
            WITH current_stopplace AS (
                SELECT s.id,
                       s.netex_id,
                       s.version
                FROM stop_place s
                         LEFT JOIN stop_place p
                                   ON s.parent_site_ref = p.netex_id
                                       AND CAST(s.parent_site_ref_version AS bigint) = p.version
                WHERE (
                    (
                        s.from_date <= current_timestamp + interval '2 hours'
                            AND (
                            s.to_date >= current_timestamp + interval '2 hours'
                                OR s.to_date IS NULL
                            )
                        )
                        OR (
                        p.from_date <= current_timestamp + interval '2 hours'
                            AND (
                            p.to_date >= current_timestamp + interval '2 hours'
                                OR p.to_date IS NULL
                            )
                        )
                    )
                  AND s.parent_stop_place = false
            ), current_quay_passenger_stop_assignment AS (
                SELECT q.netex_id,
                       q.version
                FROM current_stopplace cs
                         JOIN stop_place_quays spqs ON spqs.stop_place_id = cs.id
                         JOIN quay q ON spqs.quays_id = q.id
            ), all_scheduled_stop_point AS (
                SELECT split_part(netex_id, ':', 1) || ':PassengerStopAssignment:PQ' || split_part(netex_id, ':', 3) AS netex_id, version
                FROM current_quay_passenger_stop_assignment
                UNION ALL
                SELECT split_part(netex_id, ':', 1) || ':PassengerStopAssignment:PS' || split_part(netex_id, ':', 3) AS netex_id, version
                FROM current_stopplace
            )
            -- Missing entities in ext_fintraffic_netex_entity (MISS)
            SELECT p.netex_id, p.version, 'MISS' AS issue
            FROM all_scheduled_stop_point p
                     LEFT JOIN ext_fintraffic_netex_entity e
                               ON p.netex_id = e.id
                                   AND p.version = e.version
                                   AND e.status != 'DELETED'
            WHERE e.id IS NULL
            UNION ALL
            -- EXTRA: entries in ext_fintraffic_netex_entity not present in the current model
            SELECT e.id        AS netex_id,
                   e.version   AS version,
                   'EXTRA'     AS issue
            FROM ext_fintraffic_netex_entity e
                     LEFT JOIN all_scheduled_stop_point x
                               ON e.id = x.netex_id
                                   AND e.version = x.version
            WHERE x.netex_id IS NULL
              AND e.type = 'PassengerStopAssignment'
              AND e.status != 'DELETED'
            """;
}
