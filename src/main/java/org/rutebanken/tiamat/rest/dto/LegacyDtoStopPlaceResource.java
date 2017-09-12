package org.rutebanken.tiamat.rest.dto;

import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Deprecated
@Path("stop_place/id_mapping")
public class LegacyDtoStopPlaceResource {

    @Autowired
    private DtoStopPlaceResource dtoStopPlaceResource;

    @GET
    @Produces("text/plain")
    public Response getIdMapping(@DefaultValue(value = "300000") @QueryParam(value = "recordsPerRoundTrip") int recordsPerRoundTrip,
                                 @QueryParam("includeStopType") boolean includeStopType) {
        return dtoStopPlaceResource.getIdMapping(recordsPerRoundTrip, includeStopType);
    }
}
