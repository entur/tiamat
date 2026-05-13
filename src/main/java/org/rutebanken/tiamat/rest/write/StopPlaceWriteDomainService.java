package org.rutebanken.tiamat.rest.write;

import com.google.api.client.util.Preconditions;
import org.rutebanken.tiamat.diff.TiamatObjectDiffer;
import org.rutebanken.tiamat.diff.generic.Difference;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.ModificationEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.validation.StopPlaceMutationValidator;
import org.rutebanken.tiamat.service.stopplace.StopPlaceTerminator;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class StopPlaceWriteDomainService {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceWriteDomainService.class);
    private final StopPlaceMutationValidator stopPlaceMutationValidator;
    private final StopPlaceVersionedSaverService stopPlaceVersionedSaverService;
    private final StopPlaceTerminator stopPlaceTerminator;
    private final StopPlaceRepository stopPlaceRepository;

    private final TiamatObjectDiffer differ;
    private final MutateLock mutateLock;
    private final VersionCreator versionCreator;
    private final NetexMapper netexMapper;
    private final NetexIdMapper netexIdMapper;

    public StopPlaceWriteDomainService(
            StopPlaceMutationValidator stopPlaceMutationValidator,
            StopPlaceVersionedSaverService stopPlaceVersionedSaverService,
            StopPlaceTerminator stopPlaceTerminator,
            StopPlaceRepository stopPlaceRepository,
            TiamatObjectDiffer differ,
            MutateLock mutateLock,
            VersionCreator versionCreator,
            NetexMapper netexMapper,
            NetexIdMapper netexIdMapper
    ) {
        this.stopPlaceMutationValidator = stopPlaceMutationValidator;
        this.stopPlaceVersionedSaverService = stopPlaceVersionedSaverService;
        this.stopPlaceTerminator = stopPlaceTerminator;
        this.stopPlaceRepository = stopPlaceRepository;
        this.differ = differ;
        this.mutateLock = mutateLock;
        this.versionCreator = versionCreator;
        this.netexMapper = netexMapper;
        this.netexIdMapper = netexIdMapper;
    }

    public StopPlace getStopPlace(String stopPlaceId) {
        return stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);
    }

    @Transactional
    public StopPlace createStopPlace(org.rutebanken.netex.model.StopPlace newStopPlace) {
        var tiamatStopPlace = new StopPlace();
        var cleanStopPlace = versionCreator.createCopy(tiamatStopPlace, StopPlace.class);
        netexMapper.getFacade().map(newStopPlace, cleanStopPlace);

        if (newStopPlace.getId() != null) {
            netexIdMapper.moveOriginalIdToKeyValueList(cleanStopPlace, newStopPlace.getId());
            cleanStopPlace.setNetexId(null);
        }

        cleanStopPlace.setValidBetween(null);
        stopPlaceMutationValidator.validateStopPlaceName(cleanStopPlace);
        return mutateLock.executeInLock(() -> stopPlaceVersionedSaverService.saveNewVersion(cleanStopPlace));
    }

    @Transactional
    public StopPlace updateStopPlace(org.rutebanken.netex.model.StopPlace newStopPlace) {
        return mutateLock.executeInLock(() -> {
            var existingStopPlace = stopPlaceMutationValidator.validateStopPlaceUpdate(
                    newStopPlace.getId(),
                    false
            );
            var updatedStopPlace = versionCreator.createCopy(existingStopPlace, StopPlace.class);

            netexMapper.getFacade().map(newStopPlace, updatedStopPlace);

            validateStopPlaceUpdate(existingStopPlace, updatedStopPlace);

            if (updatedStopPlace.getQuays() != null && !updatedStopPlace.getQuays().isEmpty()) {
                for (var quay : updatedStopPlace.getQuays()) {
                    if (quay.getNetexId() != null) {
                        var existingQuay = existingStopPlace.getQuays().stream()
                                .filter(q -> q.getNetexId().equals(quay.getNetexId()))
                                .findFirst();
                        Preconditions.checkArgument(existingQuay.isPresent(),
                                "Attempting to update Quay [id = %s] on StopPlace [id = %s] , but Quay does not exist on StopPlace.",
                                quay.getNetexId(),
                                existingStopPlace.getNetexId());

                        quay.setChanged(Instant.now());
                        // mapper maps version from user input
                        quay.setVersion(existingQuay.get().getVersion());
                    }
                }
            }

            return stopPlaceVersionedSaverService.saveNewVersion(
                    existingStopPlace,
                    updatedStopPlace,
                    Set.of() // currently only mono-modal stops are supported
            );
        });
    }


    private void validateStopPlaceUpdate(StopPlace existingStopPlace, StopPlace newStopPlace) throws IllegalArgumentException {
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
