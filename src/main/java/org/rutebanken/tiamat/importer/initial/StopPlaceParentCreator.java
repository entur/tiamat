package org.rutebanken.tiamat.importer.initial;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class StopPlaceParentCreator {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceParentCreator.class);

    private final StopPlaceRepository stopPlaceRepository;
    private final StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    public StopPlaceParentCreator(StopPlaceRepository stopPlaceRepository,
                                  StopPlaceVersionedSaverService stopPlaceVersionedSaverService
    ) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.stopPlaceVersionedSaverService = stopPlaceVersionedSaverService;
    }

    public StopPlace createParentStopWithChildren(StopPlace stopPlace, Set<String> childIds) {
        List<StopPlace> children = childIds.stream()
                .map(stopPlaceRepository::findFirstByNetexIdOrderByVersionDesc)
                .filter(Objects::nonNull)
                .toList();
        stopPlace.setParentStopPlace(true);
        stopPlace.getChildren().addAll(children);

        logger.info("Create parent stop place with name {} and child stop places {}", stopPlace.getName(), childIds);
        return stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
    }
}
