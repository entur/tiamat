package org.rutebanken.tiamat.importers;

import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netexmapping.NetexIdMapper;
import org.rutebanken.tiamat.pelias.CountyAndMunicipalityLookupService;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.CentroidComputer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Qualifier("defaultStopPlaceImporter")
@Transactional
public class DefaultStopPlaceImporter implements StopPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStopPlaceImporter.class);

    private final TopographicPlaceCreator topographicPlaceCreator;

    private final CountyAndMunicipalityLookupService countyAndMunicipalityLookupService;

    private final QuayRepository quayRepository;

    private final StopPlaceRepository stopPlaceRepository;

    private final StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder;

    private final NearbyStopPlaceFinder nearbyStopPlaceFinder;

    private final CentroidComputer centroidComputer;

    private final KeyValueListAppender keyValueListAppender;

    private final QuayMerger quayMerger;

    private static DecimalFormat format = new DecimalFormat("#.#");



    @Autowired
    public DefaultStopPlaceImporter(TopographicPlaceCreator topographicPlaceCreator,
                                    CountyAndMunicipalityLookupService countyAndMunicipalityLookupService,
                                    QuayRepository quayRepository, StopPlaceRepository stopPlaceRepository,
                                    StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder,
                                    NearbyStopPlaceFinder nearbyStopPlaceFinder,
                                    CentroidComputer centroidComputer,
                                    KeyValueListAppender keyValueListAppender, QuayMerger quayMerger) {
        this.topographicPlaceCreator = topographicPlaceCreator;
        this.countyAndMunicipalityLookupService = countyAndMunicipalityLookupService;
        this.quayRepository = quayRepository;
        this.stopPlaceRepository = stopPlaceRepository;
        this.stopPlaceFromOriginalIdFinder = stopPlaceFromOriginalIdFinder;
        this.nearbyStopPlaceFinder = nearbyStopPlaceFinder;
        this.centroidComputer = centroidComputer;
        this.keyValueListAppender = keyValueListAppender;
        this.quayMerger = quayMerger;
    }

    @Override
    public StopPlace importStopPlace(StopPlace newStopPlace, SiteFrame siteFrame,
                                     AtomicInteger topographicPlacesCreatedCounter) throws InterruptedException, ExecutionException {
        logger.info("Import stop place {}", newStopPlace);

        final StopPlace foundStopPlace = findNearbyOrExistingStopPlace(newStopPlace);

        final StopPlace stopPlace;
        if (foundStopPlace != null) {
            stopPlace = handleAlreadyExistingStopPlace(foundStopPlace, newStopPlace);
        } else {
            stopPlace = handleCompletelyNewStopPlace(newStopPlace, siteFrame, topographicPlacesCreatedCounter);
        }
        return stopPlace;
    }


    public StopPlace handleCompletelyNewStopPlace(StopPlace newStopPlace, SiteFrame siteFrame, AtomicInteger topographicPlacesCreatedCounter) throws ExecutionException {

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
                logger.info("Saving quay {}", quay);
                quayRepository.save(quay);
                logger.debug("Saved quay. Got id {} back", quay.getId());
            });
        }
        centroidComputer.computeCentroidForStopPlace(newStopPlace);
        return saveAndUpdateCache(newStopPlace);
    }

    public StopPlace handleAlreadyExistingStopPlace(StopPlace foundStopPlace, StopPlace newStopPlace) {
        logger.info("Found existing stop place {} from incoming {}", foundStopPlace, newStopPlace);

        boolean quaysChanged = quayMerger.addAndSaveNewQuays(newStopPlace, foundStopPlace);
        boolean originalIdChanged = keyValueListAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, newStopPlace, foundStopPlace);
        boolean centroidChanged = centroidComputer.computeCentroidForStopPlace(foundStopPlace);

        if (originalIdChanged || quaysChanged || centroidChanged) {
            logger.info("Updated existing stop place {}. ", foundStopPlace);
            foundStopPlace.getQuays().forEach(q -> logger.info("Stop place {}:  Quay {}: {}", foundStopPlace.getId(), q.getId(), q.getName()));
            saveAndUpdateCache(foundStopPlace);
        }
        return foundStopPlace;
    }

    private StopPlace saveAndUpdateCache(StopPlace stopPlace) {
        stopPlaceRepository.save(stopPlace);
        stopPlaceFromOriginalIdFinder.update(stopPlace);
        nearbyStopPlaceFinder.update(stopPlace);
        logger.info("Saved stop place {}", stopPlace);
        return stopPlace;
    }

    private boolean hasTopographicPlaces(SiteFrame siteFrame) {
        return siteFrame.getTopographicPlaces() != null
                && siteFrame.getTopographicPlaces().getTopographicPlace() != null
                && !siteFrame.getTopographicPlaces().getTopographicPlace().isEmpty();
    }

    private void lookupCountyAndMunicipality(StopPlace stopPlace, AtomicInteger topographicPlacesCreatedCounter) {
        try {
            countyAndMunicipalityLookupService.populateCountyAndMunicipality(stopPlace, topographicPlacesCreatedCounter);
        } catch (IOException | InterruptedException e) {
            logger.warn("Could not lookup county and municipality for stop place with id {}", stopPlace.getId());
        }
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

}
