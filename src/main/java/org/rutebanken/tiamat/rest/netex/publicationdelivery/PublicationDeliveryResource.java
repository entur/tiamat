package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import com.google.common.base.MoreObjects;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.dtoassembling.dto.StopPlaceDto;
import org.rutebanken.tiamat.exporters.PublicationDeliveryExporter;
import org.rutebanken.tiamat.importers.StopPlaceImporter;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.netexmapping.NetexMapper;
import org.rutebanken.tiamat.importers.SiteFrameImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Produces("application/xml")
@Path("/publication_delivery")
public class PublicationDeliveryResource {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryResource.class);
    public static final String IMPORT_CORRELATION_ID = "importCorrelationId";

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
        try {
            PublicationDeliveryStructure responsePublicationDelivery = importPublicationDelivery(incomingPublicationDelivery);
            return Response.ok(publicationDeliveryStreamingOutput.stream(responsePublicationDelivery)).build();
        } catch (Exception e) {
            logger.error("Caught exception while importing publication delivery: " + incomingPublicationDelivery, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Caught exception while import publication delivery: " + e.getMessage()).build();
        }
    }

    @SuppressWarnings("unchecked")
    public PublicationDeliveryStructure importPublicationDelivery(PublicationDeliveryStructure incomingPublicationDelivery) {
        if(incomingPublicationDelivery.getDataObjects() == null) {
            String responseMessage = "Received publication delivery but it does not contain any data objects.";
            logger.warn(responseMessage);
            throw new RuntimeException(responseMessage);
        }
        logger.info("Got publication delivery with {} site frames", incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame().size());

        try {
            org.rutebanken.netex.model.SiteFrame siteFrameWithProcessedStopPlaces = incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame()
                    .stream()
                    .filter(element -> element.getValue() instanceof SiteFrame)
                    .map(element -> (SiteFrame) element.getValue())
                    .peek(netexSiteFrame -> {
                        MDC.put(IMPORT_CORRELATION_ID, netexSiteFrame.getId());
                        logger.info("Publication delivery contains site frame created at ", netexSiteFrame.getCreated());
                    })
                    .map(netexSiteFrame -> netexMapper.mapToTiamatModel(netexSiteFrame))
                    .map(tiamatSiteFrame -> siteFrameImporter.importSiteFrame(tiamatSiteFrame, stopPlaceImporter))
                    .findFirst().orElseThrow(() -> new RuntimeException("Could not return site frame with created stop places"));
            return new PublicationDeliveryStructure()
                    .withDataObjects(new PublicationDeliveryStructure.DataObjects()
                            .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrameWithProcessedStopPlaces)));
        } finally {
            MDC.remove(IMPORT_CORRELATION_ID);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response exportStopPlaces(
            @DefaultValue(value = "0") @QueryParam(value = "page") int page,
            @DefaultValue(value = "20") @QueryParam(value = "size") int size,
            @QueryParam(value = "q") String query,
            @QueryParam(value = "municipalityReference") List<String> municipalityReferences,
            @QueryParam(value = "countyReference") List<String> countyReferences,
            @QueryParam(value = "stopPlaceType") List<String> stopPlaceTypes) throws JAXBException {

        List<StopTypeEnumeration> stopTypeEnums = new ArrayList<>();
        if (stopPlaceTypes != null) {
            stopPlaceTypes.forEach(string ->
                    stopTypeEnums.add(StopTypeEnumeration.fromValue(string)));
        }

        logger.info("Export publication delivery with stop places '{}'", MoreObjects.toStringHelper("Query")
                .add("municipalityReferences", municipalityReferences)
                .add("countyReference", countyReferences)
                .add("stopPlaceType", stopPlaceTypes)
                .add("q", query)
                .add("page", page)
                .add("size", size));

        Pageable pageable = new PageRequest(page, size);

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportStopPlaces(query, municipalityReferences, countyReferences, stopTypeEnums, pageable);
        return Response.ok(publicationDeliveryStreamingOutput.stream(publicationDeliveryStructure)).build();
    }
}

