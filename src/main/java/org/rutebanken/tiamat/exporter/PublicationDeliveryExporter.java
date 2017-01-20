package org.rutebanken.tiamat.exporter;

import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlacesInFrame;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
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
import java.util.*;

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

        if(stopPlaceSearch.isEmpty()) {
            return exportPublicationDeliveryWithoutStops(stopPlaceRepository.findAllByOrderByChangedDesc(stopPlaceSearch.getPageable()));
        } else {
            return exportPublicationDeliveryWithoutStops(stopPlaceRepository.findStopPlace(stopPlaceSearch));
        }
    }

    public PublicationDeliveryStructure exportAllStopPlaces() throws JAXBException {
        return exportPublicationDeliveryWithoutStops(stopPlaceRepository.findAll());
    }

    public PublicationDeliveryStructure exportSiteFrame(SiteFrame siteFrame) {
        logger.info("Returning publication delivery");
        PublicationDeliveryStructure publicationDeliveryStructure = new PublicationDeliveryStructure()
                .withVersion("any")
                .withPublicationTimestamp(OffsetDateTime.now())
                .withParticipantRef(NetexIdMapper.NSR);


            publicationDeliveryStructure.withDataObjects(
                    new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame)));

        return publicationDeliveryStructure;
    }

    public PublicationDeliveryStructure exportPublicationDeliveryWithoutStops() {
        return exportPublicationDeliveryWithoutStops(null);
    }

    public PublicationDeliveryStructure exportPublicationDeliveryWithoutStops(Iterable<StopPlace> iterableStopPlaces) {
        logger.info("Preparing publication delivery export");
        org.rutebanken.tiamat.model.SiteFrame siteFrame = new org.rutebanken.tiamat.model.SiteFrame();
        siteFrame.setCreated(ZonedDateTime.now());
        siteFrame.setVersion("any");
        siteFrame.setId(1L);

        StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new StopPlacesInFrame_RelStructure();

        if(iterableStopPlaces != null) {
            iterableStopPlaces.forEach(stopPlace -> stopPlacesInFrame_relStructure.getStopPlace().add(stopPlace));
            logger.info("Adding {} stop places", stopPlacesInFrame_relStructure.getStopPlace().size());
            siteFrame.setStopPlaces(stopPlacesInFrame_relStructure);
            if(siteFrame.getStopPlaces().getStopPlace().isEmpty()) {
                siteFrame.setStopPlaces(null);
            }
        }

        List<TopographicPlace> allTopographicPlaces = topographicPlaceRepository.findAll();
        if(!allTopographicPlaces.isEmpty()) {
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
