package org.rutebanken.tiamat.importer.restore;


import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Import stop place without taking existing data into account.
 * Suitable for clean databases and importing data from tiamat export.
 * No versions will be created.
 */
@Component
public class RestoringStopPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(RestoringStopPlaceImporter.class);

    private final StopPlaceRepository stopPlaceRepository;

    private final TopographicPlaceRepository topographicPlaceRepository;

    private final NetexMapper netexMapper;

    @Autowired
    public RestoringStopPlaceImporter(StopPlaceRepository stopPlaceRepository, TopographicPlaceRepository topographicPlaceRepository, NetexMapper netexMapper) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.netexMapper = netexMapper;
    }

    public org.rutebanken.netex.model.StopPlace importStopPlace(AtomicInteger stopPlacesImported, StopPlace stopPlace) {

        if(stopPlace.getTopographicPlace() != null) {

            String netexId = stopPlace.getTopographicPlace().getNetexId();
            Long version = stopPlace.getTopographicPlace().getVersion();

            stopPlace.setTopographicPlace(topographicPlaceRepository.findFirstByNetexIdAndVersion(netexId, version));
            logger.trace("Resolved topographic place from {}:{} for stop place {}:{}", netexId, version, stopPlace.getNetexId(), stopPlace.getVersion());
        }

        stopPlaceRepository.save(stopPlace);
        logger.debug("Saving stop place {}, version {}, netex ID: {}", stopPlace.getName(), stopPlace.getVersion(), stopPlace.getNetexId());
        stopPlacesImported.incrementAndGet();
        return netexMapper.mapToNetexModel(stopPlace);
    }

}
