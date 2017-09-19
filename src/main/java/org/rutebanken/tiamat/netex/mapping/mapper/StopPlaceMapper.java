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
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class StopPlaceMapper extends CustomMapper<StopPlace, org.rutebanken.tiamat.model.StopPlace> {

    public static final String IS_PARENT_STOP_PLACE = "IS_PARENT_STOP_PLACE";

    private final PublicationDeliveryHelper publicationDeliveryHelper;

    @Autowired
    public StopPlaceMapper(PublicationDeliveryHelper publicationDeliveryHelper) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
    }

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
                if (netexAltName != null
                        && netexAltName.getName() != null
                        && netexAltName.getName().getValue() != null
                        && !netexAltName.getName().getValue().isEmpty()) {
                    //Only include non-empty alternative names
                    org.rutebanken.tiamat.model.AlternativeName tiamatAltName = new org.rutebanken.tiamat.model.AlternativeName();
                    mapperFacade.map(netexAltName, tiamatAltName);
                    alternativeNames.add(tiamatAltName);
                }
            }

            if (!alternativeNames.isEmpty()) {
                stopPlace2.getAlternativeNames().addAll(alternativeNames);
            }
        }

        String isParentStopPlaceStringValue = publicationDeliveryHelper.getValueByKey(stopPlace, IS_PARENT_STOP_PLACE);
        if(isParentStopPlaceStringValue != null) {
            if(isParentStopPlaceStringValue.equalsIgnoreCase("true")) {
                stopPlace2.setParentStopPlace(true);
            }
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
                if (alternativeName != null
                        && alternativeName.getName() != null
                        && alternativeName.getName().getValue() != null
                        && !alternativeName.getName().getValue().isEmpty()) {
                    //Only include non-empty alternative names
                    AlternativeName netexAltName = new AlternativeName();
                    mapperFacade.map(alternativeName, netexAltName);
                    netexAltName.setId(alternativeName.getNetexId());
                    netexAlternativeNames.add(netexAltName);
                }
            }

            if (!netexAlternativeNames.isEmpty()) {
                AlternativeNames_RelStructure altName = new AlternativeNames_RelStructure();
                altName.getAlternativeName().addAll(netexAlternativeNames);
                stopPlace2.setAlternativeNames(altName);
            }
        } else {
            stopPlace2.setAlternativeNames(null);
        }

        if(stopPlace2.getKeyList() == null) {
            stopPlace2.withKeyList(new KeyListStructure());
        }
        stopPlace2.getKeyList()
                .withKeyValue(new KeyValueStructure()
                        .withKey(IS_PARENT_STOP_PLACE)
                        .withValue(String.valueOf(stopPlace.isParentStopPlace())));
    }
}
