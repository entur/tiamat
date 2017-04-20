package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.dtoassembling.disassembler.StopPlaceSearchDisassembler;
import org.rutebanken.tiamat.dtoassembling.dto.StopPlaceSearchDto;
import org.rutebanken.tiamat.exporter.AsyncPublicationDeliveryExporter;
import org.rutebanken.tiamat.exporter.PublicationDeliveryExporter;
import org.rutebanken.tiamat.importer.PublicationDeliveryImporter;
import org.rutebanken.tiamat.importer.PublicationDeliveryParams;
import org.rutebanken.tiamat.model.job.ExportJob;
import org.rutebanken.tiamat.model.job.JobStatus;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
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
import java.util.Collection;
import java.util.List;

import static org.rutebanken.tiamat.exporter.AsyncPublicationDeliveryExporter.ASYNC_JOB_URL;

@Component
@Produces("application/xml")
@Path("/publication_delivery")
public class PublicationDeliveryResource {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryResource.class);

    private final PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller;

    private final PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput;

    private final StopPlaceSearchDisassembler stopPlaceSearchDisassembler;

    private final PublicationDeliveryExporter publicationDeliveryExporter;

    private final AsyncPublicationDeliveryExporter asyncPublicationDeliveryExporter;

    private final PublicationDeliveryImporter publicationDeliveryImporter;


    @Autowired
    public PublicationDeliveryResource(
                                              PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller,
                                              PublicationDeliveryStreamingOutput publicationDeliveryStreamingOutput,
                                              StopPlaceSearchDisassembler stopPlaceSearchDisassembler,
                                              PublicationDeliveryExporter publicationDeliveryExporter,
                                              AsyncPublicationDeliveryExporter asyncPublicationDeliveryExporter, PublicationDeliveryImporter publicationDeliveryImporter) {

        this.publicationDeliveryUnmarshaller = publicationDeliveryUnmarshaller;
        this.publicationDeliveryStreamingOutput = publicationDeliveryStreamingOutput;
        this.stopPlaceSearchDisassembler = stopPlaceSearchDisassembler;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.asyncPublicationDeliveryExporter = asyncPublicationDeliveryExporter;
        this.publicationDeliveryImporter = publicationDeliveryImporter;
    }

    public Response receivePublicationDelivery(InputStream inputStream) throws IOException, JAXBException, SAXException {
        return receivePublicationDelivery(inputStream, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response receivePublicationDelivery(InputStream inputStream, @BeanParam PublicationDeliveryParams publicationDeliveryParams) throws IOException, JAXBException, SAXException {

        PublicationDeliveryStructure incomingPublicationDelivery = publicationDeliveryUnmarshaller.unmarshal(inputStream);
        try {
            PublicationDeliveryStructure responsePublicationDelivery = publicationDeliveryImporter.importPublicationDelivery(incomingPublicationDelivery, publicationDeliveryParams);
            return Response.ok(publicationDeliveryStreamingOutput.stream(responsePublicationDelivery)).build();
        } catch (Exception e) {
            logger.error("Caught exception while importing publication delivery: " + incomingPublicationDelivery, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Caught exception while import publication delivery: " + e.getMessage()).build();
        }
    }

    @GET
    @Path(ASYNC_JOB_URL)
    public Collection<ExportJob> getJobs() {
        return asyncPublicationDeliveryExporter.getJobs();
    }

    @GET
    @Path(ASYNC_JOB_URL + "/{id}")
    public Response getJob(@PathParam(value = "id") long exportJobId) {

        ExportJob exportJob = asyncPublicationDeliveryExporter.getExportJob(exportJobId);

        if (exportJob == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        logger.info("Returning job {}", exportJob);
        return Response.ok(exportJob).build();
    }

    @GET
    @Path(ASYNC_JOB_URL + "/{id}/content")
    public Response getJobContents(@PathParam(value = "id") long exportJobId) {

        ExportJob exportJob = asyncPublicationDeliveryExporter.getExportJob(exportJobId);

        if (exportJob == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        logger.info("Returning result of job {}", exportJob);
        if (!exportJob.getStatus().equals(JobStatus.FINISHED)) {
            return Response.accepted("Job status is not FINISHED for job: " + exportJob).build();
        }

        InputStream inputStream = asyncPublicationDeliveryExporter.getJobFileContent(exportJob);
        return Response.ok(inputStream).build();
    }

    @GET
    @Path("async")
    public Response asyncStopPlaceSearch(@BeanParam StopPlaceSearchDto stopPlaceSearchDto) {
        StopPlaceSearch stopPlaceSearch = stopPlaceSearchDisassembler.disassemble(stopPlaceSearchDto);
        ExportJob exportJob = asyncPublicationDeliveryExporter.startExportJob(stopPlaceSearch);
        return Response.ok(exportJob).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response exportStopPlaces(@BeanParam StopPlaceSearchDto stopPlaceSearchDto) throws JAXBException, IOException, SAXException {
        StopPlaceSearch stopPlaceSearch = stopPlaceSearchDisassembler.disassemble(stopPlaceSearchDto);
        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportStopPlaces(stopPlaceSearch);
        return Response.ok(publicationDeliveryStreamingOutput.stream(publicationDeliveryStructure)).build();
    }

}

