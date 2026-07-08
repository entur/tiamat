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

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDtoCsvMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Tag(name = "Stop place resource", description = "Stop place resource")
@Produces("application/json")
@Path("/")
@Transactional
public class DtoStopPlaceResource {

    private static final Logger logger = LoggerFactory.getLogger(DtoStopPlaceResource.class);

    private final StopPlaceRepository stopPlaceRepository;

    private final DtoMappingSemaphore dtoMappingSemaphore;

    private final IdMappingDtoCsvMapper csvMapper;

    @Autowired
    public DtoStopPlaceResource(StopPlaceRepository stopPlaceRepository, DtoMappingSemaphore dtoMappingSemaphore, IdMappingDtoCsvMapper csvMapper) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.dtoMappingSemaphore = dtoMappingSemaphore;
        this.csvMapper = csvMapper;
    }

    /**
     * Return the list of Stop Place local references with their mappings to NSR IDs in CSV format:
     * local reference, NSR ID, (stop place type), valid from, valid to.
     * @param recordsPerRoundTrip batch size.
     * @param includeStopType include the stop place type.
     * @param includeFuture include future (not-yet-valid) quays.
     * @return A plain-text HTTP response listing the local references with their mappings to NSR IDs as a CSV file.
     * @throws InterruptedException
     */
    @GET
    @Path("/mapping/stop_place")
    @Produces("text/plain")
    public Response getIdMapping(@DefaultValue(value = "300000") @QueryParam(value = "recordsPerRoundTrip") int recordsPerRoundTrip,
                                        @QueryParam("includeStopType") boolean includeStopType, @QueryParam("includeFuture") boolean includeFuture) throws InterruptedException {
        dtoMappingSemaphore.aquire();
        try {
            logger.info("Fetching StopPlace mapping table...");
            return Response.ok().entity((StreamingOutput) output -> getMappings(recordsPerRoundTrip, includeStopType, includeFuture, includeFuture, true, output)).build();
        } finally {
            dtoMappingSemaphore.release();
        }
    }

    /**
     * Return the list of all StopPlace local references in plain text format (one id per line).
     * @param recordsPerRoundTrip batch size.
     * @param includeFuture include future (not-yet-valid) quays .
     * @return A plain-text HTTP response listing all the local references.
     * @throws InterruptedException
     */
    @GET
    @Path("/local_reference/stop_place")
    @Produces("text/plain")
    public Response getStopPlaceLocalReferences(@DefaultValue(value = "300000") @QueryParam(value = "recordsPerRoundTrip") int recordsPerRoundTrip, @QueryParam("includeFuture") boolean includeFuture) throws InterruptedException {

        dtoMappingSemaphore.aquire();
        try {
            logger.info("Fetching StopPlace local references...");
            return Response.ok().entity((StreamingOutput) output -> getMappings(recordsPerRoundTrip, false, includeFuture, false, false, output)).build();
        } finally {
            dtoMappingSemaphore.release();
        }
    }

    private void getMappings(int recordsPerRoundTrip, boolean includeStopType, boolean includeFuture, boolean includeValidity, boolean includeNsrId, OutputStream output) {
        int recordPosition = 0;
        boolean lastEmpty = false;
        Instant validFrom = Instant.now();
        Instant validTo = includeFuture ? null : validFrom;
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8)))) {
            while (!lastEmpty) {

                List<IdMappingDto> stopPlaceMappings = stopPlaceRepository.findKeyValueMappingsForStop(validFrom, validTo, recordPosition, recordsPerRoundTrip);
                for (IdMappingDto mapping : stopPlaceMappings) {
                    writer.println(csvMapper.toCsvString(mapping, includeStopType, includeValidity, includeNsrId));
                    recordPosition++;
                }
                writer.flush();
                if (stopPlaceMappings.isEmpty()) lastEmpty = true;
            }
        }
    }

    /**
     * Return the list of all StopPlace NSR IDs in plain text format (one id per line).
     * @param includeFuture include future (not-yet-valid) stop places.
     * @return A plain-text HTTP response listing all the NSR IDs.
     * @throws InterruptedException
     */
    @GET
    @Path("/id/stop_place")
    @Produces("text/plain")
    public String getIdUniqueStopPlaceIds(@QueryParam("includeFuture") boolean includeFuture) {
        Instant validFrom = Instant.now();
        Instant validTo = includeFuture ? null : validFrom;
        return String.join("\n", stopPlaceRepository.findUniqueStopPlaceIds(validFrom, validTo));
    }

    @GET
    @Path("/list/stop_place_quays")
    public Map<String, Set<String>> listStopPlaceQuays(@QueryParam("includeFuture") boolean includeFuture) {
        Instant validFrom = Instant.now();
        Instant validTo = includeFuture ? null : validFrom;
        return stopPlaceRepository.listStopPlaceIdsAndQuayIds(validFrom, validTo);
    }
}
