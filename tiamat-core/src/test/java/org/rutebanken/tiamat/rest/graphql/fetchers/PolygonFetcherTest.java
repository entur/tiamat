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

package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetchingEnvironment;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.rutebanken.tiamat.model.FareZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PolygonFetcherTest {

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final PolygonFetcher fetcher = new PolygonFetcher();

    @Test
    public void returnsNullWhenSourceIsNotAZone() {
        DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);
        when(env.getSource()).thenReturn("not a zone");

        assertThat(fetcher.get(env)).isNull();
    }

    @Test
    public void returnsPolygonWhenOnlyPolygonIsSet() {
        Polygon polygon = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(0, 0), new Coordinate(1, 0),
                new Coordinate(1, 1), new Coordinate(0, 0)
        });

        FareZone fareZone = new FareZone();
        fareZone.setPolygon(polygon);

        DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);
        when(env.getSource()).thenReturn(fareZone);

        assertThat(fetcher.get(env)).isEqualTo(polygon);
    }

    @Test
    public void returnsMultiPolygonWhenOnlyMultiSurfaceIsSet() {
        Polygon polygon = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(0, 0), new Coordinate(1, 0),
                new Coordinate(1, 1), new Coordinate(0, 0)
        });
        MultiPolygon multiPolygon = geometryFactory.createMultiPolygon(new Polygon[]{polygon});

        FareZone fareZone = new FareZone();
        fareZone.setMultiSurface(multiPolygon);

        DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);
        when(env.getSource()).thenReturn(fareZone);

        assertThat(fetcher.get(env)).isEqualTo(multiPolygon);
    }

    @Test
    public void prefersMultiSurfaceOverPolygonWhenBothAreSet() {
        Polygon polygon = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(0, 0), new Coordinate(1, 0),
                new Coordinate(1, 1), new Coordinate(0, 0)
        });
        MultiPolygon multiPolygon = geometryFactory.createMultiPolygon(new Polygon[]{polygon});

        FareZone fareZone = new FareZone();
        fareZone.setPolygon(polygon);
        fareZone.setMultiSurface(multiPolygon);

        DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);
        when(env.getSource()).thenReturn(fareZone);

        assertThat(fetcher.get(env)).isEqualTo(multiPolygon);
    }

    @Test
    public void returnsNullWhenNeitherPolygonNorMultiSurfaceIsSet() {
        FareZone fareZone = new FareZone();

        DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);
        when(env.getSource()).thenReturn(fareZone);

        assertThat(fetcher.get(env)).isNull();
    }
}
