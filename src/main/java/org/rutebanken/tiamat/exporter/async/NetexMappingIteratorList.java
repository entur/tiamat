package org.rutebanken.tiamat.exporter.async;

import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;

import java.util.ArrayList;
import java.util.Iterator;

public class NetexMappingIteratorList<T extends EntityStructure, N extends org.rutebanken.netex.model.EntityStructure> extends ArrayList<N> {

    private NetexMapper netexMapper;
    private Iterator<T> iterator;
    private Class<N> netexClass;

    public NetexMappingIteratorList(NetexMapper netexMapper, Iterator<T> iterator, Class<N> netexClass) {
        this.netexMapper = netexMapper;
        this.iterator = iterator;
        this.netexClass = netexClass;
    }

    @Override
    public Iterator<N> iterator() {
        return new NetexMappingIterator<>(iterator, netexMapper, netexClass);
    }

}
