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
import org.rutebanken.tiamat.repository.QuayRepository;
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
 * DataLoader for efficiently loading Quay key-value pairs to solve N+1 query problems.
 * Batches multiple key-value requests into single database queries.
 */
@Component
public class QuayKeyValuesDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(QuayKeyValuesDataLoader.class);

    private final QuayRepository quayRepository;

    @Autowired
    public QuayKeyValuesDataLoader(QuayRepository quayRepository) {
        this.quayRepository = quayRepository;
    }

    /**
     * Creates a DataLoader for batching Quay key-value requests
     */
    public DataLoader<Long, Map<String, Value>> createDataLoader() {
        return DataLoaderFactory.newDataLoader(quayIds -> {
            logger.debug("Batch loading key-values for {} Quays", quayIds.size());
            
            try {
                // Get key-value pairs for the requested quay IDs
                Map<Long, Map<String, Value>> keyValuesByQuayId = 
                    quayRepository.findKeyValuesByIds(Set.copyOf(quayIds));
                
                // Handle null result from repository
                if (keyValuesByQuayId == null) {
                    logger.warn("Repository returned null for findKeyValuesByIds, returning empty maps");
                    keyValuesByQuayId = new HashMap<>();
                }
                
                // Return in the order requested, with empty map for quays with no key-values
                final Map<Long, Map<String, Value>> finalKeyValuesByQuayId = keyValuesByQuayId;
                List<Map<String, Value>> result = quayIds.stream()
                    .map(quayId -> {
                        Map<String, Value> keyValues = finalKeyValuesByQuayId.get(quayId);
                        if (keyValues != null && !keyValues.isEmpty()) {
                            logger.debug("Found {} key-values for Quay ID {}", keyValues.size(), quayId);
                        }
                        return keyValues != null ? keyValues : new HashMap<String, Value>(); // return empty map if null
                    })
                    .collect(Collectors.toList());
                    
                return CompletableFuture.completedFuture(result);
                    
            } catch (Exception e) {
                logger.error("Error in QuayKeyValuesDataLoader batch function", e);
                // Instead of failing, return empty maps for all requested IDs
                List<Map<String, Value>> emptyResult = quayIds.stream()
                    .map(id -> new HashMap<String, Value>())
                    .collect(Collectors.toList());
                return CompletableFuture.completedFuture(emptyResult);
            }
        }, DataLoaderOptions.newOptions()
            .setBatchingEnabled(true)
            .setMaxBatchSize(100));
    }
}