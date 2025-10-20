package org.rutebanken.tiamat.importer.initial;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class StopPlaceParentUpdater {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceParentUpdater.class);

    private final StopPlaceRepository stopPlaceRepository;
    private final StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    public StopPlaceParentUpdater(StopPlaceRepository stopPlaceRepository,
                                  StopPlaceVersionedSaverService stopPlaceVersionedSaverService
    ) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.stopPlaceVersionedSaverService = stopPlaceVersionedSaverService;
    }

    public void updateParentWithChildren(String netexId, Set<String> childIds) {
        StopPlace parent = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
        parent.setParentStopPlace(true);
        List<StopPlace> children = childIds.stream()
                .map(stopPlaceRepository::findFirstByNetexIdOrderByVersionDesc)
                .toList();
        parent.getChildren().addAll(children);
        stopPlaceVersionedSaverService.saveNewVersion(null, parent);

        logger.info("Added children with ids [{}] to parent stop place {}", childIds, parent.getNetexId());
    }
}
