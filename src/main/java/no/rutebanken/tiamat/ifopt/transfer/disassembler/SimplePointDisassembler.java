package no.rutebanken.tiamat.ifopt.transfer.disassembler;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import no.rutebanken.tiamat.ifopt.transfer.dto.LocationDto;
import no.rutebanken.tiamat.ifopt.transfer.dto.SimplePointDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import no.rutebanken.tiamat.model.LocationStructure;
import no.rutebanken.tiamat.model.SimplePoint;

@Component
public class SimplePointDisassembler {

    private final GeometryFactory geometryFactory;

    @Autowired
    public SimplePointDisassembler(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    public SimplePoint disassemble(SimplePointDto simplePointDto) {

        if(simplePointDto == null || simplePointDto.location == null) return null;

        SimplePoint destination = new SimplePoint();
        LocationDto locationDto = simplePointDto.location;

        Point point = geometryFactory.createPoint(new Coordinate(locationDto.longitude, locationDto.latitude));
        destination.setLocation(new LocationStructure(point));
        return destination;
    }
}
