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
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.rest.graphql.dataloader.GraphQLDataLoaderRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * GraphQL DataFetcher for resolving tariff zones field on StopPlace entities.
 * Uses DataLoader to batch requests and avoid N+1 query problems.
 * Replaces the original StopPlaceTariffZoneFetcher with DataLoader-based implementation.
 */
@Component
public class TariffZonesDataFetcher implements DataFetcher<CompletableFuture<List<TariffZone>>> {

    private static final Logger logger = LoggerFactory.getLogger(TariffZonesDataFetcher.class);

    @Override
    public CompletableFuture<List<TariffZone>> get(DataFetchingEnvironment environment) {
        StopPlace stopPlace = environment.getSource();
        
        if (stopPlace == null) {
            logger.debug("StopPlace is null, returning empty tariff zones");
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        DataLoader<Long, List<TariffZone>> dataLoader = environment.getDataLoader(GraphQLDataLoaderRegistryService.TARIFF_ZONES_LOADER);
        if (dataLoader == null) {
            logger.warn("TariffZones DataLoader not found in registry");
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        logger.debug("Loading tariff zones for StopPlace ID: {}", stopPlace.getId());
        return dataLoader.load(stopPlace.getId());
    }
}