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

package org.rutebanken.tiamat.netex.mapping.converter;

import jakarta.xml.bind.JAXBElement;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.Site_VersionStructure;
import org.rutebanken.netex.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Converter for StopPlacesInFrame_RelStructure between Tiamat and Netex models.
 * Handles the JAXBElement wrapping required by Netex model's stopPlace_ field.
 */
@Component
public class StopPlacesInFrameRelStructureConverter extends BidirectionalConverter<org.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure, org.rutebanken.netex.model.StopPlacesInFrame_RelStructure> {

    private static final Logger logger = LoggerFactory.getLogger(StopPlacesInFrameRelStructureConverter.class);
    private static final ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public org.rutebanken.netex.model.StopPlacesInFrame_RelStructure convertTo(
            org.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure tiamatStopPlaces,
            Type<org.rutebanken.netex.model.StopPlacesInFrame_RelStructure> type,
            MappingContext mappingContext) {

        if (tiamatStopPlaces == null || tiamatStopPlaces.getStopPlace() == null || tiamatStopPlaces.getStopPlace().isEmpty()) {
            return null;
        }

        org.rutebanken.netex.model.StopPlacesInFrame_RelStructure netexStopPlaces = new org.rutebanken.netex.model.StopPlacesInFrame_RelStructure();

        logger.debug("Mapping {} stop places to netex", tiamatStopPlaces.getStopPlace().size());

        for (org.rutebanken.tiamat.model.StopPlace tiamatStopPlace : tiamatStopPlaces.getStopPlace()) {
            StopPlace netexStopPlace = mapperFacade.map(tiamatStopPlace, StopPlace.class);
            JAXBElement<StopPlace> jaxbElement = objectFactory.createStopPlace(netexStopPlace);
            netexStopPlaces.getStopPlace_().add(jaxbElement);
        }

        return netexStopPlaces;
    }

    @Override
    public org.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure convertFrom(
            org.rutebanken.netex.model.StopPlacesInFrame_RelStructure netexStopPlaces,
            Type<org.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure> type,
            MappingContext mappingContext) {

        logger.debug("Mapping {} stop places to internal model",
                netexStopPlaces != null && netexStopPlaces.getStopPlace_() != null
                        ? netexStopPlaces.getStopPlace_().size() : 0);

        org.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure tiamatStopPlaces = new org.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure();

        if (netexStopPlaces != null && netexStopPlaces.getStopPlace_() != null) {
            List<JAXBElement<? extends Site_VersionStructure>> stopPlaceElements = netexStopPlaces.getStopPlace_();
            for (JAXBElement<? extends Site_VersionStructure> element : stopPlaceElements) {
                Site_VersionStructure value = element.getValue();
                if (value instanceof StopPlace netexStopPlace) {
                    org.rutebanken.tiamat.model.StopPlace tiamatStopPlace =
                            mapperFacade.map(netexStopPlace, org.rutebanken.tiamat.model.StopPlace.class);
                    tiamatStopPlaces.getStopPlace().add(tiamatStopPlace);
                }
            }
        }

        return tiamatStopPlaces;
    }
}