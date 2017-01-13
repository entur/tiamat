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
        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:32633");

        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
        Geometry metricGeometry = JTS.transform(point, transform);
        Geometry metricBuffer = metricGeometry.buffer(meters);

        // Back to source
        MathTransform transformBack = CRS.findMathTransform(targetCRS, sourceCRS);

        Geometry buffer = JTS.transform(metricBuffer, transformBack);
        Envelope envelope = buffer.getEnvelopeInternal();

        return envelope;
    }

}
