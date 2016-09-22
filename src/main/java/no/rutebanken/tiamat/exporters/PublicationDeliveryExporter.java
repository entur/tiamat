package no.rutebanken.tiamat.exporters;

import no.rutebanken.netex.model.*;
import no.rutebanken.tiamat.model.SiteFrame;
import no.rutebanken.tiamat.model.StopPlace;
import no.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure;
import no.rutebanken.tiamat.model.TopographicPlace;
import no.rutebanken.tiamat.model.TopographicPlacesInFrame_RelStructure;
import no.rutebanken.tiamat.netexmapping.NetexMapper;
import no.rutebanken.tiamat.repository.StopPlaceRepository;
import no.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import java.util.Iterator;

@Component
@Transactional
public class PublicationDeliveryExporter {
    private StopPlaceRepository stopPlaceRepository;
    private TopographicPlaceRepository topographicPlaceRepository;
    private NetexMapper netexMapper;

    @Autowired
    public PublicationDeliveryExporter(StopPlaceRepository stopPlaceRepository, TopographicPlaceRepository topographicPlaceRepository, NetexMapper netexMapper) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.netexMapper = netexMapper;
    }

    public PublicationDeliveryStructure exportAllStopPlaces() throws JAXBException {

        SiteFrame siteFrame = new SiteFrame();

        Iterable<StopPlace> iterableStopPlaces = stopPlaceRepository.findAll();

        StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new StopPlacesInFrame_RelStructure();

        iterableStopPlaces.forEach(stopPlace -> stopPlacesInFrame_relStructure.getStopPlace().add(stopPlace));

        siteFrame.setStopPlaces(stopPlacesInFrame_relStructure);

        Iterator<TopographicPlace> topographicPlaceIterable = topographicPlaceRepository.findAll().iterator();

        TopographicPlacesInFrame_RelStructure topographicPlaces = new TopographicPlacesInFrame_RelStructure();
        topographicPlaceIterable
                .forEachRemaining(topographicPlace -> topographicPlaces.getTopographicPlace().add(topographicPlace));

        siteFrame.setTopographicPlaces(topographicPlaces);

        no.rutebanken.netex.model.SiteFrame convertedSiteFrame = netexMapper.mapToNetexModel(siteFrame);

        return new PublicationDeliveryStructure()
                .withDataObjects(new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(convertedSiteFrame)));


    }
}
