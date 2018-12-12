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

import com.google.common.base.MoreObjects;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.operation.TransformException;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StopPlaceCentroidComputer {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceCentroidComputer.class);

    /**
     * The threshold in meters for distance between stop place and quay centroid.
     * If more than this limit, log a warning;
     */
    public static final int DISTANCE_WARNING_METERS = 400;

    private final CentroidComputer centroidComputer;


    @Autowired
    public StopPlaceCentroidComputer(CentroidComputer centroidComputer) {
        this.centroidComputer = centroidComputer;
    }

    public boolean computeCentroidForStopPlace(StopPlace stopPlace) {
        Optional<Point> optionalPoint = centroidComputer.compute(stopPlace.getQuays());

        if(optionalPoint.isPresent()) {

            // Check each quay's distance to stop place centroid.
            // Intention was to reveal quays far avay from eah other, but this incorrectly checks against stop place centroid which has been generated.
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


}
