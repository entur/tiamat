package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import jakarta.xml.bind.JAXBElement;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.exporter.PublicationDeliveryCreator;
import org.rutebanken.tiamat.exporter.PublicationDeliveryStructurePage;
import org.rutebanken.tiamat.exporter.TariffZonesFromStopsExporter;
import org.rutebanken.tiamat.exporter.TiamatSiteFrameExporter;
import org.rutebanken.tiamat.exporter.TopographicPlacesExporter;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.search.ChangedStopPlaceSearch;
import org.rutebanken.tiamat.service.stopplace.ChildStopPlacesFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toSet;

@Component
@Transactional
public class ExportStopPlacesWithEffectiveChangeInPeriod {
    private static final Logger logger = LoggerFactory.getLogger(ExportStopPlacesWithEffectiveChangeInPeriod.class);
    private final StopPlaceRepository stopPlaceRepository;
    private final ChildStopPlacesFetcher childStopPlacesFetcher;
    private final TiamatSiteFrameExporter tiamatSiteFrameExporter;
    private final TopographicPlacesExporter topographicPlacesExporter;
    private final NetexMapper netexMapper;
    private final TariffZonesFromStopsExporter tariffZonesFromStopsExporter;
    private final PublicationDeliveryCreator publicationDeliveryCreator;

    public ExportStopPlacesWithEffectiveChangeInPeriod(StopPlaceRepository stopPlaceRepository,
                                                       ChildStopPlacesFetcher childStopPlacesFetcher,
                                                       TiamatSiteFrameExporter tiamatSiteFrameExporter,
                                                       TopographicPlacesExporter topographicPlacesExporter,
                                                       NetexMapper netexMapper,
                                                       TariffZonesFromStopsExporter tariffZonesFromStopsExporter,
                                                       PublicationDeliveryCreator publicationDeliveryCreator) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.childStopPlacesFetcher = childStopPlacesFetcher;
        this.tiamatSiteFrameExporter = tiamatSiteFrameExporter;
        this.topographicPlacesExporter = topographicPlacesExporter;
        this.netexMapper = netexMapper;
        this.tariffZonesFromStopsExporter = tariffZonesFromStopsExporter;
        this.publicationDeliveryCreator = publicationDeliveryCreator;
    }

    @Transactional(readOnly = true)
    public PublicationDeliveryStructurePage export(ChangedStopPlaceSearch search, ExportParams exportParams) {
        logger.info("Finding changed stop places with search params: {}", search);
        Page<StopPlace> stopPlacePage = stopPlaceRepository.findStopPlacesWithEffectiveChangeInPeriod(search);
        logger.debug("Found {} changed stop places", stopPlacePage.getSize());

        PublicationDeliveryStructure publicationDelivery = exportPublicationDeliveryWithStops(stopPlacePage.getContent(), exportParams);

        PublicationDeliveryStructurePage publicationDeliveryStructure = new PublicationDeliveryStructurePage(
                publicationDelivery,
                stopPlacePage.getSize(),
                stopPlacePage.getTotalElements(),
                stopPlacePage.hasNext());
        logger.debug("Returning publication delivery structure: {}", publicationDeliveryStructure);
        return publicationDeliveryStructure;
    }
    /**
     *
     * @param stopPlaces
     * @param exportParams
     * @return
     */
    public PublicationDeliveryStructure exportPublicationDeliveryWithStops(List<StopPlace> stopPlaces, ExportParams exportParams) {
        logger.info("Preparing publication delivery export");
        stopPlaces = childStopPlacesFetcher.resolveChildren(stopPlaces);
        org.rutebanken.tiamat.model.SiteFrame siteFrame = tiamatSiteFrameExporter.createTiamatSiteFrame("Site frame with stops");

        tiamatSiteFrameExporter.addStopsToTiamatSiteFrame(siteFrame, stopPlaces);
        topographicPlacesExporter.addTopographicPlacesToTiamatSiteFrame(exportParams.getTopographicPlaceExportMode(), siteFrame);

        boolean relevantTariffZones = ExportParams.ExportMode.RELEVANT.equals(exportParams.getTariffZoneExportMode());

        if (!relevantTariffZones && ExportParams.ExportMode.ALL.equals(exportParams.getTariffZoneExportMode())) {
            tiamatSiteFrameExporter.addAllTariffZones(siteFrame);
        }

        Set<Long> stopPlaceIds = StreamSupport.stream(stopPlaces.spliterator(), false).map(stopPlace -> stopPlace.getId()).collect(toSet());
        tiamatSiteFrameExporter.addRelevantPathLinks(stopPlaceIds, siteFrame);

        logger.info("Mapping site frame to netex model");
        org.rutebanken.netex.model.SiteFrame convertedSiteFrame = netexMapper.mapToNetexModel(siteFrame);

        if (convertedSiteFrame.getStopPlaces() != null) {
            if (relevantTariffZones) {
                final Collection<org.rutebanken.netex.model.StopPlace> stopPlace = convertedSiteFrame.getStopPlaces().getStopPlace_().stream()
                        .map(sp -> (org.rutebanken.netex.model.StopPlace) sp.getValue())
                        .toList();
            } else if (ExportParams.ExportMode.NONE.equals(exportParams.getTariffZoneExportMode())) {
                logger.info("TariffZone export mode is NONE. Removing references from {} converted stop places", convertedSiteFrame.getStopPlaces().getStopPlace_().size());
                convertedSiteFrame.getStopPlaces().getStopPlace_().stream()
                        .map(sp -> (org.rutebanken.netex.model.StopPlace) sp.getValue())
                        .forEach(convertedStop -> convertedStop.setTariffZones(null));
            }
        }

        if (ExportParams.ExportMode.NONE.equals(exportParams.getTopographicPlaceExportMode())) {
            removeVersionFromTopographicPlaceReferences(convertedSiteFrame);
        }

        return publicationDeliveryCreator.createPublicationDelivery(convertedSiteFrame);
    }
    private void removeVersionFromTopographicPlaceReferences(org.rutebanken.netex.model.SiteFrame convertedSiteFrame) {
        if (convertedSiteFrame.getStopPlaces() != null) {
            convertedSiteFrame.getStopPlaces().getStopPlace_().stream()
                    .map(sp -> (org.rutebanken.netex.model.StopPlace) sp.getValue())
                    .filter(sp -> sp.getTopographicPlaceRef() != null)
                    .forEach(sp -> sp.getTopographicPlaceRef().setVersion(null));
        }
    }
}
