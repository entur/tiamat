package org.rutebanken.tiamat.dtoassembling.assembler;

import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.dtoassembling.dto.LocationDto;
import org.rutebanken.tiamat.dtoassembling.dto.SimplePointDto;
import org.springframework.stereotype.Component;

@Component
public class PointAssembler {

    public SimplePointDto assemble(Point geometryPoint) {

        if(geometryPoint == null) {
            return null;
        }

        SimplePointDto simplePointDto = new SimplePointDto();
        simplePointDto.location = new LocationDto();


        simplePointDto.location.latitude = geometryPoint.getY();
        simplePointDto.location.longitude = geometryPoint.getX();

        return simplePointDto;
    }

}