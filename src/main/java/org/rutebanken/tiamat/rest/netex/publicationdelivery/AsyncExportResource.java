package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.rutebanken.tiamat.exporter.AsyncPublicationDeliveryExporter;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.job.ExportJob;
import org.rutebanken.tiamat.model.job.JobStatus;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Collection;

import static org.rutebanken.tiamat.exporter.AsyncPublicationDeliveryExporter.ASYNC_JOB_URL;

/**
 * Export publication delivery data to google cloud storage. Some parts like stops and parking asynchronously
 */
@Component
@Produces("application/xml")
@Path("/publication_delivery")
public class AsyncExportResource {

    private static final Logger logger = LoggerFactory.getLogger(AsyncExportResource.class);


    private final AsyncPublicationDeliveryExporter asyncPublicationDeliveryExporter;

    @Autowired
    public AsyncExportResource(AsyncPublicationDeliveryExporter asyncPublicationDeliveryExporter) {
        this.asyncPublicationDeliveryExporter = asyncPublicationDeliveryExporter;
    }

    @GET
    @Path(ASYNC_JOB_URL)
    public Collection<ExportJob> getAsyncExportJobs() {
        return asyncPublicationDeliveryExporter.getJobs();
    }

    @GET
    @Path(ASYNC_JOB_URL + "/{id}")
    public Response getAsyncExportJob(@PathParam(value = "id") long exportJobId) {

        ExportJob exportJob = asyncPublicationDeliveryExporter.getExportJob(exportJobId);

        if (exportJob == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        logger.info("Returning job {}", exportJob);
        return Response.ok(exportJob).build();
    }

    @GET
    @Path(ASYNC_JOB_URL + "/{id}/content")
    public Response getAsyncExportJobContents(@PathParam(value = "id") long exportJobId) {

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
    public Response asyncExport(@BeanParam ExportParams exportParams) {
        ExportJob exportJob = asyncPublicationDeliveryExporter.startExportJob(exportParams);
        return Response.ok(exportJob).build();
    }
}
