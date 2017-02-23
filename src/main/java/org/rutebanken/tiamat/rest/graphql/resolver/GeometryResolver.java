package org.rutebanken.tiamat.rest.graphql.resolver;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GeometryResolver {

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
        if (map.get("type") != null && map.get("coordinates") != null) {
            if ("Point".equals(map.get("type"))) {
                Coordinate[] coordinates = (Coordinate[]) map.get("coordinates");
                return geometryFactory.createPoint(coordinates[0]);
            }
        }
        return null;
    }
}
