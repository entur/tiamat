package org.rutebanken.tiamat.dtoassembling.disassembler;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.dtoassembling.dto.LocationDto;
import org.rutebanken.tiamat.dtoassembling.dto.SimplePointDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PointDisassembler {

    private final GeometryFactory geometryFactory;

    @Autowired
    public PointDisassembler(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    public Point disassemble(SimplePointDto simplePointDto) {

        if(simplePointDto == null || simplePointDto.location == null) return null;

        LocationDto locationDto = simplePointDto.location;

        Point point = geometryFactory.createPoint(new Coordinate(locationDto.longitude, locationDto.latitude));
        return point;
    }
}
