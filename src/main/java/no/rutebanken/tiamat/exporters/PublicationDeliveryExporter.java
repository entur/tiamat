package no.rutebanken.tiamat.exporters;

import no.rutebanken.netex.model.*;
import no.rutebanken.tiamat.model.*;
import no.rutebanken.tiamat.model.SiteFrame;
import no.rutebanken.tiamat.model.StopPlace;
import no.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure;
import no.rutebanken.tiamat.model.TopographicPlace;
import no.rutebanken.tiamat.model.TopographicPlacesInFrame_RelStructure;
import no.rutebanken.tiamat.netexmapping.NetexMapper;
import no.rutebanken.tiamat.repository.StopPlaceRepository;
import no.rutebanken.tiamat.repository.TopographicPlaceRepository;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.StringWriter;
import java.util.Iterator;

public class PublicationDeliveryExporter {


    private StopPlaceRepository stopPlaceRepository;
    private TopographicPlaceRepository topographicPlaceRepository;
    private NetexMapper netexMapper;

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
