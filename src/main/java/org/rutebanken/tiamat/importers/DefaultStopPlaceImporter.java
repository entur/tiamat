package org.rutebanken.tiamat.importers;

import com.google.common.util.concurrent.Striped;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.pelias.CountyAndMunicipalityLookupService;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Component
@Qualifier("defaultStopPlaceImporter")
public class DefaultStopPlaceImporter implements StopPlaceImporter {

    public static final String ORIGINAL_ID_KEY = "imported-id";

    private static final Logger logger = LoggerFactory.getLogger(DefaultStopPlaceImporter.class);

    private TopographicPlaceCreator topographicPlaceCreator;

    private CountyAndMunicipalityLookupService countyAndMunicipalityLookupService;

    private QuayRepository quayRepository;

    private StopPlaceRepository stopPlaceRepository;

    private StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder;

    private NearbyStopPlaceFinder nearbyStopPlaceFinder;

    private Striped<Semaphore> stripedSemaphores = Striped.lazyWeakSemaphore(Integer.MAX_VALUE, 1);

    @Autowired
    public DefaultStopPlaceImporter(TopographicPlaceCreator topographicPlaceCreator,
                                    CountyAndMunicipalityLookupService countyAndMunicipalityLookupService,
                                    QuayRepository quayRepository, StopPlaceRepository stopPlaceRepository,
                                    StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder,
                                    NearbyStopPlaceFinder nearbyStopPlaceFinder) {
        this.topographicPlaceCreator = topographicPlaceCreator;
        this.countyAndMunicipalityLookupService = countyAndMunicipalityLookupService;
        this.quayRepository = quayRepository;
        this.stopPlaceRepository = stopPlaceRepository;
        this.stopPlaceFromOriginalIdFinder = stopPlaceFromOriginalIdFinder;
        this.nearbyStopPlaceFinder = nearbyStopPlaceFinder;
    }

    private StopPlace findNearbyOrExistingStopPlace(StopPlace newStopPlace) {
        final StopPlace existingStopPlace = stopPlaceFromOriginalIdFinder.find(newStopPlace);
        if (existingStopPlace != null) {
            return existingStopPlace;
        }

        if (newStopPlace.getName() != null) {
            final StopPlace nearbyStopPlace = nearbyStopPlaceFinder.find(newStopPlace);
            if (nearbyStopPlace != null) {
                logger.debug("Found nearby stop place with name: {}, id: {}", nearbyStopPlace.getName(), nearbyStopPlace.getId());
                return nearbyStopPlace;
            }
        }
        return null;
    }

    private Semaphore getStripedSemaphore(StopPlace stopPlace) {
        final String semaphoreKey;
        if (stopPlace.getId() != null) {
            semaphoreKey = "new-stop-place-"+stopPlace.getId();
        } else if (stopPlace.getCentroid() != null && stopPlace.getCentroid().getLocation() != null){
            LocationStructure location = stopPlace.getCentroid().getLocation();
            semaphoreKey = "location-"+location.getLongitude()+"-"+location.getLatitude();
        } else if (stopPlace.getName() != null
                && stopPlace.getName().getValue() != null
                && !stopPlace.getName().getValue().isEmpty()){
            semaphoreKey = "name-"+stopPlace.getName().getValue();
        } else {
            //TODO: proper and sensible striped locking.
            semaphoreKey = "";
        }
        return stripedSemaphores.get(semaphoreKey);
    }

