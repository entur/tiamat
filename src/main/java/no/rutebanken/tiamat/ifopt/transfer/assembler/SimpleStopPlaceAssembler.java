package no.rutebanken.tiamat.ifopt.transfer.assembler;


import no.rutebanken.tiamat.ifopt.transfer.dto.SimpleStopPlaceDTO;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.StopPlace;

@Component
public class SimpleStopPlaceAssembler {

    public SimpleStopPlaceDTO assemble(StopPlace stopPlace) {
        SimpleStopPlaceDTO simpleStopPlaceDTO = new SimpleStopPlaceDTO();
        simpleStopPlaceDTO.id = stopPlace.getId();

        if(stopPlace.getName() != null) simpleStopPlaceDTO.name = stopPlace.getName().getValue();
        if(stopPlace.getStopPlaceType() != null) simpleStopPlaceDTO.stopPlaceType = stopPlace.getStopPlaceType().value();

        return simpleStopPlaceDTO;
    }

}