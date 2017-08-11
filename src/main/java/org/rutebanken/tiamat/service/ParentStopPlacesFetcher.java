package org.rutebanken.tiamat.service;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

/**
 * Resolve and fetch parent stop places from a list of stops
 */
@Service
public class ParentStopPlacesFetcher {

    private static final Logger logger = LoggerFactory.getLogger(ParentStopPlacesFetcher.class);

    private final StopPlaceRepository stopPlaceRepository;

    public ParentStopPlacesFetcher(StopPlaceRepository stopPlaceRepository) {
        this.stopPlaceRepository = stopPlaceRepository;
    }

    public List<StopPlace> resolveAndReplaceWithParents(List<StopPlace> stopPlaceList) {


        if (stopPlaceList == null) {
            return stopPlaceList;
        }

        return stopPlaceList.stream()
                .map(stopPlace -> {
                    if(stopPlace.getParentSiteRef() != null) {
                        // Parent stop place refs should have version. If not, let it fail.
                        StopPlace parent = stopPlaceRepository.findFirstByNetexIdAndVersion(stopPlace.getParentSiteRef().getRef(),
                                Long.parseLong(stopPlace.getParentSiteRef().getVersion()));
                        if(parent != null) {
                            logger.info("Resolved parent: {} from child {}", parent.getNetexId(), stopPlace.getNetexId());
                            return parent;
                        }
                    }
                    logger.debug("No parent. returning child: {}", stopPlace.getNetexId());
                    return stopPlace;
                })
                .collect(
                        collectingAndThen(
                                toCollection(() -> new TreeSet<>(comparing(stopPlace -> stopPlace.getNetexId()+"-"+stopPlace.getVersion()))),
                                ArrayList::new)
                );
    }

}
