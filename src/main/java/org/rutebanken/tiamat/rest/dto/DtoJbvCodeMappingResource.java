package org.rutebanken.tiamat.rest.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.rutebanken.tiamat.dtoassembling.dto.JbvCodeMappingDto;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import static org.rutebanken.tiamat.repository.QuayRepositoryImpl.JBV_CODE;

@Tag(name = "JBV code mapping resource", description = "JBV code mapping resource")
@Produces("application/json")
@Path("jbv_code_mapping")
public class DtoJbvCodeMappingResource {

    private static final Logger logger = LoggerFactory.getLogger(DtoJbvCodeMappingResource.class);

    private final QuayRepository quayRepository;

    private final StopPlaceRepository stopPlaceRepository;

    private final DtoMappingSemaphore dtoMappingSemaphore;


    @Autowired
    public DtoJbvCodeMappingResource(QuayRepository quayRepository, StopPlaceRepository stopPlaceRepository, DtoMappingSemaphore dtoMappingSemaphore) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.quayRepository = quayRepository;
        this.dtoMappingSemaphore = dtoMappingSemaphore;
    }


    @GET
    @Produces("text/plain")
    public Response getJbvCodeMapping() throws InterruptedException {

        dtoMappingSemaphore.aquire();
        try {
            logger.info("Fetching Quay mapping table for all Quays containg keyValue {}...", JBV_CODE);

            return Response.ok().entity((StreamingOutput) output -> {

                try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output)))) {
                    List<JbvCodeMappingDto> quayMappings = quayRepository.findJbvCodeMappingsForQuay();
                    for (JbvCodeMappingDto mapping : quayMappings) {
                        writer.println(mapping.toCsvString());
                    }
                    List<JbvCodeMappingDto> stopPlaceMappings = stopPlaceRepository.findJbvCodeMappingsForStopPlace();
                    for (JbvCodeMappingDto mapping : stopPlaceMappings) {
                        writer.println(mapping.toCsvString());
                    }
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    logger.warn("Catched exception when streaming id map for quay: {}", e.getMessage(), e);
                    throw e;
                }
            }).build();
        }
        finally {
            dtoMappingSemaphore.release();
        }
    }

}
