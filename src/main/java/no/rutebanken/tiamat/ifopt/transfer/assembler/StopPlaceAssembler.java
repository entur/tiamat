package no.rutebanken.tiamat.ifopt.transfer.assembler;


import no.rutebanken.tiamat.ifopt.transfer.dto.StopPlaceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.StopPlace;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class StopPlaceAssembler {

    @Autowired
    private SimplePointAssembler simplePointAssembler;

    @Autowired
    private QuayAssembler quayAssembler;

    public StopPlaceDTO assemble(StopPlace stopPlace) {
        StopPlaceDTO simpleStopPlaceDTO = new StopPlaceDTO();
        simpleStopPlaceDTO.id = stopPlace.getId();

        simpleStopPlaceDTO.name = multiLingualStringValue(stopPlace.getName());
        simpleStopPlaceDTO.shortName = multiLingualStringValue(stopPlace.getShortName());
        simpleStopPlaceDTO.description = multiLingualStringValue(stopPlace.getDescription());
        if(stopPlace.getStopPlaceType() != null) simpleStopPlaceDTO.stopPlaceType = stopPlace.getStopPlaceType().value();

        simpleStopPlaceDTO.centroid = simplePointAssembler.assemble(stopPlace.getCentroid());

        simpleStopPlaceDTO.quays = stopPlace.getQuays()
                .stream()
                .map(quay -> quayAssembler.assemble(quay))
                .collect(Collectors.toList());

        return simpleStopPlaceDTO;
    }

    public List<StopPlaceDTO> assemble(Page<StopPlace> stopPlaces) {
        if(stopPlaces != null) {
            return stopPlaces.getContent()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(this::assemble)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public String multiLingualStringValue(MultilingualString multilingualString) {

        if(multilingualString != null) {
            return multilingualString.getValue();
        }
        return null;
    }

}