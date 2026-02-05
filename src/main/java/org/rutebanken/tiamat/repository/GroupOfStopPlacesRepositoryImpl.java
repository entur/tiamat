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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.hibernate.query.NativeQuery;
import org.rutebanken.tiamat.exporter.params.GroupOfStopPlacesSearch;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.repository.iterator.ScrollableResultIterator;
import org.rutebanken.tiamat.repository.search.GroupOfStopPlacesQueryFromSearchBuilder;
import org.rutebanken.tiamat.repository.search.SearchHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupOfStopPlacesRepositoryImpl implements GroupOfStopPlacesRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(GroupOfStopPlaces.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SearchHelper searchHelper;

    @Autowired
    private GroupOfStopPlacesQueryFromSearchBuilder groupOfStopPlacesQueryFromSearchBuilder;

    @Override
    public List<GroupOfStopPlaces> findGroupOfStopPlaces(GroupOfStopPlacesSearch search) {

        Pair<String, Map<String, Object>> pair = groupOfStopPlacesQueryFromSearchBuilder.buildQueryFromSearch(search);
        Session session = entityManager.unwrap(SessionImpl.class);
        NativeQuery query = session.createNativeQuery(pair.getFirst(), GroupOfStopPlaces.class);
        query.addEntity(GroupOfStopPlaces.class);

        searchHelper.addParams(query, pair.getSecond());

        @SuppressWarnings("unchecked")
        List<GroupOfStopPlaces> groups = query.list();
        return groups;
    }

    @Override
    public List<GroupOfStopPlaces> getGroupOfStopPlacesFromStopPlaceIds(Set<Long> stopPlaceIds) {
        if (stopPlaceIds == null || stopPlaceIds.isEmpty()) {
            return new ArrayList<>();
        }

        Query query = entityManager.createNativeQuery(generateGroupOfStopPlacesQueryFromStopPlaceIds(stopPlaceIds), GroupOfStopPlaces.class);

        @SuppressWarnings("unchecked")
        List<GroupOfStopPlaces> groupOfStopPlaces = query.getResultList();
        return groupOfStopPlaces;
    }

    @Override
    public Iterator<GroupOfStopPlaces> scrollGroupOfStopPlaces() {

        return scrollGroupOfStopPlaces("select gosp.* from group_of_stop_places gosp where " +
                "gosp.version = (select max(gospv.version) from group_of_stop_places gospv where gospv.netex_id = gosp.netex_id)");
    }

    @Override
    public Iterator<GroupOfStopPlaces> scrollGroupOfStopPlaces(Set<Long> stopPlaceDbIds) {

        if (stopPlaceDbIds == null || stopPlaceDbIds.isEmpty()) {
            return new ArrayList<GroupOfStopPlaces>().iterator();
        }
        return scrollGroupOfStopPlaces(generateGroupOfStopPlacesQueryFromStopPlaceIds(stopPlaceDbIds));
    }

    private Iterator<GroupOfStopPlaces> scrollGroupOfStopPlaces(String sql) {
        Session session = entityManager.unwrap(Session.class);
        NativeQuery sqlQuery = session.createNativeQuery(sql, GroupOfStopPlaces.class);

        final int fetchSize = 100;

        sqlQuery.addEntity(GroupOfStopPlaces.class);
        sqlQuery.setReadOnly(true);
        sqlQuery.setFetchSize(fetchSize);
        sqlQuery.setCacheable(false);
        ScrollableResults results = sqlQuery.scroll(ScrollMode.FORWARD_ONLY);
        ScrollableResultIterator<GroupOfStopPlaces> groupOfStopPlacesIterator = new ScrollableResultIterator<>(results, fetchSize, session);
        return groupOfStopPlacesIterator;
    }

    private String generateGroupOfStopPlacesQueryFromStopPlaceIds(Set<Long> stopPlaceDbIds) {
        StringBuilder sqlStringBuilder = new StringBuilder("SELECT g.* FROM " +
                "   (SELECT gosp1.id, gosp1.netex_id " +
                "       FROM stop_place s " +
                "       INNER JOIN group_of_stop_places_members members ON members.ref = s.netex_id " +
                "       INNER JOIN group_of_stop_places gosp1 ON members.group_of_stop_places_id = gosp1.id " +
                "       WHERE s.id IN (");

        sqlStringBuilder.append(StringUtils.join(stopPlaceDbIds, ','));

        sqlStringBuilder.append("   ) " +
                "      AND gosp1.version = " +
                "       (SELECT max(gospv.version) " +
                "           FROM group_of_stop_places gospv " +
                "           WHERE gospv.netex_id = gosp1.netex_id) " +
                "      GROUP BY gosp1.id, gosp1.netex_id " +
                "  ) gosp " +
                "JOIN group_of_stop_places g ON gosp.netex_id = g.netex_id");

        String sql = sqlStringBuilder.toString();
        logger.info(sql);
        return sql;
    }

    @Override
    public Map<Long, List<GroupOfStopPlaces>> findGroupsByStopPlaceIds(Set<Long> stopPlaceIds) {
        if (stopPlaceIds == null || stopPlaceIds.isEmpty()) {
            return new HashMap<>();
        }

        logger.debug("Batch loading groups for {} stop places", stopPlaceIds.size());

        // Use JPQL to find groups that contain the given stop place IDs
        Map<Long, List<GroupOfStopPlaces>> groupsByStopPlaceId = new HashMap<>();
        String jpql = """
            SELECT g FROM GroupOfStopPlaces g 
            JOIN g.members m 
            WHERE m.ref IN (
                SELECT sp.netexId FROM StopPlace sp WHERE sp.id IN :stopPlaceIds
            )
            """;

        TypedQuery<GroupOfStopPlaces> jpqlQuery = entityManager.createQuery(jpql, GroupOfStopPlaces.class);
        jpqlQuery.setParameter("stopPlaceIds", stopPlaceIds);

        List<GroupOfStopPlaces> groups = jpqlQuery.getResultList();

        // Now we need to map groups back to stop place IDs
        // Get the netex IDs for the stop place IDs
        String netexIdQuery = "SELECT sp.id, sp.netexId FROM StopPlace sp WHERE sp.id IN :stopPlaceIds";
        TypedQuery<Object[]> netexQuery = entityManager.createQuery(netexIdQuery, Object[].class);
        netexQuery.setParameter("stopPlaceIds", stopPlaceIds);
        List<Object[]> netexResults = netexQuery.getResultList();

        Map<String, Long> netexIdToStopPlaceId = new HashMap<>();
        for (Object[] result : netexResults) {
            Long stopPlaceId = (Long) result[0];
            String netexId = (String) result[1];
            netexIdToStopPlaceId.put(netexId, stopPlaceId);
        }

        // Map groups to stop place IDs
        for (GroupOfStopPlaces group : groups) {
            for (var member : group.getMembers()) {
                Long stopPlaceId = netexIdToStopPlaceId.get(member.getRef());
                if (stopPlaceId != null) {
                    groupsByStopPlaceId.computeIfAbsent(stopPlaceId, k -> new ArrayList<>()).add(group);
                }
            }
        }

        // Ensure all requested stop place IDs have entries (even if empty)
        for (Long stopPlaceId : stopPlaceIds) {
            groupsByStopPlaceId.putIfAbsent(stopPlaceId, new ArrayList<>());
        }

        logger.debug("Found groups for {} stop places", groupsByStopPlaceId.size());
        return groupsByStopPlaceId;
    }
}

