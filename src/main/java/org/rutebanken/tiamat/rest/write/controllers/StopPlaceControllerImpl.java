package org.rutebanken.tiamat.rest.write.controllers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.rest.write.StopPlaceFacade;
import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "tiamat.write-api.enabled", havingValue = "true")
@Produces(MediaType.APPLICATION_JSON)
@Path("write")
public class StopPlaceControllerImpl implements StopPlaceController {

    private final StopPlaceFacade stopPlaceFacade;
    private final AuthorizationService authorizationService;
    private final boolean authorizationEnabled;

    @Autowired
    public StopPlaceControllerImpl(StopPlaceFacade stopPlaceFacade, AuthorizationService authorizationService, @Value("${authorization.enabled:true}") boolean authorizationEnabled) {
        this.stopPlaceFacade = stopPlaceFacade;
        this.authorizationService = authorizationService;
        this.authorizationEnabled = authorizationEnabled;
    }

    private void checkCanRead() {
        // TODO: more finegrained permissions
        if (authorizationEnabled && !authorizationService.canEditAllEntities()) {
            throw new ForbiddenException();
        }
    }

    private void checkCanEdit() {
        // TODO: more finegrained permissions
        if (authorizationEnabled && !authorizationService.canEditAllEntities()) {
            throw new ForbiddenException();
        }
    }

    private void checkCanDelete() {
        // TODO: more finegrained permissions
        if (authorizationEnabled && !authorizationService.canEditAllEntities()) {
            throw new ForbiddenException();
        }
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
        checkCanRead();
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
        checkCanEdit();
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
        checkCanEdit();
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
        checkCanDelete();
        return Response.accepted(
            stopPlaceFacade.deleteStopPlace(stopPlaceId)
        ).build();
    }
}
