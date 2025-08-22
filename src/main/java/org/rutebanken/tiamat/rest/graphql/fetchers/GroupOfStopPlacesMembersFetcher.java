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
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.rest.graphql.dataloader.GroupOfStopPlacesMembersDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * GraphQL DataFetcher for GroupOfStopPlaces members that uses DataLoader to batch requests
 * and avoid N+1 query problems when accessing the members field on GroupOfStopPlaces.
 */
@Component("groupOfStopPlacesMembersFetcher")
public class GroupOfStopPlacesMembersFetcher implements DataFetcher<CompletableFuture<List<StopPlace>>> {

    private static final Logger logger = LoggerFactory.getLogger(GroupOfStopPlacesMembersFetcher.class);

    @Override
    public CompletableFuture<List<StopPlace>> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        GroupOfStopPlaces groupOfStopPlaces = dataFetchingEnvironment.getSource();

        if (groupOfStopPlaces == null || groupOfStopPlaces.getMembers() == null || groupOfStopPlaces.getMembers().isEmpty()) {
            logger.debug("No GroupOfStopPlaces or members found, returning empty list");
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        DataLoader<GroupOfStopPlacesMembersDataLoader.MemberKey, StopPlace> dataLoader =
            dataFetchingEnvironment.getDataLoader("groupOfStopPlacesMembersDataLoader");

        if (dataLoader == null) {
            logger.error("GroupOfStopPlacesMembers DataLoader not found in GraphQL context");
            throw new RuntimeException("GroupOfStopPlacesMembers DataLoader not registered");
        }

        // Convert member references to keys and load them using the DataLoader
        List<GroupOfStopPlacesMembersDataLoader.MemberKey> memberKeys = groupOfStopPlaces.getMembers().stream()
            .map(GroupOfStopPlacesMembersDataLoader.MemberKey::new)
            .collect(Collectors.toList());

        logger.debug("Loading {} GroupOfStopPlaces members for group ID: {}", memberKeys.size(), groupOfStopPlaces.getNetexId());

        // Load all members using the DataLoader - this will batch the requests
        List<CompletableFuture<StopPlace>> memberFutures = memberKeys.stream()
            .map(dataLoader::load)
            .collect(Collectors.toList());

        // Combine all futures into a single future that returns the list
        CompletableFuture<Void> allOf = CompletableFuture.allOf(memberFutures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(v -> memberFutures.stream()
            .map(CompletableFuture::join)
            .filter(stopPlace -> stopPlace != null) // Filter out null results (missing members)
            .collect(Collectors.toList()));
    }
}
