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

package org.rutebanken.tiamat.importer.restore;


import org.rutebanken.tiamat.importer.StopPlaceTopographicPlaceReferenceUpdater;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Import stop place without taking existing data into account.
 * Suitable for clean databases and importing data from tiamat export.
 * No versions will be created.
 */
@Component
public class RestoringStopPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(RestoringStopPlaceImporter.class);

    private final StopPlaceRepository stopPlaceRepository;

    private final StopPlaceTopographicPlaceReferenceUpdater stopPlaceTopographicPlaceReferenceUpdater;

    private final NetexMapper netexMapper;

    @Autowired
    public RestoringStopPlaceImporter(StopPlaceRepository stopPlaceRepository, StopPlaceTopographicPlaceReferenceUpdater stopPlaceTopographicPlaceReferenceUpdater, NetexMapper netexMapper) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.stopPlaceTopographicPlaceReferenceUpdater = stopPlaceTopographicPlaceReferenceUpdater;
        this.netexMapper = netexMapper;
    }

    public org.rutebanken.netex.model.StopPlace importStopPlace(AtomicInteger stopPlacesImported, StopPlace stopPlace) {

        stopPlaceTopographicPlaceReferenceUpdater.updateTopographicReference(stopPlace);

        stopPlaceRepository.save(stopPlace);
        logger.debug("Saving stop place {}, version {}, netex ID: {}", stopPlace.getName(), stopPlace.getVersion(), stopPlace.getNetexId());
        stopPlacesImported.incrementAndGet();
        return netexMapper.mapToNetexModel(stopPlace);
    }

}
