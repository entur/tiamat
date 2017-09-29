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

package org.rutebanken.tiamat.importer.finder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.geotools.referencing.GeodeticCalculator;
import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.ValidBetween;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.awt.geom.Point2D;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class NearbyStopsWithSameTypeFinderTest extends TiamatIntegrationTest {

    private static final Coordinate OSL_GARDERMOEN = new Coordinate(60.190448, 11.106292);

    @Autowired
    private NearbyStopsWithSameTypeFinder nearbyStopsWithSameTypeFinder;

    @Before
    public void cleanRepositories() {
        stopPlaceRepository.deleteAll();
    }

    @Test
    public void findNearbyAirport() throws FactoryException, TransformException {

        Point point = geometryFactory.createPoint(OSL_GARDERMOEN);
        createSavedStopPlace("OSL", StopTypeEnumeration.AIRPORT, point);

        StopPlace incomingStopPlace = new StopPlace(new EmbeddableMultilingualString("Gardermoen"));
        incomingStopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);

        int azimuth = 90;
        int offsetMeters = 300;
        incomingStopPlace.setCentroid(getOffsetPoint(point, offsetMeters, azimuth));

        List<StopPlace> foundStopPlaces = nearbyStopsWithSameTypeFinder.find(incomingStopPlace);
        assertThat(foundStopPlaces).isNotNull();
        assertThat(foundStopPlaces).hasSize(1);
    }

    @Test
    public void findNoNearbyRailStation() throws FactoryException, TransformException {

        Point point = geometryFactory.createPoint(OSL_GARDERMOEN);
        createSavedStopPlace("OSL", StopTypeEnumeration.AIRPORT, point);

        StopPlace incomingStopPlace = new StopPlace(new EmbeddableMultilingualString("Gardermoen"));
        incomingStopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);

        incomingStopPlace.setCentroid(point);

        List<StopPlace> foundStopPlaces = nearbyStopsWithSameTypeFinder.find(incomingStopPlace);
        assertThat(foundStopPlaces).isNotNull();
        assertThat(foundStopPlaces).isEmpty();
    }

    @Test
    public void noNearByAirport() throws FactoryException, TransformException {
        Point point = geometryFactory.createPoint(OSL_GARDERMOEN);
        createSavedStopPlace("OSL Gardermoen", StopTypeEnumeration.AIRPORT, point);

        StopPlace incomingStopPlace = new StopPlace(new EmbeddableMultilingualString("Gardermoen"));
        incomingStopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);

        int offsetMeters = 9000; int azimuth = 90;
        incomingStopPlace.setCentroid(getOffsetPoint(point, offsetMeters, azimuth));

        System.out.println("Searching with stop place " + incomingStopPlace);

        List<StopPlace> foundStopPlaces = nearbyStopsWithSameTypeFinder.find(incomingStopPlace);
        assertThat(foundStopPlaces).isNotNull();
        assertThat(foundStopPlaces).isEmpty();
    }


    private StopPlace createSavedStopPlace(String name, StopTypeEnumeration stopType, Point point) {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(name));
        stopPlace.setStopPlaceType(stopType);
        stopPlace.setCentroid(point);
        stopPlace.setValidBetween(new ValidBetween(Instant.EPOCH));

        stopPlaceRepository.save(stopPlace);
        return stopPlace;
    }

    private Point getOffsetPoint(Point point, int offsetMeters, int azimuth) {
        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingGeographicPoint(point.getX(), point.getY());
        calc.setDirection(azimuth, offsetMeters);
        Point2D dest = calc.getDestinationGeographicPoint();
        return geometryFactory.createPoint(new Coordinate(dest.getX(), dest.getY()));
    }

}