package org.rutebanken.tiamat.rest.write;

import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.ModificationEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.validation.StopPlaceMutationValidator;
import org.rutebanken.tiamat.rest.write.mapper.CreateStopPlaceMapper;
import org.rutebanken.tiamat.service.stopplace.StopPlaceTerminator;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Service
public class StopPlaceWriteDomainService {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceWriteDomainService.class);
    private final StopPlaceMutationValidator stopPlaceMutationValidator;
    private final StopPlaceVersionedSaverService stopPlaceVersionedSaverService;
    private final StopPlaceTerminator stopPlaceTerminator;
    private final StopPlaceRepository stopPlaceRepository;

    private final MutateLock mutateLock;
    private final VersionCreator versionCreator;
    private final NetexMapper netexMapper;
    private final CreateStopPlaceMapper createStopPlaceMapper;
    private final StopPlaceUpdater stopPlaceUpdater;

    public StopPlaceWriteDomainService(
            StopPlaceMutationValidator stopPlaceMutationValidator,
            StopPlaceVersionedSaverService stopPlaceVersionedSaverService,
            StopPlaceTerminator stopPlaceTerminator,
            StopPlaceRepository stopPlaceRepository,
            MutateLock mutateLock,
            VersionCreator versionCreator,
            NetexMapper netexMapper,
            CreateStopPlaceMapper createStopPlaceMapper,
            StopPlaceUpdater stopPlaceUpdater
    ) {
        this.stopPlaceMutationValidator = stopPlaceMutationValidator;
        this.stopPlaceVersionedSaverService = stopPlaceVersionedSaverService;
        this.stopPlaceTerminator = stopPlaceTerminator;
        this.stopPlaceRepository = stopPlaceRepository;
        this.mutateLock = mutateLock;
        this.versionCreator = versionCreator;
        this.netexMapper = netexMapper;
        this.createStopPlaceMapper = createStopPlaceMapper;
        this.stopPlaceUpdater = stopPlaceUpdater;
    }

    public StopPlace getStopPlace(String stopPlaceId) {
        return stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);
    }

    @Transactional
    public StopPlace createStopPlace(org.rutebanken.netex.model.StopPlace newStopPlace) {
        var tiamatStopPlace = netexMapper.mapToTiamatModel(newStopPlace);
        var cleanStopPlace = createStopPlaceMapper.createCopy(tiamatStopPlace, StopPlace.class);

        stopPlaceMutationValidator.validateStopPlaceMutation(cleanStopPlace);
        return mutateLock.executeInLock(() -> stopPlaceVersionedSaverService.saveNewVersion(cleanStopPlace));
    }

    @Transactional
    public StopPlace updateStopPlace(org.rutebanken.netex.model.StopPlace newStopPlace) {
        return mutateLock.executeInLock(() -> {
            var existingStopPlace = stopPlaceMutationValidator.validateStopPlaceUpdate(
                    newStopPlace.getId(),
                    false
            );
            var tiamatStop = netexMapper.mapToTiamatModel(newStopPlace);

            var updatedStopPlace = versionCreator.createCopy(existingStopPlace, StopPlace.class);
            stopPlaceUpdater.update(updatedStopPlace, tiamatStop);

            stopPlaceMutationValidator.validateStopPlaceMutation(updatedStopPlace);

            return stopPlaceVersionedSaverService.saveNewVersion(
                    existingStopPlace,
                    updatedStopPlace,
                    Set.of() // currently only mono-modal stops are supported
            );
        });
    }

    @Transactional
    public void deleteStopPlace(String stopPlaceId) {
        // already uses mutateLock
        stopPlaceTerminator.terminateStopPlace(
                stopPlaceId,
                Instant.now(),
                "Deleted via write API",
                ModificationEnumeration.DELETE
        );
    }
}
