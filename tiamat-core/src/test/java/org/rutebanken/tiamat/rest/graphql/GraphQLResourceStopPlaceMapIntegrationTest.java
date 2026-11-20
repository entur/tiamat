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

package org.rutebanken.tiamat.rest.graphql;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.tiamat.dtoassembling.dto.StopPlaceClusterDto;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.ValidBetween;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class GraphQLResourceStopPlaceMapIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    private static final String MAP_QUERY = """
            {
              stopPlaceMapBBox(lonMin: %s, latMin: %s, lonMax: %s, latMax: %s, zoom: %d) {
                clustered
                clusters { count longitude latitude lonMin latMin lonMax latMax stopPlace { id name { value } } }
                stopPlaces { id }
              }
            }
            """;

    /**
     * At zoom 8 a cluster cell is about 37 km wide: the stops in central Oslo share a cell, Hamar gets its own.
     * Children count through their parent, and expired stop places are left out.
     * Only the single-stop cluster carries its stop place.
     */
    @Test
    public void clustersStopPlacesBelowZoomThreshold() {
        TestStopPlaces stops = createTestStopPlaces();

        executeGraphqQLQueryOnly(MAP_QUERY.formatted(10.0, 59.5, 11.5, 61.0, 8))
                .body("errors", anyOf(nullValue(), empty()))
                .body("data.stopPlaceMapBBox.clustered", equalTo(true))
                .body("data.stopPlaceMapBBox.stopPlaces", empty())
                .body("data.stopPlaceMapBBox.clusters", hasSize(2))
                .body("data.stopPlaceMapBBox.clusters[0].count", equalTo(4))
                .body("data.stopPlaceMapBBox.clusters[0].lonMin", near(10.75))
                .body("data.stopPlaceMapBBox.clusters[0].lonMax", near(10.752))
                .body("data.stopPlaceMapBBox.clusters[0].latitude", near(59.910875))
                .body("data.stopPlaceMapBBox.clusters[0].stopPlace", nullValue())
                .body("data.stopPlaceMapBBox.clusters[1].count", equalTo(1))
                .body("data.stopPlaceMapBBox.clusters[1].longitude", near(11.07))
                .body("data.stopPlaceMapBBox.clusters[1].stopPlace.id", equalTo(stops.hamar.getNetexId()))
                .body("data.stopPlaceMapBBox.clusters[1].stopPlace.name.value", equalTo("Hamar"));
    }

    @Test
    public void clustersLeaveOutIgnoredStopPlace() {
        TestStopPlaces stops = createTestStopPlaces();

        String query = """
                {
                  stopPlaceMapBBox(lonMin: 10.0, latMin: 59.5, lonMax: 11.5, latMax: 61.0, zoom: 8, ignoreStopPlaceId: "%s") {
                    clusters { count longitude }
                  }
                }
                """.formatted(stops.hamar.getNetexId());

        executeGraphqQLQueryOnly(query)
                .body("errors", anyOf(nullValue(), empty()))
                .body("data.stopPlaceMapBBox.clusters", hasSize(1))
                .body("data.stopPlaceMapBBox.clusters[0].count", equalTo(4));
    }

    /**
     * With includeExpired, the expired stop place is counted once, although it has two versions.
     */
    @Test
    public void clustersIncludeExpiredStopPlacesOncePerStopPlace() {
        createTestStopPlaces();

        String query = """
                {
                  stopPlaceMapBBox(lonMin: 10.0, latMin: 59.5, lonMax: 11.5, latMax: 61.0, zoom: 8, includeExpired: true) {
                    clustered
                    clusters { count longitude }
                  }
                }
                """;

        executeGraphqQLQueryOnly(query)
                .body("errors", anyOf(nullValue(), empty()))
                .body("data.stopPlaceMapBBox.clustered", equalTo(true))
                .body("data.stopPlaceMapBBox.clusters", hasSize(2))
                .body("data.stopPlaceMapBBox.clusters[0].count", equalTo(5))
                .body("data.stopPlaceMapBBox.clusters[1].count", equalTo(1));
    }

    /**
     * A stop place that opens in ten days is not on today's map, but is on the map for a later point in time.
     */
    @Test
    public void clustersStopPlacesValidAtGivenPointInTime() {
        StopPlace tromso = stopPlace("Tromsø", 18.95, 69.65);
        tromso.setVersion(1L);
        tromso.setValidBetween(new ValidBetween(Instant.now().plus(Duration.ofDays(10))));
        stopPlaceRepository.save(tromso);

        String query = """
                {
                  stopPlaceMapBBox(lonMin: 18.0, latMin: 69.0, lonMax: 20.0, latMax: 70.5, zoom: 8%s) {
                    clusters { count stopPlace { id } }
                  }
                }
                """;

        executeGraphqQLQueryOnly(query.formatted(""))
                .body("errors", anyOf(nullValue(), empty()))
                .body("data.stopPlaceMapBBox.clusters", empty());

        executeGraphqQLQueryOnly(query.formatted(", pointInTime: \"%s\"".formatted(Instant.now().plus(Duration.ofDays(20)))))
                .body("errors", anyOf(nullValue(), empty()))
                .body("data.stopPlaceMapBBox.clusters", hasSize(1))
                .body("data.stopPlaceMapBBox.clusters[0].stopPlace.id", equalTo(tromso.getNetexId()));
    }

    /**
     * Web Mercator stops at about ±85.05° latitude, so the bounding box is clamped there: a world-wide box works,
     * and a stop place nearer the pole than that is left out.
     */
    @Test
    public void clampsBoundingBoxToWebMercatorLatitudes() {
        createTestStopPlaces();
        StopPlace nearPole = saveStopPlaceTransactional(stopPlace("Nær polen", 15.0, 88.0));

        executeGraphqQLQueryOnly(MAP_QUERY.formatted(-180.0, -90.0, 180.0, 90.0, 3))
                .body("errors", anyOf(nullValue(), empty()))
                .body("data.stopPlaceMapBBox.clustered", equalTo(true))
                .body("data.stopPlaceMapBBox.clusters.count.sum()", equalTo(5))
                .body("data.stopPlaceMapBBox.clusters.stopPlace.id", not(hasItem(nearPole.getNetexId())));
    }

    /**
     * The limit keeps the clusters with the most stop places.
     */
    @Test
    public void limitsNumberOfClustersKeepingTheLargest() {
        createTestStopPlaces();
        double cellSizeAtZoom8 = 40_075_016.686 / (256 * Math.pow(2, 8)) * 60;

        List<StopPlaceClusterDto> clusters = stopPlaceRepository.findStopPlaceClustersWithin(
                10.0, 59.5, 11.5, 61.0, cellSizeAtZoom8, Instant.now(), null, 1);

        assertThat(clusters).hasSize(1);
        assertThat(clusters.getFirst().count()).isEqualTo(4);
        assertThat(clusters.getFirst().singleStopPlaceId()).isNull();
    }

    @Test
    public void returnsIndividualStopPlacesFromZoomThreshold() {
        TestStopPlaces stops = createTestStopPlaces();

        executeGraphqQLQueryOnly(MAP_QUERY.formatted(10.7, 59.85, 10.8, 59.95, 15))
                .body("errors", anyOf(nullValue(), empty()))
                .body("data.stopPlaceMapBBox.clustered", equalTo(false))
                .body("data.stopPlaceMapBBox.clusters", empty())
                .body("data.stopPlaceMapBBox.stopPlaces.id", hasItems(stops.standalone.getNetexId(), stops.parent.getNetexId()));
    }

    /**
     * RestAssured parses JSON numbers as Float.
     */
    private static Matcher<Number> near(double expected) {
        return new FeatureMatcher<>(closeTo(expected, 1e-5), "number", "value") {
            @Override
            protected Double featureValueOf(Number actual) {
                return actual.doubleValue();
            }
        };
    }

    private record TestStopPlaces(StopPlace standalone, StopPlace parent, StopPlace hamar) {
    }

    private TestStopPlaces createTestStopPlaces() {
        StopPlace standalone = saveStopPlaceTransactional(stopPlace("Jernbanetorget", 10.75, 59.91));
        saveStopPlaceTransactional(stopPlace("Oslo bussterminal", 10.751, 59.911));
        saveStopPlaceTransactional(stopPlace("Grønland", 10.752, 59.912));
        StopPlace hamar = saveStopPlaceTransactional(stopPlace("Hamar", 11.07, 60.79));

        StopPlace expired = stopPlace("Nedlagt", 10.7502, 59.9102);
        expired.setVersion(1L);
        expired.setValidBetween(new ValidBetween(Instant.now().minus(Duration.ofDays(20)), Instant.now().minus(Duration.ofDays(10))));
        stopPlaceRepository.save(expired);

        StopPlace expiredNextVersion = stopPlace("Nedlagt", 10.7502, 59.9102);
        expiredNextVersion.setNetexId(expired.getNetexId());
        expiredNextVersion.setVersion(2L);
        expiredNextVersion.setValidBetween(new ValidBetween(Instant.now().minus(Duration.ofDays(10)), Instant.now().minus(Duration.ofDays(1))));
        stopPlaceRepository.save(expiredNextVersion);

        StopPlace parent = new StopPlace(new EmbeddableMultilingualString("Oslo S"));
        parent.setParentStopPlace(true);
        parent.setVersion(1L);
        parent.setCentroid(geometryFactory.createPoint(new Coordinate(10.7505, 59.9105)));
        stopPlaceRepository.save(parent);

        StopPlace child = stopPlace("Oslo S tog", 10.7506, 59.9106);
        child.setVersion(1L);
        child.setParentSiteRef(new SiteRefStructure(parent.getNetexId(), String.valueOf(parent.getVersion())));
        stopPlaceRepository.save(child);
        parent.getChildren().add(child);
        stopPlaceRepository.save(parent);

        return new TestStopPlaces(standalone, parent, hamar);
    }

    private StopPlace stopPlace(String name, double longitude, double latitude) {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(name));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
        return stopPlace;
    }
}
