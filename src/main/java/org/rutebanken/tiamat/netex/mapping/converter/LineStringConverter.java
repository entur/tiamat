package org.rutebanken.tiamat.netex.mapping.converter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LineStringType;
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
        return null;
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
        lineStringType.withPosList(directPositionListType);

        return lineStringType;
    }
}
