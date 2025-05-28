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

package org.rutebanken.tiamat.netex.mapping.mapper;

import jakarta.xml.bind.JAXBElement;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.CycleStorageEquipment;
import org.rutebanken.netex.model.GeneralSign;
import org.rutebanken.netex.model.InstalledEquipment_VersionStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PlaceEquipments_RelStructure;
import org.rutebanken.netex.model.SanitaryEquipment;
import org.rutebanken.netex.model.ShelterEquipment;
import org.rutebanken.netex.model.TicketingEquipment;
import org.rutebanken.netex.model.WaitingRoomEquipment;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class PlaceEquipmentMapper extends CustomMapper<PlaceEquipments_RelStructure, PlaceEquipment> {

    final ObjectFactory objectFactory = new ObjectFactory();


    @Override
    public void mapAtoB(PlaceEquipments_RelStructure placeEquipments_relStructure, org.rutebanken.tiamat.model.PlaceEquipment placeEquipment, MappingContext context) {

        super.mapAtoB(placeEquipments_relStructure, placeEquipment, context);

        List<org.rutebanken.netex.model.InstalledEquipment_VersionStructure> netexInstalledEquipmentList = placeEquipments_relStructure
                .getInstalledEquipmentRefOrInstalledEquipment()
                .stream()
                .filter(jaxbElement -> {
                    Object equipment = jaxbElement.getValue();
                    return (equipment instanceof SanitaryEquipment |
                            equipment instanceof TicketingEquipment |
                            equipment instanceof WaitingRoomEquipment |
                            equipment instanceof CycleStorageEquipment |
                            equipment instanceof ShelterEquipment |
                            equipment instanceof GeneralSign);
                })
                .map(jaxbElement -> (InstalledEquipment_VersionStructure)jaxbElement.getValue())
                .toList();
        List<org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure> installedEquipment_versionStructures = mapperFacade.mapAsList(netexInstalledEquipmentList, org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure.class, context);

        if (!installedEquipment_versionStructures.isEmpty()) {
            placeEquipment.getInstalledEquipment().addAll(installedEquipment_versionStructures);
        }
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.PlaceEquipment placeEquipment, PlaceEquipments_RelStructure placeEquipments_relStructure, MappingContext context) {
        mapperFacade.map(placeEquipment, placeEquipments_relStructure, context);

        List<org.rutebanken.netex.model.InstalledEquipment_VersionStructure> installedEquipment_versionStructures = mapperFacade.mapAsList(placeEquipment.getInstalledEquipment(), org.rutebanken.netex.model.InstalledEquipment_VersionStructure.class, context);

        List<JAXBElement<? extends org.rutebanken.netex.model.InstalledEquipment_VersionStructure>> jaxbElements = installedEquipment_versionStructures
                .stream()
                .filter(equipment -> (equipment instanceof SanitaryEquipment |
                        equipment instanceof TicketingEquipment |
                        equipment instanceof WaitingRoomEquipment |
                        equipment instanceof CycleStorageEquipment |
                        equipment instanceof ShelterEquipment |
                        equipment instanceof GeneralSign))
                .map(equipment -> {
                    return switch (equipment) {
                        case SanitaryEquipment sanitaryEquipment -> objectFactory.createSanitaryEquipment(sanitaryEquipment);
                        case TicketingEquipment ticketingEquipment -> objectFactory.createTicketingEquipment(ticketingEquipment);
                        case WaitingRoomEquipment waitingRoomEquipment -> objectFactory.createWaitingRoomEquipment(waitingRoomEquipment);
                        case CycleStorageEquipment cycleStorageEquipment -> objectFactory.createCycleStorageEquipment(cycleStorageEquipment);
                        case ShelterEquipment shelterEquipment -> objectFactory.createShelterEquipment(shelterEquipment);
                        case GeneralSign generalSign -> objectFactory.createGeneralSign(generalSign);
                        default -> null;
                    };
                })
                .toList();


        if (!jaxbElements.isEmpty()) {
            placeEquipments_relStructure.getInstalledEquipmentRefOrInstalledEquipment().addAll(jaxbElements);
        }


    }
}
