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

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Component
@Qualifier("defaultStopPlaceImporter")
public class DefaultStopPlaceImporter implements StopPlaceImporter {

    public static final String ORIGINAL_ID_KEY = "imported-id";

    private static final Logger logger = LoggerFactory.getLogger(DefaultStopPlaceImporter.class);

    private TopographicPlaceCreator topographicPlaceCreator;

    private QuayRepository quayRepository;

    private StopPlaceRepository stopPlaceRepository;

    private StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder;

    private NearbyStopPlaceFinder nearbyStopPlaceFinder;

    @Autowired
    public DefaultStopPlaceImporter(TopographicPlaceCreator topographicPlaceCreator,
                                    QuayRepository quayRepository, StopPlaceRepository stopPlaceRepository,
                                    StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder,
                                    NearbyStopPlaceFinder nearbyStopPlaceFinder) {
        this.topographicPlaceCreator = topographicPlaceCreator;
        this.quayRepository = quayRepository;
        this.stopPlaceRepository = stopPlaceRepository;
        this.stopPlaceFromOriginalIdFinder = stopPlaceFromOriginalIdFinder;
        this.nearbyStopPlaceFinder = nearbyStopPlaceFinder;
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

        logger.debug("Import stop place. Current ID: {}, Name: '{}', Quays: {}",
                newStopPlace.getId(), newStopPlace.getName() != null ? newStopPlace.getName() : "",
                newStopPlace.getQuays() != null ? newStopPlace.getQuays().size() : 0);

        StopPlace existingStopPlace = stopPlaceFromOriginalIdFinder.find(newStopPlace);
        if (existingStopPlace != null) {
            return existingStopPlace;
        }

        if (newStopPlace.getName() != null) {

            final StopPlace nearbyStopPlace = nearbyStopPlaceFinder.find(newStopPlace);

            if (nearbyStopPlace != null) {
                logger.debug("Found nearby stop place with name: {}, id: {}", nearbyStopPlace.getName(), nearbyStopPlace.getId());

                Set<Quay> quaysToAdd = determineQuaysToAdd(newStopPlace, nearbyStopPlace);
                quaysToAdd.forEach(quay -> {
                    logger.debug("Saving quay {}, {}", quay.getId(), quay.getName());
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
        String originalId = newStopPlace.getId();
        resetIdAndKeepOriginalId(newStopPlace);

        if (newStopPlace.getQuays() != null) {
            logger.debug("Stop place has {} quays", newStopPlace.getQuays().size());
            newStopPlace.getQuays().forEach(quay -> {
                if(quay.getCentroid() == null) {
                    logger.warn("Centroid is null for quay with id {}. Ignoring it.", quay.getId());
                } else if (quay.getCentroid().getLocation() == null) {
                    logger.warn("Location for centroid of quay with id {} is null. Ignoring it.", quay.getId());
                } else {
                    resetIdAndKeepOriginalId(quay);
                    quay.getCentroid().setId("");
                    quay.getCentroid().getLocation().setId(0);
                    quayRepository.save(quay);
                }
            });
        }

        stopPlaceRepository.save(newStopPlace);
        stopPlaceFromOriginalIdFinder.update(originalId, newStopPlace.getId());
        nearbyStopPlaceFinder.update(newStopPlace);
        logger.debug("Saving stop place {} {} with {} quays", newStopPlace.getName(), newStopPlace.getId(), newStopPlace.getQuays() != null ? newStopPlace.getQuays().size() : 0);
        return newStopPlace;
    }

    public Set<Quay> determineQuaysToAdd(StopPlace newStopPlace, StopPlace nearbyStopPlace) {

        logger.info("About to compare quays for {}", nearbyStopPlace.getId());

        if (nearbyStopPlace.getQuays() == null) {
            nearbyStopPlace.setQuays(new ArrayList<>());
        }

        Set<Quay> quaysToAdd = new HashSet<>();
        if (nearbyStopPlace.getQuays().isEmpty() && newStopPlace.getQuays() != null) {
            quaysToAdd.addAll(newStopPlace.getQuays());
        } else if (newStopPlace.getQuays() != null) {
            newStopPlace.getQuays().stream()
                    .filter(newQuay -> !containsQuayWithCoordinates(newQuay, nearbyStopPlace.getQuays(), quaysToAdd))
                    .forEach(quaysToAdd::add);
        }
        return quaysToAdd;
    }

    public boolean containsQuayWithCoordinates(Quay newQuay, Collection<Quay> existingQuays, Collection<Quay> quaysToAdd) {
        return Stream.concat(existingQuays.stream(), quaysToAdd.stream())
                .anyMatch(existingQuay -> hasSameCoordinates(existingQuay, newQuay));
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

    public boolean hasSameCoordinates(Zone_VersionStructure zone1, Zone_VersionStructure zone2) {
        if (zone1.getCentroid() == null || zone2.getCentroid() == null) {
            return false;
        }
        return (zone1.getCentroid().getLocation().getGeometryPoint()
                .distance(zone2.getCentroid().getLocation().getGeometryPoint()) == 0.0);
    }



}
