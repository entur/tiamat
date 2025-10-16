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

package org.rutebanken.tiamat.importer.initial;

import org.rutebanken.tiamat.importer.StopPlaceTopographicPlaceReferenceUpdater;
import org.rutebanken.tiamat.importer.handler.StopPlaceType;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

@Component
@Transactional
public class ParallelInitialStopPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(ParallelInitialStopPlaceImporter.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceTopographicPlaceReferenceUpdater stopPlaceTopographicPlaceReferenceUpdater;

    @Autowired
    private NetexMapper netexMapper;

    @Value("${changelog.publish.enabled:false}")
    private boolean publishChangelog;

    @Autowired
    private StopPlaceRepository stopPlaceRepository; // (fixme) access this through cache??

    public List<org.rutebanken.netex.model.StopPlace> importStopPlaces(List<StopPlace> tiamatStops, AtomicInteger stopPlacesCreated) {
        Map<String, Set<String>> parentsByChild = new HashMap<>();
        Map<String, String> originalIdByNetexId = new HashMap<>();

        if (publishChangelog) {
            throw new IllegalStateException("Initial import not allowed with changelog publishing enabled! Set changelog.publish.enabled=false");
        }

        List<org.rutebanken.netex.model.StopPlace> stops = tiamatStops.parallelStream()
                .map(stopPlace -> {

                    if (stopPlace.getTariffZones() != null) {
                        stopPlace.getTariffZones().forEach(tariffZoneRef -> tariffZoneRef.setVersion(null));
                    }

                    return stopPlace;
                })
                .peek(stopPlace -> stopPlaceTopographicPlaceReferenceUpdater.updateTopographicReference(stopPlace))
                .map(stopPlace -> {
                    StopPlaceType type = determineStopPlaceType(stopPlace);
                    SiteRefStructure parentSiteRef;
                    parentSiteRef = stopPlace.getParentSiteRef();
                    stopPlace.setParentSiteRef(null);
                    StopPlace saved = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
                    if (type == StopPlaceType.CHILD) {
                        parentsByChild
                                .computeIfAbsent(parentSiteRef.getRef(), k -> new HashSet<>())
                                .add(saved.getNetexId());
                    }
                    if (type == StopPlaceType.PARENT) {
                        // (fixme) temporary "hack" to test conceptual solution.
                        originalIdByNetexId.put(saved.getNetexId(), stopPlace.getOriginalIds().stream().filter(i -> i.contains("SAM")).findFirst().get());
                    }
                    return saved;
                })
                .peek(stopPlace -> stopPlacesCreated.incrementAndGet())
                .map(stopPlace -> netexMapper.mapToNetexModel(stopPlace))
                .collect(toList());

        originalIdByNetexId.forEach((key, value) -> {
            StopPlace parent = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(key);
            parent.setParentStopPlace(true);
            List<StopPlace> children = parentsByChild.get(value).stream().map(id -> stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(id)).toList();
            parent.getChildren().addAll(children);
            stopPlaceVersionedSaverService.saveNewVersion(null, parent);
        });

        return stops;
    }

    private StopPlaceType determineStopPlaceType(StopPlace stopPlace) {
        boolean hasQuays = stopPlace.getQuays() != null && !stopPlace.getQuays().isEmpty();

        boolean hasParentRef = stopPlace.getParentSiteRef() != null
                && stopPlace.getParentSiteRef().getRef() != null
                && !stopPlace.getParentSiteRef().getRef().isBlank();

        boolean isExplicitParent = stopPlace.isParentStopPlace();

        if (isExplicitParent || (!hasQuays && !hasParentRef)) {
            return StopPlaceType.PARENT;
        }
        if (hasQuays && hasParentRef) {
            return StopPlaceType.CHILD;
        }
        if (hasQuays) {
            return StopPlaceType.MONOMODAL;
        }

        logger.info("StopPlace {} could not be classified (quays={}, parentRef={})", stopPlace.getId(), hasQuays, hasParentRef);
        return StopPlaceType.UNKNOWN;
    }
}
