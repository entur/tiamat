package org.rutebanken.tiamat.exporters;

import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.VersionFrameRefStructure;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlacesInFrame_RelStructure;
import org.rutebanken.tiamat.netexmapping.NetexMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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


    public PublicationDeliveryStructure exportStopPlaces(String query, List<String> municipalityIds, List<String> countyIds, List<org.rutebanken.tiamat.model.StopTypeEnumeration> stopPlaceTypes, Pageable pageable) {

        Page<StopPlace> stopPlaces;

        if ((query != null && !query.isEmpty()) || countyIds != null || municipalityIds != null || stopPlaceTypes != null) {
            stopPlaces = stopPlaceRepository.findStopPlace(query, municipalityIds, countyIds, stopPlaceTypes, pageable);
        } else {
            stopPlaces = stopPlaceRepository.findAllByOrderByChangedDesc(pageable);
        }
        return exportStopPlaces(stopPlaces);
    }

    public PublicationDeliveryStructure exportAllStopPlaces() throws JAXBException {
        return exportStopPlaces(stopPlaceRepository.findAll());
    }

    public PublicationDeliveryStructure exportStopPlaces(Iterable<StopPlace> iterableStopPlaces) {
        logger.info("Preparing publication delivery export");
        org.rutebanken.tiamat.model.SiteFrame siteFrame = new org.rutebanken.tiamat.model.SiteFrame();

        StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new StopPlacesInFrame_RelStructure();

        iterableStopPlaces.forEach(stopPlace -> stopPlacesInFrame_relStructure.getStopPlace().add(stopPlace));
        logger.info("Adding {} stop places", stopPlacesInFrame_relStructure.getStopPlace().size());
        siteFrame.setStopPlaces(stopPlacesInFrame_relStructure);

        Iterator<TopographicPlace> topographicPlaceIterable = topographicPlaceRepository.findAll().iterator();

        TopographicPlacesInFrame_RelStructure topographicPlaces = new TopographicPlacesInFrame_RelStructure();
        topographicPlaceIterable
                .forEachRemaining(topographicPlace -> topographicPlaces.getTopographicPlace().add(topographicPlace));
        logger.info("Adding {} topographic places", topographicPlaces.getTopographicPlace().size());
        siteFrame.setTopographicPlaces(topographicPlaces);

        logger.info("Mapping site frame to netex model");
        org.rutebanken.netex.model.SiteFrame convertedSiteFrame = netexMapper.mapToNetexModel(siteFrame);

        logger.info("Returning publication delivery");
        return new PublicationDeliveryStructure()
                .withDataObjects(new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(convertedSiteFrame)));

    }
}
