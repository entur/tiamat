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
import org.rutebanken.netex.model.AlternativeNames_RelStructure;
import org.rutebanken.netex.model.BoardingPositions_RelStructure;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.tiamat.model.AlternativeName;
import org.rutebanken.tiamat.model.BoardingPosition;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
                quay2.getAlternativeNames().addAll(alternativeNames);
            }
        }

        if (quay.getBoardingPositions() != null
                && quay.getBoardingPositions().getBoardingPositionRefOrBoardingPosition() != null
                && !quay.getBoardingPositions().getBoardingPositionRefOrBoardingPosition().isEmpty()) {
            final List<Object> netexBoardingPositions = quay.getBoardingPositions().getBoardingPositionRefOrBoardingPosition();
            List<BoardingPosition> tiamatBoardingPositions = new ArrayList<>();
            for (Object netexBoardingPosition : netexBoardingPositions) {
                if (netexBoardingPosition instanceof org.rutebanken.netex.model.BoardingPosition boardingPosition) {
                     if (boardingPosition.getPublicCode() != null
                            && !boardingPosition.getPublicCode().isEmpty()) {
                        final BoardingPosition tiamatBoardingPosition = new BoardingPosition();
                        mapperFacade.map(boardingPosition,tiamatBoardingPosition);
                        tiamatBoardingPositions.add(tiamatBoardingPosition);
                    }
                }
            }

            if (!tiamatBoardingPositions.isEmpty()) {
                quay2.getBoardingPositions().addAll(tiamatBoardingPositions);
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
    }
}
