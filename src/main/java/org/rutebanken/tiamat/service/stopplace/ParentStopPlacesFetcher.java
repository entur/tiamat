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
import java.util.HashSet;
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

    public ParentStopPlacesFetcher(StopPlaceRepository stopPlaceRepository, EntityManager entityManager) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.entityManager = entityManager;
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

        if (dataLoader == null) {
            throw new IllegalArgumentException("DataLoader is required for parent resolution");
        }
        
        return resolveParentsWithDataLoader(result, nonParentStops, keepChilds, dataLoader, session);
    }

    private List<StopPlace> resolveParentsWithDataLoader(
            List<StopPlace> result,
            List<StopPlace> nonParentStops,
            boolean keepChilds,
            DataLoader<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> dataLoader,
            Session session) {

        for (StopPlace nonParentStop : nonParentStops) {
            if (nonParentStop.getParentSiteRef() != null) {
                ParentStopPlaceDataLoader.ParentStopPlaceKey key = new ParentStopPlaceDataLoader.ParentStopPlaceKey(
                        nonParentStop.getParentSiteRef().getRef(),
                        Long.parseLong(nonParentStop.getParentSiteRef().getVersion())
                );

                // Use DataLoader for batched parent lookup
                CompletableFuture<StopPlace> parentFuture = dataLoader.load(key);
                StopPlace parent = parentFuture.join(); // This will be batched by DataLoader

                if (parent != null) {
                    logger.debug("Resolved parent via DataLoader: {} {} from child {}",
                            parent.getNetexId(), parent.getName(), nonParentStop.getNetexId());

                    // Copy name from parent to child if child has no name
                    if (nonParentStop.getName() == null || Strings.isNullOrEmpty(nonParentStop.getName().getValue())) {
                        logger.debug("Copying name from parent {} to child stop: {}", parent.getId(), parent.getName());
                        nonParentStop.setName(parent.getName());
                        session.setReadOnly(nonParentStop, true);
                    }

                    // Add parent to result if not already present
                    if (result.stream().noneMatch(stopPlace -> stopPlace.getNetexId() != null
                            && (stopPlace.getNetexId().equals(parent.getNetexId()) && stopPlace.getVersion() == parent.getVersion()))) {
                        result.add(parent);
                    }

                    // Add child to result if requested
                    if (keepChilds) {
                        result.add(nonParentStop);
                    }
                } else {
                    logger.warn("Could not resolve parent via DataLoader from {}", nonParentStop.getParentSiteRef());
                    // Add child to result even if parent couldn't be resolved
                    result.add(nonParentStop);
                }
            } else {
                // No parent reference, add child directly
                result.add(nonParentStop);
            }
        }

        return result;
    }
}
