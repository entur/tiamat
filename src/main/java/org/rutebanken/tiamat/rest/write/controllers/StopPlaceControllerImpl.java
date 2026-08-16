package org.rutebanken.tiamat.rest.write.controllers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.rutebanken.tiamat.rest.write.StopPlaceWriteService;

import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "tiamat.write-api.enabled", havingValue = "true")
@Produces(MediaType.APPLICATION_JSON)
@Path("write")
@PreAuthorize("@authorizationService.canUseWriteApi()")
public class StopPlaceControllerImpl implements StopPlaceController {

    private final StopPlaceWriteService stopPlaceWriteService;

    @Autowired
    public StopPlaceControllerImpl(StopPlaceWriteService stopPlaceWriteService) {
        this.stopPlaceWriteService = stopPlaceWriteService;
    }

    @Override
    @GET
    @Produces(
        {
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_XML + "; charset=utf-8",
        }
    )
    @Path("/{stopPlaceId}")
    public Response getStopPlace(@PathParam("stopPlaceId") String stopPlaceId) {
        return Response.ok(stopPlaceWriteService.getStopPlace(stopPlaceId)).build();
    }

    @Override
    @POST
    @Consumes(
        {
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_XML + "; charset=utf-8",
        }
    )
    public Response createStopPlace(InputStream body) {
        return Response.accepted(
            stopPlaceWriteService.createStopPlaces(body)
        ).build();
    }

    @Override
    @PUT
    @Consumes(
        {
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_XML + "; charset=utf-8",
        }
    )
    public Response updateStopPlace(InputStream body) {
        return Response.accepted(
            stopPlaceWriteService.updateStopPlace(body)
        ).build();
    }

    @Override
    @DELETE
    @Path("/{stopPlaceId}")
    public Response deleteStopPlace(
        @PathParam("stopPlaceId") String stopPlaceId
    ) {
        return Response.accepted(
            stopPlaceWriteService.deleteStopPlace(stopPlaceId)
        ).build();
    }
}
