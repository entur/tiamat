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

package org.rutebanken.tiamat.repository;

import com.google.api.client.util.Joiner;
import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.internal.SessionImpl;
import org.rutebanken.tiamat.exporter.params.GroupOfStopPlacesSearch;
import org.rutebanken.tiamat.exporter.params.ParkingSearch;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.repository.search.GroupOfStopPlacesQueryFromSearchBuilder;
import org.rutebanken.tiamat.repository.search.SearchHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

public class GroupOfStopPlacesRepositoryImpl implements GroupOfStopPlacesRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SearchHelper searchHelper;

    @Autowired
    private GroupOfStopPlacesQueryFromSearchBuilder groupOfStopPlacesQueryFromSearchBuilder;

    @Override
    public List<GroupOfStopPlaces> findGroupOfStopPlaces(GroupOfStopPlacesSearch search) {

        Pair<String, Map<String, Object>> pair = groupOfStopPlacesQueryFromSearchBuilder.buildQueryFromSearch(search);
        Session session = entityManager.unwrap(SessionImpl.class);
        SQLQuery query = session.createSQLQuery(pair.getFirst());
        query.addEntity(GroupOfStopPlaces.class);

        searchHelper.addParams(query, pair.getSecond());

        @SuppressWarnings("unchecked")
        List<GroupOfStopPlaces> groups = query.list();
        return groups;
    }

}

