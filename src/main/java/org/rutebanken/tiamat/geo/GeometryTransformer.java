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

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import static org.geotools.referencing.crs.DefaultGeographicCRS.WGS84;

public class GeometryTransformer {

    private static final String WGS84_EPSG = "EPSG:4326";
    private final CoordinateReferenceSystem utm;
    private final CoordinateReferenceSystem wgs84;
    private final Geometry geometry;

    /**
     * @param geometry Geometry to transform into UTM
     */
    public GeometryTransformer(Geometry geometry) throws FactoryException {
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);

        utm = factory.createCoordinateReferenceSystem(findUtmCrs(geometry.getCoordinate().x));
        wgs84 = factory.createCoordinateReferenceSystem(WGS84_EPSG);
        this.geometry = geometry;
    }

    public Geometry transformToUtm() throws FactoryException, TransformException {
        MathTransform transform = CRS.findMathTransform(WGS84, utm);
        return JTS.transform(geometry, transform);
    }

    public Geometry transformToWgs84(Geometry utmGeometry) throws FactoryException, TransformException {
        MathTransform transform = CRS.findMathTransform(utm, wgs84);
        return JTS.transform(utmGeometry, transform);
    }

    public static String findUtmCrs(double longitude) {
        int zone = (int) (1 + Math.floor((longitude+180)/6));
        return "EPSG:326"+zone;
    }
}
