package org.rutebanken.tiamat.exporter.async;

import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ListeningNetexMappingIterator<T extends EntityStructure, N extends org.rutebanken.netex.model.EntityStructure> extends NetexMappingIterator<T, N> {

    private final Consumer<N> listener;

    public ListeningNetexMappingIterator(NetexMapper netexMapper, Iterator<T> iterator, Class<N> netexClass, Consumer<N> listener, AtomicInteger mappedCount) {
        super(netexMapper, iterator, netexClass, mappedCount);
        this.listener = listener;
    }

    @Override
    public N next() {
        N n =  super.next();
        listener.accept(n);
        return n;
    }

}
