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

import org.junit.Test;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.ParkingArea;
import org.rutebanken.tiamat.model.ParkingProperties;
import org.rutebanken.tiamat.model.StopPlace;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;

/**
 * A GraphQL save keeps the netexId and the edits of parking properties, spaces, parking areas
 * and the accessibility assessment that the client resends with their id.
 */
public class ParkingStructureIdentityGraphQLTest extends AbstractGraphQLResourceIntegrationTest {

    private static final String CREATE_INPUT = """
            parkingProperties: [ { spaces: [ { parkingVehicleType: car numberOfSpaces: 10 } { parkingVehicleType: pedalCycle numberOfSpaces: 4 } ] } ]
            parkingAreas: [ { label: { value: "Area A" lang: "fi" } parkingProperties: { spaces: [ { parkingVehicleType: car numberOfSpaces: 3 } ] } } ]
            accessibilityAssessment: { limitations: { wheelchairAccess: TRUE stepFreeAccess: TRUE audibleSignalsAvailable: UNKNOWN visualSignsAvailable: UNKNOWN liftFreeAccess: UNKNOWN escalatorFreeAccess: UNKNOWN } }""";

    private record State(
            String propertiesId, long propertiesVersion, List<String> spaceIds, List<Integer> spaceCounts,
            String areaId, long areaVersion, String areaPropertiesId, List<String> areaSpaceIds,
            String assessmentId, String limitationId, String wheelchair) {
    }

    @Test
    public void resentStructureKeepsNetexIdsAndAppliesEdits() throws Exception {
        String parkingId = createParking();
        State before = state(parkingId);
        assertThat(before.spaceIds()).hasSize(2);

        String update = update(parkingId, """
                parkingProperties: [ { id: "%s" spaces: [ { id: "%s" parkingVehicleType: car numberOfSpaces: 12 } { id: "%s" parkingVehicleType: pedalCycle numberOfSpaces: 4 } ] } ]
                parkingAreas: [ { id: "%s" label: { value: "Area A2" lang: "fi" } parkingProperties: { id: "%s" spaces: [ { id: "%s" parkingVehicleType: car numberOfSpaces: 5 } ] } } ]
                accessibilityAssessment: { limitations: { wheelchairAccess: FALSE stepFreeAccess: TRUE audibleSignalsAvailable: UNKNOWN visualSignsAvailable: UNKNOWN liftFreeAccess: UNKNOWN escalatorFreeAccess: UNKNOWN } }"""
                .formatted(before.propertiesId(), before.spaceIds().get(0), before.spaceIds().get(1),
                        before.areaId(), before.areaPropertiesId(), before.areaSpaceIds().getFirst()));
        executeGraphqQLQueryOnly(update).body("errors", nullValue());

        State after = state(parkingId);
        assertThat(after.propertiesId()).isEqualTo(before.propertiesId());
        assertThat(after.spaceIds()).isEqualTo(before.spaceIds());
        assertThat(after.spaceCounts()).containsExactly(12, 4);
        assertThat(after.areaId()).isEqualTo(before.areaId());
        assertThat(after.areaPropertiesId()).isEqualTo(before.areaPropertiesId());
        assertThat(after.areaSpaceIds()).isEqualTo(before.areaSpaceIds());
        assertThat(after.assessmentId()).isEqualTo(before.assessmentId());
        assertThat(after.limitationId()).isEqualTo(before.limitationId());
        assertThat(after.wheelchair()).isEqualTo("FALSE");
        assertThat(after.propertiesVersion()).isGreaterThan(before.propertiesVersion());
        assertThat(after.areaVersion()).isGreaterThan(before.areaVersion());
    }

    @Test
    public void resentStructureLeavesNoOrphanRows() throws Exception {
        String parkingId = createParking();
        State before = state(parkingId);
        long properties = countRows("ParkingProperties");
        long capacities = countRows("ParkingCapacity");
        long areas = countRows("ParkingArea");

        // One space is dropped, so one row is deleted. Nothing else is added.
        executeGraphqQLQueryOnly(update(parkingId, """
                parkingProperties: [ { id: "%s" spaces: [ { id: "%s" parkingVehicleType: car numberOfSpaces: 10 } ] } ]
                parkingAreas: [ { id: "%s" parkingProperties: { id: "%s" spaces: [ { id: "%s" parkingVehicleType: car numberOfSpaces: 3 } ] } } ]"""
                .formatted(before.propertiesId(), before.spaceIds().getFirst(),
                        before.areaId(), before.areaPropertiesId(), before.areaSpaceIds().getFirst())))
                .body("errors", nullValue());

        assertThat(countRows("ParkingProperties")).isEqualTo(properties);
        assertThat(countRows("ParkingArea")).isEqualTo(areas);
        assertThat(countRows("ParkingCapacity")).isEqualTo(capacities - 1);
    }

