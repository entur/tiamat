package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.stream.Collectors;

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
                .collect(Collectors.toList());
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
            placeEquipments_relStructure.getInstalledEquipmentRefOrInstalledEquipment().addAll(jaxbElements);
        }


    }
}
