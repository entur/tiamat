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

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.general.PeriodicCacheLogger;
import org.rutebanken.tiamat.importer.AlternativeStopTypes;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class NearbyStopPlaceFinderTest {
    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    private AlternativeStopTypes alternativeTypes = new AlternativeStopTypes();
    private PeriodicCacheLogger periodicCacheLogger = new PeriodicCacheLogger();

    @Test
    public void nullCentroid() throws Exception {
        NearbyStopPlaceFinder nearbyStopPlaceFinder = new NearbyStopPlaceFinder(mock(StopPlaceRepository.class), 0, 0, TimeUnit.DAYS, periodicCacheLogger, alternativeTypes);
        StopPlace stopPlace = new StopPlace();
        StopPlace actual = nearbyStopPlaceFinder.find(stopPlace);
        assertThat(actual).isNull();
    }

    @Test
    public void nullPoint() throws Exception {
        NearbyStopPlaceFinder nearbyStopPlaceFinder = new NearbyStopPlaceFinder(mock(StopPlaceRepository.class), 0, 0, TimeUnit.DAYS, periodicCacheLogger, alternativeTypes);
        StopPlace stopPlace = new StopPlace();
        StopPlace actual = nearbyStopPlaceFinder.find(stopPlace);
        assertThat(actual).isNull();
    }

    @Test
    public void nullType() throws Exception {
        NearbyStopPlaceFinder nearbyStopPlaceFinder = new NearbyStopPlaceFinder(mock(StopPlaceRepository.class), 0, 0, TimeUnit.DAYS, periodicCacheLogger, alternativeTypes);
        StopPlace stopPlace = new StopPlace();
        nearbyStopPlaceFinder.update(stopPlace);
    }

    @Test
    public void leakingEnvelope() throws Exception {

        StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);
        NearbyStopPlaceFinder nearbyStopPlaceFinder = new NearbyStopPlaceFinder(stopPlaceRepository, 0, 0, TimeUnit.DAYS, periodicCacheLogger, alternativeTypes);

        String stopPlaceId = "NSR:StopPlace:1";

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("name"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(9, 40)));

        org.locationtech.jts.geom.Geometry envelope = stopPlace.getCentroid().getEnvelope().copy();


        when(stopPlaceRepository.findNearbyStopPlace(isA(Envelope.class),isA(String.class),isA(StopTypeEnumeration.class))).thenReturn(stopPlaceId);
        when(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId)).thenReturn(stopPlace);

        StopPlace actual = nearbyStopPlaceFinder.find(stopPlace);
        org.locationtech.jts.geom.Geometry actualEnvelope = actual.getCentroid().getEnvelope().copy();

        assertThat(actualEnvelope).isEqualTo(envelope);
    }
}
