package org.rutebanken.tiamat.importer.restore;


import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.ParkingRepository;
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
public class RestoringParkingImporter {

    private static final Logger logger = LoggerFactory.getLogger(RestoringParkingImporter.class);

    private final ParkingRepository parkingRepository;

    private final NetexMapper netexMapper;

    @Autowired
    public RestoringParkingImporter(ParkingRepository parkingRepository, NetexMapper netexMapper) {
        this.parkingRepository = parkingRepository;
        this.netexMapper = netexMapper;
    }

    public org.rutebanken.netex.model.Parking importParking(AtomicInteger parkingsImported, Parking parking) {
        parkingRepository.save(parking);
        logger.debug("Saving parking {}, version {}, netex ID: {}", parking.getName(), parking.getVersion(), parking.getNetexId());
        parkingsImported.incrementAndGet();
        return netexMapper.mapToNetexModel(parking);
    }

}
