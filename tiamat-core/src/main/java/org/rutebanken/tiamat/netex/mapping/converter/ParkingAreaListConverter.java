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
import org.rutebanken.netex.model.ParkingAreas_RelStructure;
import org.rutebanken.tiamat.model.ParkingArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ParkingAreaListConverter extends BidirectionalConverter<List<ParkingArea>, ParkingAreas_RelStructure> {

    private static final Logger logger = LoggerFactory.getLogger(ParkingAreaListConverter.class);

    @Override
    public ParkingAreas_RelStructure convertTo(List<ParkingArea> parkingAreas, Type<ParkingAreas_RelStructure> destinationType, MappingContext mappingContext) {
        if(parkingAreas == null || parkingAreas.isEmpty()) {
            return null;
        }

        ParkingAreas_RelStructure parkingAreasRelStructure = new ParkingAreas_RelStructure();

        logger.debug("Mapping {} parkingAreas to netex", parkingAreas.size());

        parkingAreas.forEach(parkingArea -> {
            org.rutebanken.netex.model.ParkingArea netexParkingArea = mapperFacade.map(parkingArea, org.rutebanken.netex.model.ParkingArea.class);
            parkingAreasRelStructure.withParkingAreaRefOrParkingArea_(List.of(new ObjectFactory().createParkingArea_(netexParkingArea)));
        });
        return parkingAreasRelStructure;
    }

    @Override
    public List<ParkingArea> convertFrom(ParkingAreas_RelStructure parkingAreasRelStructure, Type<List<ParkingArea>> destinationType, MappingContext mappingContext) {
        logger.debug("Mapping {} quays to internal model", parkingAreasRelStructure != null ? parkingAreasRelStructure.getParkingAreaRefOrParkingArea_().size() : 0);
        List<ParkingArea> parkingAreas = new ArrayList<>();
        if(parkingAreasRelStructure != null && parkingAreasRelStructure.getParkingAreaRefOrParkingArea_() != null) {
            parkingAreasRelStructure.getParkingAreaRefOrParkingArea_().stream()
                    .map(JAXBElement::getValue)
                    .filter(object -> object instanceof org.rutebanken.netex.model.ParkingArea)
                    .map(object -> ((org.rutebanken.netex.model.ParkingArea) object))
                    .map(netexParkingArea ->  mapperFacade.map(netexParkingArea, ParkingArea.class))
                    .forEach(parkingAreas::add);
        }
        return parkingAreas;
    }
}
