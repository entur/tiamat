package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

/**
 * When importing site frames with the matching stops concurrently, not thread safe.
 */
@Component
@Transactional
public class TransactionalStopPlacesImporter {

    private static final Logger logger = LoggerFactory.getLogger(TransactionalStopPlacesImporter.class);

    private final MergingStopPlaceImporter mergingStopPlaceImporter;

    @Autowired
    public TransactionalStopPlacesImporter(MergingStopPlaceImporter mergingStopPlaceImporter) {
        this.mergingStopPlaceImporter = mergingStopPlaceImporter;
    }

    public Collection<org.rutebanken.netex.model.StopPlace> importStopPlaces(List<StopPlace> stopPlaces, AtomicInteger stopPlacesCreated) {

        List<org.rutebanken.netex.model.StopPlace> createdStopPlaces = stopPlaces
                .stream()
                .filter(Objects::nonNull)
                .map(stopPlace -> {
                    org.rutebanken.netex.model.StopPlace importedStop = null;
                    try {
                        importedStop = mergingStopPlaceImporter.importStopPlace(stopPlace);
                    } catch (Exception e) {
                        throw new RuntimeException("Could not import stop place " + stopPlace, e);
                    }
                    stopPlacesCreated.incrementAndGet();
                    return importedStop;
                })
                .filter(Objects::nonNull)
                .collect(toList());

        return distinctByIdAndHighestVersion(createdStopPlaces);
    }

    /**
     * In order to get a distinct list over stop places, and the newest version if duplicates.
     *
     * @param stopPlaces
     * @return unique list with stop places based on ID
     */
    public Collection<org.rutebanken.netex.model.StopPlace> distinctByIdAndHighestVersion(List<org.rutebanken.netex.model.StopPlace> stopPlaces) {
        Map<String, org.rutebanken.netex.model.StopPlace> uniqueStopPlaces = new HashMap<>();
        for (org.rutebanken.netex.model.StopPlace stopPlace : stopPlaces) {
            if (uniqueStopPlaces.containsKey(stopPlace.getId())) {
                org.rutebanken.netex.model.StopPlace existingStopPlace = uniqueStopPlaces.get(stopPlace.getId());
                long existingStopVersion = tryParseLong(existingStopPlace.getVersion());
                long stopPlaceVersion = tryParseLong(stopPlace.getVersion());
                if (existingStopVersion < stopPlaceVersion) {
                    logger.info("Returning newest version of stop place with ID {}: {}", stopPlace.getId(), stopPlace.getVersion());
                    uniqueStopPlaces.put(stopPlace.getId(), stopPlace);
                }
            } else {
                uniqueStopPlaces.put(stopPlace.getId(), stopPlace);
            }
        }
        return uniqueStopPlaces.values();
    }

    private long tryParseLong(String version) {
        try {
            return Long.parseLong(version);
        } catch (NumberFormatException | NullPointerException e) {
            return 0L;
        }
    }


}
