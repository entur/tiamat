package org.rutebanken.tiamat.rest.dto;

import org.rutebanken.tiamat.dtoassembling.assembler.QuayAssembler;
import org.rutebanken.tiamat.dtoassembling.dto.QuayDto;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Produces("application/json")
@Path("/quay")
public class DtoQuayResource {

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private QuayAssembler quayAssembler;

    @GET
    public List<QuayDto> getQuays() {
        return quayRepository.findAll().stream().map(quay -> quayAssembler.assemble(quay)).collect(Collectors.toList());
    }

}
