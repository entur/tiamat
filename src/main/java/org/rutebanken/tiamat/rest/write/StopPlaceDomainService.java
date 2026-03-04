package org.rutebanken.tiamat.rest.write;

import jakarta.transaction.Transactional;
import org.rutebanken.tiamat.diff.TiamatObjectDiffer;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.rest.validation.StopPlaceMutationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StopPlaceDomainService {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceDomainService.class);
    private final StopPlaceMutationValidator validator;
    private final StopPlaceService stopPlaceService;

    private final TiamatObjectDiffer differ;

    public StopPlaceDomainService(
            StopPlaceMutationValidator validator,
            StopPlaceService stopPlaceService,
            TiamatObjectDiffer differ
    ) {
        this.validator = validator;
        this.stopPlaceService = stopPlaceService;
        this.differ = differ;
    }

    public StopPlace getStopPlace(String stopPlaceId) {
        return stopPlaceService.getStopPlace(stopPlaceId);
    }

    public StopPlace createStopPlace(StopPlace stopPlace) {
        validator.validateStopPlaceName(stopPlace);
        return stopPlaceService.createStopPlace(stopPlace);
    }

    @Transactional
    public StopPlace updateStopPlace(StopPlace stopPlace) throws IllegalAccessException {
        var existingStopPlace = validator.validateStopPlaceUpdate(
                stopPlace.getNetexId(),
                false
        );

        var diffResult = differ.compareObjects(existingStopPlace, stopPlace);
        if (diffResult.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format(
                            "No changes detected for StopPlace with id %s",
                            stopPlace.getNetexId()
                    )
            );
        }
        logger.debug(
                "Differences detected for StopPlace id {}: {}",
                stopPlace.getNetexId(),
                diffResult
        );
        validator.validateStopPlaceName(stopPlace);
        return stopPlaceService.updateStopPlace(existingStopPlace, stopPlace);
    }

    public void deleteStopPlace(String stopPlaceId) {
        stopPlaceService.deleteStopPlace(stopPlaceId);
    }
}
