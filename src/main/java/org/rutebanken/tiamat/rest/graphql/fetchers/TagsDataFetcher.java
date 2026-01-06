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
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.model.tag.Tag;
import org.rutebanken.tiamat.rest.graphql.dataloader.GraphQLDataLoaderRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * GraphQL DataFetcher for resolving tags field on entities using DataLoader.
 * Uses DataLoader to batch requests and avoid N+1 query problems.
 */
@Component
public class TagsDataFetcher implements DataFetcher<CompletableFuture<Set<Tag>>> {

    private static final Logger logger = LoggerFactory.getLogger(TagsDataFetcher.class);

    @Override
    public CompletableFuture<Set<Tag>> get(DataFetchingEnvironment environment) {
        Object source = environment.getSource();
        
        if (!(source instanceof IdentifiedEntity)) {
            logger.debug("Source is not an IdentifiedEntity, returning empty tags");
            return CompletableFuture.completedFuture(new HashSet<>());
        }
        
        IdentifiedEntity identifiedEntity = (IdentifiedEntity) source;
        if (identifiedEntity.getNetexId() == null) {
            logger.debug("Entity has no netexId, returning empty tags");
            return CompletableFuture.completedFuture(new HashSet<>());
        }

        DataLoader<String, Set<Tag>> dataLoader = environment.getDataLoader(GraphQLDataLoaderRegistryService.TAGS_LOADER);
        if (dataLoader == null) {
            logger.warn("Tags DataLoader not found in registry");
            return CompletableFuture.completedFuture(new HashSet<>());
        }

        logger.debug("Loading tags for entity ID: {}", identifiedEntity.getNetexId());
        return dataLoader.load(identifiedEntity.getNetexId());
    }
}