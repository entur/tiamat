package org.rutebanken.tiamat.importer.initial;


import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.ReferenceResolver;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public InitialStopPlaceImporter(QuayRepository quayRepository, StopPlaceRepository stopPlaceRepository, NetexMapper netexMapper) {
        this.quayRepository = quayRepository;
        this.stopPlaceRepository = stopPlaceRepository;
        this.netexMapper = netexMapper;
    }

    public org.rutebanken.netex.model.StopPlace importStopPlace(AtomicInteger stopPlacesImported, StopPlace stopPlace) throws InterruptedException, ExecutionException {

        if (stopPlace.getQuays() != null) {
            logger.debug("Stop place has {} quays", stopPlace.getQuays().size());
            Set<Quay> savedQuays = stopPlace.getQuays().stream().map(quay -> {
                logger.debug("Saving quay ");
                return quayRepository.save(quay);
            }).collect(Collectors.toSet());
            stopPlace.setQuays(savedQuays);
        }

        stopPlaceRepository.save(stopPlace);
        logger.debug("Saving stop place {}, version {}, netex ID: {}", stopPlace.getName(), stopPlace.getVersion(), stopPlace.getNetexId());
        stopPlacesImported.incrementAndGet();
        return netexMapper.mapToNetexModel(stopPlace);
    }

}
