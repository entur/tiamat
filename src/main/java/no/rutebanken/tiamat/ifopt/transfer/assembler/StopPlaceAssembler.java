package no.rutebanken.tiamat.ifopt.transfer.assembler;


import no.rutebanken.tiamat.ifopt.transfer.dto.StopPlaceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.StopPlace;

@Component
public class StopPlaceAssembler {

    @Autowired
    private SimplePointAssembler simplePointAssembler;


    public StopPlaceDTO assemble(StopPlace stopPlace) {
        StopPlaceDTO simpleStopPlaceDTO = new StopPlaceDTO();
        simpleStopPlaceDTO.id = stopPlace.getId();

        if(stopPlace.getName() != null) simpleStopPlaceDTO.name = stopPlace.getName().getValue();
        if(stopPlace.getStopPlaceType() != null) simpleStopPlaceDTO.stopPlaceType = stopPlace.getStopPlaceType().value();

        simpleStopPlaceDTO.centroid = simplePointAssembler.assemble(stopPlace.getCentroid());

        return simpleStopPlaceDTO;
    }

}