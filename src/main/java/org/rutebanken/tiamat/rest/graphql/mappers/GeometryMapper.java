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

package org.rutebanken.tiamat.rest.graphql.mappers;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GeometryMapper {

    @Autowired
    private GeometryFactory geometryFactory;

    public LineString createGeoJsonLineString(Map map) {
        if (map.get("type") != null && map.get("coordinates") != null) {
            if ("LineString".equals(map.get("type"))) {
                Coordinate[] coordinates = (Coordinate[]) map.get("coordinates");
                return geometryFactory.createLineString(coordinates);
            }
        }
        return null;
    }

    public Point createGeoJsonPoint(Map map) {
        if(map == null) {
            return null;
        }
        if (map.get("type") != null && map.get("coordinates") != null) {
            if ("Point".equals(map.get("type"))) {
                Coordinate[] coordinates = (Coordinate[]) map.get("coordinates");
                return geometryFactory.createPoint(coordinates[0]);
            }
        }
        return null;
    }
}
