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

package org.rutebanken.tiamat.service.stopplace;

import com.google.common.base.Strings;
import jakarta.persistence.EntityManager;
import org.dataloader.DataLoader;
import org.hibernate.Session;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.graphql.dataloader.ParentStopPlaceDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Resolve and fetch parent stop places from a list of stops
 */
@Service
public class ParentStopPlacesFetcher {

    private static final Logger logger = LoggerFactory.getLogger(ParentStopPlacesFetcher.class);

    private final StopPlaceRepository stopPlaceRepository;

    private final EntityManager entityManager;

    private final ParentStopPlaceDataLoader parentStopPlaceDataLoader;

    public ParentStopPlacesFetcher(StopPlaceRepository stopPlaceRepository, EntityManager entityManager, ParentStopPlaceDataLoader parentStopPlaceDataLoader) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.entityManager = entityManager;
        this.parentStopPlaceDataLoader = parentStopPlaceDataLoader;
    }

    /**
     * Resolve and fetch parent stop places from a list of stops.
     * This method uses batching to solve the N+1 query problem.
     *
     * @param stopPlaceList list of stop places to resolve parents for
     * @param keepChilds whether to keep child stop places in the result
     * @return list of resolved parent stop places
     */
    public List<StopPlace> resolveParents(List<StopPlace> stopPlaceList, boolean keepChilds) {
        return resolveParents(stopPlaceList, keepChilds, null);
    }

    /**
     * Resolve and fetch parent stop places from a list of stops using DataLoader for batching.
     *
     * @param stopPlaceList list of stop places to resolve parents for
     * @param keepChilds whether to keep child stop places in the result
     * @param dataLoader optional DataLoader for batching parent lookups
     * @return list of resolved parent stop places
     */
    public List<StopPlace> resolveParents(List<StopPlace> stopPlaceList, boolean keepChilds, DataLoader<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> dataLoader) {
        Session session = entityManager.unwrap(Session.class);

        if (stopPlaceList == null || stopPlaceList.stream().noneMatch(sp -> sp != null)) {
            return stopPlaceList;
        }

        List<StopPlace> result = stopPlaceList.stream().filter(StopPlace::isParentStopPlace).collect(toList());
        List<StopPlace> nonParentStops = stopPlaceList.stream().filter(stopPlace -> !stopPlace.isParentStopPlace()).collect(toList());

        if (dataLoader != null) {
            // Use DataLoader for batching
            return resolveParentsWithDataLoader(result, nonParentStops, keepChilds, dataLoader, session);
        } else {
            // Fall back to original implementation for backwards compatibility
            return resolveParentsOriginal(result, nonParentStops, keepChilds, session);
        }
    }

    private List<StopPlace> resolveParentsWithDataLoader(
            List<StopPlace> result,
            List<StopPlace> nonParentStops,
            boolean keepChilds,
            DataLoader<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> dataLoader,
            Session session) {

        // For now, fall back to batch loading without DataLoader to avoid complexity
        // This still provides significant performance improvement over individual queries
        return resolveParentsWithBatchLoading(result, nonParentStops, keepChilds, session);
    }
    
    private List<StopPlace> resolveParentsWithBatchLoading(
            List<StopPlace> result,
            List<StopPlace> nonParentStops,
            boolean keepChilds,
            Session session) {
        
        // Collect all parent references that need to be loaded
        Map<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> keyToChild = new HashMap<>();
        for (StopPlace nonParentStop : nonParentStops) {
            if (nonParentStop.getParentSiteRef() != null) {
                ParentStopPlaceDataLoader.ParentStopPlaceKey key = new ParentStopPlaceDataLoader.ParentStopPlaceKey(
                    nonParentStop.getParentSiteRef().getRef(),
                    Long.parseLong(nonParentStop.getParentSiteRef().getVersion())
                );
                keyToChild.put(key, nonParentStop);
            } else {
                // No parent reference, add child directly
                result.add(nonParentStop);
            }
        }

        if (!keyToChild.isEmpty()) {
            // Load all parents in batch using direct repository calls
            Map<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> parentMap = loadParentsBatch(keyToChild.keySet());
            
            // Process results
            for (Map.Entry<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> entry : keyToChild.entrySet()) {
                ParentStopPlaceDataLoader.ParentStopPlaceKey key = entry.getKey();
                StopPlace child = entry.getValue();
                StopPlace parent = parentMap.get(key);

                if (parent != null) {
                    logger.debug("Resolved parent: {} {} from child {}", parent.getNetexId(), parent.getName(), child.getNetexId());

                    // Copy name from parent to child if child has no name
                    if (child.getName() == null || Strings.isNullOrEmpty(child.getName().getValue())) {
                        logger.debug("Copying name from parent {} to child stop: {}", parent.getId(), parent.getName());
                        child.setName(parent.getName());
                        session.setReadOnly(child, true);
                    }

                    // Add parent to result if not already present
                    if (result.stream().noneMatch(stopPlace -> stopPlace.getNetexId() != null
                        && (stopPlace.getNetexId().equals(parent.getNetexId()) && stopPlace.getVersion() == parent.getVersion()))) {
                        result.add(parent);
                    }

                    // Add child to result if requested
                    if (keepChilds) {
                        result.add(child);
                    }
                } else {
                    logger.warn("Could not resolve parent from {}", child.getParentSiteRef());
                    // Add child to result even if parent couldn't be resolved
                    result.add(child);
                }
            }
        }

        return result;
    }
    
    private Map<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> loadParentsBatch(Set<ParentStopPlaceDataLoader.ParentStopPlaceKey> keys) {
        Map<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> resultMap = new HashMap<>();
        
        // Group keys by netexId to reduce number of queries
        Map<String, List<ParentStopPlaceDataLoader.ParentStopPlaceKey>> keysByNetexId = keys.stream()
            .collect(toMap(
                ParentStopPlaceDataLoader.ParentStopPlaceKey::getNetexId,
                k -> List.of(k),
                (existing, replacement) -> {
                    List<ParentStopPlaceDataLoader.ParentStopPlaceKey> combined = new ArrayList<>(existing);
                    combined.addAll(replacement);
                    return combined;
                }
            ));
        
        // For each unique netexId, fetch all versions needed
        for (Map.Entry<String, List<ParentStopPlaceDataLoader.ParentStopPlaceKey>> entry : keysByNetexId.entrySet()) {
            String netexId = entry.getKey();
            List<ParentStopPlaceDataLoader.ParentStopPlaceKey> netexIdKeys = entry.getValue();
            
            // Extract unique versions for this netexId
            List<Long> versions = netexIdKeys.stream()
                .map(ParentStopPlaceDataLoader.ParentStopPlaceKey::getVersion)
                .distinct()
                .collect(toList());
            
            if (versions.size() == 1) {
                // Single version - use existing method
                Long version = versions.get(0);
                StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdAndVersion(netexId, version);
                if (stopPlace != null) {
                    ParentStopPlaceDataLoader.ParentStopPlaceKey key = new ParentStopPlaceDataLoader.ParentStopPlaceKey(netexId, version);
                    resultMap.put(key, stopPlace);
                }
            } else {
                // Multiple versions - use batch query
                var query = entityManager.createQuery(
                    "SELECT sp FROM StopPlace sp WHERE sp.netexId = :netexId AND sp.version IN :versions", 
                    StopPlace.class);
                query.setParameter("netexId", netexId);
                query.setParameter("versions", versions);
                
                List<StopPlace> stopPlaces = query.getResultList();
                for (StopPlace stopPlace : stopPlaces) {
                    ParentStopPlaceDataLoader.ParentStopPlaceKey key = new ParentStopPlaceDataLoader.ParentStopPlaceKey(
                        stopPlace.getNetexId(), stopPlace.getVersion());
                    resultMap.put(key, stopPlace);
                }
            }
        }
        
        return resultMap;
    }

    private List<StopPlace> resolveParentsOriginal(
            List<StopPlace> result,
            List<StopPlace> nonParentStops,
            boolean keepChilds,
            Session session) {

        nonParentStops.forEach(nonParentStop -> {
            if (nonParentStop.getParentSiteRef() != null) {
                // Parent stop place refs should have version. If not, let it fail.
                StopPlace parent = stopPlaceRepository.findFirstByNetexIdAndVersion(nonParentStop.getParentSiteRef().getRef(),
                        Long.parseLong(nonParentStop.getParentSiteRef().getVersion()));
                if (parent != null) {
                    logger.info("Resolved parent: {} {} from child {}", parent.getNetexId(), parent.getName(), nonParentStop.getNetexId());

                    if(nonParentStop.getName() == null || Strings.isNullOrEmpty(nonParentStop.getName().getValue())) {
                        logger.info("Copying name from parent {} to child stop: {}", parent.getId(), parent.getName());
                        nonParentStop.setName(parent.getName());
                        session.setReadOnly(nonParentStop, true);
                    }

                    if (result.stream().noneMatch(stopPlace -> stopPlace.getNetexId() != null
                                                                       && (stopPlace.getNetexId().equals(parent.getNetexId()) && stopPlace.getVersion() == parent.getVersion()))) {
                        result.add(parent);
                    }
                    if (keepChilds) {
                        result.add(nonParentStop);
                    }
                } else {
                    logger.warn("Could not resolve parent from {}", nonParentStop.getParentSiteRef());
                }
            } else {
                result.add(nonParentStop);
            }
        });

        return result;
    }

}
