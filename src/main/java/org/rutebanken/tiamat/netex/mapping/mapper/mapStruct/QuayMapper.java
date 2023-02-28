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

package org.rutebanken.tiamat.netex.mapping.mapper.mapStruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.rutebanken.netex.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Mapper(uses = {
        AlternativeNamesConverter.class,
        LocalDateTimeInstantConverter.class,
        EquipmentPlacesConverter.class})
public abstract class QuayMapper {

    public org.rutebanken.tiamat.model.Quay netexQuayToTiamatQuay(Quay netexQuay) {

        org.rutebanken.tiamat.model.Quay tiamatQuay = new org.rutebanken.tiamat.model.Quay();
        if (netexQuay.getPlaceEquipments() != null &&
                netexQuay.getPlaceEquipments().getInstalledEquipmentRefOrInstalledEquipment() != null &&
                netexQuay.getPlaceEquipments().getInstalledEquipmentRefOrInstalledEquipment().isEmpty()) {
            netexQuay.setPlaceEquipments(null);
            tiamatQuay.setPlaceEquipments(null);
        }

        if (netexQuay.getAlternativeNames() != null &&
                netexQuay.getAlternativeNames().getAlternativeName() != null &&
                !netexQuay.getAlternativeNames().getAlternativeName().isEmpty()) {
            List<AlternativeName> netexAlternativeName = netexQuay.getAlternativeNames().getAlternativeName();
            List<AlternativeName> alternativeNames = new ArrayList<>();

            for (org.rutebanken.netex.model.AlternativeName netexAltName : netexAlternativeName) {
                if (netexAltName != null
                        && netexAltName.getName() != null
                        && netexAltName.getName().getValue() != null
                        && !netexAltName.getName().getValue().isEmpty()) {
                    //Only include non-empty alternative names
                    AlternativeName tiamatAltName = new AlternativeName();
                    mapperFacade.map(netexAltName, tiamatAltName);
                    alternativeNames.add(tiamatAltName);
                }
            }

            if (!alternativeNames.isEmpty()) {
                tiamatQuay.getAlternativeNames().addAll(alternativeNames);
            }
        }


        if (netexQuay.getBoardingPositions() != null
                && netexQuay.getBoardingPositions().getBoardingPositionRefOrBoardingPosition() != null
                && !netexQuay.getBoardingPositions().getBoardingPositionRefOrBoardingPosition().isEmpty()) {
            final List<Object> netexBoardingPositions = netexQuay.getBoardingPositions().getBoardingPositionRefOrBoardingPosition();
            List<BoardingPosition> tiamatBoardingPositions = new ArrayList<>();
            for (Object netexBoardingPosition : netexBoardingPositions) {
                if (netexBoardingPosition instanceof org.rutebanken.netex.model.BoardingPosition) {
                    final org.rutebanken.netex.model.BoardingPosition netexBoardingPosition1 = (org.rutebanken.netex.model.BoardingPosition) netexBoardingPosition;
                    if (netexBoardingPosition1.getPublicCode() != null
                            && !netexBoardingPosition1.getPublicCode().isEmpty()) {
                        final BoardingPosition tiamatBoardingPosition = new BoardingPosition();
                        mapperFacade.map(netexBoardingPosition1, tiamatBoardingPosition);
                        tiamatBoardingPositions.add(tiamatBoardingPosition);
                    }
                }
            }

            if (!tiamatBoardingPositions.isEmpty()) {
                tiamatQuay.getBoardingPositions().addAll(tiamatBoardingPositions);
            }
        }

        return tiamatQuay;
    }

    @Mapping(target = "localServices", ignore = true)
    @Mapping(target = "postalAddress", ignore = true)
    @Mapping(target = "roadAddress", ignore = true)
    public Quay tiamatQuayToNetexQuay(org.rutebanken.tiamat.model.Quay quay) {

        Quay tiamatQuay = new Quay();
        if (quay.getPlaceEquipments() != null &&
                quay.getPlaceEquipments().getInstalledEquipment() != null &&
                quay.getPlaceEquipments().getInstalledEquipment().isEmpty()) {
            quay.setPlaceEquipments(null);
            tiamatQuay.setPlaceEquipments(null);
        }

        if (quay.getAlternativeNames() != null &&
                !quay.getAlternativeNames().isEmpty()) {
            List<AlternativeName> alternativeNames = quay.getAlternativeNames();
            List<org.rutebanken.netex.model.AlternativeName> netexAlternativeNames = new ArrayList<>();

            for (AlternativeName alternativeName : alternativeNames) {
                if (alternativeName != null
                        && alternativeName.getName() != null
                        && alternativeName.getName().getValue() != null
                        && !alternativeName.getName().getValue().isEmpty()) {
                    //Only include non-empty alternative names
                    org.rutebanken.netex.model.AlternativeName netexAltName = new org.rutebanken.netex.model.AlternativeName();
                    mapperFacade.map(alternativeName, netexAltName);
                    netexAltName.setId(alternativeName.getNetexId());
                    netexAlternativeNames.add(netexAltName);
                }
            }

            if (!netexAlternativeNames.isEmpty()) {
                AlternativeNames_RelStructure altName = new AlternativeNames_RelStructure();
                altName.getAlternativeName().addAll(netexAlternativeNames);
                tiamatQuay.setAlternativeNames(altName);
            }
        } else {
            tiamatQuay.setAlternativeNames(null);
        }

        if (quay.getBoardingPositions() != null && !quay.getBoardingPositions().isEmpty()) {
            final List<BoardingPosition> boardingPositions = quay.getBoardingPositions();
            List<org.rutebanken.netex.model.BoardingPosition> netexBoardingPositions = new ArrayList<>();
            for (BoardingPosition boardingPosition : boardingPositions) {
                if (boardingPosition != null
                        && boardingPosition.getPublicCode() != null
                        && !boardingPosition.getPublicCode().isEmpty()) {
                    // Only Include non-empty boarding-positions
                    final org.rutebanken.netex.model.BoardingPosition netexBoardingPosition = new org.rutebanken.netex.model.BoardingPosition();
                    mapperFacade.map(boardingPosition, netexBoardingPosition);
                    netexBoardingPosition.setId(boardingPosition.getNetexId());
                    netexBoardingPosition.setPublicCode(boardingPosition.getPublicCode());

                    if (boardingPosition.getCentroid() != null) {
                        SimplePoint_VersionStructure simplePoint = new SimplePoint_VersionStructure()
                                .withLocation(new LocationStructure()
                                        .withLatitude(BigDecimal.valueOf(boardingPosition.getCentroid().getY()))
                                        .withLongitude(BigDecimal.valueOf(boardingPosition.getCentroid().getX())));
                        netexBoardingPosition.setCentroid(simplePoint);
                    }


                    netexBoardingPositions.add(netexBoardingPosition);

                }
            }
            if (!netexBoardingPositions.isEmpty()) {
                final BoardingPositions_RelStructure boardingPositionsRelStructure = new BoardingPositions_RelStructure();
                boardingPositionsRelStructure.getBoardingPositionRefOrBoardingPosition().addAll(netexBoardingPositions);
                tiamatQuay.setBoardingPositions(boardingPositionsRelStructure);
            }
        }

        return tiamatQuay;
    }
}
