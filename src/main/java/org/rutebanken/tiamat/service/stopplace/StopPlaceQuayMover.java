package org.rutebanken.tiamat.service.stopplace;

import org.rutebanken.tiamat.geo.CentroidComputer;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.CopiedEntity;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.rutebanken.tiamat.versioning.util.StopPlaceCopyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
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

    @Autowired
    private StopPlaceCopyHelper stopPlaceCopyHelper;

    public StopPlace moveQuays(List<String> quayIds, String destinationStopPlaceId, String fromVersionComment, String toVersionComment) {

        Set<StopPlace> sourceStopPlaces = resolveSourceStopPlaces(resolveQuays(quayIds));

        verifySize(quayIds, sourceStopPlaces);

        StopPlace sourceStopPlace = sourceStopPlaces.iterator().next();

        logger.debug("Found stop place to move quays {} from {}", quayIds, sourceStopPlace);

        Set<Quay> quaysToMove = removeQuaysFromStop(sourceStopPlace, fromVersionComment, quayIds);

        StopPlace response = addQuaysToDestinationStop(destinationStopPlaceId, quaysToMove, toVersionComment);

        logger.info("Moved quays: {} from stop {} to {}", quayIds, sourceStopPlace.getNetexId(), response.getNetexId());
        return response;
    }

    private StopPlace addQuaysToDestinationStop(String destinationStopPlaceId, Set<Quay> quaysToMove, String toVersionComment) {

        StopPlace destinationStopPlace;
        if(destinationStopPlaceId == null) {
            destinationStopPlace = new StopPlace();
        } else {
          destinationStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(destinationStopPlaceId);
        }

        if(destinationStopPlace == null) {
            throw new IllegalArgumentException("Cannot resolve destination stopp place by ID " + destinationStopPlaceId);
        }

        CopiedEntity<StopPlace> destination = stopPlaceCopyHelper.createCopies(destinationStopPlace);
        StopPlace stopPlaceToAddQuaysTo = destination.getCopiedEntity();

        stopPlaceToAddQuaysTo.getQuays().addAll(quaysToMove);
        centroidComputer.computeCentroidForStopPlace(stopPlaceToAddQuaysTo);
        stopPlaceToAddQuaysTo.setVersionComment(toVersionComment);

        logger.debug("Saved stop place with new quays {} {}", quaysToMove.stream().map(q -> q.getNetexId()).collect(toList()), destinationStopPlace);

        return save(destination);
    }

    private Set<Quay> removeQuaysFromStop(StopPlace sourceStopPlace, String fromVersionComment, List<String> quayIds) {
        CopiedEntity<StopPlace> source = stopPlaceCopyHelper.createCopies(sourceStopPlace);
        StopPlace stopPlaceToRemoveQuaysFrom = source.getCopiedEntity();

        Set<Quay> quaysToMove = stopPlaceToRemoveQuaysFrom.getQuays().stream().filter(quay -> quayIds.contains(quay.getNetexId())).collect(toSet());
        stopPlaceToRemoveQuaysFrom.getQuays().removeIf(quay -> quaysToMove.contains(quay));
        stopPlaceToRemoveQuaysFrom.setVersionComment(fromVersionComment);

        save(source);
        logger.debug("Saved stop place without quays {} {}", quayIds, stopPlaceToRemoveQuaysFrom);
        return quaysToMove;
    }

    /**
     * Saves parent copy if parent exist. If not, saves monomodal stop place.
     * @param copiedEntity
     * @return
     */
    private StopPlace save(CopiedEntity<StopPlace> copiedEntity) {
        if(copiedEntity.hasParent()) {
            return stopPlaceVersionedSaverService.saveNewVersion(copiedEntity.getExistingParent(), copiedEntity.getCopiedParent());
        } else
            return stopPlaceVersionedSaverService.saveNewVersion(copiedEntity.getExistingEntity(), copiedEntity.getCopiedEntity());
    }

    private void verifySize(List<String> quayIds, Set<StopPlace> sourceStopPlaces) {
        if (sourceStopPlaces.size() > 1) {
            throw new IllegalArgumentException("Cannot move quay(s) " + quayIds + " from different stops " + sourceStopPlaces);
        }
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
