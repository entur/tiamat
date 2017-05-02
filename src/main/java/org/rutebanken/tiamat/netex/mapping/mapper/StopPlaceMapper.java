package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.AlternativeName;
import org.rutebanken.netex.model.AlternativeNames_RelStructure;
import org.rutebanken.netex.model.StopPlace;

import java.util.ArrayList;
import java.util.List;

public class StopPlaceMapper extends CustomMapper<StopPlace, org.rutebanken.tiamat.model.StopPlace> {

    @Override
    public void mapAtoB(StopPlace stopPlace, org.rutebanken.tiamat.model.StopPlace stopPlace2, MappingContext context) {
        super.mapAtoB(stopPlace, stopPlace2, context);
        if (stopPlace.getPlaceEquipments() != null &&
                stopPlace.getPlaceEquipments().getInstalledEquipmentRefOrInstalledEquipment() != null &&
                stopPlace.getPlaceEquipments().getInstalledEquipmentRefOrInstalledEquipment().isEmpty()) {
            stopPlace.setPlaceEquipments(null);
            stopPlace2.setPlaceEquipments(null);
        }
        if (stopPlace.getAlternativeNames() != null &&
                stopPlace.getAlternativeNames().getAlternativeName() != null &&
                !stopPlace.getAlternativeNames().getAlternativeName().isEmpty()) {
            List<AlternativeName> netexAlternativeName = stopPlace.getAlternativeNames().getAlternativeName();
            List<org.rutebanken.tiamat.model.AlternativeName> alternativeNames = new ArrayList<>();

            for (AlternativeName netexAltName : netexAlternativeName) {
                org.rutebanken.tiamat.model.AlternativeName tiamatAltName = new org.rutebanken.tiamat.model.AlternativeName();
                mapperFacade.map(netexAltName, tiamatAltName);
                alternativeNames.add(tiamatAltName);
            }

            stopPlace2.getAlternativeNames().addAll(alternativeNames);
        }
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.StopPlace stopPlace, StopPlace stopPlace2, MappingContext context) {
        super.mapBtoA(stopPlace, stopPlace2, context);
        if (stopPlace.getPlaceEquipments() != null &&
                stopPlace.getPlaceEquipments().getInstalledEquipment() != null &&
                stopPlace.getPlaceEquipments().getInstalledEquipment().isEmpty()) {
            stopPlace.setPlaceEquipments(null);
            stopPlace2.setPlaceEquipments(null);
        }

        if (stopPlace.getAlternativeNames() != null &&
                !stopPlace.getAlternativeNames().isEmpty()) {
            List<org.rutebanken.tiamat.model.AlternativeName> alternativeNames = stopPlace.getAlternativeNames();
            List<AlternativeName> netexAlternativeNames = new ArrayList<>();

            for (org.rutebanken.tiamat.model.AlternativeName alternativeName : alternativeNames) {
                AlternativeName netexAltName = new AlternativeName();
                mapperFacade.map(alternativeName, netexAltName);
                netexAltName.setId(alternativeName.getNetexId());
                netexAlternativeNames.add(netexAltName);
            }

            AlternativeNames_RelStructure altName = new AlternativeNames_RelStructure();
            altName.getAlternativeName().addAll(netexAlternativeNames);
            stopPlace2.setAlternativeNames(altName);
        } else {
            stopPlace2.setAlternativeNames(null);
        }
    }
}
