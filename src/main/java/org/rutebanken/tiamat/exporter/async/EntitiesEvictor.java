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
import java.util.concurrent.atomic.AtomicInteger;

public class EntitiesEvictor {

    private static final Logger logger = LoggerFactory.getLogger(EntitiesEvictor.class);

    /**
     * Object types that should be evicted every time an entity is evicted.
     * Usually we would use CascadeType DETACH but entities like Tag is not related other than a reference string.
     */
    private static final Set<String> evictionClasses = Sets.newHashSet(
            Tag.class.getName()
    );


    private final SessionImpl session;

    public EntitiesEvictor(SessionImpl session) {
        this.session = session;
    }

    private final AtomicInteger calledCount = new AtomicInteger();
    private final AtomicInteger evictCalledCount = new AtomicInteger();

    @SuppressWarnings("unchecked")
    public void evictKnownEntitiesFromSession(Object entity) {
        Set<Object> evictEntities = new HashSet<>();
        try {
            session.getPersistenceContext().getEntitiesByKey().forEach((key, value) -> {
                EntityKey entityKey = (EntityKey) key;
                if (evictionClasses.contains(entityKey.getEntityName())) {
                    evictEntities.add(value);
                }
//                else if(entity instanceof StopPlace && entityKey.getEntityName().equals(TariffZone.class.getName()) ) {
//                    // Stop Places can have references to tariff zones that should be evicted
//                    // When the entity itself is of type tariff zone, it cannot be evicted.
//                    evictEntities.add(value);
//                }
            });

            evictEntities.forEach(this::evictNonNull);

        } catch (Exception e) {
            logger.warn("Error evicting entities {}", evictEntities, e);
        }
        evictNonNull(entity);
        calledCount.incrementAndGet();

        if (calledCount.get() % 1000 == 0) {
            logger.info("Status: called count: {}. Evict called count: {}. {}", calledCount.get(), evictCalledCount.get(), session.getStatistics());
        }
    }

    private void evictNonNull(Object objectToEvict) {
        if (objectToEvict != null) {
            session.evict(objectToEvict);
            evictCalledCount.incrementAndGet();
        }
    }
}
