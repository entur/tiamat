package org.rutebanken.tiamat.exporter.async;

import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class NetexMappingIteratorList<N extends org.rutebanken.netex.model.EntityStructure> extends ArrayList<N> {

    private final Callable<Iterator<N>> iteratorRetriever;

    public NetexMappingIteratorList(Callable<Iterator<N>> iteratorRetriever) {
        this.iteratorRetriever = iteratorRetriever;
    }

    @Override
    public Iterator<N> iterator() {
        try {
            return iteratorRetriever.call();
        } catch (Exception e) {
            throw new RuntimeException("Cannot get iterator", e);
        }
    }

}
