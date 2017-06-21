package org.rutebanken.tiamat.exporter;

import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

import static org.rutebanken.tiamat.exporter.PublicationDeliveryExporter.ExportMode.*;
import static org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

@Component
@Transactional
public class PublicationDeliveryExporter {
    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryExporter.class);
    private StopPlaceRepository stopPlaceRepository;
    private TopographicPlaceRepository topographicPlaceRepository;
    private TariffZoneRepository tariffZoneRepository;
    private NetexMapper netexMapper;

    public enum ExportMode {NONE, RELEVANT, ALL}

    @Autowired
    public PublicationDeliveryExporter(StopPlaceRepository stopPlaceRepository,
                                       TopographicPlaceRepository topographicPlaceRepository,
                                       TariffZoneRepository tariffZoneRepository,
                                       NetexMapper netexMapper) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.tariffZoneRepository = tariffZoneRepository;
        this.netexMapper = netexMapper;
    }

    public PublicationDeliveryStructure exportStopPlaces(StopPlaceSearch stopPlaceSearch, boolean includeTopographicPlaces) {
        ExportMode topographicPlaceExportMode = includeTopographicPlaces ? RELEVANT : NONE;
        if (stopPlaceSearch.isEmpty()) {
            return exportPublicationDeliveryWithStops(stopPlaceRepository.findAllByOrderByChangedDesc(stopPlaceSearch.getPageable()), topographicPlaceExportMode);
        } else {
            return exportPublicationDeliveryWithStops(stopPlaceRepository.findStopPlace(stopPlaceSearch), topographicPlaceExportMode);
        }
    }

    public PublicationDeliveryStructurePage exportStopPlacesWithEffectiveChangeInPeriod(ChangedStopPlaceSearch search, boolean includeTopographicPlaces) {
        ExportMode topographicPlaceExportMode = includeTopographicPlaces ? RELEVANT : NONE;
        Page<StopPlace> stopPlacePage = stopPlaceRepository.findStopPlacesWithEffectiveChangeInPeriod(search);
        return new PublicationDeliveryStructurePage(exportPublicationDeliveryWithStops(stopPlacePage, topographicPlaceExportMode), stopPlacePage.getTotalElements(), stopPlacePage.hasNext());
    }

    public PublicationDeliveryStructure exportAllStopPlaces() throws JAXBException {
        return exportPublicationDeliveryWithStops(stopPlaceRepository.findAll(), ALL);
    }

    @SuppressWarnings("unchecked")
    public PublicationDeliveryStructure exportSiteFrame(SiteFrame siteFrame) {
        PublicationDeliveryStructure publicationDeliveryStructure = new PublicationDeliveryStructure()
                .withVersion(ANY_VERSION)
                .withPublicationTimestamp(OffsetDateTime.now())
                .withParticipantRef(NetexIdHelper.NSR);


        publicationDeliveryStructure.withDataObjects(
                new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame)));

        logger.info("Returning publication delivery {} with site frame", publicationDeliveryStructure);
        return publicationDeliveryStructure;
    }

    public PublicationDeliveryStructure exportPublicationDeliveryWithoutStops() {
        return exportPublicationDeliveryWithStops(null, ALL);
    }

    public PublicationDeliveryStructure exportPublicationDeliveryWithStops(Iterable<StopPlace> iterableStopPlaces, ExportMode topographicPlaceExportMode) {
        logger.info("Preparing publication delivery export");
        org.rutebanken.tiamat.model.SiteFrame siteFrame = new org.rutebanken.tiamat.model.SiteFrame();
        siteFrame.setCreated(Instant.now());
        siteFrame.setVersion(1L);
        siteFrame.setNetexId(NetexIdHelper.generateRandomizedNetexId(siteFrame));

        StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new StopPlacesInFrame_RelStructure();

        if (iterableStopPlaces != null) {
            iterableStopPlaces.forEach(stopPlace -> stopPlacesInFrame_relStructure.getStopPlace().add(stopPlace));
            logger.info("Adding {} stop places", stopPlacesInFrame_relStructure.getStopPlace().size());
            siteFrame.setStopPlaces(stopPlacesInFrame_relStructure);
            if (siteFrame.getStopPlaces().getStopPlace().isEmpty()) {
                siteFrame.setStopPlaces(null);
            }
        }

        Collection<TopographicPlace> topographicPlacesForExport = getTopographicPlacesForExport(topographicPlaceExportMode, stopPlacesInFrame_relStructure);

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

        exportTariffZones(siteFrame);

        logger.info("Mapping site frame to netex model");
        org.rutebanken.netex.model.SiteFrame convertedSiteFrame = netexMapper.mapToNetexModel(siteFrame);

        if (NONE.equals(topographicPlaceExportMode)){
            removeVersionFromTopographicPlaceReferences(convertedSiteFrame);
        }


        return exportSiteFrame(convertedSiteFrame);
    }

    public void exportTariffZones(org.rutebanken.tiamat.model.SiteFrame siteFrame) {
        List<TariffZone> tariffZones = tariffZoneRepository.findAll();
        if (!tariffZones.isEmpty()) {
            siteFrame.setTariffZones(new TariffZonesInFrame_RelStructure(tariffZones));
            logger.info("Added {} tariffZones", tariffZones.size());
        } else {
            logger.info("No tariff zones found");
        }
    }

    private void removeVersionFromTopographicPlaceReferences(SiteFrame convertedSiteFrame) {
        if (convertedSiteFrame.getStopPlaces() != null) {
            convertedSiteFrame.getStopPlaces().getStopPlace().stream().filter(sp -> sp.getTopographicPlaceRef() != null).forEach(sp -> sp.getTopographicPlaceRef().setVersion(null));
        }
    }

    private Collection<TopographicPlace> getTopographicPlacesForExport(ExportMode topographicPlaceExportMode, StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure) {
        Collection<TopographicPlace> topographicPlacesForExport;
        if (ALL.equals(topographicPlaceExportMode)) {
            topographicPlacesForExport = topographicPlaceRepository.findAll();
            if (topographicPlacesForExport.isEmpty()) {
                logger.warn("No topographic places found to export");
            }
        } else if (RELEVANT.equals(topographicPlaceExportMode)) {
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
