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
import org.rutebanken.netex.model.DeckPlanRefStructure;
import org.rutebanken.tiamat.model.VersionOfObjectRefStructure;
import org.rutebanken.tiamat.model.vehicle.DeckPlan;
import org.rutebanken.tiamat.netex.mapping.NetexMappingException;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DeckPlanRefConverter extends BidirectionalConverter<DeckPlanRefStructure, DeckPlan> {

    private static final Logger logger = LoggerFactory.getLogger(DeckPlanRefConverter.class);

    // TODO: a mapper or converter should ideally not use repositories
    private final ReferenceResolver resolver;

    @Autowired
    public DeckPlanRefConverter(ReferenceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public DeckPlan convertTo(DeckPlanRefStructure deckPlanRefStructure, Type<DeckPlan> type, MappingContext mappingContext) {
        DeckPlan deckPlan = resolver.resolve(new VersionOfObjectRefStructure(deckPlanRefStructure.getRef(), deckPlanRefStructure.getVersion()), DeckPlan.class);
        if(deckPlan != null) {
            return deckPlan;
        }
        throw new NetexMappingException("Cannot find deck plan from ref: " +deckPlanRefStructure.getRef());
    }

    @Override
    public DeckPlanRefStructure convertFrom(DeckPlan deckPlan, Type<DeckPlanRefStructure> type, MappingContext mappingContext) {
        DeckPlanRefStructure deckPlanRefStructure = new DeckPlanRefStructure()
                .withCreated(LocalDateTime.now())
                .withRef(deckPlan.getNetexId())
                .withVersion(String.valueOf(deckPlan.getVersion()));

        logger.debug("Mapped deck plan ref structure: {}", deckPlanRefStructure);

        return deckPlanRefStructure;
    }
}
