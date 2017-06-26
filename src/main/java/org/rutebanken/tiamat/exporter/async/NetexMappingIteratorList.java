package org.rutebanken.tiamat.exporter.async;

import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

public class NetexMappingIteratorList<T extends EntityStructure, N extends org.rutebanken.netex.model.EntityStructure> extends ArrayList<N> {

    private final NetexMapper netexMapper;
    private final Iterator<T> iterator;
    private final Class<N> netexClass;
    private Consumer<N> listener = null;

    public NetexMappingIteratorList(NetexMapper netexMapper, Iterator<T> iterator, Class<N> netexClass) {
        this.netexMapper = netexMapper;
        this.iterator = iterator;
        this.netexClass = netexClass;
    }

    public NetexMappingIteratorList(NetexMapper netexMapper, Iterator<T> iterator, Class<N> netexClass, Consumer<N> listener) {
        this(netexMapper, iterator, netexClass);
        this.listener = listener;
    }

    @Override
    public Iterator<N> iterator() {
        return listener != null
                ? new ListeningNetexMappingIterator<>(netexMapper, iterator, netexClass, listener)
                : new NetexMappingIterator<>(iterator, netexMapper, netexClass);
    }

}
