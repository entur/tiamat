package org.rutebanken.tiamat.importer.initial;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class StopPlaceParentUpdater {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceParentUpdater.class);

    private final StopPlaceRepository stopPlaceRepository;
    private final StopPlaceVersionedSaverService stopPlaceVersionedSaverService;
    private final VersionCreator versionCreator;

    @Autowired
    public StopPlaceParentUpdater(StopPlaceRepository stopPlaceRepository,
                                  StopPlaceVersionedSaverService stopPlaceVersionedSaverService,
                                  VersionCreator versionCreator
    ) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.stopPlaceVersionedSaverService = stopPlaceVersionedSaverService;
        this.versionCreator = versionCreator;
    }

    public void updateParentWithChildren(String netexId, Set<String> childIds) {
        StopPlace parent = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
        if (parent == null) {
            logger.warn("Could not find parent stop place with netex id {}", netexId);
            return;
        }

        StopPlace parentStopPlaceCopy = versionCreator.createCopy(parent, StopPlace.class);
        parentStopPlaceCopy.setParentStopPlace(true);

        List<StopPlace> children = childIds.stream()
                .map(stopPlaceRepository::findFirstByNetexIdOrderByVersionDesc)
                .filter(Objects::nonNull)
                .toList();

        if (children.isEmpty()) {
            logger.warn("Could not find child stop place with netex id {}", netexId);
            return;
        }
        parentStopPlaceCopy.getChildren().addAll(children);

        stopPlaceVersionedSaverService.saveNewVersion(parent, parentStopPlaceCopy, Instant.now());
        logger.info("Added children with ids {} to parent stop place {}", childIds, parent.getNetexId());
    }
}
