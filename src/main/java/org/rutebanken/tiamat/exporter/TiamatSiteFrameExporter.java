package org.rutebanken.tiamat.exporter;

import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static org.rutebanken.tiamat.exporter.PublicationDeliveryExporter.ExportMode.ALL;
import static org.rutebanken.tiamat.exporter.PublicationDeliveryExporter.ExportMode.RELEVANT;

@Service
public class TiamatSiteFrameExporter {

    private static final Logger logger = LoggerFactory.getLogger(TariffZonesFromStopsExporter.class);


    private final TopographicPlaceRepository topographicPlaceRepository;

    private final TariffZoneRepository tariffZoneRepository;

    @Autowired
    public TiamatSiteFrameExporter(TopographicPlaceRepository topographicPlaceRepository, TariffZoneRepository tariffZoneRepository) {
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.tariffZoneRepository = tariffZoneRepository;
    }


    public org.rutebanken.tiamat.model.SiteFrame createTiamatSiteFrame(String description) {
        org.rutebanken.tiamat.model.SiteFrame siteFrame = new org.rutebanken.tiamat.model.SiteFrame();
        siteFrame.setDescription(new MultilingualStringEntity(description));
//        siteFrame.setCreated(Instant.now());
        siteFrame.setVersion(1L);
        siteFrame.setNetexId(NetexIdHelper.generateRandomizedNetexId(siteFrame));
        return siteFrame;
    }

    public void addStopsToTiamatSiteFrame(org.rutebanken.tiamat.model.SiteFrame siteFrame, Iterable<StopPlace> iterableStopPlaces) {
        StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new StopPlacesInFrame_RelStructure();

        if (iterableStopPlaces != null) {
            iterableStopPlaces.forEach(stopPlace -> stopPlacesInFrame_relStructure.getStopPlace().add(stopPlace));
            logger.info("Adding {} stop places", stopPlacesInFrame_relStructure.getStopPlace().size());
            siteFrame.setStopPlaces(stopPlacesInFrame_relStructure);
            if (siteFrame.getStopPlaces().getStopPlace().isEmpty()) {
                siteFrame.setStopPlaces(null);
            }
        }
    }

    public void addTopographicPlacesToTiamatSiteFrame(PublicationDeliveryExporter.ExportMode topographicPlaceExportMode, org.rutebanken.tiamat.model.SiteFrame siteFrame) {
        Collection<TopographicPlace> topographicPlacesForExport = getTopographicPlacesForExport(topographicPlaceExportMode, siteFrame.getStopPlaces());

        if (!topographicPlacesForExport.isEmpty()) {
            Iterator<TopographicPlace> topographicPlaceIterable = topographicPlacesForExport.iterator();

            TopographicPlacesInFrame topographicPlaces = new TopographicPlacesInFrame();
            topographicPlaceIterable
                    .forEachRemaining(topographicPlace -> topographicPlaces.getTopographicPlace().add(topographicPlace));

            logger.info("Adding {} topographic places", topographicPlaces.getTopographicPlace().size());
            siteFrame.setTopographicPlaces(topographicPlaces);
        } else {
            siteFrame.setTopographicPlaces(null);
        }
    }

    public void addTariffZones(org.rutebanken.tiamat.model.SiteFrame siteFrame) {
        List<TariffZone> tariffZones = tariffZoneRepository.findAll();
        if (!tariffZones.isEmpty()) {
            siteFrame.setTariffZones(new TariffZonesInFrame_RelStructure(tariffZones));
            logger.info("Added {} tariffZones", tariffZones.size());
        } else {
            logger.info("No tariff zones found");
        }
    }

    private Collection<TopographicPlace> getTopographicPlacesForExport(PublicationDeliveryExporter.ExportMode topographicPlaceExportMode, StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure) {
        Collection<TopographicPlace> topographicPlacesForExport;
        if (ALL.equals(topographicPlaceExportMode)) {
            topographicPlacesForExport = topographicPlaceRepository.findAll();
            if (topographicPlacesForExport.isEmpty()) {
                logger.warn("No topographic places found to export");
            }
        } else if (RELEVANT.equals(topographicPlaceExportMode) && stopPlacesInFrame_relStructure != null) {
            Set<TopographicPlace> uniqueTopographicPlaces = new HashSet<>();
            for (StopPlace stopPlace : stopPlacesInFrame_relStructure.getStopPlace()) {
                gatherTopographicPlaceTree(stopPlace.getTopographicPlace(), uniqueTopographicPlaces);
            }

            topographicPlacesForExport = new HashSet<>(uniqueTopographicPlaces);
        } else {
            topographicPlacesForExport = new ArrayList<>();
        }
        return topographicPlacesForExport;
    }

    private void gatherTopographicPlaceTree(TopographicPlace topographicPlace, Set<TopographicPlace> target) {
        if (topographicPlace != null && target.add(topographicPlace)) {
            TopographicPlaceRefStructure parentRef = topographicPlace.getParentTopographicPlaceRef();
            if (parentRef != null) {
                TopographicPlace parent = topographicPlaceRepository.findFirstByNetexIdAndVersion(parentRef.getRef(), Long.valueOf(parentRef.getVersion()));
                gatherTopographicPlaceTree(parent, target);
            }

        }
    }
}
