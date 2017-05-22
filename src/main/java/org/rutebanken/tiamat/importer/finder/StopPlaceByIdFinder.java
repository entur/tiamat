package org.rutebanken.tiamat.importer.finder;

import com.google.common.collect.Sets;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

@Component
public class StopPlaceByIdFinder {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceByIdFinder.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private StopPlaceByQuayOriginalIdFinder stopPlaceByQuayOriginalIdFinder;

    @Autowired
    private StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder;

    private List<Function<StopPlace, Function<Boolean, Set<StopPlace>>>> findFunctionList = Arrays.asList(
            stopPlace -> hasQuays -> stopPlaceByQuayOriginalIdFinder.find(stopPlace, hasQuays),
            stopPlace -> hasQuays -> findByStopPlaceOriginalId(stopPlace),
            stopPlace -> hasQuays -> findByNetexId(stopPlace),
            stopPlace -> hasQuays -> findByQuayNetexId(stopPlace, hasQuays));

    public Set<StopPlace> findByNetexId(StopPlace incomingStopPlace) {
        if (incomingStopPlace.getNetexId() != null && NetexIdHelper.isNsrId(incomingStopPlace.getNetexId())) {
            logger.debug("Looking for stop by netex id {}", incomingStopPlace.getNetexId());
            return Sets.newHashSet(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(incomingStopPlace.getNetexId()));
        }
        return new HashSet<>(0);
    }

    public Set<StopPlace> findStopPlace(StopPlace incomingStopPlace) {
        boolean hasQuays = incomingStopPlace.getQuays() != null && !incomingStopPlace.getQuays().isEmpty();
        return findFunctionList.stream()
                .map(function -> function.apply(incomingStopPlace).apply(hasQuays))
                .filter(set -> !set.isEmpty())
                .flatMap(set -> set.stream())
                .filter(Objects::nonNull)
                .collect(toSet());
    }

    public Set<StopPlace> findByQuayNetexId(StopPlace incomingStopPlace, boolean hasQuays) {
        if (hasQuays) {
            logger.debug("Looking for stop by quay netex ID");
            return incomingStopPlace.getQuays().stream()
                    .filter(quay -> quay.getNetexId() != null && NetexIdHelper.isNsrId(quay.getNetexId()))
                    .map(quay -> quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId()))
                    .filter(quay -> quay != null)
                    .map(quay -> stopPlaceRepository.findByQuay(quay))
                    .collect(toSet());
        }
        return new HashSet<>(0);
    }



    public Set<StopPlace> findByStopPlaceOriginalId(StopPlace incomingStopPlace) {
        logger.debug("Looking for stop by stops by original id: {}", incomingStopPlace.getOriginalIds());
        return Sets.newHashSet(stopPlaceFromOriginalIdFinder.find(incomingStopPlace));
    }
}
