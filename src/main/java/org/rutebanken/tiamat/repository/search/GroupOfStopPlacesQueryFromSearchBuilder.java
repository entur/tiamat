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

import org.rutebanken.tiamat.exporter.params.GroupOfStopPlacesSearch;
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
public class GroupOfStopPlacesQueryFromSearchBuilder {

    private static final Logger logger = LoggerFactory.getLogger(GroupOfStopPlacesQueryFromSearchBuilder.class);

    @Autowired
    private SearchHelper searchHelper;

    public Pair<String, Map<String, Object>> buildQueryFromSearch(GroupOfStopPlacesSearch search) {

        StringBuilder queryString = new StringBuilder("select g.* from group_of_stop_places g ");
        List<String> wheres = new ArrayList<>();
        List<String> operators = new ArrayList<>();
        List<String> orderByStatements = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();

        if(search == null) {
            logger.info("empty search object for group of stop places");
            return Pair.of(queryString.toString(), parameters);
        }

        if(search.getIdList() != null && !search.getIdList().isEmpty()) {
            wheres.add(" g.netex_id in (:idList)");
            operators.add("and");
            parameters.put("idList", search.getIdList());
        }

        if(search.getQuery() != null) {
            wheres.add("lower(g.name_value) like concat('%', lower(:query), '%')");
            operators.add("and");
            parameters.put("query", search.getQuery());
            orderByStatements.add("similarity(g.name_value, :query) desc");
        }

        searchHelper.addWheres(queryString, wheres, operators);
        searchHelper.addOrderByStatements(queryString, orderByStatements);
        final String generatedSql = searchHelper.format(queryString.toString());
        searchHelper.logIfLoggable(generatedSql, parameters, search, logger);
        return Pair.of(generatedSql, parameters);

    }
}
