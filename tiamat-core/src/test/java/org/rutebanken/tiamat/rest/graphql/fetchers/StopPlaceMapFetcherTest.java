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
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.rutebanken.tiamat.dtoassembling.dto.StopPlaceClusterDto;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.IGNORE_STOPPLACE_ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.INCLUDE_EXPIRED;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LATITUDE_MAX;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LATITUDE_MIN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LONGITUDE_MAX;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LONGITUDE_MIN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POINT_IN_TIME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ZOOM;

public class StopPlaceMapFetcherTest {

    private static final int INDIVIDUAL_FROM_ZOOM = 14;
    private static final int MAX_CLUSTERS = 5000;

    private final StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);
    private final StopPlaceFetcher stopPlaceFetcher = mock(StopPlaceFetcher.class);
    private StopPlaceMapFetcher fetcher;

    @Before
    public void setUp() {
        fetcher = new StopPlaceMapFetcher(stopPlaceRepository, stopPlaceFetcher, INDIVIDUAL_FROM_ZOOM, 60, MAX_CLUSTERS);
    }

    @Test
    public void cellSpansSixtyPixelsInWebMercatorMeters() {
        // 40075016.686 m / (256 px * 2^8) * 60 px
        assertThat(fetcher.cellSizeMeters(8)).isCloseTo(36_689.77, within(0.01));
    }

    @Test
    public void cellHalvesWithEachZoomLevel() {
        assertThat(fetcher.cellSizeMeters(9)).isCloseTo(fetcher.cellSizeMeters(8) / 2, within(1e-6));
    }

    @Test
    public void cellScalesWithConfiguredPixelSize() {
        StopPlaceMapFetcher wideCells = new StopPlaceMapFetcher(stopPlaceRepository, stopPlaceFetcher, INDIVIDUAL_FROM_ZOOM, 120, MAX_CLUSTERS);
        assertThat(wideCells.cellSizeMeters(8)).isCloseTo(fetcher.cellSizeMeters(8) * 2, within(1e-6));
    }

    @Test
    public void clustersJustBelowZoomThreshold() {
        when(stopPlaceRepository.findStopPlaceClustersWithin(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble(), any(), any(), anyInt()))
                .thenReturn(List.of(cluster(3, null)));

        StopPlaceMapFetcher.StopPlaceMap result = fetcher.get(environment(INDIVIDUAL_FROM_ZOOM - 1));

        assertThat(result.clustered()).isTrue();
        assertThat(result.clusters()).hasSize(1);
        assertThat(result.stopPlaces()).isEmpty();
        verify(stopPlaceFetcher, never()).get(any());
    }

    @Test
    public void returnsIndividualStopPlacesFromZoomThreshold() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Oslo S"));
        DataFetchingEnvironment environment = environment(INDIVIDUAL_FROM_ZOOM);
        when(stopPlaceFetcher.get(environment)).thenReturn(new PageImpl<>(List.of(stopPlace)));

        StopPlaceMapFetcher.StopPlaceMap result = fetcher.get(environment);

        assertThat(result.clustered()).isFalse();
        assertThat(result.clusters()).isEmpty();
        assertThat(result.stopPlaces()).containsExactly(stopPlace);
        verify(stopPlaceRepository, never()).findStopPlaceClustersWithin(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble(), any(), any(), anyInt());
    }

    @Test
    public void passesBoundsCellSizeIgnoredStopAndLimitToRepository() {
        DataFetchingEnvironment environment = environment(8);
        when(environment.getArgument(IGNORE_STOPPLACE_ID)).thenReturn("NSR:StopPlace:1");

        fetcher.get(environment);

        verify(stopPlaceRepository).findStopPlaceClustersWithin(eq(10.0), eq(59.5), eq(11.5), eq(61.0),
                eq(fetcher.cellSizeMeters(8)), any(), eq("NSR:StopPlace:1"), eq(MAX_CLUSTERS));
    }

    @Test
    public void defaultsPointInTimeToNow() {
        Instant before = Instant.now();

        fetcher.get(environment(8));

        Instant pointInTime = capturedPointInTime();
        assertThat(pointInTime).isBetween(before, Instant.now());
    }

    @Test
    public void usesGivenPointInTime() {
        Instant future = Instant.now().plus(Duration.ofDays(30));
        DataFetchingEnvironment environment = environment(8);
        when(environment.getArgument(POINT_IN_TIME)).thenReturn(future);

        fetcher.get(environment);

        assertThat(capturedPointInTime()).isEqualTo(future);
    }

    @Test
    public void includeExpiredDropsPointInTime() {
        DataFetchingEnvironment environment = environment(8);
        when(environment.getArgument(INCLUDE_EXPIRED)).thenReturn(true);
        when(environment.getArgument(POINT_IN_TIME)).thenReturn(Instant.now());

        fetcher.get(environment);

        verify(stopPlaceRepository).findStopPlaceClustersWithin(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble(), isNull(), any(), anyInt());
    }

    @Test
    public void loadsStopPlacesOfSingleStopClustersInOneQuery() {
        StopPlace hamar = new StopPlace(new EmbeddableMultilingualString("Hamar"));
        ReflectionTestUtils.setField(hamar, "id", 42L);
        when(stopPlaceRepository.findStopPlaceClustersWithin(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble(), any(), any(), anyInt()))
                .thenReturn(List.of(cluster(4, null), cluster(1, 42L)));
        when(stopPlaceRepository.findAllById(List.of(42L))).thenReturn(List.of(hamar));

        StopPlaceMapFetcher.StopPlaceMap result = fetcher.get(environment(8));

        assertThat(result.clusters()).extracting(StopPlaceMapFetcher.Cluster::stopPlace).containsExactly(null, hamar);
        verify(stopPlaceRepository).findAllById(List.of(42L));
    }

    private Instant capturedPointInTime() {
        ArgumentCaptor<Instant> captor = ArgumentCaptor.forClass(Instant.class);
        verify(stopPlaceRepository).findStopPlaceClustersWithin(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble(), captor.capture(), any(), anyInt());
        return captor.getValue();
    }

    private static StopPlaceClusterDto cluster(long count, Long singleStopPlaceId) {
        return new StopPlaceClusterDto(count, 10.75, 59.91, 10.7, 59.9, 10.8, 59.92, singleStopPlaceId);
    }

    private static DataFetchingEnvironment environment(int zoom) {
        DataFetchingEnvironment environment = mock(DataFetchingEnvironment.class);
        when(environment.getArgument(ZOOM)).thenReturn(zoom);
        when(environment.getArgument(LONGITUDE_MIN)).thenReturn(new BigDecimal("10.0"));
        when(environment.getArgument(LATITUDE_MIN)).thenReturn(new BigDecimal("59.5"));
        when(environment.getArgument(LONGITUDE_MAX)).thenReturn(new BigDecimal("11.5"));
        when(environment.getArgument(LATITUDE_MAX)).thenReturn(new BigDecimal("61.0"));
        return environment;
    }
}
