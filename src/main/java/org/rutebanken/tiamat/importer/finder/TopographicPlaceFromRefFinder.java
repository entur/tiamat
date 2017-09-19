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

package org.rutebanken.tiamat.importer.finder;

import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.rutebanken.tiamat.model.TopographicPlace;

import java.util.List;
import java.util.Optional;

@Component
public class TopographicPlaceFromRefFinder {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceFromRefFinder.class);

    public Optional<TopographicPlace> findTopographicPlaceFromRef(List<TopographicPlace> incomingTopographicPlaces, TopographicPlaceRefStructure topographicPlaceRef) {
        return incomingTopographicPlaces
                .stream()
                .filter(topographicPlace -> topographicPlace.getNetexId() != null)
                .filter(topographicPlace -> {
                    return topographicPlace.getNetexId().equals(topographicPlaceRef.getRef());
                })
                .peek(topographicPlace -> logger.trace("Looking at topographical place with name {} and id {}", topographicPlace.getName(), topographicPlace.getNetexId()))
                .findFirst();
    }

}
