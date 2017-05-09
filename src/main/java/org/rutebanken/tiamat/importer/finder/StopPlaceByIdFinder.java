package org.rutebanken.tiamat.importer.finder;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class StopPlaceByIdFinder {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceByIdFinder.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder;

    private List<Function<StopPlace, Function<Boolean, Optional<StopPlace>>>> findFunctionList = Arrays.asList(
            stopPlace -> hasQuays -> findByQuayOriginalId(stopPlace, hasQuays),
            stopPlace -> hasQuays -> findByStopPlaceOriginalId(stopPlace),
            stopPlace -> hasQuays -> findByNetexId(stopPlace),
            stopPlace -> hasQuays -> findByQuayNetexId(stopPlace, hasQuays));

    public Optional<StopPlace> findByNetexId(StopPlace incomingStopPlace) {
        if (incomingStopPlace.getNetexId() != null && NetexIdHelper.isNsrId(incomingStopPlace.getNetexId())) {
            logger.info("Looking for stop by netex id {}", incomingStopPlace.getNetexId());
            return Optional.ofNullable(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(incomingStopPlace.getNetexId()));
        }
        return Optional.empty();
    }

    public Optional<StopPlace> findStopPlace(StopPlace incomingStopPlace) {
        boolean hasQuays = incomingStopPlace.getQuays() != null && !incomingStopPlace.getQuays().isEmpty();
        return findFunctionList.stream()
                .map(function -> function.apply(incomingStopPlace).apply(hasQuays))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    public Optional<StopPlace> findByQuayNetexId(StopPlace incomingStopPlace, boolean hasQuays) {
        if (hasQuays) {
            logger.info("Looking for stop by quay netex ID");
            return incomingStopPlace.getQuays().stream()
                    .filter(quay -> quay.getNetexId() != null && NetexIdHelper.isNsrId(quay.getNetexId()))
                    .map(quay -> quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId()))
                    .filter(quay -> quay != null)
                    .map(quay -> stopPlaceRepository.findByQuay(quay))
                    .findAny();
        }
        return Optional.empty();
    }

    public Optional<StopPlace> findByQuayOriginalId(StopPlace incomingStopPlace, boolean hasQuays) {
        if (hasQuays) {
            logger.info("Looking for stop by quay original ID");
            return incomingStopPlace.getQuays().stream()
                    .flatMap(quay -> quay.getOriginalIds().stream())
                    .map(quayOriginalId -> {
                        try {
                            // Extract last part of ID. Remove zero padding. Fall back to string ID.
                            return String.valueOf(NetexIdHelper.extractIdPostfix(quayOriginalId));
                        } catch (NumberFormatException e) {
                            return quayOriginalId;
                        }
                    })
                    .map(quayOriginalId -> stopPlaceRepository.findStopPlaceFromQuayOriginalId(quayOriginalId))
                    .filter(stopPlaceNetexIds -> stopPlaceNetexIds != null)
                    .filter(stopPlaceNetexIds -> !stopPlaceNetexIds.isEmpty())
                    .map(stopPlaceNetexIds -> stopPlaceNetexIds.get(0))
                    .map(stopPlaceNetexId -> stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId))
                    .filter(stopPlace -> stopPlace != null)
                    .findFirst();
        }
        return Optional.empty();
    }

    public Optional<StopPlace> findByStopPlaceOriginalId(StopPlace incomingStopPlace) {
        logger.info("Looking for stop by stops original id: {}", incomingStopPlace.getOriginalIds());
        return Optional.ofNullable(stopPlaceFromOriginalIdFinder.find(incomingStopPlace));
    }
}
