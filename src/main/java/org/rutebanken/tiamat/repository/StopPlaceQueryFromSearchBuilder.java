package org.rutebanken.tiamat.repository;

import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

/**
 * Builds query from stop place search params
 */
@Component
public class StopPlaceQueryFromSearchBuilder extends SearchBuilder {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceQueryFromSearchBuilder.class);


    public Pair<String, Map<String, Object>> buildQueryString(ExportParams exportParams) {

        StopPlaceSearch stopPlaceSearch = exportParams.getStopPlaceSearch();

        StringBuilder queryString = new StringBuilder("select s.* from stop_place s ");

        List<String> wheres = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        List<String> operators = new ArrayList<>();
        List<String> orderByStatements = new ArrayList<>();

        boolean hasIdFilter = stopPlaceSearch.getNetexIdList() != null && !stopPlaceSearch.getNetexIdList().isEmpty();

        if (hasIdFilter) {
            wheres.add("s.netex_id in :netexIdList");
            parameters.put("netexIdList", stopPlaceSearch.getNetexIdList());
        } else {
            if (stopPlaceSearch.getQuery() != null) {

                parameters.put("query", stopPlaceSearch.getQuery());
                operators.add("and");

                if (NetexIdHelper.isNetexId(stopPlaceSearch.getQuery())) {
                    String netexId = stopPlaceSearch.getQuery();

                    String netexIdType = NetexIdHelper.extractIdType(netexId);

                    // Detect non NSR NetexId and search in original ID
                    if (!NetexIdHelper.isNsrId(stopPlaceSearch.getQuery())) {
                        parameters.put("originalIdKey", ORIGINAL_ID_KEY);

                        if (StopPlace.class.getSimpleName().equals(netexIdType)) {
                            wheres.add("s.id in (select spkv.stop_place_id from stop_place_key_values spkv inner join value_items v on spkv.key_values_id = v.value_id where spkv.key_values_key = :originalIdKey and v.items = :query)");
                        } else if (Quay.class.getSimpleName().equals(netexIdType)) {
                            wheres.add("s.id in (select spq.stop_place_id from stop_place_quays spq inner join quay_key_values qkv on spq.quays_id = qkv.quay_id inner join value_items v on qkv.key_values_id = v.value_id where qkv.key_values_key = :originalIdKey and v.items = :query)");
                        } else {
                            logger.warn("Detected NeTEx ID {}, but type is not supported: {}", netexId, NetexIdHelper.extractIdType(netexId));
                        }
                    } else {
                        // NSR ID detected

                        if (StopPlace.class.getSimpleName().equals(netexIdType)) {
                            wheres.add("netex_id = :query");
                        } else if (Quay.class.getSimpleName().equals(netexIdType)) {
                            wheres.add("s.id in (select spq.stop_place_id from stop_place_quays spq inner join quay q on spq.quays_id = q.id and q.netex_id = :query)");
                        } else {
                            logger.warn("Detected NeTEx ID {}, but type is not supported: {}", netexId, NetexIdHelper.extractIdType(netexId));
                        }
                    }
                } else {
                    if (stopPlaceSearch.getQuery().length() <= 3) {
                        wheres.add("lower(s.name_value) like concat(lower(:query), '%')");
                    } else {
                        wheres.add("lower(s.name_value) like concat('%', lower(:query), '%')");
                    }

                    orderByStatements.add("similarity(s.name_value, :query) desc");
                }
            }

            if (stopPlaceSearch.getStopTypeEnumerations() != null && !stopPlaceSearch.getStopTypeEnumerations().isEmpty()) {
                wheres.add("s.stop_place_type in :stopPlaceTypes");
                parameters.put("stopPlaceTypes", stopPlaceSearch.getStopTypeEnumerations().stream().map(StopTypeEnumeration::toString).collect(toList()));
                operators.add("and");
            }

            boolean hasMunicipalityFilter = exportParams.getMunicipalityReferences() != null && !exportParams.getMunicipalityReferences().isEmpty();
            boolean hasCountyFilter = exportParams.getCountyReferences() != null && !exportParams.getCountyReferences().isEmpty();

            if (hasMunicipalityFilter && !hasIdFilter) {
                String prefix;
                if (hasCountyFilter) {
                    operators.add("or");
                    prefix = "(";
                } else prefix = "";

                wheres.add(prefix + "s.topographic_place_id in (select tp.id from topographic_place tp where tp.netex_id in :municipalityId)");
                parameters.put("municipalityId", exportParams.getMunicipalityReferences());
            }

            if (hasCountyFilter && !hasIdFilter) {
                String suffix = hasMunicipalityFilter ? ")" : "";
                wheres.add("s.topographic_place_id in (select tp.id from topographic_place tp where tp.parent_ref in :countyId)" + suffix);
                parameters.put("countyId", exportParams.getCountyReferences());
            }
        }

        if (stopPlaceSearch.getVersion() != null) {
            operators.add("and");
            wheres.add("s.version = :version");
            parameters.put("version", stopPlaceSearch.getVersion());
        } else if (!stopPlaceSearch.isAllVersions()) {
            operators.add("and");
            wheres.add("s.version = (select max(sv.version) from stop_place sv where sv.netex_id = s.netex_id)");
        }

        if (stopPlaceSearch.getPointInTime() != null) {
            operators.add("and");
            //(from- and toDate is NULL), or (fromDate is set and toDate IS NULL or set)
            wheres.add("((s.from_date IS NULL AND s.to_date IS NULL) OR (s.from_date <= :pointInTime AND (s.to_date IS NULL OR s.to_date > :pointInTime)))");
            parameters.put("pointInTime", Timestamp.from(stopPlaceSearch.getPointInTime()));
        }

        addWheres(queryString, wheres, operators);

        orderByStatements.add("netex_id, version asc");
        queryString.append(" order by");

        for (int i = 0; i < orderByStatements.size(); i++) {
            if (i > 0) {
                queryString.append(',');
            }
            queryString.append(' ').append(orderByStatements.get(i)).append(' ');
        }

        final String generatedSql = basicFormatter.format(queryString.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("{}", generatedSql);
            logger.debug("params: {}", parameters.toString());
        }
        return Pair.of(generatedSql, parameters);
    }
}
