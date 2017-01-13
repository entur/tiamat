package org.rutebanken.tiamat.geo;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.springframework.stereotype.Service;

@Service
public class EnvelopeCreator {

    /**
     * Create bounding box from point, by specifying meters
     * @param point point to create envelope around
     * @param meters number of meters aournd this point
     * @return Envelope
     * @throws FactoryException
     * @throws TransformException
     */
    public Envelope createFromPoint(Point point, double meters) throws FactoryException, TransformException {
        CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem utm33n = CRS.decode("EPSG:32633");

        MathTransform transform = CRS.findMathTransform(wgs84, utm33n);
        Geometry utm33nGeometry = JTS.transform(point, transform);
        Geometry metricBuffer = utm33nGeometry.buffer(meters);

        MathTransform transformBack = CRS.findMathTransform(utm33n, wgs84);

        Geometry buffer = JTS.transform(metricBuffer, transformBack);
        Envelope envelope = buffer.getEnvelopeInternal();

        return envelope;
    }

}
