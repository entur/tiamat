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

package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The netex mapper should not map topographic place references to topographic places.
 * Because it should not know about repositories.
 * Thats why only the stop place's topographic place id and version is populated.
 * Update the reference with a proper topographic reference fetched from the repository,
 */
@Component
public class StopPlaceTopographicPlaceReferenceUpdater {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceTopographicPlaceReferenceUpdater.class);

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;


    public StopPlace updateTopographicReference(StopPlace stopPlace) {
        if (stopPlace.getTopographicPlace() != null) {
            String netexId = stopPlace.getTopographicPlace().getNetexId();
            Long version = stopPlace.getTopographicPlace().getVersion();
            stopPlace.setTopographicPlace(topographicPlaceRepository.findFirstByNetexIdAndVersion(netexId, version));
            logger.trace("Resolved topographic place from {}:{} for stop place {}:{}", netexId, version, stopPlace.getNetexId(), stopPlace.getVersion());
        }
        return stopPlace;
    }
}
