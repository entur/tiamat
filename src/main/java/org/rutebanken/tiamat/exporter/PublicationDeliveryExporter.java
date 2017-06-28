package org.rutebanken.tiamat.exporter;

import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

@Component
@Transactional
public class PublicationDeliveryExporter {
    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryExporter.class);
    private StopPlaceRepository stopPlaceRepository;
    private NetexMapper netexMapper;
    private final TiamatSiteFrameExporter tiamatSiteFrameExporter;
    private TopographicPlacesExporter topographicPlacesExporter;

    @Autowired
    public PublicationDeliveryExporter(StopPlaceRepository stopPlaceRepository,
                                       NetexMapper netexMapper, TiamatSiteFrameExporter tiamatSiteFrameExporter, TopographicPlacesExporter topographicPlacesExporter) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.netexMapper = netexMapper;
        this.tiamatSiteFrameExporter = tiamatSiteFrameExporter;
        this.topographicPlacesExporter = topographicPlacesExporter;
    }

    public PublicationDeliveryStructure exportStopPlaces(ExportParams exportParams) {
        if (exportParams.getStopPlaceSearch().isEmpty()) {
            return exportPublicationDeliveryWithStops(stopPlaceRepository.findAllByOrderByChangedDesc(exportParams.getStopPlaceSearch().getPageable()), exportParams.getTopopgraphicPlaceExportMode());
        } else {
            return exportPublicationDeliveryWithStops(stopPlaceRepository.findStopPlace(exportParams), exportParams.getTopopgraphicPlaceExportMode());
        }
    }

    public PublicationDeliveryStructurePage exportStopPlacesWithEffectiveChangeInPeriod(ChangedStopPlaceSearch search, ExportParams.ExportMode includeTopographicPlaces) {
        Page<StopPlace> stopPlacePage = stopPlaceRepository.findStopPlacesWithEffectiveChangeInPeriod(search);
        return new PublicationDeliveryStructurePage(exportPublicationDeliveryWithStops(stopPlacePage, includeTopographicPlaces), stopPlacePage.getTotalElements(), stopPlacePage.hasNext());
    }

    public PublicationDeliveryStructure createPublicationDelivery() {
        PublicationDeliveryStructure publicationDeliveryStructure = new PublicationDeliveryStructure()
                .withVersion(ANY_VERSION)
                .withPublicationTimestamp(OffsetDateTime.now())
                .withParticipantRef(NetexIdHelper.NSR);
        return publicationDeliveryStructure;
    }

    @SuppressWarnings("unchecked")
    public PublicationDeliveryStructure createPublicationDelivery(org.rutebanken.netex.model.SiteFrame siteFrame) {
        PublicationDeliveryStructure publicationDeliveryStructure = createPublicationDelivery();
        publicationDeliveryStructure.withDataObjects(
                new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame)));

        logger.info("Returning publication delivery {} with site frame", publicationDeliveryStructure);
        return publicationDeliveryStructure;
    }

    public PublicationDeliveryStructure exportPublicationDeliveryWithStops(Iterable<StopPlace> iterableStopPlaces, ExportParams.ExportMode topographicPlaceExportMode) {
        logger.info("Preparing publication delivery export");
        org.rutebanken.tiamat.model.SiteFrame siteFrame = tiamatSiteFrameExporter.createTiamatSiteFrame("Site frame with stops");
        tiamatSiteFrameExporter.addStopsToTiamatSiteFrame(siteFrame, iterableStopPlaces);
        topographicPlacesExporter.addTopographicPlacesToTiamatSiteFrame(topographicPlaceExportMode, siteFrame);
        tiamatSiteFrameExporter.addTariffZones(siteFrame);

        logger.info("Mapping site frame to netex model");
        org.rutebanken.netex.model.SiteFrame convertedSiteFrame = netexMapper.mapToNetexModel(siteFrame);

        if (ExportParams.ExportMode.NONE.equals(topographicPlaceExportMode)){
            removeVersionFromTopographicPlaceReferences(convertedSiteFrame);
        }

        return createPublicationDelivery(convertedSiteFrame);
    }

    private void removeVersionFromTopographicPlaceReferences(org.rutebanken.netex.model.SiteFrame convertedSiteFrame) {
        if (convertedSiteFrame.getStopPlaces() != null) {
            convertedSiteFrame.getStopPlaces().getStopPlace()
                    .stream()
                    .filter(sp -> sp.getTopographicPlaceRef() != null)
                    .forEach(sp -> sp.getTopographicPlaceRef().setVersion(null));
        }
    }

}
