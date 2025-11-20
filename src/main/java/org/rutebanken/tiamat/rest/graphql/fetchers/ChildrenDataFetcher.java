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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * GraphQL DataFetcher for resolving children field on ParentStopPlace entities.
 * Uses DataLoader to batch requests and avoid N+1 query problems.
 */
@Component
public class ChildrenDataFetcher implements DataFetcher<CompletableFuture<Set<StopPlace>>> {

    private static final Logger logger = LoggerFactory.getLogger(ChildrenDataFetcher.class);

    @Override
    public CompletableFuture<Set<StopPlace>> get(DataFetchingEnvironment environment) {
        StopPlace stopPlace = environment.getSource();
        
        if (stopPlace == null) {
            logger.debug("StopPlace is null, returning empty children");
            return CompletableFuture.completedFuture(new HashSet<>());
        }

        if (!stopPlace.isParentStopPlace()) {
            logger.debug("StopPlace {} is not a parent stop place, returning empty children", stopPlace.getNetexId());
            return CompletableFuture.completedFuture(new HashSet<>());
        }

        DataLoader<Long, Set<StopPlace>> dataLoader = environment.getDataLoader(GraphQLDataLoaderRegistryService.CHILDREN_LOADER);
        if (dataLoader == null) {
            logger.warn("Children DataLoader not found in registry");
            return CompletableFuture.completedFuture(new HashSet<>());
        }

        logger.debug("Loading children for ParentStopPlace ID: {}", stopPlace.getId());
        return dataLoader.load(stopPlace.getId());
    }
}