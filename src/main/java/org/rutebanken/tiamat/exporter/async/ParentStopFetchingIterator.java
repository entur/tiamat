package org.rutebanken.tiamat.exporter.async;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class ParentStopFetchingIterator implements Iterator<StopPlace> {

    private static final Logger logger = LoggerFactory.getLogger(ParentStopFetchingIterator.class);

    private final Iterator<StopPlace> iterator;

    private final StopPlaceRepository stopPlaceRepository;

    private StopPlace parent = null;

    public ParentStopFetchingIterator(Iterator<StopPlace> iterator, StopPlaceRepository stopPlaceRepository) {
        this.iterator = iterator;
        this.stopPlaceRepository = stopPlaceRepository;
    }

    @Override
    public boolean hasNext() {
        return parent != null || iterator.hasNext();
    }

    @Override
    public StopPlace next() {

        if(parent != null) {
            StopPlace next = parent;
            parent = null;
            return next;
        }


        StopPlace stopPlace = iterator.next();
        if(stopPlace.getParentSiteRef() != null) {
            parent = stopPlaceRepository.findFirstByNetexIdAndVersion(stopPlace.getParentSiteRef().getRef(), Long.parseLong(stopPlace.getParentSiteRef().getVersion()));
            logger.info("Fetched parent during iteration: {} - {}", parent.getNetexId(), parent.getVersion());
        }

        return stopPlace;
    }
}
