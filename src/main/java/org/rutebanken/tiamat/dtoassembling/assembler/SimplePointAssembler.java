package org.rutebanken.tiamat.dtoassembling.assembler;

import org.rutebanken.tiamat.dtoassembling.dto.LocationDto;
import org.rutebanken.tiamat.dtoassembling.dto.SimplePointDto;
import org.springframework.stereotype.Component;
import org.rutebanken.tiamat.model.SimplePoint;

@Component
public class SimplePointAssembler {

    public SimplePointDto assemble(SimplePoint simplePoint) {

        if(simplePoint == null) {
            return null;
        }

        SimplePointDto simplePointDto = new SimplePointDto();
        simplePointDto.location = new LocationDto();

        if (simplePoint.getLocation() != null) {
            simplePointDto.location.latitude = simplePoint.getLocation().getLatitude().doubleValue();
            simplePointDto.location.longitude = simplePoint.getLocation().getLongitude().doubleValue();
        }
        return simplePointDto;
    }

}