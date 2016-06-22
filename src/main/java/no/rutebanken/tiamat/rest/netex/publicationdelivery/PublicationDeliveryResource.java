package no.rutebanken.tiamat.rest.netex.publicationdelivery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import no.rutebanken.netex.model.ObjectFactory;

import no.rutebanken.netex.model.PublicationDeliveryStructure;
import no.rutebanken.netex.model.SiteFrame;
import no.rutebanken.tiamat.netexmapping.NetexMapper;
import no.rutebanken.tiamat.repository.StopPlaceRepository;
import no.rutebanken.tiamat.repository.TopographicPlaceRepository;
import no.rutebanken.tiamat.rest.netex.siteframe.SiteFrameImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

@Component
@Produces("application/xml")
@Path("/publication_delivery")
@Transactional
public class PublicationDeliveryResource {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryResource.class);

    private SiteFrameImporter siteFrameImporter;

    private NetexMapper netexMapper;

    @Autowired
    public PublicationDeliveryResource(SiteFrameImporter siteFrameImporter, NetexMapper netexMapper) {

        this.siteFrameImporter = siteFrameImporter;
        this.netexMapper = netexMapper;
    }


    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String receivePublicationDelivery(String xml) throws IOException, JAXBException {

        String responseMessage;
        logger.info("Incoming xml is {} characters long", xml.length());

        JAXBContext jaxbContext = JAXBContext.newInstance(no.rutebanken.netex.model.PublicationDeliveryStructure.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        JAXBElement<PublicationDeliveryStructure> jaxbElement =
                (JAXBElement<no.rutebanken.netex.model.PublicationDeliveryStructure>) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));
        PublicationDeliveryStructure publicationDelivery = jaxbElement.getValue();

        if(publicationDelivery.getDataObjects() == null) {
            responseMessage = "Received publication delivery but it does not contain any data objects.";
            logger.warn(responseMessage);
            return responseMessage;
        }
        logger.info("Got publication delivery: {}", publicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame().size());


        String importResponse = publicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame()
                .stream()
                .filter(element -> element.getValue() instanceof SiteFrame)
                .map(element -> {
                    SiteFrame siteFrame = (SiteFrame) element.getValue();
                    return netexMapper.mapToTiamatModel(siteFrame);
                })
                .map(tiamatSiteFrame -> siteFrameImporter.importSiteFrame(tiamatSiteFrame))
                .findFirst().orElse("Could not find SiteFrame in PublicationDeliveryStructure");

        logger.info(importResponse);
        return importResponse;
    }
}

