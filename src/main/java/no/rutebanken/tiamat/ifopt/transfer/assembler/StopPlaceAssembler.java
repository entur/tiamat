package no.rutebanken.tiamat.ifopt.transfer.assembler;


import no.rutebanken.tiamat.ifopt.transfer.dto.StopPlaceDTO;
import no.rutebanken.tiamat.repository.ifopt.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.StopPlace;
import uk.org.netex.netex.TopographicPlace;
import uk.org.netex.netex.TopographicPlaceRefStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class StopPlaceAssembler {

    @Autowired
    private SimplePointAssembler simplePointAssembler;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;


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

        if(stopPlace.isAllAreasWheelchairAccessible() != null) {
            simpleStopPlaceDTO.allAreasWheelchairAccessible = stopPlace.isAllAreasWheelchairAccessible();
        }

        simpleStopPlaceDTO.quays = stopPlace.getQuays()
                .stream()
                .map(quay -> quayAssembler.assemble(quay))
                .collect(Collectors.toList());

        TopographicPlaceRefStructure topographicRef = stopPlace.getTopographicPlaceRef();
        if(topographicRef != null) {
            TopographicPlace topographicPlace = topographicPlaceRepository.findOne(topographicRef.getRef());

            simpleStopPlaceDTO.topographicPlace = topographicPlace.getName().getValue();
        }

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