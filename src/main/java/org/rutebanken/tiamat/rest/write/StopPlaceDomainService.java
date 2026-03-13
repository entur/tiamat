package org.rutebanken.tiamat.rest.write;

import org.rutebanken.tiamat.diff.TiamatObjectDiffer;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.rest.validation.StopPlaceMutationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StopPlaceDomainService {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceDomainService.class);
    private final StopPlaceMutationValidator validator;
    private final StopPlaceService stopPlaceService;

    private final TiamatObjectDiffer differ;
    private final MutateLock mutateLock;

    public StopPlaceDomainService(
            StopPlaceMutationValidator validator,
            StopPlaceService stopPlaceService,
            TiamatObjectDiffer differ,
            MutateLock mutateLock
    ) {
        this.validator = validator;
        this.stopPlaceService = stopPlaceService;
        this.differ = differ;
        this.mutateLock = mutateLock;
    }

    public StopPlace getStopPlace(String stopPlaceId) {
        return stopPlaceService.getStopPlace(stopPlaceId);
    }

    public StopPlace createStopPlace(StopPlace stopPlace) {
        validator.validateStopPlaceName(stopPlace);
        return mutateLock.executeInLock(() -> stopPlaceService.createStopPlace(stopPlace));
    }

    @Transactional
    public StopPlace updateStopPlace(StopPlace stopPlace) throws IllegalAccessException {
        var existingStopPlace = validator.validateStopPlaceUpdate(
                stopPlace.getNetexId(),
                false
        );

        var diffResult = differ.compareObjects(existingStopPlace, stopPlace);
        differ.logDifference(existingStopPlace, stopPlace);
        if (diffResult.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format(
                            "No changes detected for StopPlace with id %s",
                            stopPlace.getNetexId()
                    )
            );
        }
        // TODO: there are differences when the stored stopplace has empty lists and the incoming stopplace has
        //  null values for those lists, and vica versa. perhaps we should ignore those differences?
        logger.info(
                "Differences detected for StopPlace id {}: {}",
                stopPlace.getNetexId(),
                diffResult
        );
        validator.validateStopPlaceName(stopPlace);
        return mutateLock.executeInLock(() -> stopPlaceService.updateStopPlace(existingStopPlace, stopPlace));
    }

    public void deleteStopPlace(String stopPlaceId) {
        stopPlaceService.deleteStopPlace(stopPlaceId);
    }
}
