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

import com.vividsolutions.jts.algorithm.CentroidPoint;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.model.Zone_VersionStructure;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class CentroidComputer {

    private final GeometryFactory geometryFactory;

    public CentroidComputer(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    /**
     * Computes point from a list of zone's centrois
      * @param zones netex zones
     * @return point or empty if none of the zones contained centroid point
     */
    public Optional<Point> compute(Set<? extends Zone_VersionStructure> zones) {
        CentroidPoint centroidPoint = new CentroidPoint();

        boolean anyAdded = false;
        if(zones != null) {
            for (Zone_VersionStructure zone : zones) {
                if (zone.getCentroid() != null) {
                    centroidPoint.add(zone.getCentroid());
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
