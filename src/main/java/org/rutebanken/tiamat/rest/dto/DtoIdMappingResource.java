package org.rutebanken.tiamat.rest.dto;

import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.List;

@Produces("text/plain")
@Path("/id_mapping")
public class DtoIdMappingResource {


    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    public DtoIdMappingResource(StopPlaceRepository stopPlaceRepository) {
        this.stopPlaceRepository = stopPlaceRepository;
    }

    @GET
    public Response getIdMapping(@DefaultValue(value = "20000") @QueryParam(value = "recordsPerRoundTrip") int recordsPerRoundTrip) {

        return  Response.ok().entity((StreamingOutput) output -> {
            
            int recordPosition = 0;
            int numberOfKeyValueMappings = stopPlaceRepository.findKeyValueMappingCount();

            try ( PrintWriter writer = new PrintWriter( new BufferedWriter( new OutputStreamWriter( output ) ) ) ) {
                while (numberOfKeyValueMappings > 0) {

                    List<IdMappingDto> keyValueMappings = stopPlaceRepository.findKeyValueMappings(recordPosition, recordsPerRoundTrip);
                    for (IdMappingDto mapping : keyValueMappings) {
                        writer.println(mapping.toCsvString());
                        recordPosition ++;
                    }
                    numberOfKeyValueMappings -= recordsPerRoundTrip;
                    writer.flush();
                }
                writer.close();
            }
        }).build();
    }
}
