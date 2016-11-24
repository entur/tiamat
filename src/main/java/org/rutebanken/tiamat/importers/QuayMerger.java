package org.rutebanken.tiamat.importers;

import com.vividsolutions.jts.geom.Geometry;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netexmapping.NetexIdMapper;
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

    private final KeyValueListAppender keyValueListAppender;

    private final QuayRepository quayRepository;

    @Autowired
    public QuayMerger(KeyValueListAppender keyValueListAppender, QuayRepository quayRepository) {
        this.keyValueListAppender = keyValueListAppender;
        this.quayRepository = quayRepository;
    }

    /**
     * Inspect quays from incoming AND matching stop place. If they do not exist from before, add them.
     */
    public boolean addAndSaveNewQuays(StopPlace newStopPlace, StopPlace foundStopPlace) {

        AtomicInteger updatedQuays = new AtomicInteger();
        AtomicInteger createdQuays = new AtomicInteger();


        logger.debug("About to compare quays for {}", foundStopPlace.getId());

        if (foundStopPlace.getQuays() == null) {
            foundStopPlace.setQuays(new HashSet<>());
        }

        if (newStopPlace.getQuays() == null) {
            foundStopPlace.setQuays(new HashSet<>());
        }

        if (foundStopPlace.getQuays().isEmpty() && !newStopPlace.getQuays().isEmpty()) {
            logger.debug("Existing stop place {} does not have any quays, using all quays from incoming stop {}, {}", foundStopPlace, newStopPlace, newStopPlace.getName());
            for (Quay newQuay : newStopPlace.getQuays()) {
                saveNewQuay(newQuay, foundStopPlace, createdQuays);
            }
        } else if (!newStopPlace.getQuays().isEmpty() && !newStopPlace.getQuays().isEmpty()) {
            logger.debug("Comparing existing: {}, incoming: {}. Removing/ignoring quays that has matching coordinates (but keeping their ID)", foundStopPlace, newStopPlace);

            Set<Quay> quaysToAdd = new HashSet<>();
            for (Quay newQuay : newStopPlace.getQuays()) {
                Optional<Quay> optionalExistingQuay = findQuayWithCoordinates(newQuay, foundStopPlace.getQuays(), quaysToAdd);
                if (optionalExistingQuay.isPresent()) {
                    Quay existingQuay = optionalExistingQuay.get();
                    logger.debug("Found matching quay {} for incoming quay {}. Appending original ID to the key if required {}", existingQuay, newQuay, NetexIdMapper.ORIGINAL_ID_KEY);
                    boolean changed = keyValueListAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, newQuay, existingQuay);

                    if (changed) {
                        logger.info("Updated quay {}, {}", existingQuay.getId(), existingQuay);
                        updatedQuays.incrementAndGet();
                        quayRepository.save(existingQuay);
                    }
                } else {
                    logger.info("Incoming {} does not match any existing quays for {}. Adding and saving it.", newQuay, foundStopPlace);
                    saveNewQuay(newQuay, foundStopPlace, createdQuays);
                }
            }
        }

        logger.debug("Created {} quays and updated {} quays for stop place {}", createdQuays.get(), updatedQuays.get(), foundStopPlace);
        return createdQuays.get() > 0;
    }


    private void saveNewQuay(Quay newQuay, StopPlace existingStopPlace, AtomicInteger createdQuays) {
        newQuay.setId(null);
        existingStopPlace.getQuays().add(newQuay);
        quayRepository.save(newQuay);
        createdQuays.incrementAndGet();
    }

    /**
     * Find first matching quay that has the same coordinates as the new Quay.
     */
    public Optional<Quay> findQuayWithCoordinates(Quay newQuay, Collection<Quay> existingQuays, Collection<Quay> quaysToAdd) {
        List<Quay> concatenatedQuays = new ArrayList<>();
        concatenatedQuays.addAll(existingQuays);
        concatenatedQuays.addAll(quaysToAdd);

        for (Quay alreadyAddedOrExistingQuay : concatenatedQuays) {
            boolean areClose = areClose(alreadyAddedOrExistingQuay, newQuay);
            logger.info("Does quay {} and {} have the same coordinates? {}", alreadyAddedOrExistingQuay, newQuay, areClose);
            if (areClose) {
                return Optional.of(alreadyAddedOrExistingQuay);
            }
        }
        return Optional.empty();
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
