package org.rutebanken.tiamat.netex.mapping.converter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LineStringType;
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

    @Autowired
    public LineStringConverter(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    @Override
    public LineString convertTo(LineStringType lineStringType, Type<LineString> type) {

        if(lineStringType.getPosList() != null) {
            checkSrsDimension(lineStringType);

            List<Double> values = lineStringType.getPosList().getValue();
            Coordinate[] coordinates = new Coordinate[values.size()/2];
            int coordinateIndex = 0;
            for (int index = 0; index < values.size(); index += 2) {
                Coordinate coordinate = new Coordinate(values.get(index), values.get(index+1));
                logger.debug("Parsed coordinate: {}", coordinate);
                coordinates[coordinateIndex++] = coordinate;
            }

            LineString lineString = new LineString(new CoordinateArraySequence(coordinates), geometryFactory);

            return lineString;
        }

        return null;
    }

    private void checkSrsDimension(LineStringType lineStringType) {
        boolean lineStringContains = lineStringType.getSrsDimension() != null && lineStringType.getSrsDimension().intValue() == geometryFactory.getSRID();
        boolean posListContains = lineStringType.getPosList() != null && lineStringType.getPosList().getSrsDimension().intValue() == geometryFactory.getSRID();

        if (!lineStringContains && !posListContains) {
            throw new NetexMappingException("SRS ID is not provided or has the wrong value for LineStringType or PosList: "
                    + " Expected " + geometryFactory.getSRID());
        }
    }

    @Override
    public LineStringType convertFrom(LineString lineString, Type<LineStringType> type) {

        LineStringType lineStringType = new LineStringType();

        DirectPositionListType directPositionListType = new DirectPositionListType();

        if(lineString.getCoordinates() != null) {
            logger.debug("Converting coordinates {}", lineString.getCoordinates());
            List<Double> positions = directPositionListType.getValue();
            for(Coordinate coordinate : lineString.getCoordinates()) {
                positions.add(coordinate.x);
                positions.add(coordinate.y);
            }
            directPositionListType.setCount(BigInteger.valueOf(positions.size()));
            directPositionListType.setSrsDimension(BigInteger.valueOf(lineString.getSRID()));
        }
        lineStringType.setPosList(directPositionListType);
        lineStringType.setId(LineString.class.getSimpleName());

        return lineStringType;
    }
}
