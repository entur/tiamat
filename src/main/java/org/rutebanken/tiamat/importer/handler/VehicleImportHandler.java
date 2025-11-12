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

package org.rutebanken.tiamat.importer.handler;

import com.hazelcast.core.HazelcastInstance;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.VehicleImporter;
import org.rutebanken.tiamat.importer.VehicleTypeImporter;
import org.rutebanken.tiamat.importer.converter.VehicleIdConverter;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class VehicleImportHandler {

    private static final Logger logger = LoggerFactory.getLogger(VehicleImportHandler.class);

    /**
     * Hazelcast lock key for stop place import.
     */
    private static final String VEHICLE_IMPORT_LOCK_KEY = "VEHICLE_IMPORT_LOCK_KEY";

    private final PublicationDeliveryHelper publicationDeliveryHelper;
    private final NetexMapper netexMapper;
    private final VehicleImporter vehicleImporter;
    private final VehicleIdConverter vehicleIdConverter;

    public VehicleImportHandler(PublicationDeliveryHelper publicationDeliveryHelper, NetexMapper netexMapper, VehicleImporter vehicleImporter, VehicleIdConverter vehicleIdConverter) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.netexMapper = netexMapper;
        this.vehicleImporter = vehicleImporter;
        this.vehicleIdConverter = vehicleIdConverter;
    }

    public void handleVehicles(ResourceFrame netexResourceFrame, ImportParams importParams, AtomicInteger vehiclesCounter, ResourceFrame responseResourceFrame) {
        if (publicationDeliveryHelper.hasVehicles(netexResourceFrame)) {
            var originalVehicles = netexResourceFrame.getVehicles().getVehicle();
            logger.info("Publication delivery contains {} vehicles for import.", originalVehicles.size());

            logger.info("About to check if incoming vehicles have previously been imported with the same id");
            var originalWithMappedIds = originalVehicles.stream()
                    .map(vehicleIdConverter::convertIncomingId)
                    .toList();

            logger.info("About to map {} vehicles to internal model", netexResourceFrame.getVehicles().getVehicle().size());
            List<org.rutebanken.tiamat.model.vehicle.Vehicle> mappedVehicles = netexMapper.getFacade()
                    .mapAsList(originalWithMappedIds,
                            org.rutebanken.tiamat.model.vehicle.Vehicle.class);
            logger.info("Mapped {} vehicles to internal model", mappedVehicles.size());
            List<Vehicle> importedVehicles = vehicleImporter.importVehicles(mappedVehicles, vehiclesCounter);
            responseResourceFrame.withVehicles(new VehiclesInFrame_RelStructure().withVehicle(importedVehicles));
            logger.info("Finished importing vehicles");
        }
    }
}
