package org.rutebanken.tiamat.rest.dto;

import org.rutebanken.tiamat.repository.QuayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Deprecated
@Path("quay/id_mapping")
public class LegacyDtoQuayResource {

    @Autowired
    private DtoQuayResource dtoQuayResource;

    @GET
    @Produces("text/plain")
    public Response getIdMapping(@DefaultValue(value = "300000") @QueryParam(value = "recordsPerRoundTrip") int recordsPerRoundTrip,
                                 @QueryParam("includeStopType") boolean includeStopType) {
        return dtoQuayResource.getIdMapping(recordsPerRoundTrip, includeStopType);
    }
}
