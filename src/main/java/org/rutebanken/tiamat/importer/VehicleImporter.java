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

package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.vehicle.Vehicle;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.VehicleRepository;
import org.rutebanken.tiamat.versioning.save.VehicleVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

@Transactional
@Component
public class VehicleImporter {

    private static final Logger logger = LoggerFactory.getLogger(VehicleImporter.class);

    private final NetexMapper netexMapper;

    private final VehicleRepository vehicleRepository;

    private final VehicleVersionedSaverService vehicleVersionedSaverService;

    @Autowired
    public VehicleImporter(NetexMapper netexMapper, VehicleRepository vehicleRepository, VehicleVersionedSaverService vehicleVersionedSaverService) {
        this.netexMapper = netexMapper;
        this.vehicleRepository = vehicleRepository;
        this.vehicleVersionedSaverService = vehicleVersionedSaverService;
    }

    public List<org.rutebanken.netex.model.Vehicle> importVehicles(List<Vehicle> vehicles, AtomicInteger vehiclesCounter) {

        logger.info("Importing {} incoming vehicles", vehicles.size());

        List<Vehicle> result = new ArrayList<>();

        logger.info("Importing vehicles");
        for (Vehicle incomingVehicle : vehicles) {
            result.add(importVehicle(incomingVehicle, vehiclesCounter));
        }

        return result.stream().map(vehicle -> netexMapper.mapToNetexModel(vehicle)).collect(toList());

    }

    private Vehicle importVehicle(Vehicle incomingVehicle, AtomicInteger vehiclesCounter) {
        logger.debug("{}", incomingVehicle);
        incomingVehicle = vehicleVersionedSaverService.saveNewVersion(incomingVehicle);

        vehiclesCounter.incrementAndGet();
        return incomingVehicle;
    }

}
