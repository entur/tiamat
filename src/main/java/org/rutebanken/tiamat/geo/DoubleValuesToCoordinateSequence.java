/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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
            // Swap coordinates. Tiamat uses x,y. Netex is usually y,x
            Coordinate coordinate = new Coordinate(values.get(index+1), values.get(index));
            logger.debug("Parsed coordinate: {}", coordinate);
            coordinates[coordinateIndex++] = coordinate;
        }
        return new CoordinateArraySequence(coordinates);
    }

}
