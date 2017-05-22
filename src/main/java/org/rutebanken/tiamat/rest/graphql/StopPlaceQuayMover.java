package org.rutebanken.tiamat.rest.graphql;

import org.apache.commons.lang3.tuple.Pair;
import org.rutebanken.tiamat.geo.CentroidComputer;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Transactional
@Component
public class StopPlaceQuayMover {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceQuayMover.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private CentroidComputer centroidComputer;

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    public StopPlace moveQuays(List<String> quayIds, String destinationStopPlaceId) {

        Set<StopPlace> sourceStopPlaces = resolveSourceStopPlaces(resolveQuays(quayIds));

        verifySize(quayIds, sourceStopPlaces);

        StopPlace sourceStopPlace = sourceStopPlaces.iterator().next();

        logger.debug("Found stop place to move quays {} from {}", quayIds, sourceStopPlace);

        StopPlace stopPlaceToRemoveQuaysFrom = stopPlaceVersionedSaverService.createCopy(sourceStopPlace, StopPlace.class);

        Set<Quay> quaysToMove = stopPlaceToRemoveQuaysFrom.getQuays().stream().filter(quay -> quayIds.contains(quay.getNetexId())).collect(toSet());
        stopPlaceToRemoveQuaysFrom.getQuays().removeIf(quay -> quaysToMove.contains(quay));
        stopPlaceToRemoveQuaysFrom = stopPlaceVersionedSaverService.saveNewVersion(sourceStopPlace, stopPlaceToRemoveQuaysFrom);
        logger.debug("Saved stop place without quays {} {}", quayIds, stopPlaceToRemoveQuaysFrom);

        // Old and new version of destination
        Pair<StopPlace, StopPlace> pair = resolve(destinationStopPlaceId);
        centroidComputer.computeCentroidForStopPlace(pair.getRight());
        pair.getRight().getQuays().addAll(quaysToMove);
        StopPlace savedDestinationStopPlace = stopPlaceVersionedSaverService.saveNewVersion(pair.getLeft(), pair.getRight());

        logger.debug("Saved stop place with new quays {} {}", quayIds, savedDestinationStopPlace);

        return savedDestinationStopPlace;
    }

    /**
     * @param destinationStopPlaceId netex nsr id of stop place
     * @return Returns new stop place if destinationStopPlaceId is null. If destinationStopPlaceId is set, returns a copy of existing stop place.
     */
    private Pair<StopPlace, StopPlace> resolve(String destionationStopPlaceId) {
        if (destionationStopPlaceId == null) {
            return Pair.of(null, new StopPlace());
        }
        StopPlace existingDestinationStopPlace = resolveExistingStopPlace(destionationStopPlaceId);
        return Pair.of(existingDestinationStopPlace, stopPlaceVersionedSaverService.createCopy(existingDestinationStopPlace, StopPlace.class));
    }

    private void verifySize(List<String> quayIds, Set<StopPlace> sourceStopPlaces) {
        if (sourceStopPlaces.size() > 1) {
            throw new IllegalArgumentException("Cannot move quay(s) " + quayIds + " from different stops " + sourceStopPlaces);
        }
    }

    private StopPlace resolveExistingStopPlace(String destinationStopPlaceId) {
        StopPlace existingTargetStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(destinationStopPlaceId);
        if (existingTargetStopPlace == null) {
            throw new IllegalArgumentException("Cannot resolve target stop place from ID " + destinationStopPlaceId);
        }
        return existingTargetStopPlace;
    }

    private Set<Quay> resolveQuays(List<String> quayIds) {
        return quayIds.stream()
                .map(quayId -> quayRepository.findFirstByNetexIdOrderByVersionDesc(quayId))
                .peek(quay -> {
                    if (quay == null) {
                        throw new IllegalArgumentException("Could not resolve quays from list" + quayIds);
                    }
                })
                .collect(toSet());
    }

    private Set<StopPlace> resolveSourceStopPlaces(Set<Quay> quays) {
        return quays.stream()
                .map(stopPlaceRepository::findByQuay)
                .collect(toSet());
    }
}
