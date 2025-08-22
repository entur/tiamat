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
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.VersionOfObjectRefStructure;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.generic.GenericEntityInVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * DataLoader for efficiently loading GroupOfStopPlaces member StopPlaces to solve N+1 query problems.
 * Batches multiple member reference requests into single database queries.
 */
@Component
public class GroupOfStopPlacesMembersDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(GroupOfStopPlacesMembersDataLoader.class);

    private final GenericEntityInVersionRepository genericRepository;
    private final StopPlaceRepository stopPlaceRepository;

    @Autowired
    public GroupOfStopPlacesMembersDataLoader(GenericEntityInVersionRepository genericRepository,
                                            StopPlaceRepository stopPlaceRepository) {
        this.genericRepository = genericRepository;
        this.stopPlaceRepository = stopPlaceRepository;
    }

    /**
     * Key class for batching netexId and version combinations
     */
    public static class MemberKey {
        private final String netexId;
        private final Long version; // null for latest version
        
        public MemberKey(String netexId, Long version) {
            this.netexId = netexId;
            this.version = version;
        }
        
        public MemberKey(VersionOfObjectRefStructure ref) {
            this.netexId = ref.getRef();
            this.version = ref.getVersion() != null ? Long.valueOf(ref.getVersion()) : null;
        }
        
        public String getNetexId() { return netexId; }
        public Long getVersion() { return version; }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MemberKey)) return false;
            MemberKey memberKey = (MemberKey) o;
            return Objects.equals(netexId, memberKey.netexId) && Objects.equals(version, memberKey.version);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(netexId, version);
        }
        
        @Override
        public String toString() {
            return "MemberKey{netexId='" + netexId + "', version=" + version + "}";
        }
    }

    /**
     * Creates a DataLoader for batching GroupOfStopPlaces member requests
     */
    public DataLoader<MemberKey, StopPlace> createDataLoader() {
        return DataLoaderFactory.newDataLoader(memberKeys -> {
            logger.debug("Batch loading {} GroupOfStopPlaces members", memberKeys.size());
            
            try {
                // Separate versioned and unversioned requests
                List<MemberKey> versionedKeys = memberKeys.stream()
                    .filter(key -> key.getVersion() != null)
                    .collect(Collectors.toList());
                
                List<MemberKey> unversionedKeys = memberKeys.stream()
                    .filter(key -> key.getVersion() == null)
                    .collect(Collectors.toList());
                
                Map<MemberKey, StopPlace> results = new HashMap<>();
                
                // Handle versioned requests - need individual queries for now
                // TODO: Could be optimized with a batch method that handles versioned queries
                for (MemberKey key : versionedKeys) {
                    StopPlace stopPlace = genericRepository.findFirstByNetexIdAndVersion(
                        key.getNetexId(), key.getVersion(), StopPlace.class);
                    if (stopPlace != null) {
                        results.put(key, stopPlace);
                    }
                }
                
                // Handle unversioned requests (latest version) - can be batched
                if (!unversionedKeys.isEmpty()) {
                    List<String> netexIds = unversionedKeys.stream()
                        .map(MemberKey::getNetexId)
                        .collect(Collectors.toList());
                    
                    // Use the optimized StopPlace-specific method with window function
                    List<StopPlace> stopPlaces = stopPlaceRepository.findLatestVersionByNetexIds(netexIds);
                    
                    // Map results back to keys
                    Map<String, StopPlace> stopPlaceMap = stopPlaces.stream()
                        .collect(Collectors.toMap(
                            StopPlace::getNetexId,
                            sp -> sp,
                            (existing, replacement) -> existing // handle duplicates
                        ));
                    
                    for (MemberKey key : unversionedKeys) {
                        StopPlace stopPlace = stopPlaceMap.get(key.getNetexId());
                        if (stopPlace != null) {
                            results.put(key, stopPlace);
                        }
                    }
                }
                
                // Return in the order requested, with null for missing members
                List<StopPlace> result = memberKeys.stream()
                    .map(key -> {
                        StopPlace stopPlace = results.get(key);
                        if (stopPlace != null) {
                            logger.debug("Found GroupOfStopPlaces member for key {}", key);
                        }
                        return stopPlace;
                    })
                    .collect(Collectors.toList());
                    
                return CompletableFuture.completedFuture(result);
                    
            } catch (Exception e) {
                logger.error("Error in GroupOfStopPlacesMembersDataLoader batch function", e);
                return CompletableFuture.failedFuture(new RuntimeException("Failed to load GroupOfStopPlaces members", e));
            }
        }, DataLoaderOptions.newOptions()
            .setBatchingEnabled(true)
            .setMaxBatchSize(100));
    }
}