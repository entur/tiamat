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

import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.TopographicPlaceSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class TopographicPlaceQueryFromSearchBuilder {

    @Autowired
    private SearchHelper searchHelper;

    public Pair<String, Map<String, Object>> buildQueryString(TopographicPlaceSearch topographicPlaceSearch) {
        ExportParams.VersionValidity versionValidity = topographicPlaceSearch.getVersionValidity();

        if(versionValidity == null) {
            versionValidity = ExportParams.VersionValidity.CURRENT;
        }

        StringBuilder queryString = new StringBuilder("select t.* from topographic_place t ");

        List<String> wheres = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        List<String> operators = new ArrayList<>();

        if (ExportParams.VersionValidity.CURRENT.equals(versionValidity)) {
            wheres.add("((t.from_date <= :pointInTime OR t.from_date is null) AND (t.to_date >= :pointInTime OR t.to_date IS NULL))");
        } else if (ExportParams.VersionValidity.CURRENT_FUTURE.equals(versionValidity)) {
            wheres.add("t.to_date >= :pointInTime OR t.to_date IS NULL");

        } else if (ExportParams.VersionValidity.MAX_VERSION.equals(versionValidity)) {
            wheres.add("t.version = (select max(tv.version) from topographic_place tv where tv.netex_id = t.netex_id)");
        }
        parameters.put("pointInTime", Date.from(Instant.now()));

        searchHelper.addWheres(queryString, wheres, operators);

        List<String> orderByStatements = Arrays.asList("id desc", "changed desc", "from_date desc");

        searchHelper.addOrderByStatements(queryString, orderByStatements);
        final String generatedSql = searchHelper.format(queryString.toString());

        return Pair.of(generatedSql, parameters);
    }
}
