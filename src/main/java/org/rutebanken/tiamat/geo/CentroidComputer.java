package org.rutebanken.tiamat.geo;

import com.vividsolutions.jts.algorithm.CentroidPoint;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
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
