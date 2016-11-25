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
            boolean lastEmpty = false;

            try ( PrintWriter writer = new PrintWriter( new BufferedWriter( new OutputStreamWriter( output ) ) ) ) {
                while (!lastEmpty) {

                    List<IdMappingDto> quayMappings = stopPlaceRepository.findKeyValueMappingsForQuay(recordPosition, recordsPerRoundTrip);
                    List<IdMappingDto> stopPlaceMappings = stopPlaceRepository.findKeyValueMappingsForStop(recordPosition, recordsPerRoundTrip);
                    quayMappings.addAll(stopPlaceMappings);
                    for (IdMappingDto mapping : quayMappings) {
                        writer.println(mapping.toCsvString());
                        recordPosition ++;
                    }
                    writer.flush();
                    if(quayMappings.isEmpty()) lastEmpty = true;
                }
                writer.close();
            }
        }).build();
    }
}
