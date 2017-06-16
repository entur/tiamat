package org.rutebanken.tiamat.importer.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang.NotImplementedException;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.model.TopographicPlacesInFrame_RelStructure;
import org.rutebanken.tiamat.exporter.TariffZonesFromStopsExporter;
import org.rutebanken.tiamat.exporter.TopographicPlacesExporter;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.importer.PublicationDeliveryParams;
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

@Component
public class StopPlaceImportHandler {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceImportHandler.class);

    private static final Object STOP_PLACE_IMPORT_LOCK = new Object();


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


    public void handleStops(SiteFrame netexSiteFrame, PublicationDeliveryParams publicationDeliveryParams, AtomicInteger stopPlacesCreatedMatchedOrUpdated, SiteFrame responseSiteframe) {
        if (publicationDeliveryHelper.hasStops(netexSiteFrame)) {
            List<StopPlace> tiamatStops = netexMapper.mapStopsToTiamatModel(netexSiteFrame.getStopPlaces().getStopPlace());

            tiamatStops = stopPlaceTypeFilter.filter(tiamatStops, publicationDeliveryParams.allowOnlyStopTypes);

            if (publicationDeliveryParams.ignoreStopTypes != null && !publicationDeliveryParams.ignoreStopTypes.isEmpty()) {
                tiamatStops = stopPlaceTypeFilter.filter(tiamatStops, publicationDeliveryParams.ignoreStopTypes, true);
            }

            boolean isImportTypeIdMatch = publicationDeliveryParams.importType != null && publicationDeliveryParams.importType.equals(ImportType.ID_MATCH);
            if (!isImportTypeIdMatch) {
                logger.info("Running stop place pre steps");
                tiamatStops = stopPlacePreSteps.run(tiamatStops);
            }

            int numberOfStopBeforeFiltering = tiamatStops.size();
            logger.info("About to filter {} stops based on topographic references: {}", tiamatStops.size(), publicationDeliveryParams.targetTopographicPlaces);
            tiamatStops = zoneTopographicPlaceFilter.filterByTopographicPlaceMatch(publicationDeliveryParams.targetTopographicPlaces, tiamatStops);
            logger.info("Got {} stops (was {}) after filtering by: {}", tiamatStops.size(), numberOfStopBeforeFiltering, publicationDeliveryParams.targetTopographicPlaces);

            if (publicationDeliveryParams.onlyMatchOutsideTopographicPlaces != null && !publicationDeliveryParams.onlyMatchOutsideTopographicPlaces.isEmpty()) {
                numberOfStopBeforeFiltering = tiamatStops.size();
                logger.info("Filtering stops outside given list of topographic places: {}", publicationDeliveryParams.onlyMatchOutsideTopographicPlaces);
                tiamatStops = zoneTopographicPlaceFilter.filterByTopographicPlaceMatch(publicationDeliveryParams.onlyMatchOutsideTopographicPlaces, tiamatStops, true);
                logger.info("Got {} stops (was {}) after filtering", tiamatStops.size(), numberOfStopBeforeFiltering);
            }

            if (!isImportTypeIdMatch) {
                logger.info("Running stop place post filter steps");
                tiamatStops = stopPlacePostFilterSteps.run(tiamatStops);
            }

            Collection<org.rutebanken.netex.model.StopPlace> importedOrMatchedNetexStopPlaces;
            logger.info("The import type is: {}", publicationDeliveryParams.importType);

            if (publicationDeliveryParams.importType != null && publicationDeliveryParams.importType.equals(ImportType.ID_MATCH)) {
                importedOrMatchedNetexStopPlaces = stopPlaceIdMatcher.matchStopPlaces(tiamatStops, stopPlacesCreatedMatchedOrUpdated);
            } else {
                synchronized (STOP_PLACE_IMPORT_LOCK) {
                    if (publicationDeliveryParams.importType == null || publicationDeliveryParams.importType.equals(ImportType.MERGE)) {
                        importedOrMatchedNetexStopPlaces = transactionalMergingStopPlacesImporter.importStopPlaces(tiamatStops, stopPlacesCreatedMatchedOrUpdated);
                    } else if (publicationDeliveryParams.importType.equals(ImportType.INITIAL)) {
                        importedOrMatchedNetexStopPlaces = parallelInitialStopPlaceImporter.importStopPlaces(tiamatStops, stopPlacesCreatedMatchedOrUpdated);
                    } else if (publicationDeliveryParams.importType.equals(ImportType.MATCH)) {
                        importedOrMatchedNetexStopPlaces = matchingAppendingIdStopPlacesImporter.importStopPlaces(tiamatStops, stopPlacesCreatedMatchedOrUpdated);
                    } else {
                        throw new NotImplementedException("Import type " + publicationDeliveryParams.importType + " not implemented ");
                    }
                }
            }
            
    		// Filter uniques by StopPlace.id#version
            Map<String,org.rutebanken.netex.model.StopPlace> uniqueById = new HashMap<>();
            importedOrMatchedNetexStopPlaces.stream().forEach(e -> uniqueById.put(e.getId()+"#"+e.getVersion(), e));
            importedOrMatchedNetexStopPlaces =  uniqueById.values();
            
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

    public List<Pair<String, Long>> findTopographicPlaceRefsFromStops(Collection<org.rutebanken.tiamat.model.StopPlace> stopPlaces) {
        return stopPlaces
                .stream()
                .filter(stopPlace -> stopPlace.getTopographicPlace() != null)
                .map(org.rutebanken.tiamat.model.StopPlace::getTopographicPlace)
                .map(topographicPlace -> Pair.of(topographicPlace.getNetexId(), topographicPlace.getVersion()))
                .distinct()
                .collect(Collectors.toList());
    }

}
