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
import org.rutebanken.netex.model.Decks_RelStructure;
import org.rutebanken.tiamat.model.vehicle.Deck;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeckListConverter extends BidirectionalConverter<List<Deck>, Decks_RelStructure> {
    @Override
    public Decks_RelStructure convertTo(List<Deck> decks, Type<Decks_RelStructure> type, MappingContext mappingContext) {

        if(decks == null || decks.isEmpty()) {
            return null;
        }

        return new Decks_RelStructure()
                .withDeck(decks.stream()
                        .map(d -> mapperFacade.map(d, org.rutebanken.netex.model.Deck.class))
                        .collect(Collectors.toList()));
    }

    @Override
    public List<Deck> convertFrom(Decks_RelStructure decksRelStructure, Type<List<Deck>> type, MappingContext mappingContext) {
        return null;
    }
}

