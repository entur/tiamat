package org.rutebanken.tiamat.importer;


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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Import stop place without taking existing data into account.
 * Suitable for clean databases. Topographical places may already exist,
 * or will be created by the TopoGraphicPlaceCreator.
 */
@Component
@Qualifier("cleanStopPlaceImporter")
public class SimpleStopPlaceImporter implements StopPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(SimpleStopPlaceImporter.class);

    private final TopographicPlaceCreator topographicPlaceCreator;

    private final QuayRepository quayRepository;

    private final StopPlaceRepository stopPlaceRepository;

    private final NetexMapper netexMapper;


    @Autowired
    public SimpleStopPlaceImporter(TopographicPlaceCreator topographicPlaceCreator, QuayRepository quayRepository, StopPlaceRepository stopPlaceRepository, NetexMapper netexMapper) {
        this.topographicPlaceCreator = topographicPlaceCreator;
        this.quayRepository = quayRepository;
        this.stopPlaceRepository = stopPlaceRepository;
        this.netexMapper = netexMapper;
    }

    @Override
    public org.rutebanken.netex.model.StopPlace importStopPlace(StopPlace stopPlace, SiteFrame siteFrame,
                                                                AtomicInteger topographicPlacesCreatedCounter) throws InterruptedException, ExecutionException {
        if(siteFrame.getTopographicPlaces() != null) {
            topographicPlaceCreator.setTopographicReference(stopPlace,
                    siteFrame.getTopographicPlaces().getTopographicPlace(),
                    topographicPlacesCreatedCounter);
        } else {
            logger.warn("Site frame does not contain any topographic places");
        }

        if (stopPlace.getQuays() != null) {
            logger.debug("Stop place has {} quays", stopPlace.getQuays().size());
            stopPlace.getQuays().forEach(quay -> {
                logger.debug("Saving quay ");
                quayRepository.save(quay);
            });
        }

        stopPlaceRepository.save(stopPlace);
        logger.debug("Saving stop place {} {}", stopPlace.getName(), stopPlace.getId());
        return netexMapper.mapToNetexModel(stopPlace);
    }

}
