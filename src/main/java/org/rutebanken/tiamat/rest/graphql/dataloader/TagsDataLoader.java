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
import org.rutebanken.tiamat.model.tag.Tag;
import org.rutebanken.tiamat.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * DataLoader for efficiently loading Tags to solve N+1 query problems.
 * Batches multiple tag requests into single database queries.
 */
@Component
public class TagsDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(TagsDataLoader.class);

    private final TagRepository tagRepository;

    @Autowired
    public TagsDataLoader(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Creates a DataLoader for batching Tag requests by netex ID reference
     */
    public DataLoader<String, Set<Tag>> createDataLoader() {
        return DataLoaderFactory.newDataLoader(netexIds -> {
            logger.debug("Batch loading tags for {} entities", netexIds.size());
            
            try {
                // Get all tags for the requested netex ID references
                List<Tag> allTags = tagRepository.findByIdReferencesIn(Set.copyOf(netexIds));
                
                // Group tags by idReference
                Map<String, Set<Tag>> tagsByIdReference = new HashMap<>();
                for (Tag tag : allTags) {
                    tagsByIdReference.computeIfAbsent(tag.getIdReference(), k -> new HashSet<>()).add(tag);
                }
                
                // Return in the order requested, with empty set for entities with no tags
                List<Set<Tag>> result = netexIds.stream()
                    .map(netexId -> {
                        Set<Tag> tags = tagsByIdReference.get(netexId);
                        if (tags != null && !tags.isEmpty()) {
                            logger.debug("Found {} tags for entity {}", tags.size(), netexId);
                        }
                        return tags != null ? tags : new HashSet<Tag>(); // return empty set if null
                    })
                    .collect(Collectors.toList());
                    
                return CompletableFuture.completedFuture(result);
                    
            } catch (Exception e) {
                logger.error("Error in TagsDataLoader batch function", e);
                // Instead of failing, return empty sets for all requested IDs
                List<Set<Tag>> emptyResult = netexIds.stream()
                    .map(id -> new HashSet<Tag>())
                    .collect(Collectors.toList());
                return CompletableFuture.completedFuture(emptyResult);
            }
        }, DataLoaderOptions.newOptions()
            .setBatchingEnabled(true)
            .setMaxBatchSize(100));
    }
}