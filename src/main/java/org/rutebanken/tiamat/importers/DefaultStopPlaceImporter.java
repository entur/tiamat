package org.rutebanken.tiamat.importers;

import com.google.common.util.concurrent.Striped;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netexmapping.NetexIdMapper;
import org.rutebanken.tiamat.pelias.CountyAndMunicipalityLookupService;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Qualifier("defaultStopPlaceImporter")
public class DefaultStopPlaceImporter implements StopPlaceImporter {


    private static final Logger logger = LoggerFactory.getLogger(DefaultStopPlaceImporter.class);

    private TopographicPlaceCreator topographicPlaceCreator;

    private CountyAndMunicipalityLookupService countyAndMunicipalityLookupService;

    private QuayRepository quayRepository;

    private StopPlaceRepository stopPlaceRepository;

    private StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder;

    private NearbyStopPlaceFinder nearbyStopPlaceFinder;

//    private KeyStringValueAppender keyStringValueAppender;

    private KeyValueListAppender keyValueListAppender;

    private static DecimalFormat format = new DecimalFormat("#.#");

    private Striped<Semaphore> stripedSemaphores = Striped.lazyWeakSemaphore(Integer.MAX_VALUE, 1);


    @Autowired
    public DefaultStopPlaceImporter(TopographicPlaceCreator topographicPlaceCreator,
                                    CountyAndMunicipalityLookupService countyAndMunicipalityLookupService,
                                    QuayRepository quayRepository, StopPlaceRepository stopPlaceRepository,
                                    StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder,
                                    NearbyStopPlaceFinder nearbyStopPlaceFinder, KeyValueListAppender keyValueListAppender) {
        this.topographicPlaceCreator = topographicPlaceCreator;
        this.countyAndMunicipalityLookupService = countyAndMunicipalityLookupService;
        this.quayRepository = quayRepository;
        this.stopPlaceRepository = stopPlaceRepository;
        this.stopPlaceFromOriginalIdFinder = stopPlaceFromOriginalIdFinder;
        this.nearbyStopPlaceFinder = nearbyStopPlaceFinder;
        this.keyValueListAppender = keyValueListAppender;
    }

//    @Transactional
    @Override
    public StopPlace importStopPlace(StopPlace newStopPlace, SiteFrame siteFrame,
                                     AtomicInteger topographicPlacesCreatedCounter) throws InterruptedException, ExecutionException {
        Semaphore semaphore = getStripedSemaphore(newStopPlace);
        semaphore.acquire();

        try {
            logger.info("Import stop place {}", newStopPlace);

            final StopPlace foundStopPlace = findNearbyOrExistingStopPlace(newStopPlace);

            final StopPlace stopPlace;
            if(foundStopPlace != null) {
                stopPlace = handleAlreadyExistingStopPlace(foundStopPlace, newStopPlace);
            } else {
                stopPlace = handleCompletelyNewStopPlace(newStopPlace, siteFrame, topographicPlacesCreatedCounter);
            }
            return stopPlace;
        }
        finally {
            semaphore.release();
        }
    }


    public StopPlace handleCompletelyNewStopPlace(StopPlace newStopPlace, SiteFrame siteFrame, AtomicInteger topographicPlacesCreatedCounter) throws ExecutionException {

        resetLocationIds(newStopPlace);

        if (hasTopographicPlaces(siteFrame)) {
            topographicPlaceCreator.setTopographicReference(newStopPlace,
                    siteFrame.getTopographicPlaces().getTopographicPlace(),
                    topographicPlacesCreatedCounter);
        } else {
            lookupCountyAndMunicipality(newStopPlace, topographicPlacesCreatedCounter);
        }

        if (newStopPlace.getQuays() != null) {
            logger.info("Importing quays for new stop place {}", newStopPlace);
            newStopPlace.getQuays().forEach(quay -> {
                if (!quay.hasCoordinates()) {
                    logger.warn("Quay does not have coordinates.", quay.getId());
                }
                resetLocationIds(quay);
                logger.info("Saving quay {}", quay);
                quayRepository.save(quay);
                logger.debug("Saved quay. Got id {} back", quay.getId());
            });
        }

        stopPlaceRepository.save(newStopPlace);
        stopPlaceFromOriginalIdFinder.update(newStopPlace);
        nearbyStopPlaceFinder.update(newStopPlace);
        logger.info("Saved stop place {}", newStopPlace);

        return newStopPlace;
    }

    private void resetLocationIds(Zone_VersionStructure zone) {
        if(zone.getCentroid() != null) {
           SimplePoint centroid = zone.getCentroid();
           centroid.setId(null);
            if(centroid.getLocation() != null) {
                centroid.getLocation().setId(0);
            }
        }
    }

    private boolean hasTopographicPlaces(SiteFrame siteFrame) {
        return siteFrame.getTopographicPlaces() != null
                && siteFrame.getTopographicPlaces().getTopographicPlace() != null
                && !siteFrame.getTopographicPlaces().getTopographicPlace().isEmpty();
    }

    public StopPlace handleAlreadyExistingStopPlace(StopPlace foundStopPlace, StopPlace newStopPlace) {
        logger.info("Found existing stop place {} from incoming {}", foundStopPlace, newStopPlace);

        boolean quaysChanged = addAndSaveNewQuays(newStopPlace, foundStopPlace);
        boolean originalIdChanged = keyValueListAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, newStopPlace, foundStopPlace);

