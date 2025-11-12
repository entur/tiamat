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

import org.rutebanken.tiamat.model.vehicle.VehicleType;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.versioning.save.VehicleTypeVersionedSaverService;
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
public class VehicleTypeImporter {

    private static final Logger logger = LoggerFactory.getLogger(VehicleTypeImporter.class);

    private final NetexMapper netexMapper;

    private final VehicleTypeVersionedSaverService vehicleTypeVersionedSaverService;

    @Autowired
    public VehicleTypeImporter(NetexMapper netexMapper, VehicleTypeVersionedSaverService vehicleTypeVersionedSaverService) {
        this.netexMapper = netexMapper;
        this.vehicleTypeVersionedSaverService = vehicleTypeVersionedSaverService;
    }

    public List<org.rutebanken.netex.model.VehicleType> importVehicleTypes(List<VehicleType> vehicleTypes, AtomicInteger vehicleTypesCounter) {

        logger.info("Importing {} incoming vehicle types", vehicleTypes.size());

        List<VehicleType> result = new ArrayList<>();

        logger.info("Importing vehicle types");
        for (VehicleType incomingVehicleType : vehicleTypes) {
            result.add(importVehicleType(incomingVehicleType, vehicleTypesCounter));
        }

        return result.stream().map(vehicleType -> netexMapper.mapToNetexModel(vehicleType)).collect(toList());

    }

    private VehicleType importVehicleType(VehicleType incomingVehicleType, AtomicInteger vehicleTypesCounter) {
        logger.debug("{}", incomingVehicleType);
        incomingVehicleType = vehicleTypeVersionedSaverService.saveNewVersion(incomingVehicleType);

        vehicleTypesCounter.incrementAndGet();
        return incomingVehicleType;
    }

}
