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

import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * DataLoader for batching parent stop place lookups to solve N+1 query problem
 */
@Component
public class ParentStopPlaceDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(ParentStopPlaceDataLoader.class);

    private final StopPlaceRepository stopPlaceRepository;

    public ParentStopPlaceDataLoader(StopPlaceRepository stopPlaceRepository) {
        this.stopPlaceRepository = stopPlaceRepository;
    }

    /**
     * Creates a DataLoader for batching parent stop place lookups
     * @return DataLoader configured for parent stop place batch loading
     */
    public DataLoader<ParentStopPlaceKey, StopPlace> createDataLoader() {
        BatchLoader<ParentStopPlaceKey, StopPlace> batchLoader = keys -> {
            logger.debug("Batch loading {} parent stop places", keys.size());
            
            try {
                // Group keys by netexId to versions for batch loading
                Map<String, Set<Long>> netexIdToVersions = keys.stream()
                    .collect(Collectors.groupingBy(
                        ParentStopPlaceKey::getNetexId,
                        Collectors.mapping(
                            ParentStopPlaceKey::getVersion,
                            Collectors.toSet()
                        )
                    ));
                
                logger.debug("Batching {} parent stop place keys into {} unique netexIds", 
                    keys.size(), netexIdToVersions.size());
                
                // Use repository's batch method for efficient loading (synchronous to stay in session)
                Map<String, Map<Long, StopPlace>> batchResults = stopPlaceRepository.findByNetexIdsAndVersions(netexIdToVersions);
                
                // Convert batch results back to key-based map
                Map<ParentStopPlaceKey, StopPlace> resultMap = new HashMap<>();
                for (Map.Entry<String, Map<Long, StopPlace>> netexIdEntry : batchResults.entrySet()) {
                    String netexId = netexIdEntry.getKey();
                    Map<Long, StopPlace> versionMap = netexIdEntry.getValue();
                    
                    for (Map.Entry<Long, StopPlace> versionEntry : versionMap.entrySet()) {
                        Long version = versionEntry.getKey();
                        StopPlace stopPlace = versionEntry.getValue();
                        ParentStopPlaceKey key = new ParentStopPlaceKey(netexId, version);
                        resultMap.put(key, stopPlace);
                    }
                }
                
                // Return results in the same order as keys
                List<StopPlace> results = keys.stream()
                    .map(resultMap::get)
                    .collect(Collectors.toList());
                
                logger.debug("Successfully batch loaded {}/{} parent stop places", 
                    results.stream().mapToInt(sp -> sp != null ? 1 : 0).sum(), keys.size());
                
                return CompletableFuture.completedFuture(results);
            } catch (Exception e) {
                logger.error("Error in parent stop place batch loader", e);
                // Return null list to indicate failure - DataLoader will handle this
                List<StopPlace> nullResults = keys.stream()
                    .map(key -> (StopPlace) null)
                    .collect(Collectors.toList());
                return CompletableFuture.completedFuture(nullResults);
            }
        };
        
        return DataLoader.newDataLoader(batchLoader);
    }

    /**
     * Key class for parent stop place lookups combining netexId and version
     */
    public static class ParentStopPlaceKey {
        private final String netexId;
        private final Long version;

        public ParentStopPlaceKey(String netexId, Long version) {
            this.netexId = netexId;
            this.version = version;
        }

        public String getNetexId() {
            return netexId;
        }

        public Long getVersion() {
            return version;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ParentStopPlaceKey that = (ParentStopPlaceKey) o;
            return netexId.equals(that.netexId) && version.equals(that.version);
        }

        @Override
        public int hashCode() {
            return netexId.hashCode() * 31 + version.hashCode();
        }

        @Override
        public String toString() {
            return "ParentStopPlaceKey{" +
                "netexId='" + netexId + '\'' +
                ", version=" + version +
                '}';
        }
    }
}