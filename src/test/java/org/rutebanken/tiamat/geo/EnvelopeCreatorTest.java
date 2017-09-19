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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


public class EnvelopeCreatorTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();
    private EnvelopeCreator envelopeCreator = new EnvelopeCreator(geometryFactory);

    @Test
    public void envelopeIntersectsWithCoordinate() throws FactoryException, TransformException {
        Coordinate coordinate = new Coordinate(10.493860, 59.858690);
        Coordinate coveredCoordinate = new Coordinate(10.493858, 59.858616);
        Point point = geometryFactory.createPoint(coordinate);

        final int meters = 9;

        verifyDistanceInMetersLessThanOrEqualTo(coordinate, coveredCoordinate, meters);

        Envelope envelope = envelopeCreator.createFromPoint(point, meters);

        assertThat(envelope.intersects(coveredCoordinate)).as(coveredCoordinate + " should be covered by envelope "+envelope.toString()).isTrue();
        assertThat(envelope.intersects(coordinate)).isTrue();

    }

    /**
     * Test that a coordinate does not intersect with the created envelope.
     */
    @Test
    public void envelopeDoesNotIntersectWithCoordinate() throws FactoryException, TransformException {
        Coordinate coordinate = new Coordinate(10.493860, 59.858690);
        Coordinate notCoveredCoordinate = new Coordinate(10.493682, 59.858684);
        Point point = geometryFactory.createPoint(coordinate);

        final int meters = 9;

        Envelope envelope = envelopeCreator.createFromPoint(point, meters);

        assertThat(envelope.intersects(notCoveredCoordinate)).isFalse();
    }

    /**
     * When testing with coordinates, verify that they are less than or equal in meters to each other
     */
    private void verifyDistanceInMetersLessThanOrEqualTo(Coordinate coordinate1, Coordinate coordinate2, int meters) throws TransformException {
        double distanceInMeters = JTS.orthodromicDistance(
                coordinate1,
                coordinate2,
                DefaultGeographicCRS.WGS84);
        assertThat(distanceInMeters).isLessThanOrEqualTo(meters);
    }

}