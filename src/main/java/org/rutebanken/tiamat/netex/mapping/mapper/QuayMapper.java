package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.AlternativeNames_RelStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.tiamat.model.AlternativeName;

import java.util.ArrayList;
import java.util.List;

public class QuayMapper extends CustomMapper<Quay, org.rutebanken.tiamat.model.Quay> {
    @Override
    public void mapAtoB(Quay quay, org.rutebanken.tiamat.model.Quay quay2, MappingContext context) {
        super.mapAtoB(quay, quay2, context);
        if (quay.getPlaceEquipments() != null &&
                quay.getPlaceEquipments().getInstalledEquipmentRefOrInstalledEquipment() != null &&
                quay.getPlaceEquipments().getInstalledEquipmentRefOrInstalledEquipment().isEmpty()) {
            quay.setPlaceEquipments(null);
            quay2.setPlaceEquipments(null);
        }

        if (quay.getAlternativeNames() != null &&
                quay.getAlternativeNames().getAlternativeName() != null &&
                !quay.getAlternativeNames().getAlternativeName().isEmpty()) {
            List<org.rutebanken.netex.model.AlternativeName> netexAlternativeName = quay.getAlternativeNames().getAlternativeName();
            List<org.rutebanken.tiamat.model.AlternativeName> alternativeNames = new ArrayList<>();

            for (org.rutebanken.netex.model.AlternativeName netexAltName : netexAlternativeName) {
                org.rutebanken.tiamat.model.AlternativeName tiamatAltName = new org.rutebanken.tiamat.model.AlternativeName();
                mapperFacade.map(netexAltName, tiamatAltName);
                alternativeNames.add(tiamatAltName);
            }

            if (!alternativeNames.isEmpty()) {
                quay2.getAlternativeNames().addAll(alternativeNames);
            }
        }
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.Quay quay, Quay quay2, MappingContext context) {
        super.mapBtoA(quay, quay2, context);
        if (quay.getPlaceEquipments() != null &&
                quay.getPlaceEquipments().getInstalledEquipment() != null &&
                quay.getPlaceEquipments().getInstalledEquipment().isEmpty()) {
            quay.setPlaceEquipments(null);
            quay2.setPlaceEquipments(null);
        }

        if (quay.getAlternativeNames() != null &&
                !quay.getAlternativeNames().isEmpty()) {
            List<AlternativeName> alternativeNames = quay.getAlternativeNames();
            List<org.rutebanken.netex.model.AlternativeName> netexAlternativeNames = new ArrayList<>();

            for (org.rutebanken.tiamat.model.AlternativeName alternativeName : alternativeNames) {
                org.rutebanken.netex.model.AlternativeName netexAltName = new org.rutebanken.netex.model.AlternativeName();
                mapperFacade.map(alternativeName, netexAltName);
                netexAltName.setId(alternativeName.getNetexId());
                netexAlternativeNames.add(netexAltName);
            }

            if (!netexAlternativeNames.isEmpty()) {
                AlternativeNames_RelStructure altName = new AlternativeNames_RelStructure();
                altName.getAlternativeName().addAll(netexAlternativeNames);
                quay2.setAlternativeNames(altName);
            }
        } else {
            quay2.setAlternativeNames(null);
        }
    }
}
