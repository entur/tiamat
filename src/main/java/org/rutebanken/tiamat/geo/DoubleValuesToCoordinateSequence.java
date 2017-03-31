package org.rutebanken.tiamat.geo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DoubleValuesToCoordinateSequence {

    private static final Logger logger = LoggerFactory.getLogger(DoubleValuesToCoordinateSequence.class);

    public CoordinateSequence convert(List<Double> values) {
        Coordinate[] coordinates = new Coordinate[values.size()/2];
        int coordinateIndex = 0;
        for (int index = 0; index < values.size(); index += 2) {
            Coordinate coordinate = new Coordinate(values.get(index), values.get(index+1));
            logger.debug("Parsed coordinate: {}", coordinate);
            coordinates[coordinateIndex++] = coordinate;
        }
        return new CoordinateArraySequence(coordinates);
    }

}
