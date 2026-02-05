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
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * DataLoader for efficiently loading children of ParentStopPlace to solve N+1 query problems.
 * Batches multiple children requests into single database queries.
 */
@Component
public class ChildrenDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(ChildrenDataLoader.class);

    private final StopPlaceRepository stopPlaceRepository;

    @Autowired
    public ChildrenDataLoader(StopPlaceRepository stopPlaceRepository) {
        this.stopPlaceRepository = stopPlaceRepository;
    }

    /**
     * Creates a DataLoader for batching children requests by parent stop place ID
     */
    public DataLoader<Long, Set<StopPlace>> createDataLoader() {
        return DataLoaderFactory.newDataLoader(parentStopPlaceIds -> {
            logger.debug("Batch loading children for {} parent stop places", parentStopPlaceIds.size());
            
            try {
                // Get children for the requested parent stop place IDs
                Map<Long, Set<StopPlace>> childrenByParentId = 
                    stopPlaceRepository.findChildrenByParentStopPlaceIds(Set.copyOf(parentStopPlaceIds));
                
                // Return in the order requested, with empty set for parents with no children
                List<Set<StopPlace>> result = parentStopPlaceIds.stream()
                    .map(parentId -> {
                        Set<StopPlace> children = childrenByParentId.get(parentId);
                        if (children != null && !children.isEmpty()) {
                            logger.debug("Found {} children for parent StopPlace ID {}", children.size(), parentId);
                        }
                        return children != null ? children : new HashSet<StopPlace>(); // return empty set if null
                    })
                    .collect(Collectors.toList());
                    
                return CompletableFuture.completedFuture(result);
                    
            } catch (Exception e) {
                logger.error("Error in ChildrenDataLoader batch function", e);
                // Instead of failing, return empty sets for all requested IDs
                List<Set<StopPlace>> emptyResult = parentStopPlaceIds.stream()
                    .map(id -> new HashSet<StopPlace>())
                    .collect(Collectors.toList());
                return CompletableFuture.completedFuture(emptyResult);
            }
        }, DataLoaderOptions.newOptions()
            .setBatchingEnabled(true)
            .setMaxBatchSize(100));
    }
}