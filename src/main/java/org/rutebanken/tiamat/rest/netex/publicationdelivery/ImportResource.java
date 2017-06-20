package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.importer.PublicationDeliveryImporter;
import org.rutebanken.tiamat.importer.PublicationDeliveryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Import publication deliveries
 */
@Component
@Produces("application/xml")
@Path("/publication_delivery")
public class ImportResource {

    private static final Logger logger = LoggerFactory.getLogger(ImportResource.class);

    private final PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller;

    private final PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput;

    private final PublicationDeliveryImporter publicationDeliveryImporter;

    @Autowired
    public ImportResource(PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller,
                          PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput,
                          PublicationDeliveryImporter publicationDeliveryImporter) {

        this.publicationDeliveryUnmarshaller = publicationDeliveryUnmarshaller;
        this.publicationDeliveryStreamingOutput = publicationDeliveryStreamingOutput;
        this.publicationDeliveryImporter = publicationDeliveryImporter;
    }

    public Response importPublicationDelivery(InputStream inputStream) throws IOException, JAXBException, SAXException {
        return importPublicationDelivery(inputStream, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response importPublicationDelivery(InputStream inputStream, @BeanParam PublicationDeliveryParams publicationDeliveryParams) throws IOException, JAXBException, SAXException {
        logger.info("Received Netex publication delivery, starting to parse...");

        PublicationDeliveryStructure incomingPublicationDelivery = publicationDeliveryUnmarshaller.unmarshal(inputStream);
        try {
            PublicationDeliveryStructure responsePublicationDelivery = publicationDeliveryImporter.importPublicationDelivery(incomingPublicationDelivery, publicationDeliveryParams);
            if (publicationDeliveryParams != null && publicationDeliveryParams.skipOutput) {
                return Response.ok().build();
            } else {
                return Response.ok(publicationDeliveryStreamingOutput.stream(responsePublicationDelivery)).build();
            }
        } catch (Exception e) {
            logger.error("Caught exception while importing publication delivery: " + incomingPublicationDelivery, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Caught exception while import publication delivery: " + e.getMessage()).build();
        }
    }

}
