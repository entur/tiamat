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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;

@Component
public class StopPlaceByQuayOriginalIdFinder {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceByQuayOriginalIdFinder.class);

    private Cache<String, Optional<String>> originalQuayIdCache = CacheBuilder.newBuilder()
            .maximumSize(300000)
            .build();


    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private NetexIdHelper netexIdHelper;

    public List<StopPlace> find(StopPlace incomingStopPlace, boolean hasQuays) {
        if (hasQuays) {
            return incomingStopPlace.getQuays().stream()
                    .flatMap(quay -> quay.getOriginalIds().stream())
                    .map(this::extractNumericValueIfPossible)
                    .peek(quayOriginalId -> logger.trace("looking for stop place by quay original id: {}", quayOriginalId))
                    .map(this::find)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .peek(stopPlaceNetexId -> logger.debug("Found stop place {}", stopPlaceNetexId))
                    .map(stopPlaceRepository::findFirstByNetexIdOrderByVersionDesc)
                    .filter(stopPlace -> stopPlace != null)
                    .collect(toList());
        }
        return new ArrayList<>();
    }

    private String extractNumericValueIfPossible(String quayOriginalId) {
        try {
            // Extract last part of ID. Remove zero padding. Fall back to string ID.
            return String.valueOf(netexIdHelper.extractIdPostfixNumeric(quayOriginalId));
        } catch (NumberFormatException e) {
            return quayOriginalId;
        }
    }

    public void updateCache(String stopPlaceNetexId, List<String> quayOriginalIds) {
        if(quayOriginalIds != null) {
            quayOriginalIds.forEach(quayOriginalId -> originalQuayIdCache.put(extractNumericValueIfPossible(quayOriginalId), Optional.of(stopPlaceNetexId)));
        }
    }

    private Optional<String> find(String quayOriginalId) {
        try {
            return originalQuayIdCache.get(quayOriginalId, () -> {
                logger.debug("Cache miss. Fetching stop place repository to find stop place from {}", quayOriginalId);
                List<String> stopPlaceNetexIds = stopPlaceRepository.findStopPlaceFromQuayOriginalId(quayOriginalId, Instant.now());
                if(stopPlaceNetexIds == null || stopPlaceNetexIds.isEmpty()) {
                    return Optional.empty();
                }

                if(stopPlaceNetexIds.size() > 1) {
                    logger.warn("Found more than one stop place from quay imported ID: {} - {}", quayOriginalId, stopPlaceNetexIds);
                }
                return Optional.of(stopPlaceNetexIds.getFirst());
            });
        } catch (ExecutionException e) {
            logger.warn("Caught exception when looking for stop place from quay imported ID {}", quayOriginalId);
            return Optional.empty();
        }
    }
}
