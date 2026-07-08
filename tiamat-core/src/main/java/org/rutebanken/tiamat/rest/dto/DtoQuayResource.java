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
import org.rutebanken.tiamat.repository.QuayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Component
@Tag(name = "Quay resource", description = "Quay resource")
@Produces("application/json")
@Path("/")
public class DtoQuayResource {

    private static final Logger logger = LoggerFactory.getLogger(DtoQuayResource.class);

    private final QuayRepository quayRepository;

    private final DtoMappingSemaphore dtoMappingSemaphore;

    private final IdMappingDtoCsvMapper csvMapper;

    @Autowired
    public DtoQuayResource(QuayRepository quayRepository, DtoMappingSemaphore dtoMappingSemaphore, IdMappingDtoCsvMapper csvMapper) {
        this.quayRepository = quayRepository;
        this.dtoMappingSemaphore = dtoMappingSemaphore;
        this.csvMapper = csvMapper;
    }

    /**
     * Return the list of Quay local references with their mappings to NSR IDs in CSV format:
     * local reference, NSR ID, (stop place type), valid from, valid to
     * @param recordsPerRoundTrip batch size
     * @param includeStopType include the parent stop place type
     * @param includeFuture include future (not-yet-valid) quays
     * @return A plain-text HTTP response listing the local references with their mappings to NSR IDs as a CSV file.
     * @throws InterruptedException
     */
    @GET
    @Path("mapping/quay")
    @Produces("text/plain")
    public Response getIdMapping(@DefaultValue(value = "300000") @QueryParam(value = "recordsPerRoundTrip") int recordsPerRoundTrip,
                                        @QueryParam("includeStopType") boolean includeStopType, @QueryParam("includeFuture") boolean includeFuture) throws InterruptedException {

        logger.info("Fetching Quay mapping table...");

        dtoMappingSemaphore.aquire();
        try {
            return Response.ok().entity((StreamingOutput) output -> getMappings(recordsPerRoundTrip, includeStopType, includeFuture, includeFuture, true, output)).build();
        } finally {
            dtoMappingSemaphore.release();
        }
    }

    /**
     * Return the list of all Quay local references in plain text format (one id per line)
     * @param recordsPerRoundTrip batch size
     * @param includeFuture include future (not-yet-valid) quays
     * @return A plain-text HTTP response listing all the local references.
     * @throws InterruptedException
     */
    @GET
    @Path("local_reference/quay")
    @Produces("text/plain")
    public Response getQuayLocalReferences(@DefaultValue(value = "300000") @QueryParam(value = "recordsPerRoundTrip") int recordsPerRoundTrip, @QueryParam("includeFuture") boolean includeFuture) throws InterruptedException {

        logger.info("Fetching Quay local references...");
        dtoMappingSemaphore.aquire();
        try {
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
                List<IdMappingDto> quayMappings = quayRepository.findKeyValueMappingsForQuay(validFrom, validTo, recordPosition, recordsPerRoundTrip);
                for (IdMappingDto mapping : quayMappings) {
                    writer.println(csvMapper.toCsvString(mapping, includeStopType, includeValidity, includeNsrId));
                    recordPosition++;
                }
                writer.flush();
                if (quayMappings.isEmpty()) lastEmpty = true;
            }
        } catch (Exception e) {
            logger.warn("Catched exception when streaming id map for quay: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Return the list of all Quay NSR IDs in plain text format (one id per line)
     * @param includeFuture include future (not-yet-valid) quays
     * @return A plain-text HTTP response listing all the NSR IDs.
     * @throws InterruptedException
     */
    @GET
    @Path("/id/quay")
    @Produces("text/plain")
    public String getIdUniqueQuayIds(@QueryParam("includeFuture") boolean includeFuture) {
        Instant validFrom = Instant.now();
        Instant validTo = includeFuture ? null : validFrom;
        return String.join("\n", quayRepository.findUniqueQuayIds(validFrom, validTo));
    }
}
