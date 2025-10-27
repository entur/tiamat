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

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.DeckSpaces_RelStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.tiamat.model.vehicle.PassengerSpace;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PassengerSpaceListConverter extends BidirectionalConverter<List<PassengerSpace>, DeckSpaces_RelStructure> {

    final ObjectFactory objectFactory = new ObjectFactory();


    @Override
    public DeckSpaces_RelStructure convertTo(List<PassengerSpace> deckSpaces, Type<DeckSpaces_RelStructure> type, MappingContext mappingContext) {

        if(deckSpaces == null || deckSpaces.isEmpty()) {
            return null;
        }

        return new DeckSpaces_RelStructure()
                .withDeckSpaceRefOrDeckSpace_Dummy(deckSpaces.stream()
                        .map(ds -> mapperFacade.map(ds, org.rutebanken.netex.model.PassengerSpace.class))
                        .map(objectFactory::createPassengerSpace)
                        .collect(Collectors.toList()));
    }

    @Override
    public List<PassengerSpace> convertFrom(DeckSpaces_RelStructure passengerSpacesRelStructure, Type<List<PassengerSpace>> type, MappingContext mappingContext) {
        return null;
    }
}

