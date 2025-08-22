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
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.repository.GroupOfStopPlacesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * DataLoader for efficiently loading Groups (GroupOfStopPlaces) to solve N+1 query problems.
 * Batches multiple group requests into single database queries.
 */
@Component
public class GroupsDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(GroupsDataLoader.class);

    private final GroupOfStopPlacesRepository groupOfStopPlacesRepository;

    @Autowired
    public GroupsDataLoader(GroupOfStopPlacesRepository groupOfStopPlacesRepository) {
        this.groupOfStopPlacesRepository = groupOfStopPlacesRepository;
    }

    /**
     * Creates a DataLoader for batching Group requests by stop place ID
     */
    public DataLoader<Long, List<GroupOfStopPlaces>> createDataLoader() {
        return DataLoaderFactory.newDataLoader(stopPlaceIds -> {
            logger.debug("Batch loading groups for {} stop places", stopPlaceIds.size());
            
            try {
                // Get groups for the requested stop place IDs
                Map<Long, List<GroupOfStopPlaces>> groupsByStopPlaceId = 
                    groupOfStopPlacesRepository.findGroupsByStopPlaceIds(Set.copyOf(stopPlaceIds));
                
                // Return in the order requested, with empty list for stop places with no groups
                List<List<GroupOfStopPlaces>> result = stopPlaceIds.stream()
                    .map(stopPlaceId -> {
                        List<GroupOfStopPlaces> groups = groupsByStopPlaceId.get(stopPlaceId);
                        if (groups != null && !groups.isEmpty()) {
                            logger.debug("Found {} groups for StopPlace ID {}", groups.size(), stopPlaceId);
                        }
                        return groups != null ? groups : new ArrayList<GroupOfStopPlaces>(); // return empty list if null
                    })
                    .collect(Collectors.toList());
                    
                return CompletableFuture.completedFuture(result);
                    
            } catch (Exception e) {
                logger.error("Error in GroupsDataLoader batch function", e);
                // Instead of failing, return empty lists for all requested IDs
                List<List<GroupOfStopPlaces>> emptyResult = stopPlaceIds.stream()
                    .map(id -> new ArrayList<GroupOfStopPlaces>())
                    .collect(Collectors.toList());
                return CompletableFuture.completedFuture(emptyResult);
            }
        }, DataLoaderOptions.newOptions()
            .setBatchingEnabled(true)
            .setMaxBatchSize(100));
    }
}