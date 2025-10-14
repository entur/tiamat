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
import org.rutebanken.tiamat.importer.finder.SomethingWrapper;
import org.rutebanken.tiamat.importer.finder.StopPlaceBySomethingFinder;
import org.rutebanken.tiamat.importer.handler.StopPlaceGrouper;
import org.rutebanken.tiamat.importer.handler.StopPlaceType;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.groupingBy;

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

    @Autowired
    private StopPlaceBySomethingFinder finder;

    @Value("${changelog.publish.enabled:false}")
    private boolean publishChangelog;

    @Autowired
    private VersionCreator versionCreator;

    public List<org.rutebanken.netex.model.StopPlace> importStopPlaces(List<StopPlace> tiamatStops, AtomicInteger stopPlacesCreated) {
        if (publishChangelog) {
            throw new IllegalStateException("Initial import not allowed with changelog publishing enabled! Set changelog.publish.enabled=false");
        }

        StopPlaceGrouper groupByStopPlaceHierarchy = groupByHierarchy(tiamatStops);

        List<org.rutebanken.netex.model.StopPlace> parents = groupByStopPlaceHierarchy.parents().parallelStream()
                .peek(stopPlaceTopographicPlaceReferenceUpdater::updateTopographicReference)
                .map(stopPlace -> {
                    String externalId = stopPlace.getNetexId();
                    stopPlace.setParentStopPlace(true);
                    StopPlace saved = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
                    stopPlacesCreated.incrementAndGet();

                    String importedId = saved.getKeyValues().get("imported-id").getItems().stream().findFirst().orElse(null);

                    if (externalId == null) {
                        stopPlace.getOriginalIds().forEach(originalId -> finder.updateCache(originalId, new SomethingWrapper(saved.getNetexId(), originalId)));
                    } else {
                        finder.updateCache(stopPlace.getNetexId(), new SomethingWrapper(saved.getNetexId(), importedId));
                    }

                    return netexMapper.mapToNetexModel(saved);
                })
                .toList();

        List<org.rutebanken.netex.model.StopPlace> stops = new ArrayList<>(parents);

        List<org.rutebanken.netex.model.StopPlace> children = groupByStopPlaceHierarchy.children().parallelStream()
                .peek(stopPlaceTopographicPlaceReferenceUpdater::updateTopographicReference)
                .map(stopPlace -> {
                    StopPlace parentStopPlace = null;
                    if (stopPlace.getParentSiteRef() != null && stopPlace.getParentSiteRef().getRef() != null) {
                        String importedParentId = stopPlace.getParentSiteRef().getRef();
                        parentStopPlace = finder.findByExternalId(importedParentId);
                        String resolvedParentNetexId = parentStopPlace.getNetexId();

                        if (resolvedParentNetexId != null) {
                            SiteRefStructure parentSiteRef = new SiteRefStructure();
                            parentSiteRef.setRef(resolvedParentNetexId);
                            parentSiteRef.setVersion(stopPlace.getParentSiteRef().getVersion());
                            stopPlace.setParentSiteRef(parentSiteRef);
                        } else {
                            logger.warn("Could not resolve parent {} for child {}. Keeping original reference.",
                                    importedParentId, stopPlace.getId());
                        }
                    }

                    StopPlace saved = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

                    StopPlace updatedStopPlace = null;
                    if (parentStopPlace != null) {
                        updatedStopPlace = versionCreator.createCopy(parentStopPlace, StopPlace.class);
                        updatedStopPlace.getChildren().add(saved);
                        stopPlaceVersionedSaverService.saveNewVersion(parentStopPlace, updatedStopPlace);
                    }
                    stopPlacesCreated.incrementAndGet();
                    return netexMapper.mapToNetexModel(saved);
                })
                .toList();

        stops.addAll(children);
        return stops;
    }

    private StopPlaceGrouper groupByHierarchy(List<StopPlace> stops) {
        Map<StopPlaceType, List<StopPlace>> grouped = stops.stream().collect(groupingBy(this::determineStopPlaceType));

        return new StopPlaceGrouper(
                grouped.getOrDefault(StopPlaceType.PARENT, List.of()),
                grouped.getOrDefault(StopPlaceType.CHILD, List.of()),
                grouped.getOrDefault(StopPlaceType.MONOMODAL, List.of())
        );
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
