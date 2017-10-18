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

package org.rutebanken.tiamat.exporter.async;

import com.google.common.collect.Sets;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.internal.SessionImpl;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.model.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class EntitiesEvicter {

    private static final Logger logger = LoggerFactory.getLogger(EntitiesEvicter.class);

    private static final Set<String> evictionClasses = Sets.newHashSet(TariffZone.class.getName(), TariffZoneRef.class.getName(), Tag.class.getName(), AccessibilityLimitation.class.getName(), Quay.class.getName(), AlternativeName.class.getName());

    private final SessionImpl session;

    public EntitiesEvicter(SessionImpl session) {
        this.session = session;
    }

    @SuppressWarnings("unchecked")
    public void evictKnownEntitiesFromSession(Object entity) {
        if(session != null) {

            if(entity instanceof Site_VersionStructure) {
                Site_VersionStructure site = ((Site_VersionStructure) entity);

                if(site.getTopographicPlace() != null) {
                    session.evict(site.getTopographicPlace());
                }

                if(site.getKeyValues() != null) {
                    site.getKeyValues().values().forEach(value -> session.evict(value));
                }

                if(site.getPolygon() != null) {
                    session.evict(site.getPolygon());
                }

                if(site instanceof SiteElement) {
                    SiteElement stopPlace = (SiteElement) site;
                    session.evict(stopPlace.getAccessibilityAssessment());
                }
            }

            Set<Object> evictEntities = new HashSet<>();
            try {


                session.getPersistenceContext().getEntitiesByKey().forEach((key, value) -> {
                    if(evictionClasses.contains(((EntityKey) key).getEntityName())) {
                        evictEntities.add(value);
                    }
                });

//                logger.info("Evicting {} entities", evictEntities.size());
                evictEntities.forEach(session::evict);

            } catch (Exception e) {
                logger.warn("Error evicting entities {}", evictEntities, e);
            }
            session.evict(entity);
        }
//        logger.info("{}", session.getStatistics());
    }
}
