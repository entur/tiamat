/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

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

    private List<Function<StopPlace, Function<Boolean, List<StopPlace>>>> findFunctionList = Arrays.asList(
            stopPlace -> hasQuays -> stopPlaceByQuayOriginalIdFinder.find(stopPlace, hasQuays),
            stopPlace -> hasQuays -> findByStopPlaceOriginalId(stopPlace),
            stopPlace -> hasQuays -> findByNetexId(stopPlace),
            stopPlace -> hasQuays -> findByQuayNetexId(stopPlace, hasQuays));

    public List<StopPlace> findByNetexId(StopPlace incomingStopPlace) {
        if (incomingStopPlace.getNetexId() != null && NetexIdHelper.isNsrId(incomingStopPlace.getNetexId())) {
            logger.debug("Looking for stop by netex id {}", incomingStopPlace.getNetexId());
            return Arrays.asList(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(incomingStopPlace.getNetexId()));
        }
        return new ArrayList<>(0);
    }

    public List<StopPlace> findStopPlace(StopPlace incomingStopPlace) {
        boolean hasQuays = incomingStopPlace.getQuays() != null && !incomingStopPlace.getQuays().isEmpty();
        return findFunctionList.stream()
                .map(function -> function.apply(incomingStopPlace).apply(hasQuays))
                .filter(set -> !set.isEmpty())
                .flatMap(set -> set.stream())
                .filter(Objects::nonNull)
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(org.rutebanken.tiamat.model.StopPlace::getNetexId))), ArrayList::new));
    }

    public List<StopPlace> findByQuayNetexId(StopPlace incomingStopPlace, boolean hasQuays) {
        if (hasQuays) {
            logger.debug("Looking for stop by quay netex ID");
            return incomingStopPlace.getQuays().stream()
                    .filter(quay -> quay.getNetexId() != null && NetexIdHelper.isNsrId(quay.getNetexId()))
                    .map(quay -> quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId()))
                    .filter(quay -> quay != null)
                    .map(quay -> stopPlaceRepository.findByQuay(quay))
                    .collect(toList());
        }
        return new ArrayList<>(0);
    }



    public List<StopPlace> findByStopPlaceOriginalId(StopPlace incomingStopPlace) {
        logger.debug("Looking for stop by stops by original id: {}", incomingStopPlace.getOriginalIds());
        return stopPlaceFromOriginalIdFinder.find(incomingStopPlace);
    }
}
