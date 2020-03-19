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

package org.rutebanken.tiamat.rest.promethouse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.rutebanken.tiamat.service.metrics.PrometheusMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Api(tags = {"prometheus resource"}, produces = "text/plain")
@Produces("application/json")
@Path("/")
@Transactional
public class PrometheusResource {

    @Autowired
    PrometheusMetricsService prometheusMetricsService;

    @GET
    @Path("/")
    @ApiOperation(value = "Returns OK and scrape")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "scrape")
    })
    public Response scrap() {
        final String scrape = prometheusMetricsService.scrape();
        return Response.ok(scrape).build();
    }

}
