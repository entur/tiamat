package org.rutebanken.tiamat.importer;


import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Import stop place without taking existing data into account.
 * Suitable for clean databases.
 */
@Component
public class InitialStopPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(InitialStopPlaceImporter.class);

    private final QuayRepository quayRepository;

    private final StopPlaceRepository stopPlaceRepository;

    private final NetexMapper netexMapper;

    private final TopographicPlaceCreator topographicPlaceCreator;

    @Autowired
    public InitialStopPlaceImporter(QuayRepository quayRepository, StopPlaceRepository stopPlaceRepository, NetexMapper netexMapper, TopographicPlaceCreator topographicPlaceCreator) {
        this.quayRepository = quayRepository;
        this.stopPlaceRepository = stopPlaceRepository;
        this.netexMapper = netexMapper;
        this.topographicPlaceCreator = topographicPlaceCreator;
    }

    public org.rutebanken.netex.model.StopPlace importStopPlace(AtomicInteger topographicPlacesCreatedCounter, SiteFrame siteFrame, StopPlace stopPlace) throws InterruptedException, ExecutionException {

        if(siteFrame.getTopographicPlaces() != null) {
            topographicPlaceCreator.setTopographicReference(stopPlace,
                    siteFrame.getTopographicPlaces().getTopographicPlace(),
                    topographicPlacesCreatedCounter);
        } else {
            logger.warn("Site frame does not contain any topographic places");
        }
//
//        if (stopPlace.getQuays() != null) {
//            logger.debug("Stop place has {} quays", stopPlace.getQuays().size());
//            Set<Quay> savedQuays = stopPlace.getQuays().stream().map(quay -> {
//                logger.debug("Saving quay ");
//                return quayRepository.save(quay);
//            }).collect(Collectors.toSet());
//            stopPlace.setQuays(savedQuays);
//        }

        stopPlaceRepository.save(stopPlace);
        logger.debug("Saving stop place {} {}", stopPlace.getName(), stopPlace.getNetexId());
        return netexMapper.mapToNetexModel(stopPlace);
    }

}
