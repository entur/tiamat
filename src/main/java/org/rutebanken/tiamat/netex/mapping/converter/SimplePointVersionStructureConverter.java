package org.rutebanken.tiamat.netex.mapping.converter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;

import java.math.BigDecimal;

public class SimplePointVersionStructureConverter extends BidirectionalConverter<Point, SimplePoint_VersionStructure> {

    private final GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    @Override
    public SimplePoint_VersionStructure convertTo(Point point, Type<SimplePoint_VersionStructure> type) {
        if(point == null) {
            return null;
        }

        return new SimplePoint_VersionStructure()
                .withLocation(new LocationStructure()
                    .withLongitude(BigDecimal.valueOf(point.getX()))
                    .withLatitude(BigDecimal.valueOf(point.getY())));
    }

    @Override
    public Point convertFrom(SimplePoint_VersionStructure simplePointVersionStructure, Type<Point> type) {
        if(simplePointVersionStructure != null
                && simplePointVersionStructure.getLocation() != null
                && simplePointVersionStructure.getLocation().getLongitude() != null
                && simplePointVersionStructure.getLocation().getLatitude() != null) {

            return geometryFactory.createPoint(
                    new Coordinate(simplePointVersionStructure.getLocation().getLongitude().doubleValue(),
                            simplePointVersionStructure.getLocation().getLatitude().doubleValue()));

        }
        return null;
    }


}
