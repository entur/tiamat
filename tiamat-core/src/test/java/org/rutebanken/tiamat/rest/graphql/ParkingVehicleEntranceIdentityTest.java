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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.StopPlace;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * A GraphQL save keeps the netexId of each vehicle entrance that the client sends back with its id.
 */
public class ParkingVehicleEntranceIdentityTest extends AbstractGraphQLResourceIntegrationTest {

    @Test
    public void vehicleEntranceKeepsNetexIdOnUpdate() throws Exception {
        StopPlace stopPlace = stopPlaceRepository.save(new StopPlace());

        String createQuery = "{\n" +
                "\"query\": \"mutation { " +
                "  parking: " + GraphQLNames.MUTATE_PARKING + " (Parking : {" +
                "    geometry: { type:Point coordinates:[10.5, 59.0] } " +
                "    parentSiteRef:\\\"" + stopPlace.getNetexId() + "\\\" " +
                "    vehicleEntrances: [ { label: \\\"North gate\\\" entranceType: gate isEntry: true isExit: false } ]" +
                "  }) {" +
                "    id " +
                "  }" +
                "}\",\"variables\": \"\"}";

        String parkingId = executeGraphQL(createQuery)
                .body("data.parking[0].id", notNullValue())
                .extract()
                .path("data.parking[0].id");

        String originalEntranceNetexId = firstVehicleEntranceNetexId(parkingId);
        assertThat(originalEntranceNetexId).isNotNull();

        String updateQuery = "{" +
                "\"query\":\"mutation { " +
                "  parking:" + GraphQLNames.MUTATE_PARKING + " (Parking: {" +
                "        id:\\\"" + parkingId + "\\\" " +
                "        vehicleEntrances: [ { id: \\\"" + originalEntranceNetexId + "\\\" label: \\\"North gate\\\" entranceType: gate isEntry: true isExit: false } ] " +
                "       }) { " +
                "      id " +
                "    } " +
                "}\"," +
                "\"variables\":\"\"}";

        executeGraphQL(updateQuery)
                .body("data.parking[0].id", org.hamcrest.Matchers.equalTo(parkingId));

        String updatedEntranceNetexId = firstVehicleEntranceNetexId(parkingId);

        assertThat(updatedEntranceNetexId)
                .as("re-sending an unchanged vehicle entrance should keep its netexId")
                .isEqualTo(originalEntranceNetexId);
    }

    @Test
    public void detachedVehicleEntrancesAreDeletedNotOrphaned() throws Exception {
        StopPlace stopPlace = stopPlaceRepository.save(new StopPlace());

        long before = countRows("ParkingEntranceForVehicles");

        String createQuery = "{\n" +
                "\"query\": \"mutation { " +
                "  parking: " + GraphQLNames.MUTATE_PARKING + " (Parking : {" +
                "    geometry: { type:Point coordinates:[10.5, 59.0] } " +
                "    parentSiteRef:\\\"" + stopPlace.getNetexId() + "\\\" " +
                "    vehicleEntrances: [ { label: \\\"Gate 1\\\" entranceType: gate } { label: \\\"Gate 2\\\" entranceType: gate } ]" +
                "  }) {" +
                "    id " +
                "  }" +
                "}\",\"variables\": \"\"}";

        String parkingId = executeGraphQL(createQuery)
                .extract()
                .path("data.parking[0].id");

        long afterCreate = countRows("ParkingEntranceForVehicles");
        assertThat(afterCreate).isEqualTo(before + 2);

        // Re-sending a single entrance detaches the other one from the collection.
        String updateQuery = "{" +
                "\"query\":\"mutation { " +
                "  parking:" + GraphQLNames.MUTATE_PARKING + " (Parking: {" +
                "        id:\\\"" + parkingId + "\\\" " +
                "        vehicleEntrances: [ { label: \\\"Gate 1\\\" entranceType: gate } ] " +
                "       }) { " +
                "      id " +
                "    } " +
                "}\"," +
                "\"variables\":\"\"}";

        executeGraphQL(updateQuery);

        long afterUpdate = countRows("ParkingEntranceForVehicles");

        assertThat(afterUpdate)
                .as("orphanRemoval on Parking.vehicleEntrances should delete detached rows, not leak them")
                .isEqualTo(before + 1);
    }

    @Test
    public void unknownEntranceIdIsRejectedWithChildAndParentId() throws Exception {
        String parkingId = createParkingWithEntrances("Gate 1");

        executeGraphqQLQueryOnly(updateEntrances(parkingId, "{ id: \"PRK:ParkingEntranceForVehicles:999999\" label: \"Gate 1\" }"), 400)
                .body("errors[0].message", allOf(
                        containsString("PRK:ParkingEntranceForVehicles:999999"),
                        containsString(parkingId)));

        assertThat(entrances(parkingId)).hasSize(1);
    }

    @Test
    public void entranceIdOfAnotherParkingIsRejected() throws Exception {
        String parkingId = createParkingWithEntrances("Gate 1");
        String otherParkingId = createParkingWithEntrances("Other gate");
        String foreignEntranceId = entrances(otherParkingId).getFirst().netexId();

        executeGraphqQLQueryOnly(updateEntrances(parkingId, "{ id: \"" + foreignEntranceId + "\" label: \"Gate 1\" }"), 400)
                .body("errors[0].message", allOf(containsString(foreignEntranceId), containsString(parkingId)));

        assertThat(entrances(otherParkingId).getFirst().netexId()).isEqualTo(foreignEntranceId);
    }

