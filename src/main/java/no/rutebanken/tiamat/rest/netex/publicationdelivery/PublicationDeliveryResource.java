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
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.*;
import java.io.*;
import java.util.Iterator;

@Component
@Produces("application/xml")
@Path("/publication_delivery")
@Transactional
public class PublicationDeliveryResource {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryResource.class);

    private SiteFrameImporter siteFrameImporter;

    private NetexMapper netexMapper;

    private final ObjectFactory objectFactory = new ObjectFactory();

    @Autowired
    public PublicationDeliveryResource(SiteFrameImporter siteFrameImporter, NetexMapper netexMapper) {

        this.siteFrameImporter = siteFrameImporter;
        this.netexMapper = netexMapper;
    }


    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response receivePublicationDelivery(InputStream inputStream) throws IOException, JAXBException {

        String responseMessage;

        JAXBContext jaxbContext = JAXBContext.newInstance(no.rutebanken.netex.model.PublicationDeliveryStructure.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        JAXBElement<PublicationDeliveryStructure> jaxbElement =
                (JAXBElement<no.rutebanken.netex.model.PublicationDeliveryStructure>) jaxbUnmarshaller.unmarshal(inputStream);
        PublicationDeliveryStructure incomingPublicationDelivery = jaxbElement.getValue();

        if(incomingPublicationDelivery.getDataObjects() == null) {
            responseMessage = "Received publication delivery but it does not contain any data objects.";
            logger.warn(responseMessage);
            throw new RuntimeException(responseMessage);
        }
        logger.info("Got publication delivery: {}", incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame().size());


        no.rutebanken.tiamat.model.SiteFrame siteFrameWithProcessedStopPlaces = incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame()
                .stream()
                .filter(element -> element.getValue() instanceof SiteFrame)
                .map(element -> netexMapper.mapToTiamatModel((SiteFrame) element.getValue()))
                .map(tiamatSiteFrame -> siteFrameImporter.importSiteFrame(tiamatSiteFrame))
                .findFirst().orElseThrow(() -> new RuntimeException("Could not return site frame with created stop places"));


        SiteFrame mappedSiteFrame = netexMapper.mapToNetexModel(siteFrameWithProcessedStopPlaces);

        PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryStructure()
                .withDataObjects(new PublicationDeliveryStructure.DataObjects()
                                        .withCompositeFrameOrCommonFrame(objectFactory.createSiteFrame(mappedSiteFrame)));


        Marshaller marshaller = jaxbContext.createMarshaller();
        StreamingOutput stream = outputStream -> {
            try {
                marshaller.marshal(objectFactory.createPublicationDelivery(publicationDelivery), outputStream);
            } catch (JAXBException e) {
                throw new RuntimeException("Could not marshal site frame", e);
            }
        };

        return Response.ok(stream).build();

    }
}

