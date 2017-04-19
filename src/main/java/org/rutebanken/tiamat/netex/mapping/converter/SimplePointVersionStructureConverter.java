package org.rutebanken.tiamat.netex.mapping.converter;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class SimplePointVersionStructureConverter extends BidirectionalConverter<Point, SimplePoint_VersionStructure> {

    private static final Logger logger = LoggerFactory.getLogger(SimplePointVersionStructureConverter.class);

    private static final int SCALE = 6;

    private static CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);

    private final GeometryFactory geometryFactory;

    private final CoordinateReferenceSystem epsg4326;

    private final String internalSrsName;

    @Autowired
    public SimplePointVersionStructureConverter(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
         internalSrsName = "EPSG:" + geometryFactory.getSRID();
        try {
            epsg4326 = factory.createCoordinateReferenceSystem(internalSrsName);
        } catch (FactoryException e) {
            logger.warn("Cannot create coordinatereferenceSystem {}", internalSrsName, e);
            throw new RuntimeException(e);
        }
    }


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
    public Point convertFrom(SimplePoint_VersionStructure simplePoint, Type<Point> type) {

        if(simplePoint != null
                && simplePoint.getLocation() != null) {

            if(noCoordinatesSet(simplePoint)) {
                logger.warn("Could not find long/lat or pos from location: {}", simplePoint.getLocation());
                return null;
            }

            if(hasLongLat(simplePoint)) {
                logger.debug("Detected longitude and latitude: {}", simplePoint);
                String sourceSrsName = simplePoint.getLocation().getSrsName();
                Coordinate coordinate = convertAndRoundLongLat(simplePoint);
                return transformIfDifferentSrs(coordinate, sourceSrsName);

            } else if(simplePoint.getLocation().getPos() != null) {
                logger.debug("Detected pos value: {}", simplePoint);
                String sourceSrsName = simplePoint.getLocation().getPos().getSrsName();

                List<Double> values = simplePoint.getLocation().getPos().getValue();
                if(values.size() < 2) {
                    logger.warn("Pos list does not contain 2 or more coordinates: {}", simplePoint);
                    return null;
                }

                Coordinate coordinate = new Coordinate(values.get(1), values.get(0));
                return transformIfDifferentSrs(coordinate, sourceSrsName);
            }
        }
        return null;
    }

    private Point transformIfDifferentSrs(Coordinate coordinate, String sourceSrsName) {

        if(Strings.isNullOrEmpty(sourceSrsName)) {
            logger.debug("SRS is null or empty. Assuming {}: {}", geometryFactory.getSRID(), sourceSrsName);
        } else if(!sourceSrsName.equals(internalSrsName)) {
            Coordinate transformed = transform(coordinate, sourceSrsName);
            if(transformed == null) {
                return null;
            } else {
                return geometryFactory.createPoint(transformed);
            }
        }
        return geometryFactory.createPoint(coordinate);
    }

    private Coordinate transform(Coordinate source, String srsName) {
        try {
            logger.debug("Transforming {} from {}", source, srsName);
            CoordinateReferenceSystem fromCoordinateReferenceSystem = factory.createCoordinateReferenceSystem(srsName);
            MathTransform transform = CRS.findMathTransform(fromCoordinateReferenceSystem, epsg4326);
            Coordinate destination = JTS.transform(source, null, transform);
            logger.debug("Transformed {} into {}", source, destination);
            return destination;

        } catch (TransformException|FactoryException e) {
            logger.warn("Cannot transform coordinate {} to internal coordinate reference system", source, e);
            // Do not return coordinate without transformation
            return null;
        }
    }

    private boolean hasLongLat(SimplePoint_VersionStructure simplePoint) {
        return simplePoint.getLocation().getLongitude() != null && simplePoint.getLocation().getLatitude() != null;
    }

    private boolean noCoordinatesSet(SimplePoint_VersionStructure simplePoint) {
        return simplePoint.getLocation().getLongitude() == null && simplePoint.getLocation().getLatitude() == null && simplePoint.getLocation().getPos() == null;
    }

    private Coordinate convertAndRoundLongLat(SimplePoint_VersionStructure simplePoint) {
        logger.debug("Converting point {}", simplePoint);

        return new Coordinate(round(simplePoint.getLocation().getLongitude()).doubleValue(),
                        round(simplePoint.getLocation().getLatitude()).doubleValue());
    }

    private BigDecimal round(BigDecimal bigDecimal) {
        return bigDecimal.setScale(SCALE, RoundingMode.HALF_UP);
    }
}
