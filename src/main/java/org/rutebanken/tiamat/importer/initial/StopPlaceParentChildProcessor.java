package org.rutebanken.tiamat.importer.initial;

import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.concurrent.ConcurrentHashMap.newKeySet;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;

/**
 * Handles parent-child relationship processing during initial stop place imports.
 *
 * <p>Implements a deferred processing strategy where:</p>
 * <ul>
 *   <li>Child and standalone stop places are saved immediately during processing</li>
 *   <li>Parent stop places are deferred until all children are saved, as parents need
 *       to reference their children's persisted IDs</li>
 *   <li>Parent-child relations are tracked during processing and linked when parents are created</li>
 * </ul>
 *
 */
public class StopPlaceParentChildProcessor {

    private final StopPlaceVersionedSaverService stopPlaceVersionedSaverService;
    private final StopPlaceParentCreator parentStopPlaceCreator;

    private final Map<String, Set<String>> parentRefToChildIds = new ConcurrentHashMap<>();
    private final Set<StopPlace> parentStopPlaces = ConcurrentHashMap.newKeySet();

    public StopPlaceParentChildProcessor(
            StopPlaceVersionedSaverService stopPlaceVersionedSaverService,
            StopPlaceParentCreator parentStopPlaceCreator) {
        this.stopPlaceVersionedSaverService = stopPlaceVersionedSaverService;
        this.parentStopPlaceCreator = parentStopPlaceCreator;
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
     * @param stopPlace         The stop place to process
     * @param stopPlacesCreated Counter for tracking total stop places created
     * @return Stream containing the saved stop place
     */
    public Stream<StopPlace> processStopPlace(StopPlace stopPlace, AtomicInteger stopPlacesCreated) {
        SiteRefStructure parentSiteRef = stopPlace.getParentSiteRef();
        stopPlace.setParentSiteRef(null);

        if (isParent(stopPlace, parentSiteRef)) {
            parentStopPlaces.add(stopPlace);
            return empty();
        }

        StopPlace saved = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        stopPlacesCreated.incrementAndGet();

        if (isChild(stopPlace, parentSiteRef)) {
            trackChildUnderParent(saved, parentSiteRef);
        }

        return of(saved);
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
     * @param stopPlacesCreated Counter for tracking total stop places created
     * @return List of saved parent stop places
     * @throws IllegalStateException if a parent has no matching children
     */
    public List<StopPlace> createAndSaveParentStopPlaces(AtomicInteger stopPlacesCreated) {
        return parentStopPlaces.stream()
                .flatMap(parent -> processParentStopPlace(parent, stopPlacesCreated))
                .toList();
    }

    /**
     * Processes a single parent stop place by finding its children and creating parent entities.
     *
     * @param parent            The parent stop place to process
     * @param stopPlacesCreated Counter for tracking created stop places
     * @return Stream of saved parent stop places
     * @throws IllegalStateException if the parent has no children associated with any of its identifiers
     */
    private Stream<StopPlace> processParentStopPlace(StopPlace parent, AtomicInteger stopPlacesCreated) {
        Set<String> parentIds = getNetexIdOrOriginalIds(parent);
        verifyParentStopPlaceHasChildren(parent, parentIds);

        return parentIds.stream()
                .filter(parentRefToChildIds::containsKey)
                .map(parentRefId -> createAndSaveParentWithMatchingChildren(
                        parent,
                        parentRefId,
                        stopPlacesCreated
                ));
    }

    /**
     * Creates and saves a parent stop place with its matching children.
     *
     * <p>Retrieves the children associated with the given parent reference ID,
     * creates the parent stop place with those children linked, and persists it
     * to the database.</p>
     *
     * @param parent            The parent stop place to create
     * @param parentRefId       The parent reference ID that children use to link to this parent
     * @param stopPlacesCreated Counter to increment upon successful creation
     * @return The saved parent stop place
     * @throws IllegalStateException if no children are found for the given parent reference ID
     */
    private StopPlace createAndSaveParentWithMatchingChildren(
            StopPlace parent,
            String parentRefId,
            AtomicInteger stopPlacesCreated) {

        Set<String> childIds = parentRefToChildIds.get(parentRefId);
        if (childIds == null || childIds.isEmpty()) {
            throw new IllegalStateException("No children found for matching parent id: " + parentRefId);
        }

        StopPlace savedParent = parentStopPlaceCreator.createParentStopWithChildren(parent, childIds);
        stopPlacesCreated.incrementAndGet();
        return savedParent;
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

    private void trackChildUnderParent(StopPlace savedChild, SiteRefStructure parentRef) {
        parentRefToChildIds
                .computeIfAbsent(parentRef.getRef(), k -> newKeySet())
                .add(savedChild.getNetexId());
    }

    private void verifyParentStopPlaceHasChildren(StopPlace parent, Set<String> parentIds) {
        boolean hasMatch = parentIds.stream().anyMatch(parentRefToChildIds::containsKey);

        if (!hasMatch) {
            throw new IllegalStateException("Invalid stop place without quays or children " + parent.getNetexId());
        }
    }

}
