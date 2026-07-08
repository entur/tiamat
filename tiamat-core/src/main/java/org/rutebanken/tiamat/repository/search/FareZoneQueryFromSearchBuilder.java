/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.repository.search;

import org.rutebanken.tiamat.exporter.params.FareZoneSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FareZoneQueryFromSearchBuilder {

    private static final Logger logger = LoggerFactory.getLogger(FareZoneQueryFromSearchBuilder.class);

    @Autowired
    private SearchHelper searchHelper;

    public Pair<String, Map<String, Object>> buildQueryFromSearch(FareZoneSearch search) {

        StringBuilder queryString = new StringBuilder("""
                SELECT f.*
                FROM fare_zone f
                INNER JOIN (
                    SELECT fv.netex_id, MAX(fv.version) AS max_version
                    FROM fare_zone fv
                    WHERE (fv.to_date IS NULL OR fv.to_date > NOW())
                      AND (fv.from_date IS NULL OR fv.from_date < NOW())
                    GROUP BY fv.netex_id
                ) latest_fv ON f.netex_id = latest_fv.netex_id
                          AND f.version = latest_fv.max_version
                """);
        List<String> wheres = new ArrayList<>();
        List<String> operators = new ArrayList<>();
        List<String> orderByStatements = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();

        if (search == null) {
            logger.info("empty search object for fare zone");
            return Pair.of(queryString.toString(), parameters);
        }

        if (search.getQuery() != null) {
            wheres.add("(lower(f.name_value) like concat('%', lower(:query), '%') or f.netex_id like concat('%', :query, '%'))");
            parameters.put("query", search.getQuery());
            orderByStatements.add("similarity(f.name_value, :query) desc");
        }
        if (search.getAuthorityRef() != null) {
            operators.add("and");
            wheres.add("lower(f.transport_organisation_ref) = lower(:authRef) ");
            parameters.put("authRef",search.getAuthorityRef());
        }
        if (search.getScopingMethodEnumeration() != null) {
            operators.add("and");
            wheres.add("f.scoping_method = :scopingMethod ");
            parameters.put("scopingMethod",search.getScopingMethodEnumeration().name());
        }
        if (search.getZoneTopologyEnumeration() != null) {
            operators.add("and");
            wheres.add("f.zone_topology = :zoneTopology ");
            parameters.put("zoneTopology",search.getZoneTopologyEnumeration().name());
        }

        searchHelper.addWheres(queryString, wheres, operators);
        searchHelper.addOrderByStatements(queryString, orderByStatements);
        final String generatedSql = searchHelper.format(queryString.toString());
        searchHelper.logIfLoggable(generatedSql, parameters, search, logger);
        return Pair.of(generatedSql, parameters);
    }
}
