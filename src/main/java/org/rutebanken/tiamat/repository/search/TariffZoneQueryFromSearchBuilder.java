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

import org.rutebanken.tiamat.exporter.params.TariffZoneSearch;
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
public class TariffZoneQueryFromSearchBuilder {

    private static final Logger logger = LoggerFactory.getLogger(TariffZoneQueryFromSearchBuilder.class);

    @Autowired
    private SearchHelper searchHelper;

    public Pair<String, Map<String, Object>> buildQueryFromSearch(TariffZoneSearch search) {

        StringBuilder queryString = new StringBuilder("select t.* from tariff_zone t ");
        List<String> wheres = new ArrayList<>();
        List<String> operators = new ArrayList<>();
        List<String> orderByStatements = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();

        if (search == null) {
            logger.info("empty search object for tariff zone");
            return Pair.of(queryString.toString(), parameters);
        }

        if (search.getQuery() != null) {
            wheres.add("(lower(t.name_value) like concat('%', lower(:query), '%') or t.netex_id like concat('%', :query, '%'))");
            parameters.put("query", search.getQuery());
            orderByStatements.add("similarity(t.name_value, :query) desc");
        }
        operators.add("and");
        wheres.add("t.version = (select max(tv.version) from tariff_zone tv where tv.netex_id = t.netex_id and (tv.to_date is null or tv.to_date > now()) and (tv.from_date is null or tv.from_date < now()))");

        searchHelper.addWheres(queryString, wheres, operators);
        searchHelper.addOrderByStatements(queryString, orderByStatements);
        final String generatedSql = searchHelper.format(queryString.toString());
        searchHelper.logIfLoggable(generatedSql, parameters, search, logger);
        return Pair.of(generatedSql, parameters);
    }
}
