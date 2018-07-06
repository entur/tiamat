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

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnvelopeCreator {

    private static final Logger logger = LoggerFactory.getLogger(EnvelopeCreator.class);

    private final GeometryFactory geometryFactory;

    @Autowired
    public EnvelopeCreator(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    /**
     * Create bounding box from point, by specifying meters
     * @param point point to create envelope around
     * @param meters number of meters aournd this point
     * @return Envelope
     * @throws FactoryException
     * @throws TransformException
     */
    public Envelope createFromPoint(Point point, double meters) throws FactoryException, TransformException {

        if(point.getSRID() != geometryFactory.getSRID()) {
            throw new RuntimeException("Excpected SRID " + geometryFactory.getSRID() + " but point had " + point.getSRID());
        }

        GeometryTransformer geometryTransformer = new GeometryTransformer(point);
        Geometry utmGeometry = geometryTransformer.transformToUtm();
        Geometry metricBuffer = utmGeometry.buffer(meters);

        Geometry buffer = geometryTransformer.transformToWgs84(metricBuffer);
        Envelope envelope = buffer.getEnvelopeInternal();
        logger.debug("Created envelope {} from point {}", envelope, point);
        return envelope;
    }
}
