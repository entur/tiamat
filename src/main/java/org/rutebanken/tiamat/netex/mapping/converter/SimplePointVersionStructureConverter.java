package org.rutebanken.tiamat.netex.mapping.converter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class SimplePointVersionStructureConverter extends BidirectionalConverter<Point, SimplePoint_VersionStructure> {

    private static final int SCALE = 6;

    private final GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();


    @Override
    public SimplePoint_VersionStructure convertTo(Point point, Type<SimplePoint_VersionStructure> type) {
        if(point == null) {
            return null;
        }

        BigDecimal longitude = round(BigDecimal.valueOf(point.getX()));
        BigDecimal latitude = round(BigDecimal.valueOf(point.getY()));

        return new SimplePoint_VersionStructure()
                .withLocation(new LocationStructure()
                    .withLongitude(longitude)
                    .withLatitude(latitude));
    }

    @Override
    public Point convertFrom(SimplePoint_VersionStructure simplePointVersionStructure, Type<Point> type) {
        if(simplePointVersionStructure != null
                && simplePointVersionStructure.getLocation() != null
                && simplePointVersionStructure.getLocation().getLongitude() != null
                && simplePointVersionStructure.getLocation().getLatitude() != null) {

            return geometryFactory.createPoint(
                    new Coordinate(round(simplePointVersionStructure.getLocation().getLongitude()).doubleValue(),
                            round(simplePointVersionStructure.getLocation().getLatitude()).doubleValue()));

        }
        return null;
    }

    private BigDecimal round(BigDecimal bigDecimal) {
        return bigDecimal.setScale(SCALE, RoundingMode.HALF_UP);
    }


}
