package org.rutebanken.tiamat.service.stopplace;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

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

    public List<StopPlace> resolveParents(List<StopPlace> stopPlaceList, boolean keepChilds) {


        if (stopPlaceList == null) {
            return stopPlaceList;
        }

        List<StopPlace> result = stopPlaceList.stream().filter(StopPlace::isParentStopPlace).collect(toList());

        List<StopPlace> nonParentStops = stopPlaceList.stream().filter(stopPlace -> !stopPlace.isParentStopPlace()).collect(toList());

        nonParentStops.forEach(nonParentStop -> {
            if (nonParentStop.getParentSiteRef() != null) {
                // Parent stop place refs should have version. If not, let it fail.
                StopPlace parent = stopPlaceRepository.findFirstByNetexIdAndVersion(nonParentStop.getParentSiteRef().getRef(),
                        Long.parseLong(nonParentStop.getParentSiteRef().getVersion()));
                if (parent != null) {
                    logger.info("Resolved parent: {} {} from child {}", parent.getNetexId(), parent.getName(), nonParentStop.getNetexId());

                    if(result.stream().noneMatch(stopPlace -> stopPlace.getNetexId() != null
                            && (stopPlace.getNetexId().equals(parent.getNetexId()) && stopPlace.getVersion() == parent.getVersion()))) {
                        result.add(parent);
                    }
                    if(keepChilds) {
                        result.add(nonParentStop);
                    }
                } else {
                    logger.warn("Could not resolve parent from {}", nonParentStop.getParentSiteRef());
                }
            } else {
                result.add(nonParentStop);
            }
        });

        return result;
    }

}
