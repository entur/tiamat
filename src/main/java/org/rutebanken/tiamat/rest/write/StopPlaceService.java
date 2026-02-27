package org.rutebanken.tiamat.rest.write;

import jakarta.transaction.Transactional;
import org.rutebanken.tiamat.model.ModificationEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.stopplace.StopPlaceTerminator;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class StopPlaceService {

    private final StopPlaceVersionedSaverService stopPlaceVersionedSaverService;
    private final StopPlaceTerminator stopPlaceTerminator;
    private final StopPlaceRepository stopPlaceRepository;

    public StopPlaceService(
        StopPlaceVersionedSaverService stopPlaceVersionedSaverService,
        StopPlaceTerminator stopPlaceTerminator,
        StopPlaceRepository stopPlaceRepository
    ) {
        this.stopPlaceVersionedSaverService = stopPlaceVersionedSaverService;
        this.stopPlaceTerminator = stopPlaceTerminator;
        this.stopPlaceRepository = stopPlaceRepository;
    }

    public StopPlace getStopPlace(String stopPlaceId) {
        return stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);
    }

    @Transactional
    public StopPlace createStopPlace(StopPlace stopPlace) {
        return stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
    }

    @Transactional
    public StopPlace updateStopPlace(
        StopPlace existingStopPlace,
        StopPlace newStopPlace
    ) {
        return stopPlaceVersionedSaverService.saveNewVersion(
            existingStopPlace,
            newStopPlace
        );
    }

    @Transactional
    public void deleteStopPlace(String stopPlaceId) {
        stopPlaceTerminator.terminateStopPlace(
            stopPlaceId,
            Instant.now().plusSeconds(1),
            "Deleted via write API",
            ModificationEnumeration.DELETE
        );
    }
}