        if(originalIdChanged) {
            logger.info("Updated existing stop place {}. ", foundStopPlace);
            foundStopPlace.getQuays().forEach(q -> logger.info("{}:  Quay {}: {}", foundStopPlace.getId(), q.getId(), q.getName()));
            stopPlaceRepository.save(foundStopPlace);
            stopPlaceFromOriginalIdFinder.update(newStopPlace);
            nearbyStopPlaceFinder.update(newStopPlace);
        }
        return foundStopPlace;
    }

    private void lookupCountyAndMunicipality(StopPlace stopPlace, AtomicInteger topographicPlacesCreatedCounter) {
        try {
            countyAndMunicipalityLookupService.populateCountyAndMunicipality(stopPlace, topographicPlacesCreatedCounter);
        } catch (IOException|InterruptedException e) {
            logger.warn("Could not lookup county and municipality for stop place with id {}", stopPlace.getId());
        }
    }

    /**
     * Inspect quays from incoming AND matching stop place. If they do not exist from before, add them.
     */
    public boolean addAndSaveNewQuays(StopPlace newStopPlace, StopPlace foundStopPlace) {

        AtomicInteger updatedQuays = new AtomicInteger();
        AtomicInteger createdQuays = new AtomicInteger();


        logger.debug("About to compare quays for {}", foundStopPlace.getId());

        if (foundStopPlace.getQuays() == null) {
            foundStopPlace.setQuays(new ArrayList<>());
        }

        Set<Quay> quaysToAdd = new HashSet<>();
        if (foundStopPlace.getQuays().isEmpty() && newStopPlace.getQuays() != null) {
            logger.debug("Existing stop place {} does not have any quays, using all quays from incoming stop {}, {}",
                    foundStopPlace, newStopPlace, newStopPlace.getName());
            quaysToAdd.addAll(newStopPlace.getQuays());
        } else if (newStopPlace.getQuays() != null && !newStopPlace.getQuays().isEmpty()) {

            logger.debug("Comparing existing: {}, incoming: {}. Removing/ignoring quays that has matching coordinates (but keeping their ID)",
                    foundStopPlace, newStopPlace);

            for(Quay newQuay : newStopPlace.getQuays()) {
               Optional<Quay> optionalExistingQuay = findQuayWithCoordinates(newQuay, foundStopPlace.getQuays(), quaysToAdd);

                if(optionalExistingQuay.isPresent()) {
                    Quay existingQuay = optionalExistingQuay.get();
                    logger.debug("Found matching quay {} for incoming quay {}. Appending original ID to the key if required {}", existingQuay, newQuay, NetexIdMapper.ORIGINAL_ID_KEY);
                    boolean changed = keyValueListAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, newQuay, existingQuay);

                    if(changed) {
                        logger.info("Updated quay {}, {}", existingQuay.getId(), existingQuay);
                        updatedQuays.incrementAndGet();
                        quayRepository.save(existingQuay);
                    }
                } else {
                    logger.info("Incoming {} does not match any existing quays for {}. Adding and saving it.", newQuay, foundStopPlace);
                    newQuay.setId(null);
                    resetLocationIds(newQuay);
                    foundStopPlace.getQuays().add(newQuay);
                    quayRepository.save(newQuay);
                    createdQuays.incrementAndGet();
                }
            }
        }


        logger.debug("Created {} quays and updated {} quays for stop place {}", createdQuays.get(), updatedQuays.get(), foundStopPlace);
        return updatedQuays.get() > 0 || createdQuays.get() > 0 ;
    }

    /**
     * Find first matching quay that has the same coordinates as the new Quay.
     */
    public Optional<Quay> findQuayWithCoordinates(Quay newQuay, Collection<Quay> existingQuays, Collection<Quay> quaysToAdd) {
        List<Quay> concatenatedQuays = new ArrayList<>();
        concatenatedQuays.addAll(existingQuays);
        concatenatedQuays.addAll(quaysToAdd);

        for(Quay alreadyAddedOrExistingQuay : concatenatedQuays) {
            boolean hasSameCoordinates = hasSameCoordinates(alreadyAddedOrExistingQuay, newQuay);
            logger.info("Does quay {} and {} have the same coordinates? {}", alreadyAddedOrExistingQuay, newQuay, hasSameCoordinates);
            if(hasSameCoordinates) {
                return Optional.of(alreadyAddedOrExistingQuay);
            }
        }
        return Optional.empty();
    }

    public boolean hasSameCoordinates(Quay quay1, Quay quay2) {
        if (quay1.getCentroid() == null || quay2.getCentroid() == null) {
            return false;
        }
        return (quay1.getCentroid().getLocation().getGeometryPoint()
                .distance(quay2.getCentroid().getLocation().getGeometryPoint()) == 0.0);
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
        if (stopPlace.getCentroid() != null && stopPlace.getCentroid().getLocation() != null) {
            LocationStructure location = stopPlace.getCentroid().getLocation();
            semaphoreKey = locationSemaphore(location);
        } else
        if (stopPlace.getId() != null) {
            semaphoreKey = "new-stop-place-"+stopPlace.getId();
        } else if (stopPlace.getName() != null
                && stopPlace.getName().getValue() != null
                && !stopPlace.getName().getValue().isEmpty()){
            semaphoreKey = "name-"+stopPlace.getName().getValue();
        } else {
            semaphoreKey = "all";
        }
        logger.info("Using semaphore key '{}' for stop place {}", semaphoreKey, stopPlace);
        return stripedSemaphores.get(semaphoreKey);
    }

    private String locationSemaphore(LocationStructure location) {
        return "location-"+ format.format(location.getLongitude())+"-"+ format.format(location.getLatitude());
    }


}
