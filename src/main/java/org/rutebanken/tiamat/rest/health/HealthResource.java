package org.rutebanken.tiamat.rest.health;

import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Component
@Produces("application/json")
@Path("/health")
@Transactional
public class HealthResource {
    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @GET
    @Path("readiness")
    public Response readinessProbe() {
        stopPlaceRepository.findAllByOrderByChangedDesc(new PageRequest(1, 1));
        return Response.status(Response.Status.OK).build();
    }
}
