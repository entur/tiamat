package org.rutebanken.tiamat.rest.dto;

import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

@Produces("text/plain")
@Path("/id_mapping")
public class DtoIdMappingResource {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @GET
    public Response getStopPlaces() {
        List<IdMappingDto> dtoList = stopPlaceRepository.findAllKeyValueMappings();

        return  Response.ok(getOut(dtoList)).build();
    }

    private StreamingOutput getOut(final List<IdMappingDto> idMapping) {
        return out -> {
            for (IdMappingDto idMappingDto : idMapping) {
                out.write(idMappingDto.toCsvString().getBytes());
            }
        };
    }
}
