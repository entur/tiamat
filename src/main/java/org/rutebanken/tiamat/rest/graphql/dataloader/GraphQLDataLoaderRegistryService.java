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

import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.authorization.EntityPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Service for creating and configuring DataLoader registry for GraphQL execution.
 * This provides proper DataLoader management for solving N+1 query problems.
 */
@Service
public class GraphQLDataLoaderRegistryService {

    private static final Logger logger = LoggerFactory.getLogger(GraphQLDataLoaderRegistryService.class);
    
    public static final String ENTITY_PERMISSIONS_LOADER = "entityPermissions";
    public static final String PARENT_STOP_PLACE_LOADER = "parentStopPlace";

    @Autowired
    private EntityPermissionsDataLoader entityPermissionsDataLoader;

    @Autowired
    private ParentStopPlaceDataLoader parentStopPlaceDataLoader;

    /**
     * Creates a new DataLoaderRegistry configured with all necessary DataLoaders
     * 
     * @return configured DataLoaderRegistry for GraphQL execution
     */
    public DataLoaderRegistry createDataLoaderRegistry() {
        logger.debug("Creating DataLoaderRegistry with configured DataLoaders");
        
        DataLoaderRegistry registry = new DataLoaderRegistry();
        
        // Entity Permissions DataLoader
        DataLoader<String, EntityPermissions> entityPermissionsLoader = createEntityPermissionsDataLoader();
        registry.register(ENTITY_PERMISSIONS_LOADER, entityPermissionsLoader);
        
        // Parent StopPlace DataLoader  
        DataLoader<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> parentStopPlaceLoader = createParentStopPlaceDataLoader();
        registry.register(PARENT_STOP_PLACE_LOADER, parentStopPlaceLoader);
        
        logger.debug("DataLoaderRegistry created with {} DataLoaders", registry.getKeys().size());
        
        return registry;
    }

    /**
     * Creates EntityPermissions DataLoader with proper batch function
     */
    private DataLoader<String, EntityPermissions> createEntityPermissionsDataLoader() {
        BatchLoader<String, EntityPermissions> batchLoader = (List<String> netexIds) -> {
            logger.debug("Batch loading entity permissions for {} entities", netexIds.size());
            
            return CompletableFuture.supplyAsync(() -> {
                try {
                    var permissionsMap = entityPermissionsDataLoader.batchLoadEntityPermissions(netexIds);
                    
                    // Return results in same order as input keys
                    List<EntityPermissions> results = netexIds.stream()
                        .map(permissionsMap::get)
                        .collect(Collectors.toList());
                    
                    logger.debug("Successfully batch loaded permissions for {}/{} entities", 
                        results.stream().mapToInt(p -> p != null ? 1 : 0).sum(), netexIds.size());
                    
                    return results;
                } catch (Exception e) {
                    logger.error("Error in entity permissions batch loader", e);
                    // Return null list to indicate failure - DataLoader will handle this
                    return netexIds.stream()
                        .map(id -> (EntityPermissions) null)
                        .collect(Collectors.toList());
                }
            });
        };
        
        return DataLoader.newDataLoader(batchLoader);
    }

    /**
     * Creates ParentStopPlace DataLoader with proper batch function
     */
    private DataLoader<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> createParentStopPlaceDataLoader() {
        // Delegate to the existing ParentStopPlaceDataLoader which already has proper batching logic
        logger.debug("Creating ParentStopPlace DataLoader using existing batch infrastructure");
        return parentStopPlaceDataLoader.createDataLoader();
    }
}