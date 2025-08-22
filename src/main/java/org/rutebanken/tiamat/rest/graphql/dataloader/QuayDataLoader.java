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
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * DataLoader for efficiently loading quays to solve N+1 query problems.
 * Batches multiple quay requests for stop places into single database queries.
 */
@Component
public class QuayDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(QuayDataLoader.class);

    private final QuayRepository quayRepository;

    @Autowired
    public QuayDataLoader(QuayRepository quayRepository) {
        this.quayRepository = quayRepository;
    }

    /**
     * Creates a DataLoader for batching quay requests by stop place ID
     */
    public DataLoader<Long, List<Quay>> createDataLoader() {
        return DataLoaderFactory.newDataLoader(stopPlaceIds -> {
            logger.debug("Batch loading quays for {} stop places", stopPlaceIds.size());
            
            try {
                // Get quays grouped by stop place ID (execute in current thread to maintain Hibernate session)
                Map<Long, List<Quay>> quaysByStopPlace = quayRepository.findQuaysByStopPlaceIds(Set.copyOf(stopPlaceIds));
                
                // Return in the order requested, with empty lists for stop places with no quays
                List<List<Quay>> result = stopPlaceIds.stream()
                    .map(stopPlaceId -> {
                        List<Quay> quaysForStopPlace = quaysByStopPlace.getOrDefault(stopPlaceId, List.of());
                        logger.debug("Found {} quays for stop place ID {}", quaysForStopPlace.size(), stopPlaceId);
                        return quaysForStopPlace;
                    })
                    .collect(Collectors.toList());
                    
                return CompletableFuture.completedFuture(result);
                    
            } catch (Exception e) {
                logger.error("Error in QuayDataLoader batch function", e);
                return CompletableFuture.failedFuture(new RuntimeException("Failed to load quays for stop places", e));
            }
        });
    }
}