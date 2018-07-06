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

import org.locationtech.jts.algorithm.Centroid;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.model.Zone_VersionStructure;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CentroidComputer {

    private final GeometryFactory geometryFactory;

    public CentroidComputer(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    /**
     * Computes point from a list of zone's centrois
     *
     * @param zones netex zones
     * @return point or empty if none of the zones contained centroid point
     */
    public Optional<Point> compute(Set<? extends Zone_VersionStructure> zones) {

        Geometry[] geometries = zones.stream()
                .filter(zone -> zone.getCentroid() != null)
                .map(zone -> zone.getCentroid())
                .toArray(size -> new Geometry[size]);

        if (geometries.length > 0) {
            GeometryCollection geometryCollection = new GeometryCollection(geometries, geometryFactory);
            Centroid centroidPoint = new Centroid(geometryCollection);
            Point point = geometryFactory.createPoint(centroidPoint.getCentroid());
            return Optional.of(point);
        } else return Optional.empty();

    }
}
