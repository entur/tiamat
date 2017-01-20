package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.dtoassembling.disassembler.StopPlaceSearchDisassembler;
import org.rutebanken.tiamat.exporter.AsyncPublicationDeliveryExporter;
import org.rutebanken.tiamat.exporter.PublicationDeliveryExporter;
import org.rutebanken.tiamat.importer.SimpleStopPlaceImporter;
import org.rutebanken.tiamat.importer.SiteFrameImporter;
import org.rutebanken.tiamat.importer.StopPlaceImporter;
import org.rutebanken.tiamat.model.job.ExportJob;
import org.rutebanken.tiamat.model.job.JobStatus;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
import org.rutebanken.tiamat.rest.dto.DtoStopPlaceSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static org.rutebanken.tiamat.exporter.AsyncPublicationDeliveryExporter.ASYNC_JOB_URL;

@Component
@Produces("application/xml")
@Path("/publication_delivery")
public class PublicationDeliveryResource {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryResource.class);
    public static final String IMPORT_CORRELATION_ID = "importCorrelationId";

    private SiteFrameImporter siteFrameImporter;

    private NetexMapper netexMapper;

    private PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller;

    private PublicationDeliveryPartialUnmarshaller publicationDeliveryPartialUnmarshaller;

    private PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput;

    private StopPlaceImporter stopPlaceImporter;

    private StopPlaceSearchDisassembler stopPlaceSearchDisassembler;

    private SimpleStopPlaceImporter simpleStopPlaceImporter;

    private PublicationDeliveryExporter publicationDeliveryExporter;

    private AsyncPublicationDeliveryExporter asyncPublicationDeliveryExporter;


    @Autowired
    public PublicationDeliveryResource(SiteFrameImporter siteFrameImporter, NetexMapper netexMapper,
                                       PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller,
                                       PublicationDeliveryPartialUnmarshaller publicationDeliveryPartialUnmarshaller, PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput,
                                       @Qualifier("defaultStopPlaceImporter") StopPlaceImporter stopPlaceImporter,
                                       StopPlaceSearchDisassembler stopPlaceSearchDisassembler, SimpleStopPlaceImporter simpleStopPlaceImporter, PublicationDeliveryExporter publicationDeliveryExporter, AsyncPublicationDeliveryExporter asyncPublicationDeliveryExporter) {

        this.siteFrameImporter = siteFrameImporter;
        this.netexMapper = netexMapper;
        this.publicationDeliveryUnmarshaller = publicationDeliveryUnmarshaller;
        this.publicationDeliveryPartialUnmarshaller = publicationDeliveryPartialUnmarshaller;
        this.publicationDeliveryStreamingOutput = publicationDeliveryStreamingOutput;
        this.stopPlaceImporter = stopPlaceImporter;
        this.stopPlaceSearchDisassembler = stopPlaceSearchDisassembler;
        this.simpleStopPlaceImporter = simpleStopPlaceImporter;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.asyncPublicationDeliveryExporter = asyncPublicationDeliveryExporter;
    }


    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response receivePublicationDelivery(InputStream inputStream) throws IOException, JAXBException, SAXException {
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
                    .findFirst().get();

            return publicationDeliveryExporter.exportSiteFrame(siteFrameWithProcessedStopPlaces);
        } finally {
            MDC.remove(IMPORT_CORRELATION_ID);
        }
    }


    @GET
    @Path(ASYNC_JOB_URL)
    public Collection<ExportJob> getJobs() {
        return asyncPublicationDeliveryExporter.getJobs();
    }

    @GET
    @Path(ASYNC_JOB_URL+"/{id}")
    public Response getJobContents(@PathParam(value = "id") long exportJobId) {

        ExportJob exportJob = asyncPublicationDeliveryExporter.getExportJob(exportJobId);

        logger.info("Returning result of job {}", exportJob);
        if(!exportJob.getStatus().equals(JobStatus.FINISHED)) {
            return Response.accepted("Job status is not FINISHED for job: "+exportJob).build();
        }

        InputStream inputStream = asyncPublicationDeliveryExporter.getJobFileContent(exportJob);
        return Response.ok(inputStream).build();
    }

    @GET
    @Path("async")
    public Response asyncStopPlaceSearch(@BeanParam DtoStopPlaceSearch dtoStopPlaceSearch) {
        StopPlaceSearch stopPlaceSearch = stopPlaceSearchDisassembler.disassemble(dtoStopPlaceSearch);
        ExportJob exportJob = asyncPublicationDeliveryExporter.startExportJob(stopPlaceSearch);
        return Response.ok(exportJob).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response exportStopPlaces(@BeanParam DtoStopPlaceSearch dtoStopPlaceSearch) throws JAXBException, IOException, SAXException {
        StopPlaceSearch stopPlaceSearch = stopPlaceSearchDisassembler.disassemble(dtoStopPlaceSearch);
        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportStopPlaces(stopPlaceSearch);
        return Response.ok(publicationDeliveryStreamingOutput.stream(publicationDeliveryStructure)).build();
    }

    /**
     * This method requires all incoming data to have IDs that are previously generated by Tiamat and that they are unique.
     * IDs for quays and stop places will not be generated. They will be used as is,
     */
    @POST
    @Path("initial_import")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response importPublicationDeliveryOnEmptyDatabase(InputStream inputStream) throws IOException, JAXBException, SAXException, XMLStreamException, InterruptedException, ParserConfigurationException {
        UnmarshalResult unmarshalResult = publicationDeliveryPartialUnmarshaller.unmarshal(inputStream);

        try {
            AtomicInteger topographicPlacesCounter = new AtomicInteger();
            org.rutebanken.tiamat.model.SiteFrame siteFrame = unmarshalResult.getPublicationDeliveryStructure().getDataObjects().getCompositeFrameOrCommonFrame()
                    .stream()
                    .filter(element -> element.getValue() instanceof SiteFrame)
                    .map(element -> (SiteFrame) element.getValue())
                    .peek(netexSiteFrame -> {
                        MDC.put(IMPORT_CORRELATION_ID, netexSiteFrame.getId());
                        logger.info("Publication delivery contains site frame created at ", netexSiteFrame.getCreated());
                    })
                    .map(netexSiteFrame -> netexMapper.mapToTiamatModel(netexSiteFrame))
                    .findFirst().get();

            logger.info("Importing stops");
            int stopPlacesImported = 0;
            while(true) {
                StopPlace stopPlace = unmarshalResult.getStopPlaceQueue().take();
                if(stopPlace.getId().equals(RunnableUnmarshaller.POISON_STOP_PLACE.getId())) {
                    logger.info("Finished importing stops");
                    break;
                }
                simpleStopPlaceImporter.importStopPlace(netexMapper.mapToTiamatModel(stopPlace), siteFrame, topographicPlacesCounter);
                stopPlacesImported++;
            }

            return Response.ok("Imported "+stopPlacesImported + " stop places.").build();

        } catch (Exception e) {
            logger.error("Caught exception while importing publication delivery: " + unmarshalResult.getPublicationDeliveryStructure(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Caught exception while import publication delivery: " + e.getMessage()).build();
        }
    }
}

