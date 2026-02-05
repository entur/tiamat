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
import org.rutebanken.tiamat.model.Value;
import org.rutebanken.tiamat.rest.graphql.dataloader.GraphQLDataLoaderRegistryService;
import org.rutebanken.tiamat.rest.graphql.helpers.KeyValueWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * GraphQL DataFetcher for resolving keyValues field on StopPlace entities.
 * Uses DataLoader to batch requests and avoid N+1 query problems.
 */
@Component
public class StopPlaceKeyValuesDataFetcher implements DataFetcher<CompletableFuture<List<KeyValueWrapper>>> {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceKeyValuesDataFetcher.class);

    @Override
    public CompletableFuture<List<KeyValueWrapper>> get(DataFetchingEnvironment environment) {
        StopPlace stopPlace = environment.getSource();
        if (stopPlace == null) {
            logger.debug("StopPlace is null, returning null keyValues");
            return CompletableFuture.completedFuture(null);
        }

        DataLoader<Long, Map<String, Value>> dataLoader = environment.getDataLoader(GraphQLDataLoaderRegistryService.STOP_PLACE_KEY_VALUES_LOADER);
        if (dataLoader == null) {
            logger.warn("StopPlaceKeyValues DataLoader not found in registry");
            return CompletableFuture.completedFuture(null);
        }

        logger.debug("Loading keyValues for StopPlace ID: {}", stopPlace.getId());
        
        // Load the Map from DataLoader and convert to List<KeyValueWrapper>
        return dataLoader.load(stopPlace.getId()).thenApply(keyValuesMap -> {
            if (keyValuesMap == null || keyValuesMap.isEmpty()) {
                return new ArrayList<>();
            }
            
            List<KeyValueWrapper> keyValuesList = new ArrayList<>();
            keyValuesMap.forEach((key, value) -> {
                keyValuesList.add(new KeyValueWrapper(key, value));
            });
            
            return keyValuesList;
        });
    }
}