    @Transactional
    @Override
    public StopPlace importStopPlace(StopPlace newStopPlace, SiteFrame siteFrame,
                                     AtomicInteger topographicPlacesCreatedCounter) throws InterruptedException, ExecutionException {
        if (newStopPlace.getCentroid() == null
                || newStopPlace.getCentroid().getLocation() == null
                || newStopPlace.getCentroid().getLocation().getGeometryPoint() == null) {
            logger.info("Ignoring stop place {} - {} because it lacks geometry", newStopPlace.getName(), newStopPlace.getId());
            return null;
        }

        Semaphore semaphore = getStripedSemaphore(newStopPlace);
        semaphore.acquire();

        try {


            logger.debug("Import stop place. Current ID: {}, Name: '{}', Quays: {}",
                    newStopPlace.getId(), newStopPlace.getName() != null ? newStopPlace.getName() : "",
                    newStopPlace.getQuays() != null ? newStopPlace.getQuays().size() : 0);

            final StopPlace foundStopPlace = findNearbyOrExistingStopPlace(newStopPlace);

            if(foundStopPlace != null) {
                Set<Quay> quaysToAdd = determineQuaysToAdd(newStopPlace, foundStopPlace);

                quaysToAdd.forEach(quay -> {
                    logger.debug("Saving quay {}, {}", quay.getId(), quay.getName());
                    resetIdAndKeepOriginalId(quay);
                    foundStopPlace.getQuays().add(quay);
                    quayRepository.save(quay);
                });

                if (!quaysToAdd.isEmpty()) {
                    logger.info("Found existing stop place {}. Adding {} quays to it", foundStopPlace.getId(), quaysToAdd.size());
                    stopPlaceRepository.save(foundStopPlace);
                }
                return initializeLazyReferences(foundStopPlace);
            }
            // TODO: Hack to avoid 'detached entity passed to persist'.
            newStopPlace.getCentroid().getLocation().setId(0);

            if (siteFrame.getTopographicPlaces() != null
                    && siteFrame.getTopographicPlaces().getTopographicPlace() != null
                    && !siteFrame.getTopographicPlaces().getTopographicPlace().isEmpty()) {
                topographicPlaceCreator.setTopographicReference(newStopPlace,
                        siteFrame.getTopographicPlaces().getTopographicPlace(),
                        topographicPlacesCreatedCounter);
            } else {
                lookupCountyAndMunicipality(newStopPlace);
            }
            Long originalId = newStopPlace.getId();
            resetIdAndKeepOriginalId(newStopPlace);

            if (newStopPlace.getQuays() != null) {
                logger.debug("Stop place has {} quays", newStopPlace.getQuays().size());
                newStopPlace.getQuays().forEach(quay -> {
                    if (quay.getCentroid() == null) {
                        logger.warn("Centroid is null for quay with id {}. Ignoring it.", quay.getId());
                    } else if (quay.getCentroid().getLocation() == null) {
                        logger.warn("Location for centroid of quay with id {} is null. Ignoring it.", quay.getId());
                    } else {
                        resetIdAndKeepOriginalId(quay);
                        quay.getCentroid().setId(null);
                        quay.getCentroid().getLocation().setId(0);
                        quayRepository.save(quay);
                    }
                });
            }

            stopPlaceRepository.save(newStopPlace);
            stopPlaceFromOriginalIdFinder.update(originalId, newStopPlace.getId());
            nearbyStopPlaceFinder.update(newStopPlace);
            logger.info("Saved stop place {} {} with {} quays", newStopPlace.getName(), newStopPlace.getId(), newStopPlace.getQuays() != null ? newStopPlace.getQuays().size() : 0);

            return initializeLazyReferences(newStopPlace);
        }
        finally {
            semaphore.release();
        }
    }

    private void lookupCountyAndMunicipality(StopPlace stopPlace) {
        try {
            countyAndMunicipalityLookupService.populateCountyAndMunicipality(stopPlace);
        } catch (IOException|InterruptedException e) {
            logger.warn("Could not lookup county and municipality for stop place with id {}", stopPlace.getId());
        }
    }

    private StopPlace initializeLazyReferences(StopPlace stopPlace) {
        if(stopPlace != null) {
            Hibernate.initialize(stopPlace.getLevels());
            Hibernate.initialize(stopPlace.getOtherTransportModes());
        }
        return stopPlace;
    }

    /**
     * Inspect quays from incoming stop place. If they do not exist from before, add them.
     */
    public Set<Quay> determineQuaysToAdd(StopPlace newStopPlace, StopPlace foundStopPlace) {

        logger.debug("About to compare quays for {}", foundStopPlace.getId());

        if (foundStopPlace.getQuays() == null) {
            foundStopPlace.setQuays(new ArrayList<>());
        }

        Set<Quay> quaysToAdd = new HashSet<>();
        if (foundStopPlace.getQuays().isEmpty() && newStopPlace.getQuays() != null) {
            logger.debug("Existing stop place {} does not have any quays, using all quays from incoming stop {}, {}",
                    foundStopPlace.getId(), newStopPlace.getId(), newStopPlace.getName());
            quaysToAdd.addAll(newStopPlace.getQuays());
        } else if (newStopPlace.getQuays() != null && !newStopPlace.getQuays().isEmpty()) {
            logger.debug("Existing stop {} has {} quays. Incoming stop {} has {} quays. Removing quays that has matching coordinates",
                    foundStopPlace.getId(), foundStopPlace.getQuays().size(),
                    newStopPlace.getId(), newStopPlace.getQuays().size());
            newStopPlace.getQuays().stream()
                    .filter(newQuay -> !containsQuayWithCoordinates(newQuay, foundStopPlace.getQuays(), quaysToAdd))
                    .peek(newQuay -> logger.debug("Adding quay {}, {}", newQuay.getId(), newQuay.getName()))
                    .forEach(quaysToAdd::add);
        }
        logger.debug("Found {} quays to add to existing stop place {}, {}",  quaysToAdd.size(), foundStopPlace.getId(), foundStopPlace.getName());
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
            importedId.setValue(String.valueOf(dataManagedObjectStructure.getId()));
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
