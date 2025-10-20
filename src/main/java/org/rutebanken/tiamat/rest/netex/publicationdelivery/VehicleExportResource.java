package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import lombok.extern.slf4j.Slf4j;
import org.rutebanken.tiamat.exporter.StreamingPublicationDelivery;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Produces("application/xml")
@Path("netex")
@Slf4j
public class VehicleExportResource {

    @Qualifier("syncStreamingPublicationDelivery")
    @Autowired
    private StreamingPublicationDelivery streamingPublicationDelivery;

    @GET
    @Produces(MediaType.APPLICATION_XML + "; charset=UTF-8")
    public Response getVehicleNetex(@BeanParam ExportParams exportParams) {
        log.info("Exporting publication delivery. {}", exportParams);


        StreamingOutput streamingOutput = outputStream -> {
            try {
                streamingPublicationDelivery.streamVehicles(exportParams, outputStream);
            } catch (Exception e) {
                log.warn("Could not stream composite frame. {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        };

        return Response.ok(streamingOutput).build();
    }
}