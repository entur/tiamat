package org.rutebanken.tiamat.exporter.async;

import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class NetexMappingIterator<T extends EntityStructure, N extends org.rutebanken.netex.model.EntityStructure> implements Iterator<N> {

    private static final Logger logger = LoggerFactory.getLogger(NetexMappingIterator.class);

    private final Iterator<T> iterator;
    private final NetexMapper netexMapper;
    private final Class<N> netexClass;
    private final long startTime = System.currentTimeMillis();
    private int count;

    public NetexMappingIterator(NetexMapper netexMapper, Iterator<T> iterator, Class<N> netexClass) {
        this.iterator = iterator;
        this.netexMapper = netexMapper;
        this.netexClass = netexClass;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public N next() {
        ++count;
        logStatus();
        return netexMapper.getFacade().map(iterator.next(), netexClass);
    }

    private void logStatus() {
        if (count % 1000 == 0 && logger.isInfoEnabled()) {
            String entityPerSecond = "NA";

            long duration = System.currentTimeMillis() - startTime;
            if (duration >= 1000) {
                entityPerSecond = String.valueOf(count / (duration / 1000f));
            }
            logger.info("{} {}s marshalled. {} per second", count, netexClass.getSimpleName(), entityPerSecond);
        }
    }
}
