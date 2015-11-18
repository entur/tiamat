package no.rutebanken.tiamat.rest.ifopt;

import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Component
@Produces("application/json")
@Path("/quay")
public class QuayResource {

    @Autowired
    private QuayRepository quayRepository;

    @GET
    public List<Quay> getQuays() {
        return quayRepository.findAll();
    }

}
