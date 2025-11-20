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

package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.dataloader.DataLoader;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.rest.graphql.dataloader.GraphQLDataLoaderRegistryService;
import org.rutebanken.tiamat.rest.graphql.dataloader.StopPlaceDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * GraphQL DataFetcher for parent stop place resolution using DataLoader.
 * This fetcher is used when GraphQL needs to resolve parent entities from child references.
 */
@Component
public class ParentStopPlaceDataFetcher implements DataFetcher<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ParentStopPlaceDataFetcher.class);

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        Object source = environment.getSource();
        
        if (!(source instanceof StopPlace)) {
            return null;
        }
        
        StopPlace stopPlace = (StopPlace) source;
        
        // Only resolve parent if this stop place has a parent reference
        if (stopPlace.getParentSiteRef() == null || stopPlace.getParentSiteRef().getRef() == null) {
            return null;
        }
        
        // Try to use DataLoader for batching
        DataLoader<StopPlaceDataLoader.StopPlaceKey, StopPlace> dataLoader = 
            environment.getDataLoader(GraphQLDataLoaderRegistryService.STOP_PLACE_LOADER);
        
        if (dataLoader != null) {
            String parentRef = stopPlace.getParentSiteRef().getRef();
            Long parentVersion = Long.parseLong(stopPlace.getParentSiteRef().getVersion());
            
            StopPlaceDataLoader.StopPlaceKey key = 
                new StopPlaceDataLoader.StopPlaceKey(parentRef, parentVersion);
            
            logger.debug("Using DataLoader to resolve parent {} for child {}", parentRef, stopPlace.getNetexId());
            return dataLoader.load(key);
        }
        
        logger.warn("No DataLoader available for parent stop place resolution, returning null");
        return null;
    }
}