package org.rutebanken.tiamat.geo;

import com.google.common.base.MoreObjects;
import com.vividsolutions.jts.algorithm.CentroidPoint;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.operation.TransformException;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class CentroidComputer {

    private static final Logger logger = LoggerFactory.getLogger(CentroidComputer.class);

    /**
     * The threshold in meters for distance between stop place and quay centroid.
     * If more than this limit, log a warning;
     */
    public static final int DISTANCE_WARNING_METERS = 400;

    private GeometryFactory geometryFactory;


    @Autowired
    public CentroidComputer(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    public boolean computeCentroidForStopPlace(StopPlace stopPlace) {
        Optional<Point> optionalPoint = computeCentroidForStopPlace(stopPlace.getQuays());

        if(optionalPoint.isPresent()) {
            Point point = optionalPoint.get();
            boolean changed = stopPlace.getCentroid() == null || !point.equals(stopPlace.getCentroid());
            stopPlace.setCentroid(point);
            if(changed) {
                logger.debug("Created centroid {} for stop place based on quays. {}", point, stopPlace);

                stopPlace.getQuays().forEach(quay -> {
                    try {
                        if(quay.getCentroid() != null) {
                            double distanceInMeters = JTS.orthodromicDistance(
                                    quay.getCentroid().getCoordinate(),
                                    stopPlace.getCentroid().getCoordinate(),
                                    DefaultGeographicCRS.WGS84);

                            if (distanceInMeters > DISTANCE_WARNING_METERS) {
                                String stopPlaceString = MoreObjects.toStringHelper(stopPlace)
                                        .omitNullValues()
                                        .add("name", stopPlace.getName() == null ? null : stopPlace.getName().getValue())
                                        .add("originalId", stopPlace.getOriginalIds())
                                        .toString();

                                logger.warn("Calculated stop place centroid with {} meters from quay. {} Quay {}",
                                        distanceInMeters, stopPlaceString, quay.getOriginalIds());
                            }
                        }
                    } catch (TransformException e) {
                        logger.warn("Could not determine orthodromic distance between quay and stop place {}", stopPlace);
                    }
                });


            }
            return changed;
        }

        return false;
    }

    public Optional<Point> computeCentroidForStopPlace(Set<Quay> quays) {
        CentroidPoint centroidPoint = new CentroidPoint();

        boolean anyAdded = false;
        if(quays != null) {
            for (Quay quay : quays) {
                if (quay.getCentroid() != null) {
                    centroidPoint.add(quay.getCentroid());
                    anyAdded = true;
                }
            }
        }
        if(anyAdded) {
            Point point = geometryFactory.createPoint(centroidPoint.getCentroid());
            return Optional.of(point);
        }
        else return Optional.empty();

    }
}