    @Test
    public void sameEntranceIdTwiceInOneSaveIsRejected() throws Exception {
        String parkingId = createParkingWithEntrances("Gate 1");
        String entranceId = entrances(parkingId).getFirst().netexId();
        String entrance = "{ id: \"" + entranceId + "\" label: \"Gate 1\" }";

        executeGraphqQLQueryOnly(updateEntrances(parkingId, entrance + " " + entrance), 400)
                .body("errors[0].message", allOf(containsString(entranceId), containsString("more than once")));

        assertThat(entrances(parkingId)).hasSize(1);
    }

    @Test
    public void entranceWithoutIdIsCreatedAsNewEntrance() throws Exception {
        String parkingId = createParkingWithEntrances("Gate 1");
        String originalId = entrances(parkingId).getFirst().netexId();

        executeGraphqQLQueryOnly(updateEntrances(parkingId, "{ label: \"Gate 1\" }"))
                .body("errors", nullValue());

        List<EntranceState> after = entrances(parkingId);
        assertThat(after).hasSize(1);
        assertThat(after.getFirst().netexId()).isNotEqualTo(originalId);
    }

    @Test
    public void resentEntranceKeepsNetexIdAndGetsNewVersion() throws Exception {
        String parkingId = createParkingWithEntrances("Gate 1");
        EntranceState original = entrances(parkingId).getFirst();

        executeGraphqQLQueryOnly(updateEntrances(parkingId, "{ id: \"" + original.netexId() + "\" label: \"Gate 1\" }"))
                .body("errors", nullValue());

        EntranceState after = entrances(parkingId).getFirst();
        assertThat(after.netexId()).isEqualTo(original.netexId());
        assertThat(after.version()).isGreaterThan(original.version());
    }

    @Test
    public void resentEntranceKeepsStoredCentroidWhenInputHasNoGeometry() throws Exception {
        String parkingId = createParkingWithEntrances("Gate 1");
        String entranceId = entrances(parkingId).getFirst().netexId();
        setEntranceCentroid(parkingId, geometryFactory.createPoint(new Coordinate(10.7, 59.9)));

        executeGraphqQLQueryOnly(updateEntrances(parkingId, "{ id: \"" + entranceId + "\" label: \"Gate 1 renamed\" }"))
                .body("errors", nullValue());

        EntranceState after = entrances(parkingId).getFirst();
        assertThat(after.netexId()).isEqualTo(entranceId);
        assertThat(after.centroid()).isNotNull();
        assertThat(after.centroid().getX()).isEqualTo(10.7);
        assertThat(after.centroid().getY()).isEqualTo(59.9);
    }

    private record EntranceState(String netexId, long version, Point centroid) {
    }

    private String createParkingWithEntrances(String label) throws Exception {
        StopPlace stopPlace = stopPlaceRepository.save(new StopPlace());
        String query = """
                {"query": "mutation { parking: %s (Parking: { geometry: { type:Point coordinates:[10.5, 59.0] } parentSiteRef: \\"%s\\" vehicleEntrances: [ { label: \\"%s\\" entranceType: gate } ] }) { id } }", "variables": ""}"""
                .formatted(GraphQLNames.MUTATE_PARKING, stopPlace.getNetexId(), label);
        return executeGraphQL(query).extract().path("data.parking[0].id");
    }

    private String updateEntrances(String parkingId, String entrancesInput) {
        return """
                mutation { parking: %s (Parking: { id: "%s" vehicleEntrances: [ %s ] }) { id } }"""
                .formatted(GraphQLNames.MUTATE_PARKING, parkingId, entrancesInput);
    }

    private List<EntranceState> entrances(String parkingNetexId) {
        return inLatestParking(parkingNetexId, parking -> parking.getVehicleEntrances().stream()
                .map(entrance -> new EntranceState(entrance.getNetexId(), entrance.getVersion(), entrance.getCentroid()))
                .toList());
    }

    private void setEntranceCentroid(String parkingNetexId, Point centroid) {
        inLatestParking(parkingNetexId, parking -> {
            parking.getVehicleEntrances().forEach(entrance -> entrance.setCentroid(centroid));
            return null;
        });
    }

    private <R> R inLatestParking(String parkingNetexId, Function<Parking, R> work) {
        var em = entityManagerFactory.createEntityManager();
        var tx = em.getTransaction();
        tx.begin();
        try {
            Parking parking = em.createQuery(
                            "select p from Parking p where p.netexId = :id order by p.version desc",
                            Parking.class)
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

    private String firstVehicleEntranceNetexId(String parkingNetexId) {
        var em = entityManagerFactory.createEntityManager();
        var tx = em.getTransaction();
        tx.begin();
        try {
            Parking parking = em.createQuery(
                            "select p from Parking p where p.netexId = :id order by p.version desc",
                            Parking.class)
                    .setParameter("id", parkingNetexId)
                    .setMaxResults(1)
                    .getSingleResult();
            return parking.getVehicleEntrances().getFirst().getNetexId();
        } finally {
            tx.commit();
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
