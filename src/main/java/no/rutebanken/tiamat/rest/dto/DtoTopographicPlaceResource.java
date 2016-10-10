package no.rutebanken.tiamat.rest.dto;

import no.rutebanken.tiamat.dtoassembling.assembler.CountyOrMunicipalityAssembler;
import no.rutebanken.tiamat.dtoassembling.dto.CountyOrMunicipalityDto;
import no.rutebanken.tiamat.model.TopographicPlace;
import no.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import no.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Produces("application/json")
@Path("/topographic_place")
public class DtoTopographicPlaceResource {

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private CountyOrMunicipalityAssembler countyOrMunicipalityAssembler;

    @GET
    public List<CountyOrMunicipalityDto> getAdministrativeDivisions() {
        return countyOrMunicipalityAssembler.assemble(topographicPlaceRepository.findAll());
    }
}
