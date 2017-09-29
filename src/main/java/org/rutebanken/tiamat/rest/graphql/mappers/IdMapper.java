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

package org.rutebanken.tiamat.rest.graphql.mappers;

import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class IdMapper {

    private static final Logger logger = LoggerFactory.getLogger(IdMapper.class);

    public Optional<String> extractIdIfPresent(String field, Map input) {
        if(input.get(field) != null) {
            String netexId = (String) input.get(field);
            logger.debug("Detected ID {}", netexId);
            if(netexId.isEmpty()) {
                logger.debug("The ID provided is empty '{}'", netexId);
                return Optional.empty();
            }
            return Optional.of(netexId);

        }
        return Optional.empty();
    }

    public void extractAndSetNetexId(String field, Map input, IdentifiedEntity identifiedEntity) {

        extractIdIfPresent(field, input)
                .ifPresent(netexId -> identifiedEntity.setNetexId(netexId));
    }
}
