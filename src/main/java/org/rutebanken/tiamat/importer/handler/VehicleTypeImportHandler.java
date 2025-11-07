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
import org.rutebanken.tiamat.importer.VehicleTypeImporter;
import org.rutebanken.tiamat.importer.converter.VehicleTypeIdConverter;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class VehicleTypeImportHandler {

    private static final Logger logger = LoggerFactory.getLogger(VehicleTypeImportHandler.class);

    private final PublicationDeliveryHelper publicationDeliveryHelper;

    private final NetexMapper netexMapper;

    private final VehicleTypeImporter vehicleTypeImporter;

    private final VehicleTypeIdConverter vehicleTypeIdConverter;

    public VehicleTypeImportHandler(PublicationDeliveryHelper publicationDeliveryHelper,
                                    NetexMapper netexMapper,
                                    VehicleTypeImporter vehicleTypeImporter, VehicleTypeIdConverter vehicleTypeIdConverter) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.netexMapper = netexMapper;
        this.vehicleTypeImporter = vehicleTypeImporter;
        this.vehicleTypeIdConverter = vehicleTypeIdConverter;
    }

    public void handleVehicleTypes(ResourceFrame netexResourceFrame, ImportParams importParams, AtomicInteger vehicleTypesCounter, ResourceFrame responseResourceframe) {

        if (publicationDeliveryHelper.hasVehicleTypes(netexResourceFrame)) {
            var originalVehicleTypes = netexResourceFrame.getVehicleTypes().getTransportType_DummyType();
            logger.info("Publication delivery contains {} vehicle types for import.", originalVehicleTypes.size());

            logger.info("About to check if incoming vehicle types have previously been imported with the same id");
            var originalWithMappedIds = originalVehicleTypes.stream()
                    .filter(this::isVehicleType)
                    .map(jaxbElement -> (VehicleType) jaxbElement.getValue())
                    .map(vehicleTypeIdConverter::convertIncomingId);

            logger.info("About to map {} vehicle types to internal model", originalVehicleTypes.size());
            List<org.rutebanken.tiamat.model.vehicle.VehicleType> mappedVehicleTypes = originalWithMappedIds
                    .map(netexMapper::mapToTiamatModel)
                    .collect(Collectors.toList());
            logger.info("Mapped {} vehicle types to internal model", mappedVehicleTypes.size());

            List<VehicleType> importedVehicleTypes = vehicleTypeImporter.importVehicleTypes(mappedVehicleTypes, vehicleTypesCounter);

            List<JAXBElement<? extends DataManagedObjectStructure>> vehicleTypeElements = importedVehicleTypes.stream()
                    .map(vt -> new ObjectFactory().createVehicleType(vt))
                    .collect(java.util.stream.Collectors.toList());

            responseResourceframe.withVehicleTypes(
                    new VehicleTypesInFrame_RelStructure()
                            .withTransportType_DummyType(vehicleTypeElements));

            logger.info("Finished importing vehicle types");
        }
    }

    private boolean isVehicleType(JAXBElement<? extends DataManagedObjectStructure> jaxbElement) {
        return jaxbElement.getValue() instanceof VehicleType;
    }
}
