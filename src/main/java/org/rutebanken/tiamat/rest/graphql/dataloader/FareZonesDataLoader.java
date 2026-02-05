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

package org.rutebanken.tiamat.rest.graphql.dataloader;

import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderOptions;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * DataLoader for efficiently loading FareZones to solve N+1 query problems.
 * Batches multiple fare zone requests into single database queries.
 */
@Component
public class FareZonesDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(FareZonesDataLoader.class);

    private final FareZoneRepository fareZoneRepository;

    @Autowired
    public FareZonesDataLoader(FareZoneRepository fareZoneRepository) {
        this.fareZoneRepository = fareZoneRepository;
    }

    /**
     * Creates a DataLoader for batching FareZone requests by stop place ID
     */
    public DataLoader<Long, List<FareZone>> createDataLoader() {
        return DataLoaderFactory.newDataLoader(stopPlaceIds -> {
            logger.debug("Batch loading fare zones for {} stop places", stopPlaceIds.size());
            
            try {
                // Get fare zones for the requested stop place IDs
                Map<Long, List<FareZone>> fareZonesByStopPlaceId = 
                    fareZoneRepository.findFareZonesByStopPlaceIds(Set.copyOf(stopPlaceIds));
                
                // Return in the order requested, with empty list for stop places with no fare zones
                List<List<FareZone>> result = stopPlaceIds.stream()
                    .map(stopPlaceId -> {
                        List<FareZone> fareZones = fareZonesByStopPlaceId.get(stopPlaceId);
                        if (fareZones != null && !fareZones.isEmpty()) {
                            logger.debug("Found {} fare zones for StopPlace ID {}", fareZones.size(), stopPlaceId);
                        }
                        return fareZones != null ? fareZones : new ArrayList<FareZone>(); // return empty list if null
                    })
                    .collect(Collectors.toList());
                    
                return CompletableFuture.completedFuture(result);
                    
            } catch (Exception e) {
                logger.error("Error in FareZonesDataLoader batch function", e);
                // Instead of failing, return empty lists for all requested IDs
                List<List<FareZone>> emptyResult = stopPlaceIds.stream()
                    .map(id -> new ArrayList<FareZone>())
                    .collect(Collectors.toList());
                return CompletableFuture.completedFuture(emptyResult);
            }
        }, DataLoaderOptions.newOptions()
            .setBatchingEnabled(true)
            .setMaxBatchSize(100));
    }
}