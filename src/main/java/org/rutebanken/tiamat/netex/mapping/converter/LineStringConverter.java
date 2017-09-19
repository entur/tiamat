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

package org.rutebanken.tiamat.netex.mapping.converter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LineStringType;
import org.rutebanken.tiamat.geo.DoubleValuesToCoordinateSequence;
import org.rutebanken.tiamat.netex.mapping.NetexMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Component
public class LineStringConverter extends BidirectionalConverter<LineStringType, LineString> {

    private static final Logger logger = LoggerFactory.getLogger(LineStringConverter.class);

    private final GeometryFactory geometryFactory;

    private final DoubleValuesToCoordinateSequence doubleValuesToCoordinateSequence;

    @Autowired
    public LineStringConverter(GeometryFactory geometryFactory, DoubleValuesToCoordinateSequence doubleValuesToCoordinateSequence) {
        this.geometryFactory = geometryFactory;
        this.doubleValuesToCoordinateSequence = doubleValuesToCoordinateSequence;
    }

    @Override
    public LineString convertTo(LineStringType lineStringType, Type<LineString> type, MappingContext mappingContext) {

        if(lineStringType.getPosList() != null) {
            List<Double> values = lineStringType.getPosList().getValue();

            CoordinateSequence coordinateSequence = doubleValuesToCoordinateSequence.convert(values);
            LineString lineString = new LineString(coordinateSequence, geometryFactory);

            return lineString;
        }

        return null;
    }

    @Override
    public LineStringType convertFrom(LineString lineString, Type<LineStringType> typ, MappingContext mappingContext) {

        LineStringType lineStringType = new LineStringType();

        DirectPositionListType directPositionListType = new DirectPositionListType();

        if(lineString.getCoordinates() != null) {
            logger.debug("Converting coordinates {}", lineString.getCoordinates());
            List<Double> positions = directPositionListType.getValue();
            for(Coordinate coordinate : lineString.getCoordinates()) {
                positions.add(coordinate.y);
                positions.add(coordinate.x);
            }
            directPositionListType.setCount(BigInteger.valueOf(positions.size()));
            directPositionListType.setSrsDimension(BigInteger.valueOf(2L));
        }
        lineStringType.setPosList(directPositionListType);
        lineStringType.setId(LineString.class.getSimpleName());
        lineStringType.setSrsDimension(BigInteger.valueOf(2L));

        return lineStringType;
    }
}
