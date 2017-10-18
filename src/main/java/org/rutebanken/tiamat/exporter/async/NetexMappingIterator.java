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

import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class NetexMappingIterator<T extends EntityStructure, N extends org.rutebanken.netex.model.EntityStructure> implements Iterator<N> {

    private static final Logger logger = LoggerFactory.getLogger(NetexMappingIterator.class);

    private final Iterator<T> iterator;
    private final NetexMapper netexMapper;
    private final Class<N> netexClass;
    private final long startTime = System.currentTimeMillis();
    private final AtomicInteger mappedCount;
    private final EntitiesEvicter entitiesEvicter;

    public NetexMappingIterator(NetexMapper netexMapper, Iterator<T> iterator, Class<N> netexClass, AtomicInteger mappedCount) {
        this.netexMapper = netexMapper;
        this.iterator = iterator;
        this.netexClass = netexClass;
        this.mappedCount = mappedCount;
        this.entitiesEvicter = null;
    }

    public NetexMappingIterator(NetexMapper netexMapper, Iterator<T> iterator, Class<N> netexClass, AtomicInteger mappedCount, EntitiesEvicter entitiesEvicter) {
        this.iterator = iterator;
        this.netexMapper = netexMapper;
        this.netexClass = netexClass;
        this.mappedCount = mappedCount;
        this.entitiesEvicter = entitiesEvicter;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public N next() {


        T next = iterator.next();
        N mapped = netexMapper.getFacade().map(next, netexClass);
        if (entitiesEvicter != null) {
            entitiesEvicter.evictKnownEntitiesFromSession(next);

        }
        logStatus();
        mappedCount.incrementAndGet();
        return mapped;
    }



    private void logStatus() {
        if (mappedCount.get() % 1000 == 0 && logger.isInfoEnabled()) {
            String entityPerSecond = "NA";
            int count = mappedCount.get();

            long duration = System.currentTimeMillis() - startTime;
            if (duration >= 1000) {
                entityPerSecond = String.valueOf(count / (duration / 1000f));
            }
            logger.info("{} {}s marshalled. {} per second", count, netexClass.getSimpleName(), entityPerSecond);
        }
    }
}
