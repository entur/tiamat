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
import org.rutebanken.netex.model.Levels_RelStructure;
import org.rutebanken.tiamat.model.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LevelsConverter extends BidirectionalConverter<List<Level>, Levels_RelStructure> {
    private static final Logger logger = LoggerFactory.getLogger(LevelsConverter.class);

    @Override
    public Levels_RelStructure convertTo(List<Level> levels, Type<Levels_RelStructure> type, MappingContext mappingContext) {
        if(levels == null || levels.isEmpty()) {
            return null;
        }
        logger.debug("Mapping {} levels into levels_RelStructure", levels.size());

        Levels_RelStructure levels_relStructure = new Levels_RelStructure();

        levels.forEach(level -> {
                    levels_relStructure.getLevelRefOrLevel().add(
                            mapperFacade.map(level, org.rutebanken.netex.model.Level.class)
                    );
                }
        );
        return levels_relStructure;

    }

    @Override
    public List<Level> convertFrom(Levels_RelStructure levels_relStructure, Type<List<Level>> type, MappingContext mappingContext) {
        return null;
    }
}
