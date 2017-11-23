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

package org.rutebanken.tiamat.rest.dto;

import io.swagger.annotations.Api;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.List;

@Component
@Api
@Produces("application/json")
@Path("/")
@Transactional
public class DtoStopPlaceResource {

    private static final Logger logger = LoggerFactory.getLogger(DtoStopPlaceResource.class);

    private final StopPlaceRepository stopPlaceRepository;

    private final DtoMappingSemaphore dtoMappingSemaphore;

    @Autowired
    public DtoStopPlaceResource(StopPlaceRepository stopPlaceRepository, DtoMappingSemaphore dtoMappingSemaphore) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.dtoMappingSemaphore = dtoMappingSemaphore;
    }

    @GET
    @Path("/mapping/stop_place")
    @Produces("text/plain")
    public Response getIdMapping(@DefaultValue(value = "300000") @QueryParam(value = "recordsPerRoundTrip") int recordsPerRoundTrip,
                                            @QueryParam("includeStopType") boolean includeStopType) throws InterruptedException {

        dtoMappingSemaphore.aquire();
        try {
            logger.info("Fetching StopPlace mapping table...");

            return Response.ok().entity((StreamingOutput) output -> {

                int recordPosition = 0;
                boolean lastEmpty = false;
                Instant now = Instant.now();
                try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output)))) {
                    while (!lastEmpty) {

                        List<IdMappingDto> stopPlaceMappings = stopPlaceRepository.findKeyValueMappingsForStop(now, recordPosition, recordsPerRoundTrip);
                        for (IdMappingDto mapping : stopPlaceMappings) {
                            writer.println(mapping.toCsvString(includeStopType));
                            recordPosition++;
                        }
                        writer.flush();
                        if (stopPlaceMappings.isEmpty()) lastEmpty = true;
                    }
                    writer.close();
                }
            }).build();
        }
        finally {
            dtoMappingSemaphore.release();
        }
    }

    @GET
    @Path("/id/stop_place")
    @Produces("text/plain")
    public String getIdUniqueStopPlaceIds() {
        return String.join("\n", stopPlaceRepository.findUniqueStopPlaceIds(Instant.now()));
    }
}
