package no.rutebanken.tiamat.ifopt.transfer.assembler;


import no.rutebanken.tiamat.ifopt.transfer.dto.StopPlaceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.StopPlace;

@Component
public class StopPlaceAssembler {

    @Autowired
    private SimplePointAssembler simplePointAssembler;


    public StopPlaceDTO assemble(StopPlace stopPlace) {
        StopPlaceDTO simpleStopPlaceDTO = new StopPlaceDTO();
        simpleStopPlaceDTO.id = stopPlace.getId();

        simpleStopPlaceDTO.name = multiLingualStringValue(stopPlace.getName());
        simpleStopPlaceDTO.shortName = multiLingualStringValue(stopPlace.getShortName());
        simpleStopPlaceDTO.description = multiLingualStringValue(stopPlace.getDescription());
        if(stopPlace.getStopPlaceType() != null) simpleStopPlaceDTO.stopPlaceType = stopPlace.getStopPlaceType().value();

        simpleStopPlaceDTO.centroid = simplePointAssembler.assemble(stopPlace.getCentroid());

        return simpleStopPlaceDTO;
    }

    public String multiLingualStringValue(MultilingualString multilingualString) {

        if(multilingualString != null) {
            return multilingualString.getValue();
        }
        return null;
    }

}