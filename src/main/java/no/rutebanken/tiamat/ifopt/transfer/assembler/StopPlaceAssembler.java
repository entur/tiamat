package no.rutebanken.tiamat.ifopt.transfer.assembler;


import no.rutebanken.tiamat.ifopt.transfer.dto.StopPlaceDTO;
import no.rutebanken.tiamat.repository.ifopt.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public StopPlaceDTO assemble(StopPlace stopPlace) {
        StopPlaceDTO stopPlaceDTO = new StopPlaceDTO();
        stopPlaceDTO.id = stopPlace.getId();

        stopPlaceDTO.name = multiLingualStringValue(stopPlace.getName());
        stopPlaceDTO.shortName = multiLingualStringValue(stopPlace.getShortName());
        stopPlaceDTO.description = multiLingualStringValue(stopPlace.getDescription());
        if(stopPlace.getStopPlaceType() != null) stopPlaceDTO.stopPlaceType = stopPlace.getStopPlaceType().value();
        stopPlaceDTO.centroid = simplePointAssembler.assemble(stopPlace.getCentroid());

        if(stopPlace.isAllAreasWheelchairAccessible() != null) {
            stopPlaceDTO.allAreasWheelchairAccessible = stopPlace.isAllAreasWheelchairAccessible();
        }

        stopPlaceDTO.quays = stopPlace.getQuays()
                .stream()
                .map(quay -> quayAssembler.assemble(quay))
                .collect(Collectors.toList());

        stopPlaceDTO = assembleMunicipalityAndCounty(stopPlaceDTO, stopPlace);

        return stopPlaceDTO;
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

    public StopPlaceDTO assembleMunicipalityAndCounty(StopPlaceDTO stopPlaceDTO, StopPlace stopPlace) {
        TopographicPlaceRefStructure topographicRef = stopPlace.getTopographicPlaceRef();
        if(topographicRef != null) {
            logger.trace("Found reference from stop place '{}' {} to a topographic place {}", stopPlace.getName(), stopPlace.getId(), topographicRef.getRef());

            TopographicPlace municipality = topographicPlaceRepository.findOne(topographicRef.getRef());

            if (municipality == null) {
                logger.warn("Municipality was null from reference {}", topographicRef.getRef());
                return stopPlaceDTO;
            }

            if (municipality.getName() != null) {
                stopPlaceDTO.municipality = municipality.getName().getValue();
            }

            logger.trace("Set municipality name '{}' on stop place '{}' {}", stopPlaceDTO.municipality, stopPlace.getName(), stopPlace.getId());

            if(municipality.getParentTopographicPlaceRef() != null) {

                TopographicPlace county = topographicPlaceRepository.findOne(municipality.getParentTopographicPlaceRef().getRef());

                if(county != null && county.getName() != null) {
                    logger.trace("Found county '{}' {} from municipality '{}' {}", county.getName(), county.getId(), municipality.getName(), municipality.getId());
                    stopPlaceDTO.county = county.getName().getValue();
                }
            } else {
                logger.warn("Found no county reference (parent topographic place ref) from municipality '{}' {}", municipality.getName(), municipality.getId());
            }

        }

        return stopPlaceDTO;
    }

    public String multiLingualStringValue(MultilingualString multilingualString) {

        if(multilingualString != null) {
            return multilingualString.getValue();
        }
        return null;
    }

}