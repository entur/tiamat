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

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.dtoassembling.dto.StopPlaceClusterDto;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.IGNORE_STOPPLACE_ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.INCLUDE_EXPIRED;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LATITUDE_MAX;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LATITUDE_MIN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LONGITUDE_MAX;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LONGITUDE_MIN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POINT_IN_TIME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ZOOM;

/**
 * Serves stop places for a map view. Below the configured zoom level, stop places are aggregated into clusters
 * in the database; from that zoom level and in, individual stop places are returned as by stopPlaceBBox.
 */
@Service("stopPlaceMapFetcher")
@Transactional(readOnly = true)
public class StopPlaceMapFetcher implements DataFetcher<StopPlaceMapFetcher.StopPlaceMap> {

    public record StopPlaceMap(boolean clustered, List<Cluster> clusters, List<StopPlace> stopPlaces) {
    }

    /**
     * A cluster as served by GraphQL. stopPlace is set only when the cluster holds a single stop place.
     */
    public record Cluster(long count, double longitude, double latitude,
                          double lonMin, double latMin, double lonMax, double latMax,
                          StopPlace stopPlace) {

        static Cluster of(StopPlaceClusterDto dto, StopPlace stopPlace) {
            return new Cluster(dto.count(), dto.longitude(), dto.latitude(),
                    dto.lonMin(), dto.latMin(), dto.lonMax(), dto.latMax(), stopPlace);
        }
    }

    private static final double EARTH_CIRCUMFERENCE_METERS = 40_075_016.686;
    private static final int TILE_SIZE_PIXELS = 256;

    private final StopPlaceRepository stopPlaceRepository;
    private final StopPlaceFetcher stopPlaceFetcher;
    private final int individualStopPlacesFromZoom;
    private final int clusterCellSizePixels;
    private final int maxClusters;

    StopPlaceMapFetcher(StopPlaceRepository stopPlaceRepository,
                        StopPlaceFetcher stopPlaceFetcher,
                        @Value("${tiamat.graphql.map.individual-stop-places-from-zoom:14}") int individualStopPlacesFromZoom,
                        @Value("${tiamat.graphql.map.cluster-cell-size-pixels:60}") int clusterCellSizePixels,
                        @Value("${tiamat.graphql.map.max-clusters:5000}") int maxClusters) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.stopPlaceFetcher = stopPlaceFetcher;
        this.individualStopPlacesFromZoom = individualStopPlacesFromZoom;
        this.clusterCellSizePixels = clusterCellSizePixels;
        this.maxClusters = maxClusters;
    }

    @Override
    @SuppressWarnings("unchecked")
    public StopPlaceMap get(DataFetchingEnvironment environment) {
        int zoom = environment.getArgument(ZOOM);

        if (zoom >= individualStopPlacesFromZoom) {
            Page<StopPlace> stopPlaces = (Page<StopPlace>) stopPlaceFetcher.get(environment);
            return new StopPlaceMap(false, List.of(), stopPlaces.getContent());
        }

        List<StopPlaceClusterDto> clusters = stopPlaceRepository.findStopPlaceClustersWithin(
                doubleArgument(environment, LONGITUDE_MIN),
                doubleArgument(environment, LATITUDE_MIN),
                doubleArgument(environment, LONGITUDE_MAX),
                doubleArgument(environment, LATITUDE_MAX),
                cellSizeMeters(zoom),
                pointInTime(environment),
                environment.getArgument(IGNORE_STOPPLACE_ID),
                maxClusters);
        return new StopPlaceMap(true, withSingleStopPlaces(clusters), List.of());
    }

    /**
     * Loads the stop places of all single-stop clusters in one query, so the client can show them as stop places.
     */
    private List<Cluster> withSingleStopPlaces(List<StopPlaceClusterDto> clusters) {
        List<Long> singleStopPlaceIds = clusters.stream()
                .map(StopPlaceClusterDto::singleStopPlaceId)
                .filter(Objects::nonNull)
                .toList();
        Map<Long, StopPlace> stopPlacesById = stopPlaceRepository.findAllById(singleStopPlaceIds).stream()
                .collect(Collectors.toMap(StopPlace::getId, Function.identity()));
        return clusters.stream()
                .map(cluster -> Cluster.of(cluster, stopPlacesById.get(cluster.singleStopPlaceId())))
                .toList();
    }

    /**
     * Width in Web Mercator meters of a square spanning clusterCellSizePixels on screen at the given zoom level.
     */
    double cellSizeMeters(int zoom) {
        return EARTH_CIRCUMFERENCE_METERS / (TILE_SIZE_PIXELS * Math.pow(2, zoom)) * clusterCellSizePixels;
    }

    private static Instant pointInTime(DataFetchingEnvironment environment) {
        if (Boolean.TRUE.equals(environment.getArgument(INCLUDE_EXPIRED))) {
            return null;
        }
        Instant pointInTime = environment.getArgument(POINT_IN_TIME);
        return pointInTime != null ? pointInTime : Instant.now();
    }

    private static double doubleArgument(DataFetchingEnvironment environment, String name) {
        return ((BigDecimal) environment.getArgument(name)).doubleValue();
    }
}
