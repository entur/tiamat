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
import org.rutebanken.tiamat.model.Value;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * DataLoader for efficiently loading StopPlace key-value pairs to solve N+1 query problems.
 * Batches multiple key-value requests into single database queries.
 */
@Component
public class StopPlaceKeyValuesDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceKeyValuesDataLoader.class);

    private final StopPlaceRepository stopPlaceRepository;

    @Autowired
    public StopPlaceKeyValuesDataLoader(StopPlaceRepository stopPlaceRepository) {
        this.stopPlaceRepository = stopPlaceRepository;
    }

    /**
     * Creates a DataLoader for batching StopPlace key-value requests
     */
    public DataLoader<Long, Map<String, Value>> createDataLoader() {
        return DataLoaderFactory.newDataLoader(stopPlaceIds -> {
            logger.debug("Batch loading key-values for {} StopPlaces", stopPlaceIds.size());
            
            try {
                // Get key-value pairs for the requested stop place IDs
                Map<Long, Map<String, Value>> keyValuesByStopPlaceId = 
                    stopPlaceRepository.findKeyValuesByIds(Set.copyOf(stopPlaceIds));
                
                // Handle null result from repository
                if (keyValuesByStopPlaceId == null) {
                    logger.warn("Repository returned null for findKeyValuesByIds, returning empty maps");
                    keyValuesByStopPlaceId = new HashMap<>();
                }
                
                // Return in the order requested, with empty map for stop places with no key-values
                final Map<Long, Map<String, Value>> finalKeyValuesByStopPlaceId = keyValuesByStopPlaceId;
                List<Map<String, Value>> result = stopPlaceIds.stream()
                    .map(stopPlaceId -> {
                        Map<String, Value> keyValues = finalKeyValuesByStopPlaceId.get(stopPlaceId);
                        if (keyValues != null && !keyValues.isEmpty()) {
                            logger.debug("Found {} key-values for StopPlace ID {}", keyValues.size(), stopPlaceId);
                        }
                        return keyValues != null ? keyValues : new HashMap<String, Value>(); // return empty map if null
                    })
                    .collect(Collectors.toList());
                    
                return CompletableFuture.completedFuture(result);
                    
            } catch (Exception e) {
                logger.error("Error in StopPlaceKeyValuesDataLoader batch function", e);
                // Instead of failing, return empty maps for all requested IDs
                List<Map<String, Value>> emptyResult = stopPlaceIds.stream()
                    .map(id -> new HashMap<String, Value>())
                    .collect(Collectors.toList());
                return CompletableFuture.completedFuture(emptyResult);
            }
        }, DataLoaderOptions.newOptions()
            .setBatchingEnabled(true)
            .setMaxBatchSize(100));
    }
}