package org.rutebanken.tiamat.rest.graphql;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

public class GraphQLResourceQuayMergerTest extends AbstractGraphQLResourceIntegrationTest {

    @Test
    public void mergeQuays() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));
        stopPlace.getOriginalIds().add("TEST:StopPlace:1234");
        stopPlace.getOriginalIds().add("TEST:StopPlace:5678");

        Quay fromQuay = new Quay();
        fromQuay.setCompassBearing(90F);
        fromQuay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        fromQuay.getOriginalIds().add("TEST:Quay:123401");
        fromQuay.getOriginalIds().add("TEST:Quay:567801");


        Quay toQuay = new Quay();
        toQuay.setCompassBearing(90F);
        toQuay.setCentroid(geometryFactory.createPoint(new Coordinate(11.21, 60.21)));
        toQuay.getOriginalIds().add("TEST:Quay:432101");
        toQuay.getOriginalIds().add("TEST:Quay:876501");

        Quay quayToKeepUnaltered = new Quay();
        quayToKeepUnaltered.setCompassBearing(180F);
        quayToKeepUnaltered.setCentroid(geometryFactory.createPoint(new Coordinate(11.211, 60.211)));
        quayToKeepUnaltered.getOriginalIds().add("TEST:Quay:432102");

        stopPlace.getQuays().add(fromQuay);
        stopPlace.getQuays().add(toQuay);
        stopPlace.getQuays().add(quayToKeepUnaltered);

        stopPlace = saveStopPlaceTransactional(stopPlace);

        assertThat(stopPlace.getQuays()).hasSize(3);

        //Calling GraphQL-api to merge Quays
        String graphQlJsonQuery = """
                mutation {
                  stopPlace: mergeQuays (
                          stopPlaceId: "%s",
                          fromQuayId: "%s",
                          toQuayId: "%s",
                       ) {
                            id
                            importedId
                            name { value }
                            ...on StopPlace {
                                quays {
                                    id
                                    geometry { type coordinates }
                                    compassBearing
                                    importedId
                                }
                            }
                        }
                 }"""
                .formatted(stopPlace.getNetexId(), fromQuay.getNetexId(), toQuay.getNetexId());

        Set<String> originalIds = new HashSet<>();
        originalIds.addAll(toQuay.getOriginalIds());
        originalIds.addAll(fromQuay.getOriginalIds());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.id", comparesEqualTo(stopPlace.getNetexId()))
                .body("data.stopPlace.quays", hasSize(2))
                .root("data.stopPlace.quays.find { it.id == '" +toQuay.getNetexId() + "'}")
                .body("importedId", containsInAnyOrder(originalIds.toArray()))
                .root("data.stopPlace.quays.find { it.id == '" + quayToKeepUnaltered.getNetexId() + "'}")
                .body("importedId", containsInAnyOrder(quayToKeepUnaltered.getOriginalIds().toArray()));
    }
}
