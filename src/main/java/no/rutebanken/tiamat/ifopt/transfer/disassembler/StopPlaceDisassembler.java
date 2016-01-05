package no.rutebanken.tiamat.ifopt.transfer.disassembler;

import no.rutebanken.tiamat.ifopt.transfer.dto.StopPlaceDTO;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.SimplePoint;
import uk.org.netex.netex.StopPlace;
import uk.org.netex.netex.StopTypeEnumeration;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Disassembles the StopPlaceDTO to update a StopPlace.
 */
@Component
public class StopPlaceDisassembler {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceDisassembler.class);

    @Autowired
    private SimplePointDisassembler simplePointDisassembler;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;


    @Autowired
    private QuayDisassembler quayDisassembler;

    public StopPlace disassemble(StopPlace destination, StopPlaceDTO simpleStopPlaceDTO) {

        if(destination == null) {
            return null;
        }

        logger.debug("Disassemble simpleStopPlaceDTO with id {}", simpleStopPlaceDTO.id);

        destination.setName(new MultilingualString(simpleStopPlaceDTO.name, "no", ""));
        destination.setChanged(new Date());
        destination.setShortName(new MultilingualString(simpleStopPlaceDTO.shortName, "no", ""));
        destination.setDescription(new MultilingualString(simpleStopPlaceDTO.description, "no", ""));

        destination.setStopPlaceType(Optional.ofNullable(simpleStopPlaceDTO.stopPlaceType)
                .filter(type -> !type.isEmpty())
                .map(StopTypeEnumeration::fromValue)
                .orElse(null));

        if(simpleStopPlaceDTO.centroid != null) {
            if (destination.getCentroid() == null) {
                destination.setCentroid(new SimplePoint());
            }

            destination.setCentroid(simplePointDisassembler.disassemble(simpleStopPlaceDTO.centroid));
        }

        destination.setAllAreasWheelchairAccessible(simpleStopPlaceDTO.allAreasWheelchairAccessible);


        destination.setQuays(simpleStopPlaceDTO.quays
                .stream()
                .filter(Objects::nonNull)
                .map(quay -> quayDisassembler.disassemble(quay))
                .collect(Collectors.toList()));

       return destination;

    }
}
