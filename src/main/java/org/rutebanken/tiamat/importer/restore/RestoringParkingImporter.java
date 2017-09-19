/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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
