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
import org.rutebanken.tiamat.model.StopPlace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * A GraphQL save keeps the netexId of the place equipment that the client sends back with its id.
 */
public class PlaceEquipmentIdentityGraphQLTest extends AbstractGraphQLResourceIntegrationTest {

    private static final String EQUIPMENT_FIELDS = """
            placeEquipments { id
              shelterEquipment { id seats }
              sanitaryEquipment { id numberOfToilets }
              cycleStorageEquipment { id numberOfSpaces }
              waitingRoomEquipment { id seats }
              ticketingEquipment { id numberOfMachines }
              generalSign { id signContentType }
            }""";

    @Test
    public void stopPlaceEquipmentKeepsNetexIdsWhenResentWithIds() {
        String stopPlaceId = createStopPlaceWithAllEquipment();
        io.restassured.path.json.JsonPath before = readStopPlaceEquipment(stopPlaceId);

        String update = """
                mutation { stopPlace: %s (StopPlace: { id: "%s" placeEquipments: { id: "%s"
                  shelterEquipment: [ { id: "%s" seats: 9 } ]
                  sanitaryEquipment: [ { id: "%s" numberOfToilets: 8 } ]
                  cycleStorageEquipment: [ { id: "%s" numberOfSpaces: 7 } ]
                  waitingRoomEquipment: [ { id: "%s" seats: 6 } ]
                  ticketingEquipment: [ { id: "%s" numberOfMachines: 5 } ]
                  generalSign: [ { id: "%s" signContentType: transportMode } ]
                } }) { id %s } }"""
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlaceId,
                        before.getString("placeEquipments.id"),
                        before.getString("placeEquipments.shelterEquipment[0].id"),
                        before.getString("placeEquipments.sanitaryEquipment[0].id"),
                        before.getString("placeEquipments.cycleStorageEquipment[0].id"),
                        before.getString("placeEquipments.waitingRoomEquipment[0].id"),
                        before.getString("placeEquipments.ticketingEquipment[0].id"),
                        before.getString("placeEquipments.generalSign[0].id"),
                        EQUIPMENT_FIELDS);

