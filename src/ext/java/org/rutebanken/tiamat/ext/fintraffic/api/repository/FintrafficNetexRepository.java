package org.rutebanken.tiamat.ext.fintraffic.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityOutRecord;
import org.rutebanken.tiamat.ext.fintraffic.api.model.FintrafficReadApiSearchKey;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiSearchKey;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FintrafficNetexRepository extends AbstractNetexRepository {

    public FintrafficNetexRepository(
            JdbcTemplate jdbc,
            ObjectMapper objectMapper) {
        super(jdbc, objectMapper);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Stream<ReadApiEntityOutRecord> streamStopPlaces(ReadApiSearchKey searchKey) {
        StringBuilder sql = new StringBuilder("""
            SELECT type, xml
            FROM ext_fintraffic_netex_entity
            WHERE status IN ('STALE', 'CURRENT') AND type IN (
                'ScheduledStopPoint',
                'PassengerStopAssignment',
                'TopographicPlace',
                'FareZone',
                'StopPlace',
                'Parking'
            )
         """);

        List<Object> params = new ArrayList<>();

        if (searchKey instanceof FintrafficReadApiSearchKey(String[] transportModes, String[] areaCodes, String[] municipalityCodes)) {
            List<String> filters = new ArrayList<>();

            if (transportModes != null && transportModes.length > 0) {
                filters.add("search_key->'transportModes' ??| ?::text[]");
                params.add(transportModes);
            }

            if (areaCodes != null && areaCodes.length > 0) {
                filters.add("search_key->'areaCodes' ??| ?::text[]");
                params.add(areaCodes);
            }

            if (municipalityCodes != null && municipalityCodes.length > 0) {
                filters.add("search_key->'municipalityCodes' ??| ?::text[]");
                params.add(municipalityCodes);
            }

            if (!filters.isEmpty()) {
                // TopographicPlace and FareZone are always included regardless of search filters
                // because they provide reference data needed for all stop place data
                sql.append(" AND (type IN ('TopographicPlace', 'FareZone') OR (");
                sql.append(String.join(" AND ", filters));
                sql.append("))");
            }
        }

        sql.append("""
        ORDER BY
            CASE type
                WHEN 'ScheduledStopPoint' THEN 1
                WHEN 'PassengerStopAssignment' THEN 2
                WHEN 'TopographicPlace' THEN 3
                WHEN 'FareZone' THEN 4
                WHEN 'StopPlace' THEN 5
                WHEN 'Parking' THEN 6
                ELSE 7
            END
        """);

        return jdbc.queryForStream(
                sql.toString(),
                (rs, rn) -> new ReadApiEntityOutRecord(rs.getString("type"), rs.getBytes("xml")),
                params.toArray()
        );
    }
}
