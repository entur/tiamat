package no.rutebanken.tiamat.rest.ifopt;

import no.rutebanken.tiamat.ifopt.transfer.assembler.QuayAssembler;
import no.rutebanken.tiamat.ifopt.transfer.dto.QuayDTO;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
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
public class QuayResource {

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private QuayAssembler quayAssembler;

    @GET
    public List<QuayDTO> getQuays() {
        return quayRepository.findAll().stream().map(quay -> quayAssembler.assemble(quay)).collect(Collectors.toList());
    }

}
