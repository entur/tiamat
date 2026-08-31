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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Tag(name = "Application status resource", description = "Application status resource")
@Produces("application/json")
@Path("/")
@Transactional
public class HealthResource {
    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @GET
    @Path("/ready")
    @Operation(summary = "Returns OK if Tiamat is ready and can read from the database", responses = {
            @ApiResponse(responseCode = "200", description = "application is running")
    })
    public Response readinessProbe() {
        stopPlaceRepository.findAllByOrderByChangedDesc(PageRequest.of(1, 1));
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/live")
    @Operation(summary = "Returns 200 OK if Tiamat is running", responses = {
            @ApiResponse(responseCode = "200", description = "application is running")
    })
    public Response livenessProbe() {
        return Response.ok().build();
    }

}
