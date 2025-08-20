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
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.rest.graphql.dataloader.GraphQLDataLoaderRegistryService;
import org.rutebanken.tiamat.rest.graphql.dataloader.TopographicPlaceDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * GraphQL DataFetcher for topographic place resolution using DataLoader.
 * This fetcher is used when GraphQL needs to resolve topographic place from stop place references.
 */
@Component
public class TopographicPlaceDataFetcher implements DataFetcher<Object> {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceDataFetcher.class);

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        StopPlace stopPlace = environment.getSource();
        
        if (stopPlace == null || stopPlace.getTopographicPlace() == null) {
            return null;
        }
        
        TopographicPlace topographicPlace = stopPlace.getTopographicPlace();
        
        // Always use DataLoader for consistent batching behavior
        DataLoader<TopographicPlaceDataLoader.TopographicPlaceKey, TopographicPlace> dataLoader = 
            environment.getDataLoader(GraphQLDataLoaderRegistryService.TOPOGRAPHIC_PLACE_LOADER);
        
        if (dataLoader == null) {
            throw new IllegalStateException("TopographicPlace DataLoader is not available in GraphQL context");
        }
        
        if (topographicPlace.getNetexId() == null) {
            logger.warn("TopographicPlace has null netexId for stop place: {}", stopPlace.getNetexId());
            return null;
        }
        
        String netexId = topographicPlace.getNetexId();
        Long version = topographicPlace.getVersion();
        
        TopographicPlaceDataLoader.TopographicPlaceKey key = 
            new TopographicPlaceDataLoader.TopographicPlaceKey(netexId, version);
        
        logger.debug("Loading topographic place via DataLoader: {} v{} for stop place {}", 
            netexId, version, stopPlace.getNetexId());
        
        return dataLoader.load(key);
    }
}