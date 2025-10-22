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
import org.rutebanken.netex.model.DeckEntrances_RelStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.tiamat.model.vehicle.PassengerEntrance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PassengerEntranceListConverter extends BidirectionalConverter<List<PassengerEntrance>, DeckEntrances_RelStructure> {

    final ObjectFactory objectFactory = new ObjectFactory();


    @Override
    public DeckEntrances_RelStructure convertTo(List<PassengerEntrance> entrances, Type<DeckEntrances_RelStructure> type, MappingContext mappingContext) {

        if(entrances == null || entrances.isEmpty()) {
            return null;
        }

        return new DeckEntrances_RelStructure()
                .withDeckEntranceRefOrDeckEntrance_Dummy(entrances.stream()
                        .map(ds -> mapperFacade.map(ds, org.rutebanken.netex.model.PassengerEntrance.class))
                        .map(objectFactory::createPassengerEntrance)
                        .collect(Collectors.toList()));
    }

    @Override
    public List<PassengerEntrance> convertFrom(DeckEntrances_RelStructure deckEntrancesRelStructure, Type<List<PassengerEntrance>> type, MappingContext mappingContext) {
        return null;
    }
}