        executeGraphqQLQueryOnly(update)
                .rootPath("data.stopPlace[0]")
                .body("placeEquipments.id", equalTo(before.getString("placeEquipments.id")))
                .body("placeEquipments.shelterEquipment[0].id", equalTo(before.getString("placeEquipments.shelterEquipment[0].id")))
                .body("placeEquipments.shelterEquipment[0].seats", equalTo(9))
                .body("placeEquipments.sanitaryEquipment[0].id", equalTo(before.getString("placeEquipments.sanitaryEquipment[0].id")))
                .body("placeEquipments.sanitaryEquipment[0].numberOfToilets", equalTo(8))
                .body("placeEquipments.cycleStorageEquipment[0].id", equalTo(before.getString("placeEquipments.cycleStorageEquipment[0].id")))
                .body("placeEquipments.cycleStorageEquipment[0].numberOfSpaces", equalTo(7))
                .body("placeEquipments.waitingRoomEquipment[0].id", equalTo(before.getString("placeEquipments.waitingRoomEquipment[0].id")))
                .body("placeEquipments.waitingRoomEquipment[0].seats", equalTo(6))
                .body("placeEquipments.ticketingEquipment[0].id", equalTo(before.getString("placeEquipments.ticketingEquipment[0].id")))
                .body("placeEquipments.ticketingEquipment[0].numberOfMachines", equalTo(5))
                .body("placeEquipments.generalSign[0].id", equalTo(before.getString("placeEquipments.generalSign[0].id")));
    }

    @Test
    public void quayEquipmentKeepsNetexIdWhenResentWithId() {
        String stopPlaceId = createStopPlaceWithAllEquipment();
        io.restassured.path.json.JsonPath before = readStopPlaceEquipment(stopPlaceId);
        String quayId = before.getString("quays[0].id");
        String quayShelterId = before.getString("quays[0].placeEquipments.shelterEquipment[0].id");
        assertThat(quayShelterId).isNotNull();

        String update = """
                mutation { stopPlace: %s (StopPlace: { id: "%s" quays: [ { id: "%s" placeEquipments: { id: "%s"
                  shelterEquipment: [ { id: "%s" seats: 11 } ] } } ] }) { id quays { id %s } } }"""
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlaceId, quayId,
                        before.getString("quays[0].placeEquipments.id"), quayShelterId, EQUIPMENT_FIELDS);

        executeGraphqQLQueryOnly(update)
                .rootPath("data.stopPlace[0].quays[0]")
                .body("placeEquipments.id", equalTo(before.getString("quays[0].placeEquipments.id")))
                .body("placeEquipments.shelterEquipment[0].id", equalTo(quayShelterId))
                .body("placeEquipments.shelterEquipment[0].seats", equalTo(11));
    }

    @Test
    public void parkingEquipmentKeepsNetexIdWhenResentWithId() {
        StopPlace stopPlace = stopPlaceRepository.save(new StopPlace());
        String create = """
                mutation { parking: %s (Parking: { geometry: { type:Point coordinates:[10.5, 59.0] } parentSiteRef: "%s"
                  placeEquipments: { shelterEquipment: [ { seats: 2 } ] } }) { id %s } }"""
                .formatted(GraphQLNames.MUTATE_PARKING, stopPlace.getNetexId(), EQUIPMENT_FIELDS);
        var created = executeGraphqQLQueryOnly(create).extract();
        String parkingId = created.path("data.parking[0].id");
        String shelterId = created.path("data.parking[0].placeEquipments.shelterEquipment[0].id");

        String update = """
                mutation { parking: %s (Parking: { id: "%s" placeEquipments: { id: "%s"
                  shelterEquipment: [ { id: "%s" seats: 3 } ] } }) { id %s } }"""
                .formatted(GraphQLNames.MUTATE_PARKING, parkingId,
                        created.path("data.parking[0].placeEquipments.id"), shelterId, EQUIPMENT_FIELDS);

        executeGraphqQLQueryOnly(update)
                .rootPath("data.parking[0]")
                .body("placeEquipments.shelterEquipment[0].id", equalTo(shelterId))
                .body("placeEquipments.shelterEquipment[0].seats", equalTo(3));
    }

    @Test
    public void equipmentWithoutIdIsCreatedAsNewEquipmentAndOmittedOneIsRemoved() {
        String stopPlaceId = createStopPlaceWithAllEquipment();
        io.restassured.path.json.JsonPath before = readStopPlaceEquipment(stopPlaceId);
        String oldShelterId = before.getString("placeEquipments.shelterEquipment[0].id");

        String update = """
                mutation { stopPlace: %s (StopPlace: { id: "%s" placeEquipments: { shelterEquipment: [ { seats: 4 } ] } }) { id %s } }"""
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlaceId, EQUIPMENT_FIELDS);

        var after = executeGraphqQLQueryOnly(update).extract();
        assertThat((String) after.path("data.stopPlace[0].placeEquipments.shelterEquipment[0].id")).isNotEqualTo(oldShelterId);
        assertThat((Object) after.path("data.stopPlace[0].placeEquipments.sanitaryEquipment")).isNull();
    }

    @Test
    public void unknownEquipmentIdIsRejectedWithChildAndParentId() {
        String stopPlaceId = createStopPlaceWithAllEquipment();

        String update = """
                mutation { stopPlace: %s (StopPlace: { id: "%s" placeEquipments: { shelterEquipment: [ { id: "NSR:ShelterEquipment:999999" seats: 4 } ] } }) { id } }"""
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlaceId);

        executeGraphqQLQueryOnly(update, 400)
                .body("errors[0].message", allOf(containsString("NSR:ShelterEquipment:999999"), containsString(stopPlaceId)));
    }

    @Test
    public void equipmentIdOfAnotherTypeIsRejected() {
        String stopPlaceId = createStopPlaceWithAllEquipment();
        String sanitaryId = readStopPlaceEquipment(stopPlaceId).getString("placeEquipments.sanitaryEquipment[0].id");

        String update = """
                mutation { stopPlace: %s (StopPlace: { id: "%s" placeEquipments: { shelterEquipment: [ { id: "%s" seats: 4 } ] } }) { id } }"""
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlaceId, sanitaryId);

        executeGraphqQLQueryOnly(update, 400)
                .body("errors[0].message", containsString(sanitaryId));
    }

    @Test
    public void sameEquipmentIdTwiceInOneSaveIsRejected() {
        String stopPlaceId = createStopPlaceWithAllEquipment();
        String shelterId = readStopPlaceEquipment(stopPlaceId).getString("placeEquipments.shelterEquipment[0].id");
        String shelter = "{ id: \"" + shelterId + "\" seats: 4 }";

        String update = """
                mutation { stopPlace: %s (StopPlace: { id: "%s" placeEquipments: { shelterEquipment: [ %s %s ] } }) { id } }"""
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlaceId, shelter, shelter);

        executeGraphqQLQueryOnly(update, 400)
                .body("errors[0].message", allOf(containsString(shelterId), containsString("more than once")));
    }

    @Test
    public void saveWithoutEquipmentInInputKeepsStoredEquipmentId() {
        String stopPlaceId = createStopPlaceWithAllEquipment();
        io.restassured.path.json.JsonPath before = readStopPlaceEquipment(stopPlaceId);

        String update = """
                mutation { stopPlace: %s (StopPlace: { id: "%s" description: { value: "changed" } }) { id %s } }"""
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlaceId, EQUIPMENT_FIELDS);

        executeGraphqQLQueryOnly(update)
                .rootPath("data.stopPlace[0]")
                .body("errors", nullValue())
                .body("placeEquipments.id", equalTo(before.getString("placeEquipments.id")))
                .body("placeEquipments.shelterEquipment[0].id", equalTo(before.getString("placeEquipments.shelterEquipment[0].id")));
    }

    private String createStopPlaceWithAllEquipment() {
        String create = """
                mutation { stopPlace: %s (StopPlace: { name: { value: "Equipment stop" }
                  geometry: { type: Point coordinates: [10.5, 59.9] }
                  placeEquipments: {
                    shelterEquipment: [ { seats: 1 } ]
                    sanitaryEquipment: [ { numberOfToilets: 1 } ]
                    cycleStorageEquipment: [ { numberOfSpaces: 1 } ]
                    waitingRoomEquipment: [ { seats: 1 } ]
                    ticketingEquipment: [ { numberOfMachines: 1 } ]
                    generalSign: [ { signContentType: transportMode } ]
                  }
                  quays: [ { geometry: { type: Point coordinates: [10.5, 59.9] } placeEquipments: { shelterEquipment: [ { seats: 1 } ] } } ]
                }) { id } }"""
                .formatted(GraphQLNames.MUTATE_STOPPLACE);
        return executeGraphqQLQueryOnly(create).extract().path("data.stopPlace[0].id");
    }

    private io.restassured.path.json.JsonPath readStopPlaceEquipment(String stopPlaceId) {
        String query = """
                { stopPlace: stopPlace(id: "%s") { id %s ... on StopPlace { quays { id %s } } } }"""
                .formatted(stopPlaceId, EQUIPMENT_FIELDS, EQUIPMENT_FIELDS);
        return executeGraphqQLQueryOnly(query).extract().jsonPath().setRootPath("data.stopPlace[0]");
    }
}
