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
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.ConcurrentHashMap.newKeySet;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;

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
    private StopPlaceParentCreator parentStopPlaceCreator;

    public List<org.rutebanken.netex.model.StopPlace> importStopPlaces(List<StopPlace> tiamatStops, AtomicInteger stopPlacesCreated) {
        Map<String, Set<String>> childrenByParentSiteReference = new ConcurrentHashMap<>();
        Set<StopPlace> parentStopPlaces = ConcurrentHashMap.newKeySet();

        if (publishChangelog) {
            throw new IllegalStateException("Initial import not allowed with changelog publishing enabled! Set changelog.publish.enabled=false");
        }

        List<StopPlace> stops = tiamatStops.parallelStream()
                .map(stopPlace -> {
                    if (stopPlace.getTariffZones() != null) {
                        stopPlace.getTariffZones().forEach(tariffZoneRef -> tariffZoneRef.setVersion(null));
                    }
                    return stopPlace;
                })
                .peek(stopPlace -> stopPlaceTopographicPlaceReferenceUpdater.updateTopographicReference(stopPlace))
                .flatMap(stopPlace -> {
                    SiteRefStructure parentSiteRef = stopPlace.getParentSiteRef();

                    if (isParent(stopPlace, parentSiteRef)) {
                        parentStopPlaces.add(stopPlace);
                        return empty();
                    }

                    if (isChild(stopPlace, parentSiteRef)) {
                        stopPlace.setParentSiteRef(null);
                    }

                    StopPlace saved = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
                    stopPlacesCreated.incrementAndGet();
                    if (isChild(stopPlace, parentSiteRef)) {
                        childrenByParentSiteReference
                                .computeIfAbsent(parentSiteRef.getRef(), k -> newKeySet())
                                .add(saved.getNetexId());
                    }
                    return of(saved);
                }).toList();

        List<StopPlace> parents = resolveParents(parentStopPlaces, childrenByParentSiteReference, stopPlacesCreated);

        return concat(stops.stream(), parents.stream())
                .map(netexMapper::mapToNetexModel)
                .toList();
    }

    private boolean isParent(StopPlace stopPlace, SiteRefStructure parentSiteRef) {
        return stopPlace.isParentStopPlace() || (!hasQuays(stopPlace) && parentSiteRef == null);
    }

    private boolean isChild(StopPlace stopPlace, SiteRefStructure parentSiteRef) {
        return hasQuays(stopPlace) && parentSiteRef != null;
    }

    private boolean hasQuays(StopPlace stopPlace) {
        return stopPlace.getQuays() != null && !stopPlace.getQuays().isEmpty();
    }

    private Set<String> getParentNetexIdOrOriginalIds(StopPlace parent) {
        return parent.getNetexId() != null ? Set.of(parent.getNetexId()) : parent.getOriginalIds();
    }

    private List<StopPlace> resolveParents(Set<StopPlace> parentStopPlaces, Map<String,
            Set<String>> childrenByParentSiteReference, AtomicInteger stopPlacesCreated) {
        return parentStopPlaces.stream()
                .flatMap(parent -> getParentNetexIdOrOriginalIds(parent).stream()
                        .filter(childrenByParentSiteReference::containsKey)
                        .flatMap(matchingId -> {
                            Set<String> childIds = childrenByParentSiteReference.get(matchingId);
                            if (childIds == null || childIds.isEmpty()) {
                                logger.warn("No children found for parent id {}", matchingId);
                                return empty();
                            }
                            StopPlace savedParent = parentStopPlaceCreator.createParentStopWithChildren(parent, childIds);
                            stopPlacesCreated.incrementAndGet();
                            return of(savedParent);
                        })
                ).toList();
    }
}
