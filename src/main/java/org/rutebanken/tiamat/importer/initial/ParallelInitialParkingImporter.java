package org.rutebanken.tiamat.importer.initial;

import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.versioning.ParkingVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

@Component
@Transactional
public class ParallelInitialParkingImporter {

    private static final Logger logger = LoggerFactory.getLogger(ParallelInitialParkingImporter.class);

    @Autowired
    private ParkingVersionedSaverService parkingVersionedSaverService;

    @Autowired
    private NetexMapper netexMapper;

    public List<org.rutebanken.netex.model.Parking> importParkings(List<Parking> tiamatParkings, AtomicInteger parkingsCreated) {

        return tiamatParkings.stream()
                .map(parking -> parkingVersionedSaverService.saveNewVersion(parking))
                .peek(parking -> parkingsCreated.incrementAndGet())
                .map(parking -> netexMapper.mapToNetexModel((Parking) parking))
                        .collect(toList());
    }

}
