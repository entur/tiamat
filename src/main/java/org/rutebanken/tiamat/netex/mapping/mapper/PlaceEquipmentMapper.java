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

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlaceEquipmentMapper extends CustomMapper<PlaceEquipments_RelStructure, PlaceEquipment> {

    final ObjectFactory objectFactory = new ObjectFactory();


    @Override
    public void mapAtoB(PlaceEquipments_RelStructure placeEquipmentsRelStructure, org.rutebanken.tiamat.model.PlaceEquipment placeEquipment, MappingContext context) {

        super.mapAtoB(placeEquipmentsRelStructure, placeEquipment, context);

        List<org.rutebanken.netex.model.InstalledEquipment_VersionStructure> netexInstalledEquipmentList = placeEquipmentsRelStructure
                .getInstalledEquipmentRefOrInstalledEquipment()
                .stream()
                .filter(jaxbElement -> {
                    Object equipment = jaxbElement.getValue();
                    return (equipment instanceof SanitaryEquipment ||
                            equipment instanceof TicketingEquipment ||
                            equipment instanceof WaitingRoomEquipment ||
                            equipment instanceof CycleStorageEquipment ||
                            equipment instanceof ShelterEquipment ||
                            equipment instanceof GeneralSign);
                })
                .map(jaxbElement -> (InstalledEquipment_VersionStructure)jaxbElement.getValue())
                .collect(Collectors.toList());
        List<org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure> installedEquipmentVersionStructures = mapperFacade.mapAsList(netexInstalledEquipmentList, org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure.class, context);

        if (!installedEquipmentVersionStructures.isEmpty()) {
            placeEquipment.getInstalledEquipment().addAll(installedEquipmentVersionStructures);
        }
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.PlaceEquipment placeEquipment, PlaceEquipments_RelStructure placeEquipmentsRelStructure, MappingContext context) {
        mapperFacade.map(placeEquipment, placeEquipmentsRelStructure, context);

        List<org.rutebanken.netex.model.InstalledEquipment_VersionStructure> installedEquipmentVersionStructures = mapperFacade.mapAsList(placeEquipment.getInstalledEquipment(), org.rutebanken.netex.model.InstalledEquipment_VersionStructure.class, context);

        List<JAXBElement<? extends org.rutebanken.netex.model.InstalledEquipment_VersionStructure>> jaxbElements = installedEquipmentVersionStructures
                .stream()
                .filter(equipment -> (equipment instanceof SanitaryEquipment ||
                        equipment instanceof TicketingEquipment ||
                        equipment instanceof WaitingRoomEquipment ||
                        equipment instanceof CycleStorageEquipment ||
                        equipment instanceof ShelterEquipment ||
                        equipment instanceof GeneralSign))
                .map(equipment -> {
                    if (equipment instanceof SanitaryEquipment) {
                        return objectFactory.createSanitaryEquipment((SanitaryEquipment) equipment);
                    } else if (equipment instanceof TicketingEquipment) {
                        return objectFactory.createTicketingEquipment((TicketingEquipment) equipment);
                    } else if (equipment instanceof WaitingRoomEquipment) {
                        return objectFactory.createWaitingRoomEquipment((WaitingRoomEquipment) equipment);
                    } else if (equipment instanceof CycleStorageEquipment) {
                        return objectFactory.createCycleStorageEquipment((CycleStorageEquipment) equipment);
                    } else if (equipment instanceof ShelterEquipment) {
                        return objectFactory.createShelterEquipment((ShelterEquipment) equipment);
                    } else if (equipment instanceof GeneralSign) {
                        return objectFactory.createGeneralSign((GeneralSign) equipment);
                    }
                    return null;
                })
                .collect(Collectors.toList());


        if (!jaxbElements.isEmpty()) {
            placeEquipmentsRelStructure.getInstalledEquipmentRefOrInstalledEquipment().addAll(jaxbElements);
        }


    }
}
