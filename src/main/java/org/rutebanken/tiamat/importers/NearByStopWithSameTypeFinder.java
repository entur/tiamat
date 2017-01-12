package org.rutebanken.tiamat.importers;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.Transformation;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Some nearby stops like airports can be treated as the same if they are close enough.
 */
@Component
public class NearByStopWithSameTypeFinder implements StopPlaceFinder {

    private static final Logger logger = LoggerFactory.getLogger(NearByStopWithSameTypeFinder.class);

    private final StopPlaceRepository stopPlaceRepository;


    /**
     *
     * @param stopPlaceRepository
     */
    @Autowired
    public NearByStopWithSameTypeFinder(StopPlaceRepository stopPlaceRepository) {
        this.stopPlaceRepository = stopPlaceRepository;
    }


    @Override
    public StopPlace find(StopPlace stopPlace) {


        try {

            Envelope envelope = createBoundingBox(stopPlace.getCentroid(), 800.00);
            long stopPlaceId = stopPlaceRepository.findNearbyStopPlace(envelope, stopPlace.getStopPlaceType());
            if(stopPlaceId > 0) {
                logger.debug("Found nearby match on type with stop place ID {}", stopPlaceId);
            }
        } catch (FactoryException|TransformException e) {
            logger.error("Could not find nearby stop from type and buffer", e);
        }
        return null;
    }


    public Envelope createBoundingBox(Point point, double meters) throws FactoryException, TransformException {
        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:32633");

        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
        Geometry metricGeometry = JTS.transform(point, transform);
        metricGeometry.buffer(meters);

        MathTransform transformBack = CRS.findMathTransform(targetCRS, sourceCRS);

        Geometry buffer = JTS.transform(metricGeometry, transformBack);
        Envelope envelope = buffer.getEnvelopeInternal();

        return envelope;
    }
}
