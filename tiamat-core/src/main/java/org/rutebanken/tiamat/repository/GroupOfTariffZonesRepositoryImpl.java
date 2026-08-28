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
import org.apache.commons.lang3.StringUtils;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.hibernate.query.NativeQuery;
import org.rutebanken.tiamat.exporter.params.GroupOfTariffZonesSearch;
import org.rutebanken.tiamat.model.GroupOfTariffZones;
import org.rutebanken.tiamat.repository.iterator.ScrollableResultIterator;
import org.rutebanken.tiamat.repository.search.GroupOfTariffZonesQueryFromSearchBuilder;
import org.rutebanken.tiamat.repository.search.SearchHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupOfTariffZonesRepositoryImpl implements GroupOfTariffZonesRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(GroupOfTariffZones.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SearchHelper searchHelper;

    @Autowired
    private GroupOfTariffZonesQueryFromSearchBuilder groupOfTariffZonesQueryFromSearchBuilder;

    @Override
    public List<GroupOfTariffZones> findGroupOfTariffZones(GroupOfTariffZonesSearch search) {

        Pair<String, Map<String, Object>> pair = groupOfTariffZonesQueryFromSearchBuilder.buildQueryFromSearch(search);
        Session session = entityManager.unwrap(SessionImpl.class);
        NativeQuery query = session.createNativeQuery(pair.getFirst(), GroupOfTariffZones.class);
        query.addEntity(GroupOfTariffZones.class);

        searchHelper.addParams(query, pair.getSecond());

        @SuppressWarnings("unchecked")
        List<GroupOfTariffZones> groups = query.list();
        return groups;
    }


    @Override
    public Iterator<GroupOfTariffZones> scrollGroupOfTariffZones() {

        var sql = "select gotz.* from group_of_tariff_zones gotz where " +
                "gotz.version = (select max(gotzv.version) from group_of_tariff_zones gotzv where gotzv.netex_id = gotz.netex_id)";
        return scrollGroupOfTariffZones(sql);
    }

    @Override
    public Iterator<GroupOfTariffZones> scrollGroupOfTariffZones(Set<Long> stopPlaceDbIds) {
        var sql = "select" +
                "        g.*" +
                "    from" +
                "        (select" +
                "            gotz1.id," +
                "            gotz1.netex_id," +
                "            gotz1.version " +
                "        from" +
                "            group_of_tariff_zones gotz1 " +
                "        inner join" +
                "            group_of_tariff_zones_members members " +
                "                on gotz1.id=members.group_of_tariff_zones_id " +
                "                and members.ref in (" +
                "                    select" +
                "                        ref " +
                "                from" +
                "                    stop_place_tariff_zones " +
                "                where" +
                "                    stop_place_id in (" + StringUtils.join(stopPlaceDbIds, ',') +
                "                    )" +
                "            ) " +
                "            and gotz1.version = (" +
                "                select" +
                "                    max(gotzv.version) " +
                "                from" +
                "                    group_of_tariff_zones gotzv " +
                "                where" +
                "                    gotzv.netex_id=gotz1.netex_id" +
                "            ) " +
                "        group by" +
                "            gotz1.id," +
                "            gotz1.netex_id) gotz " +
                "        join" +
                "            group_of_tariff_zones g " +
                "                on gotz.netex_id = g.netex_id " +
                "                and g.version = gotz.version ";

        logger.info(sql);
        return scrollGroupOfTariffZones(sql);
    }

    private Iterator<GroupOfTariffZones> scrollGroupOfTariffZones(String sql) {
        Session session = entityManager.unwrap(Session.class);
        NativeQuery sqlQuery = session.createNativeQuery(sql, GroupOfTariffZones.class);

        final int fetchSize = 100;

        sqlQuery.addEntity(GroupOfTariffZones.class);
        sqlQuery.setReadOnly(true);
        sqlQuery.setFetchSize(fetchSize);
        sqlQuery.setCacheable(false);
        ScrollableResults results = sqlQuery.scroll(ScrollMode.FORWARD_ONLY);
        ScrollableResultIterator<GroupOfTariffZones> groupOfTariffZonesIterator = new ScrollableResultIterator<>(results, fetchSize, session);
        return groupOfTariffZonesIterator;
    }

}

