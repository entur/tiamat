package org.rutebanken.tiamat.exporter;

import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlacesInFrame;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;

import static org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

@Component
@Transactional
public class PublicationDeliveryExporter {
    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryExporter.class);
    private StopPlaceRepository stopPlaceRepository;
    private TopographicPlaceRepository topographicPlaceRepository;
    private NetexMapper netexMapper;

    @Autowired
    public PublicationDeliveryExporter(StopPlaceRepository stopPlaceRepository, TopographicPlaceRepository topographicPlaceRepository, NetexMapper netexMapper) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.netexMapper = netexMapper;
    }

    public PublicationDeliveryStructure exportStopPlaces(StopPlaceSearch stopPlaceSearch) {

        if (stopPlaceSearch.isEmpty()) {
            return exportPublicationDeliveryWithStops(stopPlaceRepository.findAllByOrderByChangedDesc(stopPlaceSearch.getPageable()));
        } else {
            return exportPublicationDeliveryWithStops(stopPlaceRepository.findStopPlace(stopPlaceSearch));
        }
    }

    public PublicationDeliveryStructure exportAllStopPlaces() throws JAXBException {
        return exportPublicationDeliveryWithStops(stopPlaceRepository.findAll());
    }

    @SuppressWarnings("unchecked")
    public PublicationDeliveryStructure exportSiteFrame(SiteFrame siteFrame) {
        logger.info("Returning publication delivery");
        PublicationDeliveryStructure publicationDeliveryStructure = new PublicationDeliveryStructure()
                .withVersion(ANY_VERSION)
                .withPublicationTimestamp(OffsetDateTime.now())
                .withParticipantRef(NetexIdHelper.NSR);

        publicationDeliveryStructure.withDataObjects(
                new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame)));

        return publicationDeliveryStructure;
    }

    public PublicationDeliveryStructure exportPublicationDeliveryWithoutStops() {
        return exportPublicationDeliveryWithStops(null);
    }

    public PublicationDeliveryStructure exportPublicationDeliveryWithStops(Iterable<StopPlace> iterableStopPlaces) {
        logger.info("Preparing publication delivery export");
        org.rutebanken.tiamat.model.SiteFrame siteFrame = new org.rutebanken.tiamat.model.SiteFrame();
        siteFrame.setCreated(ZonedDateTime.now());
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

        List<TopographicPlace> allTopographicPlaces = topographicPlaceRepository.findAll();
        if (!allTopographicPlaces.isEmpty()) {
            Iterator<TopographicPlace> topographicPlaceIterable = allTopographicPlaces.iterator();


            TopographicPlacesInFrame topographicPlaces = new TopographicPlacesInFrame();
            topographicPlaceIterable
                    .forEachRemaining(topographicPlace -> topographicPlaces.getTopographicPlace().add(topographicPlace));

            logger.info("Adding {} topographic places", topographicPlaces.getTopographicPlace().size());
            siteFrame.setTopographicPlaces(topographicPlaces);
        } else {
            logger.warn("No topographic places found to export");
            siteFrame.setTopographicPlaces(null);
        }
        logger.info("Mapping site frame to netex model");
        org.rutebanken.netex.model.SiteFrame convertedSiteFrame = netexMapper.mapToNetexModel(siteFrame);


        return exportSiteFrame(convertedSiteFrame);
    }
}