    @Test
    public void unknownIdsAreRejectedWithChildAndParentId() throws Exception {
        String parkingId = createParking();
        State before = state(parkingId);

        executeGraphqQLQueryOnly(update(parkingId, "parkingProperties: [ { id: \"PRK:ParkingProperties:999999\" } ]"), 400)
                .body("errors[0].message", allOf(containsString("PRK:ParkingProperties:999999"), containsString(parkingId)));
        executeGraphqQLQueryOnly(update(parkingId, "parkingAreas: [ { id: \"PRK:ParkingArea:999999\" } ]"), 400)
                .body("errors[0].message", allOf(containsString("PRK:ParkingArea:999999"), containsString(parkingId)));
        executeGraphqQLQueryOnly(update(parkingId, "parkingProperties: [ { id: \"" + before.propertiesId()
                + "\" spaces: [ { id: \"PRK:ParkingCapacity:999999\" } ] } ]"), 400)
                .body("errors[0].message", allOf(containsString("PRK:ParkingCapacity:999999"), containsString(before.propertiesId())));

        assertThat(state(parkingId)).isEqualTo(before);
    }

    @Test
    public void sameIdTwiceInOneSaveIsRejected() throws Exception {
        String parkingId = createParking();
        State before = state(parkingId);
        String space = "{ id: \"" + before.spaceIds().getFirst() + "\" numberOfSpaces: 1 }";

        executeGraphqQLQueryOnly(update(parkingId, "parkingProperties: [ { id: \"" + before.propertiesId()
                + "\" spaces: [ " + space + " " + space + " ] } ]"), 400)
                .body("errors[0].message", allOf(containsString(before.spaceIds().getFirst()), containsString("more than once")));
    }

    @Test
    public void entriesWithoutIdAreCreatedAsNew() throws Exception {
        String parkingId = createParking();
        State before = state(parkingId);

        executeGraphqQLQueryOnly(update(parkingId, """
                parkingProperties: [ { spaces: [ { parkingVehicleType: car numberOfSpaces: 1 } ] } ]
                parkingAreas: [ { label: { value: "New" lang: "fi" } parkingProperties: { spaces: [ { numberOfSpaces: 2 } ] } } ]"""))
                .body("errors", nullValue());

        State after = state(parkingId);
        assertThat(after.propertiesId()).isNotEqualTo(before.propertiesId());
        assertThat(after.areaId()).isNotEqualTo(before.areaId());
    }

    private String createParking() throws Exception {
        StopPlace stopPlace = stopPlaceRepository.save(new StopPlace());
        String query = """
                {"query": "mutation { parking: %s (Parking: { geometry: { type:Point coordinates:[10.5, 59.0] } parentSiteRef: \\"%s\\" %s }) { id } }", "variables": ""}"""
                .formatted(GraphQLNames.MUTATE_PARKING, stopPlace.getNetexId(), CREATE_INPUT.replace("\n", " ").replace("\"", "\\\""));
        return executeGraphQL(query).extract().path("data.parking[0].id");
    }

    private String update(String parkingId, String input) {
        return "mutation { parking: " + GraphQLNames.MUTATE_PARKING + " (Parking: { id: \"" + parkingId + "\" " + input + " }) { id } }";
    }

    private State state(String parkingNetexId) {
        return inLatestParking(parkingNetexId, parking -> {
            ParkingProperties properties = parking.getParkingProperties().getFirst();
            ParkingArea area = parking.getParkingAreas().getFirst();
            var limitation = parking.getAccessibilityAssessment().getLimitations().getFirst();
            return new State(
                    properties.getNetexId(), properties.getVersion(),
                    properties.getSpaces().stream().map(s -> s.getNetexId()).toList(),
                    properties.getSpaces().stream().map(s -> s.getNumberOfSpaces().intValue()).toList(),
                    area.getNetexId(), area.getVersion(), area.getParkingProperties().getNetexId(),
                    area.getParkingProperties().getSpaces().stream().map(s -> s.getNetexId()).toList(),
                    parking.getAccessibilityAssessment().getNetexId(), limitation.getNetexId(),
                    String.valueOf(limitation.getWheelchairAccess()));
        });
    }

    private <R> R inLatestParking(String parkingNetexId, Function<Parking, R> work) {
        var em = entityManagerFactory.createEntityManager();
        var tx = em.getTransaction();
        tx.begin();
        try {
            Parking parking = em.createQuery(
                            "select p from Parking p where p.netexId = :id order by p.version desc", Parking.class)
                    .setParameter("id", parkingNetexId)
                    .setMaxResults(1)
                    .getSingleResult();
            R result = work.apply(parking);
            tx.commit();
            return result;
        } finally {
            em.close();
        }
    }

    private long countRows(String entityName) {
        var em = entityManagerFactory.createEntityManager();
        try {
            return em.createQuery("select count(e) from " + entityName + " e", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}
