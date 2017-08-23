package org.rutebanken.tiamat.repository;

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
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.MERGED_ID_KEY;
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
        queryString.append("left join stop_place p on s.parent_site_ref = p.netex_id and s.parent_site_ref_version = cast(p.version as text) ");

        List<String> wheres = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        List<String> operators = new ArrayList<>();
        List<String> orderByStatements = new ArrayList<>();

        boolean hasIdFilter = stopPlaceSearch.getNetexIdList() != null && !stopPlaceSearch.getNetexIdList().isEmpty();

        if (hasIdFilter) {
            wheres.add("(s.netex_id in :netexIdList OR p.netex_id in :netexIdList)");
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
                        parameters.put("mergedIdKey", MERGED_ID_KEY);

                        if (StopPlace.class.getSimpleName().equals(netexIdType)) {
                            String keyValuesQuery = "id in (select spkv.stop_place_id from stop_place_key_values spkv inner join value_items v on spkv.key_values_id = v.value_id where (spkv.key_values_key = :originalIdKey OR spkv.key_values_key = :mergedIdKey) and v.items = :query)";
                            wheres.add("(s."+keyValuesQuery +" OR p."+keyValuesQuery+")");
                        } else if (Quay.class.getSimpleName().equals(netexIdType)) {
                            wheres.add("s.id in (select spq.stop_place_id from stop_place_quays spq inner join quay_key_values qkv on spq.quays_id = qkv.quay_id inner join value_items v on qkv.key_values_id = v.value_id where (qkv.key_values_key = :originalIdKey OR qkv.key_values_key = :mergedIdKey) and v.items = :query)");
                        } else {
                            logger.warn("Detected NeTEx ID {}, but type is not supported: {}", netexId, NetexIdHelper.extractIdType(netexId));
                        }
                    } else {
                        // NSR ID detected

                        if (StopPlace.class.getSimpleName().equals(netexIdType)) {
                            wheres.add("(s.netex_id = :query or p.netex_id = :query)");
                        } else if (Quay.class.getSimpleName().equals(netexIdType)) {
                            wheres.add("s.id in (select spq.stop_place_id from stop_place_quays spq inner join quay q on spq.quays_id = q.id and q.netex_id = :query)");
                        } else {
                            logger.warn("Detected NeTEx ID {}, but type is not supported: {}", netexId, NetexIdHelper.extractIdType(netexId));
                        }
                    }
                } else {

                    final String startingWithLowerMatchQuerySql = "concat(lower(:query), '%') ";
                    final String containsLowerMatchQuerySql =  "concat('%', lower(:query), '%') ";
                    final String orNameMatchInParentStopSql = "or lower(p.name_value) like ";
                    if (stopPlaceSearch.getQuery().length() <= 3) {
                        wheres.add("(lower(s.name_value) like " + startingWithLowerMatchQuerySql + orNameMatchInParentStopSql + startingWithLowerMatchQuerySql + ")");
                    } else {
                        wheres.add("(lower(s.name_value) like " + containsLowerMatchQuerySql + orNameMatchInParentStopSql + containsLowerMatchQuerySql + ")");
                    }

                    orderByStatements.add("similarity(concat(s.name_value, p.name_value), :query) desc");
                }
            }

            if (stopPlaceSearch.getStopTypeEnumerations() != null && !stopPlaceSearch.getStopTypeEnumerations().isEmpty()) {
                wheres.add("s.stop_place_type in :stopPlaceTypes");
                parameters.put("stopPlaceTypes", stopPlaceSearch.getStopTypeEnumerations().stream().map(StopTypeEnumeration::toString).collect(toList()));
                operators.add("and");
            }

            if(stopPlaceSearch.getTags() != null && !stopPlaceSearch.getTags().isEmpty()) {
                wheres.add("s.netex_id in (select t.netex_reference from tag t where t.netex_reference = s.netex_id and t.name in :tags)");
                parameters.put("tags", stopPlaceSearch.getTags());
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

                String municipalityQuery = "topographic_place_id in (select tp.id from topographic_place tp where tp.netex_id in :municipalityId)";
                wheres.add(prefix + "(s." + municipalityQuery + " or p." + municipalityQuery + ")");
                parameters.put("municipalityId", exportParams.getMunicipalityReferences());
            }

            if (hasCountyFilter && !hasIdFilter) {
                String suffix = hasMunicipalityFilter ? ")" : "";
                String countyQuery = "topographic_place_id in (select tp.id from topographic_place tp where tp.parent_ref in :countyId)";
                wheres.add("(s." + countyQuery + " or " + "p." + countyQuery + ")" + suffix);
                parameters.put("countyId", exportParams.getCountyReferences());
            }
        }

        if (stopPlaceSearch.getVersion() != null) {
            operators.add("and");
            wheres.add("s.version = :version");
            parameters.put("version", stopPlaceSearch.getVersion());
        } else if (!stopPlaceSearch.isAllVersions()
                && stopPlaceSearch.getPointInTime() == null &&
                (stopPlaceSearch.getVersionValidity() == null || ExportParams.VersionValidity.ALL.equals(stopPlaceSearch.getVersionValidity()))) {
            operators.add("and");
            wheres.add("s.version = (select max(sv.version) from stop_place sv where sv.netex_id = s.netex_id)");
        }

        if (stopPlaceSearch.getPointInTime() != null) {
            operators.add("and");
            //(from- and toDate is NULL), or (fromDate is set and toDate IS NULL or set)
            String pointInTimeQuery = "((%s.from_date IS NULL AND %s.to_date IS NULL) OR (%s.from_date <= :pointInTime AND (%s.to_date IS NULL OR %s.to_date > :pointInTime)))";

            wheres.add("(" + formatRepeatedValue(pointInTimeQuery, "s", 5)+ " or (p.id IS NOT NULL AND " + formatRepeatedValue(pointInTimeQuery, "p", 5)+ "))");
            parameters.put("pointInTime", Timestamp.from(stopPlaceSearch.getPointInTime()));
        } else if(stopPlaceSearch.getVersionValidity() != null) {
            operators.add("and");

            if(ExportParams.VersionValidity.CURRENT.equals(stopPlaceSearch.getVersionValidity())) {

                String currentQuery = "(%s.from_date <= now() AND (%s.to_date >= now() or %s.to_date IS NULL))";
                wheres.add("("+ formatRepeatedValue(currentQuery, "s", 3) + " or " + formatRepeatedValue(currentQuery, "p", 3) + ")");
            } else if(ExportParams.VersionValidity.CURRENT_FUTURE.equals(stopPlaceSearch.getVersionValidity())) {
                String futureQuery = "s.to_date >= now() OR s.to_date IS NULL";
                String parentFutureQuery = "p.to_date >= now() OR p.to_date IS NULL";
                wheres.add("((" + futureQuery + ") or (" + parentFutureQuery +"))");
            }
        }

        if (stopPlaceSearch.isWithoutLocationOnly()) {
            operators.add("and");
            wheres.add("(s.centroid IS NULL or (p.id IS NOT NULL AND p.centroid IS NULL))");
        }

        operators.add("and");
        wheres.add("s.parent_stop_place = false");

        addWheres(queryString, wheres, operators);

        orderByStatements.add("s.netex_id, s.version asc");
        queryString.append(" order by");

        for (int i = 0; i < orderByStatements.size(); i++) {
            if (i > 0) {
                queryString.append(',');
            }
            queryString.append(' ').append(orderByStatements.get(i)).append(' ');
        }

        final String generatedSql = basicFormatter.format(queryString.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("sql: {}\nparams: {}\nSearch object: {}", generatedSql, parameters.toString(), stopPlaceSearch);
        }
        return Pair.of(generatedSql, parameters);
    }

    private String formatRepeatedValue(String format, String value, int repeated) {
        Object[] args = new Object[repeated];
        for(int i = 0; i < repeated; i++) {
            args[i] = value;
        }
        return String.format(format, args);
    }
}
