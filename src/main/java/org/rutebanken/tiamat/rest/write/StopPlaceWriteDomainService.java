package org.rutebanken.tiamat.rest.write;

import org.rutebanken.tiamat.diff.TiamatObjectDiffer;
import org.rutebanken.tiamat.diff.generic.Difference;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.ModificationEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.validation.StopPlaceMutationValidator;
import org.rutebanken.tiamat.service.stopplace.StopPlaceTerminator;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class StopPlaceWriteDomainService {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceWriteDomainService.class);
    private final StopPlaceMutationValidator stopPlaceMutationValidator;
    private final StopPlaceVersionedSaverService stopPlaceVersionedSaverService;
    private final StopPlaceTerminator stopPlaceTerminator;
    private final StopPlaceRepository stopPlaceRepository;

    private final TiamatObjectDiffer differ;
    private final MutateLock mutateLock;

    public StopPlaceWriteDomainService(
            StopPlaceMutationValidator stopPlaceMutationValidator,
            StopPlaceVersionedSaverService stopPlaceVersionedSaverService,
            StopPlaceTerminator stopPlaceTerminator,
            StopPlaceRepository stopPlaceRepository,
            TiamatObjectDiffer differ,
            MutateLock mutateLock
    ) {
        this.stopPlaceMutationValidator = stopPlaceMutationValidator;
        this.stopPlaceVersionedSaverService = stopPlaceVersionedSaverService;
        this.stopPlaceTerminator = stopPlaceTerminator;
        this.stopPlaceRepository = stopPlaceRepository;
        this.differ = differ;
        this.mutateLock = mutateLock;
    }

    public StopPlace getStopPlace(String stopPlaceId) {
        return stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);
    }

    @Transactional
    public StopPlace createStopPlace(StopPlace stopPlace) {
        stopPlaceMutationValidator.validateStopPlaceName(stopPlace);
        return mutateLock.executeInLock(() -> stopPlaceVersionedSaverService.saveNewVersion(stopPlace));
    }

    @Transactional
    public StopPlace updateStopPlace(StopPlace newStopPlace) throws IllegalAccessException {
        return mutateLock.executeInLock(() -> {
            var existingStopPlace = stopPlaceMutationValidator.validateStopPlaceUpdate(
                    newStopPlace.getNetexId(),
                    false
            );
            validateStopPlaceUpdate(existingStopPlace, newStopPlace);

            return stopPlaceVersionedSaverService.saveNewVersion(
                    existingStopPlace,
                    newStopPlace
            );
        });
    }

    private void validateStopPlaceUpdate(StopPlace existingStopPlace, StopPlace newStopPlace) throws IllegalArgumentException {
        if (existingStopPlace.getVersion() != newStopPlace.getVersion()) {
            throw new IllegalArgumentException(
                    String.format(
                            "Version mismatch for StopPlace with id %s: expected version %s, but got %s",
                            newStopPlace.getNetexId(),
                            existingStopPlace.getVersion(),
                            newStopPlace.getVersion()
                    )
            );
        }

        stopPlaceMutationValidator.validateStopPlaceName(newStopPlace);

        List<Difference> diffResult;
        try {
            diffResult = differ.compareObjects(existingStopPlace, newStopPlace);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        differ.logDifference(existingStopPlace, newStopPlace);
        if (diffResult.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format(
                            "No changes detected for StopPlace with id %s",
                            newStopPlace.getNetexId()
                    )
            );
        }
        logger.debug(
                "Differences detected for StopPlace id {}: {}",
                newStopPlace.getNetexId(),
                diffResult
        );

    }

    @Transactional
    public void deleteStopPlace(String stopPlaceId) {
        // already uses mutateLock
        stopPlaceTerminator.terminateStopPlace(
                stopPlaceId,
                Instant.now().plusSeconds(1), // needs to be in the future to avoid warning logs
                "Deleted via write API",
                ModificationEnumeration.DELETE
        );
    }
}
