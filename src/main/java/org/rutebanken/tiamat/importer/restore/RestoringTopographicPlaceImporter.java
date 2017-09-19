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


import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Import topographic place without taking existing data into account.
 * Suitable for clean databases and restoring data from tiamat export.
 */
@Component
public class RestoringTopographicPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(RestoringTopographicPlaceImporter.class);

    private final TopographicPlaceRepository topographicPlaceRepository;

    private final NetexMapper netexMapper;

    @Autowired
    public RestoringTopographicPlaceImporter(TopographicPlaceRepository topographicPlaceRepository, NetexMapper netexMapper) {
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.netexMapper = netexMapper;
    }

    public void importTopographicPlaces(AtomicInteger topographicPlacesImported, List<org.rutebanken.netex.model.TopographicPlace> topographicPlaces) {

        if (topographicPlaces.isEmpty()) {
            return;
        }

        topographicPlaces.parallelStream()
                .map(netexTopographicPlace -> netexMapper.mapToTiamatModel(netexTopographicPlace))
                .forEach(topographicPlace -> {
                    logger.debug("Saving topographic place {}, version {}, netex ID: {}", topographicPlace.getName(), topographicPlace.getVersion(), topographicPlace.getNetexId());
                    topographicPlaceRepository.save(topographicPlace);
                    topographicPlacesImported.incrementAndGet();
                });
    }

}
