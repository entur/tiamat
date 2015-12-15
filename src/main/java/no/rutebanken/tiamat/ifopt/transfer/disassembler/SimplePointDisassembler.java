package no.rutebanken.tiamat.ifopt.transfer.disassembler;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import no.rutebanken.tiamat.ifopt.transfer.dto.LocationDTO;
import no.rutebanken.tiamat.ifopt.transfer.dto.SimplePointDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.SimplePoint;

@Component
public class SimplePointDisassembler {

    private final GeometryFactory geometryFactory;

    @Autowired
    public SimplePointDisassembler(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    public SimplePoint disassemble(SimplePoint destination, SimplePointDTO simplePointDTO) {

        if(simplePointDTO.location == null) return null;

        LocationDTO locationDTO = simplePointDTO.location;

        Point location = geometryFactory.createPoint(new Coordinate(locationDTO.longitude, locationDTO.latitude));
        destination.setLocation(location);
        return destination;
    }
}
