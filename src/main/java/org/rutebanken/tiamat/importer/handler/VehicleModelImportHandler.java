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

import jakarta.xml.bind.JAXBElement;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.VehicleModelImporter;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class VehicleModelImportHandler {

    private static final Logger logger = LoggerFactory.getLogger(VehicleModelImportHandler.class);

    private final PublicationDeliveryHelper publicationDeliveryHelper;

    private final NetexMapper netexMapper;

    private final VehicleModelImporter vehicleModelImporter;

    public VehicleModelImportHandler(PublicationDeliveryHelper publicationDeliveryHelper,
                                     NetexMapper netexMapper,
                                     VehicleModelImporter vehicleModelImporter) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.netexMapper = netexMapper;
        this.vehicleModelImporter = vehicleModelImporter;
    }

    public void handleVehicleModels(ResourceFrame netexResourceFrame, ImportParams importParams, AtomicInteger vehicleModelsCounter, ResourceFrame responseResourceframe) {

        if (publicationDeliveryHelper.hasVehicleModels(netexResourceFrame)) {
            logger.info("Publication delivery contains {} vehicle models for import.", netexResourceFrame.getVehicleModels().getVehicleModel().size());

            logger.info("About to map {} vehicle models to internal model", netexResourceFrame.getVehicleModels().getVehicleModel().size());
            List<org.rutebanken.tiamat.model.vehicle.VehicleModel> mappedVehicleModels = netexMapper.getFacade()
                    .mapAsList(netexResourceFrame.getVehicleModels().getVehicleModel(),
                            org.rutebanken.tiamat.model.vehicle.VehicleModel.class);
            logger.info("Mapped {} vehicle models to internal model", mappedVehicleModels.size());
            List<VehicleModel> importedVehicleModels = vehicleModelImporter.importVehicleModels(mappedVehicleModels, vehicleModelsCounter);

            responseResourceframe.withVehicleModels(
                    new VehicleModelsInFrame_RelStructure()
                            .withVehicleModel(importedVehicleModels));

            logger.info("Finished importing vehicle models");
        }
    }
}
