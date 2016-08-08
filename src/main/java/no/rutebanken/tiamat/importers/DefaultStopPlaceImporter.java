package no.rutebanken.tiamat.importers;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import no.rutebanken.tiamat.model.*;
import no.rutebanken.tiamat.repository.QuayRepository;
import no.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Qualifier("defaultStopPlaceImporter")
public class DefaultStopPlaceImporter implements StopPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStopPlaceImporter.class);

    public static final String ORIGINAL_ID_KEY = "imported-id";

    private TopographicPlaceCreator topographicPlaceCreator;

    private QuayRepository quayRepository;

    private StopPlaceRepository stopPlaceRepository;


    @Autowired
    public DefaultStopPlaceImporter(TopographicPlaceCreator topographicPlaceCreator, QuayRepository quayRepository, StopPlaceRepository stopPlaceRepository) {
        this.topographicPlaceCreator = topographicPlaceCreator;
        this.quayRepository = quayRepository;
        this.stopPlaceRepository = stopPlaceRepository;
    }

    public StopPlace findExistingStopPlaceFromOriginalId(StopPlace stopPlace) {

        StopPlace existingStopPlace = stopPlaceRepository.findByKeyValue(ORIGINAL_ID_KEY, stopPlace.getId());


        if (existingStopPlace != null) {
            logger.info("Found stop place {} from original ID key {}", existingStopPlace.getId(), stopPlace.getId());
            return existingStopPlace;
        }

        /*
        if(stopPlace.getId() != null) {

            StopPlace existingStopPlace = stopPlaceRepository.findOne(stopPlace.getId());
            if(existingStopPlace != null) {
                logger.info("Found existing stop place from ID: {}", stopPlace.getId());
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
                    .peek(existingStopPlace -> logger.info("Found stop place from original ID. Local ID is: {}", existingStopPlace.getId()))
                    .findFirst()
                    .orElseGet(null);
        }*/

        return null;

    }

    public boolean hasSameCoordinates(Zone_VersionStructure zone1, Zone_VersionStructure zone2) {
        if (zone1.getCentroid() == null || zone2.getCentroid() == null) {
            return false;
        }
        return (zone1.getCentroid().getLocation().getGeometryPoint()
                .distance(zone2.getCentroid().getLocation().getGeometryPoint()) == 0.0);
    }


    public Envelope createBoundingBox(SimplePoint simplePoint) {

        Geometry buffer = simplePoint.getLocation().getGeometryPoint().buffer(0.004);

        Envelope envelope = buffer.getEnvelopeInternal();
        logger.trace("Created envelope {}", envelope.toString());

        return envelope;
    }


    @Override
    public StopPlace importStopPlace(StopPlace newStopPlace, SiteFrame siteFrame,
                                     AtomicInteger topographicPlacesCreatedCounter) throws InterruptedException, ExecutionException {
        if (newStopPlace.getCentroid() == null
                || newStopPlace.getCentroid().getLocation() == null
                || newStopPlace.getCentroid().getLocation().getGeometryPoint() == null) {
            logger.info("Ignoring stop place {} - {} because it lacks geometry", newStopPlace.getName(), newStopPlace.getId());
            return null;
        }

        StopPlace existingStopPlace = findExistingStopPlaceFromOriginalId(newStopPlace);
        if (existingStopPlace != null) {
            return existingStopPlace;
        }


        if (newStopPlace.getName() != null) {
            Envelope boundingBox = createBoundingBox(newStopPlace.getCentroid());
            final StopPlace nearbyStopPlace = stopPlaceRepository.findNearbyStopPlace(boundingBox, newStopPlace.getName().getValue());

            if (nearbyStopPlace != null) {
                logger.info("Found nearby stop place with name: {}, id: {}", nearbyStopPlace.getName(), nearbyStopPlace.getId());

                Set<Quay> quaysToAdd = determineQuaysToAdd(newStopPlace, nearbyStopPlace);
                logger.info("Saving {} quays", quaysToAdd.size());
                quaysToAdd.forEach(quay -> {
                    logger.info("Saving quay {}, {}", quay.getId(), quay.getName());
                    resetIdAndKeepOriginalId(quay);
                    nearbyStopPlace.getQuays().add(quay);
                    quayRepository.save(quay);
                });
                // Assume topographic place already set ?
                stopPlaceRepository.save(nearbyStopPlace);
                return nearbyStopPlace;
            }
        }

        // TODO: Hack to avoid 'detached entity passed to persist'.
        newStopPlace.getCentroid().getLocation().setId(0);

        if (siteFrame.getTopographicPlaces() != null) {
            topographicPlaceCreator.setTopographicReference(newStopPlace,
                    siteFrame.getTopographicPlaces().getTopographicPlace(),
                    topographicPlacesCreatedCounter);
        }
        resetIdAndKeepOriginalId(newStopPlace);

        if (newStopPlace.getQuays() != null) {
            logger.debug("Stop place has {} quays", newStopPlace.getQuays().size());
            newStopPlace.getQuays().forEach(quay -> {
                resetIdAndKeepOriginalId(quay);
                logger.debug("Saving quay ");


//                quay.getCentroid().setId(null);
//                quay.getCentroid().getLocation().setId(0);

                quayRepository.save(quay);
            });
        }

        stopPlaceRepository.save(newStopPlace);
        logger.debug("Saving stop place {} {}", newStopPlace.getName(), newStopPlace.getId());
        return newStopPlace;
    }

    public Set<Quay> determineQuaysToAdd(StopPlace newStopPlace, StopPlace nearbyStopPlace) {

        logger.info("About to compare quays for {}", nearbyStopPlace.getId());

        if (nearbyStopPlace.getQuays() == null) {
            nearbyStopPlace.setQuays(new ArrayList<>());
        }

        Set<Quay> quaysToAdd = new HashSet<>();
        if (nearbyStopPlace.getQuays().isEmpty() && newStopPlace.getQuays() != null) {
            newStopPlace.getQuays().forEach(quaysToAdd::add);
        } else if (newStopPlace.getQuays() != null) {

            for(Quay newQuay : newStopPlace.getQuays()) {
                for(Quay existingQuay : nearbyStopPlace.getQuays()) {

                    if (hasSameCoordinates(existingQuay, newQuay)) {
                        logger.info("Quays does have the same coordinates and are probably the same");
                    } else {
                        logger.info("Found quay with other coordinates. Will add quay to nearby stop place {}", nearbyStopPlace.getId());
                        quaysToAdd.add(newQuay);
                        break;
                    }
                }
            }
        }
        return quaysToAdd;

    }

    public void resetIdAndKeepOriginalId(DataManagedObjectStructure dataManagedObjectStructure) {
        if (dataManagedObjectStructure.getId() != null) {
            KeyValueStructure importedId = new KeyValueStructure();
            importedId.setKey(ORIGINAL_ID_KEY);
            importedId.setValue(dataManagedObjectStructure.getId());
            if (dataManagedObjectStructure.getKeyList() == null) {
                dataManagedObjectStructure.setKeyList(new KeyListStructure());
            }
            dataManagedObjectStructure.getKeyList().getKeyValue().add(importedId);
            dataManagedObjectStructure.setId(null);
            logger.debug("Moved ID {} to key {}", importedId.getValue(), ORIGINAL_ID_KEY);
        }
    }
}
