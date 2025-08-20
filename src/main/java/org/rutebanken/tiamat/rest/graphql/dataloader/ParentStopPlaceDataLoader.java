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

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * DataLoader for batching parent stop place lookups to solve N+1 query problem
 */
@Component
public class ParentStopPlaceDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(ParentStopPlaceDataLoader.class);

    private final EntityManager entityManager;
    private final StopPlaceRepository stopPlaceRepository;

    public ParentStopPlaceDataLoader(EntityManager entityManager, StopPlaceRepository stopPlaceRepository) {
        this.entityManager = entityManager;
        this.stopPlaceRepository = stopPlaceRepository;
    }

    /**
     * Creates a DataLoader for batching parent stop place lookups
     * @return DataLoader configured for parent stop place batch loading
     */
    public DataLoader<ParentStopPlaceKey, StopPlace> createDataLoader() {
        BatchLoader<ParentStopPlaceKey, StopPlace> batchLoader = keys -> {
            logger.debug("Batch loading {} parent stop places", keys.size());
            
            return CompletableFuture.supplyAsync(() -> {
                Map<ParentStopPlaceKey, StopPlace> resultMap = new HashMap<>();
                
                // Group keys by netexId to reduce number of queries
                Map<String, List<ParentStopPlaceKey>> keysByNetexId = keys.stream()
                    .collect(Collectors.groupingBy(ParentStopPlaceKey::getNetexId));
                
                // For each unique netexId, fetch all versions needed
                for (Map.Entry<String, List<ParentStopPlaceKey>> entry : keysByNetexId.entrySet()) {
                    String netexId = entry.getKey();
                    List<ParentStopPlaceKey> netexIdKeys = entry.getValue();
                    
                    // Extract unique versions for this netexId
                    List<Long> versions = netexIdKeys.stream()
                        .map(ParentStopPlaceKey::getVersion)
                        .distinct()
                        .collect(Collectors.toList());
                    
                    if (versions.size() == 1) {
                        // Single version - use existing method
                        Long version = versions.get(0);
                        StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdAndVersion(netexId, version);
                        if (stopPlace != null) {
                            ParentStopPlaceKey key = new ParentStopPlaceKey(netexId, version);
                            resultMap.put(key, stopPlace);
                        }
                    } else {
                        // Multiple versions - use batch query
                        TypedQuery<StopPlace> query = entityManager.createQuery(
                            "SELECT sp FROM StopPlace sp WHERE sp.netexId = :netexId AND sp.version IN :versions", 
                            StopPlace.class);
                        query.setParameter("netexId", netexId);
                        query.setParameter("versions", versions);
                        
                        List<StopPlace> stopPlaces = query.getResultList();
                        for (StopPlace stopPlace : stopPlaces) {
                            ParentStopPlaceKey key = new ParentStopPlaceKey(stopPlace.getNetexId(), stopPlace.getVersion());
                            resultMap.put(key, stopPlace);
                        }
                    }
                }
                
                // Return results in the same order as keys
                return keys.stream()
                    .map(resultMap::get)
                    .collect(Collectors.toList());
            });
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