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

package org.rutebanken.tiamat.rest.health;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Api
@Produces("application/json")
@Path("/")
@Transactional
public class HealthResource {
    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @GET
    @Path("/ready")
    @ApiOperation(value = "Returns OK if Tiamat is ready and can read from the database", response = Void.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "application is running")
    })
    public Response readinessProbe() {
        stopPlaceRepository.findAllByOrderByChangedDesc(new PageRequest(1, 1));
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/live")
    @ApiOperation(value = "Returns 200 OK if Tiamat is running", response = Void.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "application is running")
    })
    public Response livenessProbe() {
        return Response.ok().build();
    }

}
