/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import io.swagger.annotations.Api;
import org.rutebanken.tiamat.exporter.AsyncPublicationDeliveryExporter;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.job.ExportJob;
import org.rutebanken.tiamat.model.job.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Collection;

import static org.rutebanken.tiamat.rest.netex.publicationdelivery.AsyncExportResource.ASYNC_JOB_PATH;

/**
 * Export publication delivery data to google cloud storage. Some parts like stops and parking asynchronously
 */
@Api(tags = {"Async export resource"}, produces = "application/xml")
@Produces(MediaType.APPLICATION_XML + "; charset=UTF-8")
@Path("/netex/" + ASYNC_JOB_PATH)
public class AsyncExportResource {

    private static final Logger logger = LoggerFactory.getLogger(AsyncExportResource.class);

    public static final String ASYNC_JOB_PATH = "export";

    private final AsyncPublicationDeliveryExporter asyncPublicationDeliveryExporter;

    @Autowired
    public AsyncExportResource(AsyncPublicationDeliveryExporter asyncPublicationDeliveryExporter) {
        this.asyncPublicationDeliveryExporter = asyncPublicationDeliveryExporter;
    }

    @GET
    public Collection<ExportJob> getAsyncExportJobs() {
        return asyncPublicationDeliveryExporter.getJobs();
    }

    @GET
    @Path("{id}/status")
    public Response getAsyncExportJob(@PathParam(value = "id") long exportJobId) {

        ExportJob exportJob = asyncPublicationDeliveryExporter.getExportJob(exportJobId);

        if (exportJob == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        logger.info("Returning job {}", exportJob);
        return Response.ok(exportJob).build();
    }

    @GET
    @Path("{id}/content")
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
    @Path("initiate")
    public Response asyncExport(@BeanParam ExportParams exportParams) {
        ExportJob exportJob = asyncPublicationDeliveryExporter.startExportJob(exportParams);
        return Response.ok(exportJob).build();
    }
}
