package org.rutebanken.tiamat.model;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.jboss.logging.Logger;
import org.opengis.referencing.operation.TransformException;
import org.springframework.stereotype.Component;

@Component
public class ZoneDistanceChecker {

    public static final int DEFAULT_MAX_DISTANCE = 1000;

    private static final Logger logger = Logger.getLogger(ZoneDistanceChecker.class);

    public double getDistanceInMeters(Zone_VersionStructure zone1, Zone_VersionStructure zone2) throws TransformException {

        if(zone1 == null || zone1.getCentroid() == null || zone2 == null || zone2.getCentroid() == null) {
            logger.trace("Cannot calculate distance when one of the zones does not have centroid");
            return 0;
        }

        return JTS.orthodromicDistance(
                zone1.getCentroid().getCoordinate(),
                zone2.getCentroid().getCoordinate(),
                DefaultGeographicCRS.WGS84);
    }

    public boolean exceedsLimit(Zone_VersionStructure zone1, Zone_VersionStructure zone2) {
        return exceedsLimit(zone1, zone2, DEFAULT_MAX_DISTANCE);
    }

    public boolean exceedsLimit(Zone_VersionStructure zone1, Zone_VersionStructure zone2, int limit) {
        double distanceInMeters = 0;
        try {
            distanceInMeters = getDistanceInMeters(zone1, zone2);
        } catch (TransformException e) {
            logger.warn("Caught expcetion calculating distance. Return false.", e);
            return false;
        }
        return distanceInMeters > limit;
    }
}
