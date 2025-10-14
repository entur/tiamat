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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.rutebanken.tiamat.model.Quay;

import java.util.List;

/**
 * GraphQL DataFetcher for quay resolution using DataLoader.
 * This fetcher is used when GraphQL needs to resolve quays from stop place references.
 */
@Component
public class QuayDataFetcher implements DataFetcher<Object> {

    private static final Logger logger = LoggerFactory.getLogger(QuayDataFetcher.class);

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        StopPlace stopPlace = environment.getSource();
        
        if (stopPlace == null || stopPlace.getId() == null) {
            return List.of();
        }
        
        // Use DataLoader for consistent batching behavior
        DataLoader<Long, List<Quay>> dataLoader = 
            environment.getDataLoader(GraphQLDataLoaderRegistryService.QUAY_LOADER);
        
        if (dataLoader == null) {
            throw new IllegalStateException("Quay DataLoader is not available in GraphQL context");
        }
        
        Long stopPlaceId = stopPlace.getId();
        
        logger.debug("Loading quays via DataLoader for stop place ID: {}", stopPlaceId);
        
        return dataLoader.load(stopPlaceId);
    }
}