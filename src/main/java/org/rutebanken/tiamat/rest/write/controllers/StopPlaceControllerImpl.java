package org.rutebanken.tiamat.rest.write.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.rutebanken.tiamat.rest.write.StopPlaceFacade;
import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Produces(MediaType.APPLICATION_JSON)
@Path("write")
public class StopPlaceControllerImpl implements StopPlaceController {

    private final StopPlaceFacade stopPlaceFacade;

    @Autowired
    public StopPlaceControllerImpl(StopPlaceFacade stopPlaceFacade) {
        this.stopPlaceFacade = stopPlaceFacade;
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
        return Response.ok(stopPlaceFacade.getStopPlace(stopPlaceId)).build();
    }

    @Override
    @POST
    @Consumes(
        {
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_XML + "; charset=utf-8",
        }
    )
    public Response createStopPlace(StopPlacesDto stopPlacesDto) {
        return Response.accepted(
            stopPlaceFacade.createStopPlaces(stopPlacesDto)
        ).build();
    }

    @Override
    @PATCH
    @Consumes(
        {
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_XML + "; charset=utf-8",
        }
    )
    public Response updateStopPlace(StopPlacesDto stopPlacesDto) {
        return Response.accepted(
            stopPlaceFacade.updateStopPlace(stopPlacesDto)
        ).build();
    }

    @Override
    @DELETE
    @Path("/{stopPlaceId}")
    public Response deleteStopPlace(
        @PathParam("stopPlaceId") String stopPlaceId
    ) {
        return Response.accepted(
            stopPlaceFacade.deleteStopPlace(stopPlaceId)
        ).build();
    }
}
