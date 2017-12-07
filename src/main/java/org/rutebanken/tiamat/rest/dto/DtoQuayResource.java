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
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDtoCsvMapper;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.Period;
import java.util.List;

@Component
@Api
@Produces("application/json")
@Path("/")
public class DtoQuayResource {

    private static final Logger logger = LoggerFactory.getLogger(DtoQuayResource.class);

    private final QuayRepository quayRepository;

    private final DtoMappingSemaphore dtoMappingSemaphore;

    private IdMappingDtoCsvMapper csvMapper;

    @Autowired
    public DtoQuayResource(QuayRepository quayRepository, DtoMappingSemaphore dtoMappingSemaphore, IdMappingDtoCsvMapper csvMapper) {
        this.quayRepository = quayRepository;
        this.dtoMappingSemaphore = dtoMappingSemaphore;
        this.csvMapper = csvMapper;
    }

    @GET
    @Path("mapping/quay")
    @Produces("text/plain")
    public Response getIdMapping(@DefaultValue(value = "300000") @QueryParam(value = "recordsPerRoundTrip") int recordsPerRoundTrip,
                                        @QueryParam("includeStopType") boolean includeStopType, @QueryParam("includeFuture") boolean includeFuture) throws InterruptedException {

        logger.info("Fetching Quay mapping table...");

        dtoMappingSemaphore.aquire();
        try {

            return Response.ok().entity((StreamingOutput) output -> {

                int recordPosition = 0;
                boolean lastEmpty = false;
                Instant validFrom = Instant.now();
                Instant validTo = includeFuture ? null : validFrom;
                try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output)))) {
                    while (!lastEmpty) {
                        List<IdMappingDto> quayMappings = quayRepository.findKeyValueMappingsForQuay(validFrom, validTo, recordPosition, recordsPerRoundTrip);
                        for (IdMappingDto mapping : quayMappings) {
                            writer.println(csvMapper.toCsvString(mapping, includeStopType, includeFuture));
                            recordPosition++;
                        }
                        writer.flush();
                        if (quayMappings.isEmpty()) lastEmpty = true;
                    }
                    writer.close();
                } catch (Exception e) {
                    logger.warn("Catched exception when streaming id map for quay: {}", e.getMessage(), e);
                    throw e;
                }
            }).build();
        } finally {
            dtoMappingSemaphore.release();
        }
    }


    @GET
    @Path("/id/quay")
    @Produces("text/plain")
    public String getIdUniqueQuayIds(@QueryParam("includeFuture") boolean includeFuture) {
        Instant validFrom = Instant.now();
        Instant validTo = includeFuture ? null : validFrom;
        return String.join("\n", quayRepository.findUniqueQuayIds(validFrom, validTo));
    }
}
