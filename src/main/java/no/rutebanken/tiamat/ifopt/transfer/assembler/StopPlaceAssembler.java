package no.rutebanken.tiamat.ifopt.transfer.assembler;


import no.rutebanken.tiamat.ifopt.transfer.dto.StopPlaceDto;
import no.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import no.rutebanken.tiamat.model.MultilingualString;
import no.rutebanken.tiamat.model.StopPlace;
import no.rutebanken.tiamat.model.TopographicPlace;
import no.rutebanken.tiamat.model.TopographicPlaceRefStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class StopPlaceAssembler {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceAssembler.class);

    private SimplePointAssembler simplePointAssembler;

    private TopographicPlaceRepository topographicPlaceRepository;

    private QuayAssembler quayAssembler;

    @Autowired
    public StopPlaceAssembler(SimplePointAssembler simplePointAssembler, TopographicPlaceRepository topographicPlaceRepository, QuayAssembler quayAssembler) {
        this.simplePointAssembler = simplePointAssembler;
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.quayAssembler = quayAssembler;
    }

    public StopPlaceDto assemble(StopPlace stopPlace) {
        StopPlaceDto stopPlaceDto = new StopPlaceDto();
        stopPlaceDto.id = stopPlace.getId();

        stopPlaceDto.name = multiLingualStringValue(stopPlace.getName());
        stopPlaceDto.shortName = multiLingualStringValue(stopPlace.getShortName());
        stopPlaceDto.description = multiLingualStringValue(stopPlace.getDescription());
        if(stopPlace.getStopPlaceType() != null) stopPlaceDto.stopPlaceType = stopPlace.getStopPlaceType().value();
        stopPlaceDto.centroid = simplePointAssembler.assemble(stopPlace.getCentroid());

        if(stopPlace.isAllAreasWheelchairAccessible() != null) {
            stopPlaceDto.allAreasWheelchairAccessible = stopPlace.isAllAreasWheelchairAccessible();
        }

        stopPlaceDto.quays = stopPlace.getQuays()
                .stream()
                .map(quay -> quayAssembler.assemble(quay))
                .collect(Collectors.toList());

        stopPlaceDto = assembleMunicipalityAndCounty(stopPlaceDto, stopPlace);

        return stopPlaceDto;
    }

    public List<StopPlaceDto> assemble(Page<StopPlace> stopPlaces) {
        if(stopPlaces != null) {
            return stopPlaces.getContent()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(this::assemble)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public StopPlaceDto assembleMunicipalityAndCounty(StopPlaceDto stopPlaceDto, StopPlace stopPlace) {
        TopographicPlaceRefStructure topographicRef = stopPlace.getTopographicPlaceRef();
        if(topographicRef != null) {
            logger.trace("Found reference from stop place '{}' {} to a topographic place {}", stopPlace.getName(), stopPlace.getId(), topographicRef.getRef());

            TopographicPlace municipality = topographicPlaceRepository.findOne(topographicRef.getRef());

            if (municipality == null) {
                logger.warn("Municipality was null from reference {}", topographicRef.getRef());
                return stopPlaceDto;
            }

            if (municipality.getName() != null) {
                stopPlaceDto.municipality = municipality.getName().getValue();
            }

            logger.trace("Set municipality name '{}' on stop place '{}' {}", stopPlaceDto.municipality, stopPlace.getName(), stopPlace.getId());

            if(municipality.getParentTopographicPlaceRef() != null) {

                TopographicPlace county = topographicPlaceRepository.findOne(municipality.getParentTopographicPlaceRef().getRef());

                if(county != null && county.getName() != null) {
                    logger.trace("Found county '{}' {} from municipality '{}' {}", county.getName(), county.getId(), municipality.getName(), municipality.getId());
                    stopPlaceDto.county = county.getName().getValue();
                }
            } else {
                logger.warn("Found no county reference (parent topographic place ref) from municipality '{}' {}", municipality.getName(), municipality.getId());
            }

        }

        return stopPlaceDto;
    }

    public String multiLingualStringValue(MultilingualString multilingualString) {

        if(multilingualString != null) {
            return multilingualString.getValue();
        }
        return null;
    }

}