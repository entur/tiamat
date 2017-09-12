package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.rutebanken.tiamat.exporter.AsyncPublicationDeliveryExporter;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.job.ExportJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Deprecated
@Path("publication_delivery/async")
public class LegacyAsyncExportResource extends AsyncExportResource {

    @Autowired
    public LegacyAsyncExportResource(AsyncPublicationDeliveryExporter asyncPublicationDeliveryExporter) {
        super(asyncPublicationDeliveryExporter);
    }

    @GET
    @Override
    public Response asyncExport(@BeanParam  ExportParams exportParams) {
        return super.asyncExport(exportParams);
    }

    @GET
    @Path("job")
    @Override
    public Collection<ExportJob> getAsyncExportJobs() {
        return super.getAsyncExportJobs();
    }

    @GET
    @Path("job/{id}")
    @Override
    public Response getAsyncExportJob(@PathParam(value = "id") long exportJobId) {
        return super.getAsyncExportJob(exportJobId);
    }

    @GET
    @Path("job/{id}/content")
    @Override
    public Response getAsyncExportJobContents(@PathParam(value = "id") long exportJobId) {
        return super.getAsyncExportJobContents(exportJobId);
    }
}
