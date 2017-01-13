package org.rutebanken.tiamat.geo;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
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

    public String findUtmCrs(double longitude) {
        int zone = (int) (1 + Math.floor((longitude+180)/6));
        return "EPSG:326"+zone;
    }
}
