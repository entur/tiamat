package no.rutebanken.tiamat.rest.netex.siteframe;





import no.rutebanken.tiamat.model.*;
import no.rutebanken.tiamat.repository.QuayRepository;
import no.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class StopPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceImporter.class);

    public static final String ORIGINAL_ID_KEY = "imported-id";


    private TopographicPlaceCreator topographicPlaceCreator;

    private QuayRepository quayRepository;

    private StopPlaceRepository stopPlaceRepository;


    @Autowired
    public StopPlaceImporter(TopographicPlaceCreator topographicPlaceCreator, QuayRepository quayRepository, StopPlaceRepository stopPlaceRepository) {
        this.topographicPlaceCreator = topographicPlaceCreator;
        this.quayRepository = quayRepository;
        this.stopPlaceRepository = stopPlaceRepository;
    }

    public StopPlace findExistingStopPlaceFromOriginalId(StopPlace stopPlace) {

        if(stopPlace.getId() != null) {

            StopPlace existingStopPlace = stopPlaceRepository.findOne(stopPlace.getId());
            if(existingStopPlace != null) {
                return existingStopPlace;
            }
        }

        if(stopPlace.getKeyList() != null) {
            return stopPlace.getKeyList().getKeyValue()
                    .stream()
                    .filter(keyValueStructure -> keyValueStructure.getKey().equals(ORIGINAL_ID_KEY))
                    .map(KeyValueStructure::getValue)
                    .map(value -> stopPlaceRepository.findByKeyValue(ORIGINAL_ID_KEY, value))
                    .filter(existingStopPlace ->  existingStopPlace != null)
                    .findFirst()
                    .orElseGet(null);
        }

        return null;

    }


    public StopPlace importStopPlace(StopPlace stopPlace, SiteFrame siteFrame, AtomicInteger topographicPlacesCreatedCounter) throws InterruptedException, ExecutionException {
        if (stopPlace.getCentroid() == null
                || stopPlace.getCentroid().getLocation() == null
                || stopPlace.getCentroid().getLocation().getGeometryPoint() == null) {
            logger.info("Ignoring stop place {} - {} because it lacks geometry", stopPlace.getName(), stopPlace.getId());
            return null;
        }

        StopPlace existingStopPlace = findExistingStopPlaceFromOriginalId(stopPlace);
        if(existingStopPlace != null) {
            logger.info("Found existing stop place with ID {}", existingStopPlace.getId());
        }

        // TODO: Hack to avoid 'detached entity passed to persist'.
        stopPlace.getCentroid().getLocation().setId(0);

        if (stopPlace.getTopographicPlaceRef() != null) {
            Optional<TopographicPlace> optionalTopographicPlace = topographicPlaceCreator.findOrCreateTopographicPlace(
                    siteFrame.getTopographicPlaces().getTopographicPlace(),
                    stopPlace.getTopographicPlaceRef(),
                    topographicPlacesCreatedCounter);

            if (!optionalTopographicPlace.isPresent()) {
                logger.warn("Got no topographic places back for stop place {} {}", stopPlace.getName(), stopPlace.getId());
            }

            optionalTopographicPlace.ifPresent(topographicPlace -> {
                logger.trace("Setting topographical ref {} on stop place {} {}",
                        topographicPlace.getId(), stopPlace.getName(), stopPlace.getId());
                TopographicPlaceRefStructure newRef = new TopographicPlaceRefStructure();
                newRef.setRef(topographicPlace.getId());
                stopPlace.setTopographicPlaceRef(newRef);
            });
        }

        resetAndKeepOriginalId(stopPlace);

        if (stopPlace.getQuays() != null) {
            stopPlace.getQuays().forEach(quay -> {
                resetAndKeepOriginalId(quay);
                quayRepository.save(quay);
            });
        }

        stopPlaceRepository.save(stopPlace);
        logger.debug("Saving stop place {} {}", stopPlace.getName(), stopPlace.getId());
        return stopPlace;
    }

    public void resetAndKeepOriginalId(DataManagedObjectStructure dataManagedObjectStructure) {
        if(dataManagedObjectStructure.getId() != null) {
            KeyValueStructure importedId = new KeyValueStructure();
            importedId.setKey(ORIGINAL_ID_KEY);
            importedId.setValue(dataManagedObjectStructure.getId());
            if(dataManagedObjectStructure.getKeyList() == null) {
                dataManagedObjectStructure.setKeyList(new KeyListStructure());
            }
            dataManagedObjectStructure.getKeyList().getKeyValue().add(importedId);
            dataManagedObjectStructure.setId(null);
        }
    }
}
