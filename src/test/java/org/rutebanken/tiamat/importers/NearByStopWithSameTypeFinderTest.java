package org.rutebanken.tiamat.importers;

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
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class NearByStopWithSameTypeFinderTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    @Test
    public void testEnvelope() throws FactoryException, TransformException {
        Coordinate coordinate = new Coordinate(59.858690, 10.493860);
        Coordinate coveredCoordinate = new Coordinate(59.858616, 10.493858);
        Point point = geometryFactory.createPoint(coordinate);

        final int meters = 9;

        verifyDistanceInMetersLessThanOrEqualTo(coordinate, coveredCoordinate, meters);

        Envelope envelope = new NearByStopWithSameTypeFinder(mock(StopPlaceRepository.class))
                .createBoundingBox(point, meters);

        assertThat(envelope.intersects(coveredCoordinate)).isTrue();

    }

    private void verifyDistanceInMetersLessThanOrEqualTo(Coordinate coordinate1, Coordinate coordinate2, int meters) throws TransformException {
        double distanceInMeters = JTS.orthodromicDistance(
                coordinate1,
                coordinate2,
                DefaultGeographicCRS.WGS84);
        assertThat(distanceInMeters).isLessThanOrEqualTo(meters);
    }

    /**
     * Test that a coordinate does not intersect with the created envelope.
     */
    @Test
    public void testEnvelopeNoIntersects() throws FactoryException, TransformException {
        Coordinate coordinate = new Coordinate(59.858690, 10.493860);
        Coordinate notCoveredCoordinate = new Coordinate(59.858684, 10.493682);
        Point point = geometryFactory.createPoint(coordinate);

        final int meters = 9;

        Envelope envelope = new NearByStopWithSameTypeFinder(mock(StopPlaceRepository.class))
                .createBoundingBox(point, meters);

        assertThat(envelope.intersects(notCoveredCoordinate)).isFalse();
    }
}