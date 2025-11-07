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

import org.rutebanken.tiamat.model.vehicle.VehicleModel;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.VehicleModelRepository;
import org.rutebanken.tiamat.versioning.save.VehicleModelVersionedSaverService;
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
public class VehicleModelImporter {

    private static final Logger logger = LoggerFactory.getLogger(VehicleModelImporter.class);

    private final NetexMapper netexMapper;

    private final VehicleModelRepository vehicleModelRepository;

    private final VehicleModelVersionedSaverService vehicleModelVersionedSaverService;

    @Autowired
    public VehicleModelImporter(NetexMapper netexMapper, VehicleModelRepository vehicleModelRepository, VehicleModelVersionedSaverService vehicleModelVersionedSaverService) {
        this.netexMapper = netexMapper;
        this.vehicleModelRepository = vehicleModelRepository;
        this.vehicleModelVersionedSaverService = vehicleModelVersionedSaverService;
    }

    public List<org.rutebanken.netex.model.VehicleModel> importVehicleModels(List<VehicleModel> vehicleModels, AtomicInteger vehicleModelsCounter) {

        logger.info("Importing {} incoming vehicle types", vehicleModels.size());

        List<VehicleModel> result = new ArrayList<>();

        logger.info("Importing vehicle types");
        for (VehicleModel incomingVehicleModel : vehicleModels) {
            result.add(importVehicleModel(incomingVehicleModel, vehicleModelsCounter));
        }

        return result.stream().map(vehicleModel -> netexMapper.mapToNetexModel(vehicleModel)).collect(toList());

    }

    private VehicleModel importVehicleModel(VehicleModel incomingVehicleModel, AtomicInteger vehicleModelsCounter) {
        logger.debug("{}", incomingVehicleModel);
        incomingVehicleModel = vehicleModelVersionedSaverService.saveNewVersion(incomingVehicleModel);

        vehicleModelsCounter.incrementAndGet();
        return incomingVehicleModel;
    }

}
