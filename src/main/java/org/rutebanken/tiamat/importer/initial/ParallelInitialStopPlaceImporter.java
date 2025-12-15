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

package org.rutebanken.tiamat.importer.initial;

import org.rutebanken.tiamat.importer.StopPlaceTopographicPlaceReferenceUpdater;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.concurrent.ConcurrentHashMap.newKeySet;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;

@Component
@Transactional
public class ParallelInitialStopPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(ParallelInitialStopPlaceImporter.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceTopographicPlaceReferenceUpdater stopPlaceTopographicPlaceReferenceUpdater;

    @Autowired
    private NetexMapper netexMapper;

    @Value("${changelog.publish.enabled:false}")
    private boolean publishChangelog;

    @Autowired
    private StopPlaceParentCreator parentStopPlaceCreator;

    public List<org.rutebanken.netex.model.StopPlace> importStopPlaces(List<StopPlace> tiamatStops, AtomicInteger stopPlacesCreated) {
        Map<String, Set<String>> parentRefToChildIds = new ConcurrentHashMap<>();
        Set<StopPlace> parentStopPlaces = ConcurrentHashMap.newKeySet();

        if (publishChangelog) {
            throw new IllegalStateException("Initial import not allowed with changelog publishing enabled! Set changelog.publish.enabled=false");
        }

        List<StopPlace> stops = tiamatStops.parallelStream()
                .map(stopPlace -> {
                    if (stopPlace.getTariffZones() != null) {
                        stopPlace.getTariffZones().forEach(tariffZoneRef -> tariffZoneRef.setVersion(null));
                    }
                    return stopPlace;
                })
                .peek(stopPlace -> stopPlaceTopographicPlaceReferenceUpdater.updateTopographicReference(stopPlace))
                .flatMap(stopPlace -> processStopPlace(stopPlace, parentStopPlaces, parentRefToChildIds, stopPlacesCreated))
                .toList();

        List<StopPlace> parents = createAndSaveParentStopPlaces(parentStopPlaces, parentRefToChildIds, stopPlacesCreated);

        return concat(stops.stream(), parents.stream())
                .map(netexMapper::mapToNetexModel)
                .toList();
    }

    private boolean isParent(StopPlace stopPlace, SiteRefStructure parentSiteRef) {
        return stopPlace.isParentStopPlace() || (!hasQuays(stopPlace) && parentSiteRef == null);
    }

    private boolean isChild(StopPlace stopPlace, SiteRefStructure parentSiteRef) {
        return hasQuays(stopPlace) && parentSiteRef != null;
    }

    private boolean hasQuays(StopPlace stopPlace) {
        return stopPlace.getQuays() != null && !stopPlace.getQuays().isEmpty();
    }

    private Set<String> getNetexIdOrOriginalIds(StopPlace stopPlace) {
        return stopPlace.getNetexId() != null ? Set.of(stopPlace.getNetexId()) : stopPlace.getOriginalIds();
    }

    /**
     * Processes a stop place during initial import, classifying and handling it appropriately.
     *
     * <p>Implements a deferred processing strategy for parent-child relationships:</p>
     * <ul>
     *   <li>Parent stop places are deferred until all child stop places are saved,
     *       as parents need to reference their children's persisted IDs</li>
     *   <li>Child and standalone stop places are saved immediately</li>
     * </ul>
     *
     * <h3>Processing Steps:</h3>
     * <ol>
     *   <li>Extract and clear the parent reference</li>
     *   <li>Classify the stop place as parent, child, or standalone</li>
     *   <li>If parent stop: add to collection and exclude from stream</li>
     *   <li>If child/standalone stop: save immediately and store parent-child relations</li>
     * </ol>
     *
     * @param stopPlace The stop place to process
     * @param parentStopPlaces Set for collecting parent stop places
     * @param parentRefToChildIds Map tracking parent-child relations
     * @param stopPlacesCreated Counter for tracking total stop places created
     * @return Stream containing the saved stop place
     */
    private Stream<StopPlace> processStopPlace(
            StopPlace stopPlace,
            Set<StopPlace> parentStopPlaces,
            Map<String, Set<String>> parentRefToChildIds,
            AtomicInteger stopPlacesCreated) {

        // Extract parent reference before clearing (needed for classification)
        SiteRefStructure parentSiteRef = stopPlace.getParentSiteRef();
        stopPlace.setParentSiteRef(null);

        // Defer parent stop places until all children are saved
        if (isParent(stopPlace, parentSiteRef)) {
            parentStopPlaces.add(stopPlace);
            return empty();
        }

        // Save child and standalone stop places
        StopPlace savedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        stopPlacesCreated.incrementAndGet();

        // Track parent-child relations for later parent stop creation
        if (isChild(stopPlace, parentSiteRef)) {
            trackChildUnderParent(savedStopPlace, parentSiteRef, parentRefToChildIds);
        }

        return of(savedStopPlace);
    }

    /**
     * Creates and persists parent stop places with their associated children.
     *
     * <p>Process parent stop places that were identified during the initial
     * processing phase. For each parent stop place:</p>
     * <ol>
     *   <li>Retrieves the parent's identifiers (NetexId or originalIds)</li>
     *   <li>Finds matching children that reference this parent</li>
     *   <li>Creates the parent stop place entity with children linked</li>
     *   <li>Persists the parent and updates the counter</li>
     * </ol>
     *
     * @param parentStopPlaces Set of parent stop places identified during processing
     * @param parentRefToChildIds Map of parent references to sets of child stop place IDs
     * @param stopPlacesCreated Counter for tracking total stop places created
     * @return List of saved parent stop places
     * @throws IllegalStateException if a parent has no matching children
     */
    private List<StopPlace> createAndSaveParentStopPlaces(
            Set<StopPlace> parentStopPlaces,
            Map<String, Set<String>> parentRefToChildIds,
            AtomicInteger stopPlacesCreated) {

        return parentStopPlaces.stream()
                .flatMap(parent -> processParentStopPlace(parent, parentRefToChildIds, stopPlacesCreated))
                .toList();
    }

    /**
     * Processes a single parent stop place by finding its children and creating parent entities.
     *
     * @param parent The parent stop place to process
     * @param parentRefToChildIds Map of parent references to child stop place IDs
     * @param stopPlacesCreated Counter for tracking created stop places
     * @return Stream of saved parent stop places
     * @throws IllegalStateException if the parent has no children associated with any of its identifiers
     */
    private Stream<StopPlace> processParentStopPlace(
            StopPlace parent,
            Map<String, Set<String>> parentRefToChildIds,
            AtomicInteger stopPlacesCreated) {

        Set<String> parentIds = getNetexIdOrOriginalIds(parent);
        verifyParentStopPlaceHasChildren(parent, parentIds, parentRefToChildIds);

        return parentIds.stream()
                .filter(parentRefToChildIds::containsKey)
                .map(parentRefId -> createAndSaveParentWithMatchingChildren(
                        parent,
                        parentRefId,
                        parentRefToChildIds,
                        stopPlacesCreated
                ));
    }

    /**
     * Creates and saves a parent stop place with its matching children.
     *
     * <p>Retrieves the children associated with the given parent reference ID,
     * creates the parent stop place entity with those children linked, and persists it
     * to the database.</p>
     *
     * @param parent The parent stop place to create
     * @param parentRefId The parent reference ID that children use to link to this parent
     * @param parentRefToChildIds Map of parent references to child stop place IDs
     * @param stopPlacesCreated Counter to increment upon successful creation
     * @return The saved parent stop place
     * @throws IllegalStateException if no children are found for the given parent reference ID
     */
    private StopPlace createAndSaveParentWithMatchingChildren(
            StopPlace parent,
            String parentRefId,
            Map<String, Set<String>> parentRefToChildIds,
            AtomicInteger stopPlacesCreated) {

        Set<String> childIds = parentRefToChildIds.get(parentRefId);
        if (childIds == null || childIds.isEmpty()) {
            throw new IllegalStateException("No children found for matching parent id: " + parentRefId);
        }

        StopPlace savedParent = parentStopPlaceCreator.createParentStopWithChildren(parent, childIds);
        stopPlacesCreated.incrementAndGet();
        return savedParent;
    }

    /**
     * Tracks the parent-child relationship between a saved child stop place and its parent.
     *
     * <p>This method populates the parentRefToChildIds map which is used later to create
     * parent stop places with their associated children. The map is thread-safe for use
     * in parallel streams.</p>
     *
     * @param savedChild The saved child stop place
     * @param parentRef The parent site reference from the child
     * @param parentRefToChildIds Map tracking which children belong to which parent
     */
    private void trackChildUnderParent(
            StopPlace savedChild,
            SiteRefStructure parentRef,
            Map<String, Set<String>> parentRefToChildIds) {

        parentRefToChildIds
                .computeIfAbsent(parentRef.getRef(), k -> newKeySet())
                .add(savedChild.getNetexId());
    }

    private void verifyParentStopPlaceHasChildren(StopPlace parent, Set<String> parentIds, Map<String, Set<String>> parentRefToChildIds) {
        boolean hasMatch = parentIds.stream().anyMatch(parentRefToChildIds::containsKey);

        if (!hasMatch) {
            throw new IllegalStateException("Invalid stop place without quays or children " + parent.getNetexId());
        }
    }
}
