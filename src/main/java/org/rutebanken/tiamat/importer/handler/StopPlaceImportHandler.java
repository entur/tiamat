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

package org.rutebanken.tiamat.importer.handler;

import com.hazelcast.core.HazelcastInstance;
import org.apache.commons.lang3.NotImplementedException;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.model.TopographicPlacesInFrame_RelStructure;
import org.rutebanken.tiamat.exporter.TariffZonesFromStopsExporter;
import org.rutebanken.tiamat.exporter.TopographicPlacesExporter;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.importer.filter.StopPlaceTypeFilter;
import org.rutebanken.tiamat.importer.filter.ZoneTopographicPlaceFilter;
import org.rutebanken.tiamat.importer.initial.ParallelInitialStopPlaceImporter;
import org.rutebanken.tiamat.importer.matching.MatchingAppendingIdStopPlacesImporter;
import org.rutebanken.tiamat.importer.matching.StopPlaceIdMatcher;
import org.rutebanken.tiamat.importer.merging.TransactionalMergingStopPlacesImporter;
import org.rutebanken.tiamat.importer.modifier.StopPlacePostFilterSteps;
import org.rutebanken.tiamat.importer.modifier.StopPlacePreSteps;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Component
public class StopPlaceImportHandler {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceImportHandler.class);

    /**
     * Hazelcast lock key for stop place import.
     */
    private static final String STOP_PLACE_IMPORT_LOCK_KEY = "STOP_PLACE_IMPORT_LOCK_KEY";

    /**
     * Make this configurable. Export topographic places in response.
     */
    private static final boolean EXPORT_TOPOGRAPHIC_PLACES_FOR_STOPS = false;

    @Autowired
    private PublicationDeliveryHelper publicationDeliveryHelper;

    @Autowired
    private NetexMapper netexMapper;

    @Autowired
    private StopPlacePreSteps stopPlacePreSteps;

    @Autowired
    private ZoneTopographicPlaceFilter zoneTopographicPlaceFilter;

    @Autowired
    private TariffZonesFromStopsExporter tariffZonesFromStopsExporter;

    @Autowired
    private StopPlaceTypeFilter stopPlaceTypeFilter;

    @Autowired
    private StopPlacePostFilterSteps stopPlacePostFilterSteps;

    @Autowired
    private StopPlaceIdMatcher stopPlaceIdMatcher;

    @Autowired
    private TransactionalMergingStopPlacesImporter transactionalMergingStopPlacesImporter;

    @Autowired
    private ParallelInitialStopPlaceImporter parallelInitialStopPlaceImporter;

    @Autowired
    private MatchingAppendingIdStopPlacesImporter matchingAppendingIdStopPlacesImporter;

    @Autowired
    private TopographicPlacesExporter topographicPlacesExporter;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    public void handleStops(SiteFrame netexSiteFrame, ImportParams importParams, AtomicInteger stopPlacesCreatedMatchedOrUpdated, SiteFrame responseSiteframe) {
        if (publicationDeliveryHelper.hasStops(netexSiteFrame)) {
            List<StopPlace> tiamatStops = netexMapper.mapStopsToTiamatModel(netexSiteFrame.getStopPlaces().getStopPlace());

            tiamatStops = stopPlaceTypeFilter.filter(tiamatStops, importParams.allowOnlyStopTypes);

            if (importParams.ignoreStopTypes != null && !importParams.ignoreStopTypes.isEmpty()) {
                tiamatStops = stopPlaceTypeFilter.filter(tiamatStops, importParams.ignoreStopTypes, true);
            }

            boolean isImportTypeIdMatch = importParams.importType != null && importParams.importType.equals(ImportType.ID_MATCH);
            if (!isImportTypeIdMatch && !importParams.disablePreAndPostProcessing) {
                logger.info("Running stop place pre steps");
                tiamatStops = stopPlacePreSteps.run(tiamatStops);
            }

            int numberOfStopBeforeFiltering = tiamatStops.size();
            logger.info("About to filter {} stops based on topographic references: {}", tiamatStops.size(), importParams.targetTopographicPlaces);
            tiamatStops = zoneTopographicPlaceFilter.filterByTopographicPlaceMatch(importParams.targetTopographicPlaces, tiamatStops);
            logger.info("Got {} stops (was {}) after filtering by: {}", tiamatStops.size(), numberOfStopBeforeFiltering, importParams.targetTopographicPlaces);

            if (importParams.onlyMatchOutsideTopographicPlaces != null && !importParams.onlyMatchOutsideTopographicPlaces.isEmpty()) {
                numberOfStopBeforeFiltering = tiamatStops.size();
                logger.info("Filtering stops outside given list of topographic places: {}", importParams.onlyMatchOutsideTopographicPlaces);
                tiamatStops = zoneTopographicPlaceFilter.filterByTopographicPlaceMatch(importParams.onlyMatchOutsideTopographicPlaces, tiamatStops, true);
                logger.info("Got {} stops (was {}) after filtering", tiamatStops.size(), numberOfStopBeforeFiltering);
            }

            if (!isImportTypeIdMatch && !importParams.disablePreAndPostProcessing) {
                logger.info("Running stop place post filter steps");
                tiamatStops = stopPlacePostFilterSteps.run(tiamatStops);
            }

            if (importParams.forceStopType != null) {
                logger.info("Forcing stop type to " + importParams.forceStopType);
                tiamatStops.forEach(stopPlace -> stopPlace.setStopPlaceType(importParams.forceStopType));
            }

            Collection<org.rutebanken.netex.model.StopPlace> importedOrMatchedNetexStopPlaces;
            logger.info("The import type is: {}", importParams.importType);

            if (importParams.importType != null && importParams.importType.equals(ImportType.ID_MATCH)) {
                importedOrMatchedNetexStopPlaces = stopPlaceIdMatcher.matchStopPlaces(tiamatStops, stopPlacesCreatedMatchedOrUpdated);
            } else {
                final Lock lock = hazelcastInstance.getCPSubsystem().getLock(STOP_PLACE_IMPORT_LOCK_KEY);
                lock.lock();
                try {
                    if (importParams.importType == null || importParams.importType.equals(ImportType.MERGE)) {
                        importedOrMatchedNetexStopPlaces = transactionalMergingStopPlacesImporter.importStopPlaces(tiamatStops, stopPlacesCreatedMatchedOrUpdated);
                    } else if (importParams.importType.equals(ImportType.INITIAL)) {
                        importedOrMatchedNetexStopPlaces = parallelInitialStopPlaceImporter.importStopPlaces(tiamatStops, stopPlacesCreatedMatchedOrUpdated);
                    } else if (importParams.importType.equals(ImportType.MATCH)) {
                        importedOrMatchedNetexStopPlaces = matchingAppendingIdStopPlacesImporter.importStopPlaces(tiamatStops, stopPlacesCreatedMatchedOrUpdated);
                    } else {
                        throw new NotImplementedException("Import type " + importParams.importType + " not implemented ");
                    }
                } finally {
                    lock.unlock();
                }
            }

            // Filter uniques by StopPlace.id#version
            Map<String, org.rutebanken.netex.model.StopPlace> uniqueById = new HashMap<>();
            importedOrMatchedNetexStopPlaces.stream().forEach(e -> uniqueById.put(e.getId() + "#" + e.getVersion(), e));
            importedOrMatchedNetexStopPlaces = uniqueById.values();

            logger.info("Imported/matched/updated {} stop places", stopPlacesCreatedMatchedOrUpdated);

            tariffZonesFromStopsExporter.resolveTariffZones(importedOrMatchedNetexStopPlaces, responseSiteframe);

            if (responseSiteframe.getTariffZones() != null
                    && responseSiteframe.getTariffZones().getTariffZone() != null
                    && responseSiteframe.getTariffZones().getTariffZone().isEmpty()) {
                responseSiteframe.setTariffZones(null);
            }

            if (EXPORT_TOPOGRAPHIC_PLACES_FOR_STOPS) {
                List<TopographicPlace> netexTopographicPlaces = topographicPlacesExporter.export(findTopographicPlaceRefsFromStops(tiamatStops));

                if (!netexTopographicPlaces.isEmpty()) {
                    responseSiteframe.withTopographicPlaces(
                            new TopographicPlacesInFrame_RelStructure()
                                    .withTopographicPlace(netexTopographicPlaces));
                }
            } else {
                clearTopographicPlaceRefs(importedOrMatchedNetexStopPlaces);
            }

            if (!importedOrMatchedNetexStopPlaces.isEmpty()) {
                logger.info("Add {} stops to response site frame", importedOrMatchedNetexStopPlaces.size());
                responseSiteframe.withStopPlaces(
                        new StopPlacesInFrame_RelStructure()
                                .withStopPlace(importedOrMatchedNetexStopPlaces));
            } else {
                logger.info("No stops in response");
            }
        }
    }

    public void clearTopographicPlaceRefs(Collection<org.rutebanken.netex.model.StopPlace> stopPlaces) {
        stopPlaces.stream().forEach(stopPlace -> stopPlace.setTopographicPlaceRef(null));
    }

    public List<Pair<String, Long>> findTopographicPlaceRefsFromStops(Collection<StopPlace> stopPlaces) {
        return stopPlaces
                .stream()
                .filter(stopPlace -> stopPlace.getTopographicPlace() != null)
                .map(StopPlace::getTopographicPlace)
                .map(topographicPlace -> Pair.of(topographicPlace.getNetexId(), topographicPlace.getVersion()))
                .distinct()
                .collect(Collectors.toList());
    }

}
