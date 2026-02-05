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
import org.dataloader.DataLoaderOptions;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
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
 * DataLoader for batching topographic place lookups to solve N+1 query problem
 */
@Component
public class TopographicPlaceDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceDataLoader.class);

    private final TopographicPlaceRepository topographicPlaceRepository;

    public TopographicPlaceDataLoader(TopographicPlaceRepository topographicPlaceRepository) {
        this.topographicPlaceRepository = topographicPlaceRepository;
    }

    /**
     * Creates a DataLoader for batching topographic place lookups
     * @return DataLoader configured for topographic place batch loading
     */
    public DataLoader<TopographicPlaceKey, TopographicPlace> createDataLoader() {
        BatchLoader<TopographicPlaceKey, TopographicPlace> batchLoader = keys -> {
            logger.debug("Batch loading {} topographic places", keys.size());
            
            try {
                // Group keys by netexId to versions for batch loading
                Map<String, Set<Long>> netexIdToVersions = keys.stream()
                    .collect(Collectors.groupingBy(
                        TopographicPlaceKey::getNetexId,
                        Collectors.mapping(
                            TopographicPlaceKey::getVersion,
                            Collectors.toSet()
                        )
                    ));
                
                logger.debug("Batching {} topographic place keys into {} unique netexIds", 
                    keys.size(), netexIdToVersions.size());
                
                // Use repository's batch method for efficient loading (synchronous to stay in session)
                Map<String, Map<Long, TopographicPlace>> batchResults = topographicPlaceRepository.findByNetexIdsAndVersions(netexIdToVersions);
                
                // Convert batch results back to key-based map
                Map<TopographicPlaceKey, TopographicPlace> resultMap = new HashMap<>();
                for (Map.Entry<String, Map<Long, TopographicPlace>> netexIdEntry : batchResults.entrySet()) {
                    String netexId = netexIdEntry.getKey();
                    Map<Long, TopographicPlace> versionMap = netexIdEntry.getValue();
                    
                    for (Map.Entry<Long, TopographicPlace> versionEntry : versionMap.entrySet()) {
                        Long version = versionEntry.getKey();
                        TopographicPlace topographicPlace = versionEntry.getValue();
                        TopographicPlaceKey key = new TopographicPlaceKey(netexId, version);
                        resultMap.put(key, topographicPlace);
                    }
                }
                
                // Return results in the same order as keys
                List<TopographicPlace> results = keys.stream()
                    .map(resultMap::get)
                    .collect(Collectors.toList());
                
                logger.debug("Successfully batch loaded {}/{} topographic places", 
                    results.stream().mapToInt(tp -> tp != null ? 1 : 0).sum(), keys.size());
                
                return CompletableFuture.completedFuture(results);
            } catch (Exception e) {
                logger.error("Error in topographic place batch loader", e);
                // Return null list to indicate failure - DataLoader will handle this
                List<TopographicPlace> nullResults = keys.stream()
                    .map(key -> (TopographicPlace) null)
                    .collect(Collectors.toList());
                return CompletableFuture.completedFuture(nullResults);
            }
        };
        
        return DataLoader.newDataLoader(batchLoader, DataLoaderOptions.newOptions()
            .setBatchingEnabled(true)
            .setMaxBatchSize(100));
    }

    /**
     * Key class for topographic place lookups combining netexId and version
     */
    public static class TopographicPlaceKey {
        private final String netexId;
        private final Long version;

        public TopographicPlaceKey(String netexId, Long version) {
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
            TopographicPlaceKey that = (TopographicPlaceKey) o;
            return netexId.equals(that.netexId) && version.equals(that.version);
        }

        @Override
        public int hashCode() {
            return netexId.hashCode() * 31 + version.hashCode();
        }

        @Override
        public String toString() {
            return "TopographicPlaceKey{" +
                "netexId='" + netexId + '\'' +
                ", version=" + version +
                '}';
        }
    }
}