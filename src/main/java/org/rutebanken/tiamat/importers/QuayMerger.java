package org.rutebanken.tiamat.importers;

import com.vividsolutions.jts.geom.Geometry;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class QuayMerger {

    /**
     * The max distance for checking if two quays are nearby each other.
     * http://gis.stackexchange.com/questions/28799/what-is-the-unit-of-measurement-for-buffer-calculation
     * https://en.wikipedia.org/wiki/Decimal_degrees
     */
    public static final double MERGE_DISTANCE = 0.0001;

    private static final Logger logger = LoggerFactory.getLogger(QuayMerger.class);

    /**
     * Inspect quays from incoming AND matching stop place. If they do not exist from before, add them.
     */
    public boolean addNewQuaysOrAppendImportIds(StopPlace newStopPlace, StopPlace existingStopPlace) {

        AtomicInteger updatedQuays = new AtomicInteger();
        AtomicInteger addedQuays = new AtomicInteger();

        logger.debug("About to compare quays for {}", existingStopPlace.getId());

        if (newStopPlace.getQuays() == null) {
            newStopPlace.setQuays(new HashSet<>());
        }

        Set<Quay> result = addNewQuaysOrAppendImportIds(newStopPlace.getQuays(), existingStopPlace.getQuays(), updatedQuays, addedQuays);

        existingStopPlace.setQuays(result);

        logger.debug("Created {} quays and updated {} quays for stop place {}", addedQuays.get(), updatedQuays.get(), existingStopPlace);
        return addedQuays.get() > 0;
    }

    public Set<Quay> addNewQuaysOrAppendImportIds(Set<Quay> newQuays, Set<Quay> existingQuays, AtomicInteger updatedQuaysCounter, AtomicInteger addedQuaysCounter) {

        Set<Quay> result = new HashSet<>();
        if(existingQuays == null) {
            existingQuays = new HashSet<>();
        }
        result.addAll(existingQuays);

        for(Quay incomingQuay : newQuays) {

            boolean foundMatch = false;
            for(Quay alreadyAdded : result) {
                foundMatch = appendIdIfMatchingOriginalId(incomingQuay, alreadyAdded, updatedQuaysCounter);
                if(foundMatch) {
                    break;
                }
            }

            if(!foundMatch) {
                for (Quay alreadyAdded : result) {
                    foundMatch = appendIdIfClose(incomingQuay, alreadyAdded, updatedQuaysCounter);
                    if (foundMatch) {
                        break;
                    }
                }
            }

            if(!foundMatch) {
                logger.info("Found no match for existing quay {}. Adding it!", incomingQuay);
                result.add(incomingQuay);
                addedQuaysCounter.incrementAndGet();
            }
        }

        return result;
    }

    private boolean appendIdIfClose(Quay incomingQuay, Quay alreadyAdded, AtomicInteger updatedQuaysCounter) {
        if (areClose(incomingQuay, alreadyAdded)) {
            logger.info("New quay {} is close to existing quay {}. Appending it's ID", incomingQuay, alreadyAdded);
            boolean changed = alreadyAdded.getOriginalIds().addAll(incomingQuay.getOriginalIds());
            if(changed) {
                updatedQuaysCounter.incrementAndGet();
            }
            return true;
        }
        return false;
    }

    private boolean appendIdIfMatchingOriginalId(Quay incomingQuay, Quay alreadyAdded, AtomicInteger updatedQuaysCounter) {
        if(!Collections.disjoint(alreadyAdded.getOriginalIds(), incomingQuay.getOriginalIds())) {
            logger.info("New quay matches on original ID: {}. Adding all new IDs if any. Existing quay ID: {}", incomingQuay, alreadyAdded.getId());
            // The incoming quay could for some reason already have multiple imported IDs.
            boolean changed = alreadyAdded.getOriginalIds().addAll(incomingQuay.getOriginalIds());
            if(changed) {
                updatedQuaysCounter.incrementAndGet();
            }
            return true;
        }
        return false;
    }

    public boolean areClose(Quay quay1, Quay quay2) {
        if (!quay1.hasCoordinates() || !quay2.hasCoordinates()) {
            return false;
        }

        Geometry buffer = quay1.getCentroid().buffer(MERGE_DISTANCE);
        boolean intersects = buffer.intersects(quay2.getCentroid());
        return intersects;
    }

}
