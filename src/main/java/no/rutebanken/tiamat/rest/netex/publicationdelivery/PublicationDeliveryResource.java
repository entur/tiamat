package no.rutebanken.tiamat.rest.netex.publicationdelivery;

import no.rutebanken.netex.model.ObjectFactory;
import no.rutebanken.netex.model.PublicationDeliveryStructure;
import no.rutebanken.netex.model.SiteFrame;
import no.rutebanken.tiamat.exporters.PublicationDeliveryExporter;
import no.rutebanken.tiamat.importers.StopPlaceImporter;
import no.rutebanken.tiamat.netexmapping.NetexMapper;
import no.rutebanken.tiamat.importers.SiteFrameImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.*;
import java.io.IOException;
import java.io.InputStream;

@Component
@Produces("application/xml")
@Path("/publication_delivery")
@Transactional
public class PublicationDeliveryResource {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryResource.class);

    private SiteFrameImporter siteFrameImporter;

    private NetexMapper netexMapper;

    private PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller;

    private PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput;

    private StopPlaceImporter stopPlaceImporter;

    private PublicationDeliveryExporter publicationDeliveryExporter;

    @Autowired
    public PublicationDeliveryResource(SiteFrameImporter siteFrameImporter, NetexMapper netexMapper,
                                       PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller,
                                       PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput,
                                       @Qualifier("defaultStopPlaceImporter") StopPlaceImporter stopPlaceImporter,
                                       PublicationDeliveryExporter publicationDeliveryExporter) {

        this.siteFrameImporter = siteFrameImporter;
        this.netexMapper = netexMapper;
        this.publicationDeliveryUnmarshaller = publicationDeliveryUnmarshaller;
        this.publicationDeliveryStreamingOutput = publicationDeliveryStreamingOutput;
        this.stopPlaceImporter = stopPlaceImporter;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
    }


    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response receivePublicationDelivery(InputStream inputStream) throws IOException, JAXBException {
        PublicationDeliveryStructure incomingPublicationDelivery = publicationDeliveryUnmarshaller.unmarshal(inputStream);
        PublicationDeliveryStructure responsePublicationDelivery = receivePublicationDelivery(incomingPublicationDelivery);
        return Response.ok(publicationDeliveryStreamingOutput.stream(responsePublicationDelivery)).build();
    }

    public PublicationDeliveryStructure receivePublicationDelivery(PublicationDeliveryStructure incomingPublicationDelivery) {
        if(incomingPublicationDelivery.getDataObjects() == null) {
            String responseMessage = "Received publication delivery but it does not contain any data objects.";
            logger.warn(responseMessage);
            throw new RuntimeException(responseMessage);
        }
        logger.info("Got publication delivery: {}", incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame().size());

        no.rutebanken.tiamat.model.SiteFrame siteFrameWithProcessedStopPlaces = incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame()
                .stream()
                .filter(element -> element.getValue() instanceof SiteFrame)
                .map(element -> netexMapper.mapToTiamatModel((SiteFrame) element.getValue()))
                .map(tiamatSiteFrame -> siteFrameImporter.importSiteFrame(tiamatSiteFrame, stopPlaceImporter))
                .findFirst().orElseThrow(() -> new RuntimeException("Could not return site frame with created stop places"));


        SiteFrame mappedSiteFrame = netexMapper.mapToNetexModel(siteFrameWithProcessedStopPlaces);

        return new PublicationDeliveryStructure()
                .withDataObjects(new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(mappedSiteFrame)));

    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response exportAllStopPlaces() throws JAXBException {
        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportAllStopPlaces();
        return Response.ok(publicationDeliveryStreamingOutput.stream(publicationDeliveryStructure)).build();
    }
}